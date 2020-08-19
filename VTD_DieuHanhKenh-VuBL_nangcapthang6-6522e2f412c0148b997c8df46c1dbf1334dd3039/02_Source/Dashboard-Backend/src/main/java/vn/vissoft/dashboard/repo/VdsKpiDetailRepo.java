package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;

import java.math.BigInteger;
import java.util.List;

public interface VdsKpiDetailRepo {

    Object[] getTopRight(DashboardRequestDTO dashboardRequestDTO, int cycleId) throws Exception;

    List<Object[]> tableDetailTopRight(DashboardRequestDTO dashboardRequestDTO) throws Exception;

}
