package vn.vissoft.dashboard.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.controller.ApParamController;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.ActionAudit;
import vn.vissoft.dashboard.model.ActionDetail;
import vn.vissoft.dashboard.model.ConfigObjects;
import vn.vissoft.dashboard.model.WarningReceiveConfig;
import vn.vissoft.dashboard.repo.ActionDetailRepo;
import vn.vissoft.dashboard.repo.ConfigObjectsRepo;
import vn.vissoft.dashboard.repo.ObjectConfigRepoCustom;
import vn.vissoft.dashboard.services.ActionAuditService;
import vn.vissoft.dashboard.services.ConfigObjectsService;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ConfigObjectsServiceImpl implements ConfigObjectsService {
    private static final Logger LOGGER = LogManager.getLogger(ConfigObjectsServiceImpl.class);

    @Autowired
    private ConfigObjectsRepo configObjectsRepo;

    @Autowired
    private ObjectConfigRepoCustom objectConfigRepoCustom;

    @Value("${server.tomcat.baseimg}")
    private String mstrUploadPath;

    @Autowired
    private ActionDetailRepo actionDetailRepo;

    @Autowired
    private ActionAuditService actionAuditService;


    /**
     * Lấy ra toàn bộ danh sách chức năng
     *
     * @return List<ConfigObjects>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<ConfigObjects> getAllObject() throws Exception {
        return configObjectsRepo.findAll();
    }


    /**
     * Lấy ra danh sách chức năng không bị xóa
     *
     * @return List<ConfigObjects>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<ConfigObjects> getAllObjectNotDelete() throws Exception {
        return configObjectsRepo.findAllByStatusNotLike(-1L);
    }


    /**
     * Thêm mới và lưu thay đổi vào bảng Action Detail
     *
     * @param configObjects
     * @param user
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public Long saveObject(ConfigObjects configObjects, StaffDTO user) throws Exception {
        ConfigObjects newObject = configObjectsRepo.saveAndFlush(configObjects);
        ActionAudit actionAudit = actionAuditService.log(Constants.OBJECT.OBJECT, Constants.ACTION_CODE_ADD, user.getStaffCode(), newObject.getId(), user.getShopCode());
        saveToActionDetail(newObject, actionAudit.getId());
        return actionAudit.getId();
    }

    public void saveToActionDetail(ConfigObjects newObject, Long actionId) {
        saveActionDetail(actionId, null, newObject.getStatus().toString(), Constants.OBJECT.STATUS);
        saveActionDetail(actionId, null, newObject.getObjectName(), Constants.OBJECT.NAME);
        saveActionDetail(actionId, null, newObject.getObjectCode(), Constants.OBJECT.CODE);
        if (!DataUtil.isNullOrEmpty(newObject.getObjectImg())) {
            saveActionDetail(actionId, null, newObject.getObjectImg(), Constants.OBJECT.IMG);
        }
        saveActionDetail(actionId, null, newObject.getObjectIcon(), Constants.OBJECT.ICON);
        if (!DataUtil.isNullOrZero(newObject.getParentId())) {
            saveActionDetail(actionId, null, newObject.getParentId().toString(), Constants.OBJECT.PARENT);
        }
        if (!DataUtil.isNullOrEmpty(newObject.getObjectNameI18N())) {
            saveActionDetail(actionId, null, newObject.getObjectNameI18N(), Constants.OBJECT.NAMEI18);
        }
        if (!DataUtil.isNullOrEmpty(newObject.getObjectUrl())) {
            saveActionDetail(actionId, null, newObject.getObjectUrl(), Constants.OBJECT.URL);
        }
        saveActionDetail(actionId, null, newObject.getFunctionType().toString(), Constants.OBJECT.FUNCTION_TYPE);
        saveActionDetail(actionId, null, newObject.getOrd().toString(), Constants.OBJECT.ORD);
        saveActionDetail(actionId, null, newObject.getObjectType(), Constants.OBJECT.OBJECT_TYPE);
    }

    public void saveActionDetail(Long actionID, String oldValue, String newValue, String column) {
        ActionDetail actionDetail = new ActionDetail();
        actionDetail.setActionAuditId(actionID);
        actionDetail.setOldValue(oldValue);
        actionDetail.setNewValue(newValue);
        actionDetail.setColumnName(column);
        actionDetailRepo.save(actionDetail);
    }


    /**
     * Cập nhật và lưu thay đổi vào bảng Action Detail
     *
     * @param configObjects
     * @param user
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public Long updateObject(ConfigObjects configObjects, StaffDTO user) throws Exception {
        Optional<ConfigObjects> optional = configObjectsRepo.getById(configObjects.getId());
        if (optional.isPresent()) {
            ConfigObjects old = optional.get();
            String actionCode;
            if ("-1".equals(configObjects.getStatus())) {
                actionCode = Constants.ACTION_CODE_DELETE;
            } else {
                actionCode = Constants.ACTION_CODE_EDIT;
            }
            ActionAudit actionAudit = actionAuditService.log(Constants.OBJECT.OBJECT, actionCode, user.getStaffCode(), configObjects.getId(), user.getShopCode());
            saveUpdateActionDetail(old, configObjects, actionAudit.getId());
            configObjectsRepo.save(configObjects);
            return actionAudit.getId();
        }
        return null;
    }

    public void saveUpdateActionDetail(ConfigObjects old, ConfigObjects newob, Long actionId) throws Exception {
        if (!old.getStatus().toString().equals(newob.getStatus().toString())) {
            saveActionDetail(actionId, old.getStatus().toString(), newob.getStatus().toString(), Constants.OBJECT.STATUS);
        }
        if (!old.getObjectName().equals(newob.getObjectName())) {
            saveActionDetail(actionId, old.getObjectName(), newob.getObjectName(), Constants.OBJECT.NAME);
        }
        if (!old.getObjectCode().equals(newob.getObjectCode())) {
            saveActionDetail(actionId, old.getObjectCode(), newob.getObjectCode(), Constants.OBJECT.CODE);
        }
        if (DataUtil.isNullOrEmpty(old.getObjectImg()) && !DataUtil.isNullOrEmpty(newob.getObjectImg())) {
            saveActionDetail(actionId, null, newob.getObjectImg(), Constants.OBJECT.IMG);
        } else if (!DataUtil.isNullOrEmpty(old.getObjectImg()) && DataUtil.isNullOrEmpty(newob.getObjectImg())) {
            saveActionDetail(actionId, old.getObjectImg(), null, Constants.OBJECT.IMG);
        } else if (!DataUtil.isNullOrEmpty(newob.getObjectImg()) && !DataUtil.isNullOrEmpty(old.getObjectImg())) {
            if (!old.getObjectImg().equals(newob.getObjectImg())) {
                saveActionDetail(actionId, old.getObjectImg(), newob.getObjectImg(), Constants.OBJECT.IMG);
            }
        }
        if (!old.getObjectIcon().equals(newob.getObjectIcon())) {
            saveActionDetail(actionId, old.getObjectIcon(), newob.getObjectIcon(), Constants.OBJECT.ICON);
        }
        if (DataUtil.isNullOrZero(old.getParentId()) && !DataUtil.isNullOrZero(newob.getParentId())) {
            saveActionDetail(actionId, null, newob.getParentId().toString(), Constants.OBJECT.PARENT);
        } else if (!DataUtil.isNullOrZero(old.getParentId()) && DataUtil.isNullOrZero(newob.getParentId())) {
            saveActionDetail(actionId, old.getParentId().toString(), null, Constants.OBJECT.PARENT);
        } else if (!DataUtil.isNullOrZero(newob.getParentId()) && !DataUtil.isNullOrZero(old.getParentId())) {
            if (!old.getParentId().toString().equals(newob.getParentId().toString())) {
                saveActionDetail(actionId, old.getParentId().toString(), newob.getParentId().toString(), Constants.OBJECT.PARENT);
            }
        }
        if (DataUtil.isNullOrEmpty(old.getObjectNameI18N()) && !DataUtil.isNullOrEmpty(newob.getObjectNameI18N())) {
            saveActionDetail(actionId, null, newob.getObjectNameI18N(), Constants.OBJECT.NAMEI18);
        } else if (!DataUtil.isNullOrEmpty(old.getObjectNameI18N()) && DataUtil.isNullOrEmpty(newob.getObjectNameI18N())) {
            saveActionDetail(actionId, old.getObjectNameI18N(), null, Constants.OBJECT.NAMEI18);
        } else if (!DataUtil.isNullOrEmpty(old.getObjectNameI18N()) && !DataUtil.isNullOrEmpty(newob.getObjectNameI18N())) {
            if (!old.getObjectNameI18N().equals(newob.getObjectNameI18N())) {
                saveActionDetail(actionId, old.getObjectNameI18N(), newob.getObjectNameI18N(), Constants.OBJECT.NAMEI18);
            }
        }
        if (DataUtil.isNullOrEmpty(old.getObjectUrl()) && !DataUtil.isNullOrEmpty(newob.getObjectUrl())) {
            saveActionDetail(actionId, null, newob.getObjectUrl(), Constants.OBJECT.URL);
        } else if (!DataUtil.isNullOrEmpty(old.getObjectUrl()) && DataUtil.isNullOrEmpty(newob.getObjectUrl())) {
            saveActionDetail(actionId, old.getObjectUrl(), null, Constants.OBJECT.URL);
        } else if (!DataUtil.isNullOrEmpty(old.getObjectUrl()) && !DataUtil.isNullOrEmpty(newob.getObjectUrl())) {
            if (!old.getObjectUrl().equals(newob.getObjectUrl())) {
                saveActionDetail(actionId, old.getObjectUrl(), newob.getObjectUrl(), Constants.OBJECT.URL);
            }
        }
        if (!old.getFunctionType().toString().equals(newob.getFunctionType().toString())) {
            saveActionDetail(actionId, old.getFunctionType().toString(), newob.getFunctionType().toString(), Constants.OBJECT.FUNCTION_TYPE);
        }
        if (!old.getOrd().toString().equals(newob.getOrd().toString())) {
            saveActionDetail(actionId, old.getOrd().toString(), newob.getOrd().toString(), Constants.OBJECT.ORD);
        }
        if (!old.getObjectType().equals(newob.getObjectType())) {
            saveActionDetail(actionId, old.getObjectType(), newob.getObjectType(), Constants.OBJECT.OBJECT_TYPE);
        }

    }

    /**
     * Lấy ra danh sách chức năng đang hoạt động
     *
     * @return List<ConfigObjects>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<ConfigObjects> getAllActive() throws Exception {
        return configObjectsRepo.findAllByStatusLike(1L);
    }

    /**
     * Lấy ra chức năng theo ID
     *
     * @param id
     * @return Optional<ConfigObjects>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public Optional<ConfigObjects> findAllById(Long id) throws Exception {
        return configObjectsRepo.findById(id);
    }

    /**
     * Lấy ra chức năng theo ID chức năng cha
     *
     * @param parentId
     * @return List<ConfigObjects>
     * @throws Exception
     * @author CuongDT
     * version 1.1
     * @since 18/11/2019
     */
    @Override
    public List<ConfigObjects> getAllByParentId(Long parentId) throws Exception {
        return configObjectsRepo.getAllByParentIdAndStatusNotLike(parentId, -1L);
    }

    @Override
    public String upload(MultipartFile multipartFile, String code, Long id) throws Exception {
        Optional<ConfigObjects> optional = configObjectsRepo.getById(id);
        ConfigObjects configObjects = new ConfigObjects();
        if (optional.isPresent()) {
            configObjects = optional.get();
        }else{
            configObjects.setObjectCode(code);
        }
        int vintIndex = multipartFile.getOriginalFilename().lastIndexOf(".");
        Date date = new Date();
        long millis = date.getTime();
        String vstrOriginalName = configObjects.getObjectCode() + millis + multipartFile.getOriginalFilename().substring(vintIndex, multipartFile.getOriginalFilename().length());
        Path paths = Paths.get(mstrUploadPath + "/" + vstrOriginalName);
        File file = new File(paths.toString());
        file.getParentFile().mkdirs();
        Files.copy(multipartFile.getInputStream(), paths);
        if (!DataUtil.isNullOrEmpty(configObjects.getObjectImg())) {
            Path vpaths = Paths.get(mstrUploadPath + "/" + configObjects.getObjectImg());
            try {
                if (Files.exists(vpaths)) {
                    {
                        Files.delete(vpaths);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return vstrOriginalName;
            }
        }
        return vstrOriginalName;
    }

    @Override
    public boolean checkSelectParent(Long parentid, Long idSelect) throws Exception {
        List<ConfigObjects> list = objectConfigRepoCustom.getAllChild(parentid);
        for (ConfigObjects configObjects : list) {
            if (configObjects.getId() == idSelect) {
                return true;
            }
        }
        return false;
    }


}
