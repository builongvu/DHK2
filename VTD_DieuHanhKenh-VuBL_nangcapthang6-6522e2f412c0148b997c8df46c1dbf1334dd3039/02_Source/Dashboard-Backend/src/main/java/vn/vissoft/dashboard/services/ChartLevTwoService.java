package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ChartDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.chart.TableDTO;

public interface ChartLevTwoService {

    ChartDTO getColChartChildShop(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO, Boolean pblnZoom) throws Exception;

    TableDTO getTableChildShop(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception;

    ChartDTO getColChartChildService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO, Boolean pblnZoom) throws Exception;

    TableDTO getTableChildService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception;

    ChartDTO getLineChartChildService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception;

    ChartDTO getLineChartServiceByYear(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception;

    ChartDTO getColChartChildShopSort(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception;

    ChartDTO getColChartRankUnitZoom(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO, Boolean pblnZoom) throws Exception;

    ChartDTO getLChartChildServiceNew(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    TableDTO getUnitStaffByService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;

}
