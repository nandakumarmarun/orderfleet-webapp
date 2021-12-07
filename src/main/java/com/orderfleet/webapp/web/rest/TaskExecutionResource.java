package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;
import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentAccountingVoucherColumnService;
import com.orderfleet.webapp.service.DocumentInventoryVoucherColumnService;
import com.orderfleet.webapp.service.ExecutiveTaskPlanService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherColumnDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherColumnDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Web controller for managing TaskExecution.
 * 
 * @author Shaheer
 * @since May 07, 2016
 */
@Controller
@RequestMapping("/web")
public class TaskExecutionResource {

	private final Logger log = LoggerFactory.getLogger(TaskExecutionResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ExecutiveTaskPlanService executiveTaskPlanService;

	@Inject
	private UserDocumentService userDocumentService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private DocumentInventoryVoucherColumnService documentInventoryVoucherColumnService;

	@Inject
	private DocumentAccountingVoucherColumnService documentAccountingVoucherColumnService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	/**
	 * GET /task-execution : get executive task plans.
	 *
	 * the pagination information
	 * 
	 * @return the ResponseEntity with status 200 (OK) and the list of executive
	 *         task plan in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/task-execution", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String taskExecution(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of task execution");
		model.addAttribute("executiveTaskPlans", executiveTaskPlanService.findByUserIsCurrentUser());
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_109" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId and accountType";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		model.addAttribute("byAccounts", accountProfileRepository.findAllByCompanyIdAndAccountType("By Accounts"));
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);

		model.addAttribute("toAccounts", accountProfileRepository.findAllByCompanyIdAndAccountType("To Accounts"));
		return "company/taskExecution";
	}

	/**
	 * GET /task-execution/user-documents : get current User Documents.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         Documents list, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-execution/user-documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getUserDocuments() {
		log.debug("Web request to get current User Documents");
		List<DocumentDTO> documents = userDocumentService.findDocumentsByUserIsCurrentUser();
		return new ResponseEntity<>(documents, HttpStatus.OK);
	}

	/**
	 * GET /task-execution/products : get products.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         products, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-execution/products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getProducts() {
		log.debug("Web request to get products");
		List<ProductProfileDTO> products = productProfileService.findAllByCompany();
		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	/**
	 * GET /task-execution/documentInventoryVoucherColumns/:documentPid : get
	 * inventoryVoucherColumns.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         inventoryVoucherColumnDTOs, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-execution/documentInventoryVoucherColumns/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<InventoryVoucherColumnDTO>> getDocumentInventoryVoucherColumns(
			@PathVariable String documentPid) {
		log.debug("Web request to get documentInventoryVoucherColumns");
		List<DocumentInventoryVoucherColumn> documentInventoryVoucherColumns = documentInventoryVoucherColumnService
				.findByDocumentPid(documentPid);
		List<InventoryVoucherColumnDTO> inventoryVoucherColumnDTOs = documentInventoryVoucherColumns.stream()
				.map(InventoryVoucherColumnDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(inventoryVoucherColumnDTOs, HttpStatus.OK);
	}

	/**
	 * GET /task-execution/documentAccountingVoucherColumns/:documentPid : get
	 * accountingVoucherColumns.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         accountingVoucherColumnDTOs, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-execution/documentAccountingVoucherColumns/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountingVoucherColumnDTO>> getDocumentAccountingVoucherColumns(
			@PathVariable String documentPid) {
		log.debug("Web request to get documentAccountingVoucherColumns");
		List<DocumentAccountingVoucherColumn> documentAccountingVoucherColumns = documentAccountingVoucherColumnService
				.findByDocumentPid(documentPid);
		List<AccountingVoucherColumnDTO> accountingVoucherColumnDTOs = documentAccountingVoucherColumns.stream()
				.map(AccountingVoucherColumnDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(accountingVoucherColumnDTOs, HttpStatus.OK);
	}

}
