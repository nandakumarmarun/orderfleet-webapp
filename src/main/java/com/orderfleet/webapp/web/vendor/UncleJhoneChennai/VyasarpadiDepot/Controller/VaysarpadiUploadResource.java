package com.orderfleet.webapp.web.vendor.UncleJhoneChennai.VyasarpadiDepot.Controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.vendor.UncleJhoneChennai.VyasarpadiDepot.Service.VyasarpadiAccountsUploadService;
import com.orderfleet.webapp.web.vendor.UncleJhoneChennai.VyasarpadiDepot.Service.VyasarpadiProductsUploadService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.AccountProfileResponseUj;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.DealerResponseUJ;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.ProductProfileResponceUJ;

import net.minidev.json.parser.ParseException;

@Controller
@RequestMapping("/web")
public class VaysarpadiUploadResource {

	private final Logger log = LoggerFactory.getLogger(VaysarpadiUploadResource.class);

	@Inject
	private VyasarpadiProductsUploadService VyasarpadiProductsUploadService;

	@Inject
	private VyasarpadiAccountsUploadService VyasarpadiAccountsUploadService;

	@Inject
	private  CompanyRepository companyRepository;

	RestTemplate restTemplate = new RestTemplate();

	static final String FACTORY_CODE = "KV";

	@RequestMapping(value = "/upload-unclejhon-chennai", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String uploadXls(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Upload Uncle Jhon Masters");
		return "company/uploadUncleJhonChennai";
	}


	@RequestMapping(value = "/upload-unclejhon-chennai/vyasarpadi-accounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadAccountProfiles() throws IOException, JSONException, ParseException {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";

		log.info(Thread + "Enter : com.orderfleet.webapp.web.vendor.UncleJhoneChennai.Controller.uploadAccountProfiles() ");

		HttpHeaders headers = getHttpHeaders();
		Map<String, Object> requestBody = getCustomerRequestBody();
		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

		log.debug(Thread + " Fetching Customer Masters : " + requestEntity);

		ResponseEntity<AccountProfileResponseUj> accountProfileResponse =
				restTemplate.exchange(
						"http://192.168.2.54/?request=apiNtrich",
						HttpMethod.POST,
						requestEntity,
						AccountProfileResponseUj.class);


		log.debug(Thread + " Fetching Dealer Masters : " + requestEntity);
		Map<String, Object> dealerBody = getDealerRequestBody();
		HttpEntity<Map<String, Object>> dealerEntity = new HttpEntity<>(dealerBody, headers);
		log.debug(Thread + " Fetching Customer Masters : " + requestEntity);
		
		ResponseEntity<DealerResponseUJ> dealerResponse =
				restTemplate.exchange(
						"http://192.168.2.54/?request=apiNtrich",
						HttpMethod.POST,
						dealerEntity,
						DealerResponseUJ.class);

		log.debug(Thread + "Customer Size : "  + accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ().size());
		log.debug(Thread + "Dealer Size : " + dealerResponse.getBody().getDealerUJ().getDealer().size());

		if (accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ() != null
				&& dealerResponse.getBody().getDealerUJ().getDealer() != null) {

			VyasarpadiAccountsUploadService
					.saveUpdateLocations(
					accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ(),
					dealerResponse.getBody().getDealerUJ().getDealer());

			VyasarpadiAccountsUploadService
					.saveUpdateAccounts(
							accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ());

			VyasarpadiAccountsUploadService.saveAccountProfileGeoLocation(
					accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ());

			VyasarpadiAccountsUploadService
					.saveDealer(dealerResponse.getBody().getDealerUJ().getDealer());

			VyasarpadiAccountsUploadService
					.saveDistributorDealerAssociation(dealerResponse.getBody().getDealerUJ().getDealer());

			VyasarpadiAccountsUploadService.saveUpdateLocationAccounts(
					accountProfileResponse.getBody().getAccountProfileUJ().getAccountUJ(),
					dealerResponse.getBody().getDealerUJ().getDealer());
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private static Map<String, Object> getCustomerRequestBody() {
		Map<String, Object> requestBody = new HashMap<>();
		Map<String, String> payload = new HashMap<>();
		requestBody.put("endPoint", "ret_custmast");
		requestBody.put("apiKey", 11111);
		payload.put("factory", FACTORY_CODE);
		requestBody.put("payload", payload);
		return requestBody;
	}

	private static Map<String, Object> getDealerRequestBody() {
		Map<String, Object> requestBody = new HashMap<>();
		Map<String, String> payload = new HashMap<>();
		requestBody.put("endPoint", "ret_dealmast");
		requestBody.put("apiKey", 11111);
		payload.put("factory", FACTORY_CODE);
		requestBody.put("payload", payload);
		return requestBody;
	}

	private static Map<String, Object> getItemRequestBody() {
		Map<String, Object> requestBody = new HashMap<>();
		Map<String, String> payload = new HashMap<>();
		requestBody.put("endPoint", "ret_itemmast");
		requestBody.put("apiKey", 11111);
		payload.put("factory", "KG");
		requestBody.put("payload", payload);
		return requestBody;
	}

	private static HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	private ResponseEntity<ProductProfileResponceUJ> TestMethodProduct() {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Map<String, Object>> Entity = new HttpEntity<>(headers);
		ResponseEntity<ProductProfileResponceUJ> productProfileResponse =
				restTemplate.exchange(
						"http://192.168.1.77:3015/item",
						HttpMethod.GET,
						Entity,
						ProductProfileResponceUJ.class);
		return productProfileResponse;
	}

	private ResponseEntity<DealerResponseUJ> TestMethodDealer() {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Map<String, Object>> Entity = new HttpEntity<>(headers);
		ResponseEntity<DealerResponseUJ> productProfileResponse =
				restTemplate.exchange(
						"http://192.168.1.77:3002/dealer",
						HttpMethod.GET,
						Entity,
						DealerResponseUJ.class);
		return productProfileResponse;
	}

	private ResponseEntity<AccountProfileResponseUj> TestMethodCustomer() {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Map<String, Object>> Entity = new HttpEntity<>(headers);
		ResponseEntity<AccountProfileResponseUj> productProfileResponse =
				restTemplate.exchange(
						"http://192.168.1.77:3000/customer",
						HttpMethod.GET,
						Entity,
						AccountProfileResponseUj.class);
		return productProfileResponse;
	}


	@RequestMapping(value = "/upload-unclejhon-chennai/vyasarpadi-products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> uploadProductProfiles() throws IOException, JSONException, ParseException {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+ RandomUtil.generatePid()+" -- ["+company.getLegalName()+"] : ";

		log.info(Thread + "Enter : com.orderfleet.webapp.web.vendor.UncleJhoneChennai.Controller.uploadProductProfiles() ");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> requestBody = getItemRequestBody();
		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

		log.info(Thread + "Item Response Entity" + requestEntity);

		ResponseEntity<ProductProfileResponceUJ> productProfileResponse =
				restTemplate.exchange(
						"http://192.168.2.54/?request=apiNtrich",
						HttpMethod.POST,
						requestEntity,
						ProductProfileResponceUJ.class);


		log.debug("Product Size :" + productProfileResponse.getBody().getProductProfileUJ().getProductUJ().size());

		if (productProfileResponse.getBody().getProductProfileUJ().getProductUJ() != null) {

			VyasarpadiProductsUploadService
					.saveUpdateProductProfiles(
							productProfileResponse.getBody().getProductProfileUJ().getProductUJ());

			VyasarpadiProductsUploadService
					.saveProductGroupProduct(
							productProfileResponse.getBody().getProductProfileUJ().getProductUJ());
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
