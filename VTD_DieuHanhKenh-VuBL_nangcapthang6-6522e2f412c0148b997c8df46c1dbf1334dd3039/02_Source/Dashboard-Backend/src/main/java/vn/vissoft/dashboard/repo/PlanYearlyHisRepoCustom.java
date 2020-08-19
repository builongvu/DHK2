package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.PlanQuarterly;
import vn.vissoft.dashboard.model.PlanYearly;

public interface PlanYearlyHisRepoCustom {
    PlanYearly findPlanYearlyHis(Long plngPrdId, Long plngServiceId, String pstrChannelCode, String pstrShopCode, String pstrStaffCode) throws Exception;
    void savePlanYearHis(PlanYearly planYearly) throws Exception;
}
