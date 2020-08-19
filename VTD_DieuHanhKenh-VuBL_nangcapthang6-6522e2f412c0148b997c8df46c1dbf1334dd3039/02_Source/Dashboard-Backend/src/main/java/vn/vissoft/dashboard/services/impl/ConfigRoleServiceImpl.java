package vn.vissoft.dashboard.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.ConfigRoleDTO;
import vn.vissoft.dashboard.dto.ConfigRoleObjectDTO;
import vn.vissoft.dashboard.dto.ServiceDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.ConfigObjectsRepo;
import vn.vissoft.dashboard.repo.ConfigRolesObjectsRepo;
import vn.vissoft.dashboard.repo.ConfigRolesRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ActionDetailService;
import vn.vissoft.dashboard.services.ConfigRoleService;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
@Service
public class ConfigRoleServiceImpl implements ConfigRoleService {

    @Autowired
    ConfigRolesRepo configRolesRepo;

    @Autowired
    ConfigRolesObjectsRepo configRolesObjectsRepo;

    @Autowired
    ConfigObjectsRepo configObjectsRepo;

    @Autowired
    private ActionAuditService actionAuditService;

    @Autowired
    private ActionDetailService actionDetailService;

    private BaseMapper<ConfigRoles, ConfigRoleDTO> mapper = new BaseMapper<ConfigRoles, ConfigRoleDTO>(ConfigRoles.class, ConfigRoleDTO.class);


    private static final Logger LOGGER = LogManager.getLogger(ConfigRoleServiceImpl.class);

    @Override
    public List<ConfigRoles> findAllOrderByRoleName() {
        return configRolesRepo.findAllByOrderByRoleName();
    }

    @Override
    public List<ConfigRoles> getAllActive(Long status) {
        return configRolesRepo.getAllByStatus(status);
    }

    @Override
    public void createConfigRole(ConfigRoles configRoles, StaffDTO staffDTO) {
        ConfigRoles configRolesNew = configRolesRepo.saveAndFlush(configRoles);
        ActionAudit actionAudit = actionAuditService.log(Constants.ROLELOG, Constants.CREATE, staffDTO.getStaffCode(), configRoles.getId(), staffDTO.getShopCode());
        actionDetailService.addLogRole(configRolesNew, null, actionAudit.getId());
    }

    @Override
    public void updateConfigRole(ConfigRoles configRoles, StaffDTO staffDTO) {
        ConfigRoles oldConfigRoles = configRolesRepo.findById(configRoles.getId()).get();
        ActionAudit actionAudit = actionAuditService.log(Constants.ROLELOG, Constants.EDIT, staffDTO.getStaffCode(), configRoles.getId(), staffDTO.getShopCode());
        actionDetailService.addLogRole(configRoles, oldConfigRoles, actionAudit.getId());
        configRolesRepo.save(configRoles);
    }

    @Override
    public Optional<ConfigRoles> getRolesByCode(String code) {
        return configRolesRepo.getRoleByCode(code);
    }

    @Override
    public Optional<ConfigRoles> getRoleById(Long idService) {
        return configRolesRepo.findById(idService);
    }

    @Override
    public List<ConfigRoles> getRoleByCondition(String roleCode, String objectCode) {
        try {
            List<Object[]> listRoleSearched = configRolesRepo.getByCondition(roleCode, objectCode);
            List<ConfigRoles> listTransfer = new ArrayList<>();

            for (Object[] role : listRoleSearched) {
                ConfigRoles configRole = new ConfigRoles();
                configRole.setId(DataUtil.safeToLong(role[0].toString()));
                configRole.setRoleCode(DataUtil.safeToString(role[1].toString()));
                configRole.setRoleName(DataUtil.safeToString(role[2].toString()));
                configRole.setRoleDescription(DataUtil.safeToString(role[3].toString()));
                configRole.setStatus(DataUtil.safeToLong(role[4].toString()));

                listTransfer.add(configRole);
            }

            return listTransfer;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }

    }

    @Override
    public List<ConfigRoleObjectDTO> getActionOfRole(Long idRole) {
        try {
            List<ConfigObjects> configObjectsList = configObjectsRepo.findAllByStatusAndObjectTypeLikeOrderByObjectName(1L, "1");
            List<ConfigRolesObjects> configRolesObjectsList = configRolesRepo.getAllConfigRolesObjectsByIdRole(idRole);
            List<ConfigRoleObjectDTO> configRoleObjectDTOList = new ArrayList<>();

            for (ConfigObjects configObjects : configObjectsList) {
                boolean check = true;
                for (ConfigRolesObjects configRolesObjects : configRolesObjectsList) {
                    if (configObjects.getId().equals(configRolesObjects.getObjectId())) {
                        ConfigRoleObjectDTO configRoleObjectDTO = new ConfigRoleObjectDTO();

                        configRoleObjectDTO.setId(configRolesObjects.getId());
                        configRoleObjectDTO.setObjectId(configRolesObjects.getObjectId());
                        configRoleObjectDTO.setRoleId(configRolesObjects.getRoleId());
                        configRoleObjectDTO.setStatus(configRolesObjects.getStatus());
                        configRoleObjectDTO.setAction(configRolesObjects.getAction());
                        configRoleObjectDTO.setObjectCode(configObjects.getObjectCode());
                        configRoleObjectDTO.setObjectName(configObjects.getObjectName());
                        configRoleObjectDTO.setIsDefault(configRolesObjects.getIsDefault());

                        if (!DataUtil.isNullOrEmpty(configRoleObjectDTO.getAction())) {
                            if (configRoleObjectDTO.getAction().contains("C")) {
                                configRoleObjectDTO.setCreate(true);
                            } else {
                                configRoleObjectDTO.setCreate(false);
                            }

                            if (configRoleObjectDTO.getAction().contains("R")) {
                                configRoleObjectDTO.setRead(true);
                            } else {
                                configRoleObjectDTO.setRead(false);
                            }

                            if (configRoleObjectDTO.getAction().contains("U")) {
                                configRoleObjectDTO.setUpdate(true);
                            } else {
                                configRoleObjectDTO.setUpdate(false);
                            }

                            if (configRoleObjectDTO.getAction().contains("D")) {
                                configRoleObjectDTO.setDelete(true);
                            } else {
                                configRoleObjectDTO.setDelete(false);
                            }
                        } else {
                            configRoleObjectDTO.setCreate(false);
                            configRoleObjectDTO.setRead(false);
                            configRoleObjectDTO.setUpdate(false);
                            configRoleObjectDTO.setDelete(false);
                        }

                        configRoleObjectDTOList.add(configRoleObjectDTO);
                        check = false;
                        break;
                    }
                }

                if (check) {
                    ConfigRoleObjectDTO configRoleObjectDTO = new ConfigRoleObjectDTO();

                    configRoleObjectDTO.setObjectId(configObjects.getId());
                    configRoleObjectDTO.setObjectCode(configObjects.getObjectCode());
                    configRoleObjectDTO.setObjectName(configObjects.getObjectName());

                    configRoleObjectDTO.setCreate(false);
                    configRoleObjectDTO.setRead(false);
                    configRoleObjectDTO.setUpdate(false);
                    configRoleObjectDTO.setDelete(false);

                    configRoleObjectDTOList.add(configRoleObjectDTO);
                }
            }
            return configRoleObjectDTOList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
