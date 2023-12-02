package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanySettingRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
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
import com.orderfleet.webapp.web.rest.dto.SalesTargetTotalDTO;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupUserTargetMapper;

/**
 * Web controller for Sales Target v/s achieved report
 * 
 * @author Muhammed Riyas T
 * @since October 06, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesGroupTargetAchievedTotalResource {

	private final Logger log = LoggerFactory.getLogger(SalesTargetAchievedReportResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	/**
	 * GET /sales-target-vs-achieved-report
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/sales-group-target-achieved", method = RequestMethod.GET)
	public String getSalesTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Marketing Activity Performance");
		model.addAttribute("products", productGroupSalesTargetGroupService.findAllProductGroupByCompany());
		return "company/salesGroupTargetAchievedTotal";
	}

	@RequestMapping(value = "/sales-group-target-achieved/load-data", method = RequestMethod.GET)
	@ResponseBody
	public SalesPerformaceDTO performanceTargets(@RequestParam("productGroupPids") List<String> productGroupPids,
			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
log.info("FromDate :"+fromDate);
log.info("toDate :"+toDate);
List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();

		List<String> userPids = dashboardUsers.stream().map(user -> user.getPid()).collect(Collectors.toList());

		// filter based on product group
		List<SalesTargetGroup> salesTargetGroups = productGroupSalesTargetGrouprepository
				.findSalesTargetGroupByProductGroupPidIn(productGroupPids);

		List<String> salesTargetGroupPids = salesTargetGroups.stream().map(a -> a.getPid())
				.collect(Collectors.toList());

		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
				.findBySalesTargetGroupPidInAndUserPidInAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
						salesTargetGroupPids, userPids, fromDate, toDate);
		
		System.out.println("Size of salesTargetGroupUserTargets: "+salesTargetGroupUserTargets.size());
		
	      	salesTargetGroupUserTargets.stream().forEach(a->a.getUser().getFirstName());

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

			Map<LocalDate, List<SalesTargetGroupUserTargetDTO>> salesTargetGroupUserTargetByMonths = salesTargetGroupUserTargetDTOs
					.parallelStream().collect(Collectors.groupingBy(SalesTargetGroupUserTargetDTO::getFromDate));

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
			Map<String, List<Map<String,SalesTargetTotalDTO>>> salesTargetGroupUserTargetMap = new HashMap<>();

			List<Double> targetTotal = new ArrayList<>();

			List<Double> achievedTotal = new ArrayList<>();

			for (SalesTargetGroup salesTargetGroup : salesTargetGroups) {
				
				List<Map<String,SalesTargetTotalDTO>> finalList = new ArrayList<>();

				String groupName = salesTargetGroup.getName();

			
		
				
				List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOList =new ArrayList<>();
				
				for (LocalDate monthDate : monthsBetweenDates) {
					
					double target = 0;
					Map<String,SalesTargetTotalDTO> salesTargetDTO = new HashMap<>();
					
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
							
							target =+ salesTargetGroupUserTargetByMonth.get(month).stream().mapToDouble(SalesTargetGroupUserTargetDTO::getVolume).sum();
							salesTargetGroupUserTargetDTOList.addAll(salesTargetGroupUserTargetByMonth.get(month));
                      
							
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
				
					double acheiveAmount = 0;
					if (companySetting != null
							&& companySetting.getSalesConfiguration().getAchievementSummaryTableEnabled()) {
					
							double number = getAchievedAmountFromSummary(salesTargetGroupUserTargetDTOList,salesTargetGroup.getPid(),monthDate);
							DecimalFormat decimalFormat = new DecimalFormat("#.##");
							acheiveAmount = Double.parseDouble(decimalFormat.format(number));
							

					}
					
					
					SalesTargetTotalDTO salesDTO = new SalesTargetTotalDTO();
					salesDTO.setTargetTotal(target);
					salesDTO.setAchivedTotal(acheiveAmount);
					salesTargetDTO.put(month, salesDTO);
					finalList.add(salesTargetDTO);
					
					salesTargetGroupUserTargetMap.put(groupName, finalList);
					
				
					
				}

			}
	
			for (LocalDate monthDate : monthsBetweenDates) {
				List<SalesTargetGroupUserTargetDTO> userTargetByMonth = salesTargetGroupUserTargetByMonths
						.get(monthDate);
				if (userTargetByMonth != null) {
					Map<Object, List<SalesTargetGroupUserTargetDTO>> saleTargetGroups = userTargetByMonth.stream()
							.collect(Collectors.groupingBy(a -> a.getFromDate()));

					for (Map.Entry map : saleTargetGroups.entrySet()) {
						
						List<SalesTargetGroupUserTargetDTO> values = (List<SalesTargetGroupUserTargetDTO>) map
								.getValue();
						double total = 0;
						double achieved_total = 0;
						for (SalesTargetGroupUserTargetDTO stgt : values) {
							if (stgt.getAmount() != 0) {
								total += stgt.getAmount();

							} else {

								total += stgt.getVolume();
							}
							achieved_total += stgt.getAchievedAmount();
						}

						targetTotal.add(total);
						achievedTotal.add(achieved_total);
					}
				} else {
					double total = 0;
					targetTotal.add(total);
					achievedTotal.add(total);
				}
			}
			List<String> monthList = new ArrayList<>();
			for (LocalDate monthDate : monthsBetweenDates) {
				monthList.add(monthDate.getMonth().toString());
			}
			salesPerformaceDTO.setAchievedList(achievedTotal);
			salesPerformaceDTO.setTotalList(targetTotal);
			salesPerformaceDTO.setMonthList(monthList);
			salesPerformaceDTO.setFinalList(salesTargetGroupUserTargetMap);
			return salesPerformaceDTO;
		}
		return null;
	}



	private double getAchievedAmountFromSummary(List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOList, String groupPid, LocalDate initialDate) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		
		List<String> userPids = salesTargetGroupUserTargetDTOList.stream().map(a->a.getUserPid()).collect(Collectors.toList());
		
		
		
		List<SalesSummaryAchievment> salesSummaryAchievmentList = salesSummaryAchievmentRepository
				.findByUserPidInAndSalesTargetGroupPidAndAchievedDateBetween(userPids,
						groupPid, start, end);
		double achievedAmount = 0;
		if (!salesSummaryAchievmentList.isEmpty()) {
			for (SalesSummaryAchievment summaryAchievment : salesSummaryAchievmentList) {
				System.out.println("group :"+summaryAchievment.getSalesTargetGroup().getName()+"amount :"+summaryAchievment.getAmount()+"userId :"+summaryAchievment.getUser().getId());
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
