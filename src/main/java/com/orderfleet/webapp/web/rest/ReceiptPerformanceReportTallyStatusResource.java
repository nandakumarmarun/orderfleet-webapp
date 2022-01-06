package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.google.common.io.Files;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountingVoucherHeaderService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherXlsDownloadDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.FileDTO;
import com.orderfleet.webapp.web.rest.dto.FormFileDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherXlsDownloadDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.vendor.odoo.service.SendReceiptOdooService;
import com.orderfleet.webapp.web.vendor.sap.pravesh.service.SendTransactionSapPraveshService;

/**
 * Web controller for managing InventoryVoucher.
 * 
 * @author Muhammed Riyas T
 * @since July 21, 2016
 */
@Controller
@RequestMapping("/web")
public class ReceiptPerformanceReportTallyStatusResource {

	private final Logger log = LoggerFactory.getLogger(ReceiptPerformanceReportTallyStatusResource.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String CUSTOM = "CUSTOM";

	@Inject
	private AccountingVoucherHeaderService accountingVoucherService;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private PrimarySecondaryDocumentService primarySecondaryDocumentService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountingVoucherHeaderService accountingVoucherHeaderService;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private SendTransactionSapPraveshService sendTransactionSapPraveshService;

	@Inject
	private SendReceiptOdooService sendReceiptOdooService;

	/**
	 * GET /receipt : get all the inventory vouchers.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of inventory
	 *         vouchers in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/receipt-download-status", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountVouchers(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of accouting vouchers");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
			model.addAttribute("accounts", accountProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
			Long currentUserId = userRepository.getIdByLogin(SecurityUtils.getCurrentUserLogin());
			userIds.add(currentUserId);
			Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByUserIdIn(userIds);
//			List<Object[]> accountPidNames = locationAccountProfileRepository
//			.findAccountProfilesByLocationIdIn(locationIds);
//	int size = accountPidNames.size();
//	List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>(size);
//	for (int i = 0; i < size; i++) {
//		AccountProfileDTO accountProfileDTO = new AccountProfileDTO();
//		accountProfileDTO.setPid(accountPidNames.get(i)[0].toString());
//		accountProfileDTO.setName(accountPidNames.get(i)[1].toString());
//		accountProfileDTOs.add(accountProfileDTO);
//	}

			Set<BigInteger> apIds = locationAccountProfileRepository
					.findAccountProfileIdsByUserLocationsOrderByAccountProfilesName(locationIds);

			Set<Long> accountProfileIds = new HashSet<>();

			for (BigInteger apId : apIds) {
				accountProfileIds.add(apId.longValue());
			}
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
			// remove duplicates
			List<AccountProfile> result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());

			List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
					.accountProfilesToAccountProfileDTOs(result);
			model.addAttribute("accounts", accountProfileDTOs);
		}
		model.addAttribute("documents",
				documentRepository.findAllDocumentsByCompanyPidAndDocumentType(DocumentType.ACCOUNTING_VOUCHER,
						companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()).getPid()));

		boolean sendTransactionsSapPravesh = false;
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "COMP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="get by compId and name";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		Optional<CompanyConfiguration> opCompanyConfigurationSapPravesh = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(),
						CompanyConfig.SEND_TRANSACTIONS_SAP_PRAVESH);
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
		if (opCompanyConfigurationSapPravesh.isPresent()) {

			if (opCompanyConfigurationSapPravesh.get().getValue().equals("true")) {
				sendTransactionsSapPravesh = true;
			} else {
				sendTransactionsSapPravesh = false;
			}
		}
		model.addAttribute("sendTransactionsSapPravesh", sendTransactionsSapPravesh);

		boolean sendSalesOrderOdoo = false;
		Optional<CompanyConfiguration> opCompanyConfigurationOdoo = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SEND_TO_ODOO);
		if (opCompanyConfigurationOdoo.isPresent()) {

			if (opCompanyConfigurationOdoo.get().getValue().equals("true")) {
				sendSalesOrderOdoo = true;
			} else {
				sendSalesOrderOdoo = false;
			}
		}
		model.addAttribute("sendReceiptOdooStatus", sendSalesOrderOdoo);

		return "company/receiptDownloadStatus";
	}

	/**
	 * GET /receipt/:id : get the "id" InventoryVoucher.
	 *
	 * @param id the id of the InventoryVoucherDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         InventoryVoucherDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/receipt-download-status/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountingVoucherHeaderDTO> getAcccountVoucher(@PathVariable String pid) {
		log.debug("Web request to get accoutingVoucherDTO by pid : {}", pid);
		Optional<AccountingVoucherHeaderDTO> optionalAccountingVoucherHeaderDTO = accountingVoucherHeaderService
				.findOneByPid(pid);
		if (optionalAccountingVoucherHeaderDTO.isPresent()) {
			return new ResponseEntity<>(optionalAccountingVoucherHeaderDTO.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/receipt-download-status/sendTransactionsSapPravesh", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountingVoucherHeaderDTO> sendTransactionsSapPravesh() throws MessagingException {

		log.info("sendReceiptSap()-----");

		sendTransactionSapPraveshService.sendReceipt();

		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	@RequestMapping(value = "/receipt-download-status/sendReceiptOdoo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> sendReceiptOdoo() throws MessagingException {

		log.info("sendReceiptOdoo()-----");

		sendReceiptOdooService.sendReceipt();

		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	/**
	 * GET /receipt-download-status/images/:pid : get the "id" FormFileDTO.
	 * 
	 * @param pid the pid of the FormFileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         FormFileDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/receipt-download-status/images/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FormFileDTO>> getDynamicDocumentImages(@PathVariable String pid) {
		log.debug("Web request to get Receipt images by pid : {}", pid);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACC_QUERY_165" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountingVoucherHeader> optionalAccountingVoucherHeaderDTO = accountingVoucherHeaderRepository
				.findOneByPid(pid);
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

		AccountingVoucherHeader accountingVoucherHeader = new AccountingVoucherHeader();

		List<FormFileDTO> formFileDTOs = new ArrayList<>();
		if (optionalAccountingVoucherHeaderDTO.isPresent()) {
			accountingVoucherHeader = optionalAccountingVoucherHeaderDTO.get();
		}
		if (accountingVoucherHeader.getFiles().size() > 0) {
			FormFileDTO formFileDTO = new FormFileDTO();
			formFileDTO.setFormName(accountingVoucherHeader.getDocument().getName());
			formFileDTO.setFiles(new ArrayList<>());
			Set<File> files = accountingVoucherHeader.getFiles();
			for (File file : files) {
				FileDTO fileDTO = new FileDTO();
				fileDTO.setFileName(file.getFileName());
				fileDTO.setMimeType(file.getMimeType());
				java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file);
				if (physicalFile.exists()) {
					try {
						fileDTO.setContent(Files.toByteArray(physicalFile));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				formFileDTO.getFiles().add(fileDTO);
			}
			formFileDTOs.add(formFileDTO);
		}
		return new ResponseEntity<>(formFileDTOs, HttpStatus.OK);

	}

	@Timed
	@RequestMapping(value = "/receipt-download-status/sendReceiptToOdoo/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AccountingVoucherHeaderDTO>> sendReceiptToOdoo(@PathVariable String pid) {
		log.debug("Web request to get Receipts by pid : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_168" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get acc voucher by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVouchers = accountingVoucherHeaderRepository
				.findAccountingVoucherHeaderByPid(pid);
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
		if (accountingVouchers.size() > 0) {
			sendReceiptOdooService.sendReceiptAsync(accountingVouchers);
		}

		List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs = new ArrayList<>();

		return new ResponseEntity<>(accountingVoucherHeaderDTOs, HttpStatus.OK);

	}

	@RequestMapping(value = "/receipt-download-status/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<List<AccountingVoucherHeaderDTO>> filterInventoryVouchers(
			@RequestParam("employeePids") List<String> employeePids,
			@RequestParam("tallyDownloadStatus") String tallyDownloadStatus,
			@RequestParam("accountPid") String accountPid, @RequestParam("filterBy") String filterBy,
			@RequestParam("documentPids") String documentPid, @RequestParam String fromDate,
			@RequestParam String toDate) {
		log.debug("Web request to filter accounting vouchers");
		/*
		 * if (documentPids.isEmpty()) { return new
		 * ResponseEntity<>(Collections.emptyList(), HttpStatus.OK); }
		 */
		LocalDate fDate = LocalDate.now();
		LocalDate tDate = LocalDate.now();
		if (filterBy.equals(ReceiptPerformanceReportTallyStatusResource.CUSTOM)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		} else if (filterBy.equals(ReceiptPerformanceReportTallyStatusResource.YESTERDAY)) {
			fDate = LocalDate.now().minusDays(1);
			tDate = fDate;
		} else if (filterBy.equals(ReceiptPerformanceReportTallyStatusResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fDate = LocalDate.now().with(fieldISO, 1);
		} else if (filterBy.equals(ReceiptPerformanceReportTallyStatusResource.MTD)) {
			fDate = LocalDate.now().withDayOfMonth(1);
		}
		List<AccountingVoucherHeaderDTO> receiptStatusDTOs = getFilterData(employeePids, documentPid,
				tallyDownloadStatus, accountPid, fDate, tDate);
		return new ResponseEntity<>(receiptStatusDTOs, HttpStatus.OK);
	}

	private List<AccountingVoucherHeaderDTO> getFilterData(List<String> employeePids, String documentPid,
			String tallyDownloadStatus, String accountPid, LocalDate fDate, LocalDate tDate) {

		List<String> documentPids = new ArrayList<>();
		documentPids.add(documentPid);
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<String> userPids = employeeProfileRepository.findUserPidsByEmployeePids(employeePids);
		String currentUserPid = userRepository.getPidByLogin(SecurityUtils.getCurrentUserLogin());
		userPids.add(currentUserPid);
		if (userPids.isEmpty()) {
			return Collections.emptyList();
		}

		List<TallyDownloadStatus> tallyStatus = null;

		switch (tallyDownloadStatus) {
		case "PENDING":
			tallyStatus = Arrays.asList(TallyDownloadStatus.PENDING);
			break;
		case "PROCESSING":
			tallyStatus = Arrays.asList(TallyDownloadStatus.PROCESSING);
			break;
		case "COMPLETED":
			tallyStatus = Arrays.asList(TallyDownloadStatus.COMPLETED);
			break;
		case "FAILED":
			tallyStatus = Arrays.asList(TallyDownloadStatus.FAILED);
			break;

		case "ALL":
			tallyStatus = Arrays.asList(TallyDownloadStatus.COMPLETED, TallyDownloadStatus.PROCESSING,
					TallyDownloadStatus.PENDING, TallyDownloadStatus.FAILED);
			break;
		}

		List<AccountingVoucherHeaderDTO> accountVouchers = new ArrayList<>();
		if ("-1".equals(accountPid)) {
			accountVouchers = accountingVoucherHeaderService.getAllByCompanyIdUserPidDocumentPidAndDateBetween(userPids,
					documentPids, tallyStatus, fromDate, toDate);
		} else {
			accountVouchers = accountingVoucherHeaderService
					.getAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetween(userPids, accountPid, documentPids,
							tallyStatus, fromDate, toDate);
		}
		if (accountVouchers.isEmpty()) {
			return Collections.emptyList();
		} else {

			boolean sendToOdoo = false;
			Optional<CompanyConfiguration> opCompanyConfigurationOdoo = companyConfigurationRepository
					.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SEND_TO_ODOO);
			if (opCompanyConfigurationOdoo.isPresent()) {

				if (opCompanyConfigurationOdoo.get().getValue().equals("true")) {
					sendToOdoo = true;
				} else {
					sendToOdoo = false;
				}
			}

			for (AccountingVoucherHeaderDTO accountingVoucherHeader : accountVouchers) {
				accountingVoucherHeader.setSendToOdoo(sendToOdoo);
			}

			return accountVouchers;
		}
	}

