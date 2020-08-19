package vn.vissoft.dashboard.repo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DashboardRequest;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.VdsKpiDetailRepo;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository
public class VdsKpiDetailRepoImpl implements VdsKpiDetailRepo {

    private static final Logger LOGGER = LogManager.getLogger(DashboardRequest.class);

    @PersistenceContext
    EntityManager entityManager;

    /**
     * lay ra so don vi dat va khong dat tat ca chi tieu theo ngay
     *
     * @param dashboardRequestDTO, staffDTO
     * @return tat ca cac don vi dat chi tieu va khong dat chi tieu
     * @throws Exception, SQLException
     * @author HungNN
     * @version 1.0
     * @since 29/10/2019
     */
    @Override
    public Object[] getTopRight(DashboardRequestDTO dashboardRequestDTO, int cycleId) throws Exception, SQLException {
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            HashMap vhmpMapParams = new HashMap();
                sqlBuilder.append("select v.SL_DAT_CT,v.SL_KHONG_DAT_CT from vds_kpi_daily_detail vkdd,");
            switch (cycleId) {
                case Constants.CYCLE_ID.DAY:
                    sqlBuilder.append("vds_kpi_daily v");
                    break;
                case Constants.CYCLE_ID.MONTH:
                    sqlBuilder.append("vds_kpi_monthly v");
                    break;
                case Constants.CYCLE_ID.QUARTER:
                    sqlBuilder.append("vds_kpi_quaterly v");
                    break;
                default:
                    sqlBuilder.append("vds_kpi_daily v");
            }
            sqlBuilder.append(buildSQL(dashboardRequestDTO, vhmpMapParams));
            Query query = entityManager.createNativeQuery(sqlBuilder.toString());
            vhmpMapParams.forEach((k, v) -> {
                query.setParameter(k.toString(), v);
            });
            List<Object[]> lstObject = query.getResultList();
            if(!DataUtil.isNullOrEmpty(lstObject)) {
                return lstObject.get(0);
            }else
                return null;
        } catch (NoResultException e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * chi tiet don vi dat tat ca chi tieu va chua dat chi tieu
     *
     * @param dashboardRequestDTO
     * @return tat ca cac don vi dat chi tieu va khong dat chi tieu
     * @throws Exception, SQLException
     * @author HungNN
     * @version 1.0
     * @since 05/11/2019
     */
    @Override
    public List<Object[]> tableDetailTopRight(DashboardRequestDTO dashboardRequestDTO) throws Exception {
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            HashMap hashMap = new HashMap();
            sqlBuilder.append("select (select name from service s where s.id = csm.service_id and s.service_type = 1) as ten_chi_tieu,")
                    .append(" round(csm.lk/csm.kh_lk*100) as phan_tram,")
                    .append(" case when vkdd.NHAN_VIEN is null then (select mip.short_name from manage_info_partner mip where  mip.shop_code = vkdd.DON_VI" +
                            " and ifnull(mip.vds_channel_code,'a') = ifnull(vkdd.KENH,'a'))" +
                            " else (select staff_name from vds_staff where staff_code = vkdd.nhan_vien) end  don_vi,csm.service_id,csm.lk");
            switch (dashboardRequestDTO.getCycleId()) {
                case Constants.CYCLE_ID.DAY:
                    sqlBuilder.append(" from chart_service_measure_daily csm, vds_kpi_daily_detail vkdd, vds_kpi_daily vkd");
                    break;
                case Constants.CYCLE_ID.MONTH:
                    sqlBuilder.append(" from chart_service_measure_monthly csm, vds_kpi_monthly_detail vkdd, vds_kpi_monthly vkd");
                    break;
                case Constants.CYCLE_ID.QUARTER:
                    sqlBuilder.append(" from chart_service_measure_quaterly csm, vds_kpi_quaterly_detail vkdd, vds_kpi_quaterly vkd");
                    break;
                default:
                    sqlBuilder.append(" from chart_service_measure_daily csm, vds_kpi_daily_detail vkdd, vds_kpi_daily vkd");
            }
            sqlBuilder.append(sqlBuilderDetail(dashboardRequestDTO, hashMap));
            Query query = entityManager.createNativeQuery(sqlBuilder.toString());
            hashMap.forEach((k, v) -> {
                query.setParameter(k.toString(), v);
            });
            return query.getResultList();
        } catch (NoResultException e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }
    /**
     * dieu kien truyen vao cua cau truy van
     *
     * @param dashboardRequestDTO, ,param, staffDTO
     * @return chuoi dieu kien
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 29/10/2019
     */
    public StringBuilder buildSQL(DashboardRequestDTO dashboardRequestDTO, HashMap phmpParams) {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        vsbdStringBuilder.append(" where 1 = 1 ");
        vsbdStringBuilder.append(" and v.prd_id= vkdd.prd_id");
        vsbdStringBuilder.append(" and IFNULL(v.kenh,'a') = IFNULL(vkdd.KENH_CHA,'a')");
        vsbdStringBuilder.append(" and v.don_vi = vkdd.don_vi_cha");
        if (!DataUtil.isNullOrZero(dashboardRequestDTO.getPrdId())) {
            Long vlngPrdId = 0L;
            if (dashboardRequestDTO.getCycleId() != 0) {
                if (dashboardRequestDTO.getCycleId() == 1)
                    vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());   //lay prdId theo ngay
                else if (dashboardRequestDTO.getCycleId() == 2)
                    vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());   //lay prdId theo thang
                else if (dashboardRequestDTO.getCycleId() == 3)
                    vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());       //lay prdId theo quy
                vsbdStringBuilder.append(" and v.prd_id=:prdId");
                phmpParams.put("prdId", vlngPrdId);
            }
        }
        vsbdStringBuilder.append(" and v.don_vi =:shopCode ");
        if (DataUtil.isNullOrEmpty(dashboardRequestDTO.getParentShopCode())) {
            phmpParams.put("shopCode", dashboardRequestDTO.getObjectCode());
        } else {
            phmpParams.put("shopCode", dashboardRequestDTO.getParentShopCode());
        }
        if(!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())){
            vsbdStringBuilder.append(" and v.KENH = :vdsChannelCode");
            phmpParams.put("vdsChannelCode",dashboardRequestDTO.getVdsChannelCode());
        }
        LOGGER.debug("SQL Params : " + phmpParams);
        return vsbdStringBuilder;
    }

    /**
     * dieu kien cho cau truy van
     *
     * @param dashboardRequestDTO, phmpParams
     * @return chuoi dieu kien
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 29/10/2019
     */
    public StringBuilder sqlBuilderDetail(DashboardRequestDTO dashboardRequestDTO, HashMap phmpParams) {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        vsbdStringBuilder.append(" where 1 = 1");
        vsbdStringBuilder.append(" and vkd.don_vi = vkdd.don_vi_cha");
        vsbdStringBuilder.append(" and IFNULL(vkd.kenh,'a') = IFNULL(vkdd.KENH_CHA,'a')");
        vsbdStringBuilder.append(" and ifnull(csm.vds_channel_code,'a') = ifnull(vkdd.KENH,'a')");
        vsbdStringBuilder.append(" and csm.shop_code = vkdd.DON_VI");
        vsbdStringBuilder.append(" and ifnull(csm.staff_code,'a') = ifnull(vkdd.NHAN_VIEN,'a')");
        vsbdStringBuilder.append(" and csm.service_id in (select id from service where service_type =1)");
        if(dashboardRequestDTO.getIsTarget()==1)
        vsbdStringBuilder.append(" and vkdd.ACHIEVE = '1'");
        else if(dashboardRequestDTO.getIsTarget()==-1) vsbdStringBuilder.append(" and vkdd.ACHIEVE = '0'");
        if (!DataUtil.isNullOrZero(dashboardRequestDTO.getPrdId())) {
            Long vlngPrdId = 0L;
            if (dashboardRequestDTO.getCycleId() != 0) {
                if (dashboardRequestDTO.getCycleId() == 1)
                    vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
                else if (dashboardRequestDTO.getCycleId() == 2)
                    vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
                else if (dashboardRequestDTO.getCycleId() == 3)
                    vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
                vsbdStringBuilder.append(" and vkdd.prd_id = :prdId");
                vsbdStringBuilder.append(" and csm.prd_id = :prdId");
                vsbdStringBuilder.append(" and vkd.prd_id = :prdId");
                phmpParams.put("prdId", vlngPrdId);
            }
        }
        if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getObjectCode())) {
            vsbdStringBuilder.append(" and vkdd.don_vi_cha =:shopCode ");
            phmpParams.put("shopCode", dashboardRequestDTO.getObjectCode());
        }
        if(!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())){
            vsbdStringBuilder.append(" and vkdd.KENH_cha = :vdsChannelCode");
            phmpParams.put("vdsChannelCode",dashboardRequestDTO.getVdsChannelCode());
        }
        LOGGER.debug("SQL Params : " + phmpParams);
        return vsbdStringBuilder;

    }
}
