package com.orderfleet.webapp.web.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64.OutputStream;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.CompanyService;
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

/**
 * Web controller for managing InventoryVoucher.
 * 
 * @author Muhammed Riyas T
 * @since July 21, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesPerformanceReportTallyStatusResource {

	private final Logger log = LoggerFactory.getLogger(SalesPerformanceReportTallyStatusResource.class);

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
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private CompanyRepository CompanyRepository;

	@Inject
	private CompanyConfigurationRepository CompanyConfigurationRepository;

	/**
	 * GET /primary-sales-performance : get all the inventory vouchers.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of inventory
	 *         vouchers in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/primary-sales-performance-download-status", method = RequestMethod.GET)
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
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());
		return "company/primarySalesPerformanceTallyStatusReport";
	}

	/**
	 * GET /primary-sales-performance/:id : get the "id" InventoryVoucher.
	 *
	 * @param id the id of the InventoryVoucherDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         InventoryVoucherDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/primary-sales-performance-download-status/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> getInventoryVoucher(@PathVariable String pid) {
		log.debug("Web request to get inventoryVoucherDTO by pid : {}", pid);
		Optional<InventoryVoucherHeaderDTO> optionalInventoryVoucherHeaderDTO = inventoryVoucherService
				.findOneByPid(pid);
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

			// checking tax rate in product group if product does not have tax rate
			for (InventoryVoucherDetailDTO ivd : inventoryVoucherDTO.getInventoryVoucherDetails()) {
				if (ivd.getTaxPercentage() == 0) {
					ProductGroup pg = productGroupProductRepository.findProductGroupByProductPid(ivd.getProductPid())
							.get(0);
					if (pg != null) {
						if (pg.getTaxRate() != 0) {
							ivd.setTaxPercentage(pg.getTaxRate());
						}
					}
				}
			}

			inventoryVoucherDTO.setDocumentVolume(ivTotalVolume);
			return new ResponseEntity<>(inventoryVoucherDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/primary-sales-performance-download-status/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<List<SalesPerformanceDTO>> filterInventoryVouchers(
			@RequestParam("employeePids") List<String> employeePids,
			@RequestParam("tallyDownloadStatus") String tallyDownloadStatus,
			@RequestParam("accountPid") String accountPid, @RequestParam("filterBy") String filterBy,
			@RequestParam("documentPids") List<String> documentPids, @RequestParam String fromDate,
			@RequestParam String toDate) {
		log.debug("Web request to filter accounting vouchers");
		if (documentPids.isEmpty()) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}
		LocalDate fDate = LocalDate.now();
		LocalDate tDate = LocalDate.now();
		if (filterBy.equals(SalesPerformanceReportTallyStatusResource.CUSTOM)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		} else if (filterBy.equals(SalesPerformanceReportTallyStatusResource.YESTERDAY)) {
			fDate = LocalDate.now().minusDays(1);
			tDate = fDate;
		} else if (filterBy.equals(SalesPerformanceReportTallyStatusResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fDate = LocalDate.now().with(fieldISO, 1);
		} else if (filterBy.equals(SalesPerformanceReportTallyStatusResource.MTD)) {
			fDate = LocalDate.now().withDayOfMonth(1);
		}
		List<SalesPerformanceDTO> salesPerformanceDTOs = getFilterData(employeePids, documentPids, tallyDownloadStatus,
				accountPid, fDate, tDate);
		return new ResponseEntity<>(salesPerformanceDTOs, HttpStatus.OK);
	}

	private List<SalesPerformanceDTO> getFilterData(List<String> employeePids, List<String> documentPids,
			String tallyDownloadStatus, String accountPid, LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Long> userIds = employeeProfileRepository.findUserIdByEmployeePidIn(employeePids);
		Long currentUserId = userRepository.getIdByLogin(SecurityUtils.getCurrentUserLogin());
		userIds.add(currentUserId);
		if (userIds.isEmpty()) {
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

		List<Object[]> inventoryVouchers;
		if ("-1".equals(accountPid)) {
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndDocumentPidInAndTallyDownloadStatusDateBetweenOrderByCreatedDateDesc(userIds,
							documentPids, tallyStatus, fromDate, toDate);
		} else {
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndAccountPidInAndDocumentPidInAndTallyDownloadStatusDateBetweenOrderByCreatedDateDesc(
							userIds, accountPid, documentPids, tallyStatus, fromDate, toDate);
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

		boolean pdfDownloadStatus = false;
		Optional<CompanyConfiguration> opCompanyConfiguration = CompanyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SALES_PDF_DOWNLOAD);
		if (opCompanyConfiguration.isPresent()) {
			pdfDownloadStatus = true;
		}

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
			salesPerformanceDTO.setPdfDownloadStatus(pdfDownloadStatus);

			salesPerformanceDTO.setTallyDownloadStatus(TallyDownloadStatus.valueOf(ivData[16].toString()));
			salesPerformanceDTO.setVisitRemarks(ivData[17] == null ? null : ivData[17].toString());

			salesPerformanceDTOs.add(salesPerformanceDTO);
		}
		return salesPerformanceDTOs;
	}

	@RequestMapping(value = "/primary-sales-performance-download-status/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) {
		return primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
	}

	@RequestMapping(value = "/primary-sales-performance-download-status/download-inventory-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

	@RequestMapping(value = "/primary-sales-performance-download-status/changeStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> changeStatus(@RequestParam String pid,
			@RequestParam TallyDownloadStatus tallyDownloadStatus) {
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(pid).get();
		inventoryVoucherHeaderDTO.setTallyDownloadStatus(tallyDownloadStatus);
		inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	@RequestMapping(value = "/primary-sales-performance-download-status/downloadPdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadStatus(@RequestParam String inventoryPid, HttpServletResponse response) throws IOException {

		log.info("Download pdf with pid " + inventoryPid);

		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(inventoryPid).get();

		buildPdf(inventoryVoucherHeaderDTO, response);

	}

	private void buildPdf(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition",
				"inline;filename=\"" + "Packing Slip_" + inventoryVoucherHeaderDTO.getOrderNumber() + ".pdf\"");
		// Get the output stream for writing PDF object
		ServletOutputStream out = response.getOutputStream();
		try {
			Document document = new Document();
			/* Basic PDF Creation inside servlet */
			PdfWriter.getInstance(document, out);
			com.itextpdf.text.Font fontSize_22 = FontFactory.getFont(FontFactory.TIMES, 20f,
					com.itextpdf.text.Font.BOLD);
			com.itextpdf.text.Font fontSize_16 = FontFactory.getFont(FontFactory.TIMES, 16f,
					com.itextpdf.text.Font.BOLD);

			Paragraph companyName = new Paragraph();
			Paragraph line = new Paragraph();
			companyName.setAlignment(Element.ALIGN_CENTER);
			line.setAlignment(Element.ALIGN_CENTER);
			companyName.setFont(fontSize_22);
			companyName.add(CompanyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()).getLegalName());
			line.add(new Paragraph("_______________________________________________________"));

			String customerAddress = "";
			String customerEmail = "";
			String customerPhone = "";

			LocalDateTime date = inventoryVoucherHeaderDTO.getDocumentDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

			String orderDate = date.format(formatter);

			if (!inventoryVoucherHeaderDTO.getCustomeraddress().equalsIgnoreCase("No Address")
					&& inventoryVoucherHeaderDTO.getCustomeraddress() != null)
				customerAddress = inventoryVoucherHeaderDTO.getCustomeraddress();

			if (inventoryVoucherHeaderDTO.getCustomerEmail() != null)
				customerEmail = inventoryVoucherHeaderDTO.getCustomerEmail();

			if (inventoryVoucherHeaderDTO.getCustomerPhone() != null)
				customerPhone = inventoryVoucherHeaderDTO.getCustomerPhone();

			document.open();
			document.add(companyName);
			document.add(line);
			document.add(new Paragraph("Sales Order No :" + inventoryVoucherHeaderDTO.getOrderNumber()));
			document.add(new Paragraph("Date :" + orderDate));
			document.add(new Paragraph("Reference :"));
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("Status :", fontSize_16));
			document.add(new Paragraph("Customer :" + inventoryVoucherHeaderDTO.getReceiverAccountName()));
			document.add(new Paragraph("Address :" + customerAddress));
			document.add(new Paragraph("Email :" + customerEmail));
			document.add(new Paragraph("Phone :" + customerPhone));
			document.add(new Paragraph("\n"));
			PdfPTable table = createPdfTable(inventoryVoucherHeaderDTO);
			table.setWidthPercentage(100);
			document.add(table);
			document.add(new Paragraph("\n\n"));
			document.add(new Paragraph("Internal Note :"));
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("Customer Note :"));
			document.add(new Paragraph("\n\n"));

			double totalTaxAmount = 0.0;
			for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
					.getInventoryVoucherDetails()) {

				double amount = (inventoryVoucherDetailDTO.getSellingRate() * inventoryVoucherDetailDTO.getQuantity());
				double taxAmt = amount * inventoryVoucherDetailDTO.getTaxPercentage() / 100;
				double taxAmount = Math.round(taxAmt * 100.0) / 100.0;
				totalTaxAmount += taxAmount;
			}
			document.add(new Paragraph("Grand Total :\t \t \t \t \t" + inventoryVoucherHeaderDTO.getDocumentTotal()));
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("Tax Total :\t \t \t \t \t" + Math.round(totalTaxAmount * 100) / 100));
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("Discount :\t \t \t \t" + inventoryVoucherHeaderDTO.getDocDiscountAmount()));

			/*
			 * ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream ps =
			 * new PrintStream(baos); PdfWriter.getInstance(document, ps);
			 */

			document.close();
		} catch (DocumentException exc) {
			throw new IOException(exc.getMessage());
		} finally {
			out.close();
		}

	}

	private PdfPTable createPdfTable(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		com.itextpdf.text.Font fontWeight = FontFactory.getFont(FontFactory.TIMES, 12f, com.itextpdf.text.Font.BOLD);
		com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.TIMES, 12f);

		PdfPTable table = new PdfPTable(new float[] { 225f, 100f, 100f, 100f, 100f, 100f, 100f });

		PdfPCell cell1 = new PdfPCell(new Paragraph("Item Name", fontWeight));
		cell1.setBorder(Rectangle.NO_BORDER);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell2 = new PdfPCell(new Paragraph("Price", fontWeight));
		cell2.setBorder(Rectangle.NO_BORDER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell3 = new PdfPCell(new Paragraph("Quantity", fontWeight));
		cell3.setBorder(Rectangle.NO_BORDER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell4 = new PdfPCell(new Paragraph("Billed Quantity", fontWeight));
		cell4.setBorder(Rectangle.NO_BORDER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell5 = new PdfPCell(new Paragraph("Balance Quantity", fontWeight));
		cell5.setBorder(Rectangle.NO_BORDER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell6 = new PdfPCell(new Paragraph("Tax", fontWeight));
		cell6.setBorder(Rectangle.NO_BORDER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell7 = new PdfPCell(new Paragraph("Total", fontWeight));
		cell7.setBorder(Rectangle.NO_BORDER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(cell1);
		table.addCell(cell2);
		table.addCell(cell3);
		table.addCell(cell4);
		table.addCell(cell5);
		table.addCell(cell6);
		table.addCell(cell7);

		for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
				.getInventoryVoucherDetails()) {

			double amount = (inventoryVoucherDetailDTO.getSellingRate() * inventoryVoucherDetailDTO.getQuantity());
			double taxAmt = amount * inventoryVoucherDetailDTO.getTaxPercentage() / 100;
			double taxAmount = Math.round(taxAmt * 100.0) / 100.0;
			PdfPCell col1 = new PdfPCell(new Paragraph(inventoryVoucherDetailDTO.getProductName(), fontWeight));
			col1.setBorder(Rectangle.NO_BORDER);

			PdfPCell col2 = new PdfPCell(
					new Paragraph(String.valueOf(inventoryVoucherDetailDTO.getSellingRate()), font));
			col2.setBorder(Rectangle.NO_BORDER);
			col2.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col3 = new PdfPCell(new Paragraph(String.valueOf(inventoryVoucherDetailDTO.getQuantity()), font));
			col3.setBorder(Rectangle.NO_BORDER);
			col3.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col4 = new PdfPCell(new Paragraph(String.valueOf(0.0), font));
			col4.setBorder(Rectangle.NO_BORDER);
			col4.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col5 = new PdfPCell(new Paragraph(String.valueOf(inventoryVoucherDetailDTO.getQuantity()), font));
			col5.setBorder(Rectangle.NO_BORDER);
			col5.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col6 = new PdfPCell(new Paragraph(String.valueOf(taxAmount), font));
			col6.setBorder(Rectangle.NO_BORDER);
			col6.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col7 = new PdfPCell(new Paragraph(String.valueOf(inventoryVoucherDetailDTO.getRowTotal()), font));
			col7.setBorder(Rectangle.NO_BORDER);
			col7.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(col1);
			table.addCell(col2);
			table.addCell(col3);
			table.addCell(col4);
			table.addCell(col5);
			table.addCell(col6);
			table.addCell(col7);
		}

		return table;
	}

}
