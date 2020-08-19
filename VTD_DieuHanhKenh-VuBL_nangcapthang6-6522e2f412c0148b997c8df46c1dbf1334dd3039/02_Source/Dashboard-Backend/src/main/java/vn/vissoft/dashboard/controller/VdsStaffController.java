package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VdsStaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;

import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.model.ManageInfoPartner;
import vn.vissoft.dashboard.model.Staff;
import vn.vissoft.dashboard.repo.VdsStaffRepo;
import vn.vissoft.dashboard.services.ServiceScoreService;
import vn.vissoft.dashboard.services.VdsStaffService;
import vn.vissoft.dashboard.services.impl.TopDataServiceImpl;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/vds-staff")
public class VdsStaffController {

    private static final Logger LOGGER = LogManager.getLogger(TopDataServiceImpl.class);

    @Autowired
    private VdsStaffService vdsStaffService;

    @Autowired
    private ServiceScoreService serviceScoreService;

    /**
     * show cay don vi, danh rieng cho vds_staff
     *
     * @return ApiResponse
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 06/01/2020
     */
    @GetMapping("/getUnitVds")
    public ApiResponse getUnit(){
        try {
            List<ManageInfoPartner> listUnit = serviceScoreService.getUnitChild(Constants.VDS_STAFF.VDS_STAFF);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", listUnit);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * show du lieu bang vds_staff
     *
     * @param vdsStaffDTO
     * @return ApiResponse
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @PostMapping("/show")
    public ApiResponse getStaffVds(@RequestBody VdsStaffDTO vdsStaffDTO) throws Exception{
        try{
            List<VdsStaffDTO> list = vdsStaffService.getStaffVds(vdsStaffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * them moi ban ghi cho bang vds_staff
     *
     * @param vdsStaffDTO
     * @return thong bao them moi thanh cong
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 09/12/2019
     */
    @PostMapping("/create")
    public ApiResponse createStaffVds(@RequestBody VdsStaffDTO vdsStaffDTO, Authentication authentication) throws Exception {
        try{
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String message = vdsStaffService.createStaffVds(vdsStaffDTO,user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", message);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * check nhan vien
     *
     * @param vdsStaffDTO
     * @return thong bao check
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @PostMapping("/checkStaff")
    public ApiResponse checkStaff(@RequestBody VdsStaffDTO vdsStaffDTO, Authentication authentication) throws Exception {
        try{
            String content = vdsStaffService.checkCodeInStaff(vdsStaffDTO.getStaffCode(),vdsStaffDTO.getShopCode());
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", content);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * tim kiem nhan vien theo dieu kien truyen vao
     *
     * @param vdsStaffDTO
     * @return ApiResponse
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @PostMapping("/search")
    public ApiResponse searchStaffVds(@RequestBody VdsStaffDTO vdsStaffDTO) throws Exception{
        try{
            List<VdsStaffDTO> staffDTOList = vdsStaffService.getStaffVds(vdsStaffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", staffDTOList);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * sua ban ghi trong vds_staff
     *
     * @param vdsStaffDTO
     * @return thong bao them moi thanh cong
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @PostMapping("/edit")
    public ApiResponse editStaffVds(@RequestBody VdsStaffDTO vdsStaffDTO, Authentication authentication) throws Exception{
        try {
            StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
            String message = vdsStaffService.updateVdsStaff(vdsStaffDTO,staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", message);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * xoa nhan vien trong vds_staff
     *
     * @param vdsStaffDTO
     * @return thong bao them moi thanh cong
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 27/12/2019
     */
    @PostMapping("/delete")
    public ApiResponse deleteStaffVds(@RequestBody VdsStaffDTO vdsStaffDTO, Authentication authentication) throws Exception{
        try {
            StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
            String message = vdsStaffService.deleteStaffVds(vdsStaffDTO, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", message);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * lay nhan vien thuoc staff va khong thuoc vds_staff
     *
     * @return list nhan vien
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 30/12/2019
     */
    @PostMapping("/getStaff")
    public ApiResponse getStaff(@RequestBody VdsStaffDTO vdsStaffDTO) throws Exception{
        try {
            List<VdsStaffDTO> staffDTOList = vdsStaffService.getStaff(vdsStaffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", staffDTOList);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * upload file excel trong vds_staff
     *
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 30/12/2019
     */
    @PostMapping("/upload")
    public ApiResponse uploadVdsStaff(Authentication authentication, @RequestParam("file") MultipartFile file) throws Exception {
        try {
            StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vdsStaffService.upload(file, staffDTO));
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
