package com.orderfleet.webapp.web.vendor.odoo.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.UnitOfMeasureProduct;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureProductRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.util.RestClientUtil;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooInvoiceLine;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsOdooInvoiceMulti;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsOneOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ParamsReceiptOdoo;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdooInvoiceMulti;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOdooReceipt;
import com.orderfleet.webapp.web.vendor.odoo.dto.RequestBodyOneOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOdoo;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseBodyOneOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResponseMessageOdooInvoice;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResultOdoo;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResultOdooReceipt;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class SendInvoiceOdooService {

	private final Logger log = LoggerFactory.getLogger(SendInvoiceOdooService.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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

	private static String SEND_INVOICES_API_URL = "http://edappal.nellara.com:1214/web/api/create_invoices";

	private static String SEND_SINGLE_INVOICE_API_URL = "http://edappal.nellara.com:1214/web/api/create_invoice";

	public static int successCount = 0;
	public static int failedCount = 0;
	public static int unattendedCount = 0;

	@Transactional
	public void sendSalesOrder() {
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		log.debug("REST request to download sales orders <" + company.getLegalName() + "> : {}");

		List<OdooInvoice> odooInvoices = new ArrayList<>();
		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();

		List<VoucherType> documentVoucherTypes = Arrays.asList(VoucherType.PRIMARY_SALES,VoucherType.PRIMARY_SALES_ORDER);
		log.debug("Document Voucher Types "+documentVoucherTypes.toString());
//		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
//				company.getId());

		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypesAndCompany(documentVoucherTypes,
				company.getId());



		if (primarySecDoc.isEmpty()) {
			log.info("........No PrimarySecondaryDocument configuration Available...........");
			// return salesOrderDTOs;
		}

		List<Long> documentIdList = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		List<String> inventoryHeaderPids = new ArrayList<String>();
		String companyPid = company.getPid();

		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);

		List<Object[]> inventoryVoucherHeaders = new ArrayList<>();

		if (optSalesManagement.isPresent() && optSalesManagement.get().getValue().equalsIgnoreCase("true")) {
			// inventoryVoucherHeaders =
			// inventoryVoucherHeaderRepository.findByCompanyIdAndTallyStatusAndSalesManagementStatusOrderByCreatedDateDesc();
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_207" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="Finding By companyId,Tally status and sales mgmt Status";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndSalesManagementStatusAndDocumentOrderByCreatedDateAscLimit(
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
			String id = "INV_QUERY_208" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="Finding by Tally status,CompanyId and DocOrder";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndDocumentOrderByCreatedDateAscLimit(documentIdList);
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
				inventoryHeaderPids.add(obj[9].toString());
				userIds.add(Long.parseLong(obj[12].toString()));
				documentIds.add(Long.parseLong(obj[13].toString()));
				employeeIds.add(Long.parseLong(obj[14].toString()));
				exeIds.add(Long.parseLong(obj[15].toString()));
				receiverAccountProfileIds.add(Long.parseLong(obj[16].toString()));
				supplierAccountProfileIds.add(Long.parseLong(obj[17].toString()));
				priceLeveIds.add(obj[18] != null ? Long.parseLong(obj[18].toString()) : 0);
				orderStatusIds.add(obj[23] != null ? Long.parseLong(obj[23].toString()) : 0);

			}

			if (!ivhPids.isEmpty()) {
				 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "INV_QUERY_161" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="update InvVoucherHeader TallyDownloadStatus Using Pid";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
						TallyDownloadStatus.PROCESSING, inventoryHeaderPids);
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
//			List<AccountProfile> supplierAccountProfiles = accountProfileRepository
//					.findAllByCompanyIdAndIdsIn(receiverAccountProfileIds);

			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);

			List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
					.findAllByInventoryVoucherHeaderPidIn(ivhPids);
			List<UserStockLocation> userStockLocations = userStockLocationRepository.findAllByCompanyPid(companyPid);
			List<UnitOfMeasureProduct> unitOfMeasureProducts = unitOfMeasureProductRepository.findAllByCompanyId();

			for (Object[] obj : inventoryVoucherHeaders) {

				Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[12].toString()))
						.findAny();

				Optional<AccountProfile> opRecAccPro = receiverAccountProfiles.stream()
						.filter(a -> a.getId() == Long.parseLong(obj[16].toString())).findAny();

				Optional<UserStockLocation> opUserStockLocation = userStockLocations.stream()
						.filter(us -> us.getUser().getPid().equals(opUser.get().getPid())).findAny();

				OdooInvoice odooInvoice = new OdooInvoice();

				LocalDateTime date = null;
				if (obj[4] != null) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					String[] splitDate = obj[4].toString().split(" ");
					date = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);

				}

				DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				odooInvoice.setInvoice_date(date.format(formatter1));
				odooInvoice.setReference(obj[6].toString());
				odooInvoice.setLocation_id(Long.parseLong(opUserStockLocation.get().getStockLocation().getAlias()));

				odooInvoice.setPartner_id(
						opRecAccPro.get().getCustomerId() != null && !opRecAccPro.get().getCustomerId().equals("")
								? Long.parseLong(opRecAccPro.get().getCustomerId())
								: 0);
				odooInvoice.setJournal_type("sale");
				odooInvoice.setType("out_invoice");

				odooInvoice.setRounding_amt(obj[34] != null ? Double.parseDouble(obj[34].toString()) : 0.0);

				odooInvoice.setOrigin(obj[6].toString());

				List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetails.stream()
						.filter(ivd -> ivd.getInventoryVoucherHeader().getId() == Long.parseLong(obj[0].toString()))
						.collect(Collectors.toList()).stream()
						.sorted(Comparator.comparingLong(InventoryVoucherDetail::getId)).collect(Collectors.toList());

				List<OdooInvoiceLine> odooInvoiceLines = new ArrayList<OdooInvoiceLine>();
				for (InventoryVoucherDetail inventoryVoucherDetail : ivDetails) {

					OdooInvoiceLine odooInvoiceLine = new OdooInvoiceLine();

					odooInvoiceLine.setIs_foc(
							inventoryVoucherDetail.getFreeQuantity() > 0.0 ? inventoryVoucherDetail.getFreeQuantity()
									: 0.0);

					odooInvoiceLine.setDiscount(inventoryVoucherDetail.getDiscountPercentage());

					odooInvoiceLine.setProduct_id(inventoryVoucherDetail.getProduct().getProductId() != null
							&& !inventoryVoucherDetail.getProduct().getProductId().equals("")
									? Long.parseLong(inventoryVoucherDetail.getProduct().getProductId())
									: 0);
					odooInvoiceLine.setPrice_unit(inventoryVoucherDetail.getSellingRate());
					odooInvoiceLine.setQuantity(inventoryVoucherDetail.getQuantity());

					Optional<UnitOfMeasureProduct> opUnitOfMeasure = unitOfMeasureProducts.stream()
							.filter(us -> us.getProduct().getPid().equals(inventoryVoucherDetail.getProduct().getPid()))
							.findAny();

					if (opUnitOfMeasure.isPresent()) {
						odooInvoiceLine.setUom_id(Long.parseLong(opUnitOfMeasure.get().getUnitOfMeasure().getUomId()));
					}

					odooInvoiceLines.add(odooInvoiceLine);
				}

				odooInvoice.setInvoice_lines(odooInvoiceLines);

				// inventoryHeaderPids.add(obj[9].toString());

				odooInvoices.add(odooInvoice);
			}

			log.info("Sending (" + odooInvoices.size() + ") Invoices to Odoo....");

			RequestBodyOdooInvoiceMulti request = new RequestBodyOdooInvoiceMulti();

			request.setJsonrpc("2.0");

			ParamsOdooInvoiceMulti params = new ParamsOdooInvoiceMulti();
			params.setCreate_multi(true);
			params.setInvoices(odooInvoices);

			request.setParams(params);

			HttpEntity<RequestBodyOdooInvoiceMulti> entity = new HttpEntity<>(request,
					RestClientUtil.createTokenAuthHeaders());

			log.info(entity.getBody().toString() + "");

			ObjectMapper Obj = new ObjectMapper();

			// get object as a json string
			String jsonStr;
			try {
				jsonStr = Obj.writeValueAsString(request);
				log.info(jsonStr);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Displaying JSON String

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			log.info("Get URL: " + SEND_INVOICES_API_URL);

			try {

				ResponseBodyOdooInvoice responseBodyOdooInvoice = restTemplate.postForObject(SEND_INVOICES_API_URL,
						entity, ResponseBodyOdooInvoice.class);
				log.info(responseBodyOdooInvoice + "");

				// get object as a json string
				String jsonStr1;
				try {
					jsonStr1 = Obj.writeValueAsString(responseBodyOdooInvoice);
					log.info(jsonStr1);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				log.info("Odoo Invoice Created Success Size= " + responseBodyOdooInvoice.getResult().getMessage().size()
						+ "------------");

				changeServerDownloadStatus(responseBodyOdooInvoice.getResult().getMessage());

			} catch (HttpClientErrorException exception) {
				if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
					// throw new ServiceException(exception.getResponseBodyAsString());
					log.info(exception.getResponseBodyAsString());
					log.info("-------------------------");
					log.info(exception.getMessage());
					log.info("-------------------------");
					exception.printStackTrace();
				}
				log.info(exception.getMessage());
				// throw new ServiceException(exception.getMessage());
			} catch (Exception exception) {

				log.info(exception.getMessage());
				log.info("-------------------------");
				exception.printStackTrace();
				log.info("-------------------------");

				// throw new ServiceException(exception.getMessage());
			}
		}

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	private void changeServerDownloadStatus(List<ResponseMessageOdooInvoice> message) {

		if (!message.isEmpty()) {

			List<String> successReferences = new ArrayList<>();
			List<String> failedReferences = new ArrayList<>();
			List<String> unattendedReferences = new ArrayList<>();
			for (ResponseMessageOdooInvoice response : message) {

				if (response.getStatus_code() == 0) {
					successReferences.add(response.getReference());
				} else if (response.getStatus_code() == 1) {
					failedReferences.add(response.getReference());
				} else {
					unattendedReferences.add(response.getReference());
				}
			}

			log.info("Total Responses :- " + message.size() + "-----Success Responses :-" + successReferences.size()
					+ "-----Failed Responses :-" + failedReferences.size() + "-----Unattended Responses :-"
					+ unattendedReferences.size());

//			List<String> inventoryHeaderPid = inventoryVoucherHeaderRepository
//					.findAllByDocumentNumberServer(references);

			List<InventoryVoucherHeader> successInventoryHeaders = new ArrayList<>();
			List<InventoryVoucherHeader> successdistinctElements = new ArrayList<>();

			if (successReferences.size() > 0) {
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_200" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="Find all headers by documnetNumberServer";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				successInventoryHeaders = inventoryVoucherHeaderRepository
						.findAllHeaderdByDocumentNumberServer(successReferences);
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

				successdistinctElements = successInventoryHeaders.stream().distinct().collect(Collectors.toList());
			}
			List<InventoryVoucherHeader> failedInventoryHeaders = new ArrayList<>();
			List<InventoryVoucherHeader> faileddistinctElements = new ArrayList<>();
			if (failedReferences.size() > 0) {
				failedInventoryHeaders = inventoryVoucherHeaderRepository
						.findAllHeaderdByDocumentNumberServer(failedReferences);
				faileddistinctElements = failedInventoryHeaders.stream().distinct().collect(Collectors.toList());
			}

			List<InventoryVoucherHeader> unattendedInventoryHeaders = new ArrayList<>();
			List<InventoryVoucherHeader> unuttendeddistinctElements = new ArrayList<>();
			if (unattendedReferences.size() > 0) {
				unattendedInventoryHeaders = inventoryVoucherHeaderRepository
						.findAllHeaderdByDocumentNumberServer(unattendedReferences);
				unuttendeddistinctElements = unattendedInventoryHeaders.stream().distinct()
						.collect(Collectors.toList());
			}

			List<InventoryVoucherHeader> updatedList = new ArrayList<>();

			successCount = 0;
			for (InventoryVoucherHeader ivh : successdistinctElements) {

				InventoryVoucherHeader newIvh = ivh;

				message.stream().filter(a -> a.getReference().equalsIgnoreCase(ivh.getDocumentNumberServer())).findAny()
						.ifPresent(a -> {
							newIvh.setErpReferenceNumber(a.getId());
							newIvh.setErpStatus(a.getStatus_message());
							newIvh.setTallyDownloadStatus(TallyDownloadStatus.COMPLETED);
							updatedList.add(newIvh);
							successCount++;
						});
			}

			log.debug("updated " + successCount + " to Completed");

			failedCount = 0;
			for (InventoryVoucherHeader ivh : faileddistinctElements) {

				InventoryVoucherHeader newIvh = ivh;

				message.stream().filter(a -> a.getReference().equalsIgnoreCase(ivh.getDocumentNumberServer())).findAny()
						.ifPresent(a -> {
							newIvh.setErpStatus(a.getStatus_message());
							newIvh.setTallyDownloadStatus(TallyDownloadStatus.FAILED);
							updatedList.add(newIvh);
							failedCount++;
						});
			}

			if (failedCount > 0) {
				log.debug("updated " + failedCount + " to Failed");
			}

			unattendedCount = 0;
			for (InventoryVoucherHeader ivh : unuttendeddistinctElements) {

				InventoryVoucherHeader newIvh = ivh;

				message.stream().filter(a -> a.getReference().equalsIgnoreCase(ivh.getDocumentNumberServer())).findAny()
						.ifPresent(a -> {
							newIvh.setErpStatus(a.getStatus_message());
							newIvh.setTallyDownloadStatus(TallyDownloadStatus.PENDING);
							updatedList.add(newIvh);
							unattendedCount++;
						});
			}
			inventoryVoucherHeaderRepository.save(updatedList);

			if (unattendedCount > 0) {
				log.debug("updated " + unattendedCount + " to Pending");
			}
		}
	}

	public void sendSalesReturnOdoo() {
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		log.debug("REST request to download sales orders <" + company.getLegalName() + "> : {}");

		List<OdooInvoice> odooInvoices = new ArrayList<>();
		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES_RETURN,
				company.getId());

		if (primarySecDoc.isEmpty()) {
			log.info("........No PrimarySecondaryDocument configuration Available...........");
			// return salesOrderDTOs;
		}
		List<Long> documentIdList = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		List<String> inventoryHeaderPids = new ArrayList<String>();
		String companyPid = company.getPid();

		Optional<CompanyConfiguration> optSalesManagement = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.SALES_MANAGEMENT);

		List<Object[]> inventoryVoucherHeaders = new ArrayList<>();

		if (optSalesManagement.isPresent() && optSalesManagement.get().getValue().equalsIgnoreCase("true")) {
			// inventoryVoucherHeaders =
			// inventoryVoucherHeaderRepository.findByCompanyIdAndTallyStatusAndSalesManagementStatusOrderByCreatedDateDesc();
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_207" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="Finding By companyId,Tally status and sales mgmt Status";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndSalesManagementStatusAndDocumentOrderByCreatedDateAscLimit(
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
			String id = "INV_QUERY_208" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="Finding by Tally status,CompanyId and DocOrder";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByCompanyIdAndTallyStatusAndDocumentOrderByCreatedDateAscLimit(documentIdList);
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
				inventoryHeaderPids.add(obj[9].toString());
				userIds.add(Long.parseLong(obj[12].toString()));
				documentIds.add(Long.parseLong(obj[13].toString()));
				employeeIds.add(Long.parseLong(obj[14].toString()));
				exeIds.add(Long.parseLong(obj[15].toString()));
				receiverAccountProfileIds.add(Long.parseLong(obj[16].toString()));
				supplierAccountProfileIds.add(Long.parseLong(obj[17].toString()));
				priceLeveIds.add(obj[18] != null ? Long.parseLong(obj[18].toString()) : 0);
				orderStatusIds.add(obj[23] != null ? Long.parseLong(obj[23].toString()) : 0);

			}

			if (!ivhPids.isEmpty()) {
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_161" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="update InvVoucherHeader TallyDownloadStatus Using Pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
						TallyDownloadStatus.PROCESSING, inventoryHeaderPids);
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

			List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
					.findAllByInventoryVoucherHeaderPidIn(ivhPids);
			List<UserStockLocation> userStockLocations = userStockLocationRepository.findAllByCompanyPid(companyPid);
			List<UnitOfMeasureProduct> unitOfMeasureProducts = unitOfMeasureProductRepository.findAllByCompanyId();

			for (Object[] obj : inventoryVoucherHeaders) {

				Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[12].toString()))
						.findAny();

				Optional<AccountProfile> opRecAccPro = receiverAccountProfiles.stream()
						.filter(a -> a.getId() == Long.parseLong(obj[16].toString())).findAny();

				Optional<AccountProfile> opSupAccPro = supplierAccountProfiles.stream()
						.filter(a -> a.getId() == Long.parseLong(obj[17].toString())).findAny();

				Optional<UserStockLocation> opUserStockLocation = userStockLocations.stream()
						.filter(us -> us.getUser().getPid().equals(opUser.get().getPid())).findAny();

				OdooInvoice odooInvoice = new OdooInvoice();

				LocalDateTime date = null;
				if (obj[4] != null) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					String[] splitDate = obj[4].toString().split(" ");
					date = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);

				}

				DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				odooInvoice.setInvoice_date(date.format(formatter1));
				odooInvoice.setReference(obj[6].toString());
				odooInvoice.setLocation_id(Long.parseLong(opUserStockLocation.get().getStockLocation().getAlias()));

				odooInvoice.setPartner_id(
						opSupAccPro.get().getCustomerId() != null && !opSupAccPro.get().getCustomerId().equals("")
								? Long.parseLong(opSupAccPro.get().getCustomerId())
								: 0);
				odooInvoice.setJournal_type("sale_refund");
				odooInvoice.setType("out_refund");

				odooInvoice.setRounding_amt(obj[34] != null ? Double.parseDouble(obj[34].toString()) : 0.0);

				odooInvoice.setOrigin(obj[33] != null ? obj[33].toString() : obj[6].toString());

				List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetails.stream()
						.filter(ivd -> ivd.getInventoryVoucherHeader().getId() == Long.parseLong(obj[0].toString()))
						.collect(Collectors.toList()).stream()
						.sorted(Comparator.comparingLong(InventoryVoucherDetail::getId)).collect(Collectors.toList());

				List<OdooInvoiceLine> odooInvoiceLines = new ArrayList<OdooInvoiceLine>();
				for (InventoryVoucherDetail inventoryVoucherDetail : ivDetails) {

					OdooInvoiceLine odooInvoiceLine = new OdooInvoiceLine();

					odooInvoiceLine.setDiscount(inventoryVoucherDetail.getDiscountPercentage());
					odooInvoiceLine.setIs_foc(
							inventoryVoucherDetail.getFreeQuantity() > 0.0 ? inventoryVoucherDetail.getFreeQuantity()
									: 0.0);
					odooInvoiceLine.setProduct_id(inventoryVoucherDetail.getProduct().getProductId() != null
							&& !inventoryVoucherDetail.getProduct().getProductId().equals("")
									? Long.parseLong(inventoryVoucherDetail.getProduct().getProductId())
									: 0);
					odooInvoiceLine.setPrice_unit(inventoryVoucherDetail.getSellingRate());
					odooInvoiceLine.setQuantity(inventoryVoucherDetail.getQuantity());

					Optional<UnitOfMeasureProduct> opUnitOfMeasure = unitOfMeasureProducts.stream()
							.filter(us -> us.getProduct().getPid().equals(inventoryVoucherDetail.getProduct().getPid()))
							.findAny();

					if (opUnitOfMeasure.isPresent()) {
						odooInvoiceLine.setUom_id(Long.parseLong(opUnitOfMeasure.get().getUnitOfMeasure().getUomId()));
					}

					odooInvoiceLines.add(odooInvoiceLine);
				}

				odooInvoice.setInvoice_lines(odooInvoiceLines);

				// inventoryHeaderPids.add(obj[9].toString());

				odooInvoices.add(odooInvoice);
			}

			log.info("Sending (" + odooInvoices.size() + ") Invoices to Odoo....");

			RequestBodyOdooInvoiceMulti request = new RequestBodyOdooInvoiceMulti();

			request.setJsonrpc("2.0");

			ParamsOdooInvoiceMulti params = new ParamsOdooInvoiceMulti();
			params.setCreate_multi(true);
			params.setInvoices(odooInvoices);

			request.setParams(params);

			HttpEntity<RequestBodyOdooInvoiceMulti> entity = new HttpEntity<>(request,
					RestClientUtil.createTokenAuthHeaders());

			log.info(entity.getBody().toString() + "");

			ObjectMapper Obj = new ObjectMapper();

			// get object as a json string
			String jsonStr;
			try {
				jsonStr = Obj.writeValueAsString(request);
				log.info(jsonStr);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Displaying JSON String

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			log.info("Get URL: " + SEND_INVOICES_API_URL);

			try {

				ResponseBodyOdooInvoice responseBodyOdooInvoice = restTemplate.postForObject(SEND_INVOICES_API_URL,
						entity, ResponseBodyOdooInvoice.class);
				log.info(responseBodyOdooInvoice + "");

				// get object as a json string
				String jsonStr1;
				try {
					jsonStr1 = Obj.writeValueAsString(responseBodyOdooInvoice);
					log.info(jsonStr1);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.info("Odoo Invoice Created Success Size= " + responseBodyOdooInvoice.getResult().getMessage().size()
						+ "------------");

				changeServerDownloadStatus(responseBodyOdooInvoice.getResult().getMessage());

			} catch (HttpClientErrorException exception) {
				if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
					// throw new ServiceException(exception.getResponseBodyAsString());
					log.info(exception.getResponseBodyAsString());
					log.info("-------------------------");
					log.info(exception.getMessage());
					log.info("-------------------------");
					exception.printStackTrace();
				}
				log.info(exception.getMessage());
				// throw new ServiceException(exception.getMessage());
			} catch (Exception exception) {
				log.info(exception.getMessage());
				log.info("-------------------------");
				exception.printStackTrace();
				log.info("-------------------------");

				// throw new ServiceException(exception.getMessage());
			}
		}
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	public void sendInvoiceAsync(List<InventoryVoucherHeader> inventoryVouchers) {
		log.debug("Send invoice to  Odoo");

		String companyPid = inventoryVouchers.get(0).getCompany().getPid();
		Long companyId = inventoryVouchers.get(0).getCompany().getId();
		String documentNumber = inventoryVouchers.get(0).getDocumentNumberServer() + "";
		log.info(documentNumber + "--Document Number");
		InventoryVoucherHeader obj = inventoryVouchers.get(0);

		log.debug("Creating Registeries");
		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		List<PrimarySecondaryDocument> primarySalesorderDoc = new ArrayList<>();

		log.debug("Fetching  Document Types ");
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
				companyId);
		primarySalesorderDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES_ORDER,
				companyId);

		if (primarySecDoc.isEmpty() && primarySalesorderDoc.isEmpty()) {
			log.info("........No PrimarySecondaryDocument configuration Available...........");
			// return salesOrderDTOs;
		}

		log.debug("Assigning documets");
		Document document = primarySecDoc.get(0).getDocument();
		Document primarySalesOrderDocument = primarySalesorderDoc.get(0).getDocument();

		log.debug(" Document = "+ document.getName() + " : "+ document.getPid() + " == " + obj.getDocument().getPid());
		log.debug(" Document = "+ primarySalesOrderDocument.getName() + " : "+ primarySalesOrderDocument.getPid() + " == " + obj.getDocument().getPid());

		if(document.getPid().equals(obj.getDocument().getPid())){
			log.debug("Document Name : " + document.getName());
		}

		if(primarySalesOrderDocument.getPid().equals(obj.getDocument().getPid())){
			log.debug("Document Name : " + primarySalesOrderDocument.getName());
		}

		log.debug("Processing Order To Odoo");
		if (document.getPid().equals(obj.getDocument().getPid())  || primarySalesOrderDocument.getPid().equals(obj.getDocument().getPid())) {
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			Set<Long> userIds = new HashSet<>();
			List<String> accountPids = new ArrayList<>();

			userIds.add(obj.getEmployee().getUser().getId());
			accountPids.add(obj.getReceiverAccount().getPid());

			List<User> users = userRepository.findAllByCompanyIdAndIdIn(companyId, userIds);

			List<UserStockLocation> userStockLocations = userStockLocationRepository.findAllByCompanyPid(companyPid);

			List<UnitOfMeasureProduct> unitOfMeasureProducts = unitOfMeasureProductRepository
					.findAllByCompanyPid(companyPid);

			Optional<User> opUser = users.stream()
					.filter(u -> u.getId() == Long.parseLong(obj.getCreatedBy().getId().toString())).findAny();

			Optional<UserStockLocation> opUserStockLocation = userStockLocations.stream()
					.filter(us -> us.getUser().getPid().equals(opUser.get().getPid())).findAny();

			ParamsOdooInvoice odooInvoice = new ParamsOdooInvoice();

			odooInvoice.setCreate(true);

			odooInvoice.setInvoice_date(obj.getDocumentDate().format(formatter1));

			odooInvoice.setReference(obj.getDocumentNumberServer());
			odooInvoice.setLocation_id(Long.parseLong(opUserStockLocation.get().getStockLocation().getAlias()));

			odooInvoice.setPartner_id(obj.getReceiverAccount().getCustomerId() != null
					&& !obj.getReceiverAccount().getCustomerId().equals("")
							? Long.parseLong(obj.getReceiverAccount().getCustomerId())
							: 0);
			odooInvoice.setJournal_type("sale");
			odooInvoice.setType("out_invoice");

			odooInvoice.setRounding_amt(obj.getRoundedOff());

			List<OdooInvoiceLine> odooInvoiceLines = new ArrayList<OdooInvoiceLine>();
			for (InventoryVoucherDetail inventoryVoucherDetail : obj.getInventoryVoucherDetails()) {

				OdooInvoiceLine odooInvoiceLine = new OdooInvoiceLine();

				odooInvoiceLine.setIs_foc(
						inventoryVoucherDetail.getFreeQuantity() > 0.0 ? inventoryVoucherDetail.getFreeQuantity()
								: 0.0);

				odooInvoiceLine.setDiscount(inventoryVoucherDetail.getDiscountPercentage());

				odooInvoiceLine.setProduct_id(inventoryVoucherDetail.getProduct().getProductId() != null
						&& !inventoryVoucherDetail.getProduct().getProductId().equals("")
								? Long.parseLong(inventoryVoucherDetail.getProduct().getProductId())
								: 0);
				odooInvoiceLine.setPrice_unit(inventoryVoucherDetail.getSellingRate());
				odooInvoiceLine.setQuantity(inventoryVoucherDetail.getQuantity());

				Optional<UnitOfMeasureProduct> opUnitOfMeasure = unitOfMeasureProducts.stream()
						.filter(us -> us.getProduct().getPid().equals(inventoryVoucherDetail.getProduct().getPid()))
						.findAny();

				if (opUnitOfMeasure.isPresent()) {
					odooInvoiceLine.setUom_id(Long.parseLong(opUnitOfMeasure.get().getUnitOfMeasure().getUomId()));
				}

				odooInvoiceLines.add(odooInvoiceLine);
			}

			odooInvoice.setInvoice_lines(odooInvoiceLines);

			log.debug("sending Sales Order To Odoo");
			sendToOdooSingle(odooInvoice, obj);
		}

	}

	private void sendToOdooSingle(ParamsOdooInvoice odooParam, InventoryVoucherHeader inventoryVoucher) {
		log.info("Sending (" + odooParam.getReference() + ") Invoices to Odoo...." + inventoryVoucher.getId());

		inventoryVoucher.setTallyDownloadStatus(TallyDownloadStatus.PROCESSING);

		Set<String> inventoryVoucherPids = new HashSet<>();

		inventoryVoucherPids.add(inventoryVoucher.getPid());

		List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
				.findAllByInventoryVoucherHeaderPidIn(inventoryVoucherPids);

		if (inventoryVoucherDetails.size() > 0) {
			inventoryVoucher.setInventoryVoucherDetails(inventoryVoucherDetails);
		}
		inventoryVoucherHeaderRepository.save(inventoryVoucher);
		log.debug("updated to PROCESSING");

		RequestBodyOdooInvoice request = new RequestBodyOdooInvoice();

		request.setJsonrpc("2.0");

		request.setParams(odooParam);

		HttpEntity<RequestBodyOdooInvoice> entity = new HttpEntity<>(request, RestClientUtil.createTokenAuthHeaders());

		log.info(entity.getBody().toString() + "");

		ObjectMapper Obj = new ObjectMapper();

		// get object as a json string
		String jsonStr;
		try {
			jsonStr = Obj.writeValueAsString(request);
			log.info(jsonStr);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Displaying JSON String

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		log.info("Get URL: " + SEND_SINGLE_INVOICE_API_URL);

		try {

			ResponseBodyOdoo responseBodyOdooInvoice = restTemplate.postForObject(SEND_SINGLE_INVOICE_API_URL, entity,
					ResponseBodyOdoo.class);
			log.info(responseBodyOdooInvoice + "");

			// get object as a json string
			String jsonStr1;
			try {
				jsonStr1 = Obj.writeValueAsString(responseBodyOdooInvoice);
				log.info(jsonStr1);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			log.info("Odoo Invoice Created Success");

			changeServerDownloadStatus(responseBodyOdooInvoice.getResult(), inventoryVoucher.getCompany().getPid());

		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				// throw new ServiceException(exception.getResponseBodyAsString());
				log.info(exception.getResponseBodyAsString());
				log.info("-------------------------");
				log.info(exception.getMessage());
				log.info("-------------------------");
				exception.printStackTrace();
			}
			log.info(exception.getMessage());
			// throw new ServiceException(exception.getMessage());
		} catch (Exception exception) {

			log.info(exception.getMessage());
			log.info("-------------------------");
			exception.printStackTrace();
			log.info("-------------------------");

			// throw new ServiceException(exception.getMessage());
		}

	}

	private void changeServerDownloadStatus(ResultOdoo response, String companyPid) {

		if (response != null) {
			if (response.getMessage() != null) {
				log.debug("updating tally download status of " + response.getMessage().getReference());
				 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "INV_QUERY_201" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="Finding one header by DocNoServer and companyId";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				InventoryVoucherHeader inventoryVoucher = inventoryVoucherHeaderRepository
						.findOneHeaderByDocumentNumberServerAndCompanyPid(response.getMessage().getReference(),
								companyPid);
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

				Set<String> inventoryVoucherPids = new HashSet<>();

				inventoryVoucherPids.add(inventoryVoucher.getPid());

				List<InventoryVoucherDetail> inventoryVoucherDetails = inventoryVoucherDetailRepository
						.findAllByInventoryVoucherHeaderPidIn(inventoryVoucherPids);

				if (inventoryVoucherDetails.size() > 0) {
					inventoryVoucher.setInventoryVoucherDetails(inventoryVoucherDetails);
				}

				if (!String.valueOf(response.getStatus()).equals("503")) {
					log.info("SUCESS");
					inventoryVoucher.setTallyDownloadStatus(TallyDownloadStatus.COMPLETED);
					inventoryVoucher.setErpReferenceNumber(response.getMessage().getId());
					inventoryVoucher.setErpStatus("Success");
				} else {
					log.info("FAILED");
					inventoryVoucher.setTallyDownloadStatus(TallyDownloadStatus.FAILED);
					inventoryVoucher.setErpReferenceNumber(String.valueOf(0));
					inventoryVoucher.setErpStatus(response.getMessage().getId());
				}
				inventoryVoucherHeaderRepository.save(inventoryVoucher);

				log.debug("updated to " + inventoryVoucher.getTallyDownloadStatus());
			}
		}

	}
}
