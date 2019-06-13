package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.StageHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserCustomerGroup;
import com.orderfleet.webapp.domain.UserCustomerGroupTarget;
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
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.repository.UserCustomerGroupRepository;
import com.orderfleet.webapp.repository.UserCustomerGroupTargetRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupSalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.CustomerGropuPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserCustomerGroupTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupUserTargetMapper;

/**
 * Web controller for Sales Target v/s achieved report
 * 
 * @author Muhammed Riyas T
 * @since October 06, 2016
 */
@Controller
@RequestMapping("/web")
public class UserCustomerGroupsTargetAchievedReportResource {

	private final Logger log = LoggerFactory.getLogger(UserCustomerGroupsTargetAchievedReportResource.class);

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
	private UserCustomerGroupRepository userCustomerGroupRepository;
	
	@Inject
	private UserCustomerGroupTargetRepository userCustomerGroupTargetRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private StageHeaderRepository stageHeaderRepository;

	/**
	 * GET /sales-target-vs-achieved-report
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/user-customerGroup-target-vs-achieved-report", method = RequestMethod.GET)
	public String getSalesTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Marketing Activity Performance");
		model.addAttribute("products", productGroupSalesTargetGroupService.findAllProductGroupByCompany());
		return "company/userCustomerGroupTargetAchievedReport";
	}

	@RequestMapping(value = "/user-customerGroup-target-vs-achieved-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public CustomerGropuPerformaceDTO performanceTargets(@RequestParam("employeePid") String employeePid,
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
		List<UserCustomerGroup> customerGroupList = userCustomerGroupRepository.findUserCustomerGroupsByUserPid(userPid);
		List<Stage> customerGroup = customerGroupList.stream().map(UserCustomerGroup::getStage).collect(Collectors.toList());
		Optional<User> user = userRepository.findOneByPid(userPid);
		List<UserCustomerGroupTarget> userTargetList = userCustomerGroupTargetRepository.findByUserIdAndDateBetween(user.get().getId(),fromDate, toDate);
		LocalDateTime fromDateTime = fromDate.atTime(0,0);
		LocalDateTime toDateTime = toDate.atTime(23,59);
//		findByUserIdIn
		Set<Long> sHeaderds = stageHeaderRepository.findCustomerGroupIdByCreatedDateBetween(fromDateTime, toDateTime);
		List<Long> stageHeadIds = new ArrayList<>(sHeaderds);
		List<StageHeader> stageHeaders = stageHeaderRepository.findByUserIdIn(employeePid, stageHeadIds);
	
		if (!userTargetList.isEmpty()) {
			CustomerGropuPerformaceDTO customerGropuPerformaceDTO = new CustomerGropuPerformaceDTO();
			// Get months date between the date
			List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);
			List<UserCustomerGroupTargetDTO> userCustomerGroupTargetGroupUserTargetDTOs = 
					userTargetList.stream().map(utl -> new UserCustomerGroupTargetDTO(utl)).collect(Collectors.toList());
			// group by SalesTargetGroupName
			Map<String, List<UserCustomerGroupTargetDTO>> userCustomerGroupUserTargetBySalesTargetGroupName = userCustomerGroupTargetGroupUserTargetDTOs
					.parallelStream()
					.collect(Collectors.groupingBy(UserCustomerGroupTargetDTO::getStageName));

			// actual sales user target
			Map<String, List<UserCustomerGroupTargetDTO>> customerGroupUserTargetMap = new HashMap<>();
			for (Stage userTargetGroup : customerGroup) {
				String groupName = userTargetGroup.getName();
				List<UserCustomerGroupTargetDTO> customerGroupUserTargetList = new ArrayList<>();
				for (LocalDate monthDate : monthsBetweenDates) {
					String month = monthDate.getMonth().toString();
					// group by month, one month has only one
					// user-target
					Map<String, List<UserCustomerGroupTargetDTO>> customerGroupUserTargetByMonth = null;
					List<UserCustomerGroupTargetDTO> userTarget = userCustomerGroupUserTargetBySalesTargetGroupName
							.get(groupName);
					UserCustomerGroupTargetDTO userCustomerGroupTargetDTO = null;
					if (userTarget != null) {
						customerGroupUserTargetByMonth = userTarget.stream()
								.collect(Collectors.groupingBy(a -> a. getStartDate().getMonth().toString()));
						log.info("=====================*******===========================");
						if (customerGroupUserTargetByMonth != null
								&& customerGroupUserTargetByMonth.get(month) != null) {
							userCustomerGroupTargetDTO = customerGroupUserTargetByMonth.get(month).get(0);
							log.info("============================================================");
							log.info("Stage Header Size : "+stageHeaders.size());
							List<Stage> stages = stageHeaders.stream().filter(sh ->
									sh.getCreatedDate().isAfter(fromDateTime) &&
									sh.getCreatedDate().isBefore(toDateTime))
									.map(StageHeader::getStage).collect(Collectors.toList());
							log.info("Stage  Size : "+stages.size());
							long achivedNumber =  stages.stream().filter(s -> s.getName().equals(groupName)).count();
							log.info("Count : "+achivedNumber);
							userCustomerGroupTargetDTO.setAchivedNumber(achivedNumber);
						}
					}
					// no target saved, add a default one
					if (userCustomerGroupTargetDTO == null) {
						userCustomerGroupTargetDTO = new UserCustomerGroupTargetDTO();
						userCustomerGroupTargetDTO.setStageName(groupName);
						userCustomerGroupTargetDTO.setStagePid(userTargetGroup.getPid());
						userCustomerGroupTargetDTO.setTargetNumber(0L);
						userCustomerGroupTargetDTO.setAchivedNumber(0L);
					}
					if(userCustomerGroupTargetDTO.getAchivedNumber()==null) {
						userCustomerGroupTargetDTO.setAchivedNumber(0L);
					}
					if(userCustomerGroupTargetDTO.getTargetNumber()==null) {
						userCustomerGroupTargetDTO.setTargetNumber(0L);
					}
					customerGroupUserTargetList.add(userCustomerGroupTargetDTO);
				}
				customerGroupUserTargetMap.put(groupName, customerGroupUserTargetList);
			}
			List<String> monthList = new ArrayList<>();
			for (LocalDate monthDate : monthsBetweenDates) {
				monthList.add(monthDate.getMonth().toString());
			}
			customerGropuPerformaceDTO.setMonthList(monthList);
			customerGropuPerformaceDTO.setCustomerGroupUserTargets(customerGroupUserTargetMap);
			return customerGropuPerformaceDTO;
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
