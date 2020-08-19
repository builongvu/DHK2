package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.model.*;

@org.springframework.stereotype.Service
public interface ActionDetailService {

    void addLogService(Service service, Service oldService, Long actionAuditId);

    public void lognew(GroupService groupService, Long actionAuditID);

    void addLogService(GroupService groupService, GroupService oldgroupService, Long pkID);

    ActionDetail createActionDetail(String name, String newValue, Long actionAuditId, String oldValue);

    void addLogObjectRole(ConfigRolesObjects newConfigRolesObjects, ConfigRolesObjects oldConfigRolesObjects, Long actionAuditId);

    void addLogRole(ConfigRoles newConfigRoles, ConfigRoles oldConfigRoles, Long actionAuditId);
}
