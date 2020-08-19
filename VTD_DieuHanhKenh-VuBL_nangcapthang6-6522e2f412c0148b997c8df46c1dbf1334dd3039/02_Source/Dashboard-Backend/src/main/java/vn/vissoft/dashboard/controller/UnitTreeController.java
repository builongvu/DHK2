package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.dto.UnitTreeDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.services.UnitTreeService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/unittree")
public class UnitTreeController {

    @Autowired
    private UnitTreeService unitTreeService;

    private static final Logger LOGGER = LogManager.getLogger(UnitTreeController.class);

    /**
     * lấy ra danh sách đơn vị và nhân viên cho combobox đơn vị/nhân viên trong giao chỉ tiêu các cấp
     *
     * @author VuBL
     * @since 2019/09
     * @return ApiResponse là danh sách đơn vị, nhân viên trả về
     */
    @GetMapping("/getAllUnitTrees")
    public ApiResponse getAllUnitTrees() {
        try {
            List<UnitTreeDTO> vlstUnitTrees = unitTreeService.getAllUnitTrees();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstUnitTrees);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
