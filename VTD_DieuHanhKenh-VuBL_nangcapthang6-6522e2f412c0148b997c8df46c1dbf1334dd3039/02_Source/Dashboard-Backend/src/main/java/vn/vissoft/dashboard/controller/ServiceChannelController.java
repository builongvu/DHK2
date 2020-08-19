package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.ServiceChannel;
import vn.vissoft.dashboard.services.ServiceChannelService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/servicechannel")
public class ServiceChannelController {

    private static final Logger LOGGER = LogManager.getLogger(ServiceChannelController.class);

    @Autowired
    ServiceChannelService serviceChannelService;

    @GetMapping("getByIdService/{id}")
    public ApiResponse getServiceById(@PathVariable Long id) {
        try {
            List<ServiceChannel> vlstServiceChannel = serviceChannelService.findByServiceId(id);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstServiceChannel);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
