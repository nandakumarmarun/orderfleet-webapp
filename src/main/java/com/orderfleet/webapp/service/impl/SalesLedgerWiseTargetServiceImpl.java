package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.SalesLedgerWiseTarget;
import com.orderfleet.webapp.domain.UserWiseSalesTarget;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.SalesLedgerRepository;
import com.orderfleet.webapp.repository.SalesLedgerWiseTargetRepository;
import com.orderfleet.webapp.repository.UserWiseSalesTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SalesLedgerWiseTargetService;
import com.orderfleet.webapp.service.UserWiseSalesTargetService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerWiseMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesLedgerWiseTargetDTO;
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
public class SalesLedgerWiseTargetServiceImpl implements SalesLedgerWiseTargetService {

	private final Logger log = LoggerFactory.getLogger(SalesLedgerWiseTargetServiceImpl.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private SalesLedgerRepository salesLedgerRepository;

	@Inject
	private SalesLedgerWiseTargetRepository salesLedgerWiseTargetRepository;

	@Override
	public SalesLedgerWiseTargetDTO saveMonthlyTarget(SalesLedgerWiseMonthlyTargetDTO monthlyTargetDTO,
			LocalDate startDate, LocalDate endDate) {

		log.info("Save Monthly Target User wise sales");

		SalesLedgerWiseTarget salesLedgerWiseTarget = new SalesLedgerWiseTarget(); // set pid
		salesLedgerWiseTarget.setPid(SalesLedgerWiseTargetService.PID_PREFIX + RandomUtil.generatePid());
		salesLedgerWiseTarget
				.setSalesLedger(salesLedgerRepository.findOneByPid(monthlyTargetDTO.getSalesLedgerPid()).get()); // set
																													// company
		salesLedgerWiseTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		salesLedgerWiseTarget.setFromDate(startDate);
		salesLedgerWiseTarget.setToDate(endDate);
		salesLedgerWiseTarget.setAmount(monthlyTargetDTO.getAmount());

		salesLedgerWiseTarget = salesLedgerWiseTargetRepository.save(salesLedgerWiseTarget);
		SalesLedgerWiseTargetDTO result = new SalesLedgerWiseTargetDTO(salesLedgerWiseTarget);
		return result;

	}

}