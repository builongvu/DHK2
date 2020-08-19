package vn.vissoft.dashboard.repo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.vissoft.dashboard.dto.PlanMonthlyDTO;
import vn.vissoft.dashboard.dto.PlanQuarterlyDTO;
import vn.vissoft.dashboard.dto.PlanYearlyDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.LevelServiceExcel;
import vn.vissoft.dashboard.dto.excel.VTTServiceExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.ManageInfoPartner;
import vn.vissoft.dashboard.model.PlanMonthly;
import vn.vissoft.dashboard.model.Staff;
import vn.vissoft.dashboard.model.VdsGroupChannel;
import vn.vissoft.dashboard.repo.ChannelRepo;
import vn.vissoft.dashboard.repo.PartnerRepo;
import vn.vissoft.dashboard.repo.PlanMonthlyRepoCustom;
import vn.vissoft.dashboard.repo.StaffRepo;
import vn.vissoft.dashboard.services.ChannelService;
import vn.vissoft.dashboard.services.ServiceService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PlanMonthlyRepoImpl implements PlanMonthlyRepoCustom {

    private static final Logger LOGGER = LogManager.getLogger(PlanMonthlyRepoImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private PartnerRepo partnerRepo;

    @Autowired
    private StaffRepo staffRepo;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ReportSqlRepoImpl reportSqlRepoImpl;

    @Autowired
    private ChannelRepo channelRepo;

    /**
     * lay ra danh sach plan_monthly theo dieu kien tim kiem
     *
     * @param planMonthlyDTO
     * @return List<PlanMonthlyDTO>
     * @throws Exception
     * @author VuBL
     * @since 2019/09
     */
    @Override
    public List<PlanMonthlyDTO> findPlanMonthlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        String vstrTypeSearch = planMonthlyDTO.getTypeSearch();
        List<PlanMonthlyDTO> vlstData = null;
        int vintFirstResult = 0;
        int vintMaxResult = 1000;
        if (planMonthlyDTO.getPager() != null) {
            vintFirstResult = (planMonthlyDTO.getPager().getPage() - 1) * planMonthlyDTO.getPager().getPageSize();
            vintMaxResult = planMonthlyDTO.getPager().getPageSize();
        }
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        if (vstrTypeSearch.equals(Constants.PLAN_MONTHLY.VDS)) {
            vsbdSqlBuilder.append("select p.id, p.vds_channel_code, p.create_date, p.currency, ")
                    .append("round(p.f_schedule, 2) as fSchedule, p.prd_id as month, p.staff_code, ")
                    .append("p.shop_code, p.user, p.service_id, ");
            vsbdSqlBuilder.append("p.channelName, p.serviceName, p.productName, p.groupServiceName, ");
            vsbdSqlBuilder.append("(cast(date_add(date_add(cast(prd_id as date), interval 1 month), interval -1 day) as signed)), ");
            vsbdSqlBuilder.append("round(@fslm \\:= (select f_schedule from plan_monthly ")
                    .append("where prd_id = cast(date_add(cast(p.prd_id as date), interval -1 month) as signed) ")
                    .append("and ifnull(vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("and service_id = p.service_id ")
                    .append("and ifnull(shop_code, 'a') = ifnull(p.shop_code, 'a') ")
                    .append("and ifnull(staff_code, 'a') = ifnull(p.staff_code, 'a')), 2) as fScheduleLastMonth, ");
            vsbdSqlBuilder.append("round(@fvlm \\:= (select th from chart_service_measure_monthly ")
                    .append("where prd_id = cast(date_add(cast(p.prd_id as date), interval -1 month) as signed) ")
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
            vsbdSqlBuilder.append("from plan_monthly p ");
            vsbdSqlBuilder.append("join service s on s.id = p.service_id ")
                    .append("join group_service g on g.id = s.group_id ")
                    .append("left outer join product pr on pr.id = g.product_id ")
                    .append("left outer join vds_group_channel c on ifnull(c.vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("left outer join manage_info_partner sh on ifnull(sh.shop_code, 'a') = ifnull(p.shop_code, 'a') and ifnull(sh.vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a')")
                    .append("left outer join vds_staff st on st.staff_code = p.staff_code ");
            vsbdSqlBuilder.append(buildSQL(planMonthlyDTO, staffDTO, vhmpMapParams));
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
            vsbdSqlBuilder.append("round(@fslm \\:= (select f_schedule from plan_monthly ")
                    .append("where prd_id = cast(date_add(cast(p.prd_id as date), interval -1 month) as signed) ")
                    .append("and ifnull(vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("and service_id = p.service_id ")
                    .append("and ifnull(shop_code, 'a') = ifnull(p.shop_code, 'a') ")
                    .append("and ifnull(staff_code, 'a') = ifnull(p.staff_code, 'a')), 2) as fScheduleLastMonth, ");
            vsbdSqlBuilder.append("round(@fvlm \\:= (select th from chart_service_measure_monthly ")
                    .append("where prd_id = cast(date_add(cast(p.prd_id as date), interval -1 month) as signed) ")
                    .append("and ifnull(vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("and service_id = p.service_id ")
                    .append("and ifnull(shop_code, 'a') = ifnull(p.shop_code, 'a') ")
                    .append("and ifnull(staff_code, 'a') = ifnull(p.staff_code, 'a')), 2) as fValueLastMonth, ");
            vsbdSqlBuilder.append("round(((p.f_schedule / @fslm) * 100), 2) as compareMonthAndLastMonth, ");
            vsbdSqlBuilder.append("round(((@fvlm / @fslm) * 100), 2) as completedLastMonth, ");
            vsbdSqlBuilder.append("p.staffName, p.unitName, ");
            vsbdSqlBuilder.append("case when p.staff_code is null then ")
                    .append("round((p.f_schedule * 100 / ")
                    .append("(select sum(f_schedule) from plan_monthly ")
                    .append("where shop_code = (select parent_shop_code from manage_info_partner ")
                    .append("where shop_code = p.shop_code ")
                    .append("and vds_channel_code = p.vds_channel_code) ")
                    .append("AND service_id = p.service_id ")
                    .append("AND vds_channel_code = p.vds_channel_code ")
                    .append(")), 2) ")
                    .append("else ")
                    .append("round((p.f_schedule *100 / ")
                    .append("(select sum(f_schedule) from plan_monthly ")
                    .append("where service_id = p.service_id ")
                    .append("and shop_code = p.shop_code AND vds_channel_code = p.vds_channel_code)), 2) ")
                    .append("end as density, p.congVan ");
            vsbdSqlBuilder.append(" FROM (select p.*, ");
            vsbdSqlBuilder.append("c.vds_channel_name as channelName, s.name as serviceName, pr.name as productName, g.name as groupServiceName, st.staff_name as staffName, sh.shop_name as unitName, s.cong_van as congVan ");
            vsbdSqlBuilder.append("from plan_monthly p ");
            vsbdSqlBuilder.append("join service s on s.id = p.service_id ")
                    .append("join group_service g on g.id = s.group_id ")
                    .append("left outer join product pr on pr.id = g.product_id ")
                    .append("left outer join vds_group_channel c on ifnull(c.vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a') ")
                    .append("left outer join manage_info_partner sh on ifnull(sh.shop_code, 'a') = ifnull(p.shop_code, 'a') and ifnull(sh.vds_channel_code, 'a') = ifnull(p.vds_channel_code, 'a')")
                    .append("left outer join vds_staff st on st.staff_code = p.staff_code ");
            vsbdSqlBuilder.append(buildSQL(planMonthlyDTO, staffDTO, vhmpMapParams));
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

        LOGGER.info("findPlanMonthlysByCondition Query : " + vsbdSqlBuilder.toString());
        LOGGER.info("findPlanMonthlysByCondition Param : " + vhmpMapParams);
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlst = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlst)) {
            vlstData = new ArrayList<>();
            for (int i = 0; i < vlst.size(); i++) {
                PlanMonthlyDTO data = new PlanMonthlyDTO();
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
                data.setfScheduleLastMonth((Double) vlst.get(i)[15]);
                data.setfValueLastMonth(DataUtil.safeToDouble(vlst.get(i)[16]));
                data.setCompareMonthAndLastMonth((Double) vlst.get(i)[17]);
                if (vlst.get(i)[15] != null) {
                    double vdblFscheduleLastMonth = (double) vlst.get(i)[15];
                    if (vdblFscheduleLastMonth == 0) {
                        data.setCompletedLastMonth((double) 100);
                    } else {
                        data.setCompletedLastMonth((Double) vlst.get(i)[18]);
                    }
                } else {
                    data.setCompletedLastMonth((Double) vlst.get(i)[18]);
                }
//                data.setCompletedLastMonth(DataUtil.safeToDouble(vlst.get(i)[18]));
                data.setStaffName(DataUtil.safeToString(vlst.get(i)[19]));
                data.setUnitName(DataUtil.safeToString(vlst.get(i)[20]));
                data.setDensity(DataUtil.safeToDouble(vlst.get(i)[21]));
                data.setCongVan(DataUtil.safeToString(vlst.get(i)[22]));
                data.setMonth(String.valueOf(DataUtil.formatPrdId(data.getPrdId())));
                data.setCycleCode(planMonthlyDTO.getCycleCode());
                if (DataUtil.compareDateAndLastSystemDate(data.getMonth())) {
                    if (serviceService.checkActiveService(data.getServiceId(), data.getPrdId(), data.getCycleCode())) {
                        data.setUpdate(true);
                    } else if (!serviceService.checkActiveService(data.getServiceId(), data.getPrdId(), data.getCycleCode())) {
                        data.setUpdate(false);
                    }
                } else if (!DataUtil.compareDateInFileAndSystem(data.getMonth())) {
                    data.setUpdate(false);
                }

                vlstData.add(data);
            }
        }
        return vlstData;
    }

    /**
     * tim ke hoach thang theo du lieu trong file excel
     *
     * @param o
     * @return
     * @author DatNT
     * @since 15/09/2019
     */
    @Override
    public PlanMonthly findPlanMonthlyFromPlan(Object o, StaffDTO staffDTO) throws Exception {
        HashMap mapParams = new HashMap();
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("select p from PlanMonthly p");
        sqlBuilder.append(buildSQLFromPlan(o, mapParams, staffDTO));

        Query query = entityManager.createQuery(sqlBuilder.toString());
        mapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        List<PlanMonthly> planMonthlies = query.getResultList();
        if (DataUtil.isNullOrEmpty(planMonthlies)) return null;
        return planMonthlies.get(0);
    }

    /**
     * them du lieu planmonthly
     *
     * @param planMonthly
     * @author DatNT
     * @since 14/09/2019
     */
    @Transactional
    @Override
    public void persist(PlanMonthly planMonthly) {
        entityManager.persist(planMonthly);
    }

    /**
     * cap nhat du lieu planmonthly
     *
     * @param planMonthly
     * @author DatNT
     * @since 14/09/2019
     */
    @Transactional
    @Override
    public void update(PlanMonthly planMonthly) {
        entityManager.merge(planMonthly);
    }

    /**
     * lay ra so ban ghi cua plan_monhly khi tim kiem
     *
     * @param planMonthlyDTO
     * @return BigInteger
     * @throws Exception
     * @author VuBL
     * @since 2019/09
     */
    @Override
    public BigInteger countPlanMonthlysByCondition(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select count(*) from plan_monthly p ");
        vsbdSqlBuilder.append(buildSQL(planMonthlyDTO, staffDTO, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (BigInteger) query.getSingleResult();
    }

    /**
     * update f_schedule theo dieu kien truyen vao
     *
     * @param (plngPlanMonthlyId,fSchedule): cap nhat f_schedule theo id
     * @return (n ? u c �): so ban ghi duoc cap nhat
     * @author: hungnn
     * @version: 1.0 (sau khi du?c s?a d?i s? tang th�m)
     * @since: 21019/10/16
     */
    @Transactional
    @Override
    public int updatePlanMonthly(Long plngPlanMonthlyId, double pdblFschedule, Timestamp updateDate) throws Exception {
        int count;
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("update plan_monthly p inner join service s on p.service_id = s.id ");
        vsbdSqlBuilder.append(" set p.f_schedule =:fSchedule,p.create_date =:createDate");
        vsbdSqlBuilder.append(" where p.id =:planMonthlyId");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("fSchedule", pdblFschedule);
        query.setParameter("planMonthlyId", plngPlanMonthlyId);
        query.setParameter("createDate", updateDate);
        count = query.executeUpdate();
        return count;
    }

    @Override
    @Transactional
    public int deletePlanMonthly(Long plngPlanMonthlyId) throws Exception {
        int count;
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("delete from plan_monthly  where id =:planMonthlyId");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("planMonthlyId", plngPlanMonthlyId);
        count = query.executeUpdate();
        return count;
    }

    /**
     * lay ra file mau cua vds
     *
     * @param planMonthlyDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author DatNT
     * @since 07/11/2019
     */
    @Override
    public List findTemplateFileVDS(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();
        List vlstPlanMonthlies = null;
        List<VdsGroupChannel> groupChannels = channelService.getActiveChannel();
        List<String> vlstChannelCodesVDS = new ArrayList<>();
        for (VdsGroupChannel v : groupChannels) {
            vlstChannelCodesVDS.add(v.getCode());
        }

        vsbdSqlBuilder.append("select p.prd_id as month, p.shop_code,p.vds_channel_code, p.staff_code, p.serviceName, serviceCode ");
        vsbdSqlBuilder.append("from ");
        vsbdSqlBuilder.append("(select :prdId as prd_id,ss.vds_channel_code,ss.shop_code,ss.staff_code,");
        vsbdSqlBuilder.append("se.code as serviceCode,se.name as serviceName,se.id as service_id ");
        vsbdSqlBuilder.append("from ");
        vsbdSqlBuilder.append("(select vds_channel_code,shop_code, null as staff_code from manage_info_partner ");
        vsbdSqlBuilder.append("where parent_shop_code = :shopCode and status ='1'");
        vsbdSqlBuilder.append("union all ");
        vsbdSqlBuilder.append("select vds_channel_code, shop_code, staff_code from vds_staff ");
        vsbdSqlBuilder.append("where shop_code =:shopCode and status ='1' ) ss, ");
        vsbdSqlBuilder.append("service se, service_channel sc ");
        vsbdSqlBuilder.append(" where se.status ='1' ");
        vsbdSqlBuilder.append("and se.service_type ='1' ");
        vsbdSqlBuilder.append("and ss.vds_channel_code = sc.vds_channel_code and se.id = sc.service_id) p ");
        vsbdSqlBuilder.append("where not exists (select 1 ");
        switch (planMonthlyDTO.getCycleCode()) {
            case Constants.CYCLE_CODE.MONTH:
                vsbdSqlBuilder.append("from plan_monthly ");
                break;
            case Constants.CYCLE_CODE.QUARTER:
                vsbdSqlBuilder.append("from plan_quarterly ");
                break;
            case Constants.CYCLE_CODE.YEAR:
                vsbdSqlBuilder.append("from plan_yearly ");
                break;
        }
        vsbdSqlBuilder.append("where prd_id = p.prd_id ");
        vsbdSqlBuilder.append("and ifnull(vds_channel_code,0) = ifnull( p.vds_channel_code,0) ");
        vsbdSqlBuilder.append("and shop_code = p.shop_code ");
        vsbdSqlBuilder.append("and ifnull(staff_code,0) = ifnull( p.staff_code,0) ");
        vsbdSqlBuilder.append("and service_id = p.service_id ) ");

        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getServiceIds())) {
            vsbdSqlBuilder.append(" and p.service_id in (select id from service ")
                    .append("where id in (:serviceId) ) ");
            vhmpMapParams.put("serviceId", planMonthlyDTO.getServiceIds());
        }
        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getCongVan())) {
            vsbdSqlBuilder.append(" and p.service_id in (select id from service ")
                    .append("where cong_van like :congVan)");
            vhmpMapParams.put("congVan", "%" + planMonthlyDTO.getCongVan().trim() + "%");
        }
        if (!DataUtil.isNullOrZero(planMonthlyDTO.getProductId())) {
            vsbdSqlBuilder.append(" and p.service_id in (select id from service ")
                    .append("where group_id in (select id from group_service ")
                    .append("where product_id = (select id from product ")
                    .append("where id = :productId)))");
            vhmpMapParams.put("productId", planMonthlyDTO.getProductId());
        }

        if (!DataUtil.isNullOrZero(planMonthlyDTO.getGroupServiceId())) {
            vsbdSqlBuilder.append(" and p.service_id in (select id from service ")
                    .append("where group_id = (select id from group_service ")
                    .append("where id = :groupServiceId))");
            vhmpMapParams.put("groupServiceId", planMonthlyDTO.getGroupServiceId());
        }
        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getChannelCode())) {
            vsbdSqlBuilder.append(" and ifnull(p.vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a')");
            vhmpMapParams.put("vdsChannelCode", planMonthlyDTO.getChannelCode());
        } else {
            vsbdSqlBuilder.append(" and p.vds_channel_code in (:vdsChannelCode) ");
            vhmpMapParams.put("vdsChannelCode", vlstChannelCodesVDS);
        }
        vsbdSqlBuilder.append("group by month,vds_channel_code,shop_code,staff_code,service_id");

        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getCycleCode())) {
            if (!DataUtil.isNullOrZero(planMonthlyDTO.getReceivedDate())) {
                Long vlngReceivedDate = 0L;
                switch (planMonthlyDTO.getCycleCode()) {
                    case Constants.CYCLE_CODE.MONTH:
                        vlngReceivedDate = DataUtil.convertToPrdIdFirstDay(planMonthlyDTO.getReceivedDate());
                        break;
                    case Constants.CYCLE_CODE.QUARTER:
                        vlngReceivedDate = DataUtil.getFirstDayOfQuarter(planMonthlyDTO.getReceivedDate());
                        break;
                    case Constants.CYCLE_CODE.YEAR:
                        vlngReceivedDate = DataUtil.getFirstPrdOfYear(planMonthlyDTO.getReceivedDate());
                        break;
                }

                vhmpMapParams.put("prdId", vlngReceivedDate);
            }
        }

        if (!DataUtil.isNullOrEmpty(staffDTO.getShopCode()))
            vhmpMapParams.put("shopCode", staffDTO.getShopCode());
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlst = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlst)) {
            vlstPlanMonthlies = new ArrayList<>();
            for (int i = 0; i < vlst.size(); i++) {
                switch (planMonthlyDTO.getCycleCode()) {
                    case Constants.CYCLE_CODE.MONTH:
                        PlanMonthlyDTO monthly = new PlanMonthlyDTO();
                        monthly.setPrdId(DataUtil.safeToLong(vlst.get(i)[0]));
                        monthly.setShopCode(DataUtil.safeToString(vlst.get(i)[1]));
                        monthly.setChannelCode(DataUtil.safeToString(vlst.get(i)[2]));
                        monthly.setStaffCode(DataUtil.safeToString(vlst.get(i)[3]));
                        monthly.setServiceName(DataUtil.safeToString(vlst.get(i)[4]));
                        monthly.setServiceCode(DataUtil.safeToString(vlst.get(i)[5]));
                        monthly.setCycleCode(planMonthlyDTO.getCycleCode());

                        vlstPlanMonthlies.add(monthly);
                        break;
                    case Constants.CYCLE_CODE.QUARTER:
                        PlanQuarterlyDTO quarterly = new PlanQuarterlyDTO();
                        quarterly.setPrdId(DataUtil.safeToLong(vlst.get(i)[0]));
                        quarterly.setShopCode(DataUtil.safeToString(vlst.get(i)[1]));
                        quarterly.setChannelCode(DataUtil.safeToString(vlst.get(i)[2]));
                        quarterly.setStaffCode(DataUtil.safeToString(vlst.get(i)[3]));
                        quarterly.setServiceName(DataUtil.safeToString(vlst.get(i)[4]));
                        quarterly.setServiceCode(DataUtil.safeToString(vlst.get(i)[5]));
                        quarterly.setCycleCode(planMonthlyDTO.getCycleCode());

                        vlstPlanMonthlies.add(quarterly);
                        break;
                    case Constants.CYCLE_CODE.YEAR:
                        PlanYearlyDTO yearly = new PlanYearlyDTO();
                        yearly.setPrdId(DataUtil.safeToLong(vlst.get(i)[0]));
                        yearly.setShopCode(DataUtil.safeToString(vlst.get(i)[1]));
                        yearly.setChannelCode(DataUtil.safeToString(vlst.get(i)[2]));
                        yearly.setStaffCode(DataUtil.safeToString(vlst.get(i)[3]));
                        yearly.setServiceName(DataUtil.safeToString(vlst.get(i)[4]));
                        yearly.setServiceCode(DataUtil.safeToString(vlst.get(i)[5]));
                        yearly.setCycleCode(planMonthlyDTO.getCycleCode());

                        vlstPlanMonthlies.add(yearly);
                        break;
                }

            }
        }
        return vlstPlanMonthlies;
    }

    /**
     * lay ra file mau cua cac cap
     *
     * @param planMonthlyDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author DatNT
     * @since 07/11/2019
     */
    @Override
    public List<PlanMonthlyDTO> findTemplateFileLevel(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();
        List vlstPlanMonthlies = null;
        List<VdsGroupChannel> groupChannels = channelService.getActiveChannel();
        List<String> vlstChannelCodesVDS = new ArrayList<>();
        List<ManageInfoPartner> vlstManageInfoPartners = partnerRepo.findManageInfoPartnerLevel(staffDTO);
        List<Staff> vlstStaffs = staffRepo.findByUnitCodeOfUser(staffDTO);
        List<String> vlstShopCodes = partnerRepo.getShopCodeLevel(vlstManageInfoPartners);
        List<String> vlstStaffCodes = staffRepo.getStaffCodeLevel(vlstStaffs);
        for (VdsGroupChannel v : groupChannels) {
            vlstChannelCodesVDS.add(v.getCode());
        }

        vsbdSqlBuilder.append("select p.prd_id as month, p.shop_code,p.vds_channel_code, p.staff_code, p.serviceName, serviceCode, p.shop_name ");
        vsbdSqlBuilder.append("from ");
        vsbdSqlBuilder.append("(select :prdId as prd_id,ss.vds_channel_code,ss.shop_code,ss.staff_code,");
        vsbdSqlBuilder.append("se.code as serviceCode,se.name as serviceName,se.id as service_id, ss.shop_name ");
        vsbdSqlBuilder.append("from ");
        vsbdSqlBuilder.append("(select vds_channel_code,shop_code, null as staff_code, shop_name as shop_name from manage_info_partner ");
        if (!DataUtil.isNullOrEmpty(vlstShopCodes)) {
            vsbdSqlBuilder.append("where shop_code in(:shopCodes)");
            vhmpMapParams.put("shopCodes", vlstShopCodes);
        } else {
            vsbdSqlBuilder.append(" where shop_code is null ");
        }
        vsbdSqlBuilder.append(" and status ='1' ");
        vsbdSqlBuilder.append("union all ");
        vsbdSqlBuilder.append("select vds_channel_code, shop_code, staff_code, null as shop_name from vds_staff ");
        vsbdSqlBuilder.append("where shop_code =:shopCodeU and status ='1' and staff_type = '1') ss, ");
        if (!DataUtil.isNullOrEmpty(staffDTO.getShopCode()))
            vhmpMapParams.put("shopCodeU", staffDTO.getShopCode());
        vsbdSqlBuilder.append("service se, service_channel sc ");
        vsbdSqlBuilder.append(" where se.status ='1' ");
        vsbdSqlBuilder.append(" and se.service_type ='1' ");
        vsbdSqlBuilder.append("and ss.vds_channel_code = sc.vds_channel_code and se.id = sc.service_id) p ");
        vsbdSqlBuilder.append("where not exists (select 1 ");
        switch (planMonthlyDTO.getCycleCode()) {
            case Constants.CYCLE_CODE.MONTH:
                vsbdSqlBuilder.append("from plan_monthly ");
                break;
            case Constants.CYCLE_CODE.QUARTER:
                vsbdSqlBuilder.append("from plan_quarterly ");
                break;
            case Constants.CYCLE_CODE.YEAR:
                vsbdSqlBuilder.append("from plan_yearly ");
                break;
        }
        vsbdSqlBuilder.append("where prd_id = p.prd_id ");
        vsbdSqlBuilder.append("and ifnull(vds_channel_code,0) = ifnull( p.vds_channel_code,0) ");
        vsbdSqlBuilder.append("and shop_code = p.shop_code ");
        vsbdSqlBuilder.append("and ifnull(staff_code,0) = ifnull( p.staff_code,0) ");
        vsbdSqlBuilder.append("and service_id = p.service_id ) ");

        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getShops()) && DataUtil.isNullOrEmpty(planMonthlyDTO.getStaffs())) {
            vsbdSqlBuilder.append(" and (p.shop_code in (:shopCode) or p.staff_code in (:staffCode)) ");
            vhmpMapParams.put("shopCode", planMonthlyDTO.getShops());
            if(!DataUtil.isNullOrEmpty(vlstStaffCodes))
                vhmpMapParams.put("staffCode", vlstStaffCodes);
            else vhmpMapParams.put("staffCode", null);
        }

        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getStaffs()) && DataUtil.isNullOrEmpty(planMonthlyDTO.getShops())) {
            vsbdSqlBuilder.append("and ((p.shop_code =:shopCodeU and p.staff_code in(:staffCode)) or shop_code in (:shopCode) )");
            vhmpMapParams.put("shopCodeU", staffDTO.getShopCode());
            vhmpMapParams.put("staffCode", planMonthlyDTO.getStaffs());
            if(!DataUtil.isNullOrEmpty(vlstShopCodes))
                vhmpMapParams.put("shopCode", vlstShopCodes);
            else vhmpMapParams.put("shopCode", null);
        }

        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getShops()) && !DataUtil.isNullOrEmpty(planMonthlyDTO.getStaffs())) {
            vsbdSqlBuilder.append("and ((p.shop_code in (:shopCode)) or ");
            vsbdSqlBuilder.append("(p.shop_code =:shopCodeOfUser and p.staff_code in(:staffCode))) ");
            vhmpMapParams.put("shopCode", planMonthlyDTO.getShops());
            vhmpMapParams.put("staffCode", planMonthlyDTO.getStaffs());
            vhmpMapParams.put("shopCodeOfUser", staffDTO.getShopCode());
        }
        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getServiceIds())) {
            vsbdSqlBuilder.append(" and p.service_id in (select id from service ")
                    .append("where id in (:serviceId) ) ");
            vhmpMapParams.put("serviceId", planMonthlyDTO.getServiceIds());
        }
        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getCongVan())) {
            vsbdSqlBuilder.append(" and p.service_id in (select id from service ")
                    .append("where cong_van like :congVan)");
            vhmpMapParams.put("congVan", "%" + planMonthlyDTO.getCongVan().trim() + "%");
        }
        vsbdSqlBuilder.append(" group by month,vds_channel_code,shop_code,staff_code,service_id");

        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getCycleCode())) {
            if (!DataUtil.isNullOrZero(planMonthlyDTO.getReceivedDate())) {
                Long vlngReceivedDate = 0L;
                switch (planMonthlyDTO.getCycleCode()) {
                    case Constants.CYCLE_CODE.MONTH:
                        vlngReceivedDate = DataUtil.convertToPrdIdFirstDay(planMonthlyDTO.getReceivedDate());
                        break;
                    case Constants.CYCLE_CODE.QUARTER:
                        vlngReceivedDate = DataUtil.getFirstDayOfQuarter(planMonthlyDTO.getReceivedDate());
                        break;
                    case Constants.CYCLE_CODE.YEAR:
                        vlngReceivedDate = DataUtil.getFirstPrdOfYear(planMonthlyDTO.getReceivedDate());
                        break;
                }

                vhmpMapParams.put("prdId", vlngReceivedDate);
            }
        }

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlst = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlst)) {
            vlstPlanMonthlies = new ArrayList<>();
            for (int i = 0; i < vlst.size(); i++) {
                switch (planMonthlyDTO.getCycleCode()) {
                    case Constants.CYCLE_CODE.MONTH:
                        PlanMonthlyDTO monthly = new PlanMonthlyDTO();
                        monthly.setPrdId(DataUtil.safeToLong(vlst.get(i)[0]));
                        monthly.setShopCode(DataUtil.safeToString(vlst.get(i)[1]));
                        monthly.setChannelCode(DataUtil.safeToString(vlst.get(i)[2]));
                        monthly.setStaffCode(DataUtil.safeToString(vlst.get(i)[3]));
                        monthly.setServiceName(DataUtil.safeToString(vlst.get(i)[4]));
                        monthly.setServiceCode(DataUtil.safeToString(vlst.get(i)[5]));
                        monthly.setShopName(DataUtil.safeToString(vlst.get(i)[6]));
                        monthly.setCycleCode(planMonthlyDTO.getCycleCode());

                        vlstPlanMonthlies.add(monthly);
                        break;
                    case Constants.CYCLE_CODE.QUARTER:
                        PlanQuarterlyDTO quarterly = new PlanQuarterlyDTO();
                        quarterly.setPrdId(DataUtil.safeToLong(vlst.get(i)[0]));
                        quarterly.setShopCode(DataUtil.safeToString(vlst.get(i)[1]));
                        quarterly.setChannelCode(DataUtil.safeToString(vlst.get(i)[2]));
                        quarterly.setStaffCode(DataUtil.safeToString(vlst.get(i)[3]));
                        quarterly.setServiceName(DataUtil.safeToString(vlst.get(i)[4]));
                        quarterly.setServiceCode(DataUtil.safeToString(vlst.get(i)[5]));
                        quarterly.setShopName(DataUtil.safeToString(vlst.get(i)[6]));
                        quarterly.setCycleCode(planMonthlyDTO.getCycleCode());

                        vlstPlanMonthlies.add(quarterly);
                        break;
                    case Constants.CYCLE_CODE.YEAR:
                        PlanYearlyDTO yearly = new PlanYearlyDTO();
                        yearly.setPrdId(DataUtil.safeToLong(vlst.get(i)[0]));
                        yearly.setShopCode(DataUtil.safeToString(vlst.get(i)[1]));
                        yearly.setChannelCode(DataUtil.safeToString(vlst.get(i)[2]));
                        yearly.setStaffCode(DataUtil.safeToString(vlst.get(i)[3]));
                        yearly.setServiceName(DataUtil.safeToString(vlst.get(i)[4]));
                        yearly.setServiceCode(DataUtil.safeToString(vlst.get(i)[5]));
                        yearly.setShopName(DataUtil.safeToString(vlst.get(i)[6]));
                        yearly.setCycleCode(planMonthlyDTO.getCycleCode());

                        vlstPlanMonthlies.add(yearly);
                        break;
                }

            }
        }
        return vlstPlanMonthlies;
    }

    /**
     * dieu kien tim kiem giao chi tieu
     *
     * @param planMonthlyDTO
     * @param params
     * @return StringBuilder
     * @author VuBL
     * @since 2019/09
     */
    public StringBuilder buildSQL(PlanMonthlyDTO planMonthlyDTO, StaffDTO staffDTO, HashMap params) throws Exception {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        Long vlngReceivedDate = null;
        List<ManageInfoPartner> vlstManageInfoPartners = partnerRepo.findManageInfoPartnerLevel(staffDTO);
        List<String> vlstShopCodes = partnerRepo.getShopCodeLevel(vlstManageInfoPartners);
        List<VdsGroupChannel> groupChannels = channelService.getActiveChannel();
//        List<Staff> vlstStaffs = staffRepo.findByUnitCodeOfUser(staffDTO);
//        List<String> vlstStaffCodes = staffRepo.getStaffCodeLevel(vlstStaffs);
        List<String> vlstChannelCodesVDS = new ArrayList<>();
        for (VdsGroupChannel v : groupChannels) {
            vlstChannelCodesVDS.add(v.getCode());
        }

        vsbdStringBuilder.append(" where 1=1 ");

        if (!DataUtil.isNullOrZero(planMonthlyDTO.getReceivedDate())) {
            if (planMonthlyDTO.getCycleCode().equals(Constants.CYCLE_CODE.MONTH)) {
                vlngReceivedDate = DataUtil.convertToPrdIdFirstDay(planMonthlyDTO.getReceivedDate());
            }
            if (planMonthlyDTO.getCycleCode().equals(Constants.CYCLE_CODE.QUARTER)) {
                vlngReceivedDate = DataUtil.getFirstDayOfQuarter(planMonthlyDTO.getReceivedDate());
            }
            if (planMonthlyDTO.getCycleCode().equals(Constants.CYCLE_CODE.YEAR)) {
                vlngReceivedDate = DataUtil.getFirstPrdOfYear(planMonthlyDTO.getReceivedDate());
            }
            vsbdStringBuilder.append(" and p.prd_id = :prdId");
            params.put("prdId", vlngReceivedDate);
        }

        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getServiceIds())) {
            vsbdStringBuilder.append(" and p.service_id in (select id from service ")
                    .append("where id in (:serviceId) and status = '1' and service_type = 1) ");
            params.put("serviceId", planMonthlyDTO.getServiceIds());
        } else {
            vsbdStringBuilder.append(" and p.service_id in (select id from service where status = '1' and service_type = 1)");
        }

        if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getCongVan())) {
            vsbdStringBuilder.append(" and p.service_id in (select id from service ")
                    .append("where cong_van like :congVan)");
            params.put("congVan", "%" + planMonthlyDTO.getCongVan().trim() + "%");
        }

        //dieu kien tim kiem rieng cho VDS
        if (planMonthlyDTO.getTypeSearch().equals(Constants.PLAN_MONTHLY.VDS)) {

            vsbdStringBuilder.append(" and p.staff_code is null ");

            vsbdStringBuilder.append(" and p.shop_code in (select shop_code from manage_info_partner ")
                    .append("where vds_channel_code = p.vds_channel_code ")
                    .append("and parent_shop_code = 'VDS') ");

            if (!DataUtil.isNullOrZero(planMonthlyDTO.getProductId())) {
                vsbdStringBuilder.append(" and p.service_id in (select id from service ")
                        .append("where group_id in (select id from group_service ")
                        .append("where product_id = (select id from product ")
                        .append("where id = :productId)))");
                params.put("productId", planMonthlyDTO.getProductId());
            }

            if (!DataUtil.isNullOrZero(planMonthlyDTO.getGroupServiceId())) {
                vsbdStringBuilder.append(" and p.service_id in (select id from service ")
                        .append("where group_id = (select id from group_service ")
                        .append("where id = :groupServiceId))");
                params.put("groupServiceId", planMonthlyDTO.getGroupServiceId());
            }

            if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getChannelCode())) {
                vsbdStringBuilder.append(" and ifnull(p.vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a')");
                params.put("vdsChannelCode", planMonthlyDTO.getChannelCode());
            } else {
                vsbdStringBuilder.append(" and p.vds_channel_code in (:vdsChannelCode) ");
                params.put("vdsChannelCode", vlstChannelCodesVDS);
            }
        }

        //dieu kien tim kiem rieng cho giao cac cap
        if (planMonthlyDTO.getTypeSearch().equals(Constants.PLAN_MONTHLY.LEVEL)) {

            if (staffDTO.getShopCode().equals(Constants.PLAN_MONTHLY.VDS)) {
                vsbdStringBuilder.append(" and p.staff_code is null ");
            }

            if (!staffDTO.getShopCode().equals(Constants.PLAN_MONTHLY.VDS)) {
                vsbdStringBuilder.append(" and ifnull(p.vds_channel_code, 'a') = ifnull(:vdsChannelCodeU, 'a') ");
                params.put("vdsChannelCodeU", staffDTO.getVdsChannelCode());
            }

            if (DataUtil.isNullOrEmpty(planMonthlyDTO.getShops()) && DataUtil.isNullOrEmpty(planMonthlyDTO.getStaffs())) {
                if (vlstShopCodes.size() > 0) {
                    vsbdStringBuilder.append("and ((p.staff_code is null and p.shop_code in (:shopCodes)) or (p.staff_code is not null and p.shop_code = :shopCode)) ");
                    params.put("shopCodes", vlstShopCodes);
                    params.put("shopCode", staffDTO.getShopCode());
                } else {
                    vsbdStringBuilder.append("and (p.staff_code is not null and p.shop_code = :shopCode)");
                    params.put("shopCode", staffDTO.getShopCode());
                }
//                if (!DataUtil.isNullOrEmpty(vlstStaffs)) {
//                    vsbdStringBuilder.append(" and (p.shop_code in (:shopCodes) or p.staff_code in (:staffCodes)) ");
//                    params.put("staffCodes", vlstStaffCodes);
//                } else {
//                    vsbdStringBuilder.append(" and p.shop_code in (:shopCodes) ");
//                }
//                if (DataUtil.isNullOrEmpty(vlstShopCodes)) {
//                    params.put("shopCodes", null);
//                } else {
//                    params.put("shopCodes", vlstShopCodes);
//                }
            }

            if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getShops()) && DataUtil.isNullOrEmpty(planMonthlyDTO.getStaffs())) {
                vsbdStringBuilder.append("and p.shop_code in (:shopCode) and p.staff_code is null ");
                params.put("shopCode", planMonthlyDTO.getShops());
            }

            if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getStaffs()) && DataUtil.isNullOrEmpty(planMonthlyDTO.getShops())) {
                vsbdStringBuilder.append("and p.shop_code = :shopCodeU and p.staff_code in(:staffCode) ");
                params.put("shopCodeU", staffDTO.getShopCode());
                params.put("staffCode", planMonthlyDTO.getStaffs());
            }

            if (!DataUtil.isNullOrEmpty(planMonthlyDTO.getShops()) && !DataUtil.isNullOrEmpty(planMonthlyDTO.getStaffs())) {
                vsbdStringBuilder.append("and ((p.shop_code in (:shopCode) and p.staff_code is null) or ");
                vsbdStringBuilder.append("(p.shop_code = :shopCodeOfUser and p.staff_code in(:staffCode))) ");
                params.put("shopCode", planMonthlyDTO.getShops());
                params.put("staffCode", planMonthlyDTO.getStaffs());
                params.put("shopCodeOfUser", staffDTO.getShopCode());
            }
        }

        return vsbdStringBuilder;
    }

    /**
     * dieu kien tim kiem giao chi tieu trong file excel
     *
     * @param o
     * @param params
     * @return
     * @author DatNT
     * @since 15/09/2019
     */
    public StringBuilder buildSQLFromPlan(Object o, HashMap params, StaffDTO staffDTO) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" where 1=1 ");
        if (o instanceof VTTServiceExcel) {
            VTTServiceExcel vttServiceExcel = (VTTServiceExcel) o;

            if (!DataUtil.isNullOrEmpty(vttServiceExcel.getMonth()) && !DataUtil.isNullOrEmpty(vttServiceExcel.getCycleCode())) {
                stringBuilder.append(" and p.prdId = :prdId");
                switch (vttServiceExcel.getCycleCode()) {
                    case Constants.CYCLE_CODE.MONTH:
                        params.put("prdId", DataUtil.convertStringToLong(vttServiceExcel.getMonth().trim()));
                        break;
                    case Constants.CYCLE_CODE.QUARTER:
                        params.put("prdId", DataUtil.convertStringToFirstQuarter(vttServiceExcel.getMonth().trim()));
                        break;
                    case Constants.CYCLE_CODE.YEAR:
                        params.put("prdId", DataUtil.convertStringToFirstYear(vttServiceExcel.getMonth().trim()));
                        break;
                }

            }
            if (!DataUtil.isNullOrEmpty(vttServiceExcel.getServiceCode())) {
                stringBuilder.append(" and p.serviceId = (select id from Service where code =:serviceCode)");
                params.put("serviceCode", vttServiceExcel.getServiceCode().trim());
            }

            if (!DataUtil.isNullOrEmpty(vttServiceExcel.getChannelCode())) {
                stringBuilder.append(" and p.vdsChannelCode = :channelCode");
                stringBuilder.append(" and p.shopCode=(select shopCode from ManageInfoPartner  where  vdsChannelCode =:channelCode and parentShopCode ='VDS')");

                params.put("channelCode", vttServiceExcel.getChannelCode().trim());

            }
            stringBuilder.append(" and p.staffCode is null");
        }
        if (o instanceof LevelServiceExcel) {
            LevelServiceExcel levelServiceExcel = (LevelServiceExcel) o;
            String vstrChannelCode = channelRepo.findChannelByShopCode(levelServiceExcel.getUnit().trim()).getCode();

            if (!DataUtil.isNullOrEmpty(levelServiceExcel.getMonth()) && !DataUtil.isNullOrEmpty(levelServiceExcel.getCycleCode())) {
                stringBuilder.append(" and p.prdId = :prdId");
                switch (levelServiceExcel.getCycleCode()) {
                    case Constants.CYCLE_CODE.MONTH:
                        params.put("prdId", DataUtil.convertStringToLong(levelServiceExcel.getMonth().trim()));
                        break;
                    case Constants.CYCLE_CODE.QUARTER:
                        params.put("prdId", DataUtil.convertStringToFirstQuarter(levelServiceExcel.getMonth().trim()));
                        break;
                    case Constants.CYCLE_CODE.YEAR:
                        params.put("prdId", DataUtil.convertStringToFirstYear(levelServiceExcel.getMonth().trim()));
                        break;
                }

            }
            if (!DataUtil.isNullOrEmpty(levelServiceExcel.getServiceCode())) {
                stringBuilder.append(" and p.serviceId = (select id from Service where code =:serviceCode)");
                params.put("serviceCode", levelServiceExcel.getServiceCode().trim());
            }

            if (!DataUtil.isNullOrEmpty(levelServiceExcel.getUnit())) {
                stringBuilder.append(" and p.shopCode = :shopCode");
                params.put("shopCode", levelServiceExcel.getUnit().trim());
            }
            if (!DataUtil.isNullOrEmpty(levelServiceExcel.getStaff())) {
                stringBuilder.append(" and p.staffCode = :staffCode");
                params.put("staffCode", levelServiceExcel.getStaff().trim());

            } else {
                stringBuilder.append(" and p.staffCode is null");
            }

            if (DataUtil.isNullOrEmpty(vstrChannelCode)) {
                stringBuilder.append(" and p.vdsChannelCode is null");
            } else {
                stringBuilder.append(" and p.vdsChannelCode =:vdsChannelCode");
//                params.put("channelId", channelRepo.findChannelIdByStaffCode(levelServiceExcel.getStaff()));
                params.put("vdsChannelCode", vstrChannelCode);
            }
        }

        return stringBuilder;
    }

    @Override
    public double getScheduleOfShop(PlanMonthly planMonthlyDTO, StaffDTO staffDTO) throws Exception {
        StringBuilder sbdBuilder = new StringBuilder();
        sbdBuilder.append("select f_schedule from plan_monthly  where prd_id = :prdId and vds_channel_code = :vdsChannelCode and shop_code = :shopCode and service_id = :serviceId");
        Query query = entityManager.createNativeQuery(sbdBuilder.toString());
        query.setParameter("prdId", planMonthlyDTO.getPrdId());
        query.setParameter("vdsChannelCode", planMonthlyDTO.getVdsChannelCode());
        query.setParameter("shopCode", staffDTO.getShopCode());
        query.setParameter("serviceId", planMonthlyDTO.getServiceId());

        List<Double> sumSChedule = query.getResultList();

        return sumSChedule.size() == 0 ? 0d : sumSChedule.get(0);
    }

    @Override
    public double getSumScheduleChildShop(PlanMonthly planMonthlyDTO, List<Long> pIds, StaffDTO staffDTO) throws Exception {
        StringBuilder sbdBuilder = new StringBuilder();
        List<Long> ids = pIds.stream().filter(a -> a != null).collect(Collectors.toList());
        sbdBuilder.append("SELECT SUM(f_schedule) FROM plan_monthly ")
                .append("WHERE (shop_code IN (SELECT shop_code FROM manage_info_partner WHERE parent_shop_code = :parentShopCode) ")
                .append("or(shop_code = :parentShopCode and staff_code is not null)) ")
                .append("AND vds_channel_code = :vdsChannelCode ")
                .append("AND id NOT IN (:ids) ")
                .append("AND prd_id = :prdId ")
                .append("AND service_id = :serviceId ")
                .append("GROUP BY service_id");

        Query query = entityManager.createNativeQuery(sbdBuilder.toString());
        query.setParameter("parentShopCode", staffDTO.getShopCode());
        query.setParameter("vdsChannelCode", planMonthlyDTO.getVdsChannelCode());

        if (!DataUtil.isNullOrEmpty(ids)) {
            query.setParameter("ids", ids);
        } else {
            query.setParameter("ids", 0);
        }
        query.setParameter("serviceId", planMonthlyDTO.getServiceId());
        query.setParameter("prdId", planMonthlyDTO.getPrdId());
        List<Double> sumSchedules = query.getResultList();

        return sumSchedules.size() == 0 ? 0d : sumSchedules.get(0);
    }

    @Override
    public double getSumScheduleStaff(PlanMonthly planMonthlyDTO, List<Long> pIds, StaffDTO staffDTO) throws Exception {
        StringBuilder sbdBuilder = new StringBuilder();
        List<Long> ids = pIds.stream().filter(a -> a != null).collect(Collectors.toList());
        sbdBuilder.append("SELECT SUM(f_schedule) FROM plan_monthly ")
                .append("WHERE shop_code = :parentShopCode ")
                .append("AND vds_channel_code = :vdsChannelCode ")
                .append("AND staff_code is not null ")
                .append("AND id NOT IN (:ids) ")
                .append("AND prd_id = :prdId ")
                .append("AND service_id = :serviceId ")
                .append("GROUP BY service_id");

        Query query = entityManager.createNativeQuery(sbdBuilder.toString());
        query.setParameter("parentShopCode", staffDTO.getShopCode());
        query.setParameter("vdsChannelCode", planMonthlyDTO.getVdsChannelCode());

        if (!DataUtil.isNullOrEmpty(ids)) {
            query.setParameter("ids", ids);
        } else {
            query.setParameter("ids", 0);
        }
        query.setParameter("serviceId", planMonthlyDTO.getServiceId());
        query.setParameter("prdId", planMonthlyDTO.getPrdId());
        List<Double> sumSchedules = query.getResultList();

        return sumSchedules.size() == 0 ? 0d : sumSchedules.get(0);
    }


    /**
     * lay ra danh sach plan_monthly theo dieu kien tim kiem sql
     *
     * @param planMonthlyDTO
     * @return List<PlanMonthlyDTO>
     * @throws Exception
     * @author QuangND
     * @since 2019/09
     */
    @Override
    public List<PlanMonthlyDTO> findPlanMonthlysByReportSql(PlanMonthlyDTO planMonthlyDTO) throws Exception {
        String sql = reportSqlRepoImpl.findReportSqlById(planMonthlyDTO.getReportSqlId()).getContent();
        List<PlanMonthlyDTO> vlstData = null;
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append(sql);

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());

        if (planMonthlyDTO.getFromDate() != null) {
            Long vlngFromDate = planMonthlyDTO.getFromDate();
            Long vlngFromDatePrd = DataUtil.convertToPrdId(vlngFromDate);
            query.setParameter("fromDate", vlngFromDatePrd);
        }

        if (planMonthlyDTO.getToDate() != null) {
            Long vlngToDate = planMonthlyDTO.getToDate();
            Long vlngToDatePrd = DataUtil.convertToPrdId(vlngToDate);
            query.setParameter("toDate", vlngToDatePrd);
        }

        if (planMonthlyDTO.getShopCode() != null)
            query.setParameter("shopCode", planMonthlyDTO.getShopCode());

        List<Object[]> vlst = query.getResultList();

        if (!DataUtil.isNullOrEmpty(vlst)) {
            vlstData = new ArrayList<>();
            for (Object[] obj : vlst) {
                PlanMonthlyDTO data = new PlanMonthlyDTO();
                data.setServiceId(DataUtil.safeToLong(obj[0]));
                data.setfSchedule(DataUtil.safeToDouble(obj[1]));
                vlstData.add(data);
            }
        }
        return vlstData;
    }


}
