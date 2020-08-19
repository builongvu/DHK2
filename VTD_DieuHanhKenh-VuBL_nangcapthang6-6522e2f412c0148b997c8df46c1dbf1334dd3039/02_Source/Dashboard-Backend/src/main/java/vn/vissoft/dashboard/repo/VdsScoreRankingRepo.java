package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;

import java.math.BigInteger;
import java.util.List;

public interface VdsScoreRankingRepo {

    List<Object[]> getTopLeft(DashboardRequestDTO dashboardRequestDTO, int topNum, boolean isMax) throws Exception;

    List<Object[]> getScoreRanking(DashboardRequestDTO dashboardRequestDTO, boolean isMax) throws Exception;

    Integer countTop(DashboardRequestDTO dashboardRequestDTO) throws Exception;

}
