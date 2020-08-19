package vn.vissoft.dashboard.services;


import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.model.ConfigSingleChart;

import java.math.BigInteger;
import java.util.List;

public interface ConfigSingleChartService {

    List<ConfigSingleChartDTO> getByCondition(ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    BigInteger countByCondition(ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    String addChart(ConfigSingleChartDTO configSingleChart, StaffDTO staffDTO) throws Exception;

    String updateChart(ConfigSingleChartDTO configSingleChart, StaffDTO staffDTO) throws Exception;

    String deleteChart(ConfigSingleChartDTO configSingleChart,StaffDTO staffDTO) throws Exception;

}
