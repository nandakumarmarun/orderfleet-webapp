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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.focus.dto.AccountProfileResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationRequstFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.service.AccountProfileFocusUploadService;
import com.orderfleet.webapp.web.vendor.focus.service.ProductProfileFocusUploadService;

import net.minidev.json.parser.ParseException;

@Controller
@RequestMapping("/web")
public class UploadFocusResource {

	private static String AUTHENTICATE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/Getlogin";

	private static String ACCOUNT_PROFILE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GetCustomerData?Auth_Token=";

	private static String PRODUCT_PROFILE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GetMasterData?Auth_Token=";
	@Inject
	private AccountProfileFocusUploadService accountProfileFocusUploadService;

	@Inject
	private ProductProfileFocusUploadService productProfileFocusUploadService;
	private final Logger log = LoggerFactory.getLogger(UploadFocusResource.class);

	@RequestMapping(value = "/upload-focus", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Upload Focus Masters");

		AuthenticationRequstFocus authenticationRequstFocus = authenticationBody();

		HttpEntity<AuthenticationRequstFocus> entity = new HttpEntity<>(authenticationRequstFocus,
				RestClientUtil.createTokenAuthHeaders());

		ObjectMapper Obj = new ObjectMapper();

		String jsonStr;
		try {
			jsonStr = Obj.writeValueAsString(entity.getBody());
			log.info("Authentication Request Body: -" + jsonStr);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Authentication URL: " + AUTHENTICATE_API_URL);

		try {
			AuthenticationResponseFocus authenticationResponseFocus = restTemplate.postForObject(AUTHENTICATE_API_URL,
					entity, AuthenticationResponseFocus.class);

			Obj = new ObjectMapper();

			String jsonStr1;
			try {
				jsonStr1 = Obj.writeValueAsString(authenticationResponseFocus);
				log.info("Authentication Response Body: -" + jsonStr1);

			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (authenticationResponseFocus.getGetloginResult().getsMessage().equals("Token Generated")) {
				ACCOUNT_PROFILE_API_URL = ACCOUNT_PROFILE_API_URL
						+ authenticationResponseFocus.getGetloginResult().getAuthToken();
			} else {
				ACCOUNT_PROFILE_API_URL = "";
			}

			if (authenticationResponseFocus.getGetloginResult().getsMessage().equals("Token Generated")) {
				PRODUCT_PROFILE_API_URL = PRODUCT_PROFILE_API_URL
						+ authenticationResponseFocus.getGetloginResult().getAuthToken();
			} else {
				PRODUCT_PROFILE_API_URL = "";
			}

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

		return "company/uploadFocus";
	}

	@RequestMapping(value = "/upload-focus/uploadAccountProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadAccountProfiles() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload Account Profiles...");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get Account Profile URL: " + ACCOUNT_PROFILE_API_URL);

		AccountProfileResponseFocus accountProfileResponseFocus = restTemplate.getForObject(ACCOUNT_PROFILE_API_URL,
				AccountProfileResponseFocus.class);

		accountProfileFocusUploadService
				.saveUpdateAccountProfiles(accountProfileResponseFocus.getGetCustomerDataResult().getAccountProfiles());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-focus/uploadProductProfiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadProductProfiles() throws IOException, JSONException, ParseException {

		log.debug("web request to upload productProfiles");

		String jsonstring = productRequestBody();
		HttpEntity<String> entity = new HttpEntity<>(jsonstring, RestClientUtil.createTokenAuthHeaders());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Product profile URL:" + PRODUCT_PROFILE_API_URL);

		ProductProfileResponseFocus productProfileResponseFocus = restTemplate.postForObject(PRODUCT_PROFILE_API_URL,
				entity, ProductProfileResponseFocus.class);

		productProfileFocusUploadService
				.saveUpdateProductProfiles(productProfileResponseFocus.getGetMasterDataResult().getProductProfiles());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String productRequestBody() throws JSONException {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("MasterName", "Item_Master");
		return jsonObject.toString();

	}

	private AuthenticationRequstFocus authenticationBody() {

		AuthenticationRequstFocus authenticationRequstFocus = new AuthenticationRequstFocus();

		authenticationRequstFocus.setUserName("su");
		authenticationRequstFocus.setPassword("deva$focus");
		authenticationRequstFocus.setCompanyCode("020");

		return authenticationRequstFocus;
	}

}
