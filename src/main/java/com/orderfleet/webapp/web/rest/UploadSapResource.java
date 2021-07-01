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
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.ResponseBodySapAccountProfile;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.ResponseBodySapAccountProfileOpeningBalance;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.ResponseBodySapProductProfile;
import com.orderfleet.webapp.web.vendor.sap.prabhu.service.AccountProfileSapUploadService;
import com.orderfleet.webapp.web.vendor.sap.prabhu.service.ProductProfileSapUploadService;

/**
 * used to upload xls
 *
 * @author Sarath
 * @since Nov 16, 2016
 */

@Controller
@RequestMapping("/web")
public class UploadSapResource {

	// private static String API_URL_ACCOUNT_PROFILE =
	// "http://59.94.176.87:81/Service1.svc/GetAllCustomersNameAndId?Scode=20&dbkey=1";

	private static String API_URL_ACCOUNT_PROFILE = "http://59.94.176.87:5002/Customer/GetAllCustomers";

	private static String API_URL_ACCOUNT_PROFILE_OPENING_BALANCE = "http://59.94.176.87:81/Service1.svc/GetAllCustomers?dbkey=1&code=20";

	private static String API_URL_PRODUCT_PROFILE = "http://59.94.176.87:81/Service1.svc/GetAllItemsNameAndId?dbkey=1";

	private static String AUTHENTICATION_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEzRjhFREM2QjJCNTU3OUQ0MEVGNDg1QkNBOUNFRDBBIiwidHlwIjoiYXQrand0In0.eyJuYmYiOjE2MDExMDg1MzMsImV4cCI6MTYzMjY0NDUzMywiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo1MDAwIiwiYXVkIjoiQ3VzdG9tZXJTZXJ2aWNlLkFwaSIsImNsaWVudF9pZCI6Im5hc19jbGllbnQiLCJzdWIiOiIwNWEwNzliMC1jZjBiLTQ2ZjctYThlMy1iODk4MjIwODgzMjQiLCJhdXRoX3RpbWUiOjE2MDExMDg1MzMsImlkcCI6ImxvY2FsIiwic2VydmljZS51c2VyIjoiYWRtaW4iLCJqdGkiOiIyOUU0OTRERTg1QjA0RTdBNUM1NjM3NDhCQzIyOTEyRSIsInNpZCI6IkFDOTE4QzNEMkY3MUIzRTRBMERGQzc2MDQ4QzJBMEUzIiwiaWF0IjoxNjAxMTA4NTMzLCJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwibmFzLmNsaWVudCIsIm5hcy5zZXJ2aWNlcyJdLCJhbXIiOlsicHdkIl19.x4knTyLtPEwUSnc35EnWSyxINwOLU5YTBeCD_eXDkXmMC1bWQclkdLH18Dgict07qVWyRL9EcYT66j4p7hsUGbZrZP9TLeNpQP5BT6eRSeYvkf2lmvJe1xaCvPYrHpPGvApLJJAmQxCwex7AAW74zJLpl_SdNUf3AHBkBvjr2ibEkDBgRgOTO0Z3n3f43ZxZw3LAi_x8ZRSxITY0mpevTUpDhx2pv5-ehXe7BaCbTxAJ6dBvkAavtmB-W3wp7cnJqSfr2mFpsBzE_Ek_OzAFnu_N1ALi8yE9LpuAPSDj4hVz11i98urPebHA8lEca1yBAPI6goQlKJEB4_NXI5F8CA";

//	private static String API_URL_ACCOUNT_PROFILE = "http://117.247.186.223:81/Service1.svc/GetAllCustomersNameAndId?Scode=20&dbkey=1";
//
//	private static String API_URL_ACCOUNT_PROFILE_OPENING_BALANCE = "http://117.247.186.223:81/Service1.svc/GetAllCustomers?dbkey=1&code=20";
//
//	private static String API_URL_PRODUCT_PROFILE = "http://117.247.186.223:81/Service1.svc/GetAllItemsNameAndId?dbkey=1";

	private final Logger log = LoggerFactory.getLogger(UploadSapResource.class);

	@Inject
	private AccountProfileSapUploadService accountProfileSapUploadService;

	@Inject
	private ProductProfileSapUploadService productProfileSapUploadService;

	@RequestMapping(value = "/upload-sap", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Upload Sap Masters");
		return "company/uploadSap";
	}

	@RequestMapping(value = "/upload-sap/uploadAccountProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadAccountProfiles() throws IOException {

		log.debug("Web request to upload Account Profiles ...");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get Account Profile  URL: " + API_URL_ACCOUNT_PROFILE);
//		log.info("Get Account Profile Opening Balance  URL: " + API_URL_ACCOUNT_PROFILE_OPENING_BALANCE);

		try {

			HttpEntity<T> entity = new HttpEntity<>(createTokenAuthHeaders());

			ResponseEntity<List<ResponseBodySapAccountProfile>> responseBodyAccountProfiles = restTemplate.exchange(
					API_URL_ACCOUNT_PROFILE, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<ResponseBodySapAccountProfile>>() {
					});

//			ResponseEntity<List<ResponseBodySapAccountProfileOpeningBalance>> responseBodyAccountProfileOpeningBalances = restTemplate
//					.exchange(API_URL_ACCOUNT_PROFILE_OPENING_BALANCE, HttpMethod.GET, null,
//							new ParameterizedTypeReference<List<ResponseBodySapAccountProfileOpeningBalance>>() {
//							});

			log.info("Account Profile Size= " + responseBodyAccountProfiles.getBody().size() + "------------");

//			log.info("Account Profile Opening Balance Size= "
//					+ responseBodyAccountProfileOpeningBalances.getBody().size() + "------------");

//			accountProfileSapUploadService.saveUpdateAccountProfiles(responseBodyAccountProfiles.getBody(),
//					responseBodyAccountProfileOpeningBalances.getBody());

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

	@RequestMapping(value = "/upload-sap/uploadProductProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadProductProfiles() throws IOException {

		log.debug("Web request to upload Product Profiles ...");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get Product Profile URL: " + API_URL_PRODUCT_PROFILE);

		try {

			ResponseEntity<List<ResponseBodySapProductProfile>> responseBodyProductProfiles = restTemplate.exchange(
					API_URL_PRODUCT_PROFILE, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<ResponseBodySapProductProfile>>() {
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

}
