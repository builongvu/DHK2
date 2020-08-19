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
import vn.vissoft.dashboard.dto.ServiceScoreDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.ServiceScoreExcel;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.excelgenerator.ServiceScoreWriter;
import vn.vissoft.dashboard.model.ManageInfoPartner;
import vn.vissoft.dashboard.services.ServiceScoreService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/service/score")
public class ServiceScoreController {

    @Autowired
    private ServiceScoreService serviceScoreService;

    @Autowired
    private ServiceScoreWriter serviceScoreWriter;

    private static final Logger LOGGER = LogManager.getLogger(ServiceScoreController.class);

    /**
     * api cho tab trong so chi tieu, do du lieu cho bang
     *
     * @param serviceScoreDTO de lay serviceId
     * @return list danh sach chi tieu
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 09/12/2019
     */
    @PostMapping("/getServiceScore")
    public ApiResponse getServiceScore(@RequestBody ServiceScoreDTO serviceScoreDTO){
        try {
            List<ServiceScoreDTO> services = serviceScoreService.getServiceTable(serviceScoreDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK,true, "", services);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,false, e.getMessage(), null);
        }
    }

    /**
     * search theo dieu kien truyen vao
     *
     * @param serviceScoreDTO
     * @return list danh sach chi tieu
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 10/12/2019
     */
    @PostMapping("/search")
    public ApiResponse search(@RequestBody ServiceScoreDTO serviceScoreDTO){
        try {
            List<ServiceScoreDTO> services = serviceScoreService.getServiceTable(serviceScoreDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK,true, "", services);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,false, e.getMessage(), null);
        }
    }

    /**
     * api cho combobox don vi
     *
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 10/12/2019
     */
    @GetMapping("/getUnit")
    public ApiResponse getUnitChildVds(){
        try {
            List<ManageInfoPartner> stringList = serviceScoreService.getUnitChild(Constants.SERVICE_SCORE.SERVICE_SCORE);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", stringList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * them moi ban ghi cho bang service_score
     *
     * @param serviceScoreDTO
     * @return thong bao them moi thanh cong
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 09/12/2019
     */
    @PostMapping("/addServiceScore")
    public ApiResponse addServiceScore(@RequestBody ServiceScoreDTO serviceScoreDTO, Authentication authentication){
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String message = serviceScoreService. addServiceScore(serviceScoreDTO, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", message);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * lay ten nhan vien va staff_code
     *
     * @param shopCode de lay shopCode
     * @return list ten nhan vien
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 09/12/2019
     */
    @PostMapping("/getStaffByShopCode")
    ApiResponse getStaffByShopCode(@RequestParam("shopCode") String shopCode, @RequestParam("vdsChannelCode") String vdsChannelCode){
        try{
            List<ServiceScoreDTO> nameStaff = serviceScoreService.getStaffByCondition(shopCode, vdsChannelCode);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", nameStaff);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * update bang service score
     *
     * @param serviceScoreDTO
     * @param serviceScoreId
     * @return apiRespon
     * @throws Exception
     * @author HungNN
     * @version 1.0
     * @since 10/12/2019
     */
    @PutMapping("/updateServiceScore/{id}")
    ApiResponse updateServiceScore(@PathVariable(name = "id") Long serviceScoreId, @RequestBody ServiceScoreDTO serviceScoreDTO, Authentication authentication){
        try{
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String message = serviceScoreService.updateServiceScore(serviceScoreDTO, serviceScoreId, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", message);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * upload file trong so chi tieu
     *
     * @param file
     * @return
     * @author DatNT
     * @version 1.0
     * @since 15/9/2019
     */
    @PostMapping("/uploadServiceScore")
    public ApiResponse uploadServiceScore(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", serviceScoreService.upload(file, user));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }

    /**
     * download file trong so chi tieu
     *
     * @return
     * @author DatNT
     * @version 1.0
     * @since 12/12/2019
     */
    @GetMapping(value = "/downloadServiceScore")
    public ResponseEntity<InputStreamResource> excelServiceScore() {

        ByteArrayInputStream in = null;

        try {
            in = serviceScoreWriter.write(ServiceScoreExcel.class);
            return ResponseEntity.ok().body(new InputStreamResource(in));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
