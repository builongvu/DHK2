package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.model.ConfigSingleChart;

import java.util.List;

public interface TableRepoCustom {

    List<Object[]> sumarizeByDay(DashboardRequestDTO dashboardRequestDTO, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChart) throws Exception;

    List<Object[]> sumarizeByMonth(DashboardRequestDTO dashboardRequestDTO, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChart) throws Exception;

    List<Object[]> sumarizeByQuarter(DashboardRequestDTO dashboardRequestDTO, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChart) throws Exception;

    List<Object[]> sumarizeDetailByDay(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChart) throws Exception;

    List<Object[]> sumarizeDetailByMonth(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChart) throws Exception;

    List<Object[]> sumarizeDetailByQuarter(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChart) throws Exception;

    List<Object[]> renderExcelAllStaff(DashboardRequestDTO dashboardRequestDTO) throws Exception;
}