	@RequestMapping(value = "/receipt-download-status/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) {
		return primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
	}

	@RequestMapping(value = "/receipt-download-status/download-accounting-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadInventoryXls(@RequestParam("accountVoucherHeaderPids") String[] accountingVoucherHeaderPids,
			HttpServletResponse response) {
		List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs = new ArrayList<>();
		for (String accountingVoucherHeaderPid : accountingVoucherHeaderPids) {
			Optional<AccountingVoucherHeaderDTO> accountingVoucher = accountingVoucherService
					.findOneByPid(accountingVoucherHeaderPid);
			if (accountingVoucher.isPresent()) {
				accountingVoucherHeaderDTOs.add(accountingVoucher.get());
			}
		}
		if (accountingVoucherHeaderDTOs.isEmpty()) {
			return;
		}
		buildExcelDocument(accountingVoucherHeaderDTOs, response);
	}

	private void buildExcelDocument(List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs,
			HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "Receipt" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Account Profile", "Phone Number", "Employee", "Send Date", "Send Time",
				"Received Date", "Received Time", "Cheque Date", "Mode", "Cheque Number", "Amount", "Bank Name",
				"Expense Type", "Remarks" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, accountingVoucherHeaderDTOs);
			// Resize all columns to fit the content size
			for (int i = 0; i < headerColumns.length; i++) {
				worksheet.autoSizeColumn(i);
			}
			response.setHeader("Content-Disposition", "inline; filename=" + excelFileName);
			response.setContentType("application/vnd.ms-excel");
			// Writes the report to the output stream
			ServletOutputStream outputStream = response.getOutputStream();
			worksheet.getWorkbook().write(outputStream);
			outputStream.flush();
		} catch (IOException ex) {
			log.error("IOException on downloading Receipt {}", ex.getMessage());
		}
	}

	private void createReportRows(HSSFSheet worksheet, List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs) {
		/*
		 * CreationHelper helps us create instances of various things like DataFormat,
		 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
		 */
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss"));
		// Create Other rows and cells with Sales data
		int rowNum = 1;
		DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		DateTimeFormatter time = DateTimeFormatter.ofPattern("hh:mm:ss a");
		for (AccountingVoucherHeaderDTO avh : accountingVoucherHeaderDTOs) {
			for (AccountingVoucherDetailDTO avd : avh.getAccountingVoucherDetails()) {
				HSSFRow row = worksheet.createRow(rowNum++);
				row.createCell(0).setCellValue(avh.getAccountProfileName());
				row.createCell(1).setCellValue(avh.getPhone());
				row.createCell(2).setCellValue(avh.getEmployeeName());
				HSSFCell docDateCell = row.createCell(3);
				// docDateCell.setCellValue(avd.getVoucherDate().toLocalDate().toString());
				// docDateCell.setCellStyle(dateCellStyle);
				docDateCell.setCellValue(date.format(avd.getVoucherDate()).toString());
				HSSFCell docTimeCell = row.createCell(4);
				docTimeCell.setCellValue(time.format(avd.getVoucherDate()).toString());

				HSSFCell docDateCel2 = row.createCell(5);
				// docDateCell.setCellValue(avd.getVoucherDate().toLocalDate().toString());
				// docDateCell.setCellStyle(dateCellStyle);
				docDateCel2.setCellValue(date.format(avh.getCreatedDate()).toString());
				HSSFCell docTimeCel2 = row.createCell(6);
				docTimeCel2.setCellValue(time.format(avh.getCreatedDate()).toString());

//				HSSFCell docDateCell2 = row.createCell(4);
//				docDateCell2.setCellValue(avh.getCreatedDate().toLocalDate().toString());
//				docDateCell2.setCellStyle(dateCellStyle);
//				HSSFCell docDateCell3 = row.createCell(5);
//				docDateCell3.setCellValue(avd.getInstrumentDate().toLocalDate().toString());
//				docDateCell3.setCellStyle(dateCellStyle);
				HSSFCell docDateCell3 = row.createCell(7);
				docDateCell3.setCellValue(date.format(avd.getInstrumentDate()).toString());
//				docDateCell3.setCellValue(avd.getInstrumentDate().toLocalDate().toString());
//				docDateCell3.setCellStyle(dateCellStyle);
				row.createCell(8).setCellValue(avd.getMode().name());
				row.createCell(9).setCellValue(avd.getInstrumentNumber());
				row.createCell(10).setCellValue(avd.getAmount());
				row.createCell(11).setCellValue(avd.getBankName());
				row.createCell(12).setCellValue(avd.getIncomeExpenseHeadName());
				row.createCell(13).setCellValue(avd.getRemarks());

			}
		}
	}

	private void createHeaderRow(HSSFSheet worksheet, String[] headerColumns) {
		// Create a Font for styling header cells
		Font headerFont = worksheet.getWorkbook().createFont();
		headerFont.setFontName("Arial");
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());
		// Create a CellStyle with the font
		HSSFCellStyle headerCellStyle = worksheet.getWorkbook().createCellStyle();
		headerCellStyle.setFont(headerFont);
		// Create a Row
		HSSFRow headerRow = worksheet.createRow(0);
		// Create cells
		for (int i = 0; i < headerColumns.length; i++) {
			HSSFCell cell = headerRow.createCell(i);
			cell.setCellValue(headerColumns[i]);
			cell.setCellStyle(headerCellStyle);
		}
	}

	@RequestMapping(value = "/receipt-download-status/changeStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountingVoucherHeaderDTO> changeStatus(@RequestParam String pid,
			@RequestParam TallyDownloadStatus tallyDownloadStatus) {
		AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO();
		accountingVoucherHeaderDTO.setPid(pid);
		accountingVoucherHeaderDTO.setTallyDownloadStatus(tallyDownloadStatus);
		accountingVoucherHeaderService.updateAccountingVoucherHeaderStatus(accountingVoucherHeaderDTO);
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

//	public static void fillReport(HSSFSheet worksheet, List<AccountingVoucherXlsDownloadDTO> xlsDownloadDTOs) {
//		// Row offset
//		int startRowIndex = 1;
//		int startColIndex = 0;
//
//		// Create cell style for the body
//		HSSFCellStyle bodyCellStyle = worksheet.getWorkbook().createCellStyle();
//		bodyCellStyle.setWrapText(true);
//
//		// Create body
//		for (int i = 0; i < xlsDownloadDTOs.size(); i++) {
//			// Create a new row
//			startRowIndex = startRowIndex + 1;
//			HSSFRow row = worksheet.createRow((short) startRowIndex);
//
//			HSSFCell cell1 = row.createCell(startColIndex + 0);
//			cell1.setCellValue(xlsDownloadDTOs.get(i).getDocumentNumberLocal());
//			cell1.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell2 = row.createCell(startColIndex + 1);
//			cell2.setCellValue(xlsDownloadDTOs.get(i).getDocumentName());
//			cell2.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell3 = row.createCell(startColIndex + 2);
//			cell3.setCellValue(xlsDownloadDTOs.get(i).getUserName());
//			cell3.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell4 = row.createCell(startColIndex + 3);
//			if (xlsDownloadDTOs.get(i).getDocumentDate() != null) {
//				cell4.setCellValue(xlsDownloadDTOs.get(i).getDocumentDate().toLocalDate().toString());
//			} else {
//				cell4.setCellValue("");
//			}
//			cell4.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell5 = row.createCell(startColIndex + 4);
//			cell5.setCellValue(xlsDownloadDTOs.get(i).getReceiverAccountName());
//			cell5.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell6 = row.createCell(startColIndex + 5);
//			cell6.setCellValue(xlsDownloadDTOs.get(i).getSupplierAccountName());
//			cell6.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell7 = row.createCell(startColIndex + 6);
//			cell7.setCellValue(xlsDownloadDTOs.get(i).getDocumentTotal());
//			cell7.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell8 = row.createCell(startColIndex + 7);
//			cell8.setCellValue(xlsDownloadDTOs.get(i).getDocumentVolume());
//			cell8.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell9 = row.createCell(startColIndex + 8);
//			cell9.setCellValue(xlsDownloadDTOs.get(i).getDocDiscountAmount());
//			cell9.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell10 = row.createCell(startColIndex + 9);
//			cell10.setCellValue(xlsDownloadDTOs.get(i).getDocDiscountPercentage());
//			cell10.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell11 = row.createCell(startColIndex + 10);
//			cell11.setCellValue(xlsDownloadDTOs.get(i).getProductName());
//			cell11.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell12 = row.createCell(startColIndex + 11);
//			cell12.setCellValue(xlsDownloadDTOs.get(i).getQuantity());
//			cell12.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell13 = row.createCell(startColIndex + 12);
//			cell13.setCellValue(xlsDownloadDTOs.get(i).getFreeQuantity());
//			cell13.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell14 = row.createCell(startColIndex + 13);
//			cell14.setCellValue(xlsDownloadDTOs.get(i).getSellingRate());
//			cell14.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell15 = row.createCell(startColIndex + 14);
//			cell15.setCellValue(xlsDownloadDTOs.get(i).getTaxAmount());
//			cell15.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell16 = row.createCell(startColIndex + 15);
//			cell16.setCellValue(xlsDownloadDTOs.get(i).getDiscountAmount());
//			cell16.setCellStyle(bodyCellStyle);
//
//			HSSFCell cell17 = row.createCell(startColIndex + 16);
//			cell17.setCellValue(xlsDownloadDTOs.get(i).getRowTotal());
//			cell17.setCellStyle(bodyCellStyle);
//		}
//	}

}
