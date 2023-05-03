package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.service.BrandDevaService;
import com.orderfleet.webapp.service.BussinessUnitService;
import com.orderfleet.webapp.service.CityService;
import com.orderfleet.webapp.service.ContryFocusService;
import com.orderfleet.webapp.service.DistrictFocusService;
import com.orderfleet.webapp.service.StateFocusService;
import com.orderfleet.webapp.service.FiscalYearService;
import com.orderfleet.webapp.service.LengthTypeService;
import com.orderfleet.webapp.service.RouteCodeService;
import com.orderfleet.webapp.service.UnitsService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.BussinessUnitDTO;
import com.orderfleet.webapp.web.rest.dto.CityFocusDTO;
import com.orderfleet.webapp.web.rest.dto.ContryFocusDTO;
import com.orderfleet.webapp.web.rest.dto.DistrictFocusDTO;
import com.orderfleet.webapp.web.rest.dto.FiscalYearDTO;
import com.orderfleet.webapp.web.rest.dto.LengthTypeDTO;
import com.orderfleet.webapp.web.rest.dto.RouteCodeDTO;
import com.orderfleet.webapp.web.rest.dto.StateFocusDTO;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.focus.dto.AccountProfileResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationRequstFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.LengthTypeReponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.LengthTypeResponseObject;
import com.orderfleet.webapp.web.vendor.focus.dto.MasterDataReponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.MasterDataResponseObject;
import com.orderfleet.webapp.web.vendor.focus.dto.OutStandingResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileNewResponceFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.ProductProfileResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.service.AccountProfileFocusUploadService;
import com.orderfleet.webapp.web.vendor.focus.service.OutStandingFocusUploadService;
import com.orderfleet.webapp.web.vendor.focus.service.ProductProfileFocusUploadService;

import net.minidev.json.parser.ParseException;

@Service
public class UploadFocusResourceAutoShedule {

	private static String AUTHENTICATE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/Getlogin";

	private static String ACCOUNT_PROFILE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GetCustomerData?Auth_Token=";

	private static String PRODUCT_PROFILE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GETProductsWithPrice?Auth_Token=";

	private static String RECEIVABLE_PAYABLE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/OutStandingReportALL?Auth_Token=";

	@Inject
	private AccountProfileFocusUploadService accountProfileFocusUploadService;

	@Inject
	private OutStandingFocusUploadService outStandingFocusUploadService;

	@Inject
	private ProductProfileFocusUploadService productProfileFocusUploadService;
	private final Logger log = LoggerFactory.getLogger(UploadFocusResourceAutoShedule.class);

	@Transactional
	public void uploadToFocusAutomatically() throws URISyntaxException, IOException, JSONException, ParseException {
		log.debug("Web request to get a page of focus Upload Masters");
		uploadAccountProfiles();
		uploadProductProfiles();
		uploadReceivablePayable();

	}

	public ResponseEntity<Void> uploadAccountProfiles() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload Account Profiles...");

		String authToken = getAuthenticationToken();

		if (authToken != null) {

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			log.info("Get Account Profile URL: " + ACCOUNT_PROFILE_API_URL);

			AccountProfileResponseFocus accountProfileResponseFocus = restTemplate
					.getForObject(ACCOUNT_PROFILE_API_URL + authToken, AccountProfileResponseFocus.class);

			if (accountProfileResponseFocus.getGetCustomerDataResult().getAccountProfiles() != null) {

				log.info("Saving " + accountProfileResponseFocus.getGetCustomerDataResult().getAccountProfiles().size()
						+ " Account Profiles");

				if (accountProfileResponseFocus.getGetCustomerDataResult().getAccountProfiles().size() > 0) {
					accountProfileFocusUploadService.saveUpdateAccountProfiles(
							accountProfileResponseFocus.getGetCustomerDataResult().getAccountProfiles());
				}
			}
			System.out.println("response ok");
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	public ResponseEntity<Void> uploadProductProfiles() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload Product Profiles...");

		String authToken = getAuthenticationToken();

		if (authToken != null) {

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			log.info("Get Product Profile URL: " + PRODUCT_PROFILE_API_URL);

			ProductProfileNewResponceFocus productProfileNewResponceFocus = restTemplate
					.getForObject(PRODUCT_PROFILE_API_URL + authToken, ProductProfileNewResponceFocus.class);

			if (productProfileNewResponceFocus.getGetProductsWithPriceResult().getProduct() != null) {

				log.info("Saving " + productProfileNewResponceFocus.getGetProductsWithPriceResult().getProduct().size()
						+ " product Profiles");
				if (productProfileNewResponceFocus.getGetProductsWithPriceResult().getProduct().size() > 0) {
					productProfileFocusUploadService.saveUpdateProductProfiles(
							productProfileNewResponceFocus.getGetProductsWithPriceResult().getProduct());
				}
			}

			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	public ResponseEntity<Void> uploadReceivablePayable() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload ReceivablePayable...");

		String authToken = getAuthenticationToken();

		if (authToken != null) {

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			log.info("Get Account Profile URL: " + RECEIVABLE_PAYABLE_API_URL);

			OutStandingResponseFocus OutStandingResponseFocus = restTemplate
					.getForObject(RECEIVABLE_PAYABLE_API_URL + authToken, OutStandingResponseFocus.class);

			if (OutStandingResponseFocus.getOutStandingReportALLResult().getOutStandingFocus() != null) {

				log.info("Saving "
						+ OutStandingResponseFocus.getOutStandingReportALLResult().getOutStandingFocus().size()
						+ " Receivable payable ");

				if (OutStandingResponseFocus.getOutStandingReportALLResult().getOutStandingFocus().size() > 0) {

					outStandingFocusUploadService.saveUpdateReceivablePayable(
							OutStandingResponseFocus.getOutStandingReportALLResult().getOutStandingFocus());
				}
			}

			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String getAuthenticationToken() {

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

				return authenticationResponseFocus.getGetloginResult().getAuthToken();
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

		return null;
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
		authenticationRequstFocus.setCompanyCode("070");

		return authenticationRequstFocus;
	}

}
