package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.apache.poi.ss.formula.functions.T;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.jwt.JWTConfigurer;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdoo;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.ResponseBodySapPraveshAccountProfile;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.ResponseBodySapPraveshAccountProfileOpeningBalance;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.ResponseBodySapPraveshOutstanding;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.ResponseBodySapPraveshProductProfile;
import com.orderfleet.webapp.web.vendor.sap.pravesh.service.AccountProfileSapPraveshUploadService;
import com.orderfleet.webapp.web.vendor.sap.pravesh.service.OutstandingSapPraveshUploadService;
import com.orderfleet.webapp.web.vendor.sap.pravesh.service.ProductProfileSapPraveshUploadService;

/**
 * used to upload xls
 *
 * @author Sarath
 * @since Nov 16, 2016
 */

@Controller
@RequestMapping("/web")
public class SapUploadResource {

	private static String API_URL_ACCOUNT_PROFILE = "http://59.94.176.87:5002/Customer/GetAllCustomers";

	private static String API_URL_PRODUCT_PROFILE = "http://59.94.176.87:5002/Products/GetAllProducts";
	
	private static String API_URL_OUTSTANDING = "http://59.94.176.87:5002/Order/Outstanding";

	private static String AUTHENTICATION_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEzRjhFREM2QjJCNTU3OUQ0MEVGNDg1QkNBOUNFRDBBIiwidHlwIjoiYXQrand0In0.eyJuYmYiOjE2MDExMDg1MzMsImV4cCI6MTYzMjY0NDUzMywiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo1MDAwIiwiYXVkIjoiQ3VzdG9tZXJTZXJ2aWNlLkFwaSIsImNsaWVudF9pZCI6Im5hc19jbGllbnQiLCJzdWIiOiIwNWEwNzliMC1jZjBiLTQ2ZjctYThlMy1iODk4MjIwODgzMjQiLCJhdXRoX3RpbWUiOjE2MDExMDg1MzMsImlkcCI6ImxvY2FsIiwic2VydmljZS51c2VyIjoiYWRtaW4iLCJqdGkiOiIyOUU0OTRERTg1QjA0RTdBNUM1NjM3NDhCQzIyOTEyRSIsInNpZCI6IkFDOTE4QzNEMkY3MUIzRTRBMERGQzc2MDQ4QzJBMEUzIiwiaWF0IjoxNjAxMTA4NTMzLCJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwibmFzLmNsaWVudCIsIm5hcy5zZXJ2aWNlcyJdLCJhbXIiOlsicHdkIl19.x4knTyLtPEwUSnc35EnWSyxINwOLU5YTBeCD_eXDkXmMC1bWQclkdLH18Dgict07qVWyRL9EcYT66j4p7hsUGbZrZP9TLeNpQP5BT6eRSeYvkf2lmvJe1xaCvPYrHpPGvApLJJAmQxCwex7AAW74zJLpl_SdNUf3AHBkBvjr2ibEkDBgRgOTO0Z3n3f43ZxZw3LAi_x8ZRSxITY0mpevTUpDhx2pv5-ehXe7BaCbTxAJ6dBvkAavtmB-W3wp7cnJqSfr2mFpsBzE_Ek_OzAFnu_N1ALi8yE9LpuAPSDj4hVz11i98urPebHA8lEca1yBAPI6goQlKJEB4_NXI5F8CA";

	// private static String API_URL_ACCOUNT_PROFILE_OPENING_BALANCE =
	// "http://59.94.176.87:81/Service1.svc/GetAllCustomers?dbkey=1&code=20";

//	private static String API_URL_ACCOUNT_PROFILE = "http://117.247.186.223:81/Service1.svc/GetAllCustomersNameAndId?Scode=20&dbkey=1";
//
//	private static String API_URL_ACCOUNT_PROFILE_OPENING_BALANCE = "http://117.247.186.223:81/Service1.svc/GetAllCustomers?dbkey=1&code=20";
//
//	private static String API_URL_PRODUCT_PROFILE = "http://117.247.186.223:81/Service1.svc/GetAllItemsNameAndId?dbkey=1";

	private final Logger log = LoggerFactory.getLogger(SapUploadResource.class);

	@Inject
	private AccountProfileSapPraveshUploadService accountProfileSapUploadService;

	@Inject
	private ProductProfileSapPraveshUploadService productProfileSapUploadService;

	@Inject
	private OutstandingSapPraveshUploadService outstandingSapPraveshUploadService;

	@RequestMapping(value = "/sap-upload", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Upload Sap Masters");
		return "company/sapUpload";
	}

	@RequestMapping(value = "/sap-upload/uploadAccountProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadAccountProfiles() throws IOException {

		log.debug("Web request to upload Account Profiles ...");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get Account Profile  URL: " + API_URL_ACCOUNT_PROFILE);

		try {

			HttpEntity<T> entity = new HttpEntity<>(createTokenAuthHeaders());

			ResponseEntity<List<ResponseBodySapPraveshAccountProfile>> responseBodyAccountProfiles = restTemplate
					.exchange(API_URL_ACCOUNT_PROFILE, HttpMethod.GET, entity,
							new ParameterizedTypeReference<List<ResponseBodySapPraveshAccountProfile>>() {
							});

			log.info("Account Profile Size= " + responseBodyAccountProfiles.getBody().size() + "------------");

			accountProfileSapUploadService.saveUpdateAccountProfiles(responseBodyAccountProfiles.getBody());

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

	public static MultiValueMap<String, String> createTokenAuthHeaders() {
		MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<String, String>();
		requestHeaders.add("Authorization", "Bearer " + AUTHENTICATION_TOKEN);
		requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		return requestHeaders;
	}

	@RequestMapping(value = "/sap-upload/uploadProductProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadProductProfiles() throws IOException {

		log.debug("Web request to upload Product Profiles ...");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get Product Profile URL: " + API_URL_PRODUCT_PROFILE);

		try {
			
			HttpEntity<T> entity = new HttpEntity<>(createTokenAuthHeaders());

			ResponseEntity<List<ResponseBodySapPraveshProductProfile>> responseBodyProductProfiles = restTemplate
					.exchange(API_URL_PRODUCT_PROFILE, HttpMethod.GET, entity,
							new ParameterizedTypeReference<List<ResponseBodySapPraveshProductProfile>>() {
							});
			log.info("Product Profile Size= " + responseBodyProductProfiles.getBody().size() + "------------");

			productProfileSapUploadService.saveUpdateProductProfiles(responseBodyProductProfiles.getBody());

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
	
	@RequestMapping(value = "/sap-upload/uploadOutstanding", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadOutstanding() throws IOException {

		log.debug("Web request to upload Outstanding ...");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get Outstanding URL: " + API_URL_OUTSTANDING);

		try {
			
			HttpEntity<T> entity = new HttpEntity<>(createTokenAuthHeaders());

			ResponseEntity<List<ResponseBodySapPraveshOutstanding>> responseBodyOutstanding = restTemplate
					.exchange(API_URL_OUTSTANDING, HttpMethod.GET, entity,
							new ParameterizedTypeReference<List<ResponseBodySapPraveshOutstanding>>() {
							});
			log.info("Outstanding Size= " + responseBodyOutstanding.getBody().size() + "------------");

			outstandingSapPraveshUploadService.saveOutstandingInvoice(responseBodyOutstanding.getBody());

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

}
