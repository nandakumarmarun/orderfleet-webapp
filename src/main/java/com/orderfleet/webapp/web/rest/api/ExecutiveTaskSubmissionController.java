package com.orderfleet.webapp.web.rest.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import com.orderfleet.webapp.async.event.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orderfleet.webapp.config.Constants;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.CompanyIntegrationModule;
import com.orderfleet.webapp.domain.CustomerTimeSpent;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.VoucherNumberGenerator;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.CustomerTimeSpentType;
import com.orderfleet.webapp.domain.enums.VoucherNumberGenerationType;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyIntegrationModuleRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CustomerTimeSpentRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.VoucherNumberGeneratorRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.service.ExecutiveTaskSubmissionService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.MobileConfigurationService;
import com.orderfleet.webapp.service.async.TaskSubmissionPostSave;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.api.dto.TaskSubmissionResponse;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardWebSocketDataDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.MobileConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.ReferenceDocumentDto;
import com.orderfleet.webapp.web.rest.dto.VoucherNumberGeneratorDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import com.orderfleet.webapp.web.vendor.focus.Thread.SaleOrderFocusThread;
import com.orderfleet.webapp.web.vendor.focus.service.SendSalesOrderFocusService;
import com.orderfleet.webapp.web.vendor.service.ModernSalesDataService;
import com.orderfleet.webapp.web.vendor.service.YukthiSalesDataService;
import com.orderfleet.webapp.web.vendor.uncleJhon.Thread.SaleOrderuncleJhonThread;
import com.orderfleet.webapp.web.vendor.uncleJhon.service.SendSalesOrderEmailService;
import com.snr.yukti.util.YuktiApiUtil;

/**
 * REST controller for managing ExecutiveTaskExecution.
 *
 * @author Muhammed Riyas T
 * @since July 13, 2016
 */
