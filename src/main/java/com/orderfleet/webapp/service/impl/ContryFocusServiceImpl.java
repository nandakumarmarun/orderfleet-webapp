package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.ContryFocus;
import com.orderfleet.webapp.domain.DistrictFocus;
import com.orderfleet.webapp.domain.LengthType;
import com.orderfleet.webapp.repository.ContryFocusRepository;
import com.orderfleet.webapp.repository.DistrictFocusRepository;
import com.orderfleet.webapp.service.ContryFocusService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ContryFocusDTO;
import com.orderfleet.webapp.web.rest.dto.DistrictFocusDTO;
import com.orderfleet.webapp.web.rest.dto.LengthTypeDTO;
@Service
public class ContryFocusServiceImpl implements ContryFocusService {
	
	
	private final ContryFocusRepository contryFocusRepository ;
	private final Logger log = LoggerFactory.getLogger(DistrictFocusServiceImpl.class);
	
	
	
	public ContryFocusServiceImpl(ContryFocusRepository contryFocusRepository) {
		super();
		this.contryFocusRepository = contryFocusRepository;
	}

	@Override
	public void save(List<ContryFocusDTO> contryFocusDTOs) {
		log.info("Saving Country...");
		long start = System.nanoTime();
		Optional<ContryFocus> contryFocusop = null;
		List<ContryFocus> contryFocusList = contryFocusRepository.findAll();
		List<ContryFocus> countryFocus =  new ArrayList();
		for(ContryFocusDTO contryFocusDTO : contryFocusDTOs) {
			contryFocusop =  contryFocusList.stream().filter(pc -> pc.getMasterCode().equals(contryFocusDTO.getMasterCode())).findAny();
			ContryFocus contryFocus = new ContryFocus();
			if(contryFocusop.isPresent()){
	
				ContryFocus contryFocusoptional = contryFocusop.get();
				contryFocus.setId(contryFocusoptional.getId());
				contryFocus.setPid(contryFocusoptional.getPid());
				contryFocus.setMasterCode(  contryFocusDTO.getMasterCode());
				
				contryFocus.setMasterName(contryFocusDTO.getMasterName());
			}else {
	
				contryFocus.setPid(PID_PREFIX + RandomUtil.generatePid() );
				contryFocus.setMasterCode(contryFocusDTO.getMasterCode());
				
				contryFocus.setMasterName( contryFocusDTO.getMasterName());
			}
			countryFocus.add(contryFocus);
		}
		contryFocusRepository.save(countryFocus);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		log.info("Sync completed in {} ms", elapsedTime);
	}

}
