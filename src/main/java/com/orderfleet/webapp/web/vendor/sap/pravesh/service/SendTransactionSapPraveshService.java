package com.orderfleet.webapp.web.vendor.sap.pravesh.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.UnitOfMeasureProduct;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherDetailRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureProductRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.ApiResponseDataSapPravesh;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.ReceiptSapPravesh;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.SalesOrderItemDetailsSapPravesh;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.SalesOrderMasterSapPravesh;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class SendTransactionSapPraveshService {

	private static String API_URL_SALES_ORDER = "http://59.94.176.87:5002/Sales/SalesOrder";

	private static String API_URL_RECIPTS = "http://59.94.176.87:5002/Receipt/Receipts";

	private static String AUTHENTICATION_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEzRjhFREM2QjJCNTU3OUQ0MEVGNDg1QkNBOUNFRDBBIiwidHlwIjoiYXQrand0In0.eyJuYmYiOjE2MDExMDg1MzMsImV4cCI6MTYzMjY0NDUzMywiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo1MDAwIiwiYXVkIjoiQ3VzdG9tZXJTZXJ2aWNlLkFwaSIsImNsaWVudF9pZCI6Im5hc19jbGllbnQiLCJzdWIiOiIwNWEwNzliMC1jZjBiLTQ2ZjctYThlMy1iODk4MjIwODgzMjQiLCJhdXRoX3RpbWUiOjE2MDExMDg1MzMsImlkcCI6ImxvY2FsIiwic2VydmljZS51c2VyIjoiYWRtaW4iLCJqdGkiOiIyOUU0OTRERTg1QjA0RTdBNUM1NjM3NDhCQzIyOTEyRSIsInNpZCI6IkFDOTE4QzNEMkY3MUIzRTRBMERGQzc2MDQ4QzJBMEUzIiwiaWF0IjoxNjAxMTA4NTMzLCJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwibmFzLmNsaWVudCIsIm5hcy5zZXJ2aWNlcyJdLCJhbXIiOlsicHdkIl19.x4knTyLtPEwUSnc35EnWSyxINwOLU5YTBeCD_eXDkXmMC1bWQclkdLH18Dgict07qVWyRL9EcYT66j4p7hsUGbZrZP9TLeNpQP5BT6eRSeYvkf2lmvJe1xaCvPYrHpPGvApLJJAmQxCwex7AAW74zJLpl_SdNUf3AHBkBvjr2ibEkDBgRgOTO0Z3n3f43ZxZw3LAi_x8ZRSxITY0mpevTUpDhx2pv5-ehXe7BaCbTxAJ6dBvkAavtmB-W3wp7cnJqSfr2mFpsBzE_Ek_OzAFnu_N1ALi8yE9LpuAPSDj4hVz11i98urPebHA8lEca1yBAPI6goQlKJEB4_NXI5F8CA";

	private final Logger log = LoggerFactory.getLogger(SendTransactionSapPraveshService.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	public static int successCount = 0;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@Inject
	private UnitOfMeasureProductRepository unitOfMeasureProductRepository;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherDetailRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Transactional
	public void sendSalesOrder() {
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		log.debug("REST request to download sales orders <" + company.getLegalName() + "> : {}");

		List<SalesOrderMasterSapPravesh> salesOrders = new ArrayList<>();
//		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
//		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES_ORDER,
//				company.getId());
//		
		List<PrimarySecondaryDocument> primarySecDocPrimarySales = new ArrayList<>();
		List<PrimarySecondaryDocument> primarySecDocSecondarySales = new ArrayList<>();
		primarySecDocPrimarySales = primarySecondaryDocumentRepository
				.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES_ORDER, companyId);
		primarySecDocSecondarySales = primarySecondaryDocumentRepository
				.findByVoucherTypeAndCompany(VoucherType.SECONDARY_SALES_ORDER, companyId);

		if (primarySecDocPrimarySales.isEmpty()) {
			log.info("........No PrimarySecondaryDocument configuration Available...........");
			// return salesOrderDTOs;
		}
		List<Long> primarySalesDocumentIdList = primarySecDocPrimarySales.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		List<Long> secondarySalesDocumentIdList = primarySecDocSecondarySales.stream()
				.map(psd -> psd.getDocument().getId()).collect(Collectors.toList());

		List<Long> documentIdList = new ArrayList<>();

		documentIdList.addAll(primarySalesDocumentIdList);
		documentIdList.addAll(secondarySalesDocumentIdList);

		List<String> inventoryHeaderPid = new ArrayList<String>();
		String companyPid = company.getPid();

		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);

		List<Object[]> inventoryVoucherHeaders = new ArrayList<>();

		if (optSalesManagement.isPresent() && optSalesManagement.get().getValue().equalsIgnoreCase("true")) {
			// inventoryVoucherHeaders =
			// inventoryVoucherHeaderRepository.findByCompanyIdAndTallyStatusAndSalesManagementStatusOrderByCreatedDateDesc();
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_192" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by CompanyId and TallyStatus and salesManagement status Document";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndSalesManagementStatusAndDocumentOrderByCreatedDateDesc(
							documentIdList);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
		} else {
			// inventoryVoucherHeaders =
			// inventoryVoucherHeaderRepository.findByCompanyIdAndTallyStatusOrderByCreatedDateDesc();
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_191" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by CompanyId and TallyStatus and Document";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndDocumentOrderByCreatedDateDesc(documentIdList);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
		}
		log.debug("IVH size : {}", inventoryVoucherHeaders.size());

		if (inventoryVoucherHeaders.size() > 0) {

			Set<Long> ivhIds = new HashSet<>();
			Set<String> ivhPids = new HashSet<>();
			Set<Long> documentIds = new HashSet<>();
			Set<Long> employeeIds = new HashSet<>();
			Set<Long> receiverAccountProfileIds = new HashSet<>();
			Set<Long> supplierAccountProfileIds = new HashSet<>();
			Set<Long> priceLeveIds = new HashSet<>();
			Set<Long> userIds = new HashSet<>();
			Set<Long> orderStatusIds = new HashSet<>();
			Set<Long> exeIds = new HashSet<>();

			for (Object[] obj : inventoryVoucherHeaders) {

				ivhIds.add(Long.parseLong(obj[0].toString()));
				ivhPids.add(obj[9].toString());
				userIds.add(Long.parseLong(obj[12].toString()));
				documentIds.add(Long.parseLong(obj[13].toString()));
				employeeIds.add(Long.parseLong(obj[14].toString()));
				exeIds.add(Long.parseLong(obj[15].toString()));
				receiverAccountProfileIds.add(Long.parseLong(obj[16].toString()));
				supplierAccountProfileIds.add(Long.parseLong(obj[17].toString()));
				priceLeveIds.add(obj[18] != null ? Long.parseLong(obj[18].toString()) : 0);
				orderStatusIds.add(obj[23] != null ? Long.parseLong(obj[23].toString()) : 0);

			}
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "AP_QUERY_137" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get all by compId and IdsIn";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			List<AccountProfile> receiverAccountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(receiverAccountProfileIds);
			String flag1 = "Normal";
			LocalDateTime endLCTime1 = LocalDateTime.now();
			String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
			String endDate1 = startLCTime1.format(DATE_FORMAT1);
			Duration duration1 = Duration.between(startLCTime1, endLCTime1);
			long minutes1 = duration1.toMinutes();
			if (minutes1 <= 1 && minutes1 >= 0) {
				flag1 = "Fast";
			}
			if (minutes1 > 1 && minutes1 <= 2) {
				flag1 = "Normal";
			}
			if (minutes1 > 2 && minutes1 <= 10) {
				flag1 = "Slow";
			}
			if (minutes1 > 10) {
				flag1 = "Dead Slow";
			}
	                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description1);
			List<AccountProfile> supplierAccountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(supplierAccountProfileIds);

			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);

			List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdAndIdsIn(exeIds);

			List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
					.findAllByInventoryVoucherHeaderPidIn(ivhPids);
			List<UserStockLocation> userStockLocations = userStockLocationRepository.findAllByCompanyPid(companyPid);
			List<UnitOfMeasureProduct> unitOfMeasureProducts = unitOfMeasureProductRepository.findAllByCompanyId();

			Object[] errorPrint = null;
			try {
				for (Object[] obj : inventoryVoucherHeaders) {

					Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[12].toString()))
							.findAny();

					Optional<ExecutiveTaskExecution> opExe = executiveTaskExecutions.stream()
							.filter(doc -> doc.getId() == Long.parseLong(obj[15].toString())).findAny();

					Optional<AccountProfile> opRecAccPro = receiverAccountProfiles.stream()
							.filter(a -> a.getId() == Long.parseLong(obj[16].toString())).findAny();

					Optional<AccountProfile> opSupAccPro = supplierAccountProfiles.stream()
							.filter(a -> a.getId() == Long.parseLong(obj[17].toString())).findAny();

					Optional<UserStockLocation> opUserStockLocation = userStockLocations.stream()
							.filter(us -> us.getUser().getPid().equals(opUser.get().getPid())).findAny();

					SalesOrderMasterSapPravesh salesOrder = new SalesOrderMasterSapPravesh();

					Optional<Long> opPrimarySales = primarySalesDocumentIdList.stream()
							.filter(id -> id == Long.parseLong(obj[13].toString())).findAny();

					Optional<Long> opSecondarySales = secondarySalesDocumentIdList.stream()
							.filter(id -> id == Long.parseLong(obj[13].toString())).findAny();

					if (opPrimarySales.isPresent()) {
						salesOrder.setCustomerCode(opRecAccPro.get().getCustomerId()); // Primary key id of
																						// account
						// profile
						salesOrder.setCustomerName(opRecAccPro.get().getName());

						salesOrder.setCustomerAddr(opRecAccPro.get().getAddress());
					}

					if (opSecondarySales.isPresent()) {
						salesOrder.setCustomerCode(String.valueOf(opRecAccPro.get().getId())); // Primary key id of
																								// account
						// profile
						salesOrder.setCustomerName(opRecAccPro.get().getName());

						salesOrder.setCustomerAddr(opRecAccPro.get().getAddress());

					}

					salesOrder
							.setDealerCode(opSupAccPro.get().getCustomerId() != null ? opSupAccPro.get().getCustomerId()
									: "No Customer Id");

					salesOrder.setDealerName(opSupAccPro.get().getName());

					LocalDateTime date = null;
					if (obj[4] != null) {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						String[] splitDate = obj[4].toString().split(" ");
						date = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);
					}

					DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

					salesOrder.setDocDate(date.format(formatter1));
					salesOrder.setDocNum(obj[6].toString());
					salesOrder.setRefNo(obj[6].toString());

					salesOrder.setRemarks(opExe.get().getRemarks());

					List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetails.stream()
							.filter(ivd -> ivd.getInventoryVoucherHeader().getId() == Long.parseLong(obj[0].toString()))
							.collect(Collectors.toList()).stream()
							.sorted(Comparator.comparingLong(InventoryVoucherDetail::getId))
							.collect(Collectors.toList());

					int i = 1;
					List<SalesOrderItemDetailsSapPravesh> salesOrderDetails = new ArrayList<SalesOrderItemDetailsSapPravesh>();
					for (InventoryVoucherDetail inventoryVoucherDetail : ivDetails) {

						SalesOrderItemDetailsSapPravesh salesOrderDetail = new SalesOrderItemDetailsSapPravesh();

						salesOrderDetail.setItemCode(inventoryVoucherDetail.getProduct().getName());
						salesOrderDetail.setItemName(inventoryVoucherDetail.getProduct().getAlias() != null
								&& !inventoryVoucherDetail.getProduct().getAlias().equals("")
										? inventoryVoucherDetail.getProduct().getAlias()
										: inventoryVoucherDetail.getProduct().getName());
						salesOrderDetail.setLineNo(i);

						double quantity = Double.valueOf(inventoryVoucherDetail.getQuantity()) != null
								? inventoryVoucherDetail.getQuantity()
								: 0.0;
						double sellingRate = Double.valueOf(inventoryVoucherDetail.getSellingRate()) != null
								? inventoryVoucherDetail.getSellingRate()
								: 0.0;

						salesOrderDetail.setQuantity(quantity);

						salesOrderDetail.setTotalAmount(quantity * sellingRate);
						salesOrderDetail.setUnitPrice(sellingRate);

						salesOrderDetails.add(salesOrderDetail);

						i++;
					}

					inventoryHeaderPid.add(obj[9].toString());
					salesOrder.setSalesOrderDetails(salesOrderDetails);

					salesOrders.add(salesOrder);
				}
			} catch (NoSuchElementException | NullPointerException | ArrayIndexOutOfBoundsException e) {
				int i = 0;
				log.info("Error Printng");
				for (Object ob : errorPrint) {
					log.info(i + "-");
					if (ob != null) {
						log.info("--" + ob.toString());
					}
					i++;
				}
				e.printStackTrace();
				throw new IllegalArgumentException("Data missing in sales order..");
			}
			if (!salesOrders.isEmpty()) {
				 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "INV_QUERY_161" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="update InvVoucherHeader TallyDownloadStatus Using Pid";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
						TallyDownloadStatus.PROCESSING, inventoryHeaderPid);
				 String flag = "Normal";
					LocalDateTime endLCTime = LocalDateTime.now();
					String endTime = endLCTime.format(DATE_TIME_FORMAT);
					String endDate = startLCTime.format(DATE_FORMAT);
					Duration duration = Duration.between(startLCTime, endLCTime);
					long minutes = duration.toMinutes();
					if (minutes <= 1 && minutes >= 0) {
						flag = "Fast";
					}
					if (minutes > 1 && minutes <= 2) {
						flag = "Normal";
					}
					if (minutes > 2 && minutes <= 10) {
						flag = "Slow";
					}
					if (minutes > 10) {
						flag = "Dead Slow";
					}
			                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
							+ description);
				log.debug("updated " + updated + " to PROCESSING");
			}
		}
		log.info("Sending (" + salesOrders.size() + ") SalesOrder to SAP....");

		if (salesOrders.size() < 1) {
			return;
		}

		HttpEntity<List<SalesOrderMasterSapPravesh>> entity = new HttpEntity<>(salesOrders, createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		ObjectMapper Obj = new ObjectMapper();

		// get Oraganisation object as a json string
		String jsonStr;
		try {
			jsonStr = Obj.writeValueAsString(entity.getBody());
			log.info(jsonStr);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + API_URL_SALES_ORDER);

		try {

			// Void authenticateResponse = restTemplate.postForObject(API_URL_SALES_ORDER,
			// entity, Void.class);

//			ApiResponseDataSapPravesh authenticateResponse = restTemplate.postForObject(API_URL_SALES_ORDER, entity,
//					ApiResponseDataSapPravesh.class);

			ResponseEntity<List<ApiResponseDataSapPravesh>> authenticateResponse = restTemplate.exchange(
					API_URL_SALES_ORDER, HttpMethod.POST, entity,
					new ParameterizedTypeReference<List<ApiResponseDataSapPravesh>>() {
					});

			// new ParameterizedTypeReference<List<BitPay>>(){})

			log.info("Sap Sales Order Created Success Size" + "----"
					+ authenticateResponse.getBody().get(0).getId().size());

			changeSalesOrderServerDownloadStatus(authenticateResponse.getBody().get(0));

//			log.info("Odoo Invoice Created Success Size= " + responseBodyOdooInvoice.getResult().getMessage().size()
//					+ "------------");
//
//			changeServerDownloadStatus(responseBodyOdooInvoice.getResult().getMessage());

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

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	public static MultiValueMap<String, String> createTokenAuthHeaders() {
		MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<String, String>();
		requestHeaders.add("Authorization", "Bearer " + AUTHENTICATION_TOKEN);
		requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		return requestHeaders;
	}

	private void changeSalesOrderServerDownloadStatus(ApiResponseDataSapPravesh authenticateResponse) {

		if (authenticateResponse != null) {

			if (authenticateResponse.getStatus().equals("Success")) {

				if (authenticateResponse.getId().size() > 0) {
					List<String> inventoryHeaderPids = new ArrayList<>();

					for (String id : authenticateResponse.getId()) {
						inventoryHeaderPids.add(id);
					}

					List<InventoryVoucherHeader> successInventoryHeaders = new ArrayList<>();
					List<InventoryVoucherHeader> successdistinctElements = new ArrayList<>();

					if (inventoryHeaderPids.size() > 0) {
						DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String id = "INV_QUERY_200" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String description ="Find all headers by documnetNumberServer";
						LocalDateTime startLCTime = LocalDateTime.now();
						String startTime = startLCTime.format(DATE_TIME_FORMAT);
						String startDate = startLCTime.format(DATE_FORMAT);
						logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
						successInventoryHeaders = inventoryVoucherHeaderRepository
								.findAllHeaderdByDocumentNumberServer(inventoryHeaderPids);
						String flag = "Normal";
						LocalDateTime endLCTime = LocalDateTime.now();
						String endTime = endLCTime.format(DATE_TIME_FORMAT);
						String endDate = startLCTime.format(DATE_FORMAT);
						Duration duration = Duration.between(startLCTime, endLCTime);
						long minutes = duration.toMinutes();
						if (minutes <= 1 && minutes >= 0) {
							flag = "Fast";
						}
						if (minutes > 1 && minutes <= 2) {
							flag = "Normal";
						}
						if (minutes > 2 && minutes <= 10) {
							flag = "Slow";
						}
						if (minutes > 10) {
							flag = "Dead Slow";
						}
				                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
								+ description);

						successdistinctElements = successInventoryHeaders.stream().distinct()
								.collect(Collectors.toList());
					}
					log.info("Success Sales Order :----" + successInventoryHeaders.size());
					List<InventoryVoucherHeader> updatedList = new ArrayList<>();
					successCount = 0;
					for (InventoryVoucherHeader ivh : successdistinctElements) {

						InventoryVoucherHeader newIvh = ivh;

						authenticateResponse.getId().stream()
								.filter(a -> a.equalsIgnoreCase(ivh.getDocumentNumberServer())).findAny()
								.ifPresent(a -> {

									newIvh.setTallyDownloadStatus(TallyDownloadStatus.COMPLETED);
									updatedList.add(newIvh);
									successCount++;
								});
					}
					inventoryVoucherHeaderRepository.save(updatedList);

					log.debug("updated " + successCount + " to Completed");
//					int updated = inventoryVoucherHeaderRepository
//							.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(TallyDownloadStatus.COMPLETED,
//									inventoryHeaderPids);
//				
//					log.debug("updated " + updated + " to Completed");
				}
			}
		}

	}

	private void changeReceiptServerDownloadStatus(ApiResponseDataSapPravesh authenticateResponse) {

		if (authenticateResponse != null) {

			if (authenticateResponse.getStatus().equals("Success")) {

				if (authenticateResponse.getId().size() > 0) {
					List<String> accountingHeaderPids = new ArrayList<>();

					for (String id : authenticateResponse.getId()) {
						accountingHeaderPids.add(id);
					}

					List<AccountingVoucherHeader> successAccountingVoucherHeaders = new ArrayList<>();
					List<AccountingVoucherHeader> successdistinctElements = new ArrayList<>();

					if (accountingHeaderPids.size() > 0) {
						 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
							DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
							String id = "ACC_QUERY_159" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
							String description ="get all header by document number server";
							LocalDateTime startLCTime = LocalDateTime.now();
							String startTime = startLCTime.format(DATE_TIME_FORMAT);
							String startDate = startLCTime.format(DATE_FORMAT);
							logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
						successAccountingVoucherHeaders = accountingVoucherHeaderRepository
								.findAllHeaderdByDocumentNumberServer(accountingHeaderPids);
						 String flag = "Normal";
							LocalDateTime endLCTime = LocalDateTime.now();
							String endTime = endLCTime.format(DATE_TIME_FORMAT);
							String endDate = startLCTime.format(DATE_FORMAT);
							Duration duration = Duration.between(startLCTime, endLCTime);
							long minutes = duration.toMinutes();
							if (minutes <= 1 && minutes >= 0) {
								flag = "Fast";
							}
							if (minutes > 1 && minutes <= 2) {
								flag = "Normal";
							}
							if (minutes > 2 && minutes <= 10) {
								flag = "Slow";
							}
							if (minutes > 10) {
								flag = "Dead Slow";
							}
					                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
									+ description);
						successdistinctElements = successAccountingVoucherHeaders.stream().distinct()
								.collect(Collectors.toList());
					}
					log.info("Success Receipts :----" + successAccountingVoucherHeaders.size());
					List<AccountingVoucherHeader> updatedList = new ArrayList<>();
					successCount = 0;
					for (AccountingVoucherHeader avh : successdistinctElements) {

						AccountingVoucherHeader newAvh = avh;

						authenticateResponse.getId().stream()
								.filter(a -> a.equalsIgnoreCase(avh.getDocumentNumberServer())).findAny()
								.ifPresent(a -> {

									newAvh.setTallyDownloadStatus(TallyDownloadStatus.COMPLETED);
									updatedList.add(newAvh);
									successCount++;
								});
					}
					accountingVoucherHeaderRepository.save(updatedList);

					log.debug("updated " + successCount + " to Completed");

//					int updated = accountingVoucherHeaderRepository
//							.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(
//									TallyDownloadStatus.COMPLETED, accountingHeaderPids);
//					accountingVoucherHeaderRepository.flush();
//					log.debug("updated " + updated + " to Completed");
				}
			}
		}

	}

	@Transactional
	public void sendReceipt() {
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		log.debug("REST request to download receipts <" + company.getLegalName() + "> : {}");

		List<ReceiptSapPravesh> receiptDTOs = new ArrayList<>();

		String companyPid = company.getPid();

		Optional<CompanyConfiguration> optReceiptsManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.RECEIPT_MANAGEMENT);

		List<Object[]> accountingVoucherHeaders = new ArrayList<>();

		if (optReceiptsManagement.isPresent() && optReceiptsManagement.get().getValue().equalsIgnoreCase("true")) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACC_QUERY_156" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by companyId and tally status and sales management status";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountingVoucherHeaders = accountingVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndSalesManagementStatusByCreatedDateDesc();
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
		} else {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "ACC_QUERY_155" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get by companyId and tally status";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountingVoucherHeaders = accountingVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusByCreatedDateDesc();
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
		}

		log.info("Accounting Voucher Header size {} ", accountingVoucherHeaders.size());

		List<String> accountingPids = new ArrayList<>();

		if (accountingVoucherHeaders.size() > 0) {

			Set<Long> avhIds = new HashSet<>();
			Set<String> avhPids = new HashSet<>();
			Set<Long> documentIds = new HashSet<>();
			Set<Long> employeeIds = new HashSet<>();
			Set<Long> accountProfileIds = new HashSet<>();
			Set<Long> userIds = new HashSet<>();
			Set<Long> exeIds = new HashSet<>();

			for (Object[] obj : accountingVoucherHeaders) {

				avhIds.add(Long.parseLong(obj[0].toString()));
				avhPids.add(obj[1].toString());
				userIds.add(Long.parseLong(obj[10].toString()));
				documentIds.add(Long.parseLong(obj[3].toString()));
				employeeIds.add(Long.parseLong(obj[11].toString()));
				exeIds.add(Long.parseLong(obj[2].toString()));
				accountProfileIds.add(Long.parseLong(obj[4].toString()));

			}

			List<Document> documents = documentRepository.findAllByCompanyIdAndIdsIn(documentIds);
			List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyIdAndIdsIn(employeeIds);
			List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdAndIdsIn(exeIds);
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "AP_QUERY_137" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get all by compId and IdsIn";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			List<AccountProfile> accountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(accountProfileIds);
			String flag1 = "Normal";
			LocalDateTime endLCTime1 = LocalDateTime.now();
			String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
			String endDate1 = startLCTime1.format(DATE_FORMAT1);
			Duration duration1 = Duration.between(startLCTime1, endLCTime1);
			long minutes1 = duration1.toMinutes();
			if (minutes1 <= 1 && minutes1 >= 0) {
				flag1 = "Fast";
			}
			if (minutes1 > 1 && minutes1 <= 2) {
				flag1 = "Normal";
			}
			if (minutes1 > 2 && minutes1 <= 10) {
				flag1 = "Slow";
			}
			if (minutes1 > 10) {
				flag1 = "Dead Slow";
			}
	                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description1);
			List<AccountProfile> supplierAccountProfiles = new ArrayList<>();

			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);
			DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id11 = "AVD_QUERY_111" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description11 ="get all by accVoucherHeaderPidIn";
			LocalDateTime startLCTime11 = LocalDateTime.now();
			String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
			String startDate11 = startLCTime11.format(DATE_FORMAT11);
			logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
			List<AccountingVoucherDetail> accountingVoucherDetails = accountingVoucherDetailRepository
					.findAllByAccountingVoucherHeaderPidIn(avhPids);
			 String flag11 = "Normal";
				LocalDateTime endLCTime11 = LocalDateTime.now();
				String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
				String endDate11 = startLCTime11.format(DATE_FORMAT11);
				Duration duration11 = Duration.between(startLCTime11, endLCTime11);
				long minutes11 = duration11.toMinutes();
				if (minutes11 <= 1 && minutes11 >= 0) {
					flag11 = "Fast";
				}
				if (minutes11 > 1 && minutes11 <= 2) {
					flag11 = "Normal";
				}
				if (minutes11 > 2 && minutes11 <= 10) {
					flag11 = "Slow";
				}
				if (minutes11 > 10) {
					flag11 = "Dead Slow";
				}
		                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
						+ description11);
			int i = 1;
			for (Object[] obj : accountingVoucherHeaders) {

				String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";

				Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[10].toString()))
						.findAny();

				Optional<Document> opDocument = documents.stream()
						.filter(doc -> doc.getId() == Long.parseLong(obj[3].toString())).findAny();

				Optional<EmployeeProfile> opEmployeeProfile = employeeProfiles.stream()
						.filter(emp -> emp.getId() == Long.parseLong(obj[11].toString())).findAny();

				Optional<ExecutiveTaskExecution> opExe = executiveTaskExecutions.stream()
						.filter(doc -> doc.getId() == Long.parseLong(obj[2].toString())).findAny();

				Optional<AccountProfile> opAccPro = accountProfiles.stream()
						.filter(a -> a.getId() == Long.parseLong(obj[4].toString())).findAny();

				List<AccountingVoucherDetail> avDetails = accountingVoucherDetails.stream()
						.filter(ivd -> ivd.getAccountingVoucherHeader().getId() == Long.parseLong(obj[0].toString()))
						.collect(Collectors.toList()).stream()
						.sorted(Comparator.comparingLong(AccountingVoucherDetail::getId)).collect(Collectors.toList());

				for (AccountingVoucherDetail accountingVoucherDetail : avDetails) {
					if (accountingVoucherDetail.getAccountingVoucherAllocations().isEmpty()) {
						ReceiptSapPravesh receiptDTO = new ReceiptSapPravesh();

						receiptDTO.setAmount(accountingVoucherDetail.getAmount());
						receiptDTO.setCustomerCode(String.valueOf(opAccPro.get().getId()));// primary key id of account
																							// profile

						if (accountingVoucherDetail.getTo() != null) {
							receiptDTO.setDealerCode(accountingVoucherDetail.getTo().getCustomerId());
							receiptDTO.setDealerName(accountingVoucherDetail.getTo().getName());
						} else {
							receiptDTO.setDealerCode("No Dealer");
							receiptDTO.setDealerName("No Dealer");
						}

						receiptDTO.setOrderNo("No Order Allocation");

						receiptDTO.setCustomerName(opAccPro.get().getName());
						receiptDTO.setPayMode(accountingVoucherDetail.getMode().toString());
						receiptDTO.setPayRefNo(accountingVoucherDetail.getInstrumentNumber());

						LocalDateTime date = accountingVoucherDetail.getInstrumentDate();

						DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

						receiptDTO.setReceiptDate(date.format(formatter1));

						receiptDTO.setReceiptNo(i);

						receiptDTO.setRefNo(
								accountingVoucherDetail.getAccountingVoucherHeader().getDocumentNumberServer());
						receiptDTO.setRemarks(accountingVoucherDetail.getRemarks());
						receiptDTO.setSalesMan(opEmployeeProfile.get().getName());

						receiptDTOs.add(receiptDTO);
						i++;
					} else {
						for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
								.getAccountingVoucherAllocations()) {

							ReceiptSapPravesh receiptDTO = new ReceiptSapPravesh();

							receiptDTO.setAmount(accountingVoucherAllocation.getAccountingVoucherDetail().getAmount());
							receiptDTO.setCustomerCode(String.valueOf(opAccPro.get().getId())); // primary key id of
																								// account profile
							receiptDTO.setCustomerName(opAccPro.get().getName());

							if (accountingVoucherDetail.getTo() != null) {
								receiptDTO.setDealerCode(accountingVoucherDetail.getTo().getCustomerId());
								receiptDTO.setDealerName(accountingVoucherDetail.getTo().getName());
							} else {
								receiptDTO.setDealerCode("No Dealer");
								receiptDTO.setDealerName("No Dealer");
							}

							receiptDTO.setOrderNo(accountingVoucherAllocation.getReferenceDocumentNumber() != null
									? accountingVoucherAllocation.getReferenceDocumentNumber()
									: "No Order Reference");

							receiptDTO.setPayMode(
									accountingVoucherAllocation.getAccountingVoucherDetail().getMode().toString());
							receiptDTO.setPayRefNo(
									accountingVoucherAllocation.getAccountingVoucherDetail().getInstrumentNumber());

							LocalDateTime date = accountingVoucherAllocation.getAccountingVoucherDetail()
									.getInstrumentDate();

							DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

							receiptDTO.setReceiptDate(date.format(formatter1));

							receiptDTO.setReceiptNo(i);

							receiptDTO.setRefNo(accountingVoucherAllocation.getAccountingVoucherDetail()
									.getAccountingVoucherHeader().getDocumentNumberServer());
							receiptDTO
									.setRemarks(accountingVoucherAllocation.getAccountingVoucherDetail().getRemarks());
							receiptDTO.setSalesMan(opEmployeeProfile.get().getName());

							receiptDTOs.add(receiptDTO);
							i++;
						}
					}
					accountingPids.add(obj[1].toString());
				}

			}
		}

		if (!receiptDTOs.isEmpty()) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "ACC_QUERY_149" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="Updating AccVoucher Tally download status using  pid and company";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			int updated = accountingVoucherHeaderRepository
					.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING,
							accountingPids);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
			log.debug("updated " + updated + " to PROCESSING");
		}

		log.info("Sending (" + receiptDTOs.size() + ") Receipts to SAP....");

		if (receiptDTOs.size() < 1) {
			return;
		}

		HttpEntity<List<ReceiptSapPravesh>> entity = new HttpEntity<>(receiptDTOs, createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		ObjectMapper Obj = new ObjectMapper();

		// get Oraganisation object as a json string
		String jsonStr;
		try {
			jsonStr = Obj.writeValueAsString(entity.getBody());
			log.info(jsonStr);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + API_URL_RECIPTS);

		try {

//			ApiResponseDataSapPravesh authenticateResponse = restTemplate.postForObject(API_URL_RECIPTS, entity,
//					ApiResponseDataSapPravesh.class);

			ResponseEntity<List<ApiResponseDataSapPravesh>> authenticateResponse = restTemplate.exchange(
					API_URL_RECIPTS, HttpMethod.POST, entity,
					new ParameterizedTypeReference<List<ApiResponseDataSapPravesh>>() {
					});

			// new ParameterizedTypeReference<List<BitPay>>(){})

			// log.info("Sap Sales Order Created Success Size" + "----" +
			// authenticateResponse.getBody().get(0).getId().size());

			// changeSalesOrderServerDownloadStatus(authenticateResponse.getBody().get(0));

			log.info(
					"Sap Receipt Created Success Size" + "----" + authenticateResponse.getBody().get(0).getId().size());
//			log.info("Odoo Invoice Created Success Size= " + responseBodyOdooInvoice.getResult().getMessage().size()
//					+ "------------");
//
			changeReceiptServerDownloadStatus(authenticateResponse.getBody().get(0));

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

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
		// return receiptDTOs;

	}

}