@RestController
@RequestMapping(value = "/api/executive-task-execution", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExecutiveTaskSubmissionController {

	private final Logger log = LoggerFactory.getLogger(ExecutiveTaskSubmissionController.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ExecutiveTaskSubmissionService executiveTaskSubmissionService;

	@Inject
	private ExecutiveTaskExecutionService executiveTaskExecutionService;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private TaskSubmissionPostSave taskSubmissionPostSave;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private CustomerTimeSpentRepository customerTimeSpentRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private SimpMessagingTemplate simpMessagingTemplate;

	@Inject
	private CompanyIntegrationModuleRepository companyIntegrationModuleNameRepository;

	@Inject
	private VoucherNumberGeneratorRepository voucherNumberGeneratorRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private YukthiSalesDataService yukthiSalesDataService;

	@Inject
	private ModernSalesDataService modernSalesDataService;

	@Inject
	private MobileConfigurationService mobileConfigurationService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private SendSalesOrderFocusService sendSalesOrderFocusService;

	@Inject
	private SendSalesOrderEmailService sendSalesOrderEmailService;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Autowired
	private EventProducer eventProducer;

	/**
	 * POST /executive-task-execution : Create a new executiveTaskExecution.
	 *
	 * @param executiveTaskSubmissionDTO the executiveTaskSubmissionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         TaskSubmissionResponse
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> executiveTaskSubmission(
			@Valid @RequestBody ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		log.debug("Web request to save ExecutiveTaskExecution start");

		/*
		 * VoucherNumberGenerationType inventoryVoucherGenerationType =
		 * mobileConfiguration .getVoucherNumberGenerationType();
		 */
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String companyPid = company.getPid();

		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

		String userPid = user.getPid();

		Optional<Document> inventoryDocument = null;
		if (executiveTaskSubmissionDTO.getInventoryVouchers().size() != 0) {
			inventoryDocument = documentRepository
					.findOneByPid(executiveTaskSubmissionDTO.getInventoryVouchers().get(0).getDocumentPid());
		}

		if (inventoryDocument != null && inventoryDocument.isPresent()) {
			VoucherNumberGenerationType inventoryVoucherGenerationType = inventoryDocument.get()
					.getVoucherNumberGenerationType();

			if (inventoryVoucherGenerationType == VoucherNumberGenerationType.TYPE_2) {
				List<InventoryVoucherHeaderDTO> inventoryVoucherHeaders = executiveTaskSubmissionDTO
						.getInventoryVouchers();

				List<VoucherNumberGenerator> voucherNumberGeneratorList = voucherNumberGeneratorRepository
						.findAllByUserAndCompanyAndDocument(userPid, companyPid, inventoryDocument.get().getPid());

				if (voucherNumberGeneratorList == null || voucherNumberGeneratorList.size() == 0) {
					log.info(voucherNumberGeneratorList + " Size is either null or 0");
					TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
					taskSubmissionResponse.setStatus("Error");
					taskSubmissionResponse
							.setMessage(LocalDateTime.now() + " " + "Voucher Number Generator list not found");
					return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
				}

				List<String> documentPids = voucherNumberGeneratorList.stream().map(vng -> vng.getDocument().getPid())
						.collect(Collectors.toList());

//				List<Object[]> objectArray = inventoryVoucherHeaderRepository.getLastNumberForEachDocument(companyPid,
//						userPid, documentPids);
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_172" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get the last date with company user doc";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

				LocalDateTime lastDate = inventoryVoucherHeaderRepository.lastDateWithCompanyUserDocument(companyPid,
						userPid, documentPids);

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

				log.info("Last Date " + lastDate);
				if (lastDate == null) {
					lastDate = LocalDateTime.now();
				}
				DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "INV_QUERY_171" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 = "get  last no for each doc optimized";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);

				List<Object[]> objectArray = inventoryVoucherHeaderRepository
						.getLastNumberForEachDocumentOptimized(companyPid, userPid, documentPids, lastDate);

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
				logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1
						+ "," + description1);

				DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id11 = "INV_QUERY_174" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description11 = "get all Doc No for Each document";
				LocalDateTime startLCTime11 = LocalDateTime.now();
				String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
				String startDate11 = startLCTime11.format(DATE_FORMAT11);
				logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);

				List<Object[]> documentVoucherNumberListObject = inventoryVoucherHeaderRepository
						.getAllDocumentNumberForEachDocument(companyPid, userPid, documentPids);
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
				logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END,"
						+ flag11 + "," + description11);

				List<String> documentVoucherNumberList = new ArrayList<>();

				if (documentVoucherNumberListObject.size() > 0) {

					for (Object[] obj : documentVoucherNumberListObject) {
						documentVoucherNumberList.add(obj[0].toString());
					}
				}

				for (InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO : inventoryVoucherHeaders) {
					String documentNumberLocalPrefix = null;
					for (VoucherNumberGenerator voucherNumberGenerator : voucherNumberGeneratorList) {
						if (voucherNumberGenerator.getDocument().getPid()
								.equals(inventoryVoucherHeaderDTO.getDocumentPid())) {
							documentNumberLocalPrefix = voucherNumberGenerator.getPrefix();
						}
					}
					String documentNumberLocal = inventoryVoucherHeaderDTO.getDocumentNumberLocal();
					log.debug("----------" + documentNumberLocal + " Saving vansales order to Server---------");

					Optional<String> opExist = documentVoucherNumberList.stream()
							.filter(ol -> ol.equalsIgnoreCase(documentNumberLocal)).findAny();

					if (opExist.isPresent()) {
						TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
						log.debug("----------" + documentNumberLocal
								+ "  Saving van sales order to Server Failed---------Duplicate Found-------");
						taskSubmissionResponse.setStatus(LocalDateTime.now() + " " + "Error " + documentNumberLocal);
						taskSubmissionResponse
								.setMessage(LocalDateTime.now() + " " + "Duplicate found " + documentNumberLocal);
						return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.NOT_MODIFIED);
					}

//					if (documentNumberLocalPrefix != null) {
//						log.info("documentNumberLocal " + documentNumberLocal);
//						log.info("documentNumberLocalPrefix " + documentNumberLocalPrefix);
//						String[] splitDocumentNumberLocal = documentNumberLocal.split(documentNumberLocalPrefix);
//						log.info("splitDocumentNumberLocal 0 -- " + splitDocumentNumberLocal[0]);
//						log.info("splitDocumentNumberLocal 1 -- " + splitDocumentNumberLocal[1]);
//						long documentNumberLocalCount = Long.parseLong(splitDocumentNumberLocal[1].toString());
//						for (Object[] obj : objectArray) {
//							String dbDocumentNumberLocalPrefix = null;
//							for (VoucherNumberGenerator voucherNumberGenerator : voucherNumberGeneratorList) {
//								if (voucherNumberGenerator.getDocument().getPid().equals(obj[1].toString())) {
//									dbDocumentNumberLocalPrefix = voucherNumberGenerator.getPrefix();
//								}
//							}
//							if (dbDocumentNumberLocalPrefix != null) {
//								String[] dbDocumentNumberLocal = obj[0].toString().split(dbDocumentNumberLocalPrefix);
//								long dbDocumentNumberLocalCount = Long
//										.parseLong(dbDocumentNumberLocal[1].toString().replaceAll("\\s", ""));
//								if ((documentNumberLocalPrefix.equals(dbDocumentNumberLocalPrefix))
//										&& ((dbDocumentNumberLocalCount + 1) != documentNumberLocalCount)) {
//									log.debug("----------" + documentNumberLocal
//											+ "  Saving to Server Failed---------Not in Sequential Order");
//									TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
//									taskSubmissionResponse
//											.setStatus(LocalDateTime.now() + " " + "Error " + documentNumberLocal);
//									taskSubmissionResponse.setMessage(LocalDateTime.now() + " " + documentNumberLocal
//											+ " Not in Sequential Order ");
//									return new ResponseEntity<>(taskSubmissionResponse,
//											HttpStatus.INTERNAL_SERVER_ERROR);
//								}
//							}
//						}
//						log.debug("----------" + documentNumberLocal + " Saving to Server Success---------");
//					}
				}
			} else {

				List<String> documentPids = new ArrayList<String>();

				documentPids.add(inventoryDocument.get().getPid());

				List<Object[]> documentVoucherNumberListObject = inventoryVoucherHeaderRepository
						.getAllDocumentNumberForEachDocument(companyPid, userPid, documentPids);

				List<String> documentVoucherNumberList = new ArrayList<>();

				if (documentVoucherNumberListObject.size() > 0) {

					for (Object[] obj : documentVoucherNumberListObject) {
						documentVoucherNumberList.add(obj[0].toString());
					}
				}

				List<InventoryVoucherHeaderDTO> inventoryVoucherHeaders = executiveTaskSubmissionDTO
						.getInventoryVouchers();

				for (InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO : inventoryVoucherHeaders) {

					String documentNumberLocal = inventoryVoucherHeaderDTO.getDocumentNumberLocal();
					log.debug("----------" + documentNumberLocal + " Saving Inventory Voucher to Server---------"
							+ user.getLogin());

					Optional<String> opExist = documentVoucherNumberList.stream()
							.filter(ol -> ol.equalsIgnoreCase(documentNumberLocal)).findAny();

					if (opExist.isPresent()) {
						TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
						log.debug("----------" + documentNumberLocal
								+ "  Saving Inventory Voucher to Server Failed---------Duplicate Found-------");
						taskSubmissionResponse.setStatus(LocalDateTime.now() + " " + "Error " + documentNumberLocal);
						taskSubmissionResponse
								.setMessage(LocalDateTime.now() + " " + "Duplicate found " + documentNumberLocal);
						return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.NOT_MODIFIED);
					}
				}
			}
		}

		Optional<Document> accountingVoucherDocument = null;
		if (executiveTaskSubmissionDTO.getAccountingVouchers().size() != 0) {
			accountingVoucherDocument = documentRepository
					.findOneByPid(executiveTaskSubmissionDTO.getAccountingVouchers().get(0).getDocumentPid());
		}
		List<AccountingVoucherHeaderDTO> accountingVoucherHeaders = executiveTaskSubmissionDTO.getAccountingVouchers();
		if (accountingVoucherDocument != null && accountingVoucherDocument.isPresent()) {

			List<String> documentPids = new ArrayList<String>();

			documentPids.add(accountingVoucherDocument.get().getPid());
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACC_QUERY_164" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all document number for each document";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<Object[]> documentVoucherNumberListObject = accountingVoucherHeaderRepository
					.getAllDocumentNumberForEachDocument(companyPid, userPid, documentPids);
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
			List<String> documentVoucherNumberList = new ArrayList<>();

			if (documentVoucherNumberListObject.size() > 0) {

				for (Object[] obj : documentVoucherNumberListObject) {
					documentVoucherNumberList.add(obj[0].toString());
				}
			}

			for (AccountingVoucherHeaderDTO accountingVoucherHeaderDTO : accountingVoucherHeaders) {

				String documentNumberLocal = accountingVoucherHeaderDTO.getDocumentNumberLocal();
				log.debug("----------" + documentNumberLocal + " Saving Accounting Voucher to Server---------"
						+ user.getLogin());

				Optional<String> opExist = documentVoucherNumberList.stream()
						.filter(ol -> ol.equalsIgnoreCase(documentNumberLocal)).findAny();

				if (opExist.isPresent()) {
					TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
					log.debug("----------" + documentNumberLocal
							+ "  Saving Accounting Voucher to Server Failed---------Duplicate Found-------");
					taskSubmissionResponse.setStatus(LocalDateTime.now() + " " + "Error " + documentNumberLocal);
					taskSubmissionResponse
							.setMessage(LocalDateTime.now() + " " + "Duplicate found " + documentNumberLocal);
					return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.NOT_MODIFIED);

				}
			}
		}

		Optional<Document> dynamicVoucherDocument = null;
		if (executiveTaskSubmissionDTO.getDynamicDocuments().size() != 0) {
			dynamicVoucherDocument = documentRepository
					.findOneByPid(executiveTaskSubmissionDTO.getDynamicDocuments().get(0).getDocumentPid());
		}
		List<DynamicDocumentHeaderDTO> dynamicVoucherHeaders = executiveTaskSubmissionDTO.getDynamicDocuments();
		if (dynamicVoucherDocument != null && dynamicVoucherDocument.isPresent()) {

			List<String> documentPids = new ArrayList<String>();

			documentPids.add(dynamicVoucherDocument.get().getPid());
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "DYN_QUERY_145" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all doc no by each doc type";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<Object[]> documentVoucherNumberListObject = dynamicDocumentHeaderRepository
					.getAllDocumentNumberForEachDocument(companyPid, userPid, documentPids);
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
			List<String> documentVoucherNumberList = new ArrayList<>();

			if (documentVoucherNumberListObject.size() > 0) {

				for (Object[] obj : documentVoucherNumberListObject) {
					documentVoucherNumberList.add(obj[0].toString());
				}
			}

			for (DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO : dynamicVoucherHeaders) {

				String documentNumberLocal = dynamicDocumentHeaderDTO.getDocumentNumberLocal();
				log.debug("----------" + documentNumberLocal + " Saving Dynamic Document :- "
						+ dynamicVoucherDocument.get().getName() + " to Server---------" + user.getLogin());

				Optional<String> opExist = documentVoucherNumberList.stream()
						.filter(ol -> ol.equalsIgnoreCase(documentNumberLocal)).findAny();

				if (opExist.isPresent()) {
					TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
					log.debug(
							"----------" + documentNumberLocal + "  Saving :- " + dynamicVoucherDocument.get().getName()
									+ " to Server Failed---------Duplicate Found-------");
					taskSubmissionResponse.setStatus(LocalDateTime.now() + " " + "Error " + documentNumberLocal);
					taskSubmissionResponse
							.setMessage(LocalDateTime.now() + " " + "Duplicate found " + documentNumberLocal);
					return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.NOT_MODIFIED);

				}
			}
		}

		TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();

		try {
			log.info("......................");
			log.info(executiveTaskSubmissionDTO.toString());
			log.info("......................");
			ExecutiveTaskExecutionDTO executionDTO = executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO();
			if (executionDTO.getInterimSave()) {
				deleteOldAllExecutions(executionDTO);
				Optional<CustomerTimeSpent> optionalCustomerTimeSpent = customerTimeSpentRepository
						.findOneByUserLoginAndClientTransactionKey(SecurityUtils.getCurrentUserLogin(),
								executionDTO.getClientTransactionKey());
				if (optionalCustomerTimeSpent.isPresent()) {
					optionalCustomerTimeSpent.get().setActive(false);
					customerTimeSpentRepository.save(optionalCustomerTimeSpent.get());
				}
			}
			ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper = executiveTaskSubmissionService
					.executiveTaskSubmission(executiveTaskSubmissionDTO);
			if (tsTransactionWrapper != null) {
				taskSubmissionResponse = tsTransactionWrapper.getTaskSubmissionResponse();
				taskSubmissionPostSave.doPostSaveExecutivetaskSubmission(tsTransactionWrapper,
						executiveTaskSubmissionDTO);
			}

			// send sales orders to third party ERP
			sendSalesOrderToThirdPartyErp(tsTransactionWrapper);

			Optional<CompanyConfiguration> optSendTofocus = companyConfigurationRepository
					.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_TO_FOCUS);

			// send sales order automaticaly to focus
			if (optSendTofocus.isPresent()) {

				if (Boolean.valueOf(optSendTofocus.get().getValue())) {
					log.debug(user.getLogin() + " : " + " Sending Sales Order To Focus  : ");
					Thread foucsThread = new SaleOrderFocusThread(tsTransactionWrapper.getInventoryVouchers(),
							sendSalesOrderFocusService);
					foucsThread.start();
				}

			}
			// Send sales order email to uncleJhon secondary sales
			Optional<CompanyConfiguration> optsendEmailAutomatically = companyConfigurationRepository
					.findByCompanyPidAndName(companyPid, CompanyConfig.SEND_EMAIL_AUTOMATICALLY);
			if (optsendEmailAutomatically.isPresent() && Boolean.valueOf(optsendEmailAutomatically.get().getValue())) {
				List<PrimarySecondaryDocument> primarySecDoc = primarySecondaryDocumentRepository
						.findByVoucherTypeAndCompany(VoucherType.SECONDARY_SALES_ORDER, company.getId());
				Document document = primarySecDoc.get(0).getDocument();
				Document ivDocument = tsTransactionWrapper.getInventoryVouchers().get(0).getDocument();
				log.debug("Doc name :" + ivDocument.getName());

				if (document.getPid().equalsIgnoreCase(ivDocument.getPid())) {

					System.out.println("Enter to the loop");

					if (Boolean.valueOf(optsendEmailAutomatically.get().getValue())) {

						log.debug(user.getLogin() + " : " + " Sending Sales Order Email to Supplier automatically : ");
						Thread uncleJhonThread = new SaleOrderuncleJhonThread(
								tsTransactionWrapper.getInventoryVouchers(), sendSalesOrderEmailService);
						uncleJhonThread.start();
					}
				}
			}

			// send EmailToComplaint Modern

			if (tsTransactionWrapper.getExecutiveTaskExecution().getActivity().getEmailTocomplaint()) {
				sendEmailToComplaint(executiveTaskSubmissionDTO);
			}

			taskSubmissionResponse.setStatus("Success");
			taskSubmissionResponse.setMessage("Activity submitted successfully...");
          if (tsTransactionWrapper.getDynamicDocuments() != null) {
			Optional<CompanyConfiguration> optCrm = companyConfigurationRepository
										.findByCompanyPidAndName(companyPid, CompanyConfig.CRM_ENABLE);
					if (optCrm.isPresent() && Boolean.valueOf(optCrm.get().getValue())) {
										CompletableFuture.supplyAsync(() -> {
											sendDynamicDocumentModCApplication(tsTransactionWrapper);
											return "submitted successfully...";
											});
								}
							}
