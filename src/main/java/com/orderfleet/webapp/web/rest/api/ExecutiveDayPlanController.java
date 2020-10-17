package com.orderfleet.webapp.web.rest.api;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.domain.RootPlanSubgroupApprove;
import com.orderfleet.webapp.domain.enums.AttendanceStatus;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskGroupPlanRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.RootPlanSubgroupApproveRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DashboardNotificationService;
import com.orderfleet.webapp.service.ExecutiveDayPlanService;
import com.orderfleet.webapp.service.ExecutiveTaskPlanService;
import com.orderfleet.webapp.service.RootPlanDetailService;
import com.orderfleet.webapp.service.RootPlanHeaderService;
import com.orderfleet.webapp.web.rest.api.dto.DayPlanResponse;
import com.orderfleet.webapp.web.rest.api.dto.TaskGroupResponse;
import com.orderfleet.webapp.web.rest.dto.ExecutiveDayPlanDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskPlanDTO;

/**
 * REST controller for managing ExecutiveTaskPlan.
 * 
 * @author Shaheer
 * @since July 12, 2016
 */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExecutiveDayPlanController {

	private final Logger log = LoggerFactory.getLogger(ExecutiveDayPlanController.class);

	private final ExecutiveDayPlanService executiveDayPlanService;

	private final ExecutiveTaskGroupPlanRepository executiveTaskGroupPlanRepository;

	private final ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	private final DashboardUserRepository dashboardUserRepository;

	private final DashboardNotificationService dashboardNotificationService;

	private final SimpMessagingTemplate simpMessagingTemplate;

	@Inject
	private RootPlanHeaderService rootPlanHeaderService;

	@Inject
	private ExecutiveTaskPlanService executiveTaskPlanService;

	@Inject
	private RootPlanDetailService rootPlanDetailService;

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private RootPlanSubgroupApproveRepository rootPlanSubgroupApproveRepository;

	public ExecutiveDayPlanController(ExecutiveDayPlanService executiveDayPlanService,
			ExecutiveTaskGroupPlanRepository executiveTaskGroupPlanRepository,
			ExecutiveTaskPlanRepository executiveTaskPlanRepository, DashboardUserRepository dashboardUserRepository,
			DashboardNotificationService dashboardNotificationService, SimpMessagingTemplate simpMessagingTemplate) {
		super();
		this.executiveDayPlanService = executiveDayPlanService;
		this.executiveTaskGroupPlanRepository = executiveTaskGroupPlanRepository;
		this.executiveTaskPlanRepository = executiveTaskPlanRepository;
		this.dashboardUserRepository = dashboardUserRepository;
		this.dashboardNotificationService = dashboardNotificationService;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	/**
	 * POST /executive-day-plan : Create a new executiveDayPlan.
	 * 
	 * @param executiveDayPlan
	 *            the executiveTaskPlanDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new executiveDAyPlanDTO
	 */
	@Timed
	@RequestMapping(value = "/executive-day-plan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DayPlanResponse> createExecutiveTaskPlan(@RequestBody ExecutiveDayPlanDTO executiveDayPlan) {
		log.debug("Rest request to save ExecutiveDayPlan : {}", executiveDayPlan);
		DayPlanResponse dayPlanResponse = new DayPlanResponse();
		try {
			dayPlanResponse = executiveDayPlanService.saveExecutiveDayPlan(executiveDayPlan);
			// set achieved count(ActivityGroup)
			for (TaskGroupResponse taskGroupResponse : dayPlanResponse.getTaskGroupResponses()) {
				if (taskGroupResponse.getStartDate() != null) {
					Long achieved = executiveTaskGroupPlanRepository
							.countByUserPidAndTaskGroupActivityGroupPidAndPlannedDateBetween(
									dayPlanResponse.getUserPid(), taskGroupResponse.getActivityGroupPid(),
									taskGroupResponse.getStartDate().atTime(0, 0),
									taskGroupResponse.getEndDate().atTime(23, 59));
					taskGroupResponse.setAchieved(achieved);
				}
			}
			dayPlanResponse.setStatus("Success");
			dayPlanResponse.setMessage("Day plan submitted successfully...");
		} catch (Exception e) {
			dayPlanResponse.setStatus("Error");
			dayPlanResponse.setMessage(e.getMessage());
			log.warn("Exception in creating task plan", e);
		}
		return new ResponseEntity<>(dayPlanResponse, HttpStatus.CREATED);
	}

	/**
	 * GET /executive-day-plan : Users executive-day-plan.
	 * 
	 * This will give current user executive-day-plan to mobile devices up to
	 * current date and change the status to pending If provided custom dates
	 * give plan between dates Same as Reports -> Day plan in web, don't change
	 * status
	 * 
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new executiveDayPlanDTO
	 */
	@Timed
	@RequestMapping(value = "/executive-day-plan", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<List<ExecutiveTaskPlanDTO>> getExecutiveTaskPlan(
			@RequestParam(required = false, name = "startDate") LocalDate startDate,
			@RequestParam(required = false, name = "endDate") LocalDate endDate,
			@RequestParam(required = false, name = "fromDayPlan") boolean fromDayPlan) {
		List<ExecutiveTaskPlan> executiveTaskPlanList;
		String login = SecurityUtils.getCurrentUserLogin();
		// check day plan already assigned for this user
		LocalDate currentDate = LocalDate.now();
		executiveTaskPlanList = executiveTaskPlanRepository.findByUserLoginAndPlannedDateBetweenOrderByIdAsc(login,
				currentDate.atTime(0, 0), currentDate.atTime(23, 59));
		// if not, check and convert root plan to day plan and download
		if (executiveTaskPlanList.isEmpty() && fromDayPlan) {
			// check attendance already marked with subgroup - to solve an issue
			// in modern (Route Plan auto download)
			List<Attendance> attendanceList = attendanceRepository
					.findByCompanyIdUserLoginAndDateBetweenAndAttendanceStatus(login, AttendanceStatus.PRESENT,
							currentDate.atTime(0, 0), currentDate.atTime(23, 59));
			if (!attendanceList.isEmpty()) {
				Attendance attendance = attendanceList.stream().max(Comparator.comparing(Attendance::getPlannedDate)).get();
				if (attendance.getAttendanceStatusSubgroup() != null) {
					List<RootPlanSubgroupApprove> optionalRootPlanSubGroupApproval = rootPlanSubgroupApproveRepository
							.findByUserLoginAndAttendanceStatusSubgroupId(SecurityUtils.getCurrentUserLogin(),
									attendance.getAttendanceStatusSubgroup().getId());
					if (!optionalRootPlanSubGroupApproval.isEmpty()) {
						RootPlanSubgroupApprove rootPlanSubgroupApprove = optionalRootPlanSubGroupApproval
								.get(optionalRootPlanSubGroupApproval.size() - 1);
						if (rootPlanSubgroupApprove.getRootPlanBased()) {
							// check root plan based, convert to day plan
							RootPlanDetail rootPlanDetail = rootPlanHeaderService
									.findTaskListFromDetailByUserLoginAndStatusApproved();
							if (rootPlanDetail != null) {
								executiveTaskPlanService.saveDayPlanByRootPlanBased(rootPlanDetail.getTaskList());
								rootPlanDetailService.changeApprovalStatusDownload(rootPlanDetail);
							}
						}
					}
				}
			}
		}

		if (startDate == null || endDate == null) {
			executiveTaskPlanList = executiveTaskPlanRepository
					.findTillCurrentDateAndUserIsCurrentUser(LocalDate.now().atTime(23, 59));
			List<ExecutiveTaskPlan> updatedExecutiveTaskPlan = executiveTaskPlanList.stream().map(etpDto -> {
				// change status
				if (etpDto.getTaskPlanStatus().equals(TaskPlanStatus.CREATED)) {
					etpDto.setTaskPlanStatus(TaskPlanStatus.PENDING);
				}
				return etpDto;
			}).collect(Collectors.toList());
			executiveTaskPlanRepository.save(updatedExecutiveTaskPlan);
		} else {
			executiveTaskPlanList = executiveTaskPlanRepository.findByUserLoginAndPlannedDateBetweenOrderByIdAsc(login,
					startDate.atTime(0, 0), endDate.atTime(23, 59));
		}
		return new ResponseEntity<>(
				executiveTaskPlanList.stream().map(ExecutiveTaskPlanDTO::new).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	@Timed
	@Transactional
	@PostMapping("/skip-day-plan")
	public ResponseEntity<Void> skipDayPlan(@RequestParam(value = "taskPlanPid") String taskPlanPid,
			@RequestParam(value = "remarks") String remarks,
			@RequestParam(required = false, value = "dayClose") boolean dayClose) throws URISyntaxException {
		Optional<ExecutiveTaskPlan> optExecutiveTaskPlan = executiveTaskPlanRepository.findOneByPid(taskPlanPid);
		if (optExecutiveTaskPlan.isPresent()) {
			ExecutiveTaskPlan executiveTaskPlan = optExecutiveTaskPlan.get();
			executiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.SKIPPED);
			executiveTaskPlan.setUserRemarks(remarks);
			executiveTaskPlanRepository.save(executiveTaskPlan);

			// if dash-board user, save to dash-board notification table
			dashboardUserRepository.findByUserLogin(executiveTaskPlan.getUser().getLogin()).ifPresent(du -> {
				dashboardNotificationService.saveDashboardNotification(NotificationMessageType.TASK_SKIPPED,
						executiveTaskPlan, dayClose);
				simpMessagingTemplate.convertAndSend(
						"/live-tracking/user-notification/" + executiveTaskPlan.getCompany().getId(),
						executiveTaskPlan.getUser().getPid());
			});
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	@Timed
	@RequestMapping(value = "/executive-day-plan-datewise", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<List<ExecutiveTaskPlanDTO>> getExecutiveTaskPlanDateWise(
			@RequestParam(required = false, name = "startDate") LocalDate startDate,
			@RequestParam(required = false, name = "endDate") LocalDate endDate,
			@RequestParam(required = true, name = "filterBy") String filterBy
			) {
		List<ExecutiveTaskPlan> executiveTaskPlanList;
		String login = SecurityUtils.getCurrentUserLogin();
		
		switch(filterBy){
			case "TD":
				startDate = LocalDate.now().plusDays(1);
				endDate = LocalDate.now().plusDays(1);
				break;
			case "WFD":
				startDate = LocalDate.now().plusDays(1);
				TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
				LocalDate weekEndDate = LocalDate.now().with(fieldISO, 7);
				endDate = weekEndDate;
				break;
			case "MFD":
				startDate = LocalDate.now().plusDays(1);
				endDate = LocalDate.now().withDayOfMonth(startDate.getMonth().length(startDate.isLeapYear()));
				break;
			case "SINGLE":
				if(startDate == null){
					startDate = LocalDate.now();
					endDate = LocalDate.now();
				}else{
					endDate = startDate;
				}
				break;
			case "CUSTOM":
				if(startDate == null || endDate == null){
					startDate = LocalDate.now();
					endDate = LocalDate.now();
				}
				break;
			default :
				startDate = LocalDate.now();
				endDate = LocalDate.now();
				break;
		}

			executiveTaskPlanList = executiveTaskPlanRepository.findByUserLoginAndPlannedDateBetweenOrderByIdAsc(login,
					startDate.atTime(0, 0), endDate.atTime(23, 59));
		
		return new ResponseEntity<>(
				executiveTaskPlanList.stream().map(ExecutiveTaskPlanDTO::new).collect(Collectors.toList()),
				HttpStatus.OK);
	}

}
