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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.FormElementMasterTypeService;

import com.orderfleet.webapp.web.rest.dto.FormElementMasterTypeDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing FormElementMaster.
 *
 * @author Sarath
 * @since Nov 2, 2016
 */

@Controller
@RequestMapping("/web")
public class FormElementMasterTypeResource {

	private final Logger log = LoggerFactory.getLogger(FormElementMasterTypeResource.class);

	@Inject
	private FormElementMasterTypeService formElementMasterService;

	/**
	 * POST /form-element-master-type : Create a new formElementMaster.
	 *
	 * @param formElementMasterDTO
	 *            the formElementMasterDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new formElementMasterDTO, or with status 400 (Bad Request) if the
	 *         formElementMaster has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/form-element-master-type", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<FormElementMasterTypeDTO> createFormElementMaster(
			@Valid @RequestBody FormElementMasterTypeDTO formElementMasterDTO) throws URISyntaxException {
		log.debug("Web request to save FormElementMaster : {}", formElementMasterDTO);
		if (formElementMasterDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("formElementMaster", "idexists",
					"A new formElementMaster cannot already have an ID")).body(null);
		}
		if (formElementMasterService.findByName(formElementMasterDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("formElementMaster", "nameexists",
					"FormElementMaster already in use")).body(null);
		}
		formElementMasterDTO.setActivated(true);
		FormElementMasterTypeDTO result = formElementMasterService.save(formElementMasterDTO);
		return ResponseEntity.created(new URI("/web/form-element-master-type/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("formElementMaster", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /form-element-master-type : Updates an existing formElementMaster.
	 *
	 * @param formElementMasterDTO
	 *            the formElementMasterDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         formElementMasterDTO, or with status 400 (Bad Request) if the
	 *         formElementMasterDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the formElementMasterDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/form-element-master-type", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<FormElementMasterTypeDTO> updateFormElementMaster(
			@Valid @RequestBody FormElementMasterTypeDTO formElementMasterDTO) throws URISyntaxException {
		log.debug("REST request to update FormElementMaster : {}", formElementMasterDTO);
		if (formElementMasterDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("formElementMaster", "idNotexists",
					"FormElementMaster must have an ID")).body(null);
		}
		Optional<FormElementMasterTypeDTO> existingFormElementMaster = formElementMasterService
				.findByName(formElementMasterDTO.getName());
		if (existingFormElementMaster.isPresent()
				&& (!existingFormElementMaster.get().getPid().equals(formElementMasterDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("formElementMaster", "nameexists",
					"FormElementMaster already in use")).body(null);
		}
		FormElementMasterTypeDTO result = formElementMasterService.update(formElementMasterDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("formElementMaster", "idNotexists", "Invalid FormElementMaster ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert("formElementMaster", formElementMasterDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /form-element-master-type : get all the formElementMasters.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         formElementMasters in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/form-element-master-type", method = RequestMethod.GET)
	@Timed
	public String getAllFormElementMasters(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of FormElementMasters");
		List<FormElementMasterTypeDTO> formElementMasters = formElementMasterService
				.findAllByCompanyAndDeactivatedFormElementMasterType(true);
		List<FormElementMasterTypeDTO> formElementMasterTypeDTOs = formElementMasterService
				.findAllByCompanyAndDeactivatedFormElementMasterType(false);
		model.addAttribute("formElementMasters", formElementMasters);
		model.addAttribute("deactivatedFormElementMasters", formElementMasterTypeDTOs);
		return "company/formElementMasterType";
	}

	/**
	 * GET /form-element-master-type/:pid : get the "pid" formElementMaster.
	 *
	 * @param pid
	 *            the pid of the formElementMasterDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         formElementMasterDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/form-element-master-type/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<FormElementMasterTypeDTO> getFormElementMaster(@PathVariable String pid) {
		log.debug("Web request to get FormElementMaster by pid : {}", pid);
		return formElementMasterService.findOneByPid(pid)
				.map(formElementMasterDTO -> new ResponseEntity<>(formElementMasterDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /form-element-master-type/:id : delete the "id" formElementMaster.
	 *
	 * @param id
	 *            the id of the formElementMasterDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/form-element-master-type/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteFormElementMaster(@PathVariable String pid) {
		log.debug("REST request to delete FormElementMaster : {}", pid);
		formElementMasterService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("formElementMaster", pid.toString()))
				.build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS
	 *        /form-element-master-type/changeStatus:formElementMasterTypeDTO :
	 *        update status of formElementMasterType.
	 * 
	 * @param formElementMasterTypeDTO
	 *            the formElementMasterTypeDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/form-element-master-type/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormElementMasterTypeDTO> updateformElementMasterTypeStatus(
			@Valid @RequestBody FormElementMasterTypeDTO formElementMasterTypeDTO) {
		log.debug("Web request to update status formElementMasterType : {}", formElementMasterTypeDTO);
		FormElementMasterTypeDTO res = formElementMasterService.updateFormElementMasterTypeStatus(
				formElementMasterTypeDTO.getPid(), formElementMasterTypeDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS
	 *        /form-element-master-type/activateFormElementMaster : activate
	 *        status of FormElementMasterType.
	 * 
	 * @param formelementmaster
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/form-element-master-type/activateFormElementMaster", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormElementMasterTypeDTO> activateFormElementMasterType(
			@Valid @RequestParam String formelementmaster) {
		log.debug("Web request to activate status formElementMasterType ");
		String[] formElement = formelementmaster.split(",");
		for (String fString : formElement) {
			formElementMasterService.updateFormElementMasterTypeStatus(fString, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
