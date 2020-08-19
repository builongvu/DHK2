package vn.vissoft.dashboard.services;

import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.SearchWarningReceiveDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseWarningReceive;
import vn.vissoft.dashboard.dto.excel.BaseWarningSend;
import vn.vissoft.dashboard.model.WarningReceiveConfig;
import vn.vissoft.dashboard.model.WarningSendConfig;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface WarningReceiveService {
    List<WarningReceiveConfig> getAllWarningReceive() throws Exception;

    List<WarningReceiveConfig> getAllWarningReceiveOrderBy() throws Exception;

    List<WarningReceiveConfig> getByCondition(SearchWarningReceiveDTO searchWarningReceiveDTO, int page, int size) throws Exception;

    List<WarningReceiveConfig> checkDuplicate(String shopCode, int warningLV, Long id);

    ByteArrayInputStream getTemplate() throws Exception;

    BaseWarningReceive upload(MultipartFile file, StaffDTO user) throws Exception;

    WarningReceiveConfig saveAndFlush(WarningReceiveConfig warningReceiveConfig);

    WarningReceiveConfig getById(Long mlngId);

    String updateWarningReceive(WarningReceiveConfig warningReceiveConfig, StaffDTO user) throws Exception;

    void saveActionDetail(WarningReceiveConfig oldWarning, WarningReceiveConfig newWarning, Long actionId) throws Exception;

    String saveNewReceive(WarningReceiveConfig warningReceiveConfig, StaffDTO user) throws Exception;

    void saveActionDetail(Long actionID, String oldValue, String newValue, String column);

    String unlockWarningReceive(String[] arrId, StaffDTO user) throws Exception;

    String lockWarningReceive(String[] arrId, StaffDTO user) throws Exception;
}
