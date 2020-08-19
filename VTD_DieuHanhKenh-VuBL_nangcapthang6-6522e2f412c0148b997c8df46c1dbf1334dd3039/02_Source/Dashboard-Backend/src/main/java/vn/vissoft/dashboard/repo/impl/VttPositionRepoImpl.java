package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.model.VttPosition;
import vn.vissoft.dashboard.repo.VttPositionRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

public class VttPositionRepoImpl implements VttPositionRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    @Override
    public void persist(VttPosition vttPosition) throws Exception {
        entityManager.persist(vttPosition);
    }
}
