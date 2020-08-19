package vn.vissoft.dashboard.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vissoft.dashboard.config.I18N;
import vn.vissoft.dashboard.dto.MenuDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.ApiResponse;
import vn.vissoft.dashboard.helper.UserInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("${api_base_path}/authen")
public class AuthenticateController {
    private static Logger LOGGER = LogManager.getLogger(AuthenticateController.class);
    @PostMapping("getUserInfo")
    public ApiResponse getUserInfo(Authentication authentication)
    {
        try{
            StaffDTO userToken = (StaffDTO) authentication.getPrincipal();
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(userToken.getVsaToken(),userInfo);
            return ApiResponse.build(HttpServletResponse.SC_OK,true,"",userInfo);
        }catch(Exception e)
        {
            LOGGER.error(e.getMessage(),e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,false,e.getMessage(),null);
        }

    }

    @PostMapping("getMenu")
    public ApiResponse getMenu(Authentication authentication)
    {
        try{
            StaffDTO userToken = (StaffDTO) authentication.getPrincipal();
            List<MenuDTO> menuDTOS = userToken.getAllObjects();
            if(menuDTOS!=null)
            {
                for(MenuDTO menuDTO : menuDTOS)
                {
                    menuDTO.setObjectName(I18N.get(menuDTO.getObjectNameI18N()));
                }
            }
            return ApiResponse.build(HttpServletResponse.SC_OK,true,"",userToken.getObjects());
        }catch(Exception e)
        {
            LOGGER.error(e.getMessage(),e);
            return ApiResponse.build(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,false,e.getMessage(),null);
        }
    }
//
//    private List<ObjectToken> buildMenuList( ArrayList<ObjectToken> objectTokens) {
//        List<ObjectToken> lstReturn = Lists.newArrayList();
//        List<String> lstAllObjectCodes = Lists.newArrayList();
//        if(objectTokens==null)
//        {
//            return Lists.newArrayList();
//        }
//        for(ObjectToken object : objectTokens)
//        {
//            if(lstAllObjectCodes.contains(object.getObjectCode())){
//                continue;
//            }else{
//                lstReturn.add(object);
//                lstAllObjectCodes.addAll(getAllObjectChildren(object));
//            }
//        }
//        return lstReturn;
//    }
//
//    private Collection<? extends String> getAllObjectChildren(ObjectToken object) {
//        List<String> lstAllObjectCodes = Lists.newArrayList();
//        if(object!=null&&object.getChildObjects()!=null)
//        {
//            for(ObjectToken childObject : object.getChildObjects()){
//                lstAllObjectCodes.add(childObject.getObjectCode());
//                lstAllObjectCodes.addAll(getAllObjectChildren(childObject));
//            }
//        }
//        return lstAllObjectCodes;
//    }
}
