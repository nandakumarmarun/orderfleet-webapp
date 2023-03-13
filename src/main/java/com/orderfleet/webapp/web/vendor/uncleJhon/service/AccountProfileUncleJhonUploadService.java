package com.orderfleet.webapp.web.vendor.uncleJhon.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.DistributorDealerAssociation;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DistributorDealerProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.RouteCodeRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.vendor.focus.dto.AccountProfileFocus;
import com.orderfleet.webapp.web.vendor.focus.service.AccountProfileFocusUploadService;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.AccountUJ;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.Dealer;

@Service
public class AccountProfileUncleJhonUploadService {

	private final Logger log = LoggerFactory.getLogger(AccountProfileUncleJhonUploadService.class);

	private final AccountProfileRepository accountProfileRepository;

	private final AccountProfileService accountProfileService;

	private final AccountTypeRepository accountTypeRepository;

	private final LocationRepository locationRepository;

	private final LocationAccountProfileRepository locationAccountProfileRepository;

	private final UserRepository userRepository;

	private final LocationService locationService;

	private final CompanyRepository companyRepository;

	private final DistributorDealerProfileRepository distributorDealerProfileRepository;
	
	private final LocationHierarchyRepository locationHierarchyRepository;

	public AccountProfileUncleJhonUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			AccountProfileRepository accountProfileRepository, AccountTypeRepository accountTypeRepository,
			AccountProfileService accountProfileService, LocationRepository locationRepository,
			LocationAccountProfileRepository locationAccountProfileRepository, UserRepository userRepository,
			DistributorDealerProfileRepository distributorDealerProfileRepository, LocationService locationService,
			CompanyRepository companyRepository,LocationHierarchyRepository locationHierarchyRepository) {
		super();

		this.accountProfileRepository = accountProfileRepository;
		this.accountTypeRepository = accountTypeRepository;
		this.locationRepository = locationRepository;
		this.locationAccountProfileRepository = locationAccountProfileRepository;
		this.accountProfileService = accountProfileService;
		this.userRepository = userRepository;
		this.locationService = locationService;
		this.companyRepository = companyRepository;
		this.distributorDealerProfileRepository = distributorDealerProfileRepository;
		this.locationHierarchyRepository = locationHierarchyRepository;
	}

	public void saveUpdateLocations(List<AccountUJ> accountUJ, List<Dealer> dealer) {

		log.info("Saving Locations.........");
		long start = System.nanoTime();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		Set<Location> saveUpdateLocations = new HashSet<>();

		// find all locations
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		for (AccountUJ locDto : accountUJ) {
			// check exist by name, only one exist with a name
			Optional<Location> optionalLoc = locations.stream()
					.filter(p -> p.getLocationId().equalsIgnoreCase(locDto.getLocation().trim())).findAny();
			Location location;
			if (optionalLoc.isPresent()) {
				location = optionalLoc.get();
				// if not update, skip this iteration.
				// if (!location.getThirdpartyUpdate()) {continue;}
			} else {
				location = new Location();
				location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				location.setName(locDto.getLocation().trim());
				location.setCompany(company);
			}
			location.setLocationId(locDto.getLocation().trim());
			location.setAlias(locDto.getLocation());
			location.setActivated(true);



			saveUpdateLocations.add(location);
		}

		log.info("Saving Dealer locations.....s");
		Set<Location> distinctloc = new HashSet();
		for (Dealer DealLoc : dealer) {

			// check exist by name, only one exist with a name

			Optional<Location> optionalLoc = locations.stream()
					.filter(p -> p.getLocationId().equalsIgnoreCase(DealLoc.getTown().trim())).findAny();

			Location location;
			if (optionalLoc.isPresent()) {

				location = optionalLoc.get();
				// if not update, skip this iteration.
				// if (!location.getThirdpartyUpdate()) {continue;}
			} else {
				location = new Location();
				location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				location.setName(DealLoc.getTown().trim());
				location.setCompany(company);
			}
			location.setLocationId(DealLoc.getTown().trim());
			location.setAlias(DealLoc.getTown());
			location.setActivated(true);

			saveUpdateLocations.add(location);
		}
		distinctloc = saveUpdateLocations.stream()
				.collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Location::getLocationId))));

		locationRepository.save(distinctloc);
			locationRepository.flush();
			saveUpdateLocationHierarchy(distinctloc);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	public void saveUpdateAccounts(List<AccountUJ> accountUJ) {

		log.info("Saving Account Profiles.........");

		long start = System.nanoTime();

		final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		log.info("Company ID" + companyId);
		Company company = companyRepository.findOne(companyId);
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();
		List<AccountType> accountTypes = accountTypeRepository.findAllByCompanyIdAndActivated(true);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId();
		AccountType defaultAccount = accountTypeRepository.findByCompanyIdAndName("General");

		if (accountUJ != null) {

			for (AccountUJ apDto : accountUJ) {
				// check exist by name, only one exist with a name

				Optional<AccountProfile> optionalAP = accountProfiles.stream()
						.filter(pc -> pc.getCustomerId().equalsIgnoreCase(apDto.getCode())).findAny();
				AccountProfile accountProfile;
				if (optionalAP.isPresent()) {
					accountProfile = optionalAP.get();

				} else {
					accountProfile = new AccountProfile();

					accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
					accountProfile.setAddress(apDto.getAddress());
					accountProfile.setCustomerId(apDto.getCode());
					accountProfile.setTinNo(apDto.getGstNo());
					accountProfile.setUser(user);
					accountProfile.setCompany(company);
					accountProfile.setAccountStatus(AccountStatus.Unverified);
					accountProfile.setDataSourceType(DataSourceType.VENDOR);
					accountProfile.setImportStatus(true);

				}

				String accountCode = apDto.getCode().substring(0, apDto.getCode().indexOf('.'));

				Optional<AccountType> accountType = accountTypes.stream()
						.filter(acc -> acc.getAlias().equalsIgnoreCase(accountCode)).findAny();

				if (accountType.isPresent()) {

					accountProfile.setAccountType(accountType.get());
				} else {
					accountProfile.setAccountType(defaultAccount);
				}
				accountProfile.setName(apDto.getName() + "-" + apDto.getCode());

				accountProfile.setClosingBalance(apDto.getClosingBalance());

				if (apDto.getCity() != null && !apDto.getCity().equals("")) {
					accountProfile.setCity(apDto.getCity());
				} else {
					accountProfile.setCity("No City");
				}
				if (apDto.getPhone1() != null && !apDto.getPhone1().equals("")) {

					accountProfile.setPhone1(apDto.getPhone1());

				} else {
					accountProfile.setPhone1("");
				}
//				if (apDto.getPhone2() != null && !apDto.getPhone2().equals("")) {
//                    
//					accountProfile.setPhone2(apDto.getPhone2());
//					
//					
//				} else {
//					accountProfile.setPhone2("");
//				}

//				if (apDto.getEmail1() != null && !apDto.getEmail1().equals("")) {
//					accountProfile.setEmail1(apDto.getEmail1());
//				} else {
//					accountProfile.setEmail1(" ");
//				}

				saveUpdateAccountProfiles.add(accountProfile);
			}

			accountProfileRepository.save(saveUpdateAccountProfiles);

		}

	}

	public void saveUpdateLocationAccounts(List<AccountUJ> accountUJ, List<Dealer> dealer) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		log.debug("CompnayId :" + companyId);

		List<LocationAccountProfile> newLocationAccountProfiles = new ArrayList<>();
		List<Long> locationAccountProfilesIds = new ArrayList<>();
		List<AccountProfile> accountProfiles = accountProfileService.findAllAccountProfileByCompanyId(companyId);
		List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileRepository
				.findAllByCompanyId(companyId);
		List<Location> locations = locationService.findAllLocationByCompanyId(companyId);
		log.debug("Creating New AccountProfile Object");
		for (AccountUJ locAccDto : accountUJ) {

			LocationAccountProfile profile = new LocationAccountProfile();

			Optional<AccountProfile> locAcc = accountProfiles.stream()
					.filter(acc -> acc.getCustomerId().equalsIgnoreCase(locAccDto.getCode())).findAny();

			Optional<Location> location = locations.stream()
					.filter(pl -> pl.getName().equalsIgnoreCase(locAccDto.getLocation().trim())).findAny();

			if (locAcc.isPresent() && location.isPresent()) {

				Optional<LocationAccountProfile> acc = locationAccountProfiles.stream()
						.filter(ap -> locAcc.get().getName().equalsIgnoreCase(ap.getAccountProfile().getName()))
						.findAny();

				if (acc.isPresent()) {
					locationAccountProfilesIds.add(acc.get().getId());

				}

			}

			profile.setLocation(location.get());
			profile.setAccountProfile(locAcc.get());
			profile.setCompany(company);
			newLocationAccountProfiles.add(profile);
		}

		log.debug("Removing Exixting Mapping" + locationAccountProfilesIds.size());
		if (locationAccountProfilesIds.size() != 0) {
			locationAccountProfileRepository.deleteByIdIn(companyId, locationAccountProfilesIds);
		}
		log.debug("Saving New Location Account Profiles ");
		locationAccountProfileRepository.save(newLocationAccountProfiles);
		log.debug("location account Profile Upload Sucess");

		log.debug("Saving dealer location accounts");
	
		for (Dealer dealerLocAcc : dealer) {

			LocationAccountProfile dealerprofile = new LocationAccountProfile();

			Optional<AccountProfile> locAcc = accountProfiles.stream()
					.filter(acc -> acc.getCustomerId().equalsIgnoreCase(dealerLocAcc.getDlrcode())).findAny();

			Optional<Location> location = locations.stream()
					.filter(pl -> pl.getName().equalsIgnoreCase(dealerLocAcc.getTown().trim())).findAny();

			if (locAcc.isPresent() && location.isPresent()) {

				Optional<LocationAccountProfile> acc = locationAccountProfiles.stream()
						.filter(ap -> locAcc.get().getName().equalsIgnoreCase(ap.getAccountProfile().getName()))
						.findAny();

				if (acc.isPresent()) {
					locationAccountProfilesIds.add(acc.get().getId());

				}

			}

			dealerprofile.setLocation(location.get());
			dealerprofile.setAccountProfile(locAcc.get());
			dealerprofile.setCompany(company);
			newLocationAccountProfiles.add(dealerprofile);
		}

		log.debug("Removing Exixting Mapping in dealer" + locationAccountProfilesIds.size());
		if (locationAccountProfilesIds.size() != 0) {
			locationAccountProfileRepository.deleteByIdIn(companyId, locationAccountProfilesIds);
		}
		log.debug("Saving New Location Account Profiles in dealer");
		locationAccountProfileRepository.save(newLocationAccountProfiles);
		log.debug("location account Profile Upload Sucess in dealer");
	}

	public void saveDealer(List<Dealer> dealer) {

		log.info("Saving Dealer Master.........");

		final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		log.info("Company ID" + companyId);
		Company company = companyRepository.findOne(companyId);
		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();
		List<AccountType> accountTypes = accountTypeRepository.findAllByCompanyIdAndActivated(true);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId();
		AccountType defaultAccount = accountTypeRepository.findByCompanyIdAndName("General");

		if (dealer != null) {

			for (Dealer apDto : dealer) {
				// check exist by name, only one exist with a name

				Optional<AccountProfile> optionalAP = accountProfiles.stream()
						.filter(pc -> pc.getCustomerId().equalsIgnoreCase(apDto.getDlrcode())).findAny();
				AccountProfile accountProfile;
				if (optionalAP.isPresent()) {
					accountProfile = optionalAP.get();

				} else {
					accountProfile = new AccountProfile();

					accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
					accountProfile.setAddress(apDto.getDoor() + "~" + apDto.getLandmark() + "~" + apDto.getStreet()
							+ "~" + apDto.getState());
					accountProfile.setCustomerId(apDto.getDlrcode());
					accountProfile.setTinNo(String.valueOf(0.0));
					accountProfile.setUser(user);
					accountProfile.setCompany(company);
					accountProfile.setAccountStatus(AccountStatus.Unverified);
					accountProfile.setDataSourceType(DataSourceType.VENDOR);
					accountProfile.setImportStatus(true);

				}

				String accountCode = apDto.getDlrcode().substring(0, apDto.getDlrcode().indexOf('.'));

				Optional<AccountType> accountType = accountTypes.stream()
						.filter(acc -> acc.getAlias().equalsIgnoreCase(accountCode)).findAny();

				if (accountType.isPresent()) {

					accountProfile.setAccountType(accountType.get());
				} else {
					accountProfile.setAccountType(defaultAccount);
				}
				accountProfile.setName(apDto.getDlrname() + "-" + apDto.getDlrcode());

				accountProfile.setClosingBalance(Double.valueOf(0.0));

				if (apDto.getPlace() != null && !apDto.getPlace().equals("")) {
					accountProfile.setCity(apDto.getPlace());
				} else {
					accountProfile.setCity("No City");
				}
				if (apDto.getTelno() != null && !apDto.getTelno().equals("")) {

					accountProfile.setPhone1(apDto.getTelno());

				} else {
					accountProfile.setPhone1("");
				}
				if (apDto.getPin() != null && !apDto.getPin().equals("")) {

					accountProfile.setPin(apDto.getPin());

				} else {
					accountProfile.setPin("");
				}

//				if (apDto.getPhone2() != null && !apDto.getPhone2().equals("")) {
//                    
//					accountProfile.setPhone2(apDto.getPhone2());
//					
//					
//				} else {
//					accountProfile.setPhone2("");
//				}

//				if (apDto.getEmail1() != null && !apDto.getEmail1().equals("")) {
//					accountProfile.setEmail1(apDto.getEmail1());
//				} else {
//					accountProfile.setEmail1(" ");
//				}

				saveUpdateAccountProfiles.add(accountProfile);
			}

			accountProfileRepository.save(saveUpdateAccountProfiles);

		}

	}

	public void saveDistributorDealerAssociation(List<Dealer> dealer) {
		System.out.println("Enter here to save distributor dealer association");

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);

		List<AccountProfile> accountProfiles = accountProfileService.findAllAccountProfileByCompanyId(companyId);
		List<DistributorDealerAssociation> distributDeal = distributorDealerProfileRepository
				.findAllByCompanyId(companyId);
		List<DistributorDealerAssociation> DistributorDealerList = new ArrayList<>();

		for (Dealer dealers : dealer) {

			Optional<DistributorDealerAssociation> association = distributDeal.stream()
					.filter(dd -> dd.getDealer().getCustomerId().equalsIgnoreCase(dealers.getDlrcode())
							&& dd.getDistributor().getCustomerId().equalsIgnoreCase(dealers.getDistcode()))
					.findAny();
			DistributorDealerAssociation distributorDealer = new DistributorDealerAssociation();
			if (association.isPresent()) {

				distributorDealer = association.get();
			} else {
				Optional<AccountProfile> dist = accountProfiles.stream()
						.filter(dis -> dis.getCustomerId().equalsIgnoreCase(dealers.getDistcode())).findAny();
				Optional<AccountProfile> deal = accountProfiles.stream()
						.filter(del -> del.getCustomerId().equalsIgnoreCase(dealers.getDlrcode())).findAny();

				if (dist.isPresent() && deal.isPresent()) {
					distributorDealer.setDistributor(dist.get());
					distributorDealer.setDealer(deal.get());
					distributorDealer.setCompany(company);
				}

			}
			DistributorDealerList.add(distributorDealer);
		}
		distributorDealerProfileRepository.save(DistributorDealerList);
		System.out.println("Successfully Saved");

	}
	
	private void saveUpdateLocationHierarchy(Set<Location> distinctloc) {

		List<LocationHierarchyDTO> locationHierarchyDTOs = locationToLocationHierarchyDtos(distinctloc);

		log.info("Saving Location Hierarchies.........");

		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();

		Long version;

		// Only one version of a company hierarchy is active at a time
		Optional<LocationHierarchy> locationHierarchy = locationHierarchyRepository
				.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(companyId);

		if (locationHierarchy.isPresent()) {
			locationHierarchyRepository.updateLocationHierarchyInactivatedFor(ZonedDateTime.now(),
					locationHierarchy.get().getVersion());
			version = locationHierarchy.get().getVersion() + 1;

		} else {
			version = 1L;
		}

		// find all locations
		List<Location> locations = locationRepository.findByCompanyIdAndActivatedTrue(companyId);

		// create hierarchy
		for (LocationHierarchyDTO locationDTO : locationHierarchyDTOs) {

			// check location exist
			Optional<Location> optionalLoc = locations.stream()
					.filter(p -> p.getName().equals(locationDTO.getLocationName())).findAny();

			if (optionalLoc.isPresent()) {
				if (locationDTO.getParentName() != null && locationDTO.getParentName().length() > 0) {
					// check parent location exist
					Optional<Location> optionalParentLoc = locations.stream()
							.filter(p -> p.getName().equals(locationDTO.getParentName())).findAny();

					if (optionalParentLoc.isPresent()) {
						locationHierarchyRepository.insertLocationHierarchyWithParent(version,
								optionalLoc.get().getId(), optionalParentLoc.get().getId());
					}
				} else {
					locationHierarchyRepository.insertLocationHierarchyWithNoParent(version, optionalLoc.get().getId());
				}
			}
		}
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);

	}

	private List<LocationHierarchyDTO> locationToLocationHierarchyDtos(Set<Location> locationDtos) {

		Set<String> locations = new HashSet<>();

		for (Location locationDTO : locationDtos) {

			locations.add(locationDTO.getName());
		}

		List<LocationHierarchyDTO> locationHierarchyDTOs = new ArrayList<>();

		LocationHierarchyDTO defaultLocationHierarchyDTO = new LocationHierarchyDTO();

		defaultLocationHierarchyDTO.setLocationName("Territory");
		defaultLocationHierarchyDTO.setParentName(null);
		locationHierarchyDTOs.add(defaultLocationHierarchyDTO);

		for (String location : locations) {

			LocationHierarchyDTO locationHierarchyDTO = new LocationHierarchyDTO();

			locationHierarchyDTO.setLocationName(location);
			locationHierarchyDTO.setParentName("Territory");
			locationHierarchyDTOs.add(locationHierarchyDTO);

		}

		return locationHierarchyDTOs;
	}


}
