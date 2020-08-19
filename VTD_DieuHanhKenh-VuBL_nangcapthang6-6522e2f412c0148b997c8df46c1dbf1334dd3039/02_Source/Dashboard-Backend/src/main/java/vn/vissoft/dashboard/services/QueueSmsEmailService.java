package vn.vissoft.dashboard.services;

import vn.vissoft.dashboard.model.QueueSmsEmail;

import java.util.List;

public interface QueueSmsEmailService {

    List<QueueSmsEmail> getSmsEmailByStatus(String pstrStatus) throws Exception;

    QueueSmsEmail getSmsEmailNotSend() throws Exception;

    void updateStatusById(Long plngId, String pstrStatus, String pstrErrorCode) throws Exception;

    boolean checkProcessing() throws Exception;

}
