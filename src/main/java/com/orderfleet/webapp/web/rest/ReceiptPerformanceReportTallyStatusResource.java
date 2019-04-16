package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

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
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
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
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherXlsDownloadDTO;

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

	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String CUSTOM = "CUSTOM";

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherService;

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

	/**
	 * GET /receipt : get all the inventory vouchers.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of inventory
	 *         vouchers in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
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
			List<Object[]> accountPidNames = locationAccountProfileRepository
					.findAccountProfilesByLocationIdIn(locationIds);
			int size = accountPidNames.size();
			List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				AccountProfileDTO accountProfileDTO = new AccountProfileDTO();
				accountProfileDTO.setPid(accountPidNames.get(i)[0].toString());
				accountProfileDTO.setName(accountPidNames.get(i)[1].toString());
				accountProfileDTOs.add(accountProfileDTO);
			}
			model.addAttribute("accounts", accountProfileDTOs);
		}
		model.addAttribute("documents",
				documentRepository.findAllDocumentsByCompanyPidAndDocumentType(DocumentType.ACCOUNTING_VOUCHER,
						companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()).getPid()));
		return "company/receiptDownloadStatus";
	}

	/**
	 * GET /receipt/:id : get the "id" InventoryVoucher.
	 *
	 * @param id
	 *            the id of the InventoryVoucherDTO to retrieve
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
		List<AccountingVoucherHeaderDTO> receiptStatusDTOs = getFilterData(employeePids, documentPid, tallyDownloadStatus,
				accountPid, fDate, tDate);
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
		case "ALL":
			tallyStatus = Arrays.asList(TallyDownloadStatus.COMPLETED, TallyDownloadStatus.PROCESSING,
					TallyDownloadStatus.PENDING);
			break;
		}

		List<AccountingVoucherHeaderDTO> accountVouchers = new ArrayList<>();
		if ("-1".equals(accountPid)) {
			accountVouchers = accountingVoucherHeaderService
					.getAllByCompanyIdUserPidDocumentPidAndDateBetween(userPids,
							documentPids, tallyStatus, fromDate, toDate);
		} else {
			accountVouchers = accountingVoucherHeaderService
					.getAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetween(
							userPids, accountPid, documentPids, tallyStatus, fromDate, toDate);
		}
		if (accountVouchers.isEmpty()) {
			return Collections.emptyList();
		} else {
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
	public void downloadInventoryXls(@RequestParam("inventoryVoucherHeaderPids") String[] inventoryVoucherHeaderPids,
			HttpServletResponse response) {
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = inventoryVoucherService
				.findAllByCompanyIdAndInventoryPidIn(Arrays.asList(inventoryVoucherHeaderPids));
		if (inventoryVoucherHeaderDTOs.isEmpty()) {
			return;
		}
		buildExcelDocument(inventoryVoucherHeaderDTOs, response);
	}

	private void buildExcelDocument(List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs,
			HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "SalesOrder" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Order No", "Salesman", "Order Date", "Customer", "Supplier", "Product Name",
				"Quantity", "Unit Quantity", "Total Quantity", "Free Quantity", "Selling Rate", "Discount Amount",
				"Discount Percentage", "Tax Amount", "Row Total" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, inventoryVoucherHeaderDTOs);
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

	private void createReportRows(HSSFSheet worksheet, List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs) {
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
		for (InventoryVoucherHeaderDTO ivh : inventoryVoucherHeaderDTOs) {
			for (InventoryVoucherDetailDTO ivd : ivh.getInventoryVoucherDetails()) {
				HSSFRow row = worksheet.createRow(rowNum++);
				row.createCell(0).setCellValue(ivh.getDocumentNumberLocal());
				row.createCell(1).setCellValue(ivh.getUserName());
				HSSFCell docDateCell = row.createCell(2);
				docDateCell.setCellValue(ivh.getDocumentDate().toString());
				docDateCell.setCellStyle(dateCellStyle);
				row.createCell(3).setCellValue(ivh.getReceiverAccountName());
				row.createCell(4).setCellValue(ivh.getSupplierAccountName());

				row.createCell(5).setCellValue(ivd.getProductName());
				row.createCell(6).setCellValue(ivd.getQuantity());
				row.createCell(7).setCellValue(ivd.getProductUnitQty());
				row.createCell(8).setCellValue((ivd.getQuantity() * ivd.getProductUnitQty()));
				row.createCell(9).setCellValue(ivd.getFreeQuantity());
				row.createCell(10).setCellValue(ivd.getSellingRate());
				row.createCell(11).setCellValue(ivd.getDiscountAmount());
				row.createCell(12).setCellValue(ivd.getDiscountPercentage());
				row.createCell(13).setCellValue(ivd.getTaxAmount());
				row.createCell(14).setCellValue(ivd.getRowTotal());
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

	public static void fillReport(HSSFSheet worksheet, List<InventoryVoucherXlsDownloadDTO> xlsDownloadDTOs) {
		// Row offset
		int startRowIndex = 1;
		int startColIndex = 0;

		// Create cell style for the body
		HSSFCellStyle bodyCellStyle = worksheet.getWorkbook().createCellStyle();
		bodyCellStyle.setWrapText(true);

		// Create body
		for (int i = 0; i < xlsDownloadDTOs.size(); i++) {
			// Create a new row
			startRowIndex = startRowIndex + 1;
			HSSFRow row = worksheet.createRow((short) startRowIndex);

			HSSFCell cell1 = row.createCell(startColIndex + 0);
			cell1.setCellValue(xlsDownloadDTOs.get(i).getDocumentNumberLocal());
			cell1.setCellStyle(bodyCellStyle);

			HSSFCell cell2 = row.createCell(startColIndex + 1);
			cell2.setCellValue(xlsDownloadDTOs.get(i).getDocumentName());
			cell2.setCellStyle(bodyCellStyle);

			HSSFCell cell3 = row.createCell(startColIndex + 2);
			cell3.setCellValue(xlsDownloadDTOs.get(i).getUserName());
			cell3.setCellStyle(bodyCellStyle);

			HSSFCell cell4 = row.createCell(startColIndex + 3);
			if (xlsDownloadDTOs.get(i).getDocumentDate() != null) {
				cell4.setCellValue(xlsDownloadDTOs.get(i).getDocumentDate().toLocalDate().toString());
			} else {
				cell4.setCellValue("");
			}
			cell4.setCellStyle(bodyCellStyle);

			HSSFCell cell5 = row.createCell(startColIndex + 4);
			cell5.setCellValue(xlsDownloadDTOs.get(i).getReceiverAccountName());
			cell5.setCellStyle(bodyCellStyle);

			HSSFCell cell6 = row.createCell(startColIndex + 5);
			cell6.setCellValue(xlsDownloadDTOs.get(i).getSupplierAccountName());
			cell6.setCellStyle(bodyCellStyle);

			HSSFCell cell7 = row.createCell(startColIndex + 6);
			cell7.setCellValue(xlsDownloadDTOs.get(i).getDocumentTotal());
			cell7.setCellStyle(bodyCellStyle);

			HSSFCell cell8 = row.createCell(startColIndex + 7);
			cell8.setCellValue(xlsDownloadDTOs.get(i).getDocumentVolume());
			cell8.setCellStyle(bodyCellStyle);

			HSSFCell cell9 = row.createCell(startColIndex + 8);
			cell9.setCellValue(xlsDownloadDTOs.get(i).getDocDiscountAmount());
			cell9.setCellStyle(bodyCellStyle);

			HSSFCell cell10 = row.createCell(startColIndex + 9);
			cell10.setCellValue(xlsDownloadDTOs.get(i).getDocDiscountPercentage());
			cell10.setCellStyle(bodyCellStyle);

			HSSFCell cell11 = row.createCell(startColIndex + 10);
			cell11.setCellValue(xlsDownloadDTOs.get(i).getProductName());
			cell11.setCellStyle(bodyCellStyle);

			HSSFCell cell12 = row.createCell(startColIndex + 11);
			cell12.setCellValue(xlsDownloadDTOs.get(i).getQuantity());
			cell12.setCellStyle(bodyCellStyle);

			HSSFCell cell13 = row.createCell(startColIndex + 12);
			cell13.setCellValue(xlsDownloadDTOs.get(i).getFreeQuantity());
			cell13.setCellStyle(bodyCellStyle);

			HSSFCell cell14 = row.createCell(startColIndex + 13);
			cell14.setCellValue(xlsDownloadDTOs.get(i).getSellingRate());
			cell14.setCellStyle(bodyCellStyle);

			HSSFCell cell15 = row.createCell(startColIndex + 14);
			cell15.setCellValue(xlsDownloadDTOs.get(i).getTaxAmount());
			cell15.setCellStyle(bodyCellStyle);

			HSSFCell cell16 = row.createCell(startColIndex + 15);
			cell16.setCellValue(xlsDownloadDTOs.get(i).getDiscountAmount());
			cell16.setCellStyle(bodyCellStyle);

			HSSFCell cell17 = row.createCell(startColIndex + 16);
			cell17.setCellValue(xlsDownloadDTOs.get(i).getRowTotal());
			cell17.setCellStyle(bodyCellStyle);
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

}
