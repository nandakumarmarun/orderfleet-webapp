package com.orderfleet.webapp.service.impl;

import java.math.BigDecimal;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.catalina.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.AccountingVoucherHeaderHistory;
import com.orderfleet.webapp.domain.ActivityUserTarget;
import com.orderfleet.webapp.domain.Bank;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentApproval;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.DynamicDocumentHeaderHistory;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.IncomeExpenseHead;
import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.InventoryVoucherHeaderHistory;
import com.orderfleet.webapp.domain.OrderStatus;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.SalesLedger;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.AccountingVoucherDetailRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderHistoryRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.ActivityUserTargetRepository;
import com.orderfleet.webapp.repository.BankRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DocumentApprovalRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderHistoryRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.FilledFormDetailRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.repository.IncomeExpenseHeadRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderHistoryRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.OrderStatusRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.ReferenceDocumentRepository;
import com.orderfleet.webapp.repository.SalesLedgerRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountingVoucherHeaderService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.service.ExecutiveTaskSubmissionService;
import com.orderfleet.webapp.service.FilledFormService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.ExecutiveTaskSubmissionController;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.api.dto.TaskSubmissionResponse;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherAllocationDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

/**
 * Service Implementation for managing ExecutiveTaskSubmission.
 * 
 * @author Muhammed Riyas T
 * @since July 27, 2016
 */
@Service
@Transactional
public class ExecutiveTaskSubmissionServiceImpl implements ExecutiveTaskSubmissionService {

