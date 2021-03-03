package com.orderfleet.webapp.service.async;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentApproval;
import com.orderfleet.webapp.domain.DocumentApprovalLevel;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.StageDetail;
import com.orderfleet.webapp.domain.StageHeader;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.TaskNotificationSetting;
import com.orderfleet.webapp.domain.TaskReferenceDocument;
import com.orderfleet.webapp.domain.TaskSetting;
import com.orderfleet.webapp.domain.TaskUserNotificationSetting;
import com.orderfleet.webapp.domain.TaskUserSetting;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.domain.UserTaskAssignment;
import com.orderfleet.webapp.domain.UserTaskExecutionLog;
import com.orderfleet.webapp.domain.enums.ActivityEvent;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DashboardItemType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LoadMobileData;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.domain.enums.PriorityStatus;
import com.orderfleet.webapp.domain.enums.TargetType;
import com.orderfleet.webapp.domain.enums.TaskCreatedType;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.domain.enums.TaskPlanType;
import com.orderfleet.webapp.domain.enums.TaskStatus;
import com.orderfleet.webapp.domain.model.FirebaseData;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.api.GeoLocationServiceException;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DocumentApprovalLevelRepository;
import com.orderfleet.webapp.repository.DocumentApprovalRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.repository.SalesTargetReportSettingSalesTargetBlockRepository;
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.repository.TaskNotificationSettingRepository;
import com.orderfleet.webapp.repository.TaskReferenceDocumentRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.repository.TaskSettingRepository;
import com.orderfleet.webapp.repository.TaskUserNotificationSettingRepository;
import com.orderfleet.webapp.repository.TaskUserSettingRepository;
import com.orderfleet.webapp.repository.UserDeviceRepository;
import com.orderfleet.webapp.repository.UserTaskAssignmentRepository;
import com.orderfleet.webapp.repository.UserTaskExecutionLogRepository;
import com.orderfleet.webapp.service.DashboardItemService;
import com.orderfleet.webapp.service.DocumentApprovalService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskPlanService;
import com.orderfleet.webapp.service.KilometreCalculationService;
import com.orderfleet.webapp.service.TaskReferenceDocumentService;
import com.orderfleet.webapp.service.TaskService;
import com.orderfleet.webapp.service.UserDistanceService;
import com.orderfleet.webapp.service.UserTaskAssignmentService;
import com.orderfleet.webapp.service.exception.TaskSubmissionPostSaveException;
import com.orderfleet.webapp.service.impl.ExecutiveTaskExecutionSmsService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardItemDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardWebSocketDataDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDetailDTO;
import com.orderfleet.webapp.web.rest.dto.FirebaseRequest;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.KilometerCalculationDTO;
import com.orderfleet.webapp.web.rest.dto.MapDistanceApiDTO;
import com.orderfleet.webapp.web.rest.dto.MapDistanceDTO;
import com.orderfleet.webapp.web.rest.dto.UserDistanceDTO;
import com.orderfleet.webapp.web.vendor.odoo.service.SendInvoiceOdooService;
import com.orderfleet.webapp.web.vendor.odoo.service.SendTransactionOdooService;

/**
 * Service for update after save documents(ETS).
 * <p>
 * We use the @Async annotation to save asynchronously.
 * </p>
 */
@Service
public class TaskSubmissionPostSave {

	private final Logger log = LoggerFactory.getLogger(TaskSubmissionPostSave.class);

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private SimpMessagingTemplate simpMessagingTemplate;

	@Inject
	private DashboardItemService dashboardItemService;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private TaskSettingRepository taskSettingRepository;

	@Inject
	private TaskUserSettingRepository taskUserSettingRepository;

	@Inject
	private TaskReferenceDocumentRepository taskReferenceDocumentRepository;

	@Inject
	private UserDeviceRepository userDeviceRepository;

	@Inject
	private FirebaseService firebaseService;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private DocumentApprovalLevelRepository documentApprovalLevelRepository;

	@Inject
	private DocumentApprovalRepository documentApprovalRepository;

	@Inject
	private UserTaskAssignmentRepository userTaskAssignmentRepository;

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private UserTaskExecutionLogRepository userTaskExecutionLogRepository;

	@Inject
	private MailService mailService;

	@Inject
	private UserDistanceService userDistanceService;

	@Inject
	private KilometreCalculationService kilometreCalculationService;

	@Inject
	private TaskNotificationSettingRepository taskNotificationSettingRepository;

	@Inject
	private TaskUserNotificationSettingRepository taskUserNotificationSettingRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;

	@Inject
	private SalesTargetGroupProductRepository salesTargetGroupProductRepository;

	@Inject
	private SalesTargetReportSettingSalesTargetBlockRepository salesTargetReportSettingSalesTargetBlockRepository;

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private StageRepository stageRepository;

	@Inject
	private StageHeaderRepository stageHeaderRepository;

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private GeoLocationService geoLocationService;

	@Inject
	private ExecutiveTaskExecutionSmsService executiveTaskExecutionSmsService;

	@Inject
	private SendTransactionOdooService sendTransactionOdooService;

	private FirebaseRequest firebaseRequest;
	private List<UserDevice> userDevices;

	@Async
	@Transactional
	public void doPostSaveExecutivetaskSubmission(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper,
			ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		ExecutiveTaskExecution executiveTaskExecution = tsTransactionWrapper.getExecutiveTaskExecution();
		List<InventoryVoucherHeader> inventoryVouchers = tsTransactionWrapper.getInventoryVouchers();
		List<AccountingVoucherHeader> accountingVouchers = tsTransactionWrapper.getAccountingVouchers();
		List<DynamicDocumentHeader> dynamicDocuments = tsTransactionWrapper.getDynamicDocuments();
		log.debug("Started processing executive task submission post save..");

		try {
			log.info("Update location info");
			// update location
			updateLocationInfo(executiveTaskExecution);

			log.info("Update start location");
			// update Start location
			updateStartLocationInfo(executiveTaskExecution);

			// update dash board and live tracking view via web socket
			if (tsTransactionWrapper.isDashboardUpdate()) {
				log.info("Update dashboard");
				updateDashboad(executiveTaskExecution, executiveTaskSubmissionDTO);
			}

			log.info("Create Task And AssignToUsers, then SendNotification");
			// create new task, the assign to users
			createTaskAndAssignToUsersAndSendNotification(executiveTaskExecution, inventoryVouchers, accountingVouchers,
					dynamicDocuments);

			log.info("Save UserTask and Execution Log");
			// save user task execution log
			saveUserTaskExecutionLog(executiveTaskExecution);

			Optional<CompanyConfiguration> optDistanceTraveled = companyConfigurationRepository.findByCompanyPidAndName(
					executiveTaskExecution.getCompany().getPid(), CompanyConfig.DISTANCE_TRAVELED);
			Optional<CompanyConfiguration> optLocationVariance = companyConfigurationRepository.findByCompanyPidAndName(
					executiveTaskExecution.getCompany().getPid(), CompanyConfig.LOCATION_VARIANCE);
			if (optDistanceTraveled.isPresent()) {
				if (Boolean.valueOf(optDistanceTraveled.get().getValue())) {
					log.info("Update Distance travelled");
					// saveUpdate distance
					updateDistance(executiveTaskExecution);
					log.info("Updated Distance travelled =============");
				}
			}
			log.info("Test log msg =======================");
			if (optLocationVariance.isPresent()) {
				if (Boolean.valueOf(optLocationVariance.get().getValue())) {
					log.info("Update Location variance start ==========");
					// saveUpdate distance
					updateLocationVariance(executiveTaskExecution);
				}
			}
			User user = executiveTaskExecution.getUser();

			Company company = user.getCompany();

			if (company.getSmsApiKey() != null && !company.getSmsApiKey().isEmpty()) {
				executiveTaskExecutionSmsService.sendSms(tsTransactionWrapper);
			}

			Optional<CompanyConfiguration> optSendToOdoo = companyConfigurationRepository
					.findByCompanyPidAndName(executiveTaskExecution.getCompany().getPid(), CompanyConfig.SEND_TO_ODOO);
			// send inventory voucher to odoo
//			if (optSendToOdoo.isPresent()) {
//				if (Boolean.valueOf(optSendToOdoo.get().getValue())) {
//					sendTransactionOdooService.sendInvoicesToOdoo(tsTransactionWrapper);
//				}
//			}

//			if (inventoryVouchers != null) {
//				updateOpeningStockOfSourceStockLocation(inventoryVouchers);
//			}

		} catch (TaskSubmissionPostSaveException ex) {
			log.debug("Exception while processing doPostSaveExecutivetaskSubmission method {}", ex);
			sendErrorEmail("Error while saving executive order submission asynchronously", ex);
		} catch (Exception ex) {
			String errorMsg = "In Main Exception : Error while saving executive order submission asynchronously. "
					+ "Company : " + executiveTaskExecution.getCompany().getLegalName() + "User: "
					+ executiveTaskExecution.getUser().getLogin() + " Activity:"
					+ executiveTaskExecution.getActivity().getName() + " Account Profile :"
					+ executiveTaskExecution.getAccountProfile().getName();
			log.error(errorMsg);
			sendErrorEmail(errorMsg, ex);
		}
	}

//	private void updateOpeningStockOfSourceStockLocation(List<InventoryVoucherHeader> inventoryVouchers) {
//
//		for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVouchers) {
//			if (inventoryVoucherHeader.getInventoryVoucherDetails() != null) {
//				for (InventoryVoucherDetail inventoryVoucherDetail : inventoryVoucherHeader
//						.getInventoryVoucherDetails()) {
//					if (inventoryVoucherDetail.getSourceStockLocation() != null) {
//						Optional<OpeningStock> opOpeningStock = openingStockRepository
//								.findTop1ByProductProfilePidAndStockLocationPidOrderByCreatedDateDesc(
//										inventoryVoucherDetail.getProduct().getPid(),
//										inventoryVoucherDetail.getSourceStockLocation().getPid());
//						if (opOpeningStock.isPresent()) {
//							OpeningStock openingStock = opOpeningStock.get();
//							double newQuantity = openingStock.getQuantity() - inventoryVoucherDetail.getQuantity();
//							openingStock.setQuantity(newQuantity);
//							openingStockRepository.save(openingStock);
//						}
//					}
//				}
//			}
//		}
//	}

