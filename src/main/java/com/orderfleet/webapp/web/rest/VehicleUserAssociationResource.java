package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

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
import com.orderfleet.webapp.service.UserVehicleService;
import com.orderfleet.webapp.service.VehicleService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.VehicleDTO;

@Controller
@RequestMapping("/web")
public class VehicleUserAssociationResource {

	@Inject
	private UserService userService;
	
	@Inject 
	private VehicleService vehicleService;
	
	@Inject 
	private UserVehicleService userVehicleService;
	
	@Inject 
	private EmployeeProfileService employeeProfileService;
	
	private final Logger log = LoggerFactory.getLogger(UserProductGroupResource.class);
	
	@RequestMapping(value = "/user-vehicle-association", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserProductGroups(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User ProductGroups");

		List<UserDTO> users = userService.findAllByCompany();
		model.addAttribute("users", users);
		List<EmployeeProfileDTO> employees = employeeProfileService.findAllByCompany();
		model.addAttribute("employee",employees);
		List<VehicleDTO> vechicle = vehicleService.findAllByCompany();
		System.out.println("vehicles"+vechicle);
		model.addAttribute("vehicle", vechicle);

		return "company/userVehicleAssociation";
	}
 
	@RequestMapping(value = "/user-vehicle-association/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String employeePid, @RequestParam String assignedProductGroups) {
		log.debug("REST request to save assigned assigned productGroups", employeePid);
		System.out.println("userPid"+employeePid);
		System.out.println("vehiclepid"+assignedProductGroups);
		userVehicleService.save(employeePid, assignedProductGroups);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/user-vehicle-association/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<VehicleDTO> getUserProductGroups(@PathVariable String userPid) {
		log.debug("Web request to get get ProductGroups by user pid : {}", userPid);
		return new ResponseEntity<>(userVehicleService.findVehicleByUserPid(userPid), HttpStatus.OK);
	}

}
