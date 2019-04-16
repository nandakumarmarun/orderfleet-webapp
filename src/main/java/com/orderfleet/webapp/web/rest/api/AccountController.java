package com.orderfleet.webapp.web.rest.api;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.License;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.domain.UserOnPremise;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.LicenseRepository;
import com.orderfleet.webapp.repository.UserDeviceRepository;
import com.orderfleet.webapp.repository.UserOnPremiseRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.WhiteListedDevicesRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.async.MailService;
import com.orderfleet.webapp.web.rest.api.dto.CompanyDTO;
import com.orderfleet.webapp.web.rest.api.dto.CompanyUrlConfigDTO;
import com.orderfleet.webapp.web.rest.api.dto.ManagedUserDTO;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.api.mapper.CompanyMapper;
import com.orderfleet.webapp.web.rest.dto.KeyAndPasswordDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * REST controller for managing the current user's account.
 * 
 * @author Shaheer
 * @since May 17, 2016
 */
@RestController
@RequestMapping("/api")
public class AccountController {

	private final Logger log = LoggerFactory.getLogger(AccountController.class);

	private final UserRepository userRepository;

	private final LicenseRepository licenseRepository;

	private final UserService userService;

	private final MailService mailService;

	private final CompanyMapper companyMapper;

	private final UserDeviceService userDeviceService;

	private final UserDeviceRepository userDeviceRepository;

	private final WhiteListedDevicesRepository whiteListedDevicesRepository;
	
	@Inject
	private UserOnPremiseRepository userOnPremiseRepository;
	
	@Inject
	private CompanyRepository companyRepository;


	public AccountController(UserRepository userRepository, LicenseRepository licenseRepository,
			UserService userService, MailService mailService, CompanyMapper companyMapper,
			UserDeviceService userDeviceService, UserDeviceRepository userDeviceRepository,
			WhiteListedDevicesRepository whiteListedDevicesRepository) {
		super();
		this.userRepository = userRepository;
		this.licenseRepository = licenseRepository;
		this.userService = userService;
		this.mailService = mailService;
		this.companyMapper = companyMapper;
		this.userDeviceService = userDeviceService;
		this.userDeviceRepository = userDeviceRepository;
		this.whiteListedDevicesRepository = whiteListedDevicesRepository;
	}

