package com.orderfleet.webapp.web.vendor.sap.pravesh.service;

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
import java.util.stream.Collectors;

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
import com.orderfleet.webapp.repository.GstLedgerRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureProductRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.ExecutiveTaskSubmissionService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.ReceiptDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooInvoiceLine;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseMessageOdooInvoice;
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
		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES_ORDER,
				company.getId());

		if (primarySecDoc.isEmpty()) {
			log.info("........No PrimarySecondaryDocument configuration Available...........");
			// return salesOrderDTOs;
		}
		List<Long> documentIdList = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		List<String> inventoryHeaderPid = new ArrayList<String>();
		String companyPid = company.getPid();

		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);

		List<Object[]> inventoryVoucherHeaders = new ArrayList<>();

		if (optSalesManagement.isPresent() && optSalesManagement.get().getValue().equalsIgnoreCase("true")) {
			// inventoryVoucherHeaders =
			// inventoryVoucherHeaderRepository.findByCompanyIdAndTallyStatusAndSalesManagementStatusOrderByCreatedDateDesc();

			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndSalesManagementStatusAndDocumentOrderByCreatedDateDesc(
							documentIdList);
		} else {
			// inventoryVoucherHeaders =
			// inventoryVoucherHeaderRepository.findByCompanyIdAndTallyStatusOrderByCreatedDateDesc();

			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndDocumentOrderByCreatedDateDesc(documentIdList);
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

			List<AccountProfile> receiverAccountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(receiverAccountProfileIds);

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

					salesOrder.setCustomerCode(String.valueOf(opRecAccPro.get().getId())); // Primary key id of account
																							// profile
					salesOrder.setCustomerName(opRecAccPro.get().getName());

					salesOrder.setCustomerAddr(opRecAccPro.get().getAddress());

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
				int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
						TallyDownloadStatus.PROCESSING, inventoryHeaderPid);
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
						successInventoryHeaders = inventoryVoucherHeaderRepository
								.findAllHeaderdByDocumentNumberServer(inventoryHeaderPids);
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
						successAccountingVoucherHeaders = accountingVoucherHeaderRepository
								.findAllHeaderdByDocumentNumberServer(accountingHeaderPids);
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

		List<Object[]> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findByCompanyIdAndTallyStatusByCreatedDateDesc();

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
			List<AccountProfile> accountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(accountProfileIds);
			List<AccountProfile> supplierAccountProfiles = new ArrayList<>();

			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);
			List<AccountingVoucherDetail> accountingVoucherDetails = accountingVoucherDetailRepository
					.findAllByAccountingVoucherHeaderPidIn(avhPids);

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
			int updated = accountingVoucherHeaderRepository
					.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING,
							accountingPids);
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
