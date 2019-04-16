package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanySetting;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.SalesSummaryAchievment;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.CompanySettingRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.ProductGroupSalesTargetGrouprepository;
import com.orderfleet.webapp.repository.SalesSummaryAchievmentRepository;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.ProductGroupSalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformaceLocationWiseDTO;

/**
 * Web controller for managing Location Wise Sales View. Two view are managed in
 * this controller company/location-wise-sales
 * company/location-wise-sales-report (modern)
 *
 * @author Sarath
 * @since Oct 15, 2016
 */
@Controller
@RequestMapping("/web")
public class LocationWiseSalesResource {

	private final Logger log = LoggerFactory.getLogger(LocationWiseSalesResource.class);

	@Inject
	private LocationService locationService;

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherService;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private SalesSummaryAchievmentRepository salesSummaryAchievmentRepository;

	@Inject
	private CompanySettingRepository companySettingRepository;

	@Inject
	private ProductGroupSalesTargetGroupService productGroupSalesTargetGroupService;

	@Inject
	private ProductGroupSalesTargetGrouprepository productGroupSalesTargetGrouprepository;

	@Inject
	private LocationHierarchyService locationHierarchyService;

	@Timed
	@RequestMapping(value = "/sales-performance-report/locationwise", method = RequestMethod.GET)
	public String getSalesTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Marketing Activity Performance");
		model.addAttribute("locations", locationService.findAllByCompany());
		return "company/sales-locationwise-report";
	}

	@RequestMapping(value = "/sales-performance-report/locationwise/load-data", method = RequestMethod.GET)
	@ResponseBody
	public SalesPerformaceLocationWiseDTO performanceTargets(@RequestParam("territoryId") Long territoryId,
			@RequestParam("filterBy") String filterBy,
			@RequestParam(value = "fromDate", required = false) LocalDate fromDate,
			@RequestParam(value = "toDate", required = false) LocalDate toDate,
			@RequestParam("displayMode") String displayMode) {
		log.info("Rest Request to load sales performance report");
		List<SalesTargetGroup> salesTargetGroups = getSalesTargetGroups();
		if (!salesTargetGroups.isEmpty()) {
			return getSalesAchievedBySalesTargetGroups(salesTargetGroups, displayMode, filterBy, territoryId);
		}
		return null;
	}

	private SalesPerformaceLocationWiseDTO getSalesAchievedBySalesTargetGroups(List<SalesTargetGroup> salesTargetGroups,
			String displayMode, String filterBy, Long territoryId) {
		Company company = salesTargetGroups.get(0).getCompany();
		LocalDate fyStartDate = LocalDate.now();
		LocalDate fyEndDate = LocalDate.now();
		if (filterBy.equals("TYR")) {
			fyStartDate = company.getPeriodStartDate();
			fyEndDate = company.getPeriodEndDate();
		}
		if (filterBy.equals("LYR")) {
			fyStartDate = company.getPeriodStartDate().minusYears(1);
			fyEndDate = company.getPeriodEndDate().minusYears(1);
		}
		// report object
		SalesPerformaceLocationWiseDTO salesPerformaceDTO = new SalesPerformaceLocationWiseDTO();
		// salesGroup and its achieved amount
		Map<String, List<Double>> salesGroupAchieved = new HashMap<>();
		CompanySetting companySetting = companySettingRepository.findOneByCompanyId();
		if (companySetting != null && companySetting.getSalesConfiguration().getAchievementSummaryTableEnabled()) {
			for (SalesTargetGroup salesGroup : salesTargetGroups) {
				List<Double> achievedDateWiseList = new ArrayList<>();
				if (filterBy.equals("LMT")) {
					LocalDate initialDate = LocalDate.now().minusMonths(1);
					achievedDateWiseList.add(getAchievedAmountFromSummary(territoryId, salesGroup.getPid(),
							initialDate.with(TemporalAdjusters.firstDayOfMonth()),
							initialDate.with(TemporalAdjusters.lastDayOfMonth())));
				} else if (filterBy.equals("TMT")) {
					LocalDate initialDate = LocalDate.now();
					achievedDateWiseList.add(getAchievedAmountFromSummary(territoryId, salesGroup.getPid(),
							initialDate.with(TemporalAdjusters.firstDayOfMonth()),
							initialDate.with(TemporalAdjusters.lastDayOfMonth())));
				} else {
					LocalDate date = fyStartDate;
					while (date.isBefore(fyEndDate)) {
						if (displayMode.equals("QuarterWise")) {
							achievedDateWiseList.add(getAchievedAmountFromSummary(territoryId, salesGroup.getPid(),
									date.with(TemporalAdjusters.firstDayOfMonth()),
									date.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth())));
							date = date.plusMonths(3);
						} else {
							achievedDateWiseList.add(getAchievedAmountFromSummary(territoryId, salesGroup.getPid(),
									date.with(TemporalAdjusters.firstDayOfMonth()),
									date.with(TemporalAdjusters.lastDayOfMonth())));
							date = date.plusMonths(1);
						}
					}
				}
				salesGroupAchieved.put(salesGroup.getName(), achievedDateWiseList);
			}
		}
		salesPerformaceDTO.setSalesGroupAchieved(salesGroupAchieved);
		salesPerformaceDTO.setDateHeaderList(getDateHeaderList(filterBy, displayMode, companySetting.getCompany()));
		return salesPerformaceDTO;
	}

	private List<String> getDateHeaderList(String filterBy, String displayMode, Company company) {
		if (filterBy.equals("TYR") || filterBy.equals("LYR")) {
			if ("QuarterWise".equals(displayMode)) {
				return Arrays.asList("First Quater", "Second Quater", "Third Quater", "Fourth Quater");
			} else if ("MonthWise".equals(displayMode)) {
				List<String> dateList = new ArrayList<>();
				LocalDate startDate = company.getPeriodStartDate();
				LocalDate endDate = company.getPeriodEndDate();
				while (startDate.isBefore(endDate)) {
					dateList.add(startDate.getMonth().toString());
					startDate = startDate.plusMonths(1);
				}
				return dateList;
			}
		} else {
			if ("LMT".equals(filterBy)) {
				return Arrays.asList(LocalDate.now().minusMonths(1).getMonth().toString());
			}
		}
		return Arrays.asList(LocalDate.now().getMonth().toString());
	}

	private List<SalesTargetGroup> getSalesTargetGroups() {
		List<ProductGroupDTO> productGroupDTOs = productGroupSalesTargetGroupService.findAllProductGroupByCompany();
		if(productGroupDTOs.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> strings = new ArrayList<>();
		for (ProductGroupDTO productGroupDTO : productGroupDTOs) {
			strings.add(productGroupDTO.getPid());
		}
		return productGroupSalesTargetGrouprepository.findSalesTargetGroupByProductGroupPidIn(strings);
	}

	private double getAchievedAmountFromSummary(Long territoryId, String salesTargetGroupPid, LocalDate startDate,
			LocalDate endDate) {
		List<Long> locationIds;
		if (territoryId > 0) {
			locationIds = locationHierarchyService.getAllChildrenIdsByParentId(Long.valueOf(territoryId));
		} else {
			locationIds = locationService.findAllByCompany().stream().map(l -> Long.parseLong(l.getId())).collect(Collectors.toList());
		}
		List<SalesSummaryAchievment> salesSummaryAchievments = salesSummaryAchievmentRepository
				.findLocationIdInAndSalesTargetGroupPidAndAchievedDateBetween(locationIds, salesTargetGroupPid,
						startDate, endDate);
		return salesSummaryAchievments.stream().mapToDouble(SalesSummaryAchievment::getAmount).sum();
	}

	// *************************************** location-wise-sales
	// ********************************

	/**
	 * GET /locationWiseSales : get all the location Wise Sales.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         locationWiseSales in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/location-wise-sales", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllLocationWiseSales(Pageable pageable, Model model) {
		log.debug("Web request to get a page of location Wise Sales");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		model.addAttribute("locations", locationService.findAllByCompany());
		return "company/location-wise-sales";
	}

	/**
	 * GET /location-wise-sales/:id : get the "id" locationWiseSales.
	 *
	 * @param id
	 *            the id of the InventoryVoucherDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         InventoryVoucherDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/location-wise-sales/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InventoryVoucherHeaderDTO> getInventoryVoucher(@PathVariable String pid) {
		log.debug("Web request to get inventoryVoucherDTO by pid : {}", pid);
		return inventoryVoucherService.findOneByPid(pid)
				.map(inventoryVoucherDTO -> new ResponseEntity<>(inventoryVoucherDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/location-wise-sales/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<InventoryVoucherHeaderDTO>> filterInventoryVouchers(
			@RequestParam("employeePid") String employeePid, @RequestParam("locationPid") String locationPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter accounting vouchers");
		List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			inventoryVouchers = getFilterData(employeePid, locationPid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			inventoryVouchers = getFilterData(employeePid, locationPid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			inventoryVouchers = getFilterData(employeePid, locationPid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			inventoryVouchers = getFilterData(employeePid, locationPid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			inventoryVouchers = getFilterData(employeePid, locationPid, fromDateTime, toDateTime);
		}
		return new ResponseEntity<>(inventoryVouchers, HttpStatus.OK);
	}

	private List<InventoryVoucherHeaderDTO> getFilterData(String employeePid, String locationPid, LocalDate fDate,
			LocalDate tDate) {

		List<Document> documents = primarySecondaryDocumentRepository
				.findDocumentsByVoucherTypeAndCompanyId(VoucherType.PRIMARY_SALES_ORDER);
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<InventoryVoucherHeaderDTO> inventoryVouchers = new ArrayList<InventoryVoucherHeaderDTO>();
		if (userPid.equals("no") && locationPid.equals("no")) {
			inventoryVouchers = inventoryVoucherService.findAllByCompanyIdAndDateBetween(fromDate, toDate, documents);
		} else if (!userPid.equals("no") && locationPid.equals("no")) {
			inventoryVouchers = inventoryVoucherService.findAllByCompanyIdUserPidAndDateBetween(userPid, fromDate,
					toDate, documents);
		} else if (!locationPid.equals("no") && userPid.equals("no")) {
			inventoryVouchers = inventoryVoucherService.findAllByCompanyIdLocationPidAndDateBetween(locationPid,
					fromDate, toDate, documents);
		} else if (!userPid.equals("no") && !locationPid.equals("no")) {
			inventoryVouchers = inventoryVoucherService.findAllByCompanyIdUserPidLocationPidAndDateBetween(userPid,
					locationPid, fromDate, toDate, documents);
		}
		return inventoryVouchers;
	}

}
