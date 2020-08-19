package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.GroupServiceDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.GroupService;
import vn.vissoft.dashboard.repo.GroupServiceRepo;
import vn.vissoft.dashboard.services.GroupServiceService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;


@RestController
@RequestMapping("${api_base_path}/management/groupservice")

public class GroupServiceController implements Serializable {
    private static final Logger LOGGER = LogManager.getLogger(GroupServiceController.class);

    @Autowired
    private GroupServiceService groupServiceService;

    @Autowired
    private GroupServiceRepo groupServiceRepo;

    @PostMapping("/getGroupServicesByCondition")
    public ApiResponse getGroupServicesByCondition(@RequestBody GroupServiceDTO groupServiceDTO) {
        try {
            List<GroupServiceDTO> vlstGroupServices = groupServiceService.getGroupServicesByCondition(groupServiceDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstGroupServices);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getAllGroupServices")
    public ApiResponse getAllGroupServices() {
        List<GroupService> vlstGroupServices = groupServiceService.getAllGroupServices();
        if (DataUtil.isNullOrEmpty(vlstGroupServices)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);

        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstGroupServices);
    }

    /**
     * lay ra danh sach nhom(group_service) cho combobox nhom trong giao chi tieu VDS
     *
     * @return ApiResponse
     * @author VuBL
     * @since 2019/09
     */
    @GetMapping("/getByStatus")
    public ApiResponse getByStatus() {
        List<GroupService> vlstGroupServices = groupServiceService.getActiveGroupService();
        if (DataUtil.isNullOrEmpty(vlstGroupServices)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstGroupServices);
    }

    /**
     * lay ra danh sach nhom(group_service) theo san pham(product_id)
     *
     * @param plngProductId
     * @return ApiResponse
     * @author VuBL
     * @since 2019/09
     */

    @GetMapping("/getGroupServicesByProductIdAndStatus/{mlngProductId}")
    public ApiResponse getGroupServicesByPrdIdAndStatus(@PathVariable("mlngProductId") Long plngProductId) {
        List<GroupService> vlstGroupServices = groupServiceService.getGroupServicesByProductIdAndStatus(plngProductId, "1");
        if (DataUtil.isNullOrEmpty(vlstGroupServices)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstGroupServices);
    }

    @PostMapping("/addNewGroupService")
    public ApiResponse addNewGroupService(Authentication authentication, @RequestBody GroupService groupservices) {

        GroupService groupService = groupServiceRepo.findByCode(groupservices.getCode());

        if (!groupServiceService.validateGroupService(groupservices) && !DataUtil.isNullObject(groupService))
            return ApiResponse.build(HttpServletResponse.SC_BAD_REQUEST, false, "Validate error", null);
        else {
            try {
                //get date
                long millis = System.currentTimeMillis();
                Date dateLog = new Date(millis);
                Timestamp date = new Timestamp(millis);
                groupservices.setCreatedate(date);
                StaffDTO userToken = (StaffDTO) authentication.getPrincipal();
                groupservices.setUserupdate(userToken.getStaffCode());
                groupservices.setStatus("1");
                groupservices.setCode(groupservices.getCode().trim());
                groupservices.setName(groupservices.getName().trim());
                GroupService groupservicedto = groupServiceService.addNewGroupService(groupservices, userToken, dateLog);

                if (groupservicedto == null) {

                    return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "insert fail", null);
                }

                return ApiResponse.build(HttpServletResponse.SC_OK, true, "", groupservicedto);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
            }
        }
    }

    @GetMapping("/getAllReturnProductName")
    public ApiResponse getAllReturnProductName() {

        List<GroupServiceDTO> groupServiceDTOList = groupServiceService.findAllreturnProductName();
        if (groupServiceDTOList.isEmpty()) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);

        }

        return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, true, "", groupServiceDTOList);
    }

    @PostMapping(value = "getGroupServicebyCondition1")
    public ApiResponse getGroupServiceByCondition(@RequestBody GroupServiceDTO groupServiceDTO) {
        List<GroupServiceDTO> dtoList = groupServiceService.findGroupServiceByCondition(groupServiceDTO);
        if (dtoList.isEmpty()) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content not found", null);
        }

        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", dtoList);
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody GroupService groupService, Authentication authentication) {
        try {
            if (!groupServiceService.validateGroupService(groupService))
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Validate error", null);
            else {
                GroupService groupService1 = groupServiceRepo.getOne(groupService.getId());
                if (groupService1 != null) {
                    StaffDTO user = (StaffDTO) authentication.getPrincipal();
                    groupServiceService.update(groupService, user);
                    return ApiResponse.build(HttpServletResponse.SC_OK, true, "", groupService);
                } else {
                    return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "Not found", null);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }

    @GetMapping("/downloadTemplate")
    public ResponseEntity<InputStreamResource> downloadTemplate() {
        try {
            ByteArrayInputStream in = groupServiceService.getTemplate();
            return ResponseEntity.ok().body(new InputStreamResource(in));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/upLoadFile")
    public ApiResponse uploadFil(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", groupServiceService.upload(file, user));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/lock")
    public ApiResponse lockGroupService(Authentication authentication, @RequestBody String[] arrId) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String status = groupServiceService.lockGroupService(arrId, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, status);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/unlock")
    public ApiResponse unlockGroupservice(Authentication authentication, @RequestBody String[] arrId) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String status = groupServiceService.unLockGroupService(arrId, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, status);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getGroupByStatus")
    public ApiResponse getGroupByStatus() {
        List<GroupService> vlstGroupServices = groupServiceService.findAllByStatusNotLike("0");
        if (DataUtil.isNullOrEmpty(vlstGroupServices)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstGroupServices);
    }

    @PostMapping("search/{page}/{size}")
    public ApiResponse searchGroup(@RequestBody GroupService groupServiceDTO, @PathVariable("page") int page, @PathVariable("size") int size) {
        try {
            List<GroupServiceDTO> listgr = groupServiceService.getByCondition(groupServiceDTO, page, size);
            ApiResponse apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", listgr);
            apiResponse.setPage(page);
            apiResponse.setPageSize(size);
            apiResponse.setTotalRow(groupServiceService.countByCondition(groupServiceDTO, page, size).intValue());
            LOGGER.info("DK: " + groupServiceDTO);
            LOGGER.info("KQ: " + listgr);
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
