package vn.vissoft.dashboard.repo;

import com.google.gson.JsonArray;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.chart.PointDTO;

import java.util.List;

public interface ChartRepoCustom {

    List<Object[]> findAllDataChart(DashboardRequestDTO dashboardRequestDTO, Long plngServiceId, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    List<PointDTO> findDataTrendChart(Long plngServiceId, Long plngPrdId, String pstrShopCode, int pintCycleId, String pstrChannelCode, String pstrStaffCode) throws Exception;

    List<Object[]> findDataChildShop(DashboardRequestDTO dashboardRequestDTO, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChartDTO, String pstrSort) throws Exception;

    List<Object[]> findDataChildShopTable(DashboardRequestDTO dashboardRequestDTO, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    List<Object[]> finDataChildService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO, String pstrSort) throws Exception;

    List<Object[]> findDataLChartChildService(DashboardRequestDTO dashboardRequestDTO, Long plngServiceId) throws Exception;

    List<Object[]> findDataServiceByYear(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception;

    List<PointDTO> findDataTrendChartTable(String pstrTrend,DashboardRequestDTO dashboardRequestDTO) throws Exception;

    List<PointDTO> findDataTrendChildShop(String pstrTrend,DashboardRequestDTO dashboardRequestDTO) throws Exception;

    List<Object[]> findDataLChartServiceNew(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, List<Object[]> plstServices) throws Exception;

    List<Object[]> findDataUnitStaffByService(DashboardRequestDTO dashboardRequestDTO) throws Exception;

    boolean checkClicked(DashboardRequestDTO dashboardRequestDTO, String shopCode) throws Exception;

}
