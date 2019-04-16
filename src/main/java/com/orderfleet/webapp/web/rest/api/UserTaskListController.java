package com.orderfleet.webapp.web.rest.api;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.UserTaskListAssignment;
import com.orderfleet.webapp.domain.enums.PriorityStatus;
import com.orderfleet.webapp.repository.UserTaskListAssignmentRepository;
import com.orderfleet.webapp.web.rest.api.dto.UserTaskListDTO;

/**
 * REST controller for managing executive's assigned taskLists.
 * 
 * @author Sarath
 * @since July 15, 2016
 */
@RestController
@RequestMapping(value = "/api/user-task-lists", produces = MediaType.APPLICATION_JSON_VALUE)
@Transactional(readOnly = true)
public class UserTaskListController {

	private final Logger log = LoggerFactory.getLogger(UserTaskListController.class);

	@Inject
	private UserTaskListAssignmentRepository userTaskListAssignmentRepository;

	/**
	 * GET /user-task-lists : get all the userTaskLists.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userTaskLists in body
	 */
	@RequestMapping(method = RequestMethod.GET)
	@Timed
	public List<UserTaskListDTO> getAllAssignedUserTaskLists() {
		log.debug("REST request to get all userTaskLists");
		List<UserTaskListAssignment> userTaskListAssignments = userTaskListAssignmentRepository
				.findByUserIsCurrentUser();
		List<UserTaskListDTO> result = userTaskListAssignments.stream().map(UserTaskListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * GET /user-task-lists : get all the userTasks by startDate.
	 *
	 * @param startDate
	 *            the date of the time period of userTaskLists to get
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userTaskLists in body
	 */
	@RequestMapping(method = RequestMethod.GET, params = "startDate")
	@Timed
	public ResponseEntity<List<UserTaskListDTO>> getUserTaskByStartDate(
			@RequestParam(required = true, value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
		List<UserTaskListAssignment> userTaskListAssignments = userTaskListAssignmentRepository
				.findByStartDate(startDate);
		List<UserTaskListDTO> result = userTaskListAssignments.stream().map(UserTaskListDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * GET /user-task-lists : get userTasks between the fromDate and toDate.
	 *
	 * @param fromDate
	 *            the start of the time period of userTaskLists to get
	 * @param toDate
	 *            the end of the time period of userTaskLists to get
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userTaskLists in body
	 */
	@RequestMapping(method = RequestMethod.GET, params = { "fromDate", "toDate" })
	@Timed
	public ResponseEntity<List<UserTaskListDTO>> getUserTaskByDates(
			@RequestParam(required = true, value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

		List<UserTaskListAssignment> userTaskListAssignments = userTaskListAssignmentRepository.findByStartDateBetween(
				fromDate, toDate);
		List<UserTaskListDTO> result = userTaskListAssignments.stream().map(UserTaskListDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * GET /user-task-lists: get all userTaskLists by priorityStatus.
	 *
	 * @param priorityStatus
	 *            the priorityStatus of userTaskLists to get
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userTaskLists in body
	 */
	@RequestMapping(method = RequestMethod.GET, params = "priorityStatus")
	@Timed
	public ResponseEntity<List<UserTaskListDTO>> getUserTaskByPriorotyStatus(
			@RequestParam(value = "priorityStatus") PriorityStatus priorityStatus) {
		List<UserTaskListAssignment> userTaskListAssignments = userTaskListAssignmentRepository
				.findByPriorityStatus(priorityStatus);
		List<UserTaskListDTO> result = userTaskListAssignments.stream().map(UserTaskListDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
