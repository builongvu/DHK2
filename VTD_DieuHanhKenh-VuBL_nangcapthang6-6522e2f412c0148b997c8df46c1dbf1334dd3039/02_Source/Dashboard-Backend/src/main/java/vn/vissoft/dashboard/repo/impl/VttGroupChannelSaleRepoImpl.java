package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.model.VttGroupChannelSale;
import vn.vissoft.dashboard.repo.VttGroupChannelSaleRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

public class VttGroupChannelSaleRepoImpl implements VttGroupChannelSaleRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    @Override
    public void persist(VttGroupChannelSale vttGroupChannelSale) throws Exception {
        entityManager.persist(vttGroupChannelSale);
    }
}
