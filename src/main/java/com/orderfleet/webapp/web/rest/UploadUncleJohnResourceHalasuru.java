package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.AccountProfileResponseUj;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.DealerResponseUJ;
import com.orderfleet.webapp.web.vendor.uncleJhon.service.AccountProfileUncleJhonHalasuruUploadService;
import com.orderfleet.webapp.web.vendor.uncleJhon.service.ProductProfileUncleJhonUploadService;
import net.minidev.json.parser.ParseException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/web")
public class UploadUncleJohnResourceHalasuru {

	private final Logger log = LoggerFactory.getLogger(UploadUncleJohnResourceHalasuru.class);

	@Inject
	private ProductProfileUncleJhonUploadService productUJUploadService;

	@Inject
	private AccountProfileUncleJhonHalasuruUploadService accountProfileUncleJhonHalasuluUploadService;

	private  RestTemplate RestTemplate = new RestTemplate();
	
	static  final  String BASE_URL = "http://192.168.2.54/?request=apiNtrich";

//	@RequestMapping(value = "/upload-uncle-john-harasulu", method = RequestMethod.GET)
//	@Timed
//	@Transactional(readOnly = true)
//	public String uploadXls(Model model) throws URISyntaxException {
//		log.debug("Web request to get a page of Upload Uncle Jhon Masters");
//		return "company/uploadUncleJhonBangolore";
//	}

	@RequestMapping(value = "/upload-uncleJhon/upload-account-profiles-halasuru", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadAccountProfiles() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload Account Profiles Enter : uploadAccountProfiles()");

		log.debug("Fetching Customer Data");

		ResponseEntity<AccountProfileResponseUj> accountProfileResponse = getUncleJhoneCustomerResponseEntity();

		log.debug("Customers :" + accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ().size());

		log.debug("Fetching Dealer Data");

		ResponseEntity<DealerResponseUJ> dealerResponse = getUncleJhoneDealerResponseEntity();

		log.debug("Dealers :" + dealerResponse.getBody().getDealerUJ().getDealer().size());
		
		if (accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ() != null
				&& dealerResponse.getBody().getDealerUJ().getDealer() != null) {

			log.debug("Saving Locations");
			accountProfileUncleJhonHalasuluUploadService.saveUpdateLocations(accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ(),
					dealerResponse.getBody().getDealerUJ().getDealer());

			log.debug("Saving Customers");
			accountProfileUncleJhonHalasuluUploadService.saveUpdateAccounts(accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ());

			log.debug("Saving Customer Geo Locations");
			accountProfileUncleJhonHalasuluUploadService.saveAccountProfileGeoLocation(accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ());

			log.debug("Saving Dealers");
			accountProfileUncleJhonHalasuluUploadService.saveDealer(dealerResponse.getBody().getDealerUJ().getDealer());

			log.debug("Saving Dealer Distributor Associations");
			accountProfileUncleJhonHalasuluUploadService.saveDistributorDealerAssociation(dealerResponse.getBody().getDealerUJ().getDealer());

			log.debug("Assigning Location To Customers");
			accountProfileUncleJhonHalasuluUploadService.saveUpdateLocationAccounts(accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ(),
					dealerResponse.getBody().getDealerUJ().getDealer());

		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private ResponseEntity<AccountProfileResponseUj> getUncleJhoneCustomerResponseEntity() {

		Map<String, Object> requestBody = getRequestBody("ret_custmast","11111","KI");

		HttpHeaders headers = getHttpHeaders();

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

//		testing Api
//		HttpEntity<Map<String, Object>> Entity = new HttpEntity<>(headers);
//		ResponseEntity<AccountProfileResponseUj> accountProfileResponse = RestTemplate.exchange(
//				"http://192.168.1.77:3000/customer", HttpMethod.GET, Entity,AccountProfileResponseUj.class);
		
		ResponseEntity<AccountProfileResponseUj> accountProfileResponse = RestTemplate.exchange(
				BASE_URL, HttpMethod.POST, requestEntity,AccountProfileResponseUj.class);

		return accountProfileResponse;
	}

	private ResponseEntity<DealerResponseUJ> getUncleJhoneDealerResponseEntity() {

		Map<String, Object> dealerBody= getRequestBody("ret_dealmast","11111","KI");

		HttpHeaders headers = getHttpHeaders();

		//	testing Api
        //	HttpEntity<Map<String, Object>> Entity = new HttpEntity<>(headers);
		//	ResponseEntity<DealerResponseUJ> dealerResponse = RestTemplate.exchange(
		// "http://192.168.1.77:3002/dealer", HttpMethod.GET, Entity, DealerResponseUJ.class);

		HttpEntity<Map<String, Object>> dealerEntity = new HttpEntity<>(dealerBody, headers);

		ResponseEntity<DealerResponseUJ> dealerResponse = RestTemplate.exchange(
				BASE_URL, HttpMethod.POST, dealerEntity, DealerResponseUJ.class);

		return dealerResponse;
	}

	private static HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private Map<String, Object> getRequestBody(String endPoint,String apiKey,String factory) {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("endPoint", endPoint);
		requestBody.put("apiKey", apiKey);
		Map<String, String> payload = new HashMap<>();
		payload.put("factory", factory);
		requestBody.put("payload", payload);
		return requestBody;
	}

}
