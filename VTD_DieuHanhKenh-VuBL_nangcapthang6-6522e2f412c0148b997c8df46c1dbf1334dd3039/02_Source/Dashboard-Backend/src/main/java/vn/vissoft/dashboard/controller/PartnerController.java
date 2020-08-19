package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.ManageInfoPartnerDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ManageInfoPartner;
import vn.vissoft.dashboard.services.PartnerService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/partner")
public class PartnerController {

    private static final Logger LOGGER = LogManager.getLogger(PartnerController.class);
    @Autowired
    PartnerService partnerService;

    @GetMapping("/getAllUnits")
    public ApiResponse getAllUnits() {
        List<ManageInfoPartner> vlstUnits = partnerService.getAllUnits();
        if (DataUtil.isNullOrEmpty(vlstUnits)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstUnits);
    }
    
    @GetMapping("/getActiveUnits")
    public ApiResponse getActiveUnits() {
        List<ManageInfoPartner> vlstUnits = partnerService.getActiveUnit();
        if (DataUtil.isNullOrEmpty(vlstUnits)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstUnits);
    }

    @PostMapping("/getManageInfoPartnerLevel")
    public ApiResponse getLevelPartners(Authentication authentication) {
        List<ManageInfoPartner> vlstManageInfoPartner = null;
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            vlstManageInfoPartner = partnerService.getManageInfoPartnerLevel(user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstManageInfoPartner);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/getManageInfoPartnerDashboard")
    public ApiResponse getDashboardPartners(Authentication authentication, @RequestParam("groupId") Long plngGroupId) {
        List<ManageInfoPartner> vlstManageInfoPartner = null;
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            vlstManageInfoPartner = partnerService.getPartnerDashboard(plngGroupId,user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstManageInfoPartner);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/findPartnerReport")
    public ApiResponse findPartnerReport(Authentication authentication) {
        List<ManageInfoPartner> vlstManageInfoPartner = null;
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            vlstManageInfoPartner = partnerService.findPartnerReport(user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstManageInfoPartner);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * lay danh sach don vi co status = 1 va thoa man to_date from_date cho menu trai (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @return
     */
    @GetMapping("/getByStatusAndDate")
    public ApiResponse getByStatusAndDate() {
        List<ManageInfoPartner> vlstManageInfoPartner;
        try {
            vlstManageInfoPartner = partnerService.getByStatusAndDate();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstManageInfoPartner);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * lay don vi theo cay dieu hanh (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param pstrShopCode
     * @return
     */
    @GetMapping("/getByCodeOrParentCode")
    public ApiResponse getByCodeOrParentCode(@RequestParam("shopCode") String pstrShopCode, @RequestParam("child") int pintChild) {
        List<ManageInfoPartnerDTO> vlstManageInfoPartner;
        try {
            vlstManageInfoPartner = partnerService.getByCodeOrParentCode(pstrShopCode, pintChild);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstManageInfoPartner);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * tim kiem nhanh tren cay dieu hanh (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param pstrKeySearh
     * @return
     */
    @GetMapping ("/getByCondition")
    public ApiResponse getByCondition(@RequestParam("keySearch") String pstrKeySearh) {
        List<ManageInfoPartner> vlstManageInfoPartner;
        try {
            vlstManageInfoPartner = partnerService.getByCondition(pstrKeySearh);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstManageInfoPartner);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * them moi don vi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param authentication
     * @param manageInfoPartner
     * @return
     */
    @PostMapping("/addNewManagerInfoPartner")
    public ApiResponse addNewManagerInfoPartner(Authentication authentication, @RequestBody ManageInfoPartner manageInfoPartner) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String vstrMessage = partnerService.addPartner(manageInfoPartner, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrMessage);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * cap nhat don vi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param authentication
     * @param manageInfoPartner
     * @return
     */
    @PostMapping("/updateManagerInfoPartner")
    public ApiResponse updateManagerInfoPartner(Authentication authentication, @RequestBody ManageInfoPartner manageInfoPartner) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String vstrMessage = partnerService.updatePartner(manageInfoPartner, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrMessage);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * cau hinh hoat dong/ngung hoat dong (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param authentication
     * @param manageInfoPartner
     * @return
     */
    @PostMapping("/configActive")
    public ApiResponse configActive(Authentication authentication, @RequestBody ManageInfoPartner manageInfoPartner) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            partnerService.updateStatus(manageInfoPartner, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * tim kiem don vi (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param manageInfoPartner
     * @return
     */
    @PostMapping("/getByConditionAll")
    public ApiResponse getByConditionAll(@RequestBody ManageInfoPartner manageInfoPartner) {
        List<ManageInfoPartnerDTO> vlstManageInfoPartner;
        try {
            vlstManageInfoPartner = partnerService.getByConditionAll(manageInfoPartner);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstManageInfoPartner);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
