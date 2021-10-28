package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.FinancialClosingDetailAccounting;
import com.orderfleet.webapp.domain.FinancialClosingDetailInventory;
import com.orderfleet.webapp.domain.FinancialClosingHeader;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserAccountProfile;
import com.orderfleet.webapp.domain.UserBalance;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.FinancialClosingDetailAccountingRepository;
import com.orderfleet.webapp.repository.FinancialClosingDetailInventoryRepository;
import com.orderfleet.webapp.repository.FinancialClosingHeaderRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserAccountProfileRepository;
import com.orderfleet.webapp.repository.UserBalanceRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FinancialClosingHeaderService;
import com.orderfleet.webapp.web.rest.SalesProductGroupTargetAchievedReportResource;
import com.orderfleet.webapp.web.rest.dto.FinancialClosingReportDTO;
import com.orderfleet.webapp.web.rest.dto.FinancialClosingReportHolder;

@Service
@Transactional
public class FinancialClosingHeaderServiceImpl implements FinancialClosingHeaderService {
	
	private final Logger log = LoggerFactory.getLogger(FinancialClosingHeaderServiceImpl.class);
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private FinancialClosingHeaderRepository fcHeaderRepository;

	@Inject
	private FinancialClosingDetailInventoryRepository fcDetailInventoryRepository;

	@Inject
	private FinancialClosingDetailAccountingRepository fcDetailAccountingRepository;

	@Inject
	private UserBalanceRepository userBalanceRepository;
	
	@Inject
	private UserAccountProfileRepository userAccountProfileRepository;

	@Override
	public void processFincancialClosing(FinancialClosingReportHolder fClosingReportHolder) {
		LocalDateTime from = LocalDate.now().minusYears(3).atTime(0, 0);
		Optional<FinancialClosingHeader> optionalFCH = fcHeaderRepository.findTop1ByUserPidOrderByIdDesc(fClosingReportHolder.getSelectedUserPid());
		if (optionalFCH.isPresent()) {
			from = optionalFCH.get().getClosedDate();
		}
		LocalDateTime currentDate = LocalDateTime.now();
		Optional<User> optionalUser = userRepository.findOneByPid(fClosingReportHolder.getSelectedUserPid());
		if(optionalUser.isPresent()) {
			User empUser = optionalUser.get();
			User loggedUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
			// save to user balance
			UserBalance userBalance = new UserBalance();
			userBalance.setAmount(fClosingReportHolder.getPettyCashTotal());
			userBalance.setDate(currentDate);
			userBalance.setCompany(empUser.getCompany());
			userBalance.setUser(empUser);
			userBalance.setRemarks("");
			userBalanceRepository.save(userBalance);

			// save to closing balance
			// header
			FinancialClosingHeader fcHeader = new FinancialClosingHeader();
			fcHeader.setClosedDate(currentDate);
			fcHeader.setClosedBy(loggedUser);
			fcHeader.setUser(empUser);
			fcHeader.setClosedAmount(fClosingReportHolder.getfClosingTotal());
			fcHeader.setRemarks("");
			fcHeader.setCompany(empUser.getCompany());
			fcHeader = fcHeaderRepository.save(fcHeader);
			// details
			List<FinancialClosingDetailInventory> fcDetailInventories = new ArrayList<>();
			List<FinancialClosingDetailAccounting> fcDetailAccountings = new ArrayList<>();
			
			// save details
			List<FinancialClosingReportDTO> allReportData = new ArrayList<>();
			allReportData.addAll(fClosingReportHolder.getFinancialClosings());
			allReportData.addAll(fClosingReportHolder.getPettyCashClosings());
			
			for (FinancialClosingReportDTO fcReport : allReportData) {
				if (fcReport.getPaymentMode().equals(PaymentMode.PETTYCASH)) {
					// find user's account profile
					Optional<UserAccountProfile> userAccountProfile = userAccountProfileRepository
							.findByCompanyIdAndUserLogin(empUser.getCompany().getId(), empUser.getLogin());
					if (userAccountProfile.isPresent()) {
						List<AccountingVoucherHeader> avHeaders = accountingVoucherHeaderRepository
								.getByDocumentPidAndDateBetweenAndAccountProfilePid(fcReport.getDocumentPid(), from, currentDate,
										userAccountProfile.get().getAccountProfile().getPid());
						for (AccountingVoucherHeader avHeader : avHeaders) {
							FinancialClosingDetailAccounting fcDetailAccounting = new FinancialClosingDetailAccounting();
							fcDetailAccounting.setFinancialClosingHeader(fcHeader);
							fcDetailAccounting.setPaymentMode(fcReport.getPaymentMode());
							fcDetailAccounting.setDebitCredit(fcReport.getDebitCredit());
							fcDetailAccounting.setCreatedDate(currentDate);
							fcDetailAccounting.setCreatedBy(loggedUser);
							fcDetailAccounting.setAccountingVoucherHeader(avHeader);
							fcDetailAccounting.setCompany(empUser.getCompany());
							fcDetailAccountings.add(fcDetailAccounting);
						}
					}

				} else if (fcReport.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
					String id="INV_QUERY_142";
					String description="Selecting inv voucher from iv_vouc_header by validating companyId ,create dateBetween,doc pid and  createBypid";
					log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
					List<InventoryVoucherHeader> ivHeaders = inventoryVoucherHeaderRepository
							.getByDocumentPidAndDateBetweenAndUserPid(fcReport.getDocumentPid(), from, currentDate,
									empUser.getPid());
					for (InventoryVoucherHeader ivHeader : ivHeaders) {
						FinancialClosingDetailInventory fcDetailInventory = new FinancialClosingDetailInventory();
						fcDetailInventory.setFinancialClosingHeader(fcHeader);
						fcDetailInventory.setPaymentMode(fcReport.getPaymentMode());
						fcDetailInventory.setDebitCredit(fcReport.getDebitCredit());
						fcDetailInventory.setCreatedDate(currentDate);
						fcDetailInventory.setCreatedBy(loggedUser);
						fcDetailInventory.setInventoryVoucherHeader(ivHeader);
						fcDetailInventory.setCompany(empUser.getCompany());
						fcDetailInventories.add(fcDetailInventory);
					}
				} else if (fcReport.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
					List<AccountingVoucherHeader> avHeaders = accountingVoucherHeaderRepository
							.getByDocumentPidAndDateBetweenAndUserPid(fcReport.getDocumentPid(), from, currentDate,
									empUser.getPid());
					for (AccountingVoucherHeader avHeader : avHeaders) {
						FinancialClosingDetailAccounting fcDetailAccounting = new FinancialClosingDetailAccounting();
						fcDetailAccounting.setFinancialClosingHeader(fcHeader);
						fcDetailAccounting.setPaymentMode(fcReport.getPaymentMode());
						fcDetailAccounting.setDebitCredit(fcReport.getDebitCredit());
						fcDetailAccounting.setCreatedDate(currentDate);
						fcDetailAccounting.setCreatedBy(loggedUser);
						fcDetailAccounting.setAccountingVoucherHeader(avHeader);
						fcDetailAccounting.setCompany(empUser.getCompany());
						fcDetailAccountings.add(fcDetailAccounting);
					}
				}
			}
		
			fcDetailInventoryRepository.save(fcDetailInventories);
			fcDetailAccountingRepository.save(fcDetailAccountings);
		}
	}

}
