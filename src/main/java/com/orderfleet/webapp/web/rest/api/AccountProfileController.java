package com.orderfleet.webapp.web.rest.api;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.NewlyEditedAccountProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.NewlyEditedAccountProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.NewlyEditedAccountProfileService;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.CompanyPerformanceConfigResource;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * REST controller for managing AccountProfile (Company's customer).
 * 
 * @author Muhammed Riyas T
 * @since August 08, 2016
 */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountProfileController {

	private final Logger log = LoggerFactory.getLogger(AccountProfileController.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private final AccountProfileRepository accountProfileRepository;

	private final LocationRepository locationRepository;

	private final LocationAccountProfileRepository locationAccountProfileRepository;

	private final AccountProfileMapper accountProfileMapper;

	private final CompanyRepository companyRepository;

	private final CompanyConfigurationRepository companyConfigurationRepository;

	private final UserRepository userRepository;

	private final FileManagerService fileManagerService;

	private final NewlyEditedAccountProfileService newlyEditedAccountProfileService;

	private final NewlyEditedAccountProfileRepository newlyEditedAccountProfileRepository;

	public AccountProfileController(AccountProfileRepository accountProfileRepository,
			LocationRepository locationRepository, LocationAccountProfileRepository locationAccountProfileRepository,
			AccountProfileMapper accountProfileMapper, CompanyRepository companyRepository,
			UserRepository userRepository, FileManagerService fileManagerService,
			CompanyConfigurationRepository companyConfigurationRepository,
			NewlyEditedAccountProfileService newlyEditedAccountProfileService,
			NewlyEditedAccountProfileRepository newlyEditedAccountProfileRepository) {
		super();
		this.accountProfileRepository = accountProfileRepository;
		this.locationRepository = locationRepository;
		this.locationAccountProfileRepository = locationAccountProfileRepository;
		this.accountProfileMapper = accountProfileMapper;
		this.companyRepository = companyRepository;
		this.userRepository = userRepository;
		this.fileManagerService = fileManagerService;
		this.companyConfigurationRepository = companyConfigurationRepository;
		this.newlyEditedAccountProfileService = newlyEditedAccountProfileService;
		this.newlyEditedAccountProfileRepository = newlyEditedAccountProfileRepository;

	}

	/**
	 * POST /account-profile/:pid : Create a new Account Profile.
	 * 
	 * @param accountProfileDTO the accountProfileDTO to create
	 * @param pid               territory pid
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         accountProfileDTO
	 */
	@RequestMapping(value = "/account-profile/{pid}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> createExecutiveTaskPlan(@RequestBody AccountProfileDTO accountProfileDTO,
			@PathVariable String pid) {
		log.debug("Rest request to save AccountProfile : {} under location with pid {}", accountProfileDTO, pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get by compId and name Ignore case";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfile> exisitingAccountProfile = accountProfileRepository.findByCompanyIdAndNameIgnoreCase(
				SecurityUtils.getCurrentUsersCompanyId(), accountProfileDTO.getName());
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

		if (exisitingAccountProfile.isPresent()
				&& (accountProfileDTO.getPid() == null || accountProfileDTO.getPid().isEmpty())) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "nameexists", "Account Profile already in use"))
					.body(null);
		} else if ((exisitingAccountProfile.isPresent() && !accountProfileDTO.getPid().isEmpty())) {
			if (!exisitingAccountProfile.get().getPid().equals(accountProfileDTO.getPid())) {
				return ResponseEntity.badRequest().headers(
						HeaderUtil.createFailureAlert("accountProfile", "nameexists", "Account Profile already in use"))
						.body(null);
			}
		}
		if (!locationRepository.findOneByPid(pid).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("location", "location not found", "location not found"))
					.body(null);
		}
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

		AccountProfile newAccountProfile = accountProfileMapper.accountProfileDTOToAccountProfile(accountProfileDTO);
		if (accountProfileDTO.getPid() == null || accountProfileDTO.getPid().isEmpty()) {
			newAccountProfile.setAccountStatus(AccountStatus.Unverified);
			newAccountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get one by pid";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			Optional<CompanyConfiguration> optNewCustomerAlias = companyConfigurationRepository.findByCompanyPidAndName(
					companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()).getPid(),
					CompanyConfig.NEW_CUSTOMER_ALIAS);
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

			 DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id11 = "AP_QUERY_129" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description11 ="get all by comp data source type and create date";
				LocalDateTime startLCTime11 = LocalDateTime.now();
				String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
				String startDate11 = startLCTime11.format(DATE_FORMAT11);
				logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
			List<AccountProfile> listAccountProfiles = accountProfileRepository
					.findAllByCompanyAndDataSourceTypeAndCreatedDate(DataSourceType.MOBILE);
			String flag11 = "Normal";
			LocalDateTime endLCTime11 = LocalDateTime.now();
			String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
			String endDate11 = startLCTime11.format(DATE_FORMAT11);
			Duration duration11 = Duration.between(startLCTime11, endLCTime11);
			long minutes11 = duration11.toMinutes();
			if (minutes11 <= 1 && minutes11 >= 0) {
				flag11 = "Fast";
			}
			if (minutes11 > 1 && minutes11 <= 2) {
				flag11 = "Normal";
			}
			if (minutes11 > 2 && minutes11 <= 10) {
				flag11 = "Slow";
			}
			if (minutes11 > 10) {
				flag11 = "Dead Slow";
			}
	                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
					+ description11);

			String customerId = "N" + company.getId();

			if (listAccountProfiles.size() > 0) {
				long count = 0;
				long id111 = listAccountProfiles.get(0).getId();
				count++;
				long cId = id111 + count;

				customerId = String.valueOf(cId);
			}

			newAccountProfile.setCustomerId(customerId);

			if (optNewCustomerAlias.isPresent()) {
				if (optNewCustomerAlias.get().getValue().equalsIgnoreCase("true")) {

					if (listAccountProfiles.size() > 0) {
						String alias = listAccountProfiles.get(0).getAlias();
						if (alias.startsWith("N_")) {
							String[] stringArray = alias.split("_");

							int i = Integer.parseInt(stringArray[1]);
							
							i++;
							
							newAccountProfile.setAlias("N_" + i);
						} else {
							newAccountProfile.setAlias("N_1");
						}
					} else {
						newAccountProfile.setAlias("N_1");
					}
				} else {
					newAccountProfile.setAlias("N_1");

				}

			} else {
				newAccountProfile.setAlias("N_1");

			}

		} else {
			newAccountProfile.setId(exisitingAccountProfile.get().getId());
		}
		// set company
		newAccountProfile.setDataSourceType(DataSourceType.MOBILE);
		newAccountProfile.setCompany(company);
		newAccountProfile.setUser(user);

		log.debug("Saving new account profile from mobile user {} with account name {} and accountPid {}",
				user.getLogin(), newAccountProfile.getName(), newAccountProfile.getPid());
		AccountProfile accountProfile = accountProfileRepository.save(newAccountProfile);

		// test to remove
		// System.out.println("==============//// 1. Total AP :
		// "+locationAccountProfileRepository.findAllByCompanyId(company.getId()).size());

		// Associate account profile to territory
		if (accountProfileDTO.getPid() == null || accountProfileDTO.getPid().isEmpty()) {
			locationRepository.findOneByPid(pid).ifPresent(loc -> {
				LocationAccountProfile locationAccount = new LocationAccountProfile(loc, accountProfile, company);
				locationAccountProfileRepository.save(locationAccount);
				// test to remove
				// System.out.println("==============//// 2. Total AP :
				// "+locationAccountProfileRepository.findAllByCompanyId(company.getId()).size());
			});
			// test to remove
			// System.out.println("==============//// 3. Total AP :
			// "+locationAccountProfileRepository.findAllByCompanyId(company.getId()).size());
		}
		// test to remove
		// System.out.println("==============//// 4. Total AP :
		// "+locationAccountProfileRepository.findAllByCompanyId(company.getId()).size());
		AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/update-account-profile/{pid}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> updateAccountProfile(@RequestBody AccountProfileDTO accountProfileDTO,
			@PathVariable String pid) {
		log.debug("Rest request to Update AccountProfile : {} under location with pid {}", accountProfileDTO, pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfile> exisitingAccountProfile = accountProfileRepository.findOneByPid(pid);
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

		if (exisitingAccountProfile.isPresent()) {

			log.info("Account Profile Exists");

			AccountProfile accountProfile = accountProfileMapper.accountProfileDTOToAccountProfile(accountProfileDTO);
			accountProfile.setId(exisitingAccountProfile.get().getId());
			accountProfile.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
			accountProfile.setDataSourceType(DataSourceType.MOBILE);
			Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());

			if (opUser.isPresent()) {
				accountProfile.setUser(opUser.get());
			}

			NewlyEditedAccountProfile newlyEditedAccountProfile = newlyEditedAccountProfileService
					.accountProfileToNewlyEditedAccountProfile(accountProfileDTO, exisitingAccountProfile);

			newlyEditedAccountProfile = newlyEditedAccountProfileRepository.save(newlyEditedAccountProfile);

			// accountProfile = accountProfileRepository.save(accountProfile);

			AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
			return new ResponseEntity<>(result, HttpStatus.CREATED);

		} else {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "noAccountFound", "Account Profile not found"))
					.body(null);
		}

	}

	@Timed
	@Transactional
	@RequestMapping(value = "/account-profile/upload", method = RequestMethod.POST)
	public ResponseEntity<List<String>> uploadGeoLocationImage(
			@RequestParam("accountProfilePid") String accountProfilePid, @RequestParam("file") MultipartFile file) {
		log.debug("Rest request to upload accountProfile geo location image with pid {}", accountProfilePid);
		if (file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}
		List<String> filePid = new ArrayList<>();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfile> optionalAccountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
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
		if (optionalAccountProfile.isPresent()) {
			try {
				AccountProfile accountProfile = optionalAccountProfile.get();
				File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
						file.getOriginalFilename(), file.getContentType());
				// update account profile with file
				Set<File> files = new HashSet<>();
				files.add(uploadedFile);
				if (!accountProfile.getGeoLocationFiles().isEmpty()) {
					files.addAll(accountProfile.getGeoLocationFiles());
				}
				accountProfile.setGeoLocationFiles(files);
				accountProfileRepository.save(accountProfile);
				filePid.add(uploadedFile.getPid());
				return new ResponseEntity<>(filePid, HttpStatus.OK);
			} catch (FileManagerException | IOException ex) {
				log.debug("File upload exception : {}", ex);
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage())).body(null);
			}
		} else {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("fileUpload", "accountNotExists", "Account profile not found."))
					.body(null);
		}
	}

}
