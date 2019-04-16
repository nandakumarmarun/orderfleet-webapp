package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.AccountingVoucherHeaderHistory;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.StaticFormJSCode;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderHistoryRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.DocumentAccountTypeRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.StaticFormJSCodeRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentAccountingVoucherColumnService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskSubmissionService;
import com.orderfleet.webapp.service.IncomeExpenseHeadService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.ReferenceDocumentService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.async.TaskSubmissionPostSave;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.api.dto.TaskSubmissionResponse;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherColumnDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExevTaskExenDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.dto.ReceiverSupplierAccountDTO;
import com.orderfleet.webapp.web.rest.dto.StaticFormJSCodeDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.rest.mapper.StaticFormJSCodeMapper;

/**
 * Web controller for managing Accounting Voucher.
 * 
 * @author Muhammed Riyas T
 * @since Feb 28, 2017
 */
@Controller
@RequestMapping("/web/accounting-voucher-transaction")
public class AccountingVoucherTransactionResource {

	private final Logger log = LoggerFactory.getLogger(AccountingVoucherTransactionResource.class);

	@Inject
	private UserActivityService userActivityService;

	@Inject
	private ExecutiveTaskSubmissionService executiveTaskSubmissionService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private DocumentAccountingVoucherColumnService documentAccountingVoucherColumnService;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderHistoryRepository accountingVoucherHeaderHistoryRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentAccountTypeRepository documentAccountTypeRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;
	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private StaticFormJSCodeRepository staticFormJSCodesRepository;

	@Inject
	private StaticFormJSCodeMapper staticFormJSCodeMapper;

	@Inject
	private ReferenceDocumentService referenceDocumentService;

	@Inject
	private IncomeExpenseHeadService incomeExpenseHeadService;

	@Inject
	private ReceivablePayableService receivablePayableService;
	
	@Inject
	private TaskSubmissionPostSave taskSubmissionPostSave;
	
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;
	
	

