package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.chart.ContentTopLeftDTO;
import vn.vissoft.dashboard.dto.chart.ContentTopRightDTO;
import vn.vissoft.dashboard.dto.chart.TopDataDTO;

public interface TopDataService {

    ContentTopLeftDTO getLeftTop(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    ContentTopRightDTO getRightTop(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

}
