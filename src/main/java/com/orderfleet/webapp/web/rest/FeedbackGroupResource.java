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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.FeedbackGroupFormElement;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.GuidedSellingConfig;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.FeedbackElementType;
import com.orderfleet.webapp.repository.DocumentFormsRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.GuidedSellingConfigRepository;
import com.orderfleet.webapp.repository.UserFeedbackGroupRepository;
import com.orderfleet.webapp.service.FeedbackGroupFormElementService;
import com.orderfleet.webapp.service.FeedbackGroupService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.FeedbackGroupDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing FeedbackGroup.
 * 
 * @author Muhammed Riyas T
 * @since Feb 11, 2017
 */
@Controller
@RequestMapping("/web")
public class FeedbackGroupResource {

	private final Logger log = LoggerFactory.getLogger(FeedbackGroupResource.class);

	@Inject
	private FeedbackGroupService feedbackGroupService;

	@Inject
	private FeedbackGroupFormElementService feedbackGroupFormElementService;

	@Inject
	private GuidedSellingConfigRepository guidedSellingConfigRepository;

	@Inject
	private DocumentFormsRepository documentFormsRepository;

	@Inject
	private FormFormElementRepository formFormElementRepository;

	@Inject
	private UserFeedbackGroupRepository feedbackGroupRepository;

	@Inject
	private UserService userService;

