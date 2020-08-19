package vn.vissoft.dashboard.services;


import vn.vissoft.dashboard.dto.RoleStaffDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.ConfigObjects;
import vn.vissoft.dashboard.model.ConfigRolesStaff;

import java.util.List;
import java.util.Optional;

public interface ConfigRoleStaffService {
    List<ConfigRolesStaff> getAllConfigRoleStaff();

    void save(String[] roleId,String staffCode,StaffDTO user) throws Exception;

    List<ConfigRolesStaff> getAllActive(int status) throws Exception;

    List<RoleStaffDTO> getByStaffCode(String staffcode) throws Exception;

    Optional<ConfigRolesStaff> getByRoleId(Long roleId, String staffcode);
}
