package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityUserTarget;
import com.orderfleet.webapp.repository.ActivityUserTargetRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.ActivityUserTargetService;
import com.orderfleet.webapp.web.rest.dto.ActivityUserTargetDTO;
import com.orderfleet.webapp.web.rest.dto.MonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ActivityUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
@Controller
@RequestMapping("/web")
public class ActivityUserTargetResource {

	private final Logger log = LoggerFactory.getLogger(ActivityUserTargetResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ActivityUserTargetService activityUserTargetService;

	@Inject
	private ActivityService activityService;

	@Inject
	private UserService userService;

	@Inject
	private UserActivityRepository userActivityRepository;

	@Inject
	private ActivityUserTargetRepository activityUserTargetRepository;

	/**
	 * POST /activity-user-targets : Create a new activityUserTarget.
	 *
	 * @param activityUserTargetDTO
	 *            the activityUserTargetDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new activityUserTargetDTO, or with status 400 (Bad Request) if
	 *         the activityUserTarget has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activity-user-targets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ActivityUserTargetDTO> createActivityUserTarget(
			@Valid @RequestBody ActivityUserTargetDTO activityUserTargetDTO) throws URISyntaxException {
		log.debug("Web request to save ActivityUserTarget : {}", activityUserTargetDTO);
		if (activityUserTargetDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("activityUserTarget", "idexists",
					"A new product profile cannot already have an ID")).body(null);
		}
		ActivityUserTargetDTO result = activityUserTargetService.save(activityUserTargetDTO);
		return ResponseEntity.created(new URI("/web/activity-user-targets/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("activityUserTarget", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /activity-user-targets : Updates an existing activityUserTarget.
	 *
	 * @param activityUserTargetDTO
	 *            the activityUserTargetDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         activityUserTargetDTO, or with status 400 (Bad Request) if the
	 *         activityUserTargetDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the activityUserTargetDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activity-user-targets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityUserTargetDTO> updateActivityUserTarget(
			@Valid @RequestBody ActivityUserTargetDTO activityUserTargetDTO) throws URISyntaxException {
		log.debug("Web request to update ActivityUserTarget : {}", activityUserTargetDTO);
		if (activityUserTargetDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("activityUserTarget",
					"idNotexists", "activityUserTarget must have an ID")).body(null);
		}
		ActivityUserTargetDTO result = activityUserTargetService.update(activityUserTargetDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("activityUserTarget", "idNotexists", "Invalid activityUserTarget ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert("activityUserTarget", activityUserTargetDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /activity-user-targets : get all the activityUserTargets.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         activityUserTargets in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/activity-user-targets", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllActivityUserTargets(Model model) throws URISyntaxException {

		log.debug("Web request to get a page of ActivityUserTargets");

		model.addAttribute("activityUserTargets", activityUserTargetService.findAllByCompany());

		model.addAttribute("activities", activityService.findAllByCompany());
		model.addAttribute("users", userService.findAllByCompany());

		return "company/activityUserTargets";
	}

	/**
	 * GET /set-activity-targets : get all the activityUserTargets.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         activityUserTargets in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/set-activity-targets", method = RequestMethod.GET)
	public String setActivityTargets(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of  set Activity Target");
		model.addAttribute("users", userService.findAllByCompany());
		return "company/setActivityTarget";
	}

	@RequestMapping(value = "/set-activity-targets/monthly-activity-targets", method = RequestMethod.GET)
	public @ResponseBody List<MonthlyTargetDTO> monthlyActivityTargets(@RequestParam String userPid,
			@RequestParam String monthAndYear) {
		log.debug("Web request to get monthly Activity Targets");
		List<Activity> activities = userActivityRepository.findActivitiesByUserPid(userPid);
		if (activities.size() > 0) {

			String[] monthAndYearArray = monthAndYear.split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);

			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();

			List<MonthlyTargetDTO> monthlyTargetDTOs = new ArrayList<>();
			for (Activity activity : activities) {
				MonthlyTargetDTO monthlyTargetDTO = new MonthlyTargetDTO();
				monthlyTargetDTO.setActivityPid(activity.getPid());
				monthlyTargetDTO.setActivityName(activity.getName());
				monthlyTargetDTO.setUserPid(userPid);
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AUT_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get by user pid activityPid and date";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				List<ActivityUserTarget> activityUserTargets = activityUserTargetRepository
						.findByUserPidAndActivityPidAndStartDateGreaterThanEqualAndEndDateLessThanEqual(userPid,
								activity.getPid(), firstDateMonth, lastDateMonth);
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
				if (activityUserTargets.size() > 0) {
					monthlyTargetDTO.setTarget(activityUserTargets.get(0).getTargetNumber());
					monthlyTargetDTO.setUserActivityTragetPid(activityUserTargets.get(0).getPid());
				} else {
					monthlyTargetDTO.setTarget(0L);
				}
				monthlyTargetDTOs.add(monthlyTargetDTO);
			}
			return monthlyTargetDTOs;
		}
		return null;
	}

	@RequestMapping(value = "/set-activity-targets/monthly-activity-targets", method = RequestMethod.POST)
	public @ResponseBody MonthlyTargetDTO saveMonthlyActivityTargets(@RequestBody MonthlyTargetDTO monthlyTargetDTO) {
		log.debug("Web request to save monthly Activity Targets");
		if (monthlyTargetDTO.getUserActivityTragetPid().equals("null")) {
			String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);

			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			ActivityUserTargetDTO result = activityUserTargetService.saveMonthlyTarget(monthlyTargetDTO, firstDateMonth,
					lastDateMonth);
			monthlyTargetDTO.setUserActivityTragetPid(result.getPid());
		} else {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AUT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			activityUserTargetRepository.findOneByPid(monthlyTargetDTO.getUserActivityTragetPid())
					.ifPresent(activityUserTarget -> {
						activityUserTarget.setTargetNumber(monthlyTargetDTO.getTarget());
						activityUserTargetRepository.save(activityUserTarget);
					});
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
		return monthlyTargetDTO;
	}

	/**
	 * GET /activity-user-targets/:id : get the "id" activityUserTarget.
	 *
	 * @param id
	 *            the id of the activityUserTargetDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         activityUserTargetDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/activity-user-targets/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityUserTargetDTO> getActivityUserTarget(@PathVariable String pid) {
		log.debug("Web request to get ActivityUserTarget by pid : {}", pid);
		return activityUserTargetService.findOneByPid(pid)
				.map(activityUserTargetDTO -> new ResponseEntity<>(activityUserTargetDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /activity-user-targets/:pid : delete the "pid" activityUserTarget.
	 *
	 * @param pid
	 *            the pid of the activityUserTargetDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/activity-user-targets/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteActivityUserTarget(@PathVariable String pid) {
		log.debug("REST request to delete ActivityUserTarget : {}", pid);
		activityUserTargetService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("activityUserTarget", pid.toString()))
				.build();
	}

}
