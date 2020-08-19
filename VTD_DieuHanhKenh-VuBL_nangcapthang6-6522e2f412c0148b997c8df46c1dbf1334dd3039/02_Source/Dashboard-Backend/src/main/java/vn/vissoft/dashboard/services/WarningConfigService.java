package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.model.ServiceWarningConfig;
import vn.vissoft.dashboard.model.WarningConfig;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface WarningConfigService {
    List<WarningConfig> getAllWarning() throws Exception;
    List<WarningConfig> getAllWarningById(Long serviceId) throws Exception;
   // public WarningConfig addWarning(WarningConfig warningConfig);
    BaseUploadEntity upload(MultipartFile file, StaffDTO staffDTO) throws Exception;
    //void  addWarning(ServiceWarningConfig serviceWarningConfig, StaffDTO staffDTO) throws Exception;
    ByteArrayInputStream getTemplate() throws Exception;


//    String saveWarningSend(WarningConfig warningConfig, StaffDTO user) throws Exception;

}
