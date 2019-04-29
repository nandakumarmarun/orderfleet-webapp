
package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import com.orderfleet.webapp.service.PostDatedVoucherService;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;

/**
 * @author Anish
 * @since 14 September 2018
 *
 */
@Controller
@RequestMapping("/web")
public class PostDatedVoucherResources {

	private final Logger log = LoggerFactory.getLogger(PostDatedVoucherResources.class);

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private PostDatedVoucherService postDatedVoucherService;

	@Timed
	@RequestMapping(value = "/post-dated-vouchers", method = RequestMethod.GET)
	public String getPostDatedVouchers(Model model) {
		log.debug("Web request to get a page of Post dated vouchers");
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		return "company/postDatedVouchers";
	}

	@Timed
	@GetMapping("/post-dated-vouchers/load")
	public ResponseEntity<Map<String, List<PostDatedVoucherDTO>>> postDatedVoucherReport(
			@RequestParam String accountPid) {
		log.debug("REST request to get all post dated vouchers");
		List<PostDatedVoucherDTO> postDatedVoucherDTOs = null;
		if (accountPid.equals("no")) {
			postDatedVoucherDTOs = postDatedVoucherService.findAllPostDatedVouchers();
		} else {
			postDatedVoucherDTOs = postDatedVoucherService.findAllPostDatedVoucherByAccountProfilePid(accountPid);
		}
		Map<String, List<PostDatedVoucherDTO>> result = postDatedVoucherDTOs.parallelStream()
				.collect(Collectors.groupingBy(PostDatedVoucherDTO::getAccountProfilePid));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/post-dated-vouchers//download-post-dated-vouchers-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadProductProfileXls(@RequestParam String accountPid, HttpServletResponse response) {
		List<PostDatedVoucherDTO> postDatedVoucherDTOs = new ArrayList<>();
		if (accountPid.equals("no")) {
			postDatedVoucherDTOs = postDatedVoucherService.findAllPostDatedVouchers();
		} else {
			postDatedVoucherDTOs = postDatedVoucherService.findAllPostDatedVoucherByAccountProfilePid(accountPid);
		}

		buildExcelDocument(postDatedVoucherDTOs, response);
	}

	private void buildExcelDocument(List<PostDatedVoucherDTO> postDatedVoucherDTOs, HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "post_dated_vouchers" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Account", "Document Number", "Date", "Amount", "Remarks" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, postDatedVoucherDTOs);
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

	private void createReportRows(HSSFSheet worksheet, List<PostDatedVoucherDTO> postDatedVoucherDTOs) {
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
		postDatedVoucherDTOs = postDatedVoucherDTOs.stream()
				.sorted(Comparator.comparing(PostDatedVoucherDTO::getAccountProfileName)).collect(Collectors.toList());
		for (PostDatedVoucherDTO pdv : postDatedVoucherDTOs) {

			HSSFRow row = worksheet.createRow(rowNum++);
			row.createCell(0).setCellValue(pdv.getAccountProfileName().replace("#13;#10;", " "));
			row.createCell(1).setCellValue(pdv.getReferenceDocumentNumber());
			row.createCell(2).setCellValue(pdv.getReferenceDocumentDate().toString());
			row.createCell(3).setCellValue(pdv.getReferenceDocumentAmount());
			row.createCell(4).setCellValue(pdv.getRemark());

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
