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
import com.orderfleet.webapp.service.TaxMasterService;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class TaxMasterResource {
	private final Logger log = LoggerFactory.getLogger(TaxMasterResource.class);

	@Inject
	private TaxMasterService taxMasterService;

	/**
	 * POST /taxMasters : Create a new taxMaster.
	 *
	 * @param taxMasterDTO
	 *            the taxMasterDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new taxMasterDTO, or with status 400 (Bad Request) if the taxMaster has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/taxMasters", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<TaxMasterDTO> createTaxMaster(@Valid @RequestBody TaxMasterDTO taxMasterDTO) throws URISyntaxException {
		log.debug("Web request to save TaxMaster : {}", taxMasterDTO);
		if (taxMasterDTO.getPid() != null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taxMaster", "idexists", "A new taxMaster cannot already have an ID"))
					.body(null);
		}
		if (taxMasterService.findByName(taxMasterDTO.getVatName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taxMaster", "nameexists", "TaxMaster already in use")).body(null);
		}
		TaxMasterDTO result = taxMasterService.saveTaxMaster(taxMasterDTO);
		return ResponseEntity.created(new URI("/web/taxMasters/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("taxMaster", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /taxMasters : Updates an existing taxMaster.
	 *
	 * @param taxMasterDTO
	 *            the taxMasterDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         taxMasterDTO, or with status 400 (Bad Request) if the taxMasterDTO is not
	 *         valid, or with status 500 (Internal Server Error) if the taxMasterDTO
	 *         couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/taxMasters", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaxMasterDTO> updateTaxMaster(@Valid @RequestBody TaxMasterDTO taxMasterDTO) throws URISyntaxException {
		log.debug("REST request to update TaxMaster : {}", taxMasterDTO);
		if (taxMasterDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taxMaster", "idNotexists", "TaxMaster must have an ID")).body(null);
		}
		Optional<TaxMasterDTO> existingTaxMaster = taxMasterService.findByName(taxMasterDTO.getVatName());
		if (existingTaxMaster.isPresent() && (!existingTaxMaster.get().getPid().equals(taxMasterDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taxMaster", "nameexists", "TaxMaster already in use")).body(null);
		}
		TaxMasterDTO result = taxMasterService.updateTaxMaster(taxMasterDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taxMaster", "idNotexists", "Invalid TaxMaster ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("taxMaster", taxMasterDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /taxMasters : get all the taxMasters.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of taxMasters in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/taxMasters", method = RequestMethod.GET)
	@Timed
	public String getAllTaxMasters(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of TaxMasters");
		List<TaxMasterDTO> taxMasters = taxMasterService.findAllByCompany();
		model.addAttribute("taxMasters", taxMasters);
		return "company/taxMasters";
	}

	/**
	 * GET /taxMasters/:pid : get the "pid" taxMaster.
	 *
	 * @param pid
	 *            the pid of the taxMasterDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         taxMasterDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/taxMasters/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaxMasterDTO> getTaxMaster(@PathVariable String pid) {
		log.debug("Web request to get TaxMaster by pid : {}", pid);
		return taxMasterService.findOneByPid(pid).map(taxMasterDTO -> new ResponseEntity<>(taxMasterDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /taxMasters/:id : delete the "id" taxMaster.
	 *
	 * @param id
	 *            the id of the taxMasterDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/taxMasters/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteTaxMaster(@PathVariable String pid) {
		log.debug("REST request to delete TaxMaster : {}", pid);
		taxMasterService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("taxMaster", pid.toString())).build();
	}

}
