package vn.vissoft.dashboard.repo.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.chart.ContinuityFailDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.ContentTopRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Repository
public class ContentTopRepoImpl implements ContentTopRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * lay data spark SL DV/NV LIEN TIEP KHONG DAT (level 1)
     *
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/05
     */
    @Override
    public Object[] getContinuityFail(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngPrdId = null;
        String vstrShopCode = dashboardRequestDTO.getObjectCode();
        String vstrVdsChannelCode = dashboardRequestDTO.getVdsChannelCode();
        String vdsCodeWarning = dashboardRequestDTO.getCodeWarning();
        int vintCount = Integer.parseInt(vdsCodeWarning);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date vDate = new Date(dashboardRequestDTO.getPrdId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vDate);

        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
        }

        String vstrPrdCondition = "prd_id = " + vlngPrdId;

        for (int i = 1; i < vintCount; i++) {
            if (vintCycleId == Constants.CYCLE_ID.DAY) {
                cal.add(Calendar.MONTH, -1);
                java.util.Date vDateUtilAfter = cal.getTime();
                Date vDateSqlAfter = new Date(vDateUtilAfter.getTime());
                Long vlngPrdIdAfter = Long.valueOf(dateFormat.format(vDateSqlAfter));
                vstrPrdCondition = vstrPrdCondition + " or prd_id = " + vlngPrdIdAfter;
                cal.setTime(vDateSqlAfter);
            }
            if (vintCycleId == Constants.CYCLE_ID.MONTH) {
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                java.util.Date vDateUtilAfter = cal.getTime();
                Date vDateSqlAfter = new Date(vDateUtilAfter.getTime());
                Long vlngPrdIdAfter = Long.valueOf(dateFormat.format(vDateSqlAfter));
                vstrPrdCondition = vstrPrdCondition + " or prd_id = " + vlngPrdIdAfter;
                cal.setTime(vDateSqlAfter);
            }
            if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.MONTH, -3);
                java.util.Date vDateUtilAfter = cal.getTime();
                Date vDateSqlAfter = new Date(vDateUtilAfter.getTime());
                Long vlngPrdIdAfter = Long.valueOf(dateFormat.format(vDateSqlAfter));
                vstrPrdCondition = vstrPrdCondition + " or prd_id = " + vlngPrdIdAfter;
                cal.setTime(vDateSqlAfter);
            }
        }

        //CHU KY NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSql.append("select count(distinct(concat(don_vi,ifnull(nhan_vien,'')))) sldv_khong_ht,")
                    .append(" max(sldv_giao_ct) sldv_con, ROUND((count(distinct(concat(don_vi,ifnull(nhan_vien,'')))) / max(sldv_giao_ct) * 100), 2)")
                    .append(" from vds_redwarning_daily vrd")
                    .append(" where prd_id = :prdId")
                    .append(" and kenh_cha = :vdsChannelCode")
                    .append(" and don_vi_cha = :shopCode")
                    .append(" and exists (select * from vds_redwarning_daily")
                    .append(" where (").append(vstrPrdCondition).append(")")
                    .append(" and kenh = vrd.kenh")
                    .append(" and don_vi = vrd.don_vi")
                    .append(" and ifnull(nhan_vien,'nv') = ifnull(vrd.nhan_vien,'nv')")
                    .append(" having count(distinct(concat(prd_id,don_vi,ifnull(nhan_vien,'')))) = :count)");

        }
        //CHU KY THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSql.append("select count(distinct(concat(don_vi,ifnull(nhan_vien,'')))) sldv_khong_ht,")
                    .append(" max(sldv_giao_ct) sldv_con, ROUND((count(distinct(concat(don_vi,ifnull(nhan_vien,'')))) / max(sldv_giao_ct) * 100), 2)")
                    .append(" from vds_redwarning_monthly vrd")
                    .append(" where prd_id = :prdId")
                    .append(" and kenh_cha = :vdsChannelCode")
                    .append(" and don_vi_cha = :shopCode")
                    .append(" and exists (select * from vds_redwarning_monthly")
                    .append(" where (").append(vstrPrdCondition).append(")")
                    .append(" and kenh = vrd.kenh")
                    .append(" and don_vi = vrd.don_vi")
                    .append(" and ifnull(nhan_vien,'nv') = ifnull(vrd.nhan_vien,'nv')")
                    .append(" having count(distinct(concat(prd_id,don_vi,ifnull(nhan_vien,'')))) = :count)");
        }
        //CHU KY QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSql.append("select count(distinct(concat(don_vi,ifnull(nhan_vien,'')))) sldv_khong_ht,")
                    .append(" max(sldv_giao_ct) sldv_con, ROUND((count(distinct(concat(don_vi,ifnull(nhan_vien,'')))) / max(sldv_giao_ct) * 100), 2)")
                    .append(" from vds_redwarning_quarterly vrd")
                    .append(" where prd_id = :prdId")
                    .append(" and kenh_cha = :vdsChannelCode")
                    .append(" and don_vi_cha = :shopCode")
                    .append(" and exists (select * from vds_redwarning_quarterly")
                    .append(" where (").append(vstrPrdCondition).append(")")
                    .append(" and kenh = vrd.kenh")
                    .append(" and don_vi = vrd.don_vi")
                    .append(" and ifnull(nhan_vien,'nv') = ifnull(vrd.nhan_vien,'nv')")
                    .append(" having count(distinct(concat(prd_id,don_vi,ifnull(nhan_vien,'')))) = :count)");
        }

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("prdId", vlngPrdId);
        query.setParameter("vdsChannelCode", vstrVdsChannelCode);
        query.setParameter("shopCode", vstrShopCode);
