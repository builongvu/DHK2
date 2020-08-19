package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.dto.ApParamDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ApParam;
import vn.vissoft.dashboard.services.ApParamService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/apparam")
public class ApParamController {

    private static final Logger LOGGER = LogManager.getLogger(ApParamController.class);

    @Autowired
    private ApParamService apParamService;

    /**
     * lấy ra trạng thái(ap_param) theo type và status
     *
     * @author VuBL
     * @since 2019/09
     * @param pstrType
     * @param pstrStatus
     * @return ApiResponse là trạng thái trả về
     */
    @GetMapping("/getByTypeAndStatus/{type}/{status}")
    public ApiResponse getByTypeAndStatus(@PathVariable("type") String pstrType, @PathVariable("status") String pstrStatus) {
        List<ApParamDTO> vlstApParamDTOS = apParamService.findByTypeAndStatus(pstrType, pstrStatus);
        if(DataUtil.isNullOrEmpty(vlstApParamDTOS)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstApParamDTOS);
    }
    @GetMapping("/getAllApparam")
    public  ApiResponse getAllApparam(){
        try {
            List<ApParam>  app = apParamService.getAll();
            if(DataUtil.isNullOrEmpty(app)){
                return  ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", app);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getByType/{type}")
    public ApiResponse getByType(@PathVariable("type") String pstrType) {
        try {
            List<ApParamDTO>  vlstApParamDTOS = apParamService.findByType(pstrType);
            if(DataUtil.isNullOrEmpty(vlstApParamDTOS)) {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstApParamDTOS);
        } catch (Exception e) {
//            e.printStackTrace();
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);


        }

    }
}
