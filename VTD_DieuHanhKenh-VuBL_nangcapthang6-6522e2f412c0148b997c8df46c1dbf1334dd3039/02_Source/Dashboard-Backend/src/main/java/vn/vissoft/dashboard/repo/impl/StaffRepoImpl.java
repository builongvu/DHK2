package vn.vissoft.dashboard.repo.impl;

import viettel.passport.client.UserToken;
import vn.vissoft.dashboard.dto.*;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Staff;
import vn.vissoft.dashboard.model.VdsGroupChannel;
import vn.vissoft.dashboard.model.VdsStaff;
import vn.vissoft.dashboard.model.WarningReceiveConfig;
import vn.vissoft.dashboard.repo.StaffRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StaffRepoImpl implements StaffRepoCustom {

    @PersistenceContext
    EntityManager entityManager;
//    private BaseMapper<Staff, StaffDTO> mapper = new BaseMapper<>();

    /**
     * tim ma nhan vien theo code
     *
     * @param pstrCode
     * @return
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public Long findStaffIdByCode(String pstrCode) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();

        if (DataUtil.isNullOrEmpty(pstrCode)) return null;

        sqlBuilder.append("select s.id from Staff s where s.code=:staffCode");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("staffCode", pstrCode);
        List<Long> staffIds = query.getResultList();
        return staffIds.get(0);
    }

    /**
     * lay nhan vien trong don vi
     *
     * @param pstrShopCode
     * @return List<Staff>
     * @throws Exception
     * @author DatNT
     * @since 2019/09
     */
    @Override
    public List<Staff> findStaffInUnit(String pstrShopCode) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("select s from Staff s where shop_id =(select id from ManageInfoPartner where shop_code =:shopCode)");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("shopCode", pstrShopCode);

        return query.getResultList();
    }

    /**
     * kiem tra ton tai nhan vien trong don vi
     *
     * @param pstrShopCode
     * @param pstrStaffCode
     * @return
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public boolean checkExistedStaffInUnit(String pstrShopCode, String pstrStaffCode) throws Exception {
        boolean check = false;
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from vds_staff where staff_code = :staffCode and shop_code in (:shopCode) and staff_type ='1'");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("staffCode", pstrStaffCode);
        query.setParameter("shopCode", pstrShopCode);
        List<Object[]> vlstData = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlstData)) {
            check = true;
        }
//        for (Staff staff : findStaffInUnit(pstrShopCode)) {
//            if (pstrStaffCode.equalsIgnoreCase(staff.getStaffCode())) {
//                check = true;
//                break;
//            }
//        }
        return check;
    }

    @Override
    public StaffDTO findStaffByLoginUser(UserToken vsaToken) throws Exception {
        StaffDTO staff = new StaffDTO();
        staff.setVsaToken(vsaToken);
        staff.setStaffCode(vsaToken.getUserName());
        List<VdsStaff> lstVdsStaff = entityManager.createQuery("Select c from VdsStaff c where staffCode=:staffCode and status='1'", VdsStaff.class)
                .setParameter("staffCode", vsaToken.getUserName())
                .getResultList();

        if (lstVdsStaff != null && !lstVdsStaff.isEmpty()) {
            VdsStaff vdsStaff = lstVdsStaff.get(0);
            staff.setShopCode(vdsStaff.getShopCode());
            staff.setVdsChannelCode(vdsStaff.getVdsChannelCode());
            return staff;
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select b.shopCode, a.ttnsCode,b.staffOwnerId,b.channelTypeId from Staff a,Shop b \n")
                .append("where a.shopId = b.shopId \n")
                .append("and a.staffCode = :staffCode\n")
                .append("and a.status = 1 \n")
                .append("and b.status = 1");
        List<Object[]> lstObj = (List<Object[]>) entityManager.createQuery(sqlBuilder.toString()).setParameter("staffCode", vsaToken.getUserName()).getResultList();
        if (lstObj != null && !lstObj.isEmpty()) {
            Object[] obj = lstObj.get(0);
            String shopCode = DataUtil.safeToString(obj[0]);
            String ttnsCode = DataUtil.safeToString(obj[1]);

            sqlBuilder = new StringBuilder();
            sqlBuilder.append("select  c.vdsChannelCode from PositionStaff a, VttPosition b,MappingGroupChannel c \n")
                    .append("where a.positionCode = b.positionCode \n")
                    .append("and b.groupChannelCode = c.groupChannelCode \n")
                    .append("and a.ttnsCode = :p_ttns_code\n")
                    .append("and a.status = 1\n")
                    .append("and b.status = 1\n")
                    .append("and c.status = 1");
            List<String> lstChannelCode = entityManager.createQuery(sqlBuilder.toString()).setParameter("p_ttns_code", ttnsCode)
                    .getResultList();
            String channelCode = null;
            if (lstChannelCode != null && !lstChannelCode.isEmpty())
                channelCode = lstChannelCode.get(0);
            if (channelCode == null) {
                sqlBuilder = new StringBuilder();
                sqlBuilder.append("select  c.vdsChannelCode from Staff d, PositionStaff a, VttPosition b,MappingGroupChannel c \n")
                        .append("where a.positionCode = b.positionCode \n")
                        .append("and b.groupChannelCode = c.groupChannelCode \n")
                        .append("and a.ttnsCode = d.ttnsCode\n")
                        .append("and d.staffId = :p_staff_owner_id ")
                        .append("and a.status = 1\n")
                        .append("and b.status = 1\n")
                        .append("and c.status = 1");
                lstChannelCode = entityManager.createQuery(sqlBuilder.toString()).setParameter("p_staff_owner_id", DataUtil.safeToString(obj[2]))
                        .getResultList();
                if (lstChannelCode != null && !lstChannelCode.isEmpty())
                    channelCode = lstChannelCode.get(0);
            }

            if (channelCode == null) {
                sqlBuilder = new StringBuilder();
                sqlBuilder.append("select b.vdsChannelCode from vttGroupChannelSale a,mappingGroupChannel b \n")
                        .append("where a.status =1\n")
                        .append("and b.status = 1\n")
                        .append("and a.groupChannelCode = b.groupChannelCode \n")
                        .append("and a.channelTypeId = :p_channel_type_id");
                lstChannelCode = entityManager.createQuery(sqlBuilder.toString()).setParameter("p_channel_type_id", DataUtil.safeToLong(obj[3]))
                        .getResultList();
                if (lstChannelCode != null && !lstChannelCode.isEmpty())
                    channelCode = lstChannelCode.get(0);
            }

            staff.setShopCode(shopCode);
            staff.setVdsChannelCode(channelCode);
            return staff;
        }
        return staff;
    }

    /**
     * lay danh sach nhan vien cho combobox nhan vien
     *
     * @param staff
     * @return
     * @author VuBL
     * @since 2019/10/17
     */
    @Override
    public List<Staff> findStaffsByCondition(Staff staff) throws Exception {
        HashMap vhmpMapParams = new HashMap();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select s from Staff s ");
        vsbdSqlBuilder.append(buildSQL(staff, vhmpMapParams));
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();
    }

    /**
     * lay nhan vien theo don vi cua user dang nhap
     *
     * @param staffDTO
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2019/10
     */
    @Override
    public List<Staff> findByUnitCodeOfUser(StaffDTO staffDTO) throws Exception {
        List<Staff> staffs = new ArrayList<>();
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select  shop_code, staff_code, status, staff_name from vds_staff s ")
                .append("where ifnull(shop_code, 'a') = ifnull(:shopCode, 'a') ")
                .append("and ifnull(vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a') ")
                .append("and status='1' and staff_type ='1'");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("shopCode", staffDTO.getShopCode());
        query.setParameter("vdsChannelCode", staffDTO.getVdsChannelCode());
        List<Object[]> vlstData = query.getResultList();

        if (!DataUtil.isNullOrEmpty(vlstData)) {
            for (Object[] obj : vlstData) {
                Staff staff = new Staff();
                staff.setStaffCode(DataUtil.safeToString(obj[1]));
                staff.setStatus(DataUtil.safeToString(obj[2]));
                staff.setName(DataUtil.safeToString(obj[3]));
                staffs.add(staff);
            }
        }

        if (staffDTO.getShopCode().equals(Constants.PLAN_MONTHLY.VDS) && DataUtil.isNullOrEmpty(staffDTO.getVdsChannelCode()))
            return null;

        if (DataUtil.isNullOrEmpty(staffs)) {
            return null;
        }

        return staffs;
    }

    @Override
    public List<String> getStaffCodeLevel(List<Staff> plstStaffs) throws Exception {
        List<String> vlstStaffCodes = new ArrayList<>();
        if (!DataUtil.isNullOrEmpty(plstStaffs)) {
            for (Staff staff : plstStaffs) {
                if (!DataUtil.isNullObject(staff))
                    vlstStaffCodes.add(staff.getStaffCode());
            }
        }
        return vlstStaffCodes;
    }

    /**
     * lay ten nhan vien theo code
     *
     * @param pstrCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/11/2019
     */
    @Override
    public String findStaffNameByCode(String pstrCode) throws Exception {

        StringBuilder vsbdSqlBuilder = new StringBuilder();
        vsbdSqlBuilder.append("select s.staffName from VdsStaff s where ifnull(staffCode,'a')=ifnull(:staffCode,'a') ");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        if (!DataUtil.isNullOrEmpty(pstrCode))
            query.setParameter("staffCode", pstrCode.trim());

        List<String> vlstNames = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstNames)) return "";

        return vlstNames.get(0);
    }


    @Override
    public List<Object[]> findNameStaff(String pstrShopCode, String pstrVdsChannelCode) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        Query query;
        if (!DataUtil.isNullOrEmpty(pstrShopCode) && !DataUtil.isNullOrEmpty(pstrVdsChannelCode)) {
            sqlBuilder.append("select staff_name, staff_code from vds_staff s ");
            sqlBuilder.append("where ifnull(shop_code, 'a') = ifnull(:shopCode, 'a') ");
            sqlBuilder.append("and ifnull(vds_channel_code, 'a') = ifnull(:vdsChannelCode, 'a') ");
            sqlBuilder.append("and status = '1' and staff_type ='1'");
            sqlBuilder.append("order by case when s.staff_name like 'đ%' then 'd' else s.staff_name end ");
            query = entityManager.createNativeQuery(sqlBuilder.toString());
            query.setParameter("shopCode", pstrShopCode);
            query.setParameter("vdsChannelCode", pstrVdsChannelCode);
        } else {
            sqlBuilder.append("select staff_name, staff_code from vds_staff s ");
            sqlBuilder.append("where shop_code = 'VDS' ");
            sqlBuilder.append("and vds_channel_code is null ");
            sqlBuilder.append("and status = '1' and staff_type ='1' ");
            sqlBuilder.append("order by case when s.staff_name like 'đ%' then 'd' else s.staff_name end ");
            query = entityManager.createNativeQuery(sqlBuilder.toString());
        }
