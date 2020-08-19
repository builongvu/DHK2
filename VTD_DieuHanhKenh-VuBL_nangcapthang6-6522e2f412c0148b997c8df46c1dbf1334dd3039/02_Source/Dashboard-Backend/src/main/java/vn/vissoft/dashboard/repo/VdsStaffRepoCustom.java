package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VdsStaffDTO;
import vn.vissoft.dashboard.dto.excel.VdsStaffExcel;
import vn.vissoft.dashboard.model.Staff;
import vn.vissoft.dashboard.model.VdsStaff;

import java.math.BigInteger;
import java.util.List;

public interface VdsStaffRepoCustom {

    VdsStaff findVdsStaffByCondition(String pstrChannelCode,String pstrShopCode,String pstrStaffCode);

    List<VdsStaff> getActiveStaff() throws Exception;

    boolean checkExistedStaffCode(String pstrCode) throws Exception;

    boolean checkExistedStaffInShop(String pstrStaffCode,String pstrShopCode) throws Exception;

    List<String> findStaffByShopCode(String pstrShopCode) throws Exception;

    BigInteger createVdsStaff(VdsStaffDTO vdsStaffDTO, StaffDTO staffDTO) throws Exception;

    List<Object[]> getStaffVDS(VdsStaffDTO vdsStaffDTO) throws Exception;

    void updateStaffVds(VdsStaffDTO vdsStaffDTO, StaffDTO staffDTO, String condition) throws Exception;

    void persist(VdsStaff vdsStaff) throws Exception;

    Staff getStaff(String staffCode) throws Exception;

    boolean checkStaffInStaff(String staffCode) throws Exception;

    String checkStaffInShop(String staffCode) throws Exception;

    boolean checkStaffInVdsStaff(String staffCode) throws Exception;

    boolean checkDuplicate(VdsStaffDTO vdsStaffDTO) throws Exception;

    Long getIdByKey(VdsStaffDTO vdsStaffDTO, String access) throws Exception;

    String getStaffName(String staffCode, String shopCode) throws Exception;

    VdsStaff findVdsStaffExcel(VdsStaffExcel vdsStaffExcel) throws Exception;

    List<Object[]> getStaffInStaff(String shopCode) throws Exception;

    void update(VdsStaff vdsStaff) throws Exception;

    boolean checkDuplicateEmail(String email) throws Exception;
}