	private final Logger log = LoggerFactory.getLogger(ExecutiveTaskSubmissionController.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private BankRepository bankRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private FormRepository formRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private ReceivablePayableRepository receivablePayableRepository;

	@Inject
	private InventoryVoucherHeaderHistoryRepository inventoryVoucherHeaderHistoryRepository;

	@Inject
	private DynamicDocumentHeaderHistoryRepository documentHeaderHistoryRepository;

	@Inject
	private AccountingVoucherHeaderHistoryRepository accountingVoucherHeaderHistoryRepository;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private ReferenceDocumentRepository referenceDocumentRepository;

	@Inject
	private DocumentApprovalRepository documentApprovalRepository;

	@Inject
	private FormFormElementRepository formFormElementRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private IncomeExpenseHeadRepository incomeExpenseHeadRepository;

	@Inject
	private FilledFormDetailRepository filledFormDetailRepository;

	@Inject
	private ActivityUserTargetRepository activityUserTargetRepository;

	@Inject
	private OrderStatusRepository orderStatusRepository;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherDetailRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private SalesLedgerRepository salesLedgerRepository;

	@Override
	public ExecutiveTaskSubmissionTransactionWrapper executiveTaskSubmission(
			ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		log.info("============================ Transaction processing  start : " + user.getLogin()
				+ "============================");
		Company company = user.getCompany();
		EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUser(user);
		ExecutiveTaskExecutionDTO executionDTO = executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO();
		// save Executive Task Execution
		ExecutiveTaskExecution executiveTaskExecution = saveExecutiveTaskExecution(company, user, executionDTO);
		// save Inventory Vouchers
		List<InventoryVoucherHeader> inventoryVouchers = saveInventoryVouchers(company, user, employeeProfile,
				executiveTaskExecution, executiveTaskSubmissionDTO.getInventoryVouchers());
		// save Accounting Vouchers
		List<AccountingVoucherHeader> accountingVouchers = saveAccountingVouchers(company, user, employeeProfile,
				executiveTaskExecution, executiveTaskSubmissionDTO.getAccountingVouchers());
		// save Dynamic Forms
		List<DynamicDocumentHeader> dynamicDocuments = saveDynamicForms(company, user, employeeProfile,
				executiveTaskExecution, executiveTaskSubmissionDTO.getDynamicDocuments());
		// update executive task plan status
		updateExecutiveTaskPlanStatus(executiveTaskExecution);

		TaskSubmissionResponse submissionResponse = createResponseObject(executiveTaskExecution);
		log.info("============================ Transaction processing  completed : " + user.getLogin()
				+ "============================");
		return new ExecutiveTaskSubmissionTransactionWrapper(submissionResponse, executiveTaskExecution,
				inventoryVouchers, accountingVouchers, dynamicDocuments,
				executiveTaskSubmissionDTO.getUpdateDashboard());
	}

	/**
	 * saving executiveTaskSubmission from ThirdParty
	 *
	 * @author Sarath
	 * @param executiveTaskSubmissionDTO
	 */
	@Override
	public void saveTPExecutiveTaskSubmission(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO, User user) {
		Company company = user.getCompany();
		EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUser(user);
		// save Executive Task Execution
		ExecutiveTaskExecution executiveTaskExecution = saveExecutiveTaskExecution(company, user,
				executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO());
		// save Inventory Vouchers
		saveInventoryVouchers(company, user, employeeProfile, executiveTaskExecution,
				executiveTaskSubmissionDTO.getInventoryVouchers());

		// save Inventory Vouchers
		saveAccountingVouchers(company, user, employeeProfile, executiveTaskExecution,
				executiveTaskSubmissionDTO.getAccountingVouchers());
	}

	/**
	 * save Executive Task Execution
	 * 
	 * @param company
	 * @param user
	 * @param executiveTaskExecutionDTO
	 * @return
	 */
	private ExecutiveTaskExecution saveExecutiveTaskExecution(Company company, User user,
			ExecutiveTaskExecutionDTO executiveTaskExecutionDTO) {
		ExecutiveTaskExecution executiveTaskExecution = new ExecutiveTaskExecution();

		log.info("----Saving Executive task Execution Started----- " + user.getLogin());

		// set pid
		executiveTaskExecution.setPid(ExecutiveTaskExecutionService.PID_PREFIX + RandomUtil.generatePid());
		executiveTaskExecution.setClientTransactionKey(executiveTaskExecutionDTO.getClientTransactionKey());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
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
			String description1 = "get one by pid";
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
			logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1
					+ "," + description1);
		} else {
			executiveTaskExecution.setAccountType(executiveTaskExecution.getAccountProfile().getAccountType());
		}
		executiveTaskExecution
				.setActivity(activityRepository.findOneByPid(executiveTaskExecutionDTO.getActivityPid()).get());
		executiveTaskExecution.setPunchInDate(executiveTaskExecutionDTO.getPunchInDate());
		executiveTaskExecution.setDate(executiveTaskExecutionDTO.getDate());
		executiveTaskExecution.setSendDate(executiveTaskExecutionDTO.getSendDate());
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
		if (locationType.equals(LocationType.TowerLocation) || (executiveTaskExecutionDTO.getMcc() != null
				&& executiveTaskExecutionDTO.getMnc() != null && executiveTaskExecutionDTO.getCellId() != null
				&& executiveTaskExecutionDTO.getLac() != null)) {
			executiveTaskExecution.setMcc(executiveTaskExecutionDTO.getMcc());
			executiveTaskExecution.setMnc(executiveTaskExecutionDTO.getMnc());
			executiveTaskExecution.setCellId(executiveTaskExecutionDTO.getCellId());
			executiveTaskExecution.setLac(executiveTaskExecutionDTO.getLac());
		} else if (locationType.equals(LocationType.NoLocation) || locationType.equals(LocationType.FlightMode)) {
			executiveTaskExecution.setLocation("No Location");
		}
		if (startLocationType != null) {
			if (startLocationType.equals(LocationType.TowerLocation) || (executiveTaskExecutionDTO.getStartMcc() != null
					&& executiveTaskExecutionDTO.getStartMnc() != null
					&& executiveTaskExecutionDTO.getStartCellId() != null
					&& executiveTaskExecutionDTO.getStartLac() != null)) {
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

		executiveTaskExecution.setMockLocationStatus(executiveTaskExecutionDTO.getMockLocationStatus());
		executiveTaskExecution.setWithCustomer(executiveTaskExecutionDTO.getWithCustomer());
		// set company
		executiveTaskExecution.setCompany(company);
		executiveTaskExecution = executiveTaskExecutionRepository.save(executiveTaskExecution);

		log.info("----Saving Executive task Execution Completed----- " + user.getLogin());
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
	private List<InventoryVoucherHeader> saveInventoryVouchers(Company company, User user,
			EmployeeProfile employeeProfile, ExecutiveTaskExecution executiveTaskExecution,
			List<InventoryVoucherHeaderDTO> inventoryVoucherDTOs) {

		log.info("----Saving Inventory Vocucher Started----- " + user.getLogin());

		Optional<CompanyConfiguration> opPiecesToQuantity = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.PIECES_TO_QUANTITY);
		if (inventoryVoucherDTOs != null && inventoryVoucherDTOs.size() > 0) {
			// Optional<CompanyConfiguration> companyConfiguration =
			// companyConfigurationRepository.findByCompanyPidAndName(company.getPid(),
			// CompanyConfig.SALES_PDF_DOWNLOAD);
			// not required

			List<Object[]> documentWiseCount = new ArrayList<Object[]>(); // count ,pid ,document_id
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_175" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get the count of InvVoucherHeader";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			documentWiseCount = inventoryVoucherHeaderRepository
					.findCountOfInventoryVoucherHeader(SecurityUtils.getCurrentUsersCompanyId());
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

			List<OrderStatus> orderStatus = orderStatusRepository.findAllByCompanyId();
			List<SalesLedger> salesLedgers = salesLedgerRepository.findAllByCompanyId();

			List<InventoryVoucherHeader> inventoryVouchers = new ArrayList<>();
			for (InventoryVoucherHeaderDTO inventoryVoucherDTO : inventoryVoucherDTOs) {

				String documentNumberLocal = inventoryVoucherDTO.getDocumentNumberLocal();
				log.debug("----------Inventory Voucher Document Number:- " + documentNumberLocal);

				InventoryVoucherHeader inventoryVoucherHeader = new InventoryVoucherHeader();
				// set pid
				inventoryVoucherHeader.setPid(InventoryVoucherHeaderService.PID_PREFIX + RandomUtil.generatePid());
				inventoryVoucherHeader.setCreatedBy(user);
				Document document = documentRepository.findOneByPid(inventoryVoucherDTO.getDocumentPid()).get();
				if (document.getOrderNoEnabled()) {
					long count = 0;

					Optional<Object[]> opDocWiseCount = documentWiseCount.stream()
							.filter(dwc -> document.getPid().equals(dwc[1])).findAny();
					if (!opDocWiseCount.isPresent()) {
						count = 1;
						inventoryVoucherHeader.setOrderNumber(count);
					}

					for (Object[] obj : documentWiseCount) {
						if (document.getPid().equals(obj[1].toString())) {
							count = obj[0] != null ? Long.parseLong(obj[0].toString()) : 0;
							count = count + 1; // Finding count of total inventory vouchers incrementing one
							inventoryVoucherHeader.setOrderNumber(count);
							break;
						}
					}
				}

				if (inventoryVoucherDTO.getSalesLedgerName() != null
						&& !inventoryVoucherDTO.getSalesLedgerName().equals("")) {
					if (salesLedgers.size() > 0) {
						Optional<SalesLedger> opSl = salesLedgers.stream()
								.filter(sl -> sl.getName().equals(inventoryVoucherDTO.getSalesLedgerName())).findAny();
						if (opSl.isPresent()) {
							inventoryVoucherHeader.setSalesLedger(opSl.get());
						}
					}

				}

				inventoryVoucherHeader.setDocument(document);
				inventoryVoucherHeader.setDocumentDate(inventoryVoucherDTO.getDocumentDate());
				// set unique server and local number
				inventoryVoucherHeader.setDocumentNumberLocal(inventoryVoucherDTO.getDocumentNumberLocal());
				inventoryVoucherHeader.setDocumentNumberServer(inventoryVoucherDTO.getDocumentNumberLocal());
				inventoryVoucherHeader.setDocumentTotal(inventoryVoucherDTO.getDocumentTotal());
				inventoryVoucherHeader.setDocumentVolume(inventoryVoucherDTO.getDocumentVolume());
				inventoryVoucherHeader.setDocDiscountAmount(inventoryVoucherDTO.getDocDiscountAmount());
				inventoryVoucherHeader.setDocDiscountPercentage(inventoryVoucherDTO.getDocDiscountPercentage());

				inventoryVoucherHeader.setRoundedOff(inventoryVoucherDTO.getRoundedOff());
				inventoryVoucherHeader.setReferenceInvoiceNumber(inventoryVoucherDTO.getReferenceInvoiceNumber() != null
						? inventoryVoucherDTO.getReferenceInvoiceNumber()
						: "");

				inventoryVoucherHeader.setImageRefNo(inventoryVoucherDTO.getImageRefNo());

				if (inventoryVoucherDTO.getBookingDate() != null && !inventoryVoucherDTO.getBookingDate().equals("")) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

					LocalDate bookingDate = LocalDate.parse(inventoryVoucherDTO.getBookingDate(), formatter);
					inventoryVoucherHeader.setBookingDate(bookingDate);
				}
				inventoryVoucherHeader.setValidationDays(inventoryVoucherDTO.getValidationDays() == null
						|| inventoryVoucherDTO.getValidationDays().equals("") ? ""
								: inventoryVoucherDTO.getValidationDays());

				if (inventoryVoucherDTO.getDeliveryDateDocument() != null
						&& !inventoryVoucherDTO.getDeliveryDateDocument().equals("")) {

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					LocalDate deliveryDate = LocalDate.parse(inventoryVoucherDTO.getDeliveryDateDocument(), formatter);
					inventoryVoucherHeader.setDeliveryDate(deliveryDate);
				}
				inventoryVoucherHeader.setBookingId(
						inventoryVoucherDTO.getBookingId() == null || inventoryVoucherDTO.getBookingId().equals("") ? ""
								: inventoryVoucherDTO.getBookingId());
				DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

				String id1 = "INV_QUERY_123" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 = "get top1 by created by login ";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
				User user1 = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
				InventoryVoucherHeader lastInventoryVoucher = inventoryVoucherHeaderRepository
						.findTop1ByCreatedByLoginOrderAndDocumentPidByCreatedDateDesc(user1.getId(), document.getId());
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
				if (lastInventoryVoucher != null) {
					Date lastInventoryVoucherDate = Date
							.from(lastInventoryVoucher.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant());

					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

					Date today = new Date();

					Date currentDate = new Date();
					Date previousDate = new Date();

					try {
						currentDate = formatter.parse(formatter.format(today));
						previousDate = formatter.parse(formatter.format(lastInventoryVoucherDate));

					} catch (ParseException e) {
						e.printStackTrace();
					}

					String referenceDocNo = lastInventoryVoucher.getReferenceDocumentNumber();

					int refDocNo = 0;

					if (referenceDocNo == null || referenceDocNo.isEmpty()) {
						refDocNo = 1;
					} else {

						refDocNo = Integer.parseInt(referenceDocNo);
					}

					log.info(previousDate + "--------------" + currentDate);
					if (previousDate.compareTo(currentDate) == 0) {
						refDocNo++;
					} else {
						refDocNo = 1;
					}
					log.info("Reference Document Number =" + refDocNo + "----------");
					inventoryVoucherHeader.setReferenceDocumentNumber(String.valueOf(refDocNo));
				} else {
					inventoryVoucherHeader.setReferenceDocumentNumber("1");
				}

				if (inventoryVoucherDTO.getStatus() != null && inventoryVoucherDTO.getStatus()) {
					inventoryVoucherHeader.setStatus(true);
				}

				if (orderStatus != null && orderStatus.size() > 0) {
					inventoryVoucherHeader.setOrderStatus(orderStatus.get(0));
				}

				if (inventoryVoucherDTO.getOrderStatusId() != null) {
					inventoryVoucherHeader
							.setOrderStatus(orderStatusRepository.findOne(inventoryVoucherDTO.getOrderStatusId()));
				}

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
				DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id11 = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description11 = "get one by pid";
				LocalDateTime startLCTime11 = LocalDateTime.now();
				String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
				String startDate11 = startLCTime11.format(DATE_FORMAT11);
				logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
				inventoryVoucherHeader.setReceiverAccount(
						accountProfileRepository.findOneByPid(inventoryVoucherDTO.getReceiverAccountPid()).get());
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
				if (inventoryVoucherDTO.getSupplierAccountPid() != null)
					inventoryVoucherHeader.setSupplierAccount(
							accountProfileRepository.findOneByPid(inventoryVoucherDTO.getSupplierAccountPid()).get());
				// set company
				inventoryVoucherHeader.setCompany(company);
				// set Executive Task Execution
				inventoryVoucherHeader.setExecutiveTaskExecution(executiveTaskExecution);
				// set voucher details
				List<InventoryVoucherDetail> inventoryVoucherDetails = new ArrayList<InventoryVoucherDetail>();
				inventoryVoucherDTO.getInventoryVoucherDetails().forEach(inventoryVoucherDetailDTO -> {

//					log.info("Pieces :- " + inventoryVoucherDetailDTO.getPieces() + "------------"
//							+ "Rate Per Peices :-" + inventoryVoucherDetailDTO.getRatePerPiece());

					// set pices and rate to quantity and selling rate(SAP)
					if (opPiecesToQuantity.isPresent()) {

						if (opPiecesToQuantity.get().getValue().equals("true")) {
							inventoryVoucherDetailDTO.setQuantity(inventoryVoucherDetailDTO.getPieces());
							inventoryVoucherDetailDTO.setSellingRate(inventoryVoucherDetailDTO.getRatePerPiece());
						}
					}

					// find source and destination Stock Location
					StockLocation sourceStockLocation = null;
					StockLocation destinationStockLocation = null;
					log.info("Product Name : " + inventoryVoucherDetailDTO.getProductName());
					log.info("S StockLocation Pid : " + inventoryVoucherDetailDTO.getSourceStockLocationPid());
					log.info("S StockLocation name : " + inventoryVoucherDetailDTO.getSourceStockLocationName());
					log.info("StockLocation Pid : " + inventoryVoucherDetailDTO.getStockLocationPid());
					log.info("StockLocation Name : " + inventoryVoucherDetailDTO.getStockLocationName());
					if (inventoryVoucherDetailDTO.getSourceStockLocationPid() != null)
						sourceStockLocation = stockLocationRepository
								.findOneByPid(inventoryVoucherDetailDTO.getSourceStockLocationPid()).get();
					if (inventoryVoucherDetailDTO.getDestinationStockLocationPid() != null)
						destinationStockLocation = stockLocationRepository
								.findOneByPid(inventoryVoucherDetailDTO.getDestinationStockLocationPid()).get();

					if (inventoryVoucherDetailDTO.getStockLocationPid() != null) {
						sourceStockLocation = stockLocationRepository
								.findOneByPid(inventoryVoucherDetailDTO.getStockLocationPid()).get();
					}
					PriceLevel inventoryVoucherDetailPriceLevel = null;
					if (inventoryVoucherDetailDTO.getPriceLevelPid() != null
							&& !inventoryVoucherDetailDTO.getPriceLevelPid().equals("")) {
						inventoryVoucherDetailPriceLevel = priceLevelRepository
								.findOneByPid(inventoryVoucherDetailDTO.getPriceLevelPid()).get();
					}
					// find referenceInventory Voucher header and detail
					InventoryVoucherHeader referenceInventoryVoucherHeader = null;
					InventoryVoucherDetail referenceInventoryVoucherDetail = null;

					if (inventoryVoucherDetailDTO.getReferenceInventoryVoucherHeaderPid() != null) {
						String ids = "INV_QUERY_212";
						String descriptions = "get one inv Voucher by pid";
						log.info("{ Query Id:- " + ids + " Query Description:- " + descriptions + " }");

						referenceInventoryVoucherHeader = inventoryVoucherHeaderRepository
								.findOneByPid(inventoryVoucherDetailDTO.getReferenceInventoryVoucherHeaderPid()).get();
						referenceInventoryVoucherHeader.setStatus(Boolean.TRUE);
					}

					if (inventoryVoucherDetailDTO.getReferenceInventoryVoucherDetailId() != null) {
//						referenceInventoryVoucherDetail = inventoryVoucherDetailRepository
//								.findOne(inventoryVoucherDetailDTO.getReferenceInventoryVoucherDetailId());

						List<InventoryVoucherDetail> details = inventoryVoucherDetailRepository
								.findAllById(inventoryVoucherDetailDTO.getReferenceInventoryVoucherDetailId());

						if (details.size() > 0) {
							referenceInventoryVoucherDetail = details.get(0);
						}
					}

					Optional<ProductProfile> opProductProfile = productProfileRepository
							.findOneByPid(inventoryVoucherDetailDTO.getProductPid());
					if (!opProductProfile.isPresent()) {
						log.info(inventoryVoucherDetailDTO.toString());
						throw new IllegalArgumentException(
								inventoryVoucherDetailDTO.getProductName() + "Product Not found");
					}
					ProductProfile productProfile = opProductProfile.get();

					// Inventory Voucher Batch Details
					List<InventoryVoucherBatchDetail> inventoryVoucherBatchDetails = new ArrayList<>();
					if (inventoryVoucherDetailDTO.getInventoryVoucherBatchDetails() != null
							&& inventoryVoucherDetailDTO.getInventoryVoucherBatchDetails().size() > 0) {
						inventoryVoucherDetailDTO.getInventoryVoucherBatchDetails()
								.forEach(inventoryVoucherBatchDetail -> {
									StockLocation stockLocation = null;
									if (inventoryVoucherBatchDetail.getStockLocationPid() != null
											&& !inventoryVoucherBatchDetail.getStockLocationPid().isEmpty())
										stockLocation = stockLocationRepository
												.findOneByPid(inventoryVoucherBatchDetail.getStockLocationPid()).get();
									inventoryVoucherBatchDetails.add(new InventoryVoucherBatchDetail(productProfile,
											inventoryVoucherBatchDetail.getBatchNumber(),
											inventoryVoucherBatchDetail.getBatchDate(),
											inventoryVoucherBatchDetail.getRemarks(),
											inventoryVoucherBatchDetail.getQuantity(), company, stockLocation));
								});
					}

					if (inventoryVoucherDetailPriceLevel != null) {
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
								inventoryVoucherDetailDTO.getRemarks(), inventoryVoucherBatchDetails,
								inventoryVoucherDetailPriceLevel));
					} else {
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

				inventoryVouchers.add(inventoryVoucherHeaderRepository.save(inventoryVoucherHeader));

			}
			log.info("----Saving Inventory Voucher Completed----- " + user.getLogin());
			return inventoryVouchers;
		}
		return null;
	}

	/**
	 * save Accounting Vouchers
	 * 
	 * @param company
	 * @param user
	 * @param employeeProfile
	 * @param executiveTaskExecution
	 * @param accountingVoucherDTOs
	 * @return
	 */
	private List<AccountingVoucherHeader> saveAccountingVouchers(Company company, User user,
			EmployeeProfile employeeProfile, ExecutiveTaskExecution executiveTaskExecution,
			List<AccountingVoucherHeaderDTO> accountingVoucherDTOs) {

		log.info("----Saving Accounting Voucher Startrd----- " + user.getLogin());
		if (accountingVoucherDTOs != null && !accountingVoucherDTOs.isEmpty()) {
			List<AccountingVoucherHeader> accountingVouchers = new ArrayList<>();
			for (AccountingVoucherHeaderDTO accountingVoucherDTO : accountingVoucherDTOs) {

				String documentNumberLocal = accountingVoucherDTO.getDocumentNumberLocal();
				log.debug("----------Accounting Voucher Document Number:- " + documentNumberLocal);
				AccountingVoucherHeader accountingVoucherHeader = new AccountingVoucherHeader();
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				AccountProfile accountProfile = accountProfileRepository
						.findOneByPid(accountingVoucherDTO.getAccountProfilePid()).get();
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

				// set pid
				accountingVoucherHeader.setPid(AccountingVoucherHeaderService.PID_PREFIX + RandomUtil.generatePid());
				accountingVoucherHeader.setAccountProfile(accountProfile);
				accountingVoucherHeader.setCreatedBy(user);
				accountingVoucherHeader.setDocumentDate(accountingVoucherDTO.getDocumentDate());
				accountingVoucherHeader
						.setDocument(documentRepository.findOneByPid(accountingVoucherDTO.getDocumentPid()).get());
				accountingVoucherHeader.setEmployee(employeeProfile);
				accountingVoucherHeader.setOutstandingAmount(accountingVoucherDTO.getOutstandingAmount());
				accountingVoucherHeader.setRemarks(accountingVoucherDTO.getRemarks());
				accountingVoucherHeader.setTotalAmount(accountingVoucherDTO.getTotalAmount());
				accountingVoucherHeader.setImageRefNo(accountingVoucherDTO.getImageRefNo());

				// set company
				accountingVoucherHeader.setCompany(company);
				// set Executive Task Execution
				accountingVoucherHeader.setExecutiveTaskExecution(executiveTaskExecution);
				// set unique server and local number
				accountingVoucherHeader.setDocumentNumberLocal(accountingVoucherDTO.getDocumentNumberLocal());
				// Set unique server number
				accountingVoucherHeader.setDocumentNumberServer(accountingVoucherDTO.getDocumentNumberLocal());

				// set voucher details
				List<AccountingVoucherDetail> accountingVoucherDetails = new ArrayList<>();
				List<AccountingVoucherDetailDTO> accountingVoucherDetailDTOs = accountingVoucherDTO
						.getAccountingVoucherDetails();
				List<ReceivablePayable> receivablePayables = new ArrayList<>();
				List<AccountProfile> accountProfiles = new ArrayList<>();
				for (AccountingVoucherDetailDTO accountingVoucherDetailDTO : accountingVoucherDetailDTOs) {
					AccountingVoucherDetail accountingVoucherDetail = new AccountingVoucherDetail();
					accountingVoucherDetail.setAmount(accountingVoucherDetailDTO.getAmount());
					accountingVoucherDetail.setBy(
							accountProfileRepository.findOneByPid(accountingVoucherDetailDTO.getByAccountPid()).get());
					accountingVoucherDetail.setTo(
							accountProfileRepository.findOneByPid(accountingVoucherDetailDTO.getToAccountPid()).get());
					accountingVoucherDetail.setInstrumentDate(accountingVoucherDetailDTO.getInstrumentDate());
					accountingVoucherDetail.setInstrumentNumber(accountingVoucherDetailDTO.getInstrumentNumber());
					accountingVoucherDetail.setMode(accountingVoucherDetailDTO.getMode());
					accountingVoucherDetail.setReferenceNumber(accountingVoucherDetailDTO.getReferenceNumber());
					accountingVoucherDetail.setRemarks(accountingVoucherDetailDTO.getRemarks());
					accountingVoucherDetail.setVoucherDate(accountingVoucherDetailDTO.getVoucherDate());
					accountingVoucherDetail.setVoucherNumber(accountingVoucherDetailDTO.getVoucherNumber());
					accountingVoucherDetail
							.setProvisionalReceiptNo(accountingVoucherDetailDTO.getProvisionalReceiptNo());
					// set bank
					if (accountingVoucherDetailDTO.getBankPid() != null) {
						Bank bank = bankRepository.findOneByPid(accountingVoucherDetailDTO.getBankPid()).get();
						accountingVoucherDetail.setBank(bank);
					} else {
						accountingVoucherDetail.setBankName(accountingVoucherDetailDTO.getBankName());
					}
					// set Income Expense
					if (accountingVoucherDetailDTO.getIncomeExpenseHeadPid() != null) {
						accountingVoucherDetail.setIncomeExpenseHead(incomeExpenseHeadRepository
								.findOneByPid(accountingVoucherDetailDTO.getIncomeExpenseHeadPid()).get());
					}
					accountingVoucherDetail
							.setProvisionalReceiptNo(accountingVoucherDetailDTO.getProvisionalReceiptNo());
					List<AccountingVoucherAllocationDTO> accountingVoucherAllocationDTOs = accountingVoucherDetailDTO
							.getAccountingVoucherAllocations();
					if (accountingVoucherAllocationDTOs != null && accountingVoucherAllocationDTOs.size() > 0) {
						List<AccountingVoucherAllocation> accountingVoucherAllocations = new ArrayList<AccountingVoucherAllocation>();
						for (AccountingVoucherAllocationDTO accountingVoucherAllocationDTO : accountingVoucherAllocationDTOs) {
							AccountingVoucherAllocation accountingVoucherAllocation = new AccountingVoucherAllocation();
							accountingVoucherAllocation.setAmount(accountingVoucherAllocationDTO.getAmount());
							accountingVoucherAllocation.setMode(accountingVoucherAllocationDTO.getMode());
							accountingVoucherAllocation.setReferenceDocumentNumber(
									accountingVoucherAllocationDTO.getReferenceDocumentNumber());
							accountingVoucherAllocation
									.setReferenceNumber(accountingVoucherAllocationDTO.getReferenceNumber());
							accountingVoucherAllocation.setRemarks(accountingVoucherAllocationDTO.getRemarks());
							accountingVoucherAllocation
									.setVoucherNumber(accountingVoucherAllocationDTO.getVoucherNumber());
							accountingVoucherAllocation
									.setReceivablePayablePid(accountingVoucherAllocationDTO.getReceivablePayablePid());
							accountingVoucherAllocation
									.setReceivablePayableId(accountingVoucherAllocationDTO.getReceivablePayableId());
							if (accountingVoucherAllocationDTO.getReferenceDocumentNumber() != null) {
								Optional<ReceivablePayable> receivablePayable = receivablePayableRepository
										.findOneByAccountProfilePidAndReferenceDocumentNumber(
												accountingVoucherHeader.getAccountProfile().getPid(),
												accountingVoucherAllocationDTO.getReferenceDocumentNumber());
								receivablePayable.ifPresent(rPayable -> {
									rPayable.setReferenceDocumentBalanceAmount(
											rPayable.getReferenceDocumentBalanceAmount()
													- accountingVoucherAllocationDTO.getAmount());

									accountProfile.setClosingBalance(accountProfile.getClosingBalance()
											- accountingVoucherAllocationDTO.getAmount());

									receivablePayables.add(rPayable);
									accountProfiles.add(accountProfile);
								});
							}
							accountingVoucherAllocations.add(accountingVoucherAllocation);
						}
						accountingVoucherDetail.setAccountingVoucherAllocations(accountingVoucherAllocations);
					}
					accountingVoucherDetails.add(accountingVoucherDetail);
				}
				if (receivablePayables.size() > 0) {
					receivablePayableRepository.save(receivablePayables);
					accountProfileRepository.save(accountProfile);
				}
				accountingVoucherHeader.setAccountingVoucherDetails(accountingVoucherDetails);
				accountingVouchers.add(accountingVoucherHeader);

				// Add to web socket message
				/*
				 * MessageDTO messageDTO = new MessageDTO();
				 * messageDTO.setDocumentName(accountingVoucherDTO. getDocumentName());
				 * messageDTO.setDocumentTotal(accountingVoucherDTO. getTotalAmount());
				 * messageDTOs.add(messageDTO);
				 */
			}

			List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
					.save(accountingVouchers);

			log.info("----Saving Accounting Voucher Completed----- " + user.getLogin());
			return accountingVoucherHeaders;
		}
		return null;
	}

	/**
	 * Save filled dynamic forms
	 * 
	 * @param company
	 * @param user
	 * @param employeeProfile
	 * @param executiveTaskExecution
	 * @param dynamicDocumentHeaderDTOs
	 * @return
	 */
	private List<DynamicDocumentHeader> saveDynamicForms(Company company, User user, EmployeeProfile employeeProfile,
			ExecutiveTaskExecution executiveTaskExecution, List<DynamicDocumentHeaderDTO> dynamicDocumentHeaderDTOs) {

		log.info("----Saving Dynamic Document Startrd----- " + user.getLogin());
		if (dynamicDocumentHeaderDTOs != null && dynamicDocumentHeaderDTOs.size() > 0) {

			List<DynamicDocumentHeader> dynamicDocumentHeaders = new ArrayList<>();
			for (DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO : dynamicDocumentHeaderDTOs) {

				String documentNumberLocal = dynamicDocumentHeaderDTO.getDocumentNumberLocal();
				log.debug("----------Dynamic Document Form Document Number:- " + documentNumberLocal);
				DynamicDocumentHeader dynamicDocumentHeader = new DynamicDocumentHeader();
				// set pid
				dynamicDocumentHeader.setPid(DynamicDocumentHeaderService.PID_PREFIX + RandomUtil.generatePid());
				dynamicDocumentHeader.setCreatedBy(user);
				dynamicDocumentHeader
						.setDocument(documentRepository.findOneByPid(dynamicDocumentHeaderDTO.getDocumentPid()).get());
				dynamicDocumentHeader.setDocumentDate(dynamicDocumentHeaderDTO.getDocumentDate());
				// set unique server and local number
				dynamicDocumentHeader.setDocumentNumberServer(dynamicDocumentHeaderDTO.getDocumentNumberLocal());
				dynamicDocumentHeader.setDocumentNumberLocal(dynamicDocumentHeaderDTO.getDocumentNumberLocal());
				if (dynamicDocumentHeaderDTO.getEmployeePid() != null
						&& !dynamicDocumentHeaderDTO.getEmployeePid().equals("no")) {
					dynamicDocumentHeader.setEmployee(employeeProfileRepository
							.findEmployeeProfileByPid(dynamicDocumentHeaderDTO.getEmployeePid()));
				} else {
					dynamicDocumentHeader.setEmployee(employeeProfile);
				}
				// set company
				dynamicDocumentHeader.setCompany(company);
				// set Executive Task Execution
				dynamicDocumentHeader.setExecutiveTaskExecution(executiveTaskExecution);

				// set voucher details
				List<FilledForm> filledForms = new ArrayList<FilledForm>();
				List<FilledFormDetail> datePickerFormElements = new ArrayList<>();
				List<FilledFormDetailDTO> filledFormDetailList = new ArrayList<>();
				dynamicDocumentHeaderDTO.getFilledForms().forEach(filledFormDTO -> {
					FilledForm filledForm = new FilledForm();
					// set pid
					filledForm.setPid(FilledFormService.PID_PREFIX + RandomUtil.generatePid());
					filledForm.setImageRefNo(filledFormDTO.getImageRefNo());
					filledForm.setForm(formRepository.findOneByPid(filledFormDTO.getFormPid()).get());
					// set voucher details
					List<FilledFormDetail> filledFormDetails = new ArrayList<FilledFormDetail>();
					List<FormElement> formElements = formFormElementRepository
							.findFormElementsByFormPid(filledFormDTO.getFormPid());
					for (FilledFormDetailDTO filledFormDetailDTO : filledFormDTO.getFilledFormDetails()) {
						FilledFormDetail filledFormDetail = new FilledFormDetail();
						FormElement formElement = formElementRepository
								.findOneByPid(filledFormDetailDTO.getFormElementPid()).get();
						filledFormDetail.setFormElement(formElement);
						filledFormDetail.setValue(filledFormDetailDTO.getValue());
						if (filledFormDetailDTO.getValue() == null || filledFormDetailDTO.getValue().isEmpty()) {
							filledFormDetail.setValue(formElement.getDefaultValue());
						}
						filledFormDetails.add(filledFormDetail);
						if (formElement.getFormElementType().getName().equals("datePicker")) {
							datePickerFormElements.add(filledFormDetail);
						}
						filledFormDetailList.add(filledFormDetailDTO);
					}
					// add default value exist user not appilcable elements
					for (FormElement formElement : formElements) {
						boolean isNotExist = true;
						for (FilledFormDetailDTO filledFormDetailDTO : filledFormDTO.getFilledFormDetails()) {
							if (filledFormDetailDTO.getFormElementPid().equals(formElement.getPid())) {
								isNotExist = false;
								break;
							}
						}
						if (isNotExist) {
							if (formElement.getDefaultValue() != null && !formElement.getDefaultValue().isEmpty()) {
								FilledFormDetail filledFormDetail = new FilledFormDetail();
								filledFormDetail.setFormElement(formElement);
								filledFormDetail.setValue(formElement.getDefaultValue());
								filledFormDetails.add(filledFormDetail);
							}
						}
					}
					filledForm.setFilledFormDetails(filledFormDetails);
					filledForms.add(filledForm);
				});
				dynamicDocumentHeader.setFilledForms(filledForms);
				dynamicDocumentHeaders.add(dynamicDocumentHeader);
			}
			List<DynamicDocumentHeader> dynamicDocuments = dynamicDocumentHeaderRepository.save(dynamicDocumentHeaders);
			log.info("----Saving Dynamic Document Completed----- " + user.getLogin());
			return dynamicDocuments;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskSubmissionDTO> findExecutiveTaskSubmissions(String activityPid, String accountPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {

		List<ExecutiveTaskSubmissionDTO> executiveTaskSubmissions = new ArrayList<>();
		Document document = null;
		if (documentPid != null) {
			document = documentRepository.findOneByPid(documentPid).get();
			if (!document.getEditable()) {
				log.info("select document is not editable...");
				return executiveTaskSubmissions;
			}
		}
		List<ExecutiveTaskExecution> executiveTaskExecutions = null;
		if (fromDate == null || toDate == null) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findByUserAndActivityPidAndAccountPid(activityPid, accountPid);
		} else {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findByUserAndActivityPidAndAccountPidAndDateBetween(activityPid, accountPid, fromDate, toDate);
		}
		if (!executiveTaskExecutions.isEmpty()) {
			for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
				ExecutiveTaskSubmissionDTO executiveTaskSubmission = new ExecutiveTaskSubmissionDTO();

				// set executive task execution
				ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO(
						executiveTaskExecution);
				executiveTaskSubmission.setExecutiveTaskExecutionDTO(executiveTaskExecutionDTO);

				List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();
				List<AccountingVoucherHeaderDTO> accountingVouchers = new ArrayList<>();
				List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<>();

				if (document != null) {
					if (document.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
						inventoryVouchers = getInventoryVouchers(executiveTaskExecution.getPid(), documentPid);
					} else if (document.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
						accountingVouchers = getAccountingVouchers(executiveTaskExecution.getPid(), documentPid);
					} else if (document.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
						dynamicDocuments = getDynamicDocuments(executiveTaskExecution.getPid(), documentPid);
					}
				} else {
					inventoryVouchers = getInventoryVouchers(executiveTaskExecution.getPid(), documentPid);
					accountingVouchers = getAccountingVouchers(executiveTaskExecution.getPid(), documentPid);
					dynamicDocuments = getDynamicDocuments(executiveTaskExecution.getPid(), documentPid);
				}
				executiveTaskSubmission.setInventoryVouchers(inventoryVouchers);
				executiveTaskSubmission.setAccountingVouchers(accountingVouchers);
				executiveTaskSubmission.setDynamicDocuments(dynamicDocuments);
				if (!inventoryVouchers.isEmpty() || !accountingVouchers.isEmpty() || !dynamicDocuments.isEmpty()) {
					executiveTaskSubmissions.add(executiveTaskSubmission);
				}
			}
		}
		return executiveTaskSubmissions;
	}

	@Override
	public ExecutiveTaskSubmissionDTO findExecutiveTaskSubmissionsByTerritotyAndReferenceDocument(String territoryPid,
			String refDocumentPid, LocalDate startDate, LocalDate endDate, String processStatus) {
		ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO = new ExecutiveTaskSubmissionDTO();
		List<Boolean> tallyStatusList = new ArrayList<>();
		if ("Pending".equals(processStatus)) {
			tallyStatusList.add(Boolean.FALSE);
		} else {
			tallyStatusList.addAll(Arrays.asList(Boolean.TRUE, Boolean.FALSE));
		}
		Set<Long> accountIds = locationAccountProfileRepository.findAccountProfileIdByLocationPid(territoryPid);
		if (!accountIds.isEmpty()) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_165" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get by accountIdIn and DocumentPid TallyStatus andDocumentDateBetween";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate1 = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate1 + "," + startTime + ",_ ,0 ,START,_," + description);
			List<Object[]> inventoryVouchers = inventoryVoucherHeaderRepository
					.findByAccountIdInAndDocumentPidAndTallyStatusAndDocumentDateBetweenOrderByDocumentDateDesc(
							accountIds, refDocumentPid, tallyStatusList, startDate.atTime(0, 0),
							endDate.atTime(23, 59));
			String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate1 = startLCTime.format(DATE_FORMAT);
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
			logger.info(id + "," + endDate1 + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

			if (!inventoryVouchers.isEmpty()) {
				Set<String> ivHeaderPids = inventoryVouchers.parallelStream().map(obj -> obj[0].toString())
						.collect(Collectors.toSet());
				List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetailRepository
						.findAllByInventoryVoucherHeaderPidIn(ivHeaderPids);
				Map<String, List<InventoryVoucherDetail>> ivDetailMap = ivDetails.stream()
						.collect(Collectors.groupingBy(obj -> obj.getInventoryVoucherHeader().getPid()));
				int size = inventoryVouchers.size();
				List<InventoryVoucherHeaderDTO> inventoryVoucherDtos = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					Object[] ivHeaderData = inventoryVouchers.get(i);
					InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();
					inventoryVoucherHeaderDTO.setPid(ivHeaderData[0].toString());
					inventoryVoucherHeaderDTO.setDocumentNumberLocal(ivHeaderData[1].toString());
					inventoryVoucherHeaderDTO.setDocumentNumberServer(ivHeaderData[2].toString());
					inventoryVoucherHeaderDTO.setDocumentPid(ivHeaderData[3].toString());
					inventoryVoucherHeaderDTO.setDocumentName(ivHeaderData[4].toString());
					inventoryVoucherHeaderDTO.setCreatedDate((LocalDateTime) ivHeaderData[5]);
					inventoryVoucherHeaderDTO.setDocumentDate((LocalDateTime) ivHeaderData[6]);
					inventoryVoucherHeaderDTO.setReceiverAccountPid(ivHeaderData[7].toString());
					inventoryVoucherHeaderDTO.setReceiverAccountName(ivHeaderData[8].toString());
					inventoryVoucherHeaderDTO.setSupplierAccountPid(ivHeaderData[9].toString());
					inventoryVoucherHeaderDTO.setSupplierAccountName(ivHeaderData[10].toString());
					inventoryVoucherHeaderDTO.setEmployeePid(ivHeaderData[11].toString());
					inventoryVoucherHeaderDTO.setEmployeeName(ivHeaderData[12].toString());
					inventoryVoucherHeaderDTO.setUserName(ivHeaderData[13].toString());
					inventoryVoucherHeaderDTO.setDocumentTotal((double) ivHeaderData[14]);
					inventoryVoucherHeaderDTO.setDocumentVolume((double) ivHeaderData[15]);
					inventoryVoucherHeaderDTO.setProcessStatus(ivHeaderData[16].toString());
					// set voucher detail
					inventoryVoucherHeaderDTO
							.setInventoryVoucherDetails(ivDetailMap.get(inventoryVoucherHeaderDTO.getPid()).stream()
									.map(InventoryVoucherDetailDTO::new).collect(Collectors.toList()));
					inventoryVoucherDtos.add(inventoryVoucherHeaderDTO);
				}
				executiveTaskSubmissionDTO.getInventoryVouchers().addAll(inventoryVoucherDtos);
			}
		}
		return executiveTaskSubmissionDTO;
	}

