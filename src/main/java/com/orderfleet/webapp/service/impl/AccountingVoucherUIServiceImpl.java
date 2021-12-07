package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DocumentAccountTypeRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountingVoucherUIService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

@Service
@Transactional
public class AccountingVoucherUIServiceImpl implements AccountingVoucherUIService {
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private DocumentAccountTypeRepository documentAccountTypeRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@Override
	public List<AccountProfileDTO> findByAccountProfilesByDocument(String documentPid) {
		List<AccountType> accountTypes = documentAccountTypeRepository
				.findAccountTypesByDocumentPidAndAccountTypeColumn(documentPid, AccountTypeColumn.By);
		List<String> accountTypeStrings = new ArrayList<>();
		for (AccountType accountType : accountTypes) {
			accountTypeStrings.add(accountType.getPid());
		}
		List<AccountProfile> accountProfiles = new ArrayList<>();
		if (!accountTypeStrings.isEmpty()) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_120" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compId and accTypes In and activated";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountProfiles = accountProfileRepository.findByCompanyIdAndAccountTypesInAndActivated(accountTypeStrings,
					true);
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
		List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
				.accountProfilesToAccountProfileDTOs(accountProfiles);
		return accountProfileDTOs;
	}

	@Override
	public List<AccountProfileDTO> findToAccountProfilesByDocument(String documentPid) {
		List<AccountType> accountTypes = documentAccountTypeRepository
				.findAccountTypesByDocumentPidAndAccountTypeColumn(documentPid, AccountTypeColumn.To);
		List<String> accountTypeStrings = new ArrayList<>();
		for (AccountType accountType : accountTypes) {
			accountTypeStrings.add(accountType.getPid());
		}
		List<AccountProfile> accountProfiles = new ArrayList<>();
		if (!accountTypes.isEmpty()) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_120" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compId and accTypes In and activated";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountProfiles = accountProfileRepository.findByCompanyIdAndAccountTypesInAndActivated(accountTypeStrings,
					true);
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
		List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
				.accountProfilesToAccountProfileDTOs(accountProfiles);
		return accountProfileDTOs;
	}

	@Override
	public void saveAccountingVoucher(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO) {

	}

}
