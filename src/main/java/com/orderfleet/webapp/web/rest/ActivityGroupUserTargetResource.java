package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;

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
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.ActivityGroupService;
import com.orderfleet.webapp.service.ActivityGroupUserTargetService;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ActivityGroupUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 16, 2016
 */
@Controller
@RequestMapping("/web")
public class ActivityGroupUserTargetResource {

	private final Logger log = LoggerFactory.getLogger(ActivityGroupUserTargetResource.class);

	@Inject
	private ActivityGroupUserTargetService activityGroupUserTargetService;

	@Inject
	private ActivityGroupService activityGroupService;

	@Inject
	private UserService userService;

	/**
	 * POST /activity-group-user-targets : Create a new activityGroupUserTarget.
	 *
	 * @param activityGroupUserTargetDTO
	 *            the activityGroupUserTargetDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new activityGroupUserTargetDTO, or with status 400 (Bad Request)
	 *         if the activityGroupUserTarget has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activity-group-user-targets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ActivityGroupUserTargetDTO> createActivityGroupUserTarget(
			@Valid @RequestBody ActivityGroupUserTargetDTO activityGroupUserTargetDTO) throws URISyntaxException {
		log.debug("Web request to save ActivityGroupUserTarget : {}", activityGroupUserTargetDTO);
		if (activityGroupUserTargetDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("activityGroupUserTarget",
					"idexists", "A new product profile cannot already have an ID")).body(null);
		}

		ActivityGroupUserTargetDTO result = activityGroupUserTargetService.save(activityGroupUserTargetDTO);
		return ResponseEntity.created(new URI("/web/activity-group-user-targets/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("activityGroupUserTarget", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /activity-group-user-targets : Updates an existing
	 * activityGroupUserTarget.
	 *
	 * @param activityGroupUserTargetDTO
	 *            the activityGroupUserTargetDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         activityGroupUserTargetDTO, or with status 400 (Bad Request) if
	 *         the activityGroupUserTargetDTO is not valid, or with status 500
	 *         (Internal Server Error) if the activityGroupUserTargetDTO couldnt
	 *         be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activity-group-user-targets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityGroupUserTargetDTO> updateActivityGroupUserTarget(
			@Valid @RequestBody ActivityGroupUserTargetDTO activityGroupUserTargetDTO) throws URISyntaxException {
		log.debug("Web request to update ActivityGroupUserTarget : {}", activityGroupUserTargetDTO);
		if (activityGroupUserTargetDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("activityGroupUserTarget",
					"idNotexists", "activityUserTarget must have an ID")).body(null);
		}
		ActivityGroupUserTargetDTO result = activityGroupUserTargetService.update(activityGroupUserTargetDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("activityGroupUserTarget",
					"idNotexists", "Invalid activityUserTarget ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("activityGroupUserTarget",
				activityGroupUserTargetDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /activity-group-user-targets : get all the activityGroupUserTargets.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         activityGroupUserTargets in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/activity-group-user-targets", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllActivityGroupUserTargets(Model model) throws URISyntaxException {

		log.debug("Web request to get a page of ActivityGroupUserTargets");

		model.addAttribute("activityGroupUserTargets", activityGroupUserTargetService.findAllByCompany());

		model.addAttribute("activityGroups", activityGroupService.findAllByCompany());
		model.addAttribute("users", userService.findAllByCompany());

		return "company/activityGroupUserTargets";
	}

	/**
	 * GET /activity-group-user-targets/:id : get the "id"
	 * activityGroupUserTarget.
	 *
	 * @param id
	 *            the id of the activityGroupUserTargetDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         activityGroupUserTargetDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/activity-group-user-targets/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityGroupUserTargetDTO> getActivityGroupUserTarget(@PathVariable String pid) {
		log.debug("Web request to get ActivityGroupUserTarget by pid : {}", pid);
		return activityGroupUserTargetService.findOneByPid(pid)
				.map(activityGroupUserTargetDTO -> new ResponseEntity<>(activityGroupUserTargetDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /activity-group-user-targets/:pid : delete the "pid"
	 * activityGroupUserTarget.
	 *
	 * @param pid
	 *            the pid of the activityGroupUserTargetDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/activity-group-user-targets/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteActivityGroupUserTarget(@PathVariable String pid) {
		log.debug("REST request to delete ActivityGroupUserTarget : {}", pid);
		activityGroupUserTargetService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("activityGroupUserTarget", pid.toString())).build();
	}

}
