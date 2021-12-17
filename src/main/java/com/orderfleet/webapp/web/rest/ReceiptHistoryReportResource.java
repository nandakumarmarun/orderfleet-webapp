package com.orderfleet.webapp.web.rest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.SalesTargetBlock;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetReportSetting;
import com.orderfleet.webapp.domain.SalesTargetReportSettingSalesTargetBlock;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.SalesTargetBlockSalesTargetGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetReportSettingRepository;
import com.orderfleet.webapp.repository.SalesTargetReportSettingSalesTargetBlockRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetBlockDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

/**
 * Web controller for managing SaleReceiptReport.
 *
 * @author Sarath
 * @since Mar 31, 2018
 *
 */
@Controller
@RequestMapping("/web")
public class ReceiptHistoryReportResource {

	private final Logger log = LoggerFactory.getLogger(ReceiptHistoryReportResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private SalesTargetReportSettingRepository salesTargetReportSettingRepository;

	@Inject
	private SalesTargetReportSettingSalesTargetBlockRepository salesTargetReportSettingSalesTargetBlockRepository;

	@Inject
	private SalesTargetBlockSalesTargetGroupRepository salesTargetBlockSalesTargetGroupRepository;

	@Inject
	private SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	/**
	 * GET /other-tasks : get other-tasks page.
	 */
	@RequestMapping(value = "/receipt-history-report", method = RequestMethod.GET)
	public String getReceiptHistoryReport(Model model) {
		log.debug("web request to get a page of receipt history report");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/receipt-history-report";
	}

	@RequestMapping(value = "/receipt-history-report/getHeaders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> getSalesTargetBlocks() {
		// find Purchase History Configs
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<String> headings = new ArrayList<>();

		List<SalesTargetReportSetting> salesTargetReportSettings = salesTargetReportSettingRepository
				.findAllByCompanyIdAndTargetSettingType(BestPerformanceType.RECEIPT);

		if (!salesTargetReportSettings.isEmpty()) {

			List<SalesTargetReportSettingSalesTargetBlock> purchaseHistoryConfigs = salesTargetReportSettingSalesTargetBlockRepository
					.findBySalesTargetReportSettingPidOrderBySortOrder(salesTargetReportSettings.get(0).getPid());

			List<SalesTargetBlock> blocks = purchaseHistoryConfigs.stream()
					.map(saresetb -> saresetb.getSalesTargetBlock()).collect(Collectors.toList());
//			Collections.sort(blocks, (p1, p2) -> p1.get.compareTo(p2.firstName));

			headings = findMonthNamesFromSalesTargetBlocks(blocks, company);

			if (purchaseHistoryConfigs.size() == 0) {
				log.info("Please Configure Purchase History Settings....");
				return new ResponseEntity<>(headings, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(headings, HttpStatus.OK);
	}

	@RequestMapping(value = "/receipt-history-report/accountProfile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<AccountProfileDTO> getAccountProfilesByEmployeePid(@RequestParam("employeePid") String userPid) {
		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByUserPidIn(Arrays.asList(userPid));
//		List<Object[]> accountPidNames = locationAccountProfileRepository
//		.findAccountProfilesByLocationIdIn(locationIds);
//int size = accountPidNames.size();
//List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>(size);
//for (int i = 0; i < size; i++) {
//	AccountProfileDTO accountProfileDTO = new AccountProfileDTO();
//	accountProfileDTO.setPid(accountPidNames.get(i)[0].toString());
//	accountProfileDTO.setName(accountPidNames.get(i)[1].toString());
//	accountProfileDTOs.add(accountProfileDTO);
//}

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
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyIdAndIdsIn(accountProfileIds);
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

		List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper.accountProfilesToAccountProfileDTOs(result);
		return accountProfileDTOs;
	}

	/**
	 * GET /accountProfiles/load :load accountProfiles.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         accountProfileDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/receipt-history-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, SalesTargetReportSettingDTO>> loadAccountProfiles(@RequestParam String userPid,
			@RequestParam String accountPid) {
		log.debug("Web request to load AccountProfiles ");
		Map<String, SalesTargetReportSettingDTO> result = new HashMap<>();
		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByUserPidIn(Arrays.asList(userPid));
		final List<AccountProfile> accountProfiles = new ArrayList<>();
		if ("-1".equals(accountPid)) {
			accountProfiles.addAll(
					locationAccountProfileRepository.findAccountProfileByLocationIdIn(new ArrayList<>(locationIds)));
		} else {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountProfileRepository.findOneByPid(accountPid).ifPresent(ap -> accountProfiles.add(ap));
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

		List<SalesTargetReportSetting> salesTargetReportSettings = salesTargetReportSettingRepository
				.findAllByCompanyIdAndTargetSettingType(BestPerformanceType.RECEIPT);
		if (!salesTargetReportSettings.isEmpty() && !accountProfiles.isEmpty()) {
			accountProfiles.forEach(accnt -> {
				if (salesTargetReportSettings != null && !salesTargetReportSettings.isEmpty()) {
					List<SalesTargetBlockDTO> salesTargetBlockDTOs = new ArrayList<>();
					// for an mobile UI and setting only one data exist
					SalesTargetReportSetting salesTargetReportSetting = salesTargetReportSettings.get(0);

					// find salestargetblocks
					salesTargetReportSettingSalesTargetBlockRepository
							.findBySalesTargetReportSettingPidOrderBySortOrder(salesTargetReportSetting.getPid())
							.forEach(strsStb -> {
								SalesTargetBlockDTO slBlockDTO = setAccountWiseTargetAndAchieved(
										new SalesTargetBlockDTO(strsStb.getSalesTargetBlock(), strsStb.getSortOrder()),
										strsStb.getCompany(), accnt.getPid(), salesTargetReportSetting);
								salesTargetBlockDTOs.add(slBlockDTO);
							});
					SalesTargetReportSettingDTO salesTargetReportSettingDTO = new SalesTargetReportSettingDTO(
							salesTargetReportSetting);
					salesTargetReportSettingDTO.setSalesTargetBlocks(salesTargetBlockDTOs);

					result.put(accnt.getName(), salesTargetReportSettingDTO);
				}
			});
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	private SalesTargetBlockDTO setAccountWiseTargetAndAchieved(SalesTargetBlockDTO salesTargetBlockDTO,
			Company company, String accountPid, SalesTargetReportSetting salesTargetReportSetting) {

		if (salesTargetBlockDTO != null) {

			LocalDate startDate = findStartDate(salesTargetBlockDTO, company);
			LocalDate endDate = findEndDate(salesTargetBlockDTO, company);
			// find sales target group
			List<SalesTargetGroup> salesTargetGroups = salesTargetBlockSalesTargetGroupRepository
					.findSalesTargetGroupsBySalesTargetBlockPid(salesTargetBlockDTO.getPid());
			List<String> salesTargetGroupPids = salesTargetGroups.stream().map(SalesTargetGroup::getPid)
					.collect(Collectors.toList());
			// get sales Target Group documents
			List<Document> documents = salesTargetGroupDocumentRepository
					.findDocumentsBySalesTargetGroupPid(salesTargetGroupPids);
			if (salesTargetReportSetting.getMonthlyAverageWise()) {
				// get months
				List<LocalDate> monthRanges = getMonthsBetweenDates(startDate, endDate);
				double targetAmt = 0;
				double targetVlm = 0;
				double achievedAmt = 0;
				double achievedVlm = 0;
				for (LocalDate localDate : monthRanges) {
					SalesTargetBlockDTO tempAchieved = setSumOfAccountWiseAchievedBetweenDate(salesTargetBlockDTO,
							accountPid, documents, localDate, localDate);
					achievedAmt += tempAchieved.getAchievedAmount();
					achievedVlm += tempAchieved.getAchievedVolume();
				}
				int months = monthRanges.size();
				double targetVlmAvg = targetVlm / months;
				double targetAmtAvg = targetAmt / months;
				double achievedAmtAvg = achievedAmt / months;
				double achievedVlmAvg = achievedVlm / months;
				salesTargetBlockDTO.setTargetVolume(
						BigDecimal.valueOf(targetVlmAvg).setScale(2, RoundingMode.HALF_UP).doubleValue());
				salesTargetBlockDTO.setTargetAmount(
						BigDecimal.valueOf(targetAmtAvg).setScale(2, RoundingMode.HALF_UP).doubleValue());
				salesTargetBlockDTO.setAchievedAmount(
						BigDecimal.valueOf(achievedAmtAvg).setScale(2, RoundingMode.HALF_UP).doubleValue());
				salesTargetBlockDTO.setAchievedVolume(
						BigDecimal.valueOf(achievedVlmAvg).setScale(2, RoundingMode.HALF_UP).doubleValue());
			} else {
				// find and set target achieved
				setSumOfAccountWiseAchievedBetweenDate(salesTargetBlockDTO, accountPid, documents, startDate, endDate);
			}
		}
		return salesTargetBlockDTO;
	}

	private LocalDate findStartDate(SalesTargetBlockDTO salesTargetBlockDTO, Company company) {
		final Month month;
		if (salesTargetBlockDTO.getStartMonth() == 0) {
			month = LocalDate.now().minusMonths(salesTargetBlockDTO.getStartMonthMinus()).getMonth();
		} else {
			month = Month.of(salesTargetBlockDTO.getStartMonth());
		}
		List<LocalDate> monthRanges = getMonthsBetweenDates(
				company.getPeriodStartDate().minusYears(salesTargetBlockDTO.getStartMonthYearMinus()),
				company.getPeriodEndDate().minusYears(salesTargetBlockDTO.getEndMonthYearMinus()));
		LocalDate selectedDate = monthRanges.stream().filter(ld -> ld.getMonth().equals(month)).findAny()
				.orElse(LocalDate.now());
		return LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), 1);
	}

	private LocalDate findEndDate(SalesTargetBlockDTO salesTargetBlockDTO, Company company) {
		final Month month;
		if (salesTargetBlockDTO.getEndMonth() == 0) {
			month = LocalDate.now().minusMonths(salesTargetBlockDTO.getEndMonthMinus()).getMonth();
		} else {
			month = Month.of(salesTargetBlockDTO.getEndMonth());
		}
		List<LocalDate> monthRanges = getMonthsBetweenDates(
				company.getPeriodStartDate().minusYears(salesTargetBlockDTO.getStartMonthYearMinus()),
				company.getPeriodEndDate().minusYears(salesTargetBlockDTO.getEndMonthYearMinus()));
		LocalDate selectedDate = monthRanges.stream().filter(ld -> ld.getMonth().equals(month)).findAny()
				.orElse(LocalDate.now());
		LocalDate initial = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), 1);
		return initial.withDayOfMonth(initial.lengthOfMonth());
	}

	private List<LocalDate> getMonthsBetweenDates(LocalDate periodStartDate, LocalDate periodEndDate) {
		List<LocalDate> monthRanges = new ArrayList<>();
		while (!periodStartDate.isAfter(periodEndDate)) {
			monthRanges.add(periodStartDate);
			periodStartDate = periodStartDate.plusMonths(1);
		}
		return monthRanges;
	}

	private SalesTargetBlockDTO setSumOfAccountWiseAchievedBetweenDate(SalesTargetBlockDTO salesTargetBlockDTO,
			String accountPid, List<Document> documents, LocalDate startDate, LocalDate endDate) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACC_QUERY_139" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get Count and sum of account voucher by documents,date between and accountProfile";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate1 = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate1 + "," + startTime + ",_ ,0 ,START,_," + description);
		Object sumOfAccountWiseAchieved = accountingVoucherHeaderRepository
				.getCountAndAmountByDocumentsAndDateBetweenAndAccountProfile(documents, startDate.atTime(0, 0),
						endDate.atTime(23, 59), accountPid);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate1 = startLCTime.format(DATE_FORMAT);
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
	                logger.info(id + "," + endDate1 + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		Object[] achievedAmountVolume = (Object[]) sumOfAccountWiseAchieved;
		if (achievedAmountVolume[1] != null) {
			double roundedAchievedVlm = new BigDecimal(achievedAmountVolume[1].toString())
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			salesTargetBlockDTO.setAchievedAmount(roundedAchievedVlm);
		}
		return salesTargetBlockDTO;
	}

	// ..............................................................................
	private List<String> findMonthNamesFromSalesTargetBlocks(List<SalesTargetBlock> purchaseHistoryConfigs,
			Company company) {

		List<String> dateNames = new ArrayList<>();
		for (SalesTargetBlock purchaseHistoryConfig : purchaseHistoryConfigs) {

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

	private LocalDate findStartDate(SalesTargetBlock purchaseHistoryConfig, Company company) {
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

	private LocalDate findEndDate(SalesTargetBlock purchaseHistoryConfig, Company company) {
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

}
