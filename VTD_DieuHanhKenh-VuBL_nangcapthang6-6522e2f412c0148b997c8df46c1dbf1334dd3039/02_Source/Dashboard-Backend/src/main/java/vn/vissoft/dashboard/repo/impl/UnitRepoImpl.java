package vn.vissoft.dashboard.repo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.vissoft.dashboard.dto.ShopUnitDTO;
import vn.vissoft.dashboard.dto.UnitDTO;
import vn.vissoft.dashboard.helper.DashboardRequest;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigRoles;
import vn.vissoft.dashboard.model.Unit;
import vn.vissoft.dashboard.repo.UnitRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class UnitRepoImpl implements UnitRepoCustom {

    @PersistenceContext
    EntityManager entityManager;
    DashboardRequest dashboardRequest = new DashboardRequest();

    private static final Logger LOGGER = LogManager.getLogger(ConfigRolesRepoImpl.class);

    /**
     * tim don vi theo code
     *
     * @author DatNT
     * @since 13/09/2019
     * @param pstrCode
     * @return
     */
//    @Override
//    public Long findUnitIdByCode(String pstrCode) {
//        StringBuilder vsbdSqlBuilder = new StringBuilder();
//        vsbdSqlBuilder.append("select u.id from Unit u where u.code=:unitCode");
//        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
//        query.setParameter("unitCode", pstrCode);
//        List<Long> vlstChannelIds = query.getResultList();
//        return vlstChannelIds.get(0);
//    }
//
//    @Override
//    public boolean checkExistedUnitCode(String pstrCode) {
//
//        boolean vblnCheck = false;
//        for (ManageInfoPartner unit : findActiveUnit()) {
//            if (pstrCode.equalsIgnoreCase(unit.getShopCode())) {
//                vblnCheck = true;
//                break;
//            }
//        }
//        return vblnCheck;
//    }
//
//    @Override
//    public List<ManageInfoPartner> findActiveUnit() {
//        StringBuilder vsbdSqlBuilder = new StringBuilder();
//
//        vsbdSqlBuilder.append("select u from Unit u where u.status='1'");
//        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
//
//        return query.getResultList();
//    }

    /**
     * đếm tổng số đơn vị theo id để tính phần trăm
     *
     * @author: hungnn
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2019/10/01
     * @param
     * @return : tổng số đơn vị
     */
    public BigInteger totalUnit(){
        HashMap mapParams = new HashMap();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT COUNT(unit_id) FROM unit");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        mapParams.forEach((k,v)->{
            query.setParameter(k.toString(),v);
        });
        return (BigInteger) query.getSingleResult();
    }

    /**
     * lấy tên đơn vị theo mã đơn vị
     *
     * @author VuBL
     * @since 2019/10/15
     * @param plngUnitId
     * @return String là tên đơn vị trả về
     */
    @Override
    public String findNameByUnitId(Long plngUnitId) {
        HashMap vhmpParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select name from unit where unit_id = :unitId");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("unitId", plngUnitId);
        vhmpParams.forEach((k,v)->{
            query.setParameter(k.toString(),v);
        });
        String vstrData = (String) query.getSingleResult();
        if (DataUtil.isNullOrEmpty(vstrData)) {
            return null;
        }
        return vstrData;
    }

    @Override
    public List<Unit> getAllByStatus() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT u FROM Unit u WHERE u.status LIKE '1' order by case when u.name like 'đ%' then 'd' else u.name end");
        return entityManager.createQuery(sqlBuilder.toString()).getResultList();
    }


    @Override
    public Optional<Unit> getUnitByCode(String code) {
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT u FROM Unit u WHERE u.status = 1 AND u.code LIKE :code order by case when u.name like 'đ%' then 'd' else u.name end");
            Query query = entityManager.createQuery(sqlBuilder.toString());
            query.setParameter("code", code);

            return  query.getResultList().stream().findFirst();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Unit> getByConditon(UnitDTO unitDTO) {
        StringBuilder hql = new StringBuilder();
        HashMap vhmpMap = new HashMap();
        hql.append(buildSQL(unitDTO, vhmpMap));
        Query query = entityManager.createQuery(hql.toString());
        vhmpMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        List<Unit> list = query
                .setFirstResult((unitDTO.getPager().getPage() - 1) * unitDTO.getPager().getPageSize())
                .setMaxResults(unitDTO.getPager().getPageSize())
                .getResultList();
        return list;
    }

    @Override
    public Long countByCondition(UnitDTO unitDTO) {
        StringBuilder hql = new StringBuilder();
        HashMap vhmpMap = new HashMap();
        hql.append("select count(id) ");
        hql.append(buildSQL(unitDTO, vhmpMap));
        Query query = entityManager.createQuery(hql.toString());
        vhmpMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (Long) query.getSingleResult();
    }

    private StringBuilder buildSQL(UnitDTO unitDTO, HashMap hashMap) {
        StringBuilder vbdBuilder = new StringBuilder();
        vbdBuilder.append(" from Unit as s where 1=1 ");
        if (!DataUtil.isNullOrEmpty(unitDTO.getName())) {
            vbdBuilder.append("and s.name like CONCAT('%',:name,'%') ");
            hashMap.put("name", unitDTO.getName());
        }
        if (!DataUtil.isNullOrEmpty(unitDTO.getCode())) {
            vbdBuilder.append("and s.code like CONCAT('%',:code,'%') ");
            hashMap.put("code", unitDTO.getCode());
        }
        if (unitDTO.getRate() != null) {
            vbdBuilder.append("and s.rate = :rate ");
            hashMap.put("rate", unitDTO.getRate());
        }
        if (!DataUtil.isNullOrEmpty(unitDTO.getStatus())) {
            vbdBuilder.append("and s.status = :status ");
            hashMap.put("status", unitDTO.getStatus());
        }
        vbdBuilder.append("order by s.id desc");
        return vbdBuilder;
    }
}
