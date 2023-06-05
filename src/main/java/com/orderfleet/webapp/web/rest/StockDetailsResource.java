package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.StockFlow;
import com.orderfleet.webapp.repository.AccountingVoucherDetailRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentStockCalculationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentAccountTypeService;
import com.orderfleet.webapp.service.DocumentPriceLevelService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.StockDetailsService;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentStockCalculationDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsTotalDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Document.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Controller
@RequestMapping("/web")
public class StockDetailsResource {

	private final Logger log = LoggerFactory.getLogger(StockDetailsResource.class);

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherHeaderService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private StockDetailsService stockDetailsService;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherDetailRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private CompanyRepository companyRepository;

	private LocalDateTime dateTime;

	@RequestMapping(value = "/stockDetails", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDocuments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of stockDetails");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		if (userIds.isEmpty()) {
			String user = SecurityUtils.getCurrentUserLogin();
			System.out.println("if.." + userIds.size());
			long userId = userRepository.getIdByLogin(user);
			userIds = new ArrayList<>();
			userIds.add(userId);
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		} else {
			System.out.println("else .." + userIds.size());
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}

		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		String user = SecurityUtils.getCurrentUserLogin();
		Optional<User> userOp = userRepository.findOneByLogin(user);
		long userId = userOp.get().getId();