	private void updateLocationInfo(ExecutiveTaskExecution executiveTaskExecution)
			throws TaskSubmissionPostSaveException {
		LocationType locationType = executiveTaskExecution.getLocationType();
		// geo location
		if (locationType.equals(LocationType.GpsLocation) || (executiveTaskExecution.getLatitude() != null
				&& executiveTaskExecution.getLatitude().compareTo(BigDecimal.ZERO) != 0
				&& executiveTaskExecution.getLongitude() != null
				&& executiveTaskExecution.getLongitude().compareTo(BigDecimal.ZERO) != 0)) {
			try {
				executiveTaskExecution.setLocation(geoLocationService.findAddressFromLatLng(
						executiveTaskExecution.getLatitude() + "," + executiveTaskExecution.getLongitude()));

			} catch (GeoLocationServiceException lae) {
				log.debug("Exception while calling google GPS geo location API {}", lae);
				executiveTaskExecution.setLocation("Unable to find location");
				String locationDetails = "GPS Location => Lat: " + executiveTaskExecution.getLatitude() + " Lng: "
						+ executiveTaskExecution.getLongitude();
				String errorMsg = "Exception while calling google GPS geo location API. " + "Company : "
						+ executiveTaskExecution.getCompany().getLegalName() + " User: "
						+ executiveTaskExecution.getUser().getLogin() + " Location :"
						+ executiveTaskExecution.getLocation() + " Location Details :" + locationDetails
						+ " - ETS Object = " + executiveTaskExecution;
				sendErrorEmail(errorMsg, lae);
			}
		} else {
			executiveTaskExecution.setLocation("No Location");
		}
// COMMENTING THE TOWER LOCATION API WHILE ORDER TAKING ============================================>>>
		// tower location
//		String mcc = executiveTaskExecution.getMcc() == null ? "" : executiveTaskExecution.getMcc();
//		String mnc = executiveTaskExecution.getMnc() == null ? "" : executiveTaskExecution.getMnc();
//		String cellId = executiveTaskExecution.getCellId() == null ? "" : executiveTaskExecution.getCellId();
//		String lac = executiveTaskExecution.getLac() == null ? "" : executiveTaskExecution.getLac();
//		if (locationType.equals(LocationType.TowerLocation)
//				|| (mcc.length() > 1 && mnc.length() > 1 && cellId.length() > 1 && lac.length() > 1)) {
//			try {
//
//				TowerLocation towerLocation = geoLocationService.findAddressFromCellTower(
//						executiveTaskExecution.getMcc(), executiveTaskExecution.getMnc(),
//						executiveTaskExecution.getCellId(), executiveTaskExecution.getLac());
//				executiveTaskExecution.setTowerLocation(towerLocation.getLocation());
//				executiveTaskExecution.setTowerLatitude(towerLocation.getLat());
//				executiveTaskExecution.setTowerLongitude(towerLocation.getLan());
//			} catch (GeoLocationServiceException lae) {
//				log.debug("Exception while calling google Tower geo location API {}", lae);
//				String locationDetails = "Tower Location => mcc:" + executiveTaskExecution.getMcc() + " mnc:"
//						+ executiveTaskExecution.getMnc() + " cellID:" + executiveTaskExecution.getCellId() + " lac:"
//						+ executiveTaskExecution.getLac();
//				String errorMsg = "Exception while calling google Tower geo location API. " + "Company : "
//						+ executiveTaskExecution.getCompany().getLegalName() + " User: "
//						+ executiveTaskExecution.getUser().getLogin() + " Location :"
//						+ executiveTaskExecution.getLocation() + " Location Details :" + locationDetails
//						+ " - ETS Object = " + executiveTaskExecution;
//				sendErrorEmail(errorMsg, lae);
//			}
//		} else {
//			executiveTaskExecution.setTowerLocation("No Location");
//		}

		if (locationType.equals(LocationType.NoLocation) || locationType.equals(LocationType.FlightMode)) {
			executiveTaskExecution.setLocation("No Location");
		}
		try {
			// update
			executiveTaskExecutionRepository.save(executiveTaskExecution);
		} catch (Exception e) {
			log.debug("Exception while saving geo location details in executive task submission method {}", e);
			throw new TaskSubmissionPostSaveException(
					"Exception while saving geo location details in executive task submission method. " + "Company : "
							+ executiveTaskExecution.getCompany().getLegalName() + " User: "
							+ executiveTaskExecution.getUser().getLogin() + " Activity:"
							+ executiveTaskExecution.getActivity().getName() + " Account:"
							+ executiveTaskExecution.getAccountProfile().getName() + " - ETS Object = "
							+ executiveTaskExecution);
		}
	}

	private void updateStartLocationInfo(ExecutiveTaskExecution executiveTaskExecution) {
		LocationType startLocationType = executiveTaskExecution.getStartLocationType();
		if (startLocationType != null) {
			if (startLocationType.equals(LocationType.NoLocation)
					|| startLocationType.equals(LocationType.FlightMode)) {
				return;
			}
			if (startLocationType.equals(LocationType.GpsLocation)) {
				if (executiveTaskExecution.getLatitude() != null
						&& executiveTaskExecution.getLatitude().compareTo(BigDecimal.ZERO) != 0
						&& executiveTaskExecution.getLongitude() != null
						&& executiveTaskExecution.getLongitude().compareTo(BigDecimal.ZERO) != 0
						&& executiveTaskExecution.getStartLatitude() != null
						&& executiveTaskExecution.getStartLatitude().compareTo(BigDecimal.ZERO) != 0
						&& executiveTaskExecution.getStartLongitude() != null
						&& executiveTaskExecution.getStartLongitude().compareTo(BigDecimal.ZERO) != 0) {
					try {
						executiveTaskExecution.setStartLocation(
								geoLocationService.findAddressFromLatLng(executiveTaskExecution.getStartLatitude() + ","
										+ executiveTaskExecution.getStartLongitude()));
					} catch (GeoLocationServiceException lae) {
						log.debug("Start Location Finding Exception while calling google GPS geo location API {}", lae);
						executiveTaskExecution.setStartLocation("Unable to find location");
						String locationDetails = "GPS Location => Lat: " + executiveTaskExecution.getLatitude()
								+ " Lng: " + executiveTaskExecution.getLongitude();
						String errorMsg = "Exception while calling google GPS geo location API. " + "Company : "
								+ executiveTaskExecution.getCompany().getLegalName() + " User: "
								+ executiveTaskExecution.getUser().getLogin() + " Location :"
								+ executiveTaskExecution.getLocation() + " Location Details :" + locationDetails
								+ " - ETS Object = " + executiveTaskExecution;
						sendErrorEmail(errorMsg, lae);
					}
				}
			}
// COMMENTING THE TOWER LOCATION API WHILE ORDER TAKING ============================================>>>
//			else if (startLocationType.equals(LocationType.TowerLocation)) {
//				TowerLocation towerLocation = geoLocationService.findAddressFromCellTower(
//						executiveTaskExecution.getStartMcc(), executiveTaskExecution.getStartMnc(),
//						executiveTaskExecution.getStartCellId(), executiveTaskExecution.getStartLac());
//				if (towerLocation != null) {
//					executiveTaskExecution.setStartLocation(towerLocation.getLocation());
//					executiveTaskExecution.setStartLatitude(towerLocation.getLat());
//					executiveTaskExecution.setStartLongitude(towerLocation.getLan());
//				} else {
//					executiveTaskExecution.setStartLocation("Unable to find location");
//				}
//			}
			else if (startLocationType.equals(LocationType.NoLocation)
					|| startLocationType.equals(LocationType.FlightMode)) {
				executiveTaskExecution.setStartLocation("No Location");
			}
		} else {
			executiveTaskExecution.setStartLocation("No Location");
		}
		// update
		executiveTaskExecutionRepository.save(executiveTaskExecution);
	}

	private void updateDashboad(ExecutiveTaskExecution executiveTaskExecution,
			ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) throws TaskSubmissionPostSaveException {

		Optional<EmployeeProfile> opEmployee = employeeProfileService
				.findByUserPid(executiveTaskExecution.getUser().getPid());

		ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO(executiveTaskExecution);
		if (opEmployee.isPresent()) {
			executiveTaskExecutionDTO.setEmployeeName(opEmployee.get().getName());
		}
		Long companyId = executiveTaskExecution.getCompany().getId();
		// update live tracking(map view)
		simpMessagingTemplate.convertAndSend("/live-tracking/map-view/" + companyId, executiveTaskExecutionDTO);

		// live update dash board view
		DashboardWebSocketDataDTO dashboardWebSocketData = createDashboardWebSocketData(executiveTaskExecutionDTO,
				executiveTaskSubmissionDTO, companyId);
		// decrement plan skipped count if executed
		if (executiveTaskExecution.getExecutiveTaskPlan() != null) {
			Optional<ExecutiveTaskPlan> optionalExecutiveTaskPlan = executiveTaskPlanRepository
					.findOneByPid(executiveTaskExecution.getExecutiveTaskPlan().getPid());
			if (optionalExecutiveTaskPlan.isPresent()
					&& optionalExecutiveTaskPlan.get().getTaskPlanStatus().equals(TaskPlanStatus.SKIPPED)) {
				dashboardWebSocketData.setDecrementSkipCount(true);
			}
		}
		simpMessagingTemplate.convertAndSend("/live-tracking/dashboard-view/" + companyId, dashboardWebSocketData);
	}

