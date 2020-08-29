package com.orderfleet.webapp.web.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.SendSalesOrderEmailStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
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
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformanceDTO;
import com.orderfleet.webapp.web.rest.dto.SecondarySalesOrderExcelDTO;

/**
 * Web controller for managing InventoryVoucher.
 * 
 * @author Anish
 * @since Apr 27, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesInvoiceReportResource {

	private final Logger log = LoggerFactory.getLogger(SalesInvoiceReportResource.class);

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
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	/**
	 * GET /primary-sales-performance : get all the inventory vouchers.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of inventory
	 *         vouchers in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/sales-invoice-report", method = RequestMethod.GET)
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

		boolean sendSalesOrderEmailStatus = false;
		Optional<CompanyConfiguration> opCompanyConfiguration = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SEND_SALES_ORDER_EMAIL);
		if (opCompanyConfiguration.isPresent()) {

			if (opCompanyConfiguration.get().getValue().equals("true")) {
				sendSalesOrderEmailStatus = true;
			} else {
				sendSalesOrderEmailStatus = false;
			}
		}
		model.addAttribute("sendSalesOrderEmailStatus", sendSalesOrderEmailStatus);

		boolean pdfDownloadStatus = false;
		Optional<CompanyConfiguration> opCompanyConfigurationPdf = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SALES_PDF_DOWNLOAD);
		if (opCompanyConfigurationPdf.isPresent()) {

			if (opCompanyConfigurationPdf.get().getValue().equals("true")) {
				pdfDownloadStatus = true;
			} else {
				pdfDownloadStatus = false;
			}
		}
		model.addAttribute("pdfDownloadStatus", pdfDownloadStatus);

		return "company/salesInvoiceReport";
	}

	
	@RequestMapping(value = "/sales-invoice-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
		if (filterBy.equals(SalesInvoiceReportResource.CUSTOM)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		} else if (filterBy.equals(SalesInvoiceReportResource.YESTERDAY)) {
			fDate = LocalDate.now().minusDays(1);
			tDate = fDate;
		} else if (filterBy.equals(SalesInvoiceReportResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fDate = LocalDate.now().with(fieldISO, 1);
		} else if (filterBy.equals(SalesInvoiceReportResource.MTD)) {
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
		
		Map<String, Double> ivTotalWithoutTax = ivDetails.stream().collect(Collectors.groupingBy(obj -> obj[0].toString(),
				Collectors.summingDouble(obj -> ( (Double) (obj[4] == null ? 1.0d : obj[4]) 
												* ( (Double)obj[8]  * ((100 - (Double)obj[10]) / 100) ))) ));
		
		Map<String, Double> ivTotalTax = ivDetails.stream().collect(Collectors.groupingBy(obj -> obj[0].toString(),
				Collectors.summingDouble(obj -> ( (Double) (obj[4] == null ? 1.0d : obj[4]) 
												* ( (Double)obj[8]  * ((100 - (Double)obj[10]) / 100) ))  *((Double) obj[7] /100)    ) ));



		int size = inventoryVouchers.size();
		List<SalesPerformanceDTO> salesPerformanceDTOs = new ArrayList<>(size);
		DecimalFormat df = new DecimalFormat("0.00");
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
			salesPerformanceDTO.setTotalWithoutTax(Double.parseDouble(df.format(ivTotalWithoutTax.get(salesPerformanceDTO.getPid()))));
			salesPerformanceDTO.setTaxTotal(Double.parseDouble(df.format(ivTotalTax.get(salesPerformanceDTO.getPid()))));
			
			salesPerformanceDTO.setPdfDownloadButtonStatus(false);
			salesPerformanceDTO.setSendSalesOrderEmailStatusColumn(false);

			salesPerformanceDTO.setTallyDownloadStatus(TallyDownloadStatus.valueOf(ivData[16].toString()));
			salesPerformanceDTO.setVisitRemarks(ivData[17] == null ? null : ivData[17].toString());

			salesPerformanceDTO.setOrderNumber(ivData[18] == null ? 0 : Long.parseLong(ivData[18].toString()));

			salesPerformanceDTO.setPdfDownloadStatus(Boolean.valueOf(ivData[19].toString()));

			salesPerformanceDTO.setSendSalesOrderEmailStatus(SendSalesOrderEmailStatus.valueOf(ivData[24].toString()));
			salesPerformanceDTO.setClientDate((LocalDateTime)ivData[25]);
			salesPerformanceDTOs.add(salesPerformanceDTO);
		}
		return salesPerformanceDTOs;
	}
	

	@RequestMapping(value = "/sales-invoice-report/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) {
		return primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
	}

	@RequestMapping(value = "/sales-invoice-report/download-inventory-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadInventoryXls(@RequestParam("inventoryVoucherHeaderPids") String[] inventoryVoucherHeaderPids,
			HttpServletResponse response) {
		log.debug("start downloading....");
		List<Object[]> inventoryVouchers = inventoryVoucherHeaderRepository.findByPidsOrderByCreatedDateDesc(Arrays.asList(inventoryVoucherHeaderPids));
		log.debug("inventoryvoucherheaders foudn.....");
		List<SalesPerformanceDTO> salesPerformanceDTOs = new ArrayList<>();
		
		if (inventoryVouchers.isEmpty()) {
			return;
		}else {
			salesPerformanceDTOs = createSalesPerformanceDTO(inventoryVouchers);
		}
		buildExcelDocument(salesPerformanceDTOs, response);
	}

	private void buildExcelDocument(List<SalesPerformanceDTO> salesPerformanceDTOs,
			HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "InvoiceDetails" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Invoice Number","Invoice Date",
				"Customer", "Amt(without tax)", "Tax Total","Total Amount", 
				"Activity Date","Client Date" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, salesPerformanceDTOs);
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

	private void createReportRows(HSSFSheet worksheet, List<SalesPerformanceDTO> salesPerformanceDTOs) {
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
		for (SalesPerformanceDTO salesPerform : salesPerformanceDTOs) {
				HSSFRow row = worksheet.createRow(rowNum++);
				row.createCell(0).setCellValue(salesPerform.getDocumentNumberLocal());
				
				HSSFCell docDateCell = row.createCell(1);
				docDateCell.setCellValue(salesPerform.getDocumentDate().toString());
				docDateCell.setCellStyle(dateCellStyle);
				row.createCell(2).setCellValue(salesPerform.getReceiverAccountName());
				row.createCell(3).setCellValue(salesPerform.getTotalWithoutTax());
				row.createCell(4).setCellValue(salesPerform.getTaxTotal());
				row.createCell(5).setCellValue(salesPerform.getDocumentTotal());
			
				
				HSSFCell serverDateCell = row.createCell(6);
				serverDateCell.setCellValue(salesPerform.getCreatedDate().toString());
				serverDateCell.setCellStyle(dateCellStyle);
				
				HSSFCell clientDateCell = row.createCell(7);
				clientDateCell.setCellValue(salesPerform.getClientDate().toString());
				clientDateCell.setCellStyle(dateCellStyle);
			
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

	
	
	@RequestMapping(value = "/sales-invoice-report/downloadPdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadStatus(@RequestParam String inventoryPid, HttpServletResponse response) throws IOException {

		log.info("Download pdf with pid " + inventoryPid);

		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDtos = new ArrayList<>();

		String[] inventoryPidArray = inventoryPid.split(",");

		if (inventoryPidArray.length > 0) {

			for (String ivhPid : inventoryPidArray) {

				InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(ivhPid)
						.get();

				inventoryVoucherHeaderDtos.add(inventoryVoucherHeaderDTO);
			}
		}
		buildPdf(inventoryVoucherHeaderDtos, response);


	}

	private void buildPdf(List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/pdf");

		if (inventoryVoucherHeaderDTOs.size() > 1) {

			response.setHeader("Content-Disposition", "inline;filename=\"" + "Tax Invoice_"
					+ inventoryVoucherHeaderDTOs.get(0).getReceiverAccountName() + ".pdf\"");

		} else {
			response.setHeader("Content-Disposition", "inline;filename=\"" + "Tax Invoice_"
					+ inventoryVoucherHeaderDTOs.get(0).getOrderNumber() + ".pdf\"");
		}
		// Get the output stream for writing PDF object
		ServletOutputStream out = response.getOutputStream();
		try {
			Document document = new Document();
			/* Basic PDF Creation inside servlet */
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();

			// writer.addJavaScript("this.print(false);", false);

			com.itextpdf.text.Font fontSize_22 = FontFactory.getFont(FontFactory.TIMES, 20f,
					com.itextpdf.text.Font.BOLD);
			/*
			 * com.itextpdf.text.Font fontSize_16 = FontFactory.getFont(FontFactory.TIMES,
			 * 16f, com.itextpdf.text.Font.BOLD);
			 */
			for (InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO : inventoryVoucherHeaderDTOs) {
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

				LocalDateTime date = inventoryVoucherHeaderDTO.getCreatedDate();

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");

				String orderDate = date.format(formatter);

				if (!inventoryVoucherHeaderDTO.getCustomeraddress().equalsIgnoreCase("No Address")
						&& inventoryVoucherHeaderDTO.getCustomeraddress() != null)
					customerAddress = inventoryVoucherHeaderDTO.getCustomeraddress();

				if (inventoryVoucherHeaderDTO.getCustomerEmail() != null)
					customerEmail = inventoryVoucherHeaderDTO.getCustomerEmail();

				if (inventoryVoucherHeaderDTO.getCustomerPhone() != null)
					customerPhone = inventoryVoucherHeaderDTO.getCustomerPhone();

				document.add(companyName);
				document.add(line);
				document.add(new Paragraph("Sales Order No :" + inventoryVoucherHeaderDTO.getOrderNumber()));
				document.add(new Paragraph("Date :" + orderDate));
				document.add(new Paragraph("Executive : " + inventoryVoucherHeaderDTO.getEmployeeName()));
				document.add(new Paragraph("\n"));
				document.add(new Paragraph("Customer :" + inventoryVoucherHeaderDTO.getReceiverAccountName()));
				document.add(new Paragraph("Address :" + customerAddress));
				document.add(new Paragraph("Email :" + customerEmail));
				document.add(new Paragraph("Phone :" + customerPhone));
				document.add(new Paragraph("\n"));
				PdfPTable table = createPdfTable(inventoryVoucherHeaderDTO);
				table.setWidthPercentage(100);
				document.add(table);
				document.add(new Paragraph("\n\n"));
				document.add(new Paragraph("Remarks :" + inventoryVoucherHeaderDTO.getVisitRemarks()));
				document.add(new Paragraph("\n\n"));
				PdfPTable tableTotal = createTotalTable(inventoryVoucherHeaderDTO);
				// tableTotal.setWidthPercentage(50);
				tableTotal.setHorizontalAlignment(Element.ALIGN_LEFT);
				document.add(tableTotal);

				document.newPage();
			}

			/*
			 * ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream ps =
			 * new PrintStream(baos); PdfWriter.getInstance(document, ps);
			 */

			PdfAction action = new PdfAction(PdfAction.PRINTDIALOG);
			writer.setOpenAction(action);

			document.close();
		} catch (DocumentException exc) {
			throw new IOException(exc.getMessage());
		} finally {
			out.close();
		}

	}

	private PdfPTable createTotalTable(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		DecimalFormat df = new DecimalFormat("0.00");

		double totalTaxAmount = 0.0;
		for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
				.getInventoryVoucherDetails()) {

			/*
			 * double amount = (inventoryVoucherDetailDTO.getSellingRate() *
			 * inventoryVoucherDetailDTO.getQuantity()); double taxAmount = amount *
			 * inventoryVoucherDetailDTO.getTaxPercentage() / 100;
			 */

			double amount = (inventoryVoucherDetailDTO.getSellingRate() * inventoryVoucherDetailDTO.getQuantity());
			double discountedAmount = amount - (amount * inventoryVoucherDetailDTO.getDiscountPercentage() / 100);
			double taxAmnt = (discountedAmount * inventoryVoucherDetailDTO.getTaxPercentage() / 100);

			totalTaxAmount += taxAmnt;
		}

		PdfPTable table = new PdfPTable(new float[] { 10f, 10f });

		PdfPCell cell1 = new PdfPCell(new Paragraph("Grand Total :"));
		cell1.setBorder(Rectangle.NO_BORDER);

		PdfPCell cell2 = new PdfPCell(new Paragraph(String.valueOf(inventoryVoucherHeaderDTO.getDocumentTotal())));
		cell2.setBorder(Rectangle.NO_BORDER);

		PdfPCell cell3 = new PdfPCell(new Paragraph("Tax Total :"));
		cell3.setBorder(Rectangle.NO_BORDER);

		PdfPCell cell4 = new PdfPCell(new Paragraph(df.format(totalTaxAmount)));
		cell4.setBorder(Rectangle.NO_BORDER);

		table.addCell(cell1);
		table.addCell(cell2);
		table.addCell(cell3);
		table.addCell(cell4);

		return table;
	}

	private PdfPTable createPdfTable(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO) {

		com.itextpdf.text.Font fontWeight = FontFactory.getFont(FontFactory.TIMES, 12f, com.itextpdf.text.Font.BOLD);

		PdfPTable table = new PdfPTable(new float[] { 225f, 100f, 100f, 100f, 100f, 100f });
		table.setTotalWidth(350f);

		PdfPCell cell1 = new PdfPCell(new Paragraph("Item Name", fontWeight));
		cell1.setBorder(Rectangle.NO_BORDER);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell2 = new PdfPCell(new Paragraph("Price", fontWeight));
		cell2.setBorder(Rectangle.NO_BORDER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell3 = new PdfPCell(new Paragraph("Quantity", fontWeight));
		cell3.setBorder(Rectangle.NO_BORDER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell4 = new PdfPCell(new Paragraph("Discount %", fontWeight));
		cell4.setBorder(Rectangle.NO_BORDER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell5 = new PdfPCell(new Paragraph("Tax Amount", fontWeight));
		cell5.setBorder(Rectangle.NO_BORDER);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell6 = new PdfPCell(new Paragraph("Total", fontWeight));
		cell6.setBorder(Rectangle.NO_BORDER);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell7 = new PdfPCell(new Paragraph(""));
		cell7.setBorder(Rectangle.NO_BORDER);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(cell1);
		table.addCell(cell2);
		table.addCell(cell3);
		table.addCell(cell4);
		table.addCell(cell5);
		table.addCell(cell6);
		table.addCell(cell7);
		table.addCell(cell7);
		table.addCell(cell7);
		table.addCell(cell7);
		table.addCell(cell7);
		table.addCell(cell7);

		DecimalFormat df = new DecimalFormat("0.00");

		for (InventoryVoucherDetailDTO inventoryVoucherDetailDTO : inventoryVoucherHeaderDTO
				.getInventoryVoucherDetails()) {

			double amount = (inventoryVoucherDetailDTO.getSellingRate() * inventoryVoucherDetailDTO.getQuantity());
			double discountedAmount = amount - (amount * inventoryVoucherDetailDTO.getDiscountPercentage() / 100);
			double taxAmnt = (discountedAmount * inventoryVoucherDetailDTO.getTaxPercentage() / 100);
			// double taxAmount = Math.round(taxAmt * 100.0) / 100.0;
			String taxAmount = df.format(taxAmnt);

			PdfPCell col1 = new PdfPCell(new Paragraph(inventoryVoucherDetailDTO.getProductName()));
			col1.setBorder(Rectangle.NO_BORDER);

			PdfPCell col2 = new PdfPCell(new Paragraph(String.valueOf(inventoryVoucherDetailDTO.getSellingRate())));
			col2.setBorder(Rectangle.NO_BORDER);
			col2.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col3 = new PdfPCell(new Paragraph(String.valueOf(inventoryVoucherDetailDTO.getQuantity())));
			col3.setBorder(Rectangle.NO_BORDER);
			col3.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col4 = new PdfPCell(
					new Paragraph(String.valueOf(inventoryVoucherDetailDTO.getDiscountPercentage())));
			col4.setBorder(Rectangle.NO_BORDER);
			col4.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col5 = new PdfPCell(new Paragraph(String.valueOf(taxAmount)));
			col5.setBorder(Rectangle.NO_BORDER);
			col5.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col6 = new PdfPCell(new Paragraph(String.valueOf(inventoryVoucherDetailDTO.getRowTotal())));
			col6.setBorder(Rectangle.NO_BORDER);
			col6.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell col7 = new PdfPCell(new Paragraph(""));
			col7.setBorder(Rectangle.NO_BORDER);
			col7.setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(col1);
			table.addCell(col2);
			table.addCell(col3);
			table.addCell(col4);
			table.addCell(col5);
			table.addCell(col6);
			table.addCell(col7);
			table.addCell(col7);
			table.addCell(col7);
			table.addCell(col7);
			table.addCell(col7);
			table.addCell(col7);
		}

		return table;
	}

}