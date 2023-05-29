package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountProfileGeoLocationTagging;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;
import com.orderfleet.webapp.repository.AccountProfileGeoLocationTaggingRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileGeoLocationTaggingService;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.web.rest.api.dto.AccountProfileGeoLocationTaggingDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Controller For Add Geo Location
 *
 * @author fahad
 * @since Jul 5, 2017
 */
@Controller
@RequestMapping("/web")
public class AddGeoLocationResource {

	private final Logger log = LoggerFactory.getLogger(AddGeoLocationResource.class);

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private AccountProfileGeoLocationTaggingService accountProfileGeoLocationTaggingService;

	@Inject
	private AccountProfileGeoLocationTaggingRepository accountProfileGeoLocationTaggingRepository;
	
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@RequestMapping(value = "/add-geo-location", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Model model) throws URISyntaxException {

		model.addAttribute("accountTypes", accountTypeService.findAllByCompany());
		model.addAttribute("deactivatedAccountProfiles", accountProfileService.findAllByCompanyAndActivated(false));
		return "company/addGeoLocation";
	}

	@RequestMapping(value = "/add-geo-location/filterByAccountType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<AccountProfileDTO>> filterAccountProfilesByAccountTypes(
			@RequestParam String accountTypePids, @RequestParam String importedStatus, @RequestParam String geoTag)
			throws URISyntaxException {
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		boolean imports;
		log.info("GeoTagg ----- " + geoTag);
		// Not selected
		List<AccountProfileGeoLocationTagging> geolocations = accountProfileGeoLocationTaggingRepository
				.findAllByCompanyId();
		
	     	System.out.println("Size:"+geolocations.size());
		List<AccountProfileDTO> accountProfiles = new ArrayList<>();
		if (accountTypePids.isEmpty() && importedStatus.isEmpty()) {
			System.out.println("Enter here.......................................1");
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));

