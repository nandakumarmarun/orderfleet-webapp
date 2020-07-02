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
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class EditedAccountProfileResource {

	private final Logger log = LoggerFactory.getLogger(EditedAccountProfileResource.class);

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

	/**
	 * GET /verify-account-profile : get all the VerifyAccountProfiles.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         verifyAccountProfiles in body
	 * @throws URISyntaxException
	 * 
	 */
	@RequestMapping(value = "/edited-account-profiles", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of EditedAccountProfiles");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}

		return "company/editedAccountProfiles";
	}

	@RequestMapping(value = "/edited-account-profiles/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> getAccountProfile(@PathVariable String pid) {
		log.debug("Web request to get AccountProfile by pid : {}", pid);
		return accountProfileService.findOneByPid(pid)
				.map(accountProfileDTO -> new ResponseEntity<>(accountProfileDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/edited-account-profiles/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> filterAccountProfileDTOs(
			@RequestParam("employeePid") String employeePid, @RequestParam("filterBy") String filterBy,
			@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate) {
		log.debug("Web request to filter account profiles");
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<AccountProfileDTO>();
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

		List<Long> userIds;
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}

		if (userPid.equals("no")) {
			userIds = getAllUsersIds();

		} else {
			Optional<User> opUser = userRepository.findOneByPid(userPid);
			userIds = Arrays.asList(opUser.get().getId());

		}

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		if (userIds.size() > 0) {

			accountProfileDTOs = accountProfileService
					.findByCompanyIdAndUserIdInAndLastModifedDateBetweenOrderByLastModifedDateDesc(companyId, userIds,
							fromDate, toDate);

		}
		return accountProfileDTOs;
	}

	public List<Long> getAllUsersIds() {

		List<Long> userIds = employeeProfileRepository.findAllUserIdsByActivatedAndCompanyId(true);

		if (!userIds.isEmpty()) {
			return userIds;
		}

		return Collections.emptyList();
	}
}
