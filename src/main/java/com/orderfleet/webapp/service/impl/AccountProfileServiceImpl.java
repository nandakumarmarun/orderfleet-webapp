package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StageNameType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.projections.CustomAccountProfiles;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LeadManagementDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

/**
 * Service Implementation for managing AccountProfile.
 *
 * @author Muhammed Riyas T
 * @since June 02, 2016
 */
@Service
@Transactional
public class AccountProfileServiceImpl implements AccountProfileService {

	private final Logger log = LoggerFactory.getLogger(AccountProfileServiceImpl.class);

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private StageHeaderRepository stageHeaderRepo;

	/**
	 * Save a accountProfile.
	 *
	 * @param accountProfileDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public AccountProfileDTO save(AccountProfileDTO accountProfileDTO) {
		log.debug("Request to save AccountProfile : {}", accountProfileDTO);
		accountProfileDTO.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid()); // set
																								// pid
		AccountProfile accountProfile = accountProfileMapper.accountProfileDTOToAccountProfile(accountProfileDTO);
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (opUser.isPresent()) {
			accountProfile.setUser(opUser.get());
		} else {
			accountProfile.setUser(userRepository.findOneByLogin("siteadmin").get());
		}

		// set company
		accountProfile.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		accountProfile.setAccountStatus(accountProfileDTO.getAccountStatus());
		accountProfile = accountProfileRepository.save(accountProfile);
		AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
		return result;
	}

	/**
	 * Update a accountProfile.
	 *
	 * @param accountProfileDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public AccountProfileDTO update(AccountProfileDTO accountProfileDTO) {
		log.debug("Request to Update AccountProfile : {}", accountProfileDTO);

		return accountProfileRepository.findOneByPid(accountProfileDTO.getPid()).map(accountProfile -> {
			accountProfile.setName(accountProfileDTO.getName());
			accountProfile.setAlias(accountProfileDTO.getAlias());
			accountProfile
					.setAccountType(accountTypeRepository.findOneByPid(accountProfileDTO.getAccountTypePid()).get());
			accountProfile.setAddress(accountProfileDTO.getAddress());
			accountProfile.setCity(accountProfileDTO.getCity());
			accountProfile.setDescription(accountProfileDTO.getDescription());
			accountProfile.setEmail1(accountProfileDTO.getEmail1());
			accountProfile.setEmail2(accountProfileDTO.getEmail2());
			accountProfile.setWhatsAppNo(accountProfileDTO.getWhatsAppNo());
			accountProfile.setLocation(accountProfileDTO.getLocation());
			if(accountProfileDTO.getLatitude() != null && accountProfileDTO.getLongitude() != null) {
				accountProfile.setLatitude(accountProfileDTO.getLatitude());
				accountProfile.setLongitude(accountProfileDTO.getLongitude());
			}
			accountProfile.setPhone1(accountProfileDTO.getPhone1());
			accountProfile.setPhone2(accountProfileDTO.getPhone2());
			accountProfile.setPin(accountProfileDTO.getPin());
			accountProfile.setImportStatus(accountProfileDTO.getIsImportStatus());
			accountProfile.setCreditDays(accountProfileDTO.getCreditDays());
			accountProfile.setCreditLimit(accountProfileDTO.getCreditLimit());
			accountProfile.setContactPerson(accountProfileDTO.getContactPerson());
			accountProfile.setDefaultDiscountPercentage(accountProfileDTO.getDefaultDiscountPercentage());
			accountProfile.setClosingBalance(accountProfileDTO.getClosingBalance());
			accountProfile.setTinNo(accountProfileDTO.getTinNo());
			if(accountProfileDTO.getGeoTaggingType() != null) {
				accountProfile.setGeoTaggingType(accountProfileDTO.getGeoTaggingType());
				accountProfile.setGeoTaggedTime(accountProfileDTO.getGeoTaggedTime());
			Optional<User> opGeoTagUser = 
					userRepository.findOneByLogin(accountProfileDTO.getGeoTaggedUserLogin() == null ? "" : accountProfileDTO.getGeoTaggedUserLogin());
				if(opGeoTagUser.isPresent()) {
					accountProfile.setGeoTaggedUser(opGeoTagUser.get());
				}
			}
			
			// accountProfile.setActivated(accountProfileDTO.getActivated());
			accountProfile
					.setDefaultPriceLevel(priceLevelRepository.findOneByPid(accountProfileDTO.getDefaultPriceLevelPid())
							.map(priceLevel -> priceLevel).orElse(null));

			if (accountProfile.getUser() == null) {
				Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
				if (opUser.isPresent()) {
					accountProfile.setUser(opUser.get());
				} else {
					accountProfile.setUser(userRepository.findOneByLogin("siteadmin").get());
				}
			}

			accountProfile = accountProfileRepository.save(accountProfile);
			AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the categorys.
	 *
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AccountProfile> findAll(Pageable pageable) {
		log.debug("Request to get all AccountProfiles");
		Page<AccountProfile> result = accountProfileRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the accountProfiles.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findAllByCompany() {
		log.debug("Request to get all accountProfiles");
		List<AccountProfile> accountProfileList = accountProfileRepository.findAllByCompanyId();
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	/**
	 * Get all the accountProfiles.
	 *
	 * @param pid
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findAllByAccountType(String pid) {
		log.debug("Request to get all accountProfiles by account type");
		List<AccountProfile> accountProfileList = accountProfileRepository.findAllByAccountTypePid(pid);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	/**
	 * Get all the categorys.
	 *
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AccountProfileDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all AccountProfiles");
		Page<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId(pageable);
		Page<AccountProfileDTO> result = new PageImpl<AccountProfileDTO>(
				accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfiles.getContent()), pageable,
				accountProfiles.getTotalElements());
		return result;
	}

	/**
	 * Get one accountProfile by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public AccountProfileDTO findOne(Long id) {
		log.debug("Request to get AccountProfile : {}", id);
		AccountProfile accountProfile = accountProfileRepository.findOne(id);
		AccountProfileDTO accountProfileDTO = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
		return accountProfileDTO;
	}

	/**
	 * Get one accountProfile by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountProfileDTO> findOneByPid(String pid) {
		log.debug("Request to get AccountProfile by pid : {}", pid);
		return accountProfileRepository.findOneByPid(pid).map(accountProfile -> {
			AccountProfileDTO accountProfileDTO = accountProfileMapper
					.accountProfileToAccountProfileDTO(accountProfile);
			return accountProfileDTO;
		});
	}

	/**
	 * Get one accountProfile by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountProfileDTO> findByName(String name) {
		log.debug("Request to get AccountProfile by name : {}", name);
		return accountProfileRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(accountProfile -> {
					AccountProfileDTO accountProfileDTO = accountProfileMapper
							.accountProfileToAccountProfileDTO(accountProfile);
					return accountProfileDTO;
				});
	}

	/**
	 * Delete the accountProfile by id.
	 *
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete AccountProfile : {}", pid);
		accountProfileRepository.findOneByPid(pid).ifPresent(accountProfile -> {
			accountProfileRepository.delete(accountProfile.getId());
		});
	}

	/**
	 * Get one accountProfile by alias.
	 *
	 * @param alias the alias of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountProfileDTO> findByAlias(String alias) {
		log.debug("Request to get AccountProfile by alias : {}", alias);
		return accountProfileRepository
				.findByCompanyIdAndAliasIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), alias)
				.map(accountProfile -> {
					AccountProfileDTO accountProfileDTO = accountProfileMapper
							.accountProfileToAccountProfileDTO(accountProfile);
					return accountProfileDTO;
				});
	}

	@Override
	public List<AccountProfileDTO> findAllByAccountTypeName(String accountTypeName) {
		log.debug("Request to get all accountProfiles by account type name");
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyIdAndAccountTypeName(SecurityUtils.getCurrentUsersCompanyId(), accountTypeName);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAllByCompanyAndAccountImportStatus(boolean importStatus) {
		log.debug("Request to get all accountProfiles by import status");
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndAccountImportStatus(importStatus);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public AccountProfileDTO updateAccountProfileImportStatus(AccountProfileDTO accountProfileDTO) {
		AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfileDTO.getPid()).get();
		accountProfile.setImportStatus(true);
		accountProfileRepository.save(accountProfile);
		return accountProfileDTO;
	}

	@Override
	public List<AccountProfileDTO> findUsersAccountProfile(String userPid) {
		// current user employee locations
		List<Location> locations = employeeProfileLocationRepository.findLocationsByUserPid(userPid);
		List<LocationAccountProfile> accountProfiles = locationAccountProfileRepository
				.findDistinctAccountProfileByAccountProfileActivatedTrueAndLocationInOrderByIdAsc(locations);
		Set<AccountProfile> result = accountProfiles.stream().map(la -> la.getAccountProfile()).distinct()
				.collect(Collectors.toSet());
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(new ArrayList<>(result));
	}

	/**
	 * Update the accountProfile by pid.
	 *
	 * @param pid    the pid of the entity
	 * @param active the active of the entity
	 * @return the entity
	 */
	@Override
	public AccountProfileDTO updateAccountProfileStatus(String pid, boolean active) {
		log.debug("Request to Update ProductGroup activate or deactivate: {}", pid);
		return accountProfileRepository.findOneByPid(pid).map(accountProfile -> {
			accountProfile.setActivated(active);
			accountProfile = accountProfileRepository.save(accountProfile);
			AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
			return result;
		}).orElse(null);

	}

