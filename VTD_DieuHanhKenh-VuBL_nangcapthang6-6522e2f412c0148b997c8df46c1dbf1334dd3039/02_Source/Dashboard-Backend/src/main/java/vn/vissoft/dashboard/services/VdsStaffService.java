package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VdsStaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;

import java.util.List;

public interface VdsStaffService {

    List<VdsStaffDTO> getStaffVds(VdsStaffDTO vdsStaffDTO) throws Exception;

    String updateVdsStaff(VdsStaffDTO vdsStaffDTO, StaffDTO staffDTO) throws Exception;

    String deleteStaffVds(VdsStaffDTO vdsStaffDTO, StaffDTO staffDTO) throws Exception;

    String createStaffVds(VdsStaffDTO vdsStaffDTO, StaffDTO staffDTO) throws Exception;

    BaseUploadEntity upload(MultipartFile file, StaffDTO staffDTO) throws Exception;

    String checkCodeInStaff(String staffCode, String shopCode) throws Exception;

    List<VdsStaffDTO> getStaff(VdsStaffDTO vdsStaffDTO1) throws Exception;
}
