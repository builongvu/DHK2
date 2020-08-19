package vn.vissoft.dashboard.repo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.vissoft.dashboard.controller.ServiceScoreController;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VdsStaffDTO;
import vn.vissoft.dashboard.dto.excel.VdsStaffExcel;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Staff;
import vn.vissoft.dashboard.model.VdsStaff;
import vn.vissoft.dashboard.repo.VdsStaffRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class VdsStaffRepoImpl implements VdsStaffRepoCustom {
    @PersistenceContext
    EntityManager entityManager;

    private static final Logger LOGGER = LogManager.getLogger(ServiceScoreController.class);

    @Override
    public VdsStaff findVdsStaffByCondition(String pstrChannelCode, String pstrShopCode, String pstrStaffCode) {
        StringBuilder vsbdSqlBuilder = new StringBuilder();
        HashMap vhmpMapParams = new HashMap();

        vsbdSqlBuilder.append("select vs from VdsStaff vs ")
                .append("where 1=1 ");
        if (!DataUtil.isNullOrEmpty(pstrChannelCode)) {
            vsbdSqlBuilder.append(" and vs.vdsChannelCode =:vdsChannelCode ");
            vhmpMapParams.put("vdsChannelCode", pstrChannelCode);
        }
        if (!DataUtil.isNullOrEmpty(pstrShopCode)) {
            vsbdSqlBuilder.append(" and vs.shopCode =:shopCode ");
            vhmpMapParams.put("shopCode", pstrShopCode);
        }
        if (!DataUtil.isNullOrEmpty(pstrStaffCode)) {
            vsbdSqlBuilder.append(" and vs.staffCode =:staffCode ");
            vhmpMapParams.put("staffCode", pstrStaffCode);
        }
        System.out.println(vsbdSqlBuilder.toString());
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());
        vhmpMapParams.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });

        List<VdsStaff> vlstVdsStaffs = query.getResultList();
        if (DataUtil.isNullOrEmpty(vlstVdsStaffs)) return null;
        return vlstVdsStaffs.get(0);
    }

    /**
     * lay ra nhung nhan vien dang hoat dong
     *
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public List<VdsStaff> getActiveStaff() throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select s from VdsStaff s where status ='1'");
        Query query = entityManager.createQuery(vsbdSqlBuilder.toString());

        return query.getResultList();
    }

    /**
     * kiem tra ton tai cua staff code
     *
     * @param pstrCode
     * @return
     * @throws Exception
     * @author DatNT
     * @since 13/09/2019
     */
    @Override
    public boolean checkExistedStaffCode(String pstrCode) throws Exception {

        boolean vblnCheck = false;
        List<VdsStaff> listActive = getActiveStaff();
        if (listActive != null) {
            for (VdsStaff staff : listActive) {
                if (pstrCode.equalsIgnoreCase(staff.getStaffCode())) {
                    vblnCheck = true;
                    break;
                }
            }
        }
        return vblnCheck;
    }

    @Override
    public boolean checkExistedStaffInShop(String pstrStaffCode, String pstrShopCode) throws Exception {
        boolean vblnCheck = false;
        List<String> listStaffs = findStaffByShopCode(pstrShopCode);
        if (listStaffs != null) {
            for (String staff : listStaffs) {
                if (pstrStaffCode.equalsIgnoreCase(staff)) {
                    vblnCheck = true;
                    break;
                }
            }
        }
        return vblnCheck;
    }

    @Override
    public List<String> findStaffByShopCode(String pstrShopCode) throws Exception {
        StringBuilder vsbdSqlBuilder = new StringBuilder();

        vsbdSqlBuilder.append("select staff_code from vds_staff where shop_code =:shopCode and staff_type ='1'");
        Query query = entityManager.createNativeQuery(vsbdSqlBuilder.toString());
        query.setParameter("shopCode", pstrShopCode);
        return query.getResultList();
    }

    /**
     * them moi nhan vien vao bang vds_staff
     *
     * @param vdsStaffDTO
     * @return id ban ghi vua them moi
     * @throws Exception
     * @author HungNN
     * @since 27/12/2019
     */
    @Transactional
    @Override
    public BigInteger createVdsStaff(VdsStaffDTO vdsStaffDTO, StaffDTO staffDTO) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into vds_staff(vds_channel_code,shop_code,staff_code,staff_name,phone_number,email,status,created_user,create_date,staff_type)" +
                " values(?,?,?,?,?,?,?,?,?,?)");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getVdsChannelCode())) {
            query.setParameter(1, vdsStaffDTO.getVdsChannelCode().trim());
        }else {
            query.setParameter(1,null);
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getShopCode())) {
            query.setParameter(2, vdsStaffDTO.getShopCode().trim());
        }else {
            query.setParameter(2, null);
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffCode())) {
            query.setParameter(3, vdsStaffDTO.getStaffCode().trim());
        }else {
            query.setParameter(3,null);
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffName())) {
            query.setParameter(4, vdsStaffDTO.getStaffName().trim());
        }else {
            query.setParameter(4, null);
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getPhoneNumber())) {
            query.setParameter(5, vdsStaffDTO.getPhoneNumber().trim());
        }else {
            query.setParameter(5, null);
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getEmail())) {
            query.setParameter(6, vdsStaffDTO.getEmail().trim());
        }else {
            query.setParameter(6, null);
        }
        query.setParameter(7, Constants.PARAM_STATUS);
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffCode())) {
            query.setParameter(8, staffDTO.getStaffCode().trim());
        }else {
            query.setParameter(8, null);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formatter.format(new Date());
        query.setParameter(9, date);
        query.setParameter(10, Constants.PARAM_STATUS);
        query.executeUpdate();
        Query query1 = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
        return (BigInteger) query1.getSingleResult();
    }

    /**
     * lay du lieu cho nhan vien thuoc VDS
     *
     * @param vdsStaffDTO
     * @return
     * @throws Exception
     * @author HungNN
     * @since 26/12/2019
     */
    @Override
    public List<Object[]> getStaffVDS(VdsStaffDTO vdsStaffDTO) throws Exception {
        HashMap hashMap = new HashMap();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT vs.id, vs.staff_code, vs.staff_name,vs.phone_number,vs.email, vs.shop_code,vgc.vds_channel_name, mip.short_name, vs.staff_type, vs.shop_warning, vgc.vds_channel_code" +
                " FROM vds_staff vs, vds_group_channel vgc, manage_info_partner mip");
        sqlBuilder.append(sqlCondition(vdsStaffDTO, hashMap));
        sqlBuilder.append(" order by id desc");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        hashMap.forEach((k, v) -> {
            query.setParameter(k.toString(), v);
        });
        return query.getResultList();
    }

    /**
     * cap nhat lai thong tin trong bang vds_staff
     *
     * @param vdsStaffDTO
     * @return
     * @throws Exception
     * @author HungNN
     * @since 26/12/2019
     */
    @Transactional
    @Override
    public void updateStaffVds(VdsStaffDTO vdsStaffDTO, StaffDTO staffDTO, String condition) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("update vds_staff set staff_name = :staffName, shop_code = :shopCode,phone_number =:phoneNumber, email =:email, status =:status, staff_type = :staffType, shop_warning =:shopWarning, vds_channel_code = :vdsChannelCode");
        if (condition.equals(Constants.UPDATE)){
            sqlBuilder.append(", created_user = :user, create_date = :createDate");
        }
        sqlBuilder.append(" where id = :id");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffName())) {
            query.setParameter("staffName", vdsStaffDTO.getStaffName().trim());
        }else {
            query.setParameter("staffName", null);
        }
        query.setParameter("shopCode", vdsStaffDTO.getShopCode().trim());
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getStatus())) {
            query.setParameter("status", vdsStaffDTO.getStatus().trim());
        }else {
            query.setParameter("status",null);
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getPhoneNumber())) {
            query.setParameter("phoneNumber", vdsStaffDTO.getPhoneNumber().trim());
        }else {
            query.setParameter("phoneNumber", null);
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getEmail())) {
            query.setParameter("email", vdsStaffDTO.getEmail().trim());
        }else {
            query.setParameter("email",null);
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffType())) {
            query.setParameter("staffType", vdsStaffDTO.getStaffType().trim());
        }else {
            query.setParameter("staffType", null);
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getShopWarning())){
            query.setParameter("shopWarning", vdsStaffDTO.getShopWarning().trim());
        }else {
            query.setParameter("shopWarning", null);
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getVdsChannelCode())){
            query.setParameter("vdsChannelCode", vdsStaffDTO.getVdsChannelCode());
        }else {
            query.setParameter("vdsChannelCode", null);
        }
        if(condition.equals(Constants.UPDATE)){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = formatter.format(new Date());
            query.setParameter("user", staffDTO.getStaffCode());
            query.setParameter("createDate", date);
        }
        query.setParameter("id", vdsStaffDTO.getId());
        query.executeUpdate();
    }

    @Transactional
    @Override
    public void persist(VdsStaff vdsStaff) throws Exception {
        entityManager.persist(vdsStaff);
    }

    @Override
    public Staff getStaff(String staffCode) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select s from Staff s where staffCode = :staffCode and status = '1'");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("staffCode", staffCode.trim());
        List<Staff> list = query.getResultList();
        if(list.size() > 0) {
            return list.get(0);
        }else {
            return null;
        }
    }

    /**
     * kiem tra xem nhan vien-theo staff_code truyen vao co ton tai trong bang staff hay khong
     *
     * @param staffCode
     * @return true neu nhan vien thuoc staff
     * @throws Exception
     * @author HungNN
     * @since 27/12/2019
     */
    @Override
    public boolean checkStaffInStaff(String staffCode) throws Exception {
        Staff staff = getStaff(staffCode);
        if (staff != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String checkStaffInShop(String staffCode) throws Exception {
        try {
            Staff staff = getStaff(staffCode);
            StringBuilder sqlBuilder = new StringBuilder();
            Long shopId = staff.getShopId();
            sqlBuilder.append("select s.shopCode from Shop s where shopId = :shopId");
            Query query = entityManager.createQuery(sqlBuilder.toString());
            query.setParameter("shopId", shopId);
            List<String> list = query.getResultList();
            String shopCode = null;
            if (list.size() > 0) {
                shopCode = list.get(0);
                return shopCode;
            } else {
                return shopCode;
            }
        }catch (NoResultException e){
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }


    /**
     * kiem tra xem nhan vien-theo staff_code truyen vao co thuoc bang vds_staff hay khong
     *
     * @param staffCode
     * @return true neu nhan vien thuoc vds_staff
     * @throws Exception
     * @author HungNN
     * @since 27/12/2019
     */
    @Override
    public boolean checkStaffInVdsStaff(String staffCode) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select v from VdsStaff v where staffCode =: staffCode and status = '1'");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("staffCode", staffCode);
        List<VdsStaff> list = query.getResultList();
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * kiem tra xem nhan vien co ton tai trong vds_staff theo dieu kien truyen vao ko
     *
     * @param vdsStaffDTO
     * @return true neu nhan vien thuoc vds_staff
     * @throws Exception
     * @author HungNN
     * @since 27/12/2019
     */
    @Override
    public boolean checkDuplicate(VdsStaffDTO vdsStaffDTO) throws Exception {
        Long id = getIdByKey(vdsStaffDTO,Constants.UPDATE);
        if (id != null) {
            return true;
        } else return false;
    }

    /**
     * lay id cua theo cap key truyen vao
     *
     * @param vdsStaffDTO
     * @return id
     * @throws Exception
     * @author HungNN
     * @since 28/12/2019
     */
    @Override
    public Long getIdByKey(VdsStaffDTO vdsStaffDTO, String access) throws Exception {
        try {
            Query query;
            StringBuilder sqlBuilder = new StringBuilder();
            if (access.equals(Constants.UPDATE)) {
                sqlBuilder.append("select v.id from vds_staff v where v.staff_code = :staffCode and v.shop_code = :shopCode and v.vds_channel_code = :vdsChannelCode and v.id <> :id");
                query = entityManager.createNativeQuery(sqlBuilder.toString());
                query.setParameter("staffCode", vdsStaffDTO.getStaffCode());
                query.setParameter("shopCode", vdsStaffDTO.getShopCode());
                query.setParameter("vdsChannelCode", vdsStaffDTO.getVdsChannelCode());
                query.setParameter("id", vdsStaffDTO.getId());
            } else {
                sqlBuilder.append("select v.id from vds_staff v where staff_code = :staffCode and shop_code = :shopCode and vds_channel_code = :vdsChannelCode");
                query = entityManager.createNativeQuery(sqlBuilder.toString());
                query.setParameter("staffCode", vdsStaffDTO.getStaffCode());
                query.setParameter("shopCode", vdsStaffDTO.getShopCode());
                query.setParameter("vdsChannelCode", vdsStaffDTO.getVdsChannelCode());
            }
            BigInteger id = (BigInteger) query.getSingleResult();
            if (id != null) {
                return id.longValue();
            } else return null;
        }catch (NoResultException e){
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * lay ra ten nhan vien theo staff_code va shop_code truyen vao
     *
     * @param staffCode, shopCode
     * @return true neu nhan vien thuoc vds_staff
     * @throws Exception
     * @author HungNN
     * @since 27/12/2019
     */
    @Override
    public String getStaffName(String staffCode, String shopCode) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select s.name from Staff s, Shop sp where s.staffCode = :staffCode AND sp.shopCode =: shopCode AND s.shopId = sp.shopId");
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("staffCode", staffCode);
        query.setParameter("shopCode", shopCode);
        return (String) query.getSingleResult();
    }

    /**
     * tim vds_staff theo du lieu trong file excel
     *
     * @param vdsStaffExcel
     * @return VdsStaff
     * @throws Exception
     * @author HungNN
     * @since 28/12/2019
     */
    @Override
    public VdsStaff findVdsStaffExcel(VdsStaffExcel vdsStaffExcel) throws Exception {
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            HashMap hashMap = new HashMap();
            sqlBuilder.append("select vs from VdsStaff vs");
            sqlBuilder.append(conditionSQLExcel(vdsStaffExcel, hashMap));
            Query query = entityManager.createQuery(sqlBuilder.toString());
            hashMap.forEach((k, v) -> {
                query.setParameter(k.toString(), v);
            });
            List<VdsStaff> list = query.getResultList();
            if (list.size() > 0) {
                return list.get(0);
            } else return null;
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * tim nhan vien thuoc staff nhung khong thuoc vds_staff
     *
     * @return list
     * @throws Exception
     * @author HungNN
     * @since 30/12/2019
     */
    @Override
    public List<Object[]> getStaffInStaff(String shopCode) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select s.staff_id,s.staff_code,s.name from staff s, shop sp" +
                " where s.`status` = '1'" +
                " and sp.`status` = '1'" +
                " and s.staff_code not in (select s.staff_code from staff s, vds_staff vs where s.staff_code = vs.staff_code)" +
                " and sp.shop_id = s.shop_id" +
                " and sp.shop_code = :shopCode" +
                " order by staff_code");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("shopCode",shopCode);
        return query.getResultList();
    }

    @Transactional
    @Override
    public void update(VdsStaff vdsStaff) throws Exception {
        entityManager.merge(vdsStaff);
    }

    /**
     * check duplicate email trong vds_staff
     *
     * @return true neu email da ton tai
     * @throws Exception
     * @author HungNN
     * @since 04/01/2020
     */
    @Override
    public boolean checkDuplicateEmail(String email) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select vs from VdsStaff vs where email = :email");
        Query query = entityManager.createQuery(stringBuilder.toString());
        query.setParameter("email",email);
        List<VdsStaff> vdsStaffs = query.getResultList();
        if(vdsStaffs.size() > 0){
            return true;
        }else
        return false;
    }

    /**
     * chuoi dieu kien tim kiem trong vds_staff
     *
     * @param vdsStaffExcel
     * @return chuoi
     * @throws Exception
     * @author HungNN
     * @since 28/12/2019
     */
    StringBuilder conditionSQLExcel(VdsStaffExcel vdsStaffExcel, HashMap hashMap) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" where 1 = 1");
        if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getStaffCode())) {
            sqlBuilder.append(" and staffCode = :staffCode");
            hashMap.put("staffCode", vdsStaffExcel.getStaffCode());
        }
        if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getShopCode())) {
            sqlBuilder.append(" and shopCode = :shopCode");
            hashMap.put("shopCode", vdsStaffExcel.getShopCode());
        }
