package vn.vissoft.dashboard.repo.impl;

import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.VdsScoreServiceRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;

@Repository
public class VdsScoreServiceRepoCustomImpl implements VdsScoreServiceRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * du lieu cua bang chi tiet khi tai file excel
     *
     * @param dashboardRequestDTO
     * @return chuoi dieu kien
     * @throws Exception
     * @author hungNN
     * @version 1.0
     * @since 16/12/2019
     */
    @Override
    public List<Object[]> getDataDetail(DashboardRequestDTO dashboardRequestDTO) {
        StringBuilder sqlBuilder = new StringBuilder();
        HashMap hashMap = new HashMap();
        int cycle = dashboardRequestDTO.getCycleId();
        String vstrNationalStaff = "0";
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }

        if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
            sqlBuilder.append("select vssd.id,vssd.NHAN_VIEN,mip.short_name,vs.SCORE,vs.SCORE_MAX,vs.rank,vs.score_n1,vs.score_max_n1,vs.rank_n1, s.name,vssd.SERVICE_ID,vssd.SCORE as detail_score, vssd.DON_VI, vs.score_n2, vs.score_max_n2, vs.rank_n2, round(vssd.TH, 2), round(vssd.ty_le_ht, 2), 'a'");
            sqlBuilder.append(" from service s, manage_info_partner mip,");
