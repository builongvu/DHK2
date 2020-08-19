package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.chart.TableDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.ConfigSingleChart;
import vn.vissoft.dashboard.repo.ConfigSingleChartRepo;
import vn.vissoft.dashboard.services.TopSecondLevelService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("${api_base_path}/score-service")
public class VdsScoreServiceController {

    @Autowired
    private TopSecondLevelService topSecondLevelService;

    @Autowired
    private ConfigSingleChartRepo configSingleChartRepo;

    private static final Logger LOGGER = LogManager.getLogger(VdsScoreServiceController.class);

    /**
     * du lieu chi tiet tu bang vds_score_service va vds_score_ranking
     *
     * @param dashboardRequestDTO
     * @author: HungNN
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/12/16
     */
    @PostMapping("/downloadExcel")
    public ApiResponse downloadExcel(@RequestBody DashboardRequestDTO dashboardRequestDTO) throws Exception {
        try {
            ConfigSingleChart configSingleChart = configSingleChartRepo.getConfigChart();
            TableDTO tableDTO = topSecondLevelService.detailTableWithService(dashboardRequestDTO, configSingleChart);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", tableDTO);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
