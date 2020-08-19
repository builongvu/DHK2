package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Shop;
import vn.vissoft.dashboard.repo.ShopRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ShopRepoImpl implements ShopRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Shop> findByStatusAndOrder() throws Exception {
        StringBuilder vsblSql = new StringBuilder();
        vsblSql.append("select s from Shop s ");
        vsblSql.append("where status = '1' ");
        vsblSql.append("order by case when shopCode like 'đ%' then 'd' else shopCode end ");
        Query query = entityManager.createQuery(vsblSql.toString());
        List<Shop> vlstData = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

}
