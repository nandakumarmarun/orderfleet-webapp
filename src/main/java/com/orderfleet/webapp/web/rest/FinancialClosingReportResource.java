package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.FinancialClosingHeader;
import com.orderfleet.webapp.domain.FinancialClosingReportSettings;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserAccountProfile;
import com.orderfleet.webapp.domain.enums.DebitCredit;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.FinancialClosingHeaderRepository;
import com.orderfleet.webapp.repository.FinancialClosingReportSettingsRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserAccountProfileRepository;
import com.orderfleet.webapp.repository.UserBalanceRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.FinancialClosingHeaderService;
import com.orderfleet.webapp.web.rest.dto.FinancialClosingReportDTO;
import com.orderfleet.webapp.web.rest.dto.FinancialClosingReportHolder;

@Controller
@RequestMapping("/web")
public class FinancialClosingReportResource {

	private final Logger log = LoggerFactory.getLogger(FinancialClosingReportResource.class);

	private static final String VIEW = "company/financialClosingReport";

	@Inject
	private FinancialClosingReportSettingsRepository financialClosingReportSettingsRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private UserAccountProfileRepository userAccountProfileRepository;

	@Inject
	private UserBalanceRepository userBalanceRepository;

	@Inject
	private FinancialClosingHeaderRepository fcHeaderRepository;
	
	@Inject
	private FinancialClosingHeaderService financialClosingHeaderService;

