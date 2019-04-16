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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.TaskReferenceDocument;
import com.orderfleet.webapp.domain.UserTaskAssignment;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.PriorityStatus;
import com.orderfleet.webapp.repository.TaskReferenceDocumentRepository;
import com.orderfleet.webapp.repository.UserTaskAssignmentRepository;
import com.orderfleet.webapp.service.AccountingVoucherHeaderService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.web.rest.api.dto.TaskRefDocumentDTO;
import com.orderfleet.webapp.web.rest.api.dto.UserTaskDTO;

/**
 * REST controller for getting tasks assigned to a User.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
@RestController
@RequestMapping(value = "/api/user-tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@Transactional(readOnly = true)
public class UserTaskController {

	private final Logger log = LoggerFactory.getLogger(UserTaskController.class);

	@Inject
	private UserTaskAssignmentRepository userTaskAssignmentRepository;

	@Inject
	private TaskReferenceDocumentRepository taskReferenceDocumentRepository;

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherHeaderService;

	@Inject
	private AccountingVoucherHeaderService accountingVoucherHeaderService;

	@Inject
	private DynamicDocumentHeaderService dynamicDocumentHeaderService;

	/**
	 * GET /user-tasks : get all the userTasks.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of userTasks
	 *         in body
	 */
	@RequestMapping(method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<UserTaskDTO>> getAllAssignedUserTasks() {
		log.debug("REST request to get all userTasks");
		List<UserTaskAssignment> userTaskAssignments = userTaskAssignmentRepository.findUserTasksByUserIsCurrentUser();
		List<UserTaskDTO> result = userTaskAssignments.stream().map(uta -> {
			TaskReferenceDocument taskReferenceDocument = taskReferenceDocumentRepository
					.findByTaskId(uta.getTask().getId());
			if (taskReferenceDocument == null) {
				return new UserTaskDTO(uta, null);
			} else {
				return new UserTaskDTO(uta, taskReferenceDocument.getPid());
			}
		}).collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * GET /user-tasks : get all the userTasks by startDate.
	 *
	 * @param startDate
	 *            the date of the time period of userTasks to get
	 * @return the ResponseEntity with status 200 (OK) and the list of userTasks
	 *         in body
	 */
	@RequestMapping(method = RequestMethod.GET, params = "startDate")
	public ResponseEntity<List<UserTaskDTO>> getAssignedUserTaskByStartDate(
			@RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
		List<UserTaskAssignment> userTaskAssignments = userTaskAssignmentRepository.findByStartDate(startDate);
		List<UserTaskDTO> result = userTaskAssignments.stream().map(uta -> {
			TaskReferenceDocument taskReferenceDocument = taskReferenceDocumentRepository
					.findByTaskId(uta.getTask().getId());
			if (taskReferenceDocument == null) {
				return new UserTaskDTO(uta, null);
			} else {
				return new UserTaskDTO(uta, taskReferenceDocument.getPid());
			}
		}).collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * GET /user-tasks : get userTasks between the fromDate and toDate.
	 *
	 * @param fromDate
	 *            the start of the time period of userTasks to get
	 * @param toDate
	 *            the end of the time period of userTasks to get
	 * @return the ResponseEntity with status 200 (OK) and the list of userTasks
	 *         in body
	 */
	@RequestMapping(method = RequestMethod.GET, params = { "fromDate", "toDate" })
	public ResponseEntity<List<UserTaskDTO>> getAssignedUserTaskByDates(
			@RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		List<UserTaskAssignment> userTaskAssignments = userTaskAssignmentRepository.findByStartDateBetween(fromDate,
				toDate);
		List<UserTaskDTO> result = userTaskAssignments.stream().map(uta -> {
			TaskReferenceDocument taskReferenceDocument = taskReferenceDocumentRepository
					.findByTaskId(uta.getTask().getId());
			if (taskReferenceDocument == null) {
				return new UserTaskDTO(uta, null);
			} else {
				return new UserTaskDTO(uta, taskReferenceDocument.getPid());
			}
		}).collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * GET /user-tasks : get all userTasks by priorityStatus.
	 *
	 * @param priorityStatus
	 *            the priorityStatus of userTasks to get
	 * @return the ResponseEntity with status 200 (OK) and the list of userTasks
	 *         in body
	 */
	@RequestMapping(method = RequestMethod.GET, params = "priorityStatus")
	@Timed
	public ResponseEntity<List<UserTaskDTO>> getAssignedUserTaskByPriorotyStatus(
			@RequestParam(value = "priorityStatus") PriorityStatus priorityStatus) {
		List<UserTaskAssignment> userTaskAssignments = userTaskAssignmentRepository
				.findByPriorityStatus(priorityStatus);
		List<UserTaskDTO> result = userTaskAssignments.stream().map(uta -> {
			TaskReferenceDocument taskReferenceDocument = taskReferenceDocumentRepository
					.findByTaskId(uta.getTask().getId());
			if (taskReferenceDocument == null) {
				return new UserTaskDTO(uta, null);
			} else {
				return new UserTaskDTO(uta, taskReferenceDocument.getPid());
			}
		}).collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * GET /:pid : get the "pid" UserTask.
	 *
	 * @param pid
	 *            the pid of the UserTask to find
	 * @return the ResponseEntity with status 200 (OK) and with body the "pid"
	 *         UserTask, or with status 404 (Not Found)
	 */
	@GetMapping("/{pid}")
	@Timed
	public ResponseEntity<UserTaskDTO> getUserTaskByPid(@PathVariable String pid) {
		log.debug("REST request to get UserTask : {}", pid);
		return userTaskAssignmentRepository.findOneByPid(pid).map(uta -> {
			TaskReferenceDocument taskReferenceDocument = taskReferenceDocumentRepository
					.findByTaskId(uta.getTask().getId());
			if (taskReferenceDocument == null) {
				return new UserTaskDTO(uta, null);
			} else {
				return new UserTaskDTO(uta, taskReferenceDocument.getPid());
			}
		}).map(ut -> new ResponseEntity<>(ut, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

	}

	/**
	 * GET /task-ref-documents : get User Task Assignment This api is called
	 * when an executive click on the task-notification send from server
	 *
	 * @param pid
	 *            the pid of taskRef Document to get
	 * @return the ResponseEntity with status 200 (OK) and the list of userTasks
	 *         in body
	 */
	@Timed
	@RequestMapping(value = "/task-ref-document", method = RequestMethod.GET, params = "pid")
	public ResponseEntity<TaskRefDocumentDTO> getAssignedUserTaskByTaskRefDocument(
			@RequestParam(value = "pid") String taskRefDocumentPid) {

		TaskReferenceDocument taskReferenceDocument = taskReferenceDocumentRepository.findByPid(taskRefDocumentPid);
		if (taskReferenceDocument == null) {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}

		TaskRefDocumentDTO taskRefDocumentDTO = new TaskRefDocumentDTO();
		if (taskReferenceDocument.getRefDocument().getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
			taskRefDocumentDTO.setInventoryVoucher(
					inventoryVoucherHeaderService.findOneByPid(taskReferenceDocument.getRefTransactionPid()).get());
		} else if (taskReferenceDocument.getRefDocument().getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
			taskRefDocumentDTO.setAccountingVoucher(
					accountingVoucherHeaderService.findOneByPid(taskReferenceDocument.getRefTransactionPid()).get());
		} else if (taskReferenceDocument.getRefDocument().getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
			taskRefDocumentDTO.setDynamicDocument(
					dynamicDocumentHeaderService.findOneByPid(taskReferenceDocument.getRefTransactionPid()).get());
		}
		List<UserTaskAssignment> userTaskAssignments = userTaskAssignmentRepository
				.findUserTasksByCurrentUserAndTaskPid(taskReferenceDocument.getTask().getPid());
		List<UserTaskDTO> userTaskList = userTaskAssignments.stream().map(UserTaskDTO::new)
				.collect(Collectors.toList());
		taskRefDocumentDTO.setUserTaskList(userTaskList);
		return new ResponseEntity<>(taskRefDocumentDTO, HttpStatus.OK);
	}

}