	@Override
	public ExecutiveTaskSubmissionDTO findExecutiveTaskSubmissionsByReferenceDocuments(String accountPid,
			String documentPid, LocalDate startDate, LocalDate endDate, String processStatus) {
		Set<Long> refDocumentIds = referenceDocumentRepository.findReferenceDocumentIdByDocumentPid(documentPid);
		ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO = new ExecutiveTaskSubmissionDTO();
		// for now only inventory voucher is searching from mobile, uncomment and update
		// code if other vouchers requirement comes
		if (!refDocumentIds.isEmpty()) {
			// set inventory vouchers
			executiveTaskSubmissionDTO.getInventoryVouchers()
					.addAll(getInventoryVoucherDtos(accountPid, startDate, endDate, processStatus, refDocumentIds));
			// set accounting vouchers
//			executiveTaskSubmissionDTO.getAccountingVouchers().addAll(getAccountingVoucherDtos(accountPid, refDocumentIds));
			// set dynamic document
//			executiveTaskSubmissionDTO.getDynamicDocuments().addAll(getAllDynamicHeaderDTOs(refDocumentIds, accountPid));

			// old code
			/*
			 * List<AccountingVoucherHeader> accountingVouchers =
			 * accountingVoucherHeaderRepository
			 * .findByExecutiveTaskExecutionAccountProfilePidAndDocumentIn(accountPid,
			 * refDocuments); List<DynamicDocumentHeader> dynamicDocuments =
			 * dynamicDocumentHeaderRepository
			 * .findByExecutiveTaskExecutionAccountProfilePidAndDocumentIn(accountPid,
			 * refDocuments); executiveTaskSubmissionDTO.setExecutiveTaskExecutionDTO(null);
			 * 
			 * // set Accounting Vouchers accountingVouchers.parallelStream().forEach(avh ->
			 * { AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new
			 * AccountingVoucherHeaderDTO(avh);
			 * accountingVoucherHeaderDTO.setAccountingVoucherDetails(avh.
			 * getAccountingVoucherDetails().stream()
			 * .map(AccountingVoucherDetailDTO::new).collect(Collectors.toList()));
			 * executiveTaskSubmissionDTO.getAccountingVouchers().add(
			 * accountingVoucherHeaderDTO); }); // set Accounting Vouchers
			 * dynamicDocuments.parallelStream().forEach(ddh -> { DynamicDocumentHeaderDTO
			 * dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(ddh);
			 * dynamicDocumentHeaderDTO.setFilledForms(
			 * ddh.getFilledForms().stream().map(FilledFormDTO::new).collect(Collectors.
			 * toList()));
			 * executiveTaskSubmissionDTO.getDynamicDocuments().add(dynamicDocumentHeaderDTO
			 * ); });
			 */
		}
		return executiveTaskSubmissionDTO;
	}

