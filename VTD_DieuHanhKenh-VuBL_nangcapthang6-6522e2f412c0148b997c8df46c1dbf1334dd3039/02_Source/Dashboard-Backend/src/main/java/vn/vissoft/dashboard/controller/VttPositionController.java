package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.VttPosition;
import vn.vissoft.dashboard.services.VttPositionService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("${api_base_path}/vttPosition")

public class VttPositionController {

    private static final Logger LOGGER = LogManager.getLogger(VttPositionController.class);

    @Autowired
    private VttPositionService vttPositionService;

    @PostMapping("/addVttPosition")
    public ApiResponse addVttPosition(Authentication authentication, @RequestBody VttPosition vttPosition) {
        StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
        try {
            String vstrResult = vttPositionService.persist(vttPosition, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrResult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * upload nhom kenh theo chuc danh nhan su
     *
     * @param file
     * @return
     * @author DatNT
     * @version 1.0
     * @since 15/9/2019
     */
    @PostMapping("/uploadVttPosition")
    public ApiResponse uploadVttPosition(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vttPositionService.upload(file, user));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }
}
