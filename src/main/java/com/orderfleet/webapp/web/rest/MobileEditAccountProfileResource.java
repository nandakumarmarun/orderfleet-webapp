package com.orderfleet.webapp.web.rest;

import java.math.BigInteger;
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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.EmployeeHierarchyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;

@Controller
@RequestMapping("/web")
public class MobileEditAccountProfileResource {

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
	
	@RequestMapping(value = "/mobile-edit-account-profile", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Model model) {
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

		return "company/mobileEditAccountProfile";
	}

	@RequestMapping(value = "/mobile-edit-account-profile/filterByAccountType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<AccountProfileDTO>> filterAccountProfilesByAccountTypes(@RequestParam String accountTypePids) {
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		if (userIds.isEmpty()) {
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndDatasourceType(DataSourceType.MOBILE));
		} else {
			List<AccountProfileDTO> accountProfileDTOs1 = locationAccountProfileService
					.findAccountProfilesByCurrentUserLocations();
			for (AccountProfileDTO accountProfileDTO : accountProfileDTOs1) {
				if (accountProfileDTO.getDataSourceType() == DataSourceType.MOBILE) {
					accountProfileDTOs.add(accountProfileDTO);
				}
			}
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	/*@Timed
	@RequestMapping(value = "/mobile-edit-account-profile/changeActivatedStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfilesImported(@Valid @RequestParam String accountPids) {
		log.debug("Web request to update AccountProfiles ", accountPids);
		String[] accountPid = accountPids.split(",");
		for (String accountProfile : accountPid) {
			accountProfileService.updateAccountProfileStatus(accountProfile, false);
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@Timed
	@RequestMapping(value = "/mobile-edit-account-profile/changeImportedStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfilesActivate(@Valid @RequestParam String accountPids) {
		log.debug("Web request to update AccountProfiles ", accountPids);
		String[] accountPid = accountPids.split(",");
		for (String accountProfile : accountPid) {
			accountProfileService.updateImportedStatus(accountProfile, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
*/
	@RequestMapping(value = "/mobile-edit-account-profile/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> filterAccountProfileDTOs(
			@RequestParam("employeePid") String employeePid, @RequestParam("filterBy") String filterBy,
			@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate) {
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			accountProfileDTOs = getFilterData(employeePid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			accountProfileDTOs = getFilterData(employeePid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			accountProfileDTOs = getFilterData(employeePid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			accountProfileDTOs = getFilterData(employeePid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			accountProfileDTOs = getFilterData(employeePid, fromDateTime, toFateTime);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			accountProfileDTOs = getFilterData(employeePid, fromDateTime, fromDateTime);
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	private List<AccountProfileDTO> getFilterData(String employeePid, LocalDate fDate, LocalDate tDate) {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		List<Long> userIds = Collections.emptyList();
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}
		if (userPid.equals("no")) {
			userIds = getCurrentUsersSubordinateIds();
		} else {
			Optional<User> opUser = userRepository.findOneByPid(userPid);
			if(opUser.isPresent()) {
				userIds = Arrays.asList(opUser.get().getId());	
			}
		}
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		if (!userIds.isEmpty()) {
			accountProfileDTOs = accountProfileService
					.findByCompanyIdAndUserIdInAndDatasourceTypeMobileAndLastModifiedDateBetweenOrderByCreatedDateDesc(companyId,
							userIds, fromDate, toDate);
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
