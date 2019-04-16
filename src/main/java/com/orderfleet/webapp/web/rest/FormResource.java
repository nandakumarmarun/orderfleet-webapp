package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.FormElementService;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.service.FormService;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementOrderDTO;
import com.orderfleet.webapp.web.rest.dto.FormFormElementOrderDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Form.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Controller
@RequestMapping("/web")
public class FormResource {

	private final Logger log = LoggerFactory.getLogger(FormResource.class);

	@Inject
	private FormService formService;

	@Inject
	private DocumentService documentService;

	@Inject
	private FormElementService formElementService;

	@Inject
	private FormFormElementService formFormElementService;

	/**
	 * POST /forms : Create a new form.
	 *
	 * @param formDTO
	 *            the formDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new formDTO, or with status 400 (Bad Request) if the form has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/forms", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<FormDTO> createForm(@Valid @RequestBody FormDTO formDTO) throws URISyntaxException {
		log.debug("Web request to save Form : {}", formDTO);
		if (formDTO.getPid() != null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("form", "idexists", "A new form cannot already have an ID"))
					.body(null);
		}
		if (formService.findByName(formDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("form", "nameexists", "Form already in use")).body(null);
		}
		formDTO.setActivated(true);
		FormDTO result = formService.save(formDTO);
		return ResponseEntity.created(new URI("/web/forms/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("form", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /forms : Updates an existing form.
	 *
	 * @param formDTO
	 *            the formDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         formDTO, or with status 400 (Bad Request) if the formDTO is not
	 *         valid, or with status 500 (Internal Server Error) if the formDTO
	 *         couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/forms", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<FormDTO> updateForm(@Valid @RequestBody FormDTO formDTO) throws URISyntaxException {
		log.debug("Web request to update Form : {}", formDTO);
		if (formDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("form", "idNotexists", "Form must have an ID")).body(null);
		}
		Optional<FormDTO> existingForm = formService.findByName(formDTO.getName());
		if (existingForm.isPresent() && (!existingForm.get().getPid().equals(formDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("designation", "nameexists", "Form already in use"))
					.body(null);
		}
		FormDTO result = formService.update(formDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("form", "idNotexists", "Invalid Form ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("form", formDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /forms : get all the forms.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of forms in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/forms", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllForms(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Forms");
		List<FormDTO> forms = formService.findAllByCompanyAndDeactivatedForm(true);
		model.addAttribute("forms", forms);
		model.addAttribute("deactivatedForms", formService.findAllByCompanyAndDeactivatedForm(false));
		model.addAttribute("documents", documentService.findAllByCompany());
		model.addAttribute("questions", formElementService.findAllByCompany());
		return "company/forms";
	}

	/**
	 * @updated sarath
	 * @date July 29 2016
	 * 
	 *       GET /forms/:id : get the "id" form.
	 *
	 * @param pid
	 *            the id of the formDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         FormElementDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/forms/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<FormDTO> getForm(@PathVariable String pid) {
		log.debug("Web request to get Form by pid : {}", pid);

		// List<FormFormElement> formFormElements =
		// formFormElementService.findByFormPid(pid);

		return formService.findOneByPid(pid).map(formDTO -> new ResponseEntity<>(formDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

	}

	/**
	 * @updated sarath
	 * @date July 29 2016
	 * 
	 *       GET /forms/:id : get the "id" form.
	 *
	 * @param pid
	 *            the id of the formDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         FormElementDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/forms/formElements/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<FormElementDTO>> getFormElements(@PathVariable String pid) {
		log.debug("Web request to get Form by pid : {}", pid);

		List<FormFormElement> formFormElements = formFormElementService.findByFormPid(pid);
		List<FormElementDTO> listFormElementDTOs = new ArrayList<FormElementDTO>();
		for (FormFormElement formFormElement : formFormElements) {
			FormElementDTO formElementDTO = new FormElementDTO();
			formElementDTO.setName(formFormElement.getFormElement().getName());
			listFormElementDTOs.add(formElementDTO);
		}
		return new ResponseEntity<>(listFormElementDTOs, HttpStatus.OK);

	}

	/**
	 * DELETE /forms/:id : delete the "id" form.
	 *
	 * @param id
	 *            the id of the formDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/forms/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteForm(@PathVariable String pid) {
		log.debug("REST request to delete Form : {}", pid);
		formService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("form", pid.toString())).build();
	}

	/**
	 * GET /forms/form-elements/:pid : get the "pid" form.
	 *
	 * @param pid
	 *            the id of the formDTO to retrieve FormElements
	 * @return the list of FormElementOrderDTO
	 */
	@RequestMapping(value = "/forms/form-elements/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<FormElementOrderDTO>> getAssignedFormElements(@PathVariable String pid) {
		log.debug("Web request to get Form by pid : {}", pid);
		List<FormFormElement> formFormElements = formFormElementService.findByFormPid(pid);
		List<FormElementOrderDTO> formElementOrderDTOs = formFormElements.stream().map(FormElementOrderDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(formElementOrderDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/forms/assignQuestions", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedQuestions(@RequestBody FormFormElementOrderDTO formFormElementDTO) {
		log.debug("REST request to save assigned questions : {}", formFormElementDTO.getFormPid());
		formService.saveAssignedQuestions(formFormElementDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 8, 2017
	 * 
	 *        UPDATE STATUS /forms/changeStatus:FormDTO : update status of Form.
	 * 
	 * @param FormDTO
	 *            the FormDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/forms/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormDTO> updateFormStatus(@Valid @RequestBody FormDTO formDTO) {
		log.debug("request to update status of form", formDTO);
		FormDTO result = formService.updateFormStatus(formDTO.getPid(), formDTO.getActivated());
		return new ResponseEntity<FormDTO>(result, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        Activate STATUS /forms/activateForm: activate status of
	 *        FormElement.
	 * 
	 * @param forms
	 *            the forms to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/forms/activateForm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormDTO> activateForm(@Valid @RequestParam String forms) {
		log.debug("request to activate form");
		String[] form = forms.split(",");
		for (String formpid : form) {
			formService.updateFormStatus(formpid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}