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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.AttendanceStatusSubgroupService;
import com.orderfleet.webapp.web.rest.dto.AttendanceStatusSubgroupDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 *Web controller for managing AttendanceStatusSubgroup.
 *
 * @author fahad
 * @since Jul 25, 2017
 */
@Controller
@RequestMapping("/web")
public class AttendanceStatusSubgroupResource {
	private final Logger log = LoggerFactory.getLogger(AttendanceStatusSubgroupResource.class);

	@Inject
	private AttendanceStatusSubgroupService attendanceStatusSubgroupService;

	/**
	 * POST /attendance-status-subgroups : Create a new attendanceStatusSubgroup.
	 *
	 * @param attendanceStatusSubgroupDTO
	 *            the attendanceStatusSubgroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new attendanceStatusSubgroupDTO, or with status 400 (Bad Request) if the attendanceStatusSubgroup has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/attendance-status-subgroups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<AttendanceStatusSubgroupDTO> createAttendanceStatusSubgroup(@Valid @RequestBody AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO) throws URISyntaxException {
		log.debug("Web request to save AttendanceStatusSubgroup : {}", attendanceStatusSubgroupDTO);
		if (attendanceStatusSubgroupDTO.getId() != null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("attendanceStatusSubgroup", "idexists", "A new attendanceStatusSubgroup cannot already have an ID"))
					.body(null);
		}
		if (attendanceStatusSubgroupService.findByName(attendanceStatusSubgroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("attendanceStatusSubgroup", "nameexists", "AttendanceStatusSubgroup already in use")).body(null);
		}
		AttendanceStatusSubgroupDTO result = attendanceStatusSubgroupService.save(attendanceStatusSubgroupDTO);
		return ResponseEntity.created(new URI("/web/attendanceStatusSubgroups/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("attendanceStatusSubgroup", result.getId().toString())).body(result);
	}

	/**
	 * PUT /attendance-status-subgroups : Updates an existing attendanceStatusSubgroup.
	 *
	 * @param attendanceStatusSubgroupDTO
	 *            the attendanceStatusSubgroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         attendanceStatusSubgroupDTO, or with status 400 (Bad Request) if the attendanceStatusSubgroupDTO is not
	 *         valid, or with status 500 (Internal Server Error) if the attendanceStatusSubgroupDTO
	 *         couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/attendance-status-subgroups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AttendanceStatusSubgroupDTO> updateAttendanceStatusSubgroup(@Valid @RequestBody AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO) throws URISyntaxException {
		log.debug("REST request to update AttendanceStatusSubgroup : {}", attendanceStatusSubgroupDTO);
		if (attendanceStatusSubgroupDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("attendanceStatusSubgroup", "idNotexists", "AttendanceStatusSubgroup must have an ID")).body(null);
		}
		Optional<AttendanceStatusSubgroupDTO> existingAttendanceStatusSubgroup = attendanceStatusSubgroupService.findByName(attendanceStatusSubgroupDTO.getName());
		if (existingAttendanceStatusSubgroup.isPresent() && (!existingAttendanceStatusSubgroup.get().getId().equals(attendanceStatusSubgroupDTO.getId()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("attendanceStatusSubgroup", "nameexists", "AttendanceStatusSubgroup already in use")).body(null);
		}
		AttendanceStatusSubgroupDTO result = attendanceStatusSubgroupService.update(attendanceStatusSubgroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("attendanceStatusSubgroup", "idNotexists", "Invalid AttendanceStatusSubgroup ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("attendanceStatusSubgroup", attendanceStatusSubgroupDTO.getId().toString()))
				.body(result);
	}

	/**
	 * GET /attendance-status-subgroups : get all the attendanceStatusSubgroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of attendanceStatusSubgroups in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/attendance-status-subgroups", method = RequestMethod.GET)
	@Timed
	public String getAllAttendanceStatusSubgroups( Model model) throws URISyntaxException {
		log.debug("Web request to get a page of AttendanceStatusSubgroups");
		List<AttendanceStatusSubgroupDTO> attendanceStatusSubgroups = attendanceStatusSubgroupService.findAllByCompany();
//		List<AttendanceStatus>attendanceStatuses=new ArrayList<AttendanceStatus>();
//		for (AttendanceStatus attendanceStatus : AttendanceStatus.values()) {
//			attendanceStatuses.add(attendanceStatus);
//			}
//		model.addAttribute("attendanceStatuses", attendanceStatuses);
		model.addAttribute("attendanceStatusSubgroups", attendanceStatusSubgroups);
		return "company/attendanceStatusSubgroups";
	}

	@RequestMapping(value = "/attendance-status-subgroups/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AttendanceStatusSubgroupDTO> findOneAttendanceStatusSubgroup(@PathVariable Long id) {
		log.debug("REST request to find AttendanceStatusSubgroup : {}", id);
		AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO=attendanceStatusSubgroupService.findOne(id);
		return new ResponseEntity<AttendanceStatusSubgroupDTO>(attendanceStatusSubgroupDTO, HttpStatus.OK);
	}
	
	/**
	 * DELETE /attendance-status-subgroups/:id : delete the "id" attendanceStatusSubgroup.
	 *
	 * @param id
	 *            the id of the attendanceStatusSubgroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/attendance-status-subgroups/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteAttendanceStatusSubgroup(@PathVariable Long id) {
		log.debug("REST request to delete AttendanceStatusSubgroup : {}", id);
		attendanceStatusSubgroupService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("attendanceStatusSubgroup", id.toString())).build();
	}

}
