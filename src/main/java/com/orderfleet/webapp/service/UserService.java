package com.orderfleet.webapp.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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

import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.License;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.DashboardUIType;
import com.orderfleet.webapp.domain.enums.DeviceType;
import com.orderfleet.webapp.repository.AuthorityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.LicenseRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.ManagedUserDTO;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.RegistrationDto;

/**
 * Service class for managing users.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
@Service
@Transactional
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	public static final String PID_PREFIX = "USR-";

	// @Inject
	// private PasswordEncoder passwordEncoder;

	@Inject
	private UserRepository userRepository;

	@Inject
	private AuthorityRepository authorityRepository;

	@Inject
	private LicenseRepository licenseRepository;

	@Inject
	private CompanyRepository companyRepository;

	public Optional<User> activateRegistration(String key) {
		log.debug("Activating user for activation key {}", key);
		return userRepository.findOneByActivationKey(key).map(user -> {
			// activate given user for the registration key.
			user.setActivated(true);
			user.setActivationKey(null);
			log.debug("Activated user: {}", user);
			return user;
		});
	}

	public Optional<User> completePasswordReset(String newPassword, String key) {
		log.debug("Reset user password for reset key {}", key);

		return userRepository.findOneByResetKey(key).filter(user -> {
			ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
			return user.getResetDate().isAfter(oneDayAgo);
		}).map(user -> {
			// user.setPassword(passwordEncoder.encode(newPassword));
			user.setPassword(newPassword);
			user.setResetKey(null);
			user.setResetDate(null);
			return user;
		});
	}

	public Optional<User> requestPasswordReset(String mail) {
		return userRepository.findOneByEmail(mail).filter(User::getActivated).map(user -> {
			user.setResetKey(RandomUtil.generateResetKey());
			user.setResetDate(ZonedDateTime.now());
			return user;
		});
	}

	public User createUser(String login, String password, String firstName, String lastName, String email,
			String mobile, String langKey, String deviceKey, License license) {

		User newUser = new User();
		newUser.setPid(UserService.PID_PREFIX + RandomUtil.generatePid());
		newUser.setCompany(license.getCompany()); // set company
		Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
		Set<Authority> authorities = new HashSet<>();
		String encryptedPassword = password;// passwordEncoder.encode(password);
		newUser.setLogin(login);
		// new user gets initially a generated password
		newUser.setPassword(encryptedPassword);
		newUser.setFirstName(firstName);
		newUser.setLastName(lastName);
		newUser.setEmail(email);
		newUser.setMobile(mobile);
		newUser.setLangKey(langKey);
		// new user is not active
		newUser.setActivated(false);
		newUser.setDeviceKey(deviceKey);
		// new user gets registration key
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		authorities.add(authority);
		newUser.setAuthorities(authorities);
		userRepository.save(newUser);
		log.debug("Created Information for User: {}", newUser);

		// update license
		license.setActivated(true);
		license.setActivatedDate(ZonedDateTime.now());
		licenseRepository.save(license);
		return newUser;
	}

	public User createUser(ManagedUserDTO managedUserDTO) {
		User user = new User();
		user.setPid(UserService.PID_PREFIX + RandomUtil.generatePid());
		user.setCompany(companyRepository.findOneByPid(managedUserDTO.getCompanyPid()).get());
		user.setLogin(managedUserDTO.getLogin().toLowerCase());
		user.setFirstName(managedUserDTO.getFirstName());
		user.setLastName(managedUserDTO.getLastName());
		user.setEmail(managedUserDTO.getEmail());
		user.setMobile(managedUserDTO.getMobile());
		user.setDeviceType(DeviceType.TABLET);
		user.setDashboardUIType(managedUserDTO.getDashboardUIType());
		if (managedUserDTO.getLangKey() == null) {
			user.setLangKey("en"); // default language
		} else {
			user.setLangKey(managedUserDTO.getLangKey());
		}
		if (managedUserDTO.getAuthorities() != null) {
			Set<Authority> authorities = new HashSet<>();
			managedUserDTO.getAuthorities().stream()
					.forEach(authority -> authorities.add(authorityRepository.findOne(authority)));
			user.setAuthorities(authorities);
		}
		String encryptedPassword = RandomUtil.generatePassword();// passwordEncoder.encode(RandomUtil.generatePassword());
		user.setPassword(encryptedPassword);
		user.setResetKey(RandomUtil.generateResetKey());
		user.setResetDate(ZonedDateTime.now());

		/* ................................................... */
		// just for save data this fields are notnull
		user.setActivationKey("activationKey");
		user.setDeviceKey("deviceKey");
		/* ................................................... */

		user.setActivated(managedUserDTO.isActivated());
		userRepository.save(user);
		log.debug("Created Information for User: {}", user);
		return user;
	}

	public void updateUser(String firstName, String lastName, String email, String langKey) {
		userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			user.setLangKey(langKey);
			log.debug("Changed Information for User: {}", user);
		});
	}

	public void updateUser(String pid, String login, String firstName, String lastName, String email, boolean activated,
			String langKey, Set<String> authorities, boolean showAllUsersData, DashboardUIType dashboardUIType) {

		userRepository.findOneByPid(pid).ifPresent(u -> {
			u.setLogin(login);
			u.setFirstName(firstName);
			u.setLastName(lastName);
			u.setEmail(email);
			u.setActivated(activated);
			u.setLangKey(langKey);
			u.setDashboardUIType(dashboardUIType);
			Set<Authority> managedAuthorities = u.getAuthorities();
			u.setShowAllUsersData(showAllUsersData);
			managedAuthorities.clear();
			authorities.stream().forEach(authority -> managedAuthorities.add(authorityRepository.findOne(authority)));
			log.debug("Changed Information for User: {}", u);
		});
	}

	public void changePassword(String password) {
		userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(user -> {
			// String encryptedPassword = passwordEncoder.encode(password);
			String encryptedPassword = password;
			user.setPassword(encryptedPassword);
			log.debug("Changed password for User: {}", user);
		});
	}

	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthoritiesByLogin(String login) {
		return userRepository.findOneByLogin(login).map(user -> {
			user.getAuthorities().size();
			return user;
		});
	}

	@Transactional(readOnly = true)
	public User getUserWithAuthorities(String pid) {
		User user = userRepository.findOneByPid(pid).get();
		user.getAuthorities().size(); // eagerly load the association
		return user;
	}

	@Transactional(readOnly = true)
	public User getUserWithAuthorities() {
		Optional<User> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		User user = null;
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
			user.getAuthorities().size(); // eagerly load the association
		}
		return user;
	}

	/**
	 * Get all the users.
	 * 
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public List<UserDTO> findAllByCompany() {
		log.debug("Request to get all users");
		List<User> users = userRepository.findAllByCompanyId();
		List<UserDTO> result = new ArrayList<>();
		users.forEach(user -> {
			UserDTO userDTO = new UserDTO();
			userDTO.setPid(user.getPid());
			userDTO.setFirstName(user.getFirstName());
			userDTO.setLastName(user.getLastName());
			userDTO.setLogin(user.getLogin());
			userDTO.setChartColor(user.getChartColor());
			userDTO.setDashboardUIType(user.getDashboardUIType());
			result.add(userDTO);
		});
		return result;
	}

	/**
	 * Get all the users by company pid.
	 * 
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public List<UserDTO> findAllByCompanyPid(String companyPid) {
		log.debug("Request to get all users by company Pid");
		List<User> users = userRepository.findAllByCompanyPidSortedByName(companyPid);
		List<UserDTO> result = new ArrayList<>();
		users.forEach(user -> {
			UserDTO userDTO = new UserDTO(user);
			result.add(userDTO);
		});
		return result;
	}

	/**
	 * Get all the users.
	 * 
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllByCompanyId(Pageable pageable) {
		log.debug("Request to get all users");
		Page<User> users = userRepository.findAllByCompanyId(pageable);
		List<UserDTO> userDTOs = new ArrayList<>();
		users.forEach(user -> {
			UserDTO userDTO = new UserDTO();
			userDTO.setPid(user.getPid());
			userDTO.setFirstName(user.getFirstName());
			userDTO.setLastName(user.getLastName());
			userDTO.setLogin(user.getLogin());
			userDTOs.add(userDTO);
		});
		Page<UserDTO> result = new PageImpl<>(userDTOs, pageable, users.getTotalElements());
		return result;
	}

	/**
	 * Get one bank by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Transactional(readOnly = true)
	public User getCurrentUser() {
		Optional<User> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		User user = null;
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
		}
		return user;
	}

	@Transactional(readOnly = true)
	public ManagedUserDTO findOneByPid(String pid) {
		log.debug("Request to get User by pid : {}", pid);
		Optional<User> user = userRepository.findOneByPid(pid);
		if (user.isPresent()) {
			return new ManagedUserDTO(user.get());
		}
		return new ManagedUserDTO();
	}
	
	@Transactional(readOnly = true)
	public Optional<ManagedUserDTO> findOneByLogin(String login) {
		return userRepository.findOneByLogin(login).map(user -> {
			return new ManagedUserDTO(user);
		});
	}

	public ManagedUserDTO changeStatusUser(String pid, boolean activated) {

		Optional<User> user1 = userRepository.findOneByPid(pid);
		User user2 = user1.get();
		user2.setActivated(activated);
		user2 = userRepository.save(user2);
		ManagedUserDTO result = new ManagedUserDTO(user2);
		return result;

	}

	/**
	 * Get one user by deviceKey.
	 *
	 * @param deviceKey
	 *            the deviceKey of the entity
	 * @return the entity
	 */
	@Transactional(readOnly = true)
	public boolean findOneByDeviceKey(String deviceKey) {
		log.debug("Request to get DeviceKey by pid : {}", deviceKey);
		Optional<User> user = userRepository.findOneByDeviceKey(deviceKey);
		return user.isPresent() ? true : false;
	}

	@Transactional(readOnly = true)
	public List<UserDTO> findByUserIdIn(List<Long> userIds) {
		log.debug("Request to get all users");
		List<User> users = userRepository.findByUserIdIn(userIds);
		List<UserDTO> result = new ArrayList<>();
		users.forEach(user -> {
			UserDTO userDTO = new UserDTO();
			userDTO.setPid(user.getPid());
			userDTO.setFirstName(user.getFirstName());
			userDTO.setLastName(user.getLastName());
			userDTO.setLogin(user.getLogin());
			userDTO.setChartColor(user.getChartColor());
			userDTO.setDashboardUIType(user.getDashboardUIType());
			result.add(userDTO);
		});
		return result;
	}

	@Transactional(readOnly = true)
	public List<String> findUserPidsByUserIdIn(List<Long> userIds) {
		log.debug("Request to get all users");
		List<User> users = userRepository.findByUserIdIn(userIds);
		List<String> result = new ArrayList<>();
		users.forEach(user -> {
			result.add(user.getPid());
		});
		return result;
	}

	@Transactional(readOnly = true)
	public List<String> findAllUserPidsByCompany() {
		log.debug("Request to get all users");
		List<User> users = userRepository.findAllByCompanyId();
		List<String> result = new ArrayList<>();
		users.forEach(user -> {
			result.add(user.getPid());
		});
		return result;
	}

	@Transactional(readOnly = true)
	public Optional<User> findByName(String name) {
		log.debug("Request to get User by pid : {}", name);
		Optional<User> user = userRepository.findOneByFirstName(name);
		return user;
	}

	public void changeStatusUsers(String userPids, String activated) {
		String[] users = userPids.split(",");
		if (activated.equals("activate")) {
			for (String pid : users) {
				Optional<User> user1 = userRepository.findOneByPid(pid);
				User user2 = user1.get();
				user2.setActivated(true);
				userRepository.save(user2);
			}
		} else {
			for (String pid : users) {
				Optional<User> user1 = userRepository.findOneByPid(pid);
				User user2 = user1.get();
				user2.setActivated(false);
				userRepository.save(user2);
			}
		}
	}

	public ManagedUserDTO changeDiscontinuedStatusUser(String pid, boolean status) {
		Optional<User> opUser = userRepository.findOneByPid(pid);
		ManagedUserDTO result = new ManagedUserDTO();
		if (opUser.isPresent()) {
			User user = opUser.get();
			user.setDiscontinued(status);
			user = userRepository.save(user);
			result = new ManagedUserDTO(user);
		}
		return result;

	}

	public User updateUser(ManagedUserDTO managedUserDTO) {
		Optional<User> opUser = userRepository.findOneByPid(managedUserDTO.getPid());
		User result = new User();
		if (opUser.isPresent()) {
			User u = opUser.get();
			u.setLogin(managedUserDTO.getLogin());
			u.setEmail(managedUserDTO.getEmail());
			u.setFirstName(managedUserDTO.getFirstName());
			u.setLastName(managedUserDTO.getLastName());
			u.setMobile(managedUserDTO.getMobile());
			result = userRepository.save(u);
		}
		return result;
	}

	public List<User> findByAuthoritiesIn(Set<Authority> authorities) {
		log.debug("Request to get all users by authorities");
		List<User> users = userRepository.findByAuthoritiesIn(authorities);
		return users;
	}

	public List<User> findByAuthoritiesInAndActivated(Set<Authority> authorities, boolean activated) {
		log.debug("Request to get all active users by authorities");
		List<User> users = userRepository.findByAuthoritiesInAndActivated(authorities, activated);
		return users;
	}

	public List<UserDTO> findAllByActivated(boolean activated) {
		log.debug("Request to get all active users activated : ", activated);
		List<User> users = userRepository.findAllByActivated(activated);
		List<UserDTO> result = users.stream().map(UserDTO::new).collect(Collectors.toList());
		return result;
	}

	public Optional<UserDTO> findAllUserByCompanyPidAndUserPid(String companyPid, String userPid) {
		log.debug("Request to get all active users by Company Pid and User Pid : ", companyPid, userPid);
		return userRepository.findAllUserByCompanyPidAndUserPid(companyPid, userPid).map(usr -> {
			UserDTO result = new UserDTO(usr);
			return result;
		});
	}

	public void updateUser(List<UserDTO> dtoUsers) {
		log.debug("Request to change the DeviceKey for User/Users: {}", dtoUsers);
		dtoUsers.forEach(user -> {
			userRepository.findOneByPid(user.getPid()).ifPresent(u -> {
				u.setDeviceKey(user.getDeviceKey());
			});
		});
	}
	
	public List<User> findAllByCompanyId(){
		return userRepository.findAllByCompanyId();
	}
	
	public User createUser(RegistrationDto registrationDto, Company company, String roleName) {
		User user = new User();
		/*user.setPid(UserService.PID_PREFIX + RandomUtil.generatePid());
		user.setCompany(company); // set company
		user.setLogin(registrationDto.getEmail());
		// new user gets initially a generated password
		user.setPassword(RandomUtil.generatePassword());
		user.setFirstName(registrationDto.getFirstName());
		user.setLastName(registrationDto.getLastName());
		user.setEmail(registrationDto.getEmail());
		user.setMobile(registrationDto.getPhone());
		user.setLangKey("en");
		user.setActivated(true);
		user.setActivationKey(RandomUtil.generateActivationKey());
		Authority authority = authorityRepository.findOne(roleName);
		Set<Authority> authorities = new HashSet<>();
		authorities.add(authority);
		user.setAuthorities(authorities);
		user.setDashboardUIType(DashboardUIType.TW);
		user = userRepository.save(user);
		log.debug("Automatic admin user created with login : {}", user.getLogin());*/
		return user;
	}
}
