package com.orderfleet.webapp.web.rest;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import com.orderfleet.webapp.domain.CompanyAttributes;
import com.orderfleet.webapp.repository.CompanyAttributesRepository;
import com.orderfleet.webapp.service.*;
import com.orderfleet.webapp.web.rest.dto.CustomerAttributesDTO;
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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.EmployeeHierarchyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class VerifyAccountProfileResource {

	private final Logger log = LoggerFactory.getLogger(VerifyAccountProfileResource.class);

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeHierarchyRepository employeeHierarchyRepository;

	@Inject
	private CompanyAttributesRepository companyAttributesRepository;

	@Inject
	private CustomerAttributesService customerAttributesService;

	/**
	 * GET /verify-account-profile : get all the VerifyAccountProfiles.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         verifyAccountProfiles in body
	 * @throws URISyntaxException
	 * 
	 */
	@RequestMapping(value = "/verify-account-profile", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of VerifyAccountProfiles");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("accountTypes", accountTypeService.findAllCompanyAndAccountTypeActivated(true));
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			List<AccountProfileDTO> accountProfileDTOs1 = locationAccountProfileService
					.findAccountProfilesByCurrentUserLocations();
			List<String> accounttypePids = new ArrayList<>();
			for (AccountProfileDTO accountProfileDTO : accountProfileDTOs1) {
				accounttypePids.add(accountProfileDTO.getAccountTypePid());
			}
			if (!accounttypePids.isEmpty()) {
				model.addAttribute("accountTypes", accountTypeService.findAllByAccountTypePidIn(accounttypePids));
			}
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		List<CustomerAttributesDTO> companyAttributes = customerAttributesService.getAllCompanyAttributes();
		model.addAttribute("attributes",companyAttributes);
		return "company/verifyAccountProfile";
	}

	@RequestMapping(value = "/verify-account-profile/filterByAccountType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<AccountProfileDTO>> filterAccountProfilesByAccountTypes(
			@RequestParam String accountTypePids, @RequestParam String includeInActive) throws URISyntaxException {
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		if (includeInActive.equals("no")) {
			if (userIds.isEmpty()) {
				accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndAccountImportStatus(false));
			} else {
				List<AccountProfileDTO> accountProfileDTOs1 = locationAccountProfileService
						.findAccountProfilesByCurrentUserLocations();
				for (AccountProfileDTO accountProfileDTO : accountProfileDTOs1) {
					if (accountProfileDTO.getIsImportStatus() == false) {
						accountProfileDTOs.add(accountProfileDTO);
					}
				}
			}
			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		} else {
			if (accountTypePids.isEmpty()) {
				if (userIds.isEmpty()) {
					accountProfileDTOs
							.addAll(accountProfileService.findAllByCompanyAndAccountImportStatusAndActivated(false));
				} else {
					List<AccountProfileDTO> accountProfileDTOs1 = locationAccountProfileService
							.findAccountProfilesByCurrentUserLocations();
					for (AccountProfileDTO accountProfileDTO : accountProfileDTOs1) {
						if (accountProfileDTO.getIsImportStatus() == false
								&& accountProfileDTO.getActivated() == true) {
							accountProfileDTOs.add(accountProfileDTO);
						}
					}
				}
				return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
			} else {
				if (userIds.isEmpty()) {
					accountProfileDTOs.addAll(
							accountProfileService.findAccountProfileByAccountTypePidInAndActivatedAndImportStatus(
									Arrays.asList(accountTypePids.split(",")), false));
				} else {
					accountProfileDTOs.addAll(
							locationAccountProfileService.findAccountProfilesByCurrentUserLocationsAndAccountTypePidIn(
									Arrays.asList(accountTypePids.split(",")), false));
				}

				return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
			}
		}
	}

	@Timed
	@RequestMapping(value = "/verify-account-profile/changeActivatedStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfilesImported(@Valid @RequestParam String accountPids) {
		log.debug("Web request to update AccountProfiles ", accountPids);
		String[] accountPid = accountPids.split(",");
		for (String accountProfile : accountPid) {
			accountProfileService.updateAccountProfileStatus(accountProfile, false);
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@RequestMapping(value = "/verify-account-profile/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> getAccountProfile(@PathVariable String pid) {
		log.debug("Web request to get AccountProfile by pid : {}", pid);
		return accountProfileService.findOneByPid(pid)
				.map(accountProfileDTO -> new ResponseEntity<>(accountProfileDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@RequestMapping(value = "/verify-account-profile", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> updateAccountProfile(
			@Valid @RequestBody AccountProfileDTO accountProfileDTO) {
		log.debug("Web request to update AccountProfile : {}", accountProfileDTO);
		if (accountProfileDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "idNotexists", "Account Profile must have an ID"))
					.body(null);
		}
		Optional<AccountProfileDTO> existingAccountProfile = accountProfileService
				.findByName(accountProfileDTO.getName());
		if (existingAccountProfile.isPresent()
				&& (!existingAccountProfile.get().getPid().equals(accountProfileDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "nameexists", "Account Profile already in use"))
					.body(null);
		}
		AccountProfileDTO result = accountProfileService.update(accountProfileDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "idNotexists", "Invalid Account Profile ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("accountProfile", accountProfileDTO.getPid().toString()))
				.body(result);
	}
	
	@Timed
	@RequestMapping(value = "/verify-account-profile/changeImportedStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfilesActivate(@Valid @RequestParam String accountPids) {
		log.debug("Web request to update AccountProfiles ", accountPids);
		String[] accountPid = accountPids.split(",");
		for (String accountProfile : accountPid) {
			accountProfileService.updateImportedStatus(accountProfile, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/verify-account-profile/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> filterAccountProfileDTOs(
			@RequestParam("employeePid") String employeePid, @RequestParam("filterBy") String filterBy,
			@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
			@RequestParam("active") boolean active) {
		log.debug("Web request to filter executive task executions");
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<AccountProfileDTO>();
		if (filterBy.equals("TODAY")) {
			accountProfileDTOs = getFilterData(employeePid, LocalDate.now(), LocalDate.now(), active);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			accountProfileDTOs = getFilterData(employeePid, yeasterday, yeasterday, active);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			accountProfileDTOs = getFilterData(employeePid, weekStartDate, LocalDate.now(), active);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			accountProfileDTOs = getFilterData(employeePid, monthStartDate, LocalDate.now(), active);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			accountProfileDTOs = getFilterData(employeePid, fromDateTime, toFateTime, active);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			accountProfileDTOs = getFilterData(employeePid, fromDateTime, fromDateTime, active);
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	private List<AccountProfileDTO> getFilterData(String employeePid, LocalDate fDate, LocalDate tDate,
			boolean active) {

		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		List<Long> userIds;
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}

		if (userPid.equals("no")) {
			userIds = getCurrentUsersSubordinateIds();
		} else {
			Optional<User> opUser = userRepository.findOneByPid(userPid);
			userIds = Arrays.asList(opUser.get().getId());
		}

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		if (userIds.size() > 0) {
			if (active) {
				accountProfileDTOs = accountProfileService
						.findByCompanyIdAndUserIdInAndImportStatusAndCreatedDateBetweenOrderByCreatedDateDesc(companyId,
								userIds, fromDate, toDate, active);
				accountProfileDTOs.addAll(accountProfileService
						.findByCompanyIdAndUserIdInAndImportStatusAndCreatedDateBetweenOrderByCreatedDateDesc(companyId,
								userIds, fromDate, toDate, false));
			} else {
				accountProfileDTOs = accountProfileService
						.findByCompanyIdAndUserIdInAndImportStatusAndCreatedDateBetweenOrderByCreatedDateDesc(companyId,
								userIds, fromDate, toDate, active);
			}
		}
		
		if(userIds.size() > 0) {
			 List<Object[]> employeeList = employeeProfileRepository.findEmployeeByUserIdInAndActivatedin(userIds,true);
			 if(employeeList.size() > 0) {
				 for(AccountProfileDTO accountObj: accountProfileDTOs) {
					 employeeList.forEach(data -> {
						 if(accountObj.getUserPid().equals(data[2].toString())) {
							 accountObj.setEmployeeName(data[1].toString());
						 }
					 });
				 }
			 }
		}
		if(!accountProfileDTOs.isEmpty()) {
			accountProfileService.GetAttributeAnswers(accountProfileDTOs);
		}
		return accountProfileDTOs;
	}

	public List<Long> getCurrentUsersSubordinateIds() {
		String login = SecurityUtils.getCurrentUserLogin();
		Optional<User> optionalUser = userRepository.findOneByLogin(login);
		EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUserLogin(login);
		if (optionalUser.isPresent() && employee != null) {
			// get users from employee hierarchy
			List<Object> result = employeeHierarchyRepository
					.findChildrenByEmployeeIdAndActivatedTrue(employee.getId());
			List<Long> employeeIds = new ArrayList<>();
			for (Object object : result) {
				employeeIds.add(((BigInteger) object).longValue());
			}
			if (!employeeIds.isEmpty()) {
				return employeeProfileRepository.findUserIdByEmployeeIdIn(employeeIds);
			}
		}
		return Collections.emptyList();
	}
}
