package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.PlanMonthly;
import vn.vissoft.dashboard.repo.PlanMonthlyHisCustomRepo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PlanMonthlyHisCustomRepoImpl implements PlanMonthlyHisCustomRepo {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public PlanMonthly findPlanMonthlyHis(Long plngPrdId, Long plngServiceId, String pstrChannelCode, String pstrShopCode, String pstrStaffCode) {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();

        vsbdSqlBuilder.append("select p from PlanMonthly p ")
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

        List<PlanMonthly> vlstPlanMonthlies = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstPlanMonthlies)) return null;
        return vlstPlanMonthlies.get(0);
    }

    /**
     * luu lich su cap nhat vao bang plan_monthly_his
     *
     * @param (planMonthly): truyen vao model PlanMonthly
     * @return (khong)
     * @author: hungnn
     * @version: 1.0 (sau khi du?c s?a d?i s? tang th�m)
     * @since: 21019/10/16
     */
    @Transactional
    @Override
    public void savePlanMonthlyHis(PlanMonthly planMonthly) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        Date vdtCurrentDate = new Date();
        long vlngTime = vdtCurrentDate.getTime();
        Timestamp timestamp = new Timestamp(vlngTime);
        vsbdSqlBuilder.append("insert into plan_monthly_his (PRD_ID,SERVICE_ID,vds_channel_code,shop_code,staff_code,F_SCHEDULE,CURRENCY,org_datetime,USER,CREATE_DATE) " +
                " values (?,?,?,?,?,?,?,?,?,?)");

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter(1, planMonthly.getPrdId());
        query.setParameter(2, planMonthly.getServiceId());
        query.setParameter(3, planMonthly.getVdsChannelCode());
        query.setParameter(4, planMonthly.getShopCode());
        query.setParameter(5, planMonthly.getStaffCode());
        query.setParameter(6, planMonthly.getfSchedule());
        query.setParameter(7, planMonthly.getCurrency());
        query.setParameter(8, planMonthly.getCreateDate());
        query.setParameter(9, planMonthly.getUser());
        query.setParameter(10, timestamp);
        query.executeUpdate();

    }
}
