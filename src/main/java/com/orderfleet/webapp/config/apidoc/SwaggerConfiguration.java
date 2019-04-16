package com.orderfleet.webapp.config.apidoc;

import static springfox.documentation.builders.PathSelectors.regex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;

import com.fasterxml.classmate.TypeResolver;

import com.orderfleet.webapp.config.OrderfleetConstants;
import com.orderfleet.webapp.config.OrderfleetProperties;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Springfox Swagger configuration.
 *
 * Warning! When having a lot of REST endpoints, Springfox can become a
 * performance issue. In that case, you can use a specific Spring profile for
 * this class, so that only front-end developers have access to the Swagger
 * view.
 */
@Configuration
@ConditionalOnClass({ ApiInfo.class, BeanValidatorPluginsConfiguration.class })
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
@Profile(OrderfleetConstants.SPRING_PROFILE_SWAGGER)
public class SwaggerConfiguration {

	private final Logger log = LoggerFactory.getLogger(SwaggerConfiguration.class);

	/**
	 * Swagger Springfox configuration.
	 *
	 * @param orderfleetProperties
	 *            the properties of the application
	 * @return the Swagger Springfox configuration
	 */
	@Bean
	public Docket swaggerSpringfoxDocket(OrderfleetProperties orderfleetProperties) {
		log.debug("Starting Swagger");
		StopWatch watch = new StopWatch();
		watch.start();
		Contact contact = new Contact(orderfleetProperties.getSwagger().getContactName(),
				orderfleetProperties.getSwagger().getContactUrl(), orderfleetProperties.getSwagger().getContactEmail());

		ApiInfo apiInfo = new ApiInfo(orderfleetProperties.getSwagger().getTitle(),
				orderfleetProperties.getSwagger().getDescription(), orderfleetProperties.getSwagger().getVersion(),
				orderfleetProperties.getSwagger().getTermsOfServiceUrl(), contact,
				orderfleetProperties.getSwagger().getLicense(), orderfleetProperties.getSwagger().getLicenseUrl());

		Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo).forCodeGeneration(true)
				.directModelSubstitute(java.nio.ByteBuffer.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class).select()
				.paths(regex(orderfleetProperties.getSwagger().getDefaultIncludePattern())).build();
		watch.stop();
		log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
		return docket;
	}

	@Bean
	PageableParameterBuilderPlugin pageableParameterBuilderPlugin(TypeNameExtractor nameExtractor,
			TypeResolver resolver) {

		return new PageableParameterBuilderPlugin(nameExtractor, resolver);
	}
}