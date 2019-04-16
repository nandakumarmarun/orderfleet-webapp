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
import com.orderfleet.webapp.domain.UserTaskGroupAssignment;
import com.orderfleet.webapp.domain.enums.PriorityStatus;
import com.orderfleet.webapp.repository.UserTaskGroupAssignmentRepository;
import com.orderfleet.webapp.web.rest.api.dto.UserTaskGroupDTO;

/**
 * REST controller for managing executive's assigned taskGroups.
 * 
 * @author Shaheer
 * @since July 02, 2016
 */
@RestController
@RequestMapping(value = "/api/user-task-groups", produces = MediaType.APPLICATION_JSON_VALUE)
@Transactional(readOnly = true)
public class UserTaskGroupController {
	
	private final Logger log = LoggerFactory.getLogger(UserTaskGroupController.class);
	
	@Inject
	private UserTaskGroupAssignmentRepository userTaskGroupAssignmentRepository;
	
	/**
	 * GET /user-task-groups : get all the userTaskGroups.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userTaskGroups in body
	 */
	@RequestMapping(method = RequestMethod.GET)
	@Timed
	public List<UserTaskGroupDTO> getAllAssignedUserTaskGroups() {
		log.debug("REST request to get all userTaskGroups");
		List<UserTaskGroupAssignment> userTaskGroupAssignments = userTaskGroupAssignmentRepository.findByUserIsCurrentUser();
		List<UserTaskGroupDTO> result = userTaskGroupAssignments.stream().map(UserTaskGroupDTO::new).collect(Collectors.toList());
		return result;
	}
	
	/**
	 * GET /user-task-groups : get all the userTasks by startDate.
	 *
	 * @param startDate the date of the time period of userTaskGroups to get
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userTaskGroups in body
	 */
	@RequestMapping(method = RequestMethod.GET, params = "startDate")
	@Timed
	public ResponseEntity<List<UserTaskGroupDTO>> getUserTaskByStartDate(@RequestParam(required= true ,value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
		List<UserTaskGroupAssignment> userTaskGroupAssignments = userTaskGroupAssignmentRepository.findByStartDate(startDate);
		List<UserTaskGroupDTO> result = userTaskGroupAssignments.stream().map(UserTaskGroupDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	 /**
     * GET  /user-task-groups : get userTasks between the fromDate and toDate.
     *
     * @param fromDate the start of the time period of userTaskGroups to get
     * @param toDate the end of the time period of userTaskGroups to get
     * @return the ResponseEntity with status 200 (OK) and the list of userTaskGroups in body
     */
    @RequestMapping(method = RequestMethod.GET,params = {"fromDate", "toDate"})
    @Timed
    public ResponseEntity<List<UserTaskGroupDTO>> getUserTaskByDates(@RequestParam(required= true ,value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
    	
    	List<UserTaskGroupAssignment> userTaskGroupAssignments = userTaskGroupAssignmentRepository.findByStartDateBetween(fromDate, toDate);
    	List<UserTaskGroupDTO> result = userTaskGroupAssignments.stream().map(UserTaskGroupDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
	/**
	 * GET /user-task-groups: get all userTaskGroups by priorityStatus.
	 *
	 * @param priorityStatus the priorityStatus of userTaskGroups to get
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userTaskGroups in body
	 */
	@RequestMapping(method = RequestMethod.GET, params = "priorityStatus")
	@Timed
	public ResponseEntity<List<UserTaskGroupDTO>> getUserTaskByPriorotyStatus(@RequestParam(value = "priorityStatus") PriorityStatus priorityStatus) {
		List<UserTaskGroupAssignment> userTaskGroupAssignments = userTaskGroupAssignmentRepository.findByPriorityStatus(priorityStatus);
		List<UserTaskGroupDTO> result = userTaskGroupAssignments.stream().map(UserTaskGroupDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
