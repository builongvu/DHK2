package vn.vissoft.dashboard.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DashboardRequest;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigSingleChart;
import vn.vissoft.dashboard.repo.TableRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class TableRepoImpl implements TableRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private DashboardRequest dashboardRequest;

    /**
     * lay du lieu tong hop theo ngay tu csdl
     *
     * @param dashboardRequestDTO
     * @return du lieu tong hop theo ngay
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    public List<Object[]> sumarizeByDay(DashboardRequestDTO dashboardRequestDTO, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChart) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        List<Long> vlstServiceIds = getServiceIdsInConfig(configSingleChart);

        vsbdSqlBuilder.append(" select * from (");
        vsbdSqlBuilder.append("select (select name from service where id=csmd.service_id) as ten_chi_tieu, sl_don_vi_ht, sl_don_vi_con, lk, kh_lk,");
        vsbdSqlBuilder.append(" ty_le_ht_lk ht_lk, kh_thang, ty_le_ht ht_thang, lk_thang_n1,csmd.service_id as service_id, dvt,");
        vsbdSqlBuilder.append(" csmd.vds_channel_code as vds_channel_code,shop_code as shop_code,staff_code as staff_code , prd_id as prd_id,");
        vsbdSqlBuilder.append(" 'TONG_HOP' row_code, ");
        vsbdSqlBuilder.append("(select name from unit where code = csmd.dvt and status = '1'), csmd.trend,1 AS org_rate, ");
        vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = csmd.shop_code\n")
                .append("AND su.service_id = csmd.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("LIMIT 1) unit_name,");
        vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n")
                .append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = csmd.shop_code\n")
                .append("AND su.service_id = csmd.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate,se.service_order, th ");
        vsbdSqlBuilder.append(" from chart_service_measure_daily csmd, service se   ");
        vsbdSqlBuilder.append(dashboardRequest.buildSummarySQL(dashboardRequestDTO, vhmpMapParams, configSingleChart.getQueryParam()));
//        vsbdSqlBuilder.append(" union all");
//        vsbdSqlBuilder.append(" select null ten_chi_tieu, null sl_don_vi_ht, null sl_don_vi_con, null lk, null kh_lk,");
//        vsbdSqlBuilder.append(" null ht_lk, null kh_thang,  null ht_thang, null lk_thang_n1, service_id, null dvt,");
//        vsbdSqlBuilder.append(" vds_channel_code,  shop_code, staff_code,null prd_id, ");
//        vsbdSqlBuilder.append(" date_format(cast(prd_id as date), '%d/%m/%Y'), case when service_id in (select code from ap_param where type = 'TREND_LINE' and  status ='1') then lk else th end ,prd_id trend_prd_id,'XU_THE' row_code,");
//        vsbdSqlBuilder.append("(select name from unit where code = r.dvt and status = '1') ");
//        vsbdSqlBuilder.append(" from chart_service_measure_daily r");
//        vsbdSqlBuilder.append(" where 1 = 1 ");
//        vsbdSqlBuilder.append(" and prd_id between cast(date_add(cast(:prdId as date), interval -29 day) as signed) and :prdId");
//        vsbdSqlBuilder.append(" and shop_code = :shopCode");
//        vsbdSqlBuilder.append(" and ifnull(vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a')");
//        vsbdSqlBuilder.append(" and staff_code is null");
        vsbdSqlBuilder.append(") a");
        if (!DataUtil.isNullOrEmpty(vlstServiceIds)) {
            vsbdSqlBuilder.append(" where a.service_id in (:serviceIds)");
            vhmpMapParams.put("serviceIds", vlstServiceIds);
        } else {
            vsbdSqlBuilder.append(" where a.service_id in (0)");
        }
        vsbdSqlBuilder.append(" order by a.service_order ");


        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();
    }

    /**
     * lay du lieu chi tiet tong hop theo ngay tu csdl
     *
     * @param dashboardRequestDTO
     * @return du lieu tong hop theo ngay
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    public List<Object[]> sumarizeDetailByDay(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChart) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        String vstrNationalStaff = "0";
        Long vlngPrdId = null;
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }
        if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
            vsbdSqlBuilder.append("select * from (");
            vsbdSqlBuilder.append("select m.shop_name as ten_don_vi,'a','b',kh_lk,lk,ty_le_ht_lk,kh_thang,ty_le_ht");
            vsbdSqlBuilder.append(",lk_thang_n1,service_id,dvt,csmd.vds_channel_code  as vds_channel_code,csmd.shop_code as shop_code ");
        vsbdSqlBuilder.append(" , staff_code as staff_code,prd_id as  prd_id, 'TONG_HOP' row_code, ");
        vsbdSqlBuilder.append("(select name from unit where code = csmd.dvt and status = '1'), csmd.trend ,1 AS org_rate, ");
        vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = csmd.shop_code\n")
                .append("AND su.service_id = csmd.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("LIMIT 1) unit_name,");
        vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n")
                .append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = csmd.shop_code\n")
                .append("AND su.service_id = csmd.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate, th, 'v' ");
        vsbdSqlBuilder.append(" from chart_service_measure_daily csmd , manage_info_partner m ");
        vsbdSqlBuilder.append(" where 1=1 and m.status ='1'  and m.shop_code = csmd.shop_code and (m.vds_channel_code = csmd.vds_channel_code or (m.vds_channel_code is null or csmd.vds_channel_code is null))");
        vsbdSqlBuilder.append(dashboardRequest.buildDetailSummarySQL(dashboardRequestDTO, vhmpMapParams, configSingleChart.getQueryParam()));
//        vsbdSqlBuilder.append(" union all ");
//        vsbdSqlBuilder.append("select null ten_don_vi,'c','d', null kh_lk, null lk,null ht_lk, null kh_thang, null ht_thang,null lk_thang_n1, service_id,dvt,csmd.vds_channel_code, csmd.shop_code, staff_code, prd_id,");
//        vsbdSqlBuilder.append(" date_format(cast(prd_id as date), '%d/%m/%Y'), th, prd_id trend_prd_id, 'XU_THE' row_code,");
//        vsbdSqlBuilder.append("(select name from unit where code = csmd.dvt and status = '1') ");
//        vsbdSqlBuilder.append(" from chart_service_measure_daily csmd, manage_info_partner m");
//        vsbdSqlBuilder.append(" where 1 = 1");
//        vsbdSqlBuilder.append(" and m.shop_code = csmd.shop_code");
//        vsbdSqlBuilder.append(" and ifnull(m.vds_channel_code,'a') = ifnull(csmd.vds_channel_code,'a')");
//        vsbdSqlBuilder.append(" and ifnull(m.parent_shop_code, 'a') = ifnull(:parentShopCode, 'a')");
//        vsbdSqlBuilder.append(" and (m.vds_channel_code =:vdsChannelCode or :vdsChannelCode is null)");
//        vsbdSqlBuilder.append(" and (prd_id between cast(date_add(cast(:prdId as date), interval - 29 day) as signed) and :prdId)");
//        vsbdSqlBuilder.append(" and staff_code is null");
//        vsbdSqlBuilder.append(" and csmd.service_id =:serviceId ");
        vsbdSqlBuilder.append(" union all");
        vsbdSqlBuilder.append(" select s.staff_name as ten_don_vi,'e','f',");
        vsbdSqlBuilder.append(" p.kh_lk,p.lk,ty_le_ht_lk,p.kh_thang,ty_le_ht,p.lk_thang_n1,p.service_id,p.dvt,");
        vsbdSqlBuilder.append(" p.vds_channel_code ,p.shop_code ,  p.staff_code ,p.prd_id , 'TONG_HOP' row_code,");
        vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), p.trend ,1 AS org_rate, ");
        vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = p.shop_code\n")
                .append("AND su.service_id = p.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n")
                .append("LIMIT 1) unit_name,");
        vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n")
                .append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = p.shop_code\n")
                .append("AND su.service_id = p.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate, th, 'v' ");
        vsbdSqlBuilder.append(" from chart_service_measure_daily p");
        vsbdSqlBuilder.append(" join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code and (s.vds_channel_code = p.vds_channel_code or (s.vds_channel_code is null or p.vds_channel_code is null)) and s.staff_type='1' and s.status='1'");
        vsbdSqlBuilder.append(" where 1=1");
        vsbdSqlBuilder.append(" and prd_id = :prdId and p.shop_code =:parentShopCode and (p.vds_channel_code =:vdsChannelCode or :vdsChannelCode is null)");
        vsbdSqlBuilder.append(" and p.staff_code is not null and p.service_id =:serviceId");
//        vsbdSqlBuilder.append(" union all");
//        vsbdSqlBuilder.append(" select null ten_don_vi,'e','f',null kh_lk, null lk,null ht_lk, null kh_thang, null ht_thang, null lk_thang_n1,p.service_id, p.dvt,p.vds_channel_code, p.shop_code,");
//        vsbdSqlBuilder.append("p.staff_code, p.prd_id, date_format(cast(prd_id as date), '%d/%m/%Y'), th, prd_id trend_prd_id, 'XU_THE' row_code,");
//        vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1') ");
//        vsbdSqlBuilder.append(" from chart_service_measure_daily p");
//        vsbdSqlBuilder.append(" where 1 = 1");
//        vsbdSqlBuilder.append(" and (prd_id between cast(date_add(cast(:prdId as date), interval - 29 day) as signed) and :prdId)");
//        vsbdSqlBuilder.append(" and p.shop_code =:parentShopCode and (p.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null) and p.staff_code is not null and service_id=:serviceId ");
        vsbdSqlBuilder.append(") a");
        vsbdSqlBuilder.append(" order by  shop_code, staff_code ");}
        else{
            vsbdSqlBuilder.append("select s.staff_name as ten_don_vi,'e','f',");
            vsbdSqlBuilder.append(" p.kh_lk,p.lk,ty_le_ht_lk,p.kh_thang,ty_le_ht,p.lk_thang_n1,p.service_id,p.dvt,");
            vsbdSqlBuilder.append(" p.vds_channel_code ,p.shop_code ,  p.staff_code ,p.prd_id , 'TONG_HOP' row_code,");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), p.trend ,1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                    .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                    .append("AND un.status = 1 \n")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                    .append("AND su.shop_code = p.shop_code\n")
                    .append("AND su.service_id = p.service_id\n")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("LIMIT 1) unit_name,");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                    .append("WHERE su.unit_code = un.code\n")
                    .append("AND su.status = 1\n")
                    .append("AND un.status = 1 \n")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                    .append("AND su.shop_code = p.shop_code\n")
                    .append("AND su.service_id = p.service_id\n")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate, th, (select short_name from manage_info_partner where shop_code = p.shop_code) ");
            vsbdSqlBuilder.append(" from chart_service_measure_daily p");
            vsbdSqlBuilder.append(" join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code  and s.staff_type='1' and s.status='1'");
            vsbdSqlBuilder.append(" where 1=1");
            vsbdSqlBuilder.append(" and prd_id = :prdId ");
            vsbdSqlBuilder.append(" and p.service_id =:serviceId");
            vsbdSqlBuilder.append(" and p.vds_channel_code = :vdsChannelCode");
            vsbdSqlBuilder.append(" and s.vds_channel_code = :vdsChannelCode");
            vsbdSqlBuilder.append(" order by shop_code, staff_code ");
            if (dashboardRequestDTO.getPrdId() != null)
                vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
            vhmpMapParams.put("prdId", vlngPrdId);
            if (dashboardRequestDTO.getServiceId() != null)
                vhmpMapParams.put("serviceId", dashboardRequestDTO.getServiceId());
            if(!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode()))
                vhmpMapParams.put("vdsChannelCode",dashboardRequestDTO.getVdsChannelCode());
        }

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        return query.getResultList();
    }

    /**
     * lay du lieu chi tiet tong hop theo thang tu csdl
     *
     * @param dashboardRequestDTO
     * @return du lieu tong hop theo ngay
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    public List<Object[]> sumarizeDetailByMonth(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChart) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        String vstrNationalStaff = "0";
        Long vlngPrdId = null;
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
            vsbdSqlBuilder.append("select * from (");
            vsbdSqlBuilder.append("select m.shop_name as ten_don_vi,'a','b',kh_lk,lk,ty_le_ht_lk,kh_nam,ty_le_ht");
            vsbdSqlBuilder.append(",lk_nam_n1,service_id,dvt,csmd.vds_channel_code  as vds_channel_code,csmd.shop_code as shop_code ");
        vsbdSqlBuilder.append(" , staff_code as staff_code,prd_id as  prd_id, 'TONG_HOP' row_code, ");
        vsbdSqlBuilder.append("(select name from unit where code = csmd.dvt and status = '1'), csmd.trend ,1 AS org_rate, ");
        vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = csmd.shop_code\n")
                .append("AND su.service_id = csmd.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("LIMIT 1) unit_name,");
        vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n")
                .append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = csmd.shop_code\n")
                .append("AND su.service_id = csmd.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate, th, 'v' ");
            vsbdSqlBuilder.append(" from chart_service_measure_monthly csmd , manage_info_partner m ");
        vsbdSqlBuilder.append(" where 1=1 and m.status='1' and m.shop_code = csmd.shop_code and (m.vds_channel_code = csmd.vds_channel_code or (m.vds_channel_code is null or csmd.vds_channel_code is null))");
        vsbdSqlBuilder.append(dashboardRequest.buildDetailSummarySQL(dashboardRequestDTO, vhmpMapParams, configSingleChart.getQueryParam()));
//        vsbdSqlBuilder.append(" union all ");
//        vsbdSqlBuilder.append("select null ten_don_vi,'c','d', null kh_lk, null lk,null ht_lk, null kh_nam, null ht_nam,null lk_nam_n1, service_id,dvt,csmd.vds_channel_code, csmd.shop_code, staff_code, prd_id,");
//        vsbdSqlBuilder.append(" date_format(cast(prd_id as date), '%d/%m/%Y'), th, prd_id trend_prd_id, 'XU_THE' row_code,");
//        vsbdSqlBuilder.append("(select name from unit where code = csmd.dvt and status = '1') ");
//        vsbdSqlBuilder.append(" from chart_service_measure_monthly csmd, manage_info_partner m");
//        vsbdSqlBuilder.append(" where 1 = 1");
//        vsbdSqlBuilder.append(" and m.shop_code = csmd.shop_code");
//        vsbdSqlBuilder.append(" and ifnull(m.vds_channel_code,'a') = ifnull(csmd.vds_channel_code,'a')");
//        vsbdSqlBuilder.append(" and ifnull(m.parent_shop_code, 'a') = ifnull(:parentShopCode, 'a')");
//        vsbdSqlBuilder.append(" and (m.vds_channel_code =:vdsChannelCode or :vdsChannelCode is null)");
//        vsbdSqlBuilder.append(" and (prd_id between cast(date_add(cast(:prdId as date), interval -12 month) as signed) and :prdId)");
//        vsbdSqlBuilder.append(" and staff_code is null");
//        vsbdSqlBuilder.append(" and csmd.service_id =:serviceId ");
        vsbdSqlBuilder.append(" union all");
        vsbdSqlBuilder.append(" select s.staff_name as ten_don_vi,'e','f',");
        vsbdSqlBuilder.append(" p.kh_lk,p.lk,ty_le_ht_lk,p.kh_nam,ty_le_ht,p.lk_nam_n1,p.service_id,p.dvt,");
        vsbdSqlBuilder.append(" p.vds_channel_code ,p.shop_code ,  p.staff_code ,p.prd_id , 'TONG_HOP' row_code,");
        vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), p.trend ,1 AS org_rate, ");
        vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = p.shop_code\n")
                .append("AND su.service_id = p.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n")
                .append("LIMIT 1) unit_name,");
        vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n")
                .append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = p.shop_code\n")
                .append("AND su.service_id = p.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate, th, 'v' ");
        vsbdSqlBuilder.append(" from chart_service_measure_monthly p");
        vsbdSqlBuilder.append(" join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code and (s.vds_channel_code = p.vds_channel_code or (s.vds_channel_code is null or p.vds_channel_code is null)) and s.staff_type='1' and s.status='1'");
        vsbdSqlBuilder.append(" where 1=1");
        vsbdSqlBuilder.append(" and prd_id = :prdId and p.shop_code =:parentShopCode and (p.vds_channel_code =:vdsChannelCode or :vdsChannelCode is null)");
        vsbdSqlBuilder.append(" and p.staff_code is not null and p.service_id =:serviceId");
//        vsbdSqlBuilder.append(" union all");
//        vsbdSqlBuilder.append(" select null ten_don_vi,'e','f',null kh_lk, null lk,null ht_lk, null kh_nam, null ht_nam, null lk_nam_n1,p.service_id, p.dvt,p.vds_channel_code, p.shop_code,");
//        vsbdSqlBuilder.append("p.staff_code, p.prd_id, date_format(cast(prd_id as date), '%d/%m/%Y'), th, prd_id trend_prd_id, 'XU_THE' row_code,");
//        vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1') ");
//        vsbdSqlBuilder.append(" from chart_service_measure_monthly p");
//        vsbdSqlBuilder.append(" where 1 = 1");
//        vsbdSqlBuilder.append(" and (prd_id between cast(date_add(cast(:prdId as date), interval -12 month) as signed) and :prdId)");
//        vsbdSqlBuilder.append(" and p.shop_code =:parentShopCode and (p.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null) and p.staff_code is not null and service_id=:serviceId ");
        vsbdSqlBuilder.append(") a");
        vsbdSqlBuilder.append(" order by  shop_code, staff_code  ");}
        else{
            vsbdSqlBuilder.append("select s.staff_name as ten_don_vi,'e','f',");
            vsbdSqlBuilder.append(" p.kh_lk,p.lk,ty_le_ht_lk,p.kh_nam,ty_le_ht,p.lk_nam_n1,p.service_id,p.dvt,");
            vsbdSqlBuilder.append(" p.vds_channel_code ,p.shop_code ,  p.staff_code ,p.prd_id , 'TONG_HOP' row_code,");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), p.trend ,1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                    .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                    .append("AND un.status = 1 \n")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                    .append("AND su.shop_code = p.shop_code\n")
                    .append("AND su.service_id = p.service_id\n")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("LIMIT 1) unit_name,");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                    .append("WHERE su.unit_code = un.code\n")
                    .append("AND su.status = 1\n")
                    .append("AND un.status = 1 \n")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                    .append("AND su.shop_code = p.shop_code\n")
                    .append("AND su.service_id = p.service_id\n")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate, th, (select short_name from manage_info_partner where shop_code = p.shop_code) ");
            vsbdSqlBuilder.append(" from chart_service_measure_monthly p");
            vsbdSqlBuilder.append(" join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code and s.staff_type='1' and s.status='1'");
            vsbdSqlBuilder.append(" where 1=1");
            vsbdSqlBuilder.append(" and prd_id = :prdId ");
            vsbdSqlBuilder.append(" and p.service_id =:serviceId");
            vsbdSqlBuilder.append(" and p.vds_channel_code = :vdsChannelCode");
            vsbdSqlBuilder.append(" and s.vds_channel_code = :vdsChannelCode");
            vsbdSqlBuilder.append(" order by shop_code, staff_code ");
            if (dashboardRequestDTO.getPrdId() != null)
                vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
            vhmpMapParams.put("prdId", vlngPrdId);
            if (dashboardRequestDTO.getServiceId() != null)
                vhmpMapParams.put("serviceId", dashboardRequestDTO.getServiceId());
            if(!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode()))
                vhmpMapParams.put("vdsChannelCode",dashboardRequestDTO.getVdsChannelCode());

        }

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        return query.getResultList();

    }

    /**
     * lay du lieu chi tiet tong hop theo quy tu csdl
     *
     * @param dashboardRequestDTO
     * @return du lieu tong hop theo ngay
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    public List<Object[]> sumarizeDetailByQuarter(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChart) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        String vstrNationalStaff = "0";
        Long vlngPrdId = null;
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
            vsbdSqlBuilder.append("select * from (");
            vsbdSqlBuilder.append("select m.shop_name as ten_don_vi,'a','b',kh_lk,lk,ty_le_ht_lk,kh_nam,ty_le_ht");
            vsbdSqlBuilder.append(",lk_nam_n1,service_id,dvt,csmd.vds_channel_code  as vds_channel_code,csmd.shop_code as shop_code ");
            vsbdSqlBuilder.append(" , staff_code as staff_code,prd_id as  prd_id, 'TONG_HOP' row_code, ");
            vsbdSqlBuilder.append("(select name from unit where code = csmd.dvt and status = '1'), csmd.trend ,1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                    .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                    .append("AND un.status = 1 \n")
                    .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                    .append("AND su.shop_code = csmd.shop_code\n")
                    .append("AND su.service_id = csmd.service_id\n")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n")
                    .append("LIMIT 1) unit_name,");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                    .append("WHERE su.unit_code = un.code\n")
                    .append("AND su.status = 1\n")
                    .append("AND un.status = 1 \n")
                    .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                    .append("AND su.shop_code = csmd.shop_code\n")
                    .append("AND su.service_id = csmd.service_id\n")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate, th, 'v' ");
            vsbdSqlBuilder.append(" from chart_service_measure_quaterly csmd , manage_info_partner m ");
            vsbdSqlBuilder.append(" where 1=1 and m.status='1' and m.shop_code = csmd.shop_code and (m.vds_channel_code = csmd.vds_channel_code or (m.vds_channel_code is null or csmd.vds_channel_code is null))");
            vsbdSqlBuilder.append(dashboardRequest.buildDetailSummarySQL(dashboardRequestDTO, vhmpMapParams, configSingleChart.getQueryParam()));
//        vsbdSqlBuilder.append(" union all ");
//        vsbdSqlBuilder.append("select null ten_don_vi,'c','d', null kh_lk, null lk,null ht_lk, null kh_nam, null ht_nam,null lk_nam_n1, service_id,dvt,csmd.vds_channel_code, csmd.shop_code, staff_code, prd_id,");
//        vsbdSqlBuilder.append(" date_format(cast(prd_id as date), '%d/%m/%Y'), th, prd_id trend_prd_id, 'XU_THE' row_code,");
//        vsbdSqlBuilder.append("(select name from unit where code = csmd.dvt and status = '1') ");
//        vsbdSqlBuilder.append(" from chart_service_measure_quaterly csmd, manage_info_partner m");
//        vsbdSqlBuilder.append(" where 1 = 1");
//        vsbdSqlBuilder.append(" and m.shop_code = csmd.shop_code");
//        vsbdSqlBuilder.append(" and ifnull(m.vds_channel_code,'a') = ifnull(csmd.vds_channel_code,'a')");
//        vsbdSqlBuilder.append(" and ifnull(m.parent_shop_code, 'a') = ifnull(:parentShopCode, 'a')");
//        vsbdSqlBuilder.append(" and (m.vds_channel_code =:vdsChannelCode or :vdsChannelCode is null)");
//        vsbdSqlBuilder.append(" and (prd_id between cast(date_add((makedate(year(cast(:prdId as date)), 1) + interval quarter(cast(:prdId as date)) quarter - interval 1 quarter), interval -4 quarter) as signed) and :prdId)");
//        vsbdSqlBuilder.append(" and staff_code is null");
//        vsbdSqlBuilder.append(" and csmd.service_id =:serviceId ");
            vsbdSqlBuilder.append(" union all");
            vsbdSqlBuilder.append(" select s.staff_name as ten_don_vi,'e','f',");
            vsbdSqlBuilder.append(" p.kh_lk,p.lk,ty_le_ht_lk,p.kh_nam,ty_le_ht,p.lk_nam_n1,p.service_id,p.dvt,");
            vsbdSqlBuilder.append(" p.vds_channel_code ,p.shop_code ,  p.staff_code ,p.prd_id , 'TONG_HOP' row_code,");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), p.trend ,1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                    .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                    .append("AND un.status = 1 \n")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                    .append("AND su.shop_code = p.shop_code\n")
                    .append("AND su.service_id = p.service_id\n")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("LIMIT 1) unit_name,");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                    .append("WHERE su.unit_code = un.code\n")
                    .append("AND su.status = 1\n")
                    .append("AND un.status = 1 \n")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                    .append("AND su.shop_code = p.shop_code\n")
                    .append("AND su.service_id = p.service_id\n")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate, th, 'v' ");
            vsbdSqlBuilder.append(" from chart_service_measure_quaterly p");
            vsbdSqlBuilder.append(" join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code and (s.vds_channel_code = p.vds_channel_code or (s.vds_channel_code is null or p.vds_channel_code is null)) and s.staff_type='1' and s.status='1'");
            vsbdSqlBuilder.append(" where 1=1");
            vsbdSqlBuilder.append(" and prd_id = :prdId and p.shop_code =:parentShopCode and (p.vds_channel_code =:vdsChannelCode or :vdsChannelCode is null)");
            vsbdSqlBuilder.append(" and p.staff_code is not null and p.service_id =:serviceId");
//        vsbdSqlBuilder.append(" union all");
//        vsbdSqlBuilder.append(" select null ten_don_vi,'e','f',null kh_lk, null lk,null ht_lk, null kh_nam, null ht_nam, null lk_nam_n1,p.service_id, p.dvt,p.vds_channel_code, p.shop_code,");
//        vsbdSqlBuilder.append("p.staff_code, p.prd_id, date_format(cast(prd_id as date), '%d/%m/%Y'), th, prd_id trend_prd_id, 'XU_THE' row_code,");
//        vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1') ");
//        vsbdSqlBuilder.append(" from chart_service_measure_quaterly p");
//        vsbdSqlBuilder.append(" where 1 = 1");
//        vsbdSqlBuilder.append(" and (prd_id between cast(date_add((makedate(year(cast(:prdId as date)), 1) + interval quarter(cast(:prdId as date)) quarter - interval 1 quarter), interval -4 quarter) as signed) and :prdId)");
//        vsbdSqlBuilder.append(" and p.shop_code =:parentShopCode and (p.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null) and p.staff_code is not null and service_id=:serviceId ");
            vsbdSqlBuilder.append(") a");
            vsbdSqlBuilder.append(" order by  shop_code, staff_code  ");
        }
        else{
            vsbdSqlBuilder.append("select s.staff_name as ten_don_vi,'e','f',");
            vsbdSqlBuilder.append(" p.kh_lk,p.lk,ty_le_ht_lk,p.kh_nam,ty_le_ht,p.lk_nam_n1,p.service_id,p.dvt,");
            vsbdSqlBuilder.append(" p.vds_channel_code ,p.shop_code ,  p.staff_code ,p.prd_id , 'TONG_HOP' row_code,");
            vsbdSqlBuilder.append("(select name from unit where code = p.dvt and status = '1'), p.trend ,1 AS org_rate, ");
            vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                    .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                    .append("AND un.status = 1 \n")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                    .append("AND su.shop_code = p.shop_code\n")
                    .append("AND su.service_id = p.service_id\n")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("LIMIT 1) unit_name,");
            vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                    .append("WHERE su.unit_code = un.code\n")
                    .append("AND su.status = 1\n")
                    .append("AND un.status = 1 \n")
                    .append("AND (su.vds_channel_code = p.vds_channel_code OR (su.vds_channel_code IS NULL AND p.vds_channel_code IS null) )\n")
                    .append("AND su.shop_code = p.shop_code\n")
                    .append("AND su.service_id = p.service_id\n")
                    .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(p.prd_id,'%Y%m%d')\n")
                    .append("AND ifnull(to_date,curdate())>=str_to_date(p.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate, th, (select short_name from manage_info_partner where shop_code = p.shop_code) ");
            vsbdSqlBuilder.append(" from chart_service_measure_quaterly p");
            vsbdSqlBuilder.append(" join vds_staff s on s.shop_code = p.shop_code and s.staff_code = p.staff_code  and s.staff_type='1' and s.status='1'");
            vsbdSqlBuilder.append(" where 1=1");
            vsbdSqlBuilder.append(" and prd_id = :prdId ");
            vsbdSqlBuilder.append(" and p.service_id =:serviceId");
            vsbdSqlBuilder.append(" and p.vds_channel_code =:vdsChannelCode");
            vsbdSqlBuilder.append(" and s.vds_channel_code =:vdsChannelCode");
            vsbdSqlBuilder.append(" order by  shop_code, staff_code  ");
            if (dashboardRequestDTO.getPrdId() != null)
                vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
            vhmpMapParams.put("prdId", vlngPrdId);
            if (dashboardRequestDTO.getServiceId() != null)
                vhmpMapParams.put("serviceId", dashboardRequestDTO.getServiceId());
            if(!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode()))
                vhmpMapParams.put("vdsChannelCode",dashboardRequestDTO.getVdsChannelCode());

        }
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        return query.getResultList();
    }

    /**
     * lay du lieu tong hop theo thang tu csdl
     *
     * @param dashboardRequestDTO
     * @return du lieu tong hop theo ngay
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    public List<Object[]> sumarizeByMonth(DashboardRequestDTO dashboardRequestDTO, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChart) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        List<Long> vlstServiceIds = getServiceIdsInConfig(configSingleChart);

        vsbdSqlBuilder.append(" select * from (");
        vsbdSqlBuilder.append("select (select name from service where id=csmd.service_id) as ten_chi_tieu, sl_don_vi_ht, sl_don_vi_con, lk, kh_lk,");
        vsbdSqlBuilder.append(" ty_le_ht_lk ht_lk, kh_nam, ty_le_ht ht_nam, lk_nam_n1,csmd.service_id as service_id, dvt,");
        vsbdSqlBuilder.append(" csmd.vds_channel_code as vds_channel_code, shop_code as shop_code, staff_code as staff_code ,prd_id as prd_id,");
        vsbdSqlBuilder.append(" 'TONG_HOP' row_code, ");
        vsbdSqlBuilder.append("(select name from unit where code = csmd.dvt and status = '1'), csmd.trend ,1 AS org_rate, ");
        vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = csmd.shop_code\n")
                .append("AND su.service_id = csmd.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("LIMIT 1) unit_name,");
        vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n")
                .append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = csmd.shop_code\n")
                .append("AND su.service_id = csmd.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate,se.service_order, th ");
        vsbdSqlBuilder.append(" from chart_service_measure_monthly csmd, service se  ");
        vsbdSqlBuilder.append(dashboardRequest.buildSummarySQL(dashboardRequestDTO, vhmpMapParams, configSingleChart.getQueryParam()));
//        vsbdSqlBuilder.append(" union all");
//        vsbdSqlBuilder.append(" select null ten_chi_tieu, null sl_don_vi_ht, null sl_don_vi_con, null lk, null kh_lk,");
//        vsbdSqlBuilder.append(" null ht_lk, null kh_nam,  null ht_nam, null lk_nam_n1, service_id, null dvt,");
//        vsbdSqlBuilder.append(" vds_channel_code,  shop_code, staff_code,null prd_id, ");
//        vsbdSqlBuilder.append(" date_format(cast(prd_id as date), '%d/%m/%Y'), case when service_id in (select code from ap_param where type = 'TREND_LINE' and  status ='1') then lk else th end ,prd_id trend_prd_id,'XU_THE' row_code,");
//        vsbdSqlBuilder.append("(select name from unit where code = r.dvt and status = '1') ");
//        vsbdSqlBuilder.append(" from chart_service_measure_monthly r");
//        vsbdSqlBuilder.append(" where 1 = 1 ");
//        vsbdSqlBuilder.append(" and prd_id between cast(date_add(cast(:prdId as date), interval -12 month) as signed) and :prdId");
//        vsbdSqlBuilder.append(" and shop_code = :shopCode");
//        vsbdSqlBuilder.append(" and ifnull(vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a')");
//        vsbdSqlBuilder.append(" and staff_code is null) a");
//        vsbdSqlBuilder.append(" where a.service_id in (:serviceIds)");
        vsbdSqlBuilder.append(") a");
        if (!DataUtil.isNullOrEmpty(vlstServiceIds)) {
            vsbdSqlBuilder.append(" where a.service_id in (:serviceIds)");
            vhmpMapParams.put("serviceIds", vlstServiceIds);
        } else {
            vsbdSqlBuilder.append(" where a.service_id in (0)");
        }
        vsbdSqlBuilder.append(" order by a.service_order ");
//        vhmpMapParams.put("serviceIds", vlstServiceIds);

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();
    }

    /**
     * lay du lieu tong hop theo quy tu csdl
     *
     * @param dashboardRequestDTO
     * @return du lieu tong hop theo ngay
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 15/10/2019
     */
    @Override
    public List<Object[]> sumarizeByQuarter(DashboardRequestDTO dashboardRequestDTO, StaffDTO staffDTO, ConfigSingleChartDTO configSingleChart) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        List<Long> vlstServiceIds = getServiceIdsInConfig(configSingleChart);

        vsbdSqlBuilder.append(" select * from (");
        vsbdSqlBuilder.append("select (select name from service where id=csmd.service_id) as ten_chi_tieu, sl_don_vi_ht, sl_don_vi_con, lk, kh_lk,");
        vsbdSqlBuilder.append(" ty_le_ht_lk ht_lk, kh_nam, ty_le_ht ht_nam, lk_nam_n1,csmd.service_id as service_id, dvt,");
        vsbdSqlBuilder.append(" csmd.vds_channel_code as vds_channel_code, shop_code as shop_code,staff_code as staff_code ,prd_id as prd_id,");
        vsbdSqlBuilder.append(" 'TONG_HOP' row_code, ");
        vsbdSqlBuilder.append("(select name from unit where code = csmd.dvt and status = '1'), csmd.trend ,1 AS org_rate, ");
        vsbdSqlBuilder.append("(SELECT NAME FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n").append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = csmd.shop_code\n")
                .append("AND su.service_id = csmd.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("LIMIT 1) unit_name,");
        vsbdSqlBuilder.append("(SELECT rate FROM shop_unit su, unit un \n")
                .append("WHERE su.unit_code = un.code\n")
                .append("AND su.status = 1\n")
                .append("AND un.status = 1 \n")
                .append("AND (su.vds_channel_code = csmd.vds_channel_code OR (su.vds_channel_code IS NULL AND csmd.vds_channel_code IS null) )\n")
                .append("AND su.shop_code = csmd.shop_code\n")
                .append("AND su.service_id = csmd.service_id\n")
                .append("AND ifnull(from_date,CAST(DATE_FORMAT(curdate() ,'%Y-01-01') as DATE)) <= str_to_date(csmd.prd_id,'%Y%m%d')\n")
                .append("AND ifnull(to_date,curdate())>=str_to_date(csmd.prd_id,'%Y%m%d')\n").append("LIMIT 1) unit_rate,se.service_order, th ");
        vsbdSqlBuilder.append(" from chart_service_measure_quaterly csmd,service se  ");
        vsbdSqlBuilder.append(dashboardRequest.buildSummarySQL(dashboardRequestDTO, vhmpMapParams, configSingleChart.getQueryParam()));
