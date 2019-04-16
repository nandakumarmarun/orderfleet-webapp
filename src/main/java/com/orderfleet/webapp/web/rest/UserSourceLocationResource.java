package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserSourceLocation;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserSourceLocationRepository;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.UserSourceLocationDTO;

@Controller
@RequestMapping("/web")
public class UserSourceLocationResource {
	private final Logger log = LoggerFactory.getLogger(UserSourceLocationResource.class);
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private StockLocationRepository stockLocationRepository;
	
	@Inject
	private UserSourceLocationRepository userSourceLocationRepository;
	
	@Inject
	private UserRepository userRepository;
	
	/**
	 * GET /user-source-location : get all the filled user.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of filled
	 *         forms in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping("/user-source-location")
	@Timed
	public String getUserSourceLocation(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of filled forms");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("users", userService.findAllByCompany());
		} else {
			model.addAttribute("users", userService.findByUserIdIn(userIds));
		}
		
		model.addAttribute("stockLocations", stockLocationRepository.findAllByCompanyId());
		return "company/userSourceLocations";
		
	}
	
	@Transactional
	@Timed
	@RequestMapping(value = "/user-source-location/saveStockLocation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSourceLocationDTO> saveSourceLocation(@RequestParam String stockLocation,@RequestParam String userPid) {
		log.debug("request to save user stock Location");
		UserSourceLocation userSourceLocation1=userSourceLocationRepository.findSourceLocationsByUserPid(userPid);
		if(userSourceLocation1!=null) {
			userSourceLocationRepository.deleteByUserPid(userPid);
		}
	User user=userRepository.findOneByPid(userPid).get();
	StockLocation stockLocation2=stockLocationRepository.findOneByPid(stockLocation).get();
	UserSourceLocation userSourceLocation=new UserSourceLocation();
	userSourceLocation.setStockLocation(stockLocation2);
	userSourceLocation.setUser(user);
	userSourceLocation.setCompany(user.getCompany());
	
	userSourceLocationRepository.save(userSourceLocation);
		return new ResponseEntity<UserSourceLocationDTO>(HttpStatus.OK);
	}
	
	@Timed
	@RequestMapping(value = "/user-source-location/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSourceLocationDTO> getUserFavouriteDocuments(@PathVariable String userPid) {
		log.debug("Web request to get  by user pid : {}", userPid);
		UserSourceLocation userSourceLocation=userSourceLocationRepository.findSourceLocationsByUserPid(userPid);
		UserSourceLocationDTO userSourceLocationDTO=null;
		if(userSourceLocation!=null) {
			userSourceLocationDTO=new UserSourceLocationDTO(userSourceLocation);
		}
		return new ResponseEntity<>(userSourceLocationDTO,
				HttpStatus.OK);
	}
}
