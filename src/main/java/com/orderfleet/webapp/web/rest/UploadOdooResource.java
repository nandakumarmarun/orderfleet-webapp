package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.hibernate.service.spi.ServiceException;
import org.json.JSONException;
import org.json.JSONObject;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsOdoo;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdoo;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooAccountProfile;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooAuthentication;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooOutstandingInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooPriceLevel;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooProductProfile;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooStockLocation;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooTaxList;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooUnitOfMeasure;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooUser;
import com.orderfleet.webapp.web.vendor.odoo.service.AccountProfileOdooUploadService;
import com.orderfleet.webapp.web.vendor.odoo.service.OutstandingInvoiceOdooUploadService;
import com.orderfleet.webapp.web.vendor.odoo.service.ProductProfileOdooUploadService;
import com.orderfleet.webapp.web.vendor.odoo.service.TaxListOdooUploadService;
import com.orderfleet.webapp.web.vendor.odoo.service.UserOdooUploadService;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

/**
 * used to upload xls
 *
 * @author Sarath
 * @since Nov 16, 2016
 */

@Controller
@RequestMapping("/web")
public class UploadOdooResource {

	private static String CUSTOMER_API_URL = "http://nellaracorp.dyndns.org:11214/web/api/customers";

	private static String AUTHENTICATE_API_URL = "http://nellaracorp.dyndns.org:11214/web/session/authenticate";

	private static String PRODUCT_API_URL = "http://nellaracorp.dyndns.org:11214/web/api/products";

	private static String UNIT_OF_MEASURE_API_URL = "http://nellaracorp.dyndns.org:11214/web/api/uoms";

	private static String USER_API_URL = "http://nellaracorp.dyndns.org:11214/web/api/users";

	private static String STOCK_LOCATION_API_URL = "http://nellaracorp.dyndns.org:11214/web/api/locations";
	
	private static String PRICE_LEVEL_API_URL = "http://nellaracorp.dyndns.org:11214/web/api/pricelists";
	
	private static String OUTSTANDING_INVOICE_API_URL = "http://nellaracorp.dyndns.org:11214/web/api/outstanding";
	
	private static String TAX_LIST_API_URL = "http://nellaracorp.dyndns.org:11214/web/api/taxes";

	private final Logger log = LoggerFactory.getLogger(UploadOdooResource.class);
	
	@Inject
	private OutstandingInvoiceOdooUploadService outstandingInvoiceOdooUploadService;

	@Inject
	private AccountProfileOdooUploadService accountProfileOdooUploadService;

	@Inject
	private ProductProfileOdooUploadService productProfileOdooUploadService;
	
	@Inject
	private TaxListOdooUploadService taxListOdooUploadService;

	@Inject
	private UserOdooUploadService userOdooUploadService;

	@RequestMapping(value = "/upload-odoo", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Upload Odoo Masters");
		return "company/uploadOdoo";
	}
	
	@RequestMapping(value = "/upload-odoo/uploadTaxList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadTaxList() throws IOException, JSONException, ParseException {

		// ResponseBodyOdooAuthentication responseBodyOdooAuthentication =
		// authenticateServer();

		log.debug("Web request to upload Tax List ...");

		String jsonString = getOdooRequestBody();

		HttpEntity<String> entity = new HttpEntity<>(jsonString, RestClientUtil.createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + TAX_LIST_API_URL);

		try {
			ResponseBodyOdooTaxList responseBodyTaxList = restTemplate.postForObject(TAX_LIST_API_URL,
					entity, ResponseBodyOdooTaxList.class);
			log.info("Tax List Size= " + responseBodyTaxList.getResult().getResponse().size()
					+ "------------");

			taxListOdooUploadService
					.saveTaxList(responseBodyTaxList.getResult().getResponse());

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				log.info(exception.getMessage());
				throw new ServiceException(exception.getResponseBodyAsString());
			}
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/upload-odoo/uploadOutstandingInvoices", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadOutstandingInvoices() throws IOException, JSONException, ParseException {

		// ResponseBodyOdooAuthentication responseBodyOdooAuthentication =
		// authenticateServer();

		log.debug("Web request to upload Outstanding Invoices ...");

		String jsonString = getOdooRequestBody();

		HttpEntity<String> entity = new HttpEntity<>(jsonString, RestClientUtil.createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + OUTSTANDING_INVOICE_API_URL);

		try {
			ResponseBodyOdooOutstandingInvoice responseBodyOutstandingInvoice = restTemplate.postForObject(OUTSTANDING_INVOICE_API_URL,
					entity, ResponseBodyOdooOutstandingInvoice.class);
			log.info("Outstanding Invoices Size= " + responseBodyOutstandingInvoice.getResult().getResponse().size()
					+ "------------");

			outstandingInvoiceOdooUploadService
					.saveOutstandingInvoice(responseBodyOutstandingInvoice.getResult().getResponse());

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				log.info(exception.getMessage());
				throw new ServiceException(exception.getResponseBodyAsString());
			}
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}


