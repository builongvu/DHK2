package vn.vissoft.dashboard.repo.impl;

import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.BusinessResultsRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class BusinessResultsRepoImpl implements BusinessResultsRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * lay ra du lieu bao cao theo tinh
     *
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/07
     */
    @Override
    public List<Object[]> findResultProvincial(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        String vstrChannelCode = dashboardRequestDTO.getVdsChannelCode();
//        Long vlngPrdId = DataUtil.convertToPrdIdEndMonth(dashboardRequestDTO.getMonthYear());
        Long vlngPrdId = Long.valueOf(dashboardRequestDTO.getMonthYear());

        vsbdSql.append("SELECT province_code,MAX(round(score,2))score,MAX(rank) rank,\n")
                .append("\tMAX( tbtt_kh)tbtt_kh,MAX( tbtt_lk_n1)tbtt_lk_n1,MAX( tbtt_lk_n)tbtt_lk_n,MAX( tbtt_delta)tbtt_delta,MAX( round(tbtt_ht,2))tbtt_ht,MAX( round(tbtt_diem,2))tbtt_diem,\n")
                .append("\tMAX( tbm_kh)tbm_kh, MAX( tbm_th)tbm_th, MAX( round(tbm_ht,2))tbm_ht, MAX( round(tbm_diem,2))tbm_diem,\n")
                .append("\tMAX( tbrm_kh)tbrm_kh, MAX( tbrm_th)tbrm_th, MAX( round(tbrm_ht,2))tbrm_ht, MAX( round(tbrm_diem,2)) tbrm_diem\n")
                .append("FROM(\n")
                .append("SELECT b.province_code,round(a.score) score,a.rank ,\n")
                .append("\tNULL tbtt_kh,null tbtt_lk_n1,NULL tbtt_lk_n,NULL tbtt_delta,NULL tbtt_ht,NULL tbtt_diem,\n")
                .append("\tNULL tbm_kh, NULL tbm_th, NULL tbm_ht, NULL tbm_diem,\n")
                .append("\tNULL tbrm_kh, NULL tbrm_th, NULL tbrm_ht, NULL tbrm_diem \n")
                .append("FROM vds_score_ranking_daily a, manage_info_partner b\n")
                .append("WHERE a.prd_id = :prdId\n")
                .append("\tAND a.nhan_vien IS NULL\n")
                .append("\tAND a.kenh = :vdsChannelCode\n")
                .append("\tAND b.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND a.don_vi = b.shop_code\n")
                .append("\tAND b.shop_type = 1\n")
                .append("UNION ALL\n")
                .append("SELECT b.province_code, NULL score,NULL rank,\n")
                .append("\tkh_thang tbtt_kh,null tbtt_lk_n1,null tbtt_lk_n,lk tbtt_delta,ty_le_ht_lk tbtt_ht, c.SCORE  tbtt_diem,\n")
                .append("\tNULL tbm_kh, NULL tbm_th, NULL tbm_ht, NULL tbm_diem,\n")
                .append("\tNULL tbrm_kh, NULL tbrm_th, NULL tbrm_ht, NULL tbrm_diem \n")
                .append("FROM \tchart_service_measure_daily a,manage_info_partner b,vds_score_service_daily c\n")
                .append("WHERE a.prd_id = :prdId\n")
                .append("\tAND c.PRD_ID = :prdId\n")
                .append("\tAND c.SERVICE_ID = 3\n")
                .append("\tAND c.NHAN_VIEN IS null\n")
                .append("\tAND c.KENH = :vdsChannelCode\n")
                .append("\tAND a.shop_code = c.DON_VI\n")
                .append("\tAND a.staff_code IS NULL\n")
                .append("\tAND a.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND b.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND a.shop_code = b.shop_code\n")
                .append("\tAND b.shop_type = 1\n")
                .append("\tAND a.service_id = 3\n")
                .append("\n")
                .append("UNION ALL\n")
                .append("\n")
                .append("SELECT b.province_code, NULL score,NULL rank,\n")
                .append("\tnull tbtt_kh,lk_thang_n1 tbtt_lk_n1,lk tbtt_lk_n,null tbtt_delta,null tbtt_ht,NULL tbtt_diem,\n")
                .append("\tNULL tbm_kh, NULL tbm_th, NULL tbm_ht, NULL tbm_diem,\n")
                .append("\tNULL tbrm_kh, NULL tbrm_th, NULL tbrm_ht, NULL tbrm_diem \n")
                .append("FROM \tchart_service_measure_daily a,manage_info_partner b\n")
                .append("WHERE a.prd_id = :prdId\n")
                .append("\tAND a.staff_code IS NULL\n")
                .append("\tAND a.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND b.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND a.shop_code = b.shop_code\n")
                .append("\tAND b.shop_type = 1\n")
                .append("\tAND a.service_id = 17\n")
                .append("\n")
                .append("UNION ALL\n")
                .append("\n")
                .append("SELECT b.province_code, NULL score,NULL rank,\n")
                .append("\tnull tbtt_kh,null tbtt_lk_n1,null tbtt_lk_n,null tbtt_delta,null tbtt_ht,NULL tbtt_diem,\n")
                .append("\tkh_thang tbm_kh, lk tbm_th, ty_le_ht_lk tbm_ht,  c.SCORE   tbm_diem,\n")
                .append("\tNULL tbrm_kh, NULL tbrm_th, NULL tbrm_ht, NULL tbrm_diem \n")
                .append("FROM \tchart_service_measure_daily a,manage_info_partner b,vds_score_service_daily c\n")
                .append("WHERE a.prd_id = :prdId\n")
                .append("\tAND c.PRD_ID = :prdId\n")
                .append("\tAND c.SERVICE_ID = 4\n")
                .append("\tAND c.NHAN_VIEN IS null\n")
                .append("\tAND c.KENH = :vdsChannelCode\n")
                .append("\tAND a.shop_code = c.DON_VI\n")
                .append("\tAND a.staff_code IS NULL\n")
                .append("\tAND a.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND b.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND a.shop_code = b.shop_code\n")
                .append("\tAND b.shop_type = 1\n")
                .append("\tAND a.service_id = 4  \n")
                .append("\n")
                .append("UNION ALL\n")
                .append("\n")
                .append("SELECT b.province_code, NULL score,NULL rank,\n")
                .append("\tnull tbtt_kh,null tbtt_lk_n1,null tbtt_lk_n,null tbtt_delta,null tbtt_ht,NULL tbtt_diem,\n")
                .append("\tNULL tbm_kh, NULL tbm_th, NULL tbm_ht, NULL tbm_diem,\n")
                .append("\tkh_thang tbrm_kh, lk tbrm_th, a.ty_le_ht tbrm_ht,  c.SCORE  tbrm_diem \n")
                .append("FROM \tchart_service_measure_daily a,manage_info_partner b,vds_score_service_daily c\n")
                .append("WHERE a.prd_id = :prdId\n")
                .append("\tAND c.PRD_ID = :prdId\n")
                .append("\tAND c.SERVICE_ID = 37\n")
                .append("\tAND c.NHAN_VIEN IS null\n")
                .append("\tAND c.KENH = :vdsChannelCode\n")
                .append("\tAND a.shop_code = c.DON_VI\n")
                .append("\tAND a.staff_code IS NULL\n")
                .append("\tAND a.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND b.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND a.shop_code = b.shop_code\n")
                .append("\tAND b.shop_type = 1\n")
                .append("\tAND a.service_id = 37) a\n")
                .append("GROUP BY province_code\n")
                .append("ORDER BY province_code");

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("prdId", vlngPrdId);
        query.setParameter("vdsChannelCode", vstrChannelCode);
        List<Object[]> vlstDatas = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstDatas)) {
            return null;
        } else {
            return vlstDatas; 
        }
    }

    /**
     * lay ra du lieu bao cao theo nhan vien
     *
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/07
     */
    @Override
    public List<Object[]> findResultStaff(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        String vstrChannelCode = dashboardRequestDTO.getVdsChannelCode();
        Long vlngPrdId = Long.valueOf(dashboardRequestDTO.getMonthYear());
//        Long vlngPrdId = DataUtil.convertToPrdIdEndMonth(dashboardRequestDTO.getMonthYear());

        vsbdSql.append("SELECT province_code, don_vi, nhan_vien,\n")
                .append("\t(SELECT staff_name FROM vds_staff WHERE staff_code = a.nhan_vien AND vds_channel_code ='VDS_TINH' AND shop_code = a.don_vi) staff_name,\n")
                .append("\tMAX(round(score,2))score,MAX(rank) rank,\n")
                .append("\tMAX( tbtt_kh)tbtt_kh,MAX( tbtt_lk_n1)tbtt_lk_n1,MAX( tbtt_lk_n)tbtt_lk_n,MAX( tbtt_delta)tbtt_delta,MAX( round(tbtt_ht,2))tbtt_ht,MAX( round(tbtt_diem,2))tbtt_diem,\n")
                .append("\tMAX( tbm_kh)tbm_kh, MAX( tbm_th)tbm_th, MAX( round(tbm_ht,2))tbm_ht, MAX( round(tbm_diem,2))tbm_diem,\n")
                .append("\tMAX( tbrm_kh)tbrm_kh, MAX( tbrm_th)tbrm_th, MAX( round(tbrm_ht,2))tbrm_ht, MAX( round(tbrm_diem,2)) tbrm_diem\n")
                .append("FROM(\n")
                .append("SELECT b.province_code,a.don_vi,a.nhan_vien, (a.score) score,a.rank ,\n")
                .append("\tNULL tbtt_kh,null tbtt_lk_n1,NULL tbtt_lk_n,NULL tbtt_delta,NULL tbtt_ht,NULL tbtt_diem,\n")
                .append("\tNULL tbm_kh, NULL tbm_th, NULL tbm_ht, NULL tbm_diem,\n")
                .append("\tNULL tbrm_kh, NULL tbrm_th, NULL tbrm_ht, NULL tbrm_diem \n")
                .append("FROM vds_score_ranking_daily a, manage_info_partner b\n")
                .append("WHERE a.prd_id = :prdId\n")
                .append("\tAND a.nhan_vien IS NOT NULL\n")
                .append("\tAND a.kenh = :vdsChannelCode\n")
                .append("\tAND b.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND a.don_vi = b.shop_code\n")
                .append("\tAND b.shop_type =1\n")
                .append("\tAND a.nhan_vien IN (SELECT staff_code from vds_staff where vds_channel_code = 'VDS_TINH' AND shop_code = a.don_vi)\n")
                .append("UNION ALL\n")
                .append("SELECT b.province_code,a.shop_code,a.staff_code, NULL score,NULL rank,\n")
                .append("\tkh_thang tbtt_kh,null tbtt_lk_n1,null tbtt_lk_n,lk tbtt_delta,ty_le_ht_lk tbtt_ht, c.SCORE  tbtt_diem,\n")
                .append("\tNULL tbm_kh, NULL tbm_th, NULL tbm_ht, NULL tbm_diem,\n")
                .append("\tNULL tbrm_kh, NULL tbrm_th, NULL tbrm_ht, NULL tbrm_diem \n")
                .append("FROM \tchart_service_measure_daily a,manage_info_partner b,vds_score_service_daily c\n")
                .append("WHERE a.prd_id = :prdId\n")
                .append("\tAND c.PRD_ID = :prdId\n")
                .append("\tAND c.SERVICE_ID = 3\n")
                .append("\tAND c.KENH = :vdsChannelCode\n")
                .append("\tAND a.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND b.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND a.shop_code = c.DON_VI\n")
                .append("\tAND a.staff_code = c.nhan_vien\n")
                .append("\tAND a.shop_code = b.shop_code\n")
                .append("\tAND b.shop_type =1\n")
                .append("\tAND a.service_id = 3\n")
                .append("\tAND a.staff_code IN (SELECT staff_code from vds_staff where vds_channel_code ='VDS_TINH' AND shop_code = a.shop_code)\n")
                .append("\n")
                .append("UNION ALL\n")
                .append("\n")
                .append("SELECT b.province_code,a.shop_code,a.staff_code, NULL score,NULL rank,\n")
                .append("\tnull tbtt_kh,lk_thang_n1 tbtt_lk_n1,lk tbtt_lk_n,null tbtt_delta,null tbtt_ht,NULL tbtt_diem,\n")
                .append("\tNULL tbm_kh, NULL tbm_th, NULL tbm_ht, NULL tbm_diem,\n")
                .append("\tNULL tbrm_kh, NULL tbrm_th, NULL tbrm_ht, NULL tbrm_diem \n")
                .append("FROM \tchart_service_measure_daily a,manage_info_partner b\n")
                .append("WHERE a.prd_id = :prdId\n")
                .append("\tAND a.staff_code IS not NULL\n")
                .append("\tAND a.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND b.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND a.shop_code = b.shop_code\n")
                .append("\tAND b.shop_type =1\n")
                .append("\tAND a.service_id = 17\n")
                .append("\tAND a.staff_code IN (SELECT staff_code from vds_staff where vds_channel_code ='VDS_TINH' AND shop_code = a.shop_code)\n")
                .append("\n")
                .append("UNION ALL\n")
                .append("\n")
                .append("SELECT b.province_code,a.shop_code,a.staff_code, NULL score,NULL rank,\n")
                .append("\tnull tbtt_kh,null tbtt_lk_n1,null tbtt_lk_n,null tbtt_delta,null tbtt_ht,NULL tbtt_diem,\n")
                .append("\tkh_thang tbm_kh, lk tbm_th, ty_le_ht_lk tbm_ht,  c.SCORE   tbm_diem,\n")
                .append("\tNULL tbrm_kh, NULL tbrm_th, NULL tbrm_ht, NULL tbrm_diem \n")
                .append("FROM \tchart_service_measure_daily a,manage_info_partner b,vds_score_service_daily c\n")
                .append("WHERE a.prd_id = :prdId\n")
                .append("\tAND c.PRD_ID = :prdId\n")
                .append("\tAND c.SERVICE_ID = 4\n")
                .append("\tAND c.KENH = :vdsChannelCode\n")
                .append("\tAND a.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND b.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND a.shop_code = c.DON_VI\n")
                .append("\tAND a.staff_code =c.NHAN_VIEN\n")
                .append("\tAND a.shop_code = b.shop_code\n")
                .append("\tAND b.shop_type =1\n")
                .append("\tAND a.service_id = 4  \n")
                .append("\tAND a.staff_code IN (SELECT staff_code from vds_staff where vds_channel_code ='VDS_TINH' AND shop_code = a.shop_code) \n")
                .append("\n")
                .append("UNION ALL\n")
                .append("\n")
                .append("SELECT b.province_code,a.shop_code,a.staff_code, NULL score,NULL rank,\n")
                .append("\tnull tbtt_kh,null tbtt_lk_n1,null tbtt_lk_n,null tbtt_delta,null tbtt_ht,NULL tbtt_diem,\n")
                .append("\tNULL tbm_kh, NULL tbm_th, NULL tbm_ht, NULL tbm_diem,\n")
                .append("\tkh_thang tbrm_kh, lk tbrm_th, a.ty_le_ht tbrm_ht,  c.SCORE  tbrm_diem \n")
                .append("FROM \tchart_service_measure_daily a,manage_info_partner b,vds_score_service_daily c\n")
                .append("WHERE a.prd_id = :prdId\n")
                .append("\tAND c.PRD_ID = :prdId\n")
                .append("\tAND c.SERVICE_ID = 37\n")
                .append("\tAND c.KENH = :vdsChannelCode\n")
                .append("\tAND a.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND b.vds_channel_code = :vdsChannelCode\n")
                .append("\tAND a.shop_code = c.DON_VI\n")
                .append("\tAND a.staff_code = c.NHAN_VIEN\n")
                .append("\tAND a.shop_code = b.shop_code\n")
                .append("\tAND b.shop_type =1\n")
                .append("\tAND a.service_id = 37\n")
                .append("\tAND a.staff_code IN (SELECT staff_code from vds_staff where vds_channel_code ='VDS_TINH' AND shop_code = a.shop_code)) a\n")
                .append("GROUP BY province_code,don_vi, nhan_vien\n")
                .append("ORDER BY province_code,nhan_vien");

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("prdId", vlngPrdId);
        query.setParameter("vdsChannelCode", vstrChannelCode);
        List<Object[]> vlstDatas = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstDatas)) {
            return null;
        } else {
            return vlstDatas;
        }
    }

    /**
     * lay ra diem theo 3 chi tieu (bao cao)
     *
     * @param dashboardRequestDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/07
     */
    @Override
    public List<Object[]> findServiceScore(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
//        String monthYear = dashboardRequestDTO.getMonthYear();
//        String vstrPrdId = monthYear.substring(2, 6) + monthYear.substring(0, 2) + "01";
//        Long vlngPrdId = Long.valueOf(vstrPrdId);
        Long vlngPrdId = Long.valueOf(dashboardRequestDTO.getMonthYear());

        vsbdSql.append("SELECT MAX(tbtt_diem) tbtt_diem,MAX(tblk_diem)tblk_diem,MAX(tlrm_diem)tlrm_diem \n")
                .append("FROM (\n")
                .append("SELECT vds_channel_code,shop_code,staff_code,service_id,\n")
                .append("case when service_id = 3 then 100*score else 0 end tbtt_diem,\n")
                .append("case when service_id = 4 then 100*score else 0 end tblk_diem,\n")
                .append("case when service_id = 37 then 100*score else 0 end tlrm_diem\n")
                .append(" FROM service_score \n")
                .append("WHERE service_id IN (3,4,37) \n")
                .append("\tAND vds_channel_code = 'VDS_TINH' \n")
                .append("\tAND STATUS = 1 \n")
                .append("\tAND from_date <= STR_TO_DATE(:prdId,'%Y%m%d')\n")
                .append("\tAND ifnull(to_date,CURDATE()) >= STR_TO_DATE(:prdId,'%Y%m%d')) a\n")
                .append("GROUP BY vds_channel_code,shop_code,staff_code LIMIT 1");

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("prdId", vlngPrdId);
        List<Object[]> vlstDatas = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstDatas)) {
            return null;
        } else {
            return vlstDatas;
        }
    }

}
