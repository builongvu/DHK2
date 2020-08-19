package vn.vissoft.dashboard.repo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DashboardRequest;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.VdsScoreRankingRepo;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

@Repository
public class VdsScoreRankingRepoImpl implements VdsScoreRankingRepo {

    @PersistenceContext
    EntityManager entityManager;

    private static final Logger LOGGER = LogManager.getLogger(DashboardRequest.class);

    /**
     * lay ra 5 don vi co rank cao nhat trong bang vds_score_ranking_daily theo ngay
     *
     * @param dashboardRequestDTO, staffDTO
     * @return 5 don vi co rank cao nhat
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 25/10/2019
     */
    @Override
    public List<Object[]> getTopLeft(DashboardRequestDTO dashboardRequestDTO, int topNum, boolean isMax) throws Exception {
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            HashMap vhmpMapParams = new HashMap();
            int cycleId = dashboardRequestDTO.getCycleId();
            String channelCode = null;
            if (dashboardRequestDTO.getVdsChannelCode() != null) {
                channelCode = dashboardRequestDTO.getVdsChannelCode();
            }

            if (Constants.PARAM_STATUS.equals(dashboardRequestDTO.getNationalStaff())) {
                sqlBuilder.append("select s.staff_name, v.score,v.rank_all_staff from");
                switch (cycleId) {
                    case Constants.CYCLE_ID.DAY:
                        sqlBuilder.append(" vds_score_ranking_daily v");
                        break;
                    case Constants.CYCLE_ID.MONTH:
                        sqlBuilder.append(" vds_score_ranking_monthly v");
                        break;
                    case Constants.CYCLE_ID.QUARTER:
                        sqlBuilder.append(" vds_score_ranking_quaterly v");
                        break;
                    default:
                        sqlBuilder.append("select s.staff_name, v.score,v.rank_all_staff from");
                        sqlBuilder.append(" vds_score_ranking_daily v");
                }
                sqlBuilder.append(",vds_staff s")
                        .append(" where 1 = 1 and v.nhan_vien = s.staff_code")
                        .append(" and s.staff_type = '1'")
                        .append(" and  v.nhan_vien is not null")
                        .append(" and v.prd_id=:prdId")
                        .append(" and ifnull(v.kenh, 'a') = ifnull(:channelCode, 'a')");
                if (isMax == true) {
                    sqlBuilder.append(" order by score desc, staff_name limit :topNum");
                } else {
                    sqlBuilder.append(" order by score, staff_name limit :topNum");
                }

//                sqlBuilder.append(" order by score, name limit :topNum");
                if (!DataUtil.isNullOrZero(dashboardRequestDTO.getPrdId())) {
                    Long vlngPrdId = 0L;
                    if (dashboardRequestDTO.getCycleId() != 0) {
                        if (dashboardRequestDTO.getCycleId() == 1)
                            vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
                        else if (dashboardRequestDTO.getCycleId() == 2)
                            vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
                        else if (dashboardRequestDTO.getCycleId() == 3)
                            vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
                        vhmpMapParams.put("prdId", vlngPrdId);
                    }
                    vhmpMapParams.put("channelCode", channelCode);
                }
            } else {
                sqlBuilder.append("select mip.short_name, vs.score,vs.rank from manage_info_partner mip");
                switch (cycleId) {
                    case Constants.CYCLE_ID.DAY:
                        sqlBuilder.append(",vds_score_ranking_daily vs");
                        break;
                    case Constants.CYCLE_ID.MONTH:
                        sqlBuilder.append(",vds_score_ranking_monthly vs");
                        break;
                    case Constants.CYCLE_ID.QUARTER:
                        sqlBuilder.append(",vds_score_ranking_quaterly vs");
                        break;
                    default:
                        sqlBuilder.append(",vds_score_ranking_daily vs");
                }
                sqlBuilder.append(conditionSQL(dashboardRequestDTO, vhmpMapParams));
                sqlBuilder.append(" union all");
                sqlBuilder.append(" select s.staff_name, v.score,v.rank");
                switch (cycleId) {
                    case Constants.CYCLE_ID.DAY:
                        sqlBuilder.append(" from vds_score_ranking_daily v");
                        break;
                    case Constants.CYCLE_ID.MONTH:
                        sqlBuilder.append(" from vds_score_ranking_monthly v ");
                        break;
                    case Constants.CYCLE_ID.QUARTER:
                        sqlBuilder.append(" from vds_score_ranking_quaterly v ");
                        break;
                    default:
                        sqlBuilder.append(" from vds_score_ranking_daily v ");
                }
                sqlBuilder.append(",vds_staff s");
                sqlBuilder.append(buildSQL(dashboardRequestDTO, vhmpMapParams));
                if (isMax == true) {
                    sqlBuilder.append(" order by score desc, short_name limit :topNum");
                } else {
                    sqlBuilder.append(" order by score, short_name limit :topNum");
                }
            }

            Query query = entityManager.createNativeQuery(sqlBuilder.toString());
            vhmpMapParams.forEach((k, v) -> {
                query.setParameter(k.toString(), v);
            });
            query.setParameter("topNum", topNum);
            return query.getResultList();
        } catch (NoResultException e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * lay du lieu cho level 2 content top ben trai
     *
     * @param dashboardRequestDTO: điều kiện truyền vào của dashboard
     * @return : chuoi du lieu trong bang o content top
     * @author: HungNN
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/11/03
     */
    @Override
    public List<Object[]> getScoreRanking(DashboardRequestDTO dashboardRequestDTO, boolean isMax) throws Exception {

        StringBuilder sqlBuilder = new StringBuilder();
        HashMap mapParams = new HashMap();
        int cycleId = dashboardRequestDTO.getCycleId();
        String vstrNationalStaff = "0";
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }
        if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
            sqlBuilder.append("select mip.short_name, vs.score, vs.rank, vs.score_n1, vs.rank_n1, mip.shop_code, vs.score_max, vs.score_max_n1, vs.score_n2, vs.score_max_n2, vs.rank_n2, 'a'" +
                    " from manage_info_partner mip,");
//            int cycleId = dashboardRequestDTO.getCycleId();
            switch (cycleId) {
                case Constants.CYCLE_ID.DAY:
                    sqlBuilder.append("vds_score_ranking_daily vs");
                    break;
                case Constants.CYCLE_ID.MONTH:
                    sqlBuilder.append("vds_score_ranking_monthly vs");
                    break;
                case Constants.CYCLE_ID.QUARTER:
                    sqlBuilder.append("vds_score_ranking_quaterly vs");
                    break;
                default:
                    sqlBuilder.append("vds_score_ranking_daily vs");
            }
            sqlBuilder.append(conditionSQL(dashboardRequestDTO, mapParams));
            sqlBuilder.append(" union all");
            sqlBuilder.append(" select s.staff_name, v.score, v.rank,v.score_n1, v.rank_n1, v.don_vi, v.score_max,v.score_max_n1, v.score_n2, v.score_max_n2, v.rank_n2, 'a'");
            switch (cycleId) {
                case Constants.CYCLE_ID.DAY:
                    sqlBuilder.append(" from vds_score_ranking_daily v");
                    break;
                case Constants.CYCLE_ID.MONTH:
                    sqlBuilder.append(" from vds_score_ranking_monthly v");
                    break;
                case Constants.CYCLE_ID.QUARTER:
                    sqlBuilder.append(" from vds_score_ranking_quaterly v");
                    break;
                default:
                    sqlBuilder.append(" from vds_score_ranking_daily v");
            }
            sqlBuilder.append(",vds_staff s");
            sqlBuilder.append(buildSQL(dashboardRequestDTO, mapParams));
            if (isMax == true) {
                sqlBuilder.append(" order by score desc");
            } else {
                sqlBuilder.append(" order by score");
            }
        } else {
            sqlBuilder.append(" select s.staff_name, v.score, v.rank_all_staff,v.score_n1, v.rank_all_staff_n1, v.don_vi, v.score_max,v.score_max_n1, v.score_n2, v.score_max_n2, v.rank_all_staff_n2, (select short_name from manage_info_partner where shop_code = v.don_vi)");
            switch (cycleId) {
                case Constants.CYCLE_ID.DAY:
                    sqlBuilder.append(" from vds_score_ranking_daily v");
                    break;
                case Constants.CYCLE_ID.MONTH:
                    sqlBuilder.append(" from vds_score_ranking_monthly v");
                    break;
                case Constants.CYCLE_ID.QUARTER:
                    sqlBuilder.append(" from vds_score_ranking_quaterly v");
                    break;
                default:
                    sqlBuilder.append(" from vds_score_ranking_daily v");
            }
            sqlBuilder.append(",vds_staff s");
            sqlBuilder.append(buildSQL(dashboardRequestDTO, mapParams));
            if (isMax == true) {
                sqlBuilder.append(" order by score desc");
            } else {
                sqlBuilder.append(" order by score");
            }
        }

        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        mapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();
    }

    @Override
    public Integer countTop(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        HashMap mapParams = new HashMap();
        int cycleId = dashboardRequestDTO.getCycleId();
        String vstrNationalStaff = "0";
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }
        if (Constants.PARAM_STATUS_0.equals(vstrNationalStaff)) {
            sqlBuilder.append("select count(*) from (select 1" +
                    " from manage_info_partner mip,");
//            int cycleId = dashboardRequestDTO.getCycleId();
            switch (cycleId) {
                case Constants.CYCLE_ID.DAY:
                    sqlBuilder.append("vds_score_ranking_daily vs");
                    break;
                case Constants.CYCLE_ID.MONTH:
                    sqlBuilder.append("vds_score_ranking_monthly vs");
                    break;
                case Constants.CYCLE_ID.QUARTER:
                    sqlBuilder.append("vds_score_ranking_quaterly vs");
                    break;
                default:
                    sqlBuilder.append("vds_score_ranking_daily vs");
            }
            sqlBuilder.append(conditionSQL(dashboardRequestDTO, mapParams));
            sqlBuilder.append(" union all");
            sqlBuilder.append(" select 1");
            switch (cycleId) {
                case Constants.CYCLE_ID.DAY:
                    sqlBuilder.append(" from vds_score_ranking_daily v");
                    break;
                case Constants.CYCLE_ID.MONTH:
                    sqlBuilder.append(" from vds_score_ranking_monthly v");
                    break;
                case Constants.CYCLE_ID.QUARTER:
                    sqlBuilder.append(" from vds_score_ranking_quaterly v");
                    break;
                default:
                    sqlBuilder.append(" from vds_score_ranking_daily v");
            }
            sqlBuilder.append(",vds_staff s");
            sqlBuilder.append(buildSQL(dashboardRequestDTO, mapParams))
            .append(") a");
        } else {
            sqlBuilder.append(" select count(*) from (select 1");
            switch (cycleId) {
                case Constants.CYCLE_ID.DAY:
                    sqlBuilder.append(" from vds_score_ranking_daily v");
                    break;
                case Constants.CYCLE_ID.MONTH:
                    sqlBuilder.append(" from vds_score_ranking_monthly v");
                    break;
                case Constants.CYCLE_ID.QUARTER:
                    sqlBuilder.append(" from vds_score_ranking_quaterly v");
                    break;
                default:
                    sqlBuilder.append(" from vds_score_ranking_daily v");
            }
            sqlBuilder.append(",vds_staff s");
            sqlBuilder.append(buildSQL(dashboardRequestDTO, mapParams))
            .append(") a");
        }

        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        mapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return DataUtil.safeToInt(query.getSingleResult());
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
    public StringBuilder conditionSQL(DashboardRequestDTO dashboardRequestDTO, HashMap phmpParams) {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        vsbdStringBuilder.append(" where 1 = 1 ");
        vsbdStringBuilder.append(" and vs.rank is not null ");
        vsbdStringBuilder.append(" and vs.don_vi = mip.shop_code");
        vsbdStringBuilder.append(" and ifnull(vs.kenh,'a') = ifnull(mip.vds_channel_code,'a')");
        vsbdStringBuilder.append(" and vs.nhan_vien is null");
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
                phmpParams.put("prdId", vlngPrdId);
            }
        }
        vsbdStringBuilder.append(" and mip.parent_shop_code = :shopCode");
        if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode())) {
            phmpParams.put("shopCode", dashboardRequestDTO.getObjectCode());
        } else {
            phmpParams.put("shopCode", dashboardRequestDTO.getParentShopCode());
        }
        if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())) {
            vsbdStringBuilder.append(" and (mip.vds_channel_code = :vdsChannelCode or :vdsChannelCode is null)");
            phmpParams.put("vdsChannelCode", dashboardRequestDTO.getVdsChannelCode());
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
    public StringBuilder buildSQL(DashboardRequestDTO dashboardRequestDTO, HashMap phmpParams) {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        String vstrNationalStaff = "0";
        if (dashboardRequestDTO.getNationalStaff() != null) {
            vstrNationalStaff = dashboardRequestDTO.getNationalStaff();
        }
        vsbdStringBuilder.append(" where 1 = 1 ");
        vsbdStringBuilder.append(" and v.rank is not null ");
        vsbdStringBuilder.append(" and v.nhan_vien = s.staff_code");
        vsbdStringBuilder.append(" and v.don_vi = s.shop_code");
        vsbdStringBuilder.append(" and s.staff_type = '1'");
        vsbdStringBuilder.append(" and  v.nhan_vien is not null");
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

        LOGGER.debug("SQL Params : " + phmpParams);
        return vsbdStringBuilder;
    }
}
