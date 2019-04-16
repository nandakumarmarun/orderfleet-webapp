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
import com.orderfleet.webapp.service.KnowledgebaseService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Knowledgebase.
 * 
 * @author Muhammed Riyas T
 * @since August 08, 2016
 */
@Controller
@RequestMapping("/web")
public class KnowledgebaseResource {

	private final Logger log = LoggerFactory.getLogger(KnowledgebaseResource.class);

	@Inject
	private KnowledgebaseService knowledgebaseService;

	@Inject
	private ProductGroupService productGroupService;

	/**
	 * POST /knowledgebase : Create a new knowledgebase.
	 *
	 * @param knowledgebaseDTO
	 *            the knowledgebaseDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new knowledgebaseDTO, or with status 400 (Bad Request) if the
	 *         knowledgebase has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/knowledgebase", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<KnowledgebaseDTO> createKnowledgebase(@Valid @RequestBody KnowledgebaseDTO knowledgebaseDTO)
			throws URISyntaxException {
		log.debug("Web request to save Knowledgebase : {}", knowledgebaseDTO);
		if (knowledgebaseDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("knowledgebase", "idexists",
					"A new knowledgebase cannot already have an ID")).body(null);
		}
		if (knowledgebaseService.findByName(knowledgebaseDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("knowledgebase", "nameexists", "Knowledgebase already in use"))
					.body(null);
		}
		knowledgebaseDTO.setActivated(true);
		KnowledgebaseDTO result = knowledgebaseService.save(knowledgebaseDTO);
		return ResponseEntity.created(new URI("/web/knowledgebase/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("knowledgebase", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /knowledgebase : Updates an existing knowledgebase.
	 *
	 * @param knowledgebaseDTO
	 *            the knowledgebaseDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         knowledgebaseDTO, or with status 400 (Bad Request) if the
	 *         knowledgebaseDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the knowledgebaseDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/knowledgebase", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<KnowledgebaseDTO> updateKnowledgebase(@Valid @RequestBody KnowledgebaseDTO knowledgebaseDTO)
			throws URISyntaxException {
		log.debug("REST request to update Knowledgebase : {}", knowledgebaseDTO);
		if (knowledgebaseDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("knowledgebase", "idNotexists", "Knowledgebase must have an ID"))
					.body(null);
		}
		Optional<KnowledgebaseDTO> existingKnowledgebase = knowledgebaseService.findByName(knowledgebaseDTO.getName());
		if (existingKnowledgebase.isPresent()
				&& (!existingKnowledgebase.get().getPid().equals(knowledgebaseDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("knowledgebase", "nameexists", "Knowledgebase already in use"))
					.body(null);
		}
		KnowledgebaseDTO result = knowledgebaseService.update(knowledgebaseDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("knowledgebase", "idNotexists", "Invalid Knowledgebase ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("knowledgebase", knowledgebaseDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /knowledgebase : get all the knowledgebases.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         knowledgebases in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/knowledgebase", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllKnowledgebases(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Knowledgebases");
		
		List<KnowledgebaseDTO> knowledgebaseDTOs = knowledgebaseService
				.findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivated(false);
		model.addAttribute("knowledgebases",  knowledgebaseService.findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivated(true));
		model.addAttribute("deactivatedKnowledgebases", knowledgebaseDTOs);
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		return "company/knowledgebases";
	}

	/**
	 * GET /knowledgebase/:pid : get the "pid" knowledgebase.
	 *
	 * @param pid
	 *            the pid of the knowledgebaseDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         knowledgebaseDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/knowledgebase/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<KnowledgebaseDTO> getKnowledgebase(@PathVariable String pid) {
		log.debug("Web request to get Knowledgebase by pid : {}", pid);
		return knowledgebaseService.findOneByPid(pid)
				.map(knowledgebaseDTO -> new ResponseEntity<>(knowledgebaseDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /knowledgebase/:id : delete the "id" knowledgebase.
	 *
	 * @param id
	 *            the id of the knowledgebaseDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/knowledgebase/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteKnowledgebase(@PathVariable String pid) {
		log.debug("REST request to delete Knowledgebase : {}", pid);
		knowledgebaseService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("knowledgebase", pid.toString()))
				.build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /knowledgebase/changeStatus:knowledgebaseDTO :
	 *        update status of knowledgebase.
	 * 
	 * @param knowledgebaseDTO
	 *            the knowledgebaseDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/knowledgebase/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<KnowledgebaseDTO> updateKnowledgebaseStatus(
			@Valid @RequestBody KnowledgebaseDTO knowledgebaseDTO) {
		log.debug("Web request to update status knowledgebase : {}", knowledgebaseDTO);
		KnowledgebaseDTO result = knowledgebaseService.updateKnowledgebaseStatus(knowledgebaseDTO.getPid(),
				knowledgebaseDTO.getActivated());
		return new ResponseEntity<KnowledgebaseDTO>(result, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        Activate STATUS /knowledgebase/activateKnowledgebase : activate
	 *        status of knowledgebase.
	 * 
	 * @param knowledgebases
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/knowledgebase/activateKnowledgebase", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<KnowledgebaseDTO> activateKnowledgebase(@Valid @RequestParam String knowledgebases) {
		log.debug("Web request to activate knowledgebase : {}");
		String[] knowledgebase = knowledgebases.split(",");
		for (String knowledgebasepid : knowledgebase) {
			knowledgebaseService.updateKnowledgebaseStatus(knowledgebasepid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
