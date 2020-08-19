package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.ServiceDTO;
import vn.vissoft.dashboard.model.GroupService;

import java.util.List;

public interface ActionDetailRepoCustom {
    public void logNew(GroupService groupService, Long ActionAuditId);
    public void log(List<String> columnList, List<String> oldValueList, List<String> newValueLidst, Long action_audit_id);


}
