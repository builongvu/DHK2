package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigGroupCardDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.ConfigGroupCard;
import vn.vissoft.dashboard.model.ConfigSingleCard;
import vn.vissoft.dashboard.model.ConfigSingleChart;
import vn.vissoft.dashboard.repo.ActionAuditRepo;
import vn.vissoft.dashboard.repo.ConfigGroupCardRepo;
import vn.vissoft.dashboard.repo.ConfigSingleCardRepo;
import vn.vissoft.dashboard.repo.ConfigSingleChartRepo;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.GroupCardService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class GroupCardServiceImpl implements GroupCardService {

    private BaseMapper<ConfigGroupCard, ConfigGroupCardDTO> mapper = new BaseMapper<ConfigGroupCard, ConfigGroupCardDTO>(ConfigGroupCard.class, ConfigGroupCardDTO.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ConfigGroupCardRepo groupCardRepo;

    @Autowired
    private ActionAuditRepo actionAuditRepo;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private ConfigSingleCardRepo configSingleCardRepo;

    @Autowired
    private ConfigSingleChartRepo configSingleChartRepo;

    @Override
    public List<ConfigGroupCard> findAll() throws Exception {
        return groupCardRepo.findAll();
    }

    /**
     * tim kiem nhom (group)
     *
     * @author VuBL
     * @since 2019/02
     * @param configGroupCard
     * @return
     * @throws Exception
     */
    @Override
    public List<ConfigGroupCardDTO> getByCondition(ConfigGroupCardDTO configGroupCard) throws Exception {
        List<Object[]> vlstObj = groupCardRepo.findByCondition(configGroupCard);
        List<ConfigGroupCardDTO> vlstData = new ArrayList<>();

        if (!DataUtil.isNullOrEmpty(vlstObj)) {
            for (Object[] obj : vlstObj) {
                ConfigGroupCardDTO data = new ConfigGroupCardDTO();
                data.setGroupId(DataUtil.safeToLong(obj[0]));
                data.setGroupCode((String) obj[1]);
                data.setDefaultCycle((String) obj[2]);
                data.setGroupNameI18n((String) obj[3]);
                data.setGroupName((String) obj[4]);
                data.setVdsChannelCode((String) obj[5]);
                data.setShopCode((String) obj[6]);
                data.setShopName((String) obj[7]);
                data.setDefaultCycleName((String) obj[8]);
                data.setVdsChannelName((String) obj[9]);
                vlstData.add(data);
            }
        }

//        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    /**
     * them moi nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @param configGroupCard
     * @param staffDTO
     * @return
     * @throws Exception
     */
    @Override
    public String addGroupCard(ConfigGroupCardDTO configGroupCard, StaffDTO staffDTO) throws Exception {
        List<ConfigGroupCardDTO> vlstCheck = getByCondition(configGroupCard);
        //ngay hien tai
        long vlngDateTime = System.currentTimeMillis();
        Date vdtCurrentTime = new Date(vlngDateTime);
        //them moi
//        configGroupCard.setGroupId(null);
        configGroupCard.setGroupName(configGroupCard.getGroupName().trim());
        configGroupCard.setDefaultCycle(configGroupCard.getDefaultCycle().trim());
        if (configGroupCard.getShopCode() != null) {
            configGroupCard.setShopCode(configGroupCard.getShopCode().trim());
        }
        if (configGroupCard.getVdsChannelCode() != null) {
            configGroupCard.setVdsChannelCode(configGroupCard.getVdsChannelCode().trim());
        }
        for (ConfigGroupCardDTO configGroupCardDTO : vlstCheck) {
            if (configGroupCardDTO.equals(configGroupCard)) {
                return Constants.CONFIG_GROUP_CARDS.DUPLICATE;
            }
        }
        ConfigGroupCard configGroupCardNew = groupCardRepo.save(mapper.toPersistenceBean(configGroupCard));
//        entityManager.persist(mapper.toPersistenceBean(configGroupCard));

        //luu action_audit
//        Long groupId = configGroupCard.getGroupId();
        ActionAudit actionAudit = new ActionAudit();
        actionAudit.setActionCode(Constants.CREATE);
        actionAudit.setActionDateTime(vdtCurrentTime);
        actionAudit.setPkID(configGroupCardNew.getGroupId());
        actionAudit.setObjectCode(Constants.CONFIG_GROUP_CARD);
        actionAudit.setUser(staffDTO.getStaffCode());
        actionAudit.setShopCode(staffDTO.getShopCode());
        actionAuditRepo.save(actionAudit);

        //luu action_detail
        if (configGroupCard.getGroupCode() != null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.GROUP_CODE, configGroupCard.getGroupCode().trim(), actionAudit.getId(), null);
        }
        if (configGroupCard.getGroupName() != null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.GROUP_NAME, configGroupCard.getGroupName(), actionAudit.getId(), null);
        }
        if (configGroupCard.getDefaultCycle() != null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.DEFAULT_CYCLE, configGroupCard.getDefaultCycle(), actionAudit.getId(), null);
        }
        if (configGroupCard.getVdsChannelCode() != null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.VDS_CHANNEL_CODE, configGroupCard.getVdsChannelCode(), actionAudit.getId(), null);
        }
        if (configGroupCard.getShopCode() != null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.SHOP_CODE, configGroupCard.getShopCode(), actionAudit.getId(), null);
        }

        return Constants.CONFIG_GROUP_CARDS.SUCCESS;
    }

    /**
     * cap nhat nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @param configGroupCard
     * @param staffDTO
     * @return
     * @throws Exception
     */
    @Override
    public String updateGroupCard(ConfigGroupCardDTO configGroupCard, StaffDTO staffDTO) throws Exception {
        List<ConfigGroupCardDTO> vlstCheck = getByCondition(configGroupCard);
        //ngay hien tai
        long vlngDateTime = System.currentTimeMillis();
        Date vdtCurrentTime = new Date(vlngDateTime);
        ConfigGroupCard oldValue = groupCardRepo.getOne(configGroupCard.getGroupId());
        configGroupCard.setGroupName(configGroupCard.getGroupName().trim());
        for (ConfigGroupCardDTO configGroupCardDTO : vlstCheck) {
            if (configGroupCardDTO.equals(configGroupCard) && !configGroupCardDTO.getGroupId().toString().equals(configGroupCard.getGroupId().toString())) {
                return Constants.CONFIG_GROUP_CARDS.DUPLICATE;
            }
        }

        //luu action_audit
        ActionAudit actionAudit = new ActionAudit();
        actionAudit.setActionCode(Constants.EDIT);
        actionAudit.setActionDateTime(vdtCurrentTime);
        actionAudit.setPkID(configGroupCard.getGroupId());
        actionAudit.setObjectCode(Constants.CONFIG_GROUP_CARD);
        actionAudit.setUser(staffDTO.getStaffCode());
        actionAudit.setShopCode(staffDTO.getShopCode());
        actionAuditRepo.save(actionAudit);

        configGroupCard.setGroupId(oldValue.getGroupId());
        if (oldValue.getGroupCode() != null) {
            configGroupCard.setGroupCode(oldValue.getGroupCode().trim());
        }
        if (oldValue.getChannelId() != null) {
            configGroupCard.setChannelId(oldValue.getChannelId());
        }

        //luu action_detail
        if (configGroupCard.getGroupName() == null && oldValue.getGroupName() != null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.GROUP_NAME, null, actionAudit.getId(), oldValue.getGroupName().trim());
            configGroupCard.setGroupName(oldValue.getGroupName().trim());
        } else if (configGroupCard.getGroupName() != null && oldValue.getGroupName() == null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.GROUP_NAME, configGroupCard.getGroupName().trim(), actionAudit.getId(), null);
        } else if (configGroupCard.getGroupName() != null && oldValue.getGroupName() != null) {
            if (!configGroupCard.getGroupName().trim().equals(oldValue.getGroupName().trim())) {
                actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.GROUP_NAME, configGroupCard.getGroupName().trim(), actionAudit.getId(), oldValue.getGroupName().trim());
            }
        } else {}

        if (!configGroupCard.getDefaultCycle().trim().equals(oldValue.getDefaultCycle().trim())) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.DEFAULT_CYCLE, configGroupCard.getDefaultCycle().trim(), actionAudit.getId(), oldValue.getDefaultCycle().trim());
        }

        if (configGroupCard.getVdsChannelCode() == null && oldValue.getVdsChannelCode() != null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.VDS_CHANNEL_CODE, null, actionAudit.getId(), oldValue.getVdsChannelCode().trim());