	@RequestMapping(value = "/upload-odoo/uploadAccountProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadAccountProfiles() throws IOException, JSONException, ParseException {

		// ResponseBodyOdooAuthentication responseBodyOdooAuthentication =
		// authenticateServer();

		log.debug("Web request to upload Account Profiles ...");

		String jsonString = getOdooRequestBody();

		HttpEntity<String> entity = new HttpEntity<>(jsonString, RestClientUtil.createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + CUSTOMER_API_URL);

		try {

			ResponseBodyOdooAccountProfile responseBodyAccountProfile = restTemplate.postForObject(CUSTOMER_API_URL,
					entity, ResponseBodyOdooAccountProfile.class);
			log.info("Account Profile Size= " + responseBodyAccountProfile.getResult().getResponse().size()
					+ "------------");

			accountProfileOdooUploadService
					.saveUpdateAccountProfiles(responseBodyAccountProfile.getResult().getResponse());

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				log.info(exception.getMessage());
				throw new ServiceException(exception.getResponseBodyAsString());
			}
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-odoo/uploadProductProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadProductProfiles() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload Product Profiles ...");

		String jsonString = getOdooRequestBody();

		HttpEntity<String> entity = new HttpEntity<>(jsonString, RestClientUtil.createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		// RequestBodyOdoo requestBody = getRequestBody("get_products");

		// HttpEntity<RequestBodyOdoo> entity = new HttpEntity<>(requestBody,
		// RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + PRODUCT_API_URL);

		try {

			ResponseBodyOdooUnitOfMeasure responseBodyUnitOfMeasure = restTemplate
					.postForObject(UNIT_OF_MEASURE_API_URL, entity, ResponseBodyOdooUnitOfMeasure.class);
			log.info("UOM Size= " + responseBodyUnitOfMeasure.getResult().getResponse().size() + "------------");

			productProfileOdooUploadService
					.saveUpdateUnitOfMeasure(responseBodyUnitOfMeasure.getResult().getResponse());

			ResponseBodyOdooProductProfile responseBodyProductProfile = restTemplate.postForObject(PRODUCT_API_URL,
					entity, ResponseBodyOdooProductProfile.class);
			log.info("Product Profile Size= " + responseBodyProductProfile.getResult().getResponse().size()
					+ "------------");

			productProfileOdooUploadService
					.saveUpdateProductProfiles(responseBodyProductProfile.getResult().getResponse());

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

	@RequestMapping(value = "/upload-odoo/uploadStockLocations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadStockLocations() throws IOException, JSONException, ParseException {

		// ResponseBodyOdooAuthentication responseBodyOdooAuthentication =
		// authenticateServer();

		log.debug("Web request to upload Stock Locations ...");

		String jsonString = getOdooRequestBody();

		HttpEntity<String> entity = new HttpEntity<>(jsonString, RestClientUtil.createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + STOCK_LOCATION_API_URL);

		try {

			ResponseBodyOdooStockLocation responseBodyStockLocation = restTemplate.postForObject(STOCK_LOCATION_API_URL,
					entity, ResponseBodyOdooStockLocation.class);
			log.info("Stock Location Size= " + responseBodyStockLocation.getResult().getResponse().size()
					+ "------------");

			productProfileOdooUploadService
					.saveUpdateStockLocations(responseBodyStockLocation.getResult().getResponse());

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				log.info(exception.getMessage());
				throw new ServiceException(exception.getResponseBodyAsString());
			}
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-odoo/uploadUsers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadUsers() throws IOException, JSONException, ParseException {

		// ResponseBodyOdooAuthentication responseBodyOdooAuthentication =
		// authenticateServer();

		log.debug("Web request to upload Users ...");

		String jsonString = getOdooRequestBody();

		HttpEntity<String> entity = new HttpEntity<>(jsonString, RestClientUtil.createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + USER_API_URL);

		try {

			ResponseBodyOdooUser responseBodyUser = restTemplate.postForObject(USER_API_URL, entity,
					ResponseBodyOdooUser.class);
			log.info("User Size= " + responseBodyUser.getResult().getResponse().size() + "------------");

			userOdooUploadService.saveUpdateUsers(responseBodyUser.getResult().getResponse());

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				log.info(exception.getMessage());
				throw new ServiceException(exception.getResponseBodyAsString());
			}
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/upload-odoo/uploadPriceLevel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadPriceLevel() throws IOException, JSONException, ParseException {

		// ResponseBodyOdooAuthentication responseBodyOdooAuthentication =
		// authenticateServer();

		log.debug("Web request to upload Price Level ...");

		String jsonString = getOdooRequestBody();

		HttpEntity<String> entity = new HttpEntity<>(jsonString, RestClientUtil.createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + PRICE_LEVEL_API_URL);

		try {

			ResponseBodyOdooPriceLevel responseBodyPriceLevel = restTemplate.postForObject(PRICE_LEVEL_API_URL, entity,
					ResponseBodyOdooPriceLevel.class);
			log.info("Price Level Size= " + responseBodyPriceLevel.getResult().getResponse().size() + "------------");

			productProfileOdooUploadService.saveUpdatePriceList(responseBodyPriceLevel.getResult().getResponse());

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				log.info(exception.getMessage());
				throw new ServiceException(exception.getResponseBodyAsString());
			}
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private RequestBodyOdoo getRequestBody() throws IOException {

		ObjectMapper Obj = new ObjectMapper();

		RequestBodyOdoo requestBody = new RequestBodyOdoo();

		requestBody.setJsonrpc("2.0");
		ParamsOdoo params = new ParamsOdoo();

		params.setDb("edappal");
		params.setLogin("api_user_1");
		params.setPassword("api_user_1");

		requestBody.setParams(params);
		String jsonStr = Obj.writeValueAsString(requestBody);

		log.info("Json String-------------" + jsonStr);
		return requestBody;
	}

	private String getOdooRequestBody() throws JSONException, ParseException {
		JSONParser parser = new JSONParser();
		String s = "{}";
		Object params = parser.parse(s);

		JSONObject emptyJsonObject = new JSONObject();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("jsonrpc", "2.0");
		jsonObject.put("params", emptyJsonObject);

		return jsonObject.toString();
	}

	private ResponseBodyOdooAuthentication authenticateServer() throws IOException {

		RequestBodyOdoo request = getRequestBody();

		HttpEntity<RequestBodyOdoo> entity = new HttpEntity<>(request, RestClientUtil.createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + AUTHENTICATE_API_URL);

		try {

			ResponseBodyOdooAuthentication responseBodyAuthentication = restTemplate.postForObject(AUTHENTICATE_API_URL,
					entity, ResponseBodyOdooAuthentication.class);

			log.info("Authenitcatiuon Response---" + responseBodyAuthentication.toString());

			return responseBodyAuthentication;

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				log.info(exception.getMessage());
				throw new ServiceException(exception.getResponseBodyAsString());
			}
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {
			log.info(exception.getMessage());
			throw new ServiceException(exception.getMessage());
		}

	}

}
