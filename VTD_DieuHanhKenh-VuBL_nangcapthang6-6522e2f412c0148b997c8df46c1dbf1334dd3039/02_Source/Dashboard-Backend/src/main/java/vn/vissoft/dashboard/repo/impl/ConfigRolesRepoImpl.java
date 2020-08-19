package vn.vissoft.dashboard.repo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigRoles;
import vn.vissoft.dashboard.model.ConfigRolesObjects;
import vn.vissoft.dashboard.repo.ConfigRolesRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Repository
public class ConfigRolesRepoImpl implements ConfigRolesRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    private static final Logger LOGGER = LogManager.getLogger(ConfigRolesRepoImpl.class);

    @Override
    public List<ConfigRoles> findAllByStatus() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT c FROM ConfigRoles c");
        return entityManager.createQuery(sqlBuilder.toString()).getResultList();
    }

    @Override
    public Optional getRoleByCode(String code) {
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT c FROM ConfigRoles c WHERE c.roleCode = :code");
            Query query = entityManager.createQuery(sqlBuilder.toString());
            query.setParameter("code", code);

            return query.getResultList().stream().findFirst();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Object[]> getByCondition(String roleCode, String objectCode) {

        StringBuilder sqlBuilder = new StringBuilder();

        if (DataUtil.isNullOrEmpty(objectCode) && !DataUtil.isNullOrEmpty(roleCode)) {
            sqlBuilder.append("SELECT cr.id, cr.roleCode, cr.roleName, cr.roleDescription, cr.status ")
                    .append(" FROM ConfigRoles cr")
                    .append(" WHERE (1=1) ");
        } else {
            sqlBuilder.append("SELECT cr.id, cr.roleCode, cr.roleName, cr.roleDescription, cr.status ")
                    .append(" FROM ConfigRoles cr JOIN ConfigRolesObjects cro ON cr.id = cro.roleId ")
                    .append(" JOIN ConfigObjects co ON cro.objectId = co.id")
                    .append(" WHERE (1=1) ");
        }


        Map<String, Object> listParameter = new HashMap<>();

        if (!DataUtil.isNullOrEmpty(roleCode)) {
            sqlBuilder.append("  and (LOWER(cr.roleCode) like concat('%',CONVERT(LOWER(:roleCode),BINARY),'%') ");
            sqlBuilder.append("  or LOWER(cr.roleName) like concat('%',CONVERT(LOWER(:roleCode),BINARY),'%')) ");
            listParameter.put("roleCode", roleCode.trim());
        }

        if (!DataUtil.isNullOrEmpty(objectCode)) {
            sqlBuilder.append(" and (LOWER(co.objectCode) like concat('%',CONVERT(LOWER(:objectCode),BINARY),'%') ");
            sqlBuilder.append(" or LOWER(co.objectName) like concat('%',CONVERT(LOWER(:objectCode),BINARY),'%')) ");
            listParameter.put("objectCode", objectCode.trim());
        }

        sqlBuilder.append(" GROUP BY cr.roleCode")
                .append(" ORDER BY cr.roleName");

        Query query = entityManager.createQuery(sqlBuilder.toString());

        listParameter.forEach((k, v) -> {
            query.setParameter(k, v);
        });

        return query.getResultList();
    }

    @Override
    public List<Object[]> getActionOfRole(Long roleObjectId, Long objectId) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT cro.id, cro.objectId, cro.roleId, cro.status, cro.action")
                .append(" FROM ConfigRolesObjects cro")
                .append(" JOIN ConfigObjects co ON cro.objectId = co.id")
                .append(" WHERE cro.roleId = :roleObjectId AND cro.status = 1 ")
                .append(" AND co.id = :objectId");

        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("roleObjectId", roleObjectId);
        query.setParameter("objectId", objectId);
        return query.getResultList();
    }

    @Override
    public List<ConfigRolesObjects> getAllConfigRolesObjectsByIdRole(Long idRole) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT cro")
                .append(" FROM ConfigRolesObjects cro")
                .append(" WHERE cro.roleId = :idRole");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("idRole", idRole);
        return query.getResultList();
    }
}
