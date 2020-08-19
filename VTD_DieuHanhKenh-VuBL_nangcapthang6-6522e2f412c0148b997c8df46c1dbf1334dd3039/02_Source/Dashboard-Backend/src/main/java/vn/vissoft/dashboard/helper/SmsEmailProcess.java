//package vn.vissoft.dashboard.helper;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import vn.vissoft.dashboard.controller.ChannelController;
//import vn.vissoft.dashboard.dto.TestSendSmsEmail;
//import vn.vissoft.dashboard.model.QueueSmsEmail;
//import vn.vissoft.dashboard.services.QueueSmsEmailService;
//
//@Configuration
//@EnableScheduling
//public class SmsEmailProcess {
//
//    private static final Logger LOGGER = LogManager.getLogger(SmsEmailProcess.class);
//
//    @Autowired
//    private QueueSmsEmailService queueSmsEmailService;
//
//    @Autowired
//    private ChannelController channelController;
//
//    @Bean
//    public TaskScheduler taskScheduler2() {
//        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(10);
//        return scheduler;
//    }
//
//    //dat lich chay thu tuc gui sms/email vao 8:00 - 18:00 hang ngay (quet 3s/1 lan)
//    @Scheduled(cron = "0/3 * 8-18 * * *")
//    public void schedule() {
//        boolean check;
//
//        try {
//            //buoc 1 : check xem co tin nhan nao dang co trang thai la da lay(dang xu ly) neu co thi ko chay nua
//            check = queueSmsEmailService.checkProcessing();
//            if (check == true) {
//                return;
//            }
//
//            //buoc 2 : lay ra tin nhan de gui va thuc hien gui
//            QueueSmsEmail smsEmailToSend = null;
//            smsEmailToSend = queueSmsEmailService.getSmsEmailNotSend();
//            if (!DataUtil.isNullObject(smsEmailToSend)) {
//                Long vlngId = smsEmailToSend.getId();
//                String vstrType = smsEmailToSend.getType();
//                String vstrIsdnEmail = smsEmailToSend.getIsdnEmail();
//                String vstrContent = smsEmailToSend.getContent();
//                String vstrStatus = smsEmailToSend.getExecute_status();
//
//                //buoc 3 : update trang thai sang da lay ra(dang xu ly)
//                queueSmsEmailService.updateStatusById(vlngId, Constants.SEND_SMS_EMAIL.DA_LAY, null);
//
//                //buoc 4 : test thu (thay code goi api gui sms/email vao day)
//                TestSendSmsEmail testSendSmsEmail = new TestSendSmsEmail();
//                testSendSmsEmail.setSubjects(vstrType);
//                testSendSmsEmail.setReceivers(vstrIsdnEmail);
//                String result = channelController.test(testSendSmsEmail);
//
//                /*buoc 5 : sau khi goi api gui sms/email neu thanh cong thi update status ve thanh cong
//                neu loi thi update ve loi va update them ma loi*/
//                if (result.equalsIgnoreCase("201")) {
//                    queueSmsEmailService.updateStatusById(vlngId, Constants.SEND_SMS_EMAIL.THANH_CONG, null);
//                } else {
//                    queueSmsEmailService.updateStatusById(vlngId, Constants.SEND_SMS_EMAIL.THAT_BAI, null);
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//    }
//}
