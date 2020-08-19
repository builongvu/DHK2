package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.PlanQuarterly;
import vn.vissoft.dashboard.repo.PlanQuarterlyHisRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PlanQuarterlyHisRepoImpl implements PlanQuarterlyHisRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public PlanQuarterly findPlanQuarterlyHis(Long plngPrdId, Long plngServiceId, String pstrChannelCode, String pstrShopCode, String pstrStaffCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();

        vsbdSqlBuilder.append("select p from PlanQuarterly p ")
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

        List<PlanQuarterly> vlstPlanQuarterlies = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstPlanQuarterlies)) return null;
        return vlstPlanQuarterlies.get(0);
    }

    @Transactional
    @Override
    public void savePlanQuarterHis(PlanQuarterly planQuarterly) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        Date vdtCurrentDate = new Date();
        long vlngTime = vdtCurrentDate.getTime();
        Timestamp timestamp = new Timestamp(vlngTime);
        vsbdSqlBuilder.append("insert into plan_quarterly_his (PRD_ID,SERVICE_ID,vds_channel_code,shop_code,staff_code,F_SCHEDULE,CURRENCY,org_datetime,USER,CREATE_DATE) " +
                " values (?,?,?,?,?,?,?,?,?,?)");

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter(1, planQuarterly.getPrdId());
        query.setParameter(2, planQuarterly.getServiceId());
        query.setParameter(3, planQuarterly.getVdsChannelCode());
        query.setParameter(4, planQuarterly.getShopCode());
        query.setParameter(5, planQuarterly.getStaffCode());
        query.setParameter(6, planQuarterly.getfSchedule());
        query.setParameter(7, planQuarterly.getCurrency());
        query.setParameter(8, planQuarterly.getCreateDate());
        query.setParameter(9, planQuarterly.getUser());
        query.setParameter(10, timestamp);
        query.executeUpdate();
    }
}
