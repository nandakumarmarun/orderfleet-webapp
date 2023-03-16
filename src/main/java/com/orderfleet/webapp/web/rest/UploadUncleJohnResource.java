package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.crypto.Data;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.AccountProfileResponseUj;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.DealerResponseUJ;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.ProductProfileResponceUJ;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.ProductProfileUJ;
import com.orderfleet.webapp.web.vendor.uncleJhon.service.AccountProfileUncleJhonUploadService;
import com.orderfleet.webapp.web.vendor.uncleJhon.service.ProductProfileUncleJhonUploadService;

import net.minidev.json.parser.ParseException;

@Controller
@RequestMapping("/web")
public class UploadUncleJohnResource {

	private final Logger log = LoggerFactory.getLogger(UploadUncleJohnResource.class);

//	String baseUrl = "http://192.168.2.54/?request=apiNtrich";
	
	@Inject
	private ProductProfileUncleJhonUploadService productUJUploadService;

	@Inject
	private AccountProfileUncleJhonUploadService accountProfileUJUploadService;

	@RequestMapping(value = "/upload-uncleJhon", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Upload Uncle Jhon Masters");

		return "company/uploadUncleJhon";
	}

	@RequestMapping(value = "/upload-uncleJhon/uploadProductProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadProductProfiles() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload Product Profiles...");

		RestTemplate restTemplate = new RestTemplate();

		// Set the request headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Set the request body
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("endPoint", "ret_itemmast");
		requestBody.put("apiKey", 11111);
		Map<String, String> payload = new HashMap<>();
		payload.put("factory", "KG");
		requestBody.put("payload", payload);

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
		
		log.info("RequestEntity :"+requestEntity);

		ResponseEntity<ProductProfileResponceUJ> productProfileResponse = restTemplate.exchange(
				"http://192.168.2.54/?request=apiNtrich", HttpMethod.POST, requestEntity,
				ProductProfileResponceUJ.class);
		log.debug("Response created....");
		log.debug("Size :" + productProfileResponse.getBody().getProductProfileUJ().getProductUJ().size());
		if (productProfileResponse.getBody().getProductProfileUJ().getProductUJ() != null) {

			productUJUploadService
					.saveUpdateProductProfiles(productProfileResponse.getBody().getProductProfileUJ().getProductUJ());
			productUJUploadService
					.saveProductGroupProduct(productProfileResponse.getBody().getProductProfileUJ().getProductUJ());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-uncleJhon/uploadAccountProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadAccountProfiles() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload Account Profiles and dealer Master...");

		RestTemplate restTemplate = new RestTemplate();

		// Set the request headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Set the request body
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("endPoint", "ret_custmast");
		requestBody.put("apiKey", 11111);
		Map<String, String> payload = new HashMap<>();
		payload.put("factory", "KG");
		requestBody.put("payload", payload);

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
		
		ResponseEntity<AccountProfileResponseUj> accountProfileResponse = restTemplate.exchange("http://192.168.2.54/?request=apiNtrich", HttpMethod.POST,requestEntity,AccountProfileResponseUj.class);
		
		Map<String, Object> dealerBody = new HashMap<>();
		dealerBody.put("endPoint", "ret_dealmast");
		dealerBody.put("apiKey", 11111);
		Map<String, String> dealerpayload = new HashMap<>();
		dealerpayload.put("factory", "KG");
		dealerBody.put("dpayload", dealerpayload);

		HttpEntity<Map<String, Object>> dealerEntity = new HttpEntity<>(dealerBody, headers);

		ResponseEntity<DealerResponseUJ> dealerResponse = restTemplate.exchange("http://192.168.2.54/?request=apiNtrich", HttpMethod.POST, dealerEntity,
				DealerResponseUJ.class);

		log.debug("Response created....");
		
		log.debug("Size :" + accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ().size());
		if (accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ() != null
				&& dealerResponse.getBody().getDealerUJ().getDealer() != null) {

			accountProfileUJUploadService.saveUpdateLocations(
					accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ(),
					dealerResponse.getBody().getDealerUJ().getDealer());
			accountProfileUJUploadService
					.saveUpdateAccounts(accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ());
			accountProfileUJUploadService.saveDealer(dealerResponse.getBody().getDealerUJ().getDealer());
			accountProfileUJUploadService
					.saveDistributorDealerAssociation(dealerResponse.getBody().getDealerUJ().getDealer());
			accountProfileUJUploadService.saveUpdateLocationAccounts(
					accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ(),
					dealerResponse.getBody().getDealerUJ().getDealer());

		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
