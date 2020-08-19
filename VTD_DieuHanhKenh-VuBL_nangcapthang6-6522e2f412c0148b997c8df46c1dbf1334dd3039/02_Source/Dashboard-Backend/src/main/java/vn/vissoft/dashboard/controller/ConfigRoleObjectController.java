package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.ConfigRoles;
import vn.vissoft.dashboard.model.ConfigRolesObjects;
import vn.vissoft.dashboard.services.ConfigRoleObjectService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api_base_path}/management/roleobject")
public class ConfigRoleObjectController {

    @Autowired
    ConfigRoleObjectService configRoleObjectService;

    private static final Logger LOGGER = LogManager.getLogger(ConfigRoleObjectController.class);

    @PostMapping("/createRoleObject")
    public ApiResponse createService(@RequestBody List<ConfigRolesObjects> configRolesObjects, Authentication authentication) {
        try {
            StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
            for (ConfigRolesObjects rolesObjects : configRolesObjects) {
                configRoleObjectService.createRoleObject(rolesObjects, staffDTO);
            }
            return ApiResponse.build(HttpServletResponse.SC_CREATED, true, "", configRolesObjects);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_BAD_REQUEST, false, e.getMessage(), null);
        }
    }

}
