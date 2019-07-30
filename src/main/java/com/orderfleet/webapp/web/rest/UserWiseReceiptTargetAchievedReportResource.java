package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.UserWiseReceiptTarget;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.UserWiseReceiptTargetRepository;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseReceiptPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseReceiptTargetDTO;

/**
 * Web controller for Sales Target v/s achieved report
 * 
 * @author Muhammed Riyas T
 * @since October 06, 2016
 */
@Controller
@RequestMapping("/web")
public class UserWiseReceiptTargetAchievedReportResource {

	private final Logger log = LoggerFactory.getLogger(UserWiseReceiptTargetAchievedReportResource.class);

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private EmployeeProfileRepository employeeRepository;

	@Inject
	private UserWiseReceiptTargetRepository userWiseReceiptTargetRepository;

	@Inject
	private ActivityService activityService;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private DocumentService documentService;

	/**
	 * GET /sales-target-vs-achieved-report
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/user-wise-receipt-target-vs-achieved-report", method = RequestMethod.GET)
	public String getLocationWiseTargetAchievedReport(Model model) {
		log.debug("Web request to get a page of Marketing Activity Performance");
		model.addAttribute("employees", employeeRepository.findAllByCompanyAndDeativatedEmployeeProfile(true));
		return "company/userWiseReceiptTargetAchievedReport";
	}

	@RequestMapping(value = "/user-wise-receipt-target-vs-achieved-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public UserWiseReceiptPerformaceDTO performanceTargets(
			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		log.info("Rest Request to load user wise receipt target vs achieved report :  date between {}, {}", fromDate,
				toDate);

		// filter based on product group
		List<EmployeeProfile> employees = employeeRepository.findAllByCompanyAndDeativatedEmployeeProfile(true);
		List<String> employeePids = employees.stream().map(a -> a.getPid()).collect(Collectors.toList());
		List<UserWiseReceiptTarget> userWiseReceiptTargets = userWiseReceiptTargetRepository
				.findByEmployeeProfilePidInAndFromDateGreaterThanEqualAndToDateLessThanEqual(employeePids, fromDate,
						toDate);
		// if (!productGroupLocationTargets.isEmpty()) {
		UserWiseReceiptPerformaceDTO userWiseReceiptPerformaceDTO = new UserWiseReceiptPerformaceDTO();
		// Get months date between the date
		List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);
		List<UserWiseReceiptTargetDTO> userWiseReceiptTargetDTOs = new ArrayList<>();

		for (UserWiseReceiptTarget userWiseReceiptTarget : userWiseReceiptTargets) {
			userWiseReceiptTargetDTOs.add(new UserWiseReceiptTargetDTO(userWiseReceiptTarget));
		}
		// group by SalesTargetGroupName
		Map<String, List<UserWiseReceiptTargetDTO>> userWiseReceiptTargetByUserName = userWiseReceiptTargetDTOs
				.parallelStream().collect(Collectors.groupingBy(UserWiseReceiptTargetDTO::getUserName));

		// actual sales user target
		Map<String, List<UserWiseReceiptTargetDTO>> userWiseReceiptTargetMap = new HashMap<>();
		for (EmployeeProfile employee : employees) {
			String userName = employee.getName();
			List<UserWiseReceiptTargetDTO> userWiseReceiptTargetList = new ArrayList<>();
			for (LocalDate monthDate : monthsBetweenDates) {
				String month = monthDate.getMonth().toString();
				// group by month, one month has only one
				// user-target
				Map<String, List<UserWiseReceiptTargetDTO>> userWiseReceiptTargetByMonth = null;
				List<UserWiseReceiptTargetDTO> userTarget = userWiseReceiptTargetByUserName.get(userName);
				UserWiseReceiptTargetDTO userWiseReceiptTargetDTO = null;
				if (userTarget != null) {
					userWiseReceiptTargetByMonth = userTarget.stream()
							.collect(Collectors.groupingBy(a -> a.getFromDate().getMonth().toString()));
					if (userWiseReceiptTargetByMonth != null && userWiseReceiptTargetByMonth.get(month) != null) {
						userWiseReceiptTargetDTO = userWiseReceiptTargetByMonth.get(month).get(0);
					}
				}
				// no target saved, add a default one
				if (userWiseReceiptTargetDTO == null) {
					userWiseReceiptTargetDTO = new UserWiseReceiptTargetDTO();
					userWiseReceiptTargetDTO.setUserName(userName);
					userWiseReceiptTargetDTO.setUserPid(employee.getPid());
					userWiseReceiptTargetDTO.setVolume(0);
					userWiseReceiptTargetDTO.setAmount(0);
				}

				userWiseReceiptTargetDTO.setAchievedAmount(getAchievedAmount(employee.getPid(), monthDate));

				userWiseReceiptTargetList.add(userWiseReceiptTargetDTO);
			}
			userWiseReceiptTargetMap.put(userName, userWiseReceiptTargetList);
		}
		List<String> monthList = new ArrayList<>();
		for (LocalDate monthDate : monthsBetweenDates) {
			monthList.add(monthDate.getMonth().toString());
		}
		userWiseReceiptPerformaceDTO.setUserWiseReceiptTargets(userWiseReceiptTargetMap);
		userWiseReceiptPerformaceDTO.setMonthList(monthList);
		/*
		 * productGroupLocationPerformanceDTO.setSalesTargetGroupUserTargets(
		 * productGroupLocationTargetMap);
		 */
		return userWiseReceiptPerformaceDTO;
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

		ActivityDTO activityDto = this.getActivityByName("Receipt Data Transfer");
		DocumentDTO documentDto = this.getDocumentByName("receipts from tally");

		if (activityDto == null && documentDto == null) {
			return 0;
		}

		Double achievedAmount = 0D;
		if (!accountProfileIds.isEmpty()) {

			Set<Long> avHeaderIds = accountingVoucherHeaderRepository
					.findIdByAccountProfileAndDocumentAndActivityDateBetween(accountProfileIds, documentDto.getPid(),
							activityDto.getPid(), start.atTime(0, 0), end.atTime(23, 59));

			if (!avHeaderIds.isEmpty()) {

				achievedAmount = accountingVoucherHeaderRepository
						.totalAmountByAccountProfileAndDocumentAndActivityDateBetween(accountProfileIds,
								documentDto.getPid(), activityDto.getPid(), start.atTime(0, 0), end.atTime(23, 59));

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

	private ActivityDTO getActivityByName(String name) {
		Optional<ActivityDTO> activityDTO = activityService.findByName(name);
		if (activityDTO.isPresent()) {
			return activityDTO.get();
		}
		ActivityDTO activityDto = new ActivityDTO();
		activityDto.setName(name);
		activityDto.setActivated(true);
		activityDto.setHasDefaultAccount(false);
		activityDto.setDescription("Used to send data " + name + " from tally");
		return activityService.save(activityDto);
	}

	private DocumentDTO getDocumentByName(String name) {
		Optional<DocumentDTO> optionalDocument = documentService.findByName(name);
		if (optionalDocument.isPresent()) {
			return optionalDocument.get();
		}
		DocumentDTO documentDto = new DocumentDTO();
		documentDto.setName(name);
		documentDto.setDocumentType(DocumentType.ACCOUNTING_VOUCHER);
		documentDto.setDocumentPrefix("Receipts");
		documentDto.setDescription("used to send data " + name + " from tally");
		return documentService.save(documentDto);
	}

}
