package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.UserWiseSalesTarget;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.UserWiseSalesTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.UserWiseSalesPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseSalesTargetDTO;

/**
 * Web controller for Sales Target v/s achieved report
 * 
 * @author Muhammed Riyas T
 * @since October 06, 2016
 */
@Controller
@RequestMapping("/web")
public class UserWiseSalesTargetAchievedReportResource {

	private final Logger log = LoggerFactory.getLogger(UserWiseSalesTargetAchievedReportResource.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private EmployeeProfileRepository employeeRepository;

	@Inject
	private UserWiseSalesTargetRepository userWiseSalesTargetRepository;

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
	@RequestMapping(value = "/user-wise-sales-target-vs-achieved-report", method = RequestMethod.GET)
	public String getLocationWiseTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Marketing Activity Performance");
		model.addAttribute("employees", employeeRepository.findAllByCompanyAndDeativatedEmployeeProfile(true));
		return "company/userWiseSalesTargetAchievedReport";
	}

	@RequestMapping(value = "/user-wise-sales-target-vs-achieved-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public UserWiseSalesPerformaceDTO performanceTargets(
			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		log.info("Rest Request to load user wise sales target vs achieved report :  date between {}, {}", fromDate,
				toDate);

		// filter based on product group
		List<EmployeeProfile> employees = employeeRepository.findAllByCompanyAndDeativatedEmployeeProfile(true);
		List<String> employeePids = employees.stream().map(a -> a.getPid()).collect(Collectors.toList());
		List<UserWiseSalesTarget> userWiseSalesTargets = userWiseSalesTargetRepository
				.findByEmployeeProfilePidInAndFromDateGreaterThanEqualAndToDateLessThanEqual(employeePids, fromDate,
						toDate);
		// if (!productGroupLocationTargets.isEmpty()) {
		UserWiseSalesPerformaceDTO userWiseSalesPerformaceDTO = new UserWiseSalesPerformaceDTO();
		// Get months date between the date
		List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);
		List<UserWiseSalesTargetDTO> userWiseSalesTargetDTOs = new ArrayList<>();

		for (UserWiseSalesTarget userWiseSalesTarget : userWiseSalesTargets) {
			userWiseSalesTargetDTOs.add(new UserWiseSalesTargetDTO(userWiseSalesTarget));
		}
		// group by SalesTargetGroupName
		Map<String, List<UserWiseSalesTargetDTO>> userWiseSalesTargetByUserName = userWiseSalesTargetDTOs
				.parallelStream().collect(Collectors.groupingBy(UserWiseSalesTargetDTO::getUserName));

		// actual sales user target
		Map<String, List<UserWiseSalesTargetDTO>> userWiseSalesTargetMap = new HashMap<>();
		for (EmployeeProfile employee : employees) {
			String userName = employee.getName();
			List<UserWiseSalesTargetDTO> userWiseSalesTargetList = new ArrayList<>();
			for (LocalDate monthDate : monthsBetweenDates) {
				String month = monthDate.getMonth().toString();
				// group by month, one month has only one
				// user-target
				Map<String, List<UserWiseSalesTargetDTO>> userWiseSalesTargetByMonth = null;
				List<UserWiseSalesTargetDTO> userTarget = userWiseSalesTargetByUserName.get(userName);
				UserWiseSalesTargetDTO userWiseSalesTargetDTO = null;
				if (userTarget != null) {
					userWiseSalesTargetByMonth = userTarget.stream()
							.collect(Collectors.groupingBy(a -> a.getFromDate().getMonth().toString()));
					if (userWiseSalesTargetByMonth != null && userWiseSalesTargetByMonth.get(month) != null) {
						userWiseSalesTargetDTO = userWiseSalesTargetByMonth.get(month).get(0);
					}
				}
				// no target saved, add a default one
				if (userWiseSalesTargetDTO == null) {
					userWiseSalesTargetDTO = new UserWiseSalesTargetDTO();
					userWiseSalesTargetDTO.setUserName(userName);
					userWiseSalesTargetDTO.setUserPid(employee.getPid());
					userWiseSalesTargetDTO.setVolume(0);
					userWiseSalesTargetDTO.setAmount(0);
				}

				userWiseSalesTargetDTO.setAchievedAmount(getAchievedAmount(employee.getPid(), monthDate));

				userWiseSalesTargetList.add(userWiseSalesTargetDTO);
			}
			userWiseSalesTargetMap.put(userName, userWiseSalesTargetList);
		}
		List<String> monthList = new ArrayList<>();
		for (LocalDate monthDate : monthsBetweenDates) {
			monthList.add(monthDate.getMonth().toString());
		}
		userWiseSalesPerformaceDTO.setUserWiseSalesTargets(userWiseSalesTargetMap);
		userWiseSalesPerformaceDTO.setMonthList(monthList);
		/*
		 * productGroupLocationPerformanceDTO.setSalesTargetGroupUserTargets(
		 * productGroupLocationTargetMap);
		 */
		return userWiseSalesPerformaceDTO;
		/*
		 * } return null;
		 */
	}

	private double getAchievedAmount(String employeePid, LocalDate initialDate) {

		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());

		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfilePid(employeePid);

		if (locations.size() == 0) {
			return 0;
		}

		Set<Long> locationIds = new HashSet<>();
		for (Location location : locations) {
			locationIds.add(location.getId());
		}

		Set<Long> accountProfileIds = locationAccountProfileRepository
				.findAccountProfileIdsByUserLocationIdsIn(locationIds);

		List<PrimarySecondaryDocument> primarySecDoc = new ArrayList<>();
		primarySecDoc = primarySecondaryDocumentRepository.findByVoucherTypeAndCompany(VoucherType.PRIMARY_SALES,
				SecurityUtils.getCurrentUsersCompanyId());
		if (primarySecDoc.isEmpty()) {
			log.debug("........No PrimarySecondaryDocument configuration Available...........");
			return 0;
		}
		List<Long> documentIds = primarySecDoc.stream().map(psd -> psd.getDocument().getId())
				.collect(Collectors.toList());

		Double achievedAmount = 0D;
		if (!accountProfileIds.isEmpty()) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_167" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get Id by AccountProfileAndDocumentDateBetween";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Set<Long> ivHeaderIds = inventoryVoucherHeaderRepository.findIdByAccountProfileAndDocumentDateBetween(
					accountProfileIds, documentIds, start.atTime(0, 0), end.atTime(23, 59));
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

			if (!ivHeaderIds.isEmpty()) {
				achievedAmount = inventoryVoucherDetailRepository.sumOfAmountByHeaderIds(ivHeaderIds);
			}
		}
		return achievedAmount == null ? 0 : achievedAmount;

	}

	private List<LocalDate> monthsDateBetweenDates(LocalDate start, LocalDate end) {
		List<LocalDate> ret = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
			ret.add(date);
		}
		return ret;
	}

}
