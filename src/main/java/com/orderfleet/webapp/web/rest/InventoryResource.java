package com.orderfleet.webapp.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;

/**
 * Web controller for creating Inventory documents.
 * 
 * @author Shaheer
 * @since October 17, 2016
 */
@Controller
@RequestMapping("/web")
public class InventoryResource {

	private final Logger log = LoggerFactory.getLogger(InventoryResource.class);

	private EmployeeProfileService employeeProfileService;
	
	private UserActivityService userActivityService;
	
	private AccountProfileRepository accountProfileRepository;
	
	private DocumentRepository documentRepository;
	
	@Inject
	public InventoryResource(EmployeeProfileService employeeProfileService, UserActivityService userActivityService,
			AccountProfileRepository accountProfileRepository, DocumentRepository documentRepository) {
		super();
		this.employeeProfileService = employeeProfileService;
		this.userActivityService = userActivityService;
		this.accountProfileRepository = accountProfileRepository;
		this.documentRepository = documentRepository;
	}

	/**
	 * GET /inventory : get inventory form.
	 * 
	 */
	@RequestMapping(value = "/inventory", method = RequestMethod.GET)
	@Timed
	public String getInventoryCreationForm(Model model) {
		log.debug("Web request to get a form to create inventory documents");
		model.addAttribute("employees", employeeProfileService.findAllByCompany());
		model.addAttribute("accounts", accountProfileRepository.findAllByCompanyId());
		model.addAttribute("documents", documentRepository.findAllByCompanyId());
		return "company/inventory";
	}
	
	 /**
     * GET  /activities-by-employee/:employeePid : get activity of an employee.
     *
     * @param employeePid the id of the employee 
     * @return the ResponseEntity with status 200 (OK) and the list of activities in body
     */
    @RequestMapping(value = "/activities-by-employee/{employeePid}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    public List<ActivityDTO> getActivityByEmployee(@PathVariable String employeePid) {
    	log.debug("Web request to get Activity by employeePid : {}", employeePid);
        return userActivityService.findActivitiesByUserPid(employeePid);
    }


}
