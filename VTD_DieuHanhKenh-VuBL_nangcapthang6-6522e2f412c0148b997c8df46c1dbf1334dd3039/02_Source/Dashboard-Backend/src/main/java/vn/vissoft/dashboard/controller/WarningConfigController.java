package vn.vissoft.dashboard.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.model.ServiceWarningConfig;
import vn.vissoft.dashboard.model.WarningConfig;
import vn.vissoft.dashboard.repo.ServiceWarningConfigRepo;
import vn.vissoft.dashboard.services.ServiceWarningService;
import vn.vissoft.dashboard.services.WarningConfigService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/warningconfig")
public class WarningConfigController {
    @Autowired
    private WarningConfigService warningConfigService;
    @Autowired
    private ServiceWarningConfigRepo serviceWarningConfigRepo;
    @Autowired
    private ServiceWarningService serviceWarningService;
    private static final Logger LOGGER = LogManager.getLogger(WarningConfigController.class);

    @GetMapping("/getAllWarning")
    public ApiResponse getAllWarning() {
        try {
            List<WarningConfig> warningConfig = warningConfigService.getAllWarning();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", warningConfig);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getAllWarningById/{id}")
    public ApiResponse getAllWarningById(@PathVariable Long id) {
        try {
            List<WarningConfig> warningConfigs = warningConfigService.getAllWarningById(id);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", warningConfigs);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * upload file nguong canh bao chi tieu
     *
     * @param file
     * @return
     * @author DatNT
     * @version 1.0
     * @since 15/9/2019
     */
    @PostMapping("/uploadWarningConfig")
    public ApiResponse uploadServiceScore(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", warningConfigService.upload(file, user));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }

    @PutMapping("update")
    public ApiResponse update(@RequestBody ServiceWarningConfig serviceWarningConfig, Authentication authentication) {
        try {
            ServiceWarningConfig serviceWarningConfig1 = serviceWarningConfigRepo.getOne(serviceWarningConfig.getId());
            if (serviceWarningConfig1 != null ) {
                StaffDTO user = (StaffDTO) authentication.getPrincipal();
               String warning = serviceWarningService.update(serviceWarningConfig, user);
               if(warning.equals(Constants.SERVICE_WARNING_CONFIGS.SUCCESS)){
                   return ApiResponse.build(HttpServletResponse.SC_OK, true, "", serviceWarningConfig);
               }else if (warning.equals(Constants.SERVICE_WARNING_CONFIGS.DUPLICATE_VALUE)){
                   return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true,Constants.SERVICE_WARNING_CONFIGS.DUPLICATE_VALUE ,null);

               }
               else {
                   return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true,Constants.SERVICE_WARNING_CONFIGS.DUPLICATE ,null);
               }

            } else {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "Not found", null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/addWarning")
    public ApiResponse addWarning(@RequestBody ServiceWarningConfig serviceWarningConfig, Authentication authentication) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String warning = serviceWarningService.addWarning(serviceWarningConfig, user);
            if (warning.equals(Constants.SERVICE_WARNING_CONFIGS.SUCCESS)) {
                return ApiResponse.build(HttpServletResponse.SC_OK, true, "", serviceWarningConfig);
            }else if (warning.equals(Constants.SERVICE_WARNING_CONFIGS.DUPLICATE_VALUE)){
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true,Constants.SERVICE_WARNING_CONFIGS.DUPLICATE_VALUE ,null);

            }
            else{
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true,Constants.SERVICE_WARNING_CONFIGS.DUPLICATE ,null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }


    }

    @GetMapping("/downloadWarningTemplate")
    public ResponseEntity<InputStreamResource> downloadTemplate() {
        try {
            ByteArrayInputStream in = warningConfigService.getTemplate();
            return ResponseEntity.ok().body(new InputStreamResource(in));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
