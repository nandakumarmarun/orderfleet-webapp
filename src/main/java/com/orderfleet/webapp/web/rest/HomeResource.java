package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountGroupAccountProfileService;
import com.orderfleet.webapp.service.AccountGroupService;
import com.orderfleet.webapp.web.rest.dto.AccountGroupDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Web controller for managing AccountGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Controller
@RequestMapping("/web")
public class HomeResource {

	private final Logger log = LoggerFactory.getLogger(HomeResource.class);

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountGroups(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of home");

		return "company/home";
	}


}
