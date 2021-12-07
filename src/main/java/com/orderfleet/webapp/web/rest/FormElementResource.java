package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.FormElementMasterType;
import com.orderfleet.webapp.domain.enums.LoadMobileData;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.FormElementMasterRepository;
import com.orderfleet.webapp.repository.FormElementMasterTypeRepository;
import com.orderfleet.webapp.repository.FormElementTypeRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FormElementService;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing FormElement.
 * 
 * @author Muhammed Riyas T
 * @since June 23, 2016
 */
@Controller
@RequestMapping("/web")
public class FormElementResource {

	private final Logger log = LoggerFactory.getLogger(FormElementResource.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private FormElementService formElementService;

	@Inject
	private FormElementTypeRepository formElementTypeRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private FormElementMasterTypeRepository formElementMasterTypeRepository;

	@Inject
	private FormElementMasterRepository formElementMasterRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	/**
	 * POST /formElements : Create a new formElement.
	 *
	 * @param formElementDTO
	 *            the formElementDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         formElementDTO, or with status 400 (Bad Request) if the formElement
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/formElements", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<FormElementDTO> createFormElement(@Valid @RequestBody FormElementDTO formElementDTO)
			throws URISyntaxException {
		log.debug("Web request to save FormElement : {}", formElementDTO);

		System.out.println(
				formElementDTO.getFormLoadFromMobile() + "----------------" + formElementDTO.getFormLoadMobileData());
		if (formElementDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("formElement", "idexists",
					"A new formElement cannot already have an ID")).body(null);
		}
		if (formElementService.findByName(formElementDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("formElement", "nameexists", "Question already in use"))
					.body(null);
		}
		formElementDTO.setActivated(true);
		FormElementDTO result = formElementService.save(formElementDTO);
		return ResponseEntity.created(new URI("/web/formElements/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("formElement", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /formElements : Updates an existing formElement.
	 *
	 * @param formElementDTO
	 *            the formElementDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         formElementDTO, or with status 400 (Bad Request) if the
	 *         formElementDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the formElementDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/formElements", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<FormElementDTO> updateFormElement(@Valid @RequestBody FormElementDTO formElementDTO)
			throws URISyntaxException {
		log.debug("Web request to update FormElement : {}", formElementDTO);
		System.out.println(
				formElementDTO.getFormLoadFromMobile() + "************" + formElementDTO.getFormLoadMobileData());
		if (formElementDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("formElement", "idNotexists", "FormElement must have an ID"))
					.body(null);
		}
		Optional<FormElementDTO> existingFormElement = formElementService.findByName(formElementDTO.getName());
		if (existingFormElement.isPresent() && (!existingFormElement.get().getPid().equals(formElementDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("designation", "nameexists", "Question already in use"))
					.body(null);
		}
		FormElementDTO result = formElementService.update(formElementDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("formElement", "idNotexists", "Invalid FormElement ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("formElement", formElementDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /formElements : get all the formElements.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of formElements
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/formElements", method = RequestMethod.GET)
	public String getAllFormElements(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of FormElements");
		List<FormElementDTO> formElements = formElementService.findAllByCompanyAndDeactivatedFormElement(true);

		model.addAttribute("formElements", formElements);
		model.addAttribute("formElementTypes", formElementTypeRepository.findAll());
		model.addAttribute("loadMobileDataList", LoadMobileData.values());
		model.addAttribute("deactivatedFormElements",
				formElementService.findAllByCompanyAndDeactivatedFormElement(false));
		return "company/formElements";
	}

	/**
	 * GET /formElements/:id : get the "id" formElement.
	 *
	 * @param id
	 *            the id of the formElementDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         formElementDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/formElements/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<FormElementDTO> getFormElement(@PathVariable String pid) {
		log.debug("Web request to get FormElement by pid : {}", pid);
		return formElementService.findOneByPid(pid)
				.map(formElementDTO -> new ResponseEntity<>(formElementDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /formElements/:id : delete the "id" formElement.
	 *
	 * @param id
	 *            the id of the formElementDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/formElements/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteFormElement(@PathVariable String pid) {
		log.debug("REST request to delete FormElement : {}", pid);
		formElementService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("formElement", pid.toString())).build();
	}

	@Timed
	@RequestMapping(value = "/formElements/setDefaultValue", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormElementDTO> setDefaultValue(@RequestParam String pid, @RequestParam String defaultValue) {
		log.debug("Request to set Default Value");
		formElementService.saveDefaultValue(pid, defaultValue);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /formElements/master-table-data/:selectedMaster.
	 *
	 * @param selectedMaster
	 * @return the ResponseEntity with status 200 (OK) and with body the masters
	 *         string, or with status 404 (Not Found)
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/formElements/master-table-data/{selectedMaster}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getMasterTableOptions(@PathVariable String selectedMaster) {
		log.debug("Web request to get Master Table Options");
		List<String> masters = null;
		if (selectedMaster.equals("ProductCategory")) {
			masters = productCategoryRepository.findCategoryNamesByCompanyId();
		} else if (selectedMaster.equals("ProductGroup")) {
			masters = productGroupRepository.findGroupNamesByCompanyId();
		} else if (selectedMaster.equals("ProductProfile")) {
			masters = productProfileRepository.findProductNamesByCompanyId();
		} else if (selectedMaster.equals("AccountProfile")) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_110" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get accNames by CompId";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			masters = accountProfileRepository.findAccountNamesByCompanyId();
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
		} else if (selectedMaster.equals("Executives")) {
			masters = employeeProfileRepository.findEmployeeNamesByCompanyId();
		} else if (selectedMaster.equals("OTHER")) {
			List<FormElementMasterType> formElementMasterTypes = formElementMasterTypeRepository.findAllByCompanyId();
			masters = new ArrayList<>();
			for (FormElementMasterType formElementMasterType : formElementMasterTypes) {
				masters.add(formElementMasterType.getPid() + "~" + formElementMasterType.getName());
			}
		}
		return new ResponseEntity<>(masters, HttpStatus.OK);
	}

	/**
	 * GET /formElements/master-table-data/:selectedMaster.
	 *
	 * @param selectedMaster
	 * @return the ResponseEntity with status 200 (OK) and with body the masters
	 *         string, or with status 404 (Not Found)
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/formElements/form-element-masters/{masterTypePid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getFromElementMasters(@PathVariable String masterTypePid) {
		log.debug("Web request to get form element masters");
		List<String> masters = formElementMasterRepository.findAllNamesByCompanyIdAndTypePid(masterTypePid);
		return new ResponseEntity<>(masters, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 8, 2017
	 * 
	 *        UPDATE STATUS /formElements/changeStatus:FormElementDTO : update
	 *        status of FormElement.
	 * 
	 * @param FormElementDTO
	 *            the FormElementDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/formElements/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormElementDTO> updateFormElementStatus(@Valid @RequestBody FormElementDTO formElementDTO) {
		log.debug("Request to update the status of FormElement", formElementDTO);
		FormElementDTO result = formElementService.updateFormElementStatus(formElementDTO.getPid(),
				formElementDTO.getActivated());
		return new ResponseEntity<FormElementDTO>(result, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        Activate STATUS /formElements/activateFormElement : activate status of
	 *        FormElement.
	 * 
	 * @param formelements
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/formElements/activateFormElement", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormElementDTO> activateFormElement(@Valid @RequestParam String formelements) {
		log.debug("Request to activate FormElement");
		String[] formElement = formelements.split(",");
		for (String formpid : formElement) {
			formElementService.updateFormElementStatus(formpid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