	@RequestMapping(value = "/financial-closing-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getFincancialClosingReportView(Model model) {
		// user under current user
		userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
			if (u.getShowAllUsersData()) {
				model.addAttribute("employees", employeeProfileService.findAllByCompany());
			} else {
				List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
				model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
			}
		});
		model.addAttribute("fClosingReportHolder", new FinancialClosingReportHolder());
		return VIEW;
	}

	@RequestMapping(value = "/financial-closing-report", method = RequestMethod.POST)
	@Timed
	@Transactional(readOnly = true)
	public String loadFincancialClosingReport(@RequestParam String employeePid, Model model) {
		log.debug("Web request to get a financial closing report");
		// user under current user
		userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
			if (u.getShowAllUsersData()) {
				model.addAttribute("employees", employeeProfileService.findAllByCompany());
			} else {
				List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
				model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
			}
		});
		model.addAttribute("employeePid", employeePid);
		Optional<EmployeeProfile> optionalEmployee = employeeProfileRepository.findOneByPid(employeePid);
		User user = null;
		if (!optionalEmployee.isPresent()) {
			return VIEW;
		} else {
			user = optionalEmployee.get().getUser();
		}
		if (user == null) {
			return VIEW;
		} else {
			FinancialClosingReportHolder fClosingReportHolder = new FinancialClosingReportHolder();
			List<FinancialClosingReportDTO> fClosingReports = new ArrayList<>();
			List<FinancialClosingReportDTO> pettyCashReports = new ArrayList<>();
			List<FinancialClosingReportSettings> financialClosingSettings = financialClosingReportSettingsRepository
					.findAllByCompanyId();
			double fClosingTotal = 0.0;
			double pCashTotal = 0.0;
			LocalDateTime from = LocalDate.now().minusYears(3).atTime(0, 0);
			Optional<FinancialClosingHeader> optionalFCH = fcHeaderRepository
					.findTop1ByUserPidOrderByIdDesc(user.getPid());
			if (optionalFCH.isPresent()) {
				from = optionalFCH.get().getClosedDate();
			}
			LocalDateTime to = LocalDateTime.now();
			for (FinancialClosingReportSettings closingSetting : financialClosingSettings) {
				FinancialClosingReportDTO reportDTO = createFincancialClosingReport(closingSetting, user, from, to);
				if (reportDTO.getPaymentMode().equals(PaymentMode.PETTYCASH)) {
					if (reportDTO.getDebitCredit().equals(DebitCredit.Cr)) {
						pCashTotal = pCashTotal + reportDTO.getAmount();
					} else {
						pCashTotal = pCashTotal - reportDTO.getAmount();
					}
					pettyCashReports.add(reportDTO);
				} else {
					if (reportDTO.getDebitCredit().equals(DebitCredit.Cr)) {
						fClosingTotal = fClosingTotal + reportDTO.getAmount();
					} else {
						fClosingTotal = fClosingTotal - reportDTO.getAmount();
					}
					fClosingReports.add(reportDTO);
				}
			}
			Collections.sort(fClosingReports);
			Collections.sort(pettyCashReports);
			fClosingReportHolder.setFinancialClosings(fClosingReports);
			fClosingReportHolder.setPettyCashClosings(pettyCashReports);
			fClosingReportHolder.setfClosingTotal(fClosingTotal);
			fClosingReportHolder.setPettyCashTotal(pCashTotal);
			fClosingReportHolder.setSelectedUserPid(user.getPid());
			userBalanceRepository.findTop1ByUserPidOrderByIdDesc(user.getPid()).ifPresent(ub -> {
				fClosingReportHolder.setOpeningBalance(ub.getAmount());
				fClosingReportHolder.setPettyCashTotal(fClosingReportHolder.getPettyCashTotal() + ub.getAmount());
			});

			model.addAttribute("fClosingReportHolder", fClosingReportHolder);
		}
		return VIEW;
	}

	@RequestMapping(value = "/close-financial-report", method = RequestMethod.POST)
	@Timed
	public String closeFinance(
			@ModelAttribute("fClosingReportHolder") FinancialClosingReportHolder fClosingReportHolder, Model model) {
		financialClosingHeaderService.processFincancialClosing(fClosingReportHolder);
		return "redirect:/web/financial-closing-report";
	}

	private FinancialClosingReportDTO createFincancialClosingReport(FinancialClosingReportSettings closingSetting,
			User user, LocalDateTime from, LocalDateTime to) {
		FinancialClosingReportDTO fClosingReport = new FinancialClosingReportDTO();
		fClosingReport.setDocumentPid(closingSetting.getDocument().getPid());
		fClosingReport.setDocumentName(closingSetting.getDocument().getName());
		fClosingReport.setDocumentType(closingSetting.getDocument().getDocumentType());
		fClosingReport.setDebitCredit(closingSetting.getDebitCredit());
		fClosingReport.setPaymentMode(closingSetting.getPaymentMode());
		fClosingReport.setSortOrder(closingSetting.getSortOrder());

		List<Document> documents = new ArrayList<>();
		documents.add(closingSetting.getDocument());
		String userPid = user.getPid();
		Object obj = null;
		// if petty cash check user account
		if (closingSetting.getPaymentMode().equals(PaymentMode.PETTYCASH)) {
			// find user's account profile
			Optional<UserAccountProfile> userAccountProfile = userAccountProfileRepository
					.findByCompanyIdAndUserLogin(user.getCompany().getId(), user.getLogin());
			if (userAccountProfile.isPresent()) {
				List<AccountProfile> accountProfiles = new ArrayList<>();
				accountProfiles.add(userAccountProfile.get().getAccountProfile());
				obj = accountingVoucherHeaderRepository.getCountAndAmountByDocumentsAndDateBetweenAndAccountProfileIn(
						documents, from, to, accountProfiles);
			}

		} else if (closingSetting.getDocument().getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
			String id="INV_QUERY_110";
			String description="Selecting count of inv_voucher sum of documentTotal,documentVolume from inv_voucher and validating companyId,documentin, createdate and createByPid";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
			obj = inventoryVoucherHeaderRepository.getCountAmountAndVolumeByDocumentsAndDateBetweenAndUser(documents,
					from, to, userPid);
		} else if (closingSetting.getDocument().getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
			obj = accountingVoucherHeaderRepository.getCountAndAmountByDocumentsAndDateBetweenAndUser(documents, from,
					to, userPid);
		}
		Object[] countAndAmount = (Object[]) obj;
		if (countAndAmount != null && countAndAmount[1] != null) {
			fClosingReport.setAmount((double) countAndAmount[1]);
		}
		return fClosingReport;
	}
}
