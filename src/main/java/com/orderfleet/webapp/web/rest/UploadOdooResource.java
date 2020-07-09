package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.excel.service.AccountProfileUploadService;
import com.orderfleet.webapp.web.vendor.odoo.dto.ArgsOdoo;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsOdoo;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdoo;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooAccountProfile;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooProductProfile;
import com.orderfleet.webapp.web.vendor.odoo.service.AccountProfileOdooUploadService;
import com.orderfleet.webapp.web.vendor.odoo.service.ProductProfileOdooUploadService;

/**
 * used to upload xls
 *
 * @author Sarath
 * @since Nov 16, 2016
 */

@Controller
@RequestMapping("/web")
public class UploadOdooResource {

	private static String API_URL = "http://117.221.64.88:1214/jsonrpc";

	private final Logger log = LoggerFactory.getLogger(UploadOdooResource.class);

	@Inject
	private AccountProfileOdooUploadService accountProfileOdooUploadService;

	@Inject
	private ProductProfileOdooUploadService productProfileOdooUploadService;

	@RequestMapping(value = "/upload-odoo", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Upload Odoo Masters");
		return "company/uploadOdoo";
	}

	@RequestMapping(value = "/upload-odoo/uploadAccountProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadAccountProfiles() throws IOException {

		log.debug("Web request to upload Account Profiles ...");

		RequestBodyOdoo requestBody = getRequestBody("get_customers");

		HttpEntity<RequestBodyOdoo> entity = new HttpEntity<>(requestBody, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + API_URL);

		try {

			ResponseBodyOdooAccountProfile responseBodyAccountProfile = restTemplate.postForObject(API_URL, entity,
					ResponseBodyOdooAccountProfile.class);
			log.info("Account Profile Size= " + responseBodyAccountProfile.getResult().size() + "------------");

			accountProfileOdooUploadService.saveUpdateAccountProfiles(responseBodyAccountProfile.getResult());

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				throw new ServiceException(exception.getResponseBodyAsString());
			}
			throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {
			throw new ServiceException(exception.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-odoo/uploadProductProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadProductProfiles() throws IOException {

		log.debug("Web request to upload Product Profiles ...");

		RequestBodyOdoo requestBody = getRequestBody("get_products");

		HttpEntity<RequestBodyOdoo> entity = new HttpEntity<>(requestBody, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + API_URL);

		try {

			ResponseBodyOdooProductProfile responseBodyProductProfile = restTemplate.postForObject(API_URL, entity,
					ResponseBodyOdooProductProfile.class);
			log.info("Product Profile Size= " + responseBodyProductProfile.getResult().size() + "------------");

			productProfileOdooUploadService.saveUpdateProductProfiles(responseBodyProductProfile.getResult());

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				throw new ServiceException(exception.getResponseBodyAsString());
			}
			throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());

			throw new ServiceException(exception.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private RequestBodyOdoo getRequestBody(String getmaster) throws IOException {
		RequestBodyOdoo requestBody = new RequestBodyOdoo();

		ArgsOdoo args = new ArgsOdoo();
		ObjectMapper Obj = new ObjectMapper();

		requestBody.setMethod("call");
		requestBody.setJsonrpc("2.0");
		ParamsOdoo params = new ParamsOdoo();
		params.setService("object");
		params.setMethod("execute");
		List<Object> argsList = new ArrayList<>();

		argsList.add("edappal");
		argsList.add(1);
		argsList.add("admin123.");
		argsList.add("custom.api");

		if (getmaster.equalsIgnoreCase("get_customers")) {
			argsList.add("get_customers");
		}
		if (getmaster.equalsIgnoreCase("get_products")) {
			argsList.add("get_products");
		}
		args.setCompanyId(1);
		argsList.add(args);
		params.setArgs(argsList);
		requestBody.setParams(params);
		String jsonStr = Obj.writeValueAsString(requestBody);

		log.info("Json String-------------" + jsonStr);
		return requestBody;
	}

}
