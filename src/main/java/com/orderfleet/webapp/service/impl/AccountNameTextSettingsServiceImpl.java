package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountNameTextSettings;
import com.orderfleet.webapp.domain.ProductNameTextSettings;
import com.orderfleet.webapp.repository.AccountNameTextSettingsRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ProductNameTextSettingsRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountNameTextSettingsService;
import com.orderfleet.webapp.service.ProductNameTextSettingsService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountNameTextSettingsDTO;
import com.orderfleet.webapp.web.rest.dto.ProductNameTextSettingsDTO;

/**
 * Service Implementation for managing ProductNameTextSettings.
 * 
 * @author Muhammed Riyas T
 * @since Dec 29, 2016
 */
@Service
@Transactional
public class AccountNameTextSettingsServiceImpl implements AccountNameTextSettingsService {

	private final Logger log = LoggerFactory.getLogger(AccountNameTextSettingsServiceImpl.class);

	@Inject
	private AccountNameTextSettingsRepository accountNameTextSettingsRepository;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a productNameTextSettings.
	 * 
	 * @param productNameTextSettingsDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public void save(List<AccountNameTextSettingsDTO> accountNameTextSettingsDTOs) {
		log.debug("Request to save AccountNameTextSettings : {}", accountNameTextSettingsDTOs);
		for (AccountNameTextSettingsDTO accountNameTextSettingsDTO : accountNameTextSettingsDTOs) {
			AccountNameTextSettings accountNameTextSettings = accountNameTextSettingsRepository
					.findOneByPid(accountNameTextSettingsDTO.getPid()).get();
			accountNameTextSettings.setEnabled(accountNameTextSettingsDTO.getEnabled());
			accountNameTextSettingsRepository.save(accountNameTextSettings);
		}
	}

	/**
	 * Get all the productNameTextSettingss.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountNameTextSettingsDTO> findAllByCompany() {
		log.debug("Request to get all AccountNameTextSettings");
		List<AccountNameTextSettings> accountNameTextSettings = accountNameTextSettingsRepository.findAllByCompanyId();
		List<AccountNameTextSettingsDTO> result = accountNameTextSettings.stream().map(AccountNameTextSettingsDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one productNameTextSettings by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountNameTextSettingsDTO> findOneByPid(String pid) {
		log.debug("Request to get AccountNameTextSettings by pid : {}", pid);
		return accountNameTextSettingsRepository.findOneByPid(pid).map(accountNameTextSettings -> {
			AccountNameTextSettingsDTO accountNameTextSettingsDTO = new AccountNameTextSettingsDTO(
					accountNameTextSettings);
			return accountNameTextSettingsDTO;
		});
	}

	/**
	 * Get one productNameTextSettings by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountNameTextSettingsDTO> findByName(String name) {
		log.debug("Request to get AccountNameTextSettings by name : {}", name);
		return accountNameTextSettingsRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(accountNameTextSettings -> {
					AccountNameTextSettingsDTO accountNameTextSettingsDTO = new AccountNameTextSettingsDTO(
							accountNameTextSettings);
					return accountNameTextSettingsDTO;
				});
	}

	@Override
	public void saveDefault(List<AccountNameTextSettingsDTO> accountNameTextSettingsDTOs) {
		log.debug("Request to save AccountNameTextSettings : {}", accountNameTextSettingsDTOs);
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		for (AccountNameTextSettingsDTO accountNameTextSettingsDTO : accountNameTextSettingsDTOs) {
			Optional<AccountNameTextSettings> opAccountNameTextSettings = accountNameTextSettingsRepository
					.findByCompanyIdAndNameIgnoreCase(companyId, accountNameTextSettingsDTO.getName());
			if (!opAccountNameTextSettings.isPresent()) {
				AccountNameTextSettings accountNameTextSetting = new AccountNameTextSettings();
				accountNameTextSetting.setName(accountNameTextSettingsDTO.getName());
				accountNameTextSetting.setPid(AccountNameTextSettingsService.PID_PREFIX + RandomUtil.generatePid());
				accountNameTextSetting.setCompany(companyRepository.findOne(companyId));
				accountNameTextSettingsRepository.save(accountNameTextSetting);
			}
		}
	}

}