	/**
	 * @author Sarath T
	 * @since Feb 10, 2017
	 *
	 *        Get all the accountProfiles by actived or deactivated.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findAllByCompanyAndActivated(boolean active) {
		log.debug("Request to get all accountProfiles");
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndActivateOrDeactivateAccountProfileOrderByName(active);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findAllCustomByCompanyAndActivated(boolean active) {
		log.debug("Request to get all custom accountProfiles");
		List<CustomAccountProfiles> accountProfileList = accountProfileRepository
				.findAllCustomAccountProfileByCompanyAndActivateOrDeactivateAccountProfileOrderByName(active);
		List<AccountProfileDTO> result = accountProfileList.stream().map(cAp -> new AccountProfileDTO(cAp))
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one accountProfile by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountProfileDTO> findByCompanyIdAndName(Long companyId, String name) {
		log.debug("Request to get AccountProfile by name : {}", name);
		return accountProfileRepository.findByCompanyIdAndNameIgnoreCase(companyId, name).map(accountProfile -> {
			AccountProfileDTO accountProfileDTO = accountProfileMapper
					.accountProfileToAccountProfileDTO(accountProfile);
			return accountProfileDTO;
		});
	}

	/**
	 * Save a accountProfile.
	 *
	 * @param accountProfileDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public AccountProfileDTO saveAccountProfile(Long companyId, AccountProfileDTO accountProfileDTO, String userLogin) {
		log.debug("Request to save AccountProfile : {}", accountProfileDTO);
		accountProfileDTO.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid()); // set
																								// pid
		AccountProfile accountProfile = accountProfileMapper.accountProfileDTOToAccountProfile(accountProfileDTO);
		// set company
		accountProfile.setCompany(companyRepository.findOne(companyId));

		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (opUser.isPresent()) {
			accountProfile.setUser(opUser.get());
		} else {
			accountProfile.setUser(userRepository.findOneByLogin("siteadmin").get());
		}

		accountProfile.setAccountStatus(accountProfileDTO.getAccountStatus());
		accountProfile = accountProfileRepository.save(accountProfile);
		AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findByPidNotIn(List<String> accountPids, String AccountTypePid) {
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByPidNotInAndAccountTypePid(accountPids,
				AccountTypePid);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfiles);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByAccountTypePidInAndActivated(List<String> accountTypePids) {
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndAccountTypesInAndActivated(accountTypePids, true);
		List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
				.accountProfilesToAccountProfileDTOs(accountProfiles);
		return accountProfileDTOs;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByAccountTypePidInAndActivatedAndImportStatus(
			List<String> accountTypePids, boolean importStatus) {
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndAccountTypesInAndActivatedAndAccountImportStatus(accountTypePids, true,
						importStatus);
		List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
				.accountProfilesToAccountProfileDTOs(accountProfiles);
		return accountProfileDTOs;
	}

	@Override
	public List<AccountProfileDTO> findAllByCompanyAndAccountImportStatusAndActivated(boolean importStatus) {
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndAccountImportStatusAndActivated(importStatus, true);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	/**
	 * Get one accountProfile by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountProfile> findOneByCompanyIdAndName(Long companyId, String name) {
		log.debug("Request to get AccountProfile by name : {}", name);
		return accountProfileRepository.findByCompanyIdAndNameIgnoreCase(companyId, name);
	}

	@Override
	public AccountProfileDTO updateImportedStatus(String pid, boolean active) {
		return accountProfileRepository.findOneByPid(pid).map(accountProfile -> {
			accountProfile.setImportStatus(active);
			accountProfile.setAccountStatus(AccountStatus.Verified);
			accountProfile = accountProfileRepository.save(accountProfile);
			AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
			return result;
		}).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findByCompanyIdAndUserIdInAndImportStatusAndCreatedDateBetweenOrderByCreatedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate, boolean active) {
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findByCompanyIdAndUserIdInAndImportStatusAndCreatedDateBetweenOrderByCreatedDateDesc(companyId,
						userIds, fromDate, toDate, active);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findByCompanyIdAndUserIdInAndDatasourceTypeMobileAndLastModifiedDateBetweenOrderByCreatedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate) {
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findByCompanyIdAndUserIdInAndLastModifiedDateBetweenAndDataSourceTypeMobileOrderByLastModifiedDateDesc(
						companyId, userIds, fromDate, toDate, DataSourceType.MOBILE);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAllByCompanyAndDatasourceType(DataSourceType dataSourceType) {
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndDataSourceType(dataSourceType);
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
	}

	@Override
	public AccountProfileDTO updateAccountProfileVerifiedStatus(String pid, AccountStatus verified) {
		log.debug("Request to Update AccountProfile verified or unverified: {}", pid);
		return accountProfileRepository.findOneByPid(pid).map(accountProfile -> {
			accountProfile.setAccountStatus(verified);
			accountProfile = accountProfileRepository.save(accountProfile);
			AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
			return result;
		}).orElse(null);
	}

	@Override
	public List<LeadManagementDTO> findAllByCompanyAndAccountTypeAccountNameType(AccountNameType accountNameType) {
		log.debug("Request to get all accountProfiles by account type accountNameType");
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndAccountTypeAccountNameType(accountNameType);
		if (accountProfileList == null) {
			return null;
		}
		List<LeadManagementDTO> result = new ArrayList<>();
		accountProfileList.forEach(accountProfile -> {
			LeadManagementDTO leadManagementDTO = new LeadManagementDTO(accountProfile);
			result.add(leadManagementDTO);
		});
		return result;
	}

	@Override
	public LeadManagementDTO saveLeadManagement(LeadManagementDTO leadManagementDTO) {
		log.debug("Request to save AccountProfile Lead Management : {}", leadManagementDTO);
		leadManagementDTO.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid()); // set
																								// pid
		AccountProfile accountProfile = new AccountProfile();
		accountProfile.setPid(leadManagementDTO.getPid());
		accountProfile.setName(leadManagementDTO.getName());
		accountProfile.setAlias(leadManagementDTO.getAlias());
		accountProfile.setContactPerson(leadManagementDTO.getContactPerson());
		accountProfile.setPhone1(leadManagementDTO.getPhone());
		accountProfile.setAddress(leadManagementDTO.getAddress());
		accountProfile.setLocation(leadManagementDTO.getLocationName());
		accountProfile.setCity("No City");
		accountProfile.setAccountStatus(AccountStatus.Verified);
		// set company
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		accountProfile.setCompany(company);
		AccountType accountType = accountTypeRepository
				.findByCompanyIdAndAccountNameType(AccountNameType.LEAD_MANAGEMENT);
		if (accountType != null) {
			accountProfile.setAccountType(accountType);
		} else {
			AccountType newAccountType = new AccountType();
			newAccountType.setName("Contact");
			newAccountType.setActivated(true);
			newAccountType.setPid(AccountTypeService.PID_PREFIX + RandomUtil.generatePid());
			newAccountType.setCompany(company);
			newAccountType = accountTypeRepository.save(newAccountType);
			accountProfile.setAccountType(newAccountType);
		}
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (opUser.isPresent()) {
			accountProfile.setUser(opUser.get());
		} else {
			accountProfile.setUser(userRepository.findOneByLogin("siteadmin").get());
		}
		AccountProfile newAccountProfile = accountProfileRepository.save(accountProfile);
		locationRepository.findOneByPid(leadManagementDTO.getLocationPid()).ifPresent(loc -> {
			LocationAccountProfile locationAccount = new LocationAccountProfile(loc, newAccountProfile, company);
			locationAccountProfileRepository.save(locationAccount);
		});
		leadManagementDTO.setAccountTypePid(accountProfile.getAccountType().getPid());
		leadManagementDTO.setAccountTypeName(accountProfile.getAccountType().getName());
		return leadManagementDTO;
	}

	@Override
	public Optional<LeadManagementDTO> findLeadManagementByPid(String pid) {
		log.debug("Request to get AccountProfile by pid : {}", pid);
		return accountProfileRepository.findOneByPid(pid).map(accountProfile -> {
			LocationAccountProfile locationAccountProfile = locationAccountProfileRepository
					.findByAccountProfile(accountProfile);
			Optional<Location> location = locationRepository
					.findOneByPid(locationAccountProfile.getLocation().getPid());
			LeadManagementDTO leadManagementDTO = new LeadManagementDTO(accountProfile);
			leadManagementDTO.setLocationPid(location.get().getPid());
			return leadManagementDTO;
		});
	}

	@Override
	public LeadManagementDTO updateLeadManagement(LeadManagementDTO leadManagementDTO) {
		log.debug("Request to Update AccountProfile : {}", leadManagementDTO);

		return accountProfileRepository.findOneByPid(leadManagementDTO.getPid()).map(accountProfile -> {
			accountProfile.setName(leadManagementDTO.getName());
			accountProfile.setAlias(leadManagementDTO.getAlias());
			accountProfile.setAddress(leadManagementDTO.getAddress());
			accountProfile.setPhone1(leadManagementDTO.getPhone());
			accountProfile.setContactPerson(leadManagementDTO.getContactPerson());
			AccountProfile newAccountProfile = accountProfileRepository.save(accountProfile);
			locationRepository.findOneByPid(leadManagementDTO.getLocationPid()).ifPresent(location -> {
				LocationAccountProfile locationAccountProfile = locationAccountProfileRepository
						.findByAccountProfile(newAccountProfile);
				locationAccountProfile.setLocation(location);
				locationAccountProfileRepository.save(locationAccountProfile);
			});
			LeadManagementDTO result = new LeadManagementDTO(newAccountProfile);
			return result;
		}).orElse(null);
	}

	@Override
	public List<AccountProfileDTO> findByStageTypeName(StageNameType stageTypeName) {
		List<String> accountProfilePids = stageHeaderRepo.findByStageStageNameType(stageTypeName);
		List<AccountProfile> accountProfileList = accountProfileRepository.findByPidIn(accountProfilePids);

		return accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
	}

	@Override
	public List<AccountProfile> findAllAccountProfileByCompanyId(Long companyId) {
		List<AccountProfile> accountProfiles = new ArrayList<>();
		for (Object[] object : accountProfileRepository.findAllAccountProfileByCompanyId(companyId)) {
			AccountProfile accountProfile = new AccountProfile();
			accountProfile.setId((Long) object[0]);
			accountProfile.setPid(object[1].toString());
			accountProfile.setName(object[2].toString());
			if (object[3] != null) {
				accountProfile.setAlias(object[3].toString());

			}
			accountProfiles.add(accountProfile);
		}
		return accountProfiles;
	}

}
