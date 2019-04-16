package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
					accountProfileRepository.findOneByPid(accountProfilePid).ifPresent(ap -> {
						//add to save
						FormElementUserAccount feuacc = new FormElementUserAccount();
						feuacc.setFormElement(fe);
						feuacc.setUser(u);
						feuacc.setAccountProfile(ap);
						feuacc.setCompany(company);
						formElementUserAccounts.add(feuacc);
					});
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
