package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.ActionDetail;
import vn.vissoft.dashboard.model.ServiceWarningConfig;
import vn.vissoft.dashboard.repo.ActionDetailRepo;
import vn.vissoft.dashboard.repo.ServiceWarningConfigRepo;
import vn.vissoft.dashboard.repo.WarningConfigRepoCustom;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.GroupServiceService;
import vn.vissoft.dashboard.services.ServiceWarningService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class ServiceWarningServiceImpl implements ServiceWarningService {

    @Autowired
    private ServiceWarningConfigRepo serviceWarningConfigRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private GroupServiceService groupServiceService;

    @Autowired
    private WarningConfigRepoCustom warningConfigRepo;
    @Autowired
    private ActionDetailRepo actionDetailRepo;

    @Override
    public String update(ServiceWarningConfig serviceWarningConfig, StaffDTO staffDTO) throws Exception {
        List<ServiceWarningConfig> list = getAllIdWarningService(serviceWarningConfig.getId(), serviceWarningConfig.getServiceId(), serviceWarningConfig.getWaningLevel(), serviceWarningConfig.getVdsChannelCode(), serviceWarningConfig.getStatus());
        if (list.size() > 0) {
            return Constants.SERVICE_WARNING_CONFIGS.DUPLICATE;
        }
        ;
//        if(serviceWarningConfig.getStatus().equals("0")){
//            return Constants.SERVICE_WARNING_CONFIGS.SUCCESS;
//        };
        if ("1".equals(serviceWarningConfig.getStatus())) {
            List<ServiceWarningConfig> list1 = getCheckDuplicateUpdate(serviceWarningConfig.getId(), serviceWarningConfig.getServiceId(), serviceWarningConfig.getVdsChannelCode(), serviceWarningConfig.getFromValue(), serviceWarningConfig.getToValue(), serviceWarningConfig.getStatus());
            if (list1.size() > 0) {
                return Constants.SERVICE_WARNING_CONFIGS.DUPLICATE_VALUE;
            }
            ;
        }
        if (serviceWarningConfig.getExp() != null) {
            serviceWarningConfig.setExp(serviceWarningConfig.getExp().trim());
        }
        ServiceWarningConfig oldValue = serviceWarningConfigRepo.getOne(serviceWarningConfig.getId());
        ActionAudit actionAudit = actionAuditService.log(Constants.SERVICE_WARNING_CONFIG, Constants.EDIT, staffDTO.getStaffCode(), serviceWarningConfig.getId(), staffDTO.getShopCode());
        saveActionDetail(oldValue, serviceWarningConfig, actionAudit.getId());
        // serviceWarningConfig = serviceWarningConfigRepo.save(serviceWarningConfig);
        return Constants.SERVICE_WARNING_CONFIGS.SUCCESS;
    }

    @Override
    public String addWarning(ServiceWarningConfig serviceWarningConfig, StaffDTO staffDTO) throws Exception {
        List<ServiceWarningConfig> list = getAllByServiceIdAndWarningLevel(serviceWarningConfig.getServiceId(), serviceWarningConfig.getWaningLevel(), serviceWarningConfig.getVdsChannelCode(), serviceWarningConfig.getStatus());
        if (list.size() > 0) {
            return Constants.SERVICE_WARNING_CONFIGS.DUPLICATE;
        }
        ;
        List<ServiceWarningConfig> list1 = getCheckDuplicateAdd(serviceWarningConfig.getServiceId(), serviceWarningConfig.getVdsChannelCode(), serviceWarningConfig.getFromValue(), serviceWarningConfig.getToValue(), serviceWarningConfig.getStatus());
        if (list1.size() > 0) {
            return Constants.SERVICE_WARNING_CONFIGS.DUPLICATE_VALUE;
        }
        ;

        if (serviceWarningConfig.getExp() != null) {
            serviceWarningConfig.setExp(serviceWarningConfig.getExp().trim());
        }

        serviceWarningConfig = serviceWarningConfigRepo.saveAndFlush(serviceWarningConfig);
        ActionAudit actionAudit = actionAuditService.log(Constants.SERVICE_WARNING_CONFIG, Constants.CREATE, staffDTO.getStaffCode(), serviceWarningConfig.getId(), staffDTO.getShopCode());
        saveNewActionDetail(serviceWarningConfig, actionAudit.getId());

        return Constants.SERVICE_WARNING_CONFIGS.SUCCESS;
    }

    @Override
    public List<ServiceWarningConfig> getAllByServiceIdAndWarningLevel(Long Service, int WarningLV, String vdsCode, String status) throws Exception {
        return warningConfigRepo.checkDuplicateWarningConfig(Service, WarningLV, vdsCode, status);
    }

    @Override
    public List<ServiceWarningConfig> getAllIdWarningService(Long id, Long Service, int WarningLV, String vdsCode, String status) throws Exception {
        return warningConfigRepo.checkDuplicateWarningConfig1(id, Service, WarningLV, vdsCode, status);
    }

    @Override
    public List<ServiceWarningConfig> getCheckDuplicateAdd(Long Service, String vdsCode, Double fromvalue, Double tovalue, String status) throws Exception {
        return warningConfigRepo.checkDuplicateValue(Service, vdsCode, fromvalue, tovalue, status);
    }

    @Override
    public List<ServiceWarningConfig> getCheckDuplicateUpdate(Long id, Long Service, String vdsCode, Double fromvalue, Double tovalue, String status) throws Exception {
        return warningConfigRepo.checkDuplicateValue1(id, Service, vdsCode, fromvalue, tovalue, status);
    }

    @Override
    public void saveNewActionDetail(ServiceWarningConfig newserviceWarningConfig, Long actionId) throws Exception {
        if (newserviceWarningConfig.getId() != null) {

        }
        saveActionDetail1(actionId, null, newserviceWarningConfig.getServiceId().toString(), Constants.SERVICE_WARNING_CONFIGS.SERVICE_ID);
        if(!DataUtil.isNullOrEmpty(newserviceWarningConfig.getExp()))
        saveActionDetail1(actionId, null, newserviceWarningConfig.getExp(), Constants.SERVICE_WARNING_CONFIGS.EXP);
        saveActionDetail1(actionId, null, newserviceWarningConfig.getStatus(), Constants.SERVICE_WARNING_CONFIGS.STATUS);
        saveActionDetail1(actionId, null, newserviceWarningConfig.getVdsChannelCode(), Constants.SERVICE_WARNING_CONFIGS.VDS_CHANNEL_CODE);
        saveActionDetail1(actionId, null, newserviceWarningConfig.getFromValue().toString(), Constants.SERVICE_WARNING_CONFIGS.FROM_VALUE);
        saveActionDetail1(actionId, null, newserviceWarningConfig.getToValue().toString(), Constants.SERVICE_WARNING_CONFIGS.TO_VALUE);
        saveActionDetail1(actionId, null, newserviceWarningConfig.getWaningLevel().toString(), Constants.SERVICE_WARNING_CONFIGS.WARNING_LEVEL);

    }

    @Override
    public void saveActionDetail1(Long actionID, String oldValue, String newValue, String column) throws Exception {
        ActionDetail actionDetail = new ActionDetail();
        actionDetail.setActionAuditId(actionID);
        if (oldValue != null) {
            actionDetail.setOldValue(oldValue);
        }
        if (newValue != null) {
            actionDetail.setNewValue(newValue);
        }
        actionDetail.setColumnName(column);
        actionDetailRepo.save(actionDetail);
    }

    @Override
    public void saveActionDetail(ServiceWarningConfig oldServiceWarning, ServiceWarningConfig newServiceWarning, Long plngActionId) throws Exception {
        if (oldServiceWarning.getId() == null && newServiceWarning.getId() == null) {
        } else if (oldServiceWarning.getId() == null) {
            saveActionDetail1(plngActionId, null, newServiceWarning.getId().toString(), Constants.SERVICE_WARNING_CONFIGS.ID_SERVICE_WARNING);
        } else {
            if (!oldServiceWarning.getStatus().equals(newServiceWarning.getStatus())) {
                saveActionDetail1(plngActionId, oldServiceWarning.getStatus(), newServiceWarning.getStatus(), Constants.SERVICE_WARNING_CONFIGS.STATUS);
            }

            if (!oldServiceWarning.getVdsChannelCode().trim().equalsIgnoreCase(newServiceWarning.getVdsChannelCode().trim())) {
                saveActionDetail1(plngActionId, oldServiceWarning.getVdsChannelCode(), newServiceWarning.getVdsChannelCode(), Constants.SERVICE_WARNING_CONFIGS.VDS_CHANNEL_CODE);
            }

            if (oldServiceWarning.getWaningLevel() != newServiceWarning.getWaningLevel()) {
                saveActionDetail1(plngActionId, oldServiceWarning.getWaningLevel().toString(), newServiceWarning.getWaningLevel().toString(), Constants.SERVICE_WARNING_CONFIGS.WARNING_LEVEL);
            }

            if (Double.compare(oldServiceWarning.getFromValue(), newServiceWarning.getFromValue()) != 0) {
//        if (oldServiceWarning.getFromValue().equals(newServiceWarning.getFromValue())) {
                saveActionDetail1(plngActionId, oldServiceWarning.getFromValue().toString(), newServiceWarning.getFromValue().toString(), Constants.SERVICE_WARNING_CONFIGS.FROM_VALUE);
            }

            if (Double.compare(oldServiceWarning.getToValue(), newServiceWarning.getToValue()) != 0) {
//        if (!oldServiceWarning.getToValue().equals(newServiceWarning.getToValue())) {
                saveActionDetail1(plngActionId, oldServiceWarning.getToValue().toString(), newServiceWarning.getToValue().toString(), Constants.SERVICE_WARNING_CONFIGS.TO_VALUE);
            }

            if (!oldServiceWarning.getServiceId().equals(newServiceWarning.getServiceId())) {
                saveActionDetail1(plngActionId, oldServiceWarning.getServiceId().toString(), newServiceWarning.getServiceId().toString(), Constants.SERVICE_WARNING_CONFIGS.SERVICE_ID);
            }
            if (oldServiceWarning.getExp() != null && newServiceWarning.getExp() != null) {
                if (!oldServiceWarning.getExp().trim().equalsIgnoreCase(newServiceWarning.getExp().trim())) {
                    saveActionDetail1(plngActionId, oldServiceWarning.getExp(), newServiceWarning.getExp(), Constants.SERVICE_WARNING_CONFIGS.EXP);
                }
            } else if (oldServiceWarning.getExp() == null && newServiceWarning.getExp() != null) {
                saveActionDetail1(plngActionId, null, newServiceWarning.getExp(), Constants.SERVICE_WARNING_CONFIGS.EXP);
            } else if (oldServiceWarning.getExp() != null && newServiceWarning.getExp() == null) {
                saveActionDetail1(plngActionId, oldServiceWarning.getExp(), null, Constants.SERVICE_WARNING_CONFIGS.EXP);
            }else if(oldServiceWarning.getExp() == null && newServiceWarning.getExp() == null){
            }
            serviceWarningConfigRepo.save(newServiceWarning);
        }
    }
}

