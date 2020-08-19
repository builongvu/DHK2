package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleCardDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.chart.RenderExcelStaffDTO;
import vn.vissoft.dashboard.dto.chart.TableDTO;

import java.util.List;

public interface TableService {
//    TableDTO summarizeByDay(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO singleChart) throws Exception;
    TableDTO buildTable(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO singleChart, StaffDTO staffDTO) throws Exception;
    String setWarninglevel(Double pdblComplete, String pstrChannelCode, Long plngServiceId) throws Exception;

    List<RenderExcelStaffDTO> renderExcelAllStaff(DashboardRequestDTO dashboardRequestDTO) throws Exception;
}
