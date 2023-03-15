package com.orderfleet.webapp.web.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.springframework.web.client.RestTemplate;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.AccountProfileResponseUj;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.DealerResponseUJ;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.ProductProfileResponceUJ;
import com.orderfleet.webapp.web.vendor.uncleJhon.service.AccountProfileUncleJhonUploadService;
import com.orderfleet.webapp.web.vendor.uncleJhon.service.ProductProfileUncleJhonUploadService;

import net.minidev.json.parser.ParseException;

@Controller
@RequestMapping("/web")
public class UploadUncleJohnResource {

	private final Logger log = LoggerFactory.getLogger(UploadUncleJohnResource.class);


	String url = "http://192.168.2.54/?request=apiNtrich";

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

		ProductProfileResponceUJ productProfileResponse = new ProductProfileResponceUJ();

		String[] command = {
				"curl -d '{\"endPoint\" : \"ret_itemmast\",\"apiKey\":11111,\"payload\" : {\"factory\":\"KG\"}}' \\ -H \"Content-Type: application/json",
				url };

		String productData = curlProcessRequest(command);

		ObjectMapper objectMapper = new ObjectMapper();
		productProfileResponse = objectMapper.readValue(productData, ProductProfileResponceUJ.class);

		System.out.println("Response created....");
		System.out.println("Size :" + productProfileResponse.getProductProfileUJ().getProductUJ().size());
		if (productProfileResponse.getProductProfileUJ().getProductUJ() != null) {

			productUJUploadService
					.saveUpdateProductProfiles(productProfileResponse.getProductProfileUJ().getProductUJ());
			productUJUploadService.saveProductGroupProduct(productProfileResponse.getProductProfileUJ().getProductUJ());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/upload-uncleJhon/uploadAccountProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadAccountProfiles() throws IOException, JSONException, ParseException {

		log.debug("Web request to upload Account Profiles and dealer Master...");

		AccountProfileResponseUj accountProfileResponse = new AccountProfileResponseUj();
		DealerResponseUJ dealerResponse = new DealerResponseUJ();

	
		String[] command = {
				"curl -d '{\"endPoint\" : \"ret_custmast\",\"apiKey\":11111,\"payload\" : {\"factory\":\"KG\"}}' \\ -H \"Content-Type: application/json",
				url };
		String customertData = curlProcessRequest(command);

		ObjectMapper objectMapper = new ObjectMapper();
		accountProfileResponse = objectMapper.readValue(customertData, AccountProfileResponseUj.class);
	
		String[] dealercommand = {
				"curl -d '{\"endPoint\" : \"ret_dealmast\",\"apiKey\":11111,\"payload\" : {\"factory\":\"KG\"}}' \\ -H \"Content-Type: application/json",
				url };

		String dealerData = curlProcessRequest(dealercommand);
		
		ObjectMapper dealerMapper = new ObjectMapper();
		dealerResponse = dealerMapper.readValue(dealerData, DealerResponseUJ.class);

		System.out.println("Response created....");

		System.out.println("Size :" + accountProfileResponse.getAccountProfileUJ().getAccountUJ().size());
		if (accountProfileResponse.getAccountProfileUJ().getAccountUJ() != null
				&& dealerResponse.getDealerUJ().getDealer() != null) {

			accountProfileUJUploadService.saveUpdateLocations(
					accountProfileResponse.getAccountProfileUJ().getAccountUJ(),
					dealerResponse.getDealerUJ().getDealer());
			accountProfileUJUploadService
					.saveUpdateAccounts(accountProfileResponse.getAccountProfileUJ().getAccountUJ());
			accountProfileUJUploadService.saveDealer(dealerResponse.getDealerUJ().getDealer());
			accountProfileUJUploadService.saveDistributorDealerAssociation(dealerResponse.getDealerUJ().getDealer());
			accountProfileUJUploadService.saveUpdateLocationAccounts(
					accountProfileResponse.getAccountProfileUJ().getAccountUJ(),
					dealerResponse.getDealerUJ().getDealer());

		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String curlProcessRequest(String[] command) throws IOException {
		Process p;
		String result = null;
		StringBuilder builder = new StringBuilder();
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
			builder.append(System.getProperty("line.separator"));
		}
		result = builder.toString();
		System.out.print(result);

		return result;

	}
}
