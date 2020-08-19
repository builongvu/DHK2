package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.VttGroupChannelSale;
import vn.vissoft.dashboard.services.VttGroupChannelSaleService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("${api_base_path}/vttGroupChannelSale")

public class VttGroupChannelSaleController {

    private static final Logger LOGGER = LogManager.getLogger(VttGroupChannelSaleController.class);

    @Autowired
    private VttGroupChannelSaleService vttGroupChannelSaleService;

    @PostMapping("/addVttGroupChannelSale")
    public ApiResponse addVttGroupChannelSale(Authentication authentication, @RequestBody VttGroupChannelSale vttGroupChannel) {
        StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
        try {
            String vstrResult = vttGroupChannelSaleService.persist(vttGroupChannel, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrResult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * upload nhom kenh dua tren he thong ban hang
     *
     * @param file
     * @return
     * @author DatNT
     * @version 1.0
     * @since 15/9/2019
     */
    @PostMapping("/uploadVttGroupChannelSale")
    public ApiResponse uploadVttGroupChannelSale(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vttGroupChannelSaleService.upload(file, user));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }
}
