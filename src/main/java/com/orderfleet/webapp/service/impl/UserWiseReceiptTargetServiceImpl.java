package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.UserWiseReceiptTarget;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserWiseReceiptTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserWiseReceiptTargetService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.UserWiseReceiptMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseReceiptTargetDTO;

/**
 * Service Implementation for managing SalesTargetGroupUserTarget.
 * 
 * @author Sarath
 * @since Aug 12, 2016
 */
@Service
@Transactional
public class UserWiseReceiptTargetServiceImpl implements UserWiseReceiptTargetService {

	private final Logger log = LoggerFactory.getLogger(UserWiseReceiptTargetServiceImpl.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private UserWiseReceiptTargetRepository userWiseReceiptTargetRepository;

	@Override
	public UserWiseReceiptTargetDTO saveMonthlyTarget(UserWiseReceiptMonthlyTargetDTO monthlyTargetDTO,
			LocalDate startDate, LocalDate endDate) {

		log.info("Save Monthly Targets User wise receipts");

		UserWiseReceiptTarget userWiseReceiptTarget = new UserWiseReceiptTarget(); // set pid
		userWiseReceiptTarget.setPid(UserWiseReceiptTargetService.PID_PREFIX + RandomUtil.generatePid());
		userWiseReceiptTarget
				.setEmployeeProfile(employeeProfileRepository.findOneByPid(monthlyTargetDTO.getUserPid()).get()); // set
																													// company
		userWiseReceiptTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		userWiseReceiptTarget.setFromDate(startDate);
		userWiseReceiptTarget.setToDate(endDate);
		userWiseReceiptTarget.setAmount(monthlyTargetDTO.getAmount());
		userWiseReceiptTarget.setVolume(monthlyTargetDTO.getVolume());

		userWiseReceiptTarget = userWiseReceiptTargetRepository.save(userWiseReceiptTarget);
		UserWiseReceiptTargetDTO result = new UserWiseReceiptTargetDTO(userWiseReceiptTarget);
		return result;

	}

}