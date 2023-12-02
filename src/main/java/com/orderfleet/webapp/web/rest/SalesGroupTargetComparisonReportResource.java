package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupSalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.*;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupUserTargetMapper;
import org.apache.kafka.common.metrics.stats.Total;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.naming.Name;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Web controller for Sales Target v/s achieved report
 * 
 * @author Resmi T R
 * @since November 07, 2023
 */
@Controller
@RequestMapping("/web")
public class SalesGroupTargetComparisonReportResource {
	private final Logger log = LoggerFactory.getLogger(SalesGroupTargetComparisonReportResource.class);
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
	@RequestMapping(value = "/sales-group-target-comparison", method = RequestMethod.GET)
	public String getSalesTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Sales comparison");
		model.addAttribute("products", productGroupSalesTargetGroupService.findAllProductGroupByCompany());
		return "company/salesGroupTargetComparison";
	}

	@RequestMapping(value = "/sales-group-target-comparison/load-data", method = RequestMethod.GET)
	@ResponseBody
	public List<SalesComparisonList> performanceTargets(@RequestParam("productGroupPids") List<String> productGroupPids,
			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
														@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate, @RequestParam(value = "fromDate1", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate1,
			@RequestParam(value = "toDate1", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate1,@RequestParam("period") String period) {
        log.info("FromDate:"+fromDate);
		log.info("toDate:"+toDate);
		log.info("FromDate1:"+fromDate1);
		log.info("toDate:"+toDate1);
		log.info("period :"+period);

		List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();

		List<String> userPids = dashboardUsers.stream().map(user -> user.getPid()).collect(Collectors.toList());

		// filter based on product group
		List<SalesTargetGroup> salesTargetGroups = productGroupSalesTargetGrouprepository
				.findSalesTargetGroupByProductGroupPidIn(productGroupPids);

		List<String> salesTargetGroupPids = salesTargetGroups.stream().map(a -> a.getPid())
				.collect(Collectors.toList());

		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
				.findBySalesTargetGroupPidInAndUserPidInAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
						salesTargetGroupPids, userPids, fromDate,toDate);

		List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs = salesTargetGroupUserTargetMapper
				.salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(salesTargetGroupUserTargets);
		Map<String, Double> SalesGroupWithTarget = new HashMap<>();
         if(!salesTargetGroupUserTargetDTOs.isEmpty()) {
	     SalesGroupWithTarget = salesTargetGroupUserTargetDTOs.stream().collect(Collectors.groupingBy(SalesTargetGroupUserTargetDTO::getSalesTargetGroupName,
			Collectors.summingDouble(SalesTargetGroupUserTargetDTO::getVolume)));
       }
		

		List<SalesSummaryAchievment> salesSummaryAchievmentList = salesSummaryAchievmentRepository
				.findByUserPidInAndSalesTargetGroupPidInAndAchievedDateBetween(userPids,
						salesTargetGroupPids,fromDate,toDate);
		SalesSummaryAchievmentDTO achievmentDTO = new SalesSummaryAchievmentDTO();
		List<SalesSummaryAchievmentDTO> salesSummaryAchievmentDTOList = achievmentDTO.convertToDTo(salesSummaryAchievmentList);


		Map<String, Double> salesGroupWithAcheived = new HashMap<>();
		if(!salesSummaryAchievmentDTOList.isEmpty()) {
			salesGroupWithAcheived = salesSummaryAchievmentDTOList.stream().collect(Collectors.groupingBy(SalesSummaryAchievmentDTO::getSalesTargetGroupName,
					Collectors.summingDouble(SalesSummaryAchievmentDTO::getAmount)));
		}

		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargets1 = salesTargetGroupUserTargetRepository
				.findBySalesTargetGroupPidInAndUserPidInAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
						salesTargetGroupPids, userPids, fromDate1,toDate1);

		List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs1 = salesTargetGroupUserTargetMapper
				.salesTargetGroupUserTargetsToSalesTargetGroupUserTargetDTOs(salesTargetGroupUserTargets1);
		Map<String, Double> SalesGroupWithTarget1= new HashMap<>();
     if(!salesTargetGroupUserTargetDTOs1.isEmpty()) {
 	SalesGroupWithTarget1 = salesTargetGroupUserTargetDTOs1.stream().collect(Collectors.groupingBy(SalesTargetGroupUserTargetDTO::getSalesTargetGroupName,
			Collectors.summingDouble(SalesTargetGroupUserTargetDTO::getVolume)));
      }

		List<SalesSummaryAchievment> salesSummaryAchievmentList1 = salesSummaryAchievmentRepository
				.findByUserPidInAndSalesTargetGroupPidInAndAchievedDateBetween(userPids,
						salesTargetGroupPids,fromDate1,toDate1);
		SalesSummaryAchievmentDTO achievmentDTO1 = new SalesSummaryAchievmentDTO();
		List<SalesSummaryAchievmentDTO> salesSummaryAchievmentDTOList1 = achievmentDTO1.convertToDTo(salesSummaryAchievmentList1);

		Map<String, Double> salesGroupWithAcheived1= new HashMap<>();
		if(!salesSummaryAchievmentDTOList1.isEmpty()) {
			salesGroupWithAcheived1 = salesSummaryAchievmentDTOList1.stream().collect(Collectors.groupingBy(SalesSummaryAchievmentDTO::getSalesTargetGroupName,
					Collectors.summingDouble(SalesSummaryAchievmentDTO::getAmount)));
		}

             List<SalesComparisonList> salesComparisonLists = new ArrayList<>();
              List<SalesComparisonDTO> salescomparisonList = new ArrayList<>();
		SalesComparisonList salesComparison = new SalesComparisonList();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
		String formattedDate1 = fromDate.format(formatter);
		String formattedDate2 = fromDate1.format(formatter);
		String formattedToDate = toDate.format(formatter);
		String formattedToDate1 = toDate1.format(formatter);
		salesComparison.setFromDate1(formattedDate1);
		salesComparison.setFromDate2(formattedDate2);
		salesComparison.setToDate(formattedToDate);
		salesComparison.setToDate1(formattedToDate1);
		salesComparison.setPeriod(period);
			  Set<String> SalesTargetGroupName = new HashSet<>();
		   SalesTargetGroupName = SalesGroupWithTarget.keySet();
		   for(String groupName : SalesTargetGroupName)
		   {
			   SalesComparisonDTO salesComparisonDTO = new SalesComparisonDTO();
			   salesComparisonDTO.setSalesGroupName(groupName);
			   salesComparisonDTO.setTargetTotal1(SalesGroupWithTarget.get(groupName));
			   salesComparisonDTO.setAchievedTotal1(salesGroupWithAcheived.get(groupName));
			   salesComparisonDTO.setTargetTotal2(SalesGroupWithTarget1.get(groupName));
			   salesComparisonDTO.setAchievedTotal2(salesGroupWithAcheived1.get(groupName));
			   salescomparisonList.add(salesComparisonDTO);
		   }
          salesComparison.setSalesComparisonDTOS(salescomparisonList);
		   salesComparisonLists.add(salesComparison);
		   return salesComparisonLists;
	}


}
