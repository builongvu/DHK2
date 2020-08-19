package vn.vissoft.dashboard.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import vn.vissoft.dashboard.dto.chart.ConfigGroupCardDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigGroupCard;
import vn.vissoft.dashboard.repo.ConfigGroupCardRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;

public class ConfigGroupCardRepoImpl implements ConfigGroupCardRepoCustom {

    @Autowired
    private EntityManager entityManager;

    /**
     * tim kiem nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @param configGroupCard
     * @return
     * @throws Exception
     */
    @Override
    public List<Object[]> findByCondition(ConfigGroupCardDTO configGroupCard) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        HashMap vhmpParams = new HashMap();

        vsbdSql.append("select group_id, group_code, default_cycle, group_name_i18n, group_name, vds_channel_code, shop_code, ");
        vsbdSql.append("(select shop_name from manage_info_partner where shop_code = c.shop_code), ");
        vsbdSql.append("(select name from ap_param where type = 'DEFAULT_CYCLE' and code = c.default_cycle), ");
        vsbdSql.append("case when ");
        vsbdSql.append("(c.vds_channel_code is null and c.shop_code = 'VDS') then ");
        vsbdSql.append("(select vds_channel_name from vds_group_channel where vds_channel_code is null) ");
        vsbdSql.append("else ");
        vsbdSql.append("(select vds_channel_name from vds_group_channel where vds_channel_code = c.vds_channel_code) end ");
//        vsbdSql.append("c.vds_channel_code is null then (select vds_channel_name from vds_group_channel where vds_channel_code is null) ");
//        vsbdSql.append("else ");
//        vsbdSql.append("(select vds_channel_name from vds_group_channel where vds_channel_code = c.vds_channel_code) end ");
        vsbdSql.append("from config_group_card c ");
        vsbdSql.append("where 1 = 1 ");
        if (configGroupCard.getGroupName() != null) {
            vsbdSql.append("and lower(group_name) like concat('%', convert(lower(:groupName), binary), '%') ");
            vhmpParams.put("groupName", configGroupCard.getGroupName().trim());
        }
        if (configGroupCard.getGroupCode() != null) {
            vsbdSql.append("and group_code like :groupCode ");
            vhmpParams.put("groupCode", '%' + configGroupCard.getGroupCode().trim() + '%');
        }
        if (configGroupCard.getVdsChannelCode() != null) {
            vsbdSql.append("and vds_channel_code = :vdsChannelCode ");
            vhmpParams.put("vdsChannelCode", configGroupCard.getVdsChannelCode().trim());
        }
        if (configGroupCard.getShopCode() != null) {
            vsbdSql.append("and shop_code = :shopCode ");
            vhmpParams.put("shopCode", configGroupCard.getShopCode().trim());
        }
        if (configGroupCard.getDefaultCycle() != null) {
            vsbdSql.append("and default_cycle = :defaultCycle ");
            vhmpParams.put("defaultCycle", configGroupCard.getDefaultCycle().trim());
        }
        vsbdSql.append("order by group_id desc ");

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        vhmpParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<Object[]> vlstData = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    /**
     * lay ra tat ca nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @return
     * @throws Exception
     */
    @Override
    public List<Object[]> findAllGroupCard() throws Exception {
        StringBuilder vsbdSql = new StringBuilder();

        vsbdSql.append("select group_id, group_code, default_cycle, group_name_i18n, group_name, vds_channel_code, shop_code, ");
        vsbdSql.append("(select shop_name from manage_info_partner where shop_code = c.shop_code), ");
        vsbdSql.append("(select name from ap_param where type = 'DEFAULT_CYCLE' and code = c.default_cycle), ");
        vsbdSql.append("case when ");
        vsbdSql.append("(c.vds_channel_code is null and c.shop_code = 'VDS') then ");
        vsbdSql.append("(select vds_channel_name from vds_group_channel where vds_channel_code is null) ");
        vsbdSql.append("else ");
        vsbdSql.append("(select vds_channel_name from vds_group_channel where vds_channel_code = c.vds_channel_code) end ");
//        vsbdSql.append("c.vds_channel_code is null then (select vds_channel_name from vds_group_channel where vds_channel_code is null) ");
//        vsbdSql.append("else ");
//        vsbdSql.append("(select vds_channel_name from vds_group_channel where vds_channel_code = c.vds_channel_code) end ");
        vsbdSql.append("from config_group_card c ");
        vsbdSql.append("order by group_id desc ");

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        List<Object[]> vlstData = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    /**
     * lay du lieu cua bang config_group_card, sap xep theo group_name
     *
     * @return object[]
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 13/02/2020
     */
    @Override
    public List<Object[]> getAllOrderByNameAsc() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from config_group_card order by group_name COLLATE UTF8_UNICODE_CI");
        Query query = entityManager.createNativeQuery(stringBuilder.toString());
        return query.getResultList();
    }

}
