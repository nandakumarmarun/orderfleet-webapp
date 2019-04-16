package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.repository.AccountingVoucherDetailRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountingVoucherUIService;
import com.orderfleet.webapp.service.AccountingVoucherUISettingService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.ExecutiveTaskSubmissionService;
import com.orderfleet.webapp.service.async.TaskSubmissionPostSave;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.api.dto.TaskSubmissionResponse;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherUIDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherUISettingDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.BuyToAccountsDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;

@Controller
@RequestMapping("/web")
public class AccountingVoucherUIResource {

	private final Logger log = LoggerFactory.getLogger(AccountingVoucherUIResource.class);

	@Inject
	private AccountingVoucherUIService accountingVoucherUIService;

	@Inject
	private DocumentService documentService;

	@Inject
	private AccountingVoucherUISettingService accountingVoucherUISettingService;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private ExecutiveTaskSubmissionService executiveTaskSubmissionService;

	@Inject
	private ActivityService activityService;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherDetailRepository;

	@Inject
	private TaskSubmissionPostSave taskSubmissionPostSave;

	/**
	 * GET /accounting-voucher-ui : get all the accountingVoucherUI.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         accountProfiles in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/accounting-voucher-ui", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountingVoucherUI(Model model) {
		log.debug("Web request to get a page of accountingVoucherUI");
		List<ActivityDTO> activities = activityService.findAllByCompany();
		model.addAttribute("activities", activities);
		List<PaymentMode> paymentModes = Arrays.asList(PaymentMode.values());
		model.addAttribute("paymentModes", paymentModes);
		return "company/accountingVoucherUI";
	}

	@RequestMapping(value = "/accounting-voucher-ui/loadbuytoaccount", method = RequestMethod.GET)
	public @ResponseBody BuyToAccountsDTO getBuyToAccountByDocument(
			@RequestParam(value = "documentPid") String documentPid) {
		List<AccountProfileDTO> byAccountProfileDTOs = accountingVoucherUIService
				.findByAccountProfilesByDocument(documentPid);
		List<AccountProfileDTO> toAccountProfileDTOs = accountingVoucherUIService
				.findToAccountProfilesByDocument(documentPid);
		BuyToAccountsDTO buyToAccountsDTO = new BuyToAccountsDTO();
		buyToAccountsDTO.setBuyAccountProfiles(byAccountProfileDTOs);
		buyToAccountsDTO.setToAccountProfiles(toAccountProfileDTOs);
		return buyToAccountsDTO;
	}

	@RequestMapping(value = "/accounting-voucher-ui/loadaccountvoucher", method = RequestMethod.GET)
	public @ResponseBody List<AccountingVoucherUIDTO> getAccountingVoucher(
			@RequestParam(value = "documentPid") String documentPid,
			@RequestParam(value = "activityPid") String activityPid,
			@RequestParam(value = "paymentMode") PaymentMode paymentMode,
			@RequestParam(value = "byAccount") String byAccount, @RequestParam(value = "toAccount") String toAccount) {

		List<AccountingVoucherDetail> accountingVoucherDetails = accountingVoucherDetailRepository
				.findAllByCompanyIdByAccountAndToAccountAndPaymentModeAndAccountingVoucherHeaderPid(byAccount,
						toAccount, paymentMode, documentPid, activityPid);
		List<AccountingVoucherUIDTO> accountingVoucherUIDTOs = new ArrayList<>();
		for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherDetails) {
			AccountingVoucherUIDTO accountingVoucherUIDTO = new AccountingVoucherUIDTO();
			accountingVoucherUIDTO.setAmount((long) accountingVoucherDetail.getAmount());
			accountingVoucherUIDTO.setRemark(accountingVoucherDetail.getRemarks());
			accountingVoucherUIDTO.setDate("" + accountingVoucherDetail.getVoucherDate());
			accountingVoucherUIDTOs.add(accountingVoucherUIDTO);
		}
		return accountingVoucherUIDTOs;
	}

	@RequestMapping(value = "/accounting-voucher-ui/loaddocument", method = RequestMethod.GET)
	public @ResponseBody List<DocumentDTO> getDocumentsByUser(@RequestParam(value = "activityPid") String activityPid) {
		return documentService.findAllByActivityPid(activityPid);
	}

	@RequestMapping(value = "/accounting-voucher-ui/loadAccountingVoucherUISettings", method = RequestMethod.GET)
	public ResponseEntity<AccountingVoucherUISettingDTO> getAccountingVoucherUISettings(
			@RequestParam(value = "name", required = false) String name) {
		return accountingVoucherUISettingService.findByName(name).map(
				accountingVoucherUISettingDTO -> new ResponseEntity<>(accountingVoucherUISettingDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/accounting-voucher-ui", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<TaskSubmissionResponse> saveAccountingVoucherUI(
			@Valid @RequestBody AccountingVoucherUIDTO accountingVoucherUIDTO) {
		log.debug("Web request to save accounting documents from accounting-voucher-ui");
		TaskSubmissionResponse taskSubmissionResponse = null;
		// save
		LocalDateTime localDateTime = LocalDateTime.now();

		ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO();
		executiveTaskExecutionDTO.setDate(localDateTime);
		executiveTaskExecutionDTO.setLocationType(LocationType.NoLocation);
		executiveTaskExecutionDTO.setActivityPid(accountingVoucherUIDTO.getActivityPid());
		Optional<Document> optionalDocument = documentRepository.findOneByPid(accountingVoucherUIDTO.getDocumentPid());
		if (optionalDocument.isPresent()) {
			if (optionalDocument.get().getActivityAccount().equals(AccountTypeColumn.By)) {
				executiveTaskExecutionDTO.setAccountProfilePid(accountingVoucherUIDTO.getByAccount());
			} else {
				executiveTaskExecutionDTO.setAccountProfilePid(accountingVoucherUIDTO.getToAccount());
			}
		}

		List<AccountingVoucherHeaderDTO> accountingVouchers = new ArrayList<>();
		AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO();
		accountingVoucherHeaderDTO.setDocumentDate(localDateTime);
		accountingVoucherHeaderDTO.setDocumentPid(accountingVoucherUIDTO.getDocumentPid());
		accountingVoucherHeaderDTO.setAccountProfilePid(executiveTaskExecutionDTO.getAccountProfilePid());
		// create and set Document Number Local
		accountingVoucherHeaderDTO
				.setDocumentNumberLocal(createDocumentNumber(accountingVoucherUIDTO.getDocumentPid()));
		AccountingVoucherDetailDTO accountingVoucherDetailDTO = new AccountingVoucherDetailDTO();
		accountingVoucherDetailDTO.setMode(accountingVoucherUIDTO.getPaymentMode());
		accountingVoucherDetailDTO.setByAccountPid(accountingVoucherUIDTO.getByAccount());
		accountingVoucherDetailDTO.setToAccountPid(accountingVoucherUIDTO.getToAccount());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate date = LocalDate.parse(accountingVoucherUIDTO.getDate(), formatter);
		accountingVoucherDetailDTO.setVoucherDate(date.atTime(localDateTime.toLocalTime()));
		accountingVoucherDetailDTO.setRemarks(accountingVoucherUIDTO.getRemark());
		accountingVoucherDetailDTO.setAmount(accountingVoucherUIDTO.getAmount());
		List<AccountingVoucherDetailDTO> accountingVoucherDetails = new ArrayList<>();
		accountingVoucherDetails.add(accountingVoucherDetailDTO);
		accountingVoucherHeaderDTO.setAccountingVoucherDetails(accountingVoucherDetails);
		accountingVouchers.add(accountingVoucherHeaderDTO);

		// main object
		ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO = new ExecutiveTaskSubmissionDTO();
		executiveTaskSubmissionDTO.setExecutiveTaskExecutionDTO(executiveTaskExecutionDTO);
		executiveTaskSubmissionDTO.setAccountingVouchers(accountingVouchers);

		ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper = executiveTaskSubmissionService
				.executiveTaskSubmission(executiveTaskSubmissionDTO);
		if (tsTransactionWrapper != null) {
			taskSubmissionResponse = tsTransactionWrapper.getTaskSubmissionResponse();
			taskSubmissionPostSave.doPostSaveExecutivetaskSubmission(tsTransactionWrapper, executiveTaskSubmissionDTO);
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
}
