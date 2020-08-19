package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleCardDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.ConfigSingleCard;
import vn.vissoft.dashboard.services.ConfigSingleCardService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/single-card")
public class ConfigSingleCardController {

    @Autowired
    private ConfigSingleCardService configSingleCardService;

    private static final Logger LOGGER = LogManager.getLogger(DashboardController.class);

    @GetMapping("/getAll")
    public ApiResponse getAllCard() throws Exception {
        try {
            List<ConfigSingleCardDTO> list = configSingleCardService.getAllCard();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getActiveCard")
    public ApiResponse getActiveCard() throws Exception {
        try {
            List<ConfigSingleCard> list = configSingleCardService.getActiveCard();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/search")
    public ApiResponse searchConfigSingleCard(@RequestBody ConfigSingleCardDTO configSingleCardDTO) throws Exception{
        try {
            List<ConfigSingleCardDTO> list = configSingleCardService.searchConfigSingleCard(configSingleCardDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/delete")
    public ApiResponse deleteConfigSingleCard(@RequestBody ConfigSingleCardDTO configSingleCardDTO, Authentication authentication) throws Exception {
        try {
            StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
            String message = configSingleCardService.deleteSingleCard(configSingleCardDTO, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", message);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/create")
    public ApiResponse createConfigSingleCard(@RequestBody ConfigSingleCardDTO configSingleCardDTO, Authentication authentication) throws Exception {
        try {
            StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
            String message = configSingleCardService.insertConfigSingleCard(configSingleCardDTO, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", message);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/update")
    public ApiResponse updateConfigSingleCard(@RequestBody ConfigSingleCard configSingleCard, Authentication authentication) throws Exception {
        try {
            StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
            String message = configSingleCardService.updateConfigSingleCard(configSingleCard, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", message);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getGroupCard")
    public ApiResponse getGroupCardOrderByName() throws Exception {
        try{
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", configSingleCardService.listGroupCard());
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
