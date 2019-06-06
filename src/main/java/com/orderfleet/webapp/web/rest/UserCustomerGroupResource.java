package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.service.AccountGroupAccountProfileService;
import com.orderfleet.webapp.service.AccountGroupService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.StageService;
import com.orderfleet.webapp.service.UserPriceLevelService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.UserCustomerGroupService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.AccountGroupDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.UserCustomerGroupDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

import javassist.expr.NewArray;

/**
 * Web controller for managing AccountGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Controller
@RequestMapping("/web")
public class UserCustomerGroupResource {

	private final Logger log = LoggerFactory.getLogger(UserPriceLevelResource.class);

	@Inject
	private UserPriceLevelService userPriceLevelService;

	@Inject
	private UserCustomerGroupService userStageService;

	@Inject
	private StageService stageService;

	@Inject
	private UserService userService;

	/**
	 * GET /user-price-levels : get all the user priceLevels.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users and
	 *         priceLevels in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/user-customerGroups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserStages(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Customer Groups");

		List<UserDTO> users = userService.findAllByCompany();
		model.addAttribute("users", users);
		model.addAttribute("stages", stageService.findAllByCompany());

		return "company/userCustomerGroups";
	}

	/**
	 * * GET /user-price-levels/save : save user priceLevels.
	 * 
	 * @param pid
	 * @param assignedPriceLevels
	 * @return
	 */

	@RequestMapping(value = "/user-customerGroups/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String userPid, @RequestParam String stages) {
		log.debug("REST request to save assigned Stages", userPid);
		userStageService.save(userPid, stages);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user-customerGroups/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<UserCustomerGroupDTO>> getUserPriceLevels(@PathVariable String userPid) {
		log.debug("Web request to get get Stages by user pid : {}", userPid);
		return new ResponseEntity<>(userStageService.findUserCustomerGroupsByUserPid(userPid), HttpStatus.OK);

	}
}
