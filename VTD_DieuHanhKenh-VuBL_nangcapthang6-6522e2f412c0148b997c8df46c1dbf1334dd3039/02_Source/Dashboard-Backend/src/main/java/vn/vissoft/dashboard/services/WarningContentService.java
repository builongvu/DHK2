package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.model.WarningContent;

import java.util.List;

public interface WarningContentService {
    List<WarningContent> getAllWarningContent(String mstrStatus) throws Exception;

    WarningContent getFirst() throws Exception;
    void saveActionAuditAndDetail(WarningContent warningContent, StaffDTO user) throws Exception;
}
