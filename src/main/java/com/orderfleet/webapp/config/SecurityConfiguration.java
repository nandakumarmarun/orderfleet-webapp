package com.orderfleet.webapp.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.filter.CorsFilter;

import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.Http401UnauthorizedEntryPoint;
import com.orderfleet.webapp.security.jwt.JWTConfigurer;
import com.orderfleet.webapp.security.jwt.TokenProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {
		
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	private final UserDetailsService userDetailsService;

	public SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder,
			UserDetailsService userDetailsService) {
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.userDetailsService = userDetailsService;
	}
	
    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder
                .userDetailsService(userDetailsService);
                    //.passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
    
	@Configuration
	@Order(1)
	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

		@Autowired
		private TokenProvider tokenProvider;

		@Autowired
		private CorsFilter corsFilter;

		@Bean
		public Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint() {
			return new Http401UnauthorizedEntryPoint();
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
		}
    	
    	@Override
    	protected void configure(HttpSecurity http) throws Exception {
    		http
    			.antMatcher("/api/**")
    			.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(http401UnauthorizedEntryPoint())
    	    .and()
    	    	.csrf()
    	      	.disable()
    	      	.headers()
    	      	.frameOptions()
    	      	.disable()
    	    .and()
    	      	.sessionManagement()
    	      	.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    	    .and()     
    	      	.authorizeRequests()
    	      	.antMatchers("/api/tally/**").permitAll()
    	      	.antMatchers("/api/register").permitAll()
    	      	.antMatchers("/api/validate").permitAll()
    	      	.antMatchers("/api/authenticate").permitAll()
    	      	.antMatchers("/api/onpremise-users/*").permitAll()
    	      	.antMatchers("/api/account/reset_password/init").permitAll()
    	      	.antMatchers("/api/knowledgebase/{\\d+}/download").permitAll()
    	      	.antMatchers("/api/account/reset_password/finish").permitAll()
    	      	.antMatchers("/api/profile-info").permitAll()
    	      	.antMatchers("/api/account/registration_data").permitAll()
    	      	.antMatchers("/api/validate/user-onpremise").permitAll()
    	      	.antMatchers("/api/menu-items").permitAll()
    	      	.antMatchers("/api/ecom/**").hasAnyAuthority(AuthoritiesConstants.ECOM_REG, AuthoritiesConstants.ECOM_GUEST)
    	      	.antMatchers("/api/orderpro/integra/v1/**").hasAuthority(AuthoritiesConstants.PARTNER)
    	      	.antMatchers("/api/**").authenticated()
    	      	.antMatchers("/management/health").permitAll()
                .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/v2/api-docs/**").permitAll()
                .antMatchers("/swagger-resources/configuration/ui").permitAll()
                .antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/image/**").permitAll()
    	    .and()
    	      	.apply(securityConfigurerAdapter());
    	 }

		private JWTConfigurer securityConfigurerAdapter() {
			return new JWTConfigurer(tokenProvider);
		}
	}
	
	/*@Configuration
	@Order(2)
	public static class BasicAuthSecurityConfiguration extends WebSecurityConfigurerAdapter {

		@Bean
		public Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint() {
			return new Http401UnauthorizedEntryPoint();
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
	    	http
				.antMatcher("/basic-api/**")
	            .exceptionHandling()
	            .authenticationEntryPoint(http401UnauthorizedEntryPoint())
		    .and()
		    	.csrf()
		      	.disable()
		      	.headers()
		      	.frameOptions()
		      	.disable()
		    .and()
		      	.sessionManagement()
		      	.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    .and()     
		      	.authorizeRequests()
		      	.antMatchers("/basic-api/orderpro/integra/v1/test").permitAll()
	            .antMatchers("/basic-api/**").access("hasRole('ROLE_PARTNER') ")
	            .antMatchers("/basic-api/**").authenticated()
		    .and()
		      	.httpBasic();
		}
	}*/
    
    @Configuration
	public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    	
    	@Autowired
    	private OrderfleetProperties orderfleetProperties;

    	@Autowired
    	private RememberMeServices rememberMeServices;
    	
    	@Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
	            .antMatchers("/WEB-INF/jsp/**/*.{jsp}")
	            .antMatchers("/resources/**")
	            .antMatchers("/resources/assets/**")
	            .antMatchers(HttpMethod.OPTIONS, "/**")
	            .antMatchers("/app/**/*.{js,html}")
	            .antMatchers("/i18n/**")
	            .antMatchers("/content/**")
	            .antMatchers("/swagger-ui/index.html")
	            .antMatchers("/test/**");
                
        }
    	
    	@Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .csrf()
                //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            	//.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
            .and()
	            .rememberMe()
	            .rememberMeServices(rememberMeServices)
	            .rememberMeParameter("remember-me")
	            .key(orderfleetProperties.getSecurity().getRememberMe().getKey())
            .and()
    	        .formLogin()
    	        .loginPage("/login")
    	        .defaultSuccessUrl("/web/dashboard")
    	        .failureUrl("/login?error")
    	        .usernameParameter("username")
    	        .passwordParameter("password")
    	        .permitAll()
            .and()
                .logout()
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID", "CSRF-TOKEN")
                .permitAll()
            .and()
                .headers()
                .frameOptions()
                .disable()
            .and()
                .authorizeRequests()
                .antMatchers("/web/**").authenticated()
                .antMatchers("/websocket/tracker").permitAll()
                .antMatchers("/trial/setup").permitAll()
                .antMatchers("/trial/taskCompletionNotificationPage").permitAll()
                .antMatchers("/trial/set-up-trial").permitAll()
                .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.SITE_ADMIN)
                .antMatchers("/v2/api-docs/**").permitAll()
                .antMatchers("/swagger-resources/configuration/ui").permitAll()
                .antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.SITE_ADMIN)
                .and()
                .csrf().disable();
//            http
//        		.sessionManagement()
//        			.maximumSessions(1)
//        			.expiredUrl("/login?expired");
        }
    }
    
//    // Work around https://jira.spring.io/browse/SEC-2855
//    @Bean
//    public SessionRegistry sessionRegistry() {
//        SessionRegistry sessionRegistry = new SessionRegistryImpl();
//        return sessionRegistry;
//    }
//
//    // Register HttpSessionEventPublisher
//    @Bean
//    public static ServletListenerRegistrationBean<?> httpSessionEventPublisher() {
//        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
//    }
    
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
