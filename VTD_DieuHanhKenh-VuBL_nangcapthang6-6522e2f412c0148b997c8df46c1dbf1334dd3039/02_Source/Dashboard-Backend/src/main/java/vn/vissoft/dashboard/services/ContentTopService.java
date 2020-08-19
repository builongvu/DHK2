package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.chart.ContinuityFailDTO;
import vn.vissoft.dashboard.dto.chart.TableDTO;
import vn.vissoft.dashboard.dto.chart.TopByServiceDTO;

import java.util.List;

public interface ContentTopService {

    ContinuityFailDTO getContinuityFail(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    TableDTO getContinuityFailLvTwo(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    List<TopByServiceDTO> getTopByService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    TableDTO getTableByService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    TableDTO getComplThresholdService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    TableDTO getTableComplThreshold(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    List<TopByServiceDTO> getEvaluatePoint(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

}
