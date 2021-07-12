//package com.orderfleet.webapp.web.rest;
//
//import java.net.URISyntaxException;
//import java.time.LocalDate;
//import java.time.temporal.TemporalAdjusters;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import javax.inject.Inject;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.codahale.metrics.annotation.Timed;
//import com.orderfleet.webapp.domain.EmployeeProfile;
//import com.orderfleet.webapp.domain.Location;
//import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
//import com.orderfleet.webapp.domain.SalesLedgerWiseTarget;
//import com.orderfleet.webapp.domain.enums.VoucherType;
//import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
//import com.orderfleet.webapp.repository.EmployeeProfileRepository;
//import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
//import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
//import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
//import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
//import com.orderfleet.webapp.repository.SalesLedgerWiseTargetRepository;
//import com.orderfleet.webapp.security.SecurityUtils;
//import com.orderfleet.webapp.web.rest.dto.UserWiseSalesPerformaceDTO;
//import com.orderfleet.webapp.web.rest.dto.SalesLedgerWiseTargetDTO;
//
///**
// * Web controller for Sales Target v/s achieved report
// * 
// * @author Muhammed Riyas T
// * @since October 06, 2016
// */
//@Controller
//@RequestMapping("/web")
//public class SalesLedgerWiseTargetAchievedReportResource {
//
//	private final Logger log = LoggerFactory.getLogger(SalesLedgerWiseTargetAchievedReportResource.class);
//
//	@Inject
//	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;
//
//	@Inject
//	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;
//
//	@Inject
//	private EmployeeProfileLocationRepository employeeProfileLocationRepository;
//
//	@Inject
//	private LocationAccountProfileRepository locationAccountProfileRepository;
//
//	@Inject
//	private EmployeeProfileRepository employeeRepository;
//
//	@Inject
//	private SalesLedgerWiseTargetRepository salesLedgerWiseTargetRepository;
//
//	@Inject
//	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;
//
//	/**
//	 * GET /sales-target-vs-achieved-report
//	 *
//	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
//	 * @throws URISyntaxException if there is an error to generate the pagination
//	 *                            HTTP headers
//	 */
//	@Timed
//	@RequestMapping(value = "/user-wise-sales-target-vs-achieved-report", method = RequestMethod.GET)
//	public String getLocationWiseTargetAchievedReport(Model model) {
//		log.debug("Web request to get a page of Marketing Activity Performance");
//		model.addAttribute("employees", employeeRepository.findAllByCompanyAndDeativatedEmployeeProfile(true));
//		return "company/salesLedgerWiseTargetAchievedReport";
//	}
//
//	@RequestMapping(value = "/user-wise-sales-target-vs-achieved-report/load-data", method = RequestMethod.GET)
//	@ResponseBody
//	public UserWiseSalesPerformaceDTO performanceTargets(
//			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
//			@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
//		log.info("Rest Request to load user wise sales target vs achieved report :  date between {}, {}", fromDate,
//				toDate);
//
//		// filter based on product group
//		List<EmployeeProfile> employees = employeeRepository.findAllByCompanyAndDeativatedEmployeeProfile(true);
//		List<String> employeePids = employees.stream().map(a -> a.getPid()).collect(Collectors.toList());
//		List<SalesLedgerWiseTarget> salesLedgerWiseTargets = salesLedgerWiseTargetRepository
//				.findBySalesLedgerPidInAndFromDateGreaterThanEqualAndToDateLessThanEqual(employeePids, fromDate,
//						toDate);
//		// if (!productGroupLocationTargets.isEmpty()) {
//		UserWiseSalesPerformaceDTO salesLedgerWisePerformaceDTO = new UserWiseSalesPerformaceDTO();
//		// Get months date between the date
//		List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);
//		List<SalesLedgerWiseTargetDTO> salesLedgerWiseTargetDTOs = new ArrayList<>();
//
//		for (SalesLedgerWiseTarget salesLedgerWiseTarget : salesLedgerWiseTargets) {
//			salesLedgerWiseTargetDTOs.add(new SalesLedgerWiseTargetDTO(salesLedgerWiseTarget));
//		}
//		// group by SalesTargetGroupName
//		Map<String, List<SalesLedgerWiseTargetDTO>> salesLedgerWiseTargetByUserName = salesLedgerWiseTargetDTOs
//				.parallelStream().collect(Collectors.groupingBy(SalesLedgerWiseTargetDTO::getUserName));
//
//		// actual sales user target
//		Map<String, List<SalesLedgerWiseTargetDTO>> salesLedgerWiseTargetMap = new HashMap<>();
//		for (EmployeeProfile employee : employees) {
//			String userName = employee.getName();
//			List<SalesLedgerWiseTargetDTO> salesLedgerWiseTargetList = new ArrayList<>();
//			for (LocalDate monthDate : monthsBetweenDates) {
//				String month = monthDate.getMonth().toString();
//				// group by month, one month has only one
//				// user-target
//				Map<String, List<SalesLedgerWiseTargetDTO>> salesLedgerWiseTargetByMonth = null;
//				List<SalesLedgerWiseTargetDTO> userTarget = salesLedgerWiseTargetByUserName.get(userName);
//				SalesLedgerWiseTargetDTO salesLedgerWiseTargetDTO = null;
//				if (userTarget != null) {
//					salesLedgerWiseTargetByMonth = userTarget.stream()
//							.collect(Collectors.groupingBy(a -> a.getFromDate().getMonth().toString()));
//					if (salesLedgerWiseTargetByMonth != null && salesLedgerWiseTargetByMonth.get(month) != null) {
//						salesLedgerWiseTargetDTO = salesLedgerWiseTargetByMonth.get(month).get(0);
//					}
//				}
//				// no target saved, add a default one
//				if (salesLedgerWiseTargetDTO == null) {
//					salesLedgerWiseTargetDTO = new SalesLedgerWiseTargetDTO();
//					salesLedgerWiseTargetDTO.setUserName(userName);
//					salesLedgerWiseTargetDTO.setUserPid(employee.getPid());
//					salesLedgerWiseTargetDTO.setVolume(0);
//					salesLedgerWiseTargetDTO.setAmount(0);
//				}
//
//				salesLedgerWiseTargetDTO.setAchievedAmount(getAchievedAmount(employee.getPid(), monthDate));
//
//				salesLedgerWiseTargetList.add(salesLedgerWiseTargetDTO);
//			}
//			salesLedgerWiseTargetMap.put(userName, salesLedgerWiseTargetList);
//		}
//		List<String> monthList = new ArrayList<>();
//		for (LocalDate monthDate : monthsBetweenDates) {
//			monthList.add(monthDate.getMonth().toString());
//		}
//		salesLedgerWisePerformaceDTO.setSalesLedgerWiseTargets(salesLedgerWiseTargetMap);
//		salesLedgerWisePerformaceDTO.setMonthList(monthList);
//		/*
//		 * productGroupLocationPerformanceDTO.setSalesTargetGroupUserTargets(
//		 * productGroupLocationTargetMap);
//		 */
//		return salesLedgerWisePerformaceDTO;
//		/*
//		 * } return null;
//		 */
//	}
//
//	private double getAchievedAmount(String employeePid, LocalDate initialDate) {
//
//		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
//		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
//
//		List<Location> locations = employeeProfileLocationRepository.findLocationsBySalesLedgerPid(employeePid);
//
//		if (locations.size() == 0) {
//			return 0;
//		}
//
//		Set<Long> locationIds = new HashSet<>();
//		for (Location location : locations) {
//			locationIds.add(location.getId());
//		}
//
//		Set<Long> accountProfileIds = locationAccountProfileRepository
//				.findAccountProfileIdsByUserLocationIdsIn(locationIds);
//
//		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
//		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
//				SecurityUtils.getCurrentUsersCompanyId());
//		if (primarySecDoc.isEmpty()) {
//			log.debug("........No PrimarySecondaryDocument configuration Available...........");
//			return 0;
//		}
//		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
//				.collect(Collectors.toList());
//
//		Double achievedAmount = 0D;
//		if (!accountProfileIds.isEmpty()) {
//			Set<Long> ivHeaderIds = inventoryVoucherHeaderRepository.findIdByAccountProfileAndDocumentDateBetween(
//					accountProfileIds, documentIds, start.atTime(0, 0), end.atTime(23, 59));
//			if (!ivHeaderIds.isEmpty()) {
//				achievedAmount = inventoryVoucherDetailRepository.sumOfAmountByHeaderIds(ivHeaderIds);
//			}
//		}
//		return achievedAmount == null ? 0 : achievedAmount;
//
//	}
//
//	private List<LocalDate> monthsDateBetweenDates(LocalDate start, LocalDate end) {
//		List<LocalDate> ret = new ArrayList<>();
//		for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
//			ret.add(date);
//		}
//		return ret;
//	}
//
//}
