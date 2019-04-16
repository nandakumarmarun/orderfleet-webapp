package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.ActivityGroupService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ActivityGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 09, 2016
 */
@Controller
@RequestMapping("/web")
public class ActivityGroupResource {

	private final Logger log = LoggerFactory.getLogger(ActivityGroupResource.class);

	@Inject
	private ActivityGroupService activityGroupService;

	@Inject
	private ActivityService activityService;

	/**
	 * POST /activity-groups : Create a new activityGroup.
	 *
	 * @param activityGroupDTO
	 *            the activityGroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new activityGroupDTO, or with status 400 (Bad Request) if the
	 *         activityGroup has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activity-groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ActivityGroupDTO> createActivityGroup(@Valid @RequestBody ActivityGroupDTO activityGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to save ActivityGroup : {}", activityGroupDTO);
		if (activityGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("activityGroup", "idexists",
					"A new activityGroup cannot already have an ID")).body(null);
		}
		if (activityGroupService.findByName(activityGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("activityGroup", "nameexists", "Activity Group already in use"))
					.body(null);
		}
		activityGroupDTO.setActivated(true);
		ActivityGroupDTO result = activityGroupService.save(activityGroupDTO);
		return ResponseEntity.created(new URI("/web/activity-groups/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("activityGroup", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /activity-groups : Updates an existing activityGroup.
	 *
	 * @param activityGroupDTO
	 *            the activityGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         activityGroupDTO, or with status 400 (Bad Request) if the
	 *         activityGroupDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the activityGroupDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activity-groups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityGroupDTO> updateActivityGroup(@Valid @RequestBody ActivityGroupDTO activityGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to update ActivityGroup : {}", activityGroupDTO);
		if (activityGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("activityGroup", "idNotexists", "Activity Group must have an ID"))
					.body(null);
		}
		Optional<ActivityGroupDTO> existingActivityGroup = activityGroupService.findByName(activityGroupDTO.getName());
		if (existingActivityGroup.isPresent()
				&& (!existingActivityGroup.get().getPid().equals(activityGroupDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("activityGroup", "nameexists", "Activity Group already in use"))
					.body(null);
		}
		ActivityGroupDTO result = activityGroupService.update(activityGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activityGroup", "idNotexists", "Invalid Activity Group ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("activityGroup", activityGroupDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /activity-groups : get all the activityGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         activityGroups in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/activity-groups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllActivityGroups(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of ActivityGroups");
		List<ActivityGroupDTO> activityGroups = activityGroupService
				.findAllByCompanyIdAndDeactivatedActivityGroup(true);
		model.addAttribute("activityGroups", activityGroups);
		model.addAttribute("activities", activityService.findAllByCompany());
		model.addAttribute("deactivatedActivityGroups",
				activityGroupService.findAllByCompanyIdAndDeactivatedActivityGroup(false));
		return "company/activityGroups";
	}

	/**
	 * GET /activity-groups/:id : get the "id" activityGroup.
	 *
	 * @param id
	 *            the id of the activityGroupDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         activityGroupDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/activity-groups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityGroupDTO> getActivityGroup(@PathVariable String pid) {
		log.debug("Web request to get ActivityGroup by pid : {}", pid);
		return activityGroupService.findOneByPid(pid)
				.map(activityGroupDTO -> new ResponseEntity<>(activityGroupDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /activity-groups/:id : delete the "id" activityGroup.
	 *
	 * @param id
	 *            the id of the activityGroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/activity-groups/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteActivityGroup(@PathVariable String pid) {
		log.debug("REST request to delete ActivityGroup : {}", pid);
		activityGroupService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("activityGroup", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/activity-groups/assign-activities", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedActivities(@RequestParam String pid,
			@RequestParam String assignedActivities) {
		log.debug("REST request to save assigned account type : {}", pid);
		activityGroupService.saveAssignedActivities(pid, assignedActivities);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /activity-groups/changeStatus : update the Status of
	 *        activityGroup.
	 *
	 * @param activityGroupDTO
	 *            the activityGroupDTO of the ActivityGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         activityGroupDTO
	 */
	@Timed
	@RequestMapping(value = "/activity-groups/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ActivityGroupDTO> updateActivityGroupStatus(
			@Valid @RequestBody ActivityGroupDTO activityGroupDTO) {
		log.debug("request to update ActivityGroup status", activityGroupDTO);
		ActivityGroupDTO res = activityGroupService.updateActivityGroupActive(activityGroupDTO.getPid(),
				activityGroupDTO.getActivated());
		return new ResponseEntity<ActivityGroupDTO>(res, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        Activate STATUS /activity-groups/activateActivityGroup : activate
	 *        status of ActivityGroup.
	 * 
	 * @param activitygroups
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/activity-groups/activateActivityGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ActivityGroupDTO> activateActivityGroup(@Valid @RequestParam String activitygroups) {
		log.debug("request to activate ActivityGroup ");
		String[] activitygroup = activitygroups.split(",");
		for (String activitypid : activitygroup) {
			activityGroupService.updateActivityGroupActive(activitypid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
