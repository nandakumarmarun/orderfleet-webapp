package com.orderfleet.webapp.web.vendor.excel.api;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
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
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Company;
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
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.ReceiptDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.ReceiptExcelDTO;

@RestController
@RequestMapping(value = "/api/excel/v1")
public class ReceiptDownloadController {

	private final Logger log = LoggerFactory.getLogger(ReceiptDownloadController.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private CompanyService companyService;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherDetailRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@RequestMapping(value = "/get-receipts.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<ReceiptExcelDTO> downloadReceiptsJson() throws URISyntaxException {
		CompanyViewDTO company = companyService.findOne(SecurityUtils.getCurrentUsersCompanyId());
		log.debug("REST request to download receipts <" + company.getLegalName() + "> : {}");

		List<ReceiptExcelDTO> receiptDTOs = new ArrayList<>();

//		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
//				.findByCompanyAndStatusOrderByCreatedDateDesc();

		/*
		 * List<AccountingVoucherHeader> accountingVoucherHeaders =
		 * accountingVoucherHeaderRepository
		 * .findByCompanyAndStatusOrderByCreatedDateDesc();
		 */
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_155" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get by companyId and tally status";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findByCompanyIdAndTallyStatusByCreatedDateDesc();
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
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "AP_QUERY_137" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get all by compId and IdsIn";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			List<AccountProfile> accountProfiles = accountProfileRepository
					.findAllByCompanyIdAndIdsIn(accountProfileIds);
			 String flag1 = "Normal";
				LocalDateTime endLCTime1 = LocalDateTime.now();
				String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
				String endDate1 = startLCTime1.format(DATE_FORMAT1);
				Duration duration1 = Duration.between(startLCTime1, endLCTime1);
				long minutes1 = duration1.toMinutes();
				if (minutes1 <= 1 && minutes1 >= 0) {
					flag1 = "Fast";
				}
				if (minutes1 > 1 && minutes1 <= 2) {
					flag1 = "Normal";
				}
				if (minutes1 > 2 && minutes1 <= 10) {
					flag1 = "Slow";
				}
				if (minutes1 > 10) {
					flag1 = "Dead Slow";
				}
		                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
						+ description1);
			List<User> users = userRepository.findAllByCompanyIdAndIdsIn(userIds);
			DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id11 = "AVD_QUERY_111" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description11 ="get all by accVoucherHeaderPidIn";
			LocalDateTime startLCTime11 = LocalDateTime.now();
			String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
			String startDate11 = startLCTime11.format(DATE_FORMAT11);
			logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
			List<AccountingVoucherDetail> accountingVoucherDetails = accountingVoucherDetailRepository
					.findAllByAccountingVoucherHeaderPidIn(avhPids);
			 String flag11 = "Normal";
				LocalDateTime endLCTime11 = LocalDateTime.now();
				String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
				String endDate11 = startLCTime11.format(DATE_FORMAT11);
				Duration duration11 = Duration.between(startLCTime11, endLCTime11);
				long minutes11 = duration11.toMinutes();
				if (minutes11 <= 1 && minutes11 >= 0) {
					flag11 = "Fast";
				}
				if (minutes11 > 1 && minutes11 <= 2) {
					flag11 = "Normal";
				}
				if (minutes11 > 2 && minutes11 <= 10) {
					flag11 = "Slow";
				}
				if (minutes11 > 10) {
					flag11 = "Dead Slow";
				}
		                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
						+ description11);
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
						ReceiptExcelDTO receiptDTO = new ReceiptExcelDTO(accountingVoucherDetail);
						receiptDTO.setAccountingVoucherHeaderDTO(avdObjectToDto(obj, opUser.get(), opDocument.get(),
								opEmployeeProfile.get(), opExe.get(), opAccPro.get(), avDetails));
						receiptDTOs.add(receiptDTO);
					} else {
						for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
								.getAccountingVoucherAllocations()) {
							ReceiptExcelDTO receiptDTO = new ReceiptExcelDTO(accountingVoucherAllocation);
							receiptDTO.setAccountingVoucherHeaderDTO(avdObjectToDto(obj, opUser.get(), opDocument.get(),
									opEmployeeProfile.get(), opExe.get(), opAccPro.get(), avDetails));
							receiptDTO.setHeaderAmount(Double.parseDouble(obj[7].toString()));
							receiptDTO.setNarrationMessage(
									accountingVoucherAllocation.getAccountingVoucherDetail().getRemarks());
							receiptDTOs.add(receiptDTO);
						}
					}

				}

			}
		}

		if (!receiptDTOs.isEmpty()) {
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "ACC_QUERY_149" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="Updating AccVoucher Tally download status using  pid and company";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			int updated = accountingVoucherHeaderRepository
					.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING,
							receiptDTOs.stream().map(avh -> avh.getAccountingVoucherHeaderPid())
									.collect(Collectors.toList()));
			String flag1 = "Normal";
			LocalDateTime endLCTime1 = LocalDateTime.now();
			String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
			String endDate1 = startLCTime1.format(DATE_FORMAT1);
			Duration duration1 = Duration.between(startLCTime1, endLCTime1);
			long minutes1 = duration1.toMinutes();
			if (minutes1 <= 1 && minutes1 >= 0) {
				flag1 = "Fast";
			}
			if (minutes1 > 1 && minutes1 <= 2) {
				flag1 = "Normal";
			}
			if (minutes1 > 2 && minutes1 <= 10) {
				flag1 = "Slow";
			}
			if (minutes1 > 10) {
				flag1 = "Dead Slow";
			}
	                logger.info(id + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description);
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

