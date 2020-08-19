package vn.vissoft.dashboard.repo.impl;

import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.repo.QueueSmsEmailRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

public class QueueSmsEmailRepoImpl implements QueueSmsEmailRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * lay ra cac tin nhan/email theo trang thai
     *
     * @author VuBL
     * @since 2020/08
     * @return
     * @throws Exception
     */
    @Override
    public List<Object[]> findSmsEmailByStatus(String pstrStatus) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();

        vsbdSql.append("select id, source, type, isdn_email, content, ")
                .append("import_datetime, execute_datetime, execute_status ")
                .append("from queue_sms_email ")
                .append("where execute_status = :status ");

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("status", pstrStatus);
        List<Object[]> vlstObjs = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstObjs)) {
            return null;
        } else {
            return vlstObjs;
        }
    }

    /**
     * lay ra nhung sms/email chua duoc gui
     *
     * @author VuBL
     * @since 2020/08
     * @return
     * @throws Exception
     */
    @Override
    public Object[] findSmsEmailNotSend() throws Exception {
        StringBuilder vsbdSql = new StringBuilder();

        vsbdSql.append("select id, source, type, isdn_email, content, ")
                .append("import_datetime, execute_datetime, execute_status ")
                .append("from queue_sms_email ")
                .append("where (execute_status = '0' or execute_status = '3') ")
                .append("and date(now()) = date(import_datetime) ")
                .append("order by execute_status, import_datetime limit 1 ");

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        List<Object[]> vlstObjs = query.getResultList();

        if (DataUtil.isNullOrEmpty(vlstObjs)) {
            return null;
        } else {
            return vlstObjs.get(0);
        }
    }

    /**
     * cap nhat trang thai cua tin nhan/email
     *
     * @auhtor VuBL
     * @since 2020/08
     * @param pstrStatus
     * @throws Exception
     */
    @Override
    @Transactional
    public void updateStatusById(Long plngId, String pstrStatus, String pstrErrorCode) throws Exception {
        StringBuilder vsbdSql = new StringBuilder();

        vsbdSql.append("update queue_sms_email set execute_status = :status, execute_datetime = now(), error_code = :errorCode ")
                .append("where id = :id ");

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        query.setParameter("status", pstrStatus);
        query.setParameter("id", plngId);
        query.setParameter("errorCode", pstrErrorCode);
        query.executeUpdate();

    }

    /**
     * lay ra cac ban ghi co trang thai da lay ra(dang xu ly)
     *
     * @author VuBL
     * @since 2020/08
     * @return
     * @throws Exception
     */
    @Override
    public Long checkProcessing() throws Exception {
        StringBuilder vsbdSql = new StringBuilder();
        Long count;

        vsbdSql.append("select count(*) from queue_sms_email ")
                .append("where execute_status = '1' ")
                .append("and date(now()) = date(import_datetime) ");

        Query query = entityManager.createNativeQuery(vsbdSql.toString());
        count = DataUtil.safeToLong(query.getSingleResult());

        return count;
    }

}
