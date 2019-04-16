package com.orderfleet.webapp.web.rest.api;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.config.Constants;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.async.MailService;
import com.orderfleet.webapp.web.rest.api.dto.ManagedUserDTO;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import com.orderfleet.webapp.web.rest.util.PaginationUtil;

/**
 * REST controller for managing users.
 *
 * @author Shaheer
 * @since May 06, 2016
 */
@RestController
@RequestMapping("/api")
public class UserResource {

	private final Logger log = LoggerFactory.getLogger(UserResource.class);

	@Inject
	private UserRepository userRepository;

	@Inject
	private MailService mailService;

	@Inject
	private UserService userService;

	/**
	 * POST /users : Creates a new user.
	 * <p>
	 * Creates a new user if the login and email are not already used, and sends
	 * an mail with an activation link. The user needs to be activated on
	 * creation.
	 * </p>
	 *
	 * @param managedUserDTO
	 *            the user to create
	 * @param request
	 *            the HTTP request
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new user, or with status 400 (Bad Request) if the login or email
	 *         is already in use
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@PostMapping("/users")
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
	@PutMapping("/users")
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
	 * GET /users : get all users.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all users
	 * @throws URISyntaxException
	 *             if the pagination headers couldn't be generated
	 */
	@GetMapping("/users")
	@Timed
	public ResponseEntity<List<ManagedUserDTO>> getAllUsers(Pageable pageable) throws URISyntaxException {
		Page<User> page = userRepository.findAllWithAuthorities(pageable);
		List<ManagedUserDTO> managedUserVMs = page.getContent().stream().map(ManagedUserDTO::new)
				.collect(Collectors.toList());
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
		return new ResponseEntity<>(managedUserVMs, headers, HttpStatus.OK);
	}
	
	/**
	 * GET /users/:login : get the "login" user.
	 *
	 * @param login
	 *            the login of the user to find
	 * @return the ResponseEntity with status 200 (OK) and with body the "login"
	 *         user, or with status 404 (Not Found)
	 */
	@GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
	@Timed
	public ResponseEntity<ManagedUserDTO> getUser(@PathVariable String login) {
		log.debug("REST request to get User : {}", login);
		return userService.getUserWithAuthoritiesByLogin(login).map(ManagedUserDTO::new)
				.map(managedUserDTO -> new ResponseEntity<>(managedUserDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
