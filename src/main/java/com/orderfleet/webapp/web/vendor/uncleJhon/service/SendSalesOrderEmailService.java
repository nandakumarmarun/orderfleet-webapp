package com.orderfleet.webapp.web.vendor.uncleJhon.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.enums.SendSalesOrderEmailStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.SecondarySalesOrderExcelDTO;

@Service
public class SendSalesOrderEmailService {
	
	private final Logger log = LoggerFactory.getLogger(SendSalesOrderEmailService.class);
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;
	
	

	public SendSalesOrderEmailService(AccountProfileRepository accountProfileRepository,
			CompanyRepository companyRepository, InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository) {
		super();
		this.accountProfileRepository = accountProfileRepository;
		this.companyRepository = companyRepository;
		this.inventoryVoucherHeaderRepository = inventoryVoucherHeaderRepository;
	}

	public SendSalesOrderEmailService() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResponseEntity<InventoryVoucherHeaderDTO> salesOrderEmailToSupplier(List<InventoryVoucherHeader> inventoryVoucherHeaders) throws MessagingException {

		log.info("sendSalesOrderEmail Automatically()-----");
		

		List<SecondarySalesOrderExcelDTO> secondarySalesOrderExcelDTOs = convertInventoryObjectToExcelDTO(
				inventoryVoucherHeaders);

//		           Long companyId  = inventoryVoucherHeaders.get(0).getCompany().getId();
//
//		List<AccountProfile> accountProfiles = accountProfileRepository
//				.findAllByCompanyIdAndAccountTypeName(companyId, "Company");
//
//		String companyEmail = "";
//		if (accountProfiles != null && accountProfiles.size() > 0) {
//			companyEmail = accountProfiles.get(0).getEmail1();
//		}
//		

			String supplierEmail = secondarySalesOrderExcelDTOs.get(0).getSupplierEmail();
			String supplierName = secondarySalesOrderExcelDTOs.get(0).getSupplierName();

			if (supplierEmail != null && !supplierEmail.equalsIgnoreCase("")) {
			
				sendSalesOrderEmail(supplierName, supplierEmail, secondarySalesOrderExcelDTOs);
			}
			return new ResponseEntity<>(null, HttpStatus.OK);
		}

	private void sendSalesOrderEmail(String supplierName, String supplierEmail, 
			List<SecondarySalesOrderExcelDTO> secondarySalesOrderExcelDTOs) throws MessagingException {

		log.debug("Sending a mail to Supplier-- " + supplierEmail);
		
		Long companyId = secondarySalesOrderExcelDTOs.get(0).getCompanyId();

		long start = System.nanoTime();

		String companyName = companyRepository.findOne(companyId).getLegalName();

		JavaMailSender emailSender = getJavaMailSender();

		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setSubject(companyName + " - Sales Order");
		helper.setText("Sales Order Taken Under - " + supplierName);
		helper.setTo(supplierEmail);

//		if (companyEmail != null && !companyEmail.equalsIgnoreCase("")) {
//			log.debug("Sending a mail to Company Account-- " + companyEmail);
//			helper.setCc(companyEmail);
//		} else {
//			log.info("No Email found for Company Account-- ");
//		}
		helper.setFrom(supplierEmail);

		File excelFile = generateFile(supplierName, secondarySalesOrderExcelDTOs);
		FileSystemResource file = new FileSystemResource(excelFile);
		helper.addAttachment(file.getFilename(), file);
		emailSender.send(message);

		excelFile.delete();

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;

		List<String> succesOrders = (ArrayList<String>) secondarySalesOrderExcelDTOs.stream()
				.map(so -> so.getInventoryPid()).collect(Collectors.toList());
		
		inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderSendSalesOrderEmailStatusUsingPidAndCompanyId(
				SendSalesOrderEmailStatus.SENT, companyId, succesOrders);
		

		log.info("Sync completed in {} ms", elapsedTime);

	}

	private File generateFile(String supplierName,
			List<SecondarySalesOrderExcelDTO> secondarySalesOrderExcelDTOs) {
		log.debug("Creating Excel report");

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMYY_HHmmss");
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now);
		String excelFileName = date + "_SalesOrder" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Supplier", "Salesman", "Order Date", "Customer", "Product Name", "Quantity",
				"Selling Rate", "Total" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createSecondaryReportRows(worksheet, secondarySalesOrderExcelDTOs);
			// Resize all columns to fit the content size
			for (int i = 0; i < headerColumns.length; i++) {
				worksheet.autoSizeColumn(i);
			}

			FileOutputStream fos = new FileOutputStream(supplierName + "-" + excelFileName);

			workbook.write(fos);
			fos.flush();
			fos.close();

