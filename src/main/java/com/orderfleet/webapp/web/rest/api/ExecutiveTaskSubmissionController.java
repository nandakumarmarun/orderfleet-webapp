package com.orderfleet.webapp.web.rest.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.orderfleet.webapp.domain.CompanyIntegrationModule;
import com.orderfleet.webapp.domain.CustomerTimeSpent;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.VoucherNumberGenerator;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.CustomerTimeSpentType;
import com.orderfleet.webapp.domain.enums.VoucherNumberGenerationType;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyIntegrationModuleRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CustomerTimeSpentRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
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
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardWebSocketDataDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.MobileConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.VoucherNumberGeneratorDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import com.orderfleet.webapp.web.vendor.service.ModernSalesDataService;
import com.orderfleet.webapp.web.vendor.service.YukthiSalesDataService;
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

	/**
	 * POST /executive-task-execution : Create a new executiveTaskExecution.
	 *
	 * @param executiveTaskSubmissionDTO
	 *            the executiveTaskSubmissionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         TaskSubmissionResponse
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> executiveTaskSubmission(
			@Valid @RequestBody ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		log.debug("Web request to save ExecutiveTaskExecution start");
		log.info("=========================================="+executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().getSendDate());
		MobileConfigurationDTO mobileConfiguration = mobileConfigurationService
				.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if(mobileConfiguration == null) {
			log.debug("Mobile Configuration is not Set for this company (Type_1 or Type_2)");
			TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
			taskSubmissionResponse.setStatus("Error");
			taskSubmissionResponse.setMessage("Mobile Configuration is not Set for this company, CONTACT ADMIN");
			return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.EXPECTATION_FAILED);
		}
		VoucherNumberGenerationType inventoryVoucherGenerationType = mobileConfiguration.getVoucherNumberGenerationType();
		if (inventoryVoucherGenerationType == VoucherNumberGenerationType.TYPE_2) {
			List<InventoryVoucherHeaderDTO> inventoryVoucherHeaders = executiveTaskSubmissionDTO.getInventoryVouchers();

			Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
			String companyPid = company.getPid();

			User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

			String userPid = user.getPid();

			List<VoucherNumberGenerator> voucherNumberGeneratorList = voucherNumberGeneratorRepository
					.findAllByUserAndCompany(userPid, companyPid);

			if (voucherNumberGeneratorList == null || voucherNumberGeneratorList.size() == 0) {
				log.debug(voucherNumberGeneratorList + " Size is either null or 0");
				TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
				taskSubmissionResponse.setStatus("Error");
				taskSubmissionResponse.setMessage("Not Voucher Generator List");
				return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			List<String> documentPids = voucherNumberGeneratorList.stream().map(vng -> vng.getDocument().getPid())
					.collect(Collectors.toList());

			List<Object[]> objectArray = inventoryVoucherHeaderRepository.getLastNumberForEachDocument(companyPid,
					userPid, documentPids);

			for (InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO : inventoryVoucherHeaders) {
				String documentNumberLocalPrefix = null;
				for (VoucherNumberGenerator voucherNumberGenerator : voucherNumberGeneratorList) {
					if (voucherNumberGenerator.getDocument().getPid()
							.equals(inventoryVoucherHeaderDTO.getDocumentPid())) {
						documentNumberLocalPrefix = voucherNumberGenerator.getPrefix();
					}
				}
				String documentNumberLocal = inventoryVoucherHeaderDTO.getDocumentNumberLocal();
				log.debug("----------" + documentNumberLocal + " Saving to Server---------");
				if (documentNumberLocalPrefix != null) {
					String[] splitDocumentNumberLocal = documentNumberLocal.split(documentNumberLocalPrefix);
					long documentNumberLocalCount = Long.parseLong(splitDocumentNumberLocal[1].toString());
					for (Object[] obj : objectArray) {
						String dbDocumentNumberLocalPrefix = null;
						for (VoucherNumberGenerator voucherNumberGenerator : voucherNumberGeneratorList) {
							if (voucherNumberGenerator.getDocument().getPid().equals(obj[1].toString())) {
								dbDocumentNumberLocalPrefix = voucherNumberGenerator.getPrefix();
							}
						}
						if (dbDocumentNumberLocalPrefix != null) {
							String[] dbDocumentNumberLocal = obj[0].toString().split(dbDocumentNumberLocalPrefix);
							long dbDocumentNumberLocalCount = Long.parseLong(dbDocumentNumberLocal[1].toString());
							if ((documentNumberLocalPrefix.equals(dbDocumentNumberLocalPrefix))
									&& ((dbDocumentNumberLocalCount + 1) != documentNumberLocalCount)) {
								log.debug("----------" + documentNumberLocal + "  Saving to Server Failed---------");
								TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
								taskSubmissionResponse.setStatus("Error");
								taskSubmissionResponse.setMessage("Not in Sequential Order");
								return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
							}
						}
					}
					log.debug("----------" + documentNumberLocal + " Saving to Server Success---------");
				}
			}
		}
		TaskSubmissionResponse taskSubmissionResponse = new TaskSubmissionResponse();
		try {
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
	 * POST /executive-task-execution : Create or update executiveTaskExecution.
	 *
	 * @param executiveTaskSubmissionDTO
	 *            the executiveTaskSubmissionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         TaskSubmissionResponse
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
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
	 * @param executiveTaskSubmissionDTO
	 *            the executiveTaskSubmissionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         TaskSubmissionResponse
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
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
	 * @param executiveTaskSubmissionDTO
	 *            the executiveTaskSubmissionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         TaskSubmissionResponse
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
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
	 * @param executiveTaskSubmissionDTO
	 *            the executiveTaskSubmissionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         TaskSubmissionResponse
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
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
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
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
		return filledFormRepository
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

			List<InventoryVoucherHeader> oldInventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findAllByExecutiveTaskExecutionPid(opExecutiveTaskExecution.get().getPid());

			List<AccountingVoucherHeader> oldAccountingVoucherHeaders = accountingVoucherHeaderRepository
					.findAllByExecutiveTaskExecutionPid(opExecutiveTaskExecution.get().getPid());

			List<DynamicDocumentHeader> oldDynamicDocumentHeaders = dynamicDocumentHeaderRepository
					.findAllByExecutiveTaskExecutionPid(opExecutiveTaskExecution.get().getPid());

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
			System.out.println("order sent to ** " + apiBaseUrl);
			log.info("checking url for :modern ");
			if (apiBaseUrl != null) {
				log.info("order sending to **:modern ");
				modernSalesDataService.saveTransactions(tsTransactionWrapper, companyId, apiBaseUrl);
			}
		}
	}
}
