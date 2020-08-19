package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.Position;
import vn.vissoft.dashboard.services.PositionService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/position")
public class PositionController {

    private static final Logger LOGGER = LogManager.getLogger(PositionController.class);

    @Autowired
    private PositionService positionService;

    @GetMapping("/getAllPosition")
    public ApiResponse getAllPosition() {
        try {
            List<Position> vlstPositions = positionService.getAllPosition();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstPositions);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
