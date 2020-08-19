package vn.vissoft.dashboard.services.impl;

import com.google.common.collect.Lists;
import com.sun.jmx.snmp.InetAddressAcl;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.security.x509.IPAddressName;
import viettel.passport.client.UserToken;
import vn.vissoft.dashboard.helper.vsa.VSAClient;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.MenuDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.model.*;
import vn.vissoft.dashboard.repo.*;
import vn.vissoft.dashboard.services.AccessLoginService;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.sql.Timestamp;

import java.util.*;
import java.util.List;


@Service(value = "authenService")
public class AuthenServiceImpl {

    private static final Logger logger = LogManager.getLogger(AuthenServiceImpl.class);

    @Value("${vsa.url}")
    private String vsaUrl;

    @Value("${vsa.domain}")
    private String vsaDomain;

    @Value("${server.tomcat.baseimg}")
    private String mstrUploadPath;
//    private VSATransport vsaTransport;

    @Autowired
    private StaffRepo staffRepo;

    @Autowired
    private ConfigObjectsRepo configObjectsRepo;
    @Autowired
    private ConfigRolesRepo configRolesRepo;
    @Autowired
    private ConfigRolesObjectsRepo configRolesObjectsRepo;
    @Autowired
    private ConfigRolesStaffRepo configRolesStaffRepo;
    @Autowired
    private ConfigObjectsDashboardRepo configObjectsDashboardRepo;
    @Autowired
    private AccessLoginService accessLoginService;

    public UserToken authen(String vsauser, String pass) {
//        VSAValidate vsaValidate = new VSAValidate();
//        vsaValidate.setUser(vsauser);
//        vsaValidate.setPassword(pass);
//        vsaValidate.setDomainCode(vsaDomain);
//
//        try {
//            this.logger.info("Authen : Check vsa username[" + vsauser + "] pass[***]");
//            if (this.vsaTransport == null) {
//                initVsaWs();
//            }
//            this.vsaTransport.validate(vsaValidate);
//        } catch (Exception ee) {
//            this.logger.error(ee.getMessage(), ee);
//            this.vsaTransport = null;
//        }
//
//        this.logger.info("complete check passport for : " + vsauser);
//        return vsaValidate;
        return VSAClient.authen(vsaUrl,vsaDomain,vsauser,pass);
    }

//    public void initVsaWs() throws Exception {
//        try {
//            this.vsaTransport = new VSATransport(vsaUrl);
//        } catch (Exception ex) {
//            this.vsaTransport = null;
//            this.logger.error(ex.getMessage(), ex);
//            throw ex;
//        }
//    }

