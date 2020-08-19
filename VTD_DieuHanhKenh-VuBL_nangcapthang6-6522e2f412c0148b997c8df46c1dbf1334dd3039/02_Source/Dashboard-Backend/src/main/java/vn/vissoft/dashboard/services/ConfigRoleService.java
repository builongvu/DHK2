package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.ConfigRoleDTO;
import vn.vissoft.dashboard.dto.ConfigRoleObjectDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.ConfigRoles;
import vn.vissoft.dashboard.model.ConfigRolesObjects;
import vn.vissoft.dashboard.model.Service;

import java.util.List;
import java.util.Optional;

public interface ConfigRoleService {

    List<ConfigRoles> findAllOrderByRoleName();

    List<ConfigRoles> getAllActive(Long status);

    void createConfigRole(ConfigRoles configRoles, StaffDTO staffDTO);

    void updateConfigRole(ConfigRoles configRoles, StaffDTO staffDTO);

    Optional<ConfigRoles> getRolesByCode(String code);

    Optional<ConfigRoles> getRoleById(Long idService);

    List<ConfigRoles> getRoleByCondition(String roleCode, String objectCode);

    List<ConfigRoleObjectDTO> getActionOfRole(Long idRole);

}
