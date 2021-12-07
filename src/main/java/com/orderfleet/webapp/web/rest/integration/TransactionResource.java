package com.orderfleet.webapp.web.rest.integration;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentStockLocationSource;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.GstLedger;
import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.OrderStatus;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.SalesLedger;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherDetailRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.GstLedgerRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.OrderStatusRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.SalesLedgerRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountingVoucherHeaderService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.ExecutiveTaskSubmissionService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.SalesLedgerService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ReceiptDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderDTO;
import com.orderfleet.webapp.web.rest.dto.SalesOrderItemDTO;
import com.orderfleet.webapp.web.rest.dto.VatLedgerDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.tally.dto.GstLedgerDTO;

/**
 * REST controller for managing order data for third party application.
 * 
 * @author Sarath
 * @since Oct 28, 2016
 */
@RestController
@RequestMapping(value = "/api/tp")
public class TransactionResource {

	private final Logger log = LoggerFactory.getLogger(TransactionResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountingVoucherHeaderService accountingVoucherHeaderService;

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherHeaderService;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private DynamicDocumentHeaderService dynamicDocumentHeaderService;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private ActivityService activityService;

	@Inject
	private ExecutiveTaskSubmissionService executiveTaskSubmissionService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DocumentService documentService;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private GstLedgerRepository gstLedgerRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private CompanyService companyService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private OrderStatusRepository orderStatusRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@Inject
	private DocumentStockLocationSourceRepository documentStockLocationSourceRepository;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherDetailRepository;

	@Inject
	private SalesLedgerRepository salesLedgerRepository;

	// @Inject
	// private DocumentStockLocationSourceRepository
	// documentStockLocationSourceRepository;

	private static final String dynamicDocumentAditional = "Sales Order Addl Info";
	private static final String dynamicDocumentDespach = "Despatch Info";

	/**
	 * POST /sales-order.json : Create new salesOrders.
	 * 
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/get-sales-orders.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<SalesOrderDTO> getSalesOrderJSON() throws URISyntaxException {
		log.debug("REST request to download sales orders : {}");
		List<SalesOrderDTO> salesOrderDTOs = new ArrayList<>();
		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountTypeName("VAT");
		log.info("Retrived account profiles");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		String id = "INV_QUERY_120" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and status ";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndStatusOrderByCreatedDateDesc();
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
		log.info("Retrived inventory voucher headers");

		CompanyViewDTO company = companyService.findOne(SecurityUtils.getCurrentUsersCompanyId());

		String companyPid = company.getPid();

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ALIAS_TO_NAME);
		for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {

			String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";

			// seting inventory heder to salesOrderDTO
			SalesOrderDTO salesOrderDTO = new SalesOrderDTO(inventoryVoucherHeader);

			salesOrderDTO.setAccountProfileDTO(accountProfileMapper
					.accountProfileToAccountProfileDTO(inventoryVoucherHeader.getReceiverAccount()));

			List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();
			List<InventoryVoucherDetail> ivdList = inventoryVoucherHeader.getInventoryVoucherDetails();
			ivdList.sort((InventoryVoucherDetail i1, InventoryVoucherDetail i2) -> i1.getProduct().getName()
					.compareTo(i2.getProduct().getName()));
			for (InventoryVoucherDetail inventoryVoucherDetail : inventoryVoucherHeader.getInventoryVoucherDetails()) {
				SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO(inventoryVoucherDetail);
				if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {
					salesOrderItemDTO.setProductName(inventoryVoucherDetail.getProduct().getAlias());
				}
				if (inventoryVoucherDetail.getRferenceInventoryVoucherHeader() != null) {
					rferenceInventoryVoucherHeaderExecutiveExecutionPid = inventoryVoucherDetail
							.getRferenceInventoryVoucherHeader().getExecutiveTaskExecution().getPid();
				}
				List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTOs = new ArrayList<>();

				List<OpeningStockDTO> openingStockDTOs = new ArrayList<>();
				for (InventoryVoucherBatchDetail inventoryVoucherBatchDetail : inventoryVoucherDetail
						.getInventoryVoucherBatchDetails()) {
					openingStockDTOs = openingStockRepository
							.findByCompanyIdAndProductProfilePidAndBatchNumber(
									inventoryVoucherBatchDetail.getProductProfile().getPid(),
									inventoryVoucherBatchDetail.getBatchNumber())
							.stream().map(OpeningStockDTO::new).collect(Collectors.toList());
				}

				salesOrderItemDTO.setOpeningStockDTOs(openingStockDTOs);
				salesOrderItemDTO.setInventoryVoucherBatchDetailsDTO(inventoryVoucherBatchDetailsDTOs);
				salesOrderItemDTOs.add(salesOrderItemDTO);
			}
			List<VatLedgerDTO> vatLedgerDTOs = new ArrayList<>();
			for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
				VatLedgerDTO vatLedgerDTO = new VatLedgerDTO();
				vatLedgerDTO.setName(accountProfileDTO.getName());
				String vatledgerArray[] = accountProfileDTO.getAlias().split("\\,");
				vatLedgerDTO.setPercentageOfCalculation(vatledgerArray[1]);
				vatLedgerDTO.setVatClass(vatledgerArray[0]);
				vatLedgerDTOs.add(vatLedgerDTO);
			}
			salesOrderDTO.setVatLedgerDTOs(vatLedgerDTOs);
			salesOrderDTO.setSalesOrderItemDTOs(salesOrderItemDTOs);
			List<DynamicDocumentHeaderDTO> documentHeaderDTOs = new ArrayList<>();

			if (!rferenceInventoryVoucherHeaderExecutiveExecutionPid.equalsIgnoreCase("")) {

				DynamicDocumentHeaderDTO documentHeaderDTOAditonal = dynamicDocumentHeaderService
						.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
								rferenceInventoryVoucherHeaderExecutiveExecutionPid, dynamicDocumentAditional);
				DynamicDocumentHeaderDTO dynamicDocumentHeadersDespatch = dynamicDocumentHeaderService
						.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
								inventoryVoucherHeader.getExecutiveTaskExecution().getPid(), dynamicDocumentDespach);

				if (documentHeaderDTOAditonal.getDocumentPid() != null) {
					documentHeaderDTOs.add(documentHeaderDTOAditonal);
				}
				if (dynamicDocumentHeadersDespatch.getDocumentPid() != null) {
					documentHeaderDTOs.add(dynamicDocumentHeadersDespatch);
				}
			}
			salesOrderDTO.setDynamicDocumentHeaderDTOs(documentHeaderDTOs);
			salesOrderDTOs.add(salesOrderDTO);
		}
		log.info("Completed salesorder dtos");
		return salesOrderDTOs;
	}

	/**
	 * POST /sales-order.json : Create new salesOrders.
	 * 
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	// Method used for getting sales order (aquatech, and new companies)
	@RequestMapping(value = "/v2/get-sales-orders.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<SalesOrderDTO> getSalesOrderJsonData() throws URISyntaxException {

		long start = System.nanoTime();

		CompanyViewDTO company = companyService.findOne(SecurityUtils.getCurrentUsersCompanyId());
		log.debug("REST request to download sales orders <" + company.getLegalName() + "> : {}");

		List<SalesOrderDTO> salesOrderDTOs = new ArrayList<>();
		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES_ORDER,
				company.getId());
		if (primarySecDoc.isEmpty()) {
			log.info("........No PrimarySecondaryDocument configuration Available...........");
			return salesOrderDTOs;
		}
		List<Long> documentIdList = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountTypeName("VAT");
		List<String> inventoryHeaderPid = new ArrayList<String>();
		String companyPid = company.getPid();

		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ALIAS_TO_NAME);

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
			List<PriceLevel> priceLevels = priceLevelRepository.findAllByCompanyIdAndIdsIn(priceLeveIds);
			List<OrderStatus> orderStatusList = orderStatusRepository.findAllByCompanyIdAndIdsIn(orderStatusIds);
			List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
					.findAllByInventoryVoucherHeaderPidIn(ivhPids);
			Object[] errorPrint = null;
			try {
				for (Object[] obj : inventoryVoucherHeaders) {
					errorPrint = obj;
					String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";

					Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[12].toString()))
							.findAny();

					Optional<Document> opDocument = documents.stream()
							.filter(doc -> doc.getId() == Long.parseLong(obj[13].toString())).findAny();

					Optional<EmployeeProfile> opEmployeeProfile = employeeProfiles.stream()
							.filter(emp -> emp.getId() == Long.parseLong(obj[14].toString())).findAny();

					Optional<ExecutiveTaskExecution> opExe = executiveTaskExecutions.stream()
							.filter(doc -> doc.getId() == Long.parseLong(obj[15].toString())).findAny();

					Optional<AccountProfile> opRecAccPro = receiverAccountProfiles.stream()
							.filter(a -> a.getId() == Long.parseLong(obj[16].toString())).findAny();

					Optional<AccountProfile> opSupAccPro = supplierAccountProfiles.stream()
							.filter(a -> a.getId() == Long.parseLong(obj[17].toString())).findAny();

					PriceLevel priceLevel = null;
					if (obj[18] != null) {

						Optional<PriceLevel> opPriceLevel = priceLevels.stream()
								.filter(pl -> pl.getId() == Long.parseLong(obj[18].toString())).findAny();
						if (opPriceLevel.isPresent()) {
							priceLevel = opPriceLevel.get();
						}
					}

					Optional<OrderStatus> opOrderStatus = orderStatusList.stream()
							.filter(os -> os.getId() == Long.parseLong(obj[23].toString())).findAny();

					OrderStatus orderStatus = new OrderStatus();
					if (opOrderStatus.isPresent()) {
						orderStatus = opOrderStatus.get();
					}

					SalesOrderDTO salesOrderDTO = ivhObjToSalesOrderDTO(obj, opUser.get(), opDocument.get(),
							opEmployeeProfile.get(), opExe.get(), opRecAccPro.get(), opSupAccPro.get(), priceLevel,
							orderStatus);

					salesOrderDTO.setAccountProfileDTO(
							accountProfileMapper.accountProfileToAccountProfileDTO(opRecAccPro.get()));

					List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetails.stream()
							.filter(ivd -> ivd.getInventoryVoucherHeader().getId() == Long.parseLong(obj[0].toString()))
							.collect(Collectors.toList()).stream()
							.sorted(Comparator.comparingLong(InventoryVoucherDetail::getId))
							.collect(Collectors.toList());

					List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();
					for (InventoryVoucherDetail inventoryVoucherDetail : ivDetails) {
						SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO(inventoryVoucherDetail);

						if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {
							salesOrderItemDTO.setProductName(inventoryVoucherDetail.getProduct().getAlias());
						}

						if (inventoryVoucherDetail.getRferenceInventoryVoucherHeader() != null) {
							rferenceInventoryVoucherHeaderExecutiveExecutionPid = inventoryVoucherDetail
									.getRferenceInventoryVoucherHeader().getExecutiveTaskExecution().getPid();
						}
						List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTOs = new ArrayList<>();

						List<OpeningStockDTO> openingStockDTOs = new ArrayList<>();
						for (InventoryVoucherBatchDetail inventoryVoucherBatchDetail : inventoryVoucherDetail
								.getInventoryVoucherBatchDetails()) {
							openingStockDTOs = openingStockRepository
									.findByCompanyIdAndProductProfilePidAndBatchNumber(
											inventoryVoucherBatchDetail.getProductProfile().getPid(),
											inventoryVoucherBatchDetail.getBatchNumber())
									.stream().map(OpeningStockDTO::new).collect(Collectors.toList());
						}

						salesOrderItemDTO.setOpeningStockDTOs(openingStockDTOs);
						salesOrderItemDTO.setInventoryVoucherBatchDetailsDTO(inventoryVoucherBatchDetailsDTOs);
						salesOrderItemDTOs.add(salesOrderItemDTO);
					}
					List<VatLedgerDTO> vatLedgerDTOs = new ArrayList<>();
					for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
						VatLedgerDTO vatLedgerDTO = new VatLedgerDTO();
						vatLedgerDTO.setName(accountProfileDTO.getName());
						String vatledgerArray[] = accountProfileDTO.getAlias().split("\\,");
						vatLedgerDTO.setPercentageOfCalculation(vatledgerArray[1]);
						vatLedgerDTO.setVatClass(vatledgerArray[0]);
						vatLedgerDTOs.add(vatLedgerDTO);
					}
					salesOrderDTO.setVatLedgerDTOs(vatLedgerDTOs);
					// List<SalesOrderItemDTO> sortedSalesOrderItems = new
					// ArrayList<SalesOrderItemDTO>();
					// sortedSalesOrderItems =
					// salesOrderItemDTOs.stream().sorted(Comparator.comparingLong(SalesOrderItemDTO::getSortOrder)).collect(Collectors.toList());
					salesOrderDTO.setSalesOrderItemDTOs(salesOrderItemDTOs);
					List<DynamicDocumentHeaderDTO> documentHeaderDTOs = new ArrayList<>();

					if (!rferenceInventoryVoucherHeaderExecutiveExecutionPid.equalsIgnoreCase("")) {

						DynamicDocumentHeaderDTO documentHeaderDTOAditonal = dynamicDocumentHeaderService
								.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
										rferenceInventoryVoucherHeaderExecutiveExecutionPid, dynamicDocumentAditional);
						DynamicDocumentHeaderDTO dynamicDocumentHeadersDespatch = dynamicDocumentHeaderService
								.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(opExe.get().getPid(),
										dynamicDocumentDespach);

						if (documentHeaderDTOAditonal.getDocumentPid() != null) {
							documentHeaderDTOs.add(documentHeaderDTOAditonal);
						}
						if (dynamicDocumentHeadersDespatch.getDocumentPid() != null) {
							documentHeaderDTOs.add(dynamicDocumentHeadersDespatch);
						}
					}
					salesOrderDTO.setDynamicDocumentHeaderDTOs(documentHeaderDTOs);
					List<GstLedger> gstLedgerList = new ArrayList<>();
					gstLedgerList = gstLedgerRepository
							.findAllByCompanyIdAndActivated(SecurityUtils.getCurrentUsersCompanyId(), true);
					if (gstLedgerList != null && gstLedgerList.size() != 0) {
						List<GstLedgerDTO> gstLedgerDtos = gstLedgerList.stream().map(gst -> new GstLedgerDTO(gst))
								.collect(Collectors.toList());
						salesOrderDTO.setGstLedgerDtos(gstLedgerDtos);
					}
					inventoryHeaderPid.add(obj[9].toString());

					salesOrderDTOs.add(salesOrderDTO);
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
			if (!salesOrderDTOs.isEmpty()) {
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
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);

		return salesOrderDTOs;
	}

	// Method used for getting sales grouped by employee
	@RequestMapping(value = "/v2/get-sales.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<SalesOrderDTO> getSalesJsonData(@RequestParam("employeeVoucher") String employeeVoucher)
			throws URISyntaxException {
		long start = System.nanoTime();
		CompanyViewDTO company = companyService.findOne(SecurityUtils.getCurrentUsersCompanyId());
		log.debug("REST request to download sales <" + company.getLegalName() + "> : {}");
		log.info("REST request to download sales  ***** <" + company.getLegalName() + "> : {}" + employeeVoucher);

		if (employeeVoucher == null) {
			log.info("REST request to download sales Failed : {}" + employeeVoucher);
			return Collections.emptyList();
		}
		List<EmployeeProfile> employeeProfilesList = employeeProfileRepository.findAllByCompanyId(true);
		List<Long> empId = new ArrayList<>();
		if ("All".equalsIgnoreCase(employeeVoucher)) {
			empId = employeeProfilesList.stream().map(emp -> emp.getId()).collect(Collectors.toList());
		} else {
			empId = employeeProfilesList.stream().filter(emp -> emp.getName().trim().equals(employeeVoucher))
					.map(emp -> emp.getId()).collect(Collectors.toList());
		}
		log.info("EmployeeId found :" + empId);
		List<SalesOrderDTO> salesOrderDTOs = new ArrayList<>();
		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountTypeName("VAT");
		List<String> inventoryHeaderPid = new ArrayList<String>();
		String companyPid = company.getPid();
		log.info("Account Types found :" + accountProfileDTOs.size());
		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);
		log.info("SalesManagement Company Config:" + optSalesManagement.isPresent());

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ALIAS_TO_NAME);
		/* List<InventoryVoucherHeader> inventoryVoucherHeaders = new ArrayList<>(); */

		/*
		 * if (optSalesManagement.isPresent() &&
		 * optSalesManagement.get().getValue().equalsIgnoreCase("true")) {
		 * inventoryVoucherHeaders = inventoryVoucherHeaderRepository
		 * .findAllByCompanyIdAndTallyStatusAndSalesManagementStatusAndEmployeeOrderByCreatedDateDesc
		 * (empId);
		 * 
		 * } else { inventoryVoucherHeaders = inventoryVoucherHeaderRepository
		 * .findAllByCompanyIdAndTallyStatusAndEmployeeOrderByCreatedDateDesc(empId); }
		 */

		List<Object[]> inventoryVoucherHeaders = new ArrayList<>();

		if (optSalesManagement.isPresent() && optSalesManagement.get().getValue().equalsIgnoreCase("true")) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_193" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by CompanyId and TallyStatus and salesManagement status Employee";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndSalesManagementStatusAndEmployeeOrderByCreatedDateDesc(empId);
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
			String id = "INV_QUERY_194" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by CompanyId and TallyStatus Order and Employee";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusOrderAndEmployeeByCreatedDateDesc(empId);
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
			List<UserStockLocation> userStockLocations = userStockLocationRepository
					.findAllByCompanyPid(company.getPid());
			List<PriceLevel> priceLevels = priceLevelRepository.findAllByCompanyIdAndIdsIn(priceLeveIds);
			List<OrderStatus> orderStatusList = orderStatusRepository.findAllByCompanyIdAndIdsIn(orderStatusIds);
			List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
					.findAllByInventoryVoucherHeaderPidIn(ivhPids);
			Object[] errorPrint = null;
			try {
				log.info("Starting Try block ");
				for (Object[] obj : inventoryVoucherHeaders) {
					errorPrint = obj;

					String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";

					Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[12].toString()))
							.findAny();
					log.info("user empty  :" + !opUser.isPresent());
					Optional<Document> opDocument = documents.stream()
							.filter(doc -> doc.getId() == Long.parseLong(obj[13].toString())).findAny();
					log.info("document empty :" + !opDocument.isPresent());
					Optional<EmployeeProfile> opEmployeeProfile = employeeProfiles.stream()
							.filter(emp -> emp.getId() == Long.parseLong(obj[14].toString())).findAny();
					log.info("employee empty :" + !opEmployeeProfile.isPresent());
					Optional<ExecutiveTaskExecution> opExe = executiveTaskExecutions.stream()
							.filter(doc -> doc.getId() == Long.parseLong(obj[15].toString())).findAny();
					log.info("executive empty :" + !opExe.isPresent());
					Optional<AccountProfile> opRecAccPro = receiverAccountProfiles.stream()
							.filter(a -> a.getId() == Long.parseLong(obj[16].toString())).findAny();
					log.info("receiver account empty :" + !opRecAccPro.isPresent());
					Optional<AccountProfile> opSupAccPro = supplierAccountProfiles.stream()
							.filter(a -> a.getId() == Long.parseLong(obj[17].toString())).findAny();
					log.info("supplier account empty :" + !opSupAccPro.isPresent());
					PriceLevel priceLevel = null;

					Optional<UserStockLocation> opUserStockLocation = userStockLocations.stream()
							.filter(us -> us.getUser().getPid().equals(opUser.get().getPid())).findAny();

					if (obj[18] != null) {
						Optional<PriceLevel> opPriceLevel = priceLevels.stream()
								.filter(pl -> pl.getId() == Long.parseLong(obj[18].toString())).findAny();

						if (opPriceLevel.isPresent()) {
							priceLevel = opPriceLevel.get();
						}
					}

					Optional<OrderStatus> opOrderStatus = orderStatusList.stream()
							.filter(os -> os.getId() == Long.parseLong(obj[23].toString())).findAny();

					OrderStatus orderStatus = new OrderStatus();
					if (opOrderStatus.isPresent()) {
						orderStatus = opOrderStatus.get();
					}
					log.info("ivhObjToSalesOrderDTO function starts");
					SalesOrderDTO salesOrderDTO = ivhObjToSalesOrderDTO(obj, opUser.get(), opDocument.get(),
							opEmployeeProfile.get(), opExe.get(), opRecAccPro.get(), opSupAccPro.get(), priceLevel,
							orderStatus);

					if (opUserStockLocation.isPresent()) {
						salesOrderDTO.setGodownName(opUserStockLocation.get().getStockLocation().getName());
					}
					salesOrderDTO.setAccountProfileDTO(
							accountProfileMapper.accountProfileToAccountProfileDTO(opRecAccPro.get()));

					List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetails.stream()
							.filter(ivd -> ivd.getInventoryVoucherHeader().getId() == Long.parseLong(obj[0].toString()))
							.collect(Collectors.toList()).stream()
							.sorted(Comparator.comparingLong(InventoryVoucherDetail::getId))
							.collect(Collectors.toList());

					List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();
					log.info("inventory detail loop starts");
					for (InventoryVoucherDetail inventoryVoucherDetail : ivDetails) {
						SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO(inventoryVoucherDetail);
						if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {
							salesOrderItemDTO.setProductName(inventoryVoucherDetail.getProduct().getAlias());
						}
						if (inventoryVoucherDetail.getRferenceInventoryVoucherHeader() != null) {
							rferenceInventoryVoucherHeaderExecutiveExecutionPid = inventoryVoucherDetail
									.getRferenceInventoryVoucherHeader().getExecutiveTaskExecution().getPid();
						}
						List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTOs = new ArrayList<>();

						List<OpeningStockDTO> openingStockDTOs = new ArrayList<>();
						for (InventoryVoucherBatchDetail inventoryVoucherBatchDetail : inventoryVoucherDetail
								.getInventoryVoucherBatchDetails()) {
							openingStockDTOs = openingStockRepository
									.findByCompanyIdAndProductProfilePidAndBatchNumber(
											inventoryVoucherBatchDetail.getProductProfile().getPid(),
											inventoryVoucherBatchDetail.getBatchNumber())
									.stream().map(OpeningStockDTO::new).collect(Collectors.toList());
						}

						salesOrderItemDTO.setOpeningStockDTOs(openingStockDTOs);
						salesOrderItemDTO.setInventoryVoucherBatchDetailsDTO(inventoryVoucherBatchDetailsDTOs);
						salesOrderItemDTOs.add(salesOrderItemDTO);
					}
					List<VatLedgerDTO> vatLedgerDTOs = new ArrayList<>();
					for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
						VatLedgerDTO vatLedgerDTO = new VatLedgerDTO();
						vatLedgerDTO.setName(accountProfileDTO.getName());
						String vatledgerArray[] = accountProfileDTO.getAlias().split("\\,");
						vatLedgerDTO.setPercentageOfCalculation(vatledgerArray[1]);
						vatLedgerDTO.setVatClass(vatledgerArray[0]);
						vatLedgerDTOs.add(vatLedgerDTO);
					}
					salesOrderDTO.setVatLedgerDTOs(vatLedgerDTOs);
					salesOrderDTO.setSalesOrderItemDTOs(salesOrderItemDTOs);
					List<DynamicDocumentHeaderDTO> documentHeaderDTOs = new ArrayList<>();

