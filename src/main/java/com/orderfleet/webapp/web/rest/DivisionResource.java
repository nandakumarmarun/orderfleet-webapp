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
import com.orderfleet.webapp.service.DivisionService;

import com.orderfleet.webapp.web.rest.dto.DivisionDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Division.
 * 
 * @author Shaheer
 * @since May 07, 2016
 */
@Controller
@RequestMapping("/web")
public class DivisionResource {

	private final Logger log = LoggerFactory.getLogger(DivisionResource.class);

	@Inject
	private DivisionService divisionService;

	/**
	 * POST /divisions : Create a new division.
	 *
	 * @param divisionDTO
	 *            the divisionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new divisionDTO, or with status 400 (Bad Request) if the division
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/divisions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DivisionDTO> createDivision(@Valid @RequestBody DivisionDTO divisionDTO)
			throws URISyntaxException {
		log.debug("Web request to save Division : {}", divisionDTO);
		if (divisionDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("division", "idexists", "A new division cannot already have an ID"))
					.body(null);
		}
		if (divisionService.findByName(divisionDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("division", "nameexists", "Division already in use"))
					.body(null);
		}
		divisionDTO.setActivated(true);
		DivisionDTO result = divisionService.save(divisionDTO);
		return ResponseEntity.created(new URI("/web/divisions/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("division", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /divisions : Updates an existing division.
	 *
	 * @param divisionDTO
	 *            the divisionDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         divisionDTO, or with status 400 (Bad Request) if the divisionDTO
	 *         is not valid, or with status 500 (Internal Server Error) if the
	 *         divisionDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/divisions", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DivisionDTO> updateDivision(@Valid @RequestBody DivisionDTO divisionDTO)
			throws URISyntaxException {
		log.debug("REST request to update Division : {}", divisionDTO);
		if (divisionDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("division", "idNotexists", "Division must have an ID"))
					.body(null);
		}
		Optional<DivisionDTO> existingDivision = divisionService.findByName(divisionDTO.getName());
		if (existingDivision.isPresent() && (!existingDivision.get().getPid().equals(divisionDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("division", "nameexists", "Division already in use"))
					.body(null);
		}
		DivisionDTO result = divisionService.update(divisionDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("division", "idNotexists", "Invalid Division ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("division", divisionDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /divisions : get all the divisions.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of divisions
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/divisions", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDivisions(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Divisions");
		List<DivisionDTO> deactiveDivisionDTOs = divisionService.findAllByCompanyAndDeactivatedDivision(false);
		model.addAttribute("divisions", divisionService.findAllByCompanyAndDeactivatedDivision(true));
		model.addAttribute("deactivatedDivisions", deactiveDivisionDTOs);
		return "company/divisions";
	}

	/**
	 * GET /divisions/:pid : get the "pid" division.
	 *
	 * @param pid
	 *            the pid of the divisionDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         divisionDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/divisions/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DivisionDTO> getDivision(@PathVariable String pid) {
		log.debug("Web request to get Division by pid : {}", pid);
		return divisionService.findOneByPid(pid).map(divisionDTO -> new ResponseEntity<>(divisionDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /divisions/:id : delete the "id" division.
	 *
	 * @param id
	 *            the id of the divisionDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/divisions/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDivision(@PathVariable String pid) {
		log.debug("REST request to delete Division : {}", pid);
		divisionService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("division", pid.toString())).build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /divisions/changeStatus:divisionDTO : update status
	 *        of division.
	 * 
	 * @param divisionDTO
	 *            the divisionDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/divisions/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DivisionDTO> updateActivityGroupStatus(@Valid @RequestBody DivisionDTO divisionDTO) {
		log.debug("request to update division status", divisionDTO);
		DivisionDTO res = divisionService.updateDivisionStatus(divisionDTO.getPid(), divisionDTO.getActivated());
		return new ResponseEntity<DivisionDTO>(res, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS /divisions/activateDivision : activate status of
	 *        division.
	 * 
	 * @param divisions
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/divisions/activateDivision", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DivisionDTO> activateDivision(@Valid @RequestParam String divisions) {
		log.debug("request to activate division");
		String[] divisionArray = divisions.split(",");
		for (String divisionpid : divisionArray) {
			divisionService.updateDivisionStatus(divisionpid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
