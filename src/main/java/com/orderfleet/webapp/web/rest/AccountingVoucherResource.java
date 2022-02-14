package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountingVoucherHeaderService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherXlsDownloadDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;

/**
 * Web controller for managing AccountingVoucher.
 * 
 * @author Muhammed Riyas T
 * @since July 21, 2016
 */
@Controller
@RequestMapping("/web")
public class AccountingVoucherResource {

	private final Logger log = LoggerFactory.getLogger(AccountingVoucherResource.class);

	@Inject
	private AccountingVoucherHeaderService accountingVoucherService;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private DocumentService documentService;

	@Inject
	private EmployeeProfileService employeeProfileService;
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private LocationAccountProfileService locationAccountProfileService;
	
	/**
	 * GET /accounting-vouchers : get all the accounting vouchers.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         accounting vouchers in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/accounting-vouchers", method = RequestMethod.GET)
	public String getAllAccountingVouchers(Pageable pageable, Model model) {
		log.debug("Web request to get a page of accounting vouchers");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if(userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
			model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
		}else{
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
			model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
		}
		model.addAttribute("documents", documentService.findAllByDocumentType(DocumentType.ACCOUNTING_VOUCHER));
		return "company/accountingVouchers";
	}

	/**
	 * GET /accounting-vouchers/:id : get the "id" AccountingVoucher.
	 *
	 * @param id
	 *            the id of the AccountingVoucherDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         AccountingVoucherDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/accounting-vouchers/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountingVoucherHeaderDTO> getAccountingVoucher(@PathVariable String pid) {
		log.debug("Web request to get accountingVoucherDTO by pid : {}", pid);
		return accountingVoucherService.findOneByPid(pid)
				.map(accountingVoucherDTO -> new ResponseEntity<>(accountingVoucherDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@Timed
	@RequestMapping(value = "/accounting-vouchers/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AccountingVoucherHeaderDTO>> filterAccountingVouchers(
			@RequestParam("employeePid") String employeePid, @RequestParam("accountPid") String accountPid,
			@RequestParam("documentPid") String documentPid, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter accounting vouchers");
		List<AccountingVoucherHeaderDTO> accountingVouchers = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			accountingVouchers = getFilterData(employeePid, accountPid, documentPid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			accountingVouchers = getFilterData(employeePid, accountPid, documentPid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			accountingVouchers = getFilterData(employeePid, accountPid, documentPid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			accountingVouchers = getFilterData(employeePid, accountPid, documentPid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			accountingVouchers = getFilterData(employeePid, accountPid, documentPid, fromDateTime, toDateTime);
		}
		return new ResponseEntity<>(accountingVouchers, HttpStatus.OK);
	}

	private List<AccountingVoucherHeaderDTO> getFilterData(String employeePid, String accountPid, String documentPid,
			LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<AccountingVoucherHeaderDTO> accountingVouchers = new ArrayList<>();
		EmployeeProfileDTO employeeProfileDTO=new EmployeeProfileDTO();
		if(!employeePid.equals("no")){
		 employeeProfileDTO=employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid="no";
		if(employeeProfileDTO.getPid()!=null){
			userPid=employeeProfileDTO.getUserPid();
		}
		if (userPid.equals("no") && accountPid.equals("no") && documentPid.equals("no")) {
			String id="ACC_QUERY_103";
			String description="get all AccVoucher by companyid Between two created date";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
			accountingVouchers = accountingVoucherService.findAllByCompanyIdAndDateBetween(fromDate, toDate);
		} else if (!userPid.equals("no") && !accountPid.equals("no") && !documentPid.equals("no")) {
			accountingVouchers = accountingVoucherService.findAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetween(
					userPid, accountPid, documentPid, fromDate, toDate);
		} else if (!userPid.equals("no") && !accountPid.equals("no") && documentPid.equals("no")) {
			String id="ACC_QUERY_104";
			String description="get all AccVoucher ByCompanyIdUserPidAccountPidAndDateBetweenOrderByCreatedDate";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
			accountingVouchers = accountingVoucherService.findAllByCompanyIdUserPidAccountPidAndDateBetween(userPid,
					accountPid, fromDate, toDate);
		} else if (!userPid.equals("no") && accountPid.equals("no") && !documentPid.equals("no")) {
			accountingVouchers = accountingVoucherService.findAllByCompanyIdUserPidDocumentPidAndDateBetween(userPid,
					documentPid, fromDate, toDate);
		} else if (userPid.equals("no") && !accountPid.equals("no") && !documentPid.equals("no")) {
			accountingVouchers = accountingVoucherService
					.findAllByCompanyIdAccountPidDocumentPidAndDateBetween(accountPid, documentPid, fromDate, toDate);
		} else if (!userPid.equals("no") && accountPid.equals("no") && documentPid.equals("no")) {
			String id="ACC_QUERY_105";
			String description="get all AccVoucher ByCompanyId,UserPid,AndDateBetweenOrderByCreatedDate";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
			accountingVouchers = accountingVoucherService.findAllByCompanyIdUserPidAndDateBetween(userPid, fromDate,
					toDate);
		} else if (userPid.equals("no") && !accountPid.equals("no") && documentPid.equals("no")) {
			String id="ACC_QUERY_106";
			String description="get all AccVoucher ByCompanyId  AccountPid AndDateBetweenOrderByCreatedDateDesc";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
			accountingVouchers = accountingVoucherService.findAllByCompanyIdAccountPidAndDateBetween(accountPid,
					fromDate, toDate);
		} else if (userPid.equals("no") && accountPid.equals("no") && !documentPid.equals("no")) {
			accountingVouchers = accountingVoucherService.findAllByCompanyIdDocumentPidAndDateBetween(documentPid,
					fromDate, toDate);
		}
		
		return accountingVouchers;
	}

	@RequestMapping(value = "/accounting-vouchers/download-accounting-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadInventoryXls(@RequestParam("accountingVoucherHeaderPids") String accountingVoucherHeaderPids,
			HttpServletResponse response) {

		String[] allAccountingVoucherHeaderPids = accountingVoucherHeaderPids.split(",");
		List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs = new ArrayList<>();
		for (String accountingVoucherHeaderPid : allAccountingVoucherHeaderPids) {
			Optional<AccountingVoucherHeaderDTO> accountingVoucher = accountingVoucherService
					.findOneByPid(accountingVoucherHeaderPid);
			if (accountingVoucher.isPresent()) {
				accountingVoucherHeaderDTOs.add(accountingVoucher.get());
			}
		}
		if (!accountingVoucherHeaderDTOs.isEmpty()) {
			createXmlFileToDownload(accountingVoucherHeaderDTOs, response);
		}
	}

	private void createXmlFileToDownload(List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs,
			HttpServletResponse response) {
		log.debug("Downloading Excel report");

		// 1. Create new workbook
		HSSFWorkbook workbook = new HSSFWorkbook();

		// 2. Create new worksheet
		HSSFSheet worksheet = workbook.createSheet("POI Worksheet");

		// 4. Build layout Build title, date, and column headers
		buildReport(worksheet);

		// 5. Fill report
		fillReport(worksheet, createAccountingVoucherxmlDTO(accountingVoucherHeaderDTOs));

		// 6. Set the response properties
		String fileName = "Receipt" + ".xls";
		response.setHeader("Content-Disposition", "inline; filename=" + fileName);
		// Make sure to set the correct content type
		response.setContentType("application/vnd.ms-excel");

		// 7. Write to the output stream
		write(response, worksheet);
	}

	private void buildReport(HSSFSheet worksheet) {

		int startRowIndex = 0;
		int startColIndex = 0;
		// Create cell style for the body
		HSSFCellStyle bodyCellStyle = worksheet.getWorkbook().createCellStyle();
		bodyCellStyle.setWrapText(true);

		// Create a new row
		HSSFRow row = worksheet.createRow((short) startRowIndex);

		HSSFCell cell1 = row.createCell(startColIndex + 0);
		cell1.setCellValue("Account Profile");
		cell1.setCellStyle(bodyCellStyle);

		HSSFCell cell2 = row.createCell(startColIndex + 1);
		cell2.setCellValue("Phone Number");
		cell2.setCellStyle(bodyCellStyle);

		HSSFCell cell3 = row.createCell(startColIndex + 2);
		cell3.setCellValue("Employee");
		cell3.setCellStyle(bodyCellStyle);

		HSSFCell cell4 = row.createCell(startColIndex + 3);
		cell4.setCellValue("Send Date");
		cell4.setCellStyle(bodyCellStyle);

		HSSFCell cell5 = row.createCell(startColIndex + 4);
		cell5.setCellValue("Received Date");
		cell5.setCellStyle(bodyCellStyle);

		HSSFCell cell6 = row.createCell(startColIndex + 5);
		cell6.setCellValue("Check Date");
		cell6.setCellStyle(bodyCellStyle);

		HSSFCell cell7 = row.createCell(startColIndex + 6);
		cell7.setCellValue("Mode");
		cell7.setCellStyle(bodyCellStyle);
		
		HSSFCell cell8 = row.createCell(startColIndex + 7);
		cell8.setCellValue("Check Number");
		cell8.setCellStyle(bodyCellStyle);

		HSSFCell cell9 = row.createCell(startColIndex + 8);
		cell9.setCellValue("Amount");
		cell9.setCellStyle(bodyCellStyle);

		HSSFCell cell10 = row.createCell(startColIndex + 9);
		cell10.setCellValue("Bank Name");
		cell10.setCellStyle(bodyCellStyle);

		HSSFCell cell11 = row.createCell(startColIndex + 10);
		cell11.setCellValue("Expense Type");
		cell11.setCellStyle(bodyCellStyle);

		HSSFCell cell12 = row.createCell(startColIndex + 11);
		cell12.setCellValue("Remarks");
		cell12.setCellStyle(bodyCellStyle);

	}

	public void write(HttpServletResponse response, HSSFSheet worksheet) {
		log.debug("Writing report to the stream");
		try {
			// Retrieve the output stream
			ServletOutputStream outputStream = response.getOutputStream();
			// Write to the output stream
			worksheet.getWorkbook().write(outputStream);
			// Flush the stream
			outputStream.flush();
		} catch (Exception e) {
			log.error("Unable to write report to the output stream");
		}
	}

	private List<AccountingVoucherXlsDownloadDTO> createAccountingVoucherxmlDTO(
			List<AccountingVoucherHeaderDTO> accountingVoucherHeaderDTOs) {
		List<AccountingVoucherXlsDownloadDTO> accountingVoucherXlsDownloadDTOs = new ArrayList<>();
		for (AccountingVoucherHeaderDTO accountingVoucherHeaderDTO : accountingVoucherHeaderDTOs) {
			for (AccountingVoucherDetailDTO accountingVoucherDetailDTO : accountingVoucherHeaderDTO
					.getAccountingVoucherDetails()) {
				AccountingVoucherXlsDownloadDTO accountingVoucherXlsDownloadDTO = new AccountingVoucherXlsDownloadDTO(
						accountingVoucherHeaderDTO, accountingVoucherDetailDTO);
				accountingVoucherXlsDownloadDTOs.add(accountingVoucherXlsDownloadDTO);
			}
		}
		return accountingVoucherXlsDownloadDTOs;
	}

	public static void fillReport(HSSFSheet worksheet, List<AccountingVoucherXlsDownloadDTO> xlsDownloadDTOs) {
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
			cell1.setCellValue(xlsDownloadDTOs.get(i).getAccountProfileName());
			cell1.setCellStyle(bodyCellStyle);

			HSSFCell cell2 = row.createCell(startColIndex + 1);
			cell2.setCellValue(xlsDownloadDTOs.get(i).getPhone());
			cell2.setCellStyle(bodyCellStyle);

			HSSFCell cell3 = row.createCell(startColIndex + 2);
			cell3.setCellValue(xlsDownloadDTOs.get(i).getEmployeeName());
			cell3.setCellStyle(bodyCellStyle);

			HSSFCell cell4 = row.createCell(startColIndex + 3);
			if (xlsDownloadDTOs.get(i).getDocumentDate() != null) {
				String date = xlsDownloadDTOs.get(i).getDocumentDate().toLocalDate() + " "
						+ xlsDownloadDTOs.get(i).getDocumentDate().toLocalTime();
				cell4.setCellValue(date);
			} else {
				cell4.setCellValue("");
			}
			cell4.setCellStyle(bodyCellStyle);

			HSSFCell cell5 = row.createCell(startColIndex + 4);
			if (xlsDownloadDTOs.get(i).getCreatedDate() != null) {
				String date = xlsDownloadDTOs.get(i).getCreatedDate().toLocalDate() + " "
						+ xlsDownloadDTOs.get(i).getCreatedDate().toLocalTime();
				cell5.setCellValue(date);
			} else {
				cell5.setCellValue("");
			}
			cell5.setCellStyle(bodyCellStyle);

			HSSFCell cell6 = row.createCell(startColIndex + 5);
			if (xlsDownloadDTOs.get(i).getInstrumentDate() != null) {
				String date = xlsDownloadDTOs.get(i).getInstrumentDate().toLocalDate() + " "
						+ xlsDownloadDTOs.get(i).getInstrumentDate().toLocalTime();
				cell6.setCellValue(date);
			} else {
				cell6.setCellValue("");
			}
			cell6.setCellStyle(bodyCellStyle);

			HSSFCell cell7 = row.createCell(startColIndex + 6);
			cell7.setCellValue(xlsDownloadDTOs.get(i).getMode().toString());
			cell7.setCellStyle(bodyCellStyle);

			HSSFCell cell8 = row.createCell(startColIndex + 7);
			cell8.setCellValue(xlsDownloadDTOs.get(i).getInstrumentNumber());
			cell8.setCellStyle(bodyCellStyle);

			HSSFCell cell9 = row.createCell(startColIndex + 8);
			cell9.setCellValue(xlsDownloadDTOs.get(i).getAmount());
			cell9.setCellStyle(bodyCellStyle);
			
			HSSFCell cell10 = row.createCell(startColIndex + 9);
			cell10.setCellValue(xlsDownloadDTOs.get(i).getBankName());
			cell10.setCellStyle(bodyCellStyle);

			HSSFCell cell11 = row.createCell(startColIndex + 10);
			cell11.setCellValue(xlsDownloadDTOs.get(i).getIncomeExpenseHeadName());
			cell11.setCellStyle(bodyCellStyle);

			HSSFCell cell12 = row.createCell(startColIndex + 11);
			cell12.setCellValue(xlsDownloadDTOs.get(i).getRemarks());
			cell12.setCellStyle(bodyCellStyle);

		}
	}
}
