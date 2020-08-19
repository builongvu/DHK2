package vn.vissoft.dashboard.repo.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.vissoft.dashboard.dto.GroupServiceDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.GroupService;
import vn.vissoft.dashboard.repo.GroupServiceRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class GroupServiceRepoImpl implements GroupServiceRepoCustom {

    @PersistenceContext
    EntityManager entityManager;
//    private BaseMapper<GroupService, GroupServiceDTO> mapper = new BaseMapper<>();

    @Override
    public List<GroupService> findGroupServicesByCondition(GroupServiceDTO groupServiceDTO) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select g from GroupService g ");
        vsbdSqlBuilder.append(buildSQL1(groupServiceDTO, vhmpMapParams));
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();
    }

    @Override
    public List<GroupService> findActiveGroupService() {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append(" select g from GroupService g where g.status='1'");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    @Override
    public List<String> findGrCodeByProductCode(String pstrProductCode) {
        return null;
    }

    /**
     * tim code nhom chi tieu theo code san pham
     *
     * @param pstrCode
     * @return
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public boolean checkExistedGroupServiceCode(String pstrCode) {
        boolean vblnCheck = false;
        for (GroupService groupService : findActiveGroupService()) {
            if (pstrCode.equalsIgnoreCase(groupService.getCode())) {
                vblnCheck = true;
                break;
            }
        }
        return vblnCheck;
    }

    /**
     * tim nhom chi tieu theo san pham
     *
     * @param pstrProductCode
     * @return
     */
    @Override
    public List<String> findActiveGroupServiceCodeByProductCode(String pstrProductCode) {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select g.code from GroupService g join Product p on g.productId=p.id and p.code =:productCode and g.status='1'");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("productCode", pstrProductCode);

        return query.getResultList();
    }

    /**
     * kiem tra su ton tai cua nhom chi tieu trong cac nhom chi tieu tim dc theo san [ham
     *
     * @param pstrProductCode
     * @param pstrGroupServiceCode
     * @return
     */
    @Override
    public boolean checkExistedGroupServiceInProduct(String pstrProductCode, String pstrGroupServiceCode) {
        boolean vblnCheck = false;
        for (String groupService : findActiveGroupServiceCodeByProductCode(pstrProductCode)) {
            if (pstrGroupServiceCode.equalsIgnoreCase(groupService)) {
                vblnCheck = true;
                break;
            }
        }
        return vblnCheck;
    }

    @Transactional
    @Override
    public void persist(GroupService groupService) throws Exception {
        entityManager.persist(groupService);
    }

    @Transactional
    @Override
    public void update(GroupService groupService) throws Exception {
        entityManager.merge(groupService);
    }

    @Override
    public Long findIdByCode(String pstrCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select p.id from GroupService p where p.code =:code");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("code", pstrCode);
        List<Long> vlstIds = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstIds)) return null;
        return vlstIds.get(0);
    }

    @Override
    public List<GroupService> findActiveGroupCode(String pstrCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append(" select g from GroupService g where g.status='1' and g.code =:code");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        if(!DataUtil.isNullOrEmpty(pstrCode))
        query.setParameter("code",pstrCode.trim());

        return query.getResultList();
    }

    @Qualifier
    public List<GroupServiceDTO> getByCondition(GroupService groupServiceDTO, int page, int size) throws Exception {
        StringBuilder grhql = new StringBuilder();
        HashMap vhmHashMap = new HashMap();
        grhql.append("select DISTINCT g.id,g.product_id ,g.code,g.name as group_name,g.create_date,g.user,g.status,p.name as product_name from group_service g  join  product p on  g.product_id=p.id ");
        grhql.append(" where 1=1 ");
        if (!DataUtil.isNullOrZero(groupServiceDTO.getProductId())) {
            grhql.append(" and g.product_id = :productId");
            vhmHashMap.put("productId", groupServiceDTO.getProductId());
        }
        if (!DataUtil.isNullOrEmpty(groupServiceDTO.getCode())) {
            grhql.append(" and LOWER(g.code) like concat('%',CONVERT(LOWER(:code),BINARY),'%') ");
            vhmHashMap.put("code", "%" + groupServiceDTO.getCode().trim() + "%");
        }
        if (!DataUtil.isNullOrEmpty(groupServiceDTO.getName())) {
            grhql.append("  and LOWER(g.name) like concat('%',CONVERT(LOWER(:name),BINARY),'%')");
            vhmHashMap.put("name", groupServiceDTO.getName().trim());
        }
        if (!DataUtil.isNullOrEmpty(groupServiceDTO.getStatus())) {
            grhql.append("  and g.status =:status");
            vhmHashMap.put("status", groupServiceDTO.getStatus());
        }
        grhql.append(" order by g.id desc");
        Query query = entityManager.createNativeQuery(grhql.toString());
        vhmHashMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        query.setFirstResult((page - 1) * size).setMaxResults(size).getResultList();

        List<Object[]> grObj = query.getResultList();
        List<GroupServiceDTO> vstlGroup = new ArrayList<>();
        for (Object[] grObjs : grObj) {
            GroupServiceDTO groupService = new GroupServiceDTO();
            groupService.setId(Long.parseLong(grObjs[0].toString()));
            groupService.setProductName(grObjs[7].toString());
            groupService.setProductId(Long.parseLong(grObjs[1].toString()));
            groupService.setCode(grObjs[2].toString());
            groupService.setName(grObjs[3].toString());
            groupService.setStatus(grObjs[6].toString());
            groupService.setUserUpdate(grObjs[5].toString());
            groupService.setChangeDatetime((Timestamp) grObjs[4]);
            groupService.setChangeDatetime1(DataUtil.safeToString(new SimpleDateFormat("dd/MM/yyyy").format(grObjs[4])));
            vstlGroup.add(groupService);
        }
        return vstlGroup;
    }

    public BigInteger countByCondition(GroupService groupServiceDTO, int page, int size) throws Exception {
        StringBuilder grhql = new StringBuilder();
        HashMap vhmHashMap = new HashMap();
        grhql.append("select count(*) from group_service g  join  product p on  g.product_id=p.id ");
        if (!DataUtil.isNullOrZero(groupServiceDTO.getProductId())) {
            grhql.append(" and g.product_id = :productId");
            vhmHashMap.put("productId", groupServiceDTO.getProductId());
        }
        if (!DataUtil.isNullOrEmpty(groupServiceDTO.getCode())) {
            grhql.append(" and LOWER(g.code) like concat('%',CONVERT(LOWER(:code),BINARY),'%') ");
            vhmHashMap.put("code", "%" + groupServiceDTO.getCode().trim() + "%");
        }
        if (!DataUtil.isNullOrEmpty(groupServiceDTO.getName())) {
            grhql.append("  and LOWER(g.name) like concat('%',CONVERT(LOWER(:name),BINARY),'%')");
            vhmHashMap.put("name", groupServiceDTO.getName().trim());
        }
        if (!DataUtil.isNullOrEmpty(groupServiceDTO.getStatus())) {
            grhql.append("  and g.status =:status");
            vhmHashMap.put("status", groupServiceDTO.getStatus());
        }
        grhql.append(" order by g.id desc");
        Query query = entityManager.createNativeQuery(grhql.toString());
        vhmHashMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return (BigInteger) query.getSingleResult();
    }

    public BigInteger countGroupServiceGetByCondition(GroupServiceDTO groupServiceDTO) {
        StringBuilder grhql = new StringBuilder();
        grhql.append("select DISTINCT g.id,p.mstrName,g.code,g.name,g.createdate,g.userupdate,g.status,g.productId  from GroupService g  join  Product p on" + "g.productId=p.mlngId");
        if (groupServiceDTO.getProductId() != null) {
            grhql.append(" and g.productId = ").append(groupServiceDTO.getProductId());
        }
        if (groupServiceDTO.getCode() != null) {
            grhql.append(" and g.code like \'%").append(groupServiceDTO.getCode()).append("%\'");
        }
        if (groupServiceDTO.getName() != null) {
            grhql.append("  and g.name like \'%").append(groupServiceDTO.getName()).append("%\'");
        }
        grhql.append(" order by g.id desc");
        return (BigInteger) entityManager.createNativeQuery(grhql.toString()).getSingleResult();
    }

    @Override
    public List<GroupServiceDTO> findAllReturnProductName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select DISTINCT g.id,p.mstrName,g.code,g.name,g.createdate,g.userupdate,g.status,g.productId  from GroupService g  join  Product p on " +
                " g.productId=p.mlngId");
        Query query = entityManager.createQuery(stringBuilder.toString());
        List<GroupServiceDTO> lstReturn = new ArrayList<>();
        List<Object[]> result = query.getResultList();
        if (result != null) {
            result.forEach(item -> {
                GroupServiceDTO dto = new GroupServiceDTO();
                dto.setId(DataUtil.safeToLong(item[0]));
                dto.setProductName(DataUtil.safeToString(item[1]));
                dto.setCode(DataUtil.safeToString(item[2]));
                dto.setName(DataUtil.safeToString(item[3]));
                dto.setChangeDatetime((Timestamp) DataUtil.safeToDate(item[4]));
                dto.setUserUpdate(DataUtil.safeToString(item[5]));
                dto.setStatus(DataUtil.safeToString(item[6]));
                dto.setChangeDatetime1(DataUtil.safeToString(new SimpleDateFormat("dd/MM/yyyy").format(item[4])));
                dto.setProductId(DataUtil.safeToLong(item[7]));
                lstReturn.add(dto);
                System.out.println(dto.getId());
            });
        }

        return lstReturn;
    }

    //test
    public List<GroupService> getGroupService() {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("from GroupService");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    @Override
    public List<GroupServiceDTO> findGroupServiceByCondition(GroupServiceDTO groupServiceDTO) {
        HashMap hashMap = new HashMap();
        if (groupServiceDTO == null) System.out.println("No parameter");
        List<GroupServiceDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select DISTINCT g.id,p.mstrName,g.code,g.name,g.createdate,g.userupdate,g.status,g.productId  from GroupService g  join  Product p on " +
                "g.productId=p.mlngId ");
        if (groupServiceDTO != null) {
            sql.append(buildSQL1(groupServiceDTO, hashMap));
        }
        Query query = entityManager.createQuery(sql.toString());
        List<Object[]> result = query.getResultList();
        System.out.println("result is empty :" + result.isEmpty());
        if (!DataUtil.isNullOrEmpty(result)) {
            for (Object[] item : result) {
                GroupServiceDTO dto = new GroupServiceDTO();
                dto.setId(DataUtil.safeToLong(item[0]));
                dto.setProductName(DataUtil.safeToString(item[1]));
                dto.setCode(DataUtil.safeToString(item[2]));
                dto.setName(DataUtil.safeToString(item[3]));
                dto.setChangeDatetime((Timestamp) DataUtil.safeToDate(item[4]));
                dto.setUserUpdate(DataUtil.safeToString(item[5]));
                dto.setStatus(DataUtil.safeToString(item[6]));
                if (null != item[4]) {
                    dto.setChangeDatetime1(String.format(DataUtil.safeToString(new SimpleDateFormat("dd/MM/yyyy").format(((Timestamp) DataUtil.safeToDate(item[4])).getTime()))));
                }
                dto.setProductId(DataUtil.safeToLong(item[7]));

                list.add(dto);
            }

        }
        System.out.println(" SQl string " + sql.toString());
        return list;
    }

    private StringBuilder buildSQL1(GroupServiceDTO groupServiceDTO, HashMap params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("where 1=1");
        if (!DataUtil.isNullOrZero(groupServiceDTO.getProductId())) {
            stringBuilder.append(" and g.productId = :productId");
            params.put("productId", groupServiceDTO.getProductId());
        }
        if (!DataUtil.isNullOrEmpty(groupServiceDTO.getCode())) {
            stringBuilder.append(" and g.code like :gCode");
            params.put("gCode", groupServiceDTO.getCode());
        }
        if (!DataUtil.isNullOrEmpty(groupServiceDTO.getName())) {
            stringBuilder.append(" and g.name like :gName");
            params.put("gName", groupServiceDTO.getName());
        }
        if (!DataUtil.isNullOrEmpty(groupServiceDTO.getStatus())) {
            stringBuilder.append(" and g.status = :status");
            params.put("status", groupServiceDTO.getStatus());
        }

        return stringBuilder;

    }

    private StringBuilder buildSQL(GroupServiceDTO groupServiceDTO, HashMap phmpParams) {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        vsbdStringBuilder.append(" where 1=1 ");
        if (!DataUtil.isNullOrZero(groupServiceDTO.getProductId())) {
            vsbdStringBuilder.append(" and g.productId = :productId");
            phmpParams.put("productId", groupServiceDTO.getProductId());
        }
        return vsbdStringBuilder;
    }
}
