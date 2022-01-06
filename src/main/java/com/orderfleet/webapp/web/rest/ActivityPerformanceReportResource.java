package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityUserTarget;
import com.orderfleet.webapp.domain.UserActivity;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.repository.ActivityTypeActivityRepository;
import com.orderfleet.webapp.repository.ActivityTypeRepository;
import com.orderfleet.webapp.repository.ActivityUserTargetRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.ActivityPerformaceDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityUserTargetDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityUserTargetMapper;

/**
 * Web controller for managing Marketing Activity.
 * 
 * @author Muhammed Riyas T
 * @since October 06, 2016
 */
@Controller
@RequestMapping("/web")
public class ActivityPerformanceReportResource {

	private final Logger log = LoggerFactory.getLogger(ActivityPerformanceReportResource.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private final UserActivityRepository userActivityRepository;

	private final ActivityUserTargetRepository activityUserTargetRepository;

	private final ActivityUserTargetMapper activityUserTargetMapper;

	private final ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	private final ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private ActivityTypeRepository activityTypeRepository;

	@Inject
	private ActivityTypeActivityRepository activityTypeActivityRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	public ActivityPerformanceReportResource(UserActivityRepository userActivityRepository,
			ActivityUserTargetRepository activityUserTargetRepository,
			ActivityUserTargetMapper activityUserTargetMapper,
			ExecutiveTaskExecutionRepository executiveTaskExecutionRepository,
			ExecutiveTaskPlanRepository executiveTaskPlanRepository) {
		super();
		this.userActivityRepository = userActivityRepository;
		this.activityUserTargetRepository = activityUserTargetRepository;
		this.activityUserTargetMapper = activityUserTargetMapper;
		this.executiveTaskExecutionRepository = executiveTaskExecutionRepository;
		this.executiveTaskPlanRepository = executiveTaskPlanRepository;
	}

	/**
	 * GET /activity-performance-report
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of users in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/activity-performance-report", method = RequestMethod.GET)
	public String getActivityPerformanceReport(Model model) {
		log.debug("Web request to get a page of Marketing Activity Performance");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_TYPE_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		model.addAttribute("activityTypes", activityTypeRepository.findAllByCompanyId());
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

		return "company/activityPerformanceReport";
	}

	@RequestMapping(value = "/activity-performance-report/load-data", method = RequestMethod.GET)
	@ResponseBody
	public ActivityPerformaceDTO performanceTargets(@RequestParam("employeePid") String employeePid,
			@RequestParam("activityTypePid") String activityTypePid,
			@RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		log.info("Rest Request to load activity performance for the user : {} , and date between {}, {}", employeePid,
				fromDate, toDate);

		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}

		List<ActivityUserTarget> activityUserTargets = getEveryActivityUserTarget(userPid, fromDate, toDate,
				activityTypePid);
		if (!activityUserTargets.isEmpty()) {
			ActivityPerformaceDTO activityPerformance = new ActivityPerformaceDTO();

			// Get months date between the date
			List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);

			List<ActivityUserTargetDTO> activityUserTargetDTOs = activityUserTargetMapper
					.activityUserTargetsToActivityUserTargetDTOs(activityUserTargets);
			// group by activity name
			Map<String, List<ActivityUserTargetDTO>> activityUserTargetByActivityName = activityUserTargetDTOs
					.parallelStream().collect(Collectors.groupingBy(ActivityUserTargetDTO::getActivityName));
			// actual activity user target
			Map<String, List<ActivityUserTargetDTO>> activityUserTargetMap = new HashMap<>();
			activityUserTargetByActivityName.forEach((key, value) -> {
				List<ActivityUserTargetDTO> activityTargetList = new ArrayList<>();
				for (LocalDate monthDate : monthsBetweenDates) {
					String month = monthDate.getMonth().toString();
					// group by month, one month has only one user-target
					Map<String, List<ActivityUserTargetDTO>> activityUserTargetByMonth = value.stream()
							.collect(Collectors.groupingBy(a -> a.getStartDate().getMonth().toString()));
					if (activityUserTargetByMonth.get(month) != null) {
						ActivityUserTargetDTO activityUserTargetDTO = activityUserTargetByMonth.get(month).get(0);
						activityUserTargetDTO.setAchivedNumber(getAchievedNumber(activityUserTargetDTO, monthDate));
						activityTargetList.add(activityUserTargetDTO);
					} else {
						ActivityUserTargetDTO defaultTarget = new ActivityUserTargetDTO();
						defaultTarget.setTargetNumber(0L);
						defaultTarget.setAchivedNumber(0L);
						activityTargetList.add(defaultTarget);
					}
				}
				activityUserTargetMap.put(key, activityTargetList);
			});
			List<String> monthList = new ArrayList<>();
			for (LocalDate monthDate : monthsBetweenDates) {
				monthList.add(monthDate.getMonth().toString());
			}
			activityPerformance.setMonthList(monthList);
			activityPerformance.setActivityUserTargets(activityUserTargetMap);
			return activityPerformance;
		}
		return null;
	}

	private long getAchievedNumber(ActivityUserTargetDTO activityUserTargetDTO, LocalDate initialDate) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		Long achieved = executiveTaskExecutionRepository.countByActivityPidAndUserPidAndActivityStatusNotAndDateBetween(
				activityUserTargetDTO.getActivityPid(), activityUserTargetDTO.getUserPid(), ActivityStatus.REJECTED,
				start.atTime(0, 0), end.atTime(23, 59));
		return achieved == null ? 0 : achieved;
	}

	private List<ActivityUserTarget> getEveryActivityUserTarget(String userPid, LocalDate fromDate, LocalDate toDate,
			String activityTypePid) {
		List<UserActivity> userActivities = new ArrayList<>();
		List<Activity> activities = new ArrayList<>();
		List<ActivityUserTarget> allActivityUserTargets = new ArrayList<>();
		if ("no".equalsIgnoreCase(activityTypePid)) {
			userActivities = userActivityRepository.findByUserPid(userPid);
	        DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AUT_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by user pid and date";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			allActivityUserTargets = activityUserTargetRepository
					.findByUserPidAndStartDateGreaterThanEqualAndEndDateLessThanEqual(userPid, fromDate, toDate);
	

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
			+ description);} else {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "ACTIVITY_TYPE_ACT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get activity by activityTypePid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			activities = activityTypeActivityRepository.findActivityByActivityTypePid(activityTypePid);
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

		if (!activities.isEmpty()) {
			userActivities = userActivityRepository.findByUserPidAndActivitiesInAndActivatedTrue(userPid, activities);
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AUT_QUERY_106" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get by user pid activityIn and date";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			allActivityUserTargets = activityUserTargetRepository
					.findByUserPidAndActivityInAndStartDateGreaterThanEqualAndEndDateLessThanEqual(userPid, activities,
							fromDate, toDate);
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

		if (!userActivities.isEmpty()) {
			List<ActivityUserTarget> activityUserTargets = new ArrayList<>();
			List<LocalDate> monthsBetweenDates = monthsDateBetweenDates(fromDate, toDate);
			for (ActivityUserTarget acUserTarget : allActivityUserTargets) {
				for (LocalDate localDate : monthsBetweenDates) {
					LocalDate lDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
					 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String id = "AUT_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String description ="get by user pid activityPid and date";
						LocalDateTime startLCTime = LocalDateTime.now();
						String startTime = startLCTime.format(DATE_TIME_FORMAT);
						String startDate = startLCTime.format(DATE_FORMAT);
						logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					List<ActivityUserTarget> activityUserTargetsByMonth = activityUserTargetRepository
							.findByUserPidAndActivityPidAndStartDateGreaterThanEqualAndEndDateLessThanEqual(userPid,
									acUserTarget.getActivity().getPid(), localDate, lDate);
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
					if (activityUserTargetsByMonth.isEmpty()) {
						ActivityUserTarget activityUserTarget = new ActivityUserTarget();
						activityUserTarget.setActivity(acUserTarget.getActivity());
						activityUserTarget.setStartDate(localDate);
						activityUserTarget.setEndDate(localDate);
						activityUserTarget.setUser(acUserTarget.getUser());
						activityUserTarget.setTargetNumber(0L);
						activityUserTargets.add(activityUserTarget);
					} else {
						activityUserTargets.addAll(activityUserTargetsByMonth);
					}
				}
			}

			for (UserActivity userActivity : userActivities) {
				boolean activityExist = activityUserTargets.parallelStream()
						.anyMatch(a -> a.getActivity().getName().equals(userActivity.getActivity().getName()));
				if (!activityExist) {

					// does not contain the activity and target get from
					// executive Task Plan
					if (userActivity.getActivity().getTargetDisplayOnDayplan()) {

						for (LocalDate monthDate : monthsBetweenDates) {
							ActivityUserTarget activityUserTarget = new ActivityUserTarget();
							activityUserTarget.setActivity(userActivity.getActivity());
							activityUserTarget.setStartDate(monthDate);
							activityUserTarget.setEndDate(monthDate);
							activityUserTarget.setUser(userActivity.getUser());
							activityUserTarget.setTargetNumber(
									getTargetNumber(monthDate, userPid, userActivity.getActivity().getPid()));
							activityUserTargets.add(activityUserTarget);
						}
					} else {
						// change code
						// add for loop for month
						for (LocalDate monthDate : monthsBetweenDates) {
							// does not contain the activity
							ActivityUserTarget activityUserTarget = new ActivityUserTarget();
							activityUserTarget.setActivity(userActivity.getActivity());
							activityUserTarget.setStartDate(monthDate);
							activityUserTarget.setEndDate(monthDate);
							activityUserTarget.setUser(userActivity.getUser());
							activityUserTarget.setTargetNumber(0L);
							activityUserTargets.add(activityUserTarget);
						}
					}
				}
			}
			return activityUserTargets;
		}
		return Collections.emptyList();
	}

	private List<LocalDate> monthsDateBetweenDates(LocalDate start, LocalDate end) {
		List<LocalDate> ret = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
			ret.add(date);
		}
		return ret;
	}

	private long getTargetNumber(LocalDate initialDate, String userPid, String activityPid) {
		LocalDate start = initialDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate end = initialDate.with(TemporalAdjusters.lastDayOfMonth());
		Long achieved = executiveTaskPlanRepository.countByUserPidAndPlannedDateBetweenAndActivityPid(userPid,
				start.atTime(0, 0), end.atTime(23, 59), activityPid);
		return achieved == null ? 0 : achieved;
	}

}
