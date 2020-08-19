package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.services.VdsGroupChannelService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("${api_base_path}/management/vdsGroupChannel")
public class VdsGroupChannelController {

    @Autowired
    private VdsGroupChannelService vdsGroupChannelService;

    private static final Logger LOGGER = LogManager.getLogger(ShopUnitController.class);

    @GetMapping("/getAll")
    public ApiResponse getAll(){
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, vdsGroupChannelService.getAll());
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