	private void createTaskAndAssignToUsersAndSendNotification(ExecutiveTaskExecution executiveTaskExecution,
			List<InventoryVoucherHeader> inventoryVouchers, List<AccountingVoucherHeader> accountingVouchers,
			List<DynamicDocumentHeader> dynamicDocuments) throws TaskSubmissionPostSaveException {
		log.info("--createTaskAndAssignToUsersAndSendNotification");
		// create new Task And Assign to Users
		if (inventoryVouchers != null && inventoryVouchers.size() > 0) {
			for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVouchers) {
				List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = inventoryVoucherHeader
						.getInventoryVoucherDetails().stream().map(InventoryVoucherDetailDTO::new)
						.collect(Collectors.toList());
				// create new Task And Assign to Users
				createAndAssignTask(executiveTaskExecution, inventoryVoucherHeader.getDocument(),
						inventoryVoucherHeader.getPid(), inventoryVoucherHeader.getDocumentNumberServer(), null,
						inventoryVoucherDetailDTOs, null, null, inventoryVoucherHeader.getEmployee());
				// create new DocumentApproval And Assign to Users
				createDocumentApproval(inventoryVoucherHeader.getDocument(), inventoryVoucherDetailDTOs, null, null,
						inventoryVoucherHeader.getDocumentNumberLocal(), executiveTaskExecution);

				// Send task notification
				sendTaskNotificationToUsers(executiveTaskExecution, inventoryVoucherHeader.getDocument(),
						inventoryVoucherHeader.getPid(), inventoryVoucherHeader.getDocumentNumberServer(), null,
						inventoryVoucherDetailDTOs, null, null);

			}
		}
		if (accountingVouchers != null && accountingVouchers.size() > 0) {
			for (AccountingVoucherHeader accountingVoucherHeader : accountingVouchers) {
				List<AccountingVoucherDetailDTO> accountingVoucherDetailDTOs = accountingVoucherHeader
						.getAccountingVoucherDetails().stream().map(AccountingVoucherDetailDTO::new)
						.collect(Collectors.toList());
				// create new Task And Assign to Users
				createAndAssignTask(executiveTaskExecution, accountingVoucherHeader.getDocument(),
						accountingVoucherHeader.getPid(), accountingVoucherHeader.getDocumentNumberServer(), null, null,
						accountingVoucherDetailDTOs, null, accountingVoucherHeader.getEmployee());
				// create new DocumentApproval And Assign to Users
				createDocumentApproval(accountingVoucherHeader.getDocument(), null, accountingVoucherDetailDTOs, null,
						accountingVoucherHeader.getDocumentNumberLocal(), executiveTaskExecution);

				// Send task notification
				sendTaskNotificationToUsers(executiveTaskExecution, accountingVoucherHeader.getDocument(),
						accountingVoucherHeader.getPid(), accountingVoucherHeader.getDocumentNumberServer(), null, null,
						accountingVoucherDetailDTOs, null);

			}
		}

