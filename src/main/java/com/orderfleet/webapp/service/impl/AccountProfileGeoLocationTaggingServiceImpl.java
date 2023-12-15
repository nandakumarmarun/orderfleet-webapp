package com.orderfleet.webapp.service.impl;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountProfileGeoLocationTagging;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.AccountProfileGeoLocationTaggingRepository;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileGeoLocationTaggingService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.AccountProfileGeoLocationTaggingDTO;

@Service
@Transactional
public class AccountProfileGeoLocationTaggingServiceImpl implements AccountProfileGeoLocationTaggingService{
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountProfileRepository accountProfileRepository;
	@Inject
	private CompanyRepository companyRepository;
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private AccountProfileGeoLocationTaggingRepository accountProfileGeoLocationTaggingRepository;
	
	@Override
	public AccountProfileGeoLocationTaggingDTO save(
			AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfileGeoLocationTaggingDTO.getAccountProfilePid()).get();
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

		accountProfile.setLatitude(accountProfileGeoLocationTaggingDTO.getLatitude());
		accountProfile.setLongitude(accountProfileGeoLocationTaggingDTO.getLongitude());
		accountProfile.setLocation(accountProfileGeoLocationTaggingDTO.getLocation());
		accountProfile.setGeoTaggingType(accountProfileGeoLocationTaggingDTO.getGeoTaggingType());
        accountProfile.setGeoTaggingStatus(accountProfileGeoLocationTaggingDTO.getGeoTaggingStatus());
//		accountProfile.setGeoTaggedTime(accountProfileGeoLocationTaggingDTO.getSendDate());
		accountProfileRepository.save(accountProfile);
		
		User user=userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		Company company=companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		
		AccountProfileGeoLocationTagging accountProfileGeoLocationTagging=new AccountProfileGeoLocationTagging();
		accountProfileGeoLocationTagging.setPid(AccountProfileGeoLocationTaggingService.PID_PREFIX+RandomUtil.generatePid());
		accountProfileGeoLocationTagging.setAccountProfile(accountProfile);
		accountProfileGeoLocationTagging.setLatitude(accountProfileGeoLocationTaggingDTO.getLatitude());
		accountProfileGeoLocationTagging.setLongitude(accountProfileGeoLocationTaggingDTO.getLongitude());
		accountProfileGeoLocationTagging.setLocation(accountProfileGeoLocationTaggingDTO.getLocation());
		accountProfileGeoLocationTagging.setUser(user);
		accountProfileGeoLocationTagging.setCompany(company);
		accountProfileGeoLocationTagging.setSendDate(accountProfileGeoLocationTaggingDTO.getSendDate());
		accountProfileGeoLocationTagging = accountProfileGeoLocationTaggingRepository.save(accountProfileGeoLocationTagging);
		return new AccountProfileGeoLocationTaggingDTO(accountProfileGeoLocationTagging.getPid(),accountProfileGeoLocationTagging.getAccountProfile().getPid(), accountProfileGeoLocationTagging.getLongitude(), accountProfileGeoLocationTagging.getLatitude(), accountProfileGeoLocationTagging.getLocation(), accountProfileGeoLocationTagging.getSendDate(),accountProfileGeoLocationTagging.getUser().getFirstName(),accountProfileGeoLocationTagging.getAccountProfile().getGeoTaggingStatus());
	}

	@Override
	public List<AccountProfileGeoLocationTaggingDTO> getAllAccountProfileGeoLocationTaggingByAccountProfile(
			String pid) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "APGT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by accountProfilePid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfileGeoLocationTagging>accountProfileGeoLocationTaggings=accountProfileGeoLocationTaggingRepository.findAllAccountProfileGeoLocationTaggingByAccountProfilePid(pid);
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

		List<AccountProfileGeoLocationTaggingDTO>accountProfileGeoLocationTaggingDTOs=setGeoLocationTaggingListToGeoLocationTaggingDTOList(accountProfileGeoLocationTaggings);
		return accountProfileGeoLocationTaggingDTOs;
	}
	@Override
	public List<AccountProfileGeoLocationTaggingDTO> getAllAccountProfileGeoLocationTaggingByAccountProfileNew(String pid, AccountProfile accountProfile) {
		List<AccountProfileGeoLocationTagging>accountProfileGeoLocationTaggings=accountProfileGeoLocationTaggingRepository.findAllAccountProfileGeoLocationTaggingByAccountProfilePid(pid);
		List<AccountProfileGeoLocationTagging> filterByLongitudeAndLatitude = accountProfileGeoLocationTaggings.stream().filter(data -> data.getLatitude().equals(accountProfile.getLatitude()) && data.getLongitude().equals(accountProfile.getLongitude())).collect(Collectors.toList());
		List<AccountProfileGeoLocationTaggingDTO>accountProfileGeoLocationTaggingDTOs=setGeoLocationTaggingListToGeoLocationTaggingDTOList(filterByLongitudeAndLatitude);
		return accountProfileGeoLocationTaggingDTOs;
	}

	public List<AccountProfileGeoLocationTaggingDTO> setGeoLocationTaggingListToGeoLocationTaggingDTOList(List<AccountProfileGeoLocationTagging> accountProfileGeoLocationTaggings) {
		List<AccountProfileGeoLocationTaggingDTO> accountProfileGeoLocationTaggingDTOs=new ArrayList<>();
		for(AccountProfileGeoLocationTagging accountProfileGeoLocationTagging:accountProfileGeoLocationTaggings){
			AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO=new AccountProfileGeoLocationTaggingDTO(accountProfileGeoLocationTagging.getPid(), accountProfileGeoLocationTagging.getAccountProfile().getPid(), accountProfileGeoLocationTagging.getLongitude(), accountProfileGeoLocationTagging.getLatitude(), accountProfileGeoLocationTagging.getLocation(), accountProfileGeoLocationTagging.getSendDate(),accountProfileGeoLocationTagging.getUser().getLogin(),accountProfileGeoLocationTagging.getAccountProfile().getGeoTaggingStatus());
			accountProfileGeoLocationTaggingDTOs.add(accountProfileGeoLocationTaggingDTO);
		}
		return accountProfileGeoLocationTaggingDTOs;
		
	}

	@Override
	public Optional<AccountProfileGeoLocationTaggingDTO> findOneByPid(String pid) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "APGT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfileGeoLocationTaggingDTO>apgtDTO=	 accountProfileGeoLocationTaggingRepository.findOneByPid(pid).map(accountProfileGeoLocationTagging -> {
			AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO = new AccountProfileGeoLocationTaggingDTO(accountProfileGeoLocationTagging);
			return accountProfileGeoLocationTaggingDTO;
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
				return apgtDTO;

	}
}
