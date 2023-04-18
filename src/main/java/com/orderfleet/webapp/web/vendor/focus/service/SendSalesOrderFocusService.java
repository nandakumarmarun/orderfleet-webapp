package com.orderfleet.webapp.web.vendor.focus.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.LengthType;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.RouteCode;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LengthTypeRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.RouteCodeRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.vendor.focus.dto.ApiResponeDataFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationRequstFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.AuthenticationResponseFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.SaleOrderFocusObject;
import com.orderfleet.webapp.web.vendor.focus.dto.SalesOrderItemDetailsFocus;
import com.orderfleet.webapp.web.vendor.focus.dto.SalesOrderMasterFocus;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.ApiResponseDataSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderItemDetailsSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderMasterSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.service.SendSalesOrderSapService;

@Service
public class SendSalesOrderFocusService {

	private static String AUTHENTICATE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/Getlogin";
	private static String API_URL_SALES_ORDER = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/SalesOrder?Auth_Token=";
	private final Logger log = LoggerFactory.getLogger(SendSalesOrderSapService.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	public static int successCount = 0;

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherService;

	@Inject
	private final CompanyRepository companyRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountProfileRepository accountProfilerepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private RouteCodeRepository routeCodeRepository;

	@Inject
	private final LengthTypeRepository lengthTypeRepository;

	public SendSalesOrderFocusService(InventoryVoucherHeaderService inventoryVoucherService,
			CompanyRepository companyRepository, LengthTypeRepository lengthTypeRepository) {
		super();
		this.inventoryVoucherService = inventoryVoucherService;
		this.companyRepository = companyRepository;
		this.lengthTypeRepository = lengthTypeRepository;
	}

	public void sendSalesOrder(String inventoryPid) {

		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		log.debug("REST request to download sales orders <" + company.getLegalName() + "> : {}");

		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findByPidSalesOrder(inventoryPid);
//		List<Object[]> inventoryVouchers =  inventoryVoucherHeaderRepository.getPrimarySecondarySalesOrderForFocus(companyId, inventoryPid);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO(inventoryVoucherHeader);

		System.out.println("inventoryVoucherDTOfocus" + inventoryVoucherHeaderDTO);

		List<String> ivhPids = new ArrayList<>();
		ivhPids.add(inventoryPid);
//		
		if (inventoryVoucherHeaderDTO.getTallyDownloadStatus().equals(TallyDownloadStatus.PENDING)
				&& inventoryVoucherHeaderDTO.getSalesManagementStatus().equals(SalesManagementStatus.APPROVE)) {

			int updated = inventoryVoucherHeaderRepository
					.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(TallyDownloadStatus.PROCESSING, ivhPids);

			SaleOrderFocusObject requestBody = getRequestBody(inventoryVoucherHeaderDTO);

			System.out.println(requestBody);

//			List<SalesOrderItemDetailsFocus> salesOrderItemDetailsFocus = requestBody.getSalesOrderItemDetailsFocus();

//			 salesOrderItemDetailsFocus.forEach(data->System.out.println("details"+data.getLengthInFeet()+"==="+data.getLengthType()+"=="+data.getItem()));

			HttpEntity<SaleOrderFocusObject> entity = new HttpEntity<>(requestBody, createTokenAuthHeaders());

			log.info("ObjectBody" + entity.getBody().toString() + "");

			ObjectMapper Obj = new ObjectMapper();

			String jsonStr;

			try {
				jsonStr = Obj.writeValueAsString(entity.getBody());
				log.info(jsonStr);
				System.out.println("------------------------------------------------------------------------------");
				System.out.println("jsonString" + jsonStr);
				System.out.println("------------------------------------------------------------------------------");
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			String authToken = getAuthenticationToken();

			try {
				ApiResponeDataFocus authenticationResponseFocus = restTemplate
						.postForObject(API_URL_SALES_ORDER + authToken, entity, ApiResponeDataFocus.class);

				log.debug("Focus Sales Order Created Success Size" + "----" + authenticationResponseFocus);

				changeSalesOrderServerDownloadStatus(authenticationResponseFocus, ivhPids,companyId);
			}

			catch (HttpClientErrorException exception) {
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
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	public void salesOrderPushToFocus(List<InventoryVoucherHeader> inventoryVoucherHeader) {
		long start = System.nanoTime();

		log.debug("REST request to download sales orders  automatically: {} " + " : " + "Enter : salesOrderPushToFocus() - [" + inventoryVoucherHeader.toString()+"]");

		List<String> ivhPids = new ArrayList<>();

		for (InventoryVoucherHeader ivh : inventoryVoucherHeader) {

			InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO(ivh);

			ivhPids.add(ivh.getPid());

			if (inventoryVoucherHeaderDTO.getTallyDownloadStatus().equals(TallyDownloadStatus.PENDING)) {

				RestTemplate restTemplate = new RestTemplate();

				inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(TallyDownloadStatus.PROCESSING, ivhPids,ivh.getCompany().getId());

				SaleOrderFocusObject requestBody = getRequestBody(inventoryVoucherHeaderDTO);

				log.debug(ivh.getCompany().getLegalName() +" : "+ ivh.getDocumentNumberLocal() +" : SalesOrder RequestBody : "+requestBody.toString() + "\n");

				HttpEntity<SaleOrderFocusObject> entity = new HttpEntity<>(requestBody, createTokenAuthHeaders());

				log.debug(ivh.getCompany().getLegalName() +" : "+ ivh.getDocumentNumberLocal() +" : HTTP Request Object String : "+ entity.getBody().toString() + "\n");

				restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

				parseTOJsonString(entity);

				try {

					String authToken = getAuthenticationToken();

					ApiResponeDataFocus authenticationResponseFocus = restTemplate.postForObject(API_URL_SALES_ORDER + authToken, entity, ApiResponeDataFocus.class);

					log.debug(ivh.getCompany().getLegalName() +" : "+ ivh.getDocumentNumberLocal() +" : Focus Sales Order Created Success Size" + "----" + authenticationResponseFocus);

					changeSalesOrderServerDownloadStatus(authenticationResponseFocus, ivhPids,ivh.getCompany().getId());
				}
				catch (HttpClientErrorException exception) {
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
			long end = System.nanoTime();
			double elapsedTime = (end - start) / 1000000.0;
			log.info("Sync completed in {} ms", elapsedTime);
		}
	}

	private void parseTOJsonString(HttpEntity<SaleOrderFocusObject> entity) {
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr;
		try {
			jsonStr = Obj.writeValueAsString(entity.getBody());
			log.debug("\n");
			log.debug("Parsing Sales Order Object   : "+jsonStr);
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("Request Body String : " + jsonStr);
			System.out.println("------------------------------------------------------------------------------");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	private void changeSalesOrderServerDownloadStatus(ApiResponeDataFocus authenticateResponse, List<String> ivhPids, Long companyId) {
		System.out.println("Updating Sales Order Download Status");
		if (authenticateResponse != null) {
			System.out.println("Response Status" + authenticateResponse.getiStatus());
			if (authenticateResponse.getiStatus() == 1) {
				System.out.println("please wait......");
				int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(
						TallyDownloadStatus.COMPLETED, ivhPids, companyId);
				log.debug("updated " + updated + " to COMPLETED");
			} else {
				int updated = inventoryVoucherHeaderRepository
						.updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(TallyDownloadStatus.FAILED, ivhPids,companyId);
				log.debug("Failed " + updated + " Failed");
			}
		}

	}

	public static MultiValueMap<String, String> createTokenAuthHeaders() {
		MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<String, String>();
		requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		return requestHeaders;
	}

	private SaleOrderFocusObject getRequestBody(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		log.debug("Enter : getRequestBody() - [" + inventoryVoucherHeaderDTO.toString()+"]");

		List<AccountProfile> accountProfiles = accountProfilerepository.findAllByCompanyId(inventoryVoucherHeaderDTO.getCompanyId());

		List<LengthType> lengthType = lengthTypeRepository.findAll();

		List<ProductProfile> productProfiles = productProfileRepository.findByCompanyId(inventoryVoucherHeaderDTO.getCompanyId());

		SalesOrderMasterFocus salesOrderMasterFocus = new SalesOrderMasterFocus();

		SaleOrderFocusObject saleOrderFocusObject = new SaleOrderFocusObject();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		LocalDateTime localDateTime = inventoryVoucherHeaderDTO.getDocumentDate();

		String date = localDateTime.format(formatter);

		log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() +" : " + inventoryVoucherHeaderDTO.getReceiverAccountPid());

		Optional<AccountProfile> optAccountProfile = accountProfiles.stream().filter(data -> data.getPid().equals(inventoryVoucherHeaderDTO.getReceiverAccountPid())).findAny();

		log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() +" : " +optAccountProfile.isPresent());

		if (optAccountProfile.isPresent()) {

			List<RouteCode> routeCodeList = routeCodeRepository.findAllByAccountId(optAccountProfile.get().getId());
			routeCodeList.forEach(data -> log.debug(data.getAccountProfile().getId() + "==" + data.getAccountProfile().getName() + "==" + data.getMasterCode()));
			log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Route code : "+String.valueOf(routeCodeList.size()));
			salesOrderMasterFocus.setCustomerAc(optAccountProfile.get().getCustomerId());
			Optional<RouteCode> optRouteCode = routeCodeList.stream().filter(data -> data.getAccountProfile().getPid().equalsIgnoreCase(optAccountProfile.get().getPid())).findAny();
			optRouteCode.ifPresent(routeCode -> salesOrderMasterFocus.setRouteCode(routeCode.getMasterCode()));
		}

		log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Reciver Accountname : " + inventoryVoucherHeaderDTO.getReceiverAccountName());

		salesOrderMasterFocus.setdocNo("");
		salesOrderMasterFocus.setDate(date);
		salesOrderMasterFocus.setEmployee(inventoryVoucherHeaderDTO.getEmployeeName());
		salesOrderMasterFocus.setSnrDocId(inventoryVoucherHeaderDTO.getDocumentNumberServer());
		salesOrderMasterFocus.setSnrDocDate(date);
		salesOrderMasterFocus.setNarration("NA");
		salesOrderMasterFocus.setBusinessUnit("EKM");
		salesOrderMasterFocus.setDelivery_Site("NA");
		salesOrderMasterFocus.setFiscalYear("23-24");
		salesOrderMasterFocus.setIsIGST("No");

		List<SalesOrderItemDetailsFocus> salesOrderItems = new ArrayList<>();

		for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO.getInventoryVoucherDetails()) {

			SalesOrderItemDetailsFocus salesOrderItemDetailsFocus = new SalesOrderItemDetailsFocus();
			double quantity = inventoryVoucherDetailDTO.getQuantity();
			Optional<ProductProfile> optProductProfile = productProfiles.stream().filter(data -> data.getPid().equals(inventoryVoucherDetailDTO.getProductPid())).findAny();

			if (optProductProfile.isPresent()) {

				salesOrderItemDetailsFocus.setItem(optProductProfile.get().getProductId());
				salesOrderItemDetailsFocus.setWidth(optProductProfile.get().getWidth());

			}

			salesOrderItemDetailsFocus.setUnit(inventoryVoucherDetailDTO.getProductSKU());
			salesOrderItemDetailsFocus.setBrandDeva("NA");
			salesOrderItemDetailsFocus.setLengthType(inventoryVoucherDetailDTO.getLengthType());
			salesOrderItemDetailsFocus.setMtrConv(0.0);

			log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "LengthType : " + inventoryVoucherDetailDTO.getLengthType());

			if (inventoryVoucherDetailDTO.getLengthType() != null) {

				if (inventoryVoucherDetailDTO.getLengthType().equalsIgnoreCase("IF LENGTH IN FEET AND INCH")) {

					log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " +"Calculating Total By Length In Feet And Inch Values");
					salesOrderItemDetailsFocus.setLengthInFeet(inventoryVoucherDetailDTO.getLengthInFeet());
					salesOrderItemDetailsFocus.setLengthInInch(inventoryVoucherDetailDTO.getLengthInInch());

					Optional<LengthType> optLengthTypeInch = lengthType.stream()
							.filter(data -> data.getMasterCode().equalsIgnoreCase("IF LENGTH IN INCH")).findAny();

					Optional<LengthType> optLengthTypefeet = lengthType.stream()
							.filter(data -> data.getMasterCode().equalsIgnoreCase("Feet")).findAny();

					if (optLengthTypefeet.isPresent()) {

						if (optLengthTypeInch.isPresent()) {
							log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Present Both IF LENGTH IN INCH && Feet ");
							log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Feet meter Conversion :" + optLengthTypefeet.get().getMeterConversion());
							log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Inch meter Conversion :" + optLengthTypeInch.get().getMeterConversion());

							double lengthInMeter = (optLengthTypefeet.get().getMeterConversion()
									* inventoryVoucherDetailDTO.getLengthInFeet())
									+ (optLengthTypeInch.get().getMeterConversion()
											* inventoryVoucherDetailDTO.getLengthInInch());

							log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "LengthInMeter : " + lengthInMeter);

							double totalQuantity = (lengthInMeter * quantity * salesOrderItemDetailsFocus.getWidth());

							log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "TotalQuantity : " + totalQuantity);
							salesOrderItemDetailsFocus.setQuantity(totalQuantity);
						}

					}

				}
				else if (inventoryVoucherDetailDTO.getLengthType().equalsIgnoreCase("Feet")) {

					log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " +"Calculating Total By Length In Feet Value");
					log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Length In Feet : " + inventoryVoucherDetailDTO.getLengthInFeet());

					Optional<LengthType> optLengthType = lengthType.stream()
							.filter(data -> data.getMasterCode().equalsIgnoreCase("Feet")).findAny();

					if (optLengthType.isPresent()) {

						log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Length Type Feet Is Present");

						double lengthInMeter = (optLengthType.get().getMeterConversion()
								* inventoryVoucherDetailDTO.getLengthInFeet());

						log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Length In Meter : " + lengthInMeter);

						double totalQuantity = (lengthInMeter * quantity * salesOrderItemDetailsFocus.getWidth());

						log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Total Quantity : " + totalQuantity);

						salesOrderItemDetailsFocus.setQuantity(totalQuantity);
						salesOrderItemDetailsFocus.setMtrConv(optLengthType.get().getMeterConversion());
					}
					salesOrderItemDetailsFocus.setLengthInFeet(inventoryVoucherDetailDTO.getLengthInFeet());
				}
				else if (inventoryVoucherDetailDTO.getLengthType().equalsIgnoreCase("IF LENGTH IN INCH")) {

					log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " +"Calculating Total By Length In Inch Value");

					salesOrderItemDetailsFocus.setLengthInInch(inventoryVoucherDetailDTO.getLengthInInch());

					Optional<LengthType> optLengthType = lengthType.stream()
							.filter(data -> data.getMasterCode().equalsIgnoreCase("IF LENGTH IN INCH")).findAny();

					if (optLengthType.isPresent()) {

						log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Length Type Inch Is Present");

						double lengthInMeter = (optLengthType.get().getMeterConversion()
								* inventoryVoucherDetailDTO.getLengthInInch());

						log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " +"Length In Meter : " + lengthInMeter);

						double totalQuantity = (lengthInMeter * quantity * salesOrderItemDetailsFocus.getWidth());

						log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " +"Total Quantity : " + totalQuantity);

						salesOrderItemDetailsFocus.setQuantity(totalQuantity);
						salesOrderItemDetailsFocus.setMtrConv(optLengthType.get().getMeterConversion());
					}

				} else if (inventoryVoucherDetailDTO.getLengthType().equalsIgnoreCase("mtr")) {

					log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " +"Calculating Total By Meter Value");

					Optional<LengthType> optLengthType = lengthType.stream()
							.filter(data -> data.getMasterCode().equalsIgnoreCase("mtr")).findAny();

					salesOrderItemDetailsFocus.setLength(inventoryVoucherDetailDTO.getLengthInInch());

					if (optLengthType.isPresent()) {

						log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Length Type Meter Is Present");

						double lengthInMeter = (optLengthType.get().getMeterConversion()
								* inventoryVoucherDetailDTO.getLengthInInch());

						log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Length In Meter : " + lengthInMeter);

						double totalQuantity = (lengthInMeter * quantity * salesOrderItemDetailsFocus.getWidth());

						log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " +"Total Quantity  : " + totalQuantity);

						salesOrderItemDetailsFocus.setQuantity(totalQuantity);
						salesOrderItemDetailsFocus.setMtrConv(optLengthType.get().getMeterConversion());
					}
				} else {
					log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : " + "Length Type Number Is Present");
					salesOrderItemDetailsFocus.setLengthInFeet(0.0);
					salesOrderItemDetailsFocus.setLengthInInch(0.0);
					salesOrderItemDetailsFocus.setLength(inventoryVoucherDetailDTO.getLengthInInch());
					salesOrderItemDetailsFocus.setQuantity(0);
					salesOrderItemDetailsFocus.setMtrConv(1);
				}
			}
			salesOrderItemDetailsFocus.setAltQty(quantity);
			salesOrderItemDetailsFocus.setUnitConversion(0.0);
			salesOrderItemDetailsFocus.setSalesQty(0.0);
			salesOrderItemDetailsFocus.setMtrPcs(0.0);
			salesOrderItemDetailsFocus.setSellingRateInclTax(inventoryVoucherDetailDTO.getSellingRate());
			salesOrderItems.add(salesOrderItemDetailsFocus);
		}

		log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : Product Details : " + salesOrderItems.toString());
		log.debug(inventoryVoucherHeaderDTO.getDocumentNumberLocal() + " : Product Volume : " + String.valueOf(salesOrderItems.size()));

		saleOrderFocusObject.setSalesOrderMasterFocus(salesOrderMasterFocus);
		saleOrderFocusObject.setSalesOrderItemDetailsFocus(salesOrderItems);
		return saleOrderFocusObject;
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
			e.printStackTrace();
		}

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.debug("Authentication URL: " + AUTHENTICATE_API_URL);

		try {
			AuthenticationResponseFocus authenticationResponseFocus = restTemplate.postForObject(AUTHENTICATE_API_URL,
					entity, AuthenticationResponseFocus.class);

			Obj = new ObjectMapper();

			String jsonStr1;
			try {
				jsonStr1 = Obj.writeValueAsString(authenticationResponseFocus);
				log.info("Authentication Response Body: -" + jsonStr1);

			} catch (JsonProcessingException e) {
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
		authenticationRequstFocus.setCompanyCode("070");

		return authenticationRequstFocus;
	}
}
