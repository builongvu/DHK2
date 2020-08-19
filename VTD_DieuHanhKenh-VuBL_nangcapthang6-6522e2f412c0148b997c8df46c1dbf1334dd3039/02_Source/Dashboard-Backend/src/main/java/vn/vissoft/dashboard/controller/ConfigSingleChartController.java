package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.ConfigSingleChart;
import vn.vissoft.dashboard.services.ConfigSingleChartService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/configSingleChart")
public class ConfigSingleChartController {
    private static final Logger LOGGER = LogManager.getLogger(ConfigSingleChartController.class);

    @Autowired
    private ConfigSingleChartService configSingleChartService;

    @PostMapping("/getByCondition")
    public ApiResponse getByConddition(@RequestBody ConfigSingleChartDTO configSingleChartDTO) {
        try {
            ApiResponse apiResponse;
            List<ConfigSingleChartDTO> vlstConfigSingleCharts = configSingleChartService.getByCondition(configSingleChartDTO);
            apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstConfigSingleCharts);
            apiResponse.setTotalRow(configSingleChartService.countByCondition(configSingleChartDTO).intValue());

            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/addConfigSingleChart")
    public ApiResponse addConfigSingleChart(Authentication authentication, @RequestBody ConfigSingleChartDTO configSingleChart) {
        StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
        try {
            String vstrResult = configSingleChartService.addChart(configSingleChart, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrResult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/updateConfigSingleChart")
    public ApiResponse updateMappingGroupChannel(Authentication authentication, @RequestBody ConfigSingleChartDTO configSingleChart) {
        StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
        try {
            String vstrResult = configSingleChartService.updateChart(configSingleChart, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrResult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/deleteConfigSingleChart")
    public ApiResponse deleteMappingGroupChannel(Authentication authentication, @RequestBody ConfigSingleChartDTO configSingleChart) {
        StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
        try {
            String vstrResult = configSingleChartService.deleteChart(configSingleChart,staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrResult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
