package com.orderfleet.webapp.web.rest.integration;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

/**
 * used to save executive task execution.
 *
 *
 * @author Sarath
 * @since Sep 28, 2017
 *
 */
@RestController
@RequestMapping(value = "/api/tp/v1")
public class UploadTaskSubmissionResource {

	private final Logger log = LoggerFactory.getLogger(UploadTaskSubmissionResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private ActivityService activityService;

	@Inject
	private DocumentService documentService;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@RequestMapping(value = "/executive-task-execution", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveExecutiveTaskExecution(
			@Valid @RequestBody ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		log.debug("REST request to save executive-task-execution account profile name : {}",
				executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().getAccountProfileName());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.EXECUTIVE_TASK_EXECUTION).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					saveExecutiveTaskSubmission(executiveTaskSubmissionDTO, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>("executive-task-execution sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	private void saveExecutiveTaskSubmission(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO,
			SyncOperation syncOperation) {

		long start = System.nanoTime();

		Optional<User> userDTO = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compId and name Ignore case";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfile> optionalAP = accountProfileRepository.findByCompanyIdAndNameIgnoreCase(
				SecurityUtils.getCurrentUsersCompanyId(),
				executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().getAccountProfileName());
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
		Optional<ActivityDTO> activityDTO = activityService
				.findByName(executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().getActivityName());
		ActivityDTO activity = new ActivityDTO();
		if (activityDTO.isPresent()) {
			activity = activityDTO.get();
		}
		if (!activityDTO.isPresent()) {
			activity = new ActivityDTO();
			activity.setName(executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().getActivityName());
			activity.setActivated(true);
			activity.setHasDefaultAccount(false);
			activity.setDescription("used to send datas from xls");
			activity = activityService.save(activity);
		}

		ExecutiveTaskExecutionDTO eteDTO = new ExecutiveTaskExecutionDTO();

		AccountProfile accp = optionalAP.get();

		eteDTO.setAccountProfileName(accp.getName());
		eteDTO.setAccountProfilePid(accp.getPid());
		eteDTO.setAccountTypeName(accp.getAccountType().getName());
		eteDTO.setAccountTypePid(accp.getAccountType().getPid());
		eteDTO.setDate(LocalDateTime.now());

		eteDTO.setActivityName(activity.getName());
		eteDTO.setActivityPid(activity.getPid());
		eteDTO.setActivityStatus(ActivityStatus.RECEIVED);
		eteDTO.setLocationType(LocationType.NoLocation);
		eteDTO.setUserName(userDTO.get().getFirstName());
		eteDTO.setUserPid(userDTO.get().getPid());
		eteDTO.setRemarks("XLS UPLOAD");

		List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();
		for (InventoryVoucherHeaderDTO headerDTO : executiveTaskSubmissionDTO.getInventoryVouchers()) {
			InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();
			Optional<DocumentDTO> documentDTO = documentService.findByName(headerDTO.getDocumentName());

			DocumentDTO document = new DocumentDTO();
			if (!documentDTO.isPresent()) {
				DocumentDTO dto = new DocumentDTO();
				dto.setName(headerDTO.getDocumentName());
				dto.setDocumentType(DocumentType.INVENTORY_VOUCHER);
				dto.setDocumentPrefix(headerDTO.getDocumentName());
				dto = documentService.save(dto);
				document = dto;
			} else {
				document = documentDTO.get();
			}

			long timeInMilliSecond = System.currentTimeMillis();
			String uniquString = RandomUtil.generateServerDocumentNo().substring(0,
					RandomUtil.generateServerDocumentNo().length() - 10);
			String uniqueDocumentNo = timeInMilliSecond + "_" + userDTO.get().getLogin() + "_" + uniquString;

			inventoryVoucherHeaderDTO.setReceiverAccountPid(accp.getPid());
			inventoryVoucherHeaderDTO.setDocumentNumberLocal(uniqueDocumentNo);
			inventoryVoucherHeaderDTO.setDocumentNumberServer(uniqueDocumentNo);
			inventoryVoucherHeaderDTO.setDocumentPid(document.getPid());
			inventoryVoucherHeaderDTO.setDocumentName(document.getName());
			inventoryVoucherHeaderDTO.setStatus(true);
			inventoryVoucherHeaderDTO.setDocumentDate(LocalDateTime.now());
			inventoryVoucherHeaderDTO.setInventoryVoucherDetails(headerDTO.getInventoryVoucherDetails());
			inventoryVouchers.add(inventoryVoucherHeaderDTO);
		}

		ExecutiveTaskSubmissionDTO executiveTaskSubmission = new ExecutiveTaskSubmissionDTO();

		executiveTaskSubmission.setExecutiveTaskExecutionDTO(eteDTO);
		executiveTaskSubmission.setInventoryVouchers(inventoryVouchers);

		saveTPExecutiveTaskSubmission(executiveTaskSubmission);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

	/**
	 * saving executiveTaskSubmission from ThirdParty
	 *
	 * @author Sarath
	 * @param executiveTaskSubmissionDTO
	 */

	@Transactional
	public void saveTPExecutiveTaskSubmission(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		Company company = user.getCompany();
		EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUser(user);
		// save Executive Task Execution
		ExecutiveTaskExecution executiveTaskExecution = saveExecutiveTaskExecution(company, user,
				executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO());
		// save Inventory Vouchers
		saveInventoryVouchers(company, user, employeeProfile, executiveTaskExecution,
				executiveTaskSubmissionDTO.getInventoryVouchers());
	}

	/**
	 * save Executive Task Execution
	 * 
	 * @param company
	 * @param user
	 * @param executiveTaskExecutionDTO
	 * @return
	 */
	@Transactional
	private ExecutiveTaskExecution saveExecutiveTaskExecution(Company company, User user,
			ExecutiveTaskExecutionDTO executiveTaskExecutionDTO) {
		ExecutiveTaskExecution executiveTaskExecution = new ExecutiveTaskExecution();
		// set pid
		executiveTaskExecution.setPid(ExecutiveTaskExecutionService.PID_PREFIX + RandomUtil.generatePid());
		executiveTaskExecution.setClientTransactionKey(executiveTaskExecutionDTO.getClientTransactionKey());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		executiveTaskExecution.setAccountProfile(
				accountProfileRepository.findOneByPid(executiveTaskExecutionDTO.getAccountProfilePid()).get());
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
		if (executiveTaskExecutionDTO.getAccountTypePid() != null) {
			 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "AT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="get one by pid";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			executiveTaskExecution.setAccountType(
					accountTypeRepository.findOneByPid(executiveTaskExecutionDTO.getAccountTypePid()).get());
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
		} else {
			executiveTaskExecution.setAccountType(executiveTaskExecution.getAccountProfile().getAccountType());
		}
		executiveTaskExecution
				.setActivity(activityRepository.findOneByPid(executiveTaskExecutionDTO.getActivityPid()).get());
		executiveTaskExecution.setDate(executiveTaskExecutionDTO.getDate());
		executiveTaskExecution.setRemarks(executiveTaskExecutionDTO.getRemarks());
		executiveTaskExecution.setUser(user);
		executiveTaskExecution.setActivityStatus(ActivityStatus.RECEIVED);
		executiveTaskExecution.setIsGpsOff(executiveTaskExecutionDTO.getIsGpsOff());
		executiveTaskExecution.setIsMobileDataOff(executiveTaskExecutionDTO.getIsMobileDataOff());
		executiveTaskExecution.setStartTime(executiveTaskExecutionDTO.getStartTime());
		executiveTaskExecution.setEndTime(executiveTaskExecutionDTO.getEndTime());
		executiveTaskExecution.setStartIsGpsOff(executiveTaskExecutionDTO.getStartIsGpsOff());
		executiveTaskExecution.setStartIsMobileDataOff(executiveTaskExecutionDTO.getStartIsMobileDataOff());
		// set Executive Task Plan
		if (executiveTaskExecutionDTO.getExecutiveTaskPlanPid() != null) {
			Optional<ExecutiveTaskPlan> optionalExecPlan = executiveTaskPlanRepository
					.findOneByPid(executiveTaskExecutionDTO.getExecutiveTaskPlanPid());
			if (optionalExecPlan.isPresent()) {
				executiveTaskExecution.setExecutiveTaskPlan(optionalExecPlan.get());
			}
		}
		// set location
		LocationType locationType = executiveTaskExecutionDTO.getLocationType();
		BigDecimal lat = executiveTaskExecutionDTO.getLatitude();
		BigDecimal lon = executiveTaskExecutionDTO.getLongitude();
		executiveTaskExecution.setLocationType(locationType);
		executiveTaskExecution.setLatitude(lat);
		executiveTaskExecution.setLongitude(lon);

		LocationType startLocationType = executiveTaskExecutionDTO.getStartLocationType();
		executiveTaskExecution.setStartLocationType(startLocationType);
		executiveTaskExecution.setStartLatitude(executiveTaskExecutionDTO.getStartLatitude());
		executiveTaskExecution.setStartLongitude(executiveTaskExecutionDTO.getStartLongitude());
		if (locationType.equals(LocationType.TowerLocation)) {
			executiveTaskExecution.setMcc(executiveTaskExecutionDTO.getMcc());
			executiveTaskExecution.setMnc(executiveTaskExecutionDTO.getMnc());
			executiveTaskExecution.setCellId(executiveTaskExecutionDTO.getCellId());
			executiveTaskExecution.setLac(executiveTaskExecutionDTO.getLac());
		} else if (locationType.equals(LocationType.NoLocation) || locationType.equals(LocationType.FlightMode)) {
			executiveTaskExecution.setLocation("No Location");
		}
		if (startLocationType != null) {
			if (startLocationType.equals(LocationType.TowerLocation)) {
				executiveTaskExecution.setStartMcc(executiveTaskExecutionDTO.getStartMcc());
				executiveTaskExecution.setStartMnc(executiveTaskExecutionDTO.getStartMnc());
				executiveTaskExecution.setStartCellId(executiveTaskExecutionDTO.getStartCellId());
				executiveTaskExecution.setStartLac(executiveTaskExecutionDTO.getStartLac());
			} else if (startLocationType.equals(LocationType.NoLocation)
					|| startLocationType.equals(LocationType.FlightMode)) {
				executiveTaskExecution.setStartLocation("No Location");
			}
		} else {
			executiveTaskExecution.setStartLocation("No Location");
		}
		// set company
		executiveTaskExecution.setCompany(company);
		executiveTaskExecution = executiveTaskExecutionRepository.save(executiveTaskExecution);
		return executiveTaskExecution;
	}

	/**
	 * save Inventory Vouchers
	 * 
	 * @param company
	 * @param user
	 * @param employeeProfile
	 * @param executiveTaskExecution
	 * @param inventoryVoucherDTOs
	 * @return
	 */
	@Transactional
	private List<InventoryVoucherHeader> saveInventoryVouchers(Company company, User user,
			EmployeeProfile employeeProfile, ExecutiveTaskExecution executiveTaskExecution,
			List<InventoryVoucherHeaderDTO> inventoryVoucherDTOs) {
		if (inventoryVoucherDTOs != null && inventoryVoucherDTOs.size() > 0) {

			List<InventoryVoucherHeader> inventoryVouchers = new ArrayList<>();
			for (InventoryVoucherHeaderDTO inventoryVoucherDTO : inventoryVoucherDTOs) {
				InventoryVoucherHeader inventoryVoucherHeader = new InventoryVoucherHeader();
				// set pid
				inventoryVoucherHeader.setPid(InventoryVoucherHeaderService.PID_PREFIX + RandomUtil.generatePid());
				inventoryVoucherHeader.setCreatedBy(user);
				inventoryVoucherHeader
						.setDocument(documentRepository.findOneByPid(inventoryVoucherDTO.getDocumentPid()).get());
				inventoryVoucherHeader.setDocumentDate(inventoryVoucherDTO.getDocumentDate());
				// set unique server and local number
				inventoryVoucherHeader.setDocumentNumberLocal(inventoryVoucherDTO.getDocumentNumberLocal());
				inventoryVoucherHeader.setDocumentNumberServer(inventoryVoucherDTO.getDocumentNumberLocal());
				inventoryVoucherHeader.setDocumentTotal(inventoryVoucherDTO.getDocumentTotal());
				inventoryVoucherHeader.setDocumentVolume(inventoryVoucherDTO.getDocumentVolume());
				inventoryVoucherHeader.setDocDiscountAmount(inventoryVoucherDTO.getDocDiscountAmount());
				inventoryVoucherHeader.setDocDiscountPercentage(inventoryVoucherDTO.getDocDiscountPercentage());
				inventoryVoucherHeader.setStatus(true);
				// set price level
				if (inventoryVoucherDTO.getPriceLevelPid() != null) {
					inventoryVoucherHeader.setPriceLevel(
							priceLevelRepository.findOneByPid(inventoryVoucherDTO.getPriceLevelPid()).get());
				}
				if (inventoryVoucherDTO.getEmployeePid() != null
						&& !inventoryVoucherDTO.getEmployeePid().equals("no")) {
					inventoryVoucherHeader.setEmployee(
							employeeProfileRepository.findEmployeeProfileByPid(inventoryVoucherDTO.getEmployeePid()));
				} else {
					inventoryVoucherHeader.setEmployee(employeeProfile);
				}
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				inventoryVoucherHeader.setReceiverAccount(
						accountProfileRepository.findOneByPid(inventoryVoucherDTO.getReceiverAccountPid()).get());
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
				if (inventoryVoucherDTO.getSupplierAccountPid() != null)
					 
					inventoryVoucherHeader.setSupplierAccount(
							accountProfileRepository.findOneByPid(inventoryVoucherDTO.getSupplierAccountPid()).get());
					
				// set company
				inventoryVoucherHeader.setCompany(company);
				// set Executive Task Execution
				inventoryVoucherHeader.setExecutiveTaskExecution(executiveTaskExecution);

				List<InventoryVoucherDetail> inventoryVoucherDetails = new ArrayList<InventoryVoucherDetail>();
				inventoryVoucherDTO.getInventoryVoucherDetails().forEach(inventoryVoucherDetailDTO -> {

					// find source and destination Stock Location
					StockLocation sourceStockLocation = null;
					StockLocation destinationStockLocation = null;
					if (inventoryVoucherDetailDTO.getSourceStockLocationPid() != null)
						sourceStockLocation = stockLocationRepository
								.findOneByPid(inventoryVoucherDetailDTO.getSourceStockLocationPid()).get();
					if (inventoryVoucherDetailDTO.getDestinationStockLocationPid() != null)
						destinationStockLocation = stockLocationRepository
								.findOneByPid(inventoryVoucherDetailDTO.getDestinationStockLocationPid()).get();

					// find referenceInventory Voucher header and detail
					InventoryVoucherHeader referenceInventoryVoucherHeader = null;
					InventoryVoucherDetail referenceInventoryVoucherDetail = null;
							if (inventoryVoucherDetailDTO.getReferenceInventoryVoucherHeaderPid() != null)
						
						referenceInventoryVoucherHeader = inventoryVoucherHeaderRepository
								.findOneByPid(inventoryVoucherDetailDTO.getReferenceInventoryVoucherHeaderPid()).get();

					if (inventoryVoucherDetailDTO.getReferenceInventoryVoucherDetailId() != null)
						referenceInventoryVoucherDetail = inventoryVoucherDetailRepository
								.findOne(inventoryVoucherDetailDTO.getReferenceInventoryVoucherDetailId());

					// product find by alias
					Optional<ProductProfile> opProductProfile = productProfileRepository
							.findByCompanyIdAndAliasIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(),
									inventoryVoucherDetailDTO.getProductName());

					if (opProductProfile.isPresent()) {
						ProductProfile productProfile = opProductProfile.get();
						// Inventory Voucher Batch Details
						List<InventoryVoucherBatchDetail> inventoryVoucherBatchDetails = new ArrayList<>();

						inventoryVoucherDetails.add(new InventoryVoucherDetail(productProfile,
								inventoryVoucherDetailDTO.getQuantity(), inventoryVoucherDetailDTO.getFreeQuantity(),
								inventoryVoucherDetailDTO.getSellingRate(), inventoryVoucherDetailDTO.getMrp(),
								inventoryVoucherDetailDTO.getPurchaseRate(),
								inventoryVoucherDetailDTO.getTaxPercentage(),
								inventoryVoucherDetailDTO.getDiscountPercentage(),
								inventoryVoucherDetailDTO.getBatchNumber(), inventoryVoucherDetailDTO.getBatchDate(),
								inventoryVoucherDetailDTO.getRowTotal(), inventoryVoucherDetailDTO.getDiscountAmount(),
								inventoryVoucherDetailDTO.getTaxAmount(), inventoryVoucherDetailDTO.getLength(),
								inventoryVoucherDetailDTO.getWidth(), inventoryVoucherDetailDTO.getThickness(),
								inventoryVoucherDetailDTO.getSize(), inventoryVoucherDetailDTO.getColor(),
								inventoryVoucherDetailDTO.getItemtype(), sourceStockLocation, destinationStockLocation,
								referenceInventoryVoucherHeader, referenceInventoryVoucherDetail,
								inventoryVoucherDetailDTO.getRemarks(), inventoryVoucherBatchDetails));
					}
				});

				inventoryVoucherHeader.setInventoryVoucherDetails(inventoryVoucherDetails);
				inventoryVouchers.add(inventoryVoucherHeader);

			}
			return inventoryVoucherHeaderRepository.save(inventoryVouchers);
		}
		return null;
	}
}
