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

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.service.TaskService;
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
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DataSourceType;
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

import javax.inject.Inject;

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

	private final ActivityRepository activityRepository;

	private final LocationAccountProfileRepository locationAccountProfileRepository;

	private final AccountProfileMapper accountProfileMapper;

	private final CompanyRepository companyRepository;

	private final CompanyConfigurationRepository companyConfigurationRepository;

	private final UserRepository userRepository;

	private final FileManagerService fileManagerService;

	private final NewlyEditedAccountProfileService newlyEditedAccountProfileService;

	private final NewlyEditedAccountProfileRepository newlyEditedAccountProfileRepository;


	private final TaskRepository taskRepository;

	public AccountProfileController(AccountProfileRepository accountProfileRepository,
			LocationRepository locationRepository, LocationAccountProfileRepository locationAccountProfileRepository,
			AccountProfileMapper accountProfileMapper, CompanyRepository companyRepository,
			UserRepository userRepository, FileManagerService fileManagerService,
			CompanyConfigurationRepository companyConfigurationRepository,
			NewlyEditedAccountProfileService newlyEditedAccountProfileService,
			NewlyEditedAccountProfileRepository newlyEditedAccountProfileRepository,
									ActivityRepository activityRepository, TaskRepository taskRepository) {
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
		this.activityRepository = activityRepository;
		this.taskRepository = taskRepository;


	}

	/**
	 * POST /account-profile/:pid : Create a new Account Profile.
	 * 
	 * @param accountProfileDTO the accountProfileDTO to create
	 * @param pid               territory pid
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         accountProfileDTO
	 */
	@RequestMapping(value = "/account-profile/{pid}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> createExecutiveTaskPlan(
			@RequestBody AccountProfileDTO accountProfileDTO,
			@PathVariable String pid) {

		Optional<AccountProfile> exisitingAccountProfile =
				accountProfileRepository.
						findByCompanyIdAndNameIgnoreCase(
								SecurityUtils.getCurrentUsersCompanyId(),
								accountProfileDTO.getName());

		if (exisitingAccountProfile.isPresent()
				&& (accountProfileDTO.getPid() == null
				|| accountProfileDTO.getPid().isEmpty())) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert(
							"accountProfile",
							"nameexists",
							"Account Profile already in use"))
					.body(null);
		} else if ((exisitingAccountProfile.isPresent()
				&& !accountProfileDTO.getPid().isEmpty())) {
			if (!exisitingAccountProfile.get().getPid()
					.equals(accountProfileDTO.getPid())) {

				return ResponseEntity.badRequest().headers(
						HeaderUtil.createFailureAlert(
								"accountProfile",
								"nameexists",
								"Account Profile already in use"))
						.body(null);
			}
		}

		if (!locationRepository.findOneByPid(pid).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert(
							"location",
							"location not found",
							"location not found"))
					.body(null);
		}

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

		AccountProfile newAccountProfile =
				accountProfileMapper
						.accountProfileDTOToAccountProfile(
								accountProfileDTO);

		if (accountProfileDTO.getPid() == null
				|| accountProfileDTO.getPid().isEmpty()) {

			newAccountProfile.setAccountStatus(AccountStatus.Unverified);
			newAccountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());

			Optional<CompanyConfiguration> optNewCustomerAlias = companyConfigurationRepository
					.findByCompanyPidAndName(
							companyRepository.findOne(
									SecurityUtils.getCurrentUsersCompanyId()).getPid(),
							CompanyConfig.NEW_CUSTOMER_ALIAS);

			List<AccountProfile> listAccountProfiles =
					accountProfileRepository
							.findAllByCompanyAndDataSourceTypeAndCreatedDate(
									DataSourceType.MOBILE);

			String customerId = "N" + company.getId();

			if (listAccountProfiles.size() > 0) {
				long count = 0;
				long id111 = listAccountProfiles.get(0).getId();
				count++;
				long maxid =  accountProfileRepository.findMaximumId();
				logger.debug("Maximum Id : " + maxid);
				long cId = maxid + count;
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
		}

		newAccountProfile.setDataSourceType(DataSourceType.MOBILE);
		newAccountProfile.setCompany(company);
		newAccountProfile.setUser(user);

		log.debug("Saving new account profile from" +
						" mobile user {}" +
						" with account name {}" +
						" and accountPid {}",
				user.getLogin(),
				newAccountProfile.getName(),
				newAccountProfile.getPid());

		AccountProfile accountProfile = accountProfileRepository.save(newAccountProfile);

		//create account profile as task
		long accountTypeId = accountProfile.getAccountType().getId();
		List<Activity> activities =
				activityRepository
						.findALlByAccountTypeId(accountTypeId);
		for (Activity activity : activities) {
			if(activity.getAutoTaskCreation()){
				//convert to task
				Task task = new Task();
				task.setCompany(company);
				task.setAccountProfile(accountProfile);
				task.setActivity(activity);
				task.setAccountType(accountProfile.getAccountType());
				task.setPid(TaskService.PID_PREFIX + RandomUtil.generatePid());
				task.setRemarks("");
				taskRepository.save(task);
				logger.info("task saved...");
			}
		}

		// Associate account profile to territory
		if (accountProfileDTO.getPid() == null
				|| accountProfileDTO.getPid().isEmpty()) {
			locationRepository.findOneByPid(pid)
					.ifPresent(loc -> {
						LocationAccountProfile locationAccount =
								new LocationAccountProfile(loc, accountProfile, company);
				locationAccountProfileRepository.save(locationAccount);
			});
		}

		AccountProfileDTO result =
				accountProfileMapper
						.accountProfileToAccountProfileDTO(
						accountProfile);

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
		String description = "get one by pid";
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
		String description = "get one by pid";
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

	@Timed
	@RequestMapping(value = "/get-account-profiles", method = RequestMethod.GET)
	public ResponseEntity<AccountProfileDTO> getAccountProfile(
			@RequestParam("accountProfilePid") String accountProfilePid) {
		Optional<AccountProfile> optionalAccountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
		if (optionalAccountProfile.isPresent()) {
			log.info("account present");
			AccountProfile accountProfile = optionalAccountProfile.get();
			System.out.println(accountProfile);
			AccountProfileDTO result = accountProfileMapper.accountProfileToAccountProfileDTO(accountProfile);
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		return ResponseEntity.badRequest()
				.headers(HeaderUtil.createFailureAlert("accountProfile", "noAccountFound", "Account Profile not found"))
				.body(null);

	}

}