//        vsbdSqlBuilder.append(" union all");
//        vsbdSqlBuilder.append(" select null ten_chi_tieu, null sl_don_vi_ht, null sl_don_vi_con, null lk, null kh_lk,");
//        vsbdSqlBuilder.append(" null ht_lk, null kh_nam,  null ht_nam, null lk_nam_n1, service_id, null dvt,");
//        vsbdSqlBuilder.append(" vds_channel_code,  shop_code, staff_code,null prd_id, ");
//        vsbdSqlBuilder.append(" date_format(cast(prd_id as date), '%d/%m/%Y'), case when service_id in (select code from ap_param where type = 'TREND_LINE' and  status ='1') then lk else th end ,prd_id trend_prd_id,'XU_THE' row_code,");
//        vsbdSqlBuilder.append("(select name from unit where code = r.dvt and status = '1') ");
//        vsbdSqlBuilder.append(" from chart_service_measure_quaterly r");
//        vsbdSqlBuilder.append(" where 1 = 1 ");
//        vsbdSqlBuilder.append(" and prd_id between cast(date_add((makedate(year(cast(:prdId as date)), 1) + interval quarter(cast(:prdId as date)) quarter - interval 1 quarter), interval -4 quarter) as signed) and :prdId");
//        vsbdSqlBuilder.append(" and shop_code = :shopCode");
//        vsbdSqlBuilder.append(" and ifnull(vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a')");
//        vsbdSqlBuilder.append(" and staff_code is null) a");
//        vsbdSqlBuilder.append(" where a.service_id in (:serviceIds)");
        vsbdSqlBuilder.append(") a");
        if (!DataUtil.isNullOrEmpty(vlstServiceIds)) {
            vsbdSqlBuilder.append(" where a.service_id in (:serviceIds)");
            vhmpMapParams.put("serviceIds", vlstServiceIds);
        } else {
            vsbdSqlBuilder.append(" where a.service_id in (0)");
        }
        vsbdSqlBuilder.append(" order by a.service_order ");
        vhmpMapParams.put("serviceIds", vlstServiceIds);

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();
    }

    /**
     * lay service id trong cau hinh
     *
     * @param configSingleChart
     * @return
     */
    private List<Long> getServiceIdsInConfig(ConfigSingleChartDTO configSingleChart) {
        List<Long> vlstServiceIds = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(configSingleChart.getServiceIds())) {
            String[] varrServiceIds = configSingleChart.getServiceIds().split(",");
            for (int i = 0; i < varrServiceIds.length; i++) {
                vlstServiceIds.add(Long.valueOf(varrServiceIds[i].trim()));
            }
        }
        return vlstServiceIds;
    }

    @Override
    public List<Object[]> renderExcelAllStaff(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngPrdId = null;
        Long vlngServiceId = dashboardRequestDTO.getServiceId();
        String vstrShopCode = dashboardRequestDTO.getObjectCode();
        String vstrVdsChannelCode = dashboardRequestDTO.getVdsChannelCode();

        //CHU KY NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
            vsbdSql.append("select (select staff_name from vds_staff where staff_code = p.staff_code) as ten_nhan_vien,")
                    .append(" p.kh_lk,p.lk,round( p.lk/p.kh_lk *100,2) ht_den_ngay,p.kh_thang,round( p.lk/p.kh_thang *100,2) ht_thang,(p.lk - p.lk_thang_n1) tang_giam, p.dvt")
                    .append(" From chart_service_measure_daily p")
                    .append(" where prd_id = :prdId")
                    .append(" and service_id = :serviceId")
                    .append(" and staff_code in (")
                    .append("select staff_code from vds_staff")
                    .append(" where (shop_code,vds_channel_code)")
                    .append(" in(")
                    .append("with recursive re as")
                    .append(" (select shop_code,vds_channel_code from manage_info_partner where shop_code = :shopCode and vds_channel_code = :vdsChannelCode")
                    .append(" union")
                    .append(" select a.shop_code,a.vds_channel_code from manage_info_partner a,re where a.parent_shop_code = re.shop_code)")
                    .append(" select shop_code,vds_channel_code From re)")
                    .append(" )")
                    .append(" order by p.staff_code");
        }
        //CHU KY THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
            vsbdSql.append("select (select staff_name from vds_staff where staff_code = p.staff_code) as ten_nhan_vien,")
                    .append(" p.kh_lk,p.lk,round( p.lk/p.kh_lk *100,2) ht_den_ngay,p.kh_nam,round( p.lk/p.kh_nam *100,2) ht_thang,(p.lk - p.lk_nam_n1) tang_giam, p.dvt")
                    .append(" From chart_service_measure_monthly p")
                    .append(" where prd_id = :prdId")
                    .append(" and service_id = :serviceId")
                    .append(" and staff_code in (")
                    .append("select staff_code from vds_staff")
                    .append(" where (shop_code,vds_channel_code)")
                    .append(" in(")
                    .append("with recursive re as")
                    .append(" (select shop_code,vds_channel_code from manage_info_partner where shop_code = :shopCode and vds_channel_code = :vdsChannelCode")
                    .append(" union")
                    .append(" select a.shop_code,a.vds_channel_code from manage_info_partner a,re where a.parent_shop_code = re.shop_code)")
                    .append(" select shop_code,vds_channel_code From re)")
                    .append(" )")
                    .append(" order by p.staff_code");

        }
        //CHU KY QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
            vsbdSql.append("select (select staff_name from vds_staff where staff_code = p.staff_code) as ten_nhan_vien,")
                    .append(" p.kh_lk,p.lk,round( p.lk/p.kh_lk *100,2) ht_den_ngay,p.kh_nam,round( p.lk/p.kh_nam *100,2) ht_thang,(p.lk - p.lk_nam_n1) tang_giam, p.dvt")
                    .append(" From chart_service_measure_quaterly p")
                    .append(" where prd_id = :prdId")
                    .append(" and service_id = :serviceId")
                    .append(" and staff_code in (")
                    .append("select staff_code from vds_staff")
                    .append(" where (shop_code,vds_channel_code)")
                    .append(" in(")
                    .append("with recursive re as")
                    .append(" (select shop_code,vds_channel_code from manage_info_partner where shop_code = :shopCode and vds_channel_code = :vdsChannelCode")
                    .append(" union")
                    .append(" select a.shop_code,a.vds_channel_code from manage_info_partner a,re where a.parent_shop_code = re.shop_code)")
                    .append(" select shop_code,vds_channel_code From re)")
                    .append(" )")
                    .append(" order by p.staff_code");
        }

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("prdId", vlngPrdId);
        query.setParameter("serviceId", vlngServiceId);
        query.setParameter("shopCode", vstrShopCode);
        query.setParameter("vdsChannelCode", vstrVdsChannelCode);
        List<Object[]> vlstData = query.getResultList();

        return vlstData;
    }

}
