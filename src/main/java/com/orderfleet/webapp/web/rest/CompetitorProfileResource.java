package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.orderfleet.webapp.service.CompetitorProfileService;
import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing CompetitorProfile.
 * 
 * @author Sarath
 * @since Aug 26, 2016
 */
@Controller
@RequestMapping("/web")
public class CompetitorProfileResource {

	private final Logger log = LoggerFactory.getLogger(CompetitorProfileResource.class);

	@Inject
	private CompetitorProfileService competitorProfileService;

	/**
	 * POST /competitor-profiles : Create a new competitorProfile.
	 *
	 * @param competitorProfileDTO
	 *            the competitorProfileDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new competitorProfileDTO, or with status 400 (Bad Request) if the
	 *         competitorProfile has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/competitor-profiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<CompetitorProfileDTO> createCompetitorProfile(
			@Valid @RequestBody CompetitorProfileDTO competitorProfileDTO) throws URISyntaxException {
		log.debug("Web request to save CompetitorProfile : {}", competitorProfileDTO);
		if (competitorProfileDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("competitorProfile", "idexists",
					"A new competitorProfile cannot already have an ID")).body(null);
		}
		if (competitorProfileService.findByName(competitorProfileDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("competitorProfile", "nameexists",
					"CompetitorProfile already in use")).body(null);
		}
		competitorProfileDTO.setActivated(true);
		CompetitorProfileDTO result = competitorProfileService.save(competitorProfileDTO);
		return ResponseEntity.created(new URI("/web/competitor-profiles/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("competitorProfile", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /competitor-profiles : Updates an existing competitorProfile.
	 *
	 * @param competitorProfileDTO
	 *            the competitorProfileDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         competitorProfileDTO, or with status 400 (Bad Request) if the
	 *         competitorProfileDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the competitorProfileDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/competitor-profiles", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompetitorProfileDTO> updateCompetitorProfile(
			@Valid @RequestBody CompetitorProfileDTO competitorProfileDTO) throws URISyntaxException {
		log.debug("REST request to update CompetitorProfile : {}", competitorProfileDTO);
		if (competitorProfileDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("competitorProfile", "idNotexists",
					"CompetitorProfile must have an ID")).body(null);
		}
		Optional<CompetitorProfileDTO> existingCompetitorProfile = competitorProfileService
				.findByName(competitorProfileDTO.getName());
		if (existingCompetitorProfile.isPresent()
				&& (!existingCompetitorProfile.get().getPid().equals(competitorProfileDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("competitorProfile", "nameexists",
					"CompetitorProfile already in use")).body(null);
		}
		CompetitorProfileDTO result = competitorProfileService.update(competitorProfileDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("competitorProfile", "idNotexists", "Invalid CompetitorProfile ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert("competitorProfile", competitorProfileDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /competitor-profiles : get all the competitorProfiles.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         competitorProfiles in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/competitor-profiles", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllCompetitorProfiles(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of CompetitorProfiles");
		Page<CompetitorProfileDTO> competitorProfiles = competitorProfileService
				.findAllByCompanyAndCompetitorProfileActivatedOrderByName(pageable, true);
		List<CompetitorProfileDTO> competitorProfileDTOs = competitorProfileService
				.findAllByCompanyIdAndCompetitorProfileActivatedOrDeactivated(false);
		model.addAttribute("pagecompetitorProfiles", competitorProfiles);
		model.addAttribute("deactivatedCompetitorProfiles", competitorProfileDTOs);
		return "company/competitorProfiles";
	}

	/**
	 * GET /competitor-profiles/:pid : get the "pid" competitorProfile.
	 *
	 * @param pid
	 *            the pid of the competitorProfileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         competitorProfileDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/competitor-profiles/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompetitorProfileDTO> getCompetitorProfile(@PathVariable String pid) {
		log.debug("Web request to get CompetitorProfile by pid : {}", pid);
		return competitorProfileService.findOneByPid(pid)
				.map(competitorProfileDTO -> new ResponseEntity<>(competitorProfileDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /competitor-profiles/:id : delete the "id" competitorProfile.
	 *
	 * @param id
	 *            the id of the competitorProfileDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/competitor-profiles/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteCompetitorProfile(@PathVariable String pid) {
		log.debug("REST request to delete CompetitorProfile : {}", pid);
		competitorProfileService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("competitorProfile", pid.toString()))
				.build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /competitor-profiles/changeStatus : update the
	 *        Status of CompetitorProfile.
	 *
	 * @param competitorProfileDTO
	 *            the competitorProfileDTO of the CompetitorProfileDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         competitorProfileDTO
	 */
	@Timed
	@RequestMapping(value = "/competitor-profiles/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompetitorProfileDTO> updateCompetitorProfileStatus(
			@Valid @RequestBody CompetitorProfileDTO competitorProfileDTO) {
		log.debug("Web request to update activated CompetitorProfile : {}", competitorProfileDTO);
		CompetitorProfileDTO res = competitorProfileService.updateCompetitorProfileStatus(competitorProfileDTO.getPid(),
				competitorProfileDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since feb 11, 2017
	 * 
	 *        updateActivateCompetitorProfileStatus
	 *        /accountGroups/activateCompetitorProfile: Activate Competitor
	 *        Profile.
	 * 
	 * @param competitorprofile
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/competitor-profiles/activateCompetitorProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompetitorProfileDTO> updateCompetitorProfileActivate(
			@Valid @RequestParam String competitorprofile) {
		String[] competitor = competitorprofile.split(",");
		for (String competitorPid : competitor) {
			competitorProfileService.updateCompetitorProfileStatus(competitorPid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}
}
