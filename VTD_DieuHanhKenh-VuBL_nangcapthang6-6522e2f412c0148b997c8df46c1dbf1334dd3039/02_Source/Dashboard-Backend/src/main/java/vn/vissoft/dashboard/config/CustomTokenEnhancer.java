package vn.vissoft.dashboard.config;

import org.springframework.beans.BeanUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import viettel.passport.client.UserToken;
import vn.vissoft.dashboard.dto.MenuDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.DataUtil;
import vn.vissoft.dashboard.helper.UserInfo;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        // TODO Auto-generated method stub
        StaffDTO user = (StaffDTO) authentication.getPrincipal();
        final Map<String, Object> additionalInfo = new HashMap<>();
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user.getVsaToken(), userInfo);
        userInfo.setShopCode(user.getShopCode());
        userInfo.setVdsChannelCode(user.getVdsChannelCode());
        if (user.getDefaultObjectId() != null && user.getAllObjects() != null) {
            for (MenuDTO menuDTO : user.getAllObjects()) {
                if (DataUtil.safeEqual(menuDTO.getId(), user.getDefaultObjectId())) {
                    userInfo.setDefaultLink(menuDTO.getObjectUrl());
                    break;
                }
            }
        }


        additionalInfo.put("userInfo", userInfo);
        additionalInfo.put("authorities", user.getVsaToken().getRolesList());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }

}
