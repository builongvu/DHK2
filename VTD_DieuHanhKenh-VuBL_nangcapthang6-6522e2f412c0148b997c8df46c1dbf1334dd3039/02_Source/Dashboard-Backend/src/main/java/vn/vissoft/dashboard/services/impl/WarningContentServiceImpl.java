package vn.vissoft.dashboard.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.WarningContent;
import vn.vissoft.dashboard.repo.WarningConfigRepo;
import vn.vissoft.dashboard.repo.WarningContentRepo;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.WarningContentService;
import vn.vissoft.dashboard.services.WarningSendService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class WarningContentServiceImpl implements WarningContentService {

    @Autowired
    private WarningContentRepo warningContentRepo;

    @Autowired
    WarningConfigRepo warningConfigRepoCustom;

    @Autowired
    ActionAuditService actionAuditService;

    @Autowired
    WarningSendService warningSendService;


    /**
     * Lấy ra tất cả nội dung cảnh báo theo status
     *
     * @return List<WarningContent>
     * @param mstrStatus
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<WarningContent> getAllWarningContent(String mstrStatus) throws Exception {
        return warningContentRepo.getByMstrStatusEquals(mstrStatus);
    }


    /**
     * Lấy ra nội dung cảnh báo đầu tiên
     *
     * @return WarningContent
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public WarningContent getFirst() throws Exception {
        return warningConfigRepoCustom.getFirst();
    }



    /**
     * Lưu thêm mới nội dung cảnh báo vào Action Detail
     *
     * @param warningContent
     * @param user
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public void saveActionAuditAndDetail(WarningContent warningContent, StaffDTO user) throws Exception {
        ActionAudit actionAudit = actionAuditService.log(Constants.WARNINGCONTENT.WARNING_CONTENT, Constants.ACTION_CODE_ADD, user.getStaffCode(), warningContent.getMlngId(), user.getShopCode());
        warningSendService.saveActionDetail(actionAudit.getId(), "", warningContent.getMstrContent(), Constants.WARNINGCONTENT.CONTENT);
        warningSendService.saveActionDetail(actionAudit.getId(), "", warningContent.getMstrStatus(), Constants.WARNINGCONTENT.STATUS);
    }
}