//        if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getPhoneNumber())) {
//            sqlBuilder.append(" and phoneNumber = :phoneNumber");
//            hashMap.put("phoneNumber", vdsStaffExcel.getPhoneNumber());
//        }
//        if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getStaffName())) {
//            sqlBuilder.append(" and staffName = :staffName");
//            hashMap.put("staffName", vdsStaffExcel.getStaffName());
//        }
//        if (!DataUtil.isNullOrEmpty(vdsStaffExcel.getStaffType())) {
//            sqlBuilder.append(" and staffType = :staffType");
//            hashMap.put("staffType", vdsStaffExcel.getStaffType());
//        }
        return sqlBuilder;
    }

    /**
     * chuoi dieu kien tim kiem
     *
     * @param vdsStaffDTO
     * @return
     * @throws Exception
     * @author HungNN
     * @since 26/12/2019
     */
    StringBuilder sqlCondition(VdsStaffDTO vdsStaffDTO, HashMap hashMap) throws Exception {
        StringBuilder stringSQL = new StringBuilder();
        stringSQL.append(" WHERE 1 = 1");
        stringSQL.append(" AND ifnull(vs.vds_channel_code,'a') = ifnull(vgc.vds_channel_code,'a')");
        stringSQL.append(" AND vs.shop_code = mip.shop_code");
        stringSQL.append(" AND ifnull(vs.vds_channel_code,'a') = ifnull(mip.vds_channel_code,'a')");
        stringSQL.append(" AND vgc.`status` = 1");
        stringSQL.append(" AND vs.`STATUS` = 1");
        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffCode())) {
            stringSQL.append(" AND LOWER(vs.staff_code) like concat('%',CONVERT(LOWER(:staffCode),BINARY),'%')");
            hashMap.put("staffCode", vdsStaffDTO.getStaffCode().trim());
        }
        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffName())) {
            stringSQL.append(" AND LOWER(vs.staff_name) like concat('%',CONVERT(LOWER(:staffName),BINARY),'%')");
            hashMap.put("staffName", vdsStaffDTO.getStaffName().trim());
        }
        if (vdsStaffDTO.isPosition() == true) {
            if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getShopCode())) {
                stringSQL.append(" AND vs.shop_code = :shopCode");
                hashMap.put("shopCode", vdsStaffDTO.getShopCode());
            }
        } else {
            if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getShopCode()) && !DataUtil.isNullOrEmpty(vdsStaffDTO.getVdsChannelCode())) {
                stringSQL.append(" AND (vs.shop_code = :shopCode OR mip.parent_shop_code = :shopCode)");
                hashMap.put("shopCode", vdsStaffDTO.getShopCode());
            }else {
                stringSQL.append(" ");
            }
        }
        if(!DataUtil.isNullOrEmpty(vdsStaffDTO.getEmail())){
            stringSQL.append(" AND LOWER(vs.email) like concat('%',CONVERT(LOWER(:email),BINARY),'%')");
            hashMap.put("email", vdsStaffDTO.getEmail().trim());
        }
        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getPhoneNumber())) {
            stringSQL.append(" AND LOWER(vs.phone_number) like concat('%',CONVERT(LOWER(:phoneNumber),BINARY),'%')");
            hashMap.put("phoneNumber", vdsStaffDTO.getPhoneNumber().trim());
        }
        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getVdsChannelCode())) {
            stringSQL.append(" AND vs.vds_channel_code = :vdsChannelCode");
            hashMap.put("vdsChannelCode", vdsStaffDTO.getVdsChannelCode());
        }
        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getStaffType())) {
            stringSQL.append(" AND vs.staff_type = :staffType");
            hashMap.put("staffType", vdsStaffDTO.getStaffType());
        }
        if (!DataUtil.isNullOrEmpty(vdsStaffDTO.getShopWarning())) {
            stringSQL.append(" AND vs.shop_warning = :shopWarning");
            hashMap.put("shopWarning", vdsStaffDTO.getShopWarning());
        }
        return stringSQL;
    }
}
