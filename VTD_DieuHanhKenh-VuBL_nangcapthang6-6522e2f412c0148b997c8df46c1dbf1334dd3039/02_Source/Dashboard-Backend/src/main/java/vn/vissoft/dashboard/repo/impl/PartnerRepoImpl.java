package vn.vissoft.dashboard.repo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigGroupCardDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ManageInfoPartner;
import vn.vissoft.dashboard.repo.DashboardRepoCustom;
import vn.vissoft.dashboard.repo.PartnerRepoCustom;
import vn.vissoft.dashboard.services.impl.TopDataServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartnerRepoImpl implements PartnerRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private DashboardRepoCustom dashboardRepoCustom;

    private static final Logger LOGGER = LogManager.getLogger(TopDataServiceImpl.class);

    /**
     * lay ra don vi VDS
     *
     * @return
     * @author DatNT
     * @since 13/03/2019
     */
    @Override
    public String findShopCodeVDS() {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select u.shopCode from ManageInfoPartner u where parentShopCode is null and vdsChannelCode is null");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        List<String> vlstUnitIds = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstUnitIds)) return null;
        return vlstUnitIds.get(0);
    }

    /**
     * tim kiem shop code bang channel code o vds
     *
     * @param pstrChannelCode
     * @return
     * @throws Exception
     * @author DatNT
     */
    @Override
    public String findShopCodeByChannelCode(String pstrChannelCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select u.shopCode from ManageInfoPartner u where  vdsChannelCode =:vdsChannelCode and parentShopCode ='VDS'");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("vdsChannelCode", pstrChannelCode);

        List<String> vlstUnitIds = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstUnitIds)) return null;
        return vlstUnitIds.get(0);
    }

    /**
     * tim cac don vi thuoc kenh
     *
     * @param pstrChannelCode
     * @return
     * @throws Exception
     */
    @Override
    public List<String> findShopByChannel(String pstrChannelCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select u.shopCode from ManageInfoPartner u where  vdsChannelCode =:vdsChannelCode ");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("vdsChannelCode", pstrChannelCode);

        List<String> vlstUnitIds = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstUnitIds)) return null;
        return vlstUnitIds;
    }

    /**
     * kiem tra ton tai cua don vi trong kenh
     *
     * @param pstrShopCode
     * @param pstrChannelCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public boolean checkExistedUnitInChannel(String pstrShopCode, String pstrChannelCode) throws Exception {

        boolean vblnCheck = false;
        List<String> listShops = findShopByChannel(pstrChannelCode);
        if (!DataUtil.isNullOrEmpty(listShops)) {
            for (String shop : listShops) {
                if (pstrShopCode.equalsIgnoreCase(shop)) {
                    vblnCheck = true;
                    break;
                }
            }
        }

        return vblnCheck;
    }

    @Override
    public List<ManageInfoPartner> findByParentShopCode(String pstrParentCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select u from ManageInfoPartner u where  parentShopCode =:parentShopCode");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        query.setParameter("parentShopCode", pstrParentCode);

        List<ManageInfoPartner> vlstUnitIds = query.getResultList();

        return vlstUnitIds;
    }

    /**
     * xay dung combobox don vi giao chi tieu cac cap
     *
     * @param staffDTO
     * @return
     * @throws Exception
     * @author DatNT
     * @since 16/10/2019
     */
    @Override
    public List<ManageInfoPartner> findManageInfoPartnerLevel(StaffDTO staffDTO) throws Exception {
        List<ManageInfoPartner> vlstPartners;
        List<ManageInfoPartner> vlstManageInfoPartners = findByParentShopCode(staffDTO.getShopCode().trim());
        List<ManageInfoPartner> vlstResults = new ArrayList<>();

        if (!DataUtil.isNullOrEmpty(vlstManageInfoPartners)) {
            for (int i = 0; i < vlstManageInfoPartners.size(); i++) {
                if ("0".equals(vlstManageInfoPartners.get(i).getAssignKpi())) {
                    vlstPartners = findByParentShopCode(vlstManageInfoPartners.get(i).getShopCode());
                    for (ManageInfoPartner manageInfoPartner : vlstPartners) {
                        vlstManageInfoPartners.add(manageInfoPartner);
                    }
                }
            }
            for (ManageInfoPartner manageInfoPartner : vlstManageInfoPartners) {
                if ("1".equals(manageInfoPartner.getAssignKpi()))
                    vlstResults.add(manageInfoPartner);
            }
        }

        return vlstResults;
    }

    /**
     * xay dung cay dieu hanh dashboard
     *
     * @param plngGroupId
     * @param staffDTO
     * @return
     * @throws Exception
     * @author DatNT
     * @version 1.0
     * @since 30/10/2019
     */
    @Override
    public List<ManageInfoPartner> findPartnerDashboard(Long plngGroupId, StaffDTO staffDTO) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();
        List<ManageInfoPartner> vlstPartners = null;
        ConfigGroupCardDTO configGroupCardDTO = null;
        if (plngGroupId != null) configGroupCardDTO = dashboardRepoCustom.getConfigGroup(plngGroupId);

        if (configGroupCardDTO != null) {
            List<String> vlstShopCodes = findPartnerByGroupId(configGroupCardDTO);
            if ((!DataUtil.isNullOrEmpty(configGroupCardDTO.getVdsChannelCode()) && configGroupCardDTO.getVdsChannelCode().equalsIgnoreCase(staffDTO.getVdsChannelCode()))
                    || (configGroupCardDTO.getVdsChannelCode() == null && staffDTO.getVdsChannelCode() == null)) {
                if (!DataUtil.isNullOrEmpty(vlstShopCodes)) {
                    if (vlstShopCodes.contains(staffDTO.getShopCode())) {
                        vsbdSqlBuilder.append("WITH RECURSIVE partner as (")
                                .append(" SELECT * FROM manage_info_partner WHERE shop_code =:shopCode and ");
                        if (DataUtil.isNullOrEmpty(staffDTO.getVdsChannelCode())) {
                            vsbdSqlBuilder.append(" vds_channel_code is null ");
                        } else {
                            vsbdSqlBuilder.append(" vds_channel_code =:vdsChannelCode ");
                            vhmpMapParams.put("vdsChannelCode", staffDTO.getVdsChannelCode());
                        }
                        vsbdSqlBuilder.append(" UNION SELECT m.* FROM manage_info_partner m, partner as p WHERE p.shop_code= m.parent_shop_code)")
                                .append("SELECT * FROM partner where status='1';");
                        vhmpMapParams.put("shopCode", staffDTO.getShopCode());
                        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
                        vhmpMapParams.forEach((k, v) -> {
                            query.setParameter(k.toString(), v);
                        });
                        List<Object[]> vlstOnjects = query.getResultList();
                        vlstPartners = setPartners(vlstOnjects);

                    } else {
                        vsbdSqlBuilder.append("WITH RECURSIVE partner as (")
                                .append(" SELECT * FROM manage_info_partner WHERE shop_code =:shopCode and ");
                        if (DataUtil.isNullOrEmpty(staffDTO.getVdsChannelCode())) {
                            vsbdSqlBuilder.append(" vds_channel_code is null ");
                        } else {
                            vsbdSqlBuilder.append(" vds_channel_code =:vdsChannelCode ");
                            vhmpMapParams.put("vdsChannelCode", configGroupCardDTO.getVdsChannelCode());
                        }
                        vsbdSqlBuilder.append(" UNION SELECT m.* FROM manage_info_partner m, partner as p WHERE p.shop_code= m.parent_shop_code)")
                                .append("SELECT * FROM partner where status='1';");
                        vhmpMapParams.put("shopCode", configGroupCardDTO.getShopCode());
                        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
                        vhmpMapParams.forEach((k, v) -> {
                            query.setParameter(k.toString(), v);
                        });
                        List<Object[]> vlstOnjects = query.getResultList();
                        vlstPartners = setPartners(vlstOnjects);
                    }
                }
            } else {
                if (configGroupCardDTO.getVdsChannelCode() == null) {
                    vsbdSqlBuilder.append("WITH RECURSIVE partner as (")
                            .append(" SELECT * FROM manage_info_partner WHERE shop_code =:shopCode and ");
                    vsbdSqlBuilder.append(" vds_channel_code =:vdsChannelCode ");
                    vsbdSqlBuilder.append(" UNION SELECT m.* FROM manage_info_partner m, partner as p WHERE p.shop_code= m.parent_shop_code)")
                            .append("SELECT * FROM partner where status='1';");
                    vhmpMapParams.put("shopCode", staffDTO.getShopCode());
                    vhmpMapParams.put("vdsChannelCode", staffDTO.getVdsChannelCode());
                    Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
                    vhmpMapParams.forEach((k, v) -> {
                        query.setParameter(k.toString(), v);
                    });
                    List<Object[]> vlstOnjects = query.getResultList();
                    vlstPartners = setPartners(vlstOnjects);
                } else if (staffDTO.getVdsChannelCode() == null) {
                    vsbdSqlBuilder.append("WITH RECURSIVE partner as (")
                            .append(" SELECT * FROM manage_info_partner WHERE shop_code =:shopCode and ");
                    vsbdSqlBuilder.append(" vds_channel_code =:vdsChannelCode ");
                    vsbdSqlBuilder.append(" UNION SELECT m.* FROM manage_info_partner m, partner as p WHERE p.shop_code= m.parent_shop_code)")
                            .append("SELECT * FROM partner where status='1';");
                    vhmpMapParams.put("shopCode", configGroupCardDTO.getShopCode());
                    vhmpMapParams.put("vdsChannelCode", configGroupCardDTO.getVdsChannelCode());
                    Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
                    vhmpMapParams.forEach((k, v) -> {
                        query.setParameter(k.toString(), v);
                    });
                    List<Object[]> vlstOnjects = query.getResultList();
                    vlstPartners = setPartners(vlstOnjects);
                }
            }
        }

        return vlstPartners;
    }

    private List<ManageInfoPartner> setPartners(List<Object[]> plstObjects) {
        List<ManageInfoPartner> vlstPartners = null;
        if (!DataUtil.isNullOrEmpty(plstObjects)) {
            vlstPartners = new ArrayList<>();
            for (Object[] object : plstObjects) {
                String vstrChannelCode = (String) object[1];
                ManageInfoPartner manageInfoPartner = new ManageInfoPartner();
                manageInfoPartner.setId(DataUtil.safeToLong(object[0]));
                manageInfoPartner.setVdsChannelCode((vstrChannelCode != null ? vstrChannelCode : null));
                manageInfoPartner.setShopCode(DataUtil.safeToString(object[2]));
                manageInfoPartner.setShopName(DataUtil.safeToString(object[3]));
                manageInfoPartner.setParentShopCode(DataUtil.safeToString(object[5]));
                vlstPartners.add(manageInfoPartner);
            }
        }
        return vlstPartners;
    }

    private List<String> findPartnerByGroupId(ConfigGroupCardDTO configGroupCardDTO) {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();
        List<String> vlstShopCodes = null;
        if (!DataUtil.isNullObject(configGroupCardDTO)) {
            vsbdSqlBuilder.append("WITH RECURSIVE partner as (")
                    .append(" SELECT * FROM manage_info_partner WHERE shop_code =:shopCode and ");
            if (DataUtil.isNullOrEmpty(configGroupCardDTO.getVdsChannelCode())) {
                vsbdSqlBuilder.append(" vds_channel_code is null ");
            } else {
                vsbdSqlBuilder.append(" vds_channel_code =:vdsChannelCode ");
                vhmpMapParams.put("vdsChannelCode", configGroupCardDTO.getVdsChannelCode());
            }
            vsbdSqlBuilder.append(" UNION SELECT m.* FROM manage_info_partner m, partner as p WHERE p.shop_code= m.parent_shop_code)")
                    .append("SELECT * FROM partner;");
            vhmpMapParams.put("shopCode", configGroupCardDTO.getShopCode());
            Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
            vhmpMapParams.forEach((k, v) -> {
                query.setParameter(k.toString(), v);
            });
            List<Object[]> vlstOnjects = query.getResultList();
            if (!DataUtil.isNullOrEmpty(vlstOnjects)) {
                vlstShopCodes = new ArrayList<>();
                for (Object[] object : vlstOnjects) {
                    vlstShopCodes.add(DataUtil.safeToString(object[2]));
                }
            }
        }

        return vlstShopCodes;
    }

    /**
     * kiem tra ton tai cua don vi
     *
     * @param pstrCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public boolean checkExistedUnitCode(String pstrCode) throws Exception {

        boolean vblnCheck = false;
        List<ManageInfoPartner> listActive = findActiveUnit();
        if (listActive != null) {
            for (ManageInfoPartner unit : listActive) {
                if (pstrCode.equalsIgnoreCase(unit.getShopCode())) {
                    vblnCheck = true;
                    break;
                }
            }
        }

        return vblnCheck;
    }

    /**
     * tim don vi dang hoat dong
     *
     * @return
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public List<ManageInfoPartner> findActiveUnit() {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select u from ManageInfoPartner u where u.status='1'");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    /**
     * Lay short name theo vds_channel_code and shop_code
     *
     * @param pastrShopCode
     * @return
     * @author DatNT
     * @version 1.0
     * @since 31/10/2019
     */
    @Override
    public String getNameByShopAndChannel(String pastrShopCode) {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();
        vsbdSqlBuilder.append("select u.shortName from ManageInfoPartner u where u.shopCode =:shopCode");
        vhmpMapParams.put("shopCode", pastrShopCode);
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<String> vlstShortNames = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstShortNames)) return null;

        return vlstShortNames.get(0);

    }

    /**
     * check don vi co ton tai trong manage_info_partner theo shop_code truyen vao
     *
     * @param shopCode
     * @return : true neu co shop_code ton tai trong manage_info_partner
     * @author: HungNN
     * @version: 1.0 (sau khi được sửa đổi sẽ tăng thêm)
     * @since: 2020/01/02
     */
    @Override
    public boolean checkShopByShopCode(String shopCode) throws Exception {
        boolean check = false;
        String nameVDS = getVdsChannelByShopCode(shopCode);
        if (nameVDS != null) {
            return true;
        }
        return check;
    }

    /**
     * lay ra cac shop code trong giao chi tieu cac cap
     *
     * @param plstPartners
     * @return
     * @author DatNT
     * @version 1.0
     * @since 30/10/2019
     */
    @Override
    public List<String> getShopCodeLevel(List<ManageInfoPartner> plstPartners) throws Exception {
        List<String> vlstShopCodes = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(plstPartners)) {
            for (ManageInfoPartner manageInfoPartner : plstPartners) {
                if (!DataUtil.isNullObject(manageInfoPartner))
                    vlstShopCodes.add(manageInfoPartner.getShopCode());
            }
        }
        return vlstShopCodes;
    }

    @Override
    public List<String> getChannelCodeLevel(List<ManageInfoPartner> plstPartners) throws Exception {
        List<String> vlstChannelCodes = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(plstPartners)) {
            for (ManageInfoPartner manageInfoPartner : plstPartners) {
                if (!DataUtil.isNullObject(manageInfoPartner))
                    vlstChannelCodes.add(manageInfoPartner.getVdsChannelCode());
            }
        }
        return vlstChannelCodes;
    }

    @Override
    public List<String> getShopCodeVDS(List<String> plstChannelCode) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select distinct shopCode from ManageInfoPartner where vdsChannelCode in (:vdsChannelCode)");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("vdsChannelCode", plstChannelCode);
        List<String> vlstShopCodesVDS = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstShopCodesVDS)) return null;
        return vlstShopCodesVDS;
    }

    @Override
    public String getChannelByShopCode(String pstrShopCode, String pstrParentShopCode) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select vdsChannelCode from ManageInfoPartner where shopCode =:shopCode and parentShopCode = :parentShopCode ");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("shopCode", pstrShopCode);
        query.setParameter("parentShopCode", pstrParentShopCode);
        List<String> vlstChannelCodes = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstChannelCodes)) return null;
        return vlstChannelCodes.get(0);
    }

    @Override
    public List<Object[]> getPartner() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("WITH RECURSIVE partner as (SELECT * FROM manage_info_partner mip WHERE shop_code = 'vds' and vds_channel_code is null\n" +
                "UNION SELECT m.* FROM manage_info_partner m, partner as p WHERE p.shop_code= m.parent_shop_code)\n" +
                "SELECT * FROM partner where `status` = '1'");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        return query.getResultList();
    }

    @Override
    public List<ManageInfoPartner> findPartnerReport(StaffDTO staffDTO) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();
        List<ManageInfoPartner> vlstPartners = null;
        if (!DataUtil.isNullOrEmpty(staffDTO.getShopCode())) {
            vsbdSqlBuilder.append("WITH RECURSIVE partner as (")
                    .append("SELECT * FROM manage_info_partner WHERE shop_code =:shopCode ")
                    .append(" UNION SELECT m.* FROM manage_info_partner m, partner as p WHERE p.shop_code= m.parent_shop_code)")
                    .append(" SELECT * FROM partner");
            vhmpMapParams.put("shopCode", staffDTO.getShopCode());

            Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
            vhmpMapParams.forEach((k, v) -> {
                query.setParameter(k.toString(), v);
            });
            List<Object[]> vlstOnjects = query.getResultList();
            vlstPartners = setPartners(vlstOnjects);
        }

        return vlstPartners;
    }

    /**
     * lay danh sach don vi co status = 1 va thoa man to_date from_date cho menu trai (chuc nang khai bao cay dieu hanh VDS)
     *
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public List<Object[]> findByStatusAndDate() throws Exception {
        StringBuilder vsblSql = new StringBuilder();
        vsblSql.append("select id, vds_channel_code, shop_code, shop_name, short_name, parent_shop_code, ");
        vsblSql.append("assign_kpi, status, user, create_date, from_date, to_date ");
        vsblSql.append("from manage_info_partner m ");
        vsblSql.append("where status = '1' ");
        vsblSql.append("and current_date() between from_date and to_date ");
        Query query = entityManager.createNativeQuery(vsblSql.toString());
        List<Object[]> vlstData = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    /**
     * lay don vi theo shop_code hoac parent_shop_code khi click tren cay don vi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @param pstrShopCode
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public List<Object[]> findByCodeOrParentCode(String pstrShopCode, int child) throws Exception {
        StringBuilder vsblSql = new StringBuilder();

        //truong hop chi lay ra don vi da chon
        if (child == Constants.MANAGE_INFO_PARTNER.NO_CHILD) {
            vsblSql.append("select id, vds_channel_code, shop_code, shop_name, short_name, parent_shop_code, ");
            vsblSql.append("assign_kpi, status, user, create_date, from_date, to_date, ");
            vsblSql.append("(select shop_name from manage_info_partner where shop_code = m.parent_shop_code) ");
            vsblSql.append("from manage_info_partner m ");
            vsblSql.append("where shop_code = :shopCode ");
            vsblSql.append("and status in ('0', '1') ");
//            vsblSql.append("order by status desc, shop_name, short_name asc ");
        }

        //truong hop lay ra don vi da chon va tat ca don vi con chau
        if (child == Constants.MANAGE_INFO_PARTNER.HAVE_CHILD) {
            vsblSql.append("with recursive partner as (");
            vsblSql.append("select id, vds_channel_code, shop_code, shop_name, short_name, parent_shop_code, ");
            vsblSql.append("assign_kpi, status, user, create_date, from_date, to_date, ");
            vsblSql.append("(select shop_name from manage_info_partner where shop_code = m.parent_shop_code) ");
            vsblSql.append("from manage_info_partner m ");
            vsblSql.append("where shop_code = :shopCode ");
            vsblSql.append("union ");
            vsblSql.append("select m.id, m.vds_channel_code, m.shop_code, m.shop_name, m.short_name, m.parent_shop_code, ");
            vsblSql.append("m.assign_kpi, m.status, m.user, m.create_date, m.from_date, m.to_date, ");
            vsblSql.append("(select shop_name from manage_info_partner where shop_code = m.parent_shop_code) ");
            vsblSql.append("from manage_info_partner m, partner p ");
            vsblSql.append("where p.shop_code = m.parent_shop_code ");
            vsblSql.append(") ");
            vsblSql.append("select * from partner where status in ('0', '1') ");
//            vsblSql.append("order by status desc, shop_name, short_name asc ");
        }

        Query query = entityManager.createNativeQuery(vsblSql.toString());
        query.setParameter("shopCode", pstrShopCode);
        List<Object[]> vlstData = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    /**
     * tim kiem nhanh tren cay don vi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @param pstrKeySearch
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public List<Object[]> findByCondition(String pstrKeySearch) throws Exception {
        StringBuilder vsblSql = new StringBuilder();
        vsblSql.append("select id, vds_channel_code, shop_code, shop_name, short_name, parent_shop_code, ");
        vsblSql.append("assign_kpi, status, user, create_date, from_date, to_date from manage_info_partner ");
        vsblSql.append("where (LOWER(shop_name) like concat('%',CONVERT(LOWER(:keySearch),BINARY),'%') or LOWER(short_name) like concat('%',CONVERT(LOWER(:keySearch),BINARY),'%') or shop_code like :keySearch) ");
        vsblSql.append("and status = '1' ");
        vsblSql.append("and case when to_date is not null then current_date() between from_date and to_date else current_date() >= from_date end ");
        Query query = entityManager.createNativeQuery(vsblSql.toString());
        query.setParameter("keySearch", "%" + pstrKeySearch.trim() + "%");
        List<Object[]> vlstData = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    /**
     * check shop_code da ton tai khi them moi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @param pstrShopCode
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public Boolean checkDuplicate(String pstrShopCode) throws Exception {
        Boolean vblnCheck = true;
        StringBuilder vsblSql = new StringBuilder();
        vsblSql.append("select * from manage_info_partner ");
        vsblSql.append("where shop_code = :shopCode ");
        Query query = entityManager.createNativeQuery(vsblSql.toString());
        query.setParameter("shopCode", pstrShopCode);
        List<Object[]> vlstData = query.getResultList();

        if (vlstData.size() > 0) {
            vblnCheck = false;
        }

        return vblnCheck;
    }

    /**
     * check khi update nguoi dung chọn don vi cha la con hoac chau cua don vi do (chuc nang khai bao cay dieu hanh VDS)
     *
     * @param pstrShopCode
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public Boolean checkParentShopCode(String pstrShopCode, String pstrParentShopCode) throws Exception {
        Boolean vblnCheck = true;
        StringBuilder vsblSql = new StringBuilder();
        vsblSql.append("WITH RECURSIVE partner as (SELECT * FROM manage_info_partner WHERE shop_code = :shopCode ");
        vsblSql.append("UNION SELECT m.* FROM manage_info_partner m, partner as p WHERE p.shop_code= m.parent_shop_code) ");
        vsblSql.append("SELECT * FROM partner where shop_code = :parentShopCode ");
        Query query = entityManager.createNativeQuery(vsblSql.toString());
        query.setParameter("shopCode", pstrShopCode);
        query.setParameter("parentShopCode", pstrParentShopCode);
        List<Object[]> vlstData = query.getResultList();

        if (vlstData.size() > 0) {
            vblnCheck = false;
        }

        return vblnCheck;
    }

    /**
     * tim kiem don vi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @param manageInfoPartner
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/12
     */
    @Override
    public List<Object[]> findByCondionAll(ManageInfoPartner manageInfoPartner) throws Exception {
        StringBuilder vsblSql = new StringBuilder();
        HashMap vhmpParams = new HashMap();

        vsblSql.append("select id, vds_channel_code, shop_code, shop_name, short_name, parent_shop_code, ");
        vsblSql.append("assign_kpi, status, user, create_date, from_date, to_date, ");
        vsblSql.append("(select shop_name from manage_info_partner where shop_code = m.parent_shop_code) ");
        vsblSql.append("from manage_info_partner m ");
        vsblSql.append("where 1 = 1 ");
        if (manageInfoPartner.getShopCode() != null) {
            vsblSql.append("and shop_code like :shopCode ");
            vhmpParams.put("shopCode", "%" + manageInfoPartner.getShopCode().trim() + "%");
        }
        if (manageInfoPartner.getShopName() != null) {
            vsblSql.append("and LOWER(shop_name) like concat('%',CONVERT(LOWER(:shopName),BINARY),'%') ");
            vhmpParams.put("shopName", manageInfoPartner.getShopName().trim());
        }
        if (manageInfoPartner.getShortName() != null) {
            vsblSql.append("and LOWER(short_name) like concat('%',CONVERT(LOWER(:shortName),BINARY),'%') ");
            vhmpParams.put("shortName", manageInfoPartner.getShortName().trim());
        }
        if (manageInfoPartner.getParentShopCode() != null) {
            vsblSql.append("and parent_shop_code = :parentShopCode ");
            vhmpParams.put("parentShopCode", manageInfoPartner.getParentShopCode());
        }
        if (manageInfoPartner.getVdsChannelCode() != null) {
            vsblSql.append("and vds_channel_code = :vdsChannelCode ");
            vhmpParams.put("vdsChannelCode", manageInfoPartner.getVdsChannelCode());
        }
        if (manageInfoPartner.getStatus() != null) {
            vsblSql.append("and status = :status ");
            vhmpParams.put("status", manageInfoPartner.getStatus());
        } else {
            vsblSql.append("and status in ('0', '1') ");
        }
        vsblSql.append("order by status desc ");

        Query query = entityManager.createNativeQuery(vsblSql.toString());
        vhmpParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        List<Object[]> vlstData = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstData)) return null;
        return vlstData;
    }

    @Override
    public String getVdsChannelByShopCode(String shopCode) throws Exception {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select vdsChannelCode from ManageInfoPartner where shopCode = :shopCode and status = '1'");
            Query query = entityManager.createQuery(stringBuilder.toString());
            query.setParameter("shopCode", shopCode);
            List<String> list = query.getResultList();
            if (list.size() > 0) {
                return list.get(0);
            } else return null;
        } catch (NoResultException e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * lay ra cay dieu hanh tu con cua kenh tinh
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getPartnerOfProvinceChannel() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("WITH RECURSIVE partner as (")
                .append("SELECT * FROM manage_info_partner WHERE shop_code ='KENH_TINH' and vds_channel_code ='VDS_TINH'")
                .append(" UNION SELECT m.* FROM manage_info_partner m, partner as p WHERE p.shop_code= m.parent_shop_code)")
                .append(" SELECT shop_code FROM partner where status='1' and shop_code <> 'KENH_TINH';");

        Query query=entityManager.createNativeQuery(stringBuilder.toString());

        return query.getResultList();
    }

}
