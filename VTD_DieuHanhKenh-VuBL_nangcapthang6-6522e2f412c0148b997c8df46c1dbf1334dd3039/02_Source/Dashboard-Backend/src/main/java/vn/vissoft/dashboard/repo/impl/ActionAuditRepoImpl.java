package vn.vissoft.dashboard.repo.impl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.vissoft.dashboard.dto.ServiceDTO;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.GroupService;
import vn.vissoft.dashboard.repo.ActionAuditRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


@Transactional
public class ActionAuditRepoImpl implements ActionAuditRepoCustom {

    private static final Logger LOGGER = LogManager.getLogger(ActionAuditRepoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Long logNew(GroupService groupService) {
        ActionAudit actionAudit= createOne(groupService,"00",groupService.getId());
        try {
            entityManager.persist(  actionAudit);
            return actionAudit.getId();
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }



    @Override
    public Long logUpdate(GroupService groupService) {
        ActionAudit actionAudit= createOne(groupService,"01", groupService.getId() );

        try {
            entityManager.persist(actionAudit);
            return actionAudit.getId();
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    private ActionAudit createOne(GroupService groupService, String actioncode, Long pkID){
        ActionAudit actionAudit= new ActionAudit();
        actionAudit.setActionCode(actioncode);
        actionAudit.setPkID(pkID);
        actionAudit.setActionDateTime(groupService.getCreatedate());
        actionAudit.setUser(groupService.getUserupdate());
        actionAudit.setObjectCode("Group_service");
        return  actionAudit;
    }

    private ActionAudit createNewLogService(ServiceDTO serviceDTO, String actionCode, Long actionAuditId) {
        ActionAudit actionAudit = new ActionAudit();
        actionAudit.setActionCode(actionCode);
        actionAudit.setActionDateTime(null);
        actionAudit.setPkID(actionAuditId);
        actionAudit.setUser(serviceDTO.getUser());
        actionAudit.setObjectCode("Service");
        return actionAudit;
    }

}
