package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.model.PlanMonthly;

public interface PlanMonthlyHisCustomRepo {

    PlanMonthly findPlanMonthlyHis(Long plngPrdId,Long plngServiceId,String pstrChannelCode,String pstrShopCode,String pstrStaffCode) throws Exception;

    void savePlanMonthlyHis(PlanMonthly planMonthly) throws Exception;
}
