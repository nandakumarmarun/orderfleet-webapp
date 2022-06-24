package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.BrandDeva;
import com.orderfleet.webapp.domain.LengthType;
import com.orderfleet.webapp.domain.StateFocus;
import com.orderfleet.webapp.repository.BrandDevaRepository;
import com.orderfleet.webapp.repository.LengthTypeRepository;
import com.orderfleet.webapp.service.LengthTypeService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.LengthTypeDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.StateFocusDTO;

@Service
public class LengthTypeServiceImpl implements LengthTypeService {
	
	private final LengthTypeRepository lengthTypeRepository;
	
	private final Logger log = LoggerFactory.getLogger(BrandDevaServiceImpl.class);


	public LengthTypeServiceImpl(LengthTypeRepository lengthTypeRepository) {
		super();
		this.lengthTypeRepository = lengthTypeRepository;
	}



	@Override
	public void save(List<LengthTypeDTO> LengthTypeDTO) {
		log.info("Saving length type...");
		long start = System.nanoTime();
		Optional<LengthType> lengthTypeop = null;
		List<LengthType> lengthTypeList = lengthTypeRepository.findAll();
		List<LengthType> lengthTypes =  new ArrayList();
		for(LengthTypeDTO lengthTypeDTO : LengthTypeDTO) {
			lengthTypeop =  lengthTypeList.stream().filter(pc -> pc.getMasterCode().equals( lengthTypeDTO.getMasterCode())).findAny();
			LengthType lengthType = new LengthType();
			if(lengthTypeop.isPresent()){
				LengthType cityoptional = lengthTypeop.get();
				lengthType.setId(cityoptional.getId());
				lengthType.setPid(cityoptional.getPid());
				lengthType .setMasterCode(  lengthTypeDTO .getMasterCode());
	
				lengthType.setMasterName(lengthTypeDTO.getMasterName());
				lengthType.setMeterConversion(lengthTypeDTO.getMeterConversion());
			}else {
				lengthType.setPid(PID_PREFIX + RandomUtil.generatePid() );
				lengthType.setMasterCode(lengthTypeDTO.getMasterCode());
				lengthType.setMasterName( lengthTypeDTO.getMasterName());
				lengthType.setMeterConversion(lengthTypeDTO.getMeterConversion());
			}
			lengthTypes.add(lengthType);
		}
		lengthTypeRepository.save(lengthTypes);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		log.info("Sync completed in {} ms", elapsedTime);
	}


	@Override
	public List<LengthTypeDTO> findAllLengthTypes() {
		List<LengthType> lengthTypes = lengthTypeRepository.findAll();
		List<LengthTypeDTO> LengthTypeDTOs = lengthTypes.stream().map(LengthTypeDTO::new).collect(Collectors.toList());
		return LengthTypeDTOs;
	}

}
