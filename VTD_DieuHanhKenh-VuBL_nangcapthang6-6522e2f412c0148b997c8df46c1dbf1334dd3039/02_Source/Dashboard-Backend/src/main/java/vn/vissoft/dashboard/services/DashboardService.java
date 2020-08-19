package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.DashboardDTO;
import vn.vissoft.dashboard.helper.ApiResponse;

public interface DashboardService {

    DashboardDTO getDashboard(DashboardRequestDTO dashboardRequestDTO, Long groupId, StaffDTO staffDTO) throws Exception;

    DashboardDTO zoomConfigSingleCard(DashboardRequestDTO dashboardRequestDTO, Long groupId, StaffDTO staffDTO) throws Exception;
}