//        query.setParameter("prdIdCondition", vstrPrdCondition);
        query.setParameter("count", vintCount);
        Object[] data = (Object[]) query.getResultList().get(0);

        return data;
    }

    /**
     * lay data spark SL DV/NV LIEN TIEP KHONG DAT (level 2)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/05
     */
    @Override
    public List<Object[]> getContinuityFailLvTwo(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngPrdId = null;
        String vstrShopCode = dashboardRequestDTO.getObjectCode();
        String vstrVdsChannelCode = dashboardRequestDTO.getVdsChannelCode();
        String vdsCodeWarning = dashboardRequestDTO.getCodeWarning();
        int vintCount = Integer.parseInt(vdsCodeWarning);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date vDate = new Date(dashboardRequestDTO.getPrdId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vDate);

        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
        }

        String vstrPrdCondition = "prd_id = " + vlngPrdId;

        for (int i = 1; i < vintCount; i++) {
            if (vintCycleId == Constants.CYCLE_ID.DAY) {
                cal.add(Calendar.MONTH, -1);
                java.util.Date vDateUtilAfter = cal.getTime();
                Date vDateSqlAfter = new Date(vDateUtilAfter.getTime());
                Long vlngPrdIdAfter = Long.valueOf(dateFormat.format(vDateSqlAfter));
                vstrPrdCondition = vstrPrdCondition + " or prd_id = " + vlngPrdIdAfter;
                cal.setTime(vDateSqlAfter);
            }
            if (vintCycleId == Constants.CYCLE_ID.MONTH) {
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                java.util.Date vDateUtilAfter = cal.getTime();
                Date vDateSqlAfter = new Date(vDateUtilAfter.getTime());
                Long vlngPrdIdAfter = Long.valueOf(dateFormat.format(vDateSqlAfter));
                vstrPrdCondition = vstrPrdCondition + " or prd_id = " + vlngPrdIdAfter;
                cal.setTime(vDateSqlAfter);
            }
            if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.MONTH, -3);
                java.util.Date vDateUtilAfter = cal.getTime();
                Date vDateSqlAfter = new Date(vDateUtilAfter.getTime());
                Long vlngPrdIdAfter = Long.valueOf(dateFormat.format(vDateSqlAfter));
                vstrPrdCondition = vstrPrdCondition + " or prd_id = " + vlngPrdIdAfter;
                cal.setTime(vDateSqlAfter);
            }
        }

        //CHU KY NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSql.append("select prd_id,kenh,case when nhan_vien is null")
                    .append(" then (select short_name from manage_info_partner where shop_code = vrd.don_vi and vds_channel_code = vrd.kenh)")
                    .append(" else (select staff_name from vds_staff where staff_code = vrd.nhan_vien LIMIT 1) end dv_nv,")
                    .append(" group_concat(concat(se.name,' - ',round(vrd.th/vrd.kh*100,2),'%') order by se.name separator '; ') chi_tieu_ko_dat")
                    .append(" from vds_redwarning_daily vrd , service se")
                    .append(" where (").append(vstrPrdCondition).append(")")
                    .append(" and kenh_cha = :vdsChannelCode")
                    .append(" and don_vi_cha = :shopCode")
                    .append(" and vrd.service_id = se.id")
                    .append(" and exists (select * from vds_redwarning_daily")
                    .append(" where (").append(vstrPrdCondition).append(")")
                    .append(" and kenh = vrd.kenh")
                    .append(" and don_vi = vrd.don_vi")
                    .append(" and ifnull(nhan_vien,'nv') = ifnull(vrd.nhan_vien,'nv')")
                    .append(" having count(distinct(concat(prd_id,don_vi,ifnull(nhan_vien,'')))) = :codeWarning)")
                    .append(" group by prd_id,kenh,don_vi,nhan_vien")
                    .append(" order by kenh,dv_nv asc, prd_id desc");
        }
        //CHU KY THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSql.append("select prd_id,kenh,case when nhan_vien is null")
                    .append(" then (select short_name from manage_info_partner where shop_code = vrd.don_vi and vds_channel_code = vrd.kenh)")
                    .append(" else (select staff_name from vds_staff where staff_code = vrd.nhan_vien LIMIT 1) end dv_nv,")
                    .append(" group_concat(concat(se.name,' - ',round(vrd.th/vrd.kh*100,2),'%') order by se.name separator '; ') chi_tieu_ko_dat")
                    .append(" from vds_redwarning_monthly vrd , service se")
                    .append(" where (").append(vstrPrdCondition).append(")")
                    .append(" and kenh_cha = :vdsChannelCode")
                    .append(" and don_vi_cha = :shopCode")
                    .append(" and vrd.service_id = se.id")
                    .append(" and exists (select * from vds_redwarning_monthly")
                    .append(" where (").append(vstrPrdCondition).append(")")
                    .append(" and kenh = vrd.kenh")
                    .append(" and don_vi = vrd.don_vi")
                    .append(" and ifnull(nhan_vien,'nv') = ifnull(vrd.nhan_vien,'nv')")
                    .append(" having count(distinct(concat(prd_id,don_vi,ifnull(nhan_vien,'')))) = :codeWarning)")
                    .append(" group by prd_id,kenh,don_vi,nhan_vien")
                    .append(" order by kenh,dv_nv asc, prd_id desc");
        }
        //CHU KY QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSql.append("select prd_id,kenh,case when nhan_vien is null")
                    .append(" then (select short_name from manage_info_partner where shop_code = vrd.don_vi and vds_channel_code = vrd.kenh)")
                    .append(" else (select staff_name from vds_staff where staff_code = vrd.nhan_vien LIMIT 1) end dv_nv,")
                    .append(" group_concat(concat(se.name,' - ',round(vrd.th/vrd.kh*100,2),'%') order by se.name separator '; ') chi_tieu_ko_dat")
                    .append(" from vds_redwarning_quarterly vrd , service se")
                    .append(" where (").append(vstrPrdCondition).append(")")
                    .append(" and kenh_cha = :vdsChannelCode")
                    .append(" and don_vi_cha = :shopCode")
                    .append(" and vrd.service_id = se.id")
                    .append(" and exists (select * from vds_redwarning_quarterly")
                    .append(" where (").append(vstrPrdCondition).append(")")
                    .append(" and kenh = vrd.kenh")
                    .append(" and don_vi = vrd.don_vi")
                    .append(" and ifnull(nhan_vien,'nv') = ifnull(vrd.nhan_vien,'nv')")
                    .append(" having count(distinct(concat(prd_id,don_vi,ifnull(nhan_vien,'')))) = :codeWarning)")
                    .append(" group by prd_id,kenh,don_vi,nhan_vien")
                    .append(" order by kenh,dv_nv asc, prd_id desc");
        }

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("vdsChannelCode", vstrVdsChannelCode);
        query.setParameter("shopCode", vstrShopCode);
