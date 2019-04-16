package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.RootPlanDetailService;
import com.orderfleet.webapp.service.RootPlanHeaderService;
import com.orderfleet.webapp.service.TaskListService;
import com.orderfleet.webapp.web.rest.dto.RootPlanDetailDTO;
import com.orderfleet.webapp.web.rest.dto.TaskListDTO;

@Controller
@RequestMapping("/web")
public class RootPlanDetailResource {
	
	private final Logger log = LoggerFactory.getLogger(RootPlanDetailResource.class);
	
	@Inject
	private RootPlanDetailService rootPlanDetailService;
	
	@Inject
	private RootPlanHeaderService rootPlanHeaderService;
	
	@Inject
	private TaskListService taskListService;
	
	/**
	 * GET /root-plan-details : get all the root Plan Details.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of root Plans in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP details
	 */
	@RequestMapping(value = "/root-plan-details", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllRootPlanDetails(Model model) throws URISyntaxException {
		log.debug("Web request to get page of root plan");
		model.addAttribute("rootPlanDetails", rootPlanDetailService.findAllByCompany());
		model.addAttribute("rootPlanHeaders", rootPlanHeaderService.findAllByCompany());
		return "company/rootPlanDetail";
	}
	
	/**
	 * GET /root-plan-details/loadTaskList : get all the TaskList.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of TaskList in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP details
	 */
	@RequestMapping(value = "/root-plan-details/loadTaskList" , method = RequestMethod.GET)
	public @ResponseBody List<TaskListDTO> getTaskList() {
		List<TaskListDTO>taskListDTOs=taskListService.findAllByCompany();
		return taskListDTOs;
	}
	
	/**
	 * POST /root-plan-details : save  RootPlanDetailDTO.
	 *
	 * @return the ResponseEntity with status 200 (OK) 
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP details
	 */
	@Timed
	@ResponseBody
	@RequestMapping(value = "/root-plan-details", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RootPlanDetailDTO> saveRootPlanDetail(@Valid @RequestBody List<RootPlanDetailDTO> rootPlanDetailDTOs) {
		
		log.debug("request to save RootPlanDetail ");
		
		rootPlanDetailService.save(rootPlanDetailDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * GET /root-plan-details/loadTaskList : get all the TaskList.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of TaskList in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP details
	 */
	@RequestMapping(value = "/root-plan-details/{id}" , method = RequestMethod.GET)
	public @ResponseBody List<RootPlanDetailDTO> getRootPlanDetail(@PathVariable String id) {
		List<RootPlanDetailDTO>rootPlanDetailDTOs=rootPlanDetailService.findAllByRootPlanHeaderPid(id);
		return rootPlanDetailDTOs;
	}
}