		List<UserStockLocation> userStockLocations = userStockLocationRepository.findByUserPid(userOp.get().getPid());
		Set<StockLocation> stockLocations = userStockLocations.stream().map(usl -> usl.getStockLocation())
				.collect(Collectors.toSet());
		List<OpeningStock> openingStockUserBased = openingStockRepository
				.findByStockLocationInOrderByCreatedDateAsc(new ArrayList<>(stockLocations));
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		if (openingStockUserBased.size() != 0) {
			LocalDateTime fromDate = openingStockUserBased.get(0).getCreatedDate();
			// LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
			LocalDateTime toDate = LocalDate.now().atTime(23, 59);
			stockDetails = inventoryVoucherHeaderService.findAllStockDetails(companyId, userId, fromDate, toDate, null);
			List<StockDetailsDTO> unSaled = stockDetailsService.findOtherStockItems(userOp.get(), stockLocations,
					false);
			for (StockDetailsDTO dto : stockDetails) {
				unSaled.removeIf(unSale -> unSale.getProductName().equals(dto.getProductName()));
			}
			stockDetails.addAll(unSaled);
		}
		stockDetails
				.sort((StockDetailsDTO s1, StockDetailsDTO s2) -> s1.getProductName().compareTo(s2.getProductName()));
		model.addAttribute("stockDetails", stockDetails);

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
		return "company/stockDetails";
	}

	@RequestMapping(value = "/stockDetails/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StockDetailsDTO>> filter(@RequestParam("employeePid") String employeePid) {
		log.debug("Web request to filter Stock Details");
		dateTime = LocalDateTime.now();

		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService.findOneByPid(employeePid);
		String userPid = employeeProfileDTO.get().getUserPid();
		Optional<User> user = userRepository.findOneByPid(userPid);
		long userId = user.get().getId();
		List<UserStockLocation> userStockLocations = userStockLocationRepository.findByUserPid(user.get().getPid());
		Set<StockLocation> usersStockLocations = userStockLocations.stream().map(usl -> usl.getStockLocation())
				.collect(Collectors.toSet());
		System.out.println("stockLocation:"+usersStockLocations);
		List<OpeningStock> openingStockUserBased = openingStockRepository
				.findByStockLocationInOrderByCreatedDateAsc(new ArrayList<>(usersStockLocations));
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		if (openingStockUserBased.size() != 0) {
			LocalDateTime fromDate = openingStockUserBased.get(0).getCreatedDate();
			// LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
			LocalDateTime toDate = LocalDate.now().atTime(23, 59);
			stockDetails = inventoryVoucherHeaderService.findAllStockDetails(companyId, userId, fromDate, toDate, null);
			List<StockDetailsDTO> unSaled = stockDetailsService.findOtherStockItems(user.get(), usersStockLocations,
					false);
			for (StockDetailsDTO dto : stockDetails) {
				unSaled.removeIf(unSale -> unSale.getProductName().equals(dto.getProductName()));
			}
			stockDetails.addAll(unSaled);
		}

		stockDetails
				.sort((StockDetailsDTO s1, StockDetailsDTO s2) -> s1.getProductName().compareTo(s2.getProductName()));

		return new ResponseEntity<>(stockDetails, HttpStatus.OK);

	}

	@RequestMapping(value = "/stockDetails/downloadxls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadXls(@RequestParam("employeePid") String employeePid, HttpServletResponse response) {

		log.debug("Web request to download Stock Details");

		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService.findOneByPid(employeePid);
		String userPid = employeeProfileDTO.get().getUserPid();
		Optional<User> user = userRepository.findOneByPid(userPid);
		Long userId = user.get().getId();
		List<UserStockLocation> userStockLocations = userStockLocationRepository.findByUserPid(user.get().getPid());
		Set<StockLocation> usersStockLocations = userStockLocations.stream().map(usl -> usl.getStockLocation())
				.collect(Collectors.toSet());
		List<OpeningStock> openingStockUserBased = openingStockRepository
				.findByStockLocationInOrderByCreatedDateAsc(new ArrayList<>(usersStockLocations));
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		StockDetailsDTO stock = new StockDetailsDTO();
		if (openingStockUserBased.size() != 0) {
			LocalDateTime fromDate = openingStockUserBased.get(0).getCreatedDate();
			// LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
			LocalDateTime toDate = LocalDate.now().atTime(23, 59);
			stockDetails = inventoryVoucherHeaderService.findAllStockDetails(companyId, userId, fromDate, toDate, null);
			List<StockDetailsDTO> unSaled = stockDetailsService.findOtherStockItems(user.get(), usersStockLocations,
					false);
			for (StockDetailsDTO dto : stockDetails) {
				unSaled.removeIf(unSale -> unSale.getProductName().equals(dto.getProductName()));
				dto.setReportingTime(dateTime);

			}

			stockDetails.addAll(unSaled);
		}

		stockDetails
				.sort((StockDetailsDTO s1, StockDetailsDTO s2) -> s1.getProductName().compareTo(s2.getProductName()));

		buildExcelDocument(stockDetails, response);
	}

	private void buildExcelDocument(List<StockDetailsDTO> stockDetails, HttpServletResponse response) {
		// TODO Auto-generated method stub
		log.debug("Downloading Excel report");
		String excelFileName = "Stock Details" + ".xls";
		String sheetName = "Sheet1";

		String[] headerColumns = { "Reporting Time", "EmployeeName", "ProductName", "OpeningStock", "Sales Qty",
				"DamageQty", "ClosingStock" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, stockDetails);
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

	private void createReportRows(HSSFSheet worksheet, List<StockDetailsDTO> stockDetails) {
		// TODO Auto-generated method stub
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd h:mm:ss "));
		int rowNum = 1;

		for (StockDetailsDTO stockDTO : stockDetails) {
			HSSFRow row = worksheet.createRow(rowNum++);
			LocalDateTime localDateTime = stockDTO.getReportingTime();
			Instant i = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
			Date date = Date.from(i);
			HSSFCell DateCell = row.createCell(0);
			DateCell.setCellValue(date);
			DateCell.setCellStyle(dateCellStyle);
			row.createCell(1).setCellValue(stockDTO.getEmployeeName());
			row.createCell(2).setCellValue(stockDTO.getProductName());
			row.createCell(3).setCellValue(stockDTO.getOpeningStock());
			row.createCell(4).setCellValue(stockDTO.getSaledQuantity());
			row.createCell(5).setCellValue(stockDTO.getDamageQty());
			row.createCell(6).setCellValue(stockDTO.getClosingStock());
		}
	}

	private void createHeaderRow(HSSFSheet worksheet, String[] headerColumns) {
		// TODO Auto-generated method stub
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

	@RequestMapping(value = "/stockDetails/downloadpdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadPDF(@RequestParam("employeePid") String employeePid, HttpServletResponse response)
			throws IOException {

		log.debug("Web request to download PDF Stock Details");

		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService.findOneByPid(employeePid);
		System.out.println("NAme: "+employeeProfileDTO.get().getName());
		String userPid = employeeProfileDTO.get().getUserPid();
		Optional<User> user = userRepository.findOneByPid(userPid);
		Long userId = user.get().getId();
		List<UserStockLocation> userStockLocations = userStockLocationRepository.findByUserPid(user.get().getPid());
		Set<StockLocation> usersStockLocations = userStockLocations.stream().map(usl -> usl.getStockLocation())
				.collect(Collectors.toSet());
		List<OpeningStock> openingStockUserBased = openingStockRepository
				.findByStockLocationInOrderByCreatedDateAsc(new ArrayList<>(usersStockLocations));
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		List<StockDetailsTotalDTO> stockTotal = new ArrayList<StockDetailsTotalDTO>();
		if (openingStockUserBased.size() != 0) {
			LocalDateTime fromDate = openingStockUserBased.get(0).getCreatedDate();
			// LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
			LocalDateTime toDate = LocalDate.now().atTime(23, 59);
			stockDetails = inventoryVoucherHeaderService.findAllStockDetails(companyId, userId, fromDate, toDate, null);
			List<StockDetailsDTO> unSaled = stockDetailsService.findOtherStockItems(user.get(), usersStockLocations,
					false);
			String employeeName = null ;
			for (StockDetailsDTO dto : stockDetails) {
				unSaled.removeIf(unSale -> unSale.getProductName().equals(dto.getProductName()));
				dto.setReportingTime(dateTime);
				employeeName = dto.getEmployeeName();

			}
		
			StockDetailsTotalDTO stock = new StockDetailsTotalDTO();
		
			List<Long> eteIds = executiveTaskExecutionRepository.findAllByUserIdAndDateBetween(userId, fromDate, toDate)
					.stream().map(ext -> ext.getId()).collect(Collectors.toList());

			System.out.println("ext ids: " + eteIds.size());

			List<Object[]> inventoryVoucher = new ArrayList<>();

			if (eteIds.size() > 0) {
				inventoryVoucher = inventoryVoucherHeaderRepository.findByExecutiveTaskExecutionIdIn(eteIds);
			}

			System.out.println("iv ids:" + inventoryVoucher.size());

			List<Object[]> accountingVouchers = new ArrayList<>();
			if (eteIds.size() > 0) {
				accountingVouchers = accountingVoucherHeaderRepository.findByExecutiveTaskExecutionsIdIn(eteIds);

			}
			System.out.println("size :" + accountingVouchers.size());
			List<AccountingVoucherHeader> accountingHeader =new ArrayList<>();
			if (eteIds.size() > 0) {
			 accountingHeader = accountingVoucherHeaderRepository
					.findByExecutiveTaskExecutionIdIn(eteIds);
			}

			List<Long> accId = accountingHeader.stream().map(acc -> acc.getId()).collect(Collectors.toList());
			List<Object[]> netCollectionAmountCash = new ArrayList<>();
			List<Object[]> netCollectionAmountCheque = new ArrayList<>();

			if (!accId.isEmpty()) {
				
				netCollectionAmountCash = accountingVoucherDetailRepository
						.findnetCollectionAmountByUserIdandDateBetweenAndPaymentModeCash(accId, PaymentMode.Cash);
				netCollectionAmountCheque = accountingVoucherDetailRepository
						.findnetCollectionAmountByUserIdandDateBetweenAndPaymentModeCheque(accId, PaymentMode.Cheque);
			}

			Double documentTotal = 0.0;
			for (Object[] obj : inventoryVoucher) {
				if (obj[1].toString().equals(employeeName)) {
					
					documentTotal += Double.valueOf(obj[0].toString());
				}
			}
			stock.setDocumentTotal(documentTotal);
			Double amount = 0.0;
			for (Object[] obj : accountingVouchers) {
				if (obj[1].toString().equals(employeeName)) {
					amount += Double.valueOf(obj[0].toString());
				}
			}
			stock.setReceivedvalue(amount);

			for (Object[] obj : netCollectionAmountCash) {
				if (obj[1].toString().equals(employeeName)) {
					stock.setTotalCash(Double.valueOf(obj[0].toString()));
				}
			}
			for (Object[] obj : netCollectionAmountCheque) {
				if (obj[1].toString().equals(employeeName)) {
					stock.setTotalCheque(Double.valueOf(obj[0].toString()));
				}
			}

			stockTotal.add(stock);
			stockDetails.addAll(unSaled);
			stockDetails.sort(
					(StockDetailsDTO s1, StockDetailsDTO s2) -> s1.getProductName().compareTo(s2.getProductName()));

			
		}

		buildpdf(stockDetails, stockTotal,employeePid, response);
	}

	private void buildpdf(List<StockDetailsDTO> stockDetails, List<StockDetailsTotalDTO> stockTotal,
			String employeePid, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub

		response.setContentType("application/pdf");

		response.setHeader("Content-Disposition", "inline;filename=\"" + "StockDetails.pdf\"");

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
			String strDate = null;
			if (dateTime != null) {
			Instant i = dateTime.atZone(ZoneId.systemDefault()).toInstant();
			Date dates = Date.from(i);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
			 strDate = dateFormat.format(dates);
			}
			Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService.findOneByPid(employeePid);
			  String Employeename =employeeProfileDTO.get().getName();
			Paragraph companyName = new Paragraph();
			Paragraph line = new Paragraph();
			companyName.setAlignment(Element.ALIGN_CENTER);
			line.setAlignment(Element.ALIGN_CENTER);
			companyName.setFont(fontSize_22);
			companyName.add(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()).getLegalName());
			line.add(new Paragraph("_______________________________________________________"));
			document.add(companyName);
			document.add(line);
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("Employee Name : "+ Employeename));
			document.add(new Paragraph("Applying Date&Time : " +strDate ));
			document.add(new Paragraph("\n"));
			PdfPTable table = createPdfTable(stockDetails);
			table.setWidthPercentage(100);
			document.add(table);
			document.add(new Paragraph("\n\n"));
			PdfPTable tableTotal = createTotalTable(stockTotal);
			tableTotal.setWidthPercentage(50);
			tableTotal.setHorizontalAlignment(Element.ALIGN_LEFT);
			document.add(tableTotal);

			document.newPage();
			PdfAction action = new PdfAction(PdfAction.PRINTDIALOG);
			writer.setOpenAction(action);

			document.close();

		}

		catch (DocumentException exc) {
			throw new IOException(exc.getMessage());
		} finally {
			out.close();
		}
	}

	private PdfPTable createPdfTable(List<StockDetailsDTO> stockDetails) {
		com.itextpdf.text.Font fontWeight = FontFactory.getFont(FontFactory.TIMES, 12f, com.itextpdf.text.Font.BOLD);

		PdfPTable table = new PdfPTable(new float[] {100f, 50f, 50f, 50f, 50f });
		table.setTotalWidth(350f);

		PdfPCell blank = new PdfPCell(new Paragraph("\n"));
		blank.setBorder(Rectangle.NO_BORDER);
		blank.setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(blank);
		table.addCell(blank);
		table.addCell(blank);
		table.addCell(blank);
		table.addCell(blank);
		


		PdfPCell cell1 = new PdfPCell(new Paragraph("ProductName", fontWeight));
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell2 = new PdfPCell(new Paragraph("Opening Stock", fontWeight));
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell3 = new PdfPCell(new Paragraph("Sales Quantity", fontWeight));
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell4 = new PdfPCell(new Paragraph("Damage Qty", fontWeight));
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell cell5 = new PdfPCell(new Paragraph("Closing Stock", fontWeight));
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(cell1);
		table.addCell(cell2);
		table.addCell(cell3);
		table.addCell(cell4);
		table.addCell(cell5);
		

		
	
		for (StockDetailsDTO stockdto : stockDetails) {

			fontWeight = FontFactory.getFont(FontFactory.TIMES, 11f, com.itextpdf.text.Font.NORMAL);

			
			PdfPCell col1 = new PdfPCell(new Paragraph(String.valueOf(stockdto.getProductName()), fontWeight));
			col1.setHorizontalAlignment(Element.ALIGN_LEFT);

			PdfPCell col2 = new PdfPCell(new Paragraph(String.valueOf(stockdto.getOpeningStock()), fontWeight));
			col2.setHorizontalAlignment(Element.ALIGN_LEFT);

			PdfPCell col3 = new PdfPCell(new Paragraph(String.valueOf(stockdto.getSaledQuantity()), fontWeight));
			col3.setHorizontalAlignment(Element.ALIGN_LEFT);

			PdfPCell col4 = new PdfPCell(new Paragraph(String.valueOf(stockdto.getDamageQty()), fontWeight));
			col4.setHorizontalAlignment(Element.ALIGN_LEFT);

			PdfPCell col5 = new PdfPCell(new Paragraph(String.valueOf(stockdto.getClosingStock()), fontWeight));
			col5.setHorizontalAlignment(Element.ALIGN_LEFT);

			table.addCell(col1);
			table.addCell(col2);
			table.addCell(col3);
			table.addCell(col4);
			table.addCell(col5);
			
		}

		return table;
	}

	private PdfPTable createTotalTable(List<StockDetailsTotalDTO> stockTotal) {
		// TODO Auto-generated method stub
		stockTotal.forEach(data -> System.out.println(data.getTotalCash()));

		PdfPTable table = new PdfPTable(new float[] { 8f, 8f });

		for (StockDetailsTotalDTO stockdetails : stockTotal) {

			DecimalFormat df = new DecimalFormat("0.00");

			PdfPCell cell1 = new PdfPCell(new Paragraph("Total Bill Value:"));
			cell1.setBorder(Rectangle.NO_BORDER);

			PdfPCell cell2 = new PdfPCell(new Paragraph(String.valueOf(df.format(stockdetails.getDocumentTotal()))));
			cell2.setBorder(Rectangle.NO_BORDER);

			PdfPCell cell3 = new PdfPCell(new Paragraph("Total Received Value :"));
			cell3.setBorder(Rectangle.NO_BORDER);

			PdfPCell cell4 = new PdfPCell(new Paragraph(df.format(stockdetails.getReceivedvalue())));
			cell4.setBorder(Rectangle.NO_BORDER);

			PdfPCell cell5 = new PdfPCell(new Paragraph("Total Cash :"));
			cell5.setBorder(Rectangle.NO_BORDER);

			PdfPCell cell6 = new PdfPCell(new Paragraph(df.format(stockdetails.getTotalCash())));
			cell6.setBorder(Rectangle.NO_BORDER);

			PdfPCell cell7 = new PdfPCell(new Paragraph("Total Cheque :"));
			cell7.setBorder(Rectangle.NO_BORDER);

			PdfPCell cell8 = new PdfPCell(new Paragraph(df.format(stockdetails.getTotalCheque())));
			cell8.setBorder(Rectangle.NO_BORDER);

			table.addCell(cell1);
			table.addCell(cell2);
			table.addCell(cell3);
			table.addCell(cell4);
			table.addCell(cell5);
			table.addCell(cell6);
			table.addCell(cell7);
			table.addCell(cell8);

		}
		return table;
	}
}