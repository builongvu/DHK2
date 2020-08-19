package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.chart.ConfigGroupCardDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleCardDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;

import java.util.List;

public interface DashboardRepoCustom {

    ConfigGroupCardDTO getConfigGroup(Long plngProductId);

    List<ConfigSingleCardDTO> getSingleCard(Long plngGroupId);

    List<ConfigSingleChartDTO> getSingleChart(Long plngCardId);
}
