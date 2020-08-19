package vn.vissoft.dashboard.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.ServiceScoreDTO;
import vn.vissoft.dashboard.dto.chart.ConfigSingleChartDTO;
import vn.vissoft.dashboard.dto.excel.ServiceScoreExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ServiceScore;
import vn.vissoft.dashboard.repo.ServiceScoreRepoCustom;
import vn.vissoft.dashboard.repo.StaffRepo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ServiceScoreRepoImpl implements ServiceScoreRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private StaffRepo staffRepo;

    /**
     * lay du lieu cho bang danh sach chi tieu trong tab trong so chi tieu
     *
     * @param serviceScoreDTO
     * @return du lieu cua bang danh sach chi tieu trong tab trong so chi tieu
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 09/12/2019
     */
    @Override
    public List<Object[]> getServiceScore(ServiceScoreDTO serviceScoreDTO) {
        StringBuilder sqlBuilder = new StringBuilder();
        HashMap hashMap = new HashMap();
        sqlBuilder.append("SELECT ss.*, ");
        sqlBuilder.append("(select short_name from manage_info_partner where shop_code = ss.shop_code), ");
        sqlBuilder.append("(select name from service where id = ss.service_id) AS ten_chi_tieu, ");
        sqlBuilder.append("vs.staff_name as nhan_vien ");
        sqlBuilder.append("FROM service_score ss left outer join vds_staff vs on vs.staff_code = ss.staff_code ");
        sqlBuilder.append(conditionSearch(serviceScoreDTO, hashMap));
        sqlBuilder.append(" order by id desc ");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        hashMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();
    }

    /**
     * them moi du lieu cho bang service_score
     *
     * @param serviceScoreDTO
     * @return thong bao thanh cong hoac that bai
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 09/12/2019
     */
    @Transactional
    @Override
    public String addServiceScore(ServiceScoreDTO serviceScoreDTO) throws Exception {
        String message = null;
        Boolean vblnCheckDuplicate = checkDuplicate(serviceScoreDTO, "insert");
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into service_score(service_id, vds_channel_code, shop_code, staff_code, status, score, from_date,to_date, score_max)" +
                " values (?,?,?,?,?,?,?,?,?)");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        if (serviceScoreDTO != null &&
                (staffRepo.checkExistedStaffInUnit(serviceScoreDTO.getShopCode(), serviceScoreDTO.getStaffCode()) || DataUtil.isNullOrEmpty(serviceScoreDTO.getStaffCode())) &&
                vblnCheckDuplicate == true &&
                !DataUtil.isNullOrZero(serviceScoreDTO.getScore())) {
            query.setParameter(1, serviceScoreDTO.getServiceId());
            query.setParameter(2, serviceScoreDTO.getVdsChannelCode());
            query.setParameter(3, serviceScoreDTO.getShopCode());
            query.setParameter(4, serviceScoreDTO.getStaffCode());
            query.setParameter(5, serviceScoreDTO.getStatus());
            query.setParameter(6, serviceScoreDTO.getScore());
            Date fromDate = new Date(serviceScoreDTO.getFromDate());
            query.setParameter(7, fromDate);
            if (!DataUtil.isNullOrZero(serviceScoreDTO.getToDate())) {
                Date toDate = new Date(serviceScoreDTO.getToDate());
                query.setParameter(8, toDate);
            } else {
                query.setParameter(8, null);
            }
            query.setParameter(9, serviceScoreDTO.getScoreMax());
            query.executeUpdate();
        } else if (serviceScoreDTO != null && !DataUtil.isNullOrEmpty(serviceScoreDTO.getStaffCode()) && staffRepo.checkExistedStaffInUnit(serviceScoreDTO.getShopCode(), serviceScoreDTO.getStaffCode()) == false) {
            message = Constants.MESSAGE_CODE.STAFF_NOT_IN_SHOP;
        } else if (vblnCheckDuplicate == false) {
            message = Constants.MESSAGE_CODE.DUPLICATE;
        } else {
            message = Constants.WARNINGSEND.FAILED;
        }

        return message;
    }

    /**
     * cap nhat du lieu bang servce_score
     *
     * @param serviceScoreDTO
     * @param id
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 10/12/2019
     */
    @Transactional
    @Override
    public String updateServiceScore(ServiceScoreDTO serviceScoreDTO, Long id) throws Exception {
        String message;
        boolean vblnCheckDuplicate = checkDuplicate(serviceScoreDTO, "update");
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("update service_score" +
                " set service_id=:serviceId,vds_channel_code=:vdsChannelCode,shop_code=:shopCode,staff_code=:staffCode,status=:status,score=:score,from_date=:fromDate,to_date=:toDate,score_max=:scoreMax" +
                " where id = :id");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        if (vblnCheckDuplicate == true) {
            query.setParameter("serviceId", serviceScoreDTO.getServiceId());
            query.setParameter("vdsChannelCode", serviceScoreDTO.getVdsChannelCode());
            query.setParameter("shopCode", serviceScoreDTO.getShopCode());
            query.setParameter("staffCode", serviceScoreDTO.getStaffCode());
            query.setParameter("status", serviceScoreDTO.getStatus());
            query.setParameter("score", serviceScoreDTO.getScore());
            Date fromDate = new Date(serviceScoreDTO.getFromDate());
            query.setParameter("fromDate", fromDate);
            if (!DataUtil.isNullOrZero(serviceScoreDTO.getToDate())) {
                Date toDate = new Date(serviceScoreDTO.getToDate());
                query.setParameter("toDate", toDate);
            } else {
                query.setParameter("toDate", null);
            }
            query.setParameter("scoreMax", serviceScoreDTO.getScoreMax());
            query.setParameter("id", id);
            query.executeUpdate();
            message = null;
        } else {
            message = Constants.MESSAGE_CODE.DUPLICATE;
        }

        return message;
    }

    /**
     * tim service score theo du lieu trong file excel
     *
     * @param
     * @return
     * @author DatNT
     * @since 15/09/2019
     */
    @Override
    public ServiceScore findServiceScoreFromFile(ServiceScoreExcel serviceScore) throws Exception {
        HashMap mapParams = new HashMap();
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("select ss from ServiceScore ss");
        sqlBuilder.append(buildSQLFromPlan(serviceScore, mapParams));

        Query query = entityManager.createQuery(sqlBuilder.toString());
        mapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        List<ServiceScore> vlstServiceScores = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstServiceScores)) return null;
        return vlstServiceScores.get(0);
    }

    @Transactional
    @Override
    public void persist(ServiceScore serviceScore) {
        entityManager.persist(serviceScore);
    }

    @Transactional
    @Override
    public void update(ServiceScore serviceScore) throws Exception {
        entityManager.merge(serviceScore);
    }

    /**
     * kiem tra du lieu them moi co ton tai trong db khong
     *
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public boolean checkDuplicate(ServiceScoreDTO serviceScoreDTO, String pstrType) throws Exception {
        boolean vblnCheck;
        Long vlngId = serviceScoreDTO.getId();
        Long vlngServiceId = serviceScoreDTO.getServiceId();
        String vstrShopCode = serviceScoreDTO.getShopCode();
        String vstrStaffCode = serviceScoreDTO.getStaffCode();
        StringBuilder vsbdSql = new StringBuilder();
        Query query;

        if (pstrType.equals(Constants.UPDATE)) {
            vsbdSql.append("select * from service_score where service_id = :serviceId and shop_code = :shopCode and ifnull(staff_code, 'a') = ifnull(:staffCode, 'a') and id <> :id");
            query = entityManager.createNativeQuery(vsbdSql.toString());
            query.setParameter("serviceId", vlngServiceId);
            query.setParameter("shopCode", vstrShopCode);
            query.setParameter("staffCode", vstrStaffCode);
            query.setParameter("id", vlngId);
        } else {
            vsbdSql.append("select * from service_score where service_id = :serviceId and shop_code = :shopCode and ifnull(staff_code, 'a') = ifnull(:staffCode, 'a')");
            query = entityManager.createNativeQuery(vsbdSql.toString());
            query.setParameter("serviceId", vlngServiceId);
            query.setParameter("shopCode", vstrShopCode);
            query.setParameter("staffCode", vstrStaffCode);
        }

        List<Object[]> vlstData = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstData)) {
            vblnCheck = true;
        } else {
            vblnCheck = false;
        }

        return vblnCheck;
    }

    /**
     * dieu kien tim kiem service score trong file excel
     *
     * @param serviceScore
     * @param params
     * @return
     * @author DatNT
     * @since 15/09/2019
     */
    private StringBuilder buildSQLFromPlan(ServiceScoreExcel serviceScore, HashMap params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" where 1=1 ");

        if (!DataUtil.isNullOrEmpty(serviceScore.getVdsChannelCode())) {
            stringBuilder.append(" and ss.vdsChannelCode = :vdsChannelCode");
            params.put("vdsChannelCode", serviceScore.getVdsChannelCode().trim());
        }
        if (!DataUtil.isNullOrEmpty(serviceScore.getShopCode())) {
            stringBuilder.append(" and ss.shopCode =:shopCode");
            params.put("shopCode", serviceScore.getShopCode().trim());
        }
        if (!DataUtil.isNullOrEmpty(serviceScore.getStaffCode())) {
            stringBuilder.append(" and ss.staffCode =:staffCode");
            params.put("staffCode", serviceScore.getStaffCode().trim());
        } else {
            stringBuilder.append(" and ss.staffCode is null");
        }
        if (!DataUtil.isNullOrEmpty(serviceScore.getServiceCode())) {
            stringBuilder.append(" and ss.serviceId = (select id from Service where code =:serviceCode)");
            params.put("serviceCode", serviceScore.getServiceCode().trim());
        }

        return stringBuilder;
    }


    /**
     * dieu kien tim kiem cho bang danh sach chi tieu
     *
     * @param serviceScoreDTO
     * @return dieu kien tim kiem
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 09/12/2019
     */
    private StringBuilder conditionSearch(ServiceScoreDTO serviceScoreDTO, HashMap hashMap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" where 1 = 1");
        if (serviceScoreDTO.getStaffCode() != null && serviceScoreDTO.getShopCode() == null) {
            stringBuilder.append(" and ss.staff_code = :staffCode");
            hashMap.put("staffCode", serviceScoreDTO.getStaffCode());
        }
        if (!DataUtil.isNullOrZero(serviceScoreDTO.getServiceId())) {
            stringBuilder.append(" and ss.service_id = :serviceId");
            hashMap.put("serviceId", serviceScoreDTO.getServiceId());
        }
        if (serviceScoreDTO.getStatus() != null) {
            stringBuilder.append(" and ss.status = :status");
            hashMap.put("status", serviceScoreDTO.getStatus());
        }
        if (serviceScoreDTO.getShopCode() != null) {
            stringBuilder.append(" and ss.shop_code = :shopCode");
            hashMap.put("shopCode", serviceScoreDTO.getShopCode());
        }
        if (serviceScoreDTO.getStaffCode() != null) {
            stringBuilder.append(" and ss.staff_code = :staffCode");
            hashMap.put("staffCode", serviceScoreDTO.getStaffCode());
        }
        stringBuilder.append("  and (vs.staff_type = '1' or vs.staff_type is null)");
        return stringBuilder;
    }

    /**
     * tim id chi tieu theo dieu kien service_id,vds_channel_code,shop_code,staff_code trong so chi tieu
     *
     * @param plngServiceId
     * @param pstrChannelCode
     * @param pstrStaffCode
     * @param pstrShopCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public Long findServiceScoreIdByCondition(Long plngServiceId, String pstrChannelCode, String pstrShopCode, String pstrStaffCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select s.id from ServiceScore s where s.serviceId =:serviceId and vdsChannelCode =:vdsChannelCode ")
                .append(" and shopCode =:shopCode and ifnull(staffCode,'a') = ifnull(:staffCode,'a')");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("serviceId", plngServiceId);
        query.setParameter("vdsChannelCode", pstrChannelCode);
        query.setParameter("shopCode", pstrShopCode);
        query.setParameter("staffCode", pstrStaffCode);
        List<Long> vlstServiceIds = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstServiceIds)) return null;
        return vlstServiceIds.get(0);
    }

    @Override
    public List<Object[]> findDetailEvaluateScore(DashboardRequestDTO dashboardRequestDTO, ConfigSingleChartDTO configSingleChartDTO) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap hashMap = new HashMap();
        int vintCycleId = dashboardRequestDTO.getCycleId();
        java.sql.Date vDate = new Date(dashboardRequestDTO.getPrdId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(vDate);

        if (Constants.PARAM_STATUS_0.equals(dashboardRequestDTO.getNationalStaff())) {
            vsbdSqlBuilder.append("select case when csmd.nhan_vien is null")
                    .append(" then ( select shop_name from manage_info_partner")
                    .append(" where vds_channel_code = csmd.kenh")
                    .append(" and shop_code = csmd.don_vi )")
                    .append(" else (select staff_name from vds_staff where staff_code = csmd.nhan_vien ) end ten_dv_nv ,");
        } else if (Constants.PARAM_STATUS.equals(dashboardRequestDTO.getNationalStaff())) {
            vsbdSqlBuilder.append("select (select staff_name from vds_staff where staff_code = csmd.nhan_vien )  ten_dv_nv ,");
        }
        vsbdSqlBuilder.append(" csmd.score,csmd.score_max,")
                .append(" csmd.th thuc_hien,csmd.kh ke_hoach, csmd.ty_le_ht ty_le,(select name from service where id = csmd.service_id) ten_chi_tieu,service_id,")
                .append(" case when csmd.prd_id =:prdId then 1");

        switch (dashboardRequestDTO.getMonth()) {
            case Constants.TOP_BY_SERVICE.TWO:
                if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY || dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                    vsbdSqlBuilder.append(" when csmd.prd_id=date_add(:prdId,interval -1 month) then 2 ");
                } else if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                    vsbdSqlBuilder.append(" when csmd.prd_id=date_add(:prdId,interval -1 quarter) then 2 ");
                }
                break;
            case Constants.TOP_BY_SERVICE.THREE:
                vsbdSqlBuilder.append(" else 3 ");
                break;
        }
        vsbdSqlBuilder.append(" end, ");
        vsbdSqlBuilder.append("   csmd.score_n1,csmd.score_max_n1,")
                .append(" csmd.th_n1 thuc_hien_n1,csmd.kh_n1 ke_hoach_n1, ty_le_ht_n1 ty_le_n1,");
        vsbdSqlBuilder.append("  csmd.score_n2,csmd.score_max_n2,")
                .append(" csmd.th_n2 thuc_hien_n2,csmd.kh_n2 ke_hoach_n2, ty_le_ht_n2 ty_le_n2 ");

        if (Constants.PARAM_STATUS.equals(dashboardRequestDTO.getNationalStaff())) {
            vsbdSqlBuilder.append(", (select short_name from manage_info_partner where shop_code = csmd.don_vi) ");
        } else {
            vsbdSqlBuilder.append(", 'v' ");
        }

        switch (vintCycleId) {
            case Constants.CYCLE_ID.DAY:
                vsbdSqlBuilder.append(" From vds_score_service_daily csmd");
                break;
            case Constants.CYCLE_ID.MONTH:
                vsbdSqlBuilder.append(" From vds_score_service_monthly csmd");
                break;
            case Constants.CYCLE_ID.QUARTER:
                vsbdSqlBuilder.append(" From vds_score_service_quaterly csmd");
                break;
        }
        vsbdSqlBuilder.append(",vds_score_ranking_daily vsrd");

        vsbdSqlBuilder.append(" where csmd.KENH =:vdsChannelCode and csmd.prd_id =:prdId and vsrd.prd_id =:prdId");
        vsbdSqlBuilder.append(" and csmd.KENH = vsrd.kenh\n")
                .append("and csmd.DON_VI = vsrd.don_vi\n")
                .append("and (csmd.NHAN_VIEN = vsrd.nhan_vien or (csmd.NHAN_VIEN is null and vsrd.nhan_vien is null))");
        if (Constants.PARAM_STATUS_0.equals(dashboardRequestDTO.getNationalStaff())) {
            vsbdSqlBuilder.append(" and (\n" +
                    " ((csmd.kenh, csmd.don_vi)\n" +
                    " in (select vds_channel_code,shop_code from manage_info_partner mip\n" +
                    " where mip.vds_channel_code = :vdsChannelCode and mip.parent_shop_code = :shopCode )\n" +
                    " and csmd.nhan_vien is null )\n" +
                    " OR (csmd.kenh = :vdsChannelCode and csmd.don_vi = :shopCode\n" +
                    " and csmd.nhan_vien is not null )\n" +
                    " )");
        } else if (Constants.PARAM_STATUS.equals(dashboardRequestDTO.getNationalStaff())) {
            vsbdSqlBuilder.append(" and csmd.nhan_vien is not null");
        }

        switch (dashboardRequestDTO.getMonth()) {
            case Constants.TOP_BY_SERVICE.ONE:
                vsbdSqlBuilder.append(" and (")
                        .append(" ( :pRow = '0' and 100*vsrd.score/vsrd.score_max< (select value from ap_param where type = 'EVALUATED_RATE' and code = '1') )")
                        .append(" OR (:pRow = '1' and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '1')")
                        .append(" and 100*vsrd.score/vsrd.score_max < (select value from ap_param where type = 'EVALUATED_RATE' and code = '2'))")
                        .append(" OR (:pRow = '2' and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '2')")
                        .append(" and 100*vsrd.score/vsrd.score_max < (select value from ap_param where type = 'EVALUATED_RATE' and code = '3'))")
                        .append(" OR (:pRow = '3' and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '3')")
                        .append(" and 100*vsrd.score/vsrd.score_max < (select value from ap_param where type = 'EVALUATED_RATE' and code = '4'))")
                        .append(" OR (:pRow ='4'  and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '4'))");
                break;
            case Constants.TOP_BY_SERVICE.TWO:
                vsbdSqlBuilder.append(" and (")
                        .append(" ( :pRow = '0' and 100*vsrd.score/vsrd.score_max< (select value from ap_param where type = 'EVALUATED_RATE' and code = '1')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1< (select value from ap_param where type = 'EVALUATED_RATE' and code = '1') )")
                        .append(" OR (:pRow = '1' and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '1')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '1')")
                        .append(" and 100*vsrd.score/vsrd.score_max < (select value from ap_param where type = 'EVALUATED_RATE' and code = '2')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 < (select value from ap_param where type = 'EVALUATED_RATE' and code = '2'))")
                        .append(" OR (:pRow = '2' and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '2')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '2')")
                        .append(" and 100*vsrd.score/vsrd.score_max < (select value from ap_param where type = 'EVALUATED_RATE' and code = '3')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 < (select value from ap_param where type = 'EVALUATED_RATE' and code = '3'))")
                        .append(" OR (:pRow = '3' and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '3')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '3')")
                        .append(" and 100*vsrd.score/vsrd.score_max < (select value from ap_param where type = 'EVALUATED_RATE' and code = '4')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 < (select value from ap_param where type = 'EVALUATED_RATE' and code = '4'))")
                        .append(" OR (:pRow ='4'  and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '4')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '4'))");
                break;

            case Constants.TOP_BY_SERVICE.THREE:
                vsbdSqlBuilder.append(" and (")
                        .append(" ( :pRow = '0' and 100*vsrd.score/vsrd.score_max< (select value from ap_param where type = 'EVALUATED_RATE' and code = '1')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1< (select value from ap_param where type = 'EVALUATED_RATE' and code = '1')" +
                                " and 100*vsrd.score_n2/vsrd.score_max_n2< (select value from ap_param where type = 'EVALUATED_RATE' and code = '1') )")
                        .append(" OR (:pRow = '1' and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '1')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '1')" +
                                " and 100*vsrd.score_n2/vsrd.score_max_n2 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '1')")
                        .append(" and 100*vsrd.score/vsrd.score_max < (select value from ap_param where type = 'EVALUATED_RATE' and code = '2')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 < (select value from ap_param where type = 'EVALUATED_RATE' and code = '2')" +
                                " and 100*vsrd.score_n2/vsrd.score_max_n2 < (select value from ap_param where type = 'EVALUATED_RATE' and code = '2'))")
                        .append(" OR (:pRow = '2' and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '2')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '2')" +
                                " and 100*vsrd.score_n2/vsrd.score_max_n2 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '2')")
                        .append(" and 100*vsrd.score/vsrd.score_max < (select value from ap_param where type = 'EVALUATED_RATE' and code = '3')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 < (select value from ap_param where type = 'EVALUATED_RATE' and code = '3')" +
                                " and 100*vsrd.score_n2/vsrd.score_max_n2 < (select value from ap_param where type = 'EVALUATED_RATE' and code = '3'))")
                        .append(" OR (:pRow = '3' and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '3')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '3')" +
                                " and 100*vsrd.score_n2/vsrd.score_max_n2 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '3')")
                        .append(" and 100*vsrd.score/vsrd.score_max < (select value from ap_param where type = 'EVALUATED_RATE' and code = '4')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 < (select value from ap_param where type = 'EVALUATED_RATE' and code = '4')" +
                                " and 100*vsrd.score_n2/vsrd.score_max_n2 < (select value from ap_param where type = 'EVALUATED_RATE' and code = '4'))")
                        .append(" OR (:pRow ='4'  and 100*vsrd.score/vsrd.score_max >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '4')" +
                                " and 100*vsrd.score_n1/vsrd.score_max_n1 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '4')" +
                                " and 100*vsrd.score_n2/vsrd.score_max_n2 >= (select value from ap_param where type = 'EVALUATED_RATE' and code = '4'))");
                break;
        }

        vsbdSqlBuilder.append(") order by ten_dv_nv ");
        vsbdSqlBuilder.append(conditionEvaluateScore(dashboardRequestDTO, hashMap));

        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        hashMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        List<Object[]> vlstObjects = query.getResultList();

        return vlstObjects;

    }

    private StringBuilder conditionEvaluateScore(DashboardRequestDTO dashboardRequestDTO, HashMap hashMap) {
        StringBuilder stringBuilder = new StringBuilder();
        Long vlngPrdId = null;
        if (!DataUtil.isNullOrZero(dashboardRequestDTO.getPrdId())) {
            //convert millisecond ve prd_id
            if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.DAY) {
                vlngPrdId = DataUtil.convertToPrdId(dashboardRequestDTO.getPrdId());
            }
            if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.MONTH) {
                vlngPrdId = DataUtil.convertToPrdIdFirstDay(dashboardRequestDTO.getPrdId());
            }
            if (dashboardRequestDTO.getCycleId() == Constants.CYCLE_ID.QUARTER) {
                vlngPrdId = DataUtil.getFirstDayOfQuarter(dashboardRequestDTO.getPrdId());
            }
            hashMap.put("prdId", vlngPrdId);
        }
        if(!DataUtil.isNullOrEmpty(dashboardRequestDTO.getVdsChannelCode())){
            hashMap.put("vdsChannelCode",dashboardRequestDTO.getVdsChannelCode());
        }
        if (Constants.PARAM_STATUS_0.equals(dashboardRequestDTO.getNationalStaff())) {
            hashMap.put("shopCode",dashboardRequestDTO.getObjectCode());
        }

        if (!DataUtil.isNullOrEmpty(dashboardRequestDTO.getpRow())) {
            hashMap.put("pRow", dashboardRequestDTO.getpRow());
        }

        return stringBuilder;
    }
}
