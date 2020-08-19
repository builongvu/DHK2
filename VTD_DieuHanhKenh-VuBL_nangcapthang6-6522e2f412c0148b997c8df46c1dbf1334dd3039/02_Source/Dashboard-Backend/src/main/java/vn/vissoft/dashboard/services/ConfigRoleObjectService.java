package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.ConfigRolesObjects;

public interface ConfigRoleObjectService {

    void createRoleObject(ConfigRolesObjects configRolesObjects, StaffDTO staffDTO);

}