//			if (tsTransactionWrapper.getInventoryVouchers() != null) {
//				Optional<CompanyConfiguration> optCrm = companyConfigurationRepository
//						.findByCompanyPidAndName(companyPid, CompanyConfig.CRM_ENABLE);
//				if (optCrm.isPresent() && Boolean.valueOf(optCrm.get().getValue())) {
//
//					CompletableFuture.supplyAsync(() -> {
//						sendInventoryVoucherModCApplication(tsTransactionWrapper);
//						return "submitted successfully...";
//					});
//				}
//			}
//
//			if (tsTransactionWrapper.getAccountingVouchers() != null) {
//				Optional<CompanyConfiguration> optCrm = companyConfigurationRepository
//						.findByCompanyPidAndName(companyPid, CompanyConfig.CRM_ENABLE);
//				if (optCrm.isPresent() && Boolean.valueOf(optCrm.get().getValue())) {
//
//					CompletableFuture.supplyAsync(() -> {
//						sendAccountingVoucherModCApplication(tsTransactionWrapper);
//						return "submitted successfully...";
//					});
//				}
//			}

		} catch (DataIntegrityViolationException dive) {
			taskSubmissionResponse.setStatus(LocalDateTime.now() + " " + "Duplicate Key");
			taskSubmissionResponse.setMessage(LocalDateTime.now() + " " + dive.getMessage());
			log.error("Executive task submission DataIntegrityViolationException {}", dive);
			return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.CONFLICT);
		} catch (Exception e) {
			taskSubmissionResponse.setStatus("Error");
			taskSubmissionResponse.setMessage(LocalDateTime.now() + " " + e.getMessage());
			log.error(e.getMessage());
			return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.OK);
	}

	/**
	 * POST /executive-task-execution : Create or update executiveTaskExecution.
	 *
	 * @param executiveTaskSubmissionDTO the executiveTaskSubmissionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         TaskSubmissionResponse
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/saveOrupdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> updateExecutiveTaskSubmission(
			@Valid @RequestBody ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		log.debug("Web request to save ExecutiveTaskExecution start");

		TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
		try {
			ExecutiveTaskSubmissionTransactionWrapper transactionWrapper = executiveTaskSubmissionService
					.updationExecutiveTaskExecution(executiveTaskSubmissionDTO);

			updateDashboad(transactionWrapper.getExecutiveTaskExecution(), executiveTaskSubmissionDTO);

			taskSubmissionResponse = transactionWrapper.getTaskSubmissionResponse();
			taskSubmissionResponse.setStatus("Success");
			taskSubmissionResponse.setMessage("Activity submitted successfully...");
		} catch (DataIntegrityViolationException dive) {
			taskSubmissionResponse.setStatus("Duplicate Key");
			taskSubmissionResponse.setMessage(dive.getMessage());
			log.error("Executive task submission DataIntegrityViolationException {}", dive);
		} catch (Exception e) {
			taskSubmissionResponse.setStatus("Error");
			taskSubmissionResponse.setMessage(e.getMessage());
			log.error(e.getMessage());
		}
		return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.OK);
	}

	/**
	 * PUT /executive-task-execution/inventory-voucher/update : update accounting
	 * voucher
	 *
	 * @param executiveTaskSubmissionDTO the executiveTaskSubmissionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         TaskSubmissionResponse
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/inventory-voucher/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> updatInventoryVoucher(
			@Valid @RequestBody ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) throws URISyntaxException {
		log.debug("Web request to update Inventory Voucher");
		TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
		try {
			taskSubmissionResponse = executiveTaskSubmissionService.updateInventoryVoucher(executiveTaskSubmissionDTO);
			// set achieved
			if (taskSubmissionResponse.getStartDate() != null) {
				Long achieved = executiveTaskExecutionService
						.countByUserIdAndActivityIdAndDateBetweenAndActivityStatusNot(
								taskSubmissionResponse.getUserId(), taskSubmissionResponse.getActivityId(),
								ActivityStatus.REJECTED, taskSubmissionResponse.getStartDate().atTime(0, 0),
								taskSubmissionResponse.getEndDate().atTime(23, 59));
				taskSubmissionResponse.setAchieved(achieved);
			}
			taskSubmissionResponse.setStatus("Success");
			taskSubmissionResponse.setMessage("Inventory Voucher update submitted successfully...");
		} catch (Exception e) {
			taskSubmissionResponse.setStatus("Error");
			taskSubmissionResponse.setMessage(e.getMessage());
		}
		return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.OK);
	}

	/**
	 * PUT /executive-task-execution/accounting-voucher/update : update accounting
	 * voucher
	 *
	 * @param executiveTaskSubmissionDTO the executiveTaskSubmissionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         TaskSubmissionResponse
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/accounting-voucher/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> updateAccountingVoucher(
			@Valid @RequestBody ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) throws URISyntaxException {
		log.debug("Web request to update accounting voucher");
		TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
		try {
			taskSubmissionResponse = executiveTaskSubmissionService.updateAccountingVoucher(executiveTaskSubmissionDTO);
			taskSubmissionResponse.setStatus("Success");
			taskSubmissionResponse.setMessage("Accounting voucher update submitted successfully...");
		} catch (Exception e) {
			taskSubmissionResponse.setStatus("Error");
			taskSubmissionResponse.setMessage(e.getMessage());
		}
		return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.OK);
	}

	/**
	 * PUT /executive-task-execution/dynamic-document/update : update dynamic
	 * document
	 *
	 * @param executiveTaskSubmissionDTO the executiveTaskSubmissionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         TaskSubmissionResponse
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/dynamic-document/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> updateDynamicDocument(
			@Valid @RequestBody ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		log.debug("Web request to update Dynamic Document");
		TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
		try {
			taskSubmissionResponse = executiveTaskSubmissionService.updateDynamicDocument(executiveTaskSubmissionDTO);
			// set achieved
			if (taskSubmissionResponse.getStartDate() != null) {
				Long achieved = executiveTaskExecutionService
						.countByUserIdAndActivityIdAndDateBetweenAndActivityStatusNot(
								taskSubmissionResponse.getUserId(), taskSubmissionResponse.getActivityId(),
								ActivityStatus.REJECTED, taskSubmissionResponse.getStartDate().atTime(0, 0),
								taskSubmissionResponse.getEndDate().atTime(23, 59));
				taskSubmissionResponse.setAchieved(achieved);
			}
			taskSubmissionResponse.setStatus("Success");
			taskSubmissionResponse.setMessage("Dynamic Document update submitted successfully...");
		} catch (Exception e) {
			taskSubmissionResponse.setStatus("Error");
			taskSubmissionResponse.setMessage(e.getMessage());
		}
		return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.OK);
	}

	/**
	 * GET /executive-task-execution/search : find xecutiveTaskSubmissions
	 *
	 * @param activityPid
	 * @param accountPid
	 * @param documentPid
	 * @param fromDate
	 * @param toDate
	 * @return the ResponseEntity with status 200 (OK) and with body the List of
	 *         ExecutiveTaskSubmissionDTO
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ExecutiveTaskSubmissionDTO>> taskSubmissionSearch(
			@RequestParam(required = true) String activityPid, @RequestParam(required = true) String accountPid,
			@RequestParam(required = false) String documentPid,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		LocalDateTime fDate = null;
		LocalDateTime tDate = null;
		if (fromDate != null && toDate != null) {
			fDate = fromDate.atTime(0, 0);
			tDate = toDate.atTime(23, 59);
		}
		return new ResponseEntity<>(executiveTaskSubmissionService.findExecutiveTaskSubmissions(activityPid, accountPid,
				documentPid, fDate, tDate), HttpStatus.OK);
	}

	/**
	 * GET /search-by-reference-documents/search : find ExecutiveTaskSubmissions
	 * reference
	 *
	 * @param accountPid
	 * @param documentPid
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         ExecutiveTaskSubmissionDTO
	 */
	@Timed
	@RequestMapping(value = "/search-by-reference-documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ExecutiveTaskSubmissionDTO> taskSubmissionSearchByReferenceDocuments(
			@RequestParam(required = true) String accountPid, @RequestParam(required = true) String documentPid,
			@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate,
			@RequestParam(required = false) String processStatus) {
		return new ResponseEntity<>(executiveTaskSubmissionService.findExecutiveTaskSubmissionsByReferenceDocuments(
				accountPid, documentPid, startDate, endDate, processStatus), HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/search-by-territoty", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ExecutiveTaskSubmissionDTO> taskSubmissionSearchByTerritory(
			@RequestParam(required = true) String territoryPid, @RequestParam(required = true) String documentPid,
			@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate,
			@RequestParam(required = false) String processStatus) {
		return new ResponseEntity<>(
				executiveTaskSubmissionService.findExecutiveTaskSubmissionsByTerritotyAndReferenceDocument(territoryPid,
						documentPid, startDate, endDate, processStatus),
				HttpStatus.OK);
	}

	/**
	 * for upload filled form images
	 *
	 * @param executiveTaskExecutionPid
	 * @param dynamicDocumentRefNo
	 * @param imageRefNo
	 * @param file
	 * @return
	 */
	@Transactional
	@RequestMapping(value = "/upload/filledform", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> uploadFilledFormFile(
			@RequestParam("executiveTaskExecutionPid") String executiveTaskExecutionPid,
			@RequestParam("dynamicDocumentRefNo") String dynamicDocumentRefNo,
			@RequestParam("imageRefNo") String imageRefNo, @RequestParam("file") MultipartFile file) {
		log.debug("Request FilledForm to upload a file : {}", file);
		if (file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "FORM_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get the one form by dyn doc header executive task execution pid and image ref no";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		ResponseEntity<Object> uploadfile = filledFormRepository
				.findOneByDynamicDocumentHeaderExecutiveTaskExecutionPidAndImageRefNo(executiveTaskExecutionPid,
						imageRefNo)
				.map(filledForm -> {
					try {
						File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
								file.getOriginalFilename(), file.getContentType());
						// update filledForm with file
						filledForm.getFiles().add(uploadedFile);
						filledFormRepository.save(filledForm);
						log.debug("uploaded file for FilledForm: {}", filledForm);
						return new ResponseEntity<>(HttpStatus.OK);
					} catch (FileManagerException | IOException ex) {
						log.debug("File upload exception : {}", ex.getMessage());
						return ResponseEntity.badRequest()
								.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage()))
								.body(null);
					}
				})

				.orElse(ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("fileUpload", "formNotExists", "FilledForm not found."))
						.body(null));
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
		return uploadfile;
	}

	@Transactional
	@RequestMapping(value = "/upload/receiptImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> uploadReceiptImageFile(
			@RequestParam("executiveTaskExecutionPid") String executiveTaskExecutionPid,
			@RequestParam("imageRefNo") String imageRefNo, @RequestParam("file") MultipartFile file) {
		log.debug("Request Receipt Image to upload a file : {}", file);
		if (file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_152" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get accVoucher by executiveTaskExecutionId and ImageRefNo";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		ResponseEntity<Object> avc = accountingVoucherHeaderRepository
				.findOneByExecutiveTaskExecutionPidAndImageRefNo(executiveTaskExecutionPid, imageRefNo)
				.map(accountingVoucherHeader -> {
					try {
						File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
								file.getOriginalFilename(), file.getContentType());
						// update filledForm with file
						accountingVoucherHeader.getFiles().add(uploadedFile);
						accountingVoucherHeaderRepository.save(accountingVoucherHeader);
						log.debug("uploaded file for Accounting Voucher Header: {}", accountingVoucherHeader);
						return new ResponseEntity<>(HttpStatus.OK);
					} catch (FileManagerException | IOException ex) {
						log.debug("File upload exception : {}", ex.getMessage());
						return ResponseEntity.badRequest()
								.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage()))
								.body(null);
					}
				})

				.orElse(ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("fileUpload", "formNotExists", "FilledForm not found."))
						.body(null));
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
		return avc;

	}

	@Transactional
	@RequestMapping(value = "/upload/inventoryVoucherImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> inventoryVoucherImageeFile(
			@RequestParam("executiveTaskExecutionPid") String executiveTaskExecutionPid,
			@RequestParam("imageRefNo") String imageRefNo, @RequestParam("file") MultipartFile file) {
		log.debug("Request Inventory Image to upload a file : {}", file);
		if (file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}

		ResponseEntity<Object> ivc = inventoryVoucherHeaderRepository
				.findOneByExecutiveTaskExecutionPidAndImageRefNo(executiveTaskExecutionPid, imageRefNo)
				.map(inventoryVoucherHeader -> {
					try {
						File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
								file.getOriginalFilename(), file.getContentType());
						// update filledForm with file
						inventoryVoucherHeader.getFiles().add(uploadedFile);
						inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);
						log.debug("uploaded file for Inventory Voucher Header: {}", inventoryVoucherHeader);
						return new ResponseEntity<>(HttpStatus.OK);
					} catch (FileManagerException | IOException ex) {
						log.debug("File upload exception : {}", ex.getMessage());
						return ResponseEntity.badRequest()
								.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage()))
								.body(null);
					}
				})

				.orElse(ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("fileUpload", "formNotExists", "FilledForm not found."))
						.body(null));
		return ivc;

	}

	/**
	 * delete execution when interimSave is true; interim save used to upadte
	 * execution part by part.
	 *
	 * @param executionDTO
	 */
	private void deleteOldAllExecutions(ExecutiveTaskExecutionDTO executionDTO) {
		log.debug("Request delete execution login name : {}", SecurityUtils.getCurrentUserLogin());
		Optional<ExecutiveTaskExecution> opExecutiveTaskExecution = executiveTaskExecutionRepository
				.findByCompanyIdAndClientTransactionKey(executionDTO.getClientTransactionKey());
		if (opExecutiveTaskExecution.isPresent()) {

			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			String id = "INV_QUERY_121" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get all by executive task execution Pid ";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<InventoryVoucherHeader> oldInventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByExecutiveTaskExecutionPid(opExecutiveTaskExecution.get().getPid());
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
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "ACC_QUERY_119" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 = " get all AccVoucher By ExecutiveTask Execution Pid ";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			List<AccountingVoucherHeader> oldAccountingVoucherHeaders = accountingVoucherHeaderRepository
					.findAllByExecutiveTaskExecutionPid(opExecutiveTaskExecution.get().getPid());
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
			logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1
					+ "," + description1);
			DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id11 = "DYN_QUERY_133" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description11 = "get all documents by executive task execution pid";
			LocalDateTime startLCTime11 = LocalDateTime.now();
			String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
			String startDate11 = startLCTime11.format(DATE_FORMAT11);
			logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
			List<DynamicDocumentHeader> oldDynamicDocumentHeaders = dynamicDocumentHeaderRepository
					.findAllByExecutiveTaskExecutionPid(opExecutiveTaskExecution.get().getPid());
			String flag11 = "Normal";
			LocalDateTime endLCTime11 = LocalDateTime.now();
			String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
			String endDate11 = startLCTime.format(DATE_FORMAT11);
			Duration duration11 = Duration.between(startLCTime, endLCTime11);
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
			logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END,"
					+ flag11 + "," + description11);

			if (!oldInventoryVoucherHeaders.isEmpty()) {
				inventoryVoucherHeaderRepository.delete(oldInventoryVoucherHeaders);
			}
			if (!oldAccountingVoucherHeaders.isEmpty()) {
				accountingVoucherHeaderRepository.delete(oldAccountingVoucherHeaders);
			}
			if (!oldDynamicDocumentHeaders.isEmpty()) {
				dynamicDocumentHeaderRepository.delete(oldDynamicDocumentHeaders);
			}
			executiveTaskExecutionRepository.delete(opExecutiveTaskExecution.get());
		}

	}

	private void updateDashboad(ExecutiveTaskExecution executiveTaskExecution,
			ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		Optional<EmployeeProfile> opEmployee = employeeProfileService
				.findByUserPid(executiveTaskExecution.getUser().getPid());
		ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO(executiveTaskExecution);
		if (opEmployee.isPresent()) {
			executiveTaskExecutionDTO.setEmployeeName(opEmployee.get().getName());
		}
		Long companyId = executiveTaskExecution.getCompany().getId();
		// live update dash board view
		DashboardWebSocketDataDTO dashboardWebSocketData = createDashboardWebSocketData(executiveTaskExecutionDTO,
				executiveTaskSubmissionDTO, companyId);
		simpMessagingTemplate.convertAndSend("/live-tracking/dashboard-view/" + companyId, dashboardWebSocketData);
	}

	private DashboardWebSocketDataDTO createDashboardWebSocketData(ExecutiveTaskExecutionDTO executiveTaskExecutionDTO,
			ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO, Long companyId) {
		DashboardWebSocketDataDTO dashboardWebSocketData = new DashboardWebSocketDataDTO();
		dashboardWebSocketData.setUserPid(executiveTaskExecutionDTO.getUserPid());
		dashboardWebSocketData.setAccountProfilePid(executiveTaskExecutionDTO.getAccountProfilePid());
		dashboardWebSocketData.setLastAccountLocation(executiveTaskExecutionDTO.getAccountLocation());
		dashboardWebSocketData.setLastTime(executiveTaskExecutionDTO.getCreatedDate());
		dashboardWebSocketData.setLastLocation(executiveTaskExecutionDTO.getLocation());
		dashboardWebSocketData.setLocationType(executiveTaskExecutionDTO.getLocationType());
		dashboardWebSocketData.setIsGpsOff(executiveTaskExecutionDTO.getIsGpsOff());
		dashboardWebSocketData.setIsMobileDataOff(executiveTaskExecutionDTO.getIsMobileDataOff());

		dashboardWebSocketData.setCustomerTimeSpentType(CustomerTimeSpentType.SHOW);

		if (executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().getPid() == null) {
			dashboardWebSocketData.setReloadNumberCircle(true);
		} else {
			executiveTaskExecutionDTO.setInterimSave(true);
			dashboardWebSocketData.setReloadNumberCircle(false);
		}
		taskSubmissionPostSave.assignDashboardSummaryData(dashboardWebSocketData, executiveTaskExecutionDTO,
				executiveTaskSubmissionDTO, companyId);
		return dashboardWebSocketData;
	}

	private void sendSalesOrderToThirdPartyErp(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper) {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<CompanyIntegrationModule> integrationModuleName = companyIntegrationModuleNameRepository
				.findByCompanyId(companyId);

		// cochin dist for yukthi
		if (integrationModuleName.isPresent()
				&& Constants.YUKTI_ERP.equals(integrationModuleName.get().getIntegrationModule().getName())) {
			String apiBaseUrl = integrationModuleName.get().getBaseUrl();
			if (apiBaseUrl != null) {
				YuktiApiUtil.setApiEndpoint(companyId, apiBaseUrl);
				yukthiSalesDataService.saveTransactions(tsTransactionWrapper, companyId);
			}
			// modern erp
		} else if (integrationModuleName.isPresent()
				&& Constants.MODERN_ERP.equals(integrationModuleName.get().getIntegrationModule().getName())) {
			String apiBaseUrl = integrationModuleName.get().getBaseUrl();
			log.info("order sent to ** " + apiBaseUrl);
			log.info("checking url for :modern ");
			if (apiBaseUrl != null) {
				log.info("order sending to **:modern ");
				modernSalesDataService.saveTransactions(tsTransactionWrapper, companyId, apiBaseUrl);
			}
		}
	}

	private void sendEmailToComplaint(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		log.info("Sending Email To Complaint");
//		String toMassage = "santhosh.admin@moderndistropolis.com";
		String toMassage = "";
		String fromMassage = "salesnrich.info@gmail.com";

		if (!executiveTaskSubmissionDTO.getDynamicDocuments().isEmpty()) {
			User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
			Company company = user.getCompany();
			if (company.getId() == 8402) {
				toMassage = "santhosh.admin@moderndistropolis.com";
			}
			if (company.getId() == 7700) {
				toMassage = "moderntnb@gmail.com";
			}
			EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUser(user);
			String emString = employeeProfile.getName() != null ? employeeProfile.getName() : "";
			LocalDateTime issueDate = executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().getSendDate();
			String acName = executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().getAccountProfileName();

			for (DynamicDocumentHeaderDTO dynamicDTO : executiveTaskSubmissionDTO.getDynamicDocuments()) {
				String compliantSlip = createComplaintSlip(dynamicDTO);
				JavaMailSender mailSender = getJavaMailSender();
				MimeMessage massage = mailSender.createMimeMessage();
				MimeMessageHelper helper;
				try {
					helper = new MimeMessageHelper(massage, true);
					helper.setSubject("Complaint From " + acName);
					helper.setText("EMPLOYEE NAME : " + emString + "\n" + "ISSUE DATE  : " + issueDate + "\n"
							+ "CUSTOMER NAME : " + acName + "\n" + compliantSlip);
					helper.setTo(toMassage);
					helper.setFrom(fromMassage);
					// XlFILE AttacCHing.........
					mailSender.send(massage);
					log.debug("Sent email successfully");
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					log.warn("Email could not be sent to user '{}'", e);
				}

			}
		}
	}

	private String createComplaintSlip(DynamicDocumentHeaderDTO dynamicDTO) {
		StringBuilder builder = null;
		for (FilledFormDTO ff : dynamicDTO.getFilledForms()) {
			builder = new StringBuilder();
			builder.append("                 Complaint Details                 " + "\n");
			builder.append("               ---------------------                 " + "\n");
			for (FilledFormDetailDTO ffDetails : ff.getFilledFormDetails()) {
				builder.append("Qus: " + ffDetails.getFormElementName());
				builder.append("\n");
				builder.append("Ans: " + ffDetails.getValue() + "\n");
				builder.append("\n");
			}
			builder.append("----------------------------------------------");
		}
		return builder.toString();
	}

	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername("salesnrich.info@gmail.com");
		mailSender.setPassword("fohcijtqujnqyqcn");

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "false");

		return mailSender;
	}
		public void sendDynamicDocumentModCApplication(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper){
			if(!tsTransactionWrapper.getDynamicDocuments().isEmpty()) {
						List<DynamicDocumentHeader> ddhlist = tsTransactionWrapper.getDynamicDocuments();
						List<DynamicDocumentHeaderDTO> ddhDTO = ddhlist.stream().map(data -> new DynamicDocumentHeaderDTO(data,data.getCompany())).collect(Collectors.toList());
						ddhDTO.forEach(ddhDto -> eventProducer.dynamicDocumentPublish(ddhDto));
					}
			}


	public void sendInventoryVoucherModCApplication(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper){
		if(!tsTransactionWrapper.getInventoryVouchers().isEmpty()) {
			List<InventoryVoucherHeader> Ivhlist = tsTransactionWrapper.getInventoryVouchers();
			List<InventoryVoucherHeaderDTO> ivhDTOs = Ivhlist.stream().map(data -> new InventoryVoucherHeaderDTO(data.getCompany(), data)).collect(Collectors.toList());
			ivhDTOs.forEach(ivhDTO -> eventProducer.inventoryVoucherPublish(ivhDTO));
		}
	}
	public void sendAccountingVoucherModCApplication(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper){
		if(!tsTransactionWrapper.getAccountingVouchers().isEmpty()) {
			List<AccountingVoucherHeader> AccvList = tsTransactionWrapper.getAccountingVouchers();
			List<AccountingVoucherHeaderDTO> accDTOs = AccvList.stream().map(data -> new AccountingVoucherHeaderDTO(data.getCompany(), data)).collect(Collectors.toList());
			accDTOs.forEach(ivhDTO -> eventProducer.accountingVoucherPublish(ivhDTO));
		}
	}
}
