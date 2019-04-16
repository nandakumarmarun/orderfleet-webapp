package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing EmployeeUser.
 * 
 * @author Sarath
 * @since July 9 2016
 */
@Controller
@RequestMapping("/web")
public class EmployeeUserResource {

	private final Logger log = LoggerFactory.getLogger(EmployeeUserResource.class);

	@Inject
	private UserService userService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@RequestMapping(value = "/employee-user", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserEmployee( Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Employee");
		
		model.addAttribute("employeeUsers", employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true));
		model.addAttribute("users", userService.findAllByCompany());
		return "company/employeeUser";
	}

	/**
	 * * GET /employee-user/save : save employeeUser.
	 * 
	 * @param userPid
	 * @param employeePid
	 * @return
	 */
	@RequestMapping(value = "/employee-user/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<?> save(@RequestParam String userPid, @RequestParam String employeePid) {
		log.debug("REST request to save assigned  userPid", userPid);
		Optional<EmployeeProfile>  opEmployee = employeeProfileService.findByUserPid(userPid);
		if (opEmployee.isPresent()) {
			EmployeeProfile employeeProfile = opEmployee.get();
			employeeProfile.setUser(null);
			employeeProfileService.updateEmployee(employeeProfile);
			/*return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("employeeProfile", "nameexists", "User already in use"))
					.body(null);*/
		}
		employeeProfileService.saveEmployeeUser(employeePid, userPid);
		return new ResponseEntity<>(HttpStatus.OK);

	}
	
	@RequestMapping(value = "/employee-user/check", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<?> isEmployeeAndUserAssociated(@RequestParam String userPid, @RequestParam String employeePid) {
		log.debug("REST request to chek user and employee are associated", userPid);
		EmployeeProfileDTO employee =  employeeProfileService.findDtoByUserPid(userPid);
		if (employee != null) {
			return new ResponseEntity<>(employee,HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null,HttpStatus.OK);
		}
		

	}

	@RequestMapping(value = "/employee-user/{employeePid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EmployeeProfileDTO> getEmployeeUsers(@PathVariable String employeePid) {
		log.debug("Web request to get get EmployeeProfile by executive pid : {}", employeePid);
		Optional<EmployeeProfileDTO> employeeProfile = employeeProfileService.findOneByPid(employeePid);
		return new ResponseEntity<>(employeeProfile.get(), HttpStatus.OK);
	}

}
