package com.orderfleet.webapp.web.vendor.sap.prabhu.service;

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
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
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
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.ApiResponseDataSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderItemDetailsSap;
import com.orderfleet.webapp.web.vendor.sap.prabhu.dto.SalesOrderMasterSap;
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
public class SendSalesOrderSapService {

	private static String API_URL_SALES_ORDER = "http://59.94.176.87:5002/Sales/SalesOrder_Tiscon";

	private static String API_URL_RECIPTS = "http://59.94.176.87:5002/Receipt/Receipts";

	// private static String AUTHENTICATION_TOKEN =
	// "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEzRjhFREM2QjJCNTU3OUQ0MEVGNDg1QkNBOUNFRDBBIiwidHlwIjoiYXQrand0In0.eyJuYmYiOjE2MDExMDg1MzMsImV4cCI6MTYzMjY0NDUzMywiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo1MDAwIiwiYXVkIjoiQ3VzdG9tZXJTZXJ2aWNlLkFwaSIsImNsaWVudF9pZCI6Im5hc19jbGllbnQiLCJzdWIiOiIwNWEwNzliMC1jZjBiLTQ2ZjctYThlMy1iODk4MjIwODgzMjQiLCJhdXRoX3RpbWUiOjE2MDExMDg1MzMsImlkcCI6ImxvY2FsIiwic2VydmljZS51c2VyIjoiYWRtaW4iLCJqdGkiOiIyOUU0OTRERTg1QjA0RTdBNUM1NjM3NDhCQzIyOTEyRSIsInNpZCI6IkFDOTE4QzNEMkY3MUIzRTRBMERGQzc2MDQ4QzJBMEUzIiwiaWF0IjoxNjAxMTA4NTMzLCJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwibmFzLmNsaWVudCIsIm5hcy5zZXJ2aWNlcyJdLCJhbXIiOlsicHdkIl19.x4knTyLtPEwUSnc35EnWSyxINwOLU5YTBeCD_eXDkXmMC1bWQclkdLH18Dgict07qVWyRL9EcYT66j4p7hsUGbZrZP9TLeNpQP5BT6eRSeYvkf2lmvJe1xaCvPYrHpPGvApLJJAmQxCwex7AAW74zJLpl_SdNUf3AHBkBvjr2ibEkDBgRgOTO0Z3n3f43ZxZw3LAi_x8ZRSxITY0mpevTUpDhx2pv5-ehXe7BaCbTxAJ6dBvkAavtmB-W3wp7cnJqSfr2mFpsBzE_Ek_OzAFnu_N1ALi8yE9LpuAPSDj4hVz11i98urPebHA8lEca1yBAPI6goQlKJEB4_NXI5F8CA";

	// private static String AUTHENTICATION_TOKEN =
	// "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEzRjhFREM2QjJCNTU3OUQ0MEVGNDg1QkNBOUNFRDBBIiwidHlwIjoiYXQrand0In0.eyJuYmYiOjE2MDMyNjgyMzEsImV4cCI6MTYzNDgwNDIzMSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo1MDAwIiwiYXVkIjoiQ3VzdG9tZXJTZXJ2aWNlLkFwaSIsImNsaWVudF9pZCI6Im5hc19jbGllbnQiLCJzdWIiOiI0N2QwZTM2Yy1iZDQ1LTRkMzUtOTI2OS1jNjA4OTdlZTNkZjQiLCJhdXRoX3RpbWUiOjE2MDMyNjgyMzEsImlkcCI6ImxvY2FsIiwic2VydmljZS51c2VyIjoiYWRtaW4iLCJqdGkiOiJBOUNFOTU0NjBGOERFMjYyQkVCQkE4RDczQTk1MjU0OCIsInNpZCI6IkI2NzExREI2MERFQTA1QjgyODdDMTFCNDZDNDEyRUY0IiwiaWF0IjoxNjAzMjY4MjMxLCJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwibmFzLmNsaWVudCIsIm5hcy5zZXJ2aWNlcyJdLCJhbXIiOlsicHdkIl19.cJ1M_INpU0Tn7Ti1acM7Dxv6mQjhvkZ6m4UAX2sssTYdT7D-A-dLG-_8TBtamw-fHH-VUrWYvLiQly328WtXdZ_N9YCv_xy8dBZ5SqNu2DLOz4exe_8bt1mks-ttoEnalMhVWF6g9qo6qclTW7Wuvq3mZxyoQVppRQYiNbGju_woPh69i56Rhbig9kwHXEJo7YBV__e8o7sOgOI9NU58pHv3T9CmxpZ-pZakeAe2J7Msp1IGplByLRL-Va_qD83IjwRL_mEufCfPvKGhiovHX8jVFvDa04pG-XihjX891BkY2cR4o9S2mgMntMLEcd9nHFU_wpr2AchsCTFUC7HFtw";

