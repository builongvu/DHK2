package vn.vissoft.dashboard.repo;

import viettel.passport.client.UserToken;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.StaffRoleDTO;
import vn.vissoft.dashboard.dto.StaffUnitDTO;
import vn.vissoft.dashboard.dto.VdsStaffDTO;
import vn.vissoft.dashboard.model.Staff;

import java.util.List;

public interface StaffRepoCustom {

    Long findStaffIdByCode(String pstrCode) throws Exception;

    List<Staff> findStaffInUnit(String pstrShopCode) throws Exception;

    boolean checkExistedStaffInUnit(String pstrShopCode,String pstrStaffCode) throws Exception;

    StaffDTO findStaffByLoginUser(UserToken vsaToken) throws Exception;

    List<Staff> findStaffsByCondition(Staff staff) throws Exception;

    List<Staff> findByUnitCodeOfUser(StaffDTO staffDTO) throws Exception;

    List<String> getStaffCodeLevel(List<Staff> plstStaffs) throws Exception;

    String findStaffNameByCode(String pstrCode) throws Exception;

    List<Object[]> findNameStaff(String pstrShopCode, String pstrVdsChannelCode) throws Exception;

    List<String> findByStaffType() throws Exception;

    boolean checkExistedStaffBusiness(String pstrStaffCode) throws Exception;

    List<StaffRoleDTO> findByStaffCodeAndStaffName(String staff, String shopname, String role, int hasrole) throws Exception;
}