    public StaffDTO loadStaffInfo(UserToken vsaToken, String userName) {
        try {
            StaffDTO staffDTO = staffRepo.findStaffByLoginUser(vsaToken);
            if (staffDTO != null) {
                List<ConfigObjects> grantObjects = Lists.newArrayList();
                List<ConfigObjects> menuObjects = Lists.newArrayList();
                List<ConfigObjects> allObjects = configObjectsRepo.findAllByStatusOrderByOrd(1L);
                List<ConfigRolesStaff> allRolesStaff = configRolesStaffRepo.findAll();
                List<ConfigRolesObjects> allRolesObject = configRolesObjectsRepo.findAll();
                List<ConfigObjectsDashboard> allObjectDashboard = configObjectsDashboardRepo.findAll();

                if (allRolesStaff != null) {

                    for (ConfigRolesStaff configRolesStaff : allRolesStaff) {
                        if (DataUtil.safeEqual(configRolesStaff.getStaffCode(), staffDTO.getStaffCode())
                                && DataUtil.safeEqual(configRolesStaff.getStatus(), Constants.STATUS.ACTIVE)) {
                            for (ConfigRolesObjects configRolesObjects : allRolesObject) {
                                if (DataUtil.safeEqual(configRolesObjects.getRoleId(), configRolesStaff.getRoleId())
                                        && DataUtil.safeEqual(configRolesObjects.getStatus(), Constants.STATUS.ACTIVE)) {
                                    if (DataUtil.safeEqual(configRolesObjects.getIsDefault(), Constants.COMMON.DEFAULT_OBJECT)) {
                                        staffDTO.setDefaultObjectId(configRolesObjects.getObjectId());
                                    }
                                    for (ConfigObjects configObjects : allObjects) {
                                        if (DataUtil.safeEqual(configObjects.getId(), configRolesObjects.getObjectId())) {
                                            if (!grantObjects.contains(configObjects)) {
                                                grantObjects.add(configObjects);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!grantObjects.isEmpty()) {
                        menuObjects.addAll(allObjects);
                        for (ConfigObjects configObjects : allObjects) {
                            if (Constants.OBJECT_TYPE.MODULE.equals(configObjects.getObjectType()) && !grantObjects.contains(configObjects)) {
                                menuObjects.remove(configObjects);
                            }
                        }
                        for (ConfigObjects configObjects : allObjects) {
                            if (Constants.OBJECT_TYPE.GROUP.equals(configObjects.getObjectType()) && checkGroupNonChild(configObjects, menuObjects)) {
                                menuObjects.remove(configObjects);
                            }
                        }
                    }

                }


                ArrayList<MenuDTO> lstAllMenu = new ArrayList();
                for (int i = 0; i < menuObjects.size(); i++) {
                    MenuDTO parentObj = buildMenuDTO(menuObjects.get(i));
                    if (allObjectDashboard != null && DataUtil.safeEqual(parentObj.getFunctionType(), Constants.FUNCTION_TYPE.DASHBOARD)) {
                        for (ConfigObjectsDashboard configObjectsDashboard : allObjectDashboard) {
                            if (DataUtil.safeEqual(configObjectsDashboard.getStatus(), Constants.STATUS.ACTIVE)
                                    && DataUtil.safeEqual(configObjectsDashboard.getObjectId(), parentObj.getId())) {
                                parentObj.setFunctionId(configObjectsDashboard.getGroupCardId());
                                parentObj.setObjectUrl(Constants.COMMON.URL_DASHBOARD + configObjectsDashboard.getGroupCardId());
                                break;
                            }
                        }
                    }

                    lstAllMenu.add(parentObj);
                }
                for (int i = 0; i < lstAllMenu.size(); i++) {
                    MenuDTO parentObj = lstAllMenu.get(i);
                    if (null == parentObj.getObjectUrl() || "".equals(parentObj.getObjectUrl())) {
                        parentObj.setObjectUrl("#");
                    }

                    if (DataUtil.safeEqual(parentObj.getObjectType(), Constants.OBJECT_TYPE.GROUP)) {
                        List childList = new ArrayList();
                        for (int j = 0; j < lstAllMenu.size(); j++) {
                            MenuDTO childObject = lstAllMenu.get(j);
                            if (DataUtil.safeEqual(childObject.getParentId(), parentObj.getId())) {
                                childList.add(childObject);
                            }
                        }

                        Collections.sort(childList);
                        parentObj.setChildObjects(childList);
                    }

                }
                ArrayList<MenuDTO> parentObject = findFirstLevelMenus(lstAllMenu);
                Collections.sort(parentObject);
                staffDTO.setObjects(parentObject);
                staffDTO.setAllObjects(lstAllMenu);

                long time = System.currentTimeMillis();
                Timestamp date = new Timestamp(time);
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//                String ipAddress = request.getRemoteAddr();
                String ipList = StringUtils.defaultIfEmpty(request.getHeader(TrackingContext.X_FORWARD_FOR.getHeaderKey()), request.getRemoteAddr());
                String browser = request.getHeader("user-agent") == null ? null : request.getHeader("user-agent");
                AccessLogin log = new AccessLogin(userName, ipList, date, browser);
                accessLoginService.saveLog(log);
            }
            return staffDTO;
        } catch (Exception ee) {
            this.logger.error(ee.getMessage(), ee);
        }
        return null;
    }

    private MenuDTO buildMenuDTO(ConfigObjects configObjects) throws IOException {
        String objectImg = null;
        if(!DataUtil.isNullOrEmpty(configObjects.getObjectImg())) {
            String filePath = mstrUploadPath + "/" + configObjects.getObjectImg();
            File file = new File(filePath);
            if(file.exists())
            {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                objectImg = Base64.getEncoder().encodeToString(bytes);
            }
        }
        MenuDTO menuDTO = new MenuDTO();
        if(DataUtil.isNullOrEmpty(configObjects.getObjectNameI18N())) {
            menuDTO.setObjectNameI18N(configObjects.getObjectName());
        }else{
            menuDTO.setObjectNameI18N(configObjects.getObjectNameI18N());
        }
        menuDTO.setObjectName(I18N.get(configObjects.getObjectName()));
        menuDTO.setObjectType(configObjects.getObjectType());
        menuDTO.setObjectUrl(configObjects.getObjectUrl());
        menuDTO.setId(configObjects.getId());
        menuDTO.setObjectCode(configObjects.getObjectCode());
        menuDTO.setObjectIcon(configObjects.getObjectIcon());
        menuDTO.setObjectImg(objectImg);
        menuDTO.setOrd(configObjects.getOrd());
        menuDTO.setParentId(configObjects.getParentId());
        menuDTO.setStatus(configObjects.getStatus());
        menuDTO.setFunctionType(configObjects.getFunctionType());
        return menuDTO;
    }

    private boolean checkGroupNonChild(ConfigObjects configObjects, List<ConfigObjects> menuObjects) {
        for (ConfigObjects configObjects1 : menuObjects) {
            if (DataUtil.safeEqual(configObjects1.getParentId(), configObjects.getId())
                    && DataUtil.safeEqual(configObjects1.getObjectType(), Constants.OBJECT_TYPE.MODULE)) {
                return false;
            } else if (DataUtil.safeEqual(configObjects1.getParentId(), configObjects.getId())
                    && DataUtil.safeEqual(configObjects1.getObjectType(), Constants.OBJECT_TYPE.GROUP)) {
                return checkGroupNonChild(configObjects1, menuObjects);
            }
        }
        return true;
    }


    private ArrayList<MenuDTO> findFirstLevelMenus(ArrayList<MenuDTO> listObjects) {
        ArrayList<MenuDTO> list = new ArrayList();
        for (int i = 0; i < listObjects.size(); i++) {
            MenuDTO item = listObjects.get(i);
            if (item.getParentId() == null) {
                list.add(item);
            }
        }

        return list;
    }

    enum TrackingContext {

        X_FORWARD_FOR("x-forwarded-for", "clientIP"),
        X_CORRELATION_ID("X-Correlation-ID", "correlationID");

        private final String headerKey;
        private final String threadKey;

        TrackingContext(String s, String correlationID) {
            this.headerKey = s;
            this.threadKey = correlationID;
        }

        public String getHeaderKey() {
            return headerKey;
        }

        public String getThreadKey() {
            return threadKey;
        }
    }

}
