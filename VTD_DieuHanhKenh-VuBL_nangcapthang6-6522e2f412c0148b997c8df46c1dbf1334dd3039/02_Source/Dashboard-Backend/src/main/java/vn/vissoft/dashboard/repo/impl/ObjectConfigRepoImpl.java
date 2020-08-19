package vn.vissoft.dashboard.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigObjects;
import vn.vissoft.dashboard.model.VttGroupChannel;
import vn.vissoft.dashboard.repo.ObjectConfigRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ObjectConfigRepoImpl implements ObjectConfigRepoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ConfigObjects> getAllChild(Long id) throws Exception {
        StringBuilder vbdBuilder = new StringBuilder();
        vbdBuilder.append("WITH RECURSIVE object as (SELECT * FROM config_objects WHERE config_objects.id = ");
        vbdBuilder.append(id).append(" UNION SELECT m.* FROM config_objects m, object as p WHERE p.id = m.parent_id)\n" +
                "                             SELECT * FROM object where status = '1';");
        List<Object[]> vlstMappingChannels = entityManager.createNativeQuery(vbdBuilder.toString()).getResultList();
        List<ConfigObjects> listob = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(vlstMappingChannels)) {
            for (Object[] object : vlstMappingChannels) {
                ConfigObjects configObjects = new ConfigObjects();
                configObjects.setId(Long.parseLong(object[0].toString()));
                configObjects.setObjectCode(object[1].toString());
                configObjects.setObjectName(object[2].toString());
                listob.add(configObjects);
            }
        }
        return listob;
    }
}