	private static String AUTHENTICATION_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEzRjhFREM2QjJCNTU3OUQ0MEVGNDg1QkNBOUNFRDBBIiwidHlwIjoiYXQrand0In0.eyJuYmYiOjE2MzQ4MjM4NTgsImV4cCI6MTY2NjM1OTg1OCwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo1MDAwIiwiYXVkIjoiQ3VzdG9tZXJTZXJ2aWNlLkFwaSIsImNsaWVudF9pZCI6Im5hc19jbGllbnQiLCJzdWIiOiIwNjNhZGI5OC0yZTc0LTQxZDMtYTJhMC1hNDFiMzE3M2I1NDAiLCJhdXRoX3RpbWUiOjE2MzQ4MjM4NTcsImlkcCI6ImxvY2FsIiwic2VydmljZS51c2VyIjoiYWRtaW4iLCJqdGkiOiI5NTY1OTA3NjE1OEI3OUVDOEQ5NTdEOUNFNzE5RDU1RSIsInNpZCI6IjM0MEMzMzdFOTkxNzQ4NjlDQkY5Q0M0QzQzODIyMzM1IiwiaWF0IjoxNjM0ODIzODU4LCJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwibmFzLmNsaWVudCIsIm5hcy5zZXJ2aWNlcyJdLCJhbXIiOlsicHdkIl19.RJxvka_V9R7OSdTp329M0NWd--GjXJsw1OfomnXWB7plmxcAJUSRk6Hee1yBJ2f7TO3Mc46MdkhTgIBpUCqCmnsIPoUTD_SNdC_S_etcrTe0nYddXuub-2CxKICh6b3aVWPpp-lnHJvHXsXry6V9EuG6d--5JkCTeaXY0dY4w881XTc2q_OskH1Igmn9GWUg1ihnjqTm95U81dq6VtxzhKSW2KKHOG27HyJhK78d8lvsis1h9CJSmdbwg9FBaGUqWNexx-yzmrE7OkEWyUX8iMBpUBCXZ_hIOlHBy785NcH2uBcqdxvSvuDgMM0xKJK1XsIskQi9jzIAW2sH4JxyoQ";

	private final Logger log = LoggerFactory.getLogger(SendSalesOrderSapService.class);
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

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherService;

