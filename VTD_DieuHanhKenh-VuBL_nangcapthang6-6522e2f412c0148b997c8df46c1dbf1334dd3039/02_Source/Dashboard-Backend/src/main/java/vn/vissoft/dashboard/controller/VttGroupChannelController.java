package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.Pager;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.VttGroupChannelDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.VttGroupChannel;
import vn.vissoft.dashboard.services.VttGroupChannelService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("${api_base_path}/management/vttGroupChannel")
public class VttGroupChannelController {

    private static final Logger LOGGER = LogManager.getLogger(VttGroupChannelController.class);

    @Autowired
    private VttGroupChannelService vttGroupChannelService;

    @GetMapping("/getActiveVttChannel")
    public ApiResponse getActiveVttChannel() {
        try {
            List<VttGroupChannel> vlstVttGroupChannels = vttGroupChannelService.getActiveVttChannel();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstVttGroupChannels);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/addVttGroupChannel")
    public ApiResponse addVttGroupChannel(Authentication authentication, @RequestBody VttGroupChannel vttGroupChannel) {
        StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
        try {
            String vstrResult = vttGroupChannelService.persist(vttGroupChannel, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrResult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/checkCodeDuplicate/{code}")
    public ApiResponse checkCodeDuplicate(@PathVariable String code) throws Exception {
        try {
            String check = vttGroupChannelService.checkDuplicateVttChannel(code);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", check);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/getByCondition")
    public ApiResponse getByCondition(@RequestBody VttGroupChannelDTO vttGroupChannelDTO) {
        try {
            ApiResponse apiResponse;
            List<VttGroupChannelDTO> vlstGroupChannels = vttGroupChannelService.getByCondition(vttGroupChannelDTO);

            apiResponse = ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstGroupChannels);
            apiResponse.setPage(vttGroupChannelDTO.getPager().getPage());
            apiResponse.setPageSize(vttGroupChannelDTO.getPager().getPageSize());

            VttGroupChannelDTO vttGroupChannelDTOTotal = new VttGroupChannelDTO();
            if (!DataUtil.isNullOrEmpty(vttGroupChannelDTO.getGroupChannelCode())) {
                vttGroupChannelDTOTotal.setGroupChannelCode(vttGroupChannelDTO.getGroupChannelCode());
            }

            if (!DataUtil.isNullOrEmpty(vttGroupChannelDTO.getStatus())) {
                vttGroupChannelDTOTotal.setStatus(vttGroupChannelDTO.getStatus());
            }

            Pager pager = new Pager();
            pager.setPage(1);
            pager.setPageSize(Integer.MAX_VALUE);
            vttGroupChannelDTOTotal.setPager(pager);

            List<VttGroupChannelDTO> vlstListOfAllVTTGroup = vttGroupChannelService.getByCondition(vttGroupChannelDTOTotal);
            apiResponse.setTotalRow(vlstListOfAllVTTGroup.size());

            return apiResponse;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }

    }


    @GetMapping("/getAllNotMapping/{code}")
    public ApiResponse getAllNotMapping(@PathVariable String code) {
        try {
            List<VttGroupChannel> vlstVttGroupChannels = vttGroupChannelService.getAllNotInMapping(code);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstVttGroupChannels);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
