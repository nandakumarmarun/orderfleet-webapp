package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DepartmentService;
import com.orderfleet.webapp.service.DesignationService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * REST controller for managing EmployeeProfile.
 * 
 * @author Shaheer
 * @since June 06, 2016
 */
@Controller
@RequestMapping("/web")
public class EmployeeProfileResource {

	private final Logger log = LoggerFactory.getLogger(EmployeeProfileResource.class);

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private DesignationService designationService;

	@Inject
	private DepartmentService departmentService;
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	/**
	 * POST /employee-profiles : Create a new employeeProfile.
	 *
	 * @param employeeProfileDTO
	 *            the employeeProfileDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new employeeProfileDTO, or with status 400 (Bad Request) if the
	 *         employeeProfile has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/employee-profiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EmployeeProfileDTO> createEmployeeProfile(
			@Valid @RequestBody EmployeeProfileDTO employeeProfileDTO) throws URISyntaxException {
		log.debug("REST request to save EmployeeProfile : {}", employeeProfileDTO);
		if (employeeProfileDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("employeeProfile", "idexists",
					"A new employeeProfile cannot already have an ID")).body(null);
		}
		if (employeeProfileService.findByName(employeeProfileDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("employeeProfile", "nameexists", "Employee Profile already in use"))
					.body(null);
		}
		employeeProfileDTO.setActivated(true);
		EmployeeProfileDTO result = employeeProfileService.save(employeeProfileDTO);
		return ResponseEntity.created(new URI("/api/employee-profiles/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("employeeProfile", result.getPid())).body(result);
	}

	/**
	 * PUT /employee-profiles : Updates an existing employeeProfile.
	 *
	 * @param employeeProfileDTO
	 *            the employeeProfileDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         employeeProfileDTO, or with status 400 (Bad Request) if the
	 *         employeeProfileDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the employeeProfileDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/employee-profiles", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EmployeeProfileDTO> updateEmployeeProfile(
			@Valid @RequestBody EmployeeProfileDTO employeeProfileDTO) throws URISyntaxException {
		log.debug("Web request to update EmployeeProfile : {}", employeeProfileDTO);
		if (employeeProfileDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("employeeProfile", "idNotexists", "Employee Profile must have an ID"))
					.body(null);
		}
		Optional<EmployeeProfileDTO> existingEmployeeProfile = employeeProfileService
				.findByName(employeeProfileDTO.getName());
		if (existingEmployeeProfile.isPresent()
				&& (!existingEmployeeProfile.get().getPid().equals(employeeProfileDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("employeeProfile", "nameexists", "Employee Profile already in use"))
					.body(null);
		}
		EmployeeProfileDTO result = employeeProfileService.update(employeeProfileDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("employeeProfile", "idNotexists", "Invalid Employee Profile ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("employeeProfile", employeeProfileDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /employee-profiles : get all the employeeProfiles.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         employeeProfiles in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/employee-profiles", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllEmployeeProfiles(Pageable pageable, Model model) {
		log.debug("REST request to get a page of EmployeeProfiles");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if(userIds.isEmpty()) {
			model.addAttribute("employee", employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true));
		} else {
			if(userIds.size()>1){
				List<EmployeeProfileDTO>employeeProfileDTOs=new ArrayList<>();
				List<UserDTO> users=userService.findByUserIdIn(userIds);
				for(UserDTO userDTO:users){
					EmployeeProfileDTO employeeProfileDTO=employeeProfileService.findEmployeeProfileByUserLogin(userDTO.getLogin());
					employeeProfileDTOs.add(employeeProfileDTO);
				}
				model.addAttribute("employee", employeeProfileDTOs);
			}else{
			List<EmployeeProfileDTO>employeeProfileDTOs=new ArrayList<>();
			User userLogin=userService.getCurrentUser();
			EmployeeProfileDTO employeeProfileDTO=employeeProfileService.findEmployeeProfileByUserLogin(userLogin.getLogin());
			employeeProfileDTOs.add(employeeProfileDTO);
			model.addAttribute("employee", employeeProfileDTOs);
			}
		}
		List<EmployeeProfileDTO> deactivatedEmployeeProfiles = employeeProfileService
				.findAllByCompanyAndDeactivatedEmployeeProfile(false);
		model.addAttribute("deactivatedEmployeeProfiles", deactivatedEmployeeProfiles);
		model.addAttribute("designations", designationService.findAllByCompany());
		model.addAttribute("departments", departmentService.findAllByCompany());
		Optional<CompanyConfiguration> optCreateEmployeeBtn = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.EMPLOYEE_CREATE_BTN);
		if(optCreateEmployeeBtn.isPresent()) {
			model.addAttribute("CreateEmployeeBtn",Boolean.valueOf(optCreateEmployeeBtn.get().getValue()));
		}
		return "company/employee-profiles";
	}

	/**
	 * GET /employee-profiles/:id : get the "id" employeeProfile.
	 *
	 * @param id
	 *            the id of the employeeProfileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         employeeProfileDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/employee-profiles/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EmployeeProfileDTO> getEmployeeProfile(@PathVariable String pid) {
		log.debug("Web request to get EmployeeProfile by pid : {}", pid);
		return employeeProfileService.findOneByPid(pid)
				.map(employeeProfileDTO -> new ResponseEntity<>(employeeProfileDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /employee-profiles/:pid : delete the "pid" employeeProfile.
	 *
	 * @param pid
	 *            the pid of the employeeProfileDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/employee-profiles/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteEmployeeProfile(@PathVariable String pid) {
		log.debug("REST request to delete EmployeeProfile : {}", pid);
		employeeProfileService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("employeeProfile", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/employee-profile-image", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EmployeeProfileDTO> getCurrentUserEmployeeProfileImage() {
		log.debug("Web request to get Current User EmployeeProfileImage");
		return new ResponseEntity<>(employeeProfileService.findtCurrentUserEmployeeProfileImage(), HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017 UPDATE STATUS
	 *        /employee-profiles/changeStatus:employeeProfileDTO : update status
	 *        of employeeProfile.
	 * 
	 * @param employeeProfileDTO
	 *            the employeeProfileDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/employee-profiles/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeProfileDTO> updateActivityGroupStatus(
			@Valid @RequestBody EmployeeProfileDTO employeeProfileDTO) {
		log.debug("request to update EmployeeProfile status", employeeProfileDTO);
		EmployeeProfileDTO res = employeeProfileService.updateEmployeeProfileStatus(employeeProfileDTO.getPid(),
				employeeProfileDTO.getActivated());
		return new ResponseEntity<EmployeeProfileDTO>(res, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS /employee-profiles/activateEmployeeProfile :
	 *        activate status of employeeProfile.
	 * 
	 * @param employeeprofiles
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/employee-profiles/activateEmployeeProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeProfileDTO> activateEmployeeProfile(@Valid @RequestParam String employeeprofiles) {
		log.debug("request to Activate EmployeeProfile status");
		String[] employeeprofile = employeeprofiles.split(",");
		for (String employee : employeeprofile) {
			employeeProfileService.updateEmployeeProfileStatus(employee, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
