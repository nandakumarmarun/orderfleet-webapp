package com.orderfleet.webapp.web.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.CompanyService;
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
 * Web controller for managing SalesValueReportUploadXls.
 *
 * @author Sarath
 * @since Jul 24, 2017
 *
 */

@Controller
@RequestMapping("/web")
public class SalesValueReportUploadXlsResource {

	private final Logger log = LoggerFactory.getLogger(SalesValueReportUploadXlsResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private CompanyService companyService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private ActivityService activityService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DocumentService documentService;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	/**
	 * GET /period : get all the company period.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the object of company
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/sales-value-report-upload-xls", method = RequestMethod.GET)
	public String getPriod(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of salesValuReportUploadXls");
		model.addAttribute("companys", companyService.findAllCompaniesByActivatedTrue());
		return "company/salesValueReportUploadXls";
	}

	@Timed
	@RequestMapping(value = "/sales-value-report-upload-xls/intakeReport", method = RequestMethod.POST)
	public ResponseEntity<Void> postIntakeReport(@RequestBody MultipartFile file) throws URISyntaxException {
		log.debug("Web request to get a page of salesValuReportUploadXls");
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_116" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by company and AccImport Status";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyAndAccountImportStatus(true);
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
		List<ProductProfile> productProfiles = productProfileRepository
				.findAllByCompanyIdAndActivatedOrDeactivatedProductProfileOrderByName(true);
		try {
			int rowNumber = 0;

			StringBuilder builder = new StringBuilder();

			Workbook workbook = WorkbookFactory.create(file.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				rowNumber = row.getRowNum();
				if (rowNumber > 0) {
					rowNumber++;
					if (row.getCell(1) != null) {

						boolean dateStatus = false;
						LocalDateTime localDateTime = LocalDateTime.now();
						List<ExecutiveTaskExecution> executions;

						String accName = row.getCell(row.getFirstCellNum()).getStringCellValue();
						String prodName = row.getCell(row.getFirstCellNum() + 2).getStringCellValue();
						double qty = row.getCell(row.getFirstCellNum() + 3).getNumericCellValue();

						Optional<AccountProfile> optionalAP = accountProfiles.stream()
								.filter(pc -> pc.getName().equals(accName)).findAny();
						Optional<ProductProfile> optionalPP = productProfiles.stream()
								.filter(pc -> pc.getName().equals(prodName)).findAny();

						if (!optionalAP.isPresent()) {
							builder.append("this account name not exist  :  " + accName+"\n");
						}

						if (!optionalPP.isPresent()) {
							builder.append("this product name not exist  :  " + prodName+"\n");
						}

						if (!optionalAP.isPresent() || !optionalPP.isPresent()) {
							log.debug("this account name not exist  :  " + accName);
							log.debug("this product name not exist  :  " + prodName);
						} else {
							AccountProfile accountProfile = optionalAP.get();
							ProductProfile productProfile = optionalPP.get();

							if (row.getCell(row.getFirstCellNum() + 1) != null) {
								if (row.getCell(row.getFirstCellNum() + 1).getCellTypeEnum() == CellType.NUMERIC) {
									if (DateUtil.isCellDateFormatted(row.getCell(row.getFirstCellNum() + 1))) {
										DateFormat df = new SimpleDateFormat("d-MMM-yyyy");
										Date d = row.getCell(row.getFirstCellNum() + 1).getDateCellValue();
										String buy_date = df.format(d);
										localDateTime = convertDateToLocalDate(buy_date);
										dateStatus = false;
									}
								}
							}

							if (row.getCell(row.getFirstCellNum() + 1) != null) {
								if (row.getCell(row.getFirstCellNum() + 1).getCellTypeEnum() == CellType.STRING) {
									localDateTime = LocalDateTime.of(2016, 04, 01, 00, 00);
									dateStatus = true;
								}
							}

							if (dateStatus) {

								LocalDateTime fromDate = LocalDateTime.of(2016, 04, 01, 00, 00);
								LocalDateTime toDate = LocalDateTime.of(2017, 03, 31, 23, 59);

								executions = executiveTaskExecutionRepository
										.findAllByCompanyIdAccountPidAndDateBetweenOrderByDateDesc(
												accountProfile.getPid(), fromDate, toDate);
								dateStatus = false;
							} else {

								LocalDate monthStartDate = localDateTime.toLocalDate().withDayOfMonth(1);
								LocalDate monthEndDate = localDateTime.toLocalDate()
										.withDayOfMonth(localDateTime.toLocalDate().lengthOfMonth());

								LocalDateTime fromDate = monthStartDate.atTime(0, 0);
								LocalDateTime toDate = monthEndDate.atTime(23, 59);

								executions = executiveTaskExecutionRepository
										.findAllByCompanyIdAccountPidAndDateBetweenOrderByDateDesc(
												accountProfile.getPid(), fromDate, toDate);
							}

							if (executions.isEmpty()) {
								executions = createTaskExecution(accountProfile, localDateTime);
							}

							if (!executions.isEmpty()) {
								createInventoryDetails(executions.get(0), productProfile, qty);
							}
						}

					}
				}
			}

			File file1 = new File("nameNotExist.txt");
			if (!file1.exists()) {
				file1.createNewFile();
			}
			FileWriter fw = new FileWriter(file1.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(builder.toString());
			bw.close();
			System.out.println("Done");

		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
		log.info("accounts saved successfully..................");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void createInventoryDetails(ExecutiveTaskExecution executiveTaskExecution, ProductProfile productProfile,
			double qty) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		String id = "INV_QUERY_121" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by executive task execution Pid ";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<InventoryVoucherHeader> inventoryVoucherHeader = inventoryVoucherHeaderRepository
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
		if (!inventoryVoucherHeader.isEmpty()) {
			InventoryVoucherHeader header = inventoryVoucherHeader.get(0);

			InventoryVoucherDetail detail = new InventoryVoucherDetail();

			detail.setProduct(productProfile);
			detail.setQuantity(qty);
			detail.setInventoryVoucherHeader(header);

			List<InventoryVoucherDetail> inventoryVoucherDetails = new ArrayList<>();
			inventoryVoucherDetails.add(detail);
			header.setInventoryVoucherDetails(inventoryVoucherDetails);
			header.setInventoryVoucherDetails(inventoryVoucherDetails);

			inventoryVoucherHeaderRepository.save(header);
		}
	}

	private List<ExecutiveTaskExecution> createTaskExecution(AccountProfile accountProfile, LocalDateTime docDate) {

		Optional<User> userDTO = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());

		Optional<ActivityDTO> activityDTO = activityService.findByName("Sales Data Transfer");
		ActivityDTO activity = new ActivityDTO();
		if (activityDTO.isPresent()) {
			activity = activityDTO.get();
		}
		if (!activityDTO.isPresent()) {
			activity = new ActivityDTO();
			activity.setName("Sales Data Transfer");
			activity.setActivated(true);
			activity.setHasDefaultAccount(false);
			activity.setDescription("used to send datas from tally");
			activity = activityService.save(activity);
		}

		Optional<DocumentDTO> documentDTO = documentService.findByName("sales");

		DocumentDTO document = new DocumentDTO();
		if (!documentDTO.isPresent()) {
			DocumentDTO dto = new DocumentDTO();
			dto.setName("sales");
			dto.setDocumentType(DocumentType.INVENTORY_VOUCHER);
			dto.setDocumentPrefix("sales");
			dto = documentService.save(dto);
			document = dto;
		} else {
			document = documentDTO.get();
		}

		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();
		ExecutiveTaskExecutionDTO eteDTO = new ExecutiveTaskExecutionDTO();

		eteDTO.setAccountProfileName(accountProfile.getName());
		eteDTO.setAccountProfilePid(accountProfile.getPid());
		eteDTO.setAccountTypeName(accountProfile.getAccountType().getName());
		eteDTO.setAccountTypePid(accountProfile.getAccountType().getPid());

		eteDTO.setActivityName(activity.getName());
		eteDTO.setActivityPid(activity.getPid());
		eteDTO.setActivityStatus(ActivityStatus.RECEIVED);
		eteDTO.setLocationType(LocationType.NoLocation);
		eteDTO.setUserName(userDTO.get().getFirstName());
		eteDTO.setUserPid(userDTO.get().getPid());
		eteDTO.setRemarks("XLS UPLOAD");
		eteDTO.setDate(docDate);

		long timeInMilliSecond = System.currentTimeMillis();
		String uniqueDocumentNo = timeInMilliSecond + "_" + userDTO.get().getLogin() + "_"
				+ document.getDocumentPrefix();

		inventoryVoucherHeaderDTO.setReceiverAccountPid(accountProfile.getPid());
		inventoryVoucherHeaderDTO.setDocumentNumberLocal(uniqueDocumentNo);
		inventoryVoucherHeaderDTO.setDocumentNumberServer(uniqueDocumentNo);
		inventoryVoucherHeaderDTO.setDocumentPid(document.getPid());
		inventoryVoucherHeaderDTO.setDocumentName(document.getName());
		inventoryVoucherHeaderDTO.setStatus(true);
		inventoryVoucherHeaderDTO.setDocumentDate(docDate);
		inventoryVoucherHeaderDTO.setDocumentTotal(0);

		List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();
		inventoryVouchers.add(inventoryVoucherHeaderDTO);

		ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO = new ExecutiveTaskSubmissionDTO();

		executiveTaskSubmissionDTO.setExecutiveTaskExecutionDTO(eteDTO);
		executiveTaskSubmissionDTO.setInventoryVouchers(inventoryVouchers);

		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		Company company = user.getCompany();
		EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUser(user);
		// save Executive Task Execution
		ExecutiveTaskExecution executiveTaskExecution = saveExecutiveTaskExecution(company, user,
				executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO());
		// save Inventory Vouchers
		saveInventoryVouchers(company, user, employeeProfile, executiveTaskExecution,
				executiveTaskSubmissionDTO.getInventoryVouchers());

		List<ExecutiveTaskExecution> result = new ArrayList<>();
		result.add(executiveTaskExecution);
		return result;
	}

	@Timed
	@RequestMapping(value = "/sales-value-report-upload-xls", method = RequestMethod.POST)
	public ResponseEntity<Void> post(@RequestBody MultipartFile file) throws URISyntaxException {
		log.debug("Web request to get a page of salesValuReportUploadXls");
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_116" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by company and AccImport Status";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyAndAccountImportStatus(true);
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
		try {

			int rowNumber = 0;
			Workbook workbook = WorkbookFactory.create(file.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				rowNumber = row.getRowNum();
				if (rowNumber > 0) {
					rowNumber++;
					if (row.getCell(1) != null) {
						String accName = row.getCell(row.getFirstCellNum()).getStringCellValue();
						Optional<AccountProfile> optionalAP = accountProfiles.stream()
								.filter(pc -> pc.getName().equals(accName)).findAny();

						if (optionalAP.isPresent()) {

							Optional<User> userDTO = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());

							Optional<ActivityDTO> activityDTO = activityService.findByName("Sales Data Transfer");
							ActivityDTO activity = new ActivityDTO();
							if (activityDTO.isPresent()) {
								activity = activityDTO.get();
							}
							if (!activityDTO.isPresent()) {
								activity = new ActivityDTO();
								activity.setName("Sales Data Transfer");
								activity.setActivated(true);
								activity.setHasDefaultAccount(false);
								activity.setDescription("used to send datas from tally");
								activity = activityService.save(activity);
							}

							Optional<DocumentDTO> documentDTO = documentService.findByName("sales");

							DocumentDTO document = new DocumentDTO();
							if (!documentDTO.isPresent()) {
								DocumentDTO dto = new DocumentDTO();
								dto.setName("sales");
								dto.setDocumentType(DocumentType.INVENTORY_VOUCHER);
								dto.setDocumentPrefix("sales");
								dto = documentService.save(dto);
								document = dto;
							} else {
								document = documentDTO.get();
							}

							InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();
							ExecutiveTaskExecutionDTO eteDTO = new ExecutiveTaskExecutionDTO();

							AccountProfile accp = optionalAP.get();

							eteDTO.setAccountProfileName(accp.getName());
							eteDTO.setAccountProfilePid(accp.getPid());
							eteDTO.setAccountTypeName(accp.getAccountType().getName());
							eteDTO.setAccountTypePid(accp.getAccountType().getPid());

							eteDTO.setActivityName(activity.getName());
							eteDTO.setActivityPid(activity.getPid());
							eteDTO.setActivityStatus(ActivityStatus.RECEIVED);
							eteDTO.setLocationType(LocationType.NoLocation);
							eteDTO.setUserName(userDTO.get().getFirstName());
							eteDTO.setUserPid(userDTO.get().getPid());
							eteDTO.setRemarks("XLS UPLOAD");

							long timeInMilliSecond = System.currentTimeMillis();
							String uniqueDocumentNo = timeInMilliSecond + "_" + userDTO.get().getLogin() + "_"
									+ document.getDocumentPrefix();

							inventoryVoucherHeaderDTO.setReceiverAccountPid(accp.getPid());
							inventoryVoucherHeaderDTO.setDocumentNumberLocal(uniqueDocumentNo);
							inventoryVoucherHeaderDTO.setDocumentNumberServer(uniqueDocumentNo);
							inventoryVoucherHeaderDTO.setDocumentPid(document.getPid());
							inventoryVoucherHeaderDTO.setDocumentName(document.getName());
							inventoryVoucherHeaderDTO.setStatus(true);

							if (row.getCell(row.getFirstCellNum() + 1) != null) {
								if (row.getCell(row.getFirstCellNum() + 1).getCellTypeEnum() == CellType.NUMERIC) {
									if (DateUtil.isCellDateFormatted(row.getCell(row.getFirstCellNum() + 1))) {
										DateFormat df = new SimpleDateFormat("d-MMM-yyyy");
										Date d = row.getCell(row.getFirstCellNum() + 1).getDateCellValue();
										String buy_date = df.format(d);
										LocalDateTime dateTime = convertDateToLocalDate(buy_date);
										eteDTO.setDate(dateTime);
										inventoryVoucherHeaderDTO.setDocumentDate(dateTime);
									}
								}
							}

							if (row.getCell(row.getFirstCellNum() + 2) != null) {
								if (row.getCell(row.getFirstCellNum() + 2).getCellTypeEnum() == CellType.NUMERIC) {
									inventoryVoucherHeaderDTO.setDocumentTotal(
											row.getCell(row.getFirstCellNum() + 2).getNumericCellValue());
								}
							}

							List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();
							inventoryVouchers.add(inventoryVoucherHeaderDTO);

							ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO = new ExecutiveTaskSubmissionDTO();

							executiveTaskSubmissionDTO.setExecutiveTaskExecutionDTO(eteDTO);
							executiveTaskSubmissionDTO.setInventoryVouchers(inventoryVouchers);

							saveTPExecutiveTaskSubmission(executiveTaskSubmissionDTO);

						}

					}
				}
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
		log.info("accounts saved successfully..................");
		return new ResponseEntity<>(HttpStatus.OK);

	}

	private LocalDateTime convertDateToLocalDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy");
		LocalDate localDate = LocalDate.parse(date, formatter);
		return localDate.atTime(0, 0);
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
		 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get one by pid";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		executiveTaskExecution
				.setActivity(activityRepository.findOneByPid(executiveTaskExecutionDTO.getActivityPid()).get());
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

				inventoryVouchers.add(inventoryVoucherHeader);

			}
			return inventoryVoucherHeaderRepository.save(inventoryVouchers);
		}
		return null;
	}
}
