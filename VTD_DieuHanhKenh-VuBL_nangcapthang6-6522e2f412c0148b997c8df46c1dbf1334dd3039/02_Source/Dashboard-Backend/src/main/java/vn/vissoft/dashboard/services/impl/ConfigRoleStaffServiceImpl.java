package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.RoleStaffDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.ActionDetailRepo;
import vn.vissoft.dashboard.repo.ConfigRolesRepo;
import vn.vissoft.dashboard.repo.ConfigRolesStaffRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ConfigRoleStaffService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigRoleStaffServiceImpl implements ConfigRoleStaffService {

    @Autowired
    ConfigRolesStaffRepo configRolesStaffRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailRepo actionDetailRepo;

    @Autowired
    private ConfigRolesRepo configRolesRepo;

    @Override
    public List<ConfigRolesStaff> getAllConfigRoleStaff() {
        return configRolesStaffRepo.findAll();
    }

    @Override
    public void save(String[] roleId, String staffCode, StaffDTO user) throws Exception {
        List<ConfigRolesStaff> listRoleStaff = configRolesStaffRepo.getAllByStaffCode(staffCode);
        List<String> listRoleId = new ArrayList<>();
        for (ConfigRolesStaff roleStaff : listRoleStaff) {
            listRoleId.add(roleStaff.getRoleId().toString());
        }
        for (int i = 0; i < roleId.length; i++) {
            if (!listRoleId.contains(roleId[i].trim())) {
                ConfigRolesStaff configRolesStaff = new ConfigRolesStaff();
                configRolesStaff.setRoleId(Long.parseLong(roleId[i]));
                configRolesStaff.setStaffCode(staffCode);
                configRolesStaff.setStatus(Constants.STATUS.ACTIVE);
                ConfigRolesStaff conffigRoleS = configRolesStaffRepo.saveAndFlush(configRolesStaff);
                ActionAudit actionAudit = actionAuditService.log(Constants.ROLESTAFF.ROLESTAFFECT, Constants.ACTION_CODE_ADD, user.getStaffCode(), conffigRoleS.getId(), user.getShopCode());
                saveToActionDetail(conffigRoleS, actionAudit.getId());
            } else {
                Optional<ConfigRolesStaff> optional = configRolesStaffRepo.getByRoleIdAndStaffCode(Long.parseLong(roleId[i]),staffCode);
                if (optional.isPresent()) {
                    ConfigRolesStaff configRolesStaff = optional.get();
                    if (configRolesStaff.getStatus() == Constants.STATUS.INACTIVE) {
                        configRolesStaff.setStatus(Constants.STATUS.ACTIVE);
                        configRolesStaffRepo.save(configRolesStaff);
                        ActionAudit actionAudit = actionAuditService.log(Constants.ROLESTAFF.ROLESTAFFECT, Constants.ACTION_CODE_EDIT, user.getStaffCode(), configRolesStaff.getId(), user.getShopCode());
                        saveActionDetail(actionAudit.getId(), Constants.PARAM_STATUS_0, Constants.PARAM_STATUS, Constants.ROLESTAFF.STATUS);
                    }
                }
            }
        }
        for (int i = 0; i < listRoleId.size(); i++) {
            if (!Arrays.asList(roleId).contains(listRoleId.get(i).trim())) {
                Optional<ConfigRolesStaff> optional = configRolesStaffRepo.getByRoleIdAndStaffCode(Long.parseLong(listRoleId.get(i)),staffCode);
                if (optional.isPresent()) {
                    ConfigRolesStaff configRolesStaff = optional.get();
                    configRolesStaff.setStatus(Constants.STATUS.INACTIVE);
                    configRolesStaffRepo.save(configRolesStaff);
                    ActionAudit actionAudit = actionAuditService.log(Constants.ROLESTAFF.ROLESTAFFECT, Constants.ACTION_CODE_EDIT, user.getStaffCode(), configRolesStaff.getId(), user.getShopCode());
                    saveActionDetail(actionAudit.getId(), Constants.PARAM_STATUS, Constants.PARAM_STATUS_0, Constants.ROLESTAFF.STATUS);
                }
            }
        }
//        configRolesStaffRepo.saveAll(listSave);
    }

    @Override
    public List<ConfigRolesStaff> getAllActive(int status) throws Exception {
        return configRolesStaffRepo.getAllByStatus(status);
    }

    @Override
    public List<RoleStaffDTO> getByStaffCode(String staffcode) throws Exception {
        List<ConfigRolesStaff> vlistRoleStaff = configRolesStaffRepo.getAllByStaffCode(staffcode);
        List<ConfigRoles> vlistRole = configRolesRepo.findAllByStatusOrderByRoleName(1L);
        List<RoleStaffDTO> vlistRoleStaffDTO = new ArrayList<>();
        vlistRole.forEach(configRoles -> {
            RoleStaffDTO roleStaffDTO = new RoleStaffDTO();
            roleStaffDTO.setCode(configRoles.getRoleCode());
            roleStaffDTO.setId(configRoles.getId());
            roleStaffDTO.setName(configRoles.getRoleName());
            roleStaffDTO.setDescription(configRoles.getRoleDescription());
            vlistRoleStaff.forEach(configRolesStaff -> {
                if (configRoles.getId() == configRolesStaff.getRoleId() && configRolesStaff.getStatus() == 1) {
                    roleStaffDTO.setCheck(true);
                }
            });
            vlistRoleStaffDTO.add(roleStaffDTO);
        });
        return vlistRoleStaffDTO;
    }

    @Override
    public Optional<ConfigRolesStaff> getByRoleId(Long roleId, String staffcode) {
        return configRolesStaffRepo.getByRoleIdAndStaffCode(roleId,staffcode);
    }

    void saveToActionDetail(ConfigRolesStaff configRolesStaff, Long actionId) {
        saveActionDetail(actionId, null, configRolesStaff.getStaffCode(), Constants.ROLESTAFF.StAFFCODE);
        saveActionDetail(actionId, null, configRolesStaff.getRoleId().toString(), Constants.ROLESTAFF.ROLE_ID);
        saveActionDetail(actionId, null, configRolesStaff.getStatus().toString(), Constants.ROLESTAFF.STATUS);
    }

    public void saveActionDetail(Long actionID, String oldValue, String newValue, String column) {
        ActionDetail actionDetail = new ActionDetail();
        actionDetail.setActionAuditId(actionID);
        actionDetail.setOldValue(oldValue);
        actionDetail.setNewValue(newValue);
        actionDetail.setColumnName(column);
        actionDetailRepo.save(actionDetail);
    }
}