//        query.setParameter("prdIdCondition", vstrPrdCondition);
        query.setParameter("codeWarning", vintCount);
        List<Object[]> vlstData = query.getResultList();

        return vlstData;
    }

    /**
     * lay du lieu cho content top kenh tinh spark 3 va 4 (level 1)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/05
     */
    @Override
    public Object[] getTopByService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngServiceId = Long.valueOf(configSingleChartDTO.getServiceIds());
        String vstrVdsChannelCode = dashboardRequestDTO.getVdsChannelCode();
        String vstrShopCode = dashboardRequestDTO.getObjectCode();
        Long vlngPrdId = null;

        //convert millisecond ve prd_id
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
        }

        //CHU KY NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSql.append("select sum(a.duoi_muc_1) duoi_muc_1,")
                    .append(" sum(a.tren_muc_1_duoi_muc_2) tren_muc_1_duoi_muc_2,")
                    .append(" sum(a.tren_muc_2_duoi_muc_3) tren_muc_2_duoi_muc_3,")
                    .append(" sum(a.tren_muc_3) tren_muc_3 ")
//                    .append(" max(a.sl_don_vi_con) sl_don_vi_con")
                    .append(" from")
                    .append(" (")
                    .append(" select")
                    .append(" case when 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code ='1') then 1 else 0 end duoi_muc_1,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='1')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='2') then 1 else 0 end tren_muc_1_duoi_muc_2,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='2')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_2_duoi_muc_3,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_3 ")
//                    .append(" (SELECT max(sl_don_vi_con) FROM chart_service_measure_daily WHERE prd_id = csmd.prd_id AND service_id= csmd.service_id) sl_don_vi_con")
                    .append(" From chart_service_measure_daily csmd ")
                    .append(" where prd_id = :prdId and service_id = :serviceId")
                    .append(" and (")
                    .append(" ((vds_channel_code, shop_code)")
                    .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                    .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                    .append(" and staff_code is null )")
                    .append(" OR (vds_channel_code = :vdsChannelCode and shop_code = :shopCode")
                    .append(" and staff_code is not null )")
                    .append(" )")
                    .append(" ) a");
        }
        //CHU KY THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSql.append("select sum(a.duoi_muc_1) duoi_muc_1,")
                    .append(" sum(a.tren_muc_1_duoi_muc_2) tren_muc_1_duoi_muc_2,")
                    .append(" sum(a.tren_muc_2_duoi_muc_3) tren_muc_2_duoi_muc_3,")
                    .append(" sum(a.tren_muc_3) tren_muc_3 ")
//                    .append(" max(a.sl_don_vi_con) sl_don_vi_con")
                    .append(" from")
                    .append(" (")
                    .append(" select")
                    .append(" case when 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code ='1') then 1 else 0 end duoi_muc_1,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='1')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='2') then 1 else 0 end tren_muc_1_duoi_muc_2,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='2')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_2_duoi_muc_3,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_3 ")
//                    .append(" (SELECT max(sl_don_vi_con) FROM chart_service_measure_monthly WHERE prd_id = csmd.prd_id AND service_id= csmd.service_id) sl_don_vi_con")
                    .append(" From chart_service_measure_monthly csmd ")
                    .append(" where prd_id = :prdId and service_id = :serviceId")
                    .append(" and (")
                    .append(" ((vds_channel_code, shop_code)")
                    .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                    .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                    .append(" and staff_code is null )")
                    .append(" OR (vds_channel_code = :vdsChannelCode and shop_code = :shopCode")
                    .append(" and staff_code is not null )")
                    .append(" )")
                    .append(" ) a");
        }
        //CHU KY QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSql.append("select sum(a.duoi_muc_1) duoi_muc_1,")
                    .append(" sum(a.tren_muc_1_duoi_muc_2) tren_muc_1_duoi_muc_2,")
                    .append(" sum(a.tren_muc_2_duoi_muc_3) tren_muc_2_duoi_muc_3,")
                    .append(" sum(a.tren_muc_3) tren_muc_3 ")
//                    .append(" max(a.sl_don_vi_con) sl_don_vi_con")
                    .append(" from")
                    .append(" (")
                    .append(" select")
                    .append(" case when 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code ='1') then 1 else 0 end duoi_muc_1,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='1')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='2') then 1 else 0 end tren_muc_1_duoi_muc_2,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='2')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_2_duoi_muc_3,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_3 ")
