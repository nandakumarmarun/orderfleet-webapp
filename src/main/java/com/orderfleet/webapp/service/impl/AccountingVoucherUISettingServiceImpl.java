package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		accountingVoucherUISetting.setActivity(activityRepository.findOneByPid(accountingVoucherUISettingsDTO.getActivityPid()).get());
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		accountingVoucherUISetting.setActivity(activityRepository.findOneByPid(accountingVoucherUISettingsDTO.getActivityPid()).get());

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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AVUI_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherUISetting>accountingVoucherUISettings=accountingVoucherUISettingRepository.findAllByCompanyId();
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AVUI_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId and name ignore case";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<AccountingVoucherUISettingDTO> AVUIDTO=	 accountingVoucherUISettingRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(accountingVoucherUISetting -> {
					AccountingVoucherUISettingDTO accountingVoucherUISettingDTO=new AccountingVoucherUISettingDTO(accountingVoucherUISetting);
					return accountingVoucherUISettingDTO;
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
					return AVUIDTO;
	}
}