 			for (AccountProfileDTO account : accountProfileDTOs) {

				if (account.getGeoTaggingType() != null
						&& account.getGeoTaggingType().equals(GeoTaggingType.MOBILE_TAGGED)) {

					List<AccountProfileGeoLocationTagging> geolocation = geolocations.stream()
							.filter(a -> a.getAccountProfile().getPid().equals(account.getPid())).collect(Collectors.toList());
					
					System.out.println("Geolocation Size :"+geolocation.size());
					if(geolocation.size() == 0)
					{
						continue;
					}
					Comparator<AccountProfileGeoLocationTagging> comparator = Comparator.comparing(AccountProfileGeoLocationTagging::getSendDate);
					 AccountProfileGeoLocationTagging lastGeoLocation = geolocation.stream().filter(date->date.getSendDate()!= null).max(comparator).get();
					account.setGeoTaggedTime(lastGeoLocation.getSendDate());
					account.setGeoTaggedUserLogin(lastGeoLocation.getUser().getLogin());
                   EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUserLogin(lastGeoLocation.getUser().getLogin());
                  
                    account.setEmployeeName(employee.getName());
					
				}
				else {
					
					account.setEmployeeName(account.getGeoTaggedUserName());
				}
				accountProfiles.add(account);

			}
			if ("true".equals(geoTag)) {
				accountProfiles.removeIf(acc -> acc.getGeoTaggingType() == null);
				accountProfiles.sort((a1, a2) -> a2.getGeoTaggedTime().compareTo(a1.getGeoTaggedTime()));
			} else if ("false".equals(geoTag)) {
				accountProfiles.removeIf(acc -> acc.getGeoTaggingType() != null);
			} else {
				// do nothing
			}
			return new ResponseEntity<>(accountProfiles, HttpStatus.OK);
		}

		// Both selected
		if (!accountTypePids.isEmpty() && !importedStatus.isEmpty()) {
			System.out.println("Enter here.......................................2");
			if (importedStatus.equals("true")) {
				imports = true;
				accountProfileDTOs
						.addAll(accountProfileService.findAccountProfileByAccountTypePidInAndActivatedAndImportStatus(
								Arrays.asList(accountTypePids.split(",")), imports));
			} else if (importedStatus.equals("false")) {
				imports = false;
				accountProfileDTOs
						.addAll(accountProfileService.findAccountProfileByAccountTypePidInAndActivatedAndImportStatus(
								Arrays.asList(accountTypePids.split(",")), imports));
			} else {
				accountProfileDTOs.addAll(accountProfileService
						.findAccountProfileByAccountTypePidInAndActivated(Arrays.asList(accountTypePids.split(","))));
			}

			for (AccountProfileDTO account : accountProfileDTOs) {

				if (account.getGeoTaggingType() != null
						&& account.getGeoTaggingType().equals(GeoTaggingType.MOBILE_TAGGED)) {

					List<AccountProfileGeoLocationTagging> geolocation = geolocations.stream()
							.filter(a -> a.getAccountProfile().getPid().equals(account.getPid())).collect(Collectors.toList());
					
					Comparator<AccountProfileGeoLocationTagging> comparator = Comparator.comparing(AccountProfileGeoLocationTagging::getSendDate);
					 AccountProfileGeoLocationTagging lastGeoLocation = geolocation.stream().filter(date->date.getSendDate()!= null).max(comparator).get();
					account.setGeoTaggedTime(lastGeoLocation.getSendDate());
					account.setGeoTaggedUserLogin(lastGeoLocation.getUser().getLogin());
                   EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUserLogin(lastGeoLocation.getUser().getLogin());
                  
                    account.setEmployeeName(employee.getName());
					
				}
				else {
					
					account.setEmployeeName(account.getGeoTaggedUserName());
				}
				accountProfiles.add(account);

			}
			
			if ("true".equals(geoTag)) {
				accountProfiles.removeIf(acc -> acc.getGeoTaggingType() == null);
				accountProfiles.sort((a1, a2) -> a2.getGeoTaggedTime().compareTo(a1.getGeoTaggedTime()));
			} else if ("false".equals(geoTag)) {
				accountProfiles.removeIf(acc -> acc.getGeoTaggingType() != null);
			} else {
				// do nothing
			}
			return new ResponseEntity<>(accountProfiles, HttpStatus.OK);
		}

		// ImportStatus Selected
		if (accountTypePids.isEmpty() && !importedStatus.isEmpty()) {
			System.out.println("Enter here.......................................3");
			if (importedStatus.equals("true")) {
				imports = true;
				accountProfileDTOs
						.addAll(accountProfileService.findAllByCompanyAndAccountImportStatusAndActivated(imports));
			} else if (importedStatus.equals("false")) {
				imports = false;
				accountProfileDTOs
						.addAll(accountProfileService.findAllByCompanyAndAccountImportStatusAndActivated(imports));
			} else {
				accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
			}

			for (AccountProfileDTO account : accountProfileDTOs) {

				if (account.getGeoTaggingType() != null
						&& account.getGeoTaggingType().equals(GeoTaggingType.MOBILE_TAGGED)) {

					List<AccountProfileGeoLocationTagging> geolocation = geolocations.stream()
							.filter(a -> a.getAccountProfile().getPid().equals(account.getPid())).collect(Collectors.toList());
					
					Comparator<AccountProfileGeoLocationTagging> comparator = Comparator.comparing(AccountProfileGeoLocationTagging::getSendDate);
					 AccountProfileGeoLocationTagging lastGeoLocation = geolocation.stream().filter(date->date.getSendDate()!= null).max(comparator).get();
					account.setGeoTaggedTime(lastGeoLocation.getSendDate());
					account.setGeoTaggedUserLogin(lastGeoLocation.getUser().getLogin());
                   EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUserLogin(lastGeoLocation.getUser().getLogin());
                  
                    account.setEmployeeName(employee.getName());
					
				}
				else {
					
					account.setEmployeeName(account.getGeoTaggedUserName());
				}
				accountProfiles.add(account);

			}
			if ("true".equals(geoTag)) {
				accountProfiles.removeIf(acc -> acc.getGeoTaggingType() == null);
				accountProfiles.sort((a1, a2) -> a2.getGeoTaggedTime() != null && a1.getGeoTaggedTime() != null
						? a2.getGeoTaggedTime().compareTo(a1.getGeoTaggedTime())
						: 0);
			} else if ("false".equals(geoTag)) {
				accountProfiles.removeIf(acc -> acc.getGeoTaggingType() != null);
			} else {
				// do nothing
			}
			return new ResponseEntity<>(accountProfiles, HttpStatus.OK);
		}

		// AccountType Selected
		if (!accountTypePids.isEmpty() && importedStatus.isEmpty()) {
			System.out.println("Enter here.......................................4");
			accountProfileDTOs.addAll(accountProfileService
					.findAccountProfileByAccountTypePidInAndActivated(Arrays.asList(accountTypePids.split(","))));

			for (AccountProfileDTO account : accountProfileDTOs) {

				if (account.getGeoTaggingType() != null
						&& account.getGeoTaggingType().equals(GeoTaggingType.MOBILE_TAGGED)) {

					List<AccountProfileGeoLocationTagging> geolocation = geolocations.stream()
							.filter(a -> a.getAccountProfile().getPid().equals(account.getPid())).collect(Collectors.toList());
					
					Comparator<AccountProfileGeoLocationTagging> comparator = Comparator.comparing(AccountProfileGeoLocationTagging::getSendDate);
					 AccountProfileGeoLocationTagging lastGeoLocation = geolocation.stream().filter(date->date.getSendDate()!= null).max(comparator).get();
					account.setGeoTaggedTime(lastGeoLocation.getSendDate());
					account.setGeoTaggedUserLogin(lastGeoLocation.getUser().getLogin());
                   EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUserLogin(lastGeoLocation.getUser().getLogin());
                  
                    account.setEmployeeName(employee.getName());
					
				}
				else {
					
					account.setEmployeeName(account.getGeoTaggedUserName());
				}
				accountProfiles.add(account);

			}
			if ("true".equals(geoTag)) {
				accountProfiles.removeIf(acc -> acc.getGeoTaggingType() == null);
				accountProfiles.sort((a1, a2) -> a2.getGeoTaggedTime().compareTo(a1.getGeoTaggedTime()));
			} else if ("false".equals(geoTag)) {
				accountProfiles.removeIf(acc -> acc.getGeoTaggingType() != null);
			} else {
				// do nothing
			}
			return new ResponseEntity<>(accountProfiles, HttpStatus.OK);
		}

		return new ResponseEntity<>(accountProfiles, HttpStatus.OK);
	}

	@RequestMapping(value = "/add-geo-location/download-account-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadProductProfileXls(@RequestParam String geoTag, HttpServletResponse response) {
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
		
		List<AccountProfileGeoLocationTagging> geolocations = accountProfileGeoLocationTaggingRepository
				.findAllByCompanyId();
		List<AccountProfileDTO> accountProfiles = new ArrayList<>();
		for (AccountProfileDTO account : accountProfileDTOs) {

			if (account.getGeoTaggingType() != null
					&& account.getGeoTaggingType().equals(GeoTaggingType.MOBILE_TAGGED)) {

				List<AccountProfileGeoLocationTagging> geolocation = geolocations.stream()
						.filter(a -> a.getAccountProfile().getPid().equals(account.getPid())).collect(Collectors.toList());
				
				Comparator<AccountProfileGeoLocationTagging> comparator = Comparator.comparing(AccountProfileGeoLocationTagging::getSendDate);
				 AccountProfileGeoLocationTagging lastGeoLocation = geolocation.stream().filter(date->date.getSendDate()!= null).max(comparator).get();
				account.setGeoTaggedTime(lastGeoLocation.getSendDate());
				account.setGeoTaggedUserLogin(lastGeoLocation.getUser().getLogin());
               EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUserLogin(lastGeoLocation.getUser().getLogin());
              
                account.setEmployeeName(employee.getName());
				
			}
			else {
				
				account.setEmployeeName(account.getGeoTaggedUserName());
			}
			accountProfiles.add(account);

		}

		switch (geoTag) {
		case "all":
			// do nothing
			break;
		case "true":
			accountProfiles.removeIf(accDto -> accDto.getGeoTaggingType() == null);
			accountProfiles.sort((a1, a2) -> a2.getGeoTaggedTime() != null && a1.getGeoTaggedTime() != null
					? a2.getGeoTaggedTime().compareTo(a1.getGeoTaggedTime())
					: 0);
			break;
		case "false":
			accountProfiles.removeIf(accDto -> accDto.getGeoTaggingType() != null);
			break;
		}
		if (accountProfiles.size() != 0) {
			buildExcelDocument(accountProfiles, response);
		}

	}

	private void buildExcelDocument(List<AccountProfileDTO> accountProfileDTOs, HttpServletResponse response) {
		log.debug("Downloading Geo Tag Excel report");
		String excelFileName = "geotag_accounts" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Customer","Employee", "Tag Type", "Tag Time", "Tagged User", "Latitude",
				"Longitude", "Geo Location" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, accountProfileDTOs);
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
			log.error("IOException on downloading Geo tagged profiles {}", ex.getMessage());
		}
	}

	private void createReportRows(HSSFSheet worksheet, List<AccountProfileDTO> accountProfileDTOs) {
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
		for (AccountProfileDTO ap : accountProfileDTOs) {
			HSSFRow row = worksheet.createRow(rowNum++);
			row.createCell(0).setCellValue(ap.getName().replace("#13;#10;", " "));
			row.createCell(1).setCellValue(ap.getEmployeeName()== null ? "":ap.getEmployeeName());
			row.createCell(2).setCellValue(ap.getGeoTaggingType() == null ? "" : ap.getGeoTaggingType().toString());
			String formatDateTime = null;
			if (ap.getGeoTaggedTime() != null) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");
				formatDateTime = ap.getGeoTaggedTime().format(formatter);
			}
			row.createCell(3).setCellValue(ap.getGeoTaggedTime() == null ? "" : formatDateTime);
			row.createCell(4).setCellValue(ap.getGeoTaggedUserLogin() == null ? "" : ap.getGeoTaggedUserLogin());
			row.createCell(5).setCellValue(ap.getLatitude() == null ? "" : ap.getLatitude().toString());
			row.createCell(6).setCellValue(ap.getLongitude() == null ? "" : ap.getLongitude().toString());
			row.createCell(7).setCellValue(ap.getLocation() == null ? "" : ap.getLocation().toString());
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

	@RequestMapping(value = "/add-geo-location/getAccountProfileGeoLocation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileGeoLocationTaggingDTO>> getAllGeoLocationTaggingByAccountProfile(
			@RequestParam String accountProfilePid) {
		List<AccountProfileGeoLocationTaggingDTO> accountProfileGeoLocationTaggingDTOs = accountProfileGeoLocationTaggingService
				.getAllAccountProfileGeoLocationTaggingByAccountProfile(accountProfilePid);
		return new ResponseEntity<List<AccountProfileGeoLocationTaggingDTO>>(accountProfileGeoLocationTaggingDTOs,
				HttpStatus.OK);

	}

	@RequestMapping(value = "/add-geo-location/saveGeoLocation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> attachAccountProfile(@RequestParam("geoLocationPid") String geoLocationPid,
			@RequestParam("accountProfilePid") String accountProfilePid) {
		AccountProfileDTO accountProfileDTO = accountProfileService.findOneByPid(accountProfilePid).get();
		AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO = accountProfileGeoLocationTaggingService
				.findOneByPid(geoLocationPid).get();
		accountProfileDTO.setLatitude(accountProfileGeoLocationTaggingDTO.getLatitude());
		accountProfileDTO.setLongitude(accountProfileGeoLocationTaggingDTO.getLongitude());
		accountProfileDTO.setLocation(accountProfileGeoLocationTaggingDTO.getLocation());
		accountProfileDTO.setGeoTaggingType(GeoTaggingType.WEB_TAGGED_MOBILE);
		accountProfileDTO.setGeoTaggedTime(LocalDateTime.now());
		accountProfileDTO.setGeoTaggedUserLogin(SecurityUtils.getCurrentUserLogin());
		accountProfileDTO = accountProfileService.update(accountProfileDTO);
		return new ResponseEntity<AccountProfileDTO>(accountProfileDTO, HttpStatus.OK);

	}

	@RequestMapping(value = "/add-geo-location/attachAccountProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> attachAccountProfile(
			@RequestParam("accountProfilePid") String accountProfilePid, @RequestParam("latitude") BigDecimal latitude,
			@RequestParam("longitude") BigDecimal longitude, @RequestParam("location") String location) {
		AccountProfileDTO accountProfileDTO = accountProfileService.findOneByPid(accountProfilePid).get();
		accountProfileDTO.setLatitude(latitude);
		accountProfileDTO.setLongitude(longitude);
		accountProfileDTO.setLocation(location);
		accountProfileDTO.setGeoTaggingType(GeoTaggingType.WEB_TAGGED_MAP);
		accountProfileDTO.setGeoTaggedTime(LocalDateTime.now());
		accountProfileDTO.setGeoTaggedUserLogin(SecurityUtils.getCurrentUserLogin());
		accountProfileDTO = accountProfileService.update(accountProfileDTO);
		return new ResponseEntity<>(accountProfileDTO, HttpStatus.OK);

	}
}
