package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountingVoucherUISetting;
import com.orderfleet.webapp.repository.AccountingVoucherUISettingRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountingVoucherUISettingService;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherUISettingDTO;

@Service
@Transactional
public class AccountingVoucherUISettingServiceImpl implements AccountingVoucherUISettingService{
	
	@Inject
	private AccountingVoucherUISettingRepository accountingVoucherUISettingRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private DocumentRepository documentRepository;
	
	@Inject
	private ActivityRepository activityRepository;

	@Override
	public AccountingVoucherUISettingDTO save(AccountingVoucherUISettingDTO accountingVoucherUISettingsDTO) {
		AccountingVoucherUISetting accountingVoucherUISetting=new AccountingVoucherUISetting();
		
		accountingVoucherUISetting.setActivity(activityRepository.findOneByPid(accountingVoucherUISettingsDTO.getActivityPid()).get());
		accountingVoucherUISetting.setDocument(documentRepository.findOneByPid(accountingVoucherUISettingsDTO.getDocumentPid()).get());
		accountingVoucherUISetting.setName(accountingVoucherUISettingsDTO.getName());
		accountingVoucherUISetting.setTitle(accountingVoucherUISettingsDTO.getTitle());
		accountingVoucherUISetting.setPaymentMode(accountingVoucherUISettingsDTO.getPaymentMode());
		accountingVoucherUISetting.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		accountingVoucherUISetting=accountingVoucherUISettingRepository.save(accountingVoucherUISetting);
		AccountingVoucherUISettingDTO accountingVoucherUISettingDTO=new AccountingVoucherUISettingDTO(accountingVoucherUISetting);
		return accountingVoucherUISettingDTO;
	}

	@Override
	public AccountingVoucherUISettingDTO update(AccountingVoucherUISettingDTO accountingVoucherUISettingsDTO) {
		AccountingVoucherUISetting accountingVoucherUISetting=accountingVoucherUISettingRepository.findOne(accountingVoucherUISettingsDTO.getId());
		accountingVoucherUISetting.setActivity(activityRepository.findOneByPid(accountingVoucherUISettingsDTO.getActivityPid()).get());
		accountingVoucherUISetting.setDocument(documentRepository.findOneByPid(accountingVoucherUISettingsDTO.getDocumentPid()).get());
		accountingVoucherUISetting.setName(accountingVoucherUISettingsDTO.getName());
		accountingVoucherUISetting.setTitle(accountingVoucherUISettingsDTO.getTitle());
		accountingVoucherUISetting.setPaymentMode(accountingVoucherUISettingsDTO.getPaymentMode());
		accountingVoucherUISetting=accountingVoucherUISettingRepository.save(accountingVoucherUISetting);
		AccountingVoucherUISettingDTO accountingVoucherUISettingDTO=new AccountingVoucherUISettingDTO(accountingVoucherUISetting);
		return accountingVoucherUISettingDTO;
	}

	@Override
	public List<AccountingVoucherUISettingDTO> findAllByCompany() {
		List<AccountingVoucherUISetting>accountingVoucherUISettings=accountingVoucherUISettingRepository.findAllByCompanyId();
		List<AccountingVoucherUISettingDTO>accountingVoucherUISettingDTOs=new ArrayList<>();
		for (AccountingVoucherUISetting accountingVoucherUISetting : accountingVoucherUISettings) {
			AccountingVoucherUISettingDTO accountingVoucherUISettingDTO=new AccountingVoucherUISettingDTO(accountingVoucherUISetting);
			accountingVoucherUISettingDTOs.add(accountingVoucherUISettingDTO);
		}
		return accountingVoucherUISettingDTOs;
	}

	@Override
	public AccountingVoucherUISettingDTO findOne(Long id) {
		AccountingVoucherUISetting accountingVoucherUISetting=accountingVoucherUISettingRepository.findOne(id);
		AccountingVoucherUISettingDTO accountingVoucherUISettingDTO=new AccountingVoucherUISettingDTO(accountingVoucherUISetting);
		return accountingVoucherUISettingDTO;
	}



	@Override
	public void delete(Long id) {
accountingVoucherUISettingRepository.delete(id);		
	}

	@Override
	public Optional<AccountingVoucherUISettingDTO> findByName(String name) {
		return accountingVoucherUISettingRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(accountingVoucherUISetting -> {
					AccountingVoucherUISettingDTO accountingVoucherUISettingDTO=new AccountingVoucherUISettingDTO(accountingVoucherUISetting);
					return accountingVoucherUISettingDTO;
				});
	}
}
