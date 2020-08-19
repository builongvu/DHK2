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
import vn.vissoft.dashboard.dto.SearchWarningSendDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseWarningSend;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DownloadFileFromSystem;
import vn.vissoft.dashboard.model.Service;
import vn.vissoft.dashboard.model.WarningSendConfig;
import vn.vissoft.dashboard.repo.ApParamRepo;
import vn.vissoft.dashboard.repo.WarningConfigRepo;
import vn.vissoft.dashboard.repo.WarningSendConfigRepo;
import vn.vissoft.dashboard.services.WarningSendService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/warningsend")
public class WarningSendController {

    @Autowired
    private WarningSendService warningSendService;

    @Autowired
    private WarningConfigRepo warningConfigRepoCustom;

    @Autowired
    private WarningSendConfigRepo warningSendConfigRepo;

    @Autowired
    private ApParamRepo apParamRepo;


    @PersistenceContext
    EntityManager entityManager;

    @Value("${server.tomcat.basedir}")
    private String path;

    private DownloadFileFromSystem download = new DownloadFileFromSystem();
    private static final Logger LOGGER = LogManager.getLogger(PlanMonthlyController.class);

    /**
     * Lấy toàn bộ cấu hình gửi cảnh báo có sắp xếp theo id giảm dần
     *
     * @param
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getAll")
    public ApiResponse getAllWarningSend() {
        try {
            List<WarningSendConfig> list = warningSendService.getAllOrderBy("id", "desc");
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }


    /**
     * Lấy toàn bộ chỉ tiêu
     *
     * @param
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getService")
    public ApiResponse getAllService() {
        try {
            List<Service> list = warningSendService.getAllServiceByStatus();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }


    /**
     * Tìm kiếm cấu hình gửi cảnh báo
     *
     * @param searchWarningSendDTO
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/getByCondition/{page}/{size}")
    public ApiResponse getByCondition(@RequestBody SearchWarningSendDTO searchWarningSendDTO, @PathVariable("page") int page, @PathVariable("size") int size) {
        try {
            List<WarningSendConfig> list = warningSendService.getByCondition(searchWarningSendDTO, page, size);
            ApiResponse apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
            apiResponse.setPage(page);
            apiResponse.setPageSize(size);
            apiResponse.setTotalRow(warningConfigRepoCustom.countWarningSendGetByCondition(searchWarningSendDTO).intValue());
            LOGGER.info("DK: " + searchWarningSendDTO);
            LOGGER.info("KQ: " + list);
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }


    /**
     * Thêm mới cấu hình gửi cảnh báo và lưu vào bảng Action Audit và Action Detail
     *
     * @param warningSendConfig
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/addWarningSend")
    public ApiResponse addWarningSend(Authentication authentication, @RequestBody WarningSendConfig warningSendConfig) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String status = warningSendService.saveWarningSend(warningSendConfig, user);
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
     * Cập nhật cấu hình gửi cảnh báo và lưu vào bảng Action Audit và Action Detail
     *
     * @param authentication
     * @param warningSendConfig
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/updateWarningSend")
    public ApiResponse updateWarningSend(Authentication authentication, @RequestBody WarningSendConfig warningSendConfig) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String status = warningSendService.updateWarningSend(warningSendConfig, user);
            if (status.equals(Constants.WARNINGSEND.SUCCESS)) {
                return ApiResponse.build(HttpServletResponse.SC_OK, false, null, null);
            } else {
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, status, null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), warningSendConfig);
        }
    }

    /**
     * Tìm kiếm Chỉ tiêu theo mã chỉ tiêu
     *
     * @param id
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getService/{id}")
    public ApiResponse getServiceById(@PathVariable("id") String id) {
        try {
            List<Service> list = warningSendService.getByServiceId(id);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Tải về mẫu Excel để tải lên dữ liệu
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
            ByteArrayInputStream in = warningSendService.getTemplate();
            return ResponseEntity.ok().body(new InputStreamResource(in));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Tải về file kết quả download
     *
     * @return
     * @param fileName
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/downloadResult")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("filename") String fileName) {
        ByteArrayInputStream in = null;
        try {
            in = download.downloadFile(path, fileName);
            return ResponseEntity.ok().body(new InputStreamResource(in));
        } catch (IOException e) {
//            e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Tải lên cấu hình gửi cảnh báo từ file Excel
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
    public ApiResponse uploadWarningSend(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            BaseWarningSend baseWarningSend = warningSendService.upload(file, user);
            if (baseWarningSend.getMintTotal() == 0) {
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, I18N.get("common.table.warning.empty"), null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, baseWarningSend);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
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
    public ApiResponse unlockWarningSend(Authentication authentication, @RequestBody String[] arrId) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String status = warningSendService.unLockWarningSend(arrId, user);
            if (status.equals(I18N.get("common.table.warning.unlockSuccess"))) {
                return ApiResponse.build(HttpServletResponse.SC_OK, false, null, status);
            } else {
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
    public ApiResponse lockWarningSend(Authentication authentication, @RequestBody String[] arrId) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String status = warningSendService.lockWarningSend(arrId, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, status);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
