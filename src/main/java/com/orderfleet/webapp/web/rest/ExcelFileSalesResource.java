package com.orderfleet.webapp.web.rest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
import com.itextpdf.text.Chunk;
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
import com.itextpdf.text.pdf.codec.Base64.OutputStream;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
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
public class ExcelFileSalesResource {

	private final Logger log = LoggerFactory.getLogger(ExcelFileSalesResource.class);

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
	@RequestMapping(value = "/excel-file-sales", method = RequestMethod.GET)
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
		return "company/excel-file-sales";
	}

	/**
	 * GET /primary-sales-performance/:id : get the "id" InventoryVoucher.
	 *
	 * @param id the id of the InventoryVoucherDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         InventoryVoucherDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/excel-file-sales/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

	@RequestMapping(value = "/excel-file-sales/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
		if (filterBy.equals(ExcelFileSalesResource.CUSTOM)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		} else if (filterBy.equals(ExcelFileSalesResource.YESTERDAY)) {
			fDate = LocalDate.now().minusDays(1);
			tDate = fDate;
		} else if (filterBy.equals(ExcelFileSalesResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fDate = LocalDate.now().with(fieldISO, 1);
		} else if (filterBy.equals(ExcelFileSalesResource.MTD)) {
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

		boolean pdfDownloadButtonStatus = false;
		Optional<CompanyConfiguration> opCompanyConfiguration = CompanyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SALES_PDF_DOWNLOAD);
		if (opCompanyConfiguration.isPresent()) {

			if (opCompanyConfiguration.get().getValue().equals("true")) {
				pdfDownloadButtonStatus = true;
			} else {
				pdfDownloadButtonStatus = false;
			}
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
			salesPerformanceDTO.setPdfDownloadButtonStatus(pdfDownloadButtonStatus);

			salesPerformanceDTO.setTallyDownloadStatus(TallyDownloadStatus.valueOf(ivData[16].toString()));
			salesPerformanceDTO.setVisitRemarks(ivData[17] == null ? null : ivData[17].toString());

			salesPerformanceDTO.setOrderNumber(ivData[18] == null ? 0 : Long.parseLong(ivData[18].toString()));

			salesPerformanceDTO.setPdfDownloadStatus(Boolean.valueOf(ivData[19].toString()));

			salesPerformanceDTOs.add(salesPerformanceDTO);
		}
		return salesPerformanceDTOs;
	}

	@RequestMapping(value = "/excel-file-sales/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) {
		return primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
	}

	@RequestMapping(value = "/excel-file-sales/download-inventory-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadInventoryXls(@RequestParam("inventoryVoucherHeaderPids") String[] inventoryVoucherHeaderPids,
			HttpServletResponse response) {
		List<String> ivhList = Arrays.asList(inventoryVoucherHeaderPids);
		List<Object[]> inventoryObject = inventoryVoucherHeaderRepository.getExcelFileSales(ivhList);
		if (inventoryObject.isEmpty()) {
			return;
		}
		//buildExcelDocument(inventoryObject, response);
		buildCSVDocument(inventoryObject, response);
		inventoryVoucherHeaderRepository.
			updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(
					TallyDownloadStatus.COMPLETED, SecurityUtils.getCurrentUsersCompanyId(), ivhList);
		
	}

	private void buildCSVDocument(List<Object[]> inventoryVoucherHeaderDTOs,
			HttpServletResponse response) {
		log.debug("Downloading Excel report");
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"SalesOrder.csv\"");
		    try
		    {
		        ServletOutputStream outputStream = response.getOutputStream();
		        String outputResult = createReportEntries(inventoryVoucherHeaderDTOs);
		        outputStream.write(outputResult.getBytes());
		        outputStream.flush();
		        outputStream.close();
		    }
		    catch(Exception e)
		    {
		        System.out.println(e.toString());
		    }
	}
	
	private String createReportEntries(List<Object[]> inventoryVoucherHeaderDTOs) {
		StringBuilder sb = new StringBuilder();
		for (Object[] object : inventoryVoucherHeaderDTOs) {
				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yy");
				Date date = new Date();
				
				String accountName = object[0] != null ?object[0].toString() : "";
				String refDocNo = object[1] != null ? object[1].toString() : "0";
				String docDate = object[2] != null ? object[2].toString() : "";
				
				try {
					date = format1.parse(docDate.split(" ")[0]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String formatedDate = format2.format(date);
				String empName = object[3] != null ? object[3].toString() : ""; 
				String ppName = object[4] != null ? object[4].toString() : "";
				String sellingRate = object[5] != null ?object[5].toString() : "0.0";
				String qty = object[6] != null ? object[6].toString() : "0.0"; 
				String sku  = object[7] != null ? object[7].toString() : "";

				
				sb.append(accountName+","+refDocNo+","+formatedDate+","+ppName+","+sku+","+qty+","+sellingRate+","+empName);
				sb.append("\n");
		}
		return sb.toString();
	}
	

	@RequestMapping(value = "/excel-file-sales/changeStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> changeStatus(@RequestParam String pid,
			@RequestParam TallyDownloadStatus tallyDownloadStatus) {
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(pid).get();
		inventoryVoucherHeaderDTO.setTallyDownloadStatus(tallyDownloadStatus);
		inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	@RequestMapping(value = "/excel-file-sales/downloadPdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadStatus(@RequestParam String inventoryPid, HttpServletResponse response) throws IOException {

		log.info("Download pdf with pid " + inventoryPid);

		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(inventoryPid).get();

		buildPdf(inventoryVoucherHeaderDTO, response);

		if (!inventoryVoucherHeaderDTO.getPdfDownloadStatus()) {
				inventoryVoucherHeaderRepository.updatePdfDownlodStatusByPid(inventoryVoucherHeaderDTO.getPid());
			
		}

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
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();

			// writer.addJavaScript("this.print(false);", false);

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
	
	
	//for excel file generation
	private void buildExcelDocument(List<Object[]> inventoryVoucherHeaderDTOs,
			HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "SalesOrder" + ".xls";
		String sheetName = "Salesorder";
		String[] headerColumns = {"Customer", "OrderId", "Date","Product Name","Unit Quantity","Quantity","Row Total","Location"};
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			//createHeaderRow(worksheet, headerColumns);
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
	
	
	//for excel file generation
	private void createReportRows(HSSFSheet worksheet, List<Object[]> inventoryVoucherHeaderDTOs) {
		/*
		 * CreationHelper helps us create instances of various things like DataFormat,
		 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
		 */
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
//		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss"));
//		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yy"));
		// Create Other rows and cells with Sales data
		int rowNum = 0;
		for (Object[] object : inventoryVoucherHeaderDTOs) {
				HSSFRow row = worksheet.createRow(rowNum++);
				String accountName = object[0] != null ?object[0].toString() : "";
				String refDocNo = object[1] != null ? object[1].toString() : "0";
				String docDate = object[2] != null ? object[2].toString() : "";
				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yy");
				Date date = new Date();
				try {
					date = format1.parse(docDate.split(" ")[0]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String formatedDate = format2.format(date);
				String empName = object[3] != null ? object[3].toString() : ""; 
				String ppName = object[4] != null ? object[4].toString() : "";
				String sellingRate = object[5] != null ?object[5].toString() : "0.0";
				String qty = object[6] != null ? object[6].toString() : "0.0"; 
				String sku  = object[7] != null ? object[7].toString() : "";

				row.createCell(0).setCellValue(accountName);
				row.createCell(1).setCellValue(refDocNo);
				row.createCell(2).setCellValue(formatedDate);
				row.createCell(3).setCellValue(ppName);
				row.createCell(4).setCellValue(sku);
				row.createCell(5).setCellValue(qty);
				row.createCell(6).setCellValue(sellingRate);
				row.createCell(7).setCellValue(empName);
		}
	}

}
