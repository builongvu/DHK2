package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.ConfigRoles;
import vn.vissoft.dashboard.model.ConfigRolesObjects;

import java.util.List;
import java.util.Optional;

public interface ConfigRolesRepoCustom {

    List<ConfigRoles> findAllByStatus();

    Optional getRoleByCode(String code);

    List<Object[]> getByCondition(String roleCode, String objectCode);

    List<Object[]> getActionOfRole(Long roleObjectId, Long objectId);

    List<ConfigRolesObjects> getAllConfigRolesObjectsByIdRole(Long idRole);

}
