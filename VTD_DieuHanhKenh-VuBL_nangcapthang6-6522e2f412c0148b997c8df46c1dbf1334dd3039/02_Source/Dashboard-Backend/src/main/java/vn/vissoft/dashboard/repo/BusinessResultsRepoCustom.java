package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.helper.DashboardRequest;

import java.util.List;

public interface BusinessResultsRepoCustom {

    List<Object[]> findResultProvincial(DashboardRequestDTO dashboardRequestDTO) throws Exception;

    List<Object[]> findResultStaff(DashboardRequestDTO dashboardRequestDTO) throws Exception;

    List<Object[]> findServiceScore(DashboardRequestDTO dashboardRequestDTO) throws Exception;

}
