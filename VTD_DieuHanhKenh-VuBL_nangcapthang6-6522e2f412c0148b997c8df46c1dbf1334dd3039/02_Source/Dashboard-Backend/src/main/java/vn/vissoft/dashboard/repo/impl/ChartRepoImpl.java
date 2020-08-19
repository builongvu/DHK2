package vn.vissoft.dashboard.repo.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.chart.PointDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Unit;
import vn.vissoft.dashboard.repo.ChartRepoCustom;
import vn.vissoft.dashboard.repo.PartnerRepo;
import vn.vissoft.dashboard.repo.ServiceRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class ChartRepoImpl implements ChartRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private PartnerRepo partnerRepo;

    private NumberFormat format = NumberFormat.getInstance();
    private DecimalFormat df = new DecimalFormat("###.##");

    private SimpleDateFormat formatToPrd = new SimpleDateFormat("yyyyMMdd");

    /**
     * lay ra du lieu tong hop bieu do
     *
     * @param dashboardRequestDTO
     * @param plngServiceId
     * @param staffDTO
     * @return
     * @author VuBL
     */
    @Override
    public List<Object[]> findAllDataChart(DashboardRequestDTO dashboardRequestDTO, Long plngServiceId, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        java.sql.Date vdtDate = new Date(dashboardRequestDTO.getPrdId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vdtDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Long vlngPrdStartMonth = Long.valueOf(formatToPrd.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Long vlngPrdEndMonth = Long.valueOf(formatToPrd.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
        Long vlngPrdStartYear = Long.valueOf(formatToPrd.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        Long vlngPrdEndYear = Long.valueOf(formatToPrd.format(cal.getTime()));
        Long vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        //CHU KI NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSqlBuilder.append("select case when prd_id <= :prdId then th else null end, ")
                    .append("th_thang_n1, th_thang_n2, th_nam_n1, kh, ");
            vsbdSqlBuilder.append("date_format(cast(prd_id as date), '%d/%m/%Y') as month, ");
            vsbdSqlBuilder.append("case when day(date_add(cast(prd_id as date), interval -1 month)) != day(cast(prd_id as date)) then null ")
                    .append("else date_format(date_add(cast(prd_id as date), interval -1 month), '%d/%m/%Y') end as lastMonth, ");
            vsbdSqlBuilder.append("case when day(date_add(cast(prd_id as date), interval -2 month)) != day(cast(prd_id as date)) then null ")
                    .append("else date_format(date_add(cast(prd_id as date), interval -2 month), '%d/%m/%Y') end as lastTwoMonth, ");
            vsbdSqlBuilder.append("case when day(date_add(cast(prd_id as date), interval -1 year)) != day(cast(prd_id as date)) then null ")
                    .append("else date_format(date_add(cast(prd_id as date), interval -1 year), '%d/%m/%Y') end as lastYear, ");
            vsbdSqlBuilder.append("dvt, vds_channel_code, shop_code, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate ");
            vsbdSqlBuilder.append("from chart_service_measure_daily p ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, plngServiceId, vhmpMapParams, staffDTO, configSingleChartDTO));
            if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getObjectCode())) {
                vsbdSqlBuilder.append(" and staff_code is null ");
            }
//            vsbdSqlBuilder.append("and prd_id between :prdStartMonth and :prdEndMonth ");
//            vhmpMapParams.put("prdStartMonth", vlngPrdStartMonth);
//            vhmpMapParams.put("prdEndMonth", vlngPrdEndMonth);
            String stringPrdIdDay = DataUtil.getStringPrdIdForWhere(vlngPrdStartMonth, vlngPrdEndMonth, vintCycleId);
            if (stringPrdIdDay != null) {
                vsbdSqlBuilder.append("and prd_id in (" + stringPrdIdDay + ") ");
            }
            vsbdSqlBuilder.append("order by prd_id ");
        }
        //CHU KI THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSqlBuilder.append("select case when prd_id <= :prdId then th else null end, ")
                    .append("kh, th_nam_n1, ");
            vsbdSqlBuilder.append("date_format(cast(prd_id as date), '%m/%Y'), ");
            vsbdSqlBuilder.append("date_format(date_add(cast(prd_id as date), interval -1 year), '%m/%Y'), ");
            vsbdSqlBuilder.append("dvt, vds_channel_code, shop_code, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate ");
            vsbdSqlBuilder.append("from chart_service_measure_monthly p ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, plngServiceId, vhmpMapParams, staffDTO, configSingleChartDTO));
            if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getObjectCode())) {
                vsbdSqlBuilder.append(" and staff_code is null ");
            }
            vsbdSqlBuilder.append("and prd_id between :prdStartYear and :prdEndYear ");
            vhmpMapParams.put("prdStartYear", vlngPrdStartYear);
            vhmpMapParams.put("prdEndYear", vlngPrdEndYear);
            vsbdSqlBuilder.append("order by prd_id ");
        }
        //CHU KI QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSqlBuilder.append("select case when prd_id <= :prdId then th else null end as th, ")
                    .append("kh, th_nam_n1, ");
            vsbdSqlBuilder.append("concat('Q', quarter(cast(prd_id as date)), '/', year(cast(prd_id as date))), ");
            vsbdSqlBuilder.append("concat('Q', quarter(date_add((cast(prd_id as date)), interval -1 year)), '/', year(date_add((cast(prd_id as date)), interval -1 year))), ");
            vsbdSqlBuilder.append("dvt, vds_channel_code, shop_code, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate ");
            vsbdSqlBuilder.append("from chart_service_measure_quaterly p ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, plngServiceId, vhmpMapParams, staffDTO, configSingleChartDTO));
            if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getObjectCode())) {
                vsbdSqlBuilder.append(" and staff_code is null ");
            }
            vsbdSqlBuilder.append("and prd_id between :prdStartYear and :prdEndYear ");
            vhmpMapParams.put("prdStartYear", vlngPrdStartYear);
            vhmpMapParams.put("prdEndYear", vlngPrdEndYear);
            vsbdSqlBuilder.append("order by prd_id ");
        }

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.put("prdId", vlngPrdId);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlstData = query.getResultList();
        return vlstData;
    }

    /**
     * lay duong xu the cho table tong hop chi tiet
     *
     * @param plngServiceId
     * @param plngPrdId
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/10
     */
    @Override
    public List<PointDTO> findDataTrendChart(Long plngServiceId, Long plngPrdId, String pstrShopCode, int pintCycleId, String pstrChannelCode, String pstrStaffCode) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        String vstrUnitType = null;
        Double vdblRate = null;
        List<PointDTO> vlstData = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();
        List<String> vlstCategoriesNoData = getCategories(plngPrdId, Constants.LINE_VALUE.TREND, pintCycleId);
        Long vlngPrId = DataUtil.convertToPrdId(plngPrdId);
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        //CHU KI NGAY
        if (pintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSqlBuilder.append("select date_format(cast(prd_id as date), '%d/%m/%Y'), ")
                    .append("th ")
                    .append("from chart_service_measure_daily r ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            if (!DataUtil.isNullOrZero(plngPrdId)) {
                vsbdSqlBuilder.append("and prd_id between cast(date_add(cast(:prdId as date), interval -29 day) as signed) and :prdId ");
                vhmpMapParams.put("prdId", vlngPrId);
            }
        }
        //CHU KI THANG
        if (pintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSqlBuilder.append("select date_format(cast(prd_id as date), '%d/%m/%Y'), ")
                    .append("th ")
                    .append("from chart_service_measure_monthly r ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            if (!DataUtil.isNullOrZero(plngPrdId)) {
                vsbdSqlBuilder.append("and prd_id between cast(date_add(cast(:prdId as date), interval -12 month) as signed) and :prdId ");
                vhmpMapParams.put("prdId", vlngPrId);
            }
        }
        //CHU KI QUY
        if (pintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSqlBuilder.append("select date_format(cast(prd_id as date), '%d/%m/%Y'), ")
                    .append("th ")
                    .append("from chart_service_measure_quaterly r ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            if (!DataUtil.isNullOrZero(plngPrdId)) {
                vsbdSqlBuilder.append("and prd_id between cast(date_add((makedate(year(cast(:prdId as date)), 1) + interval quarter(cast(:prdId as date)) quarter - interval 1 quarter), interval -4 quarter) as signed) ")
                        .append("and :prdId ");
                vhmpMapParams.put("prdId", vlngPrId);
            }
        }

        if (!DataUtil.isNullOrZero(plngServiceId)) {
            vsbdSqlBuilder.append("and service_id = :serviceId ");
            vhmpMapParams.put("serviceId", plngServiceId);
        }
        if (!DataUtil.isNullOrEmpty(pstrShopCode)) {
            vsbdSqlBuilder.append("and ifnull(shop_code, 'a') = ifnull(:shopCode, 'a') ");
            vhmpMapParams.put("shopCode", pstrShopCode);
        }

        if (!DataUtil.isNullOrEmpty(pstrStaffCode)) {
            vsbdSqlBuilder.append("and ifnull(staff_code, 'a') = ifnull(:staffCode, 'a') ");
            vhmpMapParams.put("staffCode", pstrStaffCode);
        } else {
            vsbdSqlBuilder.append(" and staff_code is null ");
        }

        if (!DataUtil.isNullOrEmpty(pstrChannelCode)) {
            vsbdSqlBuilder.append(" and ifnull(vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a') ");
            vhmpMapParams.put("vdsChannelCode", pstrChannelCode);
        } else {
            vsbdSqlBuilder.append(" and vds_channel_code is null ");
        }
        vsbdSqlBuilder.append("order by prd_id");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlst = query.getResultList();
        for (Object[] obj : vlst) {
            vlstCategories.add(DataUtil.safeToString(obj[0]));
        }

        vlstCategoriesNoData.removeAll(vlstCategories);

        for (int i = 0; i < vlstCategoriesNoData.size(); i++) {
            PointDTO point = new PointDTO();
            point.setCategory(vlstCategoriesNoData.get(i));
            vlstData.add(point);
        }

        if (!DataUtil.isNullOrEmpty(vlst)) {

            for (Object[] obj : vlst) {

                Double vdblPerformValue = (Double) obj[1];
                if (!DataUtil.isNullOrZero(vdblPerformValue) && !DataUtil.isNullOrZero(vdblRate)) {
                    vdblPerformValue = vdblPerformValue / vdblRate;
                }
                if (vdblPerformValue != null) {
                    Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
                    PointDTO data = new PointDTO();
                    data.setCategory(DataUtil.safeToString(obj[0]));
                    if (vdblValue != null) {
                        data.setValue(vdblValue);
                        if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
                            data.setViewValue(null);
                        } else {
                            data.setViewValue(format.format(vdblValue) + " " + vstrUnitType);
                        }
                        vlstData.add(data);
                    }
                } else {
                    PointDTO data = new PointDTO();
                    data.setCategory(DataUtil.safeToString(obj[0]));
                    vlstData.add(data);
                }

            }
        }
        //Sap xep point theo thu tu ngay tang dan
        Collections.sort(vlstData, new Comparator<PointDTO>() {
            @Override
            public int compare(PointDTO p1, PointDTO p2) {
                return DataUtil.convertStringToDate(p1.getCategory()).compareTo(DataUtil.convertStringToDate(p2.getCategory()));
            }
        });
        return vlstData;
    }

    /**
     * lay ra du lieu bieu do level 2 theo don vi
     *
     * @param dashboardRequestDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * since 2019/11/05
     */
    @Override
    public List<Object[]> findDataChildShop(DashboardRequestDTO dashboardRequestDTO, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChartDTO, String pstrSort) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        java.sql.Date vdtDate = new Date(dashboardRequestDTO.getPrdId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vdtDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Long vlngPrdStartMonth = Long.valueOf(formatToPrd.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Long vlngPrdEndMonth = Long.valueOf(formatToPrd.format(cal.getTime()));
        Long vlngPrdQuarter = Long.valueOf(DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId()));
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        //CHU KI NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSqlBuilder.append("select m.short_name as shop_name, ");
            vsbdSqlBuilder.append("lk, kh_lk, lk_thang_n1, p.vds_channel_code, p.shop_code, dvt, ");
            vsbdSqlBuilder.append("((lk / kh_lk) * 100), service_id, staff_code, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), ");
            vsbdSqlBuilder.append("ty_le_ht, ty_le_ht_lk ");
            vsbdSqlBuilder.append("from chart_service_measure_daily p ");
            vsbdSqlBuilder.append("join manage_info_partner m on m.shop_code = p.shop_code and (m.vds_channel_code = p.vds_channel_code or (m.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, dashboardRequestDTO.getServiceId(), vhmpMapParams, staffDTO, configSingleChartDTO));
            vsbdSqlBuilder.append("and prd_id = :prdId ");
            vsbdSqlBuilder.append("and staff_code is null ");
            vsbdSqlBuilder.append("union all ");
            vsbdSqlBuilder.append("select s.staff_name, ");
            vsbdSqlBuilder.append("lk, kh_lk, lk_thang_n1, p.vds_channel_code, p.shop_code, dvt, ");
            vsbdSqlBuilder.append("((lk / kh_lk) * 100), service_id, p.staff_code, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), ");
            vsbdSqlBuilder.append("ty_le_ht, ty_le_ht_lk ");
            vsbdSqlBuilder.append("from chart_service_measure_daily p ");
            vsbdSqlBuilder.append("join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code and (s.vds_channel_code = p.vds_channel_code or (s.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            vsbdSqlBuilder.append("and prd_id = :prdId ");
            vsbdSqlBuilder.append("and p.shop_code = :shopCode ");
            vsbdSqlBuilder.append("and service_id = :serviceId ");
            if (!DataUtil.isNullOrEmpty(pstrSort)) {
                if (pstrSort.equals(Constants.MAX)) {
                    vsbdSqlBuilder.append("order by (case when service_id IN (SELECT CODE FROM ap_param WHERE TYPE = 'NO_PLAN_DAILY' AND STATUS = '1') then ty_le_ht ELSE ty_le_ht_lk END) desc limit 5 ");
                }
                if (pstrSort.equals(Constants.MIN)) {
                    vsbdSqlBuilder.append("order by (case when service_id IN (SELECT CODE FROM ap_param WHERE TYPE = 'NO_PLAN_DAILY' AND STATUS = '1') then ty_le_ht ELSE ty_le_ht_lk END) asc limit 5 ");
                }
                if (pstrSort.equals(Constants.REDUCTION)) {
                    vsbdSqlBuilder.append("order by (case when service_id IN (SELECT CODE FROM ap_param WHERE TYPE = 'NO_PLAN_DAILY' AND STATUS = '1') then ty_le_ht ELSE ty_le_ht_lk END) desc ");
                }
            }

            vhmpMapParams.put("prdId", vlngPrdId);
        }
        //CHU KI THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSqlBuilder.append("select m.short_name as shop_name, ");
            vsbdSqlBuilder.append("lk, kh_lk, lk_nam_n1, p.vds_channel_code, p.shop_code, dvt, ");
            vsbdSqlBuilder.append("((lk / kh_lk) * 100), service_id, staff_code, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), ");
            vsbdSqlBuilder.append("ty_le_ht, ty_le_ht_lk ");
            vsbdSqlBuilder.append("from chart_service_measure_monthly p ");
            vsbdSqlBuilder.append("join manage_info_partner m on m.shop_code = p.shop_code and (m.vds_channel_code = p.vds_channel_code or (m.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, dashboardRequestDTO.getServiceId(), vhmpMapParams, staffDTO, configSingleChartDTO));
            vsbdSqlBuilder.append("and prd_id between :prdStartMonth and :prdEndMonth ");
            vsbdSqlBuilder.append("and staff_code is null ");
            vsbdSqlBuilder.append("union all ");
            vsbdSqlBuilder.append("select s.staff_name, ");
            vsbdSqlBuilder.append("lk, kh_lk, lk_nam_n1, p.vds_channel_code, p.shop_code, dvt, ");
            vsbdSqlBuilder.append("((lk / kh_lk) * 100), service_id, p.staff_code, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), ");
            vsbdSqlBuilder.append("ty_le_ht, ty_le_ht_lk ");
            vsbdSqlBuilder.append("from chart_service_measure_monthly p ");
            vsbdSqlBuilder.append("join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code and (s.vds_channel_code = p.vds_channel_code or (s.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            vsbdSqlBuilder.append("and prd_id between :prdStartMonth and :prdEndMonth ");
            vsbdSqlBuilder.append("and p.shop_code = :shopCode ");
            vsbdSqlBuilder.append("and service_id = :serviceId ");
            if (!DataUtil.isNullOrEmpty(pstrSort)) {
                if (pstrSort.equals(Constants.MAX)) {
                    vsbdSqlBuilder.append("order by (case when service_id IN (SELECT CODE FROM ap_param WHERE TYPE = 'NO_PLAN_DAILY' AND STATUS = '1') then ty_le_ht ELSE ty_le_ht_lk END) desc limit 5 ");
                }
                if (pstrSort.equals(Constants.MIN)) {
                    vsbdSqlBuilder.append("order by (case when service_id IN (SELECT CODE FROM ap_param WHERE TYPE = 'NO_PLAN_DAILY' AND STATUS = '1') then ty_le_ht ELSE ty_le_ht_lk END) asc limit 5 ");
                }
                if (pstrSort.equals(Constants.REDUCTION)) {
                    vsbdSqlBuilder.append("order by (case when service_id IN (SELECT CODE FROM ap_param WHERE TYPE = 'NO_PLAN_DAILY' AND STATUS = '1') then ty_le_ht ELSE ty_le_ht_lk END) desc ");
                }
            }
            vhmpMapParams.put("prdStartMonth", vlngPrdStartMonth);
            vhmpMapParams.put("prdEndMonth", vlngPrdEndMonth);
        }
        //CHU KI QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSqlBuilder.append("select m.short_name as shop_name, ");
            vsbdSqlBuilder.append("lk, kh_lk, lk_nam_n1, p.vds_channel_code, p.shop_code, dvt, ");
            vsbdSqlBuilder.append("((lk / kh_lk) * 100), service_id, staff_code, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), ");
            vsbdSqlBuilder.append("ty_le_ht, ty_le_ht_lk ");
            vsbdSqlBuilder.append("from chart_service_measure_quaterly p ");
            vsbdSqlBuilder.append("join manage_info_partner m on m.shop_code = p.shop_code and (m.vds_channel_code = p.vds_channel_code or (m.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, dashboardRequestDTO.getServiceId(), vhmpMapParams, staffDTO, configSingleChartDTO));
            vsbdSqlBuilder.append("and prd_id = :prdQuarter ");
            vsbdSqlBuilder.append("and staff_code is null ");
            vsbdSqlBuilder.append("union all ");
            vsbdSqlBuilder.append("select s.staff_name, ");
            vsbdSqlBuilder.append("lk, kh_lk, lk_nam_n1, p.vds_channel_code, p.shop_code, dvt, ");
            vsbdSqlBuilder.append("((lk / kh_lk) * 100), service_id, p.staff_code, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), ");
            vsbdSqlBuilder.append("ty_le_ht, ty_le_ht_lk ");
            vsbdSqlBuilder.append("from chart_service_measure_quaterly p ");
            vsbdSqlBuilder.append("join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code and (s.vds_channel_code = p.vds_channel_code or (s.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            vsbdSqlBuilder.append("and prd_id = :prdQuarter ");
            vsbdSqlBuilder.append("and p.shop_code = :shopCode ");
            vsbdSqlBuilder.append("and service_id = :serviceId ");
            if (!DataUtil.isNullOrEmpty(pstrSort)) {
                if (pstrSort.equals(Constants.MAX)) {
                    vsbdSqlBuilder.append("order by (case when service_id IN (SELECT CODE FROM ap_param WHERE TYPE = 'NO_PLAN_DAILY' AND STATUS = '1') then ty_le_ht ELSE ty_le_ht_lk END) desc limit 5 ");
                }
                if (pstrSort.equals(Constants.MIN)) {
                    vsbdSqlBuilder.append("order by (case when service_id IN (SELECT CODE FROM ap_param WHERE TYPE = 'NO_PLAN_DAILY' AND STATUS = '1') then ty_le_ht ELSE ty_le_ht_lk END) asc limit 5 ");
                }
                if (pstrSort.equals(Constants.REDUCTION)) {
                    vsbdSqlBuilder.append("order by (case when service_id IN (SELECT CODE FROM ap_param WHERE TYPE = 'NO_PLAN_DAILY' AND STATUS = '1') then ty_le_ht ELSE ty_le_ht_lk END) desc ");
                }
            }

            vhmpMapParams.put("prdQuarter", vlngPrdQuarter);
        }

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlstData = query.getResultList();
        return vlstData;
    }

    @Override
    public List<Object[]> findDataChildShopTable(DashboardRequestDTO dashboardRequestDTO, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngPrdId = null;
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        //CHU KI NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSqlBuilder.append("select * from ( ");
            vsbdSqlBuilder.append("select m.short_name as shop_name, lk, kh_lk, lk_thang_n1, p.vds_channel_code, p.shop_code, dvt, ");
            vsbdSqlBuilder.append("ty_le_ht_lk, service_id, staff_code, (select name from unit where code = p.dvt and status = '1') as unit_name, ");
            vsbdSqlBuilder.append("prd_id, 'TONG_HOP' row_code, p.trend, 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ");
            vsbdSqlBuilder.append("th, kh, ty_le_ht ");
            vsbdSqlBuilder.append("from chart_service_measure_daily p ");
            vsbdSqlBuilder.append("join manage_info_partner m on m.shop_code = p.shop_code and (m.vds_channel_code = p.vds_channel_code or (m.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, dashboardRequestDTO.getServiceId(), vhmpMapParams, staffDTO, configSingleChartDTO));
            vsbdSqlBuilder.append("and prd_id = :prdId ");
            vsbdSqlBuilder.append("and staff_code is null ");
            vsbdSqlBuilder.append("union all ");
            vsbdSqlBuilder.append("select s.staff_name, lk, kh_lk, lk_thang_n1, p.vds_channel_code, p.shop_code, dvt, ty_le_ht_lk, ");
            vsbdSqlBuilder.append("service_id, p.staff_code, (select name from unit where code = p.dvt and status = '1') as unit_name, ");
            vsbdSqlBuilder.append("prd_id, 'TONG_HOP' row_code, p.trend, 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ");
            vsbdSqlBuilder.append("th, kh, ty_le_ht ");
            vsbdSqlBuilder.append("from chart_service_measure_daily p ");
            vsbdSqlBuilder.append("join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code and (s.vds_channel_code = p.vds_channel_code or (s.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            vsbdSqlBuilder.append("and prd_id = :prdId ");
            vsbdSqlBuilder.append("and (p.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null) ");
            vsbdSqlBuilder.append("and p.shop_code = :shopCode ");
            vsbdSqlBuilder.append("and service_id = :serviceId ");
            vsbdSqlBuilder.append("and p.staff_code is not null ");
            vsbdSqlBuilder.append(") a ");
            vsbdSqlBuilder.append("order by vds_channel_code, shop_code, staff_code, service_id, shop_name desc ");
            if (dashboardRequestDTO.getPrdId() != null) {
                vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
            }
            vhmpMapParams.put("prdId", vlngPrdId);
        }
        //CHU KI THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSqlBuilder.append("select * from ( ");
            vsbdSqlBuilder.append("select m.short_name as shop_name, lk, kh_lk, lk_nam_n1, p.vds_channel_code, p.shop_code, dvt, ");
            vsbdSqlBuilder.append("ty_le_ht_lk, service_id, staff_code, (select name from unit where code = p.dvt and status = '1') as unit_name, ");
            vsbdSqlBuilder.append("prd_id, 'TONG_HOP' row_code, p.trend, 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ");
            vsbdSqlBuilder.append("th, kh, ty_le_ht ");
            vsbdSqlBuilder.append("from chart_service_measure_monthly p ");
            vsbdSqlBuilder.append("join manage_info_partner m on m.shop_code = p.shop_code and (m.vds_channel_code = p.vds_channel_code or (m.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, dashboardRequestDTO.getServiceId(), vhmpMapParams, staffDTO, configSingleChartDTO));
            vsbdSqlBuilder.append("and prd_id = :prdId ");
            vsbdSqlBuilder.append("and staff_code is null ");
            vsbdSqlBuilder.append("union all ");
            vsbdSqlBuilder.append("select s.staff_name, lk, kh_lk, lk_nam_n1, p.vds_channel_code, p.shop_code, dvt, ty_le_ht_lk, ");
            vsbdSqlBuilder.append("service_id, p.staff_code, (select name from unit where code = p.dvt and status = '1') as unit_name, ");
            vsbdSqlBuilder.append("prd_id, 'TONG_HOP' row_code, p.trend, 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ");
            vsbdSqlBuilder.append("th, kh, ty_le_ht ");
            vsbdSqlBuilder.append("from chart_service_measure_monthly p ");
            vsbdSqlBuilder.append("join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code and (s.vds_channel_code = p.vds_channel_code or (s.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            vsbdSqlBuilder.append("and prd_id = :prdId ");
            vsbdSqlBuilder.append("and (p.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null) ");
            vsbdSqlBuilder.append("and p.shop_code = :shopCode ");
            vsbdSqlBuilder.append("and service_id = :serviceId ");
            vsbdSqlBuilder.append("and p.staff_code is not null ");
            vsbdSqlBuilder.append(") a ");
            vsbdSqlBuilder.append("order by vds_channel_code, shop_code, staff_code, service_id, shop_name desc ");
            if (dashboardRequestDTO.getPrdId() != null) {
                vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
            }
            vhmpMapParams.put("prdId", vlngPrdId);
        }
        //CHU KI QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSqlBuilder.append("select * from ( ");
            vsbdSqlBuilder.append("select m.short_name as shop_name, lk, kh_lk, lk_nam_n1, p.vds_channel_code, p.shop_code, dvt, ");
            vsbdSqlBuilder.append("ty_le_ht_lk, service_id, staff_code, (select name from unit where code = p.dvt and status = '1') as unit_name, ");
            vsbdSqlBuilder.append("prd_id, 'TONG_HOP' row_code, p.trend, 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ");
            vsbdSqlBuilder.append("th, kh, ty_le_ht ");
            vsbdSqlBuilder.append("from chart_service_measure_quaterly p ");
            vsbdSqlBuilder.append("join manage_info_partner m on m.shop_code = p.shop_code and (m.vds_channel_code = p.vds_channel_code or (m.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, dashboardRequestDTO.getServiceId(), vhmpMapParams, staffDTO, configSingleChartDTO));
            vsbdSqlBuilder.append("and prd_id = :prdId ");
            vsbdSqlBuilder.append("and staff_code is null ");
            vsbdSqlBuilder.append("union all ");
            vsbdSqlBuilder.append("select s.staff_name, lk, kh_lk, lk_nam_n1, p.vds_channel_code, p.shop_code, dvt, ty_le_ht_lk, ");
            vsbdSqlBuilder.append("service_id, p.staff_code, (select name from unit where code = p.dvt and status = '1') as unit_name, ");
            vsbdSqlBuilder.append("prd_id, 'TONG_HOP' row_code, p.trend, 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ");
            vsbdSqlBuilder.append("th, kh, ty_le_ht ");
            vsbdSqlBuilder.append("from chart_service_measure_quaterly p ");
            vsbdSqlBuilder.append("join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code and (s.vds_channel_code = p.vds_channel_code or (s.vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            vsbdSqlBuilder.append("and prd_id = :prdId ");
            vsbdSqlBuilder.append("and (p.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null) ");
            vsbdSqlBuilder.append("and p.shop_code = :shopCode ");
            vsbdSqlBuilder.append("and service_id = :serviceId ");
            vsbdSqlBuilder.append("and p.staff_code is not null ");
            vsbdSqlBuilder.append(") a ");
            vsbdSqlBuilder.append("order by vds_channel_code, shop_code, staff_code, service_id, shop_name desc ");
            if (dashboardRequestDTO.getPrdId() != null) {
                vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
            }
            vhmpMapParams.put("prdId", vlngPrdId);
        }

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlstData = query.getResultList();
        return vlstData;
    }

    /**
     * lay ra du lieu bieu do level 2 theo goi thue bao
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public List<Object[]> finDataChildService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO, String pstrSort) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        java.sql.Date vdtDate = new Date(dashboardRequestDTO.getPrdId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vdtDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Long vlngPrdStartMonth = Long.valueOf(formatToPrd.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Long vlngPrdEndMonth = Long.valueOf(formatToPrd.format(cal.getTime()));
        Long vlngPrdQuarter = Long.valueOf(DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId()));
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        //CHU KI NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSqlBuilder.append("select (select name from service where id = p.service_id) as service_name, ");
            vsbdSqlBuilder.append("lk, kh_lk, lk_thang_n1, vds_channel_code, shop_code, dvt, ");
            vsbdSqlBuilder.append("((lk / kh_lk) * 100), service_id, ");
            vsbdSqlBuilder.append("(lk / ");
            vsbdSqlBuilder.append("(select lk from chart_service_measure_daily ");
            vsbdSqlBuilder.append(" where service_id = :serviceId ");
            vsbdSqlBuilder.append(" and prd_id = p.prd_id ");
            vsbdSqlBuilder.append(" and shop_code = p.shop_code ");
            vsbdSqlBuilder.append(" and (vds_channel_code = p.vds_channel_code or (vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append(" and ifnull(staff_code, 'a') = ifnull(p.staff_code, 'a')) * 100) as density, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate ");
            vsbdSqlBuilder.append("from chart_service_measure_daily p ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, dashboardRequestDTO.getServiceId(), vhmpMapParams, staffDTO, configSingleChartDTO));
            vsbdSqlBuilder.append("and prd_id = :prdId ");
            vsbdSqlBuilder.append("and staff_code is null ");
            if (!DataUtil.isNullOrEmpty(pstrSort)) {
                if (pstrSort.equals(Constants.REDUCTION)) {
                    vsbdSqlBuilder.append("order by lk desc ");
                }
            }
            vhmpMapParams.put("prdId", vlngPrdId);
        }
        //CHU KI THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSqlBuilder.append("select (select name from service where id = p.service_id) as service_name, ");
            vsbdSqlBuilder.append("lk, kh_lk, lk_nam_n1, vds_channel_code, shop_code, dvt, ");
            vsbdSqlBuilder.append("((lk / kh_lk) * 100), service_id, ");
            vsbdSqlBuilder.append("(lk / ");
            vsbdSqlBuilder.append("(select lk from chart_service_measure_monthly ");
            vsbdSqlBuilder.append(" where service_id = :serviceId ");
            vsbdSqlBuilder.append(" and prd_id = p.prd_id ");
            vsbdSqlBuilder.append(" and shop_code = p.shop_code ");
            vsbdSqlBuilder.append(" and (vds_channel_code = p.vds_channel_code or (vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append(" and ifnull(staff_code, 'a') = ifnull(p.staff_code, 'a')) * 100) as density, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate ");
            vsbdSqlBuilder.append("from chart_service_measure_monthly p ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, dashboardRequestDTO.getServiceId(), vhmpMapParams, staffDTO, configSingleChartDTO));
            vsbdSqlBuilder.append("and prd_id between :prdStartMonth and :prdEndMonth ");
            vsbdSqlBuilder.append("and staff_code is null ");
            vhmpMapParams.put("prdStartMonth", vlngPrdStartMonth);
            vhmpMapParams.put("prdEndMonth", vlngPrdEndMonth);
            if (!DataUtil.isNullOrEmpty(pstrSort)) {
                if (pstrSort.equals(Constants.REDUCTION)) {
                    vsbdSqlBuilder.append("order by lk desc ");
                }
            }
        }
        //CHU KI QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSqlBuilder.append("select (select name from service where id = p.service_id) as service_name, ");
            vsbdSqlBuilder.append("lk, kh_lk, lk_nam_n1, vds_channel_code, shop_code, dvt, ");
            vsbdSqlBuilder.append("((lk / kh_lk) * 100), service_id, ");
            vsbdSqlBuilder.append("(lk / ");
            vsbdSqlBuilder.append("(select lk from chart_service_measure_quaterly ");
            vsbdSqlBuilder.append(" where service_id = :serviceId ");
            vsbdSqlBuilder.append(" and prd_id = p.prd_id ");
            vsbdSqlBuilder.append(" and shop_code = p.shop_code ");
            vsbdSqlBuilder.append(" and (vds_channel_code = p.vds_channel_code or (vds_channel_code is null or p.vds_channel_code is null)) ");
            vsbdSqlBuilder.append(" and ifnull(staff_code, 'a') = ifnull(p.staff_code, 'a')) * 100) as density, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), 1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = p.shop_code ")
                    .append("AND su.service_id = p.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate ");
            vsbdSqlBuilder.append("from chart_service_measure_quaterly p ");
            vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, dashboardRequestDTO.getServiceId(), vhmpMapParams, staffDTO, configSingleChartDTO));
            vsbdSqlBuilder.append("and prd_id = :prdQuarter ");
            vsbdSqlBuilder.append("and staff_code is null ");
            if (!DataUtil.isNullOrEmpty(pstrSort)) {
                if (pstrSort.equals(Constants.REDUCTION)) {
                    vsbdSqlBuilder.append("order by lk desc ");
                }
            }
            vhmpMapParams.put("prdQuarter", vlngPrdQuarter);
        }

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlstData = query.getResultList();
        return vlstData;
    }

    /**
     * lay du lieu bieu do duong theo goi thue bao o dashboard level 2 (chart)
     *
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public List<Object[]> findDataLChartChildService(DashboardRequestDTO dashboardRequestDTO, Long plngServiceId) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        java.sql.Date vdtDate = new Date(dashboardRequestDTO.getPrdId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vdtDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Long vlngPrdStartMonth = Long.valueOf(formatToPrd.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
        Long vlngPrdStartYear = Long.valueOf(formatToPrd.format(cal.getTime()));
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        //CHU KI NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSqlBuilder.append("select date_format(cast(prd_id as date), '%d/%m/%Y'), ");
            vsbdSqlBuilder.append("(select name from service where id = p.service_id), ");
            vsbdSqlBuilder.append("th, vds_channel_code, shop_code, dvt, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1') ");
            vsbdSqlBuilder.append("from chart_service_measure_daily p ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            vsbdSqlBuilder.append("and staff_code is null ");
            if (!DataUtil.isNullOrZero(plngServiceId)) {
                vsbdSqlBuilder.append("and service_id = :serviceId ");
                vhmpMapParams.put("serviceId", plngServiceId);
            }
            if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getObjectCode())) {
                vsbdSqlBuilder.append("and ifnull(shop_code, 'a') = ifnull(:shopCode, 'a') ");
                vhmpMapParams.put("shopCode", dashboardRequestDTO.getObjectCode());
            }
            if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())) {
                vsbdSqlBuilder.append(" and ifnull(vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a') ");
                vhmpMapParams.put("vdsChannelCode", dashboardRequestDTO.getVdsChannelCode());
            } else {
                vsbdSqlBuilder.append(" and vds_channel_code is null ");
            }
            vsbdSqlBuilder.append("and prd_id between :prdStartMonth and :prdId ");
            vhmpMapParams.put("prdStartMonth", vlngPrdStartMonth);
        }
        //CHU KI THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSqlBuilder.append("select date_format(cast(prd_id as date), '%m/%Y'), ");
            vsbdSqlBuilder.append("(select name from service where id = p.service_id), ");
            vsbdSqlBuilder.append("th, vds_channel_code, shop_code, dvt, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1') ");
            vsbdSqlBuilder.append("from chart_service_measure_monthly p ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            vsbdSqlBuilder.append("and staff_code is null ");
            if (!DataUtil.isNullOrZero(plngServiceId)) {
                vsbdSqlBuilder.append("and service_id = :serviceId ");
                vhmpMapParams.put("serviceId", plngServiceId);
            }
            if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getObjectCode())) {
                vsbdSqlBuilder.append("and ifnull(shop_code, 'a') = ifnull(:shopCode, 'a') ");
                vhmpMapParams.put("shopCode", dashboardRequestDTO.getObjectCode());
            }
            if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())) {
                vsbdSqlBuilder.append(" and ifnull(vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a') ");
                vhmpMapParams.put("vdsChannelCode", dashboardRequestDTO.getVdsChannelCode());
            } else {
                vsbdSqlBuilder.append(" and vds_channel_code is null ");
            }
            vsbdSqlBuilder.append("and prd_id between :prdStartYear and :prdId ");
            vhmpMapParams.put("prdStartYear", vlngPrdStartYear);
        }
        //CHU KI QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSqlBuilder.append("select concat('Q', quarter(cast(prd_id as date)), '/', year(cast(prd_id as date))), ");
            vsbdSqlBuilder.append("(select name from service where id = p.service_id), ");
            vsbdSqlBuilder.append("th, vds_channel_code, shop_code, dvt, ");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1') ");
            vsbdSqlBuilder.append("from chart_service_measure_quaterly p ");
            vsbdSqlBuilder.append("where 1 = 1 ");
            vsbdSqlBuilder.append("and staff_code is null ");
            if (!DataUtil.isNullOrZero(plngServiceId)) {
                vsbdSqlBuilder.append("and service_id = :serviceId ");
                vhmpMapParams.put("serviceId", plngServiceId);
            }
            if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getObjectCode())) {
                vsbdSqlBuilder.append("and ifnull(shop_code, 'a') = ifnull(:shopCode, 'a') ");
                vhmpMapParams.put("shopCode", dashboardRequestDTO.getObjectCode());
            }
            if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())) {
                vsbdSqlBuilder.append(" and ifnull(vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a') ");
                vhmpMapParams.put("vdsChannelCode", dashboardRequestDTO.getVdsChannelCode());
            } else {
                vsbdSqlBuilder.append(" and vds_channel_code is null ");
            }
            vsbdSqlBuilder.append("and prd_id between :prdStartYear and :prdId ");
            vhmpMapParams.put("prdStartYear", vlngPrdStartYear);
        }

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.put("prdId", vlngPrdId);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlstData = query.getResultList();
        return vlstData;
    }

    /**
     * lay ra bieu do chi tieu theo nam o cuoi dashboard level (chart)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/11
     */
    @Override
    public List<Object[]> findDataServiceByYear(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, StaffDTO staffDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        Long vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select th, kh, ");
        vsbdSqlBuilder.append("date_format(cast(prd_id as date), 'Năm %Y'), ");
        vsbdSqlBuilder.append("vds_channel_code, service_id, shop_code, dvt, ");
        vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1') ");
        vsbdSqlBuilder.append("from chart_service_measure_yearly p ");
        vsbdSqlBuilder.append(buildSQL(dashboardRequestDTO, dashboardRequestDTO.getServiceId(), vhmpMapParams, staffDTO, configSingleChartDTO));
        vsbdSqlBuilder.append("and prd_id between cast(date_sub(cast(:prdId as date), interval 5 year) as signed) and :prdId ");
        vsbdSqlBuilder.append("order by prd_id ");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.put("prdId", vlngPrdId);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlstData = query.getResultList();
        return vlstData;
    }

    /**
     * lay duong xu the cho table tong hop chi tiet
     *
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/10
     */
    @Override
    public List<PointDTO> findDataTrendChartTable(String pstrTrend, DashboardRequestDTO dashboardRequestDTO) throws Exception {
        List<PointDTO> vlstData = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();
        List<String> vlstCategoriesNoData = getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.TREND, dashboardRequestDTO.getCycleId());

        //thay doi moi 01/06/2020
        if (pstrTrend != "") {
            String input = DataUtil.safeToString(pstrTrend);
            Map<Long, Double> vmTrend = new HashMap<>();
            Long vlngPrdId = null;
            Double vdblValue = null;
            String[] varrTrend = input.split("\\|");
            for (String t : varrTrend) {
                PointDTO data = new PointDTO();
                vdblValue = Double.valueOf(t.substring(0, t.lastIndexOf(";")));
                vlngPrdId = Long.valueOf(t.substring(t.lastIndexOf(";") + 1));
                data.setCategory(DataUtil.convertPrdToString(vlngPrdId));
                data.setValue(vdblValue);
                data.setViewValue(String.valueOf(vdblValue));
                vlstData.add(data);
                vlstCategories.add(DataUtil.convertPrdToString(vlngPrdId));
                vmTrend.put(vlngPrdId, vdblValue);
            }
        }

//        if (!DataUtil.isNullOrEmpty(plstObjects)) {
//
//            for (Object[] obj : plstObjects) {
//
//                vlstCategories.add(DataUtil.safeToString(obj[15]));
//                Double vdblPerformValue = (Double) obj[16];
//
//                if (vdblPerformValue != null) {
//                    Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
//                    PointDTO data = new PointDTO();
//                    data.setCategory(DataUtil.safeToString(obj[15]));
//                    if (vdblValue != null) {
//                        data.setValue(vdblValue);
//                        if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
//                            data.setViewValue(null);
//                        } else {
//                            data.setViewValue(format.format(vdblValue));
//                        }
//                        vlstData.add(data);
//                    }
//                } else {
//                    if (obj[15] != null) {
//                        PointDTO data = new PointDTO();
//                        data.setCategory(DataUtil.safeToString(obj[15]));
//                        vlstData.add(data);
//                    }
//                }
//
//            }
//        }

        //set point ko co du lieu trong db
        vlstCategoriesNoData.removeAll(vlstCategories);
        for (int i = 0; i < vlstCategoriesNoData.size(); i++) {
            PointDTO point = new PointDTO();
            point.setCategory(vlstCategoriesNoData.get(i));
            vlstData.add(point);
        }

        //Sap xep point theo thu tu ngay tang dan
        Collections.sort(vlstData, new Comparator<PointDTO>() {
            @Override
            public int compare(PointDTO p1, PointDTO p2) {
                if (p1 != null && p2 != null) {
                    java.util.Date dateOne = DataUtil.convertStringToDate(p1.getCategory());
                    java.util.Date dateTwo = DataUtil.convertStringToDate(p2.getCategory());
                    if (dateOne != null && dateTwo != null) {
                        return dateOne.compareTo(DataUtil.convertStringToDate(p2.getCategory()));
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }
            }
        });
        return vlstData;
    }

    @Override
    public List<PointDTO> findDataTrendChildShop(String pstrTrend, DashboardRequestDTO dashboardRequestDTO) throws Exception {
        List<PointDTO> vlstData = new ArrayList<>();
        List<String> vlstCategories = new ArrayList<>();
        List<String> vlstCategoriesNoData = getCategories(dashboardRequestDTO.getPrdId(), Constants.LINE_VALUE.TREND, dashboardRequestDTO.getCycleId());

        //thay doi moi 01/06/2020
        if (pstrTrend != "") {
            String input = DataUtil.safeToString(pstrTrend);
            Map<Long, Double> vmTrend = new HashMap<>();
            Long vlngPrdId = null;
            Double vdblValue = null;
            String[] varrTrend = input.split("\\|");
            for (String t : varrTrend) {
                PointDTO data = new PointDTO();
                vdblValue = Double.valueOf(t.substring(0, t.lastIndexOf(";")));
                vlngPrdId = Long.valueOf(t.substring(t.lastIndexOf(";") + 1));
                data.setCategory(DataUtil.convertPrdToString(vlngPrdId));
                data.setValue(vdblValue);
                data.setViewValue(String.valueOf(vdblValue));
                vlstData.add(data);
                vlstCategories.add(DataUtil.convertPrdToString(vlngPrdId));
                vmTrend.put(vlngPrdId, vdblValue);
            }

//        if (!DataUtil.isNullOrEmpty(plstObjects)) {
//
//            for (Object[] obj : plstObjects) {
//
//                vlstCategories.add(DataUtil.safeToString(obj[12]));
//                Double vdblPerformValue = (Double) obj[13];
//
//                if (vdblPerformValue != null) {
//                    Double vdblValue = Double.valueOf(df.format(vdblPerformValue));
//                    PointDTO data = new PointDTO();
//                    data.setCategory(DataUtil.safeToString(obj[12]));
//                    if (vdblValue != null) {
//                        data.setValue(vdblValue);
//                        if (DataUtil.isNullOrEmpty(format.format(vdblValue))) {
//                            data.setViewValue(null);
//                        } else {
//                            data.setViewValue(format.format(vdblValue));
//                        }
//                        vlstData.add(data);
//                    }
//                } else {
//                    if (obj[12] != null) {
//                        PointDTO data = new PointDTO();
//                        data.setCategory(DataUtil.safeToString(obj[12]));
//                        vlstData.add(data);
//                    }
//                }
//
//            }
//        }
        }

        //set point ko co du lieu trong db
        vlstCategoriesNoData.removeAll(vlstCategories);
        for (int i = 0; i < vlstCategoriesNoData.size(); i++) {
            PointDTO point = new PointDTO();
            point.setCategory(vlstCategoriesNoData.get(i));
            vlstData.add(point);
        }

        //Sap xep point theo thu tu ngay tang dan
        Collections.sort(vlstData, new Comparator<PointDTO>() {
            @Override
            public int compare(PointDTO p1, PointDTO p2) {
                if (p1 != null && p2 != null) {
                    java.util.Date dateOne = DataUtil.convertStringToDate(p1.getCategory());
                    java.util.Date dateTwo = DataUtil.convertStringToDate(p2.getCategory());
                    if (dateOne != null && dateTwo != null) {
                        return dateOne.compareTo(dateTwo);
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }
            }
        });
        return vlstData;
    }

    /**
     * lay ra du lieu bieu do duong theo chi tieu con (new 02/06/2020)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author VuBL
     */
    @Override
    public List<Object[]> findDataLChartServiceNew(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO, List<Object[]> plstServices) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        String vstrShopCode = dashboardRequestDTO.getObjectCode();
        String vstrChannelCode = dashboardRequestDTO.getVdsChannelCode();
        List<Object[]> vlstData = null;
        Long vlngParentServiceId = null;
        String vstrSelectServices = "";
        List<Long> vlstServiceIds = new ArrayList<>();
        List<String> vlstServiceNames = new ArrayList<>();
        List<Object[]> vlstServices = null;
        java.sql.Date vdtDate = new Date(dashboardRequestDTO.getPrdId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vdtDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Long vlngPrdStartMonth = Long.valueOf(formatToPrd.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
        Long vlngPrdStartYear = Long.valueOf(formatToPrd.format(cal.getTime()));
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        if (plstServices != null && !plstServices.isEmpty()) {
            vlstServices = plstServices;
        }

        //lay ra list cac service_id va name con
        if (vlstServices != null && !vlstServices.isEmpty()) {
            for (Object[] obj : vlstServices) {
                Long serviceId = DataUtil.safeToLong(obj[0]);
                vlstServiceIds.add(DataUtil.safeToLong(obj[0]));
                vlstServiceNames.add((String) obj[1]);
                vstrSelectServices = vstrSelectServices + ",MAX(IF(csmd.service_id = " + serviceId + ",th,NULL))";
            }
        }

        //lay ra serviceId
        if (dashboardRequestDTO.getServiceId() != null) {
            vlngParentServiceId = dashboardRequestDTO.getServiceId();
        }

        //CHU KI NGAY
        if (vlstServices != null) {
            if (vintCycleId == Constants.CYCLE_ID.DAY) {
                vsbdSqlBuilder.append("SELECT date_format(cast(prd_id as date), '%d/%m/%Y'),(select name from unit where CODE = csmd.dvt AND STATUS =1 LIMIT 1) org_unit,1 org_rate,")
                        .append("(SELECT NAME FROM shop_unit su, unit un ")
                        .append("WHERE su.unit_code = un.code ")
                        .append("AND su.status = 1 ")
                        .append("AND un.status = 1 ")
                        .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null)) ")
                        .append("AND su.shop_code = csmd.shop_code ")
                        .append("AND su.service_id = csmd.service_id ")
                        .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("LIMIT 1) unit_name, ")
                        .append("(SELECT rate FROM shop_unit su, unit un ")
                        .append("WHERE su.unit_code = un.code ")
                        .append("AND su.status = 1 ")
                        .append("AND un.status = 1 ")
                        .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null)) ")
                        .append("AND su.shop_code = csmd.shop_code ")
                        .append("AND su.service_id = csmd.service_id ")
                        .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("LIMIT 1) unit_rate ")
                        .append(vstrSelectServices)
                        .append("FROM chart_service_measure_daily csmd ");
//                        .append("WHERE prd_id >= :prdStartMonth AND prd_id <= :prd ")

                String stringPrdIdDay = DataUtil.getStringPrdIdForWhere(vlngPrdStartMonth, vlngPrdId, vintCycleId);
                if (stringPrdIdDay != null) {
                    vsbdSqlBuilder.append("where prd_id in (" + stringPrdIdDay + ") ");
                }
                vsbdSqlBuilder.append("AND csmd.shop_code = :shopCode ")
                        .append("AND (csmd.vds_channel_code = :vdsChannelCode OR :vdsChannelCode IS NULL) ")
                        .append("AND staff_code IS NULL ")
                        .append("AND csmd.service_id IN (SELECT id FROM service WHERE STATUS = 1 AND parent_id = :serviceId) ")
                        .append("GROUP BY prd_id ")
                        .append("order by prd_id");
            }
            //CHU KI THANG
            if (vintCycleId == Constants.CYCLE_ID.MONTH) {
                vsbdSqlBuilder.append("SELECT date_format(cast(prd_id as date), '%m/%Y'),(select name from unit where CODE = csmd.dvt AND STATUS =1 LIMIT 1) org_unit,1 org_rate,")
                        .append("(SELECT NAME FROM shop_unit su, unit un ")
                        .append("WHERE su.unit_code = un.code ")
                        .append("AND su.status = 1 ")
                        .append("AND un.status = 1 ")
                        .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null)) ")
                        .append("AND su.shop_code = csmd.shop_code ")
                        .append("AND su.service_id = csmd.service_id ")
                        .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("LIMIT 1) unit_name, ")
                        .append("(SELECT rate FROM shop_unit su, unit un ")
                        .append("WHERE su.unit_code = un.code ")
                        .append("AND su.status = 1 ")
                        .append("AND un.status = 1 ")
                        .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null)) ")
                        .append("AND su.shop_code = csmd.shop_code ")
                        .append("AND su.service_id = csmd.service_id ")
                        .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("LIMIT 1) unit_rate ")
                        .append(vstrSelectServices)
                        .append("FROM chart_service_measure_monthly csmd ")
                        .append("WHERE prd_id >= :prdStartMonth AND prd_id <= :prd ")
                        .append("AND csmd.shop_code = :shopCode ")
                        .append("AND (csmd.vds_channel_code = :vdsChannelCode OR :vdsChannelCode IS NULL) ")
                        .append("AND staff_code IS NULL ")
                        .append("AND csmd.service_id IN (SELECT id FROM service WHERE STATUS = 1 AND parent_id = :serviceId) ")
                        .append("GROUP BY prd_id ")
                        .append("order by prd_id");
            }
            //CHU KI QUY
            if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
                vsbdSqlBuilder.append("SELECT concat('Q', quarter(cast(prd_id as date)), '/', year(cast(prd_id as date))),(select name from unit where CODE = csmd.dvt AND STATUS =1 LIMIT 1) org_unit,1 org_rate,")
                        .append("(SELECT NAME FROM shop_unit su, unit un ")
                        .append("WHERE su.unit_code = un.code ")
                        .append("AND su.status = 1 ")
                        .append("AND un.status = 1 ")
                        .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null)) ")
                        .append("AND su.shop_code = csmd.shop_code ")
                        .append("AND su.service_id = csmd.service_id ")
                        .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("LIMIT 1) unit_name, ")
                        .append("(SELECT rate FROM shop_unit su, unit un ")
                        .append("WHERE su.unit_code = un.code ")
                        .append("AND su.status = 1 ")
                        .append("AND un.status = 1 ")
                        .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null)) ")
                        .append("AND su.shop_code = csmd.shop_code ")
                        .append("AND su.service_id = csmd.service_id ")
                        .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d') ")
                        .append("LIMIT 1) unit_rate ")
                        .append(vstrSelectServices)
                        .append("FROM chart_service_measure_quaterly csmd ")
                        .append("WHERE prd_id >= :prdStartMonth AND prd_id <= :prd ")
                        .append("AND csmd.shop_code = :shopCode ")
                        .append("AND (csmd.vds_channel_code = :vdsChannelCode OR :vdsChannelCode IS NULL) ")
                        .append("AND staff_code IS NULL ")
                        .append("AND csmd.service_id IN (SELECT id FROM service WHERE STATUS = 1 AND parent_id = :serviceId) ")
                        .append("GROUP BY prd_id ")
                        .append("order by prd_id");
            }

            Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
            if (vintCycleId == Constants.CYCLE_ID.MONTH || vintCycleId == Constants.CYCLE_ID.QUARTER) {
                query.setParameter("prdStartMonth", vlngPrdStartMonth);
                query.setParameter("prd", vlngPrdId);
            }
            query.setParameter("shopCode", vstrShopCode);
            query.setParameter("vdsChannelCode", vstrChannelCode);
            query.setParameter("serviceId", vlngParentServiceId);
            vlstData = query.getResultList();
        } else {
        }

        return vlstData;
    }

    /**
     * lay data cho level 3 cua table chart (card 2 level 2)
     *
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/07
     */
    @Override
    public List<Object[]> findDataUnitStaffByService(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngPrdId = null;
        String vstrShopCode = dashboardRequestDTO.getObjectCode();
        String vstrChannelCode = dashboardRequestDTO.getVdsChannelCode() == null ? null : dashboardRequestDTO.getVdsChannelCode();
        Long vlngServiceId = dashboardRequestDTO.getServiceId();
        if (dashboardRequestDTO.getParentShopCode() != null) {
            vstrShopCode = dashboardRequestDTO.getParentShopCode();
        }

        //chu ky ngay
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
            vsbdSql.append("SELECT null, m.short_name, round(lk, 2), round(lk_thang_n1, 2), ")
                    .append("(select name from unit where code = c.dvt and status = '1'), 1, ")
                    .append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ")
                    .append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ")
                    .append("c.shop_code, c.staff_code ")
                    .append("FROM chart_service_measure_daily c, manage_info_partner m ")
                    .append("WHERE c.shop_code = m.shop_code ")
                    .append("and prd_id = :prdId ")
                    .append("AND c.shop_code IN (SELECT shop_code FROM manage_info_partner WHERE parent_shop_code = :shopCode) ")
                    .append("AND (c.vds_channel_code = :vdsChannelCode OR :vdsChannelCode is NULL) ")
                    .append("AND service_id = :serviceId ")
                    .append("AND staff_code IS NULL ")
                    .append("UNION ALL ")
                    .append("SELECT m.short_name, v.staff_name, round(lk, 2), round(lk_thang_n1, 2), ")
                    .append("(select name from unit where code = c.dvt and status = '1'), 1, ")
                    .append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ")
                    .append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ")
                    .append("null, c.staff_code ")
                    .append("FROM chart_service_measure_daily c, vds_staff v, manage_info_partner m ")
                    .append("WHERE c.staff_code = v.staff_code ")
                    .append("AND c.shop_code = m.shop_code ")
                    .append("and prd_id = :prdId ")
                    .append("AND c.shop_code = :shopCode ")
                    .append("AND (c.vds_channel_code = :vdsChannelCode OR :vdsChannelCode is NULL) ")
                    .append("AND service_id = :serviceId ")
                    .append("AND c.staff_code IS NOT NULL ")
                    .append("order by short_name ");
        }

        //chu ky thang
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
            vsbdSql.append("SELECT null, m.short_name, round(lk, 2), round(lk_nam_n1, 2), ")
                    .append("(select name from unit where code = c.dvt and status = '1'), 1, ")
                    .append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ")
                    .append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ")
                    .append("c.shop_code, c.staff_code ")
                    .append("FROM chart_service_measure_monthly c, manage_info_partner m ")
                    .append("WHERE c.shop_code = m.shop_code ")
                    .append("and prd_id = :prdId ")
                    .append("AND c.shop_code IN (SELECT shop_code FROM manage_info_partner WHERE parent_shop_code = :shopCode) ")
                    .append("AND (c.vds_channel_code = :vdsChannelCode OR :vdsChannelCode is NULL) ")
                    .append("AND service_id = :serviceId ")
                    .append("AND staff_code IS NULL ")
                    .append("UNION ALL ")
                    .append("SELECT m.short_name, v.staff_name, round(lk, 2), round(lk_nam_n1, 2), ")
                    .append("(select name from unit where code = c.dvt and status = '1'), 1, ")
                    .append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ")
                    .append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ")
                    .append("null, c.staff_code ")
                    .append("FROM chart_service_measure_monthly c, vds_staff v, manage_info_partner m ")
                    .append("WHERE c.staff_code = v.staff_code ")
                    .append("AND c.shop_code = m.shop_code ")
                    .append("and prd_id = :prdId ")
                    .append("AND c.shop_code = :shopCode ")
                    .append("AND (c.vds_channel_code = :vdsChannelCode OR :vdsChannelCode is NULL) ")
                    .append("AND service_id = :serviceId ")
                    .append("AND c.staff_code IS NOT NULL ")
                    .append("order by short_name ");
        }

        //chu ky quy
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
            vsbdSql.append("SELECT null, m.short_name, round(lk, 2), round(lk_nam_n1, 2), ")
                    .append("(select name from unit where code = c.dvt and status = '1'), 1, ")
                    .append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ")
                    .append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ")
                    .append("c.shop_code, c.staff_code ")
                    .append("FROM chart_service_measure_quaterly c, manage_info_partner m ")
                    .append("WHERE c.shop_code = m.shop_code ")
                    .append("and prd_id = :prdId ")
                    .append("AND c.shop_code IN (SELECT shop_code FROM manage_info_partner WHERE parent_shop_code = :shopCode) ")
                    .append("AND (c.vds_channel_code = :vdsChannelCode OR :vdsChannelCode is NULL) ")
                    .append("AND service_id = :serviceId ")
                    .append("AND staff_code IS NULL ")
                    .append("UNION ALL ")
                    .append("SELECT m.short_name, v.staff_name, round(lk, 2), round(lk_nam_n1, 2), ")
                    .append("(select name from unit where code = c.dvt and status = '1'), 1, ")
                    .append("(SELECT NAME FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_name_new, ")
                    .append("(SELECT rate FROM shop_unit su, unit un ")
                    .append("WHERE su.unit_code = un.code ")
                    .append("AND su.status = 1 ")
                    .append("AND un.status = 1 ")
                    .append("AND (su.vds_channel_code = c.vds_channel_code OR (su.vds_channel_code IS NULL AND c.vds_channel_code IS null)) ")
                    .append("AND su.shop_code = c.shop_code ")
                    .append("AND su.service_id = c.service_id ")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(c.prd_id,'%Y%m%d') ")
                    .append("LIMIT 1) unit_rate, ")
                    .append("null, c.staff_code ")
                    .append("FROM chart_service_measure_quaterly c, vds_staff v, manage_info_partner m ")
                    .append("WHERE c.staff_code = v.staff_code ")
                    .append("AND c.shop_code = m.shop_code ")
                    .append("and prd_id = :prdId ")
                    .append("AND c.shop_code = :shopCode ")
                    .append("AND (c.vds_channel_code = :vdsChannelCode OR :vdsChannelCode is NULL) ")
                    .append("AND service_id = :serviceId ")
                    .append("AND c.staff_code IS NOT NULL ")
                    .append("order by short_name ");
        }

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("prdId", vlngPrdId);
        query.setParameter("shopCode", vstrShopCode);
        query.setParameter("vdsChannelCode", vstrChannelCode);
        query.setParameter("serviceId", vlngServiceId);
        List<Object[]> vlstDatas = query.getResultList();

        return vlstDatas;
    }

    /**
     * check clicked
     *
     * @author VuBL
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     */
    @Override
    public boolean checkClicked(DashboardRequestDTO dashboardRequestDTO, String shopCode) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngPrdId = null;
        String vstrShopCode = shopCode == null ? null : shopCode;
        String vstrChannelCode = dashboardRequestDTO.getVdsChannelCode() == null ? null : dashboardRequestDTO.getVdsChannelCode();
        Long vlngServiceId = dashboardRequestDTO.getServiceId();

        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
            vsbdSql.append("SELECT shop_code, staff_code FROM chart_service_measure_daily ")
                    .append("where prd_id = :prdId ")
                    .append("AND shop_code = :shopCode ")
                    .append("AND (vds_channel_code = :vdsChannelCode OR :vdsChannelCode is NULL) ")
                    .append("AND service_id = :serviceId ")
                    .append("AND staff_code IS NOT NULL ");
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
            vsbdSql.append("SELECT shop_code, staff_code FROM chart_service_measure_monthly ")
                    .append("where prd_id = :prdId ")
                    .append("AND shop_code = :shopCode ")
                    .append("AND (vds_channel_code = :vdsChannelCode OR :vdsChannelCode is NULL) ")
                    .append("AND service_id = :serviceId ")
                    .append("AND staff_code IS NOT NULL ");
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
            vsbdSql.append("SELECT shop_code, staff_code FROM chart_service_measure_quaterly ")
                    .append("where prd_id = :prdId ")
                    .append("AND shop_code = :shopCode ")
                    .append("AND (vds_channel_code = :vdsChannelCode OR :vdsChannelCode is NULL) ")
                    .append("AND service_id = :serviceId ")
                    .append("AND staff_code IS NOT NULL ");
        }

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("prdId", vlngPrdId);
        query.setParameter("shopCode", vstrShopCode);
        query.setParameter("vdsChannelCode", vstrChannelCode);
        query.setParameter("serviceId", vlngServiceId);
        List<Object[]> vlstDatas = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstDatas)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * lay ra so ngay cua thang theo prd_id truyen vao đe lay ra categories (truc hoanh cua bieu đo)
     *
     * @return int
     * @author VuBL
     * @version 1.0
     * @since 2019/09
     */
    public int countDayOfMonth(Long plngPrdId) {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        Long vlngPrdId = null;
        if (!DataUtil.isNullOrZero(plngPrdId)) {
            vlngPrdId = DataUtil.convertToPrdId(plngPrdId);
        }
        vsbdSqlBuilder.append("select day(last_day(cast(:prdId as date)))");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("prdId", vlngPrdId);
        Integer vintDayOfMonth = (Integer) query.getSingleResult();
        if (DataUtil.isNullOrZero(vintDayOfMonth))
            return 0;

        return vintDayOfMonth;
    }

    /**
     * lay ra doi tuong unit chua don vi tinh duoc khai bao trong bang shop_unit
     *
     * @param plngServiceId
     * @param pstrShopCode
     * @param pstrVdsChannelCode
     * @return
     * @author VuBL
     * @since 2019/12
     */
    public Unit getNewUnitType(Long plngServiceId, String pstrShopCode, String pstrVdsChannelCode) {
        List<Unit> units = null;
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select id, code, name, rate, status from unit ");
        vsbdSqlBuilder.append("where code in (select unit_code from shop_unit ");
        vsbdSqlBuilder.append("where service_id = :serviceId ");
        vsbdSqlBuilder.append("and shop_code = :shopCode ");
        vsbdSqlBuilder.append("and ifnull(vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a') ");
        vsbdSqlBuilder.append("and current_date() between from_date and to_date ");
        vsbdSqlBuilder.append("and status = 1) ");
        vsbdSqlBuilder.append("and status = 1 ");

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("serviceId", plngServiceId);
        query.setParameter("vdsChannelCode", pstrVdsChannelCode);
        query.setParameter("shopCode", pstrShopCode);
        List<Object[]> vlstData = query.getResultList();

        if (!DataUtil.isNullOrEmpty(vlstData)) {
            for (Object[] obj : vlstData) {
                units = new ArrayList<>();
                Unit data = new Unit();
                data.setId(DataUtil.safeToLong(obj[0]));
                data.setCode(DataUtil.safeToString(obj[1]));
                data.setName(DataUtil.safeToString(obj[2]));
                data.setRate(DataUtil.safeToDouble(obj[3]));
                data.setStatus(DataUtil.safeToString(obj[4]));
                units.add(data);
            }
        }
        if (units != null)
            return units.get(0);
        else return null;
    }

    /**
     * lay ra rate theo dvt trong bang tong hop de tinh toan ra so lieu chuan
     *
     * @param pstrCode
     * @return
     * @author VuBL
     * @since 2019/12
     */
    public Double getOldRate(String pstrCode) {
        List<Unit> units = null;
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select id, code, name, rate, status from unit ");
        vsbdSqlBuilder.append("where ifnull(code, 'a') = ifnull(:code, 'a') ");
        vsbdSqlBuilder.append("and status = 1 ");

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("code", pstrCode);
        List<Object[]> vlstData = query.getResultList();

        if (!DataUtil.isNullOrEmpty(vlstData)) {
            for (Object[] obj : vlstData) {
                units = new ArrayList<>();
                Unit data = new Unit();
                data.setId(DataUtil.safeToLong(obj[0]));
                data.setCode(DataUtil.safeToString(obj[1]));
                data.setName(DataUtil.safeToString(obj[2]));
                data.setRate(DataUtil.safeToDouble(obj[3]));
                data.setStatus(DataUtil.safeToString(obj[4]));
                units.add(data);
            }
        }
        if (units == null) {
            return null;
        } else {
            return units.get(0).getRate();
        }
    }

    /**
     * lay dvt mac dinh tu bang tong hop
     *
     * @param plngServiceId
     * @param pintCycleId
     * @return
     * @author VuBL
     * @since 2019/10
     */
    public String getDefaultUnitType(Long plngServiceId, int pintCycleId) {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        if (pintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSqlBuilder.append("select distinct dvt from chart_service_measure_daily where service_id = :serviceId");
        }
        if (pintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSqlBuilder.append("select distinct dvt from chart_service_measure_monthly where service_id = :serviceId");
        }
        if (pintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSqlBuilder.append("select distinct dvt from chart_service_measure_quaterly where service_id = :serviceId");
        }
        if (pintCycleId == Constants.CYCLE_ID.YEAR) {
            vsbdSqlBuilder.append("select distinct dvt from chart_service_measure_yearly where service_id = :serviceId");
        }
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("serviceId", plngServiceId);
        List<String> vlstData = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstData)) {
            return null;
        }
        return vlstData.get(0);
    }

    /**
     * lay ra list categories cua bieu đo
     *
     * @param plngPrdId
     * @return List<String>
     * @author VuBL
     * @since 2019/10
     */
    public List<String> getCategories(Long plngPrdId, String pstrTypePrdId, int pintCycleId) {
        int vintCountDayOfMonth = countDayOfMonth(plngPrdId);
        String vstrDay;
        List<String> vlstCategories = new ArrayList<>();
        int vintDay = 0;
        int vintMonth = 0;
        int vintQuarter = 0;
        int vintYear = 0;
        String vstrMonth = null;
        java.util.Date vdtDateMin;
        Calendar calTrend = null;
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd/MM/yyyy");

        if (pstrTypePrdId.equals(Constants.LINE_VALUE.MONTH)) {
            Date vdtDate = new Date(plngPrdId);
            vintDay = vdtDate.toLocalDate().getDayOfMonth();
            vintMonth = vdtDate.toLocalDate().getMonthValue();
            vintQuarter = (vdtDate.getMonth() / 3) + 1;
            vintYear = vdtDate.toLocalDate().getYear();
            vstrMonth = String.valueOf(vintMonth);
            if (vstrMonth.length() == 1) {
                vstrMonth = 0 + vstrMonth;
            } else {
                vstrMonth = vstrMonth;
            }
        }
        if (pstrTypePrdId.equals(Constants.LINE_VALUE.LAST_MONTH)) {
            Date vdtDate = new Date(plngPrdId);
            Calendar cal = Calendar.getInstance();
            cal.setTime(vdtDate);
            cal.add(Calendar.MONTH, -1);
            java.util.Date vdtDateUtil = cal.getTime();
            vdtDate = new Date(vdtDateUtil.getTime());
            vintDay = vdtDate.toLocalDate().getDayOfMonth();
            vintMonth = vdtDate.toLocalDate().getMonthValue();
            vintQuarter = (vdtDate.getMonth() / 3) + 1;
            vintYear = vdtDate.toLocalDate().getYear();
            vstrMonth = String.valueOf(vintMonth);
            if (vstrMonth.length() == 1) {
                vstrMonth = 0 + vstrMonth;
            } else {
                vstrMonth = vstrMonth;
            }
        }
        if (pstrTypePrdId.equals(Constants.LINE_VALUE.LAST_TWO_MONTH)) {
            Date vdtDate = new Date(plngPrdId);
            Calendar cal = Calendar.getInstance();
            cal.setTime(vdtDate);
            cal.add(Calendar.MONTH, -2);
            java.util.Date vdtDateUtil = cal.getTime();
            vdtDate = new Date(vdtDateUtil.getTime());
            vintDay = vdtDate.toLocalDate().getDayOfMonth();
            vintMonth = vdtDate.toLocalDate().getMonthValue();
            vintQuarter = (vdtDate.getMonth() / 3) + 1;
            vintYear = vdtDate.toLocalDate().getYear();
            vstrMonth = String.valueOf(vintMonth);
            if (vstrMonth.length() == 1) {
                vstrMonth = 0 + vstrMonth;
            } else {
                vstrMonth = vstrMonth;
            }
        }
        if (pstrTypePrdId.equals(Constants.LINE_VALUE.LAST_YEAR)) {
            Date vdtDate = new Date(plngPrdId);
            Calendar cal = Calendar.getInstance();
            cal.setTime(vdtDate);
            cal.add(Calendar.YEAR, -1);
            java.util.Date vdtDateUtil = cal.getTime();
            vdtDate = new Date(vdtDateUtil.getTime());
            vintDay = vdtDate.toLocalDate().getDayOfMonth();
            vintMonth = vdtDate.toLocalDate().getMonthValue();
            vintQuarter = (vdtDate.getMonth() / 3) + 1;
            vintYear = vdtDate.toLocalDate().getYear();
            vstrMonth = String.valueOf(vintMonth);
            if (vstrMonth.length() == 1) {
                vstrMonth = 0 + vstrMonth;
            } else {
                vstrMonth = vstrMonth;
            }
        }
        if (pstrTypePrdId.equals(Constants.LINE_VALUE.LEVEL_TWO)) {
            Date vdtDate = new Date(plngPrdId);
            vintDay = vdtDate.toLocalDate().getDayOfMonth();
            vintMonth = vdtDate.toLocalDate().getMonthValue();
            vintQuarter = (vdtDate.getMonth() / 3) + 1;
            vintYear = vdtDate.toLocalDate().getYear();
            vstrMonth = String.valueOf(vintMonth);
            if (vstrMonth.length() == 1) {
                vstrMonth = 0 + vstrMonth;
            } else {
                vstrMonth = vstrMonth;
            }
        }
        if (pstrTypePrdId.equals(Constants.LINE_VALUE.TREND)) {
            java.util.Date vdtDateMax = new Date(plngPrdId);
            calTrend = Calendar.getInstance();
            calTrend.setTime(vdtDateMax);
            if (pintCycleId == Constants.CYCLE_ID.DAY) {
                calTrend.add(Calendar.DAY_OF_MONTH, -29);
            }
            if (pintCycleId == Constants.CYCLE_ID.MONTH) {
                calTrend.add(Calendar.MONTH, -12);
                calTrend.set(Calendar.DAY_OF_MONTH, calTrend.getActualMinimum(Calendar.DAY_OF_MONTH));
            }
            if (pintCycleId == Constants.CYCLE_ID.QUARTER) {
                calTrend.add(Calendar.MONTH, -12);
                calTrend.set(Calendar.MONTH, calTrend.get(Calendar.MONTH) / 3 * 3);
                calTrend.set(Calendar.DAY_OF_MONTH, 1);
            }
        }

        //CHU KI NGAY
        if (pintCycleId == Constants.CYCLE_ID.DAY) {
            if (pstrTypePrdId.equals(Constants.LINE_VALUE.LEVEL_TWO)) {
                for (int i = 1; i <= vintDay; i++) {
                    vstrDay = String.valueOf(i);
                    if (vstrDay.length() == 1) {
                        vlstCategories.add(0 + vstrDay + "/" + vstrMonth + "/" + vintYear);
                    } else {
                        vlstCategories.add(vstrDay + "/" + vstrMonth + "/" + vintYear);
                    }
                }
            } else if (pstrTypePrdId.equals(Constants.LINE_VALUE.TREND)) {
                for (int i = 0; i < 30; i++) {
                    vdtDateMin = calTrend.getTime();
                    vlstCategories.add(sdfDay.format(vdtDateMin));
                    calTrend.add(Calendar.DAY_OF_MONTH, 1);
                }
            } else {
                for (int i = 1; i <= vintCountDayOfMonth; i++) {
                    vstrDay = String.valueOf(i);
                    if (vstrDay.length() == 1) {
                        vlstCategories.add(0 + vstrDay + "/" + vstrMonth + "/" + vintYear);
                    } else {
                        vlstCategories.add(vstrDay + "/" + vstrMonth + "/" + vintYear);
                    }
                }
            }
        }
        //CHU KI THANG
        if (pintCycleId == Constants.CYCLE_ID.MONTH) {
            String vstrDay2;
            if (pstrTypePrdId.equals(Constants.LINE_VALUE.LEVEL_TWO)) {
                for (int i = 1; i <= vintMonth; i++) {
                    vstrDay2 = String.valueOf(i);
                    if (vstrDay2.length() == 1) {
                        vlstCategories.add("0" + i + "/" + vintYear);
                    } else {
                        vlstCategories.add(i + "/" + vintYear);
                    }
                }
            } else if (pstrTypePrdId.equals(Constants.LINE_VALUE.TREND)) {
                for (int i = 0; i <= 12; i++) {
                    vdtDateMin = calTrend.getTime();
                    vlstCategories.add(sdfDay.format(vdtDateMin));
                    calTrend.add(Calendar.MONTH, 1);
                    calTrend.set(Calendar.DAY_OF_MONTH, calTrend.getActualMinimum(Calendar.DAY_OF_MONTH));
                }
            } else {
                for (int i = 1; i <= 12; i++) {
                    vstrDay2 = String.valueOf(i);
                    if (vstrDay2.length() == 1) {
                        vlstCategories.add("0" + i + "/" + vintYear);
                    } else {
                        vlstCategories.add(i + "/" + vintYear);
                    }
                }
            }
        }
        //CHU KI QUY
        if (pintCycleId == Constants.CYCLE_ID.QUARTER) {
            if (pstrTypePrdId.equals(Constants.LINE_VALUE.LEVEL_TWO)) {
                for (int i = 1; i <= vintQuarter; i++) {
                    vlstCategories.add("Q" + i + "/" + vintYear);
                }
            } else if (pstrTypePrdId.equals(Constants.LINE_VALUE.TREND)) {
                for (int i = 0; i <= 12; i += 3) {
                    vdtDateMin = calTrend.getTime();
                    vlstCategories.add(sdfDay.format(vdtDateMin));
                    calTrend.add(Calendar.MONTH, 3);
                    calTrend.set(Calendar.MONTH, calTrend.get(Calendar.MONTH) / 3 * 3);
                    calTrend.set(Calendar.DAY_OF_MONTH, 1);
                }
            } else {
                vlstCategories.add("Q1/" + vintYear);
                vlstCategories.add("Q2/" + vintYear);
                vlstCategories.add("Q3/" + vintYear);
                vlstCategories.add("Q4/" + vintYear);
            }
        }

        return vlstCategories;
    }

    /**
     * lay ra cac service_id con theo service_id dung cho bieu do duong theo goi thue bao o dashboard level 2 (chart)
     *
     * @param plngServiceId
     * @return
     * @author VuBL
     * @since 2019/11
     */
    public List<Long> getChildServiceId(Long plngServiceId) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<Long> vlstServiceIds = new ArrayList<>();
        sqlBuilder.append("select id from service where parent_id = :serviceId");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("serviceId", plngServiceId);

        List<Object> vlstObject = query.getResultList();
        for (Object obj : vlstObject) {
            vlstServiceIds.add(DataUtil.safeToLong(obj));
        }
        return vlstServiceIds;
    }

    /**
     * đieu kien tim kiem cac đuong cua bieu do neu co
     *
     * @param phmpParams
     * @param plngServiceId
     * @return stringBuilder
     * @author VuBL
     * @since 2019/09
     */
    public StringBuilder buildSQL(DashboardRequestDTO dashboardRequestDTO, Long plngServiceId, HashMap phmpParams, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getQueryParam());
        JsonArray varrLines = (JsonArray) jsonObject.get("params");
        JsonObject queryParam;
        String vstrServiceType = null;
        String vstrShopType = null;
        if (!DataUtil.isNullObject(varrLines.get(0))) {
            queryParam = (JsonObject) varrLines.get(0);
            vstrServiceType = queryParam.get("service").getAsString();
            vstrShopType = queryParam.get("shop").getAsString();
        }

        vsbdStringBuilder.append(" where 1=1 ");

        //service_id
        if (!DataUtil.isNullOrZero(plngServiceId)) {
            if (vstrServiceType != null) {
                if (vstrServiceType.equals(Constants.SERVICE_PARAM)) {
                    vsbdStringBuilder.append(" and service_id = :serviceId ");
                    phmpParams.put("serviceId", plngServiceId);
                } else if (vstrServiceType.equals(Constants.CHILD_SERVICE_PARAM)) {
                    vsbdStringBuilder.append(" and service_id in (select id from service where parent_id = :serviceId) ");
                    phmpParams.put("serviceId", plngServiceId);
                }
            }
        }

        //vds_channel_code
