package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.PurchaseHistoryConfig;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.PurchaseHistoryConfigRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.PurchaseHistoryConfigDate;
import com.orderfleet.webapp.web.rest.dto.SalesValueReportDTO;

/**
 * Web controller for managing SalesValueReport.
 *
 * @author Sarath
 * @since Jun 8, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class SalesValueReportResource {

	private final Logger log = LoggerFactory.getLogger(SalesValueReportResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private PurchaseHistoryConfigRepository purchaseHistoryConfigRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@RequestMapping(value = "/sales-value-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllSalesValueReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of sales value report");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if(userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/salesValueReport";
	}

	@RequestMapping(value = "/sales-value-report/getHeaders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> getPurchaseHistoryConfigs() {
		// find Purchase History Configs
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		List<PurchaseHistoryConfig> purchaseHistoryConfigs = purchaseHistoryConfigRepository
				.findAllByCompanyIdOrderBySortOrderAsc();
		List<String> headings = findMonthNamesFromPurchaseHistoryConfigs(purchaseHistoryConfigs, company);
		if (purchaseHistoryConfigs.size() == 0) {
			log.info("Please Configure Purchase History Settings....");
			return new ResponseEntity<>(headings, HttpStatus.OK);
		}
		return new ResponseEntity<>(headings, HttpStatus.OK);
	}

	@RequestMapping(value = "/sales-value-report/getUserUnderValues/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Map<String, List<SalesValueReportDTO>>> getSalesValueReport(@PathVariable String userPid) {
		Map<String, List<SalesValueReportDTO>> mapSalesValueReportDTOs = new HashMap<>();
		List<String> locationPids = employeeProfileLocationRepository.findLocationsByUserPid(userPid).stream()
				.map(a -> a.getPid()).collect(Collectors.toList());
		if (locationPids.size() > 0) {
			Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
			if (company.getPeriodStartDate() == null || company.getPeriodEndDate() == null) {
				log.info("Company financial period not configure....");
				return new ResponseEntity<>(mapSalesValueReportDTOs, HttpStatus.OK);
			}

			// find Purchase History Configs
			List<PurchaseHistoryConfig> purchaseHistoryConfigs = purchaseHistoryConfigRepository
					.findAllByCompanyIdOrderBySortOrderAsc();
			if (purchaseHistoryConfigs.size() == 0) {
				log.info("Please Configure Purchase History Settings....");
				return new ResponseEntity<>(mapSalesValueReportDTOs, HttpStatus.OK);
			}

			// find performance document
			List<Document> documents = null;
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
	         DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "COMP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get by compId and name";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			Optional<CompanyConfiguration> performanceConfig = companyConfigurationRepository.findByCompanyIdAndName(
					SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.COMPANY_PERFORMANCE_BASED_ON);
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
			if (performanceConfig.isPresent()) {
				documents = primarySecondaryDocumentRepository.findDocumentsByVoucherTypeAndCompanyId(
						VoucherType.valueOf(performanceConfig.get().getValue()));
			} else {
				documents = primarySecondaryDocumentRepository
						.findDocumentsByVoucherTypeAndCompanyId(VoucherType.PRIMARY_SALES_ORDER);
			}
			if (documents.size() == 0) {
				log.info("Performance documents not configure....");
				return new ResponseEntity<>(mapSalesValueReportDTOs, HttpStatus.OK);
			}

			// find periods from Purchase History Config
			List<PurchaseHistoryConfigDate> dates = findDatesFromPurchaseHistoryConfigs(purchaseHistoryConfigs,
					company);

			List<AccountProfileDTO> accountProfileDTOs = locationAccountProfileService
					.findAccountProfileByLocationPidIn(locationPids);
 
			for (AccountProfileDTO accountProfileDTO : accountProfileDTOs) {
				Map<String, List<SalesValueReportDTO>> mapSalesValueReports = purchaseHistory(accountProfileDTO,
						company, documents, dates);
				mapSalesValueReportDTOs.putAll(mapSalesValueReports);
			}
		}
		return new ResponseEntity<>(mapSalesValueReportDTOs, HttpStatus.OK);
	}

	private Map<String, List<SalesValueReportDTO>> purchaseHistory(AccountProfileDTO accountProfileDTO, Company company,
			List<Document> documents, List<PurchaseHistoryConfigDate> dates) {

		Map<String, List<SalesValueReportDTO>> mapSalesValueReportDTOs = new HashMap<>();

		List<SalesValueReportDTO> salesValueReportDTOs = new ArrayList<>();
		for (PurchaseHistoryConfigDate purchaseHistoryConfigDate : dates) {
			// get inventory Voucher Details
			   DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_135" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get count amount and volume by doc date between and accountpid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Object object = inventoryVoucherHeaderRepository.getAmountAndVolumeByAccountPidAndDocumentDateBetween(
					accountProfileDTO.getPid(), purchaseHistoryConfigDate.getStartDate(),
					purchaseHistoryConfigDate.getEndDate(), documents);
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
			Object[] objValue = (Object[]) object;

			double documentTotal = 0;
			if (objValue[0] != null) {
				documentTotal = Math.round(Double.valueOf(objValue[0].toString()));
			}
			SalesValueReportDTO salesValueReportDTO = new SalesValueReportDTO(purchaseHistoryConfigDate.getName(),
					purchaseHistoryConfigDate.getSortOrder(), purchaseHistoryConfigDate.getLabel(),
					accountProfileDTO.getName(), accountProfileDTO.getPid(), documentTotal);
			salesValueReportDTOs.add(salesValueReportDTO);
		}

		mapSalesValueReportDTOs.put(accountProfileDTO.getName(), salesValueReportDTOs);

		return mapSalesValueReportDTOs;
	}

	private List<PurchaseHistoryConfigDate> findDatesFromPurchaseHistoryConfigs(
			List<PurchaseHistoryConfig> purchaseHistoryConfigs, Company company) {
		List<PurchaseHistoryConfigDate> dates = new ArrayList<>();
		for (PurchaseHistoryConfig purchaseHistoryConfig : purchaseHistoryConfigs) {

			LocalDate startDate = findStartDate(purchaseHistoryConfig, company);
			LocalDate endDate = findEndDate(purchaseHistoryConfig, company);
			String result = "";
			if (purchaseHistoryConfig.getCreateDynamicLabel()) {
				String startMonth = startDate.getMonth().name().substring(0, 3);
				String startYear = String.valueOf(startDate.getYear());

				String endMonth = endDate.getMonth().name().substring(0, 3);
				String endYear = String.valueOf(endDate.getYear());

				result += startMonth + " " + startYear.substring(startYear.length() - 2);
				if (!startMonth.equals(endMonth) || !startYear.equals(endYear)) {
					result += " - " + endMonth + " " + endYear.substring(endYear.length() - 2);
				}
			} else {
				result += purchaseHistoryConfig.getName();
			}
			dates.add(new PurchaseHistoryConfigDate(purchaseHistoryConfig.getId(), result, startDate.atTime(0, 0),
					endDate.atTime(23, 59), purchaseHistoryConfig.getName(), purchaseHistoryConfig.getSortOrder()));
		}
		// dates.sort((d1, d2) ->
		// d1.getStartDate().compareTo(d2.getStartDate()));
		return dates;

	}

	private List<String> findMonthNamesFromPurchaseHistoryConfigs(List<PurchaseHistoryConfig> purchaseHistoryConfigs,
			Company company) {

		List<String> dateNames = new ArrayList<>();
		for (PurchaseHistoryConfig purchaseHistoryConfig : purchaseHistoryConfigs) {

			LocalDate startDate = findStartDate(purchaseHistoryConfig, company);
			LocalDate endDate = findEndDate(purchaseHistoryConfig, company);
			String result = "";
			if (purchaseHistoryConfig.getCreateDynamicLabel()) {
				String startMonth = startDate.getMonth().name().substring(0, 3);
				String startYear = String.valueOf(startDate.getYear());

				String endMonth = endDate.getMonth().name().substring(0, 3);
				String endYear = String.valueOf(endDate.getYear());

				result += startMonth + " " + startYear;
				if (!startMonth.equals(endMonth) || !startYear.equals(endYear)) {
					result += " - " + endMonth + " " + endYear;
				}
			} else {
				result += purchaseHistoryConfig.getName();
			}
			dateNames.add(purchaseHistoryConfig.getName() + " ( " + result.toLowerCase() + " )");
		}
		// dates.sort((d1, d2) ->
		// d1.getStartDate().compareTo(d2.getStartDate()));
		return dateNames;
	}

	private LocalDate findStartDate(PurchaseHistoryConfig purchaseHistoryConfig, Company company) {
		final Month month;
		if (purchaseHistoryConfig.getStartMonth() == 0) {
			month = LocalDate.now().minusMonths(purchaseHistoryConfig.getStartMonthMinus()).getMonth();
		} else {
			month = Month.of(purchaseHistoryConfig.getStartMonth());
		}
		List<LocalDate> monthRanges = getFinancialYearMonths(
				company.getPeriodStartDate().minusYears(purchaseHistoryConfig.getStartMonthYearMinus()),
				company.getPeriodEndDate().minusYears(purchaseHistoryConfig.getStartMonthYearMinus()));
		LocalDate selectedDate = monthRanges.stream().filter(ld -> ld.getMonth().equals(month)).findAny()
				.orElse(LocalDate.now());
		return LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), 1);
	}

	private LocalDate findEndDate(PurchaseHistoryConfig purchaseHistoryConfig, Company company) {
		final Month month;
		if (purchaseHistoryConfig.getEndMonth() == 0) {
			month = LocalDate.now().minusMonths(purchaseHistoryConfig.getEndMonthMinus()).getMonth();
		} else {
			month = Month.of(purchaseHistoryConfig.getEndMonth());
		}
		List<LocalDate> monthRanges = getFinancialYearMonths(
				company.getPeriodStartDate().minusYears(purchaseHistoryConfig.getStartMonthYearMinus()),
				company.getPeriodEndDate().minusYears(purchaseHistoryConfig.getEndMonthYearMinus()));
		LocalDate selectedDate = monthRanges.stream().filter(ld -> ld.getMonth().equals(month)).findAny()
				.orElse(LocalDate.now());
		LocalDate initial = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), 1);
		return initial.withDayOfMonth(initial.lengthOfMonth());
	}

	private List<LocalDate> getFinancialYearMonths(LocalDate periodStartDate, LocalDate periodEndDate) {
		List<LocalDate> monthRanges = new ArrayList<>();
		while (!periodStartDate.isAfter(periodEndDate)) {
			monthRanges.add(periodStartDate);
			periodStartDate = periodStartDate.plusMonths(1);
		}
		return monthRanges;
	}

	class Value {

		private Long id;

		private String label;

		private Double value;

		public Value(Long id, String label, Double value) {
			super();
			this.id = id;
			this.label = label;
			this.value = value;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Double getValue() {
			return value;
		}

		public void setValue(Double value) {
			this.value = value;
		}

	}
}
