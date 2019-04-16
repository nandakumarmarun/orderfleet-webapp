package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import com.orderfleet.webapp.service.DesignationService;

import com.orderfleet.webapp.web.rest.dto.DesignationDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Designation.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
@Controller
@RequestMapping("/web")
public class DesignationResource {

	private final Logger log = LoggerFactory.getLogger(DesignationResource.class);

	@Inject
	private DesignationService designationService;

	/**
	 * POST /designations : Create a new designation.
	 *
	 * @param designationDTO
	 *            the designationDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new designationDTO, or with status 400 (Bad Request) if the
	 *         designation has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/designations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DesignationDTO> createDesignation(@Valid @RequestBody DesignationDTO designationDTO)
			throws URISyntaxException {
		log.debug("Web request to save Designation : {}", designationDTO);
		if (designationDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("designation", "idexists",
					"A new designation cannot already have an ID")).body(null);
		}
		if (designationService.findByName(designationDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("designation", "nameexists", "Designation already in use"))
					.body(null);
		}
		designationDTO.setActivated(true);
		DesignationDTO result = designationService.save(designationDTO);
		return ResponseEntity.created(new URI("/web/designations/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("designation", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /designations : Updates an existing designation.
	 *
	 * @param designationDTO
	 *            the designationDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         designationDTO, or with status 400 (Bad Request) if the
	 *         designationDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the designationDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/designations", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DesignationDTO> updateDesignation(@Valid @RequestBody DesignationDTO designationDTO)
			throws URISyntaxException {
		log.debug("Web request to update Designation : {}", designationDTO);
		if (designationDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("designation", "idNotexists", "Designation must have an ID"))
					.body(null);
		}
		Optional<DesignationDTO> existingDesignation = designationService.findByName(designationDTO.getName());
		if (existingDesignation.isPresent() && (!existingDesignation.get().getPid().equals(designationDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("designation", "nameexists", "Designation already in use"))
					.body(null);
		}
		DesignationDTO result = designationService.update(designationDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("designation", "idNotexists", "Invalid Designation ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("designation", designationDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /designations : get all the designations.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         designations in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/designations", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDesignations(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Designations");
		model.addAttribute("designations", designationService.findAllCompanyAndDeactivatedDesignation(true));
		model.addAttribute("deactivatedDesignations",
				designationService.findAllCompanyAndDeactivatedDesignation(false));
		return "company/designations";
	}

	/**
	 * GET /designations/:id : get the "id" designation.
	 *
	 * @param id
	 *            the id of the designationDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         designationDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/designations/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DesignationDTO> getDesignation(@PathVariable String pid) {
		log.debug("Web request to get Designation by pid : {}", pid);
		return designationService.findOneByPid(pid)
				.map(designationDTO -> new ResponseEntity<>(designationDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /designations/:id : delete the "id" designation.
	 *
	 * @param id
	 *            the id of the designationDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/designations/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDesignation(@PathVariable String pid) {
		log.debug("REST request to delete Designation : {}", pid);
		designationService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("designation", pid.toString())).build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017 UPDATE STATUS
	 *        /designations/changeStatus:DesignationDTO : update status of
	 *        Designation.
	 * 
	 * @param DesignationDTO
	 *            the DesignationDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/designations/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DesignationDTO> updateDesignationStatus(@Valid @RequestBody DesignationDTO designationDTO) {
		log.debug("Web request to update status Designation : {}", designationDTO);
		DesignationDTO res = designationService.updateDesignationStatus(designationDTO.getPid(),
				designationDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS /designations/activateDesignation: activate status
	 *        of designation.
	 * 
	 * @param designations
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/designations/activateDesignation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DesignationDTO> activateDesignation(@Valid @RequestParam String designations) {
		log.debug("Web request to activate status of Designation ");
		String[] designation = designations.split(",");
		for (String design : designation) {
			designationService.updateDesignationStatus(design, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
