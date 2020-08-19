package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VttGroupChannelDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.VttGroupChannel;
import vn.vissoft.dashboard.repo.MappingGroupChannelRepo;
import vn.vissoft.dashboard.repo.MappingGroupChannelRepoCustom;
import vn.vissoft.dashboard.repo.VttGroupChannelRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.VttGroupChannelService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class VttGroupChannelServiceImpl implements VttGroupChannelService {

    @Autowired
    private VttGroupChannelRepo vttGroupChannelRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private MappingGroupChannelRepo mappingGroupChannelRepo;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<VttGroupChannel> getActiveVttChannel() throws Exception {
        return vttGroupChannelRepo.findActiveVttGroupChannel();
    }

    @Override
    public String persist(VttGroupChannel vttGroupChannel, StaffDTO staffDTO) throws Exception {
        long vlngTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(vlngTime);
        if (!DataUtil.isNullObject(vttGroupChannel)) {
            String vstrCheck = checkDuplicateVttChannel(vttGroupChannel.getGroupChannelCode());
            if (DataUtil.isNullOrEmpty(vstrCheck)) {
                vttGroupChannel.setGroupChannelCode(vttGroupChannel.getGroupChannelCode().trim());
                vttGroupChannel.setGroupChannelName(vttGroupChannel.getGroupChannelName().trim());
                vttGroupChannel.setCreateDate(timestamp);
                vttGroupChannel.setUser(staffDTO.getStaffCode());
                vttGroupChannel.setStatus("1");
                entityManager.persist(vttGroupChannel);
                saveAction(vttGroupChannel, staffDTO);
            } else {
                return vstrCheck;
            }
        }
        return null;
    }

    @Override
    public List<VttGroupChannelDTO> getByCondition(VttGroupChannelDTO vttGroupChannel) throws Exception {
        List<VttGroupChannelDTO> vlstGroupChannels = new ArrayList<>();
        List<Object[]> vlstObjects = vttGroupChannelRepo.findByCondition(vttGroupChannel);
        if (!DataUtil.isNullOrEmpty(vlstObjects)) {
            for (Object[] objects : vlstObjects) {
                BigInteger vdblPositionId = (BigInteger) objects[3];
                VttGroupChannelDTO vttGroupChannelOut = new VttGroupChannelDTO();

                vttGroupChannelOut.setGroupChannelCode(DataUtil.safeToString(objects[0]));
                vttGroupChannelOut.setGroupChannelName(DataUtil.safeToString(objects[1]));
                vttGroupChannelOut.setClassification(DataUtil.safeToString(objects[2]));
                vttGroupChannelOut.setPositionId(vdblPositionId);
                vttGroupChannelOut.setPositionCode(DataUtil.safeToString(objects[4]));
                vttGroupChannelOut.setPositionName(DataUtil.safeToString(objects[5]));
                vttGroupChannelOut.setChannelTypeId(DataUtil.safeToLong(objects[6]));
                vttGroupChannelOut.setStatus(DataUtil.safeToString(objects[7]));

                vlstGroupChannels.add(vttGroupChannelOut);
            }
        }
        return vlstGroupChannels;
    }

    /**
     * check trung vtt channel code
     *
     * @param pstrVttChannelCode
     * @return
     */
    @Override
    public String checkDuplicateVttChannel(String pstrVttChannelCode) throws Exception {
        VttGroupChannel vttGroupChannel = vttGroupChannelRepo.findByGroupChannelCode(pstrVttChannelCode.trim());
        if (DataUtil.isNullObject(vttGroupChannel))
            return null;
        else return Constants.DUPLICATED_VTT_GROUP_CHANNEL;
    }

    @Override
    public List<VttGroupChannel> getAllNotInMapping(String code) throws Exception {
        return mappingGroupChannelRepo.getAllNotInMapping(code);
    }

    /**
     * luu lich su them vtt group channel
     *
     * @param vttGroupChannel
     * @param staffDTO
     */
    private void saveAction(VttGroupChannel vttGroupChannel, StaffDTO staffDTO) {
        ActionAudit actionAudit = actionAuditService.log(Constants.VTT_GROUP_CHANNEL, Constants.CREATE, staffDTO.getStaffCode(), vttGroupChannel.getGroupId(), staffDTO.getShopCode());
        actionDetailService.createActionDetail(Constants.VTT_GROUP_CHANNELS.VTT_CHANNEL_CODE, vttGroupChannel.getGroupChannelCode().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.VTT_GROUP_CHANNELS.VTT_CHANNEL_NAME, vttGroupChannel.getGroupChannelName().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.VTT_GROUP_CHANNELS.STATUS, vttGroupChannel.getStatus(), actionAudit.getId(), null);

    }

}
