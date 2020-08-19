package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.UnitDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Unit;
import vn.vissoft.dashboard.services.UnitService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/unit")
public class UnitController {

    @Autowired
    UnitService unitService;

    private static final Logger LOGGER = LogManager.getLogger(UnitController.class);

    @GetMapping("/getAllUnit")
    public ApiResponse getAllService() {
        try {
            List<Unit> vlstUnits = unitService.getAllUnit();
            if (DataUtil.isNullOrEmpty(vlstUnits)) {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstUnits);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
        }
    }

    @GetMapping("/getAll")
    public ApiResponse getAll() {
        try {
            List<Unit> vlstUnits = unitService.getAll();
            if (DataUtil.isNullOrEmpty(vlstUnits)) {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstUnits);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
        }
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", unitService.getById(id));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
        }
    }

    @PostMapping("/getByCondition")
    public ApiResponse getByCondition(@RequestBody UnitDTO unitDTO) {
        try {
            ApiResponse apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", unitService.getByCondition(unitDTO));
            apiResponse.setTotalRow(unitService.countByCondition(unitDTO).intValue());
            apiResponse.setPage(unitDTO.getPager().getPage());
            apiResponse.setPageSize(unitDTO.getPager().getPageSize());
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
        }
    }

    @PostMapping("/add")
    public ApiResponse addNewUnit(@RequestBody Unit unit,  Authentication authentication) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            unitService.addUnit(unit, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
        }
    }

    @PostMapping("/update")
    public ApiResponse updateUnit(@RequestBody Unit unit,  Authentication authentication) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            unitService.updateUnit(unit, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
        }
    }

    @PostMapping("/lockUnlock/{status}")
    public ApiResponse lockUnlock(@RequestBody String[] id, Authentication authentication, @PathVariable String status){
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            unitService.lockUnlock(id, status, user);
            String data = "";
            if(Constants.PARAM_STATUS_0.equals(status)){
                data = I18N.get("common.table.warning.lockSuccess");
            }else{
                data = I18N.get("common.table.warning.unlockSuccess");
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, data);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
