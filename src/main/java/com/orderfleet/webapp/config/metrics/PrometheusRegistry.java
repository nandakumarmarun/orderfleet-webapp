package com.orderfleet.webapp.config.metrics;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricRegistry;

import com.orderfleet.webapp.config.OrderfleetProperties;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;

@Configuration
@ConditionalOnClass(CollectorRegistry.class)
public class PrometheusRegistry implements ServletContextInitializer {

    private final Logger log = LoggerFactory.getLogger(PrometheusRegistry.class);

    private final MetricRegistry metricRegistry;

    private final OrderfleetProperties orderfleetProperties;

    public PrometheusRegistry(MetricRegistry metricRegistry, OrderfleetProperties orderfleetProperties) {
        this.metricRegistry = metricRegistry;
        this.orderfleetProperties = orderfleetProperties;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (orderfleetProperties.getMetrics().getPrometheus().isEnabled()) {
            String endpoint = orderfleetProperties.getMetrics().getPrometheus().getEndpoint();
            log.info("Initializing Metrics Prometheus endpoint at {}", endpoint);
            CollectorRegistry collectorRegistry = new CollectorRegistry();
            collectorRegistry.register(new DropwizardExports(metricRegistry));
            servletContext
                .addServlet("prometheusMetrics", new MetricsServlet(collectorRegistry))
                .addMapping(endpoint);
        }
    }
}