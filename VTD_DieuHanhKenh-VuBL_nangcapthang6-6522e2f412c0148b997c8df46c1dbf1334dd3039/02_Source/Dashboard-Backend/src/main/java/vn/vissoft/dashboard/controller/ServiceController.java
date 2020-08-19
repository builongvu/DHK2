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
import vn.vissoft.dashboard.dto.LogHistoryServiceDTO;
import vn.vissoft.dashboard.dto.ServiceDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.ServiceExcel;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.excelgenerator.ServiceExcelWriter;
import vn.vissoft.dashboard.mapper.BaseMapper;
import vn.vissoft.dashboard.model.Service;
import vn.vissoft.dashboard.repo.GroupServiceRepo;
import vn.vissoft.dashboard.repo.UnitRepo;
import vn.vissoft.dashboard.services.ServiceService;
import vn.vissoft.dashboard.services.UnitService;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api_base_path}/management/service")
public class ServiceController {

    private static final Logger LOGGER = LogManager.getLogger(ServiceController.class);

    private ServiceExcelWriter serviceExcelWriter = new ServiceExcelWriter();

    @Value("${server.tomcat.basedir}")
    private String path;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private GroupServiceRepo groupServiceRepo;

    @Autowired
    UnitService unitService;

    private BaseMapper<vn.vissoft.dashboard.model.Service, ServiceDTO> mapper = new BaseMapper<vn.vissoft.dashboard.model.Service, ServiceDTO>(vn.vissoft.dashboard.model.Service.class, ServiceDTO.class);

    /**
     * lay ra danh sach chi tieu(service) cho combobox chi tieu trong giao chi tieu VDS va cac cap
     *
     * @param serviceDTO
     * @return ApiResponse
     * @author VuBL
     * @since 2019/09
     */
    @PostMapping("/getServicesByCondition")
    public ApiResponse getServicesByCondition(@RequestBody ServiceDTO serviceDTO) {
        try {
            List<ServiceDTO> vlstServices = serviceService.getServicesByCondition(serviceDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstServices);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }


    /**
     * Tạo chỉ tiêu mới
     *
     * @param serviceDTO
     * @return ApiResponse
     * @author Manhtd
     * @since 2019/11
     */
    @Transactional
    @PostMapping("/createService")
    public ApiResponse createService(@RequestBody ServiceDTO serviceDTO, Authentication authentication) {
        try {
            if (!serviceService.checkCodeConflict(serviceDTO.getCode())) {
                return ApiResponse.build(HttpServletResponse.SC_CONFLICT, false, "code conflict", serviceDTO);
            } else {
                StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
                serviceService.createService(serviceDTO, staffDTO);
                return ApiResponse.build(HttpServletResponse.SC_CREATED, true, "", serviceDTO);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_BAD_REQUEST, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getByChannelCodeOfUser")
    public ApiResponse getByChannelCodeOfUser(Authentication authentication) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            List<Service> vlstServices = serviceService.getByChannelCodeOfUser(user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstServices);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Lấy tất cả service
     *
     * @return ApiResponse
     * @author Manhtd
     * @since 2019/11
     */
    @GetMapping("/getAllService")
    public ApiResponse getAllService() {
        List<Service> vlstServices = serviceService.getAllServices();
        if (DataUtil.isNullOrEmpty(vlstServices)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstServices);
    }

    @GetMapping("/getAllServiceByStatus")
    public ApiResponse getAllServiceByStatus() {
        List<Service> vlstServices = serviceService.findAllByStatusNotLike("0");
        if (DataUtil.isNullOrEmpty(vlstServices)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstServices);
    }

    /**
     * Sửa chỉ tiêu mới
     *
     * @param serviceDTO
     * @return ApiResponse
     * @author Manhtd
     * @since 2019/11
     */
    @PutMapping("/editService")
    public ApiResponse editService(@RequestBody ServiceDTO serviceDTO, Authentication authentication) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();

            if (serviceService.getServiceById(serviceDTO.getId()).isPresent()) {
                Service serviceById = serviceService.getServiceById(serviceDTO.getId()).get();
                if (!serviceService.checkCodeConflict(serviceDTO.getCode()) && !serviceDTO.getCode().equals(serviceById.getCode())) {
                    return ApiResponse.build(HttpServletResponse.SC_CONFLICT, false, "code conflict", serviceDTO);
                } else {
                    serviceService.editService(serviceDTO, user);
                    return ApiResponse.build(HttpServletResponse.SC_OK, true, "", serviceDTO);
                }
            } else {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "Not found", null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Lấy tất cả lịch sử service theo id
     *
     * @param id
     * @return ApiResponse
     * @author Manhtd
     * @since 2019/11
     */
    @GetMapping("/getLogOfService/{id}")
    public ApiResponse getLogOfServiceByServiceId(@PathVariable Long id) {
        List<LogHistoryServiceDTO> vlstServiceLog = serviceService.convertToLogHistory(id);
        if (DataUtil.isNullOrEmpty(vlstServiceLog)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", new ArrayList<>());
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstServiceLog);
    }

    /**
     * Tải file mẫu
     *
     * @return ResponseEntity
     * @author Manhtd
     * @since 2019/11
     */
    @GetMapping("/downloadExample")
    public ResponseEntity<InputStreamResource> excelLevelReport(Authentication authentication) {
        ByteArrayInputStream in = null;
        try {
            in = serviceExcelWriter.write(ServiceExcel.class, groupServiceRepo.findActiveGroupService(), unitService.getAllUnit());
            return ResponseEntity.ok().body(new InputStreamResource(in));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Upload file
     *
     * @return ResponseEntity
     * @author Manhtd
     * @since 2019/11
     */
    @PostMapping("/uploadService")
    public ApiResponse uploadVDS(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", serviceService.upLoadServices(file, ServiceExcel.class, user));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/downloadGuildEXP")
    public ResponseEntity<InputStreamResource> downloadGuildEXP() {
        ByteArrayInputStream in = null;
        try {
            in = serviceExcelWriter.createWordGuideExp();
            return ResponseEntity.ok().body(new InputStreamResource(in));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getServiceByOrder/{orderService}")
    public ApiResponse getServiceByOrder(@PathVariable Long orderService) {
        List<Service> vlstServiceLog = serviceService.findServiceByOrder(orderService);
        if (DataUtil.isNullOrEmpty(vlstServiceLog)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", new ArrayList<>());
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstServiceLog);
    }

    @GetMapping("/getByServiceID/{id}")
    public ApiResponse getByServiceID(@PathVariable Long id) {
        Optional optionalService = serviceService.getServiceById(id);
        if (optionalService.isPresent()) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", optionalService.get());
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", null);
    }

    @GetMapping("/checkServiceParenID/{id}")
    public ApiResponse checkServiceParenID(@PathVariable Long id) {
        List<Service> vlstServices = serviceService.checkServiceParenID(id);
        if (DataUtil.isNullOrEmpty(vlstServices)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstServices);
    }


}
