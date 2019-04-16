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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.UserFavouriteDocumentService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.UserFavouriteDocumentDTO;

/**
 * Web controller for managing UserFavouriteDocument.
 * 
 * @author Muhammed Riyas T
 * @since October 31, 2016
 */
@Controller
@RequestMapping("/web")
public class UserFavouriteDocumentResource {

	private final Logger log = LoggerFactory.getLogger(UserFavouriteDocumentResource.class);

	@Inject
	private UserFavouriteDocumentService userFavouriteDocumentService;

	@Inject
	private UserService userService;

	@Inject
	private UserActivityService userActivityService;

	@Timed
	@RequestMapping(value = "/user-favourite-documents", method = RequestMethod.GET)
	public String getUserFavouriteDocuments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Favourite Documents");
		List<UserDTO> users = userService.findAllByCompany();
		model.addAttribute("users", users);
		return "company/userFavouriteDocuments";
	}

	@Timed
	@RequestMapping(value = "/user-favourite-documents/save", method = RequestMethod.POST)
	public ResponseEntity<Void> save(@RequestBody List<UserFavouriteDocumentDTO> userFavouriteDocuments) {
		log.debug("REST request to save assigned Favourite documents");
		userFavouriteDocumentService.save(userFavouriteDocuments);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/user-favourite-documents/activities/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ActivityDTO>> getUserActivityDocuments(@PathVariable String userPid) {
		log.debug("Web request to get get Favourite Documents by user pid : {}", userPid);
		return new ResponseEntity<>(userActivityService.findActivitiesByUserPid(userPid), HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/user-favourite-documents/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserFavouriteDocumentDTO>> getUserFavouriteDocuments(@PathVariable String userPid) {
		log.debug("Web request to get get Favourite Documents by user pid : {}", userPid);
		return new ResponseEntity<>(userFavouriteDocumentService.findFavouriteDocumentsByUserPid(userPid),
				HttpStatus.OK);
	}
}
