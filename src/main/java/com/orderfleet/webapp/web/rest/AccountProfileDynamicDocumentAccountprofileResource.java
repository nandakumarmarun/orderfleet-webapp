package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

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
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.service.AccountProfileDynamicDocumentAccountprofileService;
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.web.rest.api.dto.FormFormElementDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDynamicDocumentAccountprofileDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentFormDTO;

/**
 * WEB controller for managing the current
 * AccountProfileDynamicDocumentAccountprofile.
 *
 * @author Sarath
 * @since Feb 5, 2018
 *
 */
@Controller
@RequestMapping("/web")
public class AccountProfileDynamicDocumentAccountprofileResource {

	private final Logger log = LoggerFactory.getLogger(AccountProfileDynamicDocumentAccountprofileResource.class);

	@Inject
	private DocumentService documentService;

	@Inject
	private DocumentFormsService documentFormsService;

	@Inject
	private FormFormElementService formElementService;

	@Inject
	private AccountProfileDynamicDocumentAccountprofileService accountProfileDynamicDocumentAccountprofileService;

	@RequestMapping(value = "/accountprofile-dynamicDocument-accountprofile", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfileDynamicDocumentAccountprofiles(Pageable pageable, Model model)
			throws URISyntaxException {
		log.debug("Web request to get of AccountProfileDynamicDocumentAccountprofileResource");
		model.addAttribute("dynamicdocuments", documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT));
//		model.addAttribute("accountProfileFields", Arrays.asList(
//				"Name,Alias,City,Location,Pin,Phone 1,Phone 2,E-Mail 1,E-Mail 2,Address,Description,Credit Days,Closing Balance,Default Discount Percentage,Credit Limits,Contact Person"));
		model.addAttribute("accountProfileDynamicDocumentAccountprofiles",
				accountProfileDynamicDocumentAccountprofileService.findAllByCompanyMapped());
		return "company/accountProfileDynamicDocumentAccountprofile";
	}

	@RequestMapping(value = "/accountprofile-dynamicDocument-accountprofile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<List<AccountProfileDynamicDocumentAccountprofileDTO>> createAccountGroup(
			@Valid @RequestBody List<AccountProfileDynamicDocumentAccountprofileDTO> documentAccountprofileDTOs)
			throws URISyntaxException {
		log.debug("Web request to save AccountProfileDynamicDocumentAccountprofileResource : {}",
				documentAccountprofileDTOs.size());

		List<AccountProfileDynamicDocumentAccountprofileDTO> result = accountProfileDynamicDocumentAccountprofileService
				.save(documentAccountprofileDTOs);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/accountprofile-dynamicDocument-accountprofile/getOne", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDynamicDocumentAccountprofileDTO>> getAccountProfileDynamicDocumentAccountprofileDTO(
			@RequestParam(value = "formPid") String formPid, @RequestParam(value = "documentPid") String documentPid) {
		log.debug("Web request to get AccountProfileDynamicDocumentAccountprofile by document and form pid : {}",
				documentPid, formPid);
		return new ResponseEntity<>(
				accountProfileDynamicDocumentAccountprofileService.findAllByDocumentPidAndFormPid(documentPid, formPid),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/accountprofile-dynamicDocument-accountprofile/getForms/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentFormDTO>> getForms(@PathVariable String pid) {
		log.debug("Web request to get Form by dynamic document pid : {}", pid);
		return new ResponseEntity<>(documentFormsService.findByDocumentPid(pid), HttpStatus.OK);
	}

	@RequestMapping(value = "/accountprofile-dynamicDocument-accountprofile/getFormElements/{formPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<FormFormElementDTO>> getFormElements(@PathVariable String formPid) {
		log.debug("Web request to get FormElements by form pid : {}", formPid);
		List<FormFormElementDTO> formFormElements = formElementService.findOneByFormPid(formPid);
		return new ResponseEntity<>(formFormElements, HttpStatus.OK);
	}
}
