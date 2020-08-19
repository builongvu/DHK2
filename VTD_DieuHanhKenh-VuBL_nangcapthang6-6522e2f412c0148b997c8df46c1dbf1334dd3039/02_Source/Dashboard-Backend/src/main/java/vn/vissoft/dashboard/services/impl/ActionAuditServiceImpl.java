package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.GroupService;
import vn.vissoft.dashboard.repo.ActionAuditRepo;
import vn.vissoft.dashboard.services.ActionAuditService;

import javax.transaction.Transactional;

@Transactional
@Service
public class ActionAuditServiceImpl implements ActionAuditService {

    @Autowired
    private ActionAuditRepo actionAuditRepo;

    @Override
    public ActionAudit log(String objectName, String actionCode, String user, Long pkID, String shopCode) {
        long millis = System.currentTimeMillis();
        ActionAudit actionAudit = new ActionAudit();
        actionAudit.setActionDateTime(new java.sql.Date(millis));
        actionAudit.setActionCode(actionCode);
        actionAudit.setObjectCode(DataUtil.safeToString(objectName));
        actionAudit.setUser(DataUtil.safeToString(user));
        actionAudit.setPkID(DataUtil.safeToLong(pkID));
        actionAudit.setShopCode(shopCode);
        return actionAuditRepo.saveAndFlush(actionAudit);
    }

    public Long lognew(GroupService groupService) {
        return actionAuditRepo.logNew(groupService);
    }
//
//    @Override
//    public Long update(GroupServiceDTO groupService) {
//        return actionAuditRepo.logUpdate(groupService);
//    }
}
