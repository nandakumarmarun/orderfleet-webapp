package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.EcomDisplayAttribute;
import com.orderfleet.webapp.service.EcomProductProfileProductService;
import com.orderfleet.webapp.service.EcomProductProfileService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing EcomProductProfile.
 * 
 * @author Sarath
 * @since Sep 23, 2016
 */
@Controller
@RequestMapping("/web")
public class EcomProductProfileResource {

	private final Logger log = LoggerFactory.getLogger(EcomProductProfileResource.class);

	@Inject
	private EcomProductProfileService ecomProductProfileService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private EcomProductProfileProductService ecomProductProfileProductService;

	/**
	 * POST /ecom-product-profiles : Create a new ecomProductProfile.
	 *
	 * @param ecomProductProfileDTO
	 *            the ecomProductProfileDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new ecomProductProfileDTO, or with status 400 (Bad Request) if
	 *         the ecomProductProfile has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/ecom-product-profiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<EcomProductProfileDTO> createEcomProductProfile(
			@Valid @RequestBody EcomProductProfileDTO ecomProductProfileDTO) throws URISyntaxException {
		log.debug("Web request to save EcomProductProfile : {}", ecomProductProfileDTO);
		if (ecomProductProfileDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("ecomProductProfile", "idexists",
					"A new ecomProductProfile cannot already have an ID")).body(null);
		}
		if (ecomProductProfileService.findByName(ecomProductProfileDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("ecomProductProfile", "nameexists",
					"EcomProductProfile already in use")).body(null);
		}
		ecomProductProfileDTO.setActivated(true);
		EcomProductProfileDTO result = ecomProductProfileService.save(ecomProductProfileDTO);
		return ResponseEntity.created(new URI("/web/ecom-product-profiles/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("ecomProductProfile", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /ecom-product-profiles : Updates an existing ecomProductProfile.
	 *
	 * @param ecomProductProfileDTO
	 *            the ecomProductProfileDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         ecomProductProfileDTO, or with status 400 (Bad Request) if the
	 *         ecomProductProfileDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the ecomProductProfileDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/ecom-product-profiles", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EcomProductProfileDTO> updateEcomProductProfile(
			@Valid @RequestBody EcomProductProfileDTO ecomProductProfileDTO) throws URISyntaxException {
		log.debug("REST request to update EcomProductProfile : {}", ecomProductProfileDTO);
		if (ecomProductProfileDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("ecomProductProfile",
					"idNotexists", "EcomProductProfile must have an ID")).body(null);
		}
		Optional<EcomProductProfileDTO> existingEcomProductProfile = ecomProductProfileService
				.findByName(ecomProductProfileDTO.getName());
		if (existingEcomProductProfile.isPresent()
				&& (!existingEcomProductProfile.get().getPid().equals(ecomProductProfileDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("ecomProductProfile", "nameexists",
					"EcomProductProfile already in use")).body(null);
		}
		EcomProductProfileDTO result = ecomProductProfileService.update(ecomProductProfileDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("ecomProductProfile", "idNotexists", "Invalid EcomProductProfile ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert("ecomProductProfile", ecomProductProfileDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /ecom-product-profiles : get all the ecomProductProfiles.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         ecomProductProfiles in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/ecom-product-profiles", method = RequestMethod.GET)
	@Timed
	public String getAllEcomProductProfiles(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of EcomProductProfiles");
		List<EcomProductProfileDTO> ecomProductProfiles = ecomProductProfileService
				.findAllByCompanyAndActivatedOrDeactivatedEcomProductProfile(true);
		model.addAttribute("ecomProductProfiles", ecomProductProfiles);
		model.addAttribute("deactivatedEcomProductProfiles",
				ecomProductProfileService.findAllByCompanyAndActivatedOrDeactivatedEcomProductProfile(false));
		model.addAttribute("ecomDisplayAttributes", EcomDisplayAttributes());
		model.addAttribute("products",
				productProfileService.findAllByCompanyAndActivatedProductProfileOrderByName(true));
		return "company/ecomProductProfiles";
	}

	private List<EcomDisplayAttribute> EcomDisplayAttributes() {
		List<EcomDisplayAttribute> somethingList = new ArrayList<EcomDisplayAttribute>(
				EnumSet.allOf(EcomDisplayAttribute.class));
		return somethingList;
	}

	/**
	 * GET /ecom-product-profiles/:pid : get the "pid" ecomProductProfile.
	 *
	 * @param pid
	 *            the pid of the ecomProductProfileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         ecomProductProfileDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/ecom-product-profiles/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EcomProductProfileDTO> getEcomProductProfile(@PathVariable String pid) {
		log.debug("Web request to get EcomProductProfile by pid : {}", pid);
		return ecomProductProfileService.findOneByPid(pid)
				.map(ecomProductProfileDTO -> new ResponseEntity<>(ecomProductProfileDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /ecom-product-profiles/:id : delete the "id" ecomProductProfile.
	 *
	 * @param id
	 *            the id of the ecomProductProfileDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/ecom-product-profiles/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteEcomProductProfile(@PathVariable String pid) {
		log.debug("REST request to delete EcomProductProfile : {}", pid);
		ecomProductProfileService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("ecomProductProfile", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/ecom-product-profiles/assignProducts", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedProducts(@RequestParam String pid, @RequestParam String assignedproducts) {
		log.debug("REST request to save assigned products : {}", pid);
		ecomProductProfileProductService.save(pid, assignedproducts);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/ecom-product-profiles/findProducts/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getProducts(@PathVariable String pid) {
		log.debug("REST request to get Products by producyProfilePid : {}", pid);
		List<ProductProfileDTO> ecomProductProfileProducts = ecomProductProfileProductService
				.findProductByEcomProductProfilePid(pid);
		return new ResponseEntity<>(ecomProductProfileProducts, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 * 
	 *        UPDATE STATUS
	 *        /ecom-product-profiles/changeStatus:ecomProductProfileDTO : update
	 *        status of ecomProductProfile.
	 * 
	 * @param ecomProductProfileDTO
	 *            the ecomProductProfileDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/ecom-product-profiles/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EcomProductProfileDTO> updateActivityGroupStatus(
			@Valid @RequestBody EcomProductProfileDTO ecomProductProfileDTO) {
		log.debug("request to update ecomProductProfile status", ecomProductProfileDTO);
		EcomProductProfileDTO res = ecomProductProfileService
				.updateEcomProductProfileStatus(ecomProductProfileDTO.getPid(), ecomProductProfileDTO.getActivated());
		return new ResponseEntity<EcomProductProfileDTO>(res, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 18, 2017
	 * 
	 *        Activate STATUS /ecom-product-profiles/activateEcomProductProfile
	 *        : activate status of ecomProductProfiles.
	 * 
	 * @param ecomproductprofiles
	 *            the ecomProductProfileDTO to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/ecom-product-profiles/activateEcomProductProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EcomProductProfileDTO> activateEcomProductProfile(
			@Valid @RequestParam String ecomproductprofiles) {
		log.debug("request to activate ecomProductProfile ");
		String[] ecomProductProfiles = ecomproductprofiles.split(",");
		for (String ecomPid : ecomProductProfiles) {
			ecomProductProfileService.updateEcomProductProfileStatus(ecomPid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
