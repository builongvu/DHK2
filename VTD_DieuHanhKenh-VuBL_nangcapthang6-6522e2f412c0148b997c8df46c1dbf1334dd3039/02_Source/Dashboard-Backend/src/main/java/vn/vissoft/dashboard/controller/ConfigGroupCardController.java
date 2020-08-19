package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.chart.ConfigGroupCardDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.model.ConfigGroupCard;
import vn.vissoft.dashboard.services.GroupCardService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/groupcard")
public class ConfigGroupCardController {

    private static final Logger LOGGER = LogManager.getLogger(ConfigGroupCardController.class);

    @Autowired
    private GroupCardService groupCardService;

    /**
     * lay tat ca nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @return
     */
    @GetMapping("/getAll")
    public ApiResponse getAll() {
        List<ConfigGroupCardDTO> vlstData;
        try {
            vlstData = groupCardService.getAllGroupCard();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstData);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * tim kiem nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @param configGroupCard
     * @return
     */
    @PostMapping("/getByCondition")
    public ApiResponse getByCondition(@RequestBody ConfigGroupCardDTO configGroupCard) {
        List<ConfigGroupCardDTO> vlstData;
        try {
            vlstData = groupCardService.getByCondition(configGroupCard);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstData);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * them moi nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @param configGroupCard
     * @param authentication
     * @return
     */
    @PostMapping("/addGroupCard")
    public ApiResponse addGroupCard(@RequestBody ConfigGroupCardDTO configGroupCard, Authentication authentication) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String vstrMessage = groupCardService.addGroupCard(configGroupCard, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrMessage);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * cap nhat nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @param configGroupCard
     * @param authentication
     * @return
     */
    @PostMapping("/updateGroupCard")
    public ApiResponse updateGroupCard(@RequestBody ConfigGroupCardDTO configGroupCard, Authentication authentication) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String vstrMessage = groupCardService.updateGroupCard(configGroupCard, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrMessage);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * xoa nhom (group)
     *
     * @author VuBL
     * @since 2020/02
     * @param pintGroupId
     * @return
     */
    @GetMapping("/deleteGroupCard/{groupId}")
    public ApiResponse deleteGroupCard(@PathVariable(value = "groupId") int pintGroupId, Authentication authentication) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            String vstrMessage = groupCardService.deleteGroupCard(pintGroupId, user);
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vstrMessage);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

}
