package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;

import java.util.List;

public interface ContentTopRepoCustom {

    Object[] getContinuityFail(DashboardRequestDTO dashboardRequestDTO) throws Exception;

    List<Object[]> getContinuityFailLvTwo(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    Object[] getTopByService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    List<Object[]> getTableByService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    Object[] getComplThresholdService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    List<Object[]> getTableComplThreshold(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    Object[] getEvaluatePoint(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;
}
