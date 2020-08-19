package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.ConfigRolesObjects;
import vn.vissoft.dashboard.repo.ConfigRolesObjectsRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.ConfigRoleObjectService;

@Service
public class ConfigRoleObjectServiceImpl implements ConfigRoleObjectService {

    @Autowired
    ConfigRolesObjectsRepo configRolesObjectsRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    @Override
    public void createRoleObject(ConfigRolesObjects configRolesObjects, StaffDTO staffDTO) {

        if (configRolesObjects.getId() == null) {
            ConfigRolesObjects configRolesObjectsNew = configRolesObjectsRepo.saveAndFlush(configRolesObjects);
            ActionAudit actionAudit = actionAuditService.log(Constants.OBJECTROLELOG, Constants.CREATE, staffDTO.getStaffCode(), configRolesObjectsNew.getId(), staffDTO.getShopCode());
            actionDetailService.addLogObjectRole(configRolesObjects, null, actionAudit.getId());
        } else {
            ConfigRolesObjects oldConfigRolesObjects = configRolesObjectsRepo.findById(configRolesObjects.getId()).get();
            ActionAudit actionAudit = actionAuditService.log(Constants.OBJECTROLELOG, Constants.EDIT, staffDTO.getStaffCode(), configRolesObjects.getId(), staffDTO.getShopCode());
            actionDetailService.addLogObjectRole(configRolesObjects, oldConfigRolesObjects, actionAudit.getId());
            configRolesObjectsRepo.save(configRolesObjects);

        }

    }
}
