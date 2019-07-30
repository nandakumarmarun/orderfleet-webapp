package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.UserWiseSalesTarget;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserWiseSalesTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserWiseSalesTargetService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.UserWiseSalesMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseSalesTargetDTO;

/**
 * Service Implementation for managing SalesTargetGroupUserTarget.
 * 
 * @author Sarath
 * @since Aug 12, 2016
 */
@Service
@Transactional
public class UserWiseSalesTargetServiceImpl implements UserWiseSalesTargetService {

	private final Logger log = LoggerFactory.getLogger(UserWiseSalesTargetServiceImpl.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private UserWiseSalesTargetRepository userWiseSalesTargetRepository;

	@Override
	public UserWiseSalesTargetDTO saveMonthlyTarget(UserWiseSalesMonthlyTargetDTO monthlyTargetDTO, LocalDate startDate,
			LocalDate endDate) {

		log.info("Save Monthly Target User wise sales");

		UserWiseSalesTarget userWiseSalesTarget = new UserWiseSalesTarget(); // set pid
		userWiseSalesTarget.setPid(UserWiseSalesTargetService.PID_PREFIX + RandomUtil.generatePid());
		userWiseSalesTarget
				.setEmployeeProfile(employeeProfileRepository.findOneByPid(monthlyTargetDTO.getUserPid()).get()); // set
																													// company
		userWiseSalesTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		userWiseSalesTarget.setFromDate(startDate);
		userWiseSalesTarget.setToDate(endDate);
		userWiseSalesTarget.setAmount(monthlyTargetDTO.getAmount());
		userWiseSalesTarget.setVolume(monthlyTargetDTO.getVolume());

		userWiseSalesTarget = userWiseSalesTargetRepository.save(userWiseSalesTarget);
		UserWiseSalesTargetDTO result = new UserWiseSalesTargetDTO(userWiseSalesTarget);
		return result;

	}

}