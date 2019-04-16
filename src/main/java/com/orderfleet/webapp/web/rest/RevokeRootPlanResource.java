package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.service.ExecutiveTaskPlanService;
import com.orderfleet.webapp.service.RootPlanDetailService;
import com.orderfleet.webapp.service.TaskListService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.RootPlanDetailDTO;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;

/**
 * web controller for revoke rootplan.
 *
 * @author Sarath
 * @since Oct 25, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class RevokeRootPlanResource {

	private final Logger log = LoggerFactory.getLogger(RevokeRootPlanResource.class);

	@Inject
	private UserService userService;

	@Inject
	private RootPlanDetailService rootPlanDetailService;
	
	@Inject
	private TaskListService taskListService;
	
	@Inject
	private ExecutiveTaskPlanService executiveTaskPlanService;

	@RequestMapping(value = "/revoke-root-plan", method = RequestMethod.GET)
	@Timed
	public String getAllBanks(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Banks");
		List<UserDTO> userDTOs = userService.findAllByCompany();
		model.addAttribute("users", userDTOs);
		return "company/revoke-root-plan";
	}

	@RequestMapping(value = "/revoke-root-plan/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<RootPlanDetailDTO>> getRootPlan(@RequestParam String userPid) {
		log.debug("Web request to get RootPlanDetails by pid : {}", userPid);
		LocalDate today = LocalDate.now();
		List<RootPlanDetailDTO> detailDTOs = rootPlanDetailService.findAllByUserPidAndDownloadDateBetween(userPid,
				today.atTime(0, 0), today.atTime(23, 59));
		return new ResponseEntity<>(detailDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/revoke-root-plan/loadAssignTask", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<TaskDTO>> getAssignTask(@RequestParam String taskListPid) {
		log.debug("Web request to get TaskDTO by pid : {}", taskListPid);
		List<TaskDTO>taskDTOs=taskListService.findByTaskListPid(taskListPid);
		return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/revoke-root-plan/revoke", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> revokeRootPlan(@RequestParam String detailPid) {
		log.debug("Web request to save  by pid : {}", detailPid);
		RootPlanDetail rootPlanDetail=rootPlanDetailService.findOneByPid(detailPid).get();
		rootPlanDetailService.revokeRoutePlan(rootPlanDetail);
		executiveTaskPlanService.deleteDayPlanByUserPidAndTaskListPid(rootPlanDetail.getRootPlanHeader().getUser().getPid(), rootPlanDetail.getTaskList().getPid());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