					if (!rferenceInventoryVoucherHeaderExecutiveExecutionPid.equalsIgnoreCase("")) {

						DynamicDocumentHeaderDTO documentHeaderDTOAditonal = dynamicDocumentHeaderService
								.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
										rferenceInventoryVoucherHeaderExecutiveExecutionPid, dynamicDocumentAditional);
						DynamicDocumentHeaderDTO dynamicDocumentHeadersDespatch = dynamicDocumentHeaderService
								.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(opExe.get().getPid(),
										dynamicDocumentDespach);

						if (documentHeaderDTOAditonal.getDocumentPid() != null) {
							documentHeaderDTOs.add(documentHeaderDTOAditonal);
						}
						if (dynamicDocumentHeadersDespatch.getDocumentPid() != null) {
							documentHeaderDTOs.add(dynamicDocumentHeadersDespatch);
						}
					}
					salesOrderDTO.setDynamicDocumentHeaderDTOs(documentHeaderDTOs);
					log.info("GST ledger");
					List<GstLedger> gstLedgerList = new ArrayList<>();
					gstLedgerList = gstLedgerRepository
							.findAllByCompanyIdAndActivated(SecurityUtils.getCurrentUsersCompanyId(), true);
					if (gstLedgerList != null && gstLedgerList.size() != 0) {
						List<GstLedgerDTO> gstLedgerDtos = gstLedgerList.stream().map(gst -> new GstLedgerDTO(gst))
								.collect(Collectors.toList());
						salesOrderDTO.setGstLedgerDtos(gstLedgerDtos);
					}
					inventoryHeaderPid.add(obj[9].toString());

					salesOrderDTOs.add(salesOrderDTO);
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
			if (!salesOrderDTOs.isEmpty()) {
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
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);

		return salesOrderDTOs;
	}

	// First writen for vansales based on voucher type
	@RequestMapping(value = "/v2/get-sales-by-voucher.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public List<SalesOrderDTO> getSalesOrderJsonDataByVoucherType(
			@RequestParam(value = "voucherType", required = true) VoucherType voucherType) throws URISyntaxException {

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		log.debug("REST request to download van sales <" + company.getLegalName() + "> : {}");
		log.info("voucher type : " + voucherType);
		long start = System.nanoTime();
		List<SalesOrderDTO> salesOrderDTOs = new ArrayList<>();
		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(voucherType, company.getId());
		if (primarySecDoc.isEmpty()) {
			log.info("........No PrimarySecondaryDocument configuration Available...........");
			return salesOrderDTOs;
		}
		List<Long> documentIdList = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountTypeName("VAT");
		List<String> inventoryHeaderPid = new ArrayList<String>();
		String companyPid = company.getPid();

		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);

		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ALIAS_TO_NAME);

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
		if (documentIdList.size() < 0) {
			log.info("Documents not found assigned with voucher type");
			return salesOrderDTOs;
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

			List<Document> documents = documentRepository.findAllByCompanyIdAndIdsIn(documentIds);
			List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyIdAndIdsIn(employeeIds);
			List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdAndIdsIn(exeIds);
			List<AccountProfile> receiverAccountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(receiverAccountProfileIds);
			List<AccountProfile> supplierAccountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(supplierAccountProfileIds);
			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);
			List<UserStockLocation> userStockLocations1 = userStockLocationRepository
					.findAllByCompanyPid(company.getPid());
			List<PriceLevel> priceLevels = priceLevelRepository.findAllByCompanyIdAndIdsIn(priceLeveIds);
			List<OrderStatus> orderStatusList = orderStatusRepository.findAllByCompanyIdAndIdsIn(orderStatusIds);
			List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
					.findAllByInventoryVoucherHeaderPidIn(ivhPids);

			for (Object[] obj : inventoryVoucherHeaders) {

				String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";

				Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[12].toString()))
						.findAny();

				Optional<Document> opDocument = documents.stream()
						.filter(doc -> doc.getId() == Long.parseLong(obj[13].toString())).findAny();

				Optional<EmployeeProfile> opEmployeeProfile = employeeProfiles.stream()
						.filter(emp -> emp.getId() == Long.parseLong(obj[14].toString())).findAny();

				Optional<ExecutiveTaskExecution> opExe = executiveTaskExecutions.stream()
						.filter(doc -> doc.getId() == Long.parseLong(obj[15].toString())).findAny();

				Optional<AccountProfile> opRecAccPro = receiverAccountProfiles.stream()
						.filter(a -> a.getId() == Long.parseLong(obj[16].toString())).findAny();

				Optional<AccountProfile> opSupAccPro = supplierAccountProfiles.stream()
						.filter(a -> a.getId() == Long.parseLong(obj[17].toString())).findAny();

				Optional<UserStockLocation> opUserStockLocation = userStockLocations1.stream()
						.filter(us -> us.getUser().getPid().equals(opUser.get().getPid())).findAny();

				PriceLevel priceLevel = null;
				if (obj[18] != null) {

					Optional<PriceLevel> opPriceLevel = priceLevels.stream()
							.filter(pl -> pl.getId() == Long.parseLong(obj[18].toString())).findAny();
					if (opPriceLevel.isPresent()) {
						priceLevel = opPriceLevel.get();
					}
				}

				Optional<OrderStatus> opOrderStatus = orderStatusList.stream()
						.filter(os -> os.getId() == Long.parseLong(obj[23].toString())).findAny();

				OrderStatus orderStatus = new OrderStatus();
				if (opOrderStatus.isPresent()) {
					orderStatus = opOrderStatus.get();
				}

				SalesOrderDTO salesOrderDTO = ivhObjToSalesOrderDTO(obj, opUser.get(), opDocument.get(),
						opEmployeeProfile.get(), opExe.get(), opRecAccPro.get(), opSupAccPro.get(), priceLevel,
						orderStatus);

				if (opUserStockLocation.isPresent()) {
					salesOrderDTO.setGodownName(opUserStockLocation.get().getStockLocation().getName());
				}

				salesOrderDTO.setAccountProfileDTO(
						accountProfileMapper.accountProfileToAccountProfileDTO(opRecAccPro.get()));

				List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetails.stream()
						.filter(ivd -> ivd.getInventoryVoucherHeader().getId() == Long.parseLong(obj[0].toString()))
						.collect(Collectors.toList()).stream()
						.sorted(Comparator.comparingLong(InventoryVoucherDetail::getId)).collect(Collectors.toList());

				List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();
				for (InventoryVoucherDetail inventoryVoucherDetail : ivDetails) {
					SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO(inventoryVoucherDetail);
					if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {
						salesOrderItemDTO.setProductName(inventoryVoucherDetail.getProduct().getAlias());
					}
					if (inventoryVoucherDetail.getRferenceInventoryVoucherHeader() != null) {
						rferenceInventoryVoucherHeaderExecutiveExecutionPid = inventoryVoucherDetail
								.getRferenceInventoryVoucherHeader().getExecutiveTaskExecution().getPid();
					}
					List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTOs = new ArrayList<>();

					List<OpeningStockDTO> openingStockDTOs = new ArrayList<>();
					for (InventoryVoucherBatchDetail inventoryVoucherBatchDetail : inventoryVoucherDetail
							.getInventoryVoucherBatchDetails()) {
						openingStockDTOs = openingStockRepository
								.findByCompanyIdAndProductProfilePidAndBatchNumber(
										inventoryVoucherBatchDetail.getProductProfile().getPid(),
										inventoryVoucherBatchDetail.getBatchNumber())
								.stream().map(OpeningStockDTO::new).collect(Collectors.toList());
					}

					List<StockLocation> userStockLocations = userStockLocationRepository
							.findStockLocationsByUserPid(opUser.get().getPid());

					List<DocumentStockLocationSource> documentStockLocationSources = documentStockLocationSourceRepository
							.findByDocumentPid(opDocument.get().getPid());

					List<StockLocation> stockLocations = new ArrayList<>();
					for (DocumentStockLocationSource documentStockLocationSource : documentStockLocationSources) {

						log.info("Document Stock Location Name: "
								+ documentStockLocationSource.getStockLocation().getName());
						stockLocations = userStockLocations.stream().filter(
								usl -> usl.getPid().equals(documentStockLocationSource.getStockLocation().getPid()))
								.collect(Collectors.toList());
						if (stockLocations.size() > 0) {
							log.info("Stock Location Name: " + stockLocations.get(0).getName());
							salesOrderItemDTO.setStockLocationName(stockLocations.get(0).getName());
							break;
						}
					}

					salesOrderItemDTO.setOpeningStockDTOs(openingStockDTOs);
					salesOrderItemDTO.setInventoryVoucherBatchDetailsDTO(inventoryVoucherBatchDetailsDTOs);
					salesOrderItemDTOs.add(salesOrderItemDTO);
				}
				List<VatLedgerDTO> vatLedgerDTOs = new ArrayList<>();
				for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
					VatLedgerDTO vatLedgerDTO = new VatLedgerDTO();
					vatLedgerDTO.setName(accountProfileDTO.getName());
					String vatledgerArray[] = accountProfileDTO.getAlias().split("\\,");
					vatLedgerDTO.setPercentageOfCalculation(vatledgerArray[1]);
					vatLedgerDTO.setVatClass(vatledgerArray[0]);
					vatLedgerDTOs.add(vatLedgerDTO);
				}
				salesOrderDTO.setVatLedgerDTOs(vatLedgerDTOs);
				// List<SalesOrderItemDTO> sortedSalesOrderItems = new
				// ArrayList<SalesOrderItemDTO>();
				// sortedSalesOrderItems =
				// salesOrderItemDTOs.stream().sorted(Comparator.comparingLong(SalesOrderItemDTO::getSortOrder)).collect(Collectors.toList());
				salesOrderDTO.setSalesOrderItemDTOs(salesOrderItemDTOs);
				List<DynamicDocumentHeaderDTO> documentHeaderDTOs = new ArrayList<>();

				if (!rferenceInventoryVoucherHeaderExecutiveExecutionPid.equalsIgnoreCase("")) {

					DynamicDocumentHeaderDTO documentHeaderDTOAditonal = dynamicDocumentHeaderService
							.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
									rferenceInventoryVoucherHeaderExecutiveExecutionPid, dynamicDocumentAditional);
					DynamicDocumentHeaderDTO dynamicDocumentHeadersDespatch = dynamicDocumentHeaderService
							.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(opExe.get().getPid(),
									dynamicDocumentDespach);

					if (documentHeaderDTOAditonal.getDocumentPid() != null) {
						documentHeaderDTOs.add(documentHeaderDTOAditonal);
					}
					if (dynamicDocumentHeadersDespatch.getDocumentPid() != null) {
						documentHeaderDTOs.add(dynamicDocumentHeadersDespatch);
					}
				}
				salesOrderDTO.setDynamicDocumentHeaderDTOs(documentHeaderDTOs);
				List<GstLedger> gstLedgerList = new ArrayList<>();
				gstLedgerList = gstLedgerRepository
						.findAllByCompanyIdAndActivated(SecurityUtils.getCurrentUsersCompanyId(), true);
				if (gstLedgerList != null && gstLedgerList.size() != 0) {
					List<GstLedgerDTO> gstLedgerDtos = gstLedgerList.stream().map(gst -> new GstLedgerDTO(gst))
							.collect(Collectors.toList());
					salesOrderDTO.setGstLedgerDtos(gstLedgerDtos);
				}
				inventoryHeaderPid.add(obj[9].toString());

				salesOrderDTOs.add(salesOrderDTO);
			}

			if (!salesOrderDTOs.isEmpty()) {
				int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
						TallyDownloadStatus.PROCESSING, inventoryHeaderPid);
				log.debug("updated " + updated + " to PROCESSING");
			}
		}
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);

		return salesOrderDTOs;
	}
	/*
	 * @RequestMapping(value = "/v2/get-sales.json", method = RequestMethod.GET,
	 * produces = MediaType.APPLICATION_JSON_VALUE)
	 * 
	 * @Timed
	 * 
	 * @Transactional public List<SalesOrderDTO>
	 * getSalesJsonData(@RequestParam("employeeVoucher") String employeeVoucher)
	 * throws URISyntaxException { log.debug("REST request to download sales : {}");
	 * log.info("REST request to download sales  ***** : {}" + employeeVoucher);
	 * 
	 * if (employeeVoucher == null) {
	 * log.info("REST request to download sales Failed : {}" + employeeVoucher);
	 * return Collections.emptyList(); } List<EmployeeProfile> employeeProfiles =
	 * employeeProfileRepository.findAllByCompanyId(true); List<Long> empId = new
	 * ArrayList<>(); if ("All".equalsIgnoreCase(employeeVoucher)) { empId =
	 * employeeProfiles.stream().map(emp ->
	 * emp.getId()).collect(Collectors.toList()); } else { empId =
	 * employeeProfiles.stream().filter(emp ->
	 * emp.getName().trim().equals(employeeVoucher)) .map(emp ->
	 * emp.getId()).collect(Collectors.toList()); }
	 * 
	 * List<SalesOrderDTO> salesOrderDTOs = new ArrayList<>();
	 * List<AccountProfileDTO> accountProfileDTOs =
	 * accountProfileService.findAllByAccountTypeName("VAT"); List<String>
	 * inventoryHeaderPid = new ArrayList<String>(); String companyPid =
	 * companyService.findOne(SecurityUtils.getCurrentUsersCompanyId()).getPid();
	 * 
	 * Optional<CompanyConfiguration> optSalesManagement =
	 * companyConfigurationRepository .findByCompanyPidAndName(companyPid,
	 * CompanyConfig.SALES_MANAGEMENT);
	 * 
	 * List<InventoryVoucherHeader> inventoryVoucherHeaders = new ArrayList<>();
	 * 
	 * if (optSalesManagement.isPresent() &&
	 * optSalesManagement.get().getValue().equalsIgnoreCase("true")) {
	 * inventoryVoucherHeaders = inventoryVoucherHeaderRepository
	 * .findAllByCompanyIdAndTallyStatusAndSalesManagementStatusAndEmployeeOrderByCreatedDateDesc
	 * (empId);
	 * 
	 * } else { inventoryVoucherHeaders = inventoryVoucherHeaderRepository
	 * .findAllByCompanyIdAndTallyStatusAndEmployeeOrderByCreatedDateDesc(empId); }
	 * log.debug("IVH size : {}", inventoryVoucherHeaders.size()); for
	 * (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {
	 * 
	 * String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";
	 * 
	 * // seting inventory heder to salesOrderDTO SalesOrderDTO salesOrderDTO = new
	 * SalesOrderDTO(inventoryVoucherHeader);
	 * salesOrderDTO.setAccountProfileDTO(accountProfileMapper
	 * .accountProfileToAccountProfileDTO(inventoryVoucherHeader.getReceiverAccount(
	 * )));
	 * 
	 * List<SalesOrderItemDTO> salesOrderItemDTOs = new
	 * ArrayList<SalesOrderItemDTO>();
	 * 
	 * for (InventoryVoucherDetail inventoryVoucherDetail :
	 * inventoryVoucherHeader.getInventoryVoucherDetails()) { SalesOrderItemDTO
	 * salesOrderItemDTO = new SalesOrderItemDTO(inventoryVoucherDetail); if
	 * (inventoryVoucherDetail.getRferenceInventoryVoucherHeader() != null) {
	 * rferenceInventoryVoucherHeaderExecutiveExecutionPid = inventoryVoucherDetail
	 * .getRferenceInventoryVoucherHeader().getExecutiveTaskExecution().getPid(); }
	 * List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTOs = new
	 * ArrayList<>();
	 * 
	 * List<OpeningStockDTO> openingStockDTOs = new ArrayList<>(); for
	 * (InventoryVoucherBatchDetail inventoryVoucherBatchDetail :
	 * inventoryVoucherDetail .getInventoryVoucherBatchDetails()) { openingStockDTOs
	 * = openingStockRepository .findByCompanyIdAndProductProfilePidAndBatchNumber(
	 * inventoryVoucherBatchDetail.getProductProfile().getPid(),
	 * inventoryVoucherBatchDetail.getBatchNumber())
	 * .stream().map(OpeningStockDTO::new).collect(Collectors.toList()); }
	 * 
	 * salesOrderItemDTO.setOpeningStockDTOs(openingStockDTOs);
	 * salesOrderItemDTO.setInventoryVoucherBatchDetailsDTO(
	 * inventoryVoucherBatchDetailsDTOs); salesOrderItemDTOs.add(salesOrderItemDTO);
	 * } List<VatLedgerDTO> vatLedgerDTOs = new ArrayList<>(); for
	 * (AccountProfileDTO accountProfileDTO : accountProfileDTOs) { VatLedgerDTO
	 * vatLedgerDTO = new VatLedgerDTO();
	 * vatLedgerDTO.setName(accountProfileDTO.getName()); String vatledgerArray[] =
	 * accountProfileDTO.getAlias().split("\\,");
	 * vatLedgerDTO.setPercentageOfCalculation(vatledgerArray[1]);
	 * vatLedgerDTO.setVatClass(vatledgerArray[0]); vatLedgerDTOs.add(vatLedgerDTO);
	 * } salesOrderDTO.setVatLedgerDTOs(vatLedgerDTOs);
	 * salesOrderDTO.setSalesOrderItemDTOs(salesOrderItemDTOs);
	 * List<DynamicDocumentHeaderDTO> documentHeaderDTOs = new ArrayList<>();
	 * 
	 * if
	 * (!rferenceInventoryVoucherHeaderExecutiveExecutionPid.equalsIgnoreCase("")) {
	 * 
	 * DynamicDocumentHeaderDTO documentHeaderDTOAditonal =
	 * dynamicDocumentHeaderService
	 * .findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
	 * rferenceInventoryVoucherHeaderExecutiveExecutionPid,
	 * dynamicDocumentAditional); DynamicDocumentHeaderDTO
	 * dynamicDocumentHeadersDespatch = dynamicDocumentHeaderService
	 * .findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
	 * inventoryVoucherHeader.getExecutiveTaskExecution().getPid(),
	 * dynamicDocumentDespach);
	 * 
	 * if (documentHeaderDTOAditonal.getDocumentPid() != null) {
	 * documentHeaderDTOs.add(documentHeaderDTOAditonal); } if
	 * (dynamicDocumentHeadersDespatch.getDocumentPid() != null) {
	 * documentHeaderDTOs.add(dynamicDocumentHeadersDespatch); } }
	 * salesOrderDTO.setDynamicDocumentHeaderDTOs(documentHeaderDTOs);
	 * List<GstLedger> gstLedgerList = new ArrayList<>(); gstLedgerList =
	 * gstLedgerRepository
	 * .findAllByCompanyIdAndActivated(inventoryVoucherHeader.getCompany().getId(),
	 * true); if (gstLedgerList != null && gstLedgerList.size() != 0) {
	 * List<GstLedgerDTO> gstLedgerDtos = gstLedgerList.stream().map(gst -> new
	 * GstLedgerDTO(gst)) .collect(Collectors.toList());
	 * salesOrderDTO.setGstLedgerDtos(gstLedgerDtos); }
	 * inventoryHeaderPid.add(inventoryVoucherHeader.getPid());
	 * 
	 * salesOrderDTOs.add(salesOrderDTO); } if (!salesOrderDTOs.isEmpty()) { int
	 * updated = inventoryVoucherHeaderRepository.
	 * updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
	 * TallyDownloadStatus.PROCESSING, inventoryHeaderPid); log.debug("updated " +
	 * updated + " to PROCESSING"); }
	 * 
	 * return salesOrderDTOs; }
	 */

	/**
	 * POST /sales-order.json : Create new salesOrders.
	 * 
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/get-doc-wise-sales-orders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<SalesOrderDTO> getAllSalesOrdersUnderDocuments(
			@Valid @RequestParam(required = true, name = "documentpids") String documentpids)
			throws URISyntaxException {
		log.debug("REST request to download sales orders : {}");

		String[] allDocumentPids = documentpids.split(",");

		List<String> Pids = new ArrayList<String>(Arrays.asList(allDocumentPids));

		List<Document> documentDTOs = documentRepository.findOneByPidIn(Pids);

		List<SalesOrderDTO> salesOrderDTOs = new ArrayList<>();
		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountTypeName("VAT");

		CompanyViewDTO company = companyService.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String companyPid = company.getPid();
		Optional<CompanyConfiguration> optAliasToName = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.ALIAS_TO_NAME);

		// List<DocumentDTO>
		// primarySecondaryDocument=primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(VoucherType.PRIMARY_SALES_ORDER);

		// PRIMARY_SALES_ORDER
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		String id = "INV_QUERY_125" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compId DocOrder and status";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByCompanyIdAndStatusAndDocumentsOrderByCreatedDateDesc(documentDTOs);
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
		for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {

			String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";
			SalesOrderDTO salesOrderDTO = new SalesOrderDTO(inventoryVoucherHeader);
			List<SalesOrderItemDTO> salesOrderItemDTOs = new ArrayList<SalesOrderItemDTO>();
			if (inventoryVoucherHeader.getInventoryVoucherDetails() == null) {
				throw new IllegalArgumentException("Inventory Detail not present");
			}
			for (InventoryVoucherDetail inventoryVoucherDetail : inventoryVoucherHeader.getInventoryVoucherDetails()) {
				SalesOrderItemDTO salesOrderItemDTO = new SalesOrderItemDTO(inventoryVoucherDetail);
				if (optAliasToName.isPresent() && optAliasToName.get().getValue().equalsIgnoreCase("true")) {
					salesOrderItemDTO.setProductName(inventoryVoucherDetail.getProduct().getAlias());
				}
				if (inventoryVoucherDetail.getRferenceInventoryVoucherHeader() != null) {
					rferenceInventoryVoucherHeaderExecutiveExecutionPid = inventoryVoucherDetail
							.getRferenceInventoryVoucherHeader().getExecutiveTaskExecution().getPid();
				}
				List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailsDTOs = new ArrayList<>();
				List<OpeningStockDTO> openingStockDTOs = new ArrayList<>();
				for (InventoryVoucherBatchDetail inventoryVoucherBatchDetail : inventoryVoucherDetail
						.getInventoryVoucherBatchDetails()) {
					InventoryVoucherBatchDetailDTO inventoryVoucherBatchDetailDTO = new InventoryVoucherBatchDetailDTO(
							inventoryVoucherBatchDetail);
					inventoryVoucherBatchDetailsDTOs.add(inventoryVoucherBatchDetailDTO);
					List<OpeningStock> openingStocks = openingStockRepository
							.findByCompanyIdAndProductProfilePidAndBatchNumber(
									inventoryVoucherBatchDetailDTO.getProductProfilePid(),
									inventoryVoucherBatchDetailDTO.getBatchNumber());
					for (OpeningStock openingStock : openingStocks) {
						OpeningStockDTO openingStockDTO = new OpeningStockDTO(openingStock);
						openingStockDTOs.add(openingStockDTO);
					}
				}
				salesOrderItemDTO.setOpeningStockDTOs(openingStockDTOs);
				salesOrderItemDTO.setInventoryVoucherBatchDetailsDTO(inventoryVoucherBatchDetailsDTOs);
				salesOrderItemDTOs.add(salesOrderItemDTO);
			}
			List<VatLedgerDTO> vatLedgerDTOs = new ArrayList<>();
			for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
				VatLedgerDTO vatLedgerDTO = new VatLedgerDTO();
				vatLedgerDTO.setName(accountProfileDTO.getName());
				String vatledgerArray[] = accountProfileDTO.getAlias().split("\\,");
				vatLedgerDTO.setPercentageOfCalculation(vatledgerArray[1]);
				vatLedgerDTO.setVatClass(vatledgerArray[0]);
				vatLedgerDTOs.add(vatLedgerDTO);
			}
			salesOrderDTO.setVatLedgerDTOs(vatLedgerDTOs);
			salesOrderDTO.setSalesOrderItemDTOs(salesOrderItemDTOs);
			List<DynamicDocumentHeaderDTO> documentHeaderDTOs = new ArrayList<>();

			if (!rferenceInventoryVoucherHeaderExecutiveExecutionPid.equalsIgnoreCase("")) {

				DynamicDocumentHeaderDTO documentHeaderDTOAditonal = dynamicDocumentHeaderService
						.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
								rferenceInventoryVoucherHeaderExecutiveExecutionPid, dynamicDocumentAditional);
				DynamicDocumentHeaderDTO dynamicDocumentHeadersDespatch = dynamicDocumentHeaderService
						.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
								inventoryVoucherHeader.getExecutiveTaskExecution().getPid(), dynamicDocumentDespach);

				if (documentHeaderDTOAditonal.getDocumentPid() != null) {
					documentHeaderDTOs.add(documentHeaderDTOAditonal);
				}
				if (dynamicDocumentHeadersDespatch.getDocumentPid() != null) {
					documentHeaderDTOs.add(dynamicDocumentHeadersDespatch);
				}
			}
			salesOrderDTO.setDynamicDocumentHeaderDTOs(documentHeaderDTOs);
			salesOrderDTOs.add(salesOrderDTO);
		}
		return salesOrderDTOs;
	}

	@RequestMapping(value = "/get-receipts.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<ReceiptDTO> getReceiptsJson() throws URISyntaxException {
		log.debug("REST request to download receipts : {}");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_118" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description =" get  all Accvoucher  By CompanyId And Status";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<ReceiptDTO> receiptDTOs = new ArrayList<>();
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByCompanyIdAndStatusOrderByCreatedDateDesc();
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
		for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
			for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherHeader
					.getAccountingVoucherDetails()) {
				if (accountingVoucherDetail.getAccountingVoucherAllocations().isEmpty()) {
					ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherDetail);
					receiptDTOs.add(receiptDTO);
				} else {
					for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
							.getAccountingVoucherAllocations()) {
						ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherAllocation);
						receiptDTOs.add(receiptDTO);
					}
				}
			}
		}

		return receiptDTOs;
	}

	@RequestMapping(value = "/v2/get-receipts.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<ReceiptDTO> downloadReceiptsJson() throws URISyntaxException {

		CompanyViewDTO company = companyService.findOne(SecurityUtils.getCurrentUsersCompanyId());
		log.debug("REST request to download receipts <" + company.getLegalName() + "> : {}");

		List<ReceiptDTO> receiptDTOs = new ArrayList<>();

//		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
//				.findByCompanyAndStatusOrderByCreatedDateDesc();

		/*
		 * List<AccountingVoucherHeader> accountingVoucherHeaders =
		 * accountingVoucherHeaderRepository
		 * .findByCompanyAndStatusOrderByCreatedDateDesc();
		 */
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_155" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get by companyId and tally status";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> accountingVoucherHeaders = accountingVoucherHeaderRepository
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
			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);
			List<UserStockLocation> userStockLocations = userStockLocationRepository
					.findAllByCompanyPid(company.getPid());
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
			for (Object[] obj : accountingVoucherHeaders) {

				String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";

				Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[10].toString()))
						.findAny();

				Optional<UserStockLocation> opUserStockLocation = userStockLocations.stream()
						.filter(us -> us.getUser().getPid().equals(opUser.get().getPid())).findAny();

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
						ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherDetail);
						receiptDTO.setAccountingVoucherHeaderDTO(avdObjectToDto(obj, opUser.get(), opDocument.get(),
								opEmployeeProfile.get(), opExe.get(), opAccPro.get(), avDetails));
						if (opUserStockLocation.isPresent()) {
							receiptDTO.setGodownName(opUserStockLocation.get().getStockLocation().getName());
						}
						receiptDTOs.add(receiptDTO);
					} else {
						for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
								.getAccountingVoucherAllocations()) {
							ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherAllocation);
							receiptDTO.setAccountingVoucherHeaderDTO(avdObjectToDto(obj, opUser.get(), opDocument.get(),
									opEmployeeProfile.get(), opExe.get(), opAccPro.get(), avDetails));
							receiptDTO.setHeaderAmount(Double.parseDouble(obj[7].toString()));
							receiptDTO.setNarrationMessage(
									accountingVoucherAllocation.getAccountingVoucherDetail().getRemarks());
							receiptDTO.setProvisionalReceiptNo(accountingVoucherDetail.getProvisionalReceiptNo());
							if (opUserStockLocation.isPresent()) {
								receiptDTO.setGodownName(opUserStockLocation.get().getStockLocation().getName());
							}
							receiptDTOs.add(receiptDTO);
						}
					}

				}

			}
		}

		if (!receiptDTOs.isEmpty()) {
			 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "ACC_QUERY_149" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="Updating AccVoucher Tally download status using  pid and company";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			int updated = accountingVoucherHeaderRepository
					.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING,
							receiptDTOs.stream().map(avh -> avh.getAccountingVoucherHeaderPid())
									.collect(Collectors.toList()));
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
	                logger.info(id + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description);
			log.debug("updated " + updated + " to PROCESSING");
		}
		return receiptDTOs;
	}

	private AccountingVoucherHeaderDTO avdObjectToDto(Object[] obj, User user, Document document,
			EmployeeProfile employee, ExecutiveTaskExecution executiveTaskExecution, AccountProfile accountProfile,
			List<AccountingVoucherDetail> avDetails) {

		AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO();

		accountingVoucherHeaderDTO.setPid(obj[1].toString());
		accountingVoucherHeaderDTO.setDocumentPid(document.getPid());
		accountingVoucherHeaderDTO.setDocumentName(document.getName());
		if (document.getActivityAccount() != null && document.getActivityAccount().equals(AccountTypeColumn.By)) {

			accountingVoucherHeaderDTO.setByAmount(Double.parseDouble(obj[7].toString()));
		} else if (document.getActivityAccount() != null
				&& document.getActivityAccount().equals(AccountTypeColumn.To)) {
			accountingVoucherHeaderDTO.setToAmount(Double.parseDouble(obj[7].toString()));
		}
		if (accountProfile != null) {
			accountingVoucherHeaderDTO.setAccountProfilePid(accountProfile.getPid());
			accountingVoucherHeaderDTO.setAccountProfileName(accountProfile.getName());
			accountingVoucherHeaderDTO.setPhone(accountProfile.getPhone1());
		}

		LocalDateTime createdDate = null;
		if (obj[5] != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String[] splitDate = obj[5].toString().split(" ");
			createdDate = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);
		}

		LocalDateTime documentDate = null;
		if (obj[6] != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String[] splitDate = obj[6].toString().split(" ");
			documentDate = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);
		}

		accountingVoucherHeaderDTO.setCreatedDate(createdDate);
		accountingVoucherHeaderDTO.setDocumentDate(documentDate);
		if (employee != null) {
			accountingVoucherHeaderDTO.setEmployeePid(employee.getPid());
			accountingVoucherHeaderDTO.setEmployeeName(employee.getName());
		}
		if (user != null) {
			accountingVoucherHeaderDTO.setUserName(user.getFirstName());
		}

		accountingVoucherHeaderDTO.setTotalAmount(Double.parseDouble(obj[7].toString()));
		accountingVoucherHeaderDTO.setOutstandingAmount(Double.parseDouble(obj[8].toString()));
		accountingVoucherHeaderDTO.setRemarks(obj[9].toString());
		accountingVoucherHeaderDTO.setDocumentNumberLocal(obj[12].toString());
		accountingVoucherHeaderDTO.setDocumentNumberServer(obj[13].toString());

		double chequeAmount = 0;
		double cashAmount = 0;
		for (AccountingVoucherDetail avd : avDetails) {
			if (avd.getMode() == PaymentMode.Cheque || avd.getMode() == PaymentMode.Bank) {
				chequeAmount += avd.getAmount();
			} else if (avd.getMode() == PaymentMode.Cash) {
				cashAmount += avd.getAmount();
			}
		}
		accountingVoucherHeaderDTO.setCashAmount(cashAmount);
		accountingVoucherHeaderDTO.setChequeAmount(chequeAmount);

		return accountingVoucherHeaderDTO;
	}

