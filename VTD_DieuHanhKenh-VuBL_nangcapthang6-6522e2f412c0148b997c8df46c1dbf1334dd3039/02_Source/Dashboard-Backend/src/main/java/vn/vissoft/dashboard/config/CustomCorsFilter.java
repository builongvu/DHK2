package vn.vissoft.dashboard.config;


import org.apache.logging.log4j.core.config.Order;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.vissoft.dashboard.dto.MenuDTO;
import vn.vissoft.dashboard.dto.StaffDTO;
import vn.vissoft.dashboard.helper.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter implements Filter {

    public CustomCorsFilter() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String urlRequest = request.getHeader("url");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof StaffDTO) {
            StaffDTO user = (StaffDTO) authentication.getPrincipal();
            List<MenuDTO> listMenu = user.getAllObjects();
            List<String> listUrl = new ArrayList<>();
            for (MenuDTO menu : listMenu) {
                listUrl.add(menu.getObjectUrl());
            }
            listUrl.add(Constants.LOGIN_URL);

            if (!listUrl.contains(urlRequest)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, PATCH, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }


}
