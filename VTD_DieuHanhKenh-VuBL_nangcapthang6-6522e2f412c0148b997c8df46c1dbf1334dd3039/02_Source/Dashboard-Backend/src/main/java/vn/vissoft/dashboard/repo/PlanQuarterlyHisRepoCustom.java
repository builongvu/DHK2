package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.PlanQuarterly;

public interface PlanQuarterlyHisRepoCustom {
    PlanQuarterly findPlanQuarterlyHis(Long plngPrdId, Long plngServiceId, String pstrChannelCode, String pstrShopCode, String pstrStaffCode) throws Exception;
    void savePlanQuarterHis(PlanQuarterly planQuarterly) throws Exception;
}
