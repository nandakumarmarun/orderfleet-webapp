package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserActivity;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentFormDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportDetailView;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformanceDTO;
import com.orderfleet.webapp.web.rest.dto.VisitDetailReportView;
import com.orderfleet.webapp.web.rest.dto.VisitReportView;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ExecutiveTaskExecution.
 * 
 * @author Muhammed Riyas T
 * @since July 12, 2016
 */
@Controller
@RequestMapping("/web")
public class VisitDetailReportResource {

	private final Logger log = LoggerFactory.getLogger(VisitDetailReportResource.class);

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AttendanceRepository attendanceRepository;

	/**
	 * GET /invoice-wise-reports : get all the executive task executions.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of executive
	 *         task execution in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/visit-detail-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllExecutiveTaskExecutions(Pageable pageable, Model model) {
		// user under current user

		return "company/visit-report/visits-detail-report";
	}

	@RequestMapping(value = "/visit-detail-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<VisitReportView>> filterExecutiveTaskExecutions(
			@RequestParam("employeePid") String employeePid, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate, @RequestParam boolean inclSubordinate) {

		List<Document> documents = primarySecondaryDocumentRepository.findAllDocumentsByCompanyId();
		if (documents.isEmpty()) {
			return null;
		}
		List<VisitReportView> executiveTaskExecutions = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterData(employeePid, LocalDate.now(), LocalDate.now(), documents,
					inclSubordinate);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterData(employeePid, yesterday, yesterday, documents, inclSubordinate);

		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, fromDateTime, fromDateTime, documents,
					inclSubordinate);
		}
		return new ResponseEntity<>(executiveTaskExecutions, HttpStatus.OK);
	}

	private List<VisitReportView> getFilterData(String employeePid, LocalDate fDate, LocalDate tDate,
			List<Document> documents, boolean inclSubordinate) {
		log.info("Get Fileter Data");
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<Long> userIds = getUserIdsUnderCurrentUser(employeePid, inclSubordinate);
		log.info("User Ids :" + userIds);
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}

		log.info("Finding executive Task execution");

		List<Object[]> etExtecutions = executiveTaskExecutionRepository.findByUserIdInAndDateBetween(userIds, fromDate,
				toDate);
		if (etExtecutions.isEmpty()) {
			return null;
		}

		Map<String, List<Long>> employeeWiseGrouped = etExtecutions.stream().collect(Collectors.groupingBy(
				obj -> (String) obj[1], TreeMap::new, Collectors.mapping(ete -> (Long) ete[0], Collectors.toList())));

		List<Long> eteIds = employeeWiseGrouped.values().stream().flatMap(List::stream).collect(Collectors.toList());

		log.info("Finding Visit Details");
		List<Object[]> visitDetails = new ArrayList<>();
		if (eteIds.size() > 0) {

			visitDetails = executiveTaskExecutionRepository.findByExeTaskIdIn(eteIds);
		}

		log.info("Finding executive Task execution Inventory Vouchers...");
		List<Object[]> inventoryVouchers = new ArrayList<>();
		if (eteIds.size() > 0) {
			inventoryVouchers = inventoryVoucherHeaderRepository.findByDocumentsAndExecutiveTaskIdIn(documents, eteIds);

		}

		log.info("Finding  Inventory Vouchers...");
		List<Object[]> ivhDtos = inventoryVoucherHeaderRepository
				.findByDocumentsAndExecutiveTaskExecutionIdIn(documents, eteIds);

		Set<String> ivhPids = new HashSet<>();

		for (Object[] ivh : ivhDtos) {
			ivhPids.add(ivh[4].toString());
		}
		log.info("Finding  InventoryVoucherDetails...");
		List<InventoryVoucherDetail> ivDetails = new ArrayList<>();
		if (ivhPids.size() > 0) {
			ivDetails = inventoryVoucherDetailRepository.findAllByInventoryVoucherHeaderPidIn(ivhPids);
		}

		log.info("Finding executive Task execution Accounting Vouchers...");
		List<Object[]> accountingVouchers = new ArrayList<>();
		if (eteIds.size() > 0) {
			accountingVouchers = accountingVoucherHeaderRepository.findByExecutiveTaskExecutionIdInAndDocument(eteIds,
					documents);

		}

		List<Object[]> accountProfile = new ArrayList<>();
		accountProfile = accountProfileRepository.findByUserIdInAndDateBetween(userIds, fromDate, toDate);

		log.info("Finding Attendance details.......");
		List<Object[]> attendance = new ArrayList<>();
		attendance = attendanceRepository.findByUserIdInAndDateBetween(userIds, fromDate, toDate);

		log.info("executive task execution looping started :");
		List<VisitReportView> visitReportView = new ArrayList<>();

		for (Map.Entry<String, List<Long>> entry : employeeWiseGrouped.entrySet()) {
			List<VisitDetailReportView> executiveTaskExecutionDetailViews = new ArrayList<>();

			VisitReportView visitreport = new VisitReportView();

			VisitDetailReportView executiveView = new VisitDetailReportView();

			String employeeName = entry.getKey();
			visitreport.setEmployeeName(employeeName);
			System.out.println("Name:" + employeeName);
			for (Object[] obj : attendance) {
				if (obj[2].toString().equals(employeeName)) {

					LocalDateTime attTime = LocalDateTime.parse(obj[1].toString());
					visitreport.setRoute(obj[0].toString());
					visitreport.setAttndncTime(attTime);
				}
			}

			for (Object[] obj : visitDetails) {
				if (obj[3].toString().equals(employeeName)) {
					LocalDateTime firstVisittime = LocalDateTime.parse(obj[1].toString());
					LocalDateTime lastVisitTime = LocalDateTime.parse(obj[2].toString());

					executiveView.setTotalVisit(obj[0].toString());
					executiveView.setFirstVisitTime(firstVisittime);
					executiveView.setLastVisitTime(lastVisitTime);

				}
			}

			for (Object[] obj : inventoryVouchers) {
				for (Object[] obj1 : ivhDtos) {
					if (obj1[0].toString().equals(employeeName)) {

						LocalDateTime firstSoTime = LocalDateTime.parse(obj[2].toString());
						LocalDateTime LastSoTime = LocalDateTime.parse(obj[3].toString());

						executiveView.setFirstSoTime(firstSoTime);
						executiveView.setLastSoTime(LastSoTime);
						executiveView.setTotalSo(obj[0].toString());

						executiveView.setTotalSaleOrderAmount(Double.valueOf(obj[1].toString()));

					}
				}
			}
			double receiptAmount = 0.0;
			for (Object[] obj : accountingVouchers) {
				if (obj[1].toString().equals(employeeName)) {

					receiptAmount += Double.valueOf(obj[0].toString());
				}

			}
			for (Object[] obj : accountProfile) {
				if (obj[1].toString().equals(employeeName)) {
					executiveView.setLedgerCount(accountProfile.size());

				}

			}

			double totalVolume = ivDetails.stream()
					.filter(ivd -> ivd.getInventoryVoucherHeader().getEmployee().getName().equals(employeeName))
					.mapToDouble(InventoryVoucherDetail::getVolume).sum();

			executiveView.setTotalReceiptAmount(receiptAmount);
			executiveView.setTotalKG(totalVolume);
			executiveTaskExecutionDetailViews.add(executiveView);
			visitreport.setVisitDetailReportView(executiveTaskExecutionDetailViews);
			visitReportView.add(visitreport);

		}

		return visitReportView;

	}

	@RequestMapping(value = "/visit-detail-report/downloadxls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void downloadXls(@RequestParam("employeePid") String employeePid, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate, @RequestParam boolean inclSubordinate,
			HttpServletResponse response) {
		log.debug("downloading......");
		List<Document> documents = primarySecondaryDocumentRepository.findAllDocumentsByCompanyId();
		if (documents.isEmpty()) {
			return;
		}
		List<VisitReportView> executiveTaskExecutions = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterData(employeePid, LocalDate.now(), LocalDate.now(), documents,
					inclSubordinate);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterData(employeePid, yesterday, yesterday, documents, inclSubordinate);

		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, fromDateTime, fromDateTime, documents,
					inclSubordinate);
		}
		buildExcelDocument(executiveTaskExecutions, response);
	}

	private void buildExcelDocument(List<VisitReportView> executiveTaskExecutions, HttpServletResponse response) {

		// TODO Auto-generated method stub
		log.debug("Downloading Excel report");
		String excelFileName = "VisitDetailReport" + ".xls";
		String sheetName = "Sheet1";

		String[] headerColumns = { "Employee", "Route", "Atendance MarkedTime", "First Visit Time", "Last Visit Time",
				"Total Visit", "First SO Time", "Last SO Time", "Total SO", "New Ledger Created", "Total KG",
				"Total Value", "Total Collection value" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, executiveTaskExecutions);
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
			log.error("IOException on downloading Visit Details {}", ex.getMessage());
		}

	}

	private void createReportRows(HSSFSheet worksheet, List<VisitReportView> executiveTaskExecutions) {
		// TODO Auto-generated method stub
		/*
		 * CreationHelper helps us create instances of various things like DataFormat,
		 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
		 */
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/DD/YYYY, h:mm:ss "));
		// Create Other rows and cells with Sales data
		int rowNum = 1;
		for (VisitReportView executivetask : executiveTaskExecutions) {
			HSSFRow row = worksheet.createRow(rowNum++);

			row.createCell(0).setCellValue(executivetask.getEmployeeName());

			row.createCell(1).setCellValue(executivetask.getRoute());
			LocalDateTime localDateTime = executivetask.getAttndncTime();
			Instant i = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
			Date date = Date.from(i);
			HSSFCell attndncDateCell = row.createCell(2);
			attndncDateCell.setCellValue(date);
			attndncDateCell.setCellStyle(dateCellStyle);
			for (VisitDetailReportView vdrv : executivetask.getVisitDetailReportView()) {
				if (vdrv.getFirstVisitTime() != null) {
					LocalDateTime abc = vdrv.getFirstVisitTime();
					Instant it = abc.atZone(ZoneId.systemDefault()).toInstant();
					Date firstVisit = Date.from(it);
					HSSFCell firstVisitDateCell = row.createCell(3);
					firstVisitDateCell.setCellValue(firstVisit);
					firstVisitDateCell.setCellStyle(dateCellStyle);
				}

				LocalDateTime xyz = vdrv.getLastVisitTime();
				Instant inst = xyz.atZone(ZoneId.systemDefault()).toInstant();
				Date lastVisit = Date.from(inst);
				HSSFCell lastVisitDateCell = row.createCell(4);
				lastVisitDateCell.setCellValue(lastVisit);
				lastVisitDateCell.setCellStyle(dateCellStyle);

				row.createCell(5).setCellValue(vdrv.getTotalVisit());

				HSSFCell firstsoDateCell = row.createCell(6);
				if (vdrv.getFirstSoTime() != null) {
					LocalDateTime fs = vdrv.getFirstSoTime();
					Instant insta = fs.atZone(ZoneId.systemDefault()).toInstant();
					Date firstso = Date.from(insta);
					firstsoDateCell.setCellValue(firstso);
					firstsoDateCell.setCellStyle(dateCellStyle);
				} else {
					firstsoDateCell.setCellValue("");
				}

				HSSFCell lastsoDateCell = row.createCell(7);
				if (vdrv.getLastSoTime() != null) {
					LocalDateTime ls = vdrv.getLastSoTime();
					Instant ista = ls.atZone(ZoneId.systemDefault()).toInstant();
					Date lastso = Date.from(ista);
					lastsoDateCell.setCellValue(lastso);
					lastsoDateCell.setCellStyle(dateCellStyle);
				} else {
					lastsoDateCell.setCellValue("");
				}
				row.createCell(8).setCellValue(vdrv.getTotalSo());
				if (vdrv.getLedgerCount() != null) {
					row.createCell(9).setCellValue(vdrv.getLedgerCount());
				} else {
					row.createCell(9).setCellValue(0);
				}
				row.createCell(10).setCellValue(vdrv.getTotalKG());
				row.createCell(11).setCellValue(vdrv.getTotalSaleOrderAmount());
				row.createCell(12).setCellValue(vdrv.getTotalReceiptAmount());

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

	private List<Long> getUserIdsUnderCurrentUser(String employeePid, boolean inclSubordinate) {
		List<Long> userIds = Collections.emptyList();
		if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (employeePid.equals("Dashboard Employee")) {
//				List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
//				List<Long> dashboardUserIds = dashboardUsers.stream().map(User::getId).collect(Collectors.toList());
				Set<Long> dashboardUserIds = dashboardUserRepository.findUserIdsByCompanyId();
				Set<Long> uniqueIds = new HashSet<>();
				log.info("dashboard user ids empty: " + dashboardUserIds.isEmpty());
				if (!dashboardUserIds.isEmpty()) {
					log.info(" user ids empty: " + userIds.isEmpty());
					log.info("userids :" + userIds.toString());
					if (!userIds.isEmpty()) {
						for (Long uid : userIds) {
							for (Long sid : dashboardUserIds) {
								if (uid != null && uid.equals(sid)) {
									uniqueIds.add(sid);
								}
							}
						}
					} else {
						userIds = new ArrayList<>(dashboardUserIds);
					}
				}
				if (!uniqueIds.isEmpty()) {
					userIds = new ArrayList<>(uniqueIds);
				}
			} else {
				if (userIds.isEmpty()) {
					// List<User> users = userRepository.findAllByCompanyId();
					// userIds = users.stream().map(User::getId).collect(Collectors.toList());
					userIds = userRepository.findAllUserIdsByCompanyId();
				}
			}
		} else {
			if (inclSubordinate) {
				userIds = employeeHierarchyService.getEmployeeSubordinateIds(employeePid);
				System.out.println("Testing start for Activity Transaction");
				System.out.println("employeePid:" + employeePid);
				System.out.println("userIds:" + userIds.toString());
				System.out.println("Testing end for Activity Transaction");
			} else {
				Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
				if (opEmployee.isPresent()) {
					userIds = Arrays.asList(opEmployee.get().getUser().getId());
				}
				System.out.println("Testing start for Activity Transaction");
				System.out.println("--------------------------------------");
				System.out.println("employeePid:" + employeePid);
				System.out.println("UserIds:" + userIds.toString());
				System.out.println("Testing end for Activity Transaction");
			}
		}

		return userIds;
	}

}
