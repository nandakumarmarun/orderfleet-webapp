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

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.FormElementUserAccount;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormElementUserAccountRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FormElementUserAccountService;

@Service
@Transactional
public class FormElementUserAccountServiceImpl implements FormElementUserAccountService {
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private FormElementRepository formElementRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private FormElementUserAccountRepository formElementUserAccountRepository;
	
	@Override
	public void save(String formElementPid, String userPid, String assignedAccountProfiles) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		List<FormElementUserAccount> formElementUserAccounts = new ArrayList<>();
		formElementRepository.findOneByPid(formElementPid).ifPresent(fe -> {
			userRepository.findOneByPid(userPid).ifPresent(u -> {
				String[] accountProfiles = assignedAccountProfiles.split(",");
				for (String accountProfilePid : accountProfiles) {
					 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String description ="get one by pid";
						LocalDateTime startLCTime = LocalDateTime.now();
						String startTime = startLCTime.format(DATE_TIME_FORMAT);
						String startDate = startLCTime.format(DATE_FORMAT);
						logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					accountProfileRepository.findOneByPid(accountProfilePid).ifPresent(ap -> {
						//add to save
						FormElementUserAccount feuacc = new FormElementUserAccount();
						feuacc.setFormElement(fe);
						feuacc.setUser(u);
						feuacc.setAccountProfile(ap);
						feuacc.setCompany(company);
						formElementUserAccounts.add(feuacc);
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

				}
				//delete
				formElementUserAccountRepository.deleteByFormElementPidAndUserPid(formElementPid,userPid);
				//save
				if(formElementUserAccounts.size() > 0){
					formElementUserAccountRepository.save(formElementUserAccounts);
				}
			});
		});
	}

}
