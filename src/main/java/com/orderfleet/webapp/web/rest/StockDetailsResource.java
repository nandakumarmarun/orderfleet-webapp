package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.StockFlow;
import com.orderfleet.webapp.repository.DocumentStockCalculationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
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
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
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
			stockDetails = inventoryVoucherHeaderService.findAllStockDetails(companyId, userId, fromDate, toDate,null);
			List<StockDetailsDTO> unSaled = stockDetailsService.findOtherStockItems(userOp.get(),stockLocations,false);
			for (StockDetailsDTO dto : stockDetails) {
				unSaled.removeIf(unSale -> unSale.getProductName().equals(dto.getProductName()));
			}
			stockDetails.addAll(unSaled);
		}
		stockDetails
				.sort((StockDetailsDTO s1, StockDetailsDTO s2) -> s1.getProductName().compareTo(s2.getProductName()));
		model.addAttribute("stockDetails", stockDetails);
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
		List<OpeningStock> openingStockUserBased = openingStockRepository
				.findByStockLocationInOrderByCreatedDateAsc(new ArrayList<>(usersStockLocations));
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		if (openingStockUserBased.size() != 0) {
			LocalDateTime fromDate = openingStockUserBased.get(0).getCreatedDate();
			// LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
			LocalDateTime toDate = LocalDate.now().atTime(23, 59);
			stockDetails = inventoryVoucherHeaderService.findAllStockDetails(companyId, userId, fromDate, toDate,null);
			List<StockDetailsDTO> unSaled = stockDetailsService.findOtherStockItems(user.get(),usersStockLocations,false);
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
	public  void downloadXls(@RequestParam("employeePid") String employeePid,HttpServletResponse response) {
		log.debug("Web request to download Stock Details");
		
		
		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService.findOneByPid(employeePid);
		String userPid = employeeProfileDTO.get().getUserPid();
		Optional<User> user = userRepository.findOneByPid(userPid);
		long userId = user.get().getId();
		List<UserStockLocation> userStockLocations = userStockLocationRepository.findByUserPid(user.get().getPid());
		Set<StockLocation> usersStockLocations = userStockLocations.stream().map(usl -> usl.getStockLocation())
				.collect(Collectors.toSet());
		List<OpeningStock> openingStockUserBased = openingStockRepository
				.findByStockLocationInOrderByCreatedDateAsc(new ArrayList<>(usersStockLocations));
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		if (openingStockUserBased.size() != 0) {
			LocalDateTime fromDate = openingStockUserBased.get(0).getCreatedDate();
			// LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
			LocalDateTime toDate = LocalDate.now().atTime(23, 59);
			stockDetails = inventoryVoucherHeaderService.findAllStockDetails(companyId, userId, fromDate, toDate,null);
			List<StockDetailsDTO> unSaled = stockDetailsService.findOtherStockItems(user.get(),usersStockLocations,false);
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
				"DamageQty","ClosingStock" };
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
		
		for(StockDetailsDTO stockDTO:stockDetails)
		{
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
	
	
	
	
	
	
}