//        if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())) {
        vsbdStringBuilder.append(" and (p.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null) ");
//            vsbdStringBuilder.append(" and ifnull(p.vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a') ");
        phmpParams.put("vdsChannelCode", dashboardRequestDTO.getVdsChannelCode());
//        } else {
        if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())) {
            if (vstrShopType != null) {
                if (vstrShopType.equals(Constants.SHOP_PARAM)) {
                    vsbdStringBuilder.append(" and p.vds_channel_code is null ");
                } else if (vstrShopType.equals(Constants.CHILD_SHOP_PARAM)) {
                    vsbdStringBuilder.append(" and ifnull(p.vds_channel_code, 'a') = ifnull(m.vds_channel_code, 'a') ");
                }
            }
        }
//        }

        //shop_code
        if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getObjectCode())) {
            if (vstrShopType != null) {
                if (vstrShopType.equals(Constants.SHOP_PARAM)) {
                    vsbdStringBuilder.append(" and shop_code = :shopCode ");
                } else if (vstrShopType.equals(Constants.CHILD_SHOP_PARAM)) {
                    vsbdStringBuilder.append(" and parent_shop_code = :shopCode ");
                }
            }
            if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode())) {
                phmpParams.put("shopCode", dashboardRequestDTO.getObjectCode());
            } else {
                phmpParams.put("shopCode", dashboardRequestDTO.getParentShopCode());
            }
        }

        return vsbdStringBuilder;
    }

}
