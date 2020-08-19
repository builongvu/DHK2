package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.StaffRoleDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.Staff;
import vn.vissoft.dashboard.services.StaffService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/staff")
public class StaffController {

    private static final Logger LOGGER = LogManager.getLogger(StaffController.class);

    @Autowired
    private StaffService staffService;

    /**
     * lay nhan vien theo don vi
     *
     * @author VuBL
     * @since 2019/10/17
     * @param plngShopId
     * @return
     */
    @GetMapping("/getByShopId/{shopId}")
    public ApiResponse getByShopId(@PathVariable("shopId") Long plngShopId) {
        try {
            List<Staff> vlstStaffs = staffService.getByShopId(plngShopId);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstStaffs);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * lay danh sach nhan vien
     *
     * @author VuBL
     * @since 2019/10/17
     * @param
     * @return
     */
    @GetMapping("/getByShopCodeOfUser")
    public ApiResponse getByShopCodeOfUser(Authentication authentication) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            List<Staff> staffs = staffService.getByShopCodeOfUser(user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", staffs);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getAll")
    public ApiResponse getAllStaff() {
        try {
            List<Staff> staffs = staffService.getAllStaff();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", staffs);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/getAllByStaffAndShop")
    public ApiResponse getAllByStaffAndShop(@RequestBody StaffRoleDTO staff) {
        try {
            List<StaffRoleDTO> staffs = staffService.getAllByCodeAndName(staff.getCode(), staff.getShopcode(), staff.getRole(), staff.getIsHaveRole());
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", staffs);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
