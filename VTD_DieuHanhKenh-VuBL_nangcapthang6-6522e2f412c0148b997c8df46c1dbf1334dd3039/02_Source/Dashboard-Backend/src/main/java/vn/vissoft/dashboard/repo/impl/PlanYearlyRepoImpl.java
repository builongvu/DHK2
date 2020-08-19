package vn.vissoft.dashboard.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.PlanQuarterlyDTO;
import vn.vissoft.dashboard.dto.PlanYearlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.PlanMonthly;
import vn.vissoft.dashboard.model.PlanYearly;
import vn.vissoft.dashboard.repo.PlanYearlyRepoCustom;
import vn.vissoft.dashboard.services.ServiceService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PlanYearlyRepoImpl implements PlanYearlyRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private PlanMonthlyRepoImpl planMonthlyRepo;

    @Autowired
    private ServiceService serviceService;

    @Override
    public List<PlanYearlyDTO> findPlanYearlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        String vstrTypeSearch = planMonthlyDTO.getTypeSearch();
        List<PlanYearlyDTO> vlstData = null;
        int vintFirstResult = 0;
        int vintMaxResult = 1000;
        if (planMonthlyDTO.getPager() != null) {
            vintFirstResult = (planMonthlyDTO.getPager().getPage() - 1) * planMonthlyDTO.getPager().getPageSize();
            vintMaxResult = planMonthlyDTO.getPager().getPageSize();
        }
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        Date vdtDate = new Date(planMonthlyDTO.getReceivedDate());
        int vintYear = vdtDate.toLocalDate().getYear();

        if (vstrTypeSearch.equals(Constants.PLAN_MONTHLY.VDS)) {
            vsbdSqlBuilder.append("select p.id, p.vds_channel_code, p.create_date, p.currency, ")
                    .append("round(p.f_schedule, 2) as fSchedule, p.prd_id as month, p.staff_code, ")
                    .append("p.shop_code, p.user, p.service_id, ");
            vsbdSqlBuilder.append("p.channelName, p.serviceName, p.productName, p.groupServiceName, ");
            vsbdSqlBuilder.append("(cast(date_add(date_add(cast(prd_id as date), interval 1 month), interval -1 day) as signed)), ");
            vsbdSqlBuilder.append("round(@fslm \\:= (select f_schedule from plan_yearly ")
                    .append("where prd_id = cast(date_add(cast(p.prd_id as date), interval -1 year) as signed) ")
                    .append("and ifnull(vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("and service_id = p.service_id ")
                    .append("and ifnull(shop_code, 'a') = ifnull(p.shop_code, 'a') ")
                    .append("and ifnull(staff_code, 'a') = ifnull(p.staff_code, 'a')), 2) as fScheduleLastMonth, ");
            vsbdSqlBuilder.append("round(@fvlm \\:= (select th from chart_service_measure_yearly ")
                    .append("where prd_id = cast(date_add(cast(p.prd_id as date), interval -1 year) as signed) ")
                    .append("and ifnull(vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("and service_id = p.service_id ")
                    .append("and ifnull(shop_code, 'a') = ifnull(p.shop_code, 'a') ")
                    .append("and ifnull(staff_code, 'a') = ifnull(p.staff_code, 'a')), 2) as fValueLastMonth, ");
            vsbdSqlBuilder.append("round(((p.f_schedule / @fslm) * 100), 2) as compareMonthAndLastMonth, ");
            vsbdSqlBuilder.append("round(((@fvlm / @fslm) * 100), 2) as completedLastMonth, ");
            vsbdSqlBuilder.append("'a', 'b', ");
            vsbdSqlBuilder.append("0 as density, ");
            vsbdSqlBuilder.append("p.congVan ");
            vsbdSqlBuilder.append(" FROM (select p.*, ");
            vsbdSqlBuilder.append("c.vds_channel_name as channelName, s.name as serviceName, pr.name as productName, g.name as groupServiceName, st.staff_name as staffName, sh.shop_name as unitName, s.cong_van as congVan ");
            vsbdSqlBuilder.append("from plan_yearly p ");
            vsbdSqlBuilder.append("join service s on s.id = p.service_id ")
                    .append("join group_service g on g.id = s.group_id ")
                    .append("left outer join product pr on pr.id = g.product_id ")
                    .append("left outer join vds_group_channel c on ifnull(c.vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("left outer join manage_info_partner sh on ifnull(sh.shop_code, 'a') = ifnull(p.shop_code, 'a') and ifnull(sh.vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a')")
                    .append("left outer join vds_staff st on st.staff_code = p.staff_code ");
            vsbdSqlBuilder.append(planMonthlyRepo.buildSQL(planMonthlyDTO, staffDTO, vhmpMapParams));
            if (!DataUtil.isNullObject(planMonthlyDTO.getSort())) {
                if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getSort().getColumn()) &&
                        !DataUtil.isNullOrEmpty(planMonthlyDTO.getSort().getType())) {
                    if (planMonthlyDTO.getSort().getColumn().equals(Constants.PLAN_MONTHLY.MONTH)) {
                        vsbdSqlBuilder.append(" order by prd_id " + planMonthlyDTO.getSort().getType());
                    } else {
                        vsbdSqlBuilder.append(" order by " + planMonthlyDTO.getSort().getColumn() + " collate utf8_vietnamese_ci " + planMonthlyDTO.getSort().getType());
                    }
                }
            }
            vsbdSqlBuilder.append(" limit " + vintMaxResult + " offset " + vintFirstResult);
            vsbdSqlBuilder.append(" ) p");
        }

        if (vstrTypeSearch.equals(Constants.PLAN_MONTHLY.LEVEL)) {
            vsbdSqlBuilder.append("select p.id, p.vds_channel_code, p.create_date, p.currency, ")
                    .append("round(p.f_schedule, 2) as fSchedule, p.prd_id as month, p.staff_code, ")
                    .append("p.shop_code, p.user, p.service_id, ");
            vsbdSqlBuilder.append("'a', p.serviceName, 'b', 'c', ");
            vsbdSqlBuilder.append("(cast(date_add(date_add(cast(prd_id as date), interval 1 month), interval -1 day) as signed)), ");
            vsbdSqlBuilder.append("round(@fslm \\:= (select f_schedule from plan_yearly ")
                    .append("where prd_id = cast(date_add(cast(p.prd_id as date), interval -1 year) as signed) ")
                    .append("and ifnull(vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("and service_id = p.service_id ")
                    .append("and ifnull(shop_code, 'a') = ifnull(p.shop_code, 'a') ")
                    .append("and ifnull(staff_code, 'a') = ifnull(p.staff_code, 'a')), 2) as fScheduleLastMonth, ");
            vsbdSqlBuilder.append("round(@fvlm \\:= (select th from chart_service_measure_yearly ")
                    .append("where prd_id = cast(date_add(cast(p.prd_id as date), interval -1 year) as signed) ")
                    .append("and ifnull(vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("and service_id = p.service_id ")
                    .append("and ifnull(shop_code, 'a') = ifnull(p.shop_code, 'a') ")
                    .append("and ifnull(staff_code, 'a') = ifnull(p.staff_code, 'a')), 2) as fValueLastMonth, ");
            vsbdSqlBuilder.append("round(((p.f_schedule / @fslm) * 100), 2) as compareMonthAndLastMonth, ");
            vsbdSqlBuilder.append("round(((@fvlm / @fslm) * 100), 2) as completedLastMonth, ");
            vsbdSqlBuilder.append("p.staffName, p.unitName, ");
            vsbdSqlBuilder.append("case when p.staff_code is null then ")
                    .append("round((p.f_schedule / ")
                    .append("(select (sum(f_schedule) / 100) from plan_yearly ")
                    .append("where shop_code = (select parent_shop_code from manage_info_partner ")
                    .append("where ifnull(shop_code, 'a') = ifnull(p.shop_code, 'a') ")
                    .append("and ifnull(vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a')))), 2) ")
                    .append("else ")
                    .append("round((p.f_schedule / ")
                    .append("(select (sum(f_schedule) / 100) from plan_yearly ")
                    .append("where service_id = p.service_id ")
                    .append("and shop_code = p.shop_code)), 2) ")
                    .append("end as density, p.congVan ");
            vsbdSqlBuilder.append(" FROM (select p.*, ");
            vsbdSqlBuilder.append("c.vds_channel_name as channelName, s.name as serviceName, pr.name as productName, g.name as groupServiceName, st.staff_name as staffName, sh.shop_name as unitName, s.cong_van as congVan ");
            vsbdSqlBuilder.append("from plan_yearly p ");
            vsbdSqlBuilder.append("join service s on s.id = p.service_id ")
                    .append("join group_service g on g.id = s.group_id ")
                    .append("left outer join product pr on pr.id = g.product_id ")
                    .append("left outer join vds_group_channel c on ifnull(c.vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("left outer join manage_info_partner sh on ifnull(sh.shop_code, 'a') = ifnull(p.shop_code, 'a') and ifnull(sh.vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a')")
                    .append("left outer join vds_staff st on st.staff_code = p.staff_code ");
            vsbdSqlBuilder.append(planMonthlyRepo.buildSQL(planMonthlyDTO, staffDTO, vhmpMapParams));
            if (!DataUtil.isNullObject(planMonthlyDTO.getSort())) {
                if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getSort().getColumn()) &&
                        !DataUtil.isNullOrEmpty(planMonthlyDTO.getSort().getType())) {
                    if (planMonthlyDTO.getSort().getColumn().equals(Constants.PLAN_MONTHLY.MONTH)) {
                        vsbdSqlBuilder.append(" order by prd_id " + planMonthlyDTO.getSort().getType());
                    } else {
                        vsbdSqlBuilder.append(" order by " + planMonthlyDTO.getSort().getColumn() + " collate utf8_vietnamese_ci " + planMonthlyDTO.getSort().getType());
                    }
                }
            }
            vsbdSqlBuilder.append(" limit " + vintMaxResult + " offset " + vintFirstResult);
            vsbdSqlBuilder.append(" ) p");
        }

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlst = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlst)) {
            vlstData = new ArrayList<>();
            for (int i = 0; i < vlst.size(); i++) {
                PlanYearlyDTO data = new PlanYearlyDTO();
                data.setId(DataUtil.safeToLong(vlst.get(i)[0]));
                data.setChannelCode(DataUtil.safeToString(vlst.get(i)[1]));
                data.setCreateDate((Timestamp) vlst.get(i)[2]);
                data.setCurrency(DataUtil.safeToString(vlst.get(i)[3]));
                data.setfSchedule(DataUtil.safeToDouble(vlst.get(i)[4]));
                data.setPrdId(DataUtil.safeToLong(vlst.get(i)[5]));
                data.setStaffCode(DataUtil.safeToString(vlst.get(i)[6]));
                data.setShopCode(DataUtil.safeToString(vlst.get(i)[7]));
                data.setUser(DataUtil.safeToString(vlst.get(i)[8]));
                data.setServiceId(DataUtil.safeToLong(vlst.get(i)[9]));
                data.setChannelName(DataUtil.safeToString(vlst.get(i)[10]));
                data.setServiceName(DataUtil.safeToString(vlst.get(i)[11]));
                data.setProductName(DataUtil.safeToString(vlst.get(i)[12]));
                data.setGroupServiceName(DataUtil.safeToString(vlst.get(i)[13]));
                data.setPrdIdEndOfMonth(DataUtil.safeToLong(vlst.get(i)[14]));
                data.setfScheduleLastMonth(DataUtil.safeToDouble(vlst.get(i)[15]));
                data.setfValueLastMonth(DataUtil.safeToDouble(vlst.get(i)[16]));
                data.setCompareMonthAndLastMonth(DataUtil.safeToDouble(vlst.get(i)[17]));
                data.setCompletedLastMonth(DataUtil.safeToDouble(vlst.get(i)[18]));
                data.setStaffName(DataUtil.safeToString(vlst.get(i)[19]));
                data.setUnitName(DataUtil.safeToString(vlst.get(i)[20]));
                data.setDensity(DataUtil.safeToDouble(vlst.get(i)[21]));
                data.setCongVan(DataUtil.safeToString(vlst.get(i)[22]));
                data.setMonth(String.valueOf(DataUtil.formatPrdId(data.getPrdId())));
                data.setYear(String.valueOf(vintYear));
                data.setCycleCode(planMonthlyDTO.getCycleCode());
                if (serviceService.checkActiveService(data.getServiceId(), data.getPrdId(), data.getCycleCode())) {
                    data.setUpdate(true);
                } else if (!serviceService.checkActiveService(data.getServiceId(), data.getPrdId(), data.getCycleCode())) {
                    data.setUpdate(false);
                }

                vlstData.add(data);
            }
        }
        return vlstData;
    }

    @Override
    public BigInteger countPlanYearlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select count(*) from plan_yearly p ");
        vsbdSqlBuilder.append(planMonthlyRepo.buildSQL(planMonthlyDTO, staffDTO, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (BigInteger) query.getSingleResult();
    }

    @Override
    public int updatePlanYearly(Long plngPlanYearlyId, double pdblFschedule, Timestamp updateDate) throws Exception {
        int count;
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("update plan_yearly p inner join service s on p.service_id = s.id ");
        vsbdSqlBuilder.append(" set p.f_schedule =:fSchedule,p.create_date =:createDate");
        vsbdSqlBuilder.append(" where p.id =:planYearlyId");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("fSchedule", pdblFschedule);
        query.setParameter("planYearlyId", plngPlanYearlyId);
        query.setParameter("createDate", updateDate);
        count = query.executeUpdate();
        return count;
    }

    @Transactional
    @Override
    public int deletePlanYearly(Long plngPlanYearId) throws Exception {
        int count;
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("delete from plan_yearly  where id =:planYearlyId");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("planYearlyId", plngPlanYearId);
        count = query.executeUpdate();
        return count;
    }

    @Override
    public PlanYearly findPlanYearlyFromPlan(Object o, StaffDTO staffDTO) throws Exception {
        HashMap mapParams = new HashMap();
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("select p from PlanYearly p");
        sqlBuilder.append(planMonthlyRepo.buildSQLFromPlan(o, mapParams, staffDTO));

        Query query = entityManager.createQuery(sqlBuilder.toString());
        mapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        List<PlanYearly> planYearlies = query.getResultList();
        if (DataUtil.isNullOrEmpty(planYearlies)) return null;
        return planYearlies.get(0);
    }

    /**
     * them du lieu planyearly
     *
     * @param planYearly
     * @author DatNT
     * @since 14/09/2019
     */
    @Transactional
    @Override
    public void persist(PlanYearly planYearly) {
        entityManager.persist(planYearly);
    }

    /**
     * cap nhat du lieu planyearly
     *
     * @param planYearly
     * @author DatNT
     * @since 14/09/2019
     */
    @Transactional
    @Override
    public void update(PlanYearly planYearly) {
        entityManager.merge(planYearly);
    }

    @Override
    public double getScheduleOfShop(PlanYearly planYearly, StaffDTO staffDTO) throws Exception {
        StringBuilder sbdBuilder = new StringBuilder();
        sbdBuilder.append("select f_schedule from plan_yearly  where prd_id = :prdId and vds_channel_code = :vdsChannelCode and shop_code = :shopCode and service_id = :serviceId");
        Query query = entityManager.createNativeQuery(sbdBuilder.toString());
        query.setParameter("prdId", planYearly.getPrdId());
        query.setParameter("vdsChannelCode", planYearly.getVdsChannelCode());
        query.setParameter("shopCode", staffDTO.getShopCode());
        query.setParameter("serviceId", planYearly.getServiceId());

        List<Double> sumSChedule = query.getResultList();

        return sumSChedule.size() == 0 ? 0d : sumSChedule.get(0);
    }

    @Override
    public double getSumScheduleChildShop(PlanYearly planYearly, List<Long> pIds, StaffDTO staffDTO) throws Exception {
        StringBuilder sbdBuilder = new StringBuilder();
        List<Long> ids = pIds.stream().filter(a -> a != null).collect(Collectors.toList());
        sbdBuilder.append("SELECT SUM(f_schedule) FROM plan_yearly ")
                .append("WHERE (shop_code IN (SELECT shop_code FROM manage_info_partner WHERE parent_shop_code = :parentShopCode) ")
                .append("or(shop_code = :parentShopCode and staff_code is not null)) ")
                .append("AND vds_channel_code = :vdsChannelCode ")
                .append("AND id NOT IN (:ids) ")
                .append("AND prd_id = :prdId ")
                .append("AND service_id = :serviceId ")
                .append("GROUP BY service_id");

        Query query = entityManager.createNativeQuery(sbdBuilder.toString());
        query.setParameter("parentShopCode", staffDTO.getShopCode());
        query.setParameter("vdsChannelCode", planYearly.getVdsChannelCode());

        if (!DataUtil.isNullOrEmpty(ids)) {
            query.setParameter("ids", ids);
        } else {
            query.setParameter("ids", 0);
        }
        query.setParameter("serviceId", planYearly.getServiceId());
        query.setParameter("prdId", planYearly.getPrdId());
        List<Double> sumSchedules = query.getResultList();

        return sumSchedules.size() == 0 ? 0d : sumSchedules.get(0);
    }

    @Override
    public double getSumScheduleStaff(PlanYearly planYearly, List<Long> pIds, StaffDTO staffDTO) throws Exception {
        StringBuilder sbdBuilder = new StringBuilder();
        List<Long> ids = pIds.stream().filter(a -> a != null).collect(Collectors.toList());
        sbdBuilder.append("SELECT SUM(f_schedule) FROM plan_yearly ")
                .append("WHERE shop_code = :parentShopCode ")
                .append("AND vds_channel_code = :vdsChannelCode ")
                .append("AND staff_code is not null ")
                .append("AND id NOT IN (:ids) ")
                .append("AND prd_id = :prdId ")
                .append("AND service_id = :serviceId ")
                .append("GROUP BY service_id");

        Query query = entityManager.createNativeQuery(sbdBuilder.toString());
        query.setParameter("parentShopCode", staffDTO.getShopCode());
        query.setParameter("vdsChannelCode", planYearly.getVdsChannelCode());

        if (!DataUtil.isNullOrEmpty(ids)) {
            query.setParameter("ids", ids);
        } else {
            query.setParameter("ids", 0);
        }
        query.setParameter("serviceId", planYearly.getServiceId());
        query.setParameter("prdId", planYearly.getPrdId());
        List<Double> sumSchedules = query.getResultList();

        return sumSchedules.size() == 0 ? 0d : sumSchedules.get(0);
    }

}
