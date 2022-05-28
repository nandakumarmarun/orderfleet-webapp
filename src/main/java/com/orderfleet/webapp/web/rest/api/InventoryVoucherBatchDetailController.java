package com.orderfleet.webapp.web.rest.api;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherHeaderHistory;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.InventoryVoucherBatchDetailService;
import com.orderfleet.webapp.web.rest.api.dto.InvoiceDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;

import javassist.expr.NewArray;

/**
 * REST controller for getting tasks assigned to a User.
 * 
 * @author Sarath
 * @since Dec 7, 2016
 */
@RestController
@RequestMapping("/api")
public class InventoryVoucherBatchDetailController {

	@Inject
	private InventoryVoucherBatchDetailService inventoryVoucherBatchDetailService;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private UserRepository userRepository;

	private final Logger log = LoggerFactory.getLogger(InventoryVoucherBatchDetailController.class);

	/**
	 * POST /inventory-voucher-batch-detail : Create a new
	 * inventoryVoucherBatchDetail.
	 *
	 * @param inventoryVoucherBatchDetailDTO the inventoryVoucherBatchDetailDTO to
	 *                                       create
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/inventory-voucher-batch-detail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void executiveTaskSubmission(
			@Valid @RequestBody InventoryVoucherBatchDetailDTO inventoryVoucherBatchDetailDTO)
			throws URISyntaxException {
		log.debug("Web request to save ExecutiveTaskExecution");
		inventoryVoucherBatchDetailService.save(inventoryVoucherBatchDetailDTO);
	}

	@RequestMapping(value = "/invoice-details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<InvoiceDTO>> sendInviceDetails(@RequestParam(required = false) String accountPid) {
		log.debug("API request to fetch Customers Invoice Details (sales)");

		List<InvoiceDTO> invoiceDto = new ArrayList<>();

		List<String> accountPids = new ArrayList<>();

		if (accountPid != null && !accountPid.equals("")) {
			accountPids.add(accountPid);
		} else {

			accountPids = accountProfileRepository.findAllPidsByCompany();
		}
		log.info("Account Profile Size =" + accountPids.size());

		List<AccountProfile> accountprofile = accountProfileRepository.findByPidIn(accountPids);

		List<Long> Ids = accountprofile.stream().map(acc -> acc.getId()).collect(Collectors.toList());

		List<ExecutiveTaskExecution> executiveTaskExecutionsObject = new ArrayList<>();
		executiveTaskExecutionsObject = executiveTaskExecutionRepository.getByAccountIdIn(Ids);

		List<ExecutiveTaskExecution> executiveTask = executiveTaskExecutionsObject.stream().limit(5)
				.collect(Collectors.toList());

		Set<Long> exeIds = executiveTask.stream().map(ext -> ext.getId()).collect(Collectors.toSet());

		List<Object[]> inventoryVouchers = new ArrayList<>();
		if (exeIds.size() > 0) {

			inventoryVouchers = inventoryVoucherHeaderRepository.findByExecutiveTaskExecutionsIdIn(exeIds);
		}

		for (ExecutiveTaskExecution executive : executiveTask) {
			System.out.println("enterd in to loop");
			double totalSalesOrderAmount = 0.0;
			InvoiceDTO invoicedto = new InvoiceDTO();
			invoicedto.setInvoiceDate(executive.getSendDate());
			for (Object[] obj : inventoryVouchers) {

				if (obj[1].toString().equalsIgnoreCase(executive.getPid())) {
					invoicedto.setInvoiceNo(obj[0].toString());
					if (obj[3].toString().equalsIgnoreCase("INVENTORY_VOUCHER")) {

						totalSalesOrderAmount += Double.valueOf(obj[2].toString());
					}
				}
				invoicedto.setDocumentTotal(totalSalesOrderAmount);

			}
			invoiceDto.add(invoicedto);
		}
		return new ResponseEntity<>(invoiceDto, HttpStatus.OK);

	}
}
