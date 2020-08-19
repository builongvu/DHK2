package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.PlanYearlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.PlanMonthly;
import vn.vissoft.dashboard.model.PlanQuarterly;
import vn.vissoft.dashboard.model.PlanYearly;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

public interface PlanYearlyRepoCustom {

    List<PlanYearlyDTO> findPlanYearlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    public BigInteger countPlanYearlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception;

    int updatePlanYearly(Long plngPlanYearlyId, double pdblFschedule, Timestamp updateDate) throws Exception;

    int deletePlanYearly(Long plngPlanYearId) throws Exception;

    PlanYearly findPlanYearlyFromPlan(Object o, StaffDTO staffDTO) throws Exception;

    void persist(PlanYearly planYearly);

    void update(PlanYearly planYearly);

    double getScheduleOfShop(PlanYearly planYearly, StaffDTO staffDTO) throws Exception;

    double getSumScheduleChildShop(PlanYearly planYearly, List<Long> pIds, StaffDTO staffDTO) throws Exception;

    double getSumScheduleStaff(PlanYearly planYearly, List<Long> pIds, StaffDTO staffDTO) throws Exception;

}
