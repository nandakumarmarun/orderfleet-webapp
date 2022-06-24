package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.ContryFocus;
import com.orderfleet.webapp.domain.FiscalYear;
import com.orderfleet.webapp.domain.RouteCode;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FiscalYearRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FiscalYearService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ContryFocusDTO;
import com.orderfleet.webapp.web.rest.dto.FiscalYearDTO;
import com.orderfleet.webapp.web.rest.dto.RouteCodeDTO;

@Service
public class FiscalYearServiceImpl implements FiscalYearService {
	
	
	private final CompanyRepository companyRepository;
	private final FiscalYearRepository fiscalYearRepository;
	
	private final Logger log = LoggerFactory.getLogger(FiscalYearServiceImpl.class);
	
	public FiscalYearServiceImpl(CompanyRepository companyRepository, FiscalYearRepository fiscalYearRepository) {
		super();
		this.companyRepository = companyRepository;
		this.fiscalYearRepository = fiscalYearRepository;
	}

	@Override
	public void save(List<FiscalYearDTO> FiscalYearDTOs) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		log.info("Saving fiscal year...");
		long start = System.nanoTime();
		Optional<FiscalYear> fiscalyearop = null;
		List<FiscalYear> fiscalyearList = fiscalYearRepository.findAll();
		List<FiscalYear> fiscalYears =  new ArrayList();
		for(FiscalYearDTO fiscalyearDTO :FiscalYearDTOs) {
			fiscalyearop = fiscalyearList.stream().filter(pc -> pc.getMasterCode().equals(fiscalyearDTO.getMasterCode())).findAny();
			FiscalYear fiscalYear = new FiscalYear();
			if( fiscalyearop.isPresent()){
				
				FiscalYear fiscalYearoptional = fiscalyearop.get();
				fiscalYear.setId(fiscalYearoptional.getId());
				fiscalYear.setPid(fiscalYearoptional.getPid());
				fiscalYear.setMasterCode( fiscalyearDTO.getMasterCode());
			
				fiscalYear.setMasterName(fiscalyearDTO.getMasterName());
				fiscalYear.setCompany(fiscalYearoptional.getCompany());
			}else {
				
				fiscalYear.setPid(PID_PREFIX + RandomUtil.generatePid() );
				fiscalYear.setMasterCode(fiscalyearDTO.getMasterCode());
			
				fiscalYear.setMasterName( fiscalyearDTO.getMasterName());
				fiscalYear.setCompany(company);
			}
			fiscalYears.add(fiscalYear);
		}
		fiscalYearRepository.save(fiscalYears);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		log.info("Sync completed in {} ms", elapsedTime);
	}

}
