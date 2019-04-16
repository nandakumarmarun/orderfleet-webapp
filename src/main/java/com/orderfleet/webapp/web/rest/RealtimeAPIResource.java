package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.RealtimeAPIService;
import com.orderfleet.webapp.web.rest.dto.RealtimeAPIDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing RealtimeAPI.
 *
 * @author Sarath
 * @since Apr 10, 2018
 *
 */
@Controller
@RequestMapping("/web")
public class RealtimeAPIResource {

	private final Logger log = LoggerFactory.getLogger(RealtimeAPIResource.class);

	@Inject
	private RealtimeAPIService realTimeAPIService;

	@Inject
	private CompanyService companyService;

	/**
	 * GET /realtime-api : get Realtime-API list.
	 *
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/real-time-api", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getRealtimeAPI(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Realtime API");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("realtimeAPIs", realTimeAPIService.findAll());
		return "company/real-time-api";
	}

	/**
	 * POST /real-time-api : Create a new realTimeAPI.
	 *
	 * @param realTimeAPIDTO
	 *            the realTimeAPIDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         realTimeAPIDTO, or with status 400 (Bad Request) if the realTimeAPI
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/real-time-api", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<RealtimeAPIDTO> createRealTimeAPI(@Valid @RequestBody RealtimeAPIDTO realTimeAPIDTO)
			throws URISyntaxException {
		log.debug("Web request to save RealTimeAPI : {}", realTimeAPIDTO);
		if (realTimeAPIDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("realTimeAPI", "idexists",
					"A new realTimeAPI cannot already have an ID")).body(null);
		}
		if (realTimeAPIService.findByName(realTimeAPIDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("realTimeAPI", "nameexists", "RealTimeAPI already in use"))
					.body(null);
		}
		realTimeAPIDTO.setActivated(true);
		RealtimeAPIDTO result = realTimeAPIService.save(realTimeAPIDTO);
		return ResponseEntity.created(new URI("/web/real-time-api/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("realTimeAPI", result.getId().toString())).body(result);
	}

	/**
	 * PUT /real-time-api : Updates an existing realTimeAPI.
	 *
	 * @param realTimeAPIDTO
	 *            the realTimeAPIDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         realTimeAPIDTO, or with status 400 (Bad Request) if the
	 *         realTimeAPIDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the realTimeAPIDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/real-time-api", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<RealtimeAPIDTO> updateRealTimeAPI(@Valid @RequestBody RealtimeAPIDTO realTimeAPIDTO)
			throws URISyntaxException {
		log.debug("REST request to update RealTimeAPI : {}", realTimeAPIDTO);
		if (realTimeAPIDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("realTimeAPI", "idNotexists", "RealTimeAPI must have an ID"))
					.body(null);
		}
		Optional<RealtimeAPIDTO> existingRealTimeAPI = realTimeAPIService.findByName(realTimeAPIDTO.getName());
		if (existingRealTimeAPI.isPresent() && (!existingRealTimeAPI.get().getId().equals(realTimeAPIDTO.getId()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("realTimeAPI", "nameexists", "RealTimeAPI already in use"))
					.body(null);
		}
		RealtimeAPIDTO result = realTimeAPIService.update(realTimeAPIDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("realTimeAPI", "idNotexists", "Invalid RealTimeAPI ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("realTimeAPI", realTimeAPIDTO.getId().toString()))
				.body(result);
	}

	/**
	 * GET /real-time-api/:id : get the "id" realTimeAPI.
	 *
	 * @param id
	 *            the id of the realTimeAPIDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         realTimeAPIDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/real-time-api/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<RealtimeAPIDTO> getRealTimeAPI(@PathVariable Long id) {
		log.debug("Web request to get RealTimeAPI by id : {}", id);
		return realTimeAPIService.findOneById(id)
				.map(realTimeAPIDTO -> new ResponseEntity<>(realTimeAPIDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /real-time-api/:id : delete the "id" realTimeAPI.
	 *
	 * @param id
	 *            the id of the realTimeAPIDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/real-time-api/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteRealTimeAPI(@PathVariable Long id) {
		log.debug("REST request to delete RealTimeAPI : {}", id);
		realTimeAPIService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("realTimeAPI", id.toString())).build();
	}

	@Timed
	@RequestMapping(value = "/real-time-api/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RealtimeAPIDTO> updateActivityGroupStatus(@Valid @RequestBody RealtimeAPIDTO realTimeAPIDTO) {
		log.debug("request to update realTimeAPI status", realTimeAPIDTO);
		RealtimeAPIDTO res = realTimeAPIService.updateRealtimeAPIStatus(realTimeAPIDTO.getId(),
				realTimeAPIDTO.isActivated());
		return new ResponseEntity<RealtimeAPIDTO>(res, HttpStatus.OK);
	}

	// @Timed
	// @RequestMapping(value = "/real-time-api/activateRealTimeAPI", method =
	// RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	// public ResponseEntity<RealtimeAPIDTO> activateRealTimeAPI(@Valid
	// @RequestParam String realTimeAPIs) {
	// log.debug("request to activate realTimeAPI ");
	// Long[] realTimeAPIarray = realTimeAPIs.split(",");
	// for (String realTimeAPI : realTimeAPIarray) {
	// realTimeAPIService.updateRealtimeAPIStatus(realTimeAPI, true);
	// }
	// return new ResponseEntity<RealtimeAPIDTO>(HttpStatus.OK);
	// }
	
	@RequestMapping(value = "/real-time-api/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<RealtimeAPIDTO>> getRealTimeAPIByCompanyPid(@RequestParam String companyPid) {
		log.debug("REST request to get RealTimeAPI by CompanyPid: {}", companyPid);
		List<RealtimeAPIDTO> realtimeAPIDTOs = new ArrayList<>();
		if(companyPid.equalsIgnoreCase("no")) {
			realtimeAPIDTOs = realTimeAPIService.findAll();
		}
		else {
			realtimeAPIDTOs = realTimeAPIService.findAllByCompanyPid(companyPid);
		}
		return new ResponseEntity<>(realtimeAPIDTOs, HttpStatus.OK);
	}

}
