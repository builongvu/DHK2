package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.VttObject;
import vn.vissoft.dashboard.services.VttObjectService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/vttObject")
public class VttObjectController {
    private static final Logger LOGGER = LogManager.getLogger(VttObjectController.class);

    @Autowired
    private VttObjectService vttObjectService;

    @GetMapping("/getActiveVttObject")
    public ApiResponse getActiveVttObject() {
        try {
            List<VttObject> vlstVttObjects = vttObjectService.getActiveVttObject();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstVttObjects);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