	private List<InventoryVoucherHeaderDTO> getInventoryVoucherDtos(String accountPid, LocalDate startDate,
			LocalDate endDate, String processStatus, Set<Long> refDocumentIds) {
		List<Object[]> inventoryVouchers = null;
		if (processStatus == null || processStatus.isEmpty()) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_162" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get by accountPidIn and DocumentPidIn";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate1 = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate1 + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByAccountPidInAndDocumentPidInOrderByDocumentDateDesc(accountPid, refDocumentIds);
			String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate1 = startLCTime.format(DATE_FORMAT);
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
			logger.info(id + "," + endDate1 + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		} else {
			if ("Pending".equals(processStatus)) {
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_163" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get by accountPidIn and DocumentPidIn and TallyStatusAndDocumentDateBetween";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate1 = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate1 + "," + startTime + ",_ ,0 ,START,_," + description);
				inventoryVouchers = inventoryVoucherHeaderRepository
						.findByAccountPidInAndDocumentPidInAndTallyStatusAndDocumentDateBetweenOrderByDocumentDateDesc(
								accountPid, refDocumentIds, Boolean.FALSE, startDate.atTime(0, 0),
								endDate.atTime(23, 59));
				String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate1 = startLCTime.format(DATE_FORMAT);
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
				logger.info(id + "," + endDate1 + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);

			} else {
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_164" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get by accountPidIn and DocumentPidIn and DocumentDateBetween";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate1 = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate1 + "," + startTime + ",_ ,0 ,START,_," + description);
				inventoryVouchers = inventoryVoucherHeaderRepository
						.findByAccountPidInAndDocumentPidInAndDocumentDateBetweenOrderByDocumentDateDesc(accountPid,
								refDocumentIds, startDate.atTime(0, 0), endDate.atTime(23, 59));
				String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate1 = startLCTime.format(DATE_FORMAT);
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
				logger.info(id + "," + endDate1 + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);

			}
		}
		if (inventoryVouchers.isEmpty()) {
			return Collections.emptyList();
		}
		Set<String> ivHeaderPids = inventoryVouchers.parallelStream().map(obj -> obj[0].toString())
				.collect(Collectors.toSet());
		List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetailRepository
				.findAllByInventoryVoucherHeaderPidIn(ivHeaderPids);
		Map<String, List<InventoryVoucherDetail>> ivDetailMap = ivDetails.stream()
				.collect(Collectors.groupingBy(obj -> obj.getInventoryVoucherHeader().getPid()));
		int size = inventoryVouchers.size();
		List<InventoryVoucherHeaderDTO> inventoryVoucherDtos = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			Object[] ivHeaderData = inventoryVouchers.get(i);
			InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();
			inventoryVoucherHeaderDTO.setPid(ivHeaderData[0].toString());
			inventoryVoucherHeaderDTO.setDocumentNumberLocal(ivHeaderData[1].toString());
			inventoryVoucherHeaderDTO.setDocumentNumberServer(ivHeaderData[2].toString());
			inventoryVoucherHeaderDTO.setDocumentPid(ivHeaderData[3].toString());
			inventoryVoucherHeaderDTO.setDocumentName(ivHeaderData[4].toString());
			inventoryVoucherHeaderDTO.setCreatedDate((LocalDateTime) ivHeaderData[5]);
			inventoryVoucherHeaderDTO.setDocumentDate((LocalDateTime) ivHeaderData[6]);
			inventoryVoucherHeaderDTO.setReceiverAccountPid(ivHeaderData[7].toString());
			inventoryVoucherHeaderDTO.setReceiverAccountName(ivHeaderData[8].toString());
			inventoryVoucherHeaderDTO.setSupplierAccountPid(ivHeaderData[9].toString());
			inventoryVoucherHeaderDTO.setSupplierAccountName(ivHeaderData[10].toString());
			inventoryVoucherHeaderDTO.setEmployeePid(ivHeaderData[11].toString());
			inventoryVoucherHeaderDTO.setEmployeeName(ivHeaderData[12].toString());
			inventoryVoucherHeaderDTO.setUserName(ivHeaderData[13].toString());
			inventoryVoucherHeaderDTO.setDocumentTotal((double) ivHeaderData[14]);
			inventoryVoucherHeaderDTO.setDocumentVolume((double) ivHeaderData[15]);
			inventoryVoucherHeaderDTO.setProcessStatus(ivHeaderData[16].toString());

			// set voucher detail
			inventoryVoucherHeaderDTO.setInventoryVoucherDetails(ivDetailMap.get(inventoryVoucherHeaderDTO.getPid())
					.stream().map(InventoryVoucherDetailDTO::new).collect(Collectors.toList()));
			inventoryVoucherDtos.add(inventoryVoucherHeaderDTO);
		}
		return inventoryVoucherDtos;
	}

	// performance improved code for accounting voucher header dto with accounting
	// voucher detail
	private List<AccountingVoucherHeaderDTO> getAccountingVoucherDtos(String accountPid, Set<Long> refDocumentIds) {
		List<Object[]> accountingVouchers = null;
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_140" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all accVouchers by account profile and reffered documents";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		accountingVouchers = accountingVoucherHeaderRepository.getAllByAccountAndRefDocuments(refDocumentIds,
				accountPid);
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

		Set<String> avHeaderPids = accountingVouchers.parallelStream().map(obj -> obj[0].toString())
				.collect(Collectors.toSet());
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AVD_QUERY_111" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "get all by accVoucherHeaderPidIn";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountingVoucherDetail> avDetails = accountingVoucherDetailRepository
				.findAllByAccountingVoucherHeaderPidIn(avHeaderPids);
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
		Map<String, List<AccountingVoucherDetail>> avDetailMap = avDetails.stream()
				.collect(Collectors.groupingBy(obj -> obj.getAccountingVoucherHeader().getPid()));
		int size = accountingVouchers.size();
		List<AccountingVoucherHeaderDTO> accountingVoucherDtos = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			Object[] avHeaderData = accountingVouchers.get(i);

			AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO();
			accountingVoucherHeaderDTO.setPid(avHeaderData[0].toString());
			accountingVoucherHeaderDTO.setDocumentPid(avHeaderData[1].toString());
			accountingVoucherHeaderDTO.setDocumentName(avHeaderData[2].toString());
			accountingVoucherHeaderDTO.setAccountProfilePid(avHeaderData[3].toString());
			accountingVoucherHeaderDTO.setAccountProfileName(avHeaderData[4].toString());
			accountingVoucherHeaderDTO.setCreatedDate((LocalDateTime) avHeaderData[5]);
			accountingVoucherHeaderDTO.setDocumentDate((LocalDateTime) avHeaderData[6]);
			accountingVoucherHeaderDTO.setPhone(avHeaderData[7].toString());
			accountingVoucherHeaderDTO.setEmployeePid(avHeaderData[8].toString());
			accountingVoucherHeaderDTO.setEmployeeName(avHeaderData[9].toString());
			accountingVoucherHeaderDTO.setUserName(avHeaderData[10].toString());
			accountingVoucherHeaderDTO.setTotalAmount((double) avHeaderData[11]);
			accountingVoucherHeaderDTO.setOutstandingAmount((double) avHeaderData[12]);
			accountingVoucherHeaderDTO.setRemarks(avHeaderData[13].toString());
			accountingVoucherHeaderDTO.setDocumentNumberLocal(avHeaderData[14].toString());
			accountingVoucherHeaderDTO.setDocumentNumberServer(avHeaderData[15].toString());

			// set voucher detail
			accountingVoucherHeaderDTO.setAccountingVoucherDetails(avDetailMap.get(accountingVoucherHeaderDTO.getPid())
					.stream().map(AccountingVoucherDetailDTO::new).collect(Collectors.toList()));
			accountingVoucherDtos.add(accountingVoucherHeaderDTO);
		}
		return accountingVoucherDtos;
	}
