package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.MobileConfigurationService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.UserWiseMobileConfigurationService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.MobileConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseMobileConfigurationDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * Web controller for managing MobileConfiguration.
 *
 * @author Sarath
 * @since Jul 28, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class UserMobileConfigurationResource {

	private final Logger log = LoggerFactory.getLogger(UserMobileConfigurationResource.class);

	@Inject
	private CompanyService companyService;
	@Inject
	private CompanyRepository companyRepository;
	@Inject
	private UserService userService;
	@Inject
	private UserRepository userRepository;
	@Inject
	private UserWiseMobileConfigurationService mobileConfigurationService;

	@RequestMapping(value = "/user-mobile-configuration", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getAllMobileConfiguration(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Mobile Configuration");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("mobileConfigurations", mobileConfigurationService.findAll());
		return "site_admin/userMobileConfiguration";
	}
	@RequestMapping(value = "/user-mobile-configuration/loadUsers/{companypid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<UserDTO> getAllUsersByCompany(@Valid @PathVariable("companypid") String companypid) throws URISyntaxException {
		log.debug("Web request to Get Users by companyPid: {}", companypid);
		List<UserDTO> users=userService.findAllByCompanyPid(companypid);
		return users;
	}
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/user-mobile-configuration/{userPid}", method = RequestMethod.GET)
	public @ResponseBody UserWiseMobileConfigurationDTO getMobileConfiguration(@PathVariable String userPid)
			throws URISyntaxException {
		log.debug("Web request to get Mobile Configuration while editing");
		log.info("UserPid :"+userPid);
		Optional<User> user = userRepository.findOneByPid(userPid);
		String companyPid = user.get().getCompany().getPid();
		Optional<Company> optionalCompany = companyRepository.findOneByPid(companyPid);
		Long userId = user.get().getId();
		if (user.isPresent()) {
			return mobileConfigurationService.findOneByUserId(userId).get();
		} 
		return null;
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/user-mobile-configuration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveMobileConfiguration(@RequestBody UserWiseMobileConfigurationDTO mobileConfigurationDTO)
			throws URISyntaxException {
		log.debug("Web request to save Mobile Configuration ");
      Optional<User> optionaluser= userRepository.findOneByPid(mobileConfigurationDTO.getUserPid());
	  Optional<Company> optionalCompany = companyRepository.findOneByPid(mobileConfigurationDTO.getCompanyPid());
	  log.info("CompanyName :"+optionalCompany.get().getLegalName());
		if (!optionalCompany.isPresent()) {
			return new ResponseEntity<>("Invalid Company", HttpStatus.BAD_REQUEST);
		}
		Optional<UserWiseMobileConfigurationDTO> opConfigurationDTO = mobileConfigurationService
				.findOneByUserId(optionaluser.get().getId());
		
		if (opConfigurationDTO.isPresent()) {
			mobileConfigurationDTO.setPid(opConfigurationDTO.get().getPid());
			mobileConfigurationService.updateMobileConfiguration(mobileConfigurationDTO);
		} else {
			mobileConfigurationService.saveUserMobileConfiguration(mobileConfigurationDTO);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/user-mobile-configuration/delete/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteMobileConfiguration(@PathVariable String pid) throws URISyntaxException {
		log.debug("Web request to delete Mobile Configuration By compantPid : {}", pid);
		mobileConfigurationService.deleteByPid(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("mobileConfiguration", pid.toString()))
				.build();
	}
}
