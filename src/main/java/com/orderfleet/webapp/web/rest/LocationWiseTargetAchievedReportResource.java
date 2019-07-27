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
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.CompanySetting;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.ProductGroupLocationTarget;
import com.orderfleet.webapp.domain.SalesSummaryAchievment;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanySettingRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.ProductGroupLocationTargetRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductGroupSalesTargetGrouprepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.SalesSummaryAchievmentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.ProductGroupSalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupLocationPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupLocationTargetDTO;
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
public class LocationWiseTargetAchievedReportResource {

	private final Logger log = LoggerFactory.getLogger(LocationWiseTargetAchievedReportResource.class);

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private SalesTargetGroupUserTargetMapper productGroupLocationTargetMapper;

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
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ProductGroupLocationTargetRepository productGroupLocationTargetRepository;

	@Inject
	private LocationService locationService;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	/**
	 * GET /sales-target-vs-achieved-report
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/location-wise-target-vs-achieved-report", method = RequestMethod.GET)
	public String getLocationWiseTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Marketing Activity Performance");
		model.addAttribute("products", productGroupRepository.findAllByCompanyIdAndDeactivatedProductGroup(true));
		return "company/locationWiseTargetAchievedReport";
	}

	@RequestMapping(value = "/location-wise-target-vs-achieved-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public ProductGroupLocationPerformaceDTO performanceTargets(@RequestParam("productGroupPid") String productGroupPid,
			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		log.info("Rest Request to load sales target vs achieved report : {} , and date between {}, {}", productGroupPid,
				fromDate, toDate);
		if (productGroupPid.equals("no")) {
			return null;
		}

		Set<Long> productProfileIds = productGroupProductRepository.findProductIdByProductGroupPid(productGroupPid);

		// filter based on product group
		List<LocationDTO> locations = locationService.findAllLocationsByCompanyAndActivatedLocations();
		List<String> locationPids = locations.stream().map(a -> a.getPid()).collect(Collectors.toList());
		List<ProductGroupLocationTarget> productGroupLocationTargets = productGroupLocationTargetRepository
				.findByLocationPidInAndProductGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(locationPids,
						productGroupPid, fromDate, toDate);
		// if (!productGroupLocationTargets.isEmpty()) {
		ProductGroupLocationPerformaceDTO productGroupLocationPerformanceDTO = new ProductGroupLocationPerformaceDTO();
		// Get months date between the date
		List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);
		List<ProductGroupLocationTargetDTO> productGroupLocationTargetDTOs = new ArrayList<>();

		for (ProductGroupLocationTarget productGroupLocationTarget : productGroupLocationTargets) {
			productGroupLocationTargetDTOs.add(new ProductGroupLocationTargetDTO(productGroupLocationTarget));
		}
		// group by SalesTargetGroupName
		Map<String, List<ProductGroupLocationTargetDTO>> productGroupLocationTargetByLocationName = productGroupLocationTargetDTOs
				.parallelStream().collect(Collectors.groupingBy(ProductGroupLocationTargetDTO::getLocationName));

		// actual sales user target
		Map<String, List<ProductGroupLocationTargetDTO>> productGroupLocationTargetMap = new HashMap<>();
		for (LocationDTO location : locations) {
			String locationName = location.getName();
			List<ProductGroupLocationTargetDTO> productGroupLocationTargetList = new ArrayList<>();
			for (LocalDate monthDate : monthsBetweenDates) {
				String month = monthDate.getMonth().toString();
				// group by month, one month has only one
				// user-target
				Map<String, List<ProductGroupLocationTargetDTO>> productGroupLocationTargetByMonth = null;
				List<ProductGroupLocationTargetDTO> userTarget = productGroupLocationTargetByLocationName
						.get(locationName);
				ProductGroupLocationTargetDTO productGroupLocationTargetDTO = null;
				if (userTarget != null) {
					productGroupLocationTargetByMonth = userTarget.stream()
							.collect(Collectors.groupingBy(a -> a.getFromDate().getMonth().toString()));
					if (productGroupLocationTargetByMonth != null
							&& productGroupLocationTargetByMonth.get(month) != null) {
						productGroupLocationTargetDTO = productGroupLocationTargetByMonth.get(month).get(0);
					}
				}
				// no target saved, add a default one
				if (productGroupLocationTargetDTO == null) {
					productGroupLocationTargetDTO = new ProductGroupLocationTargetDTO();
					productGroupLocationTargetDTO.setLocationName(locationName);
					productGroupLocationTargetDTO.setLocationPid(location.getPid());
					productGroupLocationTargetDTO.setVolume(0);
					productGroupLocationTargetDTO.setAmount(0);
				}

				productGroupLocationTargetDTO
						.setAchievedVolume(getAchievedVolume(location.getPid(), productProfileIds, monthDate));

				productGroupLocationTargetList.add(productGroupLocationTargetDTO);
			}
			productGroupLocationTargetMap.put(locationName, productGroupLocationTargetList);
		}
		List<String> monthList = new ArrayList<>();
		for (LocalDate monthDate : monthsBetweenDates) {
			monthList.add(monthDate.getMonth().toString());
		}
		productGroupLocationPerformanceDTO.setProductGroupLocationTargets(productGroupLocationTargetMap);
		productGroupLocationPerformanceDTO.setMonthList(monthList);
		/*
		 * productGroupLocationPerformanceDTO.setSalesTargetGroupUserTargets(
		 * productGroupLocationTargetMap);
		 */
		return productGroupLocationPerformanceDTO;
		/*
		 * } return null;
		 */
	}

	private double getAchievedVolume(String locationPid, Set<Long> productProfileIds, LocalDate initialDate) {

		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());

		Set<Long> accountProfileIds = locationAccountProfileRepository.findAccountProfileIdByLocationPid(locationPid);

		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
				SecurityUtils.getCurrentUsersCompanyId());
		if (primarySecDoc.isEmpty()) {
			log.debug("........No PrimarySecondaryDocument configuration Available...........");
			return 0;
		}
		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		Double achievedVolume = 0D;
		if (!accountProfileIds.isEmpty() && !productProfileIds.isEmpty()) {
			Set<Long> ivHeaderIds = inventoryVoucherHeaderRepository.findIdByAccountProfileAndDocumentDateBetween(
					accountProfileIds, documentIds, start.atTime(0, 0), end.atTime(23, 59));
			if (!ivHeaderIds.isEmpty()) {
				achievedVolume = inventoryVoucherDetailRepository
						.sumOfVolumeByAndProductIdsAndHeaderIds(productProfileIds, ivHeaderIds);
			}
		}
		return achievedVolume == null ? 0 : achievedVolume;

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
			SalesTargetGroupUserTargetDTO productGroupLocationTargetDTO, LocalDate initialDate) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		List<SalesSummaryAchievment> salesSummaryAchievmentList = salesSummaryAchievmentRepository
				.findByUserPidAndSalesTargetGroupPidAndAchievedDateBetween(userPid,
						productGroupLocationTargetDTO.getSalesTargetGroupPid(), start, end);
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
