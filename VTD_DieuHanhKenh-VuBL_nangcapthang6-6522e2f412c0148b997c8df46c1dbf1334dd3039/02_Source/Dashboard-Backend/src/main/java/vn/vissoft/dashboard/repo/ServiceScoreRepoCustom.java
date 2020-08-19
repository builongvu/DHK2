package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.ServiceScoreDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.excel.ServiceScoreExcel;
import vn.vissoft.dashboard.model.ServiceScore;

import java.util.List;

public interface ServiceScoreRepoCustom {

    List<Object[]> getServiceScore(ServiceScoreDTO serviceScoreDTO);

    String addServiceScore(ServiceScoreDTO serviceScoreDTO) throws Exception;

    String updateServiceScore(ServiceScoreDTO serviceScoreDTO, Long id) throws Exception;

    ServiceScore findServiceScoreFromFile(ServiceScoreExcel serviceScore) throws Exception;

    void persist(ServiceScore serviceScore) throws Exception;

    void update(ServiceScore serviceScore) throws Exception;

    boolean checkDuplicate(ServiceScoreDTO serviceScoreDTO, String pstrType) throws Exception;

    Long findServiceScoreIdByCondition(Long plngServiceId,String pstrChannelCode,String pstrShopCode,String pstrStaffCode) throws Exception;

    List<Object[]> findDetailEvaluateScore(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception;
}
