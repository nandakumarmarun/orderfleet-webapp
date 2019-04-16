package com.orderfleet.webapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
//		registry.addViewController("/").setViewName("index");
		
		//new theme indexpages
		registry.addViewController("/").setViewName("new-theme/index");
		registry.addViewController("/index").setViewName("new-theme/index");
		registry.addViewController("/marketpro").setViewName("new-theme/marketpro");
		registry.addViewController("/contactUs").setViewName("new-theme/contact");
		registry.addViewController("/orderpro").setViewName("new-theme/orderpro");
		registry.addViewController("/pharmaplus").setViewName("new-theme/pharmaplus");
		registry.addViewController("/vansales").setViewName("new-theme/vansales");
		
		registry.addViewController("/benefits").setViewName("benefits");
		registry.addViewController("/contact").setViewName("contact");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/error").setViewName("error");
		registry.addViewController("/web/management/websocket/tracker").setViewName("site_admin/tracker");
		registry.addViewController("/web/management/metrics").setViewName("site_admin/metrics");
		registry.addViewController("/web/management/health").setViewName("site_admin/health");
	}
}

