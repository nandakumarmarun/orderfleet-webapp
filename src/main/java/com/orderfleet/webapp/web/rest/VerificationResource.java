package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;

/**
 * Web controller for managing Verification.
 * 
 * @author Muhammed Riyas T
 * @since October 05, 2016
 */
@Controller
@RequestMapping("/web")
public class VerificationResource {

	private final Logger log = LoggerFactory.getLogger(VerificationResource.class);
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private ActivityService activityService;

	@Inject
	private ExecutiveTaskExecutionService executiveTaskExecutionService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	/**
	 * GET /verification : get all the executive task executions.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of executive
	 *         task execution in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/verification", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public String getAllExecutiveTaskExecutions(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of executive task executions");
		model.addAttribute("activities", activityService.findAllByCompany());
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/verification";
	}

	@Timed
	@RequestMapping(value = "/verification/accept/{pid}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> accept(@PathVariable String pid) {
		log.debug("Web request to accept : {}", pid);
		executiveTaskExecutionService.changeActivityStatus(pid, ActivityStatus.ACCEPTED, "");
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/verification/reject", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> reject(@RequestParam String pid, @RequestParam String reason) {
		log.debug("Web request to accept : {}", pid);
		executiveTaskExecutionService.changeActivityStatus(pid, ActivityStatus.REJECTED, reason);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/verification/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<List<ExecutiveTaskExecutionDTO>> filterExecutiveTaskExecutions(
			@RequestParam("employeePid") String employeePid, @RequestParam("activityPid") String activityPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter executive task executions");

		List<ExecutiveTaskExecutionDTO> executiveTaskExecutions = new ArrayList<ExecutiveTaskExecutionDTO>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterData(employeePid, activityPid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterData(employeePid, activityPid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			executiveTaskExecutions = getFilterData(employeePid, activityPid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			executiveTaskExecutions = getFilterData(employeePid, activityPid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, activityPid, fromDateTime, toFateTime);
		}
		return new ResponseEntity<>(executiveTaskExecutions, HttpStatus.OK);
	}

	private List<ExecutiveTaskExecutionDTO> getFilterData(String employeePid, String activityPid, LocalDate fDate,
			LocalDate tDate) {
		 boolean companyconfig = getCompanyCofig();
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<ExecutiveTaskExecution>();
		List<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTOs = new ArrayList<>();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}
		if (userPid.equals("no") && activityPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdAndDateBetweenOrderByDateDesc(fromDate, toDate);
		} else if (!userPid.equals("no") && !activityPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidActivityPidAndDateBetweenOrderByDateDesc(userPid, activityPid, fromDate,
							toDate);
		} else if (!userPid.equals("no") && activityPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidAndDateBetweenOrderByDateDesc(userPid, fromDate, toDate);
		} else if (userPid.equals("no") && !activityPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdActivityPidAndDateBetweenOrderByDateDesc(activityPid, fromDate, toDate);
		}
		
		for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
			EmployeeProfileDTO employeeProfileDTO2 = employeeProfileService
					.findEmployeeProfileByUserLogin(executiveTaskExecution.getUser().getLogin());
			if (employeeProfileDTO2 != null) {
				ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO(
						executiveTaskExecution);
				executiveTaskExecutionDTO.setEmployeeName(employeeProfileDTO2.getName());
				if(companyconfig)
				{
				executiveTaskExecutionDTO.setAccountProfileName(executiveTaskExecutionDTO.getDescription());
				}
				else
				{
					executiveTaskExecutionDTO.setAccountProfileName(executiveTaskExecutionDTO.getAccountProfileName());
				}
				executiveTaskExecutionDTOs.add(executiveTaskExecutionDTO);
			}
		}

		return executiveTaskExecutionDTOs;
	}
	

	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}
}
