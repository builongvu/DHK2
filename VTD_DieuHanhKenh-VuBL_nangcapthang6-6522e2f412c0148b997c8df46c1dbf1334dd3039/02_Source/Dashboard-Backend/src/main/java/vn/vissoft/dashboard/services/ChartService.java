package vn.vissoft.dashboard.services;

import com.google.gson.JsonArray;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.LineDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ChartDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.chart.PointDTO;
import vn.vissoft.dashboard.helper.DashboardRequest;
import vn.vissoft.dashboard.model.ConfigSingleChart;

import java.util.List;

public interface ChartService {

    ChartDTO getChart(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChart, StaffDTO staffDTO) throws Exception;

    ChartDTO getTrendChart(Long plngServiceId, Long plngPrdId, String pstrShopCode, JsonArray pMetaData, int pintCycleId, String pstrChannelCode, String pstrStaffCode) throws Exception;

    List<Object[]> getAllDataChart(DashboardRequestDTO dashboardRequestDTO, Long plngServiceId, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    ChartDTO getTrendChartTable(String pstrTrend,  JsonArray pMetaData,DashboardRequestDTO dashboardRequestDTO) throws Exception;

    ChartDTO getTrendChildShop(String pstrTrend,  JsonArray pMetaData,DashboardRequestDTO dashboardRequestDTO) throws Exception;
}
