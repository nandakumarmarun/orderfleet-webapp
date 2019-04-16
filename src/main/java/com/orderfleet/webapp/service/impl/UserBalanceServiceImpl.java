package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.UserBalance;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserBalanceRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserBalanceService;
import com.orderfleet.webapp.web.rest.dto.UserBalanceDTO;

@Service
@Transactional
public class UserBalanceServiceImpl implements UserBalanceService{
	
	@Inject
	private UserBalanceRepository userBalanceRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private CompanyRepository companyRepository;

	@Override
	public UserBalanceDTO save(UserBalanceDTO userBalanceDTO) {
		UserBalance userBalance=new UserBalance();
		userBalance.setAmount(userBalanceDTO.getAmount());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
		LocalDate fromDateTime = LocalDate.parse(userBalanceDTO.getDateString(), formatter);
		LocalDateTime fromDate = fromDateTime.atTime(0, 0);
		userBalance.setDate(fromDate);
		userBalance.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		userBalance.setUser(userRepository.findOneByPid(userBalanceDTO.getUserPid()).get());
		userBalance.setCreatedBy(SecurityUtils.getCurrentUserLogin());
		userBalance.setRemarks(userBalanceDTO.getRemarks());
		userBalance=userBalanceRepository.save(userBalance);
		UserBalanceDTO userBalanceDTO2=new UserBalanceDTO(userBalance);
		return userBalanceDTO2;
	}

	@Override
	public UserBalanceDTO update(UserBalanceDTO userBalanceDTO) {
		UserBalance userBalance=userBalanceRepository.findOne(userBalanceDTO.getId());
		userBalance.setAmount(userBalanceDTO.getAmount());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
		LocalDate fromDateTime = LocalDate.parse(userBalanceDTO.getDateString(), formatter);
		LocalDateTime fromDate = fromDateTime.atTime(0, 0);
		userBalance.setDate(fromDate);
		userBalance.setUser(userRepository.findOneByPid(userBalanceDTO.getUserPid()).get());
		userBalance.setCreatedBy(SecurityUtils.getCurrentUserLogin());
		userBalance.setRemarks(userBalanceDTO.getRemarks());
		userBalance=userBalanceRepository.save(userBalance);
		UserBalanceDTO userBalanceDTO2=new UserBalanceDTO(userBalance);
		return userBalanceDTO2;
	}

	@Override
	public List<UserBalanceDTO> findAllByCompany() {
		List<UserBalance> userBalances = userBalanceRepository.findAllByCompanyId();
		List<UserBalanceDTO>userBalanceDTOs=new ArrayList<>();
		for(UserBalance userBalance:userBalances) {
			UserBalanceDTO userBalanceDTO=new UserBalanceDTO(userBalance);
			userBalanceDTOs.add(userBalanceDTO);
		}
		return userBalanceDTOs;
	}

	@Override
	public UserBalanceDTO findOne(Long id) {
		UserBalance userBalance=userBalanceRepository.findOne(id);
		UserBalanceDTO userBalanceDTO=new UserBalanceDTO(userBalance);
		return userBalanceDTO;
	}

	@Override
	public void delete(Long id) {
		userBalanceRepository.delete(id);
	}

}
