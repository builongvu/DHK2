package vn.vissoft.dashboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Value("${security.oath2.resource-id}")
	private String RESOURCE_ID;
	@Value("${api_base_path}")
	private String baseApiPath;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID).stateless(false);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
        http.
                anonymous().disable()
                .authorizeRequests()
                .antMatchers("*").authenticated()
//                .antMatchers("/dashboard/**").authenticated()
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
	}


	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new RestAuthenticationFailureHandler();
	}

	@Bean
	RestAccessDeniedHandler accessDeniedHandler() {
		return new RestAccessDeniedHandler();
	}

	@Bean
	RestAuthenticationEntryPoint authenticationEntryPoint() {
		return new RestAuthenticationEntryPoint();
	}

//	@Override
//	public void configure(HttpSecurity http) throws Exception {
//		http.anonymous().disable()
//				.authorizeRequests()
//				.antMatchers(baseApiPath+"/**").authenticated()
//				.and()
//				.exceptionHandling().accessDeniedHandler(accessDeniedHandler())
//				.authenticationEntryPoint(authenticationEntryPoint());
//	}
}
