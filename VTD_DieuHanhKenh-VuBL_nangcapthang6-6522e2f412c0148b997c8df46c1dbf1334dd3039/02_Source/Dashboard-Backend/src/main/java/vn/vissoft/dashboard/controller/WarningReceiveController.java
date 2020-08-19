package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.SearchWarningReceiveDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseWarningReceive;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.model.WarningReceiveConfig;
import vn.vissoft.dashboard.repo.WarningConfigRepo;
import vn.vissoft.dashboard.services.WarningReceiveService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/warningreceive")
public class WarningReceiveController {

    @Autowired
    private WarningReceiveService warningReceiveService;

    @Autowired
    private WarningConfigRepo warningConfigRepoCustom;


    @Value("${server.tomcat.basedir}")
    private String mstrUploadPath;
    private static final Logger LOGGER = LogManager.getLogger(PlanMonthlyController.class);

    /**
     * Lấy toàn bộ cấu hình nhận cảnh báo có sắp xếp theo ID giảm dần
     *
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getAll")
    public ApiResponse getAllWarningReceive() {
        try {
            List<WarningReceiveConfig> list = warningReceiveService.getAllWarningReceiveOrderBy();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Tìm kiếm cấu hình nhận cảnh báo theo điều kiện
     *
     * @param searchWarningReceiveDTO
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/getByCondition/{page}/{size}")
    public ApiResponse getByCondition(@RequestBody SearchWarningReceiveDTO searchWarningReceiveDTO,@PathVariable("page") int page, @PathVariable("size") int size) {
        try {
            List<WarningReceiveConfig> list = warningReceiveService.getByCondition(searchWarningReceiveDTO,page,size);
            ApiResponse apiResponse =ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
            apiResponse.setPage(page);
            apiResponse.setPageSize(size);
            apiResponse.setTotalRow(warningConfigRepoCustom.countWarningReceiveGetByCondition(searchWarningReceiveDTO).intValue());
            LOGGER.info("DK: " + searchWarningReceiveDTO);
            LOGGER.info("KQ: " + list);
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Thêm mới cấu hình nhận cảnh báo và lưu vào bảng ActionAudit và ActionDetail
     *
     * @param authentication
     * @param warningReceiveConfig
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/addwarningreceive")
    public ApiResponse addWarningReceive(Authentication authentication, @RequestBody WarningReceiveConfig warningReceiveConfig) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String status = warningReceiveService.saveNewReceive(warningReceiveConfig, user);
            if (status.equals(Constants.WARNINGSEND.SUCCESS)) {
                return ApiResponse.build(HttpServletResponse.SC_OK, false, null, null);
            } else {
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, status, null);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Cập nhật cấu hình nhận cảnh báo và lưu vào bảng ActionAudit và ActionDetail
     *
     * @param authentication
     * @param warningReceiveConfig
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/updatewarningreceive")
    public ApiResponse updateWarningReceive(Authentication authentication, @RequestBody WarningReceiveConfig warningReceiveConfig) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String status = warningReceiveService.updateWarningReceive(warningReceiveConfig, user);
            if (status.equals(Constants.WARNINGSEND.SUCCESS)) {
                return ApiResponse.build(HttpServletResponse.SC_OK, false, null, null);
            } else {
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, status, null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }


    /**
     * Kiếm tra cấu hình đã tồn tại hay chưa
     *
     * @param warningReceiveConfig
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/checkDuplicate")
    public ApiResponse checkDuplicate(@RequestBody WarningReceiveConfig warningReceiveConfig) {
        try {
            String vstrShopCode = warningReceiveConfig.getMstrShopCode();
            List<WarningReceiveConfig> list = warningReceiveService.checkDuplicate(vstrShopCode, warningReceiveConfig.getMintWarningLevel(), warningReceiveConfig.getMlngId());
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Tải về mẫu cấu hình nhận cảnh báo
     *
     * @return InputStreamResource
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/downloadTemplate")
    public ResponseEntity<InputStreamResource> downloadTemplate() {
        try {
            ByteArrayInputStream in = warningReceiveService.getTemplate();
            return ResponseEntity.ok().body(new InputStreamResource(in));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Tải lên cấu hình nhận cảnh báo từ file mẫu Excel
     *
     * @param authentication
     * @param file
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/upload")
    public ApiResponse uploadLevel(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            BaseWarningReceive baseWarningSend = warningReceiveService.upload(file, user);
            if (baseWarningSend.getMintTotal() == 0) {
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, I18N.get("common.table.warning.empty"), null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", baseWarningSend);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }


    /**
     * Mở khoá cấu hình theo id
     *
     * @param authentication
     * @param arrId
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/unlock")
    public ApiResponse unlockWarning(Authentication authentication, @RequestBody String[] arrId) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String status = warningReceiveService.unlockWarningReceive(arrId, user);
            if(status.equals(I18N.get("common.table.warning.unlockSuccess"))){
                return ApiResponse.build(HttpServletResponse.SC_OK, false, null, status);
            }else{
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, status, null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }


    /**
     * Mở khoá cấu hình theo id
     *
     * @param authentication
     * @param arrId
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/lock")
    public ApiResponse lockWarning(Authentication authentication, @RequestBody String[] arrId) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String status = warningReceiveService.lockWarningReceive(arrId, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, status);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
