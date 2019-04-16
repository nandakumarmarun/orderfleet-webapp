package com.orderfleet.webapp.web.rest.integration;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.async.TPProductProfileManagementService;

@RestController
@RequestMapping(value = "/api/tp/v1")
public class ValidateApplicationResource {

	private final Logger log = LoggerFactory.getLogger(ValidateApplicationResource.class);
	public final UserRepository userRepository;
	
	@Inject
	public CompanyRepository companyRepository;
	
	public ValidateApplicationResource(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	/**
	 * POST /validate.json : validate the machine.
	 *
	 * @param key
	 *            the validation key
	 * @return the ResponseEntity with status 200 (OK) and the validated user in
	 *         body, or status 500 (Internal Server Error) if the user couldn't
	 *         be validated
	 */
	@RequestMapping(value = "/validate.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Boolean> validateAccountJSON(@RequestBody String key) {
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(user -> {
			log.info(user.getId() + "...."+user.getDeviceKey());
			if (!key.equals(user.getDeviceKey())) {
				return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
			} 
			return new ResponseEntity<Boolean>(Boolean.TRUE,HttpStatus.OK);
		}).orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}
	
	@RequestMapping(value = "/save-hard-disk", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public  ResponseEntity<Boolean> saveHardDiskNo(@RequestBody String key) {
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if(user.isPresent()) {
			return updateHDDNumber(user.get(),key);
		}else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private ResponseEntity<Boolean> updateHDDNumber(User user, String key) {
		log.info("Save users hard disk");
		boolean updated = false;
		List<User> users = userRepository.findAllByCompanyPid(user.getCompany().getPid());
		for(User singleUser : users) {
			if(singleUser.getDeviceKey() == null ) {
				updated = true;
				singleUser.setDeviceKey(key);
			}else if(singleUser.getDeviceKey().equals("deviceKey")) {
				updated = true;
				singleUser.setDeviceKey(key);
			}
		}
		userRepository.save(users);
		if(updated) {
			return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
		}else {
			return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
		}
		
	}
}