//        sqlBuilder.append("WITH RECURSIVE partner as (SELECT * FROM manage_info_partner mip WHERE shop_code = 'vds' and vds_channel_code is null ");
//        sqlBuilder.append("UNION SELECT m.* FROM manage_info_partner m, partner as p WHERE p.shop_code= m.parent_shop_code) ");
//        sqlBuilder.append("SELECT vs.staff_id,vs.name, vs.staff_code FROM partner p, v_staff vs ");
//        sqlBuilder.append("where p.`status` = '1' and vs.`status` ='1' ");
//        sqlBuilder.append("and vs.shop_code = p.shop_code ");
//        if (!DataUtil.isNullOrEmpty(pstrShopCode)) {
//            sqlBuilder.append("and vs.shop_code = :shopCode ");
//        }
//        sqlBuilder.append("order by case when vs.name like 'đ%' then 'd' else vs.name end");
//        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
//        if (!DataUtil.isNullOrEmpty(pstrShopCode)) {
//            query.setParameter("shopCode", pstrShopCode);
//        }
        return query.getResultList();
    }

    @Override
    public List<String> findByStaffType() throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("select vs.staff_code from vds_staff vs where staff_type='1' ");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        List<String> vlstStaffs = query.getResultList();
        if (!DataUtil.isNullOrEmpty(vlstStaffs))
            return vlstStaffs;
        else return null;
    }

    @Override
    public boolean checkExistedStaffBusiness(String pstrStaffCode) throws Exception {
        boolean vblnCheck = false;
        List<String> listStaffs = findByStaffType();
        if (listStaffs != null) {
            for (String staff : listStaffs) {
                if (pstrStaffCode.equalsIgnoreCase(staff.trim())) {
                    vblnCheck = true;
                    break;
                }
            }
        }

        return vblnCheck;
    }

    @Override
    public List<StaffRoleDTO> findByStaffCodeAndStaffName(String staff, String shopname, String role, int hasrole) throws Exception {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        if (DataUtil.isNullOrEmpty(role) && hasrole == 0) {
            vsbdStringBuilder.append("select * from v_all_staff as v where 1=1 ");
            if (!"".equals(staff) && !DataUtil.isNullOrEmpty(staff)) {
                vsbdStringBuilder.append("and (staff_code like '%").append(staff).append("%' or name like '%").append(staff).append("%')");
            }
            if (!"".equals(shopname) && !DataUtil.isNullOrEmpty(shopname)) {
                vsbdStringBuilder.append("and shop_code like '%").append(shopname).append("%' ");
            }
            vsbdStringBuilder.append(" and v.staff_code not in ( select s.staff_code from config_roles_staff as s where s.status = 1 group by s.staff_code )");
            vsbdStringBuilder.append("order by name");
        } else {
            vsbdStringBuilder.append("select s.staff_id, s.shop_code,s.staff_code, r.status, s.name from v_all_staff as s join config_roles_staff as c on s.staff_code = c.staff_code join config_roles as r on c.role_id = r.id\n" +
                    "where 1=1 ");
            if (!DataUtil.isNullOrEmpty(staff)) {
                vsbdStringBuilder.append("and (s.staff_code like '%").append(staff).append("%' or s.name like '%").append(staff).append("%')");
            }
            if (!DataUtil.isNullOrEmpty(shopname)) {
                vsbdStringBuilder.append("and s.shop_code like '%").append(shopname).append("%' ");
            }
            if(!DataUtil.isNullOrEmpty(role)){
                vsbdStringBuilder.append("and (r.role_code like '%").append(role).append("%' or r.role_name like '%").append(role).append("%')");
            }
            vsbdStringBuilder.append(" and c.status = 1 and s.status = 1 group by s.staff_id order by s.name");
        }
        List<Object[]> vlistObjs = entityManager.createNativeQuery(vsbdStringBuilder.toString()).getResultList();
        List<StaffRoleDTO> vlstStaff = new ArrayList<>();
        for (Object[] vlistObj : vlistObjs) {
            StaffRoleDTO vstaff = new StaffRoleDTO();
            vstaff.setId(Long.parseLong(vlistObj[0].toString()));
            vstaff.setName(vlistObj[4].toString());
            vstaff.setCode(vlistObj[2].toString());
            vstaff.setShopcode(vlistObj[1].toString());
            vstaff.setStatus(vlistObj[3].toString());
            vlstStaff.add(vstaff);
        }
        return vlstStaff;
    }

    /**
     * dieu kien tim kiem staff
     *
     * @param staff
     * @param phmpParams
     * @return
     * @author VuBL
     * @since 2019/10/17
     */
    private StringBuilder buildSQL(Staff staff, HashMap phmpParams) {
        StringBuilder vsbdStringBuilder = new StringBuilder();
        vsbdStringBuilder.append(" where 1=1 ");

        if (!DataUtil.isNullOrZero(staff.getShopId())) {
            vsbdStringBuilder.append("and shop_id = :shopId");
            phmpParams.put("shopId", staff.getShopId());
        }

        return vsbdStringBuilder;
    }
}
