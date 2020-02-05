package com.orderfleet.webapp.service.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

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
		AccountProfile accountProfile = accountProfileRepository.findOneByPid(accountProfileGeoLocationTaggingDTO.getAccountProfilePid()).get();
		accountProfile.setLatitude(accountProfileGeoLocationTaggingDTO.getLatitude());
		accountProfile.setLongitude(accountProfileGeoLocationTaggingDTO.getLongitude());
		accountProfile.setLocation(accountProfileGeoLocationTaggingDTO.getLocation());
		accountProfile.setGeoTaggingType(accountProfileGeoLocationTaggingDTO.getGeoTaggingType());
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
		return new AccountProfileGeoLocationTaggingDTO(accountProfileGeoLocationTagging.getPid(),accountProfileGeoLocationTagging.getAccountProfile().getPid(), accountProfileGeoLocationTagging.getLongitude(), accountProfileGeoLocationTagging.getLatitude(), accountProfileGeoLocationTagging.getLocation(), accountProfileGeoLocationTagging.getSendDate());
	}

	@Override
	public List<AccountProfileGeoLocationTaggingDTO> getAllAccountProfileGeoLocationTaggingByAccountProfile(
			String pid) {
		List<AccountProfileGeoLocationTagging>accountProfileGeoLocationTaggings=accountProfileGeoLocationTaggingRepository.findAllAccountProfileGeoLocationTaggingByAccountProfilePid(pid);
		List<AccountProfileGeoLocationTaggingDTO>accountProfileGeoLocationTaggingDTOs=setGeoLocationTaggingListToGeoLocationTaggingDTOList(accountProfileGeoLocationTaggings);
		return accountProfileGeoLocationTaggingDTOs;
	}

	public List<AccountProfileGeoLocationTaggingDTO> setGeoLocationTaggingListToGeoLocationTaggingDTOList(List<AccountProfileGeoLocationTagging> accountProfileGeoLocationTaggings) {
		List<AccountProfileGeoLocationTaggingDTO> accountProfileGeoLocationTaggingDTOs=new ArrayList<>();
		for(AccountProfileGeoLocationTagging accountProfileGeoLocationTagging:accountProfileGeoLocationTaggings){
			AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO=new AccountProfileGeoLocationTaggingDTO(accountProfileGeoLocationTagging.getPid(), accountProfileGeoLocationTagging.getAccountProfile().getPid(), accountProfileGeoLocationTagging.getLongitude(), accountProfileGeoLocationTagging.getLatitude(), accountProfileGeoLocationTagging.getLocation(), accountProfileGeoLocationTagging.getSendDate());
			accountProfileGeoLocationTaggingDTOs.add(accountProfileGeoLocationTaggingDTO);
		}
		return accountProfileGeoLocationTaggingDTOs;
		
	}

	@Override
	public Optional<AccountProfileGeoLocationTaggingDTO> findOneByPid(String pid) {
		return accountProfileGeoLocationTaggingRepository.findOneByPid(pid).map(accountProfileGeoLocationTagging -> {
			AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO = new AccountProfileGeoLocationTaggingDTO(accountProfileGeoLocationTagging);
			return accountProfileGeoLocationTaggingDTO;
		});
	}
}
