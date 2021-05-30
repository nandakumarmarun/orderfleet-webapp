package com.orderfleet.webapp.web.rest;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
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
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.LocationHierarchyAccount;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CompanySettingRepository;
import com.orderfleet.webapp.repository.DashboardItemRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyAccountRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationLevelDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

@Controller
@RequestMapping("/web")
public class LocationHierarchyAccountResource {

	private final Logger log = LoggerFactory.getLogger(LocationHierarchyAccountResource.class);

	private static final String TODAY = "TODAY";
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String CUSTOM = "CUSTOM";

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private DashboardItemRepository dashboardItemRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;

	@Inject
	private LocationHierarchyService locationHierarchyService;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private LocationService locationService;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private CompanySettingRepository companySettingRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private LocationHierarchyAccountRepository locationHierarchyAccountRepository;

	@RequestMapping("/location-hierarchy-accounts")
	@Timed
	public String getAllTerritoryWiseAccount(Model model) throws URISyntaxException {
		return "company/locationHierarchyAccounts";
	}

	@RequestMapping(value = "/location-hierarchy-accounts/save", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Long>> save() {

		long noOfLevels = 7;
		log.info("Started....................");
		List<AccountProfile> dbAcountProfiles = accountProfileRepository
				.findAllByCompanyAndActivateOrDeactivateAccountProfileOrderByName(true);

		List<AccountProfile> accountProfiles = new ArrayList<>();

		List<Object[]> objectArray = locationHierarchyAccountRepository.findAllByCompany();

		log.info("Db  Accounts...................." + dbAcountProfiles.size());

		log.info("Db Location Hierarchy Accounts...................." + objectArray.size());

		if (objectArray.size() > 0) {

			List<String> lapAccountProfilePids = new ArrayList<>();

			for (Object[] obj : objectArray) {
				lapAccountProfilePids.add(obj[2].toString());
			}

			accountProfiles = accountProfileRepository.findAccountProfileAndPIdsNotIn(lapAccountProfilePids);

		} else {
			accountProfiles = dbAcountProfiles;
		}

		log.info("Location Hierarchy Accounts to save...................." + accountProfiles.size() + "------------");

		List<String> accountProfilePids = accountProfiles.stream().map(AccountProfile::getPid)
				.collect(Collectors.toList());

		List<Object[]> locAccObjectist = locationAccountProfileRepository
				.findAccontProfileIdAndLocationIdByAccountProfileIds(accountProfilePids);

		log.info("Loop Started...................." + accountProfilePids.size() + "------------"
				+ locAccObjectist.size());

		List<LocationHierarchyAccount> locationHierarchyAccounts = new ArrayList<>();

		for (AccountProfile accountProfile : accountProfiles) {

			Optional<Object[]> opObject = locAccObjectist.stream()
					.filter(obj -> obj[3].toString().equals(accountProfile.getPid())).findAny();

			if (opObject.isPresent()) {

				LocationHierarchyAccount locationHierarchyAccount = new LocationHierarchyAccount();
				String locations = getLocationLevel(Long.valueOf(opObject.get()[2].toString()), noOfLevels);

				log.info(locations + "========>" + accountProfile.getName());

				locationHierarchyAccount.setAccountProfile(accountProfile);
				locationHierarchyAccount.setCompany(accountProfile.getCompany());
				locationHierarchyAccount.setLocations(locations);

				locationHierarchyAccounts.add(locationHierarchyAccount);
			}

		}

		log.info("Loop Completed....................");

		if (locationHierarchyAccounts.size() > 0) {
//			log.info("Deleting Existing Location Hierarchy AccountProfiles....................");
//			locationHierarchyAccountRepository.deleteByCompany();

			log.info("Saving Location hierrarchy AccountProfiles...................."
					+ locationHierarchyAccounts.size());
			locationHierarchyAccountRepository.save(locationHierarchyAccounts);
			log.info("Saving Completed...................." + locationHierarchyAccounts.size());

		}

		List<Long> noOfLeveles = new ArrayList<>();

		for (int i = 1; i < 7; i++) {
			noOfLeveles.add(Long.valueOf(i));
		}

		return new ResponseEntity<>(noOfLeveles, HttpStatus.OK);
	}

	@RequestMapping(value = "/location-hierarchy-accounts/view", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LocationHierarchyAccountProfileDTO>> view() {

		List<LocationHierarchyAccountProfileDTO> locationHierarchyAccounts = new ArrayList<>();

		List<Object[]> objectArray = locationHierarchyAccountRepository.findAllByCompany();

		for (Object[] obj : objectArray) {
			LocationHierarchyAccountProfileDTO locationHierarchyAccount = new LocationHierarchyAccountProfileDTO();

			locationHierarchyAccount.setAccountProfileName(obj[0].toString());
			locationHierarchyAccount.setLocations(obj[1].toString());

			locationHierarchyAccounts.add(locationHierarchyAccount);

		}

		return new ResponseEntity<>(locationHierarchyAccounts, HttpStatus.OK);

	}

	private String getLocationLevel(Long locationId, long noOfLevels) {

		Location location = locationRepository.findOne(locationId);

		StringBuilder sb1 = new StringBuilder();

		sb1.append(location.getName()).append("~");

		for (int i = (int) (noOfLevels - 1); i > 1; i--) {

			if (location != null) {

				location = locationHierarchyRepository.findParentLocationByLocationIdAndActivatedTrue(locationId);

				if (location != null) {
					sb1.append(location.getName()).append("~");

					locationId = location.getId();
				}
			}
		}

		return sb1.toString();
	}

	@RequestMapping(value = "/location-hierarchy-accounts/loadNoOfLevels", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Long>> loadNoOfLevels() {

		List<Long> noOfLeveles = new ArrayList<>();

		for (int i = 1; i < 7; i++) {
			noOfLeveles.add(Long.valueOf(i));
		}

		return new ResponseEntity<>(noOfLeveles, HttpStatus.OK);
	}

	@RequestMapping(value = "/location-hierarchy-accounts/loadAllLevels", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LocationLevelDTO>> loadAllLevels() {

		List<LocationLevelDTO> locLevels = new ArrayList<>();

		Location location = new Location();
		Optional<LocationHierarchy> opLocationHiearachy = locationHierarchyRepository
				.findRootLocationByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		List<LocationDTO> locationDTOs = new ArrayList<>();
		List<Long> parentIds = new ArrayList<>();

		for (int i = 1; i < 7; i++) {
			LocationLevelDTO locLevel = new LocationLevelDTO();

			locLevel.setLevel(Long.valueOf(i));
			if (i == 1) {
				if (opLocationHiearachy.isPresent()) {
					location = opLocationHiearachy.get().getLocation();
					locationDTOs = locationHierarchyService.findChildLocationsByParentId(location.getId());
					parentIds.clear();
					for (LocationDTO loc : locationDTOs) {
						parentIds.add(Long.valueOf(loc.getId()));
					}
					locationDTOs.sort((LocationDTO h1, LocationDTO h2) -> h1.getName().compareTo(h2.getName()));
					locLevel.setLocationDtos(locationDTOs);
				}
			} else {

				List<LocationDTO> locations = locationHierarchyService.findChildLocationsByParentIds(parentIds);

				parentIds.clear();
				for (LocationDTO loc : locations) {
					parentIds.add(Long.valueOf(loc.getId()));
				}
				locations.sort((LocationDTO h1, LocationDTO h2) -> h1.getName().compareTo(h2.getName()));
				locLevel.setLocationDtos(locations);
			}

			if (i == (7 - 1)) {
				List<AccountProfile> accountProfiles = locationAccountProfileRepository
						.findAccountProfileByLocationIdIn(parentIds);
				List<AccountProfileDTO> accountProfileDTOs = convertToDtoList(accountProfiles);
				accountProfileDTOs
						.sort((AccountProfileDTO h1, AccountProfileDTO h2) -> h1.getName().compareTo(h2.getName()));
				locLevel.setAccountProfileDTOs(accountProfileDTOs);
			}

			locLevels.add(locLevel);

		}

		return new ResponseEntity<>(locLevels, HttpStatus.OK);
	}

	@RequestMapping(value = "/location-hierarchy-accounts/loadChildLevels", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LocationLevelDTO>> loadChildLevels(@RequestParam("level") String level,
			@RequestParam("territoryIds") String territoryIds) {

		int limit = Integer.valueOf(level) + 1;

		String[] stringArray = territoryIds.split(",");

		List<Long> parentIds = new ArrayList<>();

		for (String id : stringArray) {

			if (id.equals("-1")) {
				continue;
			}
			parentIds.add(Long.valueOf(id));

		}

		List<LocationLevelDTO> locLevels = new ArrayList<>();

		for (int i = limit; i < 7; i++) {
			LocationLevelDTO locLevel = new LocationLevelDTO();

			locLevel.setLevel(Long.valueOf(i));

			List<LocationDTO> locations = locationHierarchyService.findChildLocationsByParentIds(parentIds);

			parentIds.clear();
			for (LocationDTO loc : locations) {
				parentIds.add(Long.valueOf(loc.getId()));
			}
			locations.sort((LocationDTO h1, LocationDTO h2) -> h1.getName().compareTo(h2.getName()));
			locLevel.setLocationDtos(locations);

			if (i == (7 - 1)) {
				List<AccountProfile> accountProfiles = locationAccountProfileRepository
						.findAccountProfileByLocationIdIn(parentIds);
				List<AccountProfileDTO> accountProfileDTOs = convertToDtoList(accountProfiles);
				accountProfileDTOs
						.sort((AccountProfileDTO h1, AccountProfileDTO h2) -> h1.getName().compareTo(h2.getName()));
				locLevel.setAccountProfileDTOs(accountProfileDTOs);
			}

			locLevels.add(locLevel);

		}

		return new ResponseEntity<>(locLevels, HttpStatus.OK);
	}

	private List<AccountProfileDTO> convertToDtoList(List<AccountProfile> accountProfiles) {
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();

		for (AccountProfile accountProfile : accountProfiles) {
			AccountProfileDTO accDTo = new AccountProfileDTO();
			accDTo.setPid(accountProfile.getPid());
			accDTo.setName(accountProfile.getName());
			accountProfileDTOs.add(accDTo);
		}

		return accountProfileDTOs;
	}

	@RequestMapping(value = "/location-hierarchy-accounts/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<InventoryVoucherDetailDTO>> filterAccountProfileByLocation(
			@RequestParam("employeePid") String employeePid, @RequestParam("locationIds") String locationIds,
			@RequestParam("documentPid") String documentPid, @RequestParam("filterBy") String filterBy,
			@RequestParam LocalDate fromDate, @RequestParam LocalDate toDate, @RequestParam int noOfLevels) {

		log.info("Web Request to get all Sales Data By filter " + filterBy);

		if (filterBy.equals(LocationHierarchyAccountResource.TODAY)) {
			fromDate = LocalDate.now();
			toDate = fromDate;

		} else if (filterBy.equals(LocationHierarchyAccountResource.YESTERDAY)) {
			fromDate = LocalDate.now().minusDays(1);
			toDate = fromDate;
		} else if (filterBy.equals(LocationHierarchyAccountResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			fromDate = weekStartDate;
			toDate = LocalDate.now();
		} else if (filterBy.equals(LocationHierarchyAccountResource.MTD)) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			fromDate = monthStartDate;

			toDate = LocalDate.now();
		} else if (filterBy.equals(LocationHierarchyAccountResource.CUSTOM)) {

		}

		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDtos = filterByEmployeeAccountAndDate(employeePid,
				locationIds, fromDate, toDate, documentPid, noOfLevels);

		log.info("Sales Report Size " + inventoryVoucherDetailDtos.size());

		return new ResponseEntity<>(inventoryVoucherDetailDtos, HttpStatus.OK);

	}

	private List<InventoryVoucherDetailDTO> filterByEmployeeAccountAndDate(String employeePid, String locationIds,
			LocalDate fDate, LocalDate tDate, String documentPid, int noOfLevels) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<String> accountProfilePids = new ArrayList<>();
		List<String> employeePids = new ArrayList<>();
		List<String> documentPids = new ArrayList<>();

		String[] stringArray = locationIds.split(",");

		List<Long> parentIds = new ArrayList<>();

		for (String id : stringArray) {

			if (id.equals("-1")) {
				continue;
			}
			parentIds.add(Long.valueOf(id));

		}

		List<AccountProfile> accountProfiles = locationAccountProfileRepository
				.findAccountProfileByLocationIdIn(parentIds);

		for (AccountProfile acc : accountProfiles) {
			accountProfilePids.add(acc.getPid());
		}

		for (String emp : employeePid.split(",")) {
			employeePids.add(emp);
		}

		for (String doc : documentPid.split(",")) {
			documentPids.add(doc);
		}

		log.info(accountProfilePids.size() + "----" + employeePids.size() + "-----" + documentPids.size());

		List<Long> userIds = getUserIdsUnderEmployee(employeePids);

		List<Document> documents = documentRepository
				.findAllDocumentsByDocumentPidsAndDocumentType(DocumentType.INVENTORY_VOUCHER, documentPids);
		List<Long> documentIds = documents.stream().map(Document::getId).collect(Collectors.toList());

		List<Object[]> ivDetailDtos = inventoryVoucherDetailRepository
				.findByAccountPidInAndEmployeeIdInAndDocumentIdInDateBetween(accountProfilePids, userIds, documentIds,
						fromDate, toDate);

		List<InventoryVoucherDetailDTO> inventoryVoucherDetailDTOs = new ArrayList<>();
		if (!ivDetailDtos.isEmpty()) {
			for (Object[] object : ivDetailDtos) {
				InventoryVoucherDetailDTO ivDetailDto = new InventoryVoucherDetailDTO();
				ivDetailDto.setEmployeeName(object[0].toString());
				ivDetailDto.setProductPid(object[1].toString());
				ivDetailDto.setProductName(object[2].toString());
				ivDetailDto.setProductUnitQty(object[3] == null ? 1d : (Double) object[3]);
				ivDetailDto.setQuantity((Double) object[4]);
				ivDetailDto.setAccountName(object[5].toString());
				ivDetailDto.setCreatedDate((LocalDateTime) object[6]);
				ivDetailDto.setVisitRemarks(object[7] == null ? null : object[7].toString());

				String accountPid = object[8] == null ? null : object[8].toString();

				ivDetailDto.setRowTotal(object[9] == null ? 0 : Double.valueOf(object[9].toString()));
				ivDetailDto.setDocumentName(object[10] == null ? null : object[10].toString());
				ivDetailDto.setVolume(object[11] == null ? 0 : Double.valueOf(object[11].toString()));
				inventoryVoucherDetailDTOs.add(ivDetailDto);
			}
		}
		return inventoryVoucherDetailDTOs;

	}

	private List<Long> getUserIdsUnderEmployee(List<String> employeePids) {
		List<Long> userIds = employeeProfileRepository.findAllUserIdsByActivatedAndEmployeePidIn(true, employeePids);
		return userIds;
	}

//	@Timed
//	@RequestMapping(value = "/territory-wise-account/activateAccountProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<AccountProfileDTO> accountProfileDeactivated(@Valid @RequestParam String accountprofiles,@Valid @RequestParam  boolean status) {
//		String[] accountProfiles = accountprofiles.split(",");
//		for (String accountprofile : accountProfiles) {
//			accountProfileService.updateAccountProfileStatus(accountprofile, status);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
}
