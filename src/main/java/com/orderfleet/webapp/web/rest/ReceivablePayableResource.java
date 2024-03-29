package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;

/**
 * Web controller for managing Receivable Payable.
 * 
 * @author Muhammed Riyas T
 * @since November 5, 2016
 */
@Controller
@RequestMapping("/web")
public class ReceivablePayableResource {

	private final Logger log = LoggerFactory.getLogger(ReceivablePayableResource.class);

	@Inject
	private ReceivablePayableService receivablePayableService;

	@Inject
	private AccountProfileService accountProfileService;

	/**
	 * GET /receivable-payables : get receivable-payables.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) in body
	 */
	@Timed
	@RequestMapping(value = "/receivable-payables", method = RequestMethod.GET)
	public String getReceivablePayables(Model model) {
		log.debug("Web request to get a page of receivable PayableS");
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		return "company/receivablePayables";
	}

	/**
	 * GET receivablePayable : get all receivablePayable.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         receivablePayable in body
	 */
	@Timed
	@GetMapping("/receivable-payables/load")
	public ResponseEntity<Map<String, List<ReceivablePayableDTO>>> receivablePayableReport(
			@RequestParam String accountPid, @RequestParam("filterBy") String filterBy, @RequestParam String fromDate,
			@RequestParam String toDate, @RequestParam String billedDueDateFilter) {
		log.debug("REST request to get all receivablePayable");

		List<ReceivablePayableDTO> receivablePayableDTOs = null;

//		if (accountPid.equals("no")) {
//			receivablePayableDTOs = receivablePayableService.findAllByCompany();
//
//		} else {
//			receivablePayableDTOs = receivablePayableService.findAllByAccountProfilePid(accountPid);
//
//		}

		if (filterBy.equals("TODAY")) {

			receivablePayableDTOs = getFilterData(accountPid, LocalDate.now(), LocalDate.now(), billedDueDateFilter);

		} else if (filterBy.equals("YESTERDAY")) {

			LocalDate yeasterday = LocalDate.now().minusDays(1);
			receivablePayableDTOs = getFilterData(accountPid, yeasterday, yeasterday, billedDueDateFilter);

		} else if (filterBy.equals("WTD")) {

			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			receivablePayableDTOs = getFilterData(accountPid, weekStartDate, LocalDate.now(), billedDueDateFilter);

		} else if (filterBy.equals("MTD")) {

			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			receivablePayableDTOs = getFilterData(accountPid, monthStartDate, LocalDate.now(), billedDueDateFilter);

		} else if (filterBy.equals("CUSTOM")) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			receivablePayableDTOs = getFilterData(accountPid, fromDateTime, toFateTime, billedDueDateFilter);

		} else if (filterBy.equals("SINGLE")) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			receivablePayableDTOs = getFilterData(accountPid, fromDateTime, fromDateTime, billedDueDateFilter);

		} else if (filterBy.equals("0TO15DAYS")) {

			LocalDate tDate = LocalDate.now();
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivablePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		} else if (filterBy.equals("16TO30DAYS")) {

			LocalDate tDate1 = LocalDate.now();
			Period days = Period.ofDays(16);
			LocalDate tDate = tDate1.minus(days);
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivablePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		} else if (filterBy.equals("31TO45DAYS")) {

			LocalDate tDate1 = LocalDate.now();
			Period days = Period.ofDays(31);
			LocalDate tDate = tDate1.minus(days);
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivablePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		} else if (filterBy.equals("46TO60DAYS")) {

			LocalDate tDate1 = LocalDate.now();
			Period days = Period.ofDays(46);
			LocalDate tDate = tDate1.minus(days);
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivablePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		} else if (filterBy.equals("61TO75DAYS")) {

			LocalDate tDate1 = LocalDate.now();
			Period days = Period.ofDays(61);
			LocalDate tDate = tDate1.minus(days);
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivablePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		} else if (filterBy.equals("75TO90DAYS")) {

			LocalDate tDate1 = LocalDate.now();
			Period days = Period.ofDays(76);
			LocalDate tDate = tDate1.minus(days);
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivablePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		}

		Map<String, List<ReceivablePayableDTO>> result = receivablePayableDTOs.parallelStream()
				.collect(Collectors.groupingBy(ReceivablePayableDTO::getAccountPid));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	private List<ReceivablePayableDTO> getFilterData(String accountPid, LocalDate fromDate, LocalDate toDate,
			String billedDueDateFilter) {

		/*
		 * LocalDateTime fromDate = fDate.atTime(0, 0); LocalDateTime toDate =
		 * tDate.atTime(23, 59);
		 */
		log.info("From Date  ====> " + fromDate + "\t To Date ====>" + toDate);

		List<ReceivablePayableDTO> receivablePayableDTOs = null;

		if (accountPid.equals("no")) {
			// receivablePayableDTOs = receivablePayableService.findAllByCompany();

			receivablePayableDTOs = receivablePayableService.findAllByCompanyAndDateBetween(fromDate, toDate);

		} else {
			// receivablePayableDTOs=receivablePayableService.findAllByAccountProfilePid(accountPid);

			receivablePayableDTOs = receivablePayableService.findAllByAccountProfilePidAndDateBetween(accountPid,
					fromDate, toDate);
		}
		return receivablePayableDTOs;
	}

	@RequestMapping(value = "/receivable-payables/download-receivalbes-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadProductProfileXls(@RequestParam String accountPid, @RequestParam String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate, @RequestParam String billedDueDateFilter,
			HttpServletResponse response) {
		List<ReceivablePayableDTO> receivalbePayableDTOs = new ArrayList<ReceivablePayableDTO>();

//		if (accountPid.equals("no")) {
//			receivalbePayableDTOs = receivablePayableService.findAllByCompany();
//		} else {
//			receivalbePayableDTOs = receivablePayableService.findAllByAccountProfilePid(accountPid);
//		}

		if (filterBy.equals("TODAY")) {

			receivalbePayableDTOs = getFilterData(accountPid, LocalDate.now(), LocalDate.now(), billedDueDateFilter);

		} else if (filterBy.equals("YESTERDAY")) {

			LocalDate yeasterday = LocalDate.now().minusDays(1);
			receivalbePayableDTOs = getFilterData(accountPid, yeasterday, yeasterday, billedDueDateFilter);

		} else if (filterBy.equals("WTD")) {

			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			receivalbePayableDTOs = getFilterData(accountPid, weekStartDate, LocalDate.now(), billedDueDateFilter);

		} else if (filterBy.equals("MTD")) {

			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			receivalbePayableDTOs = getFilterData(accountPid, monthStartDate, LocalDate.now(), billedDueDateFilter);

		} else if (filterBy.equals("CUSTOM")) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			receivalbePayableDTOs = getFilterData(accountPid, fromDateTime, toFateTime, billedDueDateFilter);

		} else if (filterBy.equals("SINGLE")) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			receivalbePayableDTOs = getFilterData(accountPid, fromDateTime, fromDateTime, billedDueDateFilter);

		} else if (filterBy.equals("0TO15DAYS")) {

			LocalDate tDate = LocalDate.now();
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivalbePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		} else if (filterBy.equals("16TO30DAYS")) {

			LocalDate tDate1 = LocalDate.now();
			Period days = Period.ofDays(30);
			LocalDate tDate = tDate1.minus(days);
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivalbePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		} else if (filterBy.equals("31TO45DAYS")) {

			LocalDate tDate1 = LocalDate.now();
			Period days = Period.ofDays(45);
			LocalDate tDate = tDate1.minus(days);
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivalbePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		} else if (filterBy.equals("46TO60DAYS")) {

			LocalDate tDate1 = LocalDate.now();
			Period days = Period.ofDays(60);
			LocalDate tDate = tDate1.minus(days);
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivalbePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		} else if (filterBy.equals("61TO75DAYS")) {

			LocalDate tDate1 = LocalDate.now();
			Period days = Period.ofDays(75);
			LocalDate tDate = tDate1.minus(days);
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivalbePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		} else if (filterBy.equals("75TO90DAYS")) {

			LocalDate tDate1 = LocalDate.now();
			Period days = Period.ofDays(90);
			LocalDate tDate = tDate1.minus(days);
			Period days_15 = Period.ofDays(15);
			LocalDate fDate = tDate.minus(days_15);
			receivalbePayableDTOs = getFilterData(accountPid, fDate, tDate, billedDueDateFilter);

		}

		buildExcelDocument(receivalbePayableDTOs, response);
	}

	private void buildExcelDocument(List<ReceivablePayableDTO> receivalbePayableDTOs, HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "receivalbes" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Account", "Voucher Number", "Voucher Date", "Voucher Amount", "Balance Amount",
				"Type" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, receivalbePayableDTOs);
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
			log.error("IOException on downloading Product profiles {}", ex.getMessage());
		}
	}

	private void createReportRows(HSSFSheet worksheet, List<ReceivablePayableDTO> receivalbePayableDTOs) {
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
		receivalbePayableDTOs = receivalbePayableDTOs.stream()
				.sorted(Comparator.comparing(ReceivablePayableDTO::getAccountName)).collect(Collectors.toList());
		for (ReceivablePayableDTO rp : receivalbePayableDTOs) {

			HSSFRow row = worksheet.createRow(rowNum++);
			row.createCell(0).setCellValue(rp.getAccountName().replace("#13;#10;", " "));
			row.createCell(1).setCellValue(rp.getReferenceDocumentNumber());
			row.createCell(2).setCellValue(rp.getReferenceDocumentDate().toString());
			row.createCell(3).setCellValue(rp.getReferenceDocumentAmount());
			row.createCell(4).setCellValue(rp.getReferenceDocumentBalanceAmount());
			row.createCell(5).setCellValue(rp.getReceivablePayableType().name());
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
}
