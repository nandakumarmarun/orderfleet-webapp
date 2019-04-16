package com.orderfleet.webapp.web.rest;

import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.ManagedUserDTO;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * WEB controller for managing the current user's account.
 * 
 * @author Shaheer
 * @since September 07, 2016
 */
@Controller
@RequestMapping("/web")
public class AccountResource {

	private final Logger log = LoggerFactory.getLogger(AccountResource.class);

	private UserService userService;

	private UserRepository userRepository;

	private static final String VIEW_CHANGEPASS = "account/password";
	private static final String VIEW_EDITPROFILE = "account/edit-profile";

	/**
	 * @param userService
	 */
	@Inject
	public AccountResource(UserService userService, UserRepository userRepository) {
		super();
		this.userService = userService;
		this.userRepository = userRepository;
	}
	
	/**
	 * GET /account/change_password : get change password page.
	 *
	 * @return the view
	 */
	@RequestMapping(value = "/account/change-password", method = RequestMethod.GET)
	public String showChangePasswordPage() {
		return VIEW_CHANGEPASS;
	}
	
	/**
	 * GET /account/edit_profile : get change editprofile page.
	 *
	 * @return the view
	 */
	@RequestMapping(value = "/account/edit-profile", method = RequestMethod.GET)
	public String showEditProfilePage() {
		return VIEW_EDITPROFILE;
	}
	
	 /**
     * GET  /account : get the current user.
     *
     * @return the ResponseEntity with status 200 (OK) and the current user in body, or status 500 (Internal Server Error) if the user couldn't be returned
     */
    @RequestMapping(value = "/account",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserDTO> getAccount() {
        return Optional.ofNullable(userService.getUserWithAuthorities())
            .map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
	
	/**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) or 500 (Internal Server Error) if the user couldn't be updated
     */
    @RequestMapping(value = "/account",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    public ResponseEntity<String> saveAccount(@Valid @RequestBody UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("user-management", "emailexists", "Email already in use")).body(null);
        }
        return userRepository
            .findOneByLogin(SecurityUtils.getCurrentUserLogin())
            .map(u -> {
                userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                    userDTO.getLangKey());
                return new ResponseEntity<String>(HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

	/**
	 * POST /account/change-password : changes the current user's password
	 *
	 * @param password
	 *            the new password
	 * @param model
	 *            the view-model
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) if the new password is not strong enough
	 */
	@RequestMapping(value = "/account/change-password", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public String changePassword(@RequestParam(value = "password", required = true) String password, Model model) {
		log.debug("Web request to change logged in user password : {}", password);
		try {
			if (!checkPasswordLength(password)) {
				model.addAttribute("css", "danger");
				model.addAttribute("msg", "<strong>Incorrect password</strong>");
				return VIEW_CHANGEPASS;
			}
			userService.changePassword(password);
			model.addAttribute("css", "success");
			model.addAttribute("msg", "<strong>Password changed!</strong>");
			return VIEW_CHANGEPASS;
		} catch (Exception ex) {
			model.addAttribute("css", "danger");
			model.addAttribute("msg", "<strong>An error has occurred!</strong> The password could not be changed.");
			return VIEW_CHANGEPASS;
		}

	}

    private boolean checkPasswordLength(String password) {
		return !StringUtils.isEmpty(password) && password.length() >= ManagedUserDTO.PASSWORD_MIN_LENGTH
				&& password.length() <= ManagedUserDTO.PASSWORD_MAX_LENGTH;
	}
}