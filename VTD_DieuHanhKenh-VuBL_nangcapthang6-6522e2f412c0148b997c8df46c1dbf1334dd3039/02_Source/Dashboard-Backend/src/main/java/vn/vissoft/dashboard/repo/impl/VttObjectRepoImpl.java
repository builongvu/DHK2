package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.model.VttObject;
import vn.vissoft.dashboard.repo.VttObjectRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class VttObjectRepoImpl implements VttObjectRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<VttObject> findActiveVttObject() throws Exception {
        StringBuilder sbdBuilder = new StringBuilder();
        sbdBuilder.append("select v from VttObject v where status = '1'");
        Query query = entityManager.createQuery(sbdBuilder.toString());
        List<VttObject> vlstVttObjects = query.getResultList();

        return vlstVttObjects;
    }
}
