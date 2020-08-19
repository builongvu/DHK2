package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.ShopUnitDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseShopUnit;
import vn.vissoft.dashboard.dto.excel.BaseWarningSend;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.model.ShopUnit;
import vn.vissoft.dashboard.repo.ShopRepo;
import vn.vissoft.dashboard.repo.ShopUnitRepo;
import vn.vissoft.dashboard.services.ShopUnitService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("${api_base_path}/management/shopunit")
public class ShopUnitController {
    @Autowired
    private ShopUnitService shopUnitService;
    @Autowired
    private ShopUnitRepo shopUnitRepo;

    private static final Logger LOGGER = LogManager.getLogger(ShopUnitController.class);

    @PostMapping("/getByCondition")
    public ApiResponse getAllShop(@RequestBody ShopUnitDTO shopUnitDTO) {
        try {
            ApiResponse apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", shopUnitService.getAllByCondition(shopUnitDTO));
            apiResponse.setTotalRow(Integer.parseInt(shopUnitService.countByCondition(shopUnitDTO).toString()));
            apiResponse.setPage(shopUnitDTO.getPager().getPage());
            apiResponse.setPageSize(shopUnitDTO.getPager().getPageSize());
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/savenew")
    public ApiResponse saveNew(@RequestBody ShopUnit shopUnit,  Authentication authentication){
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            boolean duplicate = shopUnitService.addShopUnit(shopUnit, user);
            if(duplicate){
                return ApiResponse.build(HttpServletResponse.SC_CONFLICT, false, null, null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, null);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/update")
    public ApiResponse update(@RequestBody ShopUnit shopUnit,  Authentication authentication){
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            boolean duplicate = shopUnitService.updateShopUnit(shopUnit, user);
            if(duplicate){
                return ApiResponse.build(HttpServletResponse.SC_CONFLICT, false, null, null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, null);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/lockUnlock/{status}")
    public ApiResponse lockUnlock(@RequestBody String[] id, Authentication authentication, @PathVariable String status){
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            shopUnitService.lockUnlock(id, status, user);
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

    @GetMapping("/getById/{id}")
    public ApiResponse getByID(@PathVariable Long id){
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, shopUnitService.getById(id));
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }


    @PostMapping("/upload")
    public ApiResponse uploadWarningSend(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            BaseShopUnit baseShopUnit = shopUnitService.upload(file, user);
            if (baseShopUnit.getTotal() == 0) {
                return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, I18N.get("common.table.warning.empty"), null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, baseShopUnit);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
        }
    }
}
