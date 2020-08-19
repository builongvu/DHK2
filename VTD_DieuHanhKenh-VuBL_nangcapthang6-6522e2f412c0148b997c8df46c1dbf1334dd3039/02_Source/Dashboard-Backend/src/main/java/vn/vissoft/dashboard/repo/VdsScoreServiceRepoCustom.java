package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;

import java.util.List;

public interface VdsScoreServiceRepoCustom {

    List<Object[]> getDataDetail(DashboardRequestDTO dashboardRequestDTO);
}
