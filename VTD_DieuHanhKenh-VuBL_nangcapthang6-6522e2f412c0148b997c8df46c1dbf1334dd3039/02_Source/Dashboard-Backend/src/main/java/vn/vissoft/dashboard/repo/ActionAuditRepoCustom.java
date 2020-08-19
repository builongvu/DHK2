package vn.vissoft.dashboard.repo;

import vn.vissoft.dashboard.dto.ServiceDTO;
import vn.vissoft.dashboard.dto.GroupServiceDTO;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.GroupService;

public interface ActionAuditRepoCustom {

    public Long logNew(GroupService groupService);

    public Long logUpdate(GroupService groupService);
}
