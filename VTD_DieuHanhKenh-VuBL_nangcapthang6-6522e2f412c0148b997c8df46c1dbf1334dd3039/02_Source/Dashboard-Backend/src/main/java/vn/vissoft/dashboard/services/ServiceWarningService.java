package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.ServiceWarningConfig;

import java.util.List;

public interface ServiceWarningService {

   String update(ServiceWarningConfig serviceWarningConfig, StaffDTO staffDTO) throws Exception;

    void saveActionDetail(ServiceWarningConfig oldServiceWarning, ServiceWarningConfig newServiceWarning, Long plngActionId) throws Exception;

    String addWarning(ServiceWarningConfig serviceWarningConfig, StaffDTO staffDTO) throws Exception;
        List<ServiceWarningConfig> getAllByServiceIdAndWarningLevel(Long Service, int WarningLV, String vdsCode,String status) throws
        Exception;
        List<ServiceWarningConfig> getAllIdWarningService(Long id,Long Service, int WarningLV, String vdsCode,String value) throws Exception;
        List<ServiceWarningConfig> getCheckDuplicateAdd(Long Service, String vdsCode, Double fromvalue,Double tovalue,String value) throws  Exception;

        List<ServiceWarningConfig> getCheckDuplicateUpdate(Long id, Long Service, String vdsCode,Double fromvalue, Double tovalue ,String status) throws Exception;
       void saveNewActionDetail(ServiceWarningConfig newserviceWarningConfig,Long actionId ) throws Exception;
    void saveActionDetail1(Long actionID, String oldValue, String newValue, String column) throws Exception;
}
