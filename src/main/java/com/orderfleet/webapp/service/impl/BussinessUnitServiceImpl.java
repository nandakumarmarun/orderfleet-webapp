package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.BussinessUnit;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.StateFocus;
import com.orderfleet.webapp.repository.BussinessUnitRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.BussinessUnitService;
import com.orderfleet.webapp.service.impl.DistrictFocusServiceImpl;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.BussinessUnitDTO;
import com.orderfleet.webapp.web.rest.dto.StateFocusDTO;

@Service
public class BussinessUnitServiceImpl implements BussinessUnitService{

	private final BussinessUnitRepository bussinessUnitRepository ;
	private final CompanyRepository companyRepository;
	private final Logger log = LoggerFactory.getLogger(DistrictFocusServiceImpl.class);
	
	
	
	public BussinessUnitServiceImpl(BussinessUnitRepository bussinessUnitRepository,
			CompanyRepository companyRepository) {
		super();
		this.bussinessUnitRepository = bussinessUnitRepository;
		this.companyRepository = companyRepository;
	}


	@Override
	public void save(List<BussinessUnitDTO> bussinessUnitDTOs) {
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		log.info("Saving bussiness unit...");
		long start = System.nanoTime();
		Optional<BussinessUnit> bussinessUnitop = null;
		List<BussinessUnit> bussinessUnitList = bussinessUnitRepository.findAll();
		List<BussinessUnit> bussinessUnits =  new ArrayList();
		for(BussinessUnitDTO bussinessUnitDTO : bussinessUnitDTOs) {
			bussinessUnitop =  bussinessUnitList.stream().filter(pc -> pc.getMasterCode().equals( bussinessUnitDTO.getMasterCode())).findAny();
			BussinessUnit bussinessUnit = new BussinessUnit();
			if(bussinessUnitop.isPresent()){
				
				BussinessUnit BussinessUnitoptional = bussinessUnitop.get();
				bussinessUnit.setId(BussinessUnitoptional.getId());
				bussinessUnit.setPid(BussinessUnitoptional.getPid());
				bussinessUnit .setMasterCode( bussinessUnitDTO.getMasterCode());
				
				bussinessUnit.setMasterName(  bussinessUnitDTO.getMasterName());
				bussinessUnit.setCompany(BussinessUnitoptional.getCompany());
			}else {
			
				bussinessUnit .setPid(PID_PREFIX + RandomUtil.generatePid() );
				bussinessUnit .setMasterCode(  bussinessUnitDTO.getMasterCode());

				bussinessUnit.setMasterName(  bussinessUnitDTO.getMasterName());
				bussinessUnit.setCompany(company);
			}
			bussinessUnits.add(bussinessUnit);
		}
		bussinessUnitRepository.save(bussinessUnits);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		log.info("Sync completed in {} ms", elapsedTime);
	}

}
