package com.orderfleet.webapp.web.vendor.focus.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.web.rest.UploadFocusResource;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.focus.dto.AccountProfileResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationRequstFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.StockRequstFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.StockResponseFocus;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.ApiResponseDataSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderMasterSap;

@Service
public class StockFocusService {

	@Inject
	ProductProfileRepository productProfileRepository;
	
	private final Logger log = LoggerFactory.getLogger(StockFocusService.class);
	private static String AUTHENTICATE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/Getlogin";
	private static String STOCK = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/GetStock?Auth_Token=";
	
	public double getStockFocus(String productPid) {
		Optional<ProductProfile> optionalProduct = productProfileRepository.findOneByCompanyIdandPid(productPid);
		if(optionalProduct.isPresent()) {
			log.debug(" request to get stock...");

			String authToken = getAuthenticationToken();
			if (authToken != null) {
				log.debug(" request to Api"+STOCK + authToken);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
				StockRequstFocus requstBody = createRequstBody(optionalProduct.get());
				HttpEntity<StockRequstFocus> entity = new HttpEntity<>(requstBody);
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr;
				try {
					jsonStr = Obj.writeValueAsString(entity.getBody());
					log.info(jsonStr);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {

					ResponseEntity<StockResponseFocus> authenticateResponse = restTemplate.exchange(
							STOCK + authToken, HttpMethod.POST, entity,
							new ParameterizedTypeReference<StockResponseFocus>() {
							});
					
					
					if(authenticateResponse.getBody().getGetStockResult().getIsStatus() == 1 && !authenticateResponse.getBody().getGetStockResult().getStockProducts().isEmpty()){
						log.info("Is status====="+authenticateResponse.getBody().getGetStockResult().getIsStatus());
						log.info("Stock Count====="+authenticateResponse.getBody().getGetStockResult().getCount());
						return authenticateResponse.getBody().getGetStockResult().getStockProducts().get(0).getAvailableStock();
					}else {
						return 0.0;
					}
				} catch (HttpClientErrorException exception) {
					if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
						log.info(exception.getResponseBodyAsString());
						exception.printStackTrace();
					}
					exception.printStackTrace();
				} catch (Exception exception) {
					log.info(exception.getMessage());
					exception.printStackTrace();
				}
			}
		}else {
			return 0.0;
		}
		return 0.0;
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
	
	private AuthenticationRequstFocus authenticationBody() {

		AuthenticationRequstFocus authenticationRequstFocus = new AuthenticationRequstFocus();

		authenticationRequstFocus.setUserName("su");
		authenticationRequstFocus.setPassword("deva$focus");
		authenticationRequstFocus.setCompanyCode("020");

		return authenticationRequstFocus;
	}
	
	private StockRequstFocus createRequstBody(ProductProfile productProfile) {
		StockRequstFocus stockRequst = new StockRequstFocus();
		stockRequst.setItemCode(productProfile.getProductId());
		stockRequst.setWarehouseCode("");
		return stockRequst;
	}
}
