package com.orderfleet.webapp.config;

import com.orderfleet.webapp.aop.logging.LoggingAspect;

import com.orderfleet.webapp.config.OrderfleetConstants;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

    @Bean
    @Profile(OrderfleetConstants.SPRING_PROFILE_DEVELOPMENT)
    public LoggingAspect loggingAspect(Environment env) {
        return new LoggingAspect(env);
    }
}