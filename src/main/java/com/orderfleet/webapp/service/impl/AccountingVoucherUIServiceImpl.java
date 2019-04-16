package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DocumentAccountTypeRepository;
import com.orderfleet.webapp.service.AccountingVoucherUIService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

@Service
@Transactional
public class AccountingVoucherUIServiceImpl implements AccountingVoucherUIService {
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
			accountProfiles = accountProfileRepository.findByCompanyIdAndAccountTypesInAndActivated(accountTypeStrings,
					true);
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
			accountProfiles = accountProfileRepository.findByCompanyIdAndAccountTypesInAndActivated(accountTypeStrings,
					true);
		}
		List<AccountProfileDTO> accountProfileDTOs = accountProfileMapper
				.accountProfilesToAccountProfileDTOs(accountProfiles);
		return accountProfileDTOs;
	}

	@Override
	public void saveAccountingVoucher(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO) {

	}

}