	/**
	 * POST /register-device : Register device if user is not mapped with any
	 * device. else validate the user device.
	 * 
	 * As of now, One user have only one device
	 *
	 * @param deviceKey
	 *            the device key
	 * @return the ResponseEntity with status 200 (OK), or status 500 (Internal
	 *         Server Error) if the device key couldn't be found
	 */
	@PostMapping(path = "/register-device", produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<String> registerDeviceKey(@RequestParam(value = "deviceKey") String deviceKey,
			@RequestParam(required = false, value = "buildVersion") String buildVersion) {
		if (isWhiteListedDevice(deviceKey)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		String login = SecurityUtils.getCurrentUserLogin();
		log.info("Validating device using key {} and user {}", deviceKey, login);
		Optional<UserDevice> userDeviceByDevice = userDeviceRepository.findByDeviceKeyAndActivatedTrue(deviceKey);
		Optional<UserDevice> userDeviceByLogin = userDeviceRepository.findByUserLoginAndActivatedTrue(login);
		if (!userDeviceByDevice.isPresent() && !userDeviceByLogin.isPresent()) {
			userDeviceService.saveDiviceKeyAndBuildVersion(deviceKey, buildVersion);
		} else if (userDeviceByLogin.isPresent() && !(userDeviceByLogin.get().getDeviceKey().equals(deviceKey))) {
			userDeviceService.update(userDeviceByLogin.get().getPid(), deviceKey, login, LocalDateTime.now(),
					buildVersion);
			return new ResponseEntity<>("Unable to login, You have no access to this resource.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (userDeviceByDevice.isPresent() && !(userDeviceByDevice.get().getUser().getLogin().equals(login))) {
			userDeviceService.update(userDeviceByDevice.get().getPid(), deviceKey, login, LocalDateTime.now(),
					buildVersion);
			return new ResponseEntity<>("Unable to login, You have no access to this resource.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (userDeviceByDevice.isPresent() && userDeviceByLogin.isPresent()) {
			userDeviceService.update(userDeviceByDevice.get().getPid(), deviceKey, login, LocalDateTime.now(),
					buildVersion);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * POST /register-fcmkey : register the device fcm key.
	 * 
	 * @param fcmKey
	 *            the fcmkey of user device
	 * @return the ResponseEntity with status 200 (OK), or status 500 (Internal
	 *         Server Error) if the device key couldn't be found
	 */
	@PostMapping(path = "/register-fcmkey", produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<String> registerDeviceFcmKey(@RequestParam(value = "deviceKey") String deviceKey,
			@RequestParam(value = "fcmKey") String fcmKey) {
		Optional<UserDevice> userDeviceExist = userDeviceRepository
				.findOneByDeviceKeyAndUserLoginAndActivatedTrue(deviceKey, SecurityUtils.getCurrentUserLogin());
		if (userDeviceExist.isPresent()) {
			userDeviceService.updateFcmKey(fcmKey);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * POST /register : register the user.
	 *
	 * @param managedUserDTO
	 *            the managed user DTO
	 * @param request
	 *            the HTTP request
	 * @return the ResponseEntity with status 201 (Created) if the user is
	 *         registred or 400 (Bad Request) if the login wor e-mail is already
	 *         in use
	 */
	@PostMapping(path = "/register", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
	@Timed
	public ResponseEntity<?> registerAccount(@Valid @RequestBody ManagedUserDTO managedUserDTO,
			HttpServletRequest request) {
		log.debug("REST request to register new User : {}", managedUserDTO);
		final String reg = "registration";

		if (userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert(reg, "userexists", "Login already in use")).body(null);
		} else if (userRepository.findOneByEmail(managedUserDTO.getEmail()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert(reg, "emailexists", "Email already in use")).body(null);
		} else {
			// validate license key with company
			Optional<License> existingLicense = licenseRepository
					.findOneByLicenseKeyAndCompanyIdAndExpireDateAfterAndActivatedFalse(managedUserDTO.getLicenseKey(),
							managedUserDTO.getCompanyId(), ZonedDateTime.now());
			if (!existingLicense.isPresent()) {
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert(reg, "licensenotexists", "Invalid license")).body(null);
			}

			User user = userService.createUser(managedUserDTO.getLogin(), managedUserDTO.getPassword(),
					managedUserDTO.getFirstName(), managedUserDTO.getLastName(),
					managedUserDTO.getEmail().toLowerCase(), managedUserDTO.getMobile(), managedUserDTO.getLangKey(),
					managedUserDTO.getDeviceKey(), existingLicense.get());

			mailService.sendActivationEmail(user);
			return new ResponseEntity<>(HttpStatus.CREATED);
		}
	}
	
	@GetMapping("/onpremise-users/{companyPid}")
	@Timed
	public ResponseEntity<List<UserDTO>> getAllUsers(@PathVariable String companyPid) {
		List<Object[]> users = userRepository.findUsersByCompanyPid(companyPid);
		List<UserDTO> userDTOs = new ArrayList<>();
		users.forEach(user ->{
			UserDTO userDTO = new UserDTO();
			userDTO.setLogin(user[0].toString());
			userDTO.setFirstName(user[1].toString());
			userDTO.setLastName(user[2].toString());
			userDTO.setCompanyPid(user[3].toString());
			userDTO.setCompanyName(user[4].toString());
			userDTOs.add(userDTO);
		});
		return new ResponseEntity<>(userDTOs, HttpStatus.OK);
	}


	/**
	 * POST /validate : validate the license key.
	 *
	 * @param key
	 *            the license key
	 * @return the ResponseEntity with status 200 (OK) and the companyDTO in
	 *         body, or status 500 (Internal Server Error) if the companyDTO
	 *         couldn't be returned
	 */
	@PostMapping(path = "/validate", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
	@Timed
	public ResponseEntity<CompanyDTO> validateLicenseKey(@RequestParam(value = "key") String key) {
		return licenseRepository.findOneByLicenseKeyAndExpireDateAfterAndActivatedFalse(key, ZonedDateTime.now())
				.map(license -> new ResponseEntity<CompanyDTO>(companyMapper.companyToCompanyDTO(license.getCompany()),
						HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}
	
	@PostMapping(path = "/validate/user-onpremise", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
	@Timed
	public ResponseEntity<CompanyUrlConfigDTO> validateUserOnpremiseAndGetRedirectUrl(@RequestParam String login) {
		Optional<UserOnPremise> optionalUser = userOnPremiseRepository.findOneByLogin(login);
		CompanyUrlConfigDTO companyUrlConfigDTO = new CompanyUrlConfigDTO();
		companyUrlConfigDTO.setBaseUrl("http://salesnrich.com/api/");
		companyUrlConfigDTO.setWebLoginUrl("http://salesnrich.com/login");
		if(optionalUser.isPresent()) {
			if(optionalUser.get().getExpireDate().isBefore(Instant.now())) {
				companyUrlConfigDTO.setValidUser(Boolean.FALSE);
			}
			String baseUrl = companyRepository.findApiUrlByCompanyPid(optionalUser.get().getCompanyPid());
			if(baseUrl != null && !baseUrl.isEmpty()) {
				companyUrlConfigDTO.setBaseUrl(baseUrl + "/api/");
				companyUrlConfigDTO.setWebLoginUrl(baseUrl + "/login");
			} else {
				return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(companyUrlConfigDTO,HttpStatus.OK);
	}

	/**
	 * GET /authenticate : check if the user is authenticated, and return its
	 * login.
	 *
	 * @param request
	 *            the HTTP request
	 * @return the login if the user is authenticated
	 */
	@GetMapping("/authenticate")
	@Timed
	public String isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	/**
	 * GET /account : get the current user.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the current user in
	 *         body, or status 500 (Internal Server Error) if the user couldn't
	 *         be returned
	 */
	@GetMapping("/account")
	@Timed
	public ResponseEntity<UserDTO> getAccount() {
		return Optional.ofNullable(userService.getUserWithAuthorities())
				.map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /account : update the current user information.
	 *
	 * @param userDTO
	 *            the current user information
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) or 500 (Internal Server Error) if the user couldn't be
	 *         updated
	 */
	@PostMapping("/account")
	@Timed
	public ResponseEntity<String> saveAccount(@Valid @RequestBody UserDTO userDTO) {
		Optional<User> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
		if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("user-management", "emailexists", "Email already in use"))
					.body(null);
		}
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(u -> {
			userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
					userDTO.getLangKey());
			return new ResponseEntity<>("", HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /account/change_password : changes the current user's password
	 *
	 * @param password
	 *            the new password
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) if the new password is not strong enough
	 */
	@PostMapping(path = "/account/change_password", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.TEXT_PLAIN_VALUE })
	@Timed
	public ResponseEntity<String> changePassword(@RequestBody String password) {
		if (!checkPasswordLength(password)) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		}
		userService.changePassword(password);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * POST /account/reset_password/init : Send an email to reset the password
	 * of the user
	 *
	 * @param mail
	 *            the mail of the user
	 * @return the ResponseEntity with status 200 (OK) if the email was sent, or
	 *         status 400 (Bad Request) if the email address is not registered
	 */
	@PostMapping(path = "/account/reset_password/init", produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<String> requestPasswordReset(@RequestBody String mail) {
		return userService.requestPasswordReset(mail).map(user -> {
			mailService.sendPasswordResetMail(user);
			return new ResponseEntity<>("email was sent", HttpStatus.OK);
		}).orElse(new ResponseEntity<>("email address not registered", HttpStatus.BAD_REQUEST));
	}

	/**
	 * POST /account/reset_password/finish : Finish to reset the password of the
	 * user
	 *
	 * @param keyAndPassword
	 *            the generated key and the new password
	 * @return the ResponseEntity with status 200 (OK) if the password has been
	 *         reset, or status 400 (Bad Request) or 500 (Internal Server Error)
	 *         if the password could not be reset
	 */
	@PostMapping(path = "/account/reset_password/finish", produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordDTO keyAndPassword) {
		if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		}
		return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
				.map(user -> new ResponseEntity<String>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	private boolean checkPasswordLength(String password) {
		return (!StringUtils.isEmpty(password) && password.length() >= ManagedUserDTO.PASSWORD_MIN_LENGTH
				&& password.length() <= ManagedUserDTO.PASSWORD_MAX_LENGTH);
	}

	private boolean isWhiteListedDevice(String deviceKey) {
		List<String> list = whiteListedDevicesRepository.findDevicesByDeviceKey(deviceKey);
		return list.contains(deviceKey);
	}

	/**
	 * Post /mobile-discontinued-status : update user
	 * mobile-discontinued-status.
	 * 
	 * @return the ResponseEntity with status 200 (OK).
	 *
	 */
	@PostMapping(value = "/mobile-discontinued-status", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> updateUserDisContinuedStatus(@RequestParam Boolean discontinued) {
		log.debug("post request for update mobile-discontinued-status from mobile- ", "user :  ",
				SecurityUtils.getCurrentUserLogin(), "status :", discontinued);
		if (discontinued) {
			Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
			if (opUser.isPresent()) {
				User user = opUser.get();
				user.setDiscontinuedMobileCompletedDate(LocalDateTime.now());
				userRepository.save(user);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}