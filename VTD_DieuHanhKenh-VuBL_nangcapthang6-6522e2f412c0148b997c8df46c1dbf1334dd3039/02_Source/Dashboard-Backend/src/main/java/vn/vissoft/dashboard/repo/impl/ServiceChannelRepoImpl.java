package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ServiceChannel;
import vn.vissoft.dashboard.repo.ServiceChannelRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;

public class ServiceChannelRepoImpl implements ServiceChannelRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<ServiceChannel> findByChannelCode(String pstrChannelCode ) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();
        vsbdSqlBuilder.append("select sc from ServiceChannel sc where 1=1 ");
        if (pstrChannelCode != null) {
            vsbdSqlBuilder.append(" and vdsChannelCode =:vdsChannelCode");
            vhmpMapParams.put("vdsChannelCode", pstrChannelCode);
        } else {
            vsbdSqlBuilder.append(" and vdsChannelCode in (select vdsChannelCode from ManageInfoPartner where parentShopCode ='VDS')");
        }

        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<ServiceChannel> vlstServiceChannels = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstServiceChannels)) return null;
        return vlstServiceChannels;
    }
}