	/**
	 * GET /feedbackGroups : get all the feedbackGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         feedbackGroups in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/feedbackGroups", method = RequestMethod.GET)
	public String getAllFeedbackGroups(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of FeedbackGroups");
		model.addAttribute("feedbackGroups", feedbackGroupService.findAllByCompany());
		model.addAttribute("formElements", getFormElements());
		model.addAttribute("users", userService.findAllByCompany());
		return "company/feedbackGroups";
	}

	/**
	 * POST /feedbackGroups : Create a new feedbackGroup.
	 *
	 * @param feedbackGroupDTO
	 *            the feedbackGroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new feedbackGroupDTO, or with status 400 (Bad Request) if the
	 *         feedbackGroup has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/feedbackGroups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FeedbackGroupDTO> createFeedbackGroup(@Valid @RequestBody FeedbackGroupDTO feedbackGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to save FeedbackGroup : {}", feedbackGroupDTO);
		if (feedbackGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("feedbackGroup", "idexists",
					"A new feedbackGroup cannot already have an ID")).body(null);
		}
		if (feedbackGroupService.findByName(feedbackGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("feedbackGroup", "nameexists", "Feedback Group already in use"))
					.body(null);
		}
		FeedbackGroupDTO result = feedbackGroupService.save(feedbackGroupDTO);
		return ResponseEntity.created(new URI("/web/feedbackGroups/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("feedbackGroup", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /feedbackGroups : Updates an existing feedbackGroup.
	 *
	 * @param feedbackGroupDTO
	 *            the feedbackGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         feedbackGroupDTO, or with status 400 (Bad Request) if the
	 *         feedbackGroupDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the feedbackGroupDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/feedbackGroups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FeedbackGroupDTO> updateFeedbackGroup(@Valid @RequestBody FeedbackGroupDTO feedbackGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to update FeedbackGroup : {}", feedbackGroupDTO);
		if (feedbackGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("feedbackGroup", "idNotexists", "Feedback Group must have an ID"))
					.body(null);
		}
		Optional<FeedbackGroupDTO> existingFeedbackGroup = feedbackGroupService.findByName(feedbackGroupDTO.getName());
		if (existingFeedbackGroup.isPresent()
				&& (!existingFeedbackGroup.get().getPid().equals(feedbackGroupDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("feedbackGroup", "nameexists", "Feedback Group already in use"))
					.body(null);
		}
		FeedbackGroupDTO result = feedbackGroupService.update(feedbackGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("feedbackGroup", "idNotexists", "Invalid account type ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("feedbackGroup", feedbackGroupDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /feedbackGroups/:id : get the "id" feedbackGroup.
	 *
	 * @param id
	 *            the id of the feedbackGroupDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         feedbackGroupDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/feedbackGroups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FeedbackGroupDTO> getFeedbackGroup(@PathVariable String pid) {
		log.debug("Web request to get FeedbackGroup by pid : {}", pid);
		return feedbackGroupService.findOneByPid(pid)
				.map(feedbackGroupDTO -> new ResponseEntity<>(feedbackGroupDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /feedbackGroups/:id : delete the "id" feedbackGroup.
	 *
	 * @param id
	 *            the id of the feedbackGroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/feedbackGroups/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteFeedbackGroup(@PathVariable String pid) {
		log.debug("REST request to delete FeedbackGroup : {}", pid);
		feedbackGroupService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("feedbackGroup", pid.toString()))
				.build();
	}

	@Timed
	@RequestMapping(value = "/feedbackGroups/assignElements", method = RequestMethod.POST)
	public ResponseEntity<Void> saveAssignedElements(@RequestParam String feedbackGroupPid,
			@RequestParam String assignedElements, @RequestParam FeedbackElementType type) {
		log.debug("REST request to save assigned elements: {}", feedbackGroupPid);
		// check element already used
		String formElementPids[] = assignedElements.split(",");
		for (String formElementPid : formElementPids) {
			FeedbackGroupFormElement feedbackGroupFormElement = feedbackGroupFormElementService
					.findByFormElementPid(formElementPid);
			if (feedbackGroupFormElement != null) {
				if (!feedbackGroupFormElement.getFeedbackGroup().getPid().equals(feedbackGroupPid)
						|| !feedbackGroupFormElement.getFeedbackElementType().equals(type)) {
					return ResponseEntity.badRequest()
							.headers(
									HeaderUtil.createFailureAlert("feedbackGroup", "formElement",
											"Already used element '"
													+ feedbackGroupFormElement.getFormElement().getName() + "'"))
							.body(null);
				}
			}
		}
		feedbackGroupFormElementService.save(feedbackGroupPid, assignedElements, type);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/feedbackGroups/findElements/{pid}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FormElementDTO>> getElements(@PathVariable String pid,
			@PathVariable FeedbackElementType type) {
		log.debug("REST request to get Elements by feedbackGroupPid : {}", pid);
		List<FormElementDTO> formElementDTOs = feedbackGroupFormElementService.findFormElementsByGroupPidAnd(pid, type);
		return new ResponseEntity<>(formElementDTOs, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/feedbackGroups/assignUsers", method = RequestMethod.POST)
	public ResponseEntity<Void> saveAssignedUsers(@RequestParam String feedbackGroupPid,
			@RequestParam String assignedUsers) {
		log.debug("REST request to save assigned  users: {}", feedbackGroupPid);
		feedbackGroupFormElementService.save(feedbackGroupPid, assignedUsers);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/feedbackGroups/setStatusField", method = RequestMethod.POST)
	public ResponseEntity<Void> saveStatusField(@RequestParam String feedbackGroupPid,
			@RequestParam String statusField) {
		log.debug("REST request to save statusField : {}", feedbackGroupPid);
		feedbackGroupFormElementService.saveStatusField(feedbackGroupPid, statusField);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/feedbackGroups/findUsers/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserDTO>> getUsers(@PathVariable String pid) {
		log.debug("REST request to get Elements by feedbackGroupPid : {}", pid);
		List<User> users = feedbackGroupRepository.findUsersByFeedbackGroupPid(pid);
		List<UserDTO> userDTOs = users.stream().map(UserDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(userDTOs, HttpStatus.OK);
	}

	private List<FormElementDTO> getFormElements() {
		GuidedSellingConfig guidedSellingConfig = guidedSellingConfigRepository.findByCompanyId();
		List<FormElementDTO> formElementDTOs = new ArrayList<>();
		if (guidedSellingConfig != null && guidedSellingConfig.getGuidedSellingInfoDocument() != null) {
			List<Form> formList = documentFormsRepository
					.findFormsByDocumentPid(guidedSellingConfig.getGuidedSellingInfoDocument().getPid());
			if (formList.size() > 0) {
				List<FormElement> formElements = formFormElementRepository
						.findFormElementsByCompanyIdAndFormsIn(formList);
				formElementDTOs = formElements.stream().map(FormElementDTO::new).collect(Collectors.toList());
			}
			return formElementDTOs;
		}
		return null;
	}
}
