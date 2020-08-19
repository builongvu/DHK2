package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.Product;
import vn.vissoft.dashboard.services.ProductService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/management/product")
public class ProductController {

    private static final Logger LOGGER= LogManager.getLogger(ProductController.class);
    @Autowired
    private ProductService productService;

    /**
     * lay ra danh sach san pham(product) trong combobox san pham cua giao chi tieu VDS
     *
     * @author VuBL
     * @since 2019/09
     * @return ApiResponse
     */
    @GetMapping("/getByStatus")
    public ApiResponse getByStatus() {
        List<Product> vlstProducts = productService.getActiveProduct();
        if(DataUtil.isNullOrEmpty(vlstProducts)) {
            return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
        }
        return ApiResponse.build(HttpServletResponse.SC_OK, true, "", vlstProducts);
    }

}
