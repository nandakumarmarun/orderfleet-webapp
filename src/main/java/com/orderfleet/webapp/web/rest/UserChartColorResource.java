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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.UserService;

@Controller
@RequestMapping("/web")
public class UserChartColorResource {
	
	private final Logger log = LoggerFactory.getLogger(UserChartColorResource.class);
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private UserRepository userRepository;

	/**
	 * GET /user-chart-color : get all the filled user.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of filled
	 *         forms in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping("/user-chart-color")
	@Timed
	public String getUserChartColor(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of filled forms");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("users", userService.findAllByCompany());
		} else {
			model.addAttribute("users", userService.findByUserIdIn(userIds));
		}
		return "company/userChartColor";
		
	}
	
	@Timed
	@RequestMapping(value = "/user-chart-color/changeColor", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> saveColor(@RequestParam String colorCode,@RequestParam String userPid) {
		log.debug("request to change color ");
		
	User user=userRepository.findOneByPid(userPid).get();
	int r= Integer.valueOf( colorCode.substring( 1, 3 ), 16 );
	int g=Integer.valueOf( colorCode.substring( 3, 5 ), 16 );
	int b= Integer.valueOf( colorCode.substring( 5, 7 ), 16 ) ;
	String color="rgb("+r+","+g+","+b+")";
	if(user!=null) {
		user.setChartColor(color);
		userRepository.save(user);
	}
		return new ResponseEntity<User>(HttpStatus.OK);
	}
}
