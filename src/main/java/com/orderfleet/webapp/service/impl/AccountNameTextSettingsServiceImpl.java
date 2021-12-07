package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "ANTS_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			AccountNameTextSettings accountNameTextSettings = accountNameTextSettingsRepository
					.findOneByPid(accountNameTextSettingsDTO.getPid()).get();
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ANTS_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountNameTextSettings> accountNameTextSettings = accountNameTextSettingsRepository.findAllByCompanyId();
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ANTS_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<AccountNameTextSettingsDTO>accNameTextDTO=	 accountNameTextSettingsRepository.findOneByPid(pid).map(accountNameTextSettings -> {
			AccountNameTextSettingsDTO accountNameTextSettingsDTO = new AccountNameTextSettingsDTO(
					accountNameTextSettings);
			return accountNameTextSettingsDTO;
		});
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
					return accNameTextDTO;
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ANTS_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compId and name ignore case";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountNameTextSettingsDTO>accountNameDTO= accountNameTextSettingsRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(accountNameTextSettings -> {
					AccountNameTextSettingsDTO accountNameTextSettingsDTO = new AccountNameTextSettingsDTO(
							accountNameTextSettings);
					return accountNameTextSettingsDTO;
				});
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
					return accountNameDTO;
	}

	@Override
	public void saveDefault(List<AccountNameTextSettingsDTO> accountNameTextSettingsDTOs) {
		log.debug("Request to save AccountNameTextSettings : {}", accountNameTextSettingsDTOs);
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		for (AccountNameTextSettingsDTO accountNameTextSettingsDTO : accountNameTextSettingsDTOs) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "ANTS_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get all by compId and name ignore case";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<AccountNameTextSettings> opAccountNameTextSettings = accountNameTextSettingsRepository
					.findByCompanyIdAndNameIgnoreCase(companyId, accountNameTextSettingsDTO.getName());
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
