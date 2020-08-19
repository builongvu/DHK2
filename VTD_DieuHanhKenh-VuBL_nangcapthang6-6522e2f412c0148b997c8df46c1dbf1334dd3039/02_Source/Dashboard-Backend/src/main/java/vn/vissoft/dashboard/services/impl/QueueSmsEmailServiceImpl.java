package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.QueueSmsEmail;
import vn.vissoft.dashboard.repo.QueueSmsEmailRepo;
import vn.vissoft.dashboard.services.QueueSmsEmailService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueueSmsEmailServiceImpl implements QueueSmsEmailService {

    @Autowired
    private QueueSmsEmailRepo queueSmsEmailRepo;

    /**
     * lay ra cac tin nhan/email theo trang thai
     *
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/08
     */
    @Override
    public List<QueueSmsEmail> getSmsEmailByStatus(String pstrStatus) throws Exception {
        List<QueueSmsEmail> vlstDatas = new ArrayList<>();
        List<Object[]> vlstObjs = queueSmsEmailRepo.findSmsEmailByStatus(pstrStatus);

        if (!DataUtil.isNullOrEmpty(vlstObjs)) {
            for (Object[] obj : vlstObjs) {
                QueueSmsEmail queueSmsEmail = new QueueSmsEmail();
                queueSmsEmail.setId(DataUtil.safeToLong(obj[0]));
                queueSmsEmail.setSource((String) obj[1]);
                queueSmsEmail.setType((String) obj[2]);
                queueSmsEmail.setIsdnEmail((String) obj[3]);
                queueSmsEmail.setContent((String) obj[4]);
                queueSmsEmail.setImportDatetime((Timestamp) obj[5]);
                queueSmsEmail.setExecuteDatetime((Timestamp) obj[6]);
                queueSmsEmail.setExecute_status((String) obj[7]);
                vlstDatas.add(queueSmsEmail);
            }
        }

        if (DataUtil.isNullOrEmpty(vlstDatas)) {
            return null;
        } else {
            return vlstDatas;
        }
    }

    /**
     * lay ra nhung sms/email chua duoc gui
     *
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/08
     */
    @Override
    public QueueSmsEmail getSmsEmailNotSend() throws Exception {
        QueueSmsEmail queueSmsEmail = null;
        Object[] objs = queueSmsEmailRepo.findSmsEmailNotSend();

        if (!DataUtil.isNullObject(objs)) {
            queueSmsEmail = new QueueSmsEmail();
            queueSmsEmail.setId(DataUtil.safeToLong(objs[0]));
            queueSmsEmail.setSource((String) objs[1]);
            queueSmsEmail.setType((String) objs[2]);
            queueSmsEmail.setIsdnEmail((String) objs[3]);
            queueSmsEmail.setContent((String) objs[4]);
            queueSmsEmail.setImportDatetime((Timestamp) objs[5]);
            queueSmsEmail.setExecuteDatetime((Timestamp) objs[6]);
            queueSmsEmail.setExecute_status((String) objs[7]);
        }

        if (DataUtil.isNullObject(queueSmsEmail)) {
            return null;
        } else {
            return queueSmsEmail;
        }
    }

    /**
     * cap nhat trang thai cua tin nhan/email
     *
     * @param pstrStatus
     * @throws Exception
     * @auhtor VuBL
     * @since 2020/08
     */
    @Override
    public void updateStatusById(Long plngId, String pstrStatus, String pstrErrorCode) throws Exception {
        queueSmsEmailRepo.updateStatusById(plngId, pstrStatus, pstrErrorCode);
    }

    /**
     * kiem tra neu co ban ghi có trang thai la da lay(dang xu ly) thi ko chay nua
     *
     * @return
     * @throws Exception
     * @author VuBL
     * @since 2020/08
     */
    @Override
    public boolean checkProcessing() throws Exception {
        Long count;
        boolean check;
        count = queueSmsEmailRepo.checkProcessing();

        if (count != 0) {
            check = true;
        } else {
            check = false;
        }

        return check;
    }

}
