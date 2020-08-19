package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.BusinessResultProvincialDTO;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.ResultsProvincialDTO;
import vn.vissoft.dashboard.dto.ResultsStaffDTO;
import vn.vissoft.dashboard.model.VdsStaff;

import java.util.List;

public interface BusinessResultsService {

    ResultsProvincialDTO getResultProvincial(DashboardRequestDTO dashboardRequestDTO) throws Exception;

    ResultsStaffDTO getResultsStaff(DashboardRequestDTO dashboardRequestDTO) throws Exception;

    VdsStaff findVdsStaffByCondition(String pstrStaffCode);
}
