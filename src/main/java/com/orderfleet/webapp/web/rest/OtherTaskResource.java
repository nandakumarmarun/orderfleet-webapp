package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.FormElementUserAccount;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.repository.FormElementTypeRepository;
import com.orderfleet.webapp.repository.FormElementUserAccountRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FormElementService;
import com.orderfleet.webapp.service.FormElementUserAccountService;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.service.FormService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.FormFormElementDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class OtherTaskResource {

	private final Logger log = LoggerFactory.getLogger(OtherTaskResource.class);

	@Inject
	private FormService formService;

	@Inject
	private FormElementService formElementService;

	@Inject
	private FormFormElementService formFormElementService;

	@Inject
	private FormElementTypeRepository formElementTypeRepository;
	
	@Inject
	private UserService userService;
	
	@Inject
	private FormElementUserAccountService formElementUserAccountService;
	
	@Inject
	private FormElementUserAccountRepository formElementUserAccountRepository;
	
	/**
	 * GET /other-tasks : get other-tasks page.
	 */
	@RequestMapping(value = "/other-tasks", method = RequestMethod.GET)
	public String getOtherTasks(Model model) {
		model.addAttribute("forms", formService.findAllByCompany());
		model.addAttribute("questions", formElementService.findAllByCompany());
		model.addAttribute("formElementTypes", formElementTypeRepository.findAll());
		model.addAttribute("users", userService.findAllByCompany());
		return "company/other-tasks";
	}

	/**
	 * POST /formElements : Create a new formElement under a form.
	 *
	 * @param formElementDTO
	 *            the formElementDTO to create
	 * @param formpid
	 *            the from under formElementDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new formElementDTO, or with status 400 (Bad Request) if the
	 *         formElement has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 * @throws IOException 
	 */
	@RequestMapping(value = "/other-tasks/formElements", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<FormFormElementDTO> createFormElement(@Valid @RequestBody FormElementDTO formElementDTO,
			@RequestParam(name = "formpid", required = false) String formpid, HttpServletResponse response) throws URISyntaxException, IOException {
		log.debug("Web request to save FormElement : {}", formElementDTO);
		if (formElementDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("formElement", "idexists",
					"A new formElement cannot already have an ID")).body(null);
		}
		if (formElementService.findByName(formElementDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("formElement", "nameexists", "Question already in use"))
					.body(null);
		}
		//save 
		FormElementDTO result = formElementService.save(formElementDTO);
		FormFormElementDTO ffeDto = new FormFormElementDTO();
		if (!StringUtils.isEmpty(formpid)) {
			// assign under a form
			FormFormElement ffe = formFormElementService.save(formpid, result.getPid());
			if(ffe != null){
				ffeDto = new FormFormElementDTO(ffe);
			}
		}
		return ResponseEntity.created(new URI("/web/other-tasks/"))
				.headers(HeaderUtil.createEntityCreationAlert("formElement", result.getPid().toString())).body(ffeDto);
	}
	
	@RequestMapping(value = "/other-tasks/assign-accounts", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccountProfiles(@RequestParam String formElementPid,@RequestParam String userPid,
			@RequestParam String assignedAccountProfiles) {
		log.debug("REST request to save assigned Account Profiles : {}", assignedAccountProfiles);
		formElementUserAccountService.save(formElementPid, userPid, assignedAccountProfiles);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/other-tasks/form-element-accounts", method = RequestMethod.GET)
	@ResponseBody
	@Timed
	public List<String> getFormElementUserAccounts(@RequestParam String formElementPid,@RequestParam String userPid) {
		List<FormElementUserAccount> formElementUserAccounts = formElementUserAccountRepository.findByFormElementPidAndUserPidAndCompanyId(formElementPid, userPid, SecurityUtils.getCurrentUsersCompanyId());
		List<String> accountProfilePids = new ArrayList<>();
		if(formElementUserAccounts != null && formElementUserAccounts.size() > 0){
			accountProfilePids = formElementUserAccounts.stream().map(feua -> feua.getAccountProfile().getPid()).collect(Collectors.toList());
		}
		return accountProfilePids;
	}

}