package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.UnitOfMeasureProductService;
import com.orderfleet.webapp.service.UnitOfMeasureService;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SetTaxRate;
import com.orderfleet.webapp.web.rest.dto.UnitOfMeasureDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing UnitOfMeasure.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Controller
@RequestMapping("/web")
public class UnitOfMeasureResource {

	private final Logger log = LoggerFactory.getLogger(UnitOfMeasureResource.class);

	@Inject
	private UnitOfMeasureService unitOfMeasureService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private UnitOfMeasureProductService unitOfMeasureProductService;

	/**
	 * POST /unitOfMeasures : Create a new unitOfMeasure.
	 *
	 * @param unitOfMeasureDTO the unitOfMeasureDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         unitOfMeasureDTO, or with status 400 (Bad Request) if the
	 *         unitOfMeasure has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/unitOfMeasures", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<UnitOfMeasureDTO> createUnitOfMeasure(@Valid @RequestBody UnitOfMeasureDTO unitOfMeasureDTO)
			throws URISyntaxException {
		log.debug("Web request to save UnitOfMeasure : {}", unitOfMeasureDTO);
		if (unitOfMeasureDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("unitOfMeasure", "idexists",
					"A new unitOfMeasure cannot already have an ID")).body(null);
		}
		if (unitOfMeasureService.findByName(unitOfMeasureDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("unitOfMeasure", "nameexists", "Unit Of Measure already in use"))
					.body(null);
		}
		unitOfMeasureDTO.setActivated(true);
		UnitOfMeasureDTO result = unitOfMeasureService.save(unitOfMeasureDTO);
		return ResponseEntity.created(new URI("/web/unitOfMeasures/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("unitOfMeasure", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /unitOfMeasures : Updates an existing unitOfMeasure.
	 *
	 * @param unitOfMeasureDTO the unitOfMeasureDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         unitOfMeasureDTO, or with status 400 (Bad Request) if the
	 *         unitOfMeasureDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the unitOfMeasureDTO couldnt be updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/unitOfMeasures", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UnitOfMeasureDTO> updateUnitOfMeasure(@Valid @RequestBody UnitOfMeasureDTO unitOfMeasureDTO)
			throws URISyntaxException {
		log.debug("Web request to update UnitOfMeasure : {}", unitOfMeasureDTO);
		if (unitOfMeasureDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("unitOfMeasure", "idNotexists", "Unit Of Measure must have an ID"))
					.body(null);
		}
		Optional<UnitOfMeasureDTO> existingUnitOfMeasure = unitOfMeasureService.findByName(unitOfMeasureDTO.getName());
		if (existingUnitOfMeasure.isPresent()
				&& (!existingUnitOfMeasure.get().getPid().equals(unitOfMeasureDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("unitOfMeasure", "nameexists", "Unit Of Measure already in use"))
					.body(null);
		}
		UnitOfMeasureDTO result = unitOfMeasureService.update(unitOfMeasureDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("unitOfMeasure", "idNotexists", "Invalid Unit Of Measure ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("unitOfMeasure", unitOfMeasureDTO.getPid().toString()))
				.body(result);
	}

	@Timed
	@RequestMapping(value = "/unitOfMeasures/assign-tax", method = RequestMethod.POST)
	public ResponseEntity<Void> saveAssignedTax(@RequestBody SetTaxRate setTaxRate) {
		log.debug("REST request to save assigned tax rate : {}", setTaxRate);
		//unitOfMeasureService.saveTaxRate(setTaxRate);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/unitOfMeasures/assign-unit-quantity", method = RequestMethod.POST)
	public ResponseEntity<Void> assignUnitQuantity(@RequestBody SetTaxRate setUnitQty) {
		log.debug("REST request to save assigned unit quantity : {}", setUnitQty);
		//unitOfMeasureService.saveUnitQuantity(setUnitQty);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /unitOfMeasures : get all the unitOfMeasures.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         unitOfMeasures in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/unitOfMeasures", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllUnitOfMeasures(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of UnitOfMeasures");
		List<UnitOfMeasureDTO> pageUnitOfMeasure = unitOfMeasureService
				.findAllByCompanyAndDeactivatedUnitOfMeasure(true);
		model.addAttribute("pageUnitOfMeasure", pageUnitOfMeasure);
		model.addAttribute("deactivatedUnitOfMeasures",
				unitOfMeasureService.findAllByCompanyAndDeactivatedUnitOfMeasure(false));
		model.addAttribute("unitOfMeasures", unitOfMeasureService.findAllByCompany());
		model.addAttribute("products",
				productProfileService.findAllByCompanyAndActivatedProductProfileOrderByName(true));
		return "company/unitOfMeasures";
	}

	/**
	 * GET /unitOfMeasures/:id : get the "id" unitOfMeasure.
	 *
	 * @param id the id of the unitOfMeasureDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         unitOfMeasureDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/unitOfMeasures/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UnitOfMeasureDTO> getUnitOfMeasure(@PathVariable String pid) {
		log.debug("Web request to get UnitOfMeasure by pid : {}", pid);
		return unitOfMeasureService.findOneByPid(pid)
				.map(unitOfMeasureDTO -> new ResponseEntity<>(unitOfMeasureDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /unitOfMeasures/:id : delete the "id" unitOfMeasure.
	 *
	 * @param id the id of the unitOfMeasureDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/unitOfMeasures/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteUnitOfMeasure(@PathVariable String pid) {
		log.debug("REST request to delete UnitOfMeasure : {}", pid);
		unitOfMeasureService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("unitOfMeasure", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/unitOfMeasures/assignProducts", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedProducts(@RequestParam String pid, @RequestParam String assignedproducts) {
		log.debug("REST request to save assigned account type : {}", pid);
		unitOfMeasureProductService.save(pid, assignedproducts);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/unitOfMeasures/findProducts/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getProducts(@PathVariable String pid) {
		log.debug("REST request to get Products by producyGroupPid : {}", pid);
		List<ProductProfileDTO> unitOfMeasureProducts = unitOfMeasureProductService.findProductByUnitOfMeasurePid(pid);
		return new ResponseEntity<>(unitOfMeasureProducts, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /activities/changeStatus : update status
	 *        (Activated/Deactivated) of unitOfMeasure.
	 * 
	 * @param unitOfMeasureDTO the unitOfMeasureDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         UnitOfMeasureDTO
	 */

	@Timed
	@RequestMapping(value = "/unitOfMeasures/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UnitOfMeasureDTO> updateUnitOfMeasureStatus(
			@Valid @RequestBody UnitOfMeasureDTO unitOfMeasureDTO) {
		log.debug("REST request to change status of ProductsGroup ", unitOfMeasureDTO);
		UnitOfMeasureDTO productDTO = unitOfMeasureService.updateUnitOfMeasureStatus(unitOfMeasureDTO.getPid(),
				unitOfMeasureDTO.getActivated());
		return new ResponseEntity<>(productDTO, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        Activate STATUS /unitOfMeasures/activateUnitOfMeasure : activate
	 *        status of UnitOfMeasure.
	 * 
	 * @param productgroups the productgroups to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/unitOfMeasures/activateUnitOfMeasure", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UnitOfMeasureDTO> activateUnitOfMeasure(@Valid @RequestParam String productgroups) {
		log.debug("REST request to activate ProductsGroup ");
		String[] unitOfMeasures = productgroups.split(",");
		for (String unitOfMeasurepid : unitOfMeasures) {
			unitOfMeasureService.updateUnitOfMeasureStatus(unitOfMeasurepid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