//                    .append(" (SELECT max(sl_don_vi_con) FROM chart_service_measure_quaterly WHERE prd_id = csmd.prd_id AND service_id= csmd.service_id) sl_don_vi_con")
                    .append(" From chart_service_measure_quaterly csmd ")
                    .append(" where prd_id = :prdId and service_id = :serviceId")
                    .append(" and (")
                    .append(" ((vds_channel_code, shop_code)")
                    .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                    .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                    .append(" and staff_code is null )")
                    .append(" OR (vds_channel_code = :vdsChannelCode and shop_code = :shopCode")
                    .append(" and staff_code is not null )")
                    .append(" )")
                    .append(" ) a");
        }

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("prdId", vlngPrdId);
        query.setParameter("serviceId", vlngServiceId);
        query.setParameter("vdsChannelCode", vstrVdsChannelCode);
        query.setParameter("shopCode", vstrShopCode);
        Object[] data = (Object[]) query.getResultList().get(0);

        return data;
    }

    /**
     * lay data table theo service spark 3,4 (level 2)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/05
     */
    @Override
    public List<Object[]> getTableByService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        String vstrVdsChannelCode = dashboardRequestDTO.getVdsChannelCode();
        String vstrShopCode = dashboardRequestDTO.getObjectCode();
        Long vlngServiceId = Long.valueOf(configSingleChartDTO.getServiceIds());
        String vstrPRow = dashboardRequestDTO.getpRow();
        Long vlngPrdId = null;

        //convert millisecond ve prd_id
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
        }

        //CHU KY NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSql.append("select case when staff_code is null")
                    .append(" then ( select shop_name from manage_info_partner")
                    .append(" where vds_channel_code = csmd.vds_channel_code")
                    .append(" and shop_code = csmd.shop_code )")
                    .append(" else (select staff_name from vds_staff where staff_code = csmd.staff_code ) end ten_dv_nv ,")
                    .append(" lk thuc_hien,kh_lk ke_hoach, round(100*lk/kh_lk,2) ty_le")
                    .append(" From chart_service_measure_daily csmd")
                    .append(" where prd_id = :prdId and service_id = :serviceId")
                    .append(" and ( (staff_code is null and (vds_channel_code, shop_code)")
                    .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                    .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                    .append(" )")
                    .append(" or (staff_code is not null and csmd.vds_channel_code = :vdsChannelCode and csmd.shop_code = :shopCode)")
                    .append(" )")
                    .append(" and (")
                    .append(" ( :pRow = '0' and 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" )")
                    .append(" OR (:pRow = '1' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" )")
                    .append(" OR (:pRow = '2' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" OR (:pRow ='3'  and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" )")
                    .append(" order by lk/kh_lk");
        }
        //CHU KY THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSql.append("select case when staff_code is null")
                    .append(" then ( select shop_name from manage_info_partner")
                    .append(" where vds_channel_code = csmd.vds_channel_code")
                    .append(" and shop_code = csmd.shop_code )")
                    .append(" else (select staff_name from vds_staff where staff_code = csmd.staff_code ) end ten_dv_nv ,")
                    .append(" lk thuc_hien,kh_lk ke_hoach, round(100*lk/kh_lk,2) ty_le")
                    .append(" From chart_service_measure_monthly csmd")
                    .append(" where prd_id = :prdId and service_id = :serviceId")
                    .append(" and ( (staff_code is null and (vds_channel_code, shop_code)")
                    .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                    .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                    .append(" )")
                    .append(" or (staff_code is not null and csmd.vds_channel_code = :vdsChannelCode and csmd.shop_code = :shopCode)")
                    .append(" )")
                    .append(" and (")
                    .append(" ( :pRow = '0' and 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" )")
                    .append(" OR (:pRow = '1' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" )")
                    .append(" OR (:pRow = '2' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" OR (:pRow ='3'  and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" )")
                    .append(" order by lk/kh_lk");
        }
        //CHU KY QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSql.append("select case when staff_code is null")
                    .append(" then ( select shop_name from manage_info_partner")
                    .append(" where vds_channel_code = csmd.vds_channel_code")
                    .append(" and shop_code = csmd.shop_code )")
                    .append(" else (select staff_name from vds_staff where staff_code = csmd.staff_code ) end ten_dv_nv ,")
                    .append(" lk thuc_hien,kh_lk ke_hoach, round(100*lk/kh_lk,2) ty_le")
                    .append(" From chart_service_measure_quaterly csmd")
                    .append(" where prd_id = :prdId and service_id = :serviceId")
                    .append(" and ( (staff_code is null and (vds_channel_code, shop_code)")
                    .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                    .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                    .append(" )")
                    .append(" or (staff_code is not null and csmd.vds_channel_code = :vdsChannelCode and csmd.shop_code = :shopCode)")
                    .append(" )")
                    .append(" and (")
                    .append(" ( :pRow = '0' and 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" )")
                    .append(" OR (:pRow = '1' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" )")
                    .append(" OR (:pRow = '2' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" OR (:pRow ='3'  and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" )")
                    .append(" order by lk/kh_lk");
        }

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("prdId", vlngPrdId);
        query.setParameter("serviceId", vlngServiceId);
        query.setParameter("vdsChannelCode", vstrVdsChannelCode);
        query.setParameter("shopCode", vstrShopCode);
        query.setParameter("pRow", vstrPRow);
        List<Object[]> vlstData = query.getResultList();

        return vlstData;
    }

    /**
     * lay ra cac nguong hoan thanh cua chi tieu (lv 2 bang tong hop)
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @Author VuBL
     * @since 2020/05
     */
    @Override
    public Object[] getComplThresholdService(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        Long vlngServiceId = Long.valueOf(dashboardRequestDTO.getServiceId());
        String vstrVdsChannelCode = dashboardRequestDTO.getVdsChannelCode();
        String vstrShopCode = dashboardRequestDTO.getObjectCode();
        Long vlngPrdId = null;
        String vstrNationalStaff = "0";
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }

        //convert millisecond ve prd_id
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
        }

        //CHU KY NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSql.append("select sum(a.duoi_muc_1) duoi_muc_1,")
                    .append(" sum(a.tren_muc_1_duoi_muc_2) tren_muc_1_duoi_muc_2,")
                    .append(" sum(a.tren_muc_2_duoi_muc_3) tren_muc_2_duoi_muc_3,")
                    .append(" sum(a.tren_muc_3) tren_muc_3 ")
