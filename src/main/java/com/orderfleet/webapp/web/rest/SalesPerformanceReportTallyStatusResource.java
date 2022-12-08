package com.orderfleet.webapp.web.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
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
import java.util.concurrent.TimeUnit;
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
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.vendor.odoo.service.SendInvoiceOdooService;

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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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

	@Inject
	private SendInvoiceOdooService sendInvoiceOdooService;

	@Inject
	private AccountProfileMapper accountProfileMapper;

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
//			List<Object[]> accountPidNames = locationAccountProfileRepository
//			.findAccountProfilesByLocationIdIn(locationIds);
//	int size = accountPidNames.size();
//	List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>(size);
//	for (int i = 0; i < size; i++) {
//		AccountProfileDTO accountProfileDTO = new AccountProfileDTO();
//		accountProfileDTO.setPid(accountPidNames.get(i)[0].toString());
//		accountProfileDTO.setName(accountPidNames.get(i)[1].toString());
//		accountProfileDTOs.add(accountProfileDTO);
//	}

			Set<BigInteger> apIds = locationAccountProfileRepository
					.findAccountProfileIdsByUserLocationsOrderByAccountProfilesName(locationIds);

			Set<Long> accountProfileIds = new HashSet<>();

			for (BigInteger apId : apIds) {
				accountProfileIds.add(apId.longValue());
			}
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "AP_QUERY_137" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get all by compId and IdsIn";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			List<AccountProfile> accountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(accountProfileIds);
			String flag1 = "Normal";
			LocalDateTime endLCTime1 = LocalDateTime.now();
			String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
			String endDate1 = startLCTime1.format(DATE_FORMAT1);
			Duration duration1 = Duration.between(startLCTime1, endLCTime1);
			long minutes1 = duration1.toMinutes();
			if (minutes1 <= 1 && minutes1 >= 0) {
				flag1 = "Fast";
			}
			if (minutes1 > 1 && minutes1 <= 2) {
				flag1 = "Normal";
			}
			if (minutes1 > 2 && minutes1 <= 10) {
				flag1 = "Slow";
			}
			if (minutes1 > 10) {
				flag1 = "Dead Slow";
			}
	                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description1);
			// remove duplicates
			List<AccountProfile> result = accountProfiles.parallelStream().distinct().collect(Collectors.toList());

			List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
					.accountProfilesToAccountProfileDTOs(result);

			model.addAttribute("accounts", accountProfileDTOs);

		}
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());

		boolean sendSalesOrderEmailStatus = false;
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "COMP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="get by compId and name";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		Optional<CompanyConfiguration> opCompanyConfiguration = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SEND_SALES_ORDER_EMAIL);
		String flag1 = "Normal";
		LocalDateTime endLCTime1 = LocalDateTime.now();
		String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
		String endDate1 = startLCTime1.format(DATE_FORMAT1);
		Duration duration1 = Duration.between(startLCTime1, endLCTime1);
		long minutes1 = duration1.toMinutes();
		if (minutes1 <= 1 && minutes1 >= 0) {
			flag1 = "Fast";
		}
		if (minutes1 > 1 && minutes1 <= 2) {
			flag1 = "Normal";
		}
		if (minutes1 > 2 && minutes1 <= 10) {
			flag1 = "Slow";
		}
		if (minutes1 > 10) {
			flag1 = "Dead Slow";
		}
                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
				+ description1);
		if (opCompanyConfiguration.isPresent()) {

			if (opCompanyConfiguration.get().getValue().equals("true")) {
				sendSalesOrderEmailStatus = true;
			} else {
				sendSalesOrderEmailStatus = false;
			}
		}
		model.addAttribute("sendSalesOrderEmailStatus", sendSalesOrderEmailStatus);

		boolean sendSalesOrderOdoo = false;
		Optional<CompanyConfiguration> opCompanyConfigurationOdoo = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SEND_SALES_ORDER_ODOO);
		if (opCompanyConfigurationOdoo.isPresent()) {

			if (opCompanyConfigurationOdoo.get().getValue().equals("true")) {
				sendSalesOrderOdoo = true;
			} else {
				sendSalesOrderOdoo = false;
			}
		}
		model.addAttribute("sendSalesOrderOdooStatus", sendSalesOrderOdoo);

		boolean pdfDownloadStatus = false;
		Optional<CompanyConfiguration> opCompanyConfigurationPdf = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SALES_PDF_DOWNLOAD);
		if (opCompanyConfigurationPdf.isPresent()) {

			if (opCompanyConfigurationPdf.get().getValue().equals("true")) {
				pdfDownloadStatus = true;
			} else {
				pdfDownloadStatus = false;			}
		}
		model.addAttribute("pdfDownloadStatus", pdfDownloadStatus);

		return "company/primarySalesPerformanceTallyStatusReport";
	}

	/**
	 * GET /primary-sales-performance/:id : get the "id" InventoryVoucher.
	 *
	 * @param pid the id of the InventoryVoucherDTO to retrieve
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
		log.debug("Web request to filter inventory vouchers");
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
		case "FAILED":
			tallyStatus = Arrays.asList(TallyDownloadStatus.FAILED);
			break;
		case "ALL":
			tallyStatus = Arrays.asList(TallyDownloadStatus.COMPLETED, TallyDownloadStatus.PROCESSING,
					TallyDownloadStatus.PENDING, TallyDownloadStatus.FAILED);
			break;
		}

		List<Object[]> inventoryVouchers;
		if ("-1".equals(accountPid)) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_157" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get all ByUserIdIn And DocumentPidIn AndTallyDownloadStatusDateBetween";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndDocumentPidInAndTallyDownloadStatusDateBetweenOrderByCreatedDateDesc(userIds,
							documentPids, tallyStatus, fromDate, toDate);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
		
		} else {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_160" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get all By UserIdIn and AccountPidIn and DocumentPidIn and TallyDownloadStatusDateBetween";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndAccountPidInAndDocumentPidInAndTallyDownloadStatusDateBetweenOrderByCreatedDateDesc(
							userIds, accountPid, documentPids, tallyStatus, fromDate, toDate);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
		}
		if (inventoryVouchers.isEmpty()) {
			return Collections.emptyList();
		} else {
			return createSalesPerformanceDTO(inventoryVouchers);
		}
	}
	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}
	private List<SalesPerformanceDTO> createSalesPerformanceDTO(List<Object[]> inventoryVouchers) {
		// fetch voucher details
		boolean compConfig= getCompanyCofig();
		Set<String> ivHeaderPids = inventoryVouchers.parallelStream().map(obj -> obj[0].toString())
				.collect(Collectors.toSet());
		List<Object[]> ivDetails = inventoryVoucherDetailRepository.findByInventoryVoucherHeaderPidIn(ivHeaderPids);
		Map<String, Double> ivTotalVolume = ivDetails.stream().collect(Collectors.groupingBy(obj -> obj[0].toString(),
				Collectors.summingDouble(obj -> ((Double) (obj[3] == null ? 1.0d : obj[3]) * (Double) obj[4]))));

		boolean pdfDownloadButtonStatus = false;
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "COMP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="get by compId and name";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		Optional<CompanyConfiguration> opCompanyConfigurationPdfStatus = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SALES_PDF_DOWNLOAD);
		String flag1 = "Normal";
		LocalDateTime endLCTime1 = LocalDateTime.now();
		String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
		String endDate1 = startLCTime1.format(DATE_FORMAT1);
		Duration duration1 = Duration.between(startLCTime1, endLCTime1);
		long minutes1 = duration1.toMinutes();
		if (minutes1 <= 1 && minutes1 >= 0) {
			flag1 = "Fast";
		}
		if (minutes1 > 1 && minutes1 <= 2) {
			flag1 = "Normal";
		}
		if (minutes1 > 2 && minutes1 <= 10) {
			flag1 = "Slow";
		}
		if (minutes1 > 10) {
			flag1 = "Dead Slow";
		}
                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
				+ description1);
		if (opCompanyConfigurationPdfStatus.isPresent()) {

			if (opCompanyConfigurationPdfStatus.get().getValue().equals("true")) {
				pdfDownloadButtonStatus = true;
			} else {
				pdfDownloadButtonStatus = false;
			}
		}

		boolean sendSalesOrderEmailStatusColumn = false;
		Optional<CompanyConfiguration> opsendSalesOrderEmailStatus = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.SEND_SALES_ORDER_EMAIL);
		if (opsendSalesOrderEmailStatus.isPresent()) {

			if (opsendSalesOrderEmailStatus.get().getValue().equals("true")) {
				sendSalesOrderEmailStatusColumn = true;
			} else {
				sendSalesOrderEmailStatusColumn = false;
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
			if(compConfig) {
			salesPerformanceDTO.setReceiverAccountName(ivData[27].toString());
			}
			else
			{
				salesPerformanceDTO.setReceiverAccountName(ivData[8].toString());
			}
			salesPerformanceDTO.setSupplierAccountPid(ivData[9].toString());
			salesPerformanceDTO.setSupplierAccountName(ivData[10].toString());
			salesPerformanceDTO.setEmployeePid(ivData[11].toString());
			salesPerformanceDTO.setEmployeeName(ivData[12].toString());
			salesPerformanceDTO.setUserName(ivData[13].toString());
			salesPerformanceDTO.setDocumentTotal((double) ivData[14]);
			salesPerformanceDTO.setDocumentVolume((double) ivData[15]);
			salesPerformanceDTO.setTotalVolume(ivTotalVolume.get(salesPerformanceDTO.getPid()));
			salesPerformanceDTO.setPdfDownloadButtonStatus(pdfDownloadButtonStatus);
			salesPerformanceDTO.setSendSalesOrderEmailStatusColumn(sendSalesOrderEmailStatusColumn);

			salesPerformanceDTO.setTallyDownloadStatus(TallyDownloadStatus.valueOf(ivData[16].toString()));
			salesPerformanceDTO.setVisitRemarks(ivData[17] == null ? null : ivData[17].toString());

			salesPerformanceDTO.setOrderNumber(ivData[18] == null ? 0 : Long.parseLong(ivData[18].toString()));

			salesPerformanceDTO.setPdfDownloadStatus(Boolean.valueOf(ivData[19].toString()));

			salesPerformanceDTO.setSendSalesOrderEmailStatus(SendSalesOrderEmailStatus.valueOf(ivData[24].toString()));

			salesPerformanceDTO.setReceiverAccountLocation(ivData[26] != null ? ivData[26].toString() : "");
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
//		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = inventoryVoucherService
//				.findAllByCompanyIdAndInventoryPidIn(Arrays.asList(inventoryVoucherHeaderPids));
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = inventoryVoucherService.findAllByCompanyIdAndInventoryPidInXLSDounlods(Arrays.asList(inventoryVoucherHeaderPids));
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
		String[] headerColumns = { "Order No", "Salesman", "Order Date", "Customer", "Customer Location", "Supplier",
				"Product Name","Price", "Quantity", "Discount Percentage", "Tax Amount", "Row Total" };
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
				row.createCell(4).setCellValue(ivh.getCustomeraddress());
				row.createCell(5).setCellValue(ivh.getSupplierAccountName());

				row.createCell(6).setCellValue(ivd.getProductName());
				row.createCell(7).setCellValue(ivd.getSellingRate());
				row.createCell(8).setCellValue(ivd.getQuantity());
				row.createCell(9).setCellValue(ivd.getDiscountPercentage());
				row.createCell(10).setCellValue(ivd.getTaxAmount());
				row.createCell(11).setCellValue(ivd.getRowTotal());
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

	@RequestMapping(value = "/primary-sales-performance-download-status/changeStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> changeStatus(@RequestParam String pid,
			@RequestParam TallyDownloadStatus tallyDownloadStatus) {
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(pid).get();
		inventoryVoucherHeaderDTO.setTallyDownloadStatus(tallyDownloadStatus);
		inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	@RequestMapping(value = "/primary-sales-performance-download-status/changeSendSalesOrderEmailStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> changeSendSalesOrderEmailStatus(@RequestParam String pid,
			@RequestParam SendSalesOrderEmailStatus sendSalesOrderEmailStatus) {
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(pid).get();
		inventoryVoucherHeaderDTO.setSendSalesOrderEmailStatus(sendSalesOrderEmailStatus);
		inventoryVoucherService.updateInventoryVoucherHeaderStatus(inventoryVoucherHeaderDTO);
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	@RequestMapping(value = "/primary-sales-performance-download-status/sendSalesOrderEmail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> sendSalesOrderEmail() throws MessagingException {

		log.info("sendSalesOrderEmail()-----");
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_185" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="getting secondary sales for excel";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> inventoryVoucherHeaderObjects = inventoryVoucherHeaderRepository
				.getSecondarySalesForExcel(SecurityUtils.getCurrentUsersCompanyId());
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		List<SecondarySalesOrderExcelDTO> secondarySalesOrderExcelDTOs = convertInventoryObjectToExcelDTO(
				inventoryVoucherHeaderObjects);

		Set<String> supplierNames = new HashSet<>();

		supplierNames.addAll(secondarySalesOrderExcelDTOs.stream().map(SecondarySalesOrderExcelDTO::getSupplierName)
				.collect(Collectors.toList()));
		 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "AP_QUERY_114" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get all by compId and accTypeName";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findAllByCompanyIdAndAccountTypeName(SecurityUtils.getCurrentUsersCompanyId(), "Company");
		 String flag1 = "Normal";
			LocalDateTime endLCTime1 = LocalDateTime.now();
			String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
			String endDate1 = startLCTime1.format(DATE_FORMAT1);
			Duration duration1 = Duration.between(startLCTime1, endLCTime1);
			long minutes1 = duration1.toMinutes();
			if (minutes1 <= 1 && minutes1 >= 0) {
				flag1 = "Fast";
			}
			if (minutes1 > 1 && minutes1 <= 2) {
				flag1 = "Normal";
			}
			if (minutes1 > 2 && minutes1 <= 10) {
				flag1 = "Slow";
			}
			if (minutes1 > 10) {
				flag1 = "Dead Slow";
			}
	                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description1);

		String companyEmail = "";
		if (accountProfiles != null && accountProfiles.size() > 0) {
			companyEmail = accountProfiles.get(0).getEmail1();
		}

		for (String supplierName : supplierNames) {

			List<SecondarySalesOrderExcelDTO> secondarySalesOrderExcelBySupplierDTOs = new ArrayList<>();

			secondarySalesOrderExcelBySupplierDTOs = secondarySalesOrderExcelDTOs.stream()
					.filter(sso -> sso.getSupplierName().equalsIgnoreCase(supplierName)).collect(Collectors.toList());

			String supplierEmail = secondarySalesOrderExcelBySupplierDTOs.stream().findFirst().get().getSupplierEmail();

			if (supplierEmail != null && !supplierEmail.equalsIgnoreCase("")) {
				sendSalesOrderEmail(supplierName, supplierEmail, companyEmail, secondarySalesOrderExcelBySupplierDTOs);
			}
		}

		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	@RequestMapping(value = "/primary-sales-performance-download-status/sendSalesOrderOdoo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> sendSalesOrderOdoo() throws MessagingException {

		log.info("sendSalesOrderOdoo()-----");

		sendInvoiceOdooService.sendSalesOrder();

		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	@RequestMapping(value = "/primary-sales-performance-download-status/sendSalesReturnOdoo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> sendSalesReturnOdoo() throws MessagingException {

		log.info("sendSalesReturnOdoo()-----");

		sendInvoiceOdooService.sendSalesReturnOdoo();

		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	private void sendSalesOrderEmail(String supplierName, String supplierEmail, String companyEmail,
			List<SecondarySalesOrderExcelDTO> secondarySalesOrderExcelBySupplierDTOs) throws MessagingException {

		log.debug("Sending a mail to Supplier-- " + supplierEmail);

		long start = System.nanoTime();

		String companyName = CompanyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()).getLegalName();

		JavaMailSender emailSender = getJavaMailSender();

		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setSubject(companyName + " - Sales Order");
		helper.setText("Sales Order Taken Under - " + supplierName);
		helper.setTo(supplierEmail);

		if (companyEmail != null && !companyEmail.equalsIgnoreCase("")) {
			log.debug("Sending a mail to Company Account-- " + companyEmail);
			helper.setCc(companyEmail);
		} else {
			log.info("No Email found for Company Account-- ");
		}
		helper.setFrom(supplierEmail);

		File excelFile = generateFile(supplierName, secondarySalesOrderExcelBySupplierDTOs);
		FileSystemResource file = new FileSystemResource(excelFile);
		helper.addAttachment(file.getFilename(), file);
		emailSender.send(message);

		excelFile.delete();

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;

		List<String> succesOrders = (ArrayList<String>) secondarySalesOrderExcelBySupplierDTOs.stream()
				.map(so -> so.getInventoryPid()).collect(Collectors.toList());
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_195" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="Updating iv header send sales order email status using pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderSendSalesOrderEmailStatusUsingPidAndCompanyId(
				SendSalesOrderEmailStatus.SENT, SecurityUtils.getCurrentUsersCompanyId(), succesOrders);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		log.info("Sync completed in {} ms", elapsedTime);

	}

	private File generateFile(String supplierName,
			List<SecondarySalesOrderExcelDTO> secondarySalesOrderExcelBySupplierDTOs) {
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
			createSecondaryReportRows(worksheet, secondarySalesOrderExcelBySupplierDTOs);
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

	private void createSecondaryReportRows(HSSFSheet worksheet,
			List<SecondarySalesOrderExcelDTO> secondarySalesOrderExcelBySupplierDTOs) {
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
		for (SecondarySalesOrderExcelDTO salesOrder : secondarySalesOrderExcelBySupplierDTOs) {
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
			List<Object[]> inventoryVoucherHeaderObjects) {

		List<SecondarySalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();
		List<String> inventoryHeaderPid = new ArrayList<String>();

		for (Object[] obj : inventoryVoucherHeaderObjects) {
			SecondarySalesOrderExcelDTO salesOrderDTO = new SecondarySalesOrderExcelDTO();

			String pattern = "dd-MMM-yyyy hh:mm:ss a";
			DateFormat df = new SimpleDateFormat(pattern);
			String dateAsString = df.format(obj[1]);

			salesOrderDTO.setDate(dateAsString != null ? dateAsString : "");
			salesOrderDTO.setReceiverName(obj[2] != null ? obj[2].toString() : "");
			salesOrderDTO.setSupplierName(obj[3] != null ? obj[3].toString() : "");
			salesOrderDTO.setSupplierEmail(obj[4] != null ? obj[4].toString() : "");
			salesOrderDTO.setItemName(obj[5] != null ? obj[5].toString() : "");
			salesOrderDTO.setQuantity(obj[6] != null ? Double.parseDouble(obj[6].toString()) : 0.0);
			salesOrderDTO.setRate(obj[7] != null ? Double.parseDouble(obj[7].toString()) : 0.0);
			salesOrderDTO.setDiscPer(obj[8] != null ? Double.parseDouble(obj[8].toString()) : 0.0);
			salesOrderDTO.setTaxPer(obj[9] != null ? Double.parseDouble(obj[9].toString()) : 0.0);

			double qty = Double.parseDouble(obj[6].toString());
			double rate = Double.parseDouble(obj[7].toString());
			double dis = Double.parseDouble(obj[8].toString());
			double taxPer = Double.parseDouble(obj[9].toString());

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
			salesOrderDTO.setInventoryPid(obj[11] != null ? obj[11].toString() : "");
			salesOrderDTO.setEmployeeName(obj[12] != null ? obj[12].toString() : "");

			salesOrderDTO.setBillNo(obj[0].toString());
			inventoryHeaderPid.add(obj[9] != null ? obj[9].toString() : "");

			salesOrderDTO.setRefDocNo(obj[13] != null ? obj[13].toString() : "0");

			double freeQuantity = Double.parseDouble(obj[14].toString());

			salesOrderDTO.setFreeQuantity(freeQuantity);

			salesOrderDTOs.add(salesOrderDTO);

		}

		return salesOrderDTOs;
	}

	@RequestMapping(value = "/primary-sales-performance-download-status/downloadPdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

		if (inventoryVoucherHeaderDtos.size() > 0) {

			for (InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO : inventoryVoucherHeaderDtos)
				if (!inventoryVoucherHeaderDTO.getPdfDownloadStatus()) {
					 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String id = "INV_QUERY_187" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String description ="Updating pdf download status by pid";
						LocalDateTime startLCTime = LocalDateTime.now();
						String startTime = startLCTime.format(DATE_TIME_FORMAT);
						String startDate = startLCTime.format(DATE_FORMAT);
						logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					inventoryVoucherHeaderRepository.updatePdfDownlodStatusByPid(inventoryVoucherHeaderDTO.getPid());
					 String flag = "Normal";
						LocalDateTime endLCTime = LocalDateTime.now();
						String endTime = endLCTime.format(DATE_TIME_FORMAT);
						String endDate = startLCTime.format(DATE_FORMAT);
						Duration duration = Duration.between(startLCTime, endLCTime);
						long minutes = duration.toMinutes();
						if (minutes <= 1 && minutes >= 0) {
							flag = "Fast";
						}
						if (minutes > 1 && minutes <= 2) {
							flag = "Normal";
						}
						if (minutes > 2 && minutes <= 10) {
							flag = "Slow";
						}
						if (minutes > 10) {
							flag = "Dead Slow";
						}
				                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
								+ description);
				}
		}

	}

	private void buildPdf(List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/pdf");

		if (inventoryVoucherHeaderDTOs.size() > 1) {

			response.setHeader("Content-Disposition", "inline;filename=\"" + "Packing Slip_"
					+ inventoryVoucherHeaderDTOs.get(0).getReceiverAccountName() + ".pdf\"");

		} else {
			response.setHeader("Content-Disposition", "inline;filename=\"" + "Packing Slip_"
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

	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);

		mailSender.setUsername("salesnrich.info@gmail.com");
		mailSender.setPassword("password@salesnrich");

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "false");

		return mailSender;
	}

}
