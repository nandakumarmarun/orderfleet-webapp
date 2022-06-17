package com.orderfleet.webapp.web.rest.api;

import java.io.IOException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.InventoryVoucherHeaderHistory;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.InventoryVoucherBatchDetailService;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.web.rest.api.dto.InvoiceDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

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
	private FileManagerService fileManagerService;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

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

		String docPid = "DOC-KKcTOuI90C1522998462406";
		String documentPid = "DOC-nx6mcvKb9G1653966130937";
		List<InvoiceDTO> invoiceDto = new ArrayList<>();

		List<String> accountPids = new ArrayList<>();

		if (accountPid != null && !accountPid.equals("")) {
			accountPids.add(accountPid);
		} else {

			accountPids = accountProfileRepository.findAllPidsByCompany();
		}

		List<AccountProfile> accountprofile = accountProfileRepository.findByPidIn(accountPids);

		List<Long> Ids = accountprofile.stream().map(acc -> acc.getId()).collect(Collectors.toList());

		List<ExecutiveTaskExecution> executiveTaskExecutionsObject = new ArrayList<>();

		executiveTaskExecutionsObject = executiveTaskExecutionRepository.getByAccountIdIn(Ids);

		Set<Long> exeIds = executiveTaskExecutionsObject.stream().map(ext -> ext.getId()).collect(Collectors.toSet());

		List<Object[]> inventoryVouchers = new ArrayList<>();
		List<InventoryVoucherHeader> deliveryVoucher = new ArrayList<>();
		if (exeIds.size() > 0) {

			inventoryVouchers = inventoryVoucherHeaderRepository.findByExecutiveTaskExecutionsIdInAndDocumentPid(exeIds,
					docPid);
			deliveryVoucher = inventoryVoucherHeaderRepository.findByExecutiveTaskExecutionIdInAndDocumentsPid(exeIds,
					documentPid);
		}

		List<Long> ivId = deliveryVoucher.stream().map(dv -> dv.getId()).collect(Collectors.toList());
		List<InventoryVoucherDetail> invDetail = new ArrayList<>();
		if (ivId.size() > 0) {
			invDetail = inventoryVoucherDetailRepository.findByInventoryHeaderIdIn(ivId);
		}
		for (InventoryVoucherDetail obj1 : invDetail) {
			Object[] ivh = inventoryVouchers.stream()
					.filter(abc -> abc[0].toString().equals(obj1.getReferenceInvoiceNo())).findAny().get();
			inventoryVouchers.remove(ivh);
		}

		List<Object[]> inventory = inventoryVouchers.stream().limit(5).collect(Collectors.toList());
		for (ExecutiveTaskExecution executive : executiveTaskExecutionsObject) {

			double totalSalesOrderAmount = 0.0;
			InvoiceDTO invoicedto = new InvoiceDTO();
			invoicedto.setInvoiceDate(executive.getDate());
			for (Object[] obj : inventory) {
				if (obj[1].toString().equalsIgnoreCase(executive.getPid())) {

					invoicedto.setInvoiceNo(obj[0].toString());
					if (obj[3].toString().equalsIgnoreCase("INVENTORY_VOUCHER")) {

						totalSalesOrderAmount += Double.valueOf(obj[2].toString());
						invoicedto.setDocumentTotal(totalSalesOrderAmount);
						invoiceDto.add(invoicedto);
					}
				}

			}
		}
		return new ResponseEntity<>(invoiceDto, HttpStatus.OK);

	}

	@Transactional
	@RequestMapping(value = "/upload/invoiceimage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> uploadAttendanceImageFile(@RequestParam("imageRefNo") String imageRefNo,
			@RequestParam("file") MultipartFile file) {
		log.debug("Request Inventory Image to upload a file : {}", file);
		if (file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}

		ResponseEntity<Object> upload = inventoryVoucherHeaderRepository.findOneByImageRefNo(imageRefNo)
				.map(invoice -> {
					try {
						File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
								file.getOriginalFilename(), file.getContentType());
						// update filledForm with file
						invoice.getFiles().add(uploadedFile);
						inventoryVoucherHeaderRepository.save(invoice);
						log.debug("uploaded file for : {}", invoice);
						return new ResponseEntity<>(HttpStatus.OK);
					} catch (FileManagerException | IOException ex) {
						log.debug("File upload exception : {}", ex.getMessage());
						return ResponseEntity.badRequest()
								.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage()))
								.body(null);
					}
				})
				.orElse(ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("fileUpload", "formNotExists", "image not found"))
						.body(null));
		return upload;
	}
}
