package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.ConfigRoleDTO;
import vn.vissoft.dashboard.dto.ConfigRoleObjectDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigRoles;
import vn.vissoft.dashboard.services.ConfigRoleService;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api_base_path}/management/role")
public class ConfigRolesController {

    @Autowired
    ConfigRoleService configRoleService;

    private static final Logger LOGGER = LogManager.getLogger(ConfigRolesController.class);

    @GetMapping("/getAllRole")
    public ApiResponse getAllService() {
        List<ConfigRoles> vlstRoles = configRoleService.findAllOrderByRoleName();
        if (DataUtil.isNullOrEmpty(vlstRoles)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstRoles);
    }

    @GetMapping("/getAllActive")
    public ApiResponse getAllRoleActive() {
        List<ConfigRoles> vlstRoles = configRoleService.getAllActive(1L);
        if (DataUtil.isNullOrEmpty(vlstRoles)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstRoles);
    }

    @GetMapping("/getRoleById/{idRole}")
    public ApiResponse getById(@PathVariable Long idRole) {
        try {
            Optional<ConfigRoles> optionalConfigRoles = configRoleService.getRoleById(idRole);
            if (optionalConfigRoles.isPresent()) {
                ConfigRoles configRoles = optionalConfigRoles.get();
                return ApiResponse.build(HttpServletResponse.SC_OK, true, "", configRoles);
            } else {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "Not found", null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/createRole")
    public ApiResponse createService(@RequestBody ConfigRoles configRoles, Authentication authentication) {
        try {
            Optional<ConfigRoles> vobjRole = configRoleService.getRolesByCode(configRoles.getRoleCode());
            if (vobjRole.isPresent()) {
                return ApiResponse.build(HttpServletResponse.SC_CONFLICT, false, "code conflict", configRoles);
            } else {
                StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
                configRoleService.createConfigRole(configRoles, staffDTO);
                return ApiResponse.build(HttpServletResponse.SC_CREATED, true, "", configRoles);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_BAD_REQUEST, false, e.getMessage(), null);
        }
    }

    @PutMapping("/editRole")
    public ApiResponse editService(@RequestBody ConfigRoles configRoles, Authentication authentication) {
        try {
            if (configRoleService.getRoleById(configRoles.getId()).isPresent()) {
                ConfigRoles serviceById = configRoleService.getRoleById(configRoles.getId()).get();
                Optional<ConfigRoles> vobjRole = configRoleService.getRolesByCode(configRoles.getRoleCode());
                if (vobjRole.isPresent() && !serviceById.getRoleCode().equals(configRoles.getRoleCode())) {
                    return ApiResponse.build(HttpServletResponse.SC_CONFLICT, false, "code conflict", vobjRole);
                } else {
                    StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
                    configRoleService.updateConfigRole(configRoles, staffDTO);
                    return ApiResponse.build(HttpServletResponse.SC_CREATED, true, "", configRoles);
                }
            } else {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "Not found", null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/searchByCondition")
    public ApiResponse getRolesByCondition(
            @RequestParam(value = "code", required = false) String uCode,
             @RequestParam(value = "codeObject", required = false) String codeObject) {

        try {
            List<ConfigRoles> configRolesList = configRoleService.getRoleByCondition(uCode, codeObject);
            if (configRolesList.size() > 0) {
                return ApiResponse.build(HttpServletResponse.SC_CREATED, true, "", configRolesList);
            } else {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "Not found", new ArrayList<>());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getActionOfRole/{idRole}")
    public ApiResponse getActionOfRole(@PathVariable Long idRole) {
        try {
            List<ConfigRoleObjectDTO> configRoleObjectDTO = configRoleService.getActionOfRole(idRole);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", configRoleObjectDTO);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }
}
