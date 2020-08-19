package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.MappingGroupChannelDTO;
import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.MappingGroupChannel;
import vn.vissoft.dashboard.repo.MappingGroupChannelRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.MappingGroupChannelService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class MappingGroupChannelServiceImpl implements MappingGroupChannelService {

    @Autowired
    private MappingGroupChannelRepo mappingGroupChannelRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @PersistenceContext
    EntityManager entityManager;

    /**
     * tim kiem mapping vds va vtt theo dieu kien
     *
     * @param mappingGroupChannel
     * @return
     * @throws Exception
     */
    @Override
    public List<MappingGroupChannel> getByCondition(MappingGroupChannelDTO mappingGroupChannel) throws Exception {
        List<Object[]> vlstMappingChannels = mappingGroupChannelRepo.findByCondition(mappingGroupChannel);
        List<MappingGroupChannel> vlstMappingGroupChannels = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(vlstMappingChannels)) {
            for (Object[] object : vlstMappingChannels) {
                Long vlngId=DataUtil.safeToLong(object[0]);
                String vstrGroupChannelCode = DataUtil.safeToString(object[1]);
                String vstrGroupChannelName = DataUtil.safeToString(object[2]);
                String vstrVdsChannelCode = DataUtil.safeToString(object[3]);
                String vstrVdsChannelName = DataUtil.safeToString(object[4]);
                String vstrStatus = DataUtil.safeToString(object[5]);
                String vstrUser = DataUtil.safeToString(object[6]);
                Timestamp vstrCreateDate = (Timestamp) object[7];

                MappingGroupChannel mappingGroupChannelOut = new MappingGroupChannel();
                mappingGroupChannelOut.setId(vlngId);
                mappingGroupChannelOut.setGroupChannelCode(vstrGroupChannelCode);
                mappingGroupChannelOut.setGroupChannelName(vstrGroupChannelName);
                mappingGroupChannelOut.setVdsChannelCode(vstrVdsChannelCode);
                mappingGroupChannelOut.setVdsChannelName(vstrVdsChannelName);
                mappingGroupChannelOut.setStatus(vstrStatus);
                mappingGroupChannelOut.setUser(vstrUser);
                mappingGroupChannelOut.setCreateDate(vstrCreateDate);

                vlstMappingGroupChannels.add(mappingGroupChannelOut);
            }
        }
        return vlstMappingGroupChannels;
    }

    /**
     * them moi mapping vtt va vds
     *
     * @param mappingGroupChannel
     * @param staffDTO
     * @return
     * @throws Exception
     */
    @Override
    public String persist(MappingGroupChannel mappingGroupChannel, StaffDTO staffDTO) throws Exception {
        long vlngTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(vlngTime);
        if (!DataUtil.isNullObject(mappingGroupChannel)) {
            String vstrCheck = checkDuplicateMappingChannel(mappingGroupChannel.getVdsChannelCode(), mappingGroupChannel.getGroupChannelCode());
            if (DataUtil.isNullOrEmpty(vstrCheck)) {
                mappingGroupChannel.setVdsChannelName(mappingGroupChannel.getVdsChannelName());
                mappingGroupChannel.setGroupChannelName(mappingGroupChannel.getGroupChannelName());
                mappingGroupChannel.setCreateDate(timestamp);
                mappingGroupChannel.setUser(staffDTO.getStaffCode());
                entityManager.persist(mappingGroupChannel);
                saveNewAction(mappingGroupChannel, staffDTO);
            } else {
                return vstrCheck;
            }
        }
        return null;
    }

    /**
     * sua mapping vtt va vds
     *
     * @param mappingGroupChannel
     * @param staffDTO
     * @throws Exception
     */
    @Override
    public String update(MappingGroupChannel mappingGroupChannel, StaffDTO staffDTO) throws Exception {
        long vlngTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(vlngTime);
        if (!DataUtil.isNullObject(mappingGroupChannel)) {
            MappingGroupChannel mappingGroupChannel1 = mappingGroupChannelRepo.findByVdsChannelCodeAndGroupChannelCodeAndIdNotLike(mappingGroupChannel.getVdsChannelCode(), mappingGroupChannel.getGroupChannelCode(), mappingGroupChannel.getId());
            if (mappingGroupChannel1 == null) {
                MappingGroupChannel mappingGroupChannelById = mappingGroupChannelRepo.getOne(mappingGroupChannel.getId());
                mappingGroupChannel.setCreateDate(timestamp);
                mappingGroupChannel.setUser(staffDTO.getStaffCode());
                saveUpdateAction(mappingGroupChannel, mappingGroupChannelById, staffDTO);
                entityManager.merge(mappingGroupChannel);
            }else{
                return Constants.DUPLICATED_MAPPING_GROUP_CHANNEL;
            }
        }
        return null;
    }

    /**
     * check trung mapping group channel
     *
     * @param pstrVdsChannelCode
     * @return
     */
    private String checkDuplicateMappingChannel(String pstrVdsChannelCode, String pstrVttChannelCode) throws Exception {
        MappingGroupChannel mappingGroupChannel = mappingGroupChannelRepo.findByVdsChannelCodeAndGroupChannelCode(pstrVdsChannelCode, pstrVttChannelCode.trim());
        if (DataUtil.isNullObject(mappingGroupChannel))
            return null;
        else return Constants.DUPLICATED_MAPPING_GROUP_CHANNEL;
    }

    /**
     * luu lich su them mapping group channel
     *
     * @param mappingGroupChannel
     * @param staffDTO
     */
    private void saveNewAction(MappingGroupChannel mappingGroupChannel, StaffDTO staffDTO) {
        ActionAudit actionAudit = actionAuditService.log(Constants.MAPPING_GROUP_CHANNEL, Constants.CREATE, staffDTO.getStaffCode(), mappingGroupChannel.getId(), staffDTO.getShopCode());
        actionDetailService.createActionDetail(Constants.MAPPING_GROUP_CHANNELS.VDS_CHANNEL_CODE, mappingGroupChannel.getVdsChannelCode(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.MAPPING_GROUP_CHANNELS.GROUP_CHANNEL_CODE, mappingGroupChannel.getGroupChannelCode(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.MAPPING_GROUP_CHANNELS.STATUS, mappingGroupChannel.getStatus(), actionAudit.getId(), null);

    }

    /**
     * luu lich su sua mapping group channel
     *
     * @param mappingGroupChannel
     * @param staffDTO
     */
    private void saveUpdateAction(MappingGroupChannel mappingGroupChannel, MappingGroupChannel mappingGroupChannelById, StaffDTO staffDTO) {
        ActionAudit actionAudit = actionAuditService.log(Constants.MAPPING_GROUP_CHANNEL, Constants.EDIT, staffDTO.getStaffCode(), mappingGroupChannel.getId(), staffDTO.getShopCode());
        if (!DataUtil.safeEqualIgnoreCase(mappingGroupChannelById.getVdsChannelCode().trim(), mappingGroupChannel.getVdsChannelCode().trim()))
            actionDetailService.createActionDetail(Constants.MAPPING_GROUP_CHANNELS.VDS_CHANNEL_CODE, mappingGroupChannel.getVdsChannelCode().trim(), actionAudit.getId(), mappingGroupChannelById.getVdsChannelCode().trim());
        if (!DataUtil.safeEqualIgnoreCase(mappingGroupChannelById.getGroupChannelCode().trim(), mappingGroupChannel.getGroupChannelCode().trim()))
            actionDetailService.createActionDetail(Constants.MAPPING_GROUP_CHANNELS.GROUP_CHANNEL_CODE, mappingGroupChannel.getGroupChannelCode().trim(), actionAudit.getId(), mappingGroupChannelById.getGroupChannelCode().trim());
        if (!DataUtil.safeEqualIgnoreCase(mappingGroupChannelById.getStatus().trim(), mappingGroupChannel.getStatus().trim()))
            actionDetailService.createActionDetail(Constants.MAPPING_GROUP_CHANNELS.STATUS, mappingGroupChannel.getStatus(), actionAudit.getId(), mappingGroupChannelById.getStatus());
    }

    @Override
    public BigInteger countByCondition(MappingGroupChannelDTO mappingGroupChannelDTO) throws Exception {
        return mappingGroupChannelRepo.countByCondition(mappingGroupChannelDTO);
    }

    @Override
    public MappingGroupChannel getById(Long id) throws Exception {

        Optional<MappingGroupChannel> optional = mappingGroupChannelRepo.getById(id);
    if(!optional.isPresent()){
        return null;
    }
        return optional.get();
    }
}
