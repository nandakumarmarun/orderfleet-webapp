package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
@Secured(AuthoritiesConstants.SITE_ADMIN)
public class PasswordChangeResource {
	private final Logger log = LoggerFactory.getLogger(PasswordChangeResource.class);
	
	@Inject
	private CompanyService companyService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private UserRepository userRepository;
	
	
	@GetMapping("/change-password")
	@Timed
	public String getCompanyies(Model model) {
		model.addAttribute("companies",companyService.findAllCompanySortedByName());
		return "site_admin/changePassword";
	}
	
	@RequestMapping(value = "/user-list/{companypid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<UserDTO> getAllUsersByCompany(@Valid @PathVariable("companypid") String companypid) throws URISyntaxException {
		log.debug("Web request to Get Users by companyPid: {}", companypid);
		List<UserDTO> users=userService.findAllByCompanyPid(companypid);
		return users;
	}
	
	 @RequestMapping(value = "/saveNewPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
		    @ResponseBody
		    @Timed
		    public ResponseEntity<String> saveNewPassword(@RequestParam String userpid,@RequestParam String password) {
		 log.info("Enterd to update password :"+password);
		      
		    Optional<User> existingUser = userRepository.findOneByPid(userpid);
		       
		        return userRepository
		            .findOneByLogin(existingUser.get().getLogin())
		            .map(u -> {
		                userService.updatePassword(password,existingUser.get().getLogin());
		                return new ResponseEntity<String>(HttpStatus.OK);
		            })
		            .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
		    }

}
