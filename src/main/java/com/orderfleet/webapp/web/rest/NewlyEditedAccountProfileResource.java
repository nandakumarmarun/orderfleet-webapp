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
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.NewlyEditedAccountProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.SendSalesOrderEmailStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.EmployeeHierarchyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.NewlyEditedAccountProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.NewlyEditedAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.NewlyEditedAccountProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class NewlyEditedAccountProfileResource {

	private final Logger log = LoggerFactory.getLogger(NewlyEditedAccountProfileResource.class);

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private NewlyEditedAccountProfileService newlyEditedaccountProfileService;

	@Inject
	private NewlyEditedAccountProfileRepository newlyEditedaccountProfileRepository;

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
	private AccountProfileMapper accountProfileMapper;

	/**
	 * GET /verify-account-profile : get all the VerifyAccountProfiles.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         verifyAccountProfiles in body
	 * @throws URISyntaxException
	 * 
	 */
	@RequestMapping(value = "/newly-edited-account-profiles", method = RequestMethod.GET)
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

		return "company/newlyEditedAccountProfiles";
	}

	@RequestMapping(value = "/newly-edited-account-profiles/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<NewlyEditedAccountProfileDTO> getAccountProfile(@PathVariable String pid) {
		log.debug("Web request to get AccountProfile by pid : {}", pid);

		NewlyEditedAccountProfileDTO newlyEditedAp = newlyEditedaccountProfileService.findOneByPid(pid);

		if (newlyEditedAp != null) {
			return new ResponseEntity<>(newlyEditedAp, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/newly-edited-account-profiles/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<NewlyEditedAccountProfileDTO>> filterAccountProfileDTOs(
			@RequestParam("employeePid") String employeePid, @RequestParam("filterBy") String filterBy,
			@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
			@RequestParam("accountStatus") String accountStatus) {
		log.debug("Web request to filter account profiles");
		List<NewlyEditedAccountProfileDTO> accountProfileDTOs = new ArrayList<NewlyEditedAccountProfileDTO>();
		if (filterBy.equals("TODAY")) {
			accountProfileDTOs = getFilterData(employeePid, LocalDate.now(), LocalDate.now(), accountStatus);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			accountProfileDTOs = getFilterData(employeePid, yeasterday, yeasterday, accountStatus);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			accountProfileDTOs = getFilterData(employeePid, weekStartDate, LocalDate.now(), accountStatus);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			accountProfileDTOs = getFilterData(employeePid, monthStartDate, LocalDate.now(), accountStatus);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			accountProfileDTOs = getFilterData(employeePid, fromDateTime, toFateTime, accountStatus);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			accountProfileDTOs = getFilterData(employeePid, fromDateTime, fromDateTime, accountStatus);
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/newly-edited-account-profiles/updateAccountProfileStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<NewlyEditedAccountProfileDTO> updateAccountProfileStatus(@RequestParam String pid) {
		
		log.info("Update Account Profile Status: "+pid);

		newlyEditedaccountProfileRepository.updateAccountProfileStatus(AccountStatus.Verified, pid);

		Optional<NewlyEditedAccountProfile> opNewlyEditedAccountProfile = newlyEditedaccountProfileRepository
				.findOneByPid(pid);

		if (opNewlyEditedAccountProfile.isPresent()) {

			AccountProfileDTO accountProfileDTO = newlyEditedaccountProfileService
					.newlyEditedAccountProfileToAccountProfileDTO(opNewlyEditedAccountProfile.get());

			Optional<AccountProfile> exisitingAccountProfile = accountProfileRepository
					.findOneByPid(opNewlyEditedAccountProfile.get().getAccountProfile().getPid());
			if (exisitingAccountProfile.isPresent()) {

				AccountProfile accountProfile = accountProfileMapper
						.accountProfileDTOToAccountProfile(accountProfileDTO);
				accountProfile.setId(exisitingAccountProfile.get().getId());
				accountProfile.setCompany(opNewlyEditedAccountProfile.get().getCompany());
				accountProfile.setDataSourceType(DataSourceType.MOBILE);
				Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());

				if (opUser.isPresent()) {
					accountProfile.setUser(opUser.get());
				}

				accountProfile = accountProfileRepository.save(accountProfile);
			}
		}
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

	private List<NewlyEditedAccountProfileDTO> getFilterData(String employeePid, LocalDate fDate, LocalDate tDate,
			String accountStatus) {

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

		List<AccountStatus> status = null;

		switch (accountStatus) {
		case "Verified":
			status = Arrays.asList(AccountStatus.Verified);
			break;
		case "Unverified":
			status = Arrays.asList(AccountStatus.Unverified);
			break;
		}

		List<NewlyEditedAccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		if (userIds.size() > 0) {

			accountProfileDTOs = newlyEditedaccountProfileService
					.findByCompanyIdAndUserIdInAndCreatedDateBetweenOrderAndAccountStatusByCreatedDateDesc(companyId,
							userIds, fromDate, toDate, status);

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
