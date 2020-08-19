package vn.vissoft.dashboard.services;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.SearchWarningSendDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseUploadEntity;
import vn.vissoft.dashboard.dto.excel.BaseWarningSend;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.Service;
import vn.vissoft.dashboard.model.WarningSendConfig;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface WarningSendService {
    List<WarningSendConfig> getAllWarningSend(int page, int totalsize) throws Exception;

    List<WarningSendConfig> getByCondition(SearchWarningSendDTO sr,int page, int size) throws Exception;

    WarningSendConfig saveAndFlush(WarningSendConfig warningSendConfig) throws Exception;

    List<WarningSendConfig> getAllByServiceIdAndWarningLevel(Long Service, int WarningLV, Long id) throws
            Exception;

    List<WarningSendConfig> getAllOrderBy(String column, String orderby) throws Exception;

    List<Service> getAllServiceByStatus() throws Exception;

    List<Service> getByServiceId(String id) throws Exception;

    List<Service> getByServiceCode(String code) throws Exception;

    ByteArrayInputStream getTemplate() throws Exception;

    BaseWarningSend upload(MultipartFile file, StaffDTO user) throws Exception;

    void saveActionDetail(WarningSendConfig oldWarning, WarningSendConfig newWarning, Long actionId) throws
            Exception;

    void saveNewActionDetail(WarningSendConfig newWarning, Long actionId) throws Exception;

    WarningSendConfig getByID(Long id) throws Exception;

    String saveWarningSend(WarningSendConfig warningSendConfig, StaffDTO user) throws Exception;

    String updateWarningSend(WarningSendConfig warningSendConfig, StaffDTO user) throws Exception;

    void saveActionDetail(Long actionID, String oldValue, String newValue, String column) throws Exception;

    String unLockWarningSend(String[] arrId, StaffDTO user) throws Exception;

    String lockWarningSend(String[] arrId, StaffDTO user) throws Exception;
}
