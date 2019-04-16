package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.CompanySetting;
import com.orderfleet.webapp.domain.SalesSummaryAchievment;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanySettingRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupSalesTargetGrouprepository;
import com.orderfleet.webapp.repository.SalesSummaryAchievmentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupSalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.SalesPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupUserTargetMapper;

/**
 * Web controller for Sales Target v/s achieved report
 * 
 * @author Muhammed Riyas T
 * @since October 06, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesTargetAchievedReportResource {

	private final Logger log = LoggerFactory.getLogger(SalesTargetAchievedReportResource.class);

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private SalesTargetGroupUserTargetMapper salesTargetGroupUserTargetMapper;

	@Inject
	private SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;

	@Inject
	private SalesTargetGroupProductRepository salesTargetGroupProductRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private SalesSummaryAchievmentRepository salesSummaryAchievmentRepository;

	@Inject
	private CompanySettingRepository companySettingRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private EmployeeProfileRepository employeeRepository;

	@Inject
	private ProductGroupSalesTargetGroupService productGroupSalesTargetGroupService;

	@Inject
	private ProductGroupSalesTargetGrouprepository productGroupSalesTargetGrouprepository;

	/**
	 * GET /sales-target-vs-achieved-report
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/sales-target-vs-achieved-report", method = RequestMethod.GET)
	public String getSalesTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Marketing Activity Performance");
		model.addAttribute("products", productGroupSalesTargetGroupService.findAllProductGroupByCompany());
		return "company/salesTargetAchievedReport";
	}

	@RequestMapping(value = "/sales-target-vs-achieved-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public SalesPerformaceDTO performanceTargets(@RequestParam("employeePid") String employeePid,
			@RequestParam("productGroupPids") List<String> productGroupPids,
			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		log.info("Rest Request to load sales target vs achieved report : {} , and date between {}, {}", employeePid,
				fromDate, toDate);
		if (employeePid.equals("no")) {
			return null;
		}
		String userPid = employeeRepository.findUserPidByEmployeePid(employeePid);
		if (userPid == null || userPid.isEmpty()) {
			return null;
		}
		// filter based on product group
		List<SalesTargetGroup> salesTargetGroups = productGroupSalesTargetGrouprepository
				.findSalesTargetGroupByProductGroupPidIn(productGroupPids);
		List<String> salesTargetGroupPids = salesTargetGroups.stream().map(a -> a.getPid())
				.collect(Collectors.toList());
		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
				.findBySalesTargetGroupPidInAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
						salesTargetGroupPids, userPid, fromDate, toDate);
		if (!salesTargetGroupUserTargets.isEmpty()) {
			SalesPerformaceDTO salesPerformaceDTO = new SalesPerformaceDTO();
			// Get months date between the date
			List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);
			List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs = salesTargetGroupUserTargetMapper
					.salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(salesTargetGroupUserTargets);
			// group by SalesTargetGroupName
			Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetBySalesTargetGroupName = salesTargetGroupUserTargetDTOs
					.parallelStream()
					.collect(Collectors.groupingBy(SalesTargetGroupUserTargetDTO::getSalesTargetGroupName));

			CompanySetting companySetting = companySettingRepository.findOneByCompanyId();
			Optional<CompanyConfiguration> optLocWiseCompanyConfig = Optional.empty();
			Set<Object[]> docIdWithSTGroupName = new HashSet<>();
			Set<Object[]> pProfileIdWithSTGroupNames = new HashSet<>();
			if (companySetting == null || !companySetting.getSalesConfiguration().getAchievementSummaryTableEnabled()) {
				docIdWithSTGroupName = salesTargetGroupDocumentRepository
						.findDocumentIdWithSalesTargetGroupNameBySalesTargetGroupPid(salesTargetGroupPids);
				pProfileIdWithSTGroupNames = salesTargetGroupProductRepository
						.findProductIdWithSalesTargetGroupNameBySalesTargetGroupPid(salesTargetGroupPids);
				optLocWiseCompanyConfig = companyConfigurationRepository
						.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.LOCATION_WISE);
			}
			// actual sales user target
			Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetMap = new HashMap<>();
			for (SalesTargetGroup salesTargetGroup : salesTargetGroups) {
				String groupName = salesTargetGroup.getName();
				List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetList = new ArrayList<>();
				for (LocalDate monthDate : monthsBetweenDates) {
					String month = monthDate.getMonth().toString();
					// group by month, one month has only one
					// user-target
					Map<String, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetByMonth = null;
					List<SalesTargetGroupUserTargetDTO> userTarget = salesTargetGroupUserTargetBySalesTargetGroupName
							.get(groupName);
					SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO = null;
					if (userTarget != null) {
						salesTargetGroupUserTargetByMonth = userTarget.stream()
								.collect(Collectors.groupingBy(a -> a.getFromDate().getMonth().toString()));
						if (salesTargetGroupUserTargetByMonth != null
								&& salesTargetGroupUserTargetByMonth.get(month) != null) {
							salesTargetGroupUserTargetDTO = salesTargetGroupUserTargetByMonth.get(month).get(0);
						}
					}
					// no target saved, add a default one
					if (salesTargetGroupUserTargetDTO == null) {
						salesTargetGroupUserTargetDTO = new SalesTargetGroupUserTargetDTO();
						salesTargetGroupUserTargetDTO.setSalesTargetGroupName(groupName);
						salesTargetGroupUserTargetDTO.setSalesTargetGroupPid(salesTargetGroup.getPid());
						salesTargetGroupUserTargetDTO.setVolume(0);
						salesTargetGroupUserTargetDTO.setAmount(0);
					}
					if (companySetting != null
							&& companySetting.getSalesConfiguration().getAchievementSummaryTableEnabled()) {
						salesTargetGroupUserTargetDTO.setAchievedAmount(
								getAchievedAmountFromSummary(userPid, salesTargetGroupUserTargetDTO, monthDate));
					} else {
						Set<Long> documentIds = new HashSet<>();
						Set<Long> productProfileIds = new HashSet<>();
						// check territory wise or user wise
						for (Object[] obj : docIdWithSTGroupName) {
							if (obj[1].toString().equals(groupName)) {
								documentIds.add(Long.parseLong(obj[0].toString()));
							}
						}
						for (Object[] obj : pProfileIdWithSTGroupNames) {
							if (obj[1].toString().equals(groupName)) {
								productProfileIds.add(Long.parseLong(obj[0].toString()));
							}
						}
						if (optLocWiseCompanyConfig.isPresent()) {
							salesTargetGroupUserTargetDTO
									.setAchievedAmount(getAchievedAmountFromTransactionTerritoryWise(userPid, monthDate,
											documentIds, productProfileIds));
						} else {
							salesTargetGroupUserTargetDTO.setAchievedAmount(getAchievedAmountFromTransactionUserWise(
									userPid, monthDate, documentIds, productProfileIds));
						}
					}
					salesTargetGroupUserTargetList.add(salesTargetGroupUserTargetDTO);
				}
				salesTargetGroupUserTargetMap.put(groupName, salesTargetGroupUserTargetList);
			}
			List<String> monthList = new ArrayList<>();
			for (LocalDate monthDate : monthsBetweenDates) {
				monthList.add(monthDate.getMonth().toString());
			}
			salesPerformaceDTO.setMonthList(monthList);
			salesPerformaceDTO.setSalesTargetGroupUserTargets(salesTargetGroupUserTargetMap);
			return salesPerformaceDTO;
		}
		return null;
	}

	private double getAchievedAmountFromTransactionUserWise(String userPid, LocalDate initialDate,
			Set<Long> documentIds, Set<Long> productProfileIds) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		Double achievedAmount = 0D;
		if (!documentIds.isEmpty() && !productProfileIds.isEmpty()) {
			Set<Long> ivHeaderIds = inventoryVoucherHeaderRepository
					.findIdByUserPidAndDocumentsAndProductsAndCreatedDateBetween(userPid, documentIds,
							start.atTime(0, 0), end.atTime(23, 59));
			if (!ivHeaderIds.isEmpty()) {
				achievedAmount = inventoryVoucherDetailRepository
						.sumOfAmountByAndProductIdsAndHeaderIds(productProfileIds, ivHeaderIds);
			}
		}
		return achievedAmount == null ? 0 : achievedAmount;
	}

	private double getAchievedAmountFromTransactionTerritoryWise(String userPid, LocalDate initialDate,
			Set<Long> documentIds, Set<Long> productProfileIds) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		Double achievedAmount = 0D;
		// user's account profile
		Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByUserPidIn(Arrays.asList(userPid));
		Set<Long> accountProfileIds = locationAccountProfileRepository
				.findAccountProfileIdsByUserLocationIdsIn(locationIds);
		if (!documentIds.isEmpty() && !productProfileIds.isEmpty()) {
			// get achieved amount
			achievedAmount = inventoryVoucherDetailRepository
					.sumOfAmountByDocumentsAndProductsAndAccountProfilesAndCreatedDateBetween(accountProfileIds,
							documentIds, productProfileIds, start.atTime(0, 0), end.atTime(23, 59));
		}
		return achievedAmount == null ? 0 : achievedAmount;
	}

	private double getAchievedAmountFromSummary(String userPid,
			SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO, LocalDate initialDate) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		List<SalesSummaryAchievment> salesSummaryAchievmentList = salesSummaryAchievmentRepository
				.findByUserPidAndSalesTargetGroupPidAndAchievedDateBetween(userPid,
						salesTargetGroupUserTargetDTO.getSalesTargetGroupPid(), start, end);
		double achievedAmount = 0;
		if (!salesSummaryAchievmentList.isEmpty()) {
			for (SalesSummaryAchievment summaryAchievment : salesSummaryAchievmentList) {
				achievedAmount += summaryAchievment.getAmount();
			}
		}
		return achievedAmount;
	}

	private List<LocalDate> monthsDateBetweenDates(LocalDate start, LocalDate end) {
		List<LocalDate> ret = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
			ret.add(date);
		}
		return ret;
	}

}
