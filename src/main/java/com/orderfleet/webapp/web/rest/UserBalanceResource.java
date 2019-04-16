package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.UserBalanceService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.UserBalanceDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class UserBalanceResource {
	private final Logger log = LoggerFactory.getLogger(UserBalanceResource.class);
	
	@Inject
	private UserService userService;
	
	@Inject
	private UserBalanceService userBalanceService;
	
	/**
	 * GET /user-balances : get all the userBalances.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of userBalances in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-balances", method = RequestMethod.GET)
	@Timed
	public String getAllUserBalances(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of UserBalances");
		List<UserDTO> userDTOs = userService.findAllByCompany();
		model.addAttribute("users", userDTOs);
		List<UserBalanceDTO> userBalanceDTOs = userBalanceService.findAllByCompany();
		model.addAttribute("userBalances", userBalanceDTOs);
		return "company/userBalances";
	}
	
	/**
	 * POST /user-balances : Create a new userBalance.
	 *
	 * @param userBalanceDTO
	 *            the userBalanceDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new userBalanceDTO, or with status 400 (Bad Request) if the userBalance has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/user-balances", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<UserBalanceDTO> createUserBalance(@Valid @RequestBody UserBalanceDTO userBalanceDTO) throws URISyntaxException {
		log.debug("Web request to save UserBalance : {}", userBalanceDTO);
		if (userBalanceDTO.getId() != null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("userBalance", "idexists", "A new userBalance cannot already have an ID"))
					.body(null);
		}
		UserBalanceDTO result = userBalanceService.save(userBalanceDTO);
		return ResponseEntity.created(new URI("/web/userBalances" ))
				.headers(HeaderUtil.createEntityCreationAlert("userBalance", result.getId().toString())).body(result);
	}
	
	/**
	 * PUT /user-balances : Updates an existing userBalance.
	 *
	 * @param userBalanceDTO
	 *            the userBalanceDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         userBalanceDTO, or with status 400 (Bad Request) if the userBalanceDTO is not
	 *         valid, or with status 500 (Internal Server Error) if the userBalanceDTO
	 *         couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/user-balances", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserBalanceDTO> updateUserBalance(@Valid @RequestBody UserBalanceDTO userBalanceDTO) throws URISyntaxException {
		log.debug("REST request to update UserBalance : {}", userBalanceDTO);
		if (userBalanceDTO.getId() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("userBalance", "idNotexists", "UserBalance must have an ID")).body(null);
		}
		UserBalanceDTO result = userBalanceService.update(userBalanceDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("userBalance", "idNotexists", "Invalid UserBalance ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("userBalance", userBalanceDTO.getId().toString()))
				.body(result);
	}
	/**
	 * GET /user-balances/:pid : get the "pid" userBalance.
	 *
	 * @param pid
	 *            the pid of the userBalanceDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         userBalanceDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/user-balances/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserBalanceDTO> getUserBalance(@PathVariable Long id) {
		log.debug("Web request to get UserBalance by id : {}", id);
		UserBalanceDTO userBalanceDTO=userBalanceService.findOne(id);
		return new ResponseEntity<UserBalanceDTO>(userBalanceDTO, HttpStatus.OK);
	}

	/**
	 * DELETE /user-balances/:id : delete the "id" userBalance.
	 *
	 * @param id
	 *            the id of the userBalanceDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/user-balances/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteUserBalance(@PathVariable Long id) {
		log.debug("REST request to delete UserBalance : {}", id);
		userBalanceService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("userBalance", id.toString())).build();
	}
}
