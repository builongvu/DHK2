package vn.vissoft.dashboard.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import viettel.passport.client.UserToken;
import viettel.passport.client.VSAValidate;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.services.impl.AuthenServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private AuthenServiceImpl authenService;

    public CustomAuthenticationProvider(AuthenServiceImpl authenService) {
        this.authenService = authenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            // logic xac thuc user
            UsernamePasswordAuthenticationToken result;
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();
//            VSAValidate vsaValidate = authenService.authen(username, password);
            UserToken userToken = authenService.authen(username,password);
            if (userToken!=null) {
                List<GrantedAuthority> lst = new ArrayList<>();
                StaffDTO staffDTO = authenService.loadStaffInfo(userToken, username);
                if (staffDTO == null) {
                    throw new UsernameNotFoundException("staff not exist");
                }

                userToken.getRolesList().forEach(item -> lst.add(new SimpleGrantedAuthority(item.getRoleCode())));
                result = new UsernamePasswordAuthenticationToken(staffDTO, password, lst);
                return result;
            }
            throw new UsernameNotFoundException("authentication failed");
        } catch (UsernameNotFoundException e) {
            throw e;
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.equals(UsernamePasswordAuthenticationToken.class);
    }

    public AuthenServiceImpl getAuthenService() {
        return authenService;
    }

    public void setAuthenService(AuthenServiceImpl authenService) {
        this.authenService = authenService;
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("dashboard-client"));
    }
}
