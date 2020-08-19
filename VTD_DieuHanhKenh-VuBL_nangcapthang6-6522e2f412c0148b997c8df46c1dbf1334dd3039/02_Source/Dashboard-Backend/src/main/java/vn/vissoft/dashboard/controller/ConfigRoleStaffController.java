package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.ConfigRoleDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.ConfigRolesStaffRepo;
import vn.vissoft.dashboard.services.ConfigRoleStaffService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/rolestaff")
public class ConfigRoleStaffController {

    private static final Logger LOGGER = LogManager.getLogger(ConfigRoleStaffController.class);

    @Autowired
    ConfigRolesStaffRepo configRolesStaffRepo;

    @Autowired
    ConfigRoleStaffService configRoleStaffService;

    @GetMapping("/getAll")
    public ApiResponse getAllRoleStaff() {
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, configRolesStaffRepo.getAllByStaffCode("slgb_admin_bi"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true, e.getMessage(), null);
        }
    }

    @GetMapping("/getAllActive")
    public ApiResponse getAllActive() {
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, configRolesStaffRepo.getAllByStatus(Constants.STATUS.ACTIVE));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true, e.getMessage(), null);
        }
    }

    @PostMapping("/saveAll/{staffcode}")
    public ApiResponse saveRoleStaff(Authentication authentication, @PathVariable("staffcode") String staffcode, @RequestBody String[] roleId) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            configRoleStaffService.save(roleId, staffcode, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true, e.getMessage(), null);
        }
    }

    @GetMapping("/getRoleByStaffCode/{staffcode}")
    public ApiResponse getRoleByStaffCode(@PathVariable String staffcode) {
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, configRoleStaffService.getByStaffCode(staffcode));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true, e.getMessage(), null);
        }
    }


}
