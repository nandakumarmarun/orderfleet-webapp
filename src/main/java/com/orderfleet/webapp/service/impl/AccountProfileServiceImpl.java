package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.orderfleet.webapp.repository.CounrtyCRepository;
import com.orderfleet.webapp.repository.DistrictCRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.repository.StateCRepository;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private CounrtyCRepository countrycrepository;

	@Inject
	private StateCRepository statecrepository;

	@Inject
	private DistrictCRepository districtcrepository;

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
		log.info("AccountProfile Type :" + accountProfileDTO.getAccountTypeName());
		accountProfileDTO.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid()); // set
																								// pid
		AccountProfile accountProfile = accountProfileMapper.accountProfileDTOToAccountProfile(accountProfileDTO);
		accountProfile.setCountryc(countrycrepository.findOne(accountProfileDTO.getCountryId()));
		accountProfile.setStatec(statecrepository.findOne(Long.valueOf(accountProfileDTO.getStateId())));
		accountProfile.setDistrictc(districtcrepository.findOne(Long.valueOf(accountProfileDTO.getDistrictId())));

		log.info("AccountProfile Type 111 :" + accountProfile.getAccountType().getName());
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
		log.debug("counrty id", accountProfileDTO.getCountryId());
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountProfileDTO accprofileDTO = accountProfileRepository.findOneByPid(accountProfileDTO.getPid())
				.map(accountProfile -> {
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
					logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag
							+ "," + description);

					accountProfile.setName(accountProfileDTO.getName());
					accountProfile.setAlias(accountProfileDTO.getAlias());
					DateTimeFormatter DATE_TIME_FORMATS = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMATS = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String ids = "AT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String descriptions = "get one by pid";
					LocalDateTime startLCTimes = LocalDateTime.now();
					String startTimes = startLCTime.format(DATE_TIME_FORMATS);
					String startDates = startLCTime.format(DATE_FORMATS);
					logger.info(ids + "," + startDates + "," + startTimes + ",_ ,0 ,START,_," + descriptions);
					accountProfile.setAccountType(
							accountTypeRepository.findOneByPid(accountProfileDTO.getAccountTypePid()).get());
					String flags = "Normal";
					LocalDateTime endLCTimes = LocalDateTime.now();
					String endTimes = endLCTimes.format(DATE_TIME_FORMATS);
					String endDates = startLCTimes.format(DATE_FORMATS);
					Duration durations = Duration.between(startLCTimes, endLCTimes);
					long minute = durations.toMinutes();
					if (minute <= 1 && minute >= 0) {
						flags = "Fast";
					}
					if (minute > 1 && minute <= 2) {
						flags = "Normal";
					}
					if (minute > 2 && minute <= 10) {
						flags = "Slow";
					}
					if (minute > 10) {
						flags = "Dead Slow";
					}
					logger.info(ids + "," + endDates + "," + startTimes + "," + endTimes + "," + minute + ",END,"
							+ flags + "," + descriptions);

					accountProfile.setCountryc(countrycrepository.findOne(accountProfileDTO.getCountryId()));
					accountProfile.setStatec(statecrepository.findOne(Long.valueOf(accountProfileDTO.getStateId())));
					accountProfile
							.setDistrictc(districtcrepository.findOne(Long.valueOf(accountProfileDTO.getDistrictId())));

					accountProfile.setAddress(accountProfileDTO.getAddress());
					accountProfile.setCity(accountProfileDTO.getCity());
					accountProfile.setDescription(accountProfileDTO.getDescription());
					accountProfile.setEmail1(accountProfileDTO.getEmail1());
					accountProfile.setEmail2(accountProfileDTO.getEmail2());
					accountProfile.setWhatsAppNo(accountProfileDTO.getWhatsAppNo());
					accountProfile.setLocation(accountProfileDTO.getLocation());
					if (accountProfileDTO.getLatitude() != null && accountProfileDTO.getLongitude() != null) {
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
					accountProfile.setCustomerId(accountProfileDTO.getCustomerId());
					accountProfile.setLocationRadius(accountProfileDTO.getLocationRadius());
					/*
					 * accountProfile.setCountryc(accountProfileDTO.getCountryc());
					 * accountProfile.setStatec(accountProfileDTO.getStatec());
					 * accountProfile.setDistrictc(accountProfileDTO.getDistrictc());
					 */

					if (accountProfileDTO.getGeoTaggingType() != null) {
						accountProfile.setGeoTaggingType(accountProfileDTO.getGeoTaggingType());
						accountProfile.setGeoTaggedTime(accountProfileDTO.getGeoTaggedTime());
						Optional<User> opGeoTagUser = userRepository
								.findOneByLogin(accountProfileDTO.getGeoTaggedUserLogin() == null ? ""
										: accountProfileDTO.getGeoTaggedUserLogin());
						if (opGeoTagUser.isPresent()) {
							accountProfile.setGeoTaggedUser(opGeoTagUser.get());
						}
					}

					accountProfile.setDataSourceType(accountProfileDTO.getDataSourceType());

					// accountProfile.setActivated(accountProfileDTO.getActivated());
					accountProfile.setDefaultPriceLevel(
							priceLevelRepository.findOneByPid(accountProfileDTO.getDefaultPriceLevelPid())
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
		return accprofileDTO;
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository.findAllByCompanyId();
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_108" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by accountType Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository.findAllByAccountTypePid(pid);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compId using page";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Page<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyId(pageable);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfileDTO> apDTO = accountProfileRepository.findOneByPid(pid).map(accountProfile -> {
			AccountProfileDTO accountProfileDTO = accountProfileMapper
					.accountProfileToAccountProfileDTO(accountProfile);
			return accountProfileDTO;
		});
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
		return apDTO;

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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and name Ignore case";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfileDTO> accprofileDTO = accountProfileRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(accountProfile -> {
					AccountProfileDTO accountProfileDTO = accountProfileMapper
							.accountProfileToAccountProfileDTO(accountProfile);
					return accountProfileDTO;
				});
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

		return accprofileDTO;
	}

	/**
	 * Delete the accountProfile by id.
	 *
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete AccountProfile : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		accountProfileRepository.findOneByPid(pid).ifPresent(accountProfile -> {
			accountProfileRepository.delete(accountProfile.getId());
		});
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_113" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and alias ignorecase";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfileDTO> apdto = accountProfileRepository
				.findByCompanyIdAndAliasIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), alias)
				.map(accountProfile -> {
					AccountProfileDTO accountProfileDTO = accountProfileMapper
							.accountProfileToAccountProfileDTO(accountProfile);
					return accountProfileDTO;
				});
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
		return apdto;
	}

	@Override
	public List<AccountProfileDTO> findAllByAccountTypeName(String accountTypeName) {
		log.debug("Request to get all accountProfiles by account type name");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_114" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compId and accTypeName";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyIdAndAccountTypeName(SecurityUtils.getCurrentUsersCompanyId(), accountTypeName);
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

		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAllByCompanyAndAccountImportStatus(boolean importStatus) {
		log.debug("Request to get all accountProfiles by import status");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_116" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by company and AccImport Status";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndAccountImportStatus(importStatus);
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
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public AccountProfileDTO updateAccountProfileImportStatus(AccountProfileDTO accountProfileDTO) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfileDTO.getPid()).get();
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountProfileDTO apdto = accountProfileRepository.findOneByPid(pid).map(accountProfile -> {
			accountProfile.setActivated(active);
			accountProfile = accountProfileRepository.save(accountProfile);
			AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
			return result;
		}).orElse(null);
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

		return apdto;

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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_117" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by comp and activated and deactivated accProfile";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndActivateOrDeactivateAccountProfileOrderByName(active);
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

		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findAllByCompanyAndActivatedLimitToCount(boolean active) {
		log.debug("Request to get all accountProfiles");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_117" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by comp and activated and deactivated accProfile";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndActivateOrDeactivateAccountProfileOrderByNameLimitByCount(active);
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

		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findAllCustomByCompanyAndActivated(boolean active) {
		log.debug("Request to get all custom accountProfiles");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_135" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all customAccountProfile by CompanyAndActivateOrDeactivateAccProfile";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<CustomAccountProfiles> accountProfileList = accountProfileRepository
				.findAllCustomAccountProfileByCompanyAndActivateOrDeactivateAccountProfileOrderByName(active);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and name Ignore case";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfileDTO> apDTO = accountProfileRepository.findByCompanyIdAndNameIgnoreCase(companyId, name)
				.map(accountProfile -> {
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
					logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag
							+ "," + description);
					AccountProfileDTO accountProfileDTO = accountProfileMapper
							.accountProfileToAccountProfileDTO(accountProfile);
					return accountProfileDTO;
				});
		return apDTO;
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
		log.info("AccProfiledto Type :" + accountProfileDTO.getAccountTypeName()); // pid
		AccountProfile accountProfile = accountProfileMapper.accountProfileDTOToAccountProfile(accountProfileDTO);
		// set company
		accountProfile.setCompany(companyRepository.findOne(companyId));
		log.info("AccountProfile Account Type :" + accountProfile.getAccountType().getName());
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_119" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by Pid in and accountTypePid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByPidNotInAndAccountTypePid(accountPids,
				AccountTypePid);
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

		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfiles);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByAccountTypePidInAndActivated(List<String> accountTypePids) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_120" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and accTypes In and activated";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndAccountTypesInAndActivated(accountTypePids, true);
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
		List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
				.accountProfilesToAccountProfileDTOs(accountProfiles);
		return accountProfileDTOs;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByAccountTypePidInAndActivatedLimitCount(
			List<String> accountTypePids) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_120" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and accTypes In and activated";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		List<Long> accountTypeIds = accountTypeRepository.findAccountTypeIdsByAccountTypePidIn(accountTypePids);

		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndAccountTypesInAndActivatedLimitCount(accountTypeIds, true);
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
		List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
				.accountProfilesToAccountProfileDTOs(accountProfiles);
		return accountProfileDTOs;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByAccountTypePidInAndActivatedAndImportStatus(
			List<String> accountTypePids, boolean importStatus) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_121" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and accTypes In and activated and accStatus";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndAccountTypesInAndActivatedAndAccountImportStatus(accountTypePids, true,
						importStatus);
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

		List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
				.accountProfilesToAccountProfileDTOs(accountProfiles);
		return accountProfileDTOs;
	}

	@Override
	public List<AccountProfileDTO> findAccountProfileByAccountTypePidInAndActivatedAndImportStatusLimitCount(
			List<String> accountTypePids, boolean importStatus) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_121" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and accTypes In and activated and accStatus";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Long> accountTypeIds = accountTypeRepository.findAccountTypeIdsByAccountTypePidIn(accountTypePids);
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndAccountTypesInAndActivatedAndAccountImportStatusLimitCount(accountTypeIds, true,
						importStatus);
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

		List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
				.accountProfilesToAccountProfileDTOs(accountProfiles);
		return accountProfileDTOs;
	}

	@Override
	public List<AccountProfileDTO> findAllByCompanyAndAccountImportStatusAndActivated(boolean importStatus) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_122" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by comp and activated and accStatus";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndAccountImportStatusAndActivated(importStatus, true);
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

		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAllByCompanyAndAccountImportStatusAndActivatedLimitCount(boolean importStatus) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_122" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by comp and activated and accStatus";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndAccountImportStatusAndActivatedLimitCount(importStatus, true);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountProfileDTO apDTO = accountProfileRepository.findOneByPid(pid).map(accountProfile -> {
			accountProfile.setImportStatus(active);
			accountProfile.setAccountStatus(AccountStatus.Verified);
			accountProfile = accountProfileRepository.save(accountProfile);
			AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
			return result;
		}).orElse(null);
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

		return apDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findByCompanyIdAndUserIdInAndImportStatusAndCreatedDateBetweenOrderByCreatedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate, boolean active) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_126" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by cmopId and userIdIn and create date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findByCompanyIdAndUserIdInAndImportStatusAndCreatedDateBetweenOrderByCreatedDateDesc(companyId,
						userIds, fromDate, toDate, active);
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
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findByCompanyIdAndUserIdInAndDatasourceTypeMobileAndLastModifiedDateBetweenOrderByCreatedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_127" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId UserIdIn and LastModifiedDateBetween";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findByCompanyIdAndUserIdInAndLastModifiedDateBetweenAndDataSourceTypeMobileOrderByLastModifiedDateDesc(
						companyId, userIds, fromDate, toDate, DataSourceType.MOBILE);
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

		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAllByCompanyAndDatasourceType(DataSourceType dataSourceType) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_128" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by comp and data source type";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndDataSourceType(dataSourceType);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_132" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by comp and acc Name Type";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndAccountTypeAccountNameType(accountNameType);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AT_QUERY_113" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId and accNameType";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountType accountType = accountTypeRepository
				.findByCompanyIdAndAccountNameType(AccountNameType.LEAD_MANAGEMENT);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<LeadManagementDTO> lmDTO = accountProfileRepository.findOneByPid(pid).map(accountProfile -> {
			LocationAccountProfile locationAccountProfile = locationAccountProfileRepository
					.findByAccountProfile(accountProfile);
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

			Optional<Location> location = locationRepository
					.findOneByPid(locationAccountProfile.getLocation().getPid());
			LeadManagementDTO leadManagementDTO = new LeadManagementDTO(accountProfile);
			leadManagementDTO.setLocationPid(location.get().getPid());
			return leadManagementDTO;
		});
		return lmDTO;
	}

	@Override
	public LeadManagementDTO updateLeadManagement(LeadManagementDTO leadManagementDTO) {
		log.debug("Request to Update AccountProfile : {}", leadManagementDTO);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		LeadManagementDTO lmDTO = accountProfileRepository.findOneByPid(leadManagementDTO.getPid())
				.map(accountProfile -> {
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

		return lmDTO;
	}

	@Override
	public List<AccountProfileDTO> findByStageTypeName(StageNameType stageTypeName) {
		List<String> accountProfilePids = stageHeaderRepo.findByStageStageNameType(stageTypeName);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_118" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by PidIn";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository.findByPidIn(accountProfilePids);
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
		return accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
	}

	@Override
	public List<AccountProfile> findAllAccountProfileByCompanyId(Long companyId) {
		List<AccountProfile> accountProfiles = new ArrayList<>();

		System.out.println(companyId + "------------------------------------");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AT_QUERY_108" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountType> accountTypes = accountTypeRepository.findAllByCompanyId(companyId);
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

		System.out.println(accountTypes.size() + "----------------------------");
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AP_QUERY_134" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "get all accProfiles by compId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		for (Object[] object : accountProfileRepository.findAllAccountProfileByCompanyId(companyId)) {
			AccountProfile accountProfile = new AccountProfile();
			accountProfile.setId((Long) object[0]);
			accountProfile.setPid(object[1].toString());
			accountProfile.setName(object[2].toString());
			if (object[3] != null) {
				accountProfile.setAlias(object[3].toString());

			} else {
				accountProfile.setAlias(object[2].toString());
			}
			if (object[4] != null) {
				accountProfile.setCustomerId(object[4].toString());

			}
			if (object[5] != null) {
				Optional<AccountType> opAccType = accountTypes.stream()
						.filter(ac -> ac.getPid().equals(object[5].toString())).findAny();
				if (opAccType.isPresent()) {
					accountProfile.setAccountType(opAccType.get());
				}
			}
			accountProfiles.add(accountProfile);
		}
		String flag1 = "Normal";
		LocalDateTime endLCTime1 = LocalDateTime.now();
		String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
		String endDate1 = startLCTime1.format(DATE_FORMAT1);
		Duration duration1 = Duration.between(startLCTime1, endLCTime1);
		long minutes1 = duration1.toMinutes();
		if (minutes1 <= 1 && minutes1 >= 0) {
			flag1 = "Fast";
		}
		if (minutes1 > 1 && minutes1 <= 2) {
			flag1 = "Normal";
		}
		if (minutes1 > 2 && minutes1 <= 10) {
			flag1 = "Slow";
		}
		if (minutes1 > 10) {
			flag1 = "Dead Slow";
		}
		logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
				+ description1);
		return accountProfiles;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findByCompanyIdAndUserIdInAndLastModifedDateBetweenOrderByLastModifedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_140" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by compId UserIdIn andLastModifedDateBetween";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findByCompanyIdAndUserIdInAndLastModifedDateBetweenOrderByLastModifedDateDesc(companyId, userIds,
						fromDate, toDate);
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
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

	@Override
	public List<AccountProfileDTO> findAllByCompanyAndActivatedLimitToCountAndSearchValue(boolean active,
			String searchValue) {
		log.debug("Request to get all accountProfiles");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_117" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by comp and activated and deactivated accProfile";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndActivateOrDeactivateAccountProfileOrderByNameLimitByCountAndSearchValue(active,
						searchValue);
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

		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
		return result;
	}

}
