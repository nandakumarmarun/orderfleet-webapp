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
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.ActivityTypeService;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityTypeDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class ActivityTypeResource {

	private final Logger log = LoggerFactory.getLogger(ActivityTypeResource.class);

	@Inject
	private ActivityTypeService activityTypeService;

	@Inject
	private ActivityService activityService;
	/**
	 * POST /activity-types : Create a new activityType.
	 *
	 * @param activityTypeDTO
	 *            the activityTypeDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new activityTypeDTO, or with status 400 (Bad Request) if the
	 *         activityType has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activity-types", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ActivityTypeDTO> createActivityType(@Valid @RequestBody ActivityTypeDTO activityTypeDTO)
			throws URISyntaxException {
		log.debug("Web request to save ActivityType : {}", activityTypeDTO);
		if (activityTypeDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("activityType", "idexists",
					"A new activityType cannot already have an ID")).body(null);
		}
		if (activityTypeService.findByName(activityTypeDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activityType", "nameexists", "ActivityType already in use"))
					.body(null);
		}
		activityTypeDTO.setActivated(true);
		ActivityTypeDTO result = activityTypeService.save(activityTypeDTO);
		return ResponseEntity.created(new URI("/web/activityTypes/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("activityType", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /activity-types : Updates an existing activityType.
	 *
	 * @param activityTypeDTO
	 *            the activityTypeDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         activityTypeDTO, or with status 400 (Bad Request) if the
	 *         activityTypeDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the activityTypeDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activity-types", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityTypeDTO> updateActivityType(@Valid @RequestBody ActivityTypeDTO activityTypeDTO)
			throws URISyntaxException {
		log.debug("REST request to update ActivityType : {}", activityTypeDTO);
		if (activityTypeDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("activityType", "idNotexists", "ActivityType must have an ID"))
					.body(null);
		}
		Optional<ActivityTypeDTO> existingActivityType = activityTypeService.findByName(activityTypeDTO.getName());
		if (existingActivityType.isPresent()
				&& (!existingActivityType.get().getPid().equals(activityTypeDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activityType", "nameexists", "ActivityType already in use"))
					.body(null);
		}
		ActivityTypeDTO result = activityTypeService.update(activityTypeDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activityType", "idNotexists", "Invalid ActivityType ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("activityType", activityTypeDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /activity-types : get all the activityTypes.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         activityTypes in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/activity-types", method = RequestMethod.GET)
	@Timed
	public String getAllActivityTypes(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of ActivityTypes");
		List<ActivityTypeDTO> activityTypes = activityTypeService.findAllByCompanyAndDeactivatedActivityType(true);
		List<ActivityTypeDTO> deactivatedActivityTypes = activityTypeService
				.findAllByCompanyAndDeactivatedActivityType(false);
		model.addAttribute("activityTypes", activityTypes);
		model.addAttribute("deactivatedActivityTypes", deactivatedActivityTypes);
		model.addAttribute("activities", activityService.findAllByCompany());
		return "company/activityTypes";
	}

	/**
	 * GET /activity-types/:pid : get the "pid" activityType.
	 *
	 * @param pid
	 *            the pid of the activityTypeDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         activityTypeDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/activity-types/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityTypeDTO> getActivityType(@PathVariable String pid) {
		log.debug("Web request to get ActivityType by pid : {}", pid);
		return activityTypeService.findOneByPid(pid)
				.map(activityTypeDTO -> new ResponseEntity<>(activityTypeDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	
	/**
	 * DELETE /activity-types/:id : delete the "id" activityType.
	 *
	 * @param id
	 *            the id of the activityTypeDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/activity-types/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteActivityType(@PathVariable String pid) {
		log.debug("REST request to delete ActivityType : {}", pid);
		activityTypeService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("activityType", pid.toString()))
				.build();
	}

	/**
	 * 
	 * UPDATE STATUS /activity-types/changeStatus:activityTypeDTO : update
	 * status of activityType.
	 * 
	 * @param activityTypeDTO
	 *            the activityTypeDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/activity-types/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ActivityTypeDTO> updateActivityGroupStatus(
			@Valid @RequestBody ActivityTypeDTO activityTypeDTO) {
		log.debug("request to update activityType status", activityTypeDTO);
		ActivityTypeDTO res = activityTypeService.updateActivityTypeStatus(activityTypeDTO.getPid(),
				activityTypeDTO.getActivated());
		return new ResponseEntity<ActivityTypeDTO>(res, HttpStatus.OK);
	}

	/**
	 * 
	 * Activate STATUS /activity-types/activateActivityType : activate status of
	 * activityTypes.
	 * 
	 * @param activityTypes
	 *            the activityTypes to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/activity-types/activateActivityType", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ActivityTypeDTO> activateActivityType(@Valid @RequestParam String activityTypes) {
		log.debug("request to activate activityType ");
		String[] activityTypearray = activityTypes.split(",");
		for (String activityType : activityTypearray) {
			activityTypeService.updateActivityTypeStatus(activityType, true);
		}
		return new ResponseEntity<ActivityTypeDTO>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/activity-types/findActivities/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<List<ActivityDTO>> getActivitys(@PathVariable String pid) {
		log.debug("REST request to get activitys by activityTypePid : {}", pid);
		return new ResponseEntity<>(activityTypeService.findAllActivityByActivityTypePid(pid), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/activity-types/assignActivities", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccounts(@RequestParam String pid, @RequestParam String assignedActivities) {
		log.debug("REST request to save assigned account type : {}", pid);
		activityTypeService.saveActivityTypeActivity(pid, assignedActivities);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
