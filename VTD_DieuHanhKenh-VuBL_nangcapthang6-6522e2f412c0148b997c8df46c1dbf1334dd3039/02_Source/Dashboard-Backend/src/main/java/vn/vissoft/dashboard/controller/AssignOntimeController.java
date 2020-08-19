package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.ApParam;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.AssignOntimeService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("${api_base_path}/management/assignontime")
public class AssignOntimeController {

    private static final Logger LOGGER = LogManager.getLogger(AssignOntimeController.class);

    @Autowired
    private AssignOntimeService assignOntimeService;

    @Autowired
    private ActionAuditService actionAuditService;

    @PostMapping("addnewontime")
    public ApiResponse AddAssignOntime(Authentication authentication, @RequestBody ApParam apParam) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            apParam.setCreateDatetime(new DataUtil().getDateTimeNow());
            ApParam newapParam = assignOntimeService.SaveAndFlush(apParam);
            ActionAudit actionAudit = actionAuditService.log(Constants.APPARAM.APPARAM, Constants.ACTION_CODE_ADD, user.getStaffCode(), newapParam.getId(), user.getShopCode());
            assignOntimeService.SaveNewActionDetail(newapParam, actionAudit.getId());
            return ApiResponse.build(HttpServletResponse.SC_OK, true, null, apParam);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("updatenewontime")
    public ApiResponse UpdateAssignOntime(Authentication authentication, @RequestBody ApParam apParam) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            ApParam oldap = assignOntimeService.getById(apParam.getId());
            apParam.setCreateDatetime(new DataUtil().getDateTimeNow());
            if (!oldap.getValue().equals(apParam.getValue())) {
                ActionAudit actionAudit = actionAuditService.log(Constants.APPARAM.APPARAM, Constants.ACTION_CODE_EDIT, user.getStaffCode(), apParam.getId(), user.getShopCode());
                assignOntimeService.SaveActionDetail(oldap, apParam, actionAudit.getId());
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, null, apParam);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
