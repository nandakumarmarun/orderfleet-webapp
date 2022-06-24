package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.BrandDeva;
import com.orderfleet.webapp.domain.DistrictFocus;
import com.orderfleet.webapp.repository.BrandDevaRepository;
import com.orderfleet.webapp.repository.DistrictFocusRepository;
import com.orderfleet.webapp.service.DistrictFocusService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.DistrictFocusDTO;

@Service
public class DistrictFocusServiceImpl implements DistrictFocusService {

	private final DistrictFocusRepository  districtFocusRepository ;
	private final Logger log = LoggerFactory.getLogger(DistrictFocusServiceImpl.class);
	
	
	public DistrictFocusServiceImpl(DistrictFocusRepository districtFocusRepository) {
		super();
		this.districtFocusRepository = districtFocusRepository;
	}



	@Override
	public void save(List<DistrictFocusDTO> districtFocusDTOs) {
		log.info("Saving districts...");
		long start = System.nanoTime();
		Optional<DistrictFocus> districtFocusop = null;
		List<DistrictFocus> districtFocusList = districtFocusRepository.findAll();
		List<DistrictFocus> districtFocus =  new ArrayList();
		for(DistrictFocusDTO districtFocusDTO : districtFocusDTOs) {
			districtFocusop=  districtFocusList.stream().filter(pc -> pc.getMasterCode().equals(districtFocusDTO.getMasterCode())).findAny();
			DistrictFocus districtFocusnew  = new DistrictFocus();
			if(districtFocusop.isPresent()){
			
				DistrictFocus branddevoptional = districtFocusop.get();
				districtFocusnew.setId(branddevoptional.getId());
				districtFocusnew.setPid(branddevoptional.getPid());
				districtFocusnew.setMasterCode(districtFocusDTO.getMasterCode());
			
				districtFocusnew.setMasterName(districtFocusDTO.getMasterName());
			}else {
	
				districtFocusnew.setPid(PID_PREFIX + RandomUtil.generatePid() );
				districtFocusnew.setMasterCode(districtFocusDTO.getMasterCode());
				districtFocusnew.setMasterName(districtFocusDTO.getMasterName());
			}
			districtFocus.add(districtFocusnew);
		}
		districtFocusRepository.save(districtFocus);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		log.info("Sync completed in {} ms", elapsedTime);
	}

}
