package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.PlanYearly;
import vn.vissoft.dashboard.repo.PlanYearlyHisRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PlanYearlyHisRepoImpl implements PlanYearlyHisRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public PlanYearly findPlanYearlyHis(Long plngPrdId, Long plngServiceId, String pstrChannelCode, String pstrShopCode, String pstrStaffCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();

        vsbdSqlBuilder.append("select p from PlanYearly p ")
                .append("where 1=1 ");
        if (!DataUtil.isNullOrZero(plngPrdId)) {
            vsbdSqlBuilder.append(" and p.prdId =:prdId ");
            vhmpMapParams.put("prdId", plngPrdId);
        }
        if (!DataUtil.isNullOrZero(plngServiceId)) {
            vsbdSqlBuilder.append(" and p.serviceId =:serviceId ");
            vhmpMapParams.put("serviceId", plngServiceId);
        }
        if (!DataUtil.isNullOrEmpty(pstrChannelCode)) {
            vsbdSqlBuilder.append(" and p.vdsChannelCode =:vdsChannelCode ");
            vhmpMapParams.put("vdsChannelCode", pstrChannelCode);
        }
        if (!DataUtil.isNullOrEmpty(pstrShopCode)) {
            vsbdSqlBuilder.append(" and p.shopCode =:shopCode ");
            vhmpMapParams.put("shopCode", pstrShopCode);
        }
        if (!DataUtil.isNullOrEmpty(pstrStaffCode)) {
            vsbdSqlBuilder.append(" and p.staffCode =:staffCode ");
            vhmpMapParams.put("staffCode", pstrStaffCode);
        }
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<PlanYearly> vlstPlanYearlies = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstPlanYearlies)) return null;
        return vlstPlanYearlies.get(0);
    }

    @Transactional
    @Override
    public void savePlanYearHis(PlanYearly planYearly) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        Date vdtCurrentDate = new Date();
        long vlngTime = vdtCurrentDate.getTime();
        Timestamp timestamp = new Timestamp(vlngTime);
        vsbdSqlBuilder.append("insert into plan_yearly_his (PRD_ID,SERVICE_ID,vds_channel_code,shop_code,staff_code,F_SCHEDULE,CURRENCY,org_datetime,USER,CREATE_DATE) " +
                " values (?,?,?,?,?,?,?,?,?,?)");

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter(1, planYearly.getPrdId());
        query.setParameter(2, planYearly.getServiceId());
        query.setParameter(3, planYearly.getVdsChannelCode());
        query.setParameter(4, planYearly.getShopCode());
        query.setParameter(5, planYearly.getStaffCode());
        query.setParameter(6, planYearly.getfSchedule());
        query.setParameter(7, planYearly.getCurrency());
        query.setParameter(8, planYearly.getCreateDatetime());
        query.setParameter(9, planYearly.getUser());
        query.setParameter(10, timestamp);
        query.executeUpdate();
    }
}
