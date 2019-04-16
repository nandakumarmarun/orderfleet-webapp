package com.orderfleet.webapp.config;

import com.orderfleet.webapp.config.OrderfleetProperties;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.jsr107.Eh107Configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = { MetricsConfiguration.class })
@AutoConfigureBefore(value = { WebConfigurer.class, DatabaseConfiguration.class })
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(OrderfleetProperties orderfleetProperties) {
    	OrderfleetProperties.Cache.Ehcache ehcache =
    			orderfleetProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(Expirations.timeToLiveExpiration(Duration.of(ehcache.getTimeToLiveSeconds(), TimeUnit.SECONDS)))
                .build());
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
        	cm.createCache(com.orderfleet.webapp.domain.Company.class.getName(), jcacheConfiguration);
        	cm.createCache(com.orderfleet.webapp.domain.Country.class.getName(), jcacheConfiguration);
        	cm.createCache(com.orderfleet.webapp.domain.State.class.getName(), jcacheConfiguration);
        	cm.createCache(com.orderfleet.webapp.domain.District.class.getName(), jcacheConfiguration);
            cm.createCache(com.orderfleet.webapp.domain.User.class.getName(), jcacheConfiguration);
            cm.createCache(com.orderfleet.webapp.domain.Authority.class.getName(), jcacheConfiguration);
            cm.createCache(com.orderfleet.webapp.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            cm.createCache(com.orderfleet.webapp.domain.PersistentToken.class.getName(), jcacheConfiguration);
            cm.createCache(com.orderfleet.webapp.domain.User.class.getName() + ".persistentTokens", jcacheConfiguration);
            cm.createCache(com.orderfleet.webapp.domain.MenuItem.class.getName(), jcacheConfiguration);
            cm.createCache(com.orderfleet.webapp.domain.IntegrationModule.class.getName(), jcacheConfiguration);
            // orderfleet-needle-ehcache-add-entry
        };
    }
}