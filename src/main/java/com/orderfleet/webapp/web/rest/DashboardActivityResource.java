package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.DashboardActivityService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;

/**
 * Web controller for managing DashboardActivity.
 * 
 * @author Sarath
 * @since Oct 27, 2016
 */
@Controller
@RequestMapping("/web")
public class DashboardActivityResource {

	private final Logger log = LoggerFactory.getLogger(DashboardActivityResource.class);

	@Inject
	private DashboardActivityService dashboardActivityService;

	@Inject
	private ActivityService activityService;

	/**
	 * POST /dashboard-activities : Create a new dashboardActivities.
	 *
	 * @param activitiesPids
	 *            the dashboardActivities to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/dashboard-activities", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Void> createDashboardActivity(@RequestBody List<String> activitiesPids)
			throws URISyntaxException {
		log.debug("Web request to save DashboardActivity : {}", activitiesPids);
		dashboardActivityService.save(activitiesPids);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * GET /dashboard-activities : get all the dashboard activities.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         dashboard-activities in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/dashboard-activities", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDashboardActivities(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of DashboardActivities");
		model.addAttribute("dashboardActivities", dashboardActivityService.findActivitiesByCompanyId());
		model.addAttribute("activities", activityService.findAllByCompany());
		return "company/dashboardActivities";
	}

	/**
	 * GET /dashboard-activities/edit : get dashboardActivity.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         ActivityDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/dashboard-activities/edit", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ActivityDTO>> getDashboardActivity() {
		log.debug("Web request to get DashboardActivity");
		List<ActivityDTO> activityDTOs = dashboardActivityService.findActivitiesByCompanyId();
		return new ResponseEntity<>(activityDTOs, HttpStatus.OK);
	}
}
