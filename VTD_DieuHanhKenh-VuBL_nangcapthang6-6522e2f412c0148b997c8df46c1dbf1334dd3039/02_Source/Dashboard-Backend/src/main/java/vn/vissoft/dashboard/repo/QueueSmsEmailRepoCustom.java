package vn.vissoft.dashboard.repo;

import java.util.List;

public interface QueueSmsEmailRepoCustom {

    List<Object[]> findSmsEmailByStatus(String pstrStatus) throws Exception;

    Object[] findSmsEmailNotSend() throws Exception;

    void updateStatusById(Long plngId, String pstrStatus, String pstrErrorCode) throws Exception;

    Long checkProcessing() throws Exception;

}
