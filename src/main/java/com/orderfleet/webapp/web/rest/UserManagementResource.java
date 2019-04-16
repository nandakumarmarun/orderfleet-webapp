package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.config.Constants;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.AuthorityRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.async.MailService;
import com.orderfleet.webapp.web.rest.api.dto.ManagedUserDTO;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * REST controller for managing users.
 *
 * @author Shaheer
 * @since May 06, 2016
 */
@Controller
@RequestMapping("/web/management")
public class UserManagementResource {

	private final Logger log = LoggerFactory.getLogger(UserManagementResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private MailService mailService;

	@Inject
	private UserService userService;

	@Inject
	AuthorityRepository authorityRepository;

	/**
	 * GET /users : get all users.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all users
	 * @throws URISyntaxException
	 *             if the pagination headers couldn't be generated
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getUserManagementPage(Pageable pageable, Model model) {
		log.debug("Web request to get user-management page with user");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("authorities", authorityRepository.findAll());

		return "site_admin/user-management";
	}

	/**
	 * POST /users : Creates a new user.
	 * <p>
	 * Creates a new user if the login and email are not already used, and sends an
	 * mail with an activation link. The user needs to be activated on creation.
	 * </p>
	 *
	 * @param managedUserVM
	 *            the user to create
	 * @param request
	 *            the HTTP request
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         user, or with status 400 (Bad Request) if the login or email is
	 *         already in use
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<?> createUser(@RequestBody ManagedUserDTO managedUserDTO, HttpServletRequest request)
			throws URISyntaxException {
		log.debug("REST request to save User : {}", managedUserDTO);

		// Lowercase the user login before comparing with database
		if (userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use"))
					.body(null);
		} else if (userRepository.findOneByEmail(managedUserDTO.getEmail()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("userManagement", "emailexists", "Email already in use"))
					.body(null);
		} else {
			User newUser = userService.createUser(managedUserDTO);
			mailService.sendCreationEmail(newUser);
			return ResponseEntity
					.created(new URI("/api/users/" + newUser.getLogin())).headers(HeaderUtil
							.createAlert("A user is created with identifier " + newUser.getLogin(), newUser.getLogin()))
					.body(newUser);
		}
	}

	/**
	 * PUT /users : Updates an existing User.
	 *
	 * @param managedUserDTO
	 *            the user to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         user, or with status 400 (Bad Request) if the login or email is
	 *         already in use, or with status 500 (Internal Server Error) if the
	 *         user couldn't be updated
	 */
	@ResponseBody
	@RequestMapping(value = "/users", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<ManagedUserDTO> updateUser(@RequestBody ManagedUserDTO managedUserDTO) {
		log.debug("REST request to update User : {}", managedUserDTO);
		Optional<User> existingUser = userRepository.findOneByEmail(managedUserDTO.getEmail());
		if (existingUser.isPresent() && (!existingUser.get().getPid().equals(managedUserDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("userManagement", "emailexists", "E-mail already in use"))
					.body(null);
		}
		existingUser = userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase());
		if (existingUser.isPresent() && (!existingUser.get().getPid().equals(managedUserDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use"))
					.body(null);
		}
		userService.updateUser(managedUserDTO.getPid(), managedUserDTO.getLogin(), managedUserDTO.getFirstName(),
				managedUserDTO.getLastName(), managedUserDTO.getEmail(), managedUserDTO.isActivated(),
				managedUserDTO.getLangKey(), managedUserDTO.getAuthorities(), managedUserDTO.isShowAllUserData(),
				managedUserDTO.getDashboardUIType());

		return ResponseEntity.ok().headers(HeaderUtil.createAlert("userManagement.updated", managedUserDTO.getLogin()))
				.body(new ManagedUserDTO(userService.getUserWithAuthorities(managedUserDTO.getPid())));
	}

	/**
	 * GET /users/:login : get the "login" user.
	 *
	 * @param login
	 *            the login of the user to find
	 * @return the ResponseEntity with status 200 (OK) and with body the "login"
	 *         user, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/users/{login:" + Constants.LOGIN_REGEX
			+ "}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ManagedUserDTO> getUser(@PathVariable String login) {
		log.debug("REST request to get User : {}", login);
		return userService.getUserWithAuthoritiesByLogin(login).map(ManagedUserDTO::new)
				.map(managedUserDTO -> new ResponseEntity<>(managedUserDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * GET /users/:id : get the "id" user.
	 *
	 * @param id
	 *            the id of the userDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the userDTO, or
	 *         with status 404 (Not Found)
	 */
	@RequestMapping(value = "/users/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ManagedUserDTO> getUserByPid(@PathVariable String pid) {
		log.debug("Web request to get User by pid : {}", pid);
		ManagedUserDTO user = userService.findOneByPid(pid);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/users/changeStatus", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<ManagedUserDTO> changeStatusUser(@RequestBody ManagedUserDTO managedUserDTO) {
		log.debug("Web request to get User by pid : {}", managedUserDTO.getPid());
		ManagedUserDTO userDTO = userService.changeStatusUser(managedUserDTO.getPid(), managedUserDTO.isActivated());
		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}

	/**
	 * GET /users/filter : get all the users.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/users/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	@Transactional(readOnly = true)
	public ResponseEntity<List<ManagedUserDTO>> filterCompanyUser(@RequestParam String companyPid) {
		log.debug("Web request to get user-management page with ComapnyPid : {}", companyPid);
		List<User> users = new ArrayList<>();
		List<ManagedUserDTO> managedUserList = new ArrayList<ManagedUserDTO>();
		if (!companyPid.equals("no")) {
			users = userRepository.findAllWithAuthoritiesAndCompanyPid(companyPid);
			managedUserList = users.stream().map(ManagedUserDTO::new).collect(Collectors.toList());
		} else {
			users = userRepository.findAllByCompanyId();
			managedUserList = users.stream().map(ManagedUserDTO::new).collect(Collectors.toList());
		}
		return new ResponseEntity<>(managedUserList, HttpStatus.OK);
	}

	/**
	 * GET /users/:companyPid : get the "company" users.
	 *
	 * @param companyPid
	 *            the companyPid of the user to find
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 */
	@GetMapping("/users/company/{companyPid}")
	@Timed
	public ResponseEntity<List<UserDTO>> getCompanyUsers(@PathVariable String companyPid) {
		log.debug("REST request to get users of a company");
		List<UserDTO> users = userService.findAllByCompanyPid(companyPid);
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	// private void createUserRemoteLocation(ManagedUserDTO user)
	// {
	// final String uri = "http://salesnrich.com/api/account/registration_data";
	// UserRemoteLocationDTO userRemoteLocationDTO=new UserRemoteLocationDTO();
	// userRemoteLocationDTO.setLogin(user.getLogin());
	// userRemoteLocationDTO.setEmail(user.getEmail());
	// userRemoteLocationDTO.setFirstName(user.getFirstName());
	// userRemoteLocationDTO.setLastName(user.getLastName());
	// userRemoteLocationDTO.setMobile(user.getMobile());
	// RestTemplate restTemplate = new RestTemplate();
	// restTemplate.postForObject( uri, userRemoteLocationDTO,
	// UserRemoteLocationDTO.class);
	// }

	@RequestMapping(value = "/users/loadUser", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<List<ManagedUserDTO>> loadUser(@RequestParam String companyPid) {
		log.debug("Load User by Company Pid", companyPid);
		List<User> users = new ArrayList<User>();
		List<ManagedUserDTO> managedUserList = new ArrayList<ManagedUserDTO>();
		users = userRepository.findAllWithAuthoritiesAndCompanyPid(companyPid);
		managedUserList = users.stream().map(ManagedUserDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(managedUserList, HttpStatus.OK);

	}

	@RequestMapping(value = "/users/activateUsers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public ResponseEntity<List<ManagedUserDTO>> activateUsers(@RequestParam String users,
			@RequestParam String activate) {
		userService.changeStatusUsers(users, activate);
		return new ResponseEntity<>(HttpStatus.OK);

	}
}