// Performance improved  code for dynamic document header and filled form
//	private List<DynamicDocumentHeaderDTO> getAllDynamicHeaderDTOs(Set<Long> refDocuments, String accountPid){
//		List<Object[]> dheaders = null;
//		dheaders = dynamicDocumentHeaderRepository.findAllByAccountProfilePidAndDocumentIn(refDocuments , accountPid);
//		
//		
//		Set<String> dDHeaderPids = dheaders.parallelStream().map(obj -> obj[0].toString()).collect(Collectors.toSet());
//		List<FilledForm> filledForms = filledFormRepository.findByDynamicDocumentHeaderPidIn(dDHeaderPids);
//
//		Map<String, List<FilledForm>> filledFormMap = filledForms.stream()
//				.collect(Collectors.groupingBy(obj -> obj.getDynamicDocumentHeader().getPid()));
//		int size = dheaders.size();
//		
//		
//		List<DynamicDocumentHeaderDTO> dynamicDocumentHeaderList = new ArrayList<>(size);
//		
//		for (int i = 0; i < size; i++) {
//			Object[] dDHeader = dheaders.get(i);
//	
//			DynamicDocumentHeaderDTO ddhDto = new DynamicDocumentHeaderDTO();
//			
//			ddhDto.setPid(dDHeader[0].toString());
//			ddhDto.setDocumentNumberLocal(dDHeader[1].toString());
//			ddhDto.setDocumentNumberServer(dDHeader[2].toString());
//			ddhDto.setDocumentPid(dDHeader[3].toString());
//			ddhDto.setDocumentName(dDHeader[4].toString());
//			ddhDto.setCreatedDate((LocalDateTime)dDHeader[5]);
//			ddhDto.setDocumentDate((LocalDateTime)dDHeader[6]);
//			ddhDto.setEmployeePid(dDHeader[7].toString());
//			ddhDto.setEmployeeName(dDHeader[8].toString());
//			ddhDto.setUserName(dDHeader[9].toString());
//			ddhDto.setUserPid(dDHeader[10].toString());
//			ddhDto.setActivityName(dDHeader[11].toString());
//			ddhDto.setAccountName(dDHeader[12].toString());
//			ddhDto.setEmplyeePhone(dDHeader[13].toString());
//			ddhDto.setAccountAddress(dDHeader[14].toString());
//			ddhDto.setAccountPhone(dDHeader[15].toString());
//			ddhDto.setAccountEmail(dDHeader[16].toString());
//			ddhDto.setIsNew((boolean)dDHeader[17]);
//			
//			ddhDto.setFilledForms(filledFormMap.get(ddhDto.getPid()).stream()
//					.map(FilledFormDTO::new).collect(Collectors.toList()));
//			
//			dynamicDocumentHeaderList.add(ddhDto);
//			
//			
//
//		}
//		
//		return dynamicDocumentHeaderList;
//	}

	/**
	 * @param executiveTaskExecutionPid
	 * @param documentPid
	 * @return
	 */
	private List<InventoryVoucherHeaderDTO> getInventoryVouchers(String executiveTaskExecutionPid, String documentPid) {
		List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();
		List<InventoryVoucherHeader> inventoryVoucherHeaders = null;
		if (documentPid != null) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			String id = "INV_QUERY_118" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get by executive task execution pid and doc Pid ";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByExecutiveTaskExecutionPidAndDocumentPid(executiveTaskExecutionPid, documentPid);
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

			String id = "INV_QUERY_119" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get by executive task execution pid and doc editable true ";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVoucherHeaders = inventoryVoucherHeaderRepository
					.findByExecutiveTaskExecutionPidAndDocumentEditableTrue(executiveTaskExecutionPid);
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
		Set<Document> documents = new HashSet<>();
		for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {
			documents.add(inventoryVoucherHeader.getDocument());
		}
		if (documents.size() > 0) {
			List<DocumentApproval> documentApprovals = documentApprovalRepository
					.findByCompanyIdAndDocumentsAndCompletdIsFalse(documents);
			for (InventoryVoucherHeader inventoryVoucherHeader : inventoryVoucherHeaders) {
				boolean status = true;
				for (DocumentApproval documentApproval : documentApprovals) {
					if (documentApproval.getDocument().getPid().equals(inventoryVoucherHeader.getDocument().getPid())) {
						status = false;
						break;
					}
				}
				if (status) {
					InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO(
							inventoryVoucherHeader);
					inventoryVoucherHeaderDTO
							.setInventoryVoucherDetails(inventoryVoucherHeader.getInventoryVoucherDetails().stream()
									.map(InventoryVoucherDetailDTO::new).collect(Collectors.toList()));
					inventoryVouchers.add(inventoryVoucherHeaderDTO);
				}
			}
		}
		return inventoryVouchers;
	}

	/**
	 * @param executiveTaskExecutionPid
	 * @param documentPid
	 * @return
	 */
	private List<AccountingVoucherHeaderDTO> getAccountingVouchers(String executiveTaskExecutionPid,
			String documentPid) {
		List<AccountingVoucherHeaderDTO> accountingVouchers = new ArrayList<>();
		List<AccountingVoucherHeader> accountingVoucherHeaders = null;
		if (documentPid != null) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACC_QUERY_116" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = " get AccVoucher By ExecutiveTaskExecutionId and document pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountingVoucherHeaders = accountingVoucherHeaderRepository
					.findByExecutiveTaskExecutionPidAndDocumentPid(executiveTaskExecutionPid, documentPid);
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
			String id = "ACC_QUERY_117" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "  get AccVoucher By ExecutiveTaskExecutionId and document pid Editable isTrue";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountingVoucherHeaders = accountingVoucherHeaderRepository
					.findByExecutiveTaskExecutionPidAndDocumentEditableTrue(executiveTaskExecutionPid);
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
		Set<Document> documents = new HashSet<>();
		for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
			documents.add(accountingVoucherHeader.getDocument());
		}
		if (documents.size() > 0) {
			List<DocumentApproval> documentApprovals = documentApprovalRepository
					.findByCompanyIdAndDocumentsAndCompletdIsFalse(documents);
			for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
				boolean status = true;
				for (DocumentApproval documentApproval : documentApprovals) {
					if (documentApproval.getDocument().getPid()
							.equals(accountingVoucherHeader.getDocument().getPid())) {
						status = false;
						break;
					}
				}
				if (status) {
					AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO(
							accountingVoucherHeader);
					accountingVoucherHeaderDTO
							.setAccountingVoucherDetails(accountingVoucherHeader.getAccountingVoucherDetails().stream()
									.map(AccountingVoucherDetailDTO::new).collect(Collectors.toList()));
					accountingVouchers.add(accountingVoucherHeaderDTO);
				}
			}
		}
		return accountingVouchers;
	}

	/**
	 * @param executiveTaskExecutionPid
	 * @param documentPid
	 * @return
	 */
	private List<DynamicDocumentHeaderDTO> getDynamicDocuments(String executiveTaskExecutionPid, String documentPid) {
		List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<>();
		List<DynamicDocumentHeader> dynamicDocumentHeaders = null;
		if (documentPid != null) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "DYN_QUERY_111" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get documents by executive task executionPid and Doc Pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			dynamicDocumentHeaders = dynamicDocumentHeaderRepository
					.findByExecutiveTaskExecutionPidAndDocumentPid(executiveTaskExecutionPid, documentPid);
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
			String id = "DYN_QUERY_112" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get documents by executive task executionPid and Doc editable true";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			dynamicDocumentHeaders = dynamicDocumentHeaderRepository
					.findByExecutiveTaskExecutionPidAndDocumentEditableTrue(executiveTaskExecutionPid);
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

		Set<Document> documents = new HashSet<>();
		for (DynamicDocumentHeader dynamicDocumentHeader : dynamicDocumentHeaders) {
			documents.add(dynamicDocumentHeader.getDocument());
		}
		if (documents.size() > 0) {
			List<DocumentApproval> documentApprovals = documentApprovalRepository
					.findByCompanyIdAndDocumentsAndCompletdIsFalse(documents);
			for (DynamicDocumentHeader dynamicDocumentHeader : dynamicDocumentHeaders) {
				boolean status = true;
				for (DocumentApproval documentApproval : documentApprovals) {
					if (documentApproval.getDocument().getPid().equals(dynamicDocumentHeader.getDocument().getPid())) {
						status = false;
						break;
					}
				}
				if (status) {
					DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(
							dynamicDocumentHeader);
					dynamicDocumentHeaderDTO.setFilledForms(dynamicDocumentHeader.getFilledForms().stream()
							.map(FilledFormDTO::new).collect(Collectors.toList()));
					dynamicDocuments.add(dynamicDocumentHeaderDTO);
				}
			}
		}
		return dynamicDocuments;
	}

	@Override
	public TaskSubmissionResponse updateInventoryVoucher(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		InventoryVoucherHeaderDTO inventoryVoucherDTO = executiveTaskSubmissionDTO.getInventoryVouchers().get(0);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "INV_QUERY_212" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one inv Voucher by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		InventoryVoucherHeader inventoryVoucherHeader = inventoryVoucherHeaderRepository
				.findOneByPid(inventoryVoucherDTO.getPid()).get();
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

		// save history
		InventoryVoucherHeaderHistory inventoryVoucherHeaderHistory = new InventoryVoucherHeaderHistory(
				inventoryVoucherHeader);
		inventoryVoucherHeaderHistoryRepository.save(inventoryVoucherHeaderHistory);

		// set header values
		inventoryVoucherHeader.setDocumentTotal(inventoryVoucherDTO.getDocumentTotal());
		inventoryVoucherHeader.setDocumentVolume(inventoryVoucherDTO.getDocumentVolume());
		inventoryVoucherHeader.setDocDiscountAmount(inventoryVoucherDTO.getDocDiscountAmount());
		inventoryVoucherHeader.setDocDiscountPercentage(inventoryVoucherDTO.getDocDiscountPercentage());
		inventoryVoucherHeader.setReceiverAccount(
				accountProfileRepository.findOneByPid(inventoryVoucherDTO.getReceiverAccountPid()).get());
		if (inventoryVoucherDTO.getSupplierAccountPid() != null)
			inventoryVoucherHeader.setSupplierAccount(
					accountProfileRepository.findOneByPid(inventoryVoucherDTO.getSupplierAccountPid()).get());
		// set price level
		if (inventoryVoucherDTO.getPriceLevelPid() != null) {
			inventoryVoucherHeader
					.setPriceLevel(priceLevelRepository.findOneByPid(inventoryVoucherDTO.getPriceLevelPid()).get());
		}

		if (inventoryVoucherDTO.getOrderStatusId() != null) {
			OrderStatus orderStatus = orderStatusRepository.findOne(inventoryVoucherDTO.getOrderStatusId());
			inventoryVoucherHeader.setOrderStatus(orderStatus);
		}

		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		inventoryVoucherHeader.setUpdatedBy(user);
		inventoryVoucherHeader.setUpdatedDate(LocalDateTime.now());

		List<InventoryVoucherDetail> inventoryVoucherDetails = new ArrayList<>();

		// set detail values
		for (InventoryVoucherDetail inventoryVoucherDetail : inventoryVoucherHeader.getInventoryVoucherDetails()) {
			for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherDTO
					.getInventoryVoucherDetails()) {
				if (inventoryVoucherDetail.getProduct().getPid().equals(inventoryVoucherDetailDTO.getProductPid())) {
					inventoryVoucherDetail.setBatchDate(inventoryVoucherDetailDTO.getBatchDate());
					inventoryVoucherDetail.setBatchNumber(inventoryVoucherDetailDTO.getBatchNumber());
					inventoryVoucherDetail.setColor(inventoryVoucherDetailDTO.getColor());
					if (inventoryVoucherDetailDTO.getDestinationStockLocationPid() != null)
						inventoryVoucherDetail.setDestinationStockLocation(stockLocationRepository
								.findOneByPid(inventoryVoucherDetailDTO.getDestinationStockLocationPid()).get());
					inventoryVoucherDetail.setDiscountAmount(inventoryVoucherDetailDTO.getDiscountAmount());
					inventoryVoucherDetail.setDiscountPercentage(inventoryVoucherDetailDTO.getDiscountPercentage());
					inventoryVoucherDetail.setFreeQuantity(inventoryVoucherDetailDTO.getFreeQuantity());
					inventoryVoucherDetail.setLength(inventoryVoucherDetailDTO.getLength());
					inventoryVoucherDetail.setMrp(inventoryVoucherDetailDTO.getMrp());
					inventoryVoucherDetail.setPurchaseRate(inventoryVoucherDetailDTO.getPurchaseRate());
					inventoryVoucherDetail.setQuantity(inventoryVoucherDetailDTO.getQuantity());
					inventoryVoucherDetail.setRowTotal(inventoryVoucherDetailDTO.getRowTotal());
					inventoryVoucherDetail.setSellingRate(inventoryVoucherDetailDTO.getSellingRate());
					inventoryVoucherDetail.setSize(inventoryVoucherDetailDTO.getSize());
					if (inventoryVoucherDetailDTO.getSourceStockLocationPid() != null)
						inventoryVoucherDetail.setSourceStockLocation(stockLocationRepository
								.findOneByPid(inventoryVoucherDetailDTO.getSourceStockLocationPid()).get());
					inventoryVoucherDetail.setTaxAmount(inventoryVoucherDetailDTO.getTaxAmount());
					inventoryVoucherDetail.setTaxPercentage(inventoryVoucherDetailDTO.getTaxPercentage());
					inventoryVoucherDetail.setThickness(inventoryVoucherDetailDTO.getThickness());
					inventoryVoucherDetail.setWidth(inventoryVoucherDetailDTO.getWidth());

					// check batch
					List<InventoryVoucherBatchDetail> inventoryVoucherBatchDetails = new ArrayList<>();
					if (inventoryVoucherDetail.getInventoryVoucherBatchDetails().size() > 0) {
						for (InventoryVoucherBatchDetail inventoryVoucherBatchDetail : inventoryVoucherDetail
								.getInventoryVoucherBatchDetails()) {
							for (InventoryVoucherBatchDetailDTO inventoryVoucherBatchDetailDTO : inventoryVoucherDetailDTO
									.getInventoryVoucherBatchDetails()) {
								if (inventoryVoucherBatchDetail.getBatchNumber()
										.equals(inventoryVoucherBatchDetailDTO.getBatchNumber())
										&& inventoryVoucherBatchDetail.getStockLocation().getPid()
												.equals(inventoryVoucherBatchDetailDTO.getStockLocationPid())) {
									inventoryVoucherBatchDetail
											.setQuantity(inventoryVoucherBatchDetailDTO.getQuantity());
									inventoryVoucherBatchDetails.add(inventoryVoucherBatchDetail);
									break;
								}
							}
						}
					}
					// add new batches
					inventoryVoucherBatchDetails.addAll(findNewInventoryVoucherBatchDetails(
							inventoryVoucherBatchDetails, inventoryVoucherDetailDTO.getInventoryVoucherBatchDetails(),
							inventoryVoucherHeader.getCompany(), inventoryVoucherDetail.getProduct()));
					inventoryVoucherDetail.setInventoryVoucherBatchDetails(inventoryVoucherBatchDetails);

					inventoryVoucherDetails.add(inventoryVoucherDetail);
					break;
				}
			}
		}
		// set new items
		inventoryVoucherDetails.addAll(findNewInventoryVoucherDetails(inventoryVoucherDetails,
				inventoryVoucherDTO.getInventoryVoucherDetails(), inventoryVoucherHeader.getCompany()));
		inventoryVoucherHeader.setInventoryVoucherDetails(inventoryVoucherDetails);

		// update inventory Voucher Header
		inventoryVoucherHeaderRepository.save(inventoryVoucherHeader);

		TaskSubmissionResponse submissionResponse = createResponseObject(
				inventoryVoucherHeader.getExecutiveTaskExecution());
		return submissionResponse;
	}

	private List<InventoryVoucherBatchDetail> findNewInventoryVoucherBatchDetails(
			List<InventoryVoucherBatchDetail> inventoryVoucherBatchDetails,
			List<InventoryVoucherBatchDetailDTO> inventoryVoucherBatchDetailDTOs, Company company,
			ProductProfile productProfile) {
		List<InventoryVoucherBatchDetail> newInventoryVoucherBatchDetail = new ArrayList<>();
		for (InventoryVoucherBatchDetailDTO inventoryVoucherBatchDetailDTO : inventoryVoucherBatchDetailDTOs) {
			boolean isNew = true;
			for (InventoryVoucherBatchDetail inventoryVoucherBatchDetail : inventoryVoucherBatchDetails) {
				if (inventoryVoucherBatchDetail.getBatchNumber().equals(inventoryVoucherBatchDetailDTO.getBatchNumber())
						&& inventoryVoucherBatchDetail.getStockLocation().getPid()
								.equals(inventoryVoucherBatchDetailDTO.getStockLocationPid())) {
					isNew = false;
					break;
				}
			}
			if (isNew) {
				StockLocation stockLocation = null;
				if (inventoryVoucherBatchDetailDTO.getStockLocationPid() != null
						&& !inventoryVoucherBatchDetailDTO.getStockLocationPid().isEmpty())
					stockLocation = stockLocationRepository
							.findOneByPid(inventoryVoucherBatchDetailDTO.getStockLocationPid()).get();
				newInventoryVoucherBatchDetail.add(new InventoryVoucherBatchDetail(productProfile,
						inventoryVoucherBatchDetailDTO.getBatchNumber(), inventoryVoucherBatchDetailDTO.getBatchDate(),
						inventoryVoucherBatchDetailDTO.getRemarks(), inventoryVoucherBatchDetailDTO.getQuantity(),
						company, stockLocation));
			}
		}
		return newInventoryVoucherBatchDetail;
	}

	/**
	 * @param inventoryVoucherDetails
	 * @param inventoryVoucherDetailDTOs
	 * @return
	 */
	private List<InventoryVoucherDetail> findNewInventoryVoucherDetails(
			List<InventoryVoucherDetail> inventoryVoucherDetails,
			List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs, Company company) {
		List<InventoryVoucherDetail> newInventoryVoucherDetails = new ArrayList<>();
		for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherDetailDTOs) {
			boolean isNew = true;
			for (InventoryVoucherDetail inventoryVoucherDetail : inventoryVoucherDetails) {
				if (inventoryVoucherDetail.getProduct().getPid().equals(inventoryVoucherDetailDTO.getProductPid())) {
					isNew = false;
					break;
				}
			}
			if (isNew) {
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

				ProductProfile productProfile = productProfileRepository
						.findOneByPid(inventoryVoucherDetailDTO.getProductPid()).get();

				// Inventory Voucher Batch Details
				List<InventoryVoucherBatchDetail> inventoryVoucherBatchDetails = new ArrayList<>();
				if (inventoryVoucherDetailDTO.getInventoryVoucherBatchDetails() != null
						&& inventoryVoucherDetailDTO.getInventoryVoucherBatchDetails().size() > 0) {
					inventoryVoucherDetailDTO.getInventoryVoucherBatchDetails().forEach(inventoryVoucherBatchDetail -> {
						StockLocation stockLocation = null;
						if (inventoryVoucherBatchDetail.getStockLocationPid() != null
								&& !inventoryVoucherBatchDetail.getStockLocationPid().isEmpty())
							stockLocation = stockLocationRepository
									.findOneByPid(inventoryVoucherBatchDetail.getStockLocationPid()).get();
						inventoryVoucherBatchDetails.add(new InventoryVoucherBatchDetail(productProfile,
								inventoryVoucherBatchDetail.getBatchNumber(),
								inventoryVoucherBatchDetail.getBatchDate(), inventoryVoucherBatchDetail.getRemarks(),
								inventoryVoucherBatchDetail.getQuantity(), company, stockLocation));
					});
				}
				newInventoryVoucherDetails.add(new InventoryVoucherDetail(productProfile,
						inventoryVoucherDetailDTO.getQuantity(), inventoryVoucherDetailDTO.getFreeQuantity(),
						inventoryVoucherDetailDTO.getSellingRate(), inventoryVoucherDetailDTO.getMrp(),
						inventoryVoucherDetailDTO.getPurchaseRate(), inventoryVoucherDetailDTO.getTaxPercentage(),
						inventoryVoucherDetailDTO.getDiscountPercentage(), inventoryVoucherDetailDTO.getBatchNumber(),
						inventoryVoucherDetailDTO.getBatchDate(), inventoryVoucherDetailDTO.getRowTotal(),
						inventoryVoucherDetailDTO.getDiscountAmount(), inventoryVoucherDetailDTO.getTaxAmount(),
						inventoryVoucherDetailDTO.getLength(), inventoryVoucherDetailDTO.getWidth(),
						inventoryVoucherDetailDTO.getThickness(), inventoryVoucherDetailDTO.getSize(),
						inventoryVoucherDetailDTO.getColor(), inventoryVoucherDetailDTO.getItemtype(),
						sourceStockLocation, destinationStockLocation, referenceInventoryVoucherHeader,
						referenceInventoryVoucherDetail, inventoryVoucherDetailDTO.getRemarks(),
						inventoryVoucherBatchDetails));
			}
		}
		return newInventoryVoucherDetails;
	}

	@Override
	public TaskSubmissionResponse updateAccountingVoucher(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = executiveTaskSubmissionDTO.getAccountingVouchers()
				.get(0);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_165" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountingVoucherHeader accountingVoucherHeader = accountingVoucherHeaderRepository
				.findOneByPid(accountingVoucherHeaderDTO.getPid()).get();
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

		// save history
		AccountingVoucherHeaderHistory accountingVoucherHeaderHistory = new AccountingVoucherHeaderHistory(
				accountingVoucherHeader);
		accountingVoucherHeaderHistoryRepository.save(accountingVoucherHeaderHistory);

		// set header values
		accountingVoucherHeader.setTotalAmount(accountingVoucherHeaderDTO.getTotalAmount());
		accountingVoucherHeader.setOutstandingAmount(accountingVoucherHeaderDTO.getOutstandingAmount());
		accountingVoucherHeader.setRemarks(accountingVoucherHeaderDTO.getRemarks());

		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		accountingVoucherHeader.setUpdatedBy(user);
		accountingVoucherHeader.setUpdatedDate(LocalDateTime.now());

		List<AccountingVoucherDetail> accountingVoucherDetails = new ArrayList<>();

		// set detail values
		for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherHeader.getAccountingVoucherDetails()) {
			for (AccountingVoucherDetailDTO accountingVoucherDetailDTO : accountingVoucherHeaderDTO
					.getAccountingVoucherDetails()) {
				if (accountingVoucherDetail.getVoucherNumber().equals(accountingVoucherDetailDTO.getVoucherNumber())) {
					accountingVoucherDetail.setAmount(accountingVoucherDetailDTO.getAmount());
					accountingVoucherDetail.setBy(
							accountProfileRepository.findOneByPid(accountingVoucherDetailDTO.getByAccountPid()).get());
					accountingVoucherDetail.setTo(
							accountProfileRepository.findOneByPid(accountingVoucherDetailDTO.getToAccountPid()).get());
					accountingVoucherDetail.setInstrumentDate(accountingVoucherDetailDTO.getInstrumentDate());
					accountingVoucherDetail.setInstrumentNumber(accountingVoucherDetailDTO.getInstrumentNumber());
					accountingVoucherDetail.setMode(accountingVoucherDetailDTO.getMode());
					accountingVoucherDetail.setReferenceNumber(accountingVoucherDetailDTO.getReferenceNumber());
					accountingVoucherDetail.setRemarks(accountingVoucherDetailDTO.getRemarks());
					accountingVoucherDetail.setVoucherDate(accountingVoucherDetailDTO.getVoucherDate());
					accountingVoucherDetail.setVoucherNumber(accountingVoucherDetailDTO.getVoucherNumber());
					// set bank
					if (accountingVoucherDetailDTO.getBankPid() != null) {
						Bank bank = bankRepository.findOneByPid(accountingVoucherDetailDTO.getBankPid()).get();
						accountingVoucherDetail.setBank(bank);
					} else {
						accountingVoucherDetail.setBankName(accountingVoucherDetailDTO.getBankName());
					}
					accountingVoucherDetails.add(accountingVoucherDetail);
					break;
				}
			}
		}

		// add new items
		accountingVoucherDetails.addAll(findNewAccountingVoucherDetails(accountingVoucherDetails,
				accountingVoucherHeaderDTO.getAccountingVoucherDetails()));
		accountingVoucherHeader.setAccountingVoucherDetails(accountingVoucherDetails);

		// update accounting Voucher
		accountingVoucherHeaderRepository.save(accountingVoucherHeader);
		TaskSubmissionResponse submissionResponse = createResponseObject(
				accountingVoucherHeader.getExecutiveTaskExecution());
		return submissionResponse;
	}

	private List<AccountingVoucherDetail> findNewAccountingVoucherDetails(
			List<AccountingVoucherDetail> accountingVoucherDetails,
			List<AccountingVoucherDetailDTO> accountingVoucherDetailDTOs) {
		List<AccountingVoucherDetail> newAccountingVoucherDetails = new ArrayList<>();
		for (AccountingVoucherDetailDTO accountingVoucherDetailDTO : accountingVoucherDetailDTOs) {
			boolean isNew = true;
			for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherDetails) {
				if (accountingVoucherDetail.getVoucherNumber().equals(accountingVoucherDetailDTO.getVoucherNumber())) {
					isNew = false;
					break;
				}
			}
			if (isNew) {
				AccountingVoucherDetail accountingVoucherDetail = new AccountingVoucherDetail();
				accountingVoucherDetail.setAmount(accountingVoucherDetailDTO.getAmount());
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				accountingVoucherDetail.setBy(
						accountProfileRepository.findOneByPid(accountingVoucherDetailDTO.getByAccountPid()).get());
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

				accountingVoucherDetail.setTo(
						accountProfileRepository.findOneByPid(accountingVoucherDetailDTO.getToAccountPid()).get());
				accountingVoucherDetail.setInstrumentDate(accountingVoucherDetailDTO.getInstrumentDate());
				accountingVoucherDetail.setInstrumentNumber(accountingVoucherDetailDTO.getInstrumentNumber());
				accountingVoucherDetail.setMode(accountingVoucherDetailDTO.getMode());
				accountingVoucherDetail.setReferenceNumber(accountingVoucherDetailDTO.getReferenceNumber());
				accountingVoucherDetail.setRemarks(accountingVoucherDetailDTO.getRemarks());
				accountingVoucherDetail.setVoucherDate(accountingVoucherDetailDTO.getVoucherDate());
				accountingVoucherDetail.setVoucherNumber(accountingVoucherDetailDTO.getVoucherNumber());
				// set bank
				if (accountingVoucherDetailDTO.getBankPid() != null) {
					Bank bank = bankRepository.findOneByPid(accountingVoucherDetailDTO.getBankPid()).get();
					accountingVoucherDetail.setBank(bank);
				} else {
					accountingVoucherDetail.setBankName(accountingVoucherDetailDTO.getBankName());
				}
				newAccountingVoucherDetails.add(accountingVoucherDetail);
			}
		}
		return newAccountingVoucherDetails;
	}

	@Override
	public TaskSubmissionResponse updateDynamicDocument(ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {

		DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = executiveTaskSubmissionDTO.getDynamicDocuments().get(0);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_146" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentHeaderDTO.getPid()).get();
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

		// save history
		DynamicDocumentHeaderHistory dynamicDocumentHeaderHistory = new DynamicDocumentHeaderHistory(
				dynamicDocumentHeader);
		documentHeaderHistoryRepository.save(dynamicDocumentHeaderHistory);

		// set form element values
		FilledFormDTO filledFormDTO = dynamicDocumentHeaderDTO.getFilledForms().get(0);
		List<FilledFormDetail> filledFormDetails = new ArrayList<>();
		FilledForm updatedFilledForm = null;
		for (FilledForm filledForm : dynamicDocumentHeader.getFilledForms()) {
			if (filledForm.getPid().equals(filledFormDTO.getPid())) {
				updatedFilledForm = filledForm;
				for (FilledFormDetail filledFormDetail : updatedFilledForm.getFilledFormDetails()) {
					for (FilledFormDetailDTO filledFormDetailDTO : filledFormDTO.getFilledFormDetails()) {
						if (filledFormDetail.getFormElement().getPid()
								.equals(filledFormDetailDTO.getFormElementPid())) {
							filledFormDetail.setValue(filledFormDetailDTO.getValue());
							filledFormDetails.add(filledFormDetail);
						}
					}
				}
				break;
			}
		}
		// set new form elements
		filledFormDetails.addAll(findNewFormElements(filledFormDetails, filledFormDTO.getFilledFormDetails()));
		updatedFilledForm.setFilledFormDetails(filledFormDetails);

		// update filled form
		filledFormRepository.save(updatedFilledForm);

		TaskSubmissionResponse submissionResponse = createResponseObject(
				dynamicDocumentHeader.getExecutiveTaskExecution());
		return submissionResponse;
	}

	/**
	 * update from web UI
	 * 
	 * @param dynamicDocumentHeaderDTO
	 * @return TaskSubmissionResponse
	 */
	@Override
	public TaskSubmissionResponse updateDynamicDocument(DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_146" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentHeaderDTO.getPid()).get();
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

		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		dynamicDocumentHeader.setUpdatedBy(user);
		dynamicDocumentHeader.setUpdatedDate(LocalDateTime.now());
		dynamicDocumentHeader = dynamicDocumentHeaderRepository.save(dynamicDocumentHeader);

		// save history
		DynamicDocumentHeaderHistory dynamicDocumentHeaderHistory = new DynamicDocumentHeaderHistory(
				dynamicDocumentHeader);
		documentHeaderHistoryRepository.save(dynamicDocumentHeaderHistory);

		// update forms
		List<FilledForm> filledForms = new ArrayList<>();
		for (FilledForm filledForm : dynamicDocumentHeader.getFilledForms()) {
			for (FilledFormDTO filledFormDTO : dynamicDocumentHeaderDTO.getFilledForms()) {
				if (filledForm.getPid().equals(filledFormDTO.getPid())) {
					List<FilledFormDetail> filledFormDetails = new ArrayList<>();
					if (filledForm.getForm() != null && filledForm.getForm().getMultipleRecord()) {
						List<FilledFormDetail> fFormDetails = filledForm.getFilledFormDetails();
						List<FilledFormDetailDTO> fFormDetailDtos = filledFormDTO.getFilledFormDetails();
						for (FilledFormDetailDTO filledFormDetailDTO : fFormDetailDtos) {
							Optional<FilledFormDetail> optionalFFDetail = fFormDetails.stream()
									.filter(ffd -> ffd.getId() == filledFormDetailDTO.getId()).findAny();
							if (optionalFFDetail.isPresent()) {
								FilledFormDetail filledFormDetail = optionalFFDetail.get();
								filledFormDetail.setValue(filledFormDetailDTO.getValue());
								filledFormDetails.add(filledFormDetail);
							} else {
								// create new
								FilledFormDetail filledFormDetail = new FilledFormDetail();
								filledFormDetail.setFilledForm(filledForm);
								filledFormDetail.setValue(filledFormDetailDTO.getValue());
								filledFormDetail.setFormElement(formElementRepository
										.findOneByPid(filledFormDetailDTO.getFormElementPid()).get());
								filledFormDetails.add(filledFormDetail);
							}
						}
						// Delete others
						for (FilledFormDetail filledFormDetail : fFormDetails) {
							Optional<FilledFormDetailDTO> optionalFFDetailDto = fFormDetailDtos.stream()
									.filter(ffdDto -> ffdDto.getId() == filledFormDetail.getId()).findAny();
							if (!optionalFFDetailDto.isPresent()) {
								DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
								DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
								String id1 = "FFD_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_"
										+ LocalDateTime.now();
								String description1 = "delete by filled formId";
								LocalDateTime startLCTime1 = LocalDateTime.now();
								String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
								String startDate1 = startLCTime1.format(DATE_FORMAT1);
								logger.info(
										id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
								filledFormDetailRepository.deleteByFilledFormId(filledFormDetail.getId());
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
								logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1
										+ ",END," + flag1 + "," + description1);

							}
						}
					} else {
						for (FilledFormDetailDTO filledFormDetailDTO : filledFormDTO.getFilledFormDetails()) {
							for (FilledFormDetail filledFormDetail : filledForm.getFilledFormDetails()) {
								if (filledFormDetail.getFormElement().getPid()
										.equals(filledFormDetailDTO.getFormElementPid())) {
									// create new object
									if (filledFormDetail.getFormElement().getFormElementType().getName()
											.equals("checkBox")) {
										FilledFormDetail tempFFD = new FilledFormDetail();
										tempFFD.setFilledForm(filledFormDetail.getFilledForm());
										tempFFD.setFormElement(filledFormDetail.getFormElement());
										tempFFD.setValue(filledFormDetailDTO.getValue());
										filledFormDetails.add(tempFFD);
									} else {
										filledFormDetail.setValue(filledFormDetailDTO.getValue());
										filledFormDetails.add(filledFormDetail);
									}
									break;
								}
							}
						}
						// set new form elements
						filledFormDetails
								.addAll(findNewFormElements(filledFormDetails, filledFormDTO.getFilledFormDetails()));
					}
					filledForm.setFilledFormDetails(filledFormDetails);
					filledForms.add(filledForm);
				}
			}
		}
		// update filled form
		filledFormRepository.save(filledForms);

		TaskSubmissionResponse submissionResponse = createResponseObject(
				dynamicDocumentHeader.getExecutiveTaskExecution());
		return submissionResponse;
	}

	/**
	 * @param filledFormDetails
	 * @param filledFormDetailDTOs
	 * @return
	 */
	private List<FilledFormDetail> findNewFormElements(List<FilledFormDetail> filledFormDetails,
			List<FilledFormDetailDTO> filledFormDetailDTOs) {
		List<FilledFormDetail> newFilledFormDetails = new ArrayList<>();
		for (FilledFormDetailDTO filledFormDetailDTO : filledFormDetailDTOs) {
			boolean isNew = true;
			for (FilledFormDetail filledFormDetail : filledFormDetails) {
				if (filledFormDetail.getFormElement().getPid().equals(filledFormDetailDTO.getFormElementPid())) {
					isNew = false;
					break;
				}
			}
			if (isNew) {
				FilledFormDetail filledFormDetail = new FilledFormDetail();
				filledFormDetail.setValue(filledFormDetailDTO.getValue());
				filledFormDetail.setFormElement(
						formElementRepository.findOneByPid(filledFormDetailDTO.getFormElementPid()).get());
				filledFormDetails.add(filledFormDetail);
				newFilledFormDetails.add(filledFormDetail);
			}
		}
		return newFilledFormDetails;
	}

	private void updateExecutiveTaskPlanStatus(ExecutiveTaskExecution executiveTaskExecution) {
		// update task plan status to completed
		if (executiveTaskExecution.getExecutiveTaskPlan() != null) {
			Optional<ExecutiveTaskPlan> optionalExecutiveTaskPlan = executiveTaskPlanRepository
					.findOneByPid(executiveTaskExecution.getExecutiveTaskPlan().getPid());
			if (optionalExecutiveTaskPlan.isPresent()) {
				ExecutiveTaskPlan executiveTaskPlan = optionalExecutiveTaskPlan.get();
				executiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.COMPLETED);
				executiveTaskPlanRepository.save(executiveTaskPlan);
			}
		} else {
			ExecutiveTaskPlan executiveTaskPlan = executiveTaskPlanRepository
					.findTop1ByUserIdAndActivityIdAndAccountProfileIdAndTaskPlanStatusAndPlannedDateBetweenOrderByIdDesc(
							executiveTaskExecution.getUser().getId(), executiveTaskExecution.getActivity().getId(),
							executiveTaskExecution.getAccountProfile().getId(), TaskPlanStatus.PENDING,
							LocalDate.now().atTime(0, 0), LocalDate.now().atTime(23, 59));
			if (executiveTaskPlan != null) {
				executiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.COMPLETED);
				executiveTaskPlanRepository.save(executiveTaskPlan);
			}
		}
	}

	/**
	 * @param executiveTaskExecution
	 * @return
	 */
	private TaskSubmissionResponse createResponseObject(ExecutiveTaskExecution executiveTaskExecution) {
		Long userId = executiveTaskExecution.getUser().getId();
		Long activityId = executiveTaskExecution.getActivity().getId();
		LocalDate currentDate = LocalDate.now();
		LocalDate monthStartDate = currentDate.withDayOfMonth(1);
		LocalDate monthEndDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

		TaskSubmissionResponse submissionResponse = new TaskSubmissionResponse();
		submissionResponse.setExecutiveTaskExecutionPid(executiveTaskExecution.getPid());
		submissionResponse.setActivityId(activityId);
		submissionResponse.setUserId(userId);
		// achieved and target setting
		List<ActivityUserTarget> activityUserTargets = activityUserTargetRepository
				.findByUserIsCurrentUserAndDateBetween(monthStartDate, monthEndDate);
		// one month has only one user-target
		if (!activityUserTargets.isEmpty()) {
			ActivityUserTarget activityUserTarget = activityUserTargets.get(0);
			submissionResponse.setTarget(activityUserTarget.getTargetNumber());
			submissionResponse.setStartDate(activityUserTarget.getStartDate());
			// set achieved
			Long achieved = executiveTaskExecutionRepository
					.countByUserIdAndActivityIdAndActivityStatusNotAndDateBetween(userId, activityId,
							ActivityStatus.REJECTED, monthStartDate.atTime(0, 0), monthEndDate.atTime(23, 59));
			submissionResponse.setAchieved(achieved);
			submissionResponse.setEndDate(activityUserTarget.getEndDate());
		}
		return submissionResponse;
	}

	/* ========================== update section ========================== */

	@Override
	public ExecutiveTaskSubmissionTransactionWrapper updationExecutiveTaskExecution(
			ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		log.info("============================ order processing save or update started : "
				+ SecurityUtils.getCurrentUserLogin() + "============================");

		ExecutiveTaskExecution executiveTaskExecution = new ExecutiveTaskExecution();
		List<InventoryVoucherHeader> inventoryVouchers = new ArrayList<>();
		List<AccountingVoucherHeader> accountingVouchers = new ArrayList<>();
		List<DynamicDocumentHeader> dynamicDocuments = new ArrayList<>();

		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (opUser.isPresent()) {
			User user = opUser.get();

			Company company = user.getCompany();
			EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUser(user);

			Optional<ExecutiveTaskExecution> opExecutiveTaskExecution = executiveTaskExecutionRepository
					.findByCompanyIdAndClientTransactionKey(
							executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().getClientTransactionKey());

			if (opExecutiveTaskExecution.isPresent()) {
				// update Executive Task Execution
				executiveTaskExecution = updateExecutiveTaskExecution(
						executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO(), opExecutiveTaskExecution.get());
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

				String id = "INV_QUERY_121" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get all by executive task execution Pid ";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				List<InventoryVoucherHeader> oldInventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByExecutiveTaskExecutionPid(executiveTaskExecution.getPid());
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
						.findAllByExecutiveTaskExecutionPid(executiveTaskExecution.getPid());
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
						.findAllByExecutiveTaskExecutionPid(executiveTaskExecution.getPid());
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
				if (!executiveTaskSubmissionDTO.getInventoryVouchers().isEmpty()) {
					// update Inventory Vouchers
					inventoryVouchers = updateInventoryVouchers(company, user, employeeProfile, executiveTaskExecution,
							executiveTaskSubmissionDTO.getInventoryVouchers(), oldInventoryVoucherHeaders);
				}

				if (!executiveTaskSubmissionDTO.getAccountingVouchers().isEmpty()) {
					// update Accounting Vouchers
					accountingVouchers = updateAccountingVouchers(company, user, employeeProfile,
							executiveTaskExecution, executiveTaskSubmissionDTO.getAccountingVouchers(),
							oldAccountingVoucherHeaders);
				}
				if (!executiveTaskSubmissionDTO.getDynamicDocuments().isEmpty()) {
					// update Dynamic Forms
					dynamicDocuments = updateDynamicDocuments(company, user, employeeProfile, executiveTaskExecution,
							executiveTaskSubmissionDTO.getDynamicDocuments(), oldDynamicDocumentHeaders);
				}

			} else {
				// save Executive Task Execution
				executiveTaskExecution = saveExecutiveTaskExecution(company, user,
						executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO());

				// save Inventory Vouchers
				inventoryVouchers = saveInventoryVouchers(company, user, employeeProfile, executiveTaskExecution,
						executiveTaskSubmissionDTO.getInventoryVouchers());

				// save Accounting Vouchers
				accountingVouchers = saveAccountingVouchers(company, user, employeeProfile, executiveTaskExecution,
						executiveTaskSubmissionDTO.getAccountingVouchers());

				// save Dynamic Forms
				dynamicDocuments = saveDynamicForms(company, user, employeeProfile, executiveTaskExecution,
						executiveTaskSubmissionDTO.getDynamicDocuments());
			}

		}
		// update executive task plan status
		updateExecutiveTaskPlanStatus(executiveTaskExecution);

		TaskSubmissionResponse submissionResponse = createResponseObject(executiveTaskExecution);
		log.info("============================ order processing  completed : " + SecurityUtils.getCurrentUserLogin()
				+ "============================");
		return new ExecutiveTaskSubmissionTransactionWrapper(submissionResponse, executiveTaskExecution,
				inventoryVouchers, accountingVouchers, dynamicDocuments,
				executiveTaskSubmissionDTO.getUpdateDashboard());
	}

	private ExecutiveTaskExecution updateExecutiveTaskExecution(ExecutiveTaskExecutionDTO executiveTaskExecutionDTO,
			ExecutiveTaskExecution oldExecutiveTaskExecution) {
		ExecutiveTaskExecution executiveTaskExecution = oldExecutiveTaskExecution;
		executiveTaskExecution.setDate(executiveTaskExecutionDTO.getDate());
		executiveTaskExecution.setRemarks(executiveTaskExecutionDTO.getRemarks());
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
		executiveTaskExecution = executiveTaskExecutionRepository.save(executiveTaskExecution);
		return executiveTaskExecution;
	}

	private List<InventoryVoucherHeader> updateInventoryVouchers(Company company, User user,
			EmployeeProfile employeeProfile, ExecutiveTaskExecution executiveTaskExecution,
			List<InventoryVoucherHeaderDTO> newInventoryVoucherDTOs,
			List<InventoryVoucherHeader> oldInventoryVoucherHeaders) {
		List<InventoryVoucherHeader> inventoryVouchers = new ArrayList<>();

		List<InventoryVoucherHeaderDTO> newInventoryVoucherHeaderDTOs = new ArrayList<>();

		for (InventoryVoucherHeaderDTO inventoryVoucherDTO : newInventoryVoucherDTOs) {
			String documentNumberLocal = inventoryVoucherDTO.getDocumentNumberLocal();
			log.debug("----------Inventory Voucher Document Number:- " + documentNumberLocal);
			Optional<InventoryVoucherHeader> optionalIVH = oldInventoryVoucherHeaders.stream().filter(
					pc -> pc.getDocumentNumberLocal().equalsIgnoreCase(inventoryVoucherDTO.getDocumentNumberLocal()))
					.findAny();
			if (optionalIVH.isPresent()) {
				InventoryVoucherHeader inventoryVoucherHeader = optionalIVH.get();

				inventoryVoucherHeader.setDocumentDate(inventoryVoucherDTO.getDocumentDate());
				inventoryVoucherHeader.setDocumentTotal(inventoryVoucherDTO.getDocumentTotal());
				inventoryVoucherHeader.setDocumentVolume(inventoryVoucherDTO.getDocumentVolume());
				inventoryVoucherHeader.setDocDiscountAmount(inventoryVoucherDTO.getDocDiscountAmount());
				inventoryVoucherHeader.setDocDiscountPercentage(inventoryVoucherDTO.getDocDiscountPercentage());

				// set price level
				if (inventoryVoucherDTO.getPriceLevelPid() != null) {
					Optional<PriceLevel> priceLevel = priceLevelRepository
							.findOneByPid(inventoryVoucherDTO.getPriceLevelPid());
					if (priceLevel.isPresent())
						inventoryVoucherHeader.setPriceLevel(priceLevel.get());
				}
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				Optional<AccountProfile> receIvAcc = accountProfileRepository
						.findOneByPid(inventoryVoucherDTO.getReceiverAccountPid());
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

				if (receIvAcc.isPresent())
					inventoryVoucherHeader.setReceiverAccount(receIvAcc.get());
				if (inventoryVoucherDTO.getSupplierAccountPid() != null) {
					Optional<AccountProfile> suplIvAcc = accountProfileRepository
							.findOneByPid(inventoryVoucherDTO.getSupplierAccountPid());
					if (suplIvAcc.isPresent())
						inventoryVoucherHeader.setSupplierAccount(suplIvAcc.get());
				}
				// set voucher details
				List<InventoryVoucherDetail> inventoryVoucherDetails = new ArrayList<InventoryVoucherDetail>();
				inventoryVoucherDTO.getInventoryVoucherDetails().forEach(inventoryVoucherDetailDTO -> {

					// find source and destination Stock Location
					StockLocation sourceStockLocation = null;
					StockLocation destinationStockLocation = null;
					if (inventoryVoucherDetailDTO.getSourceStockLocationPid() != null) {
						Optional<StockLocation> opSourceStockLocation = stockLocationRepository
								.findOneByPid(inventoryVoucherDetailDTO.getSourceStockLocationPid());
						if (opSourceStockLocation.isPresent())
							sourceStockLocation = opSourceStockLocation.get();
					}
					if (inventoryVoucherDetailDTO.getDestinationStockLocationPid() != null) {
						Optional<StockLocation> opdestinationStockLocation = stockLocationRepository
								.findOneByPid(inventoryVoucherDetailDTO.getDestinationStockLocationPid());
						if (opdestinationStockLocation.isPresent())
							stockLocationRepository
									.findOneByPid(inventoryVoucherDetailDTO.getDestinationStockLocationPid());
					}
					// find referenceInventory Voucher header and detail
					InventoryVoucherHeader referenceInventoryVoucherHeader = null;
					InventoryVoucherDetail referenceInventoryVoucherDetail = null;

					Optional<ProductProfile> opProductProfile = productProfileRepository
							.findOneByPid(inventoryVoucherDetailDTO.getProductPid());

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

			} else {
				newInventoryVoucherHeaderDTOs.add(inventoryVoucherDTO);
			}
		}
		List<InventoryVoucherHeader> result = inventoryVoucherHeaderRepository.save(inventoryVouchers);

		if (!newInventoryVoucherHeaderDTOs.isEmpty()) {
			List<InventoryVoucherHeader> newInventoryList = saveInventoryVouchers(company, user, employeeProfile,
					executiveTaskExecution, newInventoryVoucherHeaderDTOs);
			result.addAll(newInventoryList);
		}
		return result;
	}

	private List<AccountingVoucherHeader> updateAccountingVouchers(Company company, User user,
			EmployeeProfile employeeProfile, ExecutiveTaskExecution executiveTaskExecution,
			List<AccountingVoucherHeaderDTO> newAccountingVouchers,
			List<AccountingVoucherHeader> oldAccountingVoucherHeaders) {
		List<AccountingVoucherHeaderDTO> headerDTOs = new ArrayList<>();
		List<AccountingVoucherHeader> accountingVoucherHeaders = new ArrayList<>();
		for (AccountingVoucherHeaderDTO accountingVoucherDTO : newAccountingVouchers) {

			String documentNumberLocal = accountingVoucherDTO.getDocumentNumberLocal();
			log.debug("----------Accounting Voucher Document Number:- " + documentNumberLocal);
			Optional<AccountingVoucherHeader> optionalAVH = oldAccountingVoucherHeaders.stream().filter(
					pc -> pc.getDocumentNumberLocal().equalsIgnoreCase(accountingVoucherDTO.getDocumentNumberLocal()))
					.findAny();
			if (optionalAVH.isPresent()) {

				AccountingVoucherHeader accountingVoucherHeader = optionalAVH.get();
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				Optional<AccountProfile> opAccountProfile = accountProfileRepository
						.findOneByPid(accountingVoucherDTO.getAccountProfilePid());
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

				if (opAccountProfile.isPresent()) {
					AccountProfile accountProfile = opAccountProfile.get();
					accountingVoucherHeader.setAccountProfile(accountProfile);
					accountingVoucherHeader.setDocumentDate(accountingVoucherDTO.getDocumentDate());
					accountingVoucherHeader.setOutstandingAmount(accountingVoucherDTO.getOutstandingAmount());
					accountingVoucherHeader.setRemarks(accountingVoucherDTO.getRemarks());
					accountingVoucherHeader.setTotalAmount(accountingVoucherDTO.getTotalAmount());

					// set voucher details
					List<AccountingVoucherDetail> accountingVoucherDetails = new ArrayList<>();
					List<AccountingVoucherDetailDTO> accountingVoucherDetailDTOs = accountingVoucherDTO
							.getAccountingVoucherDetails();
					List<ReceivablePayable> receivablePayables = new ArrayList<>();
					List<AccountProfile> accountProfiles = new ArrayList<>();
					for (AccountingVoucherDetailDTO accountingVoucherDetailDTO : accountingVoucherDetailDTOs) {
						AccountingVoucherDetail accountingVoucherDetail = new AccountingVoucherDetail();
						accountingVoucherDetail.setAmount(accountingVoucherDetailDTO.getAmount());

						Optional<AccountProfile> byAccountProfile = accountProfileRepository
								.findOneByPid(accountingVoucherDetailDTO.getByAccountPid());
						if (byAccountProfile.isPresent()) {
							accountingVoucherDetail.setBy(byAccountProfile.get());
						}
						Optional<AccountProfile> toAccountProfile = accountProfileRepository
								.findOneByPid(accountingVoucherDetailDTO.getToAccountPid());
						if (toAccountProfile.isPresent()) {
							accountingVoucherDetail.setTo(toAccountProfile.get());
						}
						accountingVoucherDetail.setInstrumentDate(accountingVoucherDetailDTO.getInstrumentDate());
						accountingVoucherDetail.setInstrumentNumber(accountingVoucherDetailDTO.getInstrumentNumber());
						accountingVoucherDetail.setMode(accountingVoucherDetailDTO.getMode());
						accountingVoucherDetail.setReferenceNumber(accountingVoucherDetailDTO.getReferenceNumber());
						accountingVoucherDetail.setRemarks(accountingVoucherDetailDTO.getRemarks());
						accountingVoucherDetail.setVoucherDate(accountingVoucherDetailDTO.getVoucherDate());
						accountingVoucherDetail.setVoucherNumber(accountingVoucherDetailDTO.getVoucherNumber());
						// set bank
						if (accountingVoucherDetailDTO.getBankPid() != null) {
							Optional<Bank> bank = bankRepository.findOneByPid(accountingVoucherDetailDTO.getBankPid());
							if (bank.isPresent())
								accountingVoucherDetail.setBank(bank.get());
						} else {
							accountingVoucherDetail.setBankName(accountingVoucherDetailDTO.getBankName());
						}
						// set Income Expense
						if (accountingVoucherDetailDTO.getIncomeExpenseHeadPid() != null) {
							Optional<IncomeExpenseHead> opIncomeExpenseHead = incomeExpenseHeadRepository
									.findOneByPid(accountingVoucherDetailDTO.getIncomeExpenseHeadPid());
							if (opIncomeExpenseHead.isPresent())
								accountingVoucherDetail.setIncomeExpenseHead(opIncomeExpenseHead.get());
						}
						List<AccountingVoucherAllocationDTO> accountingVoucherAllocationDTOs = accountingVoucherDetailDTO
								.getAccountingVoucherAllocations();
						if (accountingVoucherAllocationDTOs != null && accountingVoucherAllocationDTOs.size() > 0) {
							List<AccountingVoucherAllocation> accountingVoucherAllocations = new ArrayList<AccountingVoucherAllocation>();
							for (AccountingVoucherAllocationDTO accountingVoucherAllocationDTO : accountingVoucherAllocationDTOs) {
								AccountingVoucherAllocation accountingVoucherAllocation = new AccountingVoucherAllocation();
								accountingVoucherAllocation.setAmount(accountingVoucherAllocationDTO.getAmount());
								accountingVoucherAllocation.setMode(accountingVoucherAllocationDTO.getMode());
								accountingVoucherAllocation.setReferenceDocumentNumber(
										accountingVoucherAllocationDTO.getReferenceDocumentNumber());
								accountingVoucherAllocation
										.setReferenceNumber(accountingVoucherAllocationDTO.getReferenceNumber());
								accountingVoucherAllocation.setRemarks(accountingVoucherAllocationDTO.getRemarks());
								accountingVoucherAllocation
										.setVoucherNumber(accountingVoucherAllocationDTO.getVoucherNumber());
								accountingVoucherAllocation.setReceivablePayablePid(
										accountingVoucherAllocationDTO.getReceivablePayablePid());
								if (accountingVoucherAllocationDTO.getReferenceDocumentNumber() != null) {
									Optional<ReceivablePayable> receivablePayable = receivablePayableRepository
											.findOneByAccountProfilePidAndReferenceDocumentNumber(
													accountingVoucherHeader.getAccountProfile().getPid(),
													accountingVoucherAllocationDTO.getReferenceDocumentNumber());
									receivablePayable.ifPresent(rPayable -> {
										rPayable.setReferenceDocumentBalanceAmount(
												rPayable.getReferenceDocumentBalanceAmount()
														- accountingVoucherAllocationDTO.getAmount());

										accountProfile.setClosingBalance(accountProfile.getClosingBalance()
												- accountingVoucherAllocationDTO.getAmount());

										receivablePayables.add(rPayable);
										accountProfiles.add(accountProfile);
									});
								}
								accountingVoucherAllocations.add(accountingVoucherAllocation);
							}
							accountingVoucherDetail.setAccountingVoucherAllocations(accountingVoucherAllocations);
						}
						accountingVoucherDetails.add(accountingVoucherDetail);
					}
					if (receivablePayables.size() > 0) {
						receivablePayableRepository.save(receivablePayables);
						accountProfileRepository.save(accountProfile);
					}
					accountingVoucherHeader.setAccountingVoucherDetails(accountingVoucherDetails);
					accountingVoucherHeaders.add(accountingVoucherHeader);
				}
			} else {
				headerDTOs.add(accountingVoucherDTO);
			}
		}
		List<AccountingVoucherHeader> result = accountingVoucherHeaderRepository.save(accountingVoucherHeaders);
		if (!headerDTOs.isEmpty()) {
			List<AccountingVoucherHeader> newInventoryList = saveAccountingVouchers(company, user, employeeProfile,
					executiveTaskExecution, headerDTOs);
			result.addAll(newInventoryList);
		}
		return result;
	}

	private List<DynamicDocumentHeader> updateDynamicDocuments(Company company, User user,
			EmployeeProfile employeeProfile, ExecutiveTaskExecution executiveTaskExecution,
			List<DynamicDocumentHeaderDTO> newDynamicDocuments, List<DynamicDocumentHeader> oldDynamicDocumentHeaders) {
		List<DynamicDocumentHeaderDTO> documentHeaderDTOs = new ArrayList<>();
		List<DynamicDocumentHeader> dynamicDocumentHeaders = new ArrayList<>();
		for (DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO : newDynamicDocuments) {
			Optional<DynamicDocumentHeader> optionalDDH = oldDynamicDocumentHeaders.stream().filter(pc -> pc
					.getDocumentNumberLocal().equalsIgnoreCase(dynamicDocumentHeaderDTO.getDocumentNumberLocal()))
					.findAny();
			String documentNumberLocal = dynamicDocumentHeaderDTO.getDocumentNumberLocal();
			log.debug("----------Dynamic Document Number:- " + documentNumberLocal);
			if (optionalDDH.isPresent()) {
				DynamicDocumentHeader dynamicDocumentHeader = optionalDDH.get();
				dynamicDocumentHeader.setDocumentDate(dynamicDocumentHeaderDTO.getDocumentDate());
				// set voucher details
				List<FilledForm> filledForms = new ArrayList<FilledForm>();
				List<FilledFormDetail> datePickerFormElements = new ArrayList<>();
				List<FilledFormDetailDTO> filledFormDetailList = new ArrayList<>();
				dynamicDocumentHeaderDTO.getFilledForms().forEach(filledFormDTO -> {
					FilledForm filledForm = new FilledForm();
					// set pid
					filledForm.setPid(FilledFormService.PID_PREFIX + RandomUtil.generatePid());
					filledForm.setImageRefNo(filledFormDTO.getImageRefNo());
					filledForm.setForm(formRepository.findOneByPid(filledFormDTO.getFormPid()).get());
					// set voucher details
					List<FilledFormDetail> filledFormDetails = new ArrayList<FilledFormDetail>();
					List<FormElement> formElements = formFormElementRepository
							.findFormElementsByFormPid(filledFormDTO.getFormPid());
					for (FilledFormDetailDTO filledFormDetailDTO : filledFormDTO.getFilledFormDetails()) {
						FilledFormDetail filledFormDetail = new FilledFormDetail();
						FormElement formElement = formElementRepository
								.findOneByPid(filledFormDetailDTO.getFormElementPid()).get();
						filledFormDetail.setFormElement(formElement);
						filledFormDetail.setValue(filledFormDetailDTO.getValue());
						if (filledFormDetailDTO.getValue() == null || filledFormDetailDTO.getValue().isEmpty()) {
							filledFormDetail.setValue(formElement.getDefaultValue());
						}
						filledFormDetails.add(filledFormDetail);
						if (formElement.getFormElementType().getName().equals("datePicker")) {
							datePickerFormElements.add(filledFormDetail);
						}
						filledFormDetailList.add(filledFormDetailDTO);
					}
					// add default value exist user not appilcable elements
					for (FormElement formElement : formElements) {
						boolean isNotExist = true;
						for (FilledFormDetailDTO filledFormDetailDTO : filledFormDTO.getFilledFormDetails()) {
							if (filledFormDetailDTO.getFormElementPid().equals(formElement.getPid())) {
								isNotExist = false;
								break;
							}
						}
						if (isNotExist) {
							if (formElement.getDefaultValue() != null && !formElement.getDefaultValue().isEmpty()) {
								FilledFormDetail filledFormDetail = new FilledFormDetail();
								filledFormDetail.setFormElement(formElement);
								filledFormDetail.setValue(formElement.getDefaultValue());
								filledFormDetails.add(filledFormDetail);
							}
						}
					}
					filledForm.setFilledFormDetails(filledFormDetails);
					filledForms.add(filledForm);
				});
				dynamicDocumentHeader.setFilledForms(filledForms);
				dynamicDocumentHeaders.add(dynamicDocumentHeader);
			} else {
				documentHeaderDTOs.add(dynamicDocumentHeaderDTO);
			}
		}
		List<DynamicDocumentHeader> result = dynamicDocumentHeaderRepository.save(dynamicDocumentHeaders);
		if (!documentHeaderDTOs.isEmpty()) {
			List<DynamicDocumentHeader> newDnamicList = saveDynamicForms(company, user, employeeProfile,
					executiveTaskExecution, documentHeaderDTOs);
			result.addAll(newDnamicList);
		}
		return result;

	}
}
