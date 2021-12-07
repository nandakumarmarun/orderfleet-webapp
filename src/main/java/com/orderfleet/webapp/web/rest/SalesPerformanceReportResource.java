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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
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
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherXlsDownloadDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformanceDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

/**
 * Web controller for managing InventoryVoucher.
 * 
 * @author Muhammed Riyas T
 * @since July 21, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesPerformanceReportResource {

	private final Logger log = LoggerFactory.getLogger(SalesPerformanceReportResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String CUSTOM = "CUSTOM";

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherService;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

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
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	/**
	 * GET /primary-sales-performance : get all the inventory vouchers.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of inventory
	 *         vouchers in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/primary-sales-performance", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllInventoryVouchers(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of inventory vouchers");
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
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());
		return "company/primarySalesPerformanceReport";
	}

	/**
	 * GET /primary-sales-performance/:id : get the "id" InventoryVoucher.
	 *
	 * @param id the id of the InventoryVoucherDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         InventoryVoucherDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/primary-sales-performance/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> getInventoryVoucher(@PathVariable String pid) {
		log.debug("Web request to get inventoryVoucherDTO by pid : {}", pid);
		Optional<InventoryVoucherHeaderDTO> optionalInventoryVoucherHeaderDTO = inventoryVoucherService
				.findByPid(pid);
		if (optionalInventoryVoucherHeaderDTO.isPresent()) {
			InventoryVoucherHeaderDTO inventoryVoucherDTO = optionalInventoryVoucherHeaderDTO.get();
			Double ivTotalVolume = inventoryVoucherDTO.getInventoryVoucherDetails().stream()
					.collect(Collectors.summingDouble(ivd -> {
						if (ivd.getProductUnitQty() != null) {
							return (ivd.getProductUnitQty() * ivd.getQuantity());
						} else {
							return 0;
						}
					}));
			inventoryVoucherDTO.setDocumentVolume(ivTotalVolume);
			return new ResponseEntity<>(inventoryVoucherDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/primary-sales-performance/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<List<SalesPerformanceDTO>> filterInventoryVouchers(
			@RequestParam("employeePids") List<String> employeePids, @RequestParam("status") String status,
			@RequestParam("accountPid") String accountPid, @RequestParam("filterBy") String filterBy,
			@RequestParam("documentPids") List<String> documentPids, @RequestParam String fromDate,
			@RequestParam String toDate) {
		log.debug("Web request to filter accounting vouchers");
		if (documentPids.isEmpty()) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}
		LocalDate fDate = LocalDate.now();
		LocalDate tDate = LocalDate.now();
		if (filterBy.equals(SalesPerformanceReportResource.CUSTOM)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		} else if (filterBy.equals(SalesPerformanceReportResource.YESTERDAY)) {
			fDate = LocalDate.now().minusDays(1);
			tDate = fDate;
		} else if (filterBy.equals(SalesPerformanceReportResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fDate = LocalDate.now().with(fieldISO, 1);
		} else if (filterBy.equals(SalesPerformanceReportResource.MTD)) {
			fDate = LocalDate.now().withDayOfMonth(1);
		}
		List<SalesPerformanceDTO> salesPerformanceDTOs = getFilterData(employeePids, documentPids, status, accountPid,
				fDate, tDate);
		return new ResponseEntity<>(salesPerformanceDTOs, HttpStatus.OK);
	}

	private List<SalesPerformanceDTO> getFilterData(List<String> employeePids, List<String> documentPids, String status,
			String accountPid, LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Long> userIds = employeeProfileRepository.findUserIdByEmployeePidIn(employeePids);
		Long currentUserId = userRepository.getIdByLogin(SecurityUtils.getCurrentUserLogin());
		userIds.add(currentUserId);
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}
		List<Boolean> tallyStatus;
		if (status.equals("processed")) {
			tallyStatus = Arrays.asList(Boolean.TRUE);
		} else {
			tallyStatus = Arrays.asList(Boolean.TRUE, Boolean.FALSE);
		}
		List<Object[]> inventoryVouchers;
		if ("-1".equals(accountPid)) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_156" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get by userIdIn and DocPidIn and status Dtae between";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndDocumentPidInAndStatusDateBetweenOrderByCreatedDateDesc(userIds, documentPids,
							tallyStatus, fromDate, toDate);
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
			String id = "INV_QUERY_158" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all By UserIdIn and AccountPidIn And DocumentPidIn And StatusDateBetween";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndAccountPidInAndDocumentPidInAndStatusDateBetweenOrderByCreatedDateDesc(userIds,
							accountPid, documentPids, tallyStatus, fromDate, toDate);
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
		if (inventoryVouchers.isEmpty()) {
			return Collections.emptyList();
		} else {
			return createSalesPerformanceDTO(inventoryVouchers);
		}
	}

	private List<SalesPerformanceDTO> createSalesPerformanceDTO(List<Object[]> inventoryVouchers) {
		// fetch voucher details
		Set<String> ivHeaderPids = inventoryVouchers.parallelStream().map(obj -> obj[0].toString())
				.collect(Collectors.toSet());
		List<Object[]> ivDetails = inventoryVoucherDetailRepository.findByInventoryVoucherHeaderPidIn(ivHeaderPids);
		Map<String, Double> ivTotalVolume = ivDetails.stream().collect(Collectors.groupingBy(obj -> obj[0].toString(),
				Collectors.summingDouble(obj -> ((Double) (obj[3] == null ? 1.0d : obj[3]) * (Double) obj[4]))));
		int size = inventoryVouchers.size();
		List<SalesPerformanceDTO> salesPerformanceDTOs = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			SalesPerformanceDTO salesPerformanceDTO = new SalesPerformanceDTO();
			Object[] ivData = inventoryVouchers.get(i);
			salesPerformanceDTO.setPid(ivData[0].toString());
			salesPerformanceDTO.setDocumentNumberLocal(ivData[1].toString());
			salesPerformanceDTO.setDocumentNumberServer(ivData[2].toString());
			salesPerformanceDTO.setDocumentPid(ivData[3].toString());
			salesPerformanceDTO.setDocumentName(ivData[4].toString());
			salesPerformanceDTO.setCreatedDate((LocalDateTime) ivData[5]);
			salesPerformanceDTO.setDocumentDate((LocalDateTime) ivData[6]);
			salesPerformanceDTO.setReceiverAccountPid(ivData[7].toString());
			salesPerformanceDTO.setReceiverAccountName(ivData[8].toString());
			salesPerformanceDTO.setSupplierAccountPid(ivData[9].toString());
			salesPerformanceDTO.setSupplierAccountName(ivData[10].toString());
			salesPerformanceDTO.setEmployeePid(ivData[11].toString());
			salesPerformanceDTO.setEmployeeName(ivData[12].toString());
			salesPerformanceDTO.setUserName(ivData[13].toString());
			salesPerformanceDTO.setDocumentTotal((double) ivData[14]);
			salesPerformanceDTO.setDocumentVolume((double) ivData[15]);
			salesPerformanceDTO.setTotalVolume(ivTotalVolume.get(salesPerformanceDTO.getPid()));

			salesPerformanceDTO.setStatus((boolean) ivData[16]);

			salesPerformanceDTOs.add(salesPerformanceDTO);
		}
		return salesPerformanceDTOs;
	}

	@RequestMapping(value = "/primary-sales-performance/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) {
		return primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
	}

	@RequestMapping(value = "/primary-sales-performance/download-inventory-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadInventoryXls(@RequestParam("inventoryVoucherHeaderPids") String[] inventoryVoucherHeaderPids,
			HttpServletResponse response) {
		log.debug("fetching inventory voucher header " + LocalDateTime.now());
		List<Object[]> inventoryVoucherHeaderDTOs = inventoryVoucherService
				.findByCompanyIdAndInventoryPidIn(Arrays.asList(inventoryVoucherHeaderPids));
		if (inventoryVoucherHeaderDTOs.isEmpty()) {
			return;
		}
		log.debug("fetched inventory voucher header " + LocalDateTime.now());
		log.debug("inventory voucher header size" + inventoryVoucherHeaderDTOs.size());
		buildExcelDocument(inventoryVoucherHeaderDTOs, response);
	}

	private void buildExcelDocument(List<Object[]> inventoryVoucherHeaderDTOs, HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "SalesOrder" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Order No", "Salesman", "Order Date", "Customer", "Supplier", "Product Name",
				"Quantity", "Unit Quantity", "Total Quantity", "Free Quantity", "Selling Rate", "Discount Amount",
				"Discount Percentage", "Tax Amount", "Row Total" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			// createReportRows(worksheet, inventoryVoucherHeaderDTOs);
			createReportRowsFasterWay(worksheet, inventoryVoucherHeaderDTOs);
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
			log.error("IOException on downloading Sales Order {}", ex.getMessage());
		}
	}

	private void createReportRowsFasterWay(HSSFSheet worksheet, List<Object[]> inventoryVoucherObjectArray) {
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
		for (Object[] ivArray : inventoryVoucherObjectArray) {
			HSSFRow row = worksheet.createRow(rowNum++);
			row.createCell(0).setCellValue(ivArray[0] != null ? ivArray[0].toString() : "");
			row.createCell(1).setCellValue(ivArray[1] != null ? ivArray[1].toString() : "");
			HSSFCell docDateCell = row.createCell(2);
			docDateCell.setCellValue(ivArray[2] != null ? ivArray[2].toString() : "");
			docDateCell.setCellStyle(dateCellStyle);
			row.createCell(3).setCellValue(ivArray[3] != null ? ivArray[3].toString() : "");
			row.createCell(4).setCellValue(ivArray[4] != null ? ivArray[4].toString() : "");

			row.createCell(5).setCellValue(ivArray[5] != null ? ivArray[5].toString() : "");
			row.createCell(6).setCellValue(ivArray[6] != null ? ivArray[6].toString() : "0");
			double unitQty = 1.0;
			if (ivArray[7] != null) {
				unitQty = Double.parseDouble(ivArray[7].toString());
			}
			row.createCell(7).setCellValue(unitQty);
			if (ivArray[6] != null) {
				double totalQty = Double.parseDouble(ivArray[6].toString()) * unitQty;
				row.createCell(8).setCellValue(totalQty);
			}
			row.createCell(9).setCellValue(ivArray[8] != null ? ivArray[8].toString() : "0");
			row.createCell(10).setCellValue(ivArray[9] != null ? Double.parseDouble(ivArray[9].toString()) : 0.0);
			row.createCell(11).setCellValue(ivArray[10] != null ? Double.parseDouble(ivArray[10].toString()) : 0.0);

			row.createCell(12).setCellValue(ivArray[11] != null ? Double.parseDouble(ivArray[11].toString()) : 0.0);
			row.createCell(13).setCellValue(ivArray[12] != null ? Double.parseDouble(ivArray[12].toString()) : 0.0);
			row.createCell(14).setCellValue(ivArray[13] != null ? Double.parseDouble(ivArray[13].toString()) : 0.0);

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

	@RequestMapping(value = "/primary-sales-performance/changeStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> changeStatus(@RequestParam String pid) {
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(pid).get();
		inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

//	private void createReportRows(HSSFSheet worksheet, List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs) {
//	/*
//	 * CreationHelper helps us create instances of various things like DataFormat,
//	 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
//	 */
//	HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
//	// Create Cell Style for formatting Date
//	HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
//	dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss"));
//	// Create Other rows and cells with Sales data
//	int rowNum = 1;
//	for (InventoryVoucherHeaderDTO ivh : inventoryVoucherHeaderDTOs) {
//		for (InventoryVoucherDetailDTO ivd : ivh.getInventoryVoucherDetails()) {
//			HSSFRow row = worksheet.createRow(rowNum++);
//			row.createCell(0).setCellValue(ivh.getDocumentNumberLocal());
//			row.createCell(1).setCellValue(ivh.getUserName());
//			HSSFCell docDateCell = row.createCell(2);
//			docDateCell.setCellValue(ivh.getDocumentDate().toString());
//			docDateCell.setCellStyle(dateCellStyle);
//			row.createCell(3).setCellValue(ivh.getReceiverAccountName());
//			row.createCell(4).setCellValue(ivh.getSupplierAccountName());
//
//			row.createCell(5).setCellValue(ivd.getProductName());
//			row.createCell(6).setCellValue(ivd.getQuantity());
//			double unitQty = 1.0;
//			if(ivd.getProductUnitQty()!=null) {
//				unitQty = ivd.getProductUnitQty();
//			}
//			row.createCell(7).setCellValue(unitQty);
//			row.createCell(8).setCellValue((ivd.getQuantity() * unitQty));
//			row.createCell(9).setCellValue(ivd.getFreeQuantity());
//			row.createCell(10).setCellValue(ivd.getSellingRate());
//			row.createCell(11).setCellValue(ivd.getDiscountAmount());
//			row.createCell(12).setCellValue(ivd.getDiscountPercentage());
//			row.createCell(13).setCellValue(ivd.getTaxAmount());
//			row.createCell(14).setCellValue(ivd.getRowTotal());
//		}
//	}
//}	

//	public static void fillReport(HSSFSheet worksheet, List<InventoryVoucherXlsDownloadDTO> xlsDownloadDTOs) {
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
