package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Cycle;
import vn.vissoft.dashboard.services.CycleService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/cycle")
public class CycleController {

    private static final Logger LOGGER = LogManager.getLogger(CycleController.class);

    @Autowired
    private CycleService cycleService;

    /**
     * lay ra danh sach cycle tren combobox chu ky o chuc nang giao chi tieu
     *
     * @author VuBL
     * @since 2019/12
     * @return
     */
    @GetMapping("/getByAssign")
    public ApiResponse getByAssign() {
        try {
            List<Cycle> cycles = cycleService.getByAssign(1);
            if (DataUtil.isNullOrEmpty(cycles)) {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
            } else {
                return ApiResponse.build(HttpServletResponse.SC_OK, true, "", cycles);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
