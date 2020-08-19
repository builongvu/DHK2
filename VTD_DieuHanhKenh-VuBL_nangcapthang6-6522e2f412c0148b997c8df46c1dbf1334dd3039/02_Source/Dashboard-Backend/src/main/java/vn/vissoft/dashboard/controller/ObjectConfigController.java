package vn.vissoft.dashboard.controller;

import java.io.*;

import org.apache.commons.codec.binary.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.dto.excel.BaseWarningSend;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ConfigObjects;
import vn.vissoft.dashboard.services.ConfigObjectsService;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api_base_path}/management/objectconfig")
public class ObjectConfigController {

    @Value("${server.tomcat.baseimg}")
    private String mstrUploadPath;

    private static final Logger LOGGER = LogManager.getLogger(ObjectConfigController.class);

    @Autowired
    private ConfigObjectsService configObjectsService;

    /**
     * Lấy danh sách chức năng không bị xóa
     *
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getAllNotDelete")
    public ApiResponse getAllNotDelete() {
        try {
            List<ConfigObjects> list = configObjectsService.getAllObjectNotDelete();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Lấy toàn bộ danh sách chức năng
     *
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getAll")
    public ApiResponse getAll() {
        try {
            List<ConfigObjects> list = configObjectsService.getAllObject();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Lấy danh sách chức năng đang hoạt động
     *
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getAllActive")
    public ApiResponse getAllActive() {
        try {
            List<ConfigObjects> list = configObjectsService.getAllActive();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Thêm mới chức năng
     *
     * @param authentication
     * @param configObjects
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/save")
    public ApiResponse saveNewConfigObject(Authentication authentication, @RequestBody ConfigObjects configObjects) {
        try {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", configObjectsService.saveObject(configObjects, user));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Cập nhật chức năng
     *
     * @param authentication
     * @param lstConfigObjects
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @PostMapping("/update")
    public ApiResponse updateConfigObject(Authentication authentication, @RequestBody List<ConfigObjects> lstConfigObjects) {
        try {
            Long actionId = null;
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            for (ConfigObjects configObjects : lstConfigObjects) {
                actionId = configObjectsService.updateObject(configObjects, user);
            }
            ;
            return ApiResponse.build(HttpServletResponse.SC_OK, true, "", actionId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Lấy danh sách chức năng theo chức năng cha
     *
     * @param id
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getByIdObject/{id}")
    public ApiResponse getByIdObject(@PathVariable Long id) {
        try {
            Optional<ConfigObjects> optionalConfigObjects = configObjectsService.findAllById(id);
            if (optionalConfigObjects.isPresent()) {
                return ApiResponse.build(HttpServletResponse.SC_OK, true, "", optionalConfigObjects.get());
            } else {
                return ApiResponse.build(HttpServletResponse.SC_NO_CONTENT, false, "content is not found", null);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    /**
     * Lấy danh sách chức năng theo chức năng cha
     *
     * @param parentId
     * @return
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @GetMapping("/getallbyparentid/{parentId}")
    public ApiResponse getAllByParentId(@PathVariable Long parentId) {
        try {
            if (parentId == -1L) {
                return ApiResponse.build(HttpServletResponse.SC_OK, true, "", configObjectsService.getAllByParentId(null));
            } else {
                return ApiResponse.build(HttpServletResponse.SC_OK, true, "", configObjectsService.getAllByParentId(parentId));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage(), null);
        }
    }

    @PostMapping("/upload/{code}/{id}")
    public ApiResponse uploadWarningSend(@PathVariable("code") String code, @PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
        try {
            String status = configObjectsService.upload(file,code, id);
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, status);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
        }
    }

    @PostMapping("/image")
    public ApiResponse getImage(@RequestBody String vstrUrl) {
        FileInputStream fis = null;
        try {
            File f = new File(mstrUploadPath + "/" + vstrUrl);
            fis = new FileInputStream(f);
            byte byteArray[] = new byte[(int) f.length()];
            fis.read(byteArray);
            String imageString = Base64.encodeBase64String(byteArray);
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, imageString);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
        } finally {
            if(fis!=null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

        }
    }

    @GetMapping("/checkSelectParent/{id}/{idselect}")
    public ApiResponse checkSelectParent(@PathVariable String id, @PathVariable String idselect) {
        try {
            return ApiResponse.build(HttpServletResponse.SC_OK, false, null, configObjectsService.checkSelectParent(Long.parseLong(id), Long.parseLong(idselect)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, e.getMessage() + "", null);
        }
    }
}
