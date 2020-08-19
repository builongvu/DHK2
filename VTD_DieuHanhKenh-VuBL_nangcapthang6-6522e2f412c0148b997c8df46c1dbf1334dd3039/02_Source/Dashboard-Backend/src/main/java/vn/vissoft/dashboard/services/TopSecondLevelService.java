package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ChartDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.chart.TableDTO;
import vn.vissoft.dashboard.dto.chart.TopDataDTO;
import vn.vissoft.dashboard.model.ConfigSingleChart;

public interface TopSecondLevelService {

    ChartDTO detailRankShopBarChart(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChart) throws Exception;

    TableDTO detailRankShopTable(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    TableDTO detailShopTargetTable(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    TableDTO detailTableWithService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChart configSingleChart) throws Exception;

    TableDTO detailScoreRanking(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;
}
