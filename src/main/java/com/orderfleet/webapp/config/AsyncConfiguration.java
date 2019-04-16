package com.orderfleet.webapp.config;

import com.orderfleet.webapp.async.ExceptionHandlingAsyncTaskExecutor;
import com.orderfleet.webapp.config.OrderfleetProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration implements AsyncConfigurer {

    private final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

    private final OrderfleetProperties orderfleetProperties;

    public AsyncConfiguration(OrderfleetProperties orderfleetProperties) {
        this.orderfleetProperties = orderfleetProperties;
    }

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        log.debug("Creating Async Task Executor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(orderfleetProperties.getAsync().getCorePoolSize());
        executor.setMaxPoolSize(orderfleetProperties.getAsync().getMaxPoolSize());
        executor.setQueueCapacity(orderfleetProperties.getAsync().getQueueCapacity());
        executor.setThreadNamePrefix("orderfleet-application-Executor-");
        return new ExceptionHandlingAsyncTaskExecutor(executor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
