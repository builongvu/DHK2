package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VdsGroupChannelDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.Staff;
import vn.vissoft.dashboard.model.VdsGroupChannel;
import vn.vissoft.dashboard.repo.ActionAuditRepo;
import vn.vissoft.dashboard.repo.ChannelRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.ChannelService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private ChannelRepo channelRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @PersistenceContext
    EntityManager entityManager;

//    private BaseMapper<VdsGroupChannel, VdsGroupChannelDTO> mapper = new BaseMapper<VdsGroupChannel, VdsGroupChannelDTO>(VdsGroupChannel.class, VdsGroupChannelDTO.class);

    @Override
    public List<VdsGroupChannel> findAll() {
        return channelRepo.findAll();
    }

    @Override
    public List<VdsGroupChannel> getActiveChannel() throws Exception {
        return channelRepo.findActiveChannel();
    }

    @Override
    public String persist(VdsGroupChannel vdsGroupChannel, StaffDTO staffDTO) throws Exception {
        long vlngTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(vlngTime);
        if (!DataUtil.isNullObject(vdsGroupChannel)) {
            String vstrCheck = checkDuplicateVdsChannel(vdsGroupChannel.getCode());
            if (DataUtil.isNullOrEmpty(vstrCheck)) {
                vdsGroupChannel.setCreatedDate(timestamp);
                vdsGroupChannel.setUser(staffDTO.getStaffCode());
                entityManager.persist(vdsGroupChannel);
                saveAction(vdsGroupChannel, staffDTO);
            } else {
                return vstrCheck;
            }
        }
        return null;
    }

    @Override
    public List<VdsGroupChannel> getActiveGroupChannel() throws Exception {
        return channelRepo.findActiveGroupChannel();
    }

    /**
     * combobox nhom kenh VDS (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param pstrShopCode
     * @return
     * @throws Exception
     */
    @Override
    public List<VdsGroupChannel> getChannelByCondion(String pstrShopCode) throws Exception {
        List<VdsGroupChannel> vlstData = new ArrayList<>();
        if (DataUtil.isNullOrEmpty(pstrShopCode)) {
            vlstData = channelRepo.findActiveGroupChannel();
        } else if (pstrShopCode.trim().equals(Constants.PLAN_MONTHLY.VDS)) {
            vlstData = channelRepo.findByStatusAndNotExists();
        } else {
            VdsGroupChannel data = channelRepo.findChannelByShopCode(pstrShopCode);
            if (data != null) {
                vlstData.add(data);
            }
        }

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    @Override
    public String getNameByCode(String pstrVdsChannelCode) throws Exception {
        return channelRepo.findNameByCode(pstrVdsChannelCode);
    }

    /**
     * check trung vds channel code
     *
     * @param pstrVdsChannelCode
     * @return
     */
    private String checkDuplicateVdsChannel(String pstrVdsChannelCode) throws Exception {
        VdsGroupChannel vdsGroupChannel = channelRepo.findByCode(pstrVdsChannelCode.trim());
        if (DataUtil.isNullObject(vdsGroupChannel))
            return null;
        else return Constants.DUPLICATED_VDS_GROUP_CHANNEL;
    }

    /**
     * luu lich su them vds group channel
     *
     * @param vdsGroupChannel
     * @param staffDTO
     */
    private void saveAction(VdsGroupChannel vdsGroupChannel, StaffDTO staffDTO) {
        ActionAudit actionAudit = actionAuditService.log(Constants.VDS_GROUP_CHANNEL, Constants.CREATE, staffDTO.getStaffCode(), vdsGroupChannel.getId(), staffDTO.getShopCode());
        actionDetailService.createActionDetail(Constants.VDS_GROUP_CHANNELS.VDS_CHANNEL_CODE, vdsGroupChannel.getCode().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.VDS_GROUP_CHANNELS.VDS_CHANNEL_NAME, vdsGroupChannel.getName().trim(), actionAudit.getId(), null);
        actionDetailService.createActionDetail(Constants.VDS_GROUP_CHANNELS.STATUS, vdsGroupChannel.getStatus(), actionAudit.getId(), null);

    }

}