//            int cycle = dashboardRequestDTO.getCycleId();
            switch (cycle) {
                case 1:
                    sqlBuilder.append("vds_score_service_daily vssd,vds_score_ranking_daily vs");
                    break;
                case 2:
                    sqlBuilder.append("vds_score_service_monthly vssd,vds_score_ranking_monthly vs");
                    break;
                case 3:
                    sqlBuilder.append("vds_score_service_quaterly vssd,vds_score_ranking_quaterly vs");
                    break;
            }
            sqlBuilder.append(conditionSQL(dashboardRequestDTO, hashMap));
            sqlBuilder.append(" union all");
        sqlBuilder.append(" select vs.id,s.staff_code,s.staff_name, v.score,v.score_max,v.rank,v.score_n1,v.score_max_n1,v.rank_n1,ss.name,vs.service_id,vs.score as detail_score, vs.don_vi, v.score_n2, v.score_max_n2, v.rank_n2, round(vs.TH, 2), round(vs.ty_le_ht, 2), 'a' ");
        sqlBuilder.append(" from service ss,vds_staff s,");
            switch (cycle) {
                case 1:
                    sqlBuilder.append("vds_score_service_daily vs, vds_score_ranking_daily v");
                    break;
                case 2:
                    sqlBuilder.append("vds_score_service_monthly vs, vds_score_ranking_monthly v");
                    break;
                case 3:
                    sqlBuilder.append("vds_score_service_quaterly vs, vds_score_ranking_quaterly v");
                    break;
            }
            sqlBuilder.append(buildSQL(dashboardRequestDTO, hashMap));
            String isMax = dashboardRequestDTO.getSorted();
            if (isMax.equalsIgnoreCase(Constants.DESC)) {
                sqlBuilder.append(" order by score desc");
            } else {
                sqlBuilder.append(" order by score");
            }
        } else {
            sqlBuilder.append(" select vs.id,s.staff_code,s.staff_name as staff_name, v.score,v.score_max,v.rank_all_staff,v.score_n1,v.score_max_n1,v.rank_all_staff_n1,ss.name as service_name,vs.service_id,vs.score as detail_score, vs.don_vi, v.score_n2, v.score_max_n2, v.rank_all_staff_n2, round(vs.TH, 2), round(vs.ty_le_ht, 2), (select short_name from manage_info_partner where shop_code = v.don_vi) ");
            sqlBuilder.append(" from service ss,vds_staff s,");
            switch (cycle) {
                case 1:
                    sqlBuilder.append("vds_score_service_daily vs, vds_score_ranking_daily v");
                    break;
                case 2:
                    sqlBuilder.append("vds_score_service_monthly vs, vds_score_ranking_monthly v");
                    break;
                case 3:
                    sqlBuilder.append("vds_score_service_quaterly vs, vds_score_ranking_quaterly v");
                    break;
            }
            sqlBuilder.append(buildSQL(dashboardRequestDTO, hashMap));
            String isMax = dashboardRequestDTO.getSorted();
            if (isMax.equalsIgnoreCase(Constants.DESC)) {
                sqlBuilder.append(" order by score desc");
            } else {
                sqlBuilder.append(" order by score");
            }
        }

        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        hashMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();
    }

    /**
     * dieu kien theo prd_id va shop_code truyen vao
     *
     * @param dashboardRequestDTO
     * @return chuoi dieu kien
     * @throws Exception
     * @author hungNN
     * @version 1.0
     * @since 16/12/2019
     */
    public StringBuilder conditionSQL(DashboardRequestDTO dashboardRequestDTO, HashMap phmpParams){
        StringBuilder vsbdStringBuilder = new StringBuilder();
        vsbdStringBuilder.append(" where 1 = 1 ");
        vsbdStringBuilder.append(" and vssd.NHAN_VIEN is null");
        vsbdStringBuilder.append(" and vssd.DON_VI = vs.don_vi");
        vsbdStringBuilder.append(" and ifnull(vssd.NHAN_VIEN,'a') = ifnull(vs.nhan_vien,'a')");
        vsbdStringBuilder.append(" and ifnull(vssd.KENH,'a') = ifnull(vs.kenh,'a')");
        vsbdStringBuilder.append(" and vssd.SERVICE_ID = s.id");
        vsbdStringBuilder.append(" and vs.don_vi = mip.shop_code");
        vsbdStringBuilder.append(" and ifnull(vs.KENH,'a') = ifnull(mip.vds_channel_code,'a')");
        if (!DataUtil.isNullOrZero(dashboardRequestDTO.getPrdId())) {
            Long vlngPrdId = 0L;
            if (dashboardRequestDTO.getCycleId() != 0) {
                if (dashboardRequestDTO.getCycleId() == 1)
                    vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());   //lay prdId theo ngay
                else if (dashboardRequestDTO.getCycleId() == 2)
                    vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());   //lay prdId theo thang
                else if (dashboardRequestDTO.getCycleId() == 3)
                    vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());       //lay prdId theo quy
                vsbdStringBuilder.append(" and vs.prd_id = :prdId");
                vsbdStringBuilder.append(" and vssd.PRD_ID = :prdId");
                phmpParams.put("prdId", vlngPrdId);
            }
        }
        vsbdStringBuilder.append(" and mip.parent_shop_code = :shopCode");
        if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode())) {
            phmpParams.put("shopCode", dashboardRequestDTO.getObjectCode());
        } else {
            phmpParams.put("shopCode", dashboardRequestDTO.getParentShopCode());
        }
        if(!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())){
            vsbdStringBuilder.append(" and (mip.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null)");
            phmpParams.put("vdsChannelCode",dashboardRequestDTO.getVdsChannelCode());
        }
        return vsbdStringBuilder;
    }

    /**
     * dieu kien truyen vao cua top ben trai theo ngay
     *
     * @param dashboardRequestDTO, param, staffDTO
     * @return chuoi dieu kien
     * @throws Exception
     * @author hungNN
     * @version 1.0
     * @since 28/10/2019
     */
    public StringBuilder buildSQL(DashboardRequestDTO dashboardRequestDTO, HashMap phmpParams){
        StringBuilder vsbdStringBuilder = new StringBuilder();
        String vstrNationalStaff = "0";
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }
        vsbdStringBuilder.append(" where 1 = 1 ");
        vsbdStringBuilder.append(" and vs.don_vi = s.shop_code");
        vsbdStringBuilder.append(" and vs.nhan_vien = s.staff_code");
        vsbdStringBuilder.append(" and s.staff_type ='1'");
        vsbdStringBuilder.append(" and ifnull(vs.nhan_vien,'a') = ifnull(v.nhan_vien,'a')");
        vsbdStringBuilder.append(" and v.nhan_vien is not null");
        vsbdStringBuilder.append(" and ifnull(v.kenh,'a') = ifnull(vs.kenh,'a')");
        vsbdStringBuilder.append(" and vs.service_id = ss.id");
        vsbdStringBuilder.append(" and v.don_vi = vs.don_vi");
        if (!DataUtil.isNullOrZero(dashboardRequestDTO.getPrdId())) {
            Long vlngPrdId = 0L;
            if (dashboardRequestDTO.getCycleId() != 0) {
                if (dashboardRequestDTO.getCycleId() == 1)
                    vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
                else if (dashboardRequestDTO.getCycleId() == 2)
                    vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
                else if (dashboardRequestDTO.getCycleId() == 3)
                    vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
                vsbdStringBuilder.append(" and v.prd_id=:prdId");
                vsbdStringBuilder.append(" and vs.prd_id=:prdId");
                phmpParams.put("prdId", vlngPrdId);
            }
        }

        if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
            vsbdStringBuilder.append(" and v.don_vi =:shopCode");
            if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode())) {
                phmpParams.put("shopCode", dashboardRequestDTO.getObjectCode());
            } else {
                phmpParams.put("shopCode", dashboardRequestDTO.getParentShopCode());
            }
            if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())) {
                vsbdStringBuilder.append(" and (v.kenh = :vdsChannelCode or :vdsChannelCode is null)");
                phmpParams.put("vdsChannelCode", dashboardRequestDTO.getVdsChannelCode());
            }
        } else {
            vsbdStringBuilder.append(" and (v.kenh = :vdsChannelCode or :vdsChannelCode is null)");
            phmpParams.put("vdsChannelCode", dashboardRequestDTO.getVdsChannelCode());
        }

        return vsbdStringBuilder;
    }
}
