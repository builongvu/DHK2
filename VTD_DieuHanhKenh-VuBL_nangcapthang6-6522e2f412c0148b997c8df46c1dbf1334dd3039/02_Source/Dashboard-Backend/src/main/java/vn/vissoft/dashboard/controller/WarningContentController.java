package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.WarningContent;
import vn.vissoft.dashboard.services.WarningContentService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/warningcontent")
public class WarningContentController {

    @Autowired
    WarningContentService warningContentService;

    private static final Logger LOGGER = LogManager.getLogger(PlanMonthlyController.class);

    /**
     * lấy ra toàn bộ nội dung cảnh báo theo trạng thái
     *
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getAll")
    public ApiResponse getAllWarningContent() {
        try {
            List<WarningContent> list = warningContentService.getAllWarningContent("1");
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
