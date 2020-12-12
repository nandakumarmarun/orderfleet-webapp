package com.orderfleet.webapp.web.vendor.garuda.api;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountingVoucherDetailRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.vendor.garuda.dto.ReceiptGarudaDTO;

@RestController
@RequestMapping(value = "/api/garuda")
public class ReceiptGaruda {
	
	private final Logger log = LoggerFactory.getLogger(ReceiptGaruda.class);

	@Inject
	private CompanyService companyService;
	
	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;
	
	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;
	
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherDetailRepository;

	@Inject
	private UserRepository userRepository;

	
	@RequestMapping(value = "/get-receipts.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<ReceiptGarudaDTO> downloadReceiptsJson() throws URISyntaxException {
		CompanyViewDTO company = companyService.findOne(SecurityUtils.getCurrentUsersCompanyId());
		log.debug("REST request to download receipts <" + company.getLegalName() + "> : {}");

		List<ReceiptGarudaDTO> receiptDTOs = new ArrayList<>();
		
		List<Object[]> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findByCompanyIdAndTallyStatusByCreatedDateDesc();

		if (accountingVoucherHeaders.size() > 0) {

			Set<Long> avhIds = new HashSet<>();
			Set<String> avhPids = new HashSet<>();
			Set<Long> documentIds = new HashSet<>();
			Set<Long> employeeIds = new HashSet<>();
			Set<Long> accountProfileIds = new HashSet<>();
			Set<Long> userIds = new HashSet<>();
			Set<Long> exeIds = new HashSet<>();

			for (Object[] obj : accountingVoucherHeaders) {
				avhIds.add(Long.parseLong(obj[0].toString()));
				avhPids.add(obj[1].toString());
				userIds.add(Long.parseLong(obj[10].toString()));
				documentIds.add(Long.parseLong(obj[3].toString()));
				employeeIds.add(Long.parseLong(obj[11].toString()));
				exeIds.add(Long.parseLong(obj[2].toString()));
				accountProfileIds.add(Long.parseLong(obj[4].toString()));

			}

			List<Document> documents = documentRepository.findAllByCompanyIdAndIdsIn(documentIds);
			List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyIdAndIdsIn(employeeIds);
			List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdAndIdsIn(exeIds);
			List<AccountProfile> accountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(accountProfileIds);
			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);
			List<AccountingVoucherDetail> accountingVoucherDetails = accountingVoucherDetailRepository
					.findAllByAccountingVoucherHeaderPidIn(avhPids);

			for (Object[] obj : accountingVoucherHeaders) {

				String rferenceInventoryVoucherHeaderExecutiveExecutionPid = "";

				Optional<User> opUser = users.stream().filter(u -> u.getId() == Long.parseLong(obj[10].toString()))
						.findAny();

				Optional<Document> opDocument = documents.stream()
						.filter(doc -> doc.getId() == Long.parseLong(obj[3].toString())).findAny();

				Optional<EmployeeProfile> opEmployeeProfile = employeeProfiles.stream()
						.filter(emp -> emp.getId() == Long.parseLong(obj[11].toString())).findAny();

				Optional<ExecutiveTaskExecution> opExe = executiveTaskExecutions.stream()
						.filter(doc -> doc.getId() == Long.parseLong(obj[2].toString())).findAny();

				Optional<AccountProfile> opAccPro = accountProfiles.stream()
						.filter(a -> a.getId() == Long.parseLong(obj[4].toString())).findAny();

				List<AccountingVoucherDetail> avDetails = accountingVoucherDetails.stream()
						.filter(ivd -> ivd.getAccountingVoucherHeader().getId() == Long.parseLong(obj[0].toString()))
						.collect(Collectors.toList()).stream()
						.sorted(Comparator.comparingLong(AccountingVoucherDetail::getId)).collect(Collectors.toList());

				for (AccountingVoucherDetail accountingVoucherDetail : avDetails) {
					if (accountingVoucherDetail.getAccountingVoucherAllocations().isEmpty()) {
						ReceiptGarudaDTO receiptDTO = new ReceiptGarudaDTO(accountingVoucherDetail);
						receiptDTO.setAccountingVoucherHeaderDTO(avdObjectToDto(obj, opUser.get(), opDocument.get(),
								opEmployeeProfile.get(), opExe.get(), opAccPro.get(), avDetails));
						receiptDTOs.add(receiptDTO);
					} else {
						for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
								.getAccountingVoucherAllocations()) {
							ReceiptGarudaDTO receiptDTO = new ReceiptGarudaDTO(accountingVoucherAllocation);
							receiptDTO.setAccountingVoucherHeaderDTO(avdObjectToDto(obj, opUser.get(), opDocument.get(),
									opEmployeeProfile.get(), opExe.get(), opAccPro.get(), avDetails));
//							receiptDTO.setHeaderAmount(Double.parseDouble(obj[7].toString()));
//							receiptDTO.setNarrationMessage(
//									accountingVoucherAllocation.getAccountingVoucherDetail().getRemarks());
							receiptDTOs.add(receiptDTO);
						}
					}

				}

			}
		}

		if (!receiptDTOs.isEmpty()) {
			int updated = accountingVoucherHeaderRepository
					.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING,
							receiptDTOs.stream().map(avh -> avh.getReceiptNo())
									.collect(Collectors.toList()));
			log.debug("updated " + updated + " to PROCESSING");
		}
		return receiptDTOs;
	}
	
	private AccountingVoucherHeaderDTO avdObjectToDto(Object[] obj, User user, Document document,
			EmployeeProfile employee, ExecutiveTaskExecution executiveTaskExecution, AccountProfile accountProfile,
			List<AccountingVoucherDetail> avDetails) {

		AccountingVoucherHeaderDTO accountingVoucherHeaderDTO = new AccountingVoucherHeaderDTO();

		accountingVoucherHeaderDTO.setPid(obj[1].toString());
		accountingVoucherHeaderDTO.setDocumentPid(document.getPid());
		accountingVoucherHeaderDTO.setDocumentName(document.getName());
		if (document.getActivityAccount() != null && document.getActivityAccount().equals(AccountTypeColumn.By)) {

			accountingVoucherHeaderDTO.setByAmount(Double.parseDouble(obj[7].toString()));
		} else if (document.getActivityAccount() != null
				&& document.getActivityAccount().equals(AccountTypeColumn.To)) {
			accountingVoucherHeaderDTO.setToAmount(Double.parseDouble(obj[7].toString()));
		}
		if (accountProfile != null) {
			accountingVoucherHeaderDTO.setAccountProfilePid(accountProfile.getPid());
			accountingVoucherHeaderDTO.setAccountProfileName(accountProfile.getName());
			accountingVoucherHeaderDTO.setPhone(accountProfile.getPhone1());
		}

		LocalDateTime createdDate = null;
		if (obj[5] != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String[] splitDate = obj[5].toString().split(" ");
			createdDate = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);
		}

		LocalDateTime documentDate = null;
		if (obj[6] != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String[] splitDate = obj[6].toString().split(" ");
			documentDate = LocalDate.parse(splitDate[0], formatter).atTime(0, 0);
		}

		accountingVoucherHeaderDTO.setCreatedDate(createdDate);
		accountingVoucherHeaderDTO.setDocumentDate(documentDate);
		if (employee != null) {
			accountingVoucherHeaderDTO.setEmployeePid(employee.getPid());
			accountingVoucherHeaderDTO.setEmployeeName(employee.getName());
		}
		if (user != null) {
			accountingVoucherHeaderDTO.setUserName(user.getFirstName());
		}

		accountingVoucherHeaderDTO.setTotalAmount(Double.parseDouble(obj[7].toString()));
		accountingVoucherHeaderDTO.setOutstandingAmount(Double.parseDouble(obj[8].toString()));
		accountingVoucherHeaderDTO.setRemarks(obj[9].toString());
		accountingVoucherHeaderDTO.setDocumentNumberLocal(obj[12].toString());
		accountingVoucherHeaderDTO.setDocumentNumberServer(obj[13].toString());

		double chequeAmount = 0;
		double cashAmount = 0;
		for (AccountingVoucherDetail avd : avDetails) {
			if (avd.getMode() == PaymentMode.Cheque || avd.getMode() == PaymentMode.Bank) {
				chequeAmount += avd.getAmount();
			} else if (avd.getMode() == PaymentMode.Cash) {
				cashAmount += avd.getAmount();
			}
		}
		accountingVoucherHeaderDTO.setCashAmount(cashAmount);
		accountingVoucherHeaderDTO.setChequeAmount(chequeAmount);

		return accountingVoucherHeaderDTO;
	}

}
