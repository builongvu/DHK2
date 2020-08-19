package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.model.ConfigSingleChart;

import java.math.BigInteger;
import java.util.List;

public interface ConfigSingleChartRepoCustom {
    String getJsonArray();

    ConfigSingleChart getConfigChart();

    List<Object[]> findByCondition(ConfigSingleChartDTO configSingleChart) throws Exception;

    BigInteger countByCondition(ConfigSingleChartDTO configSingleChartDTO) throws Exception;

    List<String> findListServiceSearch(List<Long> serviceIds) throws Exception;

    void addChart(ConfigSingleChart configSingleChart) throws Exception;

    void updateChart(ConfigSingleChart configSingleChart) throws Exception;

}
