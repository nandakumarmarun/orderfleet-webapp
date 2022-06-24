package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.BrandDeva;
import com.orderfleet.webapp.repository.BrandDevaRepository;
import com.orderfleet.webapp.service.BrandDevaService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.UploadFocusResource;
import com.orderfleet.webapp.web.rest.dto.BankDTO;
import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;

@Service
public class BrandDevaServiceImpl implements BrandDevaService {

	private final BrandDevaRepository brandDevaRepository;
	
	
	public BrandDevaServiceImpl(BrandDevaRepository brandDevaRepository) {
		super();
		this.brandDevaRepository = brandDevaRepository;
	}

	private final Logger log = LoggerFactory.getLogger(BrandDevaServiceImpl.class);
	
	@Override
	public void save(List<BrandDevaDTO> brandDevaDTOs) {
		log.info("Saving brand deva...");
		long start = System.nanoTime();
		Optional<BrandDeva> branddevaop = null;
		List<BrandDeva> branddevaList = brandDevaRepository.findAll();
		List<BrandDeva> brandevas =  new ArrayList();
		for(BrandDevaDTO brandDevaDTO : brandDevaDTOs) {
			branddevaop =  branddevaList.stream().filter(pc -> pc.getMasterCode().equals(brandDevaDTO.getMasterCode())).findAny();
			BrandDeva brandDeva = new BrandDeva();
			if(branddevaop.isPresent()){
			
				BrandDeva branddevoptional = branddevaop.get();
				brandDeva.setId(branddevoptional.getId());
				brandDeva.setPid(branddevoptional.getPid());
				brandDeva.setMasterCode(brandDevaDTO.getMasterCode());
			
				brandDeva.setMasterName(brandDevaDTO.getMasterName());
			}else {
			
				brandDeva.setPid(PID_PREFIX + RandomUtil.generatePid() );
				brandDeva.setMasterCode(brandDevaDTO.getMasterCode());
				
				brandDeva.setMasterName(brandDevaDTO.getMasterName());
			}
			brandevas.add(brandDeva);
		}
		brandDevaRepository.save(brandevas);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		log.info("Sync completed in {} ms", elapsedTime);
	}
	}