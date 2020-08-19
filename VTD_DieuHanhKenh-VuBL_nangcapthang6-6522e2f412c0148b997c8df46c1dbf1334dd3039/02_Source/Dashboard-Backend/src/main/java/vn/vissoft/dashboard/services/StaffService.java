package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.ServiceScoreDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.StaffRoleDTO;
import vn.vissoft.dashboard.dto.VdsStaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.model.Staff;

import java.util.List;

public interface StaffService {

    List<Staff> getByShopId(Long plngShopId) throws Exception;

    List<Staff> getByShopCodeOfUser(StaffDTO staffDTO) throws Exception;

    List<Staff> getAllStaff();

    List<StaffRoleDTO> getAllByCodeAndName(String staff, String shop, String role, int hasrole) throws Exception;
}


