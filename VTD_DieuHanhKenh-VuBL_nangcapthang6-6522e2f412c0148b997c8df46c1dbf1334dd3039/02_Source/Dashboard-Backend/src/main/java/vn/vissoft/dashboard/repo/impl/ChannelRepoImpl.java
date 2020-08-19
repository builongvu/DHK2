package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.VdsGroupChannel;
import vn.vissoft.dashboard.repo.ChannelRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChannelRepoImpl implements ChannelRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * tim tat ca cac channel code
     *
     * @return
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 20/9/2019
     */
    @Override
    public List<String> findAllChannelCode() throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select c.code from VdsGroupChannel c ");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    /**
     * kiem tra channel code da ton tai(true:la da ton tai,false: la k ton tai)
     *
     * @param pstrCode
     * @return
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 24/09/2019
     */
    @Override
    public boolean checkExistedChannelCode(String pstrCode) throws Exception {

        boolean vblnCheck = false;
        List<VdsGroupChannel> listActive = findActiveChannel();
        if (listActive != null) {
            for (VdsGroupChannel channel : listActive) {
                if (pstrCode.equalsIgnoreCase(channel.getCode())) {
                    vblnCheck = true;
                    break;
                }
            }
        }

        return vblnCheck;
    }

    /**
     * lay danh sach kenh cho combobox kenh
     *
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/09
     */
    @Override
    public List<VdsGroupChannel> findActiveChannel() throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select c from VdsGroupChannel c ")
                .append("where c.status='1' ")
                .append("and c.code in (select vdsChannelCode from ManageInfoPartner ")
                .append("where parentShopCode = 'VDS') ")
                .append("order by case when c.name like 'đ%' then 'd' else c.name end");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    /**
     * combobox ma nhom kenh vds
     * @return
     * @throws Exception
     */
    @Override
    public List<VdsGroupChannel> findActiveGroupChannel() throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select c from VdsGroupChannel c ")
                .append("where c.status='1' ")
                .append("order by case when c.name like 'đ%' then 'd' else c.name end");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    /**
     * kiem tra ma nhom chi tieu co ton tai, hoat dong hay khong
     * @param pstrCode
     * @return
     * @throws Exception
     */
    @Override
    public boolean checkExistedGroupChannelCode(String pstrCode) throws Exception {
        boolean vblnCheck = false;
        List<VdsGroupChannel> listActive = findActiveGroupChannel();
        if (listActive != null) {
            for (VdsGroupChannel channel : listActive) {
                if (pstrCode.equalsIgnoreCase(channel.getCode())) {
                    vblnCheck = true;
                    break;
                }
            }
        }

        return vblnCheck;
    }

    /**
     * lay channel theo shop_code (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param pstrShopCode
     * @return
     * @throws Exception
     */
    @Override
    public VdsGroupChannel findChannelByShopCode(String pstrShopCode) throws Exception {
        StringBuilder vsblSql = new StringBuilder();
        vsblSql.append("select v from VdsGroupChannel v ");
        vsblSql.append("where ifnull(code, 'a') = ifnull((select vdsChannelCode from ManageInfoPartner ");
        vsblSql.append("where shopCode = :shopCode), 'a') ");
        vsblSql.append("and status = '1' ");
        vsblSql.append("order by case when v.name like 'đ%' then 'd' else v.name end ");
        Query query = entityManager.createQuery(vsblSql.toString());
        query.setParameter("shopCode", pstrShopCode);
        VdsGroupChannel data = (VdsGroupChannel) query.getSingleResult();

        if (DataUtil.isNullObject(data)) return null;
        return data;
    }

    /**
     * lay ra list channel cho cbb nhom kenh VDS (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @return
     * @throws Exception
     */
    @Override
    public List<VdsGroupChannel> findByStatusAndNotExists() throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        List<VdsGroupChannel> vlstData = new ArrayList<>();
        vsbdSqlBuilder.append("select id, vds_channel_code, vds_channel_name, status, user, created_date from vds_group_channel c ");
        vsbdSqlBuilder.append("where c.status = '1' ");
        vsbdSqlBuilder.append("and ifnull(c.vds_channel_code, 'a') not in (select ifnull(vds_channel_code, 'a') from manage_info_partner) ");
        vsbdSqlBuilder.append("order by case when c.vds_channel_name like 'đ%' then 'd' else c.vds_channel_name end");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        List<Object[]> vlstObj = query.getResultList();

        if (!DataUtil.isNullOrEmpty(vlstObj)) {
            for (Object[] obj : vlstObj) {
                VdsGroupChannel data = new VdsGroupChannel();
                data.setId(DataUtil.safeToLong(obj[0]));
                data.setCode((String) obj[1]);
                data.setName((String) obj[2]);
                data.setStatus((String) obj[3]);
                data.setUser((String) obj[4]);
                data.setCreatedDate((Date) obj[5]);
                vlstData.add(data);
            }
        }

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    @Override
    public String findNameByCode(String pstrVdsChannelCode) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        String vstrName;

        if (pstrVdsChannelCode == null) {
            vsbdSql.append("select vds_channel_name from vds_group_channel where vds_channel_code is null");
        } else {
            vsbdSql.append("select vds_channel_name from vds_group_channel where vds_channel_code = :vdsChannelCode");
        }
        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        if (pstrVdsChannelCode != null) {
            query.setParameter("vdsChannelCode", pstrVdsChannelCode.trim());
        }

        List<String> vlstName = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstName)) {
            return null;
        }
        return vlstName.get(0);
    }

}