	@Transactional
	public void sendSalesOrder(String inventoryPid) {
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		log.debug("REST request to download sales orders <" + company.getLegalName() + "> : {}");

		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(inventoryPid).get();

		List<String> ivhPids = new ArrayList<>();
		ivhPids.add(inventoryPid);

		if (inventoryVoucherHeaderDTO.getTallyDownloadStatus().equals(TallyDownloadStatus.PENDING)
				&& inventoryVoucherHeaderDTO.getSalesManagementStatus().equals(SalesManagementStatus.APPROVE)) {
			log.info("Downloading to sap prabhu.............");
			String id="INV_QUERY_161";
			String description=" Updating invVou Header by Tally download status using pid";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
			Long start1= System.nanoTime();
			int updated = inventoryVoucherHeaderRepository
					.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(TallyDownloadStatus.PROCESSING, ivhPids);
			String flag;
			Long end = System.nanoTime();
			Long TimeTaken = (end - start1) / 1000000;
			long minutes = TimeUnit.MILLISECONDS.toMinutes(TimeTaken);
			if (minutes < 2) {
				flag = "normal";
			} else if (minutes > 2) {
				flag = "slow";
			} else {
				flag = "deadSlow";
			}
			log.info("{ Query Id:- " + id + " TotalTime:- " + minutes+ "Result:-"+flag+" }");
		
			log.debug("updated " + updated + " to PROCESSING");

			log.info("Sending  SalesOrder to SAP....");

			SalesOrderMasterSap requestBody = getRequestBody(inventoryVoucherHeaderDTO);

			List<SalesOrderMasterSap> salesOrders = new ArrayList<>();

			salesOrders.add(requestBody);

			HttpEntity<List<SalesOrderMasterSap>> entity = new HttpEntity<>(salesOrders, createTokenAuthHeaders());

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

				ResponseEntity<List<ApiResponseDataSap>> authenticateResponse = restTemplate.exchange(
						API_URL_SALES_ORDER, HttpMethod.POST, entity,
						new ParameterizedTypeReference<List<ApiResponseDataSap>>() {
						});

				// new ParameterizedTypeReference<List<BitPay>>(){})

				log.info("Sap Sales Order Created Success Size" + "----"
						+ authenticateResponse.getBody().get(0).getId().size());

				changeSalesOrderServerDownloadStatus(authenticateResponse.getBody().get(0), ivhPids);

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

	private void changeSalesOrderServerDownloadStatus(ApiResponseDataSap authenticateResponse, List<String> ivhPids) {

		if (authenticateResponse != null) {

			if (authenticateResponse.getStatus().equals("Success")) {
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_161" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="update InvVoucherHeader TallyDownloadStatus Using Pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
						TallyDownloadStatus.COMPLETED, ivhPids);
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
				log.debug("updated " + updated + " to COMPLETED");
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

	private SalesOrderMasterSap getRequestBody(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<CompanyConfiguration> opPiecesToQuantity = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.PIECES_TO_QUANTITY);

		SalesOrderMasterSap salesOrderMasterSap = new SalesOrderMasterSap();

		salesOrderMasterSap.setDbKey(1);
		salesOrderMasterSap.setLocation("");

		salesOrderMasterSap.setCustomerCode(inventoryVoucherHeaderDTO.getReceiverAccountAlias());
		salesOrderMasterSap.setCustomerName(inventoryVoucherHeaderDTO.getReceiverAccountName());

		salesOrderMasterSap.setCustomerRef("");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDateTime localDateTime = inventoryVoucherHeaderDTO.getClientDate();
		String date = localDateTime.format(formatter);

		salesOrderMasterSap.setPostingDate(date);
		salesOrderMasterSap.setDocDate(date);

		salesOrderMasterSap.setValidUntil(date);
		salesOrderMasterSap.setSalesCommitDate(date);

		salesOrderMasterSap.setRemarks(inventoryVoucherHeaderDTO.getVisitRemarks());
		salesOrderMasterSap.setPriority("4");

		List<SalesOrderItemDetailsSap> salesOrderItems = new ArrayList<>();

		for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
				.getInventoryVoucherDetails()) {
			SalesOrderItemDetailsSap salesOrderItemDetailsSap = new SalesOrderItemDetailsSap();
			double quantity = inventoryVoucherDetailDTO.getQuantity();
			String itemType = inventoryVoucherDetailDTO.getItemtype() != null ? inventoryVoucherDetailDTO.getItemtype()
					: "MTS";
			if (opPiecesToQuantity.isPresent()) {
				if (opPiecesToQuantity.get().getValue().equals("true")) {

					if (inventoryVoucherDetailDTO.getProductSKU() != null
							&& inventoryVoucherDetailDTO.getProductSKU().equalsIgnoreCase("MTS")) {
						quantity = (quantity * inventoryVoucherDetailDTO.getProductUnitQty()) / 1000; // Quantity into
																										// MTS;
						itemType = inventoryVoucherDetailDTO.getProductSKU();
					} else if (inventoryVoucherDetailDTO.getProductSKU() != null
							&& inventoryVoucherDetailDTO.getProductSKU().equalsIgnoreCase("Pcs")) {
						itemType = inventoryVoucherDetailDTO.getProductSKU();
					}
				}
			}

			salesOrderItemDetailsSap.setItemCode(inventoryVoucherDetailDTO.getProductName());
			salesOrderItemDetailsSap.setItemName(inventoryVoucherDetailDTO.getProductAlias());
			salesOrderItemDetailsSap.setQuantity(String.valueOf(quantity));
			salesOrderItemDetailsSap.setuPrice(String.valueOf("0.0"));
			salesOrderItemDetailsSap.setTaxCode("");
			salesOrderItemDetailsSap.setWareHouseCode("PSO2");
			salesOrderItemDetailsSap.setItemtype("MTS");
			// salesOrderItemDetailsSap.setItemtype(itemType);
			salesOrderItemDetailsSap.setArecieved("");

			salesOrderItems.add(salesOrderItemDetailsSap);
		}

		salesOrderMasterSap.setItemDetails(salesOrderItems);

		int Scode = 0;

		if (inventoryVoucherHeaderDTO.getEmployeeAlias() != null) {
			try {
				Scode = Integer.parseInt(inventoryVoucherHeaderDTO.getEmployeeAlias());
			} catch (NumberFormatException e) {
				Scode = 0;
			}
		}
		salesOrderMasterSap.setsCode(Scode);
		salesOrderMasterSap.setOrderType("O");
		salesOrderMasterSap.setDiscount(0.0);
		salesOrderMasterSap.setShipTo("");
		salesOrderMasterSap.setBillTo("");

		return salesOrderMasterSap;
	}

}
