package com.orderfleet.webapp.web.vendor.UncleJhoneChennai.PallikkuppamDepot.Services;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileGeoLocationTaggingService;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.AccountUJ;
import com.orderfleet.webapp.web.vendor.uncleJhon.DTO.Dealer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PallikkuppamAccountsUploadService {
	String PID_PREFIX = "DDA-";

	final String FACTORY_CODE ="KL";
	final String FACTORY_NAME = "PALLIKKUPPAM DEPOT";



	private final Logger log = LoggerFactory.getLogger(PallikkuppamAccountsUploadService.class);

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

	@Inject
	private AccountProfileGeoLocationTaggingRepository accountProfileGeoLocationTaggingRepository;


	public PallikkuppamAccountsUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
                                             AccountProfileRepository accountProfileRepository, AccountTypeRepository accountTypeRepository,
                                             AccountProfileService accountProfileService, LocationRepository locationRepository,
                                             LocationAccountProfileRepository locationAccountProfileRepository, UserRepository userRepository,
                                             DistributorDealerProfileRepository distributorDealerProfileRepository, LocationService locationService,
                                             CompanyRepository companyRepository, LocationHierarchyRepository locationHierarchyRepository) {
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

	public void saveUpdateLocations(
			List<AccountUJ> accountUJ,
			List<Dealer> dealer) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";
		log.info(Thread + "Enter : saveUpdateAccounts() ");
		long start = System.nanoTime();

		Set<Location> saveUpdateLocations = new HashSet<>();
		Set<Location> distinctloc = new HashSet<>();

		List<Location> locations =
				locationRepository
						.findAllByCompanyId(company.getId());

		Optional<Location> optionalParentLoc =
				locations
						.stream()
						.filter(p -> p.getLocationId()
								.equalsIgnoreCase(FACTORY_CODE))
						.findAny();

		log.info(Thread + "Parent Location : " + (optionalParentLoc.isPresent() ? optionalParentLoc.get().getName() : "No Location Present"));

		if(!optionalParentLoc.isPresent()){
			Location location;
			location = new Location();
			location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
			location.setCompany(company);
			location.setLocationId(FACTORY_CODE);
			location.setName(FACTORY_NAME);
			location.setAlias(FACTORY_NAME);
			location.setDescription(FACTORY_NAME);
			location.setActivated(true);
			saveUpdateLocations.add(location);
		}

		log.info(Thread + "Processing Data");
		log.info(Thread + "Saving Customer Locations");

		for (AccountUJ locDto : accountUJ) {
			Location location;
			Optional<Location> optionalLoc =
					locations
							.stream()
							.filter(p -> p.getLocationId()
									.equalsIgnoreCase(locDto.getLocation().trim()))
							.findAny();

			if (optionalLoc.isPresent()) {
				location = optionalLoc.get();
			} else {
				location = new Location();
				location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				location.setCompany(company);
				location.setLocationId(locDto.getLocation().trim());
			}
			location.setName(locDto.getLocation().trim());
			location.setAlias(locDto.getLocation());
			location.setActivated(true);
			location.setDescription(FACTORY_NAME);
			saveUpdateLocations.add(location);
		}

		log.info(Thread + "Saving Dealer Locations");

		for (Dealer DealLoc : dealer) {
			Location location;
			Optional<Location> optionalLoc =
					locations
							.stream()
							.filter(p -> p.getLocationId()
									.equalsIgnoreCase(DealLoc.getTown().trim()))
							.findAny();


			if (optionalLoc.isPresent()) {
				location = optionalLoc.get();
			} else {
				location = new Location();
				location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				location.setCompany(company);
				location.setLocationId(DealLoc.getTown().trim());
			}
			location.setName(DealLoc.getTown().trim());
			location.setAlias(DealLoc.getTown());
			location.setActivated(true);
			location.setDescription(FACTORY_NAME);
			saveUpdateLocations.add(location);
		}

		distinctloc = saveUpdateLocations
				.stream()
				.collect(
						Collectors.toCollection(()
								-> new TreeSet<>(Comparator
								.comparing(Location::getLocationId))));

		locationRepository.save(distinctloc);
		locationRepository.flush();
		saveUpdateLocationHierarchy(distinctloc);
		saveSync(start);
	}

	public void saveUpdateAccounts(
			List<AccountUJ> accountUJ) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";
		log.info(Thread + "Enter : saveUpdateLocations() ");

		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

		List<AccountType> accountTypes = accountTypeRepository.findAllByCompanyIdAndActivated(true);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId();

		final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		AccountType defaultAccount = accountTypeRepository.findByCompanyIdAndName("General");

		log.debug(Thread + "Default AccountType : " + defaultAccount.getName() );
		log.debug(Thread + "Processing Customer Data : " + accountProfiles.size());

		if (accountUJ != null) {
			for (AccountUJ apDto : accountUJ) {

				Optional<AccountProfile> optionalAP =
						accountProfiles
								.stream()
								.filter(pc -> pc.getCustomerId()
										.equalsIgnoreCase(apDto.getNumcode().trim()))
								.findAny();

				AccountProfile accountProfile;
				if (optionalAP.isPresent()) {
					accountProfile = optionalAP.get();
				} else {
					accountProfile = new AccountProfile();
					accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
					accountProfile.setAddress(apDto.getAddress());
					accountProfile.setTinNo(apDto.getGstNo());
					accountProfile.setUser(user);
					accountProfile.setCompany(company);
					accountProfile.setAccountStatus(AccountStatus.Unverified);
					accountProfile.setDataSourceType(DataSourceType.VENDOR);
					accountProfile.setImportStatus(true);
				}
				accountProfile.setName(apDto.getName().trim() + "-" + apDto.getCode().trim());
				accountProfile.setClosingBalance(apDto.getClosingBalance());
				accountProfile.setCustomerId(apDto.getNumcode().trim());
				accountProfile.setCustomerCode(apDto.getCode().trim());
				accountProfile.setLocation(apDto.getLocation());
				accountProfile.setMailingName(FACTORY_CODE);
				accountProfile.setDescription(FACTORY_NAME);

				String accountCode = apDto.getCode().substring(0, apDto.getCode().indexOf('.'));
				Optional<AccountType> accountType = accountTypes
						.stream()
						.filter(acc -> acc.getAlias()
								.equalsIgnoreCase(accountCode))
						.findAny();

				if (accountType.isPresent()) {
					accountProfile.setAccountType(accountType.get());
				} else {
					accountProfile.setAccountType(defaultAccount);
				}

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

				if (!apDto.getLatitude().trim().isEmpty()) {
					String coordinates = apDto.getLatitude();
					String[] parts = coordinates.split(",");
					String latitudeString = parts[0];
					String longitudeString = parts[1];
					if (longitudeString.trim() != null
							&& latitudeString.trim() != null
							&& !latitudeString.trim().isEmpty()
							&& !longitudeString.trim().isEmpty()
							&& longitudeString.trim() != "NIL"
							&& latitudeString.trim() != "NIL") {
						try{
							double latitude = Double.parseDouble(latitudeString.trim());
							double longitude = Double.parseDouble(longitudeString.trim());
							accountProfile.setLatitude(BigDecimal.valueOf(latitude));
							accountProfile.setLongitude(BigDecimal.valueOf(longitude));
						}
						catch(Exception e){
							log.debug("Exception Catched : " +e.getMessage());
						}

						accountProfile.setGeoTaggingType(GeoTaggingType.WEB_TAGGED);
					}
				}
				saveUpdateAccountProfiles.add(accountProfile);
			}
			log.debug(Thread + "Saving Account Profiles");
			try{
				accountProfileRepository.save(saveUpdateAccountProfiles);

				accountProfileService.autoTaskCreationForAccountProfiles( company);
			}
			catch(Exception e){
				log.debug("Exception Catched : " +e.getMessage());
			}
		}
	}

	public void saveUpdateLocationAccounts(
			List<AccountUJ> accountUJ,
			List<Dealer> dealer) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";
		log.info(Thread + "Enter : saveUpdateLocations() ");

		List<LocationAccountProfile> newLocationAccountProfiles = new ArrayList<>();
		List<Long> locationAccountProfilesIds = new ArrayList<>();

		List<AccountProfile> accountProfiles = accountProfileService
				.findAllAccountProfileByCompanyId(
						companyId);

		List<LocationAccountProfile> locationAccountProfiles = locationAccountProfileRepository
				.findAllByCompanyId(
						companyId);

		List<Location> locations = locationService.findAllLocationByCompanyId(companyId);

		log.debug(Thread +"ProcessingData ");
		for (AccountUJ locAccDto : accountUJ) {
			LocationAccountProfile profile = new LocationAccountProfile();
			Optional<AccountProfile> locAcc =
					accountProfiles
							.stream()
							.filter(acc -> acc.getCustomerId()
									.equalsIgnoreCase(locAccDto.getNumcode().trim()))
							.findAny();

			Optional<Location> location =
					locations
							.stream()
							.filter(pl -> pl.getName()
									.equalsIgnoreCase(locAccDto.getLocation().trim()))
							.findAny();

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

		log.debug(Thread + "Removing Existing Mapping" + locationAccountProfilesIds.size());
		if (locationAccountProfilesIds.size() != 0) {
			locationAccountProfileRepository.deleteByIdIn(companyId, locationAccountProfilesIds);
		}

		log.debug(Thread + "Saving New Location Account Profiles ");
		locationAccountProfileRepository.save(newLocationAccountProfiles);

		log.debug(Thread + "Saving dealer location accounts");
		for (Dealer dealerLocAcc : dealer) {
			LocationAccountProfile dealerprofile = new LocationAccountProfile();

			Optional<AccountProfile> locAcc = accountProfiles
					.stream()
					.filter(acc -> acc.getCustomerId()
							.equalsIgnoreCase(dealerLocAcc.getDlrcode().trim()))
					.findAny();

			Optional<Location> location = locations
					.stream()
					.filter(pl -> pl.getName()
							.equalsIgnoreCase(dealerLocAcc.getTown().trim()))
					.findAny();

			if (locAcc.isPresent() && location.isPresent()) {
				Optional<LocationAccountProfile> acc = locationAccountProfiles
						.stream()
						.filter(ap -> locAcc.get().getName()
								.equalsIgnoreCase(ap.getAccountProfile().getName()))
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

		log.debug(Thread + "Removing Existing Mapping in dealer" + locationAccountProfilesIds.size());
		if (locationAccountProfilesIds.size() != 0) {
			locationAccountProfileRepository.deleteByIdIn(companyId, locationAccountProfilesIds);
		}

		log.debug(Thread + "Saving New Location Account Profiles in dealer");
		locationAccountProfileRepository.save(newLocationAccountProfiles);
	}

	public void saveAccountProfileGeoLocation(
			List<AccountUJ> accountUJ) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";
		log.info(Thread + "Enter : saveAccountProfileGeoLocation() ");

		List<AccountProfileGeoLocationTagging> newAssociaton = new ArrayList<>();

		String userLogin = SecurityUtils.getCurrentUserLogin();

		User user = userRepository.findByCompanyIdAndLogin(companyId, userLogin);

		List<String> customerId = accountUJ
				.stream()
				.map(acc -> acc.getNumcode())
				.collect(Collectors.toList());

		List<AccountProfile> acccountProfiles =
				accountProfileRepository
						.findAccountProfilesByCompanyIdAndCustomerIdIn(
								companyId, customerId);

		List<AccountProfileGeoLocationTagging> geotag =
				accountProfileGeoLocationTaggingRepository
						.findAllByCompanyId(companyId);

		for (AccountProfile accProfile : acccountProfiles) {
			AccountProfileGeoLocationTagging apglt = new AccountProfileGeoLocationTagging();

			Optional<AccountProfileGeoLocationTagging> accGeotag =
					geotag.stream()
							.filter(acc -> acc.getAccountProfile()
									.getCustomerId()
									.equals(accProfile.getCustomerId()))
							.findAny();

			if (accGeotag.isPresent()) {
				continue;
			} else {
				apglt.setPid(AccountProfileGeoLocationTaggingService.PID_PREFIX + RandomUtil.generatePid());
				apglt.setAccountProfile(accProfile);
				apglt.setLatitude(accProfile.getLatitude());
				apglt.setLongitude(accProfile.getLongitude());
				apglt.setLocation(accProfile.getLocation());
				apglt.setSendDate(LocalDateTime.now());
				apglt.setCompany(company);
				apglt.setCreatedDate(LocalDateTime.now());
				apglt.setUser(user);
				newAssociaton.add(apglt);
			}
		}
		if (newAssociaton.size() != 0) {
			accountProfileGeoLocationTaggingRepository.save(newAssociaton);
		}
	}

	public void saveDealer(
			List<Dealer> dealer) {
		final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";
		log.info(Thread + "Enter : saveDealer() ");

		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

		List<AccountType> accountTypes = accountTypeRepository.findAllByCompanyIdAndActivated(true);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId();

		AccountType defaultAccount = accountTypeRepository.findByCompanyIdAndName("General");
		log.debug(Thread + "Default AccountType : " + defaultAccount.getName() );
		log.debug(Thread + "Processing Customer Data : " + accountProfiles.size());

		if (dealer != null) {
			for (Dealer apDto : dealer) {
				AccountProfile accountProfile;
				Optional<AccountProfile> optionalAP =
						accountProfiles
								.stream()
								.filter(pc -> pc.getCustomerId()
										.equalsIgnoreCase(apDto.getDlrcode().trim()))
								.findAny();

				if (optionalAP.isPresent()) {
					accountProfile = optionalAP.get();
				} else {
					accountProfile = new AccountProfile();
					accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
					accountProfile.setAddress(apDto.getDoor().trim() + "~" + apDto.getLandmark().trim() + "~"
							+ apDto.getStreet().trim() + "~" + apDto.getState().trim());
					accountProfile.setTinNo(String.valueOf(0.0));
					accountProfile.setUser(user);
					accountProfile.setCompany(company);
					accountProfile.setAccountStatus(AccountStatus.Unverified);
					accountProfile.setDataSourceType(DataSourceType.VENDOR);
					accountProfile.setImportStatus(true);
				}
				accountProfile.setCustomerId(apDto.getDlrcode().trim());
				accountProfile.setCustomerCode(apDto.getDlrcode().trim());
				accountProfile.setName(apDto.getDlrname().trim() + "-" + apDto.getDlrcode().trim());
				accountProfile.setClosingBalance(Double.valueOf(0.0));

				String accountCode = apDto.getDlrcode().substring(0, apDto.getDlrcode().indexOf('.'));
				Optional<AccountType> accountType =
						accountTypes
								.stream()
								.filter(acc -> acc.getAlias()
										.equalsIgnoreCase(accountCode))
								.findAny();

				if (accountType.isPresent()) {
					accountProfile.setAccountType(accountType.get());
				} else {
					accountProfile.setAccountType(defaultAccount);
				}

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
				saveUpdateAccountProfiles.add(accountProfile);
			}
			log.debug(Thread + "Saving Dealers " + saveUpdateAccountProfiles.size());
			accountProfileRepository.save(saveUpdateAccountProfiles);
		}
	}

	public void saveDistributorDealerAssociation(
			List<Dealer> dealer) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";
		log.info(Thread + "Enter : saveDistributorDealerAssociation() ");

		List<DistributorDealerAssociation> DistributorDealerList = new ArrayList<>();

		if(dealer.size() != 0) {
			List<String> customerId =
					dealer.stream()
							.map(deal -> deal.getDlrcode().trim())
							.collect(Collectors.toList());

			log.debug(Thread + " DealerProfiles : " + customerId.size());

			List<AccountProfile> dealerProfiles =
					accountProfileRepository
							.findAllAccountProfileByCustomerIdInAndCompanyId(
									customerId, companyId);

			log.debug(Thread + " DealerProfiles : " + dealerProfiles.size());

			List<String> distCode =
					dealer.stream()
							.map(dist -> dist.getDistcode().trim())
							.collect(Collectors.toList());

			List<AccountProfile> customerprofiles =
					accountProfileRepository
							.findAllAccountProfleByCompanyIdAndCustomerCodeIn(
									companyId,
									distCode);
			log.debug(Thread + "Customers : " + customerprofiles.size());

			List<DistributorDealerAssociation> distributDeal =
					distributorDealerProfileRepository
							.findAllByCompanyId(companyId);

			log.debug(Thread + " DistributorDealerAssociation : " + distributDeal.size());
			for (Dealer dealers : dealer) {
				Optional<DistributorDealerAssociation> association =
						distributDeal
								.stream()
								.filter(dd -> dd.getDealer().getCustomerId().trim()
										.equalsIgnoreCase(dealers.getDlrcode().trim())
										&& dd.getDistributor().getCustomerCode().trim()
										.equalsIgnoreCase(dealers.getDistcode().trim()))
								.findAny();
				DistributorDealerAssociation distributorDealer = new DistributorDealerAssociation();
				if (association.isPresent()) {
					continue;
				} else {
					Optional<AccountProfile> dist = customerprofiles
							.stream()
							.filter(dis -> dis.getCustomerCode()
									.equalsIgnoreCase(dealers.getDistcode().trim()))
							.findAny();

					Optional<AccountProfile> deal = dealerProfiles
							.stream()
							.filter(del -> del.getCustomerId().trim()
									.equalsIgnoreCase(dealers.getDlrcode().trim()))
							.findAny();

					if (dist.isPresent() && deal.isPresent()) {
						distributorDealer.setPid(PID_PREFIX + RandomUtil.generatePid());
						distributorDealer.setDistributor(dist.get());
						distributorDealer.setDealer(deal.get());
						distributorDealer.setCompany(company);
						DistributorDealerList.add(distributorDealer);
					}
				}
			}
			distributorDealerProfileRepository.save(DistributorDealerList);
			log.debug(Thread + " DistributorDealerList" + DistributorDealerList.size());
		}
	}

	private void saveUpdateLocationHierarchy(
			Set<Location> distinctloc) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";

		log.info(Thread + "Enter : saveUpdateLocationHierarchy() ");

		long start = System.nanoTime();
		Long version;
		List<LocationHierarchyDTO> locationHierarchyDTOs = locationToLocationHierarchyDtos(distinctloc);

		Optional<LocationHierarchy> locationHierarchy =
				locationHierarchyRepository
						.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(
								companyId);

		log.debug(Thread +" Location Hierarchy is present : " + locationHierarchy.isPresent());
		if (locationHierarchy.isPresent()) {
			log.debug("Location Hierarchy Is Present");
			Optional<Location> optionalParentLoc = locationRepository
					.findOneByLocationIdAndCompanyId(
							FACTORY_CODE,companyId);

			if(optionalParentLoc.isPresent()){
				log.debug(Thread + "Parent Location : " + (optionalParentLoc.isPresent() ? optionalParentLoc.get().getName() : "No Parent"));
				locationHierarchyRepository.deleteByCompanyIdAndParent(companyId,optionalParentLoc.get().getId());
			}
			version = locationHierarchy.get().getVersion() + 1;
			log.debug(Thread + " Hierarchy Version : " + version );
		} else {
			log.debug("Location Hierarchy Not Present");
			version = 1L;
			log.debug(Thread + " Hierarchy Version : " + version );
		}

		List<Location> locations =
				locationRepository
						.findByCompanyIdAndActivatedTrue(companyId);

		log.debug(Thread +" locations: " + locations.size());

		for (LocationHierarchyDTO locationDTO : locationHierarchyDTOs) {
			Optional<Location> optionalLoc =
					locations
							.stream()
							.filter(p -> p.getName()
									.equals(locationDTO.getLocationName()))
							.findAny();

			if (optionalLoc.isPresent()) {
				if (locationDTO.getParentName() != null
						&& locationDTO.getParentName().length() > 0) {

					Optional<Location> optionalParentLoc =
							locations
									.stream()
									.filter(p -> p.getName()
											.equals(locationDTO.getParentName()))
									.findAny();

					if (optionalParentLoc.isPresent()) {
						locationHierarchyRepository
								.insertLocationHierarchyWithParent(
										version,
										optionalLoc.get().getId(),
										optionalParentLoc.get().getId());
					}
				} else {
					locationHierarchyRepository
							.insertLocationHierarchyWithNoParent(
									version,
									optionalLoc.get().getId());
				}
			}
		}
		saveSync(start);
	}

	private List<LocationHierarchyDTO> locationToLocationHierarchyDtos(
			Set<Location> locationDtos) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		String Thread = "Thread "+RandomUtil.generateThread()+" -- ["+company.getLegalName()+"] : ";

		LocationHierarchyDTO defaultLocationHierarchyDTO = new LocationHierarchyDTO();

		Set<String> locations = new HashSet<>();

		List<LocationHierarchyDTO> locationHierarchyDTOs = new ArrayList<>();

		Optional<Location> optionalParent = locationRepository.findOneByLocationIdAndCompanyId("Territory",companyId);

		if(optionalParent.isPresent()){
			Optional<LocationHierarchy> optTerritory =
					locationHierarchyRepository
							.findByCompanyIdAndLocation(
									companyId,optionalParent.get().getId());
			if(!optTerritory.isPresent()){
				defaultLocationHierarchyDTO.setLocationName("Territory");
				defaultLocationHierarchyDTO.setParentName(null);
				locationHierarchyDTOs.add(defaultLocationHierarchyDTO);
			}
		}

		Optional<Location> optionalParentLoc = locationRepository.findOneByLocationIdAndCompanyId(FACTORY_CODE,companyId);

		if(optionalParentLoc.isPresent()){
			Optional<LocationHierarchy> optLocationHierarchy =
					locationHierarchyRepository
							.findByCompanyIdAndLocation(
									companyId,optionalParentLoc.get().getId());
			if(!optLocationHierarchy.isPresent()){
				LocationHierarchyDTO parentLocationHierarchyDTO = new LocationHierarchyDTO();
				parentLocationHierarchyDTO.setLocationName(optionalParentLoc.get().getName());
				parentLocationHierarchyDTO.setParentName("Territory");
				locationHierarchyDTOs.add(parentLocationHierarchyDTO);
			}
		}

		log.debug(Thread + "Parent Location : " + (optionalParentLoc.isPresent() ? optionalParentLoc.get().getName() : "No Parent"));

		for (Location locationDTO : locationDtos) {
			locations.add(locationDTO.getName());
		}

		log.debug(Thread + "Processing Locations");
		for (String location : locations) {
			LocationHierarchyDTO locationHierarchyDTO = new LocationHierarchyDTO();
			locationHierarchyDTO.setLocationName(location);
			if (optionalParentLoc.isPresent()){
				locationHierarchyDTO.setParentName(optionalParentLoc.get().getName());
			}
			locationHierarchyDTOs.add(locationHierarchyDTO);
		}
		return locationHierarchyDTOs;
	}

	private void saveSync(long start) {
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);
	}

}
