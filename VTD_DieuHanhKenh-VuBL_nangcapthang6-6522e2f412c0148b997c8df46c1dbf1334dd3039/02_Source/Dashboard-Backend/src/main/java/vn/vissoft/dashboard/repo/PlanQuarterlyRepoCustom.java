package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.PlanQuarterlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.PlanMonthly;
import vn.vissoft.dashboard.model.PlanQuarterly;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

public interface PlanQuarterlyRepoCustom {

    List<PlanQuarterlyDTO> findPlanQuarterlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    public BigInteger countPlanQuarterlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;
    int updatePlanQuarterly(Long plngPlanQuarterlyId, double pdblFschedule, Timestamp updateDate) throws Exception;
    int deletePlanQuarterly(Long plngPlanQuarterId) throws Exception;
    PlanQuarterly findPlanQuarterFromPlan(Object o, StaffDTO staffDTO) throws Exception;
    void persist(PlanQuarterly planQuarterly);

    void update(PlanQuarterly planQuarterly);

    double getScheduleOfShop(PlanQuarterly planQuarterlyDTO,StaffDTO staffDTO) throws Exception;

    double getSumScheduleChildShop(PlanQuarterly planQuarterlyDTO,List<Long> pIds,StaffDTO staffDTO) throws Exception;

    double getSumScheduleStaff(PlanQuarterly planQuarterly,List<Long> ids,StaffDTO staffDTO) throws Exception;

}