//                    .append(" max(a.sl_don_vi_con) sl_don_vi_con")
                    .append(" from")
                    .append(" (")
                    .append(" select")
                    .append(" case when 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code ='1') then 1 else 0 end duoi_muc_1,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='1')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='2') then 1 else 0 end tren_muc_1_duoi_muc_2,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='2')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_2_duoi_muc_3,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_3 ")
//                    .append(" (SELECT max(sl_don_vi_con) FROM chart_service_measure_daily WHERE prd_id = csmd.prd_id AND service_id= csmd.service_id) sl_don_vi_con")
                    .append(" From chart_service_measure_daily csmd ")
                    .append(" where prd_id = :prdId and service_id = :serviceId");
            if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
                vsbdSql.append(" and (")
                        .append(" ((vds_channel_code, shop_code)")
                        .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                        .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                        .append(" and staff_code is null )")
                        .append(" OR (vds_channel_code = :vdsChannelCode and shop_code = :shopCode")
                        .append(" and staff_code is not null )")
                        .append(" )")
                        .append(" ) a");
                vhmpMapParams.put("prdId", vlngPrdId);
                vhmpMapParams.put("serviceId", vlngServiceId);
                vhmpMapParams.put("vdsChannelCode", vstrVdsChannelCode);
                vhmpMapParams.put("shopCode", vstrShopCode);
            } else {
                vsbdSql.append(" and staff_code is not null")
                        .append(" and (vds_channel_code = :vdsChannelCode or :vdsChannelCode is null)")
                        .append(" ) a");
                vhmpMapParams.put("prdId", vlngPrdId);
                vhmpMapParams.put("serviceId", vlngServiceId);
                vhmpMapParams.put("vdsChannelCode", vstrVdsChannelCode);
            }
        }
        //CHU KY THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSql.append("select sum(a.duoi_muc_1) duoi_muc_1,")
                    .append(" sum(a.tren_muc_1_duoi_muc_2) tren_muc_1_duoi_muc_2,")
                    .append(" sum(a.tren_muc_2_duoi_muc_3) tren_muc_2_duoi_muc_3,")
                    .append(" sum(a.tren_muc_3) tren_muc_3 ")
//                    .append(" max(a.sl_don_vi_con) sl_don_vi_con")
                    .append(" from")
                    .append(" (")
                    .append(" select")
                    .append(" case when 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code ='1') then 1 else 0 end duoi_muc_1,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='1')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='2') then 1 else 0 end tren_muc_1_duoi_muc_2,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='2')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_2_duoi_muc_3,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_3 ")
//                    .append(" (SELECT max(sl_don_vi_con) FROM chart_service_measure_monthly WHERE prd_id = csmd.prd_id AND service_id= csmd.service_id) sl_don_vi_con")
                    .append(" From chart_service_measure_monthly csmd ")
                    .append(" where prd_id = :prdId and service_id = :serviceId");
            if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
                vsbdSql.append(" and (")
                        .append(" ((vds_channel_code, shop_code)")
                        .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                        .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                        .append(" and staff_code is null )")
                        .append(" OR (vds_channel_code = :vdsChannelCode and shop_code = :shopCode")
                        .append(" and staff_code is not null )")
                        .append(" )")
                        .append(" ) a");
                vhmpMapParams.put("prdId", vlngPrdId);
                vhmpMapParams.put("serviceId", vlngServiceId);
                vhmpMapParams.put("vdsChannelCode", vstrVdsChannelCode);
                vhmpMapParams.put("shopCode", vstrShopCode);
            } else {
                vsbdSql.append(" and staff_code is not null")
                        .append(" and (vds_channel_code = :vdsChannelCode or :vdsChannelCode is null)")
                        .append(" ) a");
                vhmpMapParams.put("prdId", vlngPrdId);
                vhmpMapParams.put("serviceId", vlngServiceId);
                vhmpMapParams.put("vdsChannelCode", vstrVdsChannelCode);
            }
        }
        //CHU KY QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSql.append("select sum(a.duoi_muc_1) duoi_muc_1,")
                    .append(" sum(a.tren_muc_1_duoi_muc_2) tren_muc_1_duoi_muc_2,")
                    .append(" sum(a.tren_muc_2_duoi_muc_3) tren_muc_2_duoi_muc_3,")
                    .append(" sum(a.tren_muc_3) tren_muc_3 ")
//                    .append(" max(a.sl_don_vi_con) sl_don_vi_con")
                    .append(" from")
                    .append(" (")
                    .append(" select")
                    .append(" case when 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code ='1') then 1 else 0 end duoi_muc_1,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='1')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='2') then 1 else 0 end tren_muc_1_duoi_muc_2,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='2')")
                    .append(" and 100*lk/kh_lk<(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_2_duoi_muc_3,")
                    .append(" case when 100*lk/kh_lk>=(select value from ap_param where type = 'COMPLETION_RATE' and code ='3') then 1 else 0 end tren_muc_3 ")
