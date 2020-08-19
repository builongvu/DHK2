package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.DashboardRequestDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.TestSendSmsEmail;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.VdsGroupChannel;
import vn.vissoft.dashboard.services.ChannelService;
import vn.vissoft.dashboard.services.QueueSmsEmailService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/channel")
public class ChannelController {

    private static final Logger LOGGER = LogManager.getLogger(ChannelController.class);
    @Autowired
    private ChannelService channelService;

    /**
     * lay ra danh sach kenh(channel) cho combobox kenh cua giao chi tieu VDS va cac cap
     *
     * @return ApiResponse
     * @author VuBL
     * @since 2019/09
     */
    @GetMapping("/getByStatus")
    public ApiResponse getByStatus() {
        try {
            List<VdsGroupChannel> vlstChannels = channelService.getActiveChannel();
            if (DataUtil.isNullOrEmpty(vlstChannels)) {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstChannels);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getAll")
    public ApiResponse getAll() {
        try {
            List<VdsGroupChannel> vlstChannels = channelService.findAll();
            if (DataUtil.isNullOrEmpty(vlstChannels)) {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstChannels);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/addVdsGroupChannel")
    public ApiResponse addVdsGroupChannel(Authentication authentication, @RequestBody VdsGroupChannel vdsGroupChannel) {
        StaffDTO staffDTO = (StaffDTO) authentication.getPrincipal();
        try {
            String vstrResult = channelService.persist(vdsGroupChannel, staffDTO);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrResult);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
//            e.printStackTrace();
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * lay ra combobox nhom kenh vds
     *
     * @return ApiResponse
     * @author VuBL
     * @since 2019/09
     */
    @GetMapping("/getActiveVdsGroupChannel")
    public ApiResponse getActiveVdsGroupChannel() {
        try {
            List<VdsGroupChannel> vlstChannels = channelService.getActiveGroupChannel();
            if (DataUtil.isNullOrEmpty(vlstChannels)) {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
            }
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstChannels);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * combobox nhom kenh VDS (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @param pstrShopCode
     * @return
     */
    @GetMapping("/getChannelByCondition")
    public ApiResponse getChannelByCondition(@RequestParam("shopCode") String pstrShopCode) {
        try {
            List<VdsGroupChannel> vlstData = channelService.getChannelByCondion(pstrShopCode);
            if (DataUtil.isNullOrEmpty(vlstData)) {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
            } else {
                return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstData);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @GetMapping("/getNameByCode/{vdsChannelCode}")
    public ApiResponse getNameByCode(@PathVariable String vdsChannelCode) {
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", channelService.getNameByCode(vdsChannelCode));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
//            e.printStackTrace();
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    public String test(TestSendSmsEmail testSendSmsEmail) throws Exception {
        try {
            if (testSendSmsEmail.getSubjects().equalsIgnoreCase(Constants.SEND_SMS_EMAIL.SMS)) {
                return "201";
            }
            else if (testSendSmsEmail.getSubjects().equalsIgnoreCase(Constants.SEND_SMS_EMAIL.EMAIL)) {
                return "201";
            } else {
                return "500";
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "500";
    }

    @Autowired
    private QueueSmsEmailService queueSmsEmailService;

    @GetMapping("/test")
    public ApiResponse test() {
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", queueSmsEmailService.getSmsEmailNotSend());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
//            e.printStackTrace();
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
