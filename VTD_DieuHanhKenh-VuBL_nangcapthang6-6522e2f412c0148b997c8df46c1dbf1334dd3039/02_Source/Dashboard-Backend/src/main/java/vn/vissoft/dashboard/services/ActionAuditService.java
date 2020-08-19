package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.dto.ServiceDTO;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.dto.GroupServiceDTO;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.GroupService;

public interface ActionAuditService {
    public Long lognew(GroupService groupService);

    ActionAudit log(String objectName, String actionCode, String user, Long pkID, String shopCode);

}