			return new File(supplierName + "-" + excelFileName);

		} catch (IOException ex) {
			log.error("IOException on downloading Sales Order {}", ex.getMessage());
		}
		return null;
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

	private void createSecondaryReportRows(HSSFSheet worksheet,
			List<SecondarySalesOrderExcelDTO> secondarySalesOrderExcelDTOs) {
		/*
		 * CreationHelper helps us create instances of various things like DataFormat,
		 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
		 */
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss a"));
		// Create Other rows and cells with Sales data
		int rowNum = 1;
		for (SecondarySalesOrderExcelDTO salesOrder : secondarySalesOrderExcelDTOs) {
			HSSFRow row = worksheet.createRow(rowNum++);

			row.createCell(0).setCellValue(salesOrder.getSupplierName());
			row.createCell(1).setCellValue(salesOrder.getEmployeeName());
			HSSFCell docDateCell = row.createCell(2);
			docDateCell.setCellValue(salesOrder.getDate().toString());
			docDateCell.setCellStyle(dateCellStyle);
			row.createCell(3).setCellValue(salesOrder.getReceiverName());
			row.createCell(4).setCellValue(salesOrder.getItemName());
			row.createCell(5).setCellValue(salesOrder.getQuantity());
			row.createCell(6).setCellValue(salesOrder.getRate());
			row.createCell(7).setCellValue(salesOrder.getTotal());

		}

	}

	

	private List<SecondarySalesOrderExcelDTO> convertInventoryObjectToExcelDTO(
			List<InventoryVoucherHeader> inventoryVoucherHeaderObjects) {

		List<SecondarySalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();
//		List<String> inventoryHeaderPid = new ArrayList<String>();

		for (InventoryVoucherHeader ivh : inventoryVoucherHeaderObjects) {
			SecondarySalesOrderExcelDTO salesOrderDTO = new SecondarySalesOrderExcelDTO();

			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a");
			String dateAsString = ivh.getCreatedDate().format(formatter);

			salesOrderDTO.setDate(dateAsString != null ? dateAsString : "");
			salesOrderDTO.setReceiverName(ivh.getReceiverAccount().getName() != null ? ivh.getReceiverAccount().getName() : "");
			salesOrderDTO.setSupplierName(ivh.getSupplierAccount().getName() != null ?ivh.getSupplierAccount().getName() : "");
			salesOrderDTO.setSupplierEmail(ivh.getSupplierAccount().getEmail1()!= null ? ivh.getSupplierAccount().getEmail1().toString() : "");
			for(InventoryVoucherDetail ivd :ivh.getInventoryVoucherDetails() )
			{
			salesOrderDTO.setItemName(ivd.getProduct().getName() != null ? ivd.getProduct().getName() : "");
			salesOrderDTO.setQuantity(ivd.getQuantity() != 0.0 ? ivd.getQuantity() : 0.0);
			salesOrderDTO.setRate(ivd.getSellingRate() != 0.0 ? ivd.getSellingRate() : 0.0);
			salesOrderDTO.setDiscPer(ivd.getDiscountPercentage() != 0.0 ? ivd.getDiscountPercentage() : 0.0);
			salesOrderDTO.setTaxPer(ivd.getTaxPercentage() != 0.0 ? ivd.getTaxPercentage() : 0.0);
			salesOrderDTO.setFreeQuantity(ivd.getFreeQuantity() != 0.0 ? ivd.getFreeQuantity() :0.0);

			double qty = ivd.getQuantity();
			double rate = ivd.getSellingRate();
			double dis = ivd.getDiscountPercentage();
			double taxPer = ivd.getTaxPercentage();

			double amountValue = qty * rate;
			double discountValue = amountValue * dis / 100;
			double amountWithoutTax = amountValue - discountValue;

			// tax calculation

			double taxValue = amountWithoutTax * taxPer / 100;
			double totalAmount = amountWithoutTax + taxValue;

			DecimalFormat round = new DecimalFormat(".##");

			double cgst = Double.parseDouble(round.format(taxValue / 2));
			double sgst = Double.parseDouble(round.format(taxValue / 2));
			
			salesOrderDTO.setTotal(Double.parseDouble(round.format(totalAmount)));
			salesOrderDTO.setDiscPrice(Double.parseDouble(round.format(discountValue)));
			salesOrderDTO.setCGSTAmt(cgst);
			salesOrderDTO.setSGSTAmt(sgst);
//			inventoryHeaderPid.add(ivd.getTaxPercentage() != 0.0 ? ivd.getTaxPercentage() : "");
			}
			salesOrderDTO.setInventoryPid(ivh.getPid()!= null ? ivh.getPid() : "");
			salesOrderDTO.setEmployeeName(ivh.getEmployee().getName() != null ? ivh.getEmployee().getName() : "");

			salesOrderDTO.setBillNo(ivh.getDocumentNumberServer());

			salesOrderDTO.setRefDocNo(ivh.getReferenceDocumentNumber() != null ? ivh.getReferenceDocumentNumber(): "0");
			salesOrderDTO.setCompanyId(ivh.getCompany().getId());
		
			salesOrderDTOs.add(salesOrderDTO);


		}

		return salesOrderDTOs;
	}
	public JavaMailSender getJavaMailSender() {
		   JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		   mailSender.setHost("smtp.gmail.com");
		   mailSender.setPort(587);
		   mailSender.setUsername("salesnrich.info@gmail.com");
		   mailSender.setPassword("fohcijtqujnqyqcn");

		   Properties props = mailSender.getJavaMailProperties();
		   props.put("mail.transport.protocol", "smtp");
		   props.put("mail.smtp.auth", "true");
		   props.put("mail.smtp.starttls.enable", "true");
		   props.put("mail.debug", "false");

		   return mailSender;
		}
}