//	@RequestMapping(value = "/v2/get-receipts.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public List<ReceiptDTO> downloadReceiptsJson() throws URISyntaxException {
//
//		CompanyViewDTO company = companyService.findOne(SecurityUtils.getCurrentUsersCompanyId());
//		log.debug("REST request to download receipts <" + company.getLegalName() + "> : {}");
//
//		List<ReceiptDTO> receiptDTOs = new ArrayList<>();
//		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
//				.findByCompanyAndStatusOrderByCreatedDateDesc();
//		
//		for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
//			for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherHeader
//					.getAccountingVoucherDetails()) {
//				if (accountingVoucherDetail.getAccountingVoucherAllocations().isEmpty()) {
//					ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherDetail);
//					receiptDTOs.add(receiptDTO);
//				} else {
//					for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
//							.getAccountingVoucherAllocations()) {
//						ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherAllocation);
//						receiptDTO.setHeaderAmount(accountingVoucherHeader.getTotalAmount());
//						receiptDTO.setNarrationMessage(
//								accountingVoucherAllocation.getAccountingVoucherDetail().getRemarks());
//						receiptDTO.setProvisionalReceiptNo(accountingVoucherDetail.getProvisionalReceiptNo());
//						receiptDTOs.add(receiptDTO);
//					}
//				}
//			}
//		}
//
//		if (!receiptDTOs.isEmpty()) {
//			int updated = accountingVoucherHeaderRepository
//					.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING,
//							receiptDTOs.stream().map(avh -> avh.getAccountingVoucherHeaderPid())
//									.collect(Collectors.toList()));
//			log.debug("updated " + updated + " to PROCESSING");
//		}
//		return receiptDTOs;
//	}

	/**
	 * POST /update-receipt-status .
	 *
	 * @param String the List<String> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/update-receipt-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdarteReceiptStatus(@Valid @RequestBody List<String> AccountingVoucherHeaderPids)
			throws URISyntaxException {
		log.debug("REST request to update Accounting Voucher Header Status : {}", AccountingVoucherHeaderPids.size());
		for (String accountingVoucherHeaderPid : AccountingVoucherHeaderPids) {
			Optional<AccountingVoucherHeaderDTO> accountingVoucherHeader = accountingVoucherHeaderService
					.findOneByPid(accountingVoucherHeaderPid);
			if (accountingVoucherHeader.isPresent()) {
				accountingVoucherHeaderService.updateAccountingVoucherHeaderStatus(accountingVoucherHeader.get());
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@RequestMapping(value = "/v2/update-receipt-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdateReceiptStatus(@Valid @RequestBody List<String> accountingVoucherHeaderPids)
			throws URISyntaxException {
		CompanyViewDTO company = companyService.findOne(SecurityUtils.getCurrentUsersCompanyId());
		log.debug("REST request to update Accounting Voucher Header Status <" + company.getLegalName() + "> : {}",
				accountingVoucherHeaderPids.size());
		if (!accountingVoucherHeaderPids.isEmpty()) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "ACC_QUERY_149" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="Updating AccVoucher Tally download status using  pid and company";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			int updated = accountingVoucherHeaderRepository
					.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.COMPLETED,
							accountingVoucherHeaderPids);
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
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /update-order-status .
	 *
	 * @param String the List<String> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/update-order-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdarteOrderStatus(@Valid @RequestBody List<String> inventoryVoucherHeaderPids)
			throws URISyntaxException {
		log.debug("REST request to update Inventory Voucher Header Status : {}", inventoryVoucherHeaderPids.size());

		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaders = inventoryVoucherHeaderService
				.findAllByCompanyIdAndInventoryPidIn(inventoryVoucherHeaderPids);
		if (!inventoryVoucherHeaders.isEmpty()) {
			inventoryVoucherHeaderService.updateInventoryVoucherHeadersStatus(inventoryVoucherHeaders);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	// method used for update order status based on tally response : updating
	// variable TallyDownloadStatus enum
	@RequestMapping(value = "/v2/update-order-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<Void> UpdarteOrderStatusWithTallyStatus(
			@Valid @RequestBody List<String> inventoryVoucherHeaderPids) throws URISyntaxException {

		CompanyViewDTO company = companyService.findOne(SecurityUtils.getCurrentUsersCompanyId());
		log.debug("REST request to update Inventory Voucher Header Status <" + company.getLegalName() + "> : {}",
				inventoryVoucherHeaderPids.size());

		if (!inventoryVoucherHeaderPids.isEmpty()) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_161" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="update InvVoucherHeader TallyDownloadStatus Using Pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);


			int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
					TallyDownloadStatus.COMPLETED, inventoryVoucherHeaderPids);
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
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /get-receipts.json : Create new salesOrders.
	 * 
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/get-ledgers.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<AccountProfileDTO> getLedgersJson() throws URISyntaxException {
		log.debug("REST request to download ledgers : {}");
		List<AccountProfileDTO> allAccountProfileDTOs = accountProfileService
				.findAllByCompanyAndAccountImportStatus(false);

		// seting location name
		allAccountProfileDTOs.forEach(acc -> {
			List<LocationAccountProfile> locationAccountProfileDTOs = locationAccountProfileService
					.findAllByCompanyAndAccountProfilePid(SecurityUtils.getCurrentUsersCompanyId(), acc.getPid());
			if (locationAccountProfileDTOs != null && locationAccountProfileDTOs.size() > 0) {
				acc.setLocation(locationAccountProfileDTOs.get(0).getLocation().getName());
			}
		});

		return allAccountProfileDTOs;
	}

	/**
	 * POST /update-ledgers-status .
	 *
	 * @param String the List<String> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/update-ledgers-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdarteLedgersStatus(@Valid @RequestBody List<String> AccountProfilePids)
			throws URISyntaxException {
		log.debug("REST request to update Account Profile Status : {}", AccountProfilePids.size());
		for (String AccountProfilePid : AccountProfilePids) {
			Optional<AccountProfileDTO> AccountProfileDTO = accountProfileService.findOneByPid(AccountProfilePid);
			if (AccountProfileDTO.isPresent()) {
				accountProfileService.updateAccountProfileImportStatus(AccountProfileDTO.get());
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /send sales from tally .
	 *
	 * @param String the List<inventoryVoucherHeaderDTO> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/tally-accounting-vouchers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> sendReceiptVouchersFromTally(
			@Valid @RequestBody List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs) {
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (!accountingVoucherHeaderDTOs.isEmpty() && opUser.isPresent()) {
			ActivityDTO activityDto = this.getActivityByName("Receipt Data Transfer");
			DocumentDTO documentDto = this.getDocumentByName("receipts from tally");
			LocalDateTime documentDate = accountingVoucherHeaderDTOs.get(0).getDocumentDate();
			// delete InventoryVoucherHeaders for avoidind duplicates and for new or updated
			// sales vouchers
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "ACC_QUERY_138" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get AccVoucher by documentDate,exective Task Execution Pid and document pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
					.findAllByCompanyIdAndDocumentDateAndActivityAndDocumentOrderByCreatedDateDesc(documentDate,
							activityDto.getPid(), documentDto.getPid());
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
			if (!accountingVoucherHeaders.isEmpty()) {
				deleteAccountingVoucherHeaders(accountingVoucherHeaders);
				// return ResponseEntity.ok().body("Receipt already uploaded in this date : " +
				// documentDate);
			}

			int documentUniqueNoToPref = 1;
			for (AccountingVoucherHeaderDTO accountingVoucherHeaderDTO : accountingVoucherHeaderDTOs) {
				documentUniqueNoToPref = documentUniqueNoToPref + 1;
				ExecutiveTaskExecutionDTO eteDTO = new ExecutiveTaskExecutionDTO();
				// get account profile
				Optional<AccountProfileDTO> accountProfileDTO = accountProfileService
						.findByName(accountingVoucherHeaderDTO.getAccountProfileName());
				if (accountProfileDTO.isPresent()) {
					AccountProfileDTO accpDTO = accountProfileDTO.get();
					eteDTO.setAccountProfileName(accpDTO.getName());
					eteDTO.setAccountProfilePid(accpDTO.getPid());
					eteDTO.setAccountTypeName(accpDTO.getAccountTypeName());
					eteDTO.setAccountTypePid(accpDTO.getAccountTypePid());
					eteDTO.setDate(accountingVoucherHeaderDTO.getDocumentDate());
					eteDTO.setActivityName(activityDto.getName());
					eteDTO.setActivityPid(activityDto.getPid());
					eteDTO.setActivityStatus(ActivityStatus.RECEIVED);
					eteDTO.setDate(accountingVoucherHeaderDTO.getDocumentDate());
					eteDTO.setLocationType(LocationType.NoLocation);
					eteDTO.setUserName(opUser.get().getFirstName());
					eteDTO.setUserPid(opUser.get().getPid());

					accountingVoucherHeaderDTO.setAccountProfilePid(accpDTO.getPid());

					LocalDateTime localDateTime = LocalDateTime.now();
					long dateValue = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
					accountingVoucherHeaderDTO.setDocumentNumberLocal(
							dateValue + "_" + accountingVoucherHeaderDTO.getDocumentNumberLocal());
					accountingVoucherHeaderDTO.setDocumentNumberServer(
							dateValue + "_" + accountingVoucherHeaderDTO.getDocumentNumberServer());
					accountingVoucherHeaderDTO.setDocumentPid(documentDto.getPid());
					accountingVoucherHeaderDTO.setDocumentName(documentDto.getName());
					accountingVoucherHeaderDTO.setStatus(true);

					accountingVoucherHeaderDTO.setAccountingVoucherDetails(new ArrayList<>());

					List<AccountingVoucherHeaderDTO> accountingVouchers = new ArrayList<>();
					accountingVouchers.add(accountingVoucherHeaderDTO);
					ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO = new ExecutiveTaskSubmissionDTO();

					System.out.println(accountingVouchers);
					executiveTaskSubmissionDTO.setExecutiveTaskExecutionDTO(eteDTO);
					executiveTaskSubmissionDTO.setAccountingVouchers(accountingVouchers);
					executiveTaskSubmissionService.saveTPExecutiveTaskSubmission(executiveTaskSubmissionDTO,
							opUser.get());
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * POST /send sales from tally .
	 *
	 * @param String the List<inventoryVoucherHeaderDTO> to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/tally-inventory-vouchers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> sendSalesVouchersFromTally(
			@Valid @RequestBody List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs) throws URISyntaxException {
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (!inventoryVoucherHeaderDTOs.isEmpty() && opUser.isPresent()) {
			ActivityDTO activityDto = this.getActivityByName("Sales Data Transfer");
			DocumentDTO documentDto = this.getDocumentByName("sales");
			LocalDateTime documentDate = inventoryVoucherHeaderDTOs.get(0).getDocumentDate();

			List<String> salesLedgerNames = inventoryVoucherHeaderDTOs.stream()
					.map(InventoryVoucherHeaderDTO::getSalesLedgerName).collect(Collectors.toList());
			List<SalesLedger> salesLedgers = new ArrayList<>();

			if (salesLedgerNames.size() > 0) {

				log.info("Saving SalesLedgers......");

				List<SalesLedger> dbSalesLedgers = salesLedgerRepository.findAllByCompanyId();

				List<SalesLedger> newSalesLedgers = new ArrayList<>();

				for (String ledgerName : salesLedgerNames) {
					SalesLedger salesLedger;
					Optional<SalesLedger> optionalSL = dbSalesLedgers.stream()
							.filter(pc -> pc.getName().equalsIgnoreCase(ledgerName)).findFirst();
					if (optionalSL.isPresent()) {
						salesLedger = optionalSL.get();
					} else {
						salesLedger = new SalesLedger();
						salesLedger.setPid(SalesLedgerService.PID_PREFIX + RandomUtil.generatePid());
						salesLedger.setName(ledgerName);
						salesLedger.setCompany(opUser.get().getCompany());
						salesLedger.setActivated(true);
					}
					Optional<SalesLedger> duplicateSL = newSalesLedgers.stream()
							.filter(pc -> pc.getName().equalsIgnoreCase(ledgerName)).findFirst();
					if (duplicateSL.isPresent()) {
						continue;
					}

					newSalesLedgers.add(salesLedger);
				}

				salesLedgers = salesLedgerRepository.save(newSalesLedgers);

				log.info("Saved SalesLedgers Size ......" + salesLedgers.size());

			}

			// delete InventoryVoucherHeaders for avoidind duplicates and for
			// new or updated sales vouchers
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_127" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get all by doc Date order and activity";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByCompanyIdAndDocumentDateAndActivityAndDocumentOrderByCreatedDateDesc(documentDate,
							activityDto.getPid(), documentDto.getPid());
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
		
			
			if (!inventoryVoucherHeaders.isEmpty()) {
				deleteInventoryVoucherHeaders(inventoryVoucherHeaders);
				// return ResponseEntity.ok().body("Sales already uploaded in this date : " +
				// documentDate);
			}
			int documentUniqueNoToPref = 1;
			for (InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO : inventoryVoucherHeaderDTOs) {
				documentUniqueNoToPref = documentUniqueNoToPref + 1;

				ExecutiveTaskExecutionDTO eteDTO = new ExecutiveTaskExecutionDTO();

				// get account profile
				Optional<AccountProfileDTO> accountProfileDTO = accountProfileService
						.findByName(inventoryVoucherHeaderDTO.getReceiverAccountName());

				if (accountProfileDTO.isPresent()) {
					AccountProfileDTO accpDTO = accountProfileDTO.get();
					eteDTO.setAccountProfileName(accpDTO.getName());
					eteDTO.setAccountProfilePid(accpDTO.getPid());
					eteDTO.setAccountTypeName(accpDTO.getAccountTypeName());
					eteDTO.setAccountTypePid(accpDTO.getAccountTypePid());
					eteDTO.setDate(inventoryVoucherHeaderDTO.getDocumentDate());
					eteDTO.setActivityName(activityDto.getName());
					eteDTO.setActivityPid(activityDto.getPid());
					eteDTO.setActivityStatus(ActivityStatus.RECEIVED);
					eteDTO.setDate(inventoryVoucherHeaderDTO.getDocumentDate());
					eteDTO.setLocationType(LocationType.NoLocation);
					eteDTO.setUserName(opUser.get().getFirstName());
					eteDTO.setUserPid(opUser.get().getPid());

					inventoryVoucherHeaderDTO.setReceiverAccountPid(accpDTO.getPid());
					inventoryVoucherHeaderDTO
							.setDocumentNumberLocal(inventoryVoucherHeaderDTO.getDocumentNumberLocal());
					inventoryVoucherHeaderDTO
							.setDocumentNumberServer(inventoryVoucherHeaderDTO.getDocumentNumberServer());
					inventoryVoucherHeaderDTO.setDocumentPid(documentDto.getPid());
					inventoryVoucherHeaderDTO.setDocumentName(documentDto.getName());
					inventoryVoucherHeaderDTO.setStatus(true);

					if (inventoryVoucherHeaderDTO.getInventoryVoucherDetails() == null) {
						log.info(inventoryVoucherHeaderDTO.getReceiverAccountName() + ": Customer");
						return new ResponseEntity<>(
								"Inventory Detail not present for ledger "
										+ inventoryVoucherHeaderDTO.getReceiverAccountName(),
								HttpStatus.EXPECTATION_FAILED);
					}
					for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
							.getInventoryVoucherDetails()) {
						Optional<ProductProfileDTO> productProfileDTO = productProfileService
								.findByName(inventoryVoucherDetailDTO.getProductName());
						if (productProfileDTO.isPresent()) {
							inventoryVoucherDetailDTO.setProductPid(productProfileDTO.get().getPid());
						}
					}
					List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();
					inventoryVouchers.add(inventoryVoucherHeaderDTO);

					ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO = new ExecutiveTaskSubmissionDTO();

					executiveTaskSubmissionDTO.setExecutiveTaskExecutionDTO(eteDTO);
					executiveTaskSubmissionDTO.setInventoryVouchers(inventoryVouchers);

					executiveTaskSubmissionService.saveTPExecutiveTaskSubmission(executiveTaskSubmissionDTO,
							opUser.get());
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * delete InventoryVoucherHeaders for avoidind duplicates and for new or updated
	 * sales vouchers
	 * 
	 * @param inventoryVoucherHeaders
	 */
	@Transactional
	private void deleteInventoryVoucherHeaders(List<InventoryVoucherHeader> inventoryVoucherHeaders) {
		log.debug("request to delete inventory Voucher Headers : {}", inventoryVoucherHeaders.size());
		List<ExecutiveTaskExecution> executivetaskexecutions = inventoryVoucherHeaders.stream()
				.map(a -> a.getExecutiveTaskExecution()).collect(Collectors.toList());
		inventoryVoucherDetailRepository.deleteByInventoryVoucherHeaderIdIn(
				inventoryVoucherHeaders.stream().map(a -> a.getId()).collect(Collectors.toList()));
		inventoryVoucherHeaderRepository.delete(inventoryVoucherHeaders);
		executiveTaskExecutionRepository.delete(executivetaskexecutions);
	}

	/**
	 * delete InventoryVoucherHeaders for avoidind duplicates and for new or updated
	 * sales vouchers
	 * 
	 * @param inventoryVoucherHeaders
	 */
	@Transactional
	private void deleteAccountingVoucherHeaders(List<AccountingVoucherHeader> accountingVoucherHeaders) {
		log.debug("request to delete accounting Voucher Headers : {}", accountingVoucherHeaders.size());
		List<ExecutiveTaskExecution> executivetaskexecutions = accountingVoucherHeaders.stream()
				.map(a -> a.getExecutiveTaskExecution()).collect(Collectors.toList());
		accountingVoucherHeaderRepository.deleteInBatch(accountingVoucherHeaders);
		executiveTaskExecutionRepository.delete(executivetaskexecutions);
	}

	private ActivityDTO getActivityByName(String name) {
		Optional<ActivityDTO> activityDTO = activityService.findByName(name);
		if (activityDTO.isPresent()) {
			return activityDTO.get();
		}
		ActivityDTO activityDto = new ActivityDTO();
		activityDto.setName(name);
		activityDto.setActivated(true);
		activityDto.setHasDefaultAccount(false);
		activityDto.setDescription("Used to send data " + name + " from tally");
		return activityService.save(activityDto);
	}

	private DocumentDTO getDocumentByName(String name) {
		Optional<DocumentDTO> optionalDocument = documentService.findByName(name);
		if (optionalDocument.isPresent()) {
			log.info(name + "Document found");
			return optionalDocument.get();
		}
		log.info(name + "Document not found");
		DocumentDTO documentDto = new DocumentDTO();
		documentDto.setName(name);
		if (name.equals("sales")) {
			documentDto.setDocumentType(DocumentType.INVENTORY_VOUCHER);
			documentDto.setDocumentPrefix("Sales");
		} else {
			documentDto.setDocumentType(DocumentType.ACCOUNTING_VOUCHER);
			documentDto.setDocumentPrefix("Receipts");
		}

		documentDto.setDescription("used to send data " + name + " from tally");
		return documentService.save(documentDto);
	}

	private SalesOrderDTO ivhObjToSalesOrderDTO(Object[] obj, User user, Document document,
			EmployeeProfile employeeProfile, ExecutiveTaskExecution executiveTaskExecution,
			AccountProfile receiverAccountProfile, AccountProfile supplierAccountProfile, PriceLevel priceLevel,
			OrderStatus orderStatus) {
		log.info("ivh to salesorder starts");
		SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
		salesOrderDTO.setInventoryVoucherHeaderPid(obj[9].toString());
		salesOrderDTO.setId(Long.parseLong(obj[0].toString()));
		salesOrderDTO.setLedgerName(receiverAccountProfile.getName());
		salesOrderDTO.setTrimChar(receiverAccountProfile.getTrimChar());
		salesOrderDTO.setLedgerAddress(receiverAccountProfile.getAddress());
		LocalDateTime date = null;
		if (obj[4] != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String[] splitDate = obj[4].toString().split(" ");
			date = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);

		}
		log.info("date split completed");
		salesOrderDTO.setDate(date);
		salesOrderDTO.setDocumentName(document.getName());
		salesOrderDTO.setDocumentAlias(document.getAlias());
		salesOrderDTO.setDocDiscountAmount(Double.parseDouble(obj[2].toString()));
		salesOrderDTO.setDocDiscountPercentage(Double.parseDouble(obj[3].toString()));
		if (receiverAccountProfile.getDefaultPriceLevel() != null) {
			salesOrderDTO.setPriceLevel(receiverAccountProfile.getDefaultPriceLevel().getName());
		}
		salesOrderDTO.setInventoryVoucherHeaderDTO(ivhObjectToivhDTO(obj, user, document, employeeProfile,
				executiveTaskExecution, receiverAccountProfile, supplierAccountProfile, priceLevel, orderStatus));
		log.info("function callss.....");
		salesOrderDTO.setLedgerState(receiverAccountProfile.getStateName());
		salesOrderDTO.setLedgerCountry(receiverAccountProfile.getCountryName());
		salesOrderDTO.setLedgerGstType(receiverAccountProfile.getGstRegistrationType());
		salesOrderDTO.setLedgerPinCode(receiverAccountProfile.getPin() != null ? receiverAccountProfile.getPin() : "");
		if (employeeProfile != null) {
			salesOrderDTO.setEmployeeAlias(employeeProfile.getAlias());
		}
		salesOrderDTO.setActivityRemarks(
				executiveTaskExecution.getRemarks() != null ? executiveTaskExecution.getRemarks() : "");
		return salesOrderDTO;
	}

	private InventoryVoucherHeaderDTO ivhObjectToivhDTO(Object[] obj, User user, Document document,
			EmployeeProfile employeeProfile, ExecutiveTaskExecution executiveTaskExecution,
			AccountProfile receiverAccountProfile, AccountProfile supplierAccountProfile, PriceLevel priceLevel,
			OrderStatus orderStatus) {
		log.info("function starts .... ivhObjectToivhDTO");
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();

		inventoryVoucherHeaderDTO.setPid(obj[9].toString());
		inventoryVoucherHeaderDTO.setDocumentNumberLocal(obj[5].toString());
		inventoryVoucherHeaderDTO.setDocumentNumberServer(obj[6].toString());

		if (document != null) {
			inventoryVoucherHeaderDTO.setDocumentPid(document.getPid());
			inventoryVoucherHeaderDTO.setDocumentName(document.getName());
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime createdDate = null;
		LocalDateTime documentDate = null;
		if (obj[1] != null) {
			String[] splitDate = obj[1].toString().split(" ");
			createdDate = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);

		}
		if (obj[4] != null) {
			String[] splitDate = obj[4].toString().split(" ");
			documentDate = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);

		}
		log.info("date parsing completed");
		inventoryVoucherHeaderDTO.setCreatedDate(createdDate);
		inventoryVoucherHeaderDTO.setDocumentDate(documentDate);

		if (receiverAccountProfile != null) {
			inventoryVoucherHeaderDTO.setReceiverAccountPid(receiverAccountProfile.getPid());
			inventoryVoucherHeaderDTO.setReceiverAccountName(receiverAccountProfile.getName());
		}

		inventoryVoucherHeaderDTO.setProcessStatus(obj[22].toString());

		if (supplierAccountProfile != null) {
			inventoryVoucherHeaderDTO.setSupplierAccountPid(supplierAccountProfile.getPid());
			inventoryVoucherHeaderDTO.setSupplierAccountName(supplierAccountProfile.getName());
		}

		if (employeeProfile != null) {
			inventoryVoucherHeaderDTO.setEmployeePid(employeeProfile.getPid());
			inventoryVoucherHeaderDTO.setEmployeeName(employeeProfile.getName());
		}
		if (user != null) {
			inventoryVoucherHeaderDTO.setUserName(user.getFirstName());
		}

		inventoryVoucherHeaderDTO.setDocumentTotal(Double.parseDouble(obj[7].toString()));
		inventoryVoucherHeaderDTO.setDocumentVolume(Double.parseDouble(obj[8].toString()));
		inventoryVoucherHeaderDTO.setDocDiscountAmount(Double.parseDouble(obj[2].toString()));
		inventoryVoucherHeaderDTO.setDocDiscountPercentage(Double.parseDouble(obj[3].toString()));
		inventoryVoucherHeaderDTO.setStatus(Boolean.valueOf(obj[10].toString()));

		if (priceLevel != null) {
			inventoryVoucherHeaderDTO.setPriceLevelPid(priceLevel.getPid());
			inventoryVoucherHeaderDTO.setPriceLevelName(priceLevel.getName());
		}
		if (orderStatus != null) {
			inventoryVoucherHeaderDTO.setOrderStatusId(orderStatus.getId());
			inventoryVoucherHeaderDTO.setOrderStatusName(orderStatus.getName());
		}

		if (obj[26] != null) {
			inventoryVoucherHeaderDTO.setTallyDownloadStatus(TallyDownloadStatus.valueOf(obj[26].toString()));

		}
		log.info("setOrderNumber obj[27]");
		inventoryVoucherHeaderDTO.setOrderNumber(obj[27] == null ? 0 : Long.parseLong(obj[27].toString()));

		inventoryVoucherHeaderDTO.setCustomeraddress(receiverAccountProfile.getAddress());
		inventoryVoucherHeaderDTO.setCustomerEmail(receiverAccountProfile.getEmail1());
		inventoryVoucherHeaderDTO.setCustomerPhone(receiverAccountProfile.getPhone1());
		inventoryVoucherHeaderDTO.setVisitRemarks(
				executiveTaskExecution.getRemarks() == null ? "" : executiveTaskExecution.getRemarks());

		inventoryVoucherHeaderDTO.setPdfDownloadStatus(Boolean.getBoolean(obj[28].toString()));
		if (obj[29] != null) {
			inventoryVoucherHeaderDTO.setSalesManagementStatus(SalesManagementStatus.valueOf(obj[29].toString()));

		}
		inventoryVoucherHeaderDTO.setDocumentTotalUpdated(Double.parseDouble(obj[30].toString()));
		inventoryVoucherHeaderDTO.setDocumentVolumeUpdated(Double.parseDouble(obj[31].toString()));
		inventoryVoucherHeaderDTO.setUpdatedStatus(Boolean.getBoolean(obj[32].toString()));
		if (inventoryVoucherHeaderDTO.getUpdatedStatus()) {
			inventoryVoucherHeaderDTO.setDocumentTotal(inventoryVoucherHeaderDTO.getDocumentTotalUpdated());
			inventoryVoucherHeaderDTO.setDocumentVolume(inventoryVoucherHeaderDTO.getDocumentVolumeUpdated());
		}
		return inventoryVoucherHeaderDTO;
	}
}
