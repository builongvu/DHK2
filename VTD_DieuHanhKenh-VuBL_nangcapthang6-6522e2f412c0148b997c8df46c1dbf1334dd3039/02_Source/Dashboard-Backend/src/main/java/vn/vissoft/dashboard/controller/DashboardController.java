package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.*;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigGroupCard;
import vn.vissoft.dashboard.model.ConfigSingleChart;
import vn.vissoft.dashboard.services.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    private static final Logger LOGGER = LogManager.getLogger(DashboardController.class);

    @Autowired
    private ChartService chartService;

    @Autowired
    private GroupCardService groupCardService;

    @Autowired
    private TableService tableService;

    @Autowired
    private ContentTopService contentTopService;

    @PostMapping("/getDashboard")
    public ApiResponse getDashboard(Authentication authentication, @RequestBody DashboardRequestDTO dashboardRequestDTO) {
        DashboardDTO dashboardDTO = null;
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            if (user != null) {
                dashboardDTO = dashboardService.getDashboard(dashboardRequestDTO, dashboardRequestDTO.getGroupId(), user);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", dashboardDTO);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);

        }
    }

    @GetMapping("/getGroups")
    public ApiResponse getGroups() {
        List<ConfigGroupCard> vlstGroupCards = null;
        try {
            vlstGroupCards = groupCardService.findAll();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstGroupCards);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * hien thi toan bo du lieu khi phong to
     *
     * @param dashboardRequestDTO
     * @param authentication
     * @return dashboarddto
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 18/11/2019
     */
    @PostMapping("/zoomAllData")
    public ApiResponse getBarChart(Authentication authentication, @RequestBody DashboardRequestDTO dashboardRequestDTO){
        DashboardDTO dashboardDTO = null;
        try{
            StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
            if (!DataUtil.isNullObject(staffDTO)) {
                dashboardDTO = dashboardService.zoomConfigSingleCard(dashboardRequestDTO, dashboardRequestDTO.getGroupId(), staffDTO);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", dashboardDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/renderExcelAllStaff")
    public ApiResponse renderExcelAllStaff(Authentication authentication, @RequestBody DashboardRequestDTO dashboardRequestDTO) throws Exception {
        try {
            List<RenderExcelStaffDTO> vlstDatas = tableService.renderExcelAllStaff(dashboardRequestDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstDatas);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/getKoDat")
    public ApiResponse getKoDat(Authentication authentication, @RequestBody DashboardRequestDTO dashboardRequestDTO) throws Exception {
        try {
            ConfigSingleChartDTO configSingleChartDTO = new ConfigSingleChartDTO();
            List<TopByServiceDTO> data = contentTopService.getTopByService(dashboardRequestDTO,configSingleChartDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
