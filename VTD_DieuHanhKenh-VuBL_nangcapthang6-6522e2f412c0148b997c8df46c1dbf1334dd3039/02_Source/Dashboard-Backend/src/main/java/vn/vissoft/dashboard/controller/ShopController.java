package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.model.Shop;
import vn.vissoft.dashboard.services.ShopSerivce;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/shop")
public class ShopController {

    @Autowired
    ShopSerivce shopSerivce;

    private static final Logger LOGGER = LogManager.getLogger(PlanMonthlyController.class);

    /**
     * lấy ra toàn bộ shop
     *
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getAll")
    public ApiResponse getAllShop() {
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", shopSerivce.getAllShops());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * lay tat ca shop dang hoat dong (chuc nang khai bao cay dieu hanh VDS)
     *
     * @author VuBL
     * @since 2019/12
     * @return
     */
    @GetMapping("/getAllByStatus")
    public ApiResponse getAllByStatus() {
        List<Shop> vlstShops;
        try {
            vlstShops = shopSerivce.getByStatusAndOrder();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstShops);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }
}
