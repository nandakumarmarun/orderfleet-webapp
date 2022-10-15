package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.BussinessUnit;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.RouteCode;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.RouteCodeRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.RouteCodeService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.BussinessUnitDTO;
import com.orderfleet.webapp.web.rest.dto.RouteCodeDTO;

@Service
public class RouteCodeServiceImpl implements RouteCodeService {

	private final CompanyRepository companyRepository;
	
	@Inject
	private final RouteCodeRepository routeCodeRepository;
	

	public RouteCodeServiceImpl(CompanyRepository companyRepository, RouteCodeRepository routeCodeRepository) {
		super();
		this.companyRepository = companyRepository;
		this.routeCodeRepository = routeCodeRepository;
	}

	private final Logger log = LoggerFactory.getLogger(RouteCodeServiceImpl.class);

	@Override
	public void save(List<RouteCodeDTO> routeCodeDTOs) {
		log.info("Saving RouteCode...");
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		long start = System.nanoTime();
		Optional<RouteCode> routeCodeop = null;
		List<RouteCode> RouteCodeList = routeCodeRepository.findAll();
		List<RouteCode> RouteCodes =  new ArrayList();
		for(RouteCodeDTO routeCodeDTO: routeCodeDTOs) {
			routeCodeop =  RouteCodeList.stream().filter(pc -> pc.getMasterCode().equals(routeCodeDTO.getMasterCode())).findAny();
			RouteCode  routeCode  = new RouteCode();
			if(routeCodeop.isPresent()){
				RouteCode BussinessUnitoptional = routeCodeop.get();
				routeCode.setId(BussinessUnitoptional.getId());
				routeCode.setPid(BussinessUnitoptional.getPid());
				routeCode.setMasterCode( routeCodeDTO.getMasterCode());
			
				routeCode.setMasterName(  routeCodeDTO.getMasterName());
				routeCode.setCompany(company);
			}else {
				routeCode.setPid(PID_PREFIX + RandomUtil.generatePid() );
				routeCode.setMasterCode(routeCodeDTO.getMasterCode());
				routeCode.setMasterName(routeCodeDTO.getMasterName());
				routeCode.setCompany(company);
			}
			RouteCodes.add(routeCode);
		}
		routeCodeRepository.save(RouteCodes);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		log.info("Sync completed in {} ms", elapsedTime);	
	}

}
