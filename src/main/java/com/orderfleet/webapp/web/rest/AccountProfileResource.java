package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.service.*;
import com.orderfleet.webapp.web.rest.dto.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.security.SecurityUtils;

import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing AccountProfile.
 * 
 * @author Muhammed Riyas T
 * @since June 02, 2016
 */
@Controller
@RequestMapping("/web")
public class AccountProfileResource {

	private final Logger log = LoggerFactory.getLogger(AccountProfileResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private PriceLevelService priceLevelService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private CounrtyCRepository countryCRepository;

	@Inject
	private DistrictCRepository districtCRepository;

	@Inject
	private StateCRepository stateCRepository;

	@Inject
	private LocationService locationService;
	@Inject
	private AccountProfileAttributesService accountProfileAttributesService;

	@Inject
	private CompanyAttributesRepository companyAttributesRepository;
	@Inject
	private AccountProfileAttributesRepository accountProfileAttributesRepository;

	@Inject
	private CustomerAttributesService customerAttributesService;

	/**
	 * POST /accountProfiles : Create a new accountProfile.
	 *
	 * @param accountProfileDTO the accountProfileDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         accountProfileDTO, or with status 400 (Bad Request) if the
	 *         accountProfile has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/accountProfiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<AccountProfileDTO> createAccountProfile(
			@Valid @RequestBody AccountProfileDTO accountProfileDTO) throws URISyntaxException {
		log.info("location radius" + accountProfileDTO.getLocationRadius());
		log.debug("Web request to save AccountProfile : {}", accountProfileDTO);
		if (accountProfileDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("accountProfile", "idexists",
					"A new account profile cannot already have an ID")).body(null);
		}
		if (accountProfileService.findByName(accountProfileDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "nameexists", "Account Profile already in use"))
					.body(null);
		}
		accountProfileDTO.setAccountStatus(AccountStatus.Verified);
		accountProfileDTO.setDataSourceType(DataSourceType.WEB);
		AccountProfileDTO result = accountProfileService.save(accountProfileDTO);
		return ResponseEntity.created(new URI("/web/accountProfiles/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("accountProfile", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /accountProfiles : Updates an existing accountProfile.
	 *
	 * @param accountProfileDTO the accountProfileDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         accountProfileDTO, or with status 400 (Bad Request) if the
	 *         accountProfileDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the accountProfileDTO couldnt be updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/accountProfiles", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
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
		accountProfileDTO.setDataSourceType(DataSourceType.WEB);
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

	@RequestMapping(value = "/accountProfiles/saveLocationRadius", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfile>> updateLocationRadius(
			@RequestParam("locationRadius") double locationRadius) {
		log.debug("Web request to update location radius*  " + locationRadius);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId();
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);

		for (AccountProfile accountProfileslist : accountProfiles) {
			accountProfileslist.setLocationRadius(locationRadius);
		}

		accountProfileRepository.save(accountProfiles);

		return new ResponseEntity<>(accountProfiles, HttpStatus.OK);

	}

	@RequestMapping(value = "/accountProfiles/loadStates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StateCDTO>> loadStates(@RequestParam("countryId") String countryId) {
		log.debug("Web request to load states by country*  ");

		List<StateC> statesList = stateCRepository.findAllByCountryId(Long.valueOf(countryId));
		List<StateCDTO> states = converttostatedto(statesList);
		return new ResponseEntity<>(states, HttpStatus.OK);

	}

	@RequestMapping(value = "/accountProfiles/loadDistricts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DistrictCDTO>> loadDistricts(@RequestParam("stateId") String stateId) {
		log.debug("Web request to load districts by states*  ");

		List<DistrictC> districtsList = districtCRepository.findAllByStateId(Long.valueOf(stateId));
		List<DistrictCDTO> districts = converttodistrictdto(districtsList);
		return new ResponseEntity<>(districts, HttpStatus.OK);

	}

	/**
	 * GET /accountProfiles : get all the accountProfiles.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         accountProfiles in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/accountProfiles", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Pageable pageable, Model model) {
		model.addAttribute("accountTypes", accountTypeService.findAllByCompany());
		model.addAttribute("priceLevels", priceLevelService.findAllByCompany());
		model.addAttribute("deactivatedAccountProfiles", accountProfileService.findAllByCompanyAndActivated(false));
		model.addAttribute("locations", locationService.findAllByCompanyAndLocationActivated(true));
		List<CountryC> countriesList = countryCRepository.findAllCountries();
		List<CountryCDTO> countries = converttodto(countriesList);
		model.addAttribute("countries", countries);
		List<StateC> statesList = stateCRepository.findAllStates();
		List<StateCDTO> states = converttostatedto(statesList);
		model.addAttribute("states", states);

		List<DistrictC> districtsList = districtCRepository.findAllDistricts();
		List<DistrictCDTO> districts = converttodistrictdto(districtsList);
		model.addAttribute("districts", districts);
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		List<CompanyAttributes> compAttributes = companyAttributesRepository.findByCompanyId(companyId);
		boolean detailButton = false;
		if(compAttributes.size()>0)
		{
			detailButton = true;
		}
		model.addAttribute("DetailButtonVisible",detailButton);
		return "company/accountProfiles";
	}

	private List<DistrictCDTO> converttodistrictdto(List<DistrictC> districtsList) {
		List<DistrictCDTO> districtdtos = new ArrayList<>();

		for (DistrictC district : districtsList) {
			DistrictCDTO districtcdto = new DistrictCDTO(district);
			districtdtos.add(districtcdto);

		}

		return districtdtos;
	}

	private List<StateCDTO> converttostatedto(List<StateC> statesList) {
		List<StateCDTO> statecdtos = new ArrayList<>();

		for (StateC state : statesList) {
			StateCDTO statecdto = new StateCDTO(state);
			statecdtos.add(statecdto);

		}
		return statecdtos;
	}

	private List<CountryCDTO> converttodto(List<CountryC> countriesList) {
		List<CountryCDTO> countrycdtos = new ArrayList<>();

		for (CountryC country : countriesList) {
			CountryCDTO counrtycdto = new CountryCDTO(country);
			countrycdtos.add(counrtycdto);

		}
		return countrycdtos;
	}

	/**
	 * GET /accountProfiles/load :load accountProfiles.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         accountProfileDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/accountProfiles/load", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AccountProfileDTO>> loadAccountProfiles() {
		log.debug("Web request to load AccountProfiles* ");
		return new ResponseEntity<>(accountProfileService.findAllByCompanyAndActivated(true), HttpStatus.OK);
//		return new ResponseEntity<>(accountProfileService.findAllCustomByCompanyAndActivated(true), HttpStatus.OK);
	}

	/**
	 * GET /accountProfiles/:id : get the "id" accountProfile.
	 *
	 * @param id the id of the accountProfileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         accountProfileDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/accountProfiles/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> getAccountProfile(@PathVariable String pid) {
		log.debug("Web request to get AccountProfile by pid : {}", pid);
		return accountProfileService.findOneByPid(pid)
				.map(accountProfileDTO -> new ResponseEntity<>(accountProfileDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/accountProfiles/locations", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<LocationDTO>> locationAccountProfiles(@RequestParam String accountProfilePid) {
		log.debug("REST request to location Account Profiles : {}", accountProfilePid);
		List<LocationDTO> locationDTOs = locationAccountProfileService
				.findAllLocationByAccountProfilePid(accountProfilePid);
		return new ResponseEntity<>(locationDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/accountProfiles/assign-locations", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedLocations(@RequestParam String locationPid,
			@RequestParam String assignedAccountProfiles) {
		log.debug("REST request to save assigned Account Profiles : {}", locationPid);
		String[] locationPids = locationPid.split(",");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountProfile accountProfile = accountProfileRepository.findOneByPid(assignedAccountProfiles).get();
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);

		locationAccountProfileRepository.deleteByAccountProfilePid(SecurityUtils.getCurrentUsersCompanyId(),
				accountProfile.getId());
		locationAccountProfileService.save(locationPids[0], assignedAccountProfiles);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * DELETE /accountProfiles/:pid : delete the "pid" accountProfile.
	 *
	 * @param pid the pid of the accountProfileDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/accountProfiles/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteAccountProfile(@PathVariable String pid) {
		log.debug("REST request to delete AccountProfile : {}", pid);
		accountProfileService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("accountProfile", pid.toString()))
				.build();
	}

	/**
	 * GET /accountProfiles/user/:id : get the "id" accountProfile.
	 *
	 * @param id the user's id of the accountProfileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         accountProfileDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/accountProfiles/user/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> getUsersAccountProfile(@PathVariable String pid) {
		log.debug("Web request to get AccountProfile by user pid : {}", pid);
		return new ResponseEntity<>(accountProfileService.findUsersAccountProfile(pid), HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 6, 2017
	 *
	 *        UPDATE STATUS /accountGroups/changeStatus :accountProfileDTO : update
	 *        status of accountProfile.
	 * 
	 * @param accountProfileDTO
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         accountProfileDTO
	 */
	@Timed
	@RequestMapping(value = "/accountProfiles/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfilesActivate(
			@Valid @RequestBody AccountProfileDTO accountProfileDTO) {
		log.debug("Web request to update AccountProfiles ", accountProfileDTO);
		AccountProfileDTO result = accountProfileService.updateAccountProfileStatus(accountProfileDTO.getPid(),
				accountProfileDTO.getActivated());

		return new ResponseEntity<>(result, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS /accountProfiles/activateAccountProfile : activate
	 *        status of accountProfile.
	 * 
	 * @param accountprofiles the accountprofiles to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/accountProfiles/activateAccountProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfileDeactivated(@Valid @RequestParam String accountprofiles) {
		log.debug("Web request to get Deactive AccountProfiles");
		String[] accountProfiles = accountprofiles.split(",");
		for (String accountprofile : accountProfiles) {
			accountProfileService.updateAccountProfileStatus(accountprofile, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/accountProfiles/filterByAccountType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<AccountProfileDTO>> filterAccountProfilesByAccountTypes(
			@RequestParam String accountTypePids, @RequestParam String importedStatus) throws URISyntaxException {
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		boolean imports;
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		// Not selected
		if (accountTypePids.isEmpty() && importedStatus.isEmpty()) {
			if (userIds.isEmpty()) {
				// accountProfileDTOs =
				// accountProfileService.findAllByCompanyAndActivated(true);
				// accountProfileDTOs =
				// accountProfileService.findAllByCompanyAndActivated(true);

				accountProfileDTOs = accountProfileService.findAllByCompanyAndActivatedLimitToCount(true);

			} else {
				accountProfileDTOs = locationAccountProfileService
						.findAccountProfilesByCurrentUserLocationsLimitCount();
			}

			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}

		// Both selected
		if (!accountTypePids.isEmpty() && !importedStatus.isEmpty()) {
			if (importedStatus.equals("true")) {
				imports = true;
				if (userIds.isEmpty()) {
					accountProfileDTOs.addAll(accountProfileService
							.findAccountProfileByAccountTypePidInAndActivatedAndImportStatusLimitCount(
									Arrays.asList(accountTypePids.split(",")), imports));
				} else {
					accountProfileDTOs.addAll(locationAccountProfileService
							.findAccountProfilesByCurrentUserLocationsAndAccountTypePidInLimitCount(
									Arrays.asList(accountTypePids.split(",")), imports));
				}
			} else if (importedStatus.equals("false")) {
				imports = false;
				if (userIds.isEmpty()) {
					accountProfileDTOs.addAll(accountProfileService
							.findAccountProfileByAccountTypePidInAndActivatedAndImportStatusLimitCount(
									Arrays.asList(accountTypePids.split(",")), imports));
				} else {
					accountProfileDTOs.addAll(locationAccountProfileService
							.findAccountProfilesByCurrentUserLocationsAndAccountTypePidInLimitCount(
									Arrays.asList(accountTypePids.split(",")), imports));
				}
			} else {
				if (userIds.isEmpty()) {
					accountProfileDTOs
							.addAll(accountProfileService.findAccountProfileByAccountTypePidInAndActivatedLimitCount(
									Arrays.asList(accountTypePids.split(","))));
				} else {
					accountProfileDTOs.addAll(locationAccountProfileService
							.findAccountByCurrentUserLocationsAndAccountTypePidInLimitCount(
									Arrays.asList(accountTypePids.split(","))));
				}
			}

			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}

		// ImportStatus Selected
		if (accountTypePids.isEmpty() && !importedStatus.isEmpty()) {
			if (importedStatus.equals("true")) {
				imports = true;
				if (userIds.isEmpty()) {
					accountProfileDTOs.addAll(accountProfileService
							.findAllByCompanyAndAccountImportStatusAndActivatedLimitCount(imports));
				} else {
					accountProfileDTOs.addAll(locationAccountProfileService
							.findAccountProfilesByCurrentUserLocationsAndImpotedStatusLimitCount(imports));
				}
			} else if (importedStatus.equals("false")) {
				imports = false;
				if (userIds.isEmpty()) {
					accountProfileDTOs.addAll(accountProfileService
							.findAllByCompanyAndAccountImportStatusAndActivatedLimitCount(imports));
				} else {
					accountProfileDTOs.addAll(locationAccountProfileService
							.findAccountProfilesByCurrentUserLocationsAndImpotedStatusLimitCount(imports));
				}
			} else {
				if (userIds.isEmpty()) {
					accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivatedLimitToCount(true));
				} else {
					accountProfileDTOs.addAll(locationAccountProfileService
							.findAccountByCurrentUserLocationsAndAllImpotedStatusLimitCount());
				}
			}

			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}

		// AccountType Selected
		if (!accountTypePids.isEmpty() && importedStatus.isEmpty()) {
			if (userIds.isEmpty()) {
				accountProfileDTOs.addAll(accountProfileService
						.findAccountProfileByAccountTypePidInAndActivated(Arrays.asList(accountTypePids.split(","))));
			} else {
				accountProfileDTOs
						.addAll(locationAccountProfileService.findAccountByCurrentUserLocationsAndAccountTypePidIn(
								Arrays.asList(accountTypePids.split(","))));
			}
			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/accountProfiles/location/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LocationAccountProfileDTO>> getProductProfileLocations(@PathVariable String pid) {
		log.debug("Web request to get AccountProfile location by pid : {}", pid);
		return new ResponseEntity<>(locationAccountProfileService.findLocationByAccountProfilePid(pid), HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/accountProfiles/get-by-status-filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AccountProfileDTO>> getAccountProfilesByStatus(@RequestParam boolean active,
			@RequestParam boolean deactivate) {
		log.debug("Web request to get get activitys : {}");
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		if (active == true && deactivate == true) {
//			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
//			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(false));
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivatedLimitToCount(true));
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivatedLimitToCount(false));
		} else if (active) {
			// accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivatedLimitToCount(true));
		} else if (deactivate) {
			// accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(false));
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivatedLimitToCount(false));
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/accountProfiles/get-by-search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AccountProfileDTO>> getAccountProfilesByStatusAndSearch(@RequestParam boolean active,
			@RequestParam boolean deactivate, @RequestParam String searchValue) {
		log.debug("Web request to get get activitys : {}");
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		if (active == true && deactivate == true) {
//			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
//			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(false));
			accountProfileDTOs.addAll(
					accountProfileService.findAllByCompanyAndActivatedLimitToCountAndSearchValue(true, searchValue));
			accountProfileDTOs.addAll(
					accountProfileService.findAllByCompanyAndActivatedLimitToCountAndSearchValue(false, searchValue));
		} else if (active) {
			// accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
			accountProfileDTOs.addAll(
					accountProfileService.findAllByCompanyAndActivatedLimitToCountAndSearchValue(true, searchValue));
		} else if (deactivate) {
			// accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(false));
			accountProfileDTOs.addAll(
					accountProfileService.findAllByCompanyAndActivatedLimitToCountAndSearchValue(false, searchValue));
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/accountProfiles/verifyStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfilesVerifyStatus(
			@Valid @RequestBody AccountProfileDTO accountProfileDTO) {
		log.debug("Web request to update AccountProfiles Verified Status ", accountProfileDTO);

		AccountProfileDTO result = accountProfileService.updateAccountProfileVerifiedStatus(accountProfileDTO.getPid(),
				accountProfileDTO.getAccountStatus() == AccountStatus.Verified ? AccountStatus.Unverified
						: AccountStatus.Verified);

		return new ResponseEntity<>(result, HttpStatus.OK);

	}

	@RequestMapping(value = "/accountProfiles/download-profile-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void downloadProductProfileXls(@RequestParam String status, HttpServletResponse response) {
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<AccountProfileDTO>();
		boolean active = false, deactivate = false;
		if (status.equals("All")) {
			active = true;
			deactivate = true;
		} else if (status.equals("Active")) {
			active = true;
			deactivate = false;
		} else if (status.equals("Deactive")) {
			deactivate = true;
			active = false;
		}

		if (active == true && deactivate == true) {
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(false));
			// accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivatedLimitToCount(true));
			// accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivatedLimitToCount(false));
		} else if (active) {
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
			// accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivatedLimitToCount(true));
		} else if (deactivate) {
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(false));
			// accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivatedLimitToCount(false));
		}

		List<String> accountProfilePids = accountProfileDTOs.stream().map(AccountProfileDTO::getPid)
				.collect(Collectors.toList());
		
		List<Object[]> locationAccountProfileObjects = locationAccountProfileRepository
				.findAllLocationObjectsByAccountProfilePids(accountProfilePids);

		buildExcelDocument(accountProfileDTOs, locationAccountProfileObjects, response);
	}

	private void buildExcelDocument(List<AccountProfileDTO> accountProfileDTOs, List<Object[]> locationAccountProfiles,
									HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "accountProfile" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Name", "Alias", "CustomerId", "Location/Territory", "Type", "Closing Balance",
				"Address", "Phone1", "Email1", "WhatsApp No", "Account Status", "GSTIN", "GST Registration Type",
				"Created Date", "Last Updated Date", "Created By", "Stage", "Status" };
		List<CompanyAttributes> companyAttributes = companyAttributesRepository.findAllByCompanyId();
		Map<Long, CompanyAttributes> uniqueMap = companyAttributes.stream()
				.collect(Collectors.groupingBy(ComAttr-> ComAttr.getAttributes().getId(),
						Collectors.collectingAndThen(Collectors.toList(), list -> list.get(0))));

		List<CompanyAttributes>	companyAttributesList = new ArrayList<>(uniqueMap.values());
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			Sheet sheet = workbook.getSheetAt(0);
			createHeaderRow(worksheet, headerColumns,companyAttributesList);
			createReportRows(worksheet, accountProfileDTOs, locationAccountProfiles,companyAttributesList);
			// Resize all columns to fit the content size
			for (int i = 0; i < headerColumns.length; i++) {
				worksheet.autoSizeColumn(i);
			}

			response.setHeader("Content-Disposition", "inline; filename=" + excelFileName);
			response.setContentType("application/vnd.ms-excel");
			// Writes the report to the output stream
			ServletOutputStream outputStream = response.getOutputStream();
			worksheet.getWorkbook().write(outputStream);
			outputStream.flush();
		} catch (IOException ex) {
			log.error("IOException on downloading Product profiles {}", ex.getMessage());
		}
	}

	private void createHeaderRow(HSSFSheet worksheet, String[] headerColumns, List<CompanyAttributes> companyAttributes) {

		// Create a Font for styling header cells
		List<String> Questions = companyAttributes.stream().map(a->a.getAttributes().getQuestions()).collect(Collectors.toList());
		Font headerFont = worksheet.getWorkbook().createFont();
		headerFont.setFontName("Arial");
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.BLACK.getIndex());
		// Create a CellStyle with the font
		HSSFCellStyle headerCellStyle = worksheet.getWorkbook().createCellStyle();
		headerCellStyle.setFont(headerFont);
		// Create a Row
		HSSFRow headerRow = worksheet.createRow(0);
		// Create cells
		for (int i = 0; i < headerColumns.length; i++) {
			HSSFCell cell = headerRow.createCell(i);
			cell.setCellValue(headerColumns[i]);
			cell.setCellStyle(headerCellStyle);
		}

		int existingColumnCount = headerRow.getLastCellNum();
		for (int i = 0; i < companyAttributes.size(); i++) {
			Cell cell = headerRow.createCell(existingColumnCount + i);
			cell.setCellValue(Questions.get(i));
			cell.setCellStyle(headerCellStyle);
		}
	}

	private void createReportRows(HSSFSheet worksheet, List<AccountProfileDTO> accountProfileDTO,
								  List<Object[]> locationAccountProfiles, List<CompanyAttributes> companyAttributes) {
		
		/*
		 * CreationHelper helps us create instances of various things like DataFormat,
		 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
		 */
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss"));
		// Create Other rows and cells with Sales data
		List<String> accPid = accountProfileDTO.stream().map(acc -> acc.getPid()).collect(Collectors.toList());
		List<AccountProfileAttributes> accountProfileAttributesList = accountProfileAttributesRepository
				.findAccountProfileAttributesByAccountProfilePidIn(accPid);
		int rowNum = 1;
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
		for (AccountProfileDTO ap : accountProfileDTO) {
			List<AccountProfileAttributes> accountProfileAttributes = accountProfileAttributesList.stream().filter(aa -> aa.getAccountProfile().getPid().equals(ap.getPid()))
					.collect(Collectors.toList());

			HSSFRow	row = worksheet.createRow(rowNum++);
			row.createCell(0).setCellValue(ap.getName().replace("#13;#10;", " "));
			row.createCell(1).setCellValue(ap.getAlias());
			row.createCell(2).setCellValue(ap.getCustomerId());

			Optional<Object[]> opLap = locationAccountProfiles.stream()
					.filter(lap -> lap[2].toString().equals(ap.getPid())).findFirst();

			if (opLap.isPresent()) {
				row.createCell(3).setCellValue(opLap.get()[1].toString());
			} else {
				row.createCell(3).setCellValue("");
			}

			row.createCell(4).setCellValue(ap.getAccountTypeName());
			row.createCell(5).setCellValue(String.format("%.2f", ap.getClosingBalance()));
			row.createCell(6).setCellValue(ap.getAddress());
			row.createCell(7).setCellValue(ap.getPhone1() == null ? "" : ap.getPhone1());
			row.createCell(8).setCellValue(ap.getEmail1() == null ? "" : ap.getEmail1());
			row.createCell(9).setCellValue(ap.getWhatsAppNo() == null ? "" : ap.getWhatsAppNo());
			row.createCell(10).setCellValue(ap.getAccountStatus().toString());
			row.createCell(11).setCellValue(ap.getTinNo() == null ? "" : ap.getTinNo());
			row.createCell(12).setCellValue(ap.getGstRegistrationType() == null ? "" : ap.getGstRegistrationType());
			LocalDateTime ldt = ap.getCreatedDate();
			if (ldt != null) {
				row.createCell(13).setCellValue(ldt.format(df));
			} else {
				row.createCell(13).setCellValue("");
			}
			LocalDateTime lmdt = ap.getLastModifiedDate();
			if (lmdt != null) {
				row.createCell(14).setCellValue(lmdt.format(df));
			} else {
				row.createCell(14).setCellValue("");
			}
			row.createCell(15).setCellValue(ap.getUserName() == null ? "" : ap.getUserName());
			row.createCell(16).setCellValue(ap.getLeadToCashStage() == null ? "" : ap.getLeadToCashStage());
			row.createCell(17).setCellValue(ap.getActivated() ? "Activated" : "Deactivated");

     if(accountProfileAttributes.size()>0)
         {
	   int i=0;
			 for (CompanyAttributes comp : companyAttributes) {


			   for(AccountProfileAttributes attr : accountProfileAttributes) {

				 if(attr.getAttributesPid().equals(comp.getAttributes().getPid()))
				 {
					 Cell cell = row.createCell(18 + i);
				   cell.setCellValue(attr.getAnswers());
			 }

		 }
			 i++;
			 }
			}}
		}

	@GetMapping(value = "/accountProfiles/attributes")
	@Timed
	public ResponseEntity<List<AccountProfileAttributesDTO>> getAttributes(@RequestParam String accountProfilePid) {
		log.info("Web requset to get account attributes :"+accountProfilePid);
		List<AccountProfileAttributesDTO> accountProfileAttributesDTOs = accountProfileAttributesService.getAccountProfileAttributesByAccountProfilePid(accountProfilePid);

		if (accountProfileAttributesDTOs != null && !accountProfileAttributesDTOs.isEmpty()) {
			return new ResponseEntity<>(accountProfileAttributesDTOs, HttpStatus.OK);
		}
		return new ResponseEntity<>(Collections.EMPTY_LIST, HttpStatus.OK);
	}
}
