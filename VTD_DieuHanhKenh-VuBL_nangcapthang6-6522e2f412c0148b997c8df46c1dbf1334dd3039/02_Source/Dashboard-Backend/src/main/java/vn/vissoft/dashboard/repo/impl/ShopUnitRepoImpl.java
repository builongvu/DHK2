package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.dto.MappingGroupChannelDTO;
import vn.vissoft.dashboard.dto.ShopUnitDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Shop;
import vn.vissoft.dashboard.model.ShopUnit;
import vn.vissoft.dashboard.repo.ShopUnitRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopUnitRepoImpl implements ShopUnitRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<ShopUnit> getAllByCondition(ShopUnitDTO shopUnitDTO) throws ParseException {
        StringBuilder hql = new StringBuilder();
        HashMap vhmpMap = new HashMap();
        hql.append("select s.id, s.vds_channel_code, s.shop_code, s.service_id, s.unit_code, s.from_date, s.to_date, s.`status`");
        hql.append(buildSQL(shopUnitDTO, vhmpMap));
        Query query = entityManager.createNativeQuery(hql.toString());
        vhmpMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        List<Object[]> vlistObj = query
                .setFirstResult((shopUnitDTO.getPager().getPage() - 1) * shopUnitDTO.getPager().getPageSize())
                .setMaxResults(shopUnitDTO.getPager().getPageSize())
                .getResultList();
        List<ShopUnit> vlist = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (!DataUtil.isNullOrEmpty(vlistObj)) {
            for (Object[] vobj : vlistObj) {
                ShopUnit shopUnit = new ShopUnit();
                BigInteger id = (BigInteger) vobj[0];
                shopUnit.setMlngId(id.longValue());
                if (vobj[1] != null) {
                    shopUnit.setMstrVdsChannelCode(vobj[1].toString());
                }
                shopUnit.setMstrShopCode(vobj[2].toString());
                BigInteger svid = (BigInteger) vobj[3];
                shopUnit.setMlngServiceId(svid.longValue());
                shopUnit.setMstrUnitCode(vobj[4].toString());
                shopUnit.setMdtFromDate(df.parse(vobj[5].toString()));
                if(vobj[6] != null){
                    shopUnit.setMdtToDate(df.parse(vobj[6].toString()));
                }
                shopUnit.setMstrStatus(vobj[7].toString());
                vlist.add(shopUnit);
            }
        }
        return vlist;
    }

    @Override
    public Long countByCondition(ShopUnitDTO shopUnitDTO) {
        StringBuilder hql = new StringBuilder();
        HashMap vhmpMap = new HashMap();
        hql.append("select count(s.id) ");
        hql.append(buildSQL(shopUnitDTO, vhmpMap));
        Query query = entityManager.createNativeQuery(hql.toString());
        vhmpMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        BigInteger totalrow = (BigInteger) query.getSingleResult();
        return totalrow.longValue();
    }

    private StringBuilder buildSQL(ShopUnitDTO shopUnitDTO, HashMap hashMap) {
        StringBuilder vbdBuilder = new StringBuilder();
        vbdBuilder.append(" from shop_unit as s " +
                "join service as e on s.service_id = e.id " +
                "join manage_info_partner as m on s.shop_code = m.shop_code " +
                "join unit as u on s.unit_code = u.code where e.status = 1 and m.status = 1 ");
        if (shopUnitDTO.getServiceId() != null) {
            vbdBuilder.append("and s.service_id = :serviceId ");
            hashMap.put("serviceId", shopUnitDTO.getServiceId());
        }
        if (!DataUtil.isNullOrEmpty(shopUnitDTO.getShopCode())) {
            vbdBuilder.append("and s.shop_code = :shopcode ");
            hashMap.put("shopcode", shopUnitDTO.getShopCode());
        }
        if (!DataUtil.isNullOrEmpty(shopUnitDTO.getUnitCode())) {
            vbdBuilder.append("and s.unit_code = :unitCode ");
            hashMap.put("unitCode", shopUnitDTO.getUnitCode());
        }
        if (!DataUtil.isNullOrEmpty(shopUnitDTO.getStatus())) {
            vbdBuilder.append("and s.status = :status ");
            hashMap.put("status", shopUnitDTO.getStatus());
        }
        vbdBuilder.append("order by s.id desc");
        return vbdBuilder;
    }

}
