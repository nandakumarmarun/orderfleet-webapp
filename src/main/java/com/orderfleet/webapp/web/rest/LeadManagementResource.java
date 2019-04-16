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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.service.AccountActivityTaskConfigService;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LeadManagementDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class LeadManagementResource {

	private final Logger log = LoggerFactory.getLogger(LeadManagementResource.class);
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private LocationRepository locationRepository;
	
	@Inject
	private AccountActivityTaskConfigService activityConfigService;

	@RequestMapping(value = "/leadManagement", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Pageable pageable, Model model) {
		model.addAttribute("territories",locationRepository.findAllByCompanyId());
		return "company/leadManagement";
	}
	
	@Timed
	@RequestMapping(value = "/leadManagement/load", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LeadManagementDTO>> loadLeadManagement() {
		log.debug("Web request to load LeadManagementAccountProfiles ");
		return new ResponseEntity<>(accountProfileService
				.findAllByCompanyAndAccountTypeAccountNameType(AccountNameType.LEAD_MANAGEMENT), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/leadManagement", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<LeadManagementDTO> createLeadManagement(
			@Valid @RequestBody LeadManagementDTO leadManagementDTO) throws URISyntaxException {
		log.debug("Web request to save AccountProfile : {}", leadManagementDTO);
		if (leadManagementDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("accountProfile", "idexists",
					"A new account profile cannot already have an ID")).body(null);
		}
		if (accountProfileService.findByName(leadManagementDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "nameexists", "Account Profile already in use"))
					.body(null);
		}	
		LeadManagementDTO result = accountProfileService.saveLeadManagement(leadManagementDTO);
		//creating new task
		activityConfigService.createTaskAndSendNotification(result);

		return ResponseEntity.created(new URI("/web/leadManagement/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("lead Management", result.getPid().toString()))
				.body(result);
	}
	
	@RequestMapping(value = "/leadManagement/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<LeadManagementDTO> getLeadManagement(@PathVariable String pid) {
		log.debug("Web request to get AccountProfile by pid : {}", pid);
		return accountProfileService.findLeadManagementByPid(pid)
				.map(leadManagementDTO -> new ResponseEntity<>(leadManagementDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@RequestMapping(value = "/leadManagement", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<LeadManagementDTO> updateLeadManagement(
			@Valid @RequestBody LeadManagementDTO leadManagementDTO) {
		log.debug("Web request to update LeadManagement : {}", leadManagementDTO);
		if (leadManagementDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("leadManagement", "idNotexists", "Lead Management must have an ID"))
					.body(null);
		}
		Optional<AccountProfileDTO> existingAccountProfile = accountProfileService
				.findByName(leadManagementDTO.getName());
		if (existingAccountProfile.isPresent()
				&& (!existingAccountProfile.get().getPid().equals(leadManagementDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("leadManagement", "nameexists", "Lead Management already in use"))
					.body(null);
		}
		LeadManagementDTO result = accountProfileService.updateLeadManagement(leadManagementDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("leadManagement", "idNotexists", "Invalid Lead Management ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("leadManagement", leadManagementDTO.getPid().toString()))
				.body(result);
	}
	
	
	
}