//                    .append(" (SELECT max(sl_don_vi_con) FROM chart_service_measure_quaterly WHERE prd_id = csmd.prd_id AND service_id= csmd.service_id) sl_don_vi_con")
                    .append(" From chart_service_measure_quaterly csmd ")
                    .append(" where prd_id = :prdId and service_id = :serviceId");
            if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
                vsbdSql.append(" and (")
                        .append(" ((vds_channel_code, shop_code)")
                        .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                        .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                        .append(" and staff_code is null )")
                        .append(" OR (vds_channel_code = :vdsChannelCode and shop_code = :shopCode")
                        .append(" and staff_code is not null )")
                        .append(" )")
                        .append(" ) a");
                vhmpMapParams.put("prdId", vlngPrdId);
                vhmpMapParams.put("serviceId", vlngServiceId);
                vhmpMapParams.put("vdsChannelCode", vstrVdsChannelCode);
                vhmpMapParams.put("shopCode", vstrShopCode);
            } else {
                vsbdSql.append(" and staff_code is not null")
                        .append(" and (vds_channel_code = :vdsChannelCode or :vdsChannelCode is null)")
                        .append(" ) a");
                vhmpMapParams.put("prdId", vlngPrdId);
                vhmpMapParams.put("serviceId", vlngServiceId);
                vhmpMapParams.put("vdsChannelCode", vstrVdsChannelCode);
            }
        }

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
//        query.setParameter("prdId", vlngPrdId);
//        query.setParameter("serviceId", vlngServiceId);
//        query.setParameter("vdsChannelCode", vstrVdsChannelCode);
//        query.setParameter("shopCode", vstrShopCode);
        Object[] data = (Object[]) query.getResultList().get(0);

        return data;
    }

    @Override
    public List<Object[]> getTableComplThreshold(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        String vstrVdsChannelCode = dashboardRequestDTO.getVdsChannelCode();
        String vstrShopCode = dashboardRequestDTO.getObjectCode();
        Long vlngServiceId = Long.valueOf(dashboardRequestDTO.getServiceId());
        String vstrPRow = dashboardRequestDTO.getpRow();
        Long vlngPrdId = null;
        String vstrNationalStaff = "0";
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }

        //convert millisecond ve prd_id
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
        }

        //CHU KY NGAY
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSql.append("select case when staff_code is null")
                    .append(" then ( select shop_name from manage_info_partner")
                    .append(" where vds_channel_code = csmd.vds_channel_code")
                    .append(" and shop_code = csmd.shop_code )")
                    .append(" else (select staff_name from vds_staff where staff_code = csmd.staff_code ) end ten_dv_nv ,")
                    .append(" lk thuc_hien,kh_lk ke_hoach, round(100*lk/kh_lk,2) ty_le")
                    .append(" From chart_service_measure_daily csmd")
                    .append(" where prd_id = :prdId and service_id = :serviceId");
            if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
                vsbdSql.append(" and ( (staff_code is null and (vds_channel_code, shop_code)")
                        .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                        .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                        .append(" )")
                        .append(" or (staff_code is not null and csmd.vds_channel_code = :vdsChannelCode and csmd.shop_code = :shopCode)")
                        .append(" )");
            } else {
                vsbdSql.append(" and staff_code is not null");
                vsbdSql.append(" and (csmd.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null)");
            }
            vsbdSql.append(" and (")
                    .append(" ( :pRow = '0' and 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" )")
                    .append(" OR (:pRow = '1' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" )")
                    .append(" OR (:pRow = '2' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" OR (:pRow ='3'  and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" )")
                    .append(" order by lk/kh_lk");
        }
        //CHU KY THANG
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSql.append("select case when staff_code is null")
                    .append(" then ( select shop_name from manage_info_partner")
                    .append(" where vds_channel_code = csmd.vds_channel_code")
                    .append(" and shop_code = csmd.shop_code )")
                    .append(" else (select staff_name from vds_staff where staff_code = csmd.staff_code ) end ten_dv_nv ,")
                    .append(" lk thuc_hien,kh_lk ke_hoach, round(100*lk/kh_lk,2) ty_le")
                    .append(" From chart_service_measure_monthly csmd")
                    .append(" where prd_id = :prdId and service_id = :serviceId");
            if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
                vsbdSql.append(" and ( (staff_code is null and (vds_channel_code, shop_code)")
                        .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                        .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                        .append(" )")
                        .append(" or (staff_code is not null and csmd.vds_channel_code = :vdsChannelCode and csmd.shop_code = :shopCode)")
                        .append(" )");
            } else {
                vsbdSql.append(" and staff_code is not null");
                vsbdSql.append(" and (csmd.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null)");
            }
                    vsbdSql.append(" and (")
                    .append(" ( :pRow = '0' and 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" )")
                    .append(" OR (:pRow = '1' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" )")
                    .append(" OR (:pRow = '2' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" OR (:pRow ='3'  and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" )")
                    .append(" order by lk/kh_lk");
        }
        //CHU KY QUY
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSql.append("select case when staff_code is null")
                    .append(" then ( select shop_name from manage_info_partner")
                    .append(" where vds_channel_code = csmd.vds_channel_code")
                    .append(" and shop_code = csmd.shop_code )")
                    .append(" else (select staff_name from vds_staff where staff_code = csmd.staff_code ) end ten_dv_nv ,")
                    .append(" lk thuc_hien,kh_lk ke_hoach, round(100*lk/kh_lk,2) ty_le")
                    .append(" From chart_service_measure_quaterly csmd")
                    .append(" where prd_id = :prdId and service_id = :serviceId");
            if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
                vsbdSql.append(" and ( (staff_code is null and (vds_channel_code, shop_code)")
                        .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                        .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                        .append(" )")
                        .append(" or (staff_code is not null and csmd.vds_channel_code = :vdsChannelCode and csmd.shop_code = :shopCode)")
                        .append(" )");
            } else {
                vsbdSql.append(" and staff_code is not null");
                vsbdSql.append(" and (csmd.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null)");
            }
                    vsbdSql.append(" and (")
                    .append(" ( :pRow = '0' and 100*lk/kh_lk< (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" )")
                    .append(" OR (:pRow = '1' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '1')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" )")
                    .append(" OR (:pRow = '2' and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '2')")
                    .append(" and 100*lk/kh_lk < (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" OR (:pRow ='3'  and 100*lk/kh_lk >= (select value from ap_param where type = 'COMPLETION_RATE' and code = '3')")
                    .append(" )")
                    .append(" )")
                    .append(" order by lk/kh_lk");
        }

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
            query.setParameter("prdId", vlngPrdId);
            query.setParameter("serviceId", vlngServiceId);
            query.setParameter("vdsChannelCode", vstrVdsChannelCode);
            query.setParameter("shopCode", vstrShopCode);
            query.setParameter("pRow", vstrPRow);
        } else {
            query.setParameter("prdId", vlngPrdId);
            query.setParameter("serviceId", vlngServiceId);
            query.setParameter("pRow", vstrPRow);
            query.setParameter("vdsChannelCode", vstrVdsChannelCode);
        }
        List<Object[]> vlstData = query.getResultList();

        return vlstData;
    }

    /**
     * diem danh gia don vi/nhan vien lv1
     *
     * @param dashboardRequestDTO
     * @param configSingleChartDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/05
     */
    @Override
    public Object[] getEvaluatePoint(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        String vstrVdsChannelCode = dashboardRequestDTO.getVdsChannelCode();
        String vstrShopCode = dashboardRequestDTO.getObjectCode();
        String vstrMonth = "1";
        Long vlngPrdId = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date vDate = new Date(dashboardRequestDTO.getPrdId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vDate);
        String vstrNationalStaff = "0";
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(configSingleChartDTO.getQueryParam());
        JsonArray params = null;
        JsonObject queryParam;
        if (!DataUtil.isNullObject(jsonObject))
            params = (JsonArray) jsonObject.get("params");
        if (params != null) {
            if (null != params.get(0)) {
                queryParam = (JsonObject) params.get(0);
                vstrMonth = queryParam.get("month").getAsString();
            }
        }

        //convert millisecond ve prd_id
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
        }