//            configGroupCard.setVdsChannelCode(oldValue.getVdsChannelCode().trim());
        } else if (configGroupCard.getVdsChannelCode() != null && oldValue.getVdsChannelCode() == null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.VDS_CHANNEL_CODE, configGroupCard.getVdsChannelCode().trim(), actionAudit.getId(), null);
        } else if (configGroupCard.getVdsChannelCode() != null && oldValue.getVdsChannelCode() != null) {
            if (!configGroupCard.getVdsChannelCode().trim().equals(oldValue.getVdsChannelCode().trim())) {
                actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.VDS_CHANNEL_CODE, configGroupCard.getVdsChannelCode().trim(), actionAudit.getId(), oldValue.getVdsChannelCode().trim());
            }
        } else {}

        if (configGroupCard.getShopCode() == null && oldValue.getShopCode() != null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.SHOP_CODE, null, actionAudit.getId(), oldValue.getShopCode().trim());
//            configGroupCard.setShopCode(oldValue.getShopCode().trim());
        } else if (configGroupCard.getShopCode() != null && oldValue.getShopCode() == null) {
            actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.SHOP_CODE, configGroupCard.getShopCode().trim(), actionAudit.getId(), null);
        } else if (configGroupCard.getShopCode() != null && oldValue.getShopCode() != null) {
            if (!configGroupCard.getShopCode().trim().equals(oldValue.getShopCode().trim())) {
                actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.SHOP_CODE, configGroupCard.getShopCode().trim(), actionAudit.getId(), oldValue.getShopCode().trim());
            }
        } else {}

        //update
        entityManager.merge(mapper.toPersistenceBean(configGroupCard));

        return Constants.CONFIG_GROUP_CARDS.SUCCESS;
    }

    /**
     * kiem tra group da co card chua, neu co thi khong cho xoa
     *
     * @author VuBL
     * @since 2020/02
     * @param pintGroupId
     * @return
     * @throws Exception
     */
    @Override
    public boolean checkGroupHaveCard(int pintGroupId) throws Exception {
        boolean vblnCheck;
        List<Object> vlstCheck = new ArrayList<>();
        List<ConfigSingleCard> vlstCards = configSingleCardRepo.findByGroupIdOrDrillDownObjectId(pintGroupId, pintGroupId);
        List<ConfigSingleChart> vlstCharts = configSingleChartRepo.findByDrillDownObjectId(pintGroupId);
        vlstCheck.addAll(vlstCards);
        vlstCheck.addAll(vlstCharts);


        if (vlstCheck.size() > 0) {
            vblnCheck = false;
        } else {
            vblnCheck = true;
        }

        return vblnCheck;
    }

    /**
     * xoa nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @param pintGroupId
     * @param staffDTO
     * @return
     * @throws Exception
     */
    @Override
    public String deleteGroupCard(int pintGroupId, StaffDTO staffDTO) throws Exception {
        //ngay hien tai
        long vlngDateTime = System.currentTimeMillis();
        Date vdtCurrentTime = new Date(vlngDateTime);
        String message;
        if (checkGroupHaveCard(pintGroupId)) {
            ConfigGroupCard oldValue = groupCardRepo.getOne((long) pintGroupId);

            //luu action_audit
            ActionAudit actionAudit = new ActionAudit();
            actionAudit.setActionCode(Constants.ACTION_CODE_DELETE);
            actionAudit.setActionDateTime(vdtCurrentTime);
            actionAudit.setPkID((long) pintGroupId);
            actionAudit.setObjectCode(Constants.CONFIG_GROUP_CARD);
            actionAudit.setUser(staffDTO.getStaffCode());
            actionAudit.setShopCode(staffDTO.getShopCode());
            actionAuditRepo.save(actionAudit);

            //luu action_detail
            if (oldValue.getGroupCode() != null) {
                actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.GROUP_CODE, null, actionAudit.getId(), oldValue.getGroupCode().trim());
            }
            if (oldValue.getGroupName() != null) {
                actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.GROUP_NAME, null, actionAudit.getId(), oldValue.getGroupName().trim());
            }
            if (oldValue.getDefaultCycle() != null) {
                actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.DEFAULT_CYCLE, null, actionAudit.getId(), oldValue.getDefaultCycle().trim());
            }
            if (oldValue.getVdsChannelCode() != null) {
                actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.VDS_CHANNEL_CODE, null, actionAudit.getId(), oldValue.getVdsChannelCode().trim());
            }
            if (oldValue.getShopCode() != null) {
                actionDetailService.createActionDetail(Constants.CONFIG_GROUP_CARDS.SHOP_CODE, null, actionAudit.getId(), oldValue.getShopCode().trim());
            }

            //xoa
            groupCardRepo.deleteByGroupId((long) pintGroupId);
            message = Constants.CONFIG_GROUP_CARDS.SUCCESS;
        } else {
            message = Constants.CONFIG_GROUP_CARDS.GROUP_HAVE_CARD;
        }

        return message;
    }

    /**
     * lay tat ca nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @return
     * @throws Exception
     */
    @Override
    public List<ConfigGroupCardDTO> getAllGroupCard() throws Exception {
        List<Object[]> vlstObj = groupCardRepo.findAllGroupCard();
        List<ConfigGroupCardDTO> vlstData = new ArrayList<>();

        if (!DataUtil.isNullOrEmpty(vlstObj)) {
            for (Object[] obj : vlstObj) {
                ConfigGroupCardDTO data = new ConfigGroupCardDTO();
                data.setGroupId(DataUtil.safeToLong(obj[0]));
                data.setGroupCode((String) obj[1]);
                data.setDefaultCycle((String) obj[2]);
                data.setGroupNameI18n((String) obj[3]);
                data.setGroupName((String) obj[4]);
                data.setVdsChannelCode((String) obj[5]);
                data.setShopCode((String) obj[6]);
                data.setShopName((String) obj[7]);
                data.setDefaultCycleName((String) obj[8]);
                data.setVdsChannelName((String) obj[9]);
                vlstData.add(data);
            }
        }

//        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }
}
