package vn.vissoft.dashboard.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import vn.vissoft.dashboard.dto.ServiceDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Service;
import vn.vissoft.dashboard.model.ServiceChannel;
import vn.vissoft.dashboard.model.WarningSendConfig;
import vn.vissoft.dashboard.repo.ServiceChannelRepo;
import vn.vissoft.dashboard.repo.ServiceRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceRepoImpl implements ServiceRepoCustom {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private ServiceChannelRepo serviceChannelRepo;

    /**
     * lay ra danh sach chi tieu(service) cho combobox giao chi tieu VDS
     *
     * @param serviceDTO
     * @return List<Service> là danh sách chỉ tiêu tìm được
     * @throws Exception
     * @author VuBL
     * @since 2019/09
     */
    @Override
    public List<Service> findServicesByCondition(ServiceDTO serviceDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select s from Service s ");
        vsbdSqlBuilder.append(buildSQL(serviceDTO, vhmpMapParams));
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();

    }

    /**
     * lay ra danh sach chi tieu(service) cho combobox giao chi tieu cac cap
     *
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/10
     */
    @Override
    public List<Service> findByChannelCodeOfUser(StaffDTO staffDTO) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        Query query;
        if (staffDTO.getShopCode().equals(Constants.PLAN_MONTHLY.VDS) && DataUtil.isNullOrEmpty(staffDTO.getVdsChannelCode())) {
            vsbdSqlBuilder.append("select s from Service s ")
                    .append("where id in (select serviceId from ServiceChannel ")
                    .append("where vdsChannelCode in (select vdsChannelCode from ManageInfoPartner ")
                    .append("where parentShopCode = 'VDS')) ")
                    .append("and serviceType = 1");
            query = entityManager.createQuery(vsbdSqlBuilder.toString());
        } else {
            vsbdSqlBuilder.append("select s from Service s ")
                    .append("where id in (select serviceId from ServiceChannel ")
                    .append("where ifnull(vdsChannelCode, 'a') = ifnull(:vdsChannelCode, 'a')) ")
                    .append("and serviceType = 1");
            query = entityManager.createQuery(vsbdSqlBuilder.toString());
            query.setParameter("vdsChannelCode", staffDTO.getVdsChannelCode());
        }
        return query.getResultList();
    }

    /**
     * tim ten chi tieu theo id chi tieu
     *
     * @param plngServiceId
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public String findNameById(Long plngServiceId) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select s.name from Service s where s.id=:serviceId ");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("serviceId", plngServiceId);
        List<String> vlstServiceNames = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstServiceNames)) return null;
        return vlstServiceNames.get(0);
    }

    /**
     * tim id chi tieu theo code chi tieu
     *
     * @param pstrCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public Long findServiceIdByCode(String pstrCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select s.id from Service s where s.code=:serviceCode ");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("serviceCode", pstrCode);
        List<Long> vlstServiceIds = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstServiceIds)) return null;
        return vlstServiceIds.get(0);
    }

//    @Override
//    public List<String> findAllServiceCode() {
//        StringBuilder vsbdSqlBuilder = new StringBuilder();
//
//        vsbdSqlBuilder.append("select s.code from Service s ");
//        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
//
//        return query.getResultList();
//    }

    /**
     * kiem tra ton tai chi tieu trong csdl
     *
     * @param pstrCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public boolean checkExistedServiceCode(String pstrCode) throws Exception {

        boolean vblnCheck = false;
        List<Service> services = findActiveService();
        for (Service service : services) {
            if (pstrCode.equalsIgnoreCase(service.getCode().trim())) {
                vblnCheck = true;
                break;
            }
        }
        return vblnCheck;
    }

    /**
     * tim nhung chi tieu dang hoat dong
     *
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public List<Service> findActiveService() throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select s from Service s where s.status='1'");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    @Override
    public List<Service> findActiveServiceAssign() throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select s from Service s where s.status='1' and serviceType=1");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    @Override
    public boolean checkExistedServiceAssign(String pstrCode) throws Exception {
        boolean vblnCheck = false;
        List<Service> services = findActiveServiceAssign();
        for (Service service : services) {
            if (pstrCode.equalsIgnoreCase(service.getCode().trim())) {
                vblnCheck = true;
                break;
            }
        }
        return vblnCheck;
    }

    /**
     * tim chi tieu theo code nhom chi tieu
     *
     * @param pstrGroupServiceCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public List<String> findActiveServiceCodeByGrCode(String pstrGroupServiceCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select s.code from Service s join GroupService g on s.groupServiceId=g.id and g.code =:groupServiceCode and s.status='1'");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("groupServiceCode", pstrGroupServiceCode);

        return query.getResultList();
    }

    /**
     * kiem tra ton tai chi tieu trong nhom chi tieu
     *
     * @param pstrGroupServiceCode
     * @param pstrServiceCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public boolean checkExistedServiceInGr(String pstrGroupServiceCode, String pstrServiceCode) throws Exception {
        boolean vblnCheck = false;
        for (String service : findActiveServiceCodeByGrCode(pstrGroupServiceCode)) {
            if (pstrServiceCode.equalsIgnoreCase(service)) {
                vblnCheck = true;
                break;
            }
        }
        return vblnCheck;
    }

    @Override
    public boolean checkServiceInChannel(String pstrChannelCode, String pstrServiceCode) throws Exception {
        boolean vblnCheck = false;
        Long vlngServiceId = findServiceIdByCode(pstrServiceCode.trim());
        List<ServiceChannel> vlstServiceChannels = serviceChannelRepo.findByChannelCode(pstrChannelCode);
        for (ServiceChannel serviceChannel : vlstServiceChannels) {
            if (vlngServiceId.equals(serviceChannel.getServiceId())) {
                vblnCheck = true;
                break;
            }
        }

        return vblnCheck;
    }

    /**
     * lay ra ngay from_date
     *
     * @param : id trong bang service
     * @return : from_date trong service
     * @author: hungnn
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/16
     */
    @Override
    public Date getFromDate(Long serviceId) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select s.from_date from service s");
        vsbdSqlBuilder.append(builder(serviceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        List<Date> vlstDates = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstDates)) return null;
        return vlstDates.get(0);
    }

    /**
     * lay ra ngay to_date
     *
     * @param : id trong bang service
     * @return : to_date trong service
     * @author: hungnn
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/16
     */
    @Override
    public Date getToDate(Long serviceId) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select s.to_date from service s");
        vsbdSqlBuilder.append(builder(serviceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        List<Date> vlstDates = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstDates)) return null;
        return vlstDates.get(0);
    }

    /**
     * tinh so ngay truoc ngay: FROM_DATE - value (trong ap_param)
     *
     * @param : id trong bang service
     * @return : date truoc ngay FROM_DATE voi gia tri : FROM_DATE - value
     * @author: hungnn
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/16
     */
    @Override
    public Date getValueBefore(Long serviceId) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select SUBDATE(from_date, INTERVAL(select a.value from ap_param a ")
                .append(" where a.type = 'ASSIGN_ONTIME' and a.code = 'BEFORE' ) DAY )")
                .append(" from service s ");
        vsbdSqlBuilder.append(builder(serviceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (Date) query.getSingleResult();
    }

    /**
     * tinh so ngay truoc ngay: dau thang hien tai - value (trong ap_param)
     *
     * @param : id trong bang service, ngay hien tai
     * @return : date truoc ngay dau thang hien tai voi gia tri : sysdate(01/mm/yyyy) - value
     * @author: HungNN
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/23
     */
    @Override
    public Date getValueBeforeFirstDay(Long plngServiceId, Long pdtPrd) {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select SUBDATE(cast(:prdId as date), INTERVAL(select a.value from ap_param a ")
                .append(" where a.type = 'ASSIGN_ONTIME' and a.code = 'BEFORE' ) DAY )")
                .append(" from service s ");
        vsbdSqlBuilder.append(builder(plngServiceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.put("prdId", pdtPrd);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (Date) query.getSingleResult();
    }

    /**
     * tinh so ngay sau ngay: dau thang hien tai +value (trong ap_param)
     *
     * @param : id trong bang service, ngay hien tai
     * @return : date sau ngay dau thang hien tai voi gia tri : sysdate(01/mm/yyyy) + value
     * @author: HungNN
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/23
     */
    @Override
    public Date getValueAfterFirstDay(Long plngServiceId, Long pdtPrd) {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select ADDDATE(cast(:prdId as date), INTERVAL(select a.value from ap_param a ")
                .append(" where a.type = 'ASSIGN_ONTIME' and a.code = 'AFTER' ) DAY )")
                .append(" from service s ");
        vsbdSqlBuilder.append(builder(plngServiceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.put("prdId", pdtPrd);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (Date) query.getSingleResult();
    }

    /**
     * tinh so ngay sau ngay: FROM_DATE + value (trong ap_param)
     *
     * @param : id trong bang service
     * @return : date sau ngay FROM_DATE voi gia tri : FROM_DATE + value
     * @author: hungnn
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/16
     */
    @Override
    public Date getValueAfter(Long plngServiceId) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select ADDDATE(from_date, INTERVAL(select a.value from ap_param a " +
                " where a.type = 'ASSIGN_ONTIME' and a.code = 'AFTER' ) DAY )" +
                " from service s ");
        vsbdSqlBuilder.append(builder(plngServiceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        return (Date) query.getSingleResult();
    }

    /**
     * tinh so ngay sao cho: ngay dau thang cua ngay hien tai - value(ap_param)
     *
     * @param : id trong bang service id
     * @return : date can lay
     * @author: hungnn
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/16
     */
    @Override
    public Date getValueValidateBefore(Long plngServiceId, Date pdtDate) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select SUBDATE((SUBDATE(date_add(LAST_DAY(:date),interval 1 DAY), interval 1 month)), ")
                .append(" INTERVAL(select a.value from ap_param a ")
                .append(" where a.type = 'ASSIGN_ONTIME' and a.code = 'BEFORE' ) DAY) from service s ");
        vsbdSqlBuilder.append(builder(plngServiceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("date", pdtDate);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (Date) query.getSingleResult();
    }

    /**
     * tinh so ngay sao cho: ngay dau quy cua ngay hien tai - value(ap_param)
     *
     * @param : id trong bang service id
     * @return : date can lay
     * @author: hungnn
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/16
     */
    @Override
    public Date getValueBeforeByQuarter(Long plngServiceId, Date pdtDate) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select SUBDATE(MAKEDATE(YEAR(:date), 1) + INTERVAL QUARTER(:date) QUARTER - INTERVAL 1 QUARTER, ")
                .append(" INTERVAL(select a.value from ap_param a ")
                .append(" where a.type = 'ASSIGN_ONTIME' and a.code = 'BEFORE' ) DAY) from service s ");
        vsbdSqlBuilder.append(builder(plngServiceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("date", pdtDate);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (Date) query.getSingleResult();
    }

    @Override
    public Date getValueBeforeByYear(Long plngServiceId, Date pdtDate) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select SUBDATE(MAKEDATE(YEAR(:date), 1) , ")
                .append(" INTERVAL(select a.value from ap_param a ")
                .append(" where a.type = 'ASSIGN_ONTIME' and a.code = 'BEFORE' ) DAY) from service s ");
        vsbdSqlBuilder.append(builder(plngServiceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("date", pdtDate);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (Date) query.getSingleResult();
    }

    /**
     * tinh so ngay sao cho: ngay dau thang cua from_date + value(ap_param)
     *
     * @param : (khong)
     * @return : date can lay
     * @author: hungnn
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/16
     */
    @Override
    public Date getValueValidateAfter(Long plngServiceId, Date pdtDate) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select ADDDATE((SUBDATE(date_add(LAST_DAY(:date),interval 1 DAY), interval 1 month)), ")
                .append(" INTERVAL(select a.value from ap_param a ")
                .append(" where a.type = 'ASSIGN_ONTIME' and a.code = 'AFTER' ) DAY) from service s ");
        vsbdSqlBuilder.append(builder(plngServiceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("date", pdtDate);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (Date) query.getSingleResult();
    }

    @Override
    public Date getValueAfterByQuerter(Long plngServiceId, Date pdtDate) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select ADDDATE(MAKEDATE(YEAR(:date), 1) + INTERVAL QUARTER(:date) QUARTER - INTERVAL 1 QUARTER, ")
                .append(" INTERVAL(select a.value from ap_param a ")
                .append(" where a.type = 'ASSIGN_ONTIME' and a.code = 'AFTER' ) DAY) from service s ");
        vsbdSqlBuilder.append(builder(plngServiceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("date", pdtDate);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (Date) query.getSingleResult();
    }

    @Override
    public Date getValueAfterByYear(Long plngServiceId, Date pdtDate) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select ADDDATE(MAKEDATE(YEAR(:date), 1), ")
                .append(" INTERVAL(select a.value from ap_param a ")
                .append(" where a.type = 'ASSIGN_ONTIME' and a.code = 'AFTER' ) DAY) from service s ");
        vsbdSqlBuilder.append(builder(plngServiceId, vhmpMapParams));
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("date", pdtDate);
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (Date) query.getSingleResult();
    }

    /**
     * dem tong so chi tieu
     * <p>
     * param : (khong)
     *
     * @return : tong so chi tieu
     * @author HungNN
     * @since 2019/10/29
     */
    @Override
    public BigInteger countService() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select count(id) from service");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        return (BigInteger) query.getSingleResult();
    }

    /**
     * dieu kien loc ra chi tieu
     *
     * @param serviceDTO
     * @param phmpParams
     * @return
     * @author VuBL
     * @since 2019/09
     */
    private StringBuilder buildSQL(ServiceDTO serviceDTO, HashMap phmpParams) {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        vsbdStringBuilder.append(" where 1=1 ");
        vsbdStringBuilder.append(" and status = 1");
        vsbdStringBuilder.append(" and serviceType = 1 ");

        if (!DataUtil.isNullOrZero(serviceDTO.getGroupServiceId())) {
            vsbdStringBuilder.append(" and s.groupServiceId = :groupServiceId");
            phmpParams.put("groupServiceId", serviceDTO.getGroupServiceId());
        }

        if (!DataUtil.isNullOrEmpty(serviceDTO.getCongVan())) {
            vsbdStringBuilder.append(" and s.congVan like :congVan");
            phmpParams.put("congVan", "%" + serviceDTO.getCongVan() + "%");
        }

        if (!DataUtil.isNullOrEmpty(serviceDTO.getName())) {
            vsbdStringBuilder.append(" and s.name like :name");
            phmpParams.put("name", "%" + serviceDTO.getName().trim() + "%");
        }

        if (!DataUtil.isNullOrEmpty(serviceDTO.getVdsChannelCode())) {
            vsbdStringBuilder.append(" and s.id in (select serviceId from ServiceChannel ")
                    .append("where vdsChannelCode = :vdsChannelCode)");
            phmpParams.put("vdsChannelCode", serviceDTO.getVdsChannelCode());
        }
        return vsbdStringBuilder;
    }

    /**
     * dieu kien cho cac ham ben tren
     *
     * @param : serviceId : id cua bang service
     * @return : dieu kien de append vao cau query
     * @author: hungnn
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/16
     */
    private StringBuilder builder(Long plngServiceId, HashMap phmpParams) {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        vsbdStringBuilder.append(" where 1=1 ");

        if (!DataUtil.isNullOrZero(plngServiceId)) {
            vsbdStringBuilder.append(" and s.id = :serviceId");
            phmpParams.put("serviceId", plngServiceId);
        }
        return vsbdStringBuilder;
    }

    @Override
    public List<Object[]> getLogOfServiceByServiceId(Long idService) {

        if (!DataUtil.isNullOrZero(idService)) {
            StringBuilder vsbdStringBuilder = new StringBuilder();
            vsbdStringBuilder.append("SELECT * FROM ( ");
            vsbdStringBuilder.append(" (SELECT action_audit.action_code, action_audit.pk_id, action_audit.action_datetime AS dateCreate, action_audit.user, action_detail.column_name, ");
            vsbdStringBuilder.append(" action_detail.old_value, action_detail.new_value,action_audit.object_code, action_detail.action_audit_id ");
            vsbdStringBuilder.append(" FROM action_audit JOIN action_detail ON action_audit.id = action_detail.action_audit_id");
            vsbdStringBuilder.append(" WHERE action_audit.pk_id =:idService AND action_audit.action_code = '01'");
            vsbdStringBuilder.append(" AND (action_audit.object_code = 'Service')");
            vsbdStringBuilder.append(" ORDER BY dateCreate DESC)");
            vsbdStringBuilder.append(" UNION");
            vsbdStringBuilder.append(" (SELECT action_audit.action_code, action_audit.pk_id, action_audit.action_datetime AS dateCreate, action_audit.user, action_detail.column_name, ");
            vsbdStringBuilder.append(" action_detail.old_value, action_detail.new_value,action_audit.object_code , action_detail.action_audit_id");
            vsbdStringBuilder.append(" FROM action_audit JOIN action_detail ON action_audit.id = action_detail.action_audit_id");
            vsbdStringBuilder.append(" WHERE action_audit.action_code = '00' AND action_audit.pk_id =:idService AND action_audit.object_code = 'Service'");
            vsbdStringBuilder.append(" GROUP BY action_detail.action_audit_id");
            vsbdStringBuilder.append(" ORDER BY dateCreate DESC)");
            vsbdStringBuilder.append(" )as UnionTable");
            vsbdStringBuilder.append(" ORDER BY dateCreate DESC");

            Query query = entityManager.createNativeQuery(vsbdStringBuilder.toString());
            query.setParameter("idService", idService);
            return query.getResultList();

        }
        return null;
    }

    @Override
    public List<Service> findAllByCode(String mstrCode) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT s FROM Service s WHERE s.status = 1 AND s.code LIKE :code");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("code", mstrCode);
        return query.getResultList();
    }

    @Override
    public List<Service> findServiceByOrder(Long serviceOrder) {

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT s FROM Service s WHERE s.serviceOrder LIKE :serviceOrder");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("serviceOrder", serviceOrder);

        return query.getResultList();
    }

    @Override
    public List<Service> checkServiceParenID(Long serviceID) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("WITH RECURSIVE partner as (SELECT * FROM service WHERE service.id = :serviceID " +
                "                                UNION SELECT m.* FROM service m, partner as p WHERE p.id = m.parent_id)\n" +
                "                             SELECT * FROM partner where status = '1'");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("serviceID", serviceID);

        List<Object[]> vlstServiceObject = query.getResultList();
        List<Service> vlstService = new ArrayList<>();
        for (Object[] objects : vlstServiceObject) {
            Service service = new Service();
            service.setId(DataUtil.safeToLong(objects[0]));
            service.setCode(DataUtil.safeToString(objects[2]));
            vlstService.add(service);
        }
        return vlstService;
    }

    /**
     * lay ra cac chi tieu con
     *
     * @author VuBL
     * @param plngServiceId
     * @return
     * @throws Exception
     */
    @Override
    public List<Object[]> findChildServiceNew(Long plngServiceId) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("SELECT id, NAME FROM service WHERE parent_id = :serviceId");

        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("serviceId", plngServiceId);

        List<Object[]> vlstData = query.getResultList();
        return vlstData;
    }

    @Override
    public Long findParentIdById(Long id) throws Exception {
        if (id != null) {
            StringBuilder sqlBuilder = new StringBuilder();

            sqlBuilder.append("select parent_id from service where id = :id");

            Query query = entityManager.createNativeQuery(sqlBuilder.toString());
            query.setParameter("id", id);

            List<Long> vlstData = query.getResultList();
            if (vlstData != null) {
                return DataUtil.safeToLong(vlstData.get(0));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