//        String vstrPrdCondition = "prd_id = " + vlngPrdId;

        //build dieu kien voi prd_id
//        int vintCount = Integer.parseInt(vstrMonth);
//        for (int i = 1; i < vintCount; i++) {
//            if (vintCycleId == Constants.CYCLE_ID.DAY) {
//                cal.add(Calendar.MONTH, -1);
//                java.util.Date vDateUtilAfter = cal.getTime();
//                Date vDateSqlAfter = new Date(vDateUtilAfter.getTime());
//                Long vlngPrdIdAfter = Long.valueOf(dateFormat.format(vDateSqlAfter));
//                vstrPrdCondition = vstrPrdCondition + " or prd_id = " + vlngPrdIdAfter;
//                cal.setTime(vDateSqlAfter);
//            }
//            if (vintCycleId == Constants.CYCLE_ID.MONTH) {
//                cal.add(Calendar.MONTH, -1);
//                cal.set(Calendar.DAY_OF_MONTH, 1);
//                java.util.Date vDateUtilAfter = cal.getTime();
//                Date vDateSqlAfter = new Date(vDateUtilAfter.getTime());
//                Long vlngPrdIdAfter = Long.valueOf(dateFormat.format(vDateSqlAfter));
//                vstrPrdCondition = vstrPrdCondition + " or prd_id = " + vlngPrdIdAfter;
//                cal.setTime(vDateSqlAfter);
//            }
//            if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
//                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
//                cal.set(Calendar.DAY_OF_MONTH, 1);
//                cal.add(Calendar.MONTH, -3);
//                java.util.Date vDateUtilAfter = cal.getTime();
//                Date vDateSqlAfter = new Date(vDateUtilAfter.getTime());
//                Long vlngPrdIdAfter = Long.valueOf(dateFormat.format(vDateSqlAfter));
//                vstrPrdCondition = vstrPrdCondition + " or prd_id = " + vlngPrdIdAfter;
//                cal.setTime(vDateSqlAfter);
//            }
//        }

        vsbdSql.append("select sum(a.duoi_muc_1) duoi_muc_1,")
                .append(" sum(a.tren_muc_1_duoi_muc_2) tren_muc_1_duoi_muc_2,")
                .append(" sum(a.tren_muc_2_duoi_muc_3) tren_muc_2_duoi_muc_3,")
                .append(" sum(a.tren_muc_3_duoi_muc_4) tren_muc_3_duoi_muc_4,")
                .append(" sum(a.tren_muc_4) tren_muc_4 ")
                .append(" from")
                .append(" (");
        if (Constants.CODE_WARNING.ONE.equals(vstrMonth)) {
            vsbdSql.append(" select")
                    .append(" case when (100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='1') then 1 else 0 end duoi_muc_1,")
                    .append(" case when (100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='1')")
                    .append(" and (100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='2') then 1 else 0 end tren_muc_1_duoi_muc_2,")
                    .append(" case when (100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='2')")
                    .append(" and (100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='3') then 1 else 0 end tren_muc_2_duoi_muc_3,")
                    .append(" case when (100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='3')")
                    .append(" and (100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='4') then 1 else 0 end tren_muc_3_duoi_muc_4,")
                    .append(" case when (100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='4') then 1 else 0 end tren_muc_4 ");
        }
        if (Constants.CODE_WARNING.TWO.equals(vstrMonth)) {
            vsbdSql.append("select\n")
                    .append(" case when (100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='1') \n")
                    .append(" AND (100*score_n1/score_max_n1)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='1') then 1 else 0 end duoi_muc_1,\n")
                    .append(" case when ((100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='1')\n")
                    .append(" AND (100*score_n1/score_max_n1)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='1'))\n")
                    .append(" AND ((100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='2') \n")
                    .append(" AND (100*score_n1/score_max_n1)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='2')) then 1 else 0 end tren_muc_1_duoi_muc_2,\n")
                    .append(" case when ((100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='2')\n")
                    .append(" AND (100*score_n1/score_max_n1)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='2'))\n")
                    .append(" AND ((100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='3') \n")
                    .append(" AND (100*score_n1/score_max_n1)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='3')) then 1 else 0 end tren_muc_2_duoi_muc_3,\n")
                    .append(" case when ((100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='3') \n")
                    .append(" AND (100*score_n1/score_max_n1)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='3'))\n")
                    .append(" AND ((100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='4') \n")
                    .append(" AND (100*score_n1/score_max_n1)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='4')) then 1 else 0 end tren_muc_3_duoi_muc_4,\n")
                    .append(" case when (100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='4') \n")
                    .append(" AND (100*score_n1/score_max_n1)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='4') then 1 else 0 end tren_muc_4 ");
        }
        if (Constants.CODE_WARNING.THREE.equals(vstrMonth)) {
            vsbdSql.append("select          \n")
                    .append(" case when (100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='1') \n")
                    .append(" AND (100*score_n1/score_max_n1)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='1') \n")
                    .append(" AND (100*score_n2/score_max_n2)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='1') then 1 else 0 end duoi_muc_1,\n")
                    .append(" case when ((100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='1')\n")
                    .append(" AND (100*score_n1/score_max_n1)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='1') \n")
                    .append(" AND (100*score_n2/score_max_n2)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='1'))\n")
                    .append(" AND ((100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='2') \n")
                    .append(" AND (100*score_n1/score_max_n1)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='2')\n")
                    .append(" AND (100*score_n2/score_max_n2)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='2')) then 1 else 0 end tren_muc_1_duoi_muc_2,\n")
                    .append(" case when ((100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='2')\n")
                    .append(" AND (100*score_n1/score_max_n1)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='2')\n")
                    .append(" AND (100*score_n2/score_max_n2)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='2'))\n")
                    .append(" AND ((100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='3') \n")
                    .append(" AND (100*score_n1/score_max_n1)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='3')\n")
                    .append(" AND (100*score_n2/score_max_n2)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='3')) then 1 else 0 end tren_muc_2_duoi_muc_3,\n")
                    .append(" case when ((100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='3') \n")
                    .append(" AND (100*score_n1/score_max_n1)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='3')\n")
                    .append(" AND (100*score_n2/score_max_n2)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='3'))\n")
                    .append(" AND ((100*score/score_max)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='4') \n")
                    .append(" AND (100*score_n1/score_max_n1)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='4')\n")
                    .append(" AND (100*score_n2/score_max_n2)<(select value from ap_param where type = 'EVALUATED_RATE' and code ='4')) then 1 else 0 end tren_muc_3_duoi_muc_4,\n")
                    .append(" case when (100*score/score_max)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='4') \n")
                    .append(" AND (100*score_n1/score_max_n1)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='4') \n")
                    .append(" AND (100*score_n2/score_max_n2)>=(select value from ap_param where type = 'EVALUATED_RATE' and code ='4') then 1 else 0 end tren_muc_4");
        }
        if (vintCycleId == Constants.CYCLE_ID.DAY) {
            vsbdSql.append(" From vds_score_ranking_daily csmd ");
        }
        if (vintCycleId == Constants.CYCLE_ID.MONTH) {
            vsbdSql.append(" From vds_score_ranking_monthly csmd ");
        }
        if (vintCycleId == Constants.CYCLE_ID.QUARTER) {
            vsbdSql.append(" From vds_score_ranking_quaterly csmd ");
        }
//        vsbdSql.append(" where (").append(vstrPrdCondition).append(")");
        vsbdSql.append(" where prd_id = :prdId");
        if (vstrNationalStaff.equals(Constants.PARAM_STATUS_0)) {
            vsbdSql.append(" and (")
                    .append(" ((kenh, don_vi)")
                    .append(" in (select vds_channel_code,shop_code from manage_info_partner mip")
                    .append(" where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )")
                    .append(" and nhan_vien is null )")
                    .append(" OR (kenh = :vdsChannelCode and don_vi = :shopCode")
                    .append(" and nhan_vien is not null )")
                    .append(" )");
        } else {
            vsbdSql.append(" and kenh = :vdsChannelCode");
            vsbdSql.append(" and nhan_vien is not null");
        }
        vsbdSql.append(" ) a");


        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("prdId", vlngPrdId);
        query.setParameter("vdsChannelCode", vstrVdsChannelCode);
        if (vstrNationalStaff.equals(Constants.PARAM_STATUS_0)) {
            query.setParameter("shopCode", vstrShopCode);
        }
        Object[] data = (Object[]) query.getResultList().get(0);

        return data;
    }

}
