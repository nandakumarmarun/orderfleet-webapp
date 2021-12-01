package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserActivity;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDetailView;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionView;

@Controller
@RequestMapping("/web")
public class ActivityTimeSpendResource {

	private final Logger log = LoggerFactory.getLogger(ExecutiveTaskExecutionResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private ActivityService activityService;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private UserActivityRepository userActivityRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@RequestMapping(value = "/activity-time-spend", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllExecutiveActivityTimeSpend(Model model) throws URISyntaxException {
		log.debug("Web request to get  executive task executions");
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
			model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
			model.addAttribute("activities", activityService.findAllByCompanyAndDeactivatedActivity(true));
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
			model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
			List<UserActivity> activities = userActivityRepository.findByUserIsCurrentUser();
			List<Activity> activitys = new ArrayList<>();
			for (UserActivity userActivity : activities) {
				activitys.add(userActivity.getActivity());
			}
			model.addAttribute("activities", activitys);
		}
		return "company/activityTimeSpends";
	}

	@RequestMapping(value = "/activity-time-spend/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ExecutiveTaskExecutionView>> filterExecutiveTaskExecutions(
			@RequestParam("employeePid") String employeePid, @RequestParam("activityPid") String activityPid,
			@RequestParam("accountPid") String accountPid, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter executive task executions");
		List<ExecutiveTaskExecutionView> executiveTaskExecutions = new ArrayList<ExecutiveTaskExecutionView>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterData(employeePid, activityPid, accountPid, LocalDate.now(),
					LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterData(employeePid, activityPid, accountPid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			executiveTaskExecutions = getFilterData(employeePid, activityPid, accountPid, weekStartDate,
					LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			executiveTaskExecutions = getFilterData(employeePid, activityPid, accountPid, monthStartDate,
					LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, activityPid, accountPid, fromDateTime, toFateTime);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, activityPid, accountPid, fromDateTime, fromDateTime);
		}
		return new ResponseEntity<>(executiveTaskExecutions, HttpStatus.OK);
	}

	private List<ExecutiveTaskExecutionView> getFilterData(String employeePid, String activityPid, String accountPid,
			LocalDate fDate, LocalDate tDate) {

		String userPid = "no";
		if (!employeePid.equals("no") && !employeePid.equals("Dashboard Employee")) {
			Optional<EmployeeProfileDTO> opEmployee = employeeProfileService.findOneByPid(employeePid);
			if (opEmployee.isPresent()) {
				userPid = opEmployee.get().getUserPid();
			}
		}

		List<Long> userIds = getUserIdsUnderCurrentUser(employeePid);

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<>();
		if (userPid.equals("no") && activityPid.equals("no") && accountPid.equals("no")) {
			// user under current user
			if (userIds.isEmpty()) {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByCompanyIdAndDateBetweenOrderByDateDesc(fromDate, toDate);
			} else {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByUserIdInAndDateBetweenOrderByDateDesc(userIds, fromDate, toDate);
			}
		} else if (!userPid.equals("no") && !activityPid.equals("no") && !accountPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidActivityPidAccountPidAndDateBetweenOrderByDateDesc(userPid, activityPid,
							accountPid, fromDate, toDate);
		} else if (!userPid.equals("no") && activityPid.equals("no") && accountPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidAndDateBetweenOrderByDateDesc(userPid, fromDate, toDate);
		} else if (!activityPid.equals("no") && userPid.equals("no") && accountPid.equals("no")) {
			// user under current user
			if (userIds.isEmpty()) {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByCompanyIdActivityPidAndDateBetweenOrderByDateDesc(activityPid, fromDate, toDate);
			} else {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByUserIdInActivityPidAndDateBetweenOrderByDateDesc(userIds, activityPid, fromDate,
								toDate);
			}
		} else if (!accountPid.equals("no") && userPid.equals("no") && activityPid.equals("no")) {
			// user under current user
			if (userIds.isEmpty()) {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByCompanyIdAccountPidAndDateBetweenOrderByDateDesc(accountPid, fromDate, toDate);
			} else {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByUserIdInAccountPidAndDateBetweenOrderByDateDesc(userIds, accountPid, fromDate,
								toDate);
			}
		} else if (!userPid.equals("no") && !activityPid.equals("no") && accountPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidActivityPidAndDateBetweenOrderByDateDesc(userPid, activityPid, fromDate,
							toDate);
		} else if (!userPid.equals("no") && !accountPid.equals("no") && activityPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByDateDesc(userPid, accountPid, fromDate,
							toDate);
		} else if (!accountPid.equals("no") && !activityPid.equals("no") && userPid.equals("no")) {
			// user under current user
			if (userIds.isEmpty()) {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByCompanyIdActivityPidAccountPidAndDateBetweenOrderByDateDesc(activityPid, accountPid,
								fromDate, toDate);
			} else {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByUserIdInActivityPidAccountPidAndDateBetweenOrderByDateDesc(userIds, activityPid,
								accountPid, fromDate, toDate);
			}
		}
		List<ExecutiveTaskExecutionView> executiveTaskExecutionViews = new ArrayList<>();
		for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
			ExecutiveTaskExecutionView executiveTaskExecutionView = new ExecutiveTaskExecutionView(
					executiveTaskExecution);
			EmployeeProfileDTO employeeProfileDTO = employeeProfileService
					.findEmployeeProfileByUserLogin(executiveTaskExecution.getUser().getLogin());
			if (employeeProfileDTO != null) {
				executiveTaskExecutionView.setEmployeeName(employeeProfileDTO.getName());
				String timeSpend = findTimeSpend(executiveTaskExecution.getStartTime(),
						executiveTaskExecution.getEndTime());
				executiveTaskExecutionView.setTimeSpend(timeSpend);
				List<ExecutiveTaskExecutionDetailView> executiveTaskExecutionDetailViews = new ArrayList<ExecutiveTaskExecutionDetailView>();
			
				List<Object[]> inventoryVouchers = new ArrayList<>();
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				
				String id = "INV_QUERY_116" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description = "get by executive task execution Id";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				inventoryVouchers = inventoryVoucherHeaderRepository
						.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
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
				for (Object[] obj : inventoryVouchers) {
					ExecutiveTaskExecutionDetailView executiveTaskExecutionDetailView = new ExecutiveTaskExecutionDetailView(
							obj[0].toString(), obj[1].toString(), Double.valueOf(obj[2].toString()), obj[3].toString());
					executiveTaskExecutionDetailView.setDocumentVolume(Double.valueOf(obj[4].toString()));
					executiveTaskExecutionDetailViews.add(executiveTaskExecutionDetailView);
				}

				 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id1 = "ACC_QUERY_115" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description1 ="get AccVoucher By ExecutiveTaskExecutionId";
					LocalDateTime startLCTime1 = LocalDateTime.now();
					String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
					String startDate1 = startLCTime1.format(DATE_FORMAT1);
					logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
				List<Object[]> accountingVouchers = new ArrayList<>();
				accountingVouchers = accountingVoucherHeaderRepository
						.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
				 String flag1 = "Normal";
					LocalDateTime endLCTime1 = LocalDateTime.now();
					String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
					String endDate1 = startLCTime1.format(DATE_FORMAT1);
					Duration duration1 = Duration.between(startLCTime1, endLCTime1);
					long minutes1 = duration1.toMinutes();
					if (minutes1 <= 1 && minutes1 >= 0) {
						flag1 = "Fast";
					}
					if (minutes1 > 1 && minutes1 <= 2) {
						flag1 = "Normal";
					}
					if (minutes1 > 2 && minutes1 <= 10) {
						flag1 = "Slow";
					}
					if (minutes1 > 10) {
						flag1 = "Dead Slow";
					}
			                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
							+ description1);

				
				for (Object[] obj : accountingVouchers) {
					executiveTaskExecutionDetailViews.add(new ExecutiveTaskExecutionDetailView(obj[0].toString(),
							obj[1].toString(), Double.valueOf(obj[2].toString()), obj[3].toString()));
				}
				DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id11 = "DYN_QUERY_110" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description11 ="get all documents by Executive Task executionId";
				LocalDateTime startLCTime11 = LocalDateTime.now();
				String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
				String startDate11 = startLCTime11.format(DATE_FORMAT11);
				logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
				List<Object[]> dynamicDocuments = new ArrayList<>();
				dynamicDocuments = dynamicDocumentHeaderRepository
						.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
				String flag11 = "Normal";
				LocalDateTime endLCTime11 = LocalDateTime.now();
				String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
				String endDate11 = startLCTime11.format(DATE_FORMAT11);
				Duration duration11 = Duration.between(startLCTime11, endLCTime11);
				long minutes11 = duration11.toMinutes();
				if (minutes11 <= 1 && minutes11 >= 0) {
					flag11 = "Fast";
				}
				if (minutes11 > 1 && minutes11 <= 2) {
					flag11 = "Normal";
				}
				if (minutes11 > 2 && minutes11 <= 10) {
					flag11 = "Slow";
				}
				if (minutes11 > 10) {
					flag11 = "Dead Slow";
				}
		                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
						+ description11);
				for (Object[] obj : dynamicDocuments) {
					boolean imageFound = false;
					// check image saved
					 DateTimeFormatter DATE_TIME_FORMATS = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMATS = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String ids = "FORM_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String descriptions ="get the form by checking exists by header pid";
						LocalDateTime startLCTimes = LocalDateTime.now();
						String startTimes = startLCTimes.format(DATE_TIME_FORMATS);
						String startDates= startLCTimes.format(DATE_FORMATS);
						logger.info(ids + "," + startDates + "," + startTimes + ",_ ,0 ,START,_," + descriptions);
					if (filledFormRepository.existsByHeaderPidIfFiles(obj[0].toString()))
					{
						
						imageFound = true;
					}
					 String flags = "Normal";
						LocalDateTime endLCTimes = LocalDateTime.now();
						String endTimes= endLCTimes.format(DATE_TIME_FORMATS);
						String endDates = startLCTimes.format(DATE_FORMATS);
						Duration durations = Duration.between(startLCTimes, endLCTimes);
						long minute = durations.toMinutes();
						if (minute <= 1 && minute >= 0) {
							flags = "Fast";
						}
						if (minute > 1 && minute <= 2) {
							flags = "Normal";
						}
						if (minute > 2 && minute <= 10) {
							flags = "Slow";
						}
						if (minute > 10) {
							flags = "Dead Slow";
						}
				                logger.info(ids + "," + endDates + "," + startTimes + "," + endTimes + "," + minute + ",END," + flags + ","
								+ descriptions);

					executiveTaskExecutionDetailViews.add(new ExecutiveTaskExecutionDetailView(obj[0].toString(),
							obj[1].toString(), obj[2].toString(), imageFound));
				}
				// if condition for document wise filter
				if (!executiveTaskExecutionDetailViews.isEmpty()) {
					executiveTaskExecutionView.setExecutiveTaskExecutionDetailViews(executiveTaskExecutionDetailViews);
					executiveTaskExecutionViews.add(executiveTaskExecutionView);
				}
			}
		}
		return executiveTaskExecutionViews;
	}

	private List<Long> getUserIdsUnderCurrentUser(String employeePid) {
		List<Long> userIds = new ArrayList<>();

		if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (employeePid.equals("Dashboard Employee")) {
				List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
				List<Long> dashboardUserIds = dashboardUsers.stream().map(a -> a.getId()).collect(Collectors.toList());

				Set<Long> uniqueIds = new HashSet<>();

				if (!dashboardUserIds.isEmpty()) {
					for (Long uid : userIds) {
						for (Long sid : dashboardUserIds) {
							if (uid.equals(sid)) {
								uniqueIds.add(sid);
							}
						}
					}
				}
				if (!uniqueIds.isEmpty()) {
					userIds = new ArrayList<>(uniqueIds);
				}

			}
		} else {
			userIds = Collections.emptyList();
		}
		return userIds;
	}

	public String findTimeSpend(LocalDateTime startTime, LocalDateTime endTime) {
		long hours = 0;
		long minutes = 0;
		long seconds = 0;
		if (startTime != null && endTime != null) {
			long years = startTime.until(endTime, ChronoUnit.YEARS);
			startTime = startTime.plusYears(years);

			long months = startTime.until(endTime, ChronoUnit.MONTHS);
			startTime = startTime.plusMonths(months);

			long days = startTime.until(endTime, ChronoUnit.DAYS);
			startTime = startTime.plusDays(days);
			hours = startTime.until(endTime, ChronoUnit.HOURS);
			startTime = startTime.plusHours(hours);

			minutes = startTime.until(endTime, ChronoUnit.MINUTES);
			startTime = startTime.plusMinutes(minutes);

			seconds = startTime.until(endTime, ChronoUnit.SECONDS);
		}
		return hours + " : " + minutes + " : " + seconds;

	}
}