//	@RequestMapping(value = "/get-receipts.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public List<ReceiptExcelDTO> downloadReceiptsJson() throws URISyntaxException {
//		log.debug("REST request to download receipts : {}");
//		List<ReceiptExcelDTO> receiptDTOs = new ArrayList<>();
//		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
//				.findByCompanyAndStatusOrderByCreatedDateDesc();
//		for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
//			for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherHeader
//					.getAccountingVoucherDetails()) {
//				if (accountingVoucherDetail.getAccountingVoucherAllocations().isEmpty()) {
//					ReceiptExcelDTO receiptDTO = new ReceiptExcelDTO(accountingVoucherDetail);
//					receiptDTOs.add(receiptDTO);
//				} else {
//					for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
//							.getAccountingVoucherAllocations()) {
//						ReceiptExcelDTO receiptDTO = new ReceiptExcelDTO(accountingVoucherAllocation);
//						receiptDTO.setHeaderAmount(accountingVoucherHeader.getTotalAmount());
//						receiptDTO.setNarrationMessage(
//								accountingVoucherAllocation.getAccountingVoucherDetail().getRemarks());
//						receiptDTOs.add(receiptDTO);
//					}
//				}
//			}
//		}
//
//		if (!receiptDTOs.isEmpty()) {
//			int updated = accountingVoucherHeaderRepository
//					.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING,
//							receiptDTOs.stream().map(avh -> avh.getAccountingVoucherHeaderPid())
//									.collect(Collectors.toList()));
//			log.debug("updated " + updated + " to PROCESSING");
//		}
//		return receiptDTOs;
//	}

	@RequestMapping(value = "/update-receipt-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdateReceiptStatus(@Valid @RequestBody List<String> accountingVoucherHeaderPids)
			throws URISyntaxException {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		log.debug("REST request to update Accounting Voucher Header Status (" + company.getLegalName() + ") : {}",
				accountingVoucherHeaderPids.size());
		if (!accountingVoucherHeaderPids.isEmpty()) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACC_QUERY_149" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="Updating AccVoucher Tally download status using  pid and company";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountingVoucherHeaderRepository.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(
					TallyDownloadStatus.COMPLETED, accountingVoucherHeaderPids);
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
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
