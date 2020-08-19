package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.MappingGroupChannelDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.MappingGroupChannel;
import vn.vissoft.dashboard.services.MappingGroupChannelService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/mappingGroupChannel")
public class MappingGroupChannelController {

    private static final Logger LOGGER = LogManager.getLogger(MappingGroupChannelController.class);

    @Autowired
    private MappingGroupChannelService mappingGroupChannelService;

    @PostMapping("/getByCondition")
    public ApiResponse getByConddition(@RequestBody MappingGroupChannelDTO mappingGroupChannel) {
        try {
            ApiResponse apiResponse;
            List<MappingGroupChannel> vlstMappingChannels = mappingGroupChannelService.getByCondition(mappingGroupChannel);
            apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstMappingChannels);
            apiResponse.setPage(mappingGroupChannel.getPager().getPage());
            apiResponse.setPageSize(mappingGroupChannel.getPager().getPageSize());
            apiResponse.setTotalRow(mappingGroupChannelService.countByCondition(mappingGroupChannel).intValue());

            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/addMappingGroupChannel")
    public ApiResponse addMappingGroupChannel(Authentication authentication, @RequestBody MappingGroupChannel mappingGroupChannel) {
        StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
        try {
            String vstrResult = mappingGroupChannelService.persist(mappingGroupChannel, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrResult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/updateMappingGroupChannel")
    public ApiResponse updateMappingGroupChannel(Authentication authentication, @RequestBody MappingGroupChannel mappingGroupChannel) {
        StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
        try {
            String vresult = mappingGroupChannelService.update(mappingGroupChannel, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vresult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", mappingGroupChannelService.getById(id));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
