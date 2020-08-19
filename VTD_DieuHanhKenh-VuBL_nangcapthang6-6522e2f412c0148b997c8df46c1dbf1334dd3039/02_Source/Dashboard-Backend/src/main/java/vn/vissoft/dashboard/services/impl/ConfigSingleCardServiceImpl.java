package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleCardDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.ConfigSingleCardService;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ConfigSingleCardServiceImpl implements ConfigSingleCardService {

    @Autowired
    private ConfigSingleCardRepo configSingleCardRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @Autowired
    private ConfigSingleChartRepo configSingleChartRepo;

    @Autowired
    private ConfigGroupCardRepo configGroupCardRepo;

    @Autowired
    private ApParamRepo apParamRepo;

    @Autowired
    private ServiceRepo serviceRepo;

    @Override
    public List<ConfigSingleCard> getActiveCard() throws Exception {
        return configSingleCardRepo.getActiveCard();
    }

    /**
     * do du lieu cho bang
     *
     * @return list du lieu
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 05/02/2020
     */
    @Override
    public List<ConfigSingleCardDTO> getAllCard() throws Exception {
        List<Object[]> listObject = configSingleCardRepo.getAllOrderById();
        List<ApParam> apParamList = apParamRepo.findAllByType(Constants.CONFIG_SINGLE_CARD.SIZE);
        List<ConfigGroupCard> groupCardList = configGroupCardRepo.findAll();
        List<vn.vissoft.dashboard.model.Service> serviceList = serviceRepo.findAll();
        Map<String, String> mapCardSize = new HashMap();
        Map<Integer, String> mapGroupId = new HashMap();
        Map<Long, String> mapServiceId = new HashMap<>();
        for(ApParam apParam : apParamList) {
            mapCardSize.put(apParam.getCode(), apParam.getName());
        }
        for(ConfigGroupCard configGroupCard : groupCardList) {
            mapGroupId.put(configGroupCard.getGroupId().intValue(), configGroupCard.getGroupName());
        }
        for(vn.vissoft.dashboard.model.Service service : serviceList) {
            mapServiceId.put(service.getId(), service.getName());
        }
        List<ConfigSingleCardDTO> singleCardDTOList = new ArrayList<>();

        Long cardId; String cardName; String cardNameI18N; String cardSize;
        Date createDate; Integer drillDown; Integer drillDownType;
        Integer drillDownObjectId; Integer groupId; Long serviceId;
        Integer status; String cardType;
        String description; String groupName;

        for(Object[] configSingleCard : listObject){
             cardId = DataUtil.safeToLong(configSingleCard[0]);
             cardName = DataUtil.safeToString(configSingleCard[1]);
             cardNameI18N = DataUtil.safeToString(configSingleCard[2]);
             cardSize = DataUtil.safeToString(configSingleCard[3]);
             createDate = (Date) configSingleCard[4];
             drillDown = DataUtil.safeToInt(configSingleCard[5]);
             drillDownType = DataUtil.safeToInt(configSingleCard[6]);
             drillDownObjectId = DataUtil.safeToInt(configSingleCard[7]);
             groupId = DataUtil.safeToInt(configSingleCard[8]);
             serviceId = DataUtil.safeToLong(configSingleCard[9]);
             status = DataUtil.safeToInt(configSingleCard[10]);
             cardType = DataUtil.safeToString(configSingleCard[12]);
             description = DataUtil.safeToString(configSingleCard[13]);
             groupName = DataUtil.safeToString(configSingleCard[14]);

            ConfigSingleCardDTO configSingleCardDTO = new ConfigSingleCardDTO();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = simpleDateFormat.format(createDate);

            configSingleCardDTO.setCreateDate(date);
            configSingleCardDTO.setCardId(cardId);
            configSingleCardDTO.setCardName(cardName);
            configSingleCardDTO.setCardNameI18n(cardNameI18N);
            configSingleCardDTO.setCardSize(cardSize);
            if(!DataUtil.isNullOrEmpty(configSingleCardDTO.getCardSize())) {
                configSingleCardDTO.setSizeName(mapCardSize.get(configSingleCardDTO.getCardSize()));
            }
            configSingleCardDTO.setDrilldown(drillDown);
            if(drillDown != null) {
                if(configSingleCardDTO.getDrilldown() == 1) {
                    configSingleCardDTO.setShowDrillDown(I18N.get(Constants.COMMON_TABLE_WARNING_YES));
                } else {
                    configSingleCardDTO.setShowDrillDown(I18N.get(Constants.COMMON_TABLE_WARNING_NO));
                }
            }
            configSingleCardDTO.setDrillDownType(drillDownType);
            if(!DataUtil.isNullOrZero(drillDownObjectId)) {
                configSingleCardDTO.setDrillDownObjectId(drillDownObjectId);
                String showGroupChild = mapGroupId.get(drillDownObjectId);
                configSingleCardDTO.setShowDrillDownObjectId(showGroupChild);
            }
            if(!DataUtil.isNullOrZero(groupId)) {
                configSingleCardDTO.setGroupId(groupId);
                configSingleCardDTO.setGroupName(groupName);
            }
            if(!DataUtil.isNullOrZero(serviceId)) {
                configSingleCardDTO.setServiceId(serviceId);
                configSingleCardDTO.setServiceName(mapServiceId.get(serviceId));
            }
            if(status != null) {
                configSingleCardDTO.setStatus(status);
                if(configSingleCardDTO.getStatus() == 1) {
                    configSingleCardDTO.setShowStatus(I18N.get(Constants.COMMON_MESSAGE_STATUS_WORK));
                } else {
                    configSingleCardDTO.setShowStatus(I18N.get(Constants.COMMON_MESSAGE_STATUS_NOT_WORK));
                }
            }
            if(configSingleCard[11] != null) {
                configSingleCardDTO.setZoom((Integer) configSingleCard[11]);
                if(configSingleCardDTO.getZoom() == 1) {
                    configSingleCardDTO.setShowZoom(I18N.get(Constants.COMMON_TABLE_WARNING_YES));
                } else {
                    configSingleCardDTO.setShowZoom(I18N.get(Constants.COMMON_TABLE_WARNING_NO));
                }
            }
            if(!DataUtil.isNullOrEmpty(cardType)){
                configSingleCardDTO.setCardType(cardType);
                configSingleCardDTO.setNameCardType(description);
            }
            singleCardDTOList.add(configSingleCardDTO);
        }
        return singleCardDTOList;
    }

    /**
     * xoa card theo cardId, xoa thanh cong neu ko co chart theo cardId truyen vao
     *
     * @return thong bao
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 05/02/2020
     */
    @Override
    public String deleteSingleCard(ConfigSingleCardDTO configSingleCardDTO, StaffDTO staffDTO) throws Exception {
        String message;
        Long cardId = configSingleCardDTO.getCardId();
        List<ConfigSingleChart> list = configSingleChartRepo.findAllByCardId(cardId);
        if(list.size() > 0) {
            message = Constants.ERROR;
        } else {
            ConfigSingleCard configSingleCard = configSingleCardRepo.getOne(cardId);
            ActionAudit actionAudit = actionAuditService.log(Constants.CONFIG_SINGLE_CARD.CONFIG_SINGLE_CARD, Constants.ACTION_CODE_DELETE, staffDTO.getStaffCode(), cardId, staffDTO.getShopCode());
            if(!DataUtil.isNullOrEmpty(configSingleCard.getCardName())){
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_NAME, null, actionAudit.getId(), configSingleCard.getCardName());
            }
            if(!DataUtil.isNullOrEmpty(configSingleCard.getCardNameI18n())) {
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_NAME_I18N, null, actionAudit.getId(), configSingleCard.getCardNameI18n());
            }
            if(!DataUtil.isNullOrEmpty(configSingleCard.getCardSize())) {
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_SIZE, null, actionAudit.getId(), configSingleCard.getCardSize());
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(configSingleCard.getCreateDate());
            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CREATE_DATE, null, actionAudit.getId(), date);
            if(!DataUtil.isNullOrZero(configSingleCard.getDrilldown())) {
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN, null, actionAudit.getId(), configSingleCard.getDrilldown().toString());
            }
            if(!DataUtil.isNullOrZero(configSingleCard.getDrillDownType())) {
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_TYPE, null, actionAudit.getId(), configSingleCard.getDrillDownType().toString());
            }
            if(!DataUtil.isNullOrZero(configSingleCard.getDrillDownObjectId())) {
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_OBJECT_ID, null, actionAudit.getId(), configSingleCard.getDrillDownObjectId().toString());
            }
            if(!DataUtil.isNullOrZero(configSingleCard.getGroupId())) {
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.GROUP_ID, null, actionAudit.getId(), configSingleCard.getGroupId().toString());
            }
            if(!DataUtil.isNullOrZero(configSingleCard.getServiceId())) {
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.SERVICE_ID, null, actionAudit.getId(), configSingleCard.getServiceId().toString());
            }
            if(!DataUtil.isNullOrZero(configSingleCard.getStatus())) {
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.STATUS, null, actionAudit.getId(), configSingleCard.getStatus().toString());
            }
            if(!DataUtil.isNullOrZero(configSingleCard.getZoom())) {
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.ZOOM, null, actionAudit.getId(), configSingleCard.getZoom().toString());
            }
            if(!DataUtil.isNullOrZero(configSingleCard.getCardType())) {
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_TYPE, null, actionAudit.getId(), configSingleCard.getCardType());
            }
            configSingleCardRepo.deleteById(cardId);
            message = Constants.SUCCESS;
        }
        return message;
    }

    @Override
    public List<ConfigSingleCardDTO> searchConfigSingleCard(ConfigSingleCardDTO configSingleCardDTO) throws Exception {
        List<Object[]> list = configSingleCardRepo.searchSingleCard(configSingleCardDTO);
        List<ApParam> apParamList = apParamRepo.findAllByType(Constants.CONFIG_SINGLE_CARD.SIZE);
        List<ConfigGroupCard> groupCardList = configGroupCardRepo.findAll();
        List<vn.vissoft.dashboard.model.Service> serviceList = serviceRepo.findAll();
        Map<String, String> mapCardSize = new HashMap<>();
        Map<Integer, String> mapGroupId = new HashMap<>();
        Map<Long, String> mapService = new HashMap<>();
        for(ApParam apParam : apParamList) {
            mapCardSize.put(apParam.getCode(), apParam.getName());
        }
        for(ConfigGroupCard configGroupCard : groupCardList) {
            mapGroupId.put(configGroupCard.getGroupId().intValue(), configGroupCard.getGroupName());
        }
        for(vn.vissoft.dashboard.model.Service service : serviceList) {
            mapService.put(service.getId(), service.getName());
        }

        List<ConfigSingleCardDTO> singleCardDTOList = new ArrayList<>();

        Long cardId; String cardName; String cardNameI18N; String cardSize;
        Date createDate; Integer drillDown; Integer drillDownType;
        Integer drillDownObjectId; Integer groupId; Long serviceId;
        Integer status; String cardType;
        String description; String groupName;

        for(Object[] configSingleCard : list) {
            ConfigSingleCardDTO singleCardDTO = new ConfigSingleCardDTO();

             cardId = DataUtil.safeToLong(configSingleCard[0]);
             cardName = DataUtil.safeToString(configSingleCard[1]);
             cardNameI18N = DataUtil.safeToString(configSingleCard[2]);
             cardSize = DataUtil.safeToString(configSingleCard[3]);
             createDate = (Date) configSingleCard[4];
             drillDown = DataUtil.safeToInt(configSingleCard[5]);
             drillDownType = DataUtil.safeToInt(configSingleCard[6]);
             drillDownObjectId = DataUtil.safeToInt(configSingleCard[7]);
             groupId = DataUtil.safeToInt(configSingleCard[8]);
             serviceId = DataUtil.safeToLong(configSingleCard[9]);
             status = DataUtil.safeToInt(configSingleCard[10]);
             cardType = DataUtil.safeToString(configSingleCard[12]);
             description = DataUtil.safeToString(configSingleCard[13]);
             groupName = DataUtil.safeToString(configSingleCard[14]);

            singleCardDTO.setCardId(cardId);
            singleCardDTO.setCardName(cardName);
            singleCardDTO.setCardNameI18n(cardNameI18N);
            singleCardDTO.setCardSize(cardSize);
            if(!DataUtil.isNullOrEmpty(singleCardDTO.getCardSize())) {
                singleCardDTO.setSizeName(mapCardSize.get(singleCardDTO.getCardSize()));
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = simpleDateFormat.format(createDate);
            singleCardDTO.setCreateDate(date);
            singleCardDTO.setDrilldown(drillDown);
            if(drillDown != null) {
                if(singleCardDTO.getDrilldown() == 1) {
                    singleCardDTO.setShowDrillDown(I18N.get(Constants.COMMON_TABLE_WARNING_YES));
                } else {
                    singleCardDTO.setShowDrillDown(I18N.get(Constants.COMMON_TABLE_WARNING_NO));
                }
            }
            singleCardDTO.setDrillDownType(drillDownType);
            if(!DataUtil.isNullOrZero(drillDownObjectId)) {
                singleCardDTO.setDrillDownObjectId(drillDownObjectId);
                singleCardDTO.setShowDrillDownObjectId(mapGroupId.get(singleCardDTO.getDrillDownObjectId()));
            }
            singleCardDTO.setGroupId(groupId);
            if(!DataUtil.isNullOrZero(singleCardDTO.getGroupId())) {
                singleCardDTO.setGroupName(groupName);
            }
            singleCardDTO.setServiceId(serviceId);
            if(!DataUtil.isNullOrZero(singleCardDTO.getServiceId())) {
                singleCardDTO.setServiceName(mapService.get(serviceId));
            }
            singleCardDTO.setStatus(status);
            if(status != null) {
                if(singleCardDTO.getStatus() == 1) {
                    singleCardDTO.setShowStatus(I18N.get(Constants.COMMON_MESSAGE_STATUS_WORK));
                } else {
                    singleCardDTO.setShowStatus(I18N.get(Constants.COMMON_MESSAGE_STATUS_NOT_WORK));
                }
            }
            if(configSingleCard[11] != null) {
                singleCardDTO.setZoom((Integer) configSingleCard[11]);
                if(singleCardDTO.getZoom() == 1) {
                    singleCardDTO.setShowZoom(I18N.get(Constants.COMMON_TABLE_WARNING_YES));
                } else {
                    singleCardDTO.setShowZoom(I18N.get(Constants.COMMON_TABLE_WARNING_NO));
                }
            }
            singleCardDTO.setCardType(cardType);
            if(!DataUtil.isNullOrEmpty(singleCardDTO.getCardType())){
                singleCardDTO.setNameCardType(description);
            }
            singleCardDTOList.add(singleCardDTO);
        }
        return singleCardDTOList;
    }

    @Override
    public String insertConfigSingleCard(ConfigSingleCardDTO configSingleCardDTO, StaffDTO staffDTO) throws Exception {
        String message;
//        if (configSingleCardDTO.getServiceId() == -1) {
//            configSingleCardDTO.setServiceId(null);
//        }
        ConfigSingleCard configSingleCard = new ConfigSingleCard();
        if (!DataUtil.isNullOrEmpty(configSingleCardDTO.getCardName())) {
            configSingleCard.setCardName(configSingleCardDTO.getCardName().trim());
        }
        if (!DataUtil.isNullOrEmpty(configSingleCardDTO.getCardNameI18n())) {
            configSingleCard.setCardNameI18n(configSingleCardDTO.getCardNameI18n().trim());
        }
        if (!DataUtil.isNullOrEmpty(configSingleCardDTO.getCardSize())) {
            configSingleCard.setCardSize(configSingleCardDTO.getCardSize().trim());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String string = simpleDateFormat.format(new Date());
        Date date = simpleDateFormat.parse(string);
        configSingleCard.setCreateDate(date);
        if (configSingleCardDTO.getDrilldown() != null) {
            configSingleCard.setDrilldown(configSingleCardDTO.getDrilldown());
        }
        if (!DataUtil.isNullOrZero(configSingleCardDTO.getDrillDownType())) {
            configSingleCard.setDrillDownType(configSingleCardDTO.getDrillDownType());
        }
        if (!DataUtil.isNullOrZero(configSingleCardDTO.getDrillDownObjectId())) {
            configSingleCard.setDrillDownObjectId(configSingleCardDTO.getDrillDownObjectId());
        }
        if (!DataUtil.isNullOrZero(configSingleCardDTO.getGroupId())) {
            configSingleCard.setGroupId(configSingleCardDTO.getGroupId());
        }
        if (!DataUtil.isNullOrZero(configSingleCardDTO.getServiceId())) {
            configSingleCard.setServiceId(configSingleCardDTO.getServiceId());
        }
        configSingleCard.setStatus(Integer.parseInt(Constants.PARAM_STATUS));
        if (configSingleCardDTO.getZoom() != null) {
            configSingleCard.setZoom(configSingleCardDTO.getZoom());
        }
        if (!DataUtil.isNullOrEmpty(configSingleCardDTO.getCardType())) {
            configSingleCard.setCardType(configSingleCardDTO.getCardType().trim());
        }
        List<Object[]> checkDuplicate = configSingleCardRepo.checkDuplicate(configSingleCard, Constants.INSERT);
        if (configSingleCard.getDrillDownObjectId() != null && configSingleCard.getGroupId() != null) {
            if (!configSingleCard.getGroupId().toString().equals(configSingleCard.getDrillDownObjectId().toString())) {
                if (checkDuplicate.size() == 0) {
                    ConfigSingleCard configSingleCardNew = configSingleCardRepo.save(configSingleCard);
                    ActionAudit actionAudit = actionAuditService.log(Constants.CONFIG_SINGLE_CARD.CONFIG_SINGLE_CARD, Constants.ACTION_CODE_ADD, staffDTO.getStaffCode(), configSingleCardNew.getCardId(), staffDTO.getShopCode());
                    if (!DataUtil.isNullOrEmpty(configSingleCard.getCardName())) {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_NAME, configSingleCard.getCardName(), actionAudit.getId(), null);
                    }
                    if (!DataUtil.isNullOrEmpty(configSingleCard.getCardNameI18n())) {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_NAME_I18N, configSingleCard.getCardNameI18n(), actionAudit.getId(), null);
                    }
                    if (!DataUtil.isNullOrEmpty(configSingleCard.getCardSize())) {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_SIZE, configSingleCard.getCardSize(), actionAudit.getId(), null);
                    }
                    if (configSingleCard.getDrilldown() != null) {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN, configSingleCard.getDrilldown().toString(), actionAudit.getId(), null);
                    }
                    if (!DataUtil.isNullOrZero(configSingleCard.getDrillDownType())) {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_TYPE, configSingleCard.getDrillDownType().toString(), actionAudit.getId(), null);
                    }
                    if (!DataUtil.isNullOrZero(configSingleCard.getDrillDownObjectId())) {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_OBJECT_ID, configSingleCard.getDrillDownObjectId().toString(), actionAudit.getId(), null);
                    }
                    if (!DataUtil.isNullOrZero(configSingleCard.getGroupId())) {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.GROUP_ID, configSingleCard.getGroupId().toString(), actionAudit.getId(), null);
                    }
                    if (!DataUtil.isNullOrZero(configSingleCard.getServiceId())) {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.SERVICE_ID, configSingleCard.getServiceId().toString(), actionAudit.getId(), null);
                    }
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.STATUS, Constants.PARAM_STATUS, actionAudit.getId(), null);
                    if (configSingleCard.getZoom() != null) {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.ZOOM, configSingleCard.getZoom().toString(), actionAudit.getId(), null);
                    }
                    if (!DataUtil.isNullOrEmpty(configSingleCard.getCardType())) {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_TYPE, configSingleCard.getCardType(), actionAudit.getId(), null);
                    }
                    message = Constants.WARNINGSEND.SUCCESS;
                } else {
                    message = Constants.MESSAGE_CODE.DUPLICATE;
                }
            } else {
                message = Constants.CONFIG_SINGLE_CARD.DUPLICATE_GROUP;
            }
        } else {
            if (checkDuplicate.size() == 0) {
                ConfigSingleCard configSingleCardNew = configSingleCardRepo.save(configSingleCard);
                ActionAudit actionAudit = actionAuditService.log(Constants.CONFIG_SINGLE_CARD.CONFIG_SINGLE_CARD, Constants.ACTION_CODE_ADD, staffDTO.getStaffCode(), configSingleCardNew.getCardId(), staffDTO.getShopCode());
                if (!DataUtil.isNullOrEmpty(configSingleCard.getCardName())) {
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_NAME, configSingleCard.getCardName(), actionAudit.getId(), null);
                }
                if (!DataUtil.isNullOrEmpty(configSingleCard.getCardNameI18n())) {
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_NAME_I18N, configSingleCard.getCardNameI18n(), actionAudit.getId(), null);
                }
                if (!DataUtil.isNullOrEmpty(configSingleCard.getCardSize())) {
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_SIZE, configSingleCard.getCardSize(), actionAudit.getId(), null);
                }
                if (configSingleCard.getDrilldown() != null) {
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN, configSingleCard.getDrilldown().toString(), actionAudit.getId(), null);
                }
                if (!DataUtil.isNullOrZero(configSingleCard.getDrillDownType())) {
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_TYPE, configSingleCard.getDrillDownType().toString(), actionAudit.getId(), null);
                }
                if (!DataUtil.isNullOrZero(configSingleCard.getDrillDownObjectId())) {
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_OBJECT_ID, configSingleCard.getDrillDownObjectId().toString(), actionAudit.getId(), null);
                }
                if (!DataUtil.isNullOrZero(configSingleCard.getGroupId())) {
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.GROUP_ID, configSingleCard.getGroupId().toString(), actionAudit.getId(), null);
                }
                if (!DataUtil.isNullOrZero(configSingleCard.getServiceId())) {
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.SERVICE_ID, configSingleCard.getServiceId().toString(), actionAudit.getId(), null);
                }
                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.STATUS, Constants.PARAM_STATUS, actionAudit.getId(), null);
                if (configSingleCard.getZoom() != null) {
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.ZOOM, configSingleCard.getZoom().toString(), actionAudit.getId(), null);
                }
                if (!DataUtil.isNullOrEmpty(configSingleCard.getCardType())) {
                    actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_TYPE, configSingleCard.getCardType(), actionAudit.getId(), null);
                }
                message = Constants.WARNINGSEND.SUCCESS;
            } else {
                message = Constants.MESSAGE_CODE.DUPLICATE;
            }
        }
        return message;
    }

    @Override
    public String updateConfigSingleCard(ConfigSingleCard configSingleCard, StaffDTO staffDTO) throws Exception {
        String message;
        if(configSingleCard.getGroupId() != null && configSingleCard.getDrillDownObjectId() != null) {
            if (configSingleCard.getGroupId().toString().trim().equals(configSingleCard.getDrillDownObjectId().toString().trim())) {
                message = Constants.CONFIG_SINGLE_CARD.DUPLICATE_GROUP;
            } else {
                List<Object[]> checkDuplicate = configSingleCardRepo.checkDuplicate(configSingleCard, Constants.UPDATE);
                if (checkDuplicate.size() == 0) {
                    ActionAudit actionAudit = actionAuditService.log(Constants.CONFIG_SINGLE_CARD.CONFIG_SINGLE_CARD, Constants.ACTION_CODE_EDIT, staffDTO.getStaffCode(), configSingleCard.getCardId(), staffDTO.getShopCode());
                    ConfigSingleCard cardOld = configSingleCardRepo.getOne(configSingleCard.getCardId());
                    if (!DataUtil.isNullOrEmpty(configSingleCard.getCardName())) {
                        if (!DataUtil.isNullOrEmpty(cardOld.getCardName())) {
                            if (!cardOld.getCardName().trim().equals(configSingleCard.getCardName().trim())) {
                                configSingleCard.setCardName(configSingleCard.getCardName().trim());
                                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_NAME, configSingleCard.getCardName(), actionAudit.getId(), DataUtil.safeToString(cardOld.getCardName()));
                            }
                        } else {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_NAME, configSingleCard.getCardName(), actionAudit.getId(), null);
                        }
                    } else {
                        configSingleCard.setCardName(cardOld.getCardName());
                    }

                    if (!DataUtil.isNullOrEmpty(configSingleCard.getCardSize())) {
                        if (!DataUtil.isNullOrEmpty(cardOld.getCardSize())) {
                            if (!cardOld.getCardSize().equals(configSingleCard.getCardSize())) {
                                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_SIZE, configSingleCard.getCardSize(), actionAudit.getId(), DataUtil.safeToString(cardOld.getCardSize()));
                            }
                        } else {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_SIZE, configSingleCard.getCardSize(), actionAudit.getId(), null);
                        }
                    } else {
                        configSingleCard.setCardSize(cardOld.getCardSize());
                    }

                    if (configSingleCard.getDrilldown() != null) {
                        if (cardOld.getDrilldown() != null) {
                            if (cardOld.getDrilldown() != configSingleCard.getDrilldown()) {
                                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN, configSingleCard.getDrilldown().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getDrilldown()));
                            }
                        } else {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN, configSingleCard.getDrilldown().toString(), actionAudit.getId(), null);
                        }
                    } else {
                        configSingleCard.setDrilldown(cardOld.getDrilldown());
                    }
                    if (configSingleCard.getDrillDownType() != null) {
                        if (cardOld.getDrillDownType() != null) {
                            if (cardOld.getDrillDownType() != configSingleCard.getDrillDownType()) {
                                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_TYPE, configSingleCard.getDrillDownType().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getDrillDownType()));
                            }
                        } else {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_TYPE, configSingleCard.getDrillDownType().toString(), actionAudit.getId(), null);
                        }
                    } else {
                        configSingleCard.setDrillDownType(cardOld.getDrillDownType());
                    }
                    if (configSingleCard.getDrillDownObjectId() != null) {
                        if (cardOld.getDrillDownObjectId() != null) {
                            if (cardOld.getDrillDownObjectId() != configSingleCard.getDrillDownObjectId()) {
                                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_OBJECT_ID, configSingleCard.getDrillDownObjectId().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getDrillDownObjectId()));
                            }
                        } else {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_OBJECT_ID, configSingleCard.getDrillDownObjectId().toString(), actionAudit.getId(), null);
                        }
                    }
                    if (!DataUtil.isNullOrZero(configSingleCard.getGroupId())) {
                        if (!DataUtil.isNullOrZero(cardOld.getGroupId())) {
                            if (!cardOld.getGroupId().toString().equals(configSingleCard.getGroupId().toString())) {
                                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.GROUP_ID, configSingleCard.getGroupId().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getGroupId()));
                            }
                        } else {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.GROUP_ID, configSingleCard.getGroupId().toString(), actionAudit.getId(), null);
                        }
                    } else {
                        configSingleCard.setGroupId(configSingleCard.getGroupId());
                    }
                    if (!DataUtil.isNullOrZero(configSingleCard.getServiceId())) {
                        if (!DataUtil.isNullOrZero(cardOld.getServiceId())) {
                            if (cardOld.getServiceId() != configSingleCard.getServiceId()) {
                                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.SERVICE_ID, configSingleCard.getServiceId().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getServiceId()));
                            }
                        } else {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.SERVICE_ID, configSingleCard.getServiceId().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getServiceId()));
                        }
                    }
                    if (configSingleCard.getStatus() != null) {
                        if (cardOld.getStatus() != null) {
                            if (cardOld.getStatus() != configSingleCard.getStatus()) {
                                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.STATUS, configSingleCard.getStatus().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getStatus()));
                            }
                        } else {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.STATUS, configSingleCard.getStatus().toString(), actionAudit.getId(), null);
                        }
                    } else {
                        configSingleCard.setStatus(cardOld.getStatus());
                    }
                    if (configSingleCard.getZoom() != null) {
                        if (cardOld.getZoom() != null) {
                            if (cardOld.getZoom() != configSingleCard.getZoom()) {
                                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.ZOOM, configSingleCard.getZoom().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getZoom()));
                            }
                        } else {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.ZOOM, configSingleCard.getZoom().toString(), actionAudit.getId(), null);
                        }
                    }
                    if (!DataUtil.isNullOrEmpty(configSingleCard.getCardType())) {
                        if (!DataUtil.isNullOrEmpty(cardOld.getCardType())) {
                            if (!cardOld.getCardType().equals(configSingleCard.getCardType())) {
                                actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_TYPE, configSingleCard.getCardType(), actionAudit.getId(), DataUtil.safeToString(cardOld.getCardType()));
                            }
                        } else {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_TYPE, configSingleCard.getCardType(), actionAudit.getId(), null);
                        }
                    } else {
                        configSingleCard.setCardType(cardOld.getCardType());
                    }
                    configSingleCard.setCardNameI18n(cardOld.getCardNameI18n());
                    configSingleCard.setCreateDate(cardOld.getCreateDate());
                    configSingleCardRepo.updateSingleCard(configSingleCard);
                    message = Constants.SUCCESS;
                } else {
                    message = Constants.MESSAGE_CODE.DUPLICATE;
                }
            }
        } else {
            List<Object[]> checkDuplicate = configSingleCardRepo.checkDuplicate(configSingleCard, Constants.UPDATE);
            if (checkDuplicate.size() == 0) {
                ActionAudit actionAudit = actionAuditService.log(Constants.CONFIG_SINGLE_CARD.CONFIG_SINGLE_CARD, Constants.ACTION_CODE_EDIT, staffDTO.getStaffCode(), configSingleCard.getCardId(), staffDTO.getShopCode());
                ConfigSingleCard cardOld = configSingleCardRepo.getOne(configSingleCard.getCardId());
                if (!DataUtil.isNullOrEmpty(configSingleCard.getCardName())) {
                    if (!DataUtil.isNullOrEmpty(cardOld.getCardName())) {
                        if (!cardOld.getCardName().trim().equals(configSingleCard.getCardName().trim())) {
                            configSingleCard.setCardName(configSingleCard.getCardName().trim());
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_NAME, configSingleCard.getCardName(), actionAudit.getId(), DataUtil.safeToString(cardOld.getCardName()));
                        }
                    } else {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_NAME, configSingleCard.getCardName(), actionAudit.getId(), null);
                    }
                } else {
                    configSingleCard.setCardName(cardOld.getCardName());
                }

                if (!DataUtil.isNullOrEmpty(configSingleCard.getCardSize())) {
                    if (!DataUtil.isNullOrEmpty(cardOld.getCardSize())) {
                        if (!cardOld.getCardSize().equals(configSingleCard.getCardSize())) {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_SIZE, configSingleCard.getCardSize(), actionAudit.getId(), DataUtil.safeToString(cardOld.getCardSize()));
                        }
                    } else {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_SIZE, configSingleCard.getCardSize(), actionAudit.getId(), null);
                    }
                } else {
                    configSingleCard.setCardSize(cardOld.getCardSize());
                }

                if (configSingleCard.getDrilldown() != null) {
                    if (cardOld.getDrilldown() != null) {
                        if (cardOld.getDrilldown() != configSingleCard.getDrilldown()) {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN, configSingleCard.getDrilldown().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getDrilldown()));
                        }
                    } else {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN, configSingleCard.getDrilldown().toString(), actionAudit.getId(), null);
                    }
                } else {
                    configSingleCard.setDrilldown(cardOld.getDrilldown());
                }
                if (configSingleCard.getDrillDownType() != null) {
                    if (cardOld.getDrillDownType() != null) {
                        if (cardOld.getDrillDownType() != configSingleCard.getDrillDownType()) {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_TYPE, configSingleCard.getDrillDownType().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getDrillDownType()));
                        }
                    } else {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_TYPE, configSingleCard.getDrillDownType().toString(), actionAudit.getId(), null);
                    }
                } else {
                    configSingleCard.setDrillDownType(cardOld.getDrillDownType());
                }
                if (configSingleCard.getDrillDownObjectId() != null) {
                    if (cardOld.getDrillDownObjectId() != null) {
                        if (cardOld.getDrillDownObjectId() != configSingleCard.getDrillDownObjectId()) {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_OBJECT_ID, configSingleCard.getDrillDownObjectId().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getDrillDownObjectId()));
                        }
                    } else {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.DRILL_DOWN_OBJECT_ID, configSingleCard.getDrillDownObjectId().toString(), actionAudit.getId(), null);
                    }
                }
                if (!DataUtil.isNullOrZero(configSingleCard.getGroupId())) {
                    if (!DataUtil.isNullOrZero(cardOld.getGroupId())) {
                        if (!cardOld.getGroupId().toString().equals(configSingleCard.getGroupId().toString())) {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.GROUP_ID, configSingleCard.getGroupId().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getGroupId()));
                        }
                    } else {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.GROUP_ID, configSingleCard.getGroupId().toString(), actionAudit.getId(), null);
                    }
                } else {
                    configSingleCard.setGroupId(configSingleCard.getGroupId());
                }
                if (!DataUtil.isNullOrZero(configSingleCard.getServiceId())) {
                    if (!DataUtil.isNullOrZero(cardOld.getServiceId())) {
                        if (cardOld.getServiceId() != configSingleCard.getServiceId()) {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.SERVICE_ID, configSingleCard.getServiceId().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getServiceId()));
                        }
                    } else {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.SERVICE_ID, configSingleCard.getServiceId().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getServiceId()));
                    }
                }
                if (configSingleCard.getStatus() != null) {
                    if (cardOld.getStatus() != null) {
                        if (cardOld.getStatus() != configSingleCard.getStatus()) {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.STATUS, configSingleCard.getStatus().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getStatus()));
                        }
                    } else {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.STATUS, configSingleCard.getStatus().toString(), actionAudit.getId(), null);
                    }
                } else {
                    configSingleCard.setStatus(cardOld.getStatus());
                }
                if (configSingleCard.getZoom() != null) {
                    if (cardOld.getZoom() != null) {
                        if (cardOld.getZoom() != configSingleCard.getZoom()) {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.ZOOM, configSingleCard.getZoom().toString(), actionAudit.getId(), DataUtil.safeToString(cardOld.getZoom()));
                        }
                    } else {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.ZOOM, configSingleCard.getZoom().toString(), actionAudit.getId(), null);
                    }
                }
                if (!DataUtil.isNullOrEmpty(configSingleCard.getCardType())) {
                    if (!DataUtil.isNullOrEmpty(cardOld.getCardType())) {
                        if (!cardOld.getCardType().equals(configSingleCard.getCardType())) {
                            actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_TYPE, configSingleCard.getCardType(), actionAudit.getId(), DataUtil.safeToString(cardOld.getCardType()));
                        }
                    } else {
                        actionDetailService.createActionDetail(Constants.CONFIG_SINGLE_CARD.CARD_TYPE, configSingleCard.getCardType(), actionAudit.getId(), null);
                    }
                } else {
                    configSingleCard.setCardType(cardOld.getCardType());
                }
                configSingleCard.setCardNameI18n(cardOld.getCardNameI18n());
                configSingleCard.setCreateDate(cardOld.getCreateDate());
                configSingleCardRepo.updateSingleCard(configSingleCard);
                message = Constants.SUCCESS;
            } else {
                message = Constants.MESSAGE_CODE.DUPLICATE;
            }
        }
        return message;
    }

    @Override
    public List<ConfigGroupCard> listGroupCard() throws Exception {
        List<Object[]> listObject = configGroupCardRepo.getAllOrderByNameAsc();
        List<ConfigGroupCard> list = new ArrayList<>();
        for(Object[] objects : listObject) {
            ConfigGroupCard configGroupCard = new ConfigGroupCard();
            configGroupCard.setGroupId(DataUtil.safeToLong(objects[0]));
            configGroupCard.setGroupName(DataUtil.safeToString(objects[5]));
            list.add(configGroupCard);
        }
        return list;
    }
}