		if (dynamicDocuments != null && !dynamicDocuments.isEmpty()) {
			log.info("createAndAssignTask dynamicdocuments");
			for (DynamicDocumentHeader dynamicDocumentHeader : dynamicDocuments) {
				List<FilledFormDetail> datePickerFormElements = new ArrayList<>();
				List<FilledFormDetailDTO> filledFormDetailList = new ArrayList<>();
				dynamicDocumentHeader.getFilledForms().forEach(filledForm -> {
					// set voucher details
					for (FilledFormDetail filledFormDetail : filledForm.getFilledFormDetails()) {
						if (filledFormDetail.getFormElement().getFormElementType().getName().equals("datePicker")) {
							datePickerFormElements.add(filledFormDetail);
						}
						filledFormDetailList.add(new FilledFormDetailDTO(filledFormDetail));
					}
				});
				log.info("for loop createAndAssignTask dynamicdocuments");
				// create new Task And Assign to Users
				createAndAssignTask(executiveTaskExecution, dynamicDocumentHeader.getDocument(),
						dynamicDocumentHeader.getPid(), dynamicDocumentHeader.getDocumentNumberServer(),
						datePickerFormElements, null, null, filledFormDetailList, dynamicDocumentHeader.getEmployee());
				// create new DocumentApproval And Assign to Users
				createDocumentApproval(dynamicDocumentHeader.getDocument(), null, null, filledFormDetailList,
						dynamicDocumentHeader.getDocumentNumberLocal(), executiveTaskExecution);

				// Send task notification
				sendTaskNotificationToUsers(executiveTaskExecution, dynamicDocumentHeader.getDocument(),
						dynamicDocumentHeader.getPid(), dynamicDocumentHeader.getDocumentNumberServer(),
						datePickerFormElements, null, null, filledFormDetailList);
				if (executiveTaskExecution.getCompany().getId() == 304935L && datePickerFormElements.size() == 1) {
					saveTaskAndUserTaskAutomatically(executiveTaskExecution, dynamicDocumentHeader,
							datePickerFormElements);
				}

			}
		}

	}

	private void saveUserTaskExecutionLog(ExecutiveTaskExecution executiveTaskExecution) {
		UserTaskAssignment userTaskAssignment = userTaskAssignmentRepository
				.findTop1ByExecutiveUserIdAndTaskActivityIdAndTaskAccountProfileIdAndTaskStatusOrderByIdDesc(
						executiveTaskExecution.getUser().getId(), executiveTaskExecution.getActivity().getId(),
						executiveTaskExecution.getAccountProfile().getId(), TaskStatus.OPENED);
		if (userTaskAssignment != null) {
			UserTaskExecutionLog userTaskExecutionLog = new UserTaskExecutionLog(userTaskAssignment,
					executiveTaskExecution, executiveTaskExecution.getCompany());
			userTaskExecutionLogRepository.save(userTaskExecutionLog);
		}
	}

	/**
	 * create DashboardWebSocketDataDTO object for update dashboard via websocket
	 * 
	 * @param executiveTaskSubmissionDTO
	 * @param executiveTaskExecutionDTO
	 * @return
	 */
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

		dashboardWebSocketData.setReloadNumberCircle(true);
		assignDashboardSummaryData(dashboardWebSocketData, executiveTaskExecutionDTO, executiveTaskSubmissionDTO,
				companyId);
		return dashboardWebSocketData;
	}

	public void assignDashboardSummaryData(DashboardWebSocketDataDTO dashboardWebSocketData,
			ExecutiveTaskExecutionDTO executiveTaskExecutionDTO, ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO,
			Long companyId) {
		List<InventoryVoucherHeaderDTO> inventoryVouchers = executiveTaskSubmissionDTO.getInventoryVouchers();
		List<AccountingVoucherHeaderDTO> accountingVouchers = executiveTaskSubmissionDTO.getAccountingVouchers();
		List<DynamicDocumentHeaderDTO> dynamicDocuments = executiveTaskSubmissionDTO.getDynamicDocuments();
		List<DashboardSummaryDTO> summaryDatas = new ArrayList<>();
		List<DashboardItemDTO> dashboardItems = dashboardItemService.findAllByCompanyId(companyId);
		for (DashboardItemDTO dashboardItem : dashboardItems) {
			if (dashboardItem.getDashboardItemType().equals(DashboardItemType.ACTIVITY)) {
				log.info("Inside activity >>>>>>>");
				// if this is first request
				if (executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().getPid() == null) {
					boolean status = checkActivityExistInDashboardItem(dashboardItem, executiveTaskExecutionDTO);
					if (status) {
						DashboardSummaryDTO dashboardSummaryDTO = new DashboardSummaryDTO();
						dashboardSummaryDTO.setDashboardItemPid(dashboardItem.getPid());
						dashboardSummaryDTO.setLabel(dashboardItem.getName());
						dashboardSummaryDTO.setDashboardItemType(dashboardItem.getDashboardItemType());
						dashboardSummaryDTO.setTaskPlanType(dashboardItem.getTaskPlanType());
						dashboardSummaryDTO.setAchieved(1);
						summaryDatas.add(dashboardSummaryDTO);
					}
				}
			} else if (dashboardItem.getDashboardItemType().equals(DashboardItemType.DOCUMENT)) {
				DashboardSummaryDTO dashboardSummaryDTO = checkDocumentExistInDashboardItem(dashboardItem,
						executiveTaskExecutionDTO, inventoryVouchers, accountingVouchers, dynamicDocuments);
				if (dashboardSummaryDTO != null) {
					summaryDatas.add(dashboardSummaryDTO);
				}
			} else if (dashboardItem.getDashboardItemType().equals(DashboardItemType.PRODUCT)) {
				DashboardSummaryDTO dashboardSummaryDTO = checkProductExistInDashboardItem(dashboardItem,
						executiveTaskExecutionDTO, inventoryVouchers);
				if (dashboardSummaryDTO != null) {
					summaryDatas.add(dashboardSummaryDTO);
				}
			} else if (dashboardItem.getDashboardItemType().equals(DashboardItemType.TARGET)) {
				DashboardSummaryDTO dashboardSummaryDTO = getDashboardSummaryWithSalesTargetBlockData(dashboardItem,
						executiveTaskExecutionDTO, inventoryVouchers, companyId);
				if (dashboardSummaryDTO != null) {
					summaryDatas.add(dashboardSummaryDTO);
				}
			}
		}
		dashboardWebSocketData.setDashboardItems(summaryDatas);
	}

	private boolean checkActivityExistInDashboardItem(DashboardItemDTO dashboardItem,
			ExecutiveTaskExecutionDTO executiveTaskExecutionDTO) {
		for (ActivityDTO activity : dashboardItem.getActivities()) {
			if (activity.getPid().equals(executiveTaskExecutionDTO.getActivityPid())) {
				if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
					return true;
				} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
					if (executiveTaskExecutionDTO.getExecutiveTaskPlanPid() != null
							&& !executiveTaskExecutionDTO.getExecutiveTaskPlanPid().isEmpty()) {
						return true;
					}
				} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
					if (executiveTaskExecutionDTO.getExecutiveTaskPlanPid() == null
							|| executiveTaskExecutionDTO.getExecutiveTaskPlanPid().isEmpty()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private DashboardSummaryDTO checkDocumentExistInDashboardItem(DashboardItemDTO dashboardItem,
			ExecutiveTaskExecutionDTO executiveTaskExecutionDTO, List<InventoryVoucherHeaderDTO> inventoryVouchers,
			List<AccountingVoucherHeaderDTO> accountingVouchers, List<DynamicDocumentHeaderDTO> dynamicDocuments) {
		boolean status = false;
		String executiveTaskPlanPid = executiveTaskExecutionDTO.getExecutiveTaskPlanPid();
		if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
			status = true;
		} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
			if (executiveTaskPlanPid != null && !executiveTaskPlanPid.isEmpty()) {
				status = true;
			}
		} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
			if (executiveTaskPlanPid == null || executiveTaskPlanPid.isEmpty()) {
				status = true;
			}
		}
		if (!status) {
			return null;
		}
		if (dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
			log.info("Processing Inventory Voucher >>>>>>>");
			long salesCount = 0;
			double salesAmount = 0;
			boolean addToDashboard = false;
			if (inventoryVouchers != null && !inventoryVouchers.isEmpty()) {
				for (InventoryVoucherHeaderDTO inventoryVoucher : inventoryVouchers) {
					if (dashboardItem.getDocuments().stream()
							.anyMatch(doc -> doc.getPid().equals(inventoryVoucher.getDocumentPid()))) {
						addToDashboard = true;
						if (inventoryVoucher.getIsNew()) {
							salesAmount += inventoryVoucher.getDocumentTotal();
							salesCount += 1;
						}
					}
				}
				if (addToDashboard) {
					DashboardSummaryDTO dashboardSummaryDTO = new DashboardSummaryDTO();
					dashboardSummaryDTO.setDashboardItemPid(dashboardItem.getPid());
					dashboardSummaryDTO.setLabel(dashboardItem.getName());
					dashboardSummaryDTO.setDashboardItemType(dashboardItem.getDashboardItemType());
					dashboardSummaryDTO.setTaskPlanType(dashboardItem.getTaskPlanType());
					dashboardSummaryDTO.setCount(salesCount);
					dashboardSummaryDTO.setAmount(salesAmount);
					return dashboardSummaryDTO;
				}
			}
		} else if (dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
			log.info("Processing Accounting Voucher >>>>>>>");
			long receiptsCount = 0;
			double receiptsAmount = 0;
			boolean addToDashboard = false;
			if (accountingVouchers != null && !accountingVouchers.isEmpty()) {
				for (AccountingVoucherHeaderDTO accountingVoucher : accountingVouchers) {
					if (dashboardItem.getDocuments().stream()
							.anyMatch(doc -> doc.getPid().equals(accountingVoucher.getDocumentPid()))) {
						addToDashboard = true;
						if (accountingVoucher.getIsNew()) {
							receiptsAmount += accountingVoucher.getTotalAmount();
							receiptsCount += 1;
						}
					}
				}
				if (addToDashboard) {
					DashboardSummaryDTO dashboardSummaryDTO = new DashboardSummaryDTO();
					dashboardSummaryDTO.setDashboardItemPid(dashboardItem.getPid());
					dashboardSummaryDTO.setLabel(dashboardItem.getName());
					dashboardSummaryDTO.setDashboardItemType(dashboardItem.getDashboardItemType());
					dashboardSummaryDTO.setTaskPlanType(dashboardItem.getTaskPlanType());
					dashboardSummaryDTO.setCount(receiptsCount);
					dashboardSummaryDTO.setAmount(receiptsAmount);
					return dashboardSummaryDTO;
				}
			}
		} else if (dashboardItem.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
			log.info("Processing Dynamic document >>>>>>>");
			long count = 0;
			boolean addToDashboard = false;
			if (dynamicDocuments != null && !dynamicDocuments.isEmpty()) {
				for (DynamicDocumentHeaderDTO dynamicDocumentDTO : dynamicDocuments) {
					if (dashboardItem.getDocuments().stream()
							.anyMatch(doc -> doc.getPid().equals(dynamicDocumentDTO.getDocumentPid()))) {
						addToDashboard = true;
						if (dynamicDocumentDTO.getIsNew()) {
							count += 1;
						}
					}
				}
				if (addToDashboard) {
					DashboardSummaryDTO dashboardSummaryDTO = new DashboardSummaryDTO();
					dashboardSummaryDTO.setDashboardItemPid(dashboardItem.getPid());
					dashboardSummaryDTO.setLabel(dashboardItem.getName());
					dashboardSummaryDTO.setDashboardItemType(dashboardItem.getDashboardItemType());
					dashboardSummaryDTO.setTaskPlanType(dashboardItem.getTaskPlanType());
					dashboardSummaryDTO.setCount(count);
					dashboardSummaryDTO.setAmount(0);
					return dashboardSummaryDTO;
				}
			}
		}
		return null;
	}

	private DashboardSummaryDTO checkProductExistInDashboardItem(DashboardItemDTO dashboardItem,
			ExecutiveTaskExecutionDTO executiveTaskExecutionDTO, List<InventoryVoucherHeaderDTO> inventoryVouchers) {
		boolean status = false;
		String executiveTaskPlanPid = executiveTaskExecutionDTO.getExecutiveTaskPlanPid();
		if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
			status = true;
		} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
			if (executiveTaskPlanPid != null && !executiveTaskPlanPid.isEmpty()) {
				status = true;
			}
		} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
			if (executiveTaskPlanPid == null || executiveTaskPlanPid.isEmpty()) {
				status = true;
			}
		}
		if (!status) {
			return null;
		}
		double salesVolume = 0;
		double salesAmount = 0;
		boolean addToDashboard = false;
		if (inventoryVouchers != null && !inventoryVouchers.isEmpty()) {
			// find products under groups
			List<String> productGroupPids = dashboardItem.getProductGroups().stream().map(pg -> pg.getPid())
					.collect(Collectors.toList());
			List<ProductProfile> productProfiles = productGroupProductRepository
					.findProductByProductGroupPidIn(productGroupPids);
			if (!productProfiles.isEmpty()) {
				for (InventoryVoucherHeaderDTO inventoryVoucher : inventoryVouchers) {
					double productVolume = 0;
					addToDashboard = true;
					for (InventoryVoucherDetailDTO ivDetailDTO : inventoryVoucher.getInventoryVoucherDetails()) {
						Optional<ProductProfile> optProduct = productProfiles.stream()
								.filter(p -> p.getPid().equals(ivDetailDTO.getProductPid())).findAny();
						if (optProduct.isPresent()) {
							productVolume += (optProduct.get().getUnitQty() == null ? 1 : optProduct.get().getUnitQty())
									* ivDetailDTO.getQuantity();
						}
					}
					if (inventoryVoucher.getIsNew()) {
						salesVolume += productVolume;
						salesAmount += inventoryVoucher.getDocumentTotal();
					}
				}
			}

			if (addToDashboard) {
				DashboardSummaryDTO dashboardSummaryDTO = new DashboardSummaryDTO();
				dashboardSummaryDTO.setDashboardItemPid(dashboardItem.getPid());
				dashboardSummaryDTO.setLabel(dashboardItem.getName());
				dashboardSummaryDTO.setDashboardItemType(dashboardItem.getDashboardItemType());
				dashboardSummaryDTO.setTaskPlanType(dashboardItem.getTaskPlanType());
				dashboardSummaryDTO.setVolume(salesVolume);
				dashboardSummaryDTO.setAmount(salesAmount);
				return dashboardSummaryDTO;
			}
		}
		return null;
	}

	private DashboardSummaryDTO getDashboardSummaryWithSalesTargetBlockData(DashboardItemDTO dashboardItem,
			ExecutiveTaskExecutionDTO executiveTaskExecutionDTO, List<InventoryVoucherHeaderDTO> inventoryVouchers,
			Long companyId) {
		double salesAmount = 0;
		double salesVolume = 0;
		boolean addToDashboard = false;
		// get target type
		List<TargetType> targetTypes = salesTargetReportSettingSalesTargetBlockRepository
				.findTargetTypeBySalesTargetBlockPidAndCompanyId(dashboardItem.getSalesTargetBlockPid(), companyId);
		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
				.findBySalesTargetGroupPidAndUserPidAndAccountWiseTargetFalse(dashboardItem.getSalesTargetGroupPid(),
						executiveTaskExecutionDTO.getUserPid());
		if (inventoryVouchers != null && !inventoryVouchers.isEmpty() && !targetTypes.isEmpty()
				&& !salesTargetGroupUserTargets.isEmpty()) {
			// find documents and products configured in dashboardItem
			List<Document> documents = salesTargetGroupDocumentRepository
					.findDocumentsBySalesTargetGroupPid(dashboardItem.getSalesTargetGroupPid());
			List<ProductProfile> productProfiles = salesTargetGroupProductRepository
					.findProductsBySalesTargetGroupPid(dashboardItem.getSalesTargetGroupPid());

			if (!documents.isEmpty() && !productProfiles.isEmpty()) {
				for (InventoryVoucherHeaderDTO inventoryVoucher : inventoryVouchers) {
					double productVolume = 0;
					addToDashboard = true;
					Optional<Document> optDocument = documents.stream()
							.filter(d -> d.getPid().equals(inventoryVoucher.getDocumentPid())).findAny();
					for (InventoryVoucherDetailDTO ivDetailDTO : inventoryVoucher.getInventoryVoucherDetails()) {
						Optional<ProductProfile> optProduct = productProfiles.stream()
								.filter(p -> p.getPid().equals(ivDetailDTO.getProductPid())).findAny();
						if (optDocument.isPresent() && optProduct.isPresent()) {
							productVolume += (optProduct.get().getUnitQty() == null ? 1 : optProduct.get().getUnitQty())
									* ivDetailDTO.getQuantity();
						}
					}
					if (inventoryVoucher.getIsNew()) {
						salesVolume += productVolume;
						salesAmount += inventoryVoucher.getDocumentTotal();
					}
				}
			}

			if (addToDashboard) {
				DashboardSummaryDTO dashboardSummaryDTO = new DashboardSummaryDTO();
				dashboardSummaryDTO.setDashboardItemPid(dashboardItem.getPid());
				dashboardSummaryDTO.setLabel(dashboardItem.getName());
				dashboardSummaryDTO.setDashboardItemType(dashboardItem.getDashboardItemType());
				dashboardSummaryDTO.setTaskPlanType(dashboardItem.getTaskPlanType());
				dashboardSummaryDTO.setTargetType(targetTypes.get(0));
				dashboardSummaryDTO.setTargetAchievedVolume(salesVolume);
				if (!salesTargetGroupUserTargets.isEmpty()) {
					dashboardSummaryDTO
							.setTargetAverageVolume((salesVolume / salesTargetGroupUserTargets.get(0).getVolume()));
				}
				dashboardSummaryDTO.setTargetAchievedAmount(salesAmount);
				return dashboardSummaryDTO;
			}
		}
		return null;
	}

	/**
	 * Create new Task And Assign to Approver Users
	 * 
	 * @param executiveTaskExecution
	 * @param document
	 * @param refTransactionPid
	 * @param refTransDocumentNumber
	 * @param datePickerFormElements
	 * @throws TaskSubmissionPostSaveException
	 */
	private void createAndAssignTask(ExecutiveTaskExecution executiveTaskExecution, Document document,
			String refTransactionPid, String refTransDocumentNumber, List<FilledFormDetail> datePickerFormElements,
			List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs,
			List<AccountingVoucherDetailDTO> accountingVoucherDetailDTOs,
			List<FilledFormDetailDTO> filledFormDetailDTOs, EmployeeProfile employee)
			throws TaskSubmissionPostSaveException {
		log.info("create and assign task..........................");
		List<TaskSetting> taskSettingList = taskSettingRepository.findByActivityPidAndDocumentPidAndActivityEvent(
				executiveTaskExecution.getActivity().getPid(), document.getPid(), ActivityEvent.ONCREATE);
		for (TaskSetting taskSetting : taskSettingList) {
			processTaskScript(taskSetting, executiveTaskExecution, document, refTransactionPid, refTransDocumentNumber,
					datePickerFormElements, inventoryVoucherDetailDTOs, accountingVoucherDetailDTOs,
					filledFormDetailDTOs);
			// customer journey stage script
			processStageScript(taskSetting, executiveTaskExecution, document, refTransactionPid, filledFormDetailDTOs,
					employee);
		}
		log.info("create and assign task.........................end");
	}

	@SuppressWarnings("unchecked")
	private void processTaskScript(TaskSetting taskSetting, ExecutiveTaskExecution executiveTaskExecution,
			Document document, String refTransactionPid, String refTransDocumentNumber,
			List<FilledFormDetail> datePickerFormElements, List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs,
			List<AccountingVoucherDetailDTO> accountingVoucherDetailDTOs,
			List<FilledFormDetailDTO> filledFormDetailDTOs) throws TaskSubmissionPostSaveException {
		TaskUserSetting taskUserSetting = taskUserSettingRepository
				.findByExecutorPidAndTaskSettingPid(executiveTaskExecution.getUser().getPid(), taskSetting.getPid());
		if (taskUserSetting != null) {
			log.info("taskUserSetting ! = null");
			if (taskSetting.getRequired()) {
				log.info("taskUserSetting required true");
				saveTaskAndUserTask(taskSetting, taskUserSetting, executiveTaskExecution, document, refTransactionPid,
						refTransDocumentNumber, datePickerFormElements);
			} else {
				// run script
				ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
				try {
					if (taskSetting.getScript() != null && !taskSetting.getScript().isEmpty()) {
						engine.eval(taskSetting.getScript());
						Invocable invocable = (Invocable) engine;
						List<String> result = new ArrayList<>();
						if (document.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
							result = (ArrayList<String>) invocable.invokeFunction("inventoryTaskRequired",
									inventoryVoucherDetailDTOs);
						} else if (document.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
							result = (ArrayList<String>) invocable.invokeFunction("accountingTaskRequired",
									accountingVoucherDetailDTOs);
						} else if (document.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)
								&& taskSetting.getScript().indexOf("dynamicDocumentTaskRequired") != -1) {
							log.info("result in dynamic document else if");
							result = (ArrayList<String>) invocable.invokeFunction("dynamicDocumentTaskRequired",
									filledFormDetailDTOs);
						}
						log.info("-result size--" + result.size());
						if (!result.isEmpty()) {
							log.info("result is not empty");
							saveTaskAndUserTask(taskSetting, taskUserSetting, executiveTaskExecution, document,
									refTransactionPid, refTransDocumentNumber, datePickerFormElements);
						}
					}
				} catch (Exception e) {
					log.debug("Exception while processing createAndAssignTask method {}", e);
					throw new TaskSubmissionPostSaveException("Exception while processing createAndAssignTask method. "
							+ "Company : " + executiveTaskExecution.getCompany().getLegalName() + " User:"
							+ executiveTaskExecution.getUser().getLogin() + " Activity:"
							+ executiveTaskExecution.getActivity().getName() + " Account:"
							+ executiveTaskExecution.getAccountProfile().getName());
				}
			}

		}
	}

	@SuppressWarnings("unchecked")
	private void processStageScript(TaskSetting taskSetting, ExecutiveTaskExecution executiveTaskExecution,
			Document document, String refTransactionPid, List<FilledFormDetailDTO> filledFormDetailDTOs,
			EmployeeProfile employee) throws TaskSubmissionPostSaveException {
		long companyId = document.getCompany().getId();
		List<FormElement> formElementsLoadFromMobile = formElementRepository
				.findAllByCompanyIdAndLoadFromMobile(companyId);
		if (formElementsLoadFromMobile != null && formElementsLoadFromMobile.size() != 0) {
			for (FilledFormDetailDTO dto : filledFormDetailDTOs) {
				for (FormElement fe : formElementsLoadFromMobile) {
					if (dto.getFormElementPid().equals(fe.getPid())) {
						if (fe.getFormLoadMobileData() == LoadMobileData.PRODUCT_GROUP) {
							Optional<ProductGroup> opProductGroup = productGroupRepository
									.findByCompanyIdAndNameIgnoreCase(companyId, dto.getValue());
							if (opProductGroup.isPresent()) {
								dto.setValue(opProductGroup.get().getPid());
							}
						} else if (fe.getFormLoadMobileData() == LoadMobileData.TERRITORY) {
							Optional<Location> opLocation = locationRepository
									.findByCompanyIdAndNameIgnoreCase(companyId, dto.getValue());
							if (opLocation.isPresent()) {
								dto.setValue(opLocation.get().getPid());
							}

						}
					}
				}
			}
		}
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		log.info("Js code Workging....");
		try {
			if (taskSetting.getScript() != null && !taskSetting.getScript().isEmpty()) {
				engine.eval(taskSetting.getScript());
				Invocable invocable = (Invocable) engine;
				HashMap<String, String> customerJourneyMap = new HashMap<>();
				if (document.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
					if (taskSetting.getScript().indexOf("customerJourneyStage") != -1 ? Boolean.TRUE : Boolean.FALSE) {

						customerJourneyMap = (HashMap<String, String>) invocable.invokeFunction("customerJourneyStage",
								executiveTaskExecution.getActivity().getName(), document.getName(), "",
								filledFormDetailDTOs);
						log.info("Activity Name:" + executiveTaskExecution.getActivity().getName());
						log.info("Document Name:" + document.getName());
						filledFormDetailDTOs.forEach(f -> log.info(f.toString()));

					}
				} else if (document.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
					log.info("Accounting voucher stage changing");
					Optional<CompanyConfiguration> optStageChangeAccountingVoucher = companyConfigurationRepository
							.findByCompanyIdAndName(companyId, CompanyConfig.STAGE_CHANGES_FOR_ACCOUNTING_VOUCHER);
					// company configuration for stage change of accounting vouchers
					if (optStageChangeAccountingVoucher.isPresent()) {

						if (Boolean.valueOf(optStageChangeAccountingVoucher.get().getValue())) {
							if (taskSetting.getScript().indexOf("customerJourneyStage") != -1 ? Boolean.TRUE
									: Boolean.FALSE) {

								customerJourneyMap = (HashMap<String, String>) invocable.invokeFunction(
										"customerJourneyStage", executiveTaskExecution.getActivity().getName(),
										document.getName(), "", filledFormDetailDTOs);
								log.info("Accounting voucher document stage change");
								log.info("Activity Name:" + executiveTaskExecution.getActivity().getName());
								log.info("Document Name:" + document.getName());
								// filledFormDetailDTOs.forEach(f ->log.info(f.toString()));

							}
						}
					}
				}
				if (!customerJourneyMap.isEmpty()) {
					// save to header and details
					log.info("save to header and details:" + customerJourneyMap.get("Stage"));
					saveStageDetails(executiveTaskExecution, document, refTransactionPid, customerJourneyMap, employee);
				}
			}
		} catch (Exception e) {
			log.debug("Exception while updating customer journey script {}", e);
			throw new TaskSubmissionPostSaveException("Exception while updating customer journey script. "
					+ "Company : " + executiveTaskExecution.getCompany().getLegalName() + " User:"
					+ executiveTaskExecution.getUser().getLogin() + " Activity:"
					+ executiveTaskExecution.getActivity().getName() + " Account:"
					+ executiveTaskExecution.getAccountProfile().getName());
		}
	}

	private void saveStageDetails(ExecutiveTaskExecution executiveTaskExecution, Document document,
			String dynamicDocPid, HashMap<String, String> customerJourneyMap, EmployeeProfile employeeProfile) {
		Company company = document.getCompany();
		StageHeader stageHeader = new StageHeader();
		List<Stage> stages = stageRepository.findByCompanyIdAndName(company.getId(), customerJourneyMap.get("Stage"));
		if (stages.isEmpty()) {
			return;
		}
		stageHeader.setAccountProfile(executiveTaskExecution.getAccountProfile());
		stageHeader.setStage(stages.get(0));
		stageHeader.setEmployeeProfile(employeeProfile);
		stageHeader.setRemarks(customerJourneyMap.get("Remarks"));
		if (customerJourneyMap.get("Revenue") != null && !customerJourneyMap.get("Revenue").isEmpty()) {
			stageHeader.setValue(new BigDecimal(customerJourneyMap.get("Revenue")));
		}
		stageHeader.setCreatedDate(LocalDateTime.now());
		stageHeader.setCreatedBy(executiveTaskExecution.getUser());
		stageHeader.setCompany(company);

		StageHeader savedHeader = stageHeaderRepository.save(stageHeader);

		StageDetail stageDetail = new StageDetail();
		stageDetail.setExecutiveTaskExecution(executiveTaskExecution);
		stageDetail.setDynamicDocumentHeaderPid(dynamicDocPid);
		stageDetail.setActivity(executiveTaskExecution.getActivity());
		stageDetail.setCompany(company);
		stageDetail.setDocument(document);

		savedHeader.setStageDetails(stageDetail);

		// update account profile stage field
		AccountProfile accountProfile = stageHeader.getAccountProfile();
		accountProfile.setLeadToCashStage(stageHeader.getStage().getName());
		accountProfileRepository.save(accountProfile);
	}

	private void saveTaskAndUserTask(TaskSetting taskSetting, TaskUserSetting taskUserSetting,
			ExecutiveTaskExecution executiveTaskExecution, Document document, String refTransactionPid,
			String refTransDocumentNumber, List<FilledFormDetail> datePickerFormElements) {
		// create task
		Task task = new Task();
		task.setAccountProfile(executiveTaskExecution.getAccountProfile());
		task.setAccountType(executiveTaskExecution.getAccountType());
		task.setActivity(taskSetting.getTaskActivity());
		task.setCompany(executiveTaskExecution.getCompany());
		task.setPid(TaskService.PID_PREFIX + RandomUtil.generatePid());
		task.setRemarks(executiveTaskExecution.getRemarks());
		log.info("Saving task -- " + task.toString());
		task = taskRepository.save(task);

		// save task reference document
		TaskReferenceDocument taskReferenceDocument = new TaskReferenceDocument();
		taskReferenceDocument.setPid(TaskReferenceDocumentService.PID_PREFIX + RandomUtil.generatePid());
		taskReferenceDocument.setCompany(executiveTaskExecution.getCompany());
		taskReferenceDocument.setExecutiveTaskExecution(executiveTaskExecution);
		taskReferenceDocument.setRefDocument(document);
		taskReferenceDocument.setRefTransactionPid(refTransactionPid);
		taskReferenceDocument.setRefTransDocumentNumber(refTransDocumentNumber);
		taskReferenceDocument.setTask(task);
		log.info("Saving taskReference - " + taskReferenceDocument.toString());
		taskReferenceDocumentRepository.save(taskReferenceDocument);

		if (!taskUserSetting.getApprovers().isEmpty()) {
			// find start date
			LocalDate startDate = null;
			boolean dateExist = false;
			if (taskSetting.getFormElementPid() != null && datePickerFormElements != null
					&& !datePickerFormElements.isEmpty()) {
				for (FilledFormDetail filledFormDetail : datePickerFormElements) {
					if (filledFormDetail.getFormElement().getPid().equals(taskSetting.getFormElementPid())) {
						if (filledFormDetail.getValue() != null && !filledFormDetail.getValue().isEmpty()) {
							startDate = LocalDate.parse(filledFormDetail.getValue());
							dateExist = true;
						}
					}
				}
			}
			if (!dateExist) {
				startDate = LocalDate.now();
			}
			// assign task
			List<UserTaskAssignment> userTaskAssignments = new ArrayList<>();
			// assign task plan
			List<ExecutiveTaskPlan> executiveTaskPlanList = new ArrayList<>();
			// users to send notification
			List<String> users = new ArrayList<>();
			for (User executiveUser : taskUserSetting.getApprovers()) {
				if (checkAccountIsAccessible(executiveUser.getPid(),
						executiveTaskExecution.getAccountProfile().getPid())) {
					// user task assignment
					UserTaskAssignment userTaskAssignment = new UserTaskAssignment();
					userTaskAssignment.setCompany(executiveTaskExecution.getCompany());
					userTaskAssignment.setExecutiveUser(executiveUser);
					userTaskAssignment.setPid(UserTaskAssignmentService.PID_PREFIX + RandomUtil.generatePid());
					userTaskAssignment.setPriorityStatus(PriorityStatus.MEDIUM);
					userTaskAssignment.setRemarks(executiveTaskExecution.getRemarks());
					userTaskAssignment.setStartDate(startDate);
					userTaskAssignment.setTask(task);
					userTaskAssignment.setUser(executiveTaskExecution.getUser());
					userTaskAssignments.add(userTaskAssignment);

					// create new executive plan
					if (taskSetting.getCreatePlan()) {
						ExecutiveTaskPlan newExecutiveTaskPlan = new ExecutiveTaskPlan();
						newExecutiveTaskPlan.setPid(ExecutiveTaskPlanService.PID_PREFIX + RandomUtil.generatePid());
						newExecutiveTaskPlan.setAccountProfile(executiveTaskExecution.getAccountProfile());
						newExecutiveTaskPlan.setAccountType(executiveTaskExecution.getAccountType());
						newExecutiveTaskPlan.setActivity(taskSetting.getTaskActivity());
						newExecutiveTaskPlan.setCreatedDate(LocalDateTime.now());
						newExecutiveTaskPlan.setCreatedBy(executiveTaskExecution.getUser().getLogin());
						newExecutiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.CREATED);
						LocalTime time = LocalTime.now();
						newExecutiveTaskPlan.setPlannedDate(LocalDateTime.of(startDate, time));
						newExecutiveTaskPlan.setRemarks("");
						newExecutiveTaskPlan.setUserRemarks("");
						newExecutiveTaskPlan.setUser(executiveUser);
						newExecutiveTaskPlan.setCompany(executiveTaskExecution.getCompany());
						newExecutiveTaskPlan.setTaskCreatedType(TaskCreatedType.TASK_SERVER_AUTO);
						// set task
						newExecutiveTaskPlan.setTask(task);
						executiveTaskPlanList.add(newExecutiveTaskPlan);
					}

					// users for send notification
					users.add(executiveUser.getPid());
				}
			}
			userTaskAssignmentRepository.save(userTaskAssignments);
			executiveTaskPlanRepository.save(executiveTaskPlanList);

			// send notifications
			userDevices = userDeviceRepository
					.findAllByCompanyAndUserPidINAndActivatedTrue(executiveTaskExecution.getCompany().getId(), users);
			if (CollectionUtils.isEmpty(userDevices))
				return;

			String[] usersFcmKeys = userDevices.stream().map(ud -> ud.getFcmKey()).toArray(String[]::new);
			FirebaseRequest firebaseRequest = new FirebaseRequest();
			firebaseRequest.setRegistrationIds(usersFcmKeys);

			String activityName = taskReferenceDocument.getTask().getActivity().getName();
			String accountName = taskReferenceDocument.getTask().getAccountProfile().getName();
			String taskCreatedUser = taskUserSetting.getExecutor().getFirstName();
			FirebaseData data = new FirebaseData();
			data.setTitle("You have a new task - " + activityName);
			data.setMessage("New task - " + activityName + ", Account - " + accountName + " Created User - "
					+ taskCreatedUser + ". Date - " + startDate);
			if (taskSetting.getCreatePlan()) {
				if (LocalDate.now().isEqual(startDate)) {
					data.setTodaysPlan(true);
				}
				data.setMessageType(NotificationMessageType.PLAN);
			} else {
				data.setMessageType(NotificationMessageType.TASK);
			}
			data.setPidUrl(taskReferenceDocument.getPid());
			data.setNotificationPid("");
			data.setSentDate(LocalDateTime.now().toString());
			firebaseRequest.setData(data);

			setFirebaseRequest(firebaseRequest);
			setUserDevices(userDevices);
			// task-usertask send notification asynchronous
			firebaseService.sendNotificationToUsers(firebaseRequest, userDevices,
					executiveTaskExecution.getUser().getLogin());
		}
	}

	@Transactional
	private void saveTaskAndUserTaskAutomatically(ExecutiveTaskExecution executiveTaskExecution,
			DynamicDocumentHeader dynamicDocumentHeader, List<FilledFormDetail> datePickerFormElements) {
		LocalDate startDate = null;

		for (FilledFormDetail filledFormDetail : datePickerFormElements) {
			log.info("Filled From Value ~ : " + filledFormDetail.getValue());
			if (filledFormDetail.getValue() == null || "".equals(filledFormDetail.getValue())) {
				log.info("Task Not Created ~ Date null");
				return;
			}
			startDate = LocalDate.parse(filledFormDetail.getValue());
			if (startDate.isBefore(LocalDate.now())) {
				log.info("start Date b4 :" + startDate.toString());
				log.info("Task Not Created ~ Before");
				return;
			}
			if (startDate.isEqual(LocalDate.now())) {
				log.info("start Date equal :" + startDate.toString());
				log.info("Task Not Created ~ Equal");
				return;
			}
		}
		List<Task> tasksByActivityAndAccount = taskRepository.findTaskByCompanyIdActivityPidAndAccountPid(
				executiveTaskExecution.getCompany().getId(), executiveTaskExecution.getActivity().getPid(),
				executiveTaskExecution.getAccountProfile().getPid());
		Task task = new Task();
		if (tasksByActivityAndAccount.isEmpty()) {
			task.setAccountProfile(executiveTaskExecution.getAccountProfile());
			task.setAccountType(executiveTaskExecution.getAccountType());
			task.setActivity(executiveTaskExecution.getActivity());
			task.setCompany(executiveTaskExecution.getCompany());
			task.setPid(TaskService.PID_PREFIX + RandomUtil.generatePid());
			task.setRemarks(executiveTaskExecution.getRemarks());
			task = taskRepository.save(task);
		} else {
			task = tasksByActivityAndAccount.get(0);
		}

		List<ExecutiveTaskPlan> executiveTaskPlanList = new ArrayList<>();
		ExecutiveTaskPlan newExecutiveTaskPlan = new ExecutiveTaskPlan();
		newExecutiveTaskPlan.setPid(ExecutiveTaskPlanService.PID_PREFIX + RandomUtil.generatePid());
		newExecutiveTaskPlan.setAccountProfile(executiveTaskExecution.getAccountProfile());
		newExecutiveTaskPlan.setAccountType(executiveTaskExecution.getAccountType());
		newExecutiveTaskPlan.setActivity(executiveTaskExecution.getActivity());
		newExecutiveTaskPlan.setCreatedDate(LocalDateTime.now());
		newExecutiveTaskPlan.setCreatedBy(executiveTaskExecution.getUser().getLogin());
		newExecutiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.CREATED);
		LocalTime time = LocalTime.now();
		newExecutiveTaskPlan.setPlannedDate(LocalDateTime.of(startDate, time));
		newExecutiveTaskPlan.setRemarks("");
		newExecutiveTaskPlan.setUserRemarks("");
		newExecutiveTaskPlan.setUser(executiveTaskExecution.getUser());
		newExecutiveTaskPlan.setCompany(executiveTaskExecution.getCompany());
		newExecutiveTaskPlan.setTaskCreatedType(TaskCreatedType.TASK_SERVER_AUTO);
		// set task
		newExecutiveTaskPlan.setTask(task);
		executiveTaskPlanList.add(newExecutiveTaskPlan);
		executiveTaskPlanRepository.save(executiveTaskPlanList);
	}

	private boolean checkAccountIsAccessible(String userPid, String accountPid) {
		// current user employee locations
		List<Location> locations = employeeProfileLocationRepository.findLocationsByUserPid(userPid);
		if (!locations.isEmpty()) {
			// get accounts in employee locations
			List<AccountProfile> accountProfiles = locationAccountProfileRepository
					.findAccountProfilesByUserLocations(locations);
			for (AccountProfile accountProfile : accountProfiles) {
				if (accountProfile.getPid().equals(accountPid)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Create new Document Approval And Assign to Users
	 * 
	 * @param document
	 * @throws TaskSubmissionPostSaveException
	 * @throws ScriptException
	 */
	@SuppressWarnings("unchecked")
	private void createDocumentApproval(Document document, List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs,
			List<AccountingVoucherDetailDTO> accountingVoucherDetailDTOs,
			List<FilledFormDetailDTO> filledFormDetailDTOs, String docTransactionNumber,
			ExecutiveTaskExecution executiveTaskExecution) throws TaskSubmissionPostSaveException {
		List<DocumentApprovalLevel> documentApprovalLevels = documentApprovalLevelRepository
				.findAllByDocumentPid(document.getPid());
		if (!documentApprovalLevels.isEmpty()) {
			for (DocumentApprovalLevel documentApprovalLevel : documentApprovalLevels) {
				if (documentApprovalLevel.getRequired()) {
					saveDocumentApproval(documentApprovalLevel, null, docTransactionNumber, executiveTaskExecution);
				} else {
					// run script
					ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
					try {
						if (documentApprovalLevel.getScript() != null && !documentApprovalLevel.getScript().isEmpty()) {
							engine.eval(documentApprovalLevel.getScript());
							Invocable invocable = (Invocable) engine;
							List<String> result = new ArrayList<>();
							if (document.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
								result = (ArrayList<String>) invocable.invokeFunction("inventoryApprovalRequired",
										inventoryVoucherDetailDTOs);
							} else if (document.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
								result = (ArrayList<String>) invocable.invokeFunction("accountingApprovalRequired",
										accountingVoucherDetailDTOs);
							} else if (document.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
								result = (ArrayList<String>) invocable.invokeFunction("dynamicDocumentApprovalRequired",
										filledFormDetailDTOs);
							}
							if (!result.isEmpty()) {
								saveDocumentApproval(documentApprovalLevel, result, docTransactionNumber,
										executiveTaskExecution);
							}
						}
					} catch (Exception e) {
						log.debug("Exception while processing createDocumentApproval method {}", e);
						throw new TaskSubmissionPostSaveException(
								"Exception while processing createDocumentApproval method. " + "Company : "
										+ executiveTaskExecution.getCompany().getLegalName() + " User:"
										+ executiveTaskExecution.getUser().getLogin() + " Activity:"
										+ executiveTaskExecution.getActivity().getName() + " Document:"
										+ document.getName());
					}
				}
			}
		}
	}

	/**
	 * @param documentApprovalLevel
	 */
	private void saveDocumentApproval(DocumentApprovalLevel documentApprovalLevel, List<String> remarks,
			String docTransactionNumber, ExecutiveTaskExecution executiveTaskExecution) {
		String docName = documentApprovalLevel.getDocument().getName();
		String accountName = executiveTaskExecution.getAccountProfile().getName();
		String createdUser = executiveTaskExecution.getUser().getFirstName();

		DocumentApproval documentApproval = new DocumentApproval();
		String message = docName + " : " + docTransactionNumber + ", Account : " + accountName + " Created User - "
				+ createdUser + ". Date : " + documentApproval.getCreatedDate() + "\nItems:\n";
		if (remarks != null) {
			message = message.concat(String.join("\n", remarks));
		}
		documentApproval.setPid(DocumentApprovalService.PID_PREFIX + RandomUtil.generatePid());
		documentApproval.setDocumentApprovalLevel(documentApprovalLevel);
		documentApproval.setDocument(documentApprovalLevel.getDocument());
		documentApproval.setCompleted(false);
		documentApproval.setApprovalStatus(ApprovalStatus.PENDING);
		documentApproval.setCompany(documentApprovalLevel.getCompany());
		documentApproval.setRemarks(message);
		documentApproval = documentApprovalRepository.save(documentApproval);

		List<String> userPids = new ArrayList<>();
		for (User user : documentApproval.getDocumentApprovalLevel().getUsers()) {
			userPids.add(user.getPid());
		}
		// send notifications
		userDevices = userDeviceRepository.findAllByUserPidINAndActivatedTrue(userPids);
		if (CollectionUtils.isEmpty(userDevices))
			return;

		String[] usersFcmKeys = userDevices.stream().map(ud -> ud.getFcmKey()).toArray(String[]::new);
		FirebaseRequest firebaseRequest = new FirebaseRequest();
		firebaseRequest.setRegistrationIds(usersFcmKeys);

		FirebaseData data = new FirebaseData();
		data.setTitle("You have an approval task - " + documentApproval.getDocumentApprovalLevel().getName());
		data.setMessage(message);
		data.setMessageType(NotificationMessageType.APPROVAL);
		data.setPidUrl(documentApproval.getPid());
		data.setNotificationPid("");
		data.setSentDate(LocalDateTime.now().toString());
		firebaseRequest.setData(data);

		setFirebaseRequest(firebaseRequest);
		setUserDevices(userDevices);
		// document-approval send notification asynchronous
		firebaseService.sendNotificationToUsers(firebaseRequest, userDevices,
				executiveTaskExecution.getUser().getLogin());
	}

	private void sendTaskNotificationToUsers(ExecutiveTaskExecution executiveTaskExecution, Document document,
			String refTransactionPid, String refTransDocumentNumber, List<FilledFormDetail> datePickerFormElements,
			List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs,
			List<AccountingVoucherDetailDTO> accountingVoucherDetailDTOs,
			List<FilledFormDetailDTO> filledFormDetailDTOs) {
		log.info("Send task notification to users...........................");
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		Optional<TaskNotificationSetting> optionalTNS = taskNotificationSettingRepository
				.findByActivityPidAndDocumentPidAndActivityEvent(executiveTaskExecution.getActivity().getPid(),
						document.getPid(), ActivityEvent.ONCREATE);
		// Send notification based on document, if check territory enabled, else
		// send based on current user
		if (optionalTNS.isPresent()) {
			List<TaskUserNotificationSetting> tUNSettings = taskUserNotificationSettingRepository
					.findByTaskNotificationSettingPid(optionalTNS.get().getPid());
			if (!tUNSettings.isEmpty() && !tUNSettings.get(0).getApprovers().isEmpty()) {
				// now expect only one exist for a TaskNotificationSetting
				TaskUserNotificationSetting taskUserNotificationSetting = tUNSettings.get(0);
				// users to send notification
				List<String> users = new ArrayList<>();
				// check territory enabled
				if (taskUserNotificationSetting.getEnableTerritory()) {
					for (User user : taskUserNotificationSetting.getApprovers()) {
						List<Location> locations = employeeProfileLocationRepository
								.findLocationsByUserPid(user.getPid());
						List<AccountProfile> accountProfiles = locationAccountProfileRepository
								.findAccountProfileByLocationIdIn(
										locations.stream().map(Location::getId).collect(Collectors.toList()));
						if (accountProfiles.stream().anyMatch(
								ac -> ac.getPid().equals(executiveTaskExecution.getAccountProfile().getPid()))) {
							users.add(user.getPid());
						}
					}
				} else {
					// users to send notification
					users = taskUserNotificationSetting.getApprovers().stream().map(User::getPid)
							.collect(Collectors.toList());
				}

				// send notifications
				userDevices = userDeviceRepository.findAllByCompanyAndUserPidINAndActivatedTrue(
						executiveTaskExecution.getCompany().getId(), users);
				if (CollectionUtils.isEmpty(userDevices))
					return;

				String[] usersFcmKeys = userDevices.stream().map(ud -> ud.getFcmKey()).toArray(String[]::new);
				FirebaseRequest firebaseRequest = new FirebaseRequest();
				firebaseRequest.setRegistrationIds(usersFcmKeys);

				String activityName = executiveTaskExecution.getActivity().getName();
				String accountName = executiveTaskExecution.getAccountProfile().getName();
				String taskCreatedUser = taskUserNotificationSetting.getExecutor().getFirstName();
				if (taskUserNotificationSetting.getEnableTerritory()) {
					taskCreatedUser = "";
				}

				String executionDate = formatter.format(executiveTaskExecution.getDate());
				FirebaseData data = new FirebaseData();
				data.setTitle("Notification");
				data.setMessage("User " + taskCreatedUser + " has executed " + activityName + " and created document "
						+ document.getName() + ", Ref No:" + refTransDocumentNumber + ", Date " + executionDate
						+ ", Ref Account: " + accountName);
				data.setMessageType(NotificationMessageType.INFO);
				data.setPidUrl("");
				data.setNotificationPid("");
				data.setSentDate(LocalDateTime.now().toString());
				firebaseRequest.setData(data);

				setFirebaseRequest(firebaseRequest);
				setUserDevices(userDevices);
				// task-usertask send notification asynchronous
				firebaseService.sendNotificationToUsers(firebaseRequest, userDevices,
						executiveTaskExecution.getUser().getLogin());
			}
		}
	}

	public FirebaseRequest getFirebaseRequest() {
		return this.firebaseRequest;
	}

	public List<UserDevice> getUserDevices() {
		return this.userDevices;
	}

	private void setFirebaseRequest(FirebaseRequest firebaseRequest) {
		this.firebaseRequest = firebaseRequest;
	}

	private void setUserDevices(List<UserDevice> userDevices) {
		this.userDevices = userDevices;
	}

	/**
	 * @param executiveTaskExecution
	 * @throws TaskSubmissionPostSaveException
	 */
	private void updateDistance(ExecutiveTaskExecution executiveTaskExecution) throws TaskSubmissionPostSaveException {
		log.info("save or update executive travalled disatance");
		executiveTaskExecution = executiveTaskExecutionRepository.findOne(executiveTaskExecution.getId());
		List<LocationType> locationTypes = new ArrayList<>();
		locationTypes.add(LocationType.TowerLocation);
		locationTypes.add(LocationType.GpsLocation);
		List<ExecutiveTaskExecution> lastExecutiveTaskExecution = new ArrayList<>();
		try {
			LocalDateTime date = executiveTaskExecution.getDate();
			lastExecutiveTaskExecution = executiveTaskExecutionRepository
					.findTop2ByUserPidAndDateBetweenAndLocationTypeInOrderByDateDesc(
							executiveTaskExecution.getUser().getPid(), date.toLocalDate().atTime(0, 0),
							date.toLocalDate().atTime(23, 59), locationTypes);
			if (lastExecutiveTaskExecution == null || lastExecutiveTaskExecution.isEmpty()) {
				return;
			}
			int size = lastExecutiveTaskExecution.size();
			if (size >= 2) {
				updateDistanceWithPreviousTaskExecution(executiveTaskExecution, lastExecutiveTaskExecution);
			} else if (size == 1) {
				updateDistanceInAttendance(executiveTaskExecution, lastExecutiveTaskExecution);
			}
		} catch (Exception e) {
			log.debug("Exception while processing updateDistance method {}", e);
			throw new TaskSubmissionPostSaveException("Exception while processing updateDistance method. "
					+ "Company : " + executiveTaskExecution.getCompany().getLegalName() + " User:"
					+ executiveTaskExecution.getUser().getLogin() + " Activity:"
					+ executiveTaskExecution.getActivity().getName() + " Exception : " + e);
		}

	}

	private void updateDistanceWithPreviousTaskExecution(ExecutiveTaskExecution executiveTaskExecution,
			List<ExecutiveTaskExecution> lastExecutiveTaskExecution) throws TaskSubmissionPostSaveException {
		// find distance between last saved location and current location
		double kilometers = 0;
		double metres = 0;
		Long companyId = executiveTaskExecution.getCompany().getId();
		if (lastExecutiveTaskExecution.get(1).getLatitude() != null
				&& lastExecutiveTaskExecution.get(1).getLatitude().doubleValue() != 0
				&& executiveTaskExecution.getLongitude() != null
				&& executiveTaskExecution.getLongitude().doubleValue() != 0) {
			String origin = lastExecutiveTaskExecution.get(1).getLatitude() + ","
					+ lastExecutiveTaskExecution.get(1).getLongitude();
			String destination = executiveTaskExecution.getLatitude() + "," + executiveTaskExecution.getLongitude();
			if (!origin.equals(destination)) {
				MapDistanceDTO distance = saveKilometreDifference(executiveTaskExecution, origin, destination,
						companyId, lastExecutiveTaskExecution, null);
				if (distance != null) {
					kilometers = distance.getValue() * 0.001;
					metres = distance.getValue();
				}
			}
		}
		// save or update executive traveled distance
		Optional<UserDistanceDTO> userDistanceDTO = userDistanceService.findByCompanyIdAndUserIdAndDate(companyId,
				executiveTaskExecution.getUser().getPid(), executiveTaskExecution.getDate().toLocalDate());
		if (userDistanceDTO.isPresent()) {
			// update
			if (metres > 0) {
				UserDistanceDTO udDTO = userDistanceDTO.get();
				udDTO.setKilometre(userDistanceDTO.get().getKilometre() + kilometers);
				udDTO.setEndLocation(executiveTaskExecution.getLocation());
				userDistanceService.update(udDTO, companyId);
			}
		} else {
			// save
			UserDistanceDTO udDTO = new UserDistanceDTO();
			udDTO.setKilometre(kilometers);
			udDTO.setUserPid(executiveTaskExecution.getUser().getPid());
			udDTO.setUserName(executiveTaskExecution.getUser().getFirstName());
			udDTO.setDate(executiveTaskExecution.getDate().toLocalDate());
			udDTO.setStartLocation(lastExecutiveTaskExecution.get(1).getLocation());
			udDTO.setEndLocation(executiveTaskExecution.getLocation());
			userDistanceService.save(udDTO, companyId);
		}
		log.info("distance travalled updated...");
	}

	private void updateDistanceInAttendance(ExecutiveTaskExecution executiveTaskExecution,
			List<ExecutiveTaskExecution> lastExecutiveTaskExecution) throws TaskSubmissionPostSaveException {
		if (lastExecutiveTaskExecution.get(0).getLatitude() != null
				&& lastExecutiveTaskExecution.get(0).getLatitude().doubleValue() != 0
				&& executiveTaskExecution.getLongitude() != null
				&& executiveTaskExecution.getLongitude().doubleValue() != 0) {
			Optional<Attendance> attendance = attendanceRepository.findTop1ByCompanyPidAndUserPidOrderByCreatedDateDesc(
					executiveTaskExecution.getCompany().getPid(), executiveTaskExecution.getUser().getPid());
			String origin = executiveTaskExecution.getLatitude() + "," + executiveTaskExecution.getLongitude();
			if (attendance.isPresent() && attendance.get().getCreatedDate().toLocalDate().isEqual(LocalDate.now())) {
				if (attendance.get().getLatitude() != null && attendance.get().getLongitude() != null
						&& !attendance.get().getLatitude().equals(BigDecimal.ZERO)
						&& !attendance.get().getLongitude().equals(BigDecimal.ZERO)) {
					origin = attendance.get().getLatitude() + " , " + attendance.get().getLongitude();
					log.info("Attendance gps location tracked" + origin);

				} else if (attendance.get().getTowerLatitude() != null && attendance.get().getTowerLongitude() != null
						&& !attendance.get().getTowerLatitude().equals(BigDecimal.ZERO)
						&& !attendance.get().getTowerLongitude().equals(BigDecimal.ZERO)) {
					origin = attendance.get().getTowerLatitude() + " , " + attendance.get().getTowerLongitude();
					log.info("Attendance tower location tracked" + origin);
				}
			}
			if (origin != null && !origin.isEmpty()) {
				String destination = executiveTaskExecution.getLatitude() + "," + executiveTaskExecution.getLongitude();
				saveKilometreDifference(executiveTaskExecution, origin, destination,
						executiveTaskExecution.getCompany().getId(), lastExecutiveTaskExecution, attendance);
			}
		}
	}

	private MapDistanceDTO saveKilometreDifference(ExecutiveTaskExecution executiveTaskExecution, String origin,
			String destination, Long companyId, List<ExecutiveTaskExecution> lastExecutiveTaskExecution,
			Optional<Attendance> attendance) throws TaskSubmissionPostSaveException {
		MapDistanceApiDTO distanceApiJson = null;
		MapDistanceDTO distance = null;
		try {
			distanceApiJson = geoLocationService.findDistance(origin, destination);
			if (distanceApiJson != null && !distanceApiJson.getRows().isEmpty()) {
				distance = distanceApiJson.getRows().get(0).getElements().get(0).getDistance();
				if (distance != null) {
					log.debug("Distance != null");
					KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
					kiloCalDTO.setKilometre(distance.getValue() * 0.001);
					kiloCalDTO.setMetres(distance.getValue());
					kiloCalDTO.setUserPid(executiveTaskExecution.getUser().getPid());
					kiloCalDTO.setUserName(executiveTaskExecution.getUser().getFirstName());
					kiloCalDTO.setDate(executiveTaskExecution.getDate().toLocalDate());
					if (lastExecutiveTaskExecution.size() == 2 && lastExecutiveTaskExecution.get(1) == null
							&& attendance.isPresent()) {
						kiloCalDTO.setStartLocation("Attendance");
					} else {
						log.debug("Start location not attendance");
						kiloCalDTO.setStartLocation(lastExecutiveTaskExecution.get(0).getLocation());
					}
					kiloCalDTO.setEndLocation(executiveTaskExecution.getLocation());
					kiloCalDTO.setTaskExecutionPid(executiveTaskExecution.getPid());
					log.debug("Saving Kilometre calculation...");
					kilometreCalculationService.save(kiloCalDTO, companyId);
				}
			}
			log.debug(" distance == null");
		} catch (Exception e) {
			log.debug("Exception while processing saveKilometreDifference method {}", e);
			throw new TaskSubmissionPostSaveException("Exception while processing saveKilometreDifference method. "
					+ "Company : " + executiveTaskExecution.getCompany().getLegalName() + " User:"
					+ executiveTaskExecution.getUser().getLogin() + " Disatance API JSON :" + distanceApiJson
					+ " Exception : " + e);
		}
		return distance;
	}

	/**
	 * used to update variance betwee account location to order taken location
	 * 
	 * @param executiveTaskExecution
	 */
	private void updateLocationVariance(ExecutiveTaskExecution executiveTaskExecution) {
		log.info("save or update executive location variance");

		ExecutiveTaskExecution execution = executiveTaskExecution;

		if (execution.getLatitude() != null && execution.getAccountProfile().getLatitude() != null
				&& execution.getLatitude().doubleValue() != 0
				&& execution.getAccountProfile().getLatitude().doubleValue() != 0) {
			String variance = "";
			double accLocLat = execution.getAccountProfile().getLatitude().doubleValue();
			double accLocLng = execution.getAccountProfile().getLongitude().doubleValue();

			double exeLocLat = execution.getLatitude().doubleValue();
			double exeLocLng = execution.getLongitude().doubleValue();

			String origin = accLocLat + "," + accLocLng;
			String destination = exeLocLat + "," + exeLocLng;

			if (!origin.equals(destination)) {
				double distance = geoLocationService.computeDistanceBetween(accLocLat, accLocLng, exeLocLat, exeLocLng);
				variance = distance + " KM";
			} else {
				variance = "0 KM";
			}
			execution.setLocationVariance(variance);
			// update
			executiveTaskExecutionRepository.save(execution);
		}
	}

	private void sendErrorEmail(String msg, Exception ex) {
		// send mail
		mailService.sendExceptionEmail("prashob.aitrich@gmail.com", msg, ExceptionUtils.getStackTrace(ex), false,
				false);
	}

}