	/**
	 * GET /accounting-voucher-transaction
	 *
	 * @param pageable
	 *            the pagination information
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(method = RequestMethod.GET)
	public String accountingVoucherTransaction(Model model) {
		log.debug("Web request to get a page of Accounting Voucher Transaction");
		model.addAttribute("activityDocuments", getUserAcivityDocuments());
		model.addAttribute("employees", employeeProfileService.findAllByCompany());
		model.addAttribute("incomeExpenseHeads", incomeExpenseHeadService.findAllByCompany());
		EmployeeProfileDTO employeeProfile = employeeProfileService
				.findEmployeeProfileByUserLogin(SecurityUtils.getCurrentUserLogin());
		if (employeeProfile != null) {
			model.addAttribute("currentEmployeePid", employeeProfile.getPid());
		} else {
			model.addAttribute("currentEmployeePid", "no");
		}
		return "company/accountingVoucherTransaction";
	}

	@Timed
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> accountingVoucher(
			@RequestBody ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		log.debug("Web request to save dynamic documents");
		TaskSubmissionResponse taskSubmissionResponse = null;
		if (executiveTaskSubmissionDTO.getAccountingVouchers().get(0).getPid() == null) {
			// save
			LocalDateTime localDateTime = LocalDateTime.now();
			executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().setDate(localDateTime);
			executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().setLocationType(LocationType.NoLocation);
			executiveTaskSubmissionDTO.getAccountingVouchers().get(0).setDocumentDate(localDateTime);
			// create and set Document Number Local
			String documentNumberLocal = createDocumentNumber(
					executiveTaskSubmissionDTO.getAccountingVouchers().get(0).getDocumentPid());
			executiveTaskSubmissionDTO.getAccountingVouchers().get(0).setDocumentNumberLocal(documentNumberLocal);
			ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper = executiveTaskSubmissionService.executiveTaskSubmission(executiveTaskSubmissionDTO);
			if(tsTransactionWrapper != null) {
				taskSubmissionResponse = tsTransactionWrapper.getTaskSubmissionResponse();
				taskSubmissionPostSave.doPostSaveExecutivetaskSubmission(tsTransactionWrapper,executiveTaskSubmissionDTO);
			}
		} else {
			// update
			taskSubmissionResponse = executiveTaskSubmissionService.updateAccountingVoucher(executiveTaskSubmissionDTO);
		}
		return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.OK);
	}

	private String createDocumentNumber(String documentPid) {
		Optional<Document> document = documentRepository.findOneByPid(documentPid);
		String documentNumber = System.currentTimeMillis() + "_" + SecurityUtils.getCurrentUserLogin() + "_"
				+ document.get().getDocumentPrefix();
		// find previous document number
		AccountingVoucherHeader accountingVoucherHeader = accountingVoucherHeaderRepository
				.findTop1ByCreatedByLoginOrderByCreatedDateDesc(SecurityUtils.getCurrentUserLogin());

		if (accountingVoucherHeader != null) {
			String[] arr = accountingVoucherHeader.getDocumentNumberLocal().split("_");
			int i = Integer.valueOf(arr[arr.length - 1]) + 1;
			documentNumber += "_" + i;
		} else {
			documentNumber += "_0";
		}
		return documentNumber;
	}

	private List<DocumentDTO> getUserAcivityDocuments() {
		List<DocumentDTO> documents = new ArrayList<>();
		List<ActivityDTO> activityDTOs = userActivityService.findActivitiesByUserIsCurrentUser();
		for (ActivityDTO activityDTO : activityDTOs) {
			for (DocumentDTO documentDTO : activityDTO.getDocuments()) {
				if (documentDTO.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
					documentDTO.setPid(documentDTO.getPid() + "~" + activityDTO.getPid());
					documentDTO.setName(documentDTO.getName() + " --- [" + activityDTO.getName() + "]");
					documents.add(documentDTO);
				}
			}
		}
		return documents;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<AccountingVoucherHeaderDTO> search(@RequestParam("documentPid") String documentPid,
			@RequestParam("activityPid") String activityPid, @RequestParam("accountPid") String accountPid) {
		log.debug("Web request to  search accounting vouchers");
		return getAccountingVouchers(accountPid, documentPid);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/{accountingVoucherPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody AccountingVoucherHeaderDTO getAccountingVoucher(
			@PathVariable("accountingVoucherPid") String accountingVoucherPid) {
		AccountingVoucherHeader accountingVoucherHeader = accountingVoucherHeaderRepository
				.findOneByPid(accountingVoucherPid).get();
		AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO(accountingVoucherHeader);
		accountingVoucherHeaderDTO.setAccountingVoucherDetails(accountingVoucherHeader.getAccountingVoucherDetails()
				.stream().map(AccountingVoucherDetailDTO::new).collect(Collectors.toList()));
		return accountingVoucherHeaderDTO;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/product-details/{productPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ProductProfileDTO getProductDetails(@PathVariable("productPid") String productPid) {
		return productProfileService.findOneByPid(productPid).get();
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/previous-document-number", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String getPreviousDocumentNumber() {
		AccountingVoucherHeader accountingVoucherHeader = accountingVoucherHeaderRepository
				.findTop1ByCreatedByLoginOrderByCreatedDateDesc(SecurityUtils.getCurrentUserLogin());
		if (accountingVoucherHeader != null) {
			return accountingVoucherHeader.getDocumentNumberLocal();
		}
		return "nothing";
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/receivables/{accountPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<ReceivablePayableDTO> getBatchs(@PathVariable("accountPid") String accountPid) {
		log.debug("REST request to get product batch numbers");
		return receivablePayableService
				.findAllByAccountProfilePidAndReceivablePayableType(accountPid, ReceivablePayableType.Receivable);
	}

	private List<AccountingVoucherHeaderDTO> getAccountingVouchers(String accountPid, String documentPid) {
		List<AccountingVoucherHeaderDTO> accountingVouchers = new ArrayList<>();
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findByExecutiveTaskExecutionAccountProfilePidAndDocumentPid(accountPid, documentPid);
		for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
			AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO(
					accountingVoucherHeader);
			// find history
			List<AccountingVoucherHeaderHistory> accountingVoucherHeaderHistories = accountingVoucherHeaderHistoryRepository
					.findByPidOrderByIdDesc(accountingVoucherHeader.getPid());
			if (!accountingVoucherHeaderHistories.isEmpty()) {
				List<AccountingVoucherHeaderDTO> history = accountingVoucherHeaderHistories.stream()
						.map(AccountingVoucherHeaderDTO::new).collect(Collectors.toList());
				accountingVoucherHeaderDTO.setHistory(history);
			}
			accountingVouchers.add(accountingVoucherHeaderDTO);
		}
		return accountingVouchers;
	}

	/**
	 * GET /accounting-voucher-transaction/documentAccountingVoucherColumns/:
	 * documentPid : get accountingVoucherColumns.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         accountingVoucherColumnDTOs, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/documentAccountingVoucherColumns/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

	@Timed
	@RequestMapping(value = "/static-js-code/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StaticFormJSCodeDTO> getStaticJsCode(@PathVariable String documentPid) {
		log.debug("Web request to get static-js-code");
		StaticFormJSCode staticFormJSCode = staticFormJSCodesRepository.findByDocumentPidAndCompany(documentPid);
		StaticFormJSCodeDTO result = staticFormJSCodeMapper.staticFormJSCodeToStaticFormJSCodeDTO(staticFormJSCode);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/reference-documents/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DocumentDTO>> getReferenceDocuments(@PathVariable String documentPid) {
		log.debug("Web request to get reference-documents");
		return new ResponseEntity<>(referenceDocumentService.findReferenceDocumentsByDocumentPid(documentPid),
				HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/load-by-to-account/{activityPid}/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ReceiverSupplierAccountDTO loadReceiverSupplierAccounts(
			@PathVariable("activityPid") String activityPid, @PathVariable("documentPid") String documentPid) {
		// current user employee locations
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();

		List<AccountType> allByAccountTypes = documentAccountTypeRepository
				.findAccountTypesByDocumentPidAndAccountTypeColumn(documentPid, AccountTypeColumn.By);
		List<AccountType> allToAccountTypes = documentAccountTypeRepository
				.findAccountTypesByDocumentPidAndAccountTypeColumn(documentPid, AccountTypeColumn.To);

		List<AccountProfileDTO> byAccounts = new ArrayList<>();
		if (!allByAccountTypes.isEmpty()) {
			byAccounts = accountProfileMapper.accountProfilesToAccountProfileDTOs(locationAccountProfileRepository
					.findAccountProfilesByUserLocationsAccountTypesAndAccountProfileActivated(locations,
							allByAccountTypes));
		}
		List<AccountProfileDTO> toAccounts = new ArrayList<>();
		if (!allToAccountTypes.isEmpty()) {
			toAccounts = accountProfileMapper.accountProfilesToAccountProfileDTOs(locationAccountProfileRepository
					.findAccountProfilesByUserLocationsAccountTypesAndAccountProfileActivated(locations,
							allToAccountTypes));
		}
		return new ReceiverSupplierAccountDTO(byAccounts, toAccounts);
	}
	
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/loadExecutiveTaskExecution", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ExevTaskExenDTO loadExecutiveTaskExecution(@RequestParam("pid") String pid,@RequestParam("accPid") String accPid) {
		log.debug("Web request to get ExevTaskExenDTO");
		Optional<ExecutiveTaskExecution>optionalExecutiveTaskExecution=executiveTaskExecutionRepository.findOneByPid(pid); 
		Optional<AccountingVoucherHeader>optionalAccountingVoucherHeader=accountingVoucherHeaderRepository.findOneByPid(accPid);
		ExevTaskExenDTO exevTaskExenDTO=new ExevTaskExenDTO();
		if(optionalExecutiveTaskExecution.isPresent() && optionalAccountingVoucherHeader.isPresent()) {
			exevTaskExenDTO.setAccountPid(optionalAccountingVoucherHeader.get().getAccountProfile().getPid());
			exevTaskExenDTO.setActivityPid(optionalExecutiveTaskExecution.get().getActivity().getPid());
			exevTaskExenDTO.setUserPid(optionalExecutiveTaskExecution.get().getUser().getPid());
			exevTaskExenDTO.setDocumentPid(optionalAccountingVoucherHeader.get().getDocument().getPid());
			exevTaskExenDTO.setEmployeePid(optionalAccountingVoucherHeader.get().getEmployee().getPid());
		}
		return exevTaskExenDTO;
	}

}
