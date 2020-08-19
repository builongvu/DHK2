package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.model.VttPosition;

public interface VttPositionService {
    String persist(VttPosition vttPosition, StaffDTO staffDTO) throws Exception;

    BaseUploadEntity upload(MultipartFile file, StaffDTO user) throws Exception;

}
