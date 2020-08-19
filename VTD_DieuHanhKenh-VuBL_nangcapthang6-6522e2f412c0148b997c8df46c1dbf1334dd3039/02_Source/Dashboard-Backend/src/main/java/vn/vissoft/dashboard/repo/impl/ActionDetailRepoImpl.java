package vn.vissoft.dashboard.repo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ActionDetail;
import vn.vissoft.dashboard.model.GroupService;
import vn.vissoft.dashboard.repo.ActionDetailRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class ActionDetailRepoImpl implements ActionDetailRepoCustom {

    private static final Logger LOGGER = LogManager.getLogger(ActionDetailRepoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void logNew(GroupService groupService, Long actionAuditId) {

        if (!DataUtil.isNullOrZero(groupService.getId())) {

            add("Id", groupService.getId().toString(), actionAuditId, null);
        }
        if (!DataUtil.isNullOrEmpty(groupService.getCreatedate().toString())) {

            add("change_datetime", groupService.getCreatedate().toString(), actionAuditId, null);
        }
        if (!DataUtil.isNullOrEmpty(groupService.getName())) {
            add("name", groupService.getName(), actionAuditId, null);

        }
        if (!DataUtil.isNullOrEmpty(groupService.getCode())) {

            add("code", groupService.getCode().toString(), actionAuditId, null);
        }
        if (!DataUtil.isNullOrZero(groupService.getProductId())) {

            add("product_id", groupService.getProductId().toString(), actionAuditId, null);
        }
        if (!DataUtil.isNullOrEmpty(groupService.getStatus())) {

            add("Status", groupService.getStatus(), actionAuditId, null);
        }
        if (!DataUtil.isNullOrEmpty(groupService.getUserupdate())) {

            add("user_update", groupService.getUserupdate(), actionAuditId, null);
        }

    }


    @Override
    public void log(List<String> columnList, List<String> oldValueList, List<String> newValueLidst, Long action_audit_id) {


        for (int i = 0; i < columnList.size(); i++) {
            if (oldValueList != null && oldValueList.get(i).equals(newValueLidst.get(i))) {
                ActionDetail actionDetail = new ActionDetail();
                actionDetail.setActionAuditId(action_audit_id);
                actionDetail.setColumnName(columnList.get(i));
                actionDetail.setOldValue(oldValueList.get(i));
                actionDetail.setNewValue(newValueLidst.get(i));

                try {
                    entityManager.persist(actionDetail);

                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    System.out.println(e.getMessage());

                }
            }
        }

    }

    private void add(String name, String newValue, Long actionAuditId, String oldValue) {
        ActionDetail actionDetail = new ActionDetail();
        actionDetail.setActionAuditId(DataUtil.safeToLong(actionAuditId));
        actionDetail.setColumnName(DataUtil.safeToString(name));
        actionDetail.setNewValue(DataUtil.safeToString(newValue));
        actionDetail.setOldValue(oldValue);
        try {
            entityManager.persist(actionDetail);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.out.println(e.getMessage());
        }

    }

}
