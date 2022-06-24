package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.BrandDeva;
import com.orderfleet.webapp.domain.City;
import com.orderfleet.webapp.domain.StateFocus;
import com.orderfleet.webapp.repository.StateFocusRepository;
import com.orderfleet.webapp.service.StateFocusService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.CityFocusDTO;
import com.orderfleet.webapp.web.rest.dto.StateFocusDTO;
@Service
public class StateFocusServiceImpl implements StateFocusService {

	private final StateFocusRepository stateFocusRepository;
	
	private final Logger log = LoggerFactory.getLogger(BrandDevaServiceImpl.class);
	
	public StateFocusServiceImpl(StateFocusRepository stateFocusRepository) {
		super();
		this.stateFocusRepository = stateFocusRepository;
	}

	@Override
	public void save(List<StateFocusDTO> StateFocusDTOs) {
		log.info("Saving state...");
		long start = System.nanoTime();
		Optional<StateFocus> stateop = null;
		List<StateFocus> stateList = stateFocusRepository.findAll();
		List<StateFocus> states =  new ArrayList();
		for(StateFocusDTO stateFocusDTO : StateFocusDTOs) {
			stateop =  stateList.stream().filter(pc -> pc.getMasterCode().equals( stateFocusDTO.getMasterCode())).findAny();
			StateFocus state = new StateFocus();
			if(stateop.isPresent()){
			
				StateFocus cityoptional = stateop .get();
				state.setId(cityoptional.getId());
				state.setPid(cityoptional.getPid());
				state .setMasterCode( stateFocusDTO.getMasterCode());
			
				state.setMasterName(  stateFocusDTO.getMasterName());
			}else {
	
				state .setPid(PID_PREFIX + RandomUtil.generatePid() );
				state .setMasterCode(  stateFocusDTO.getMasterCode());
				state .setMasterName(  stateFocusDTO.getMasterName());
			}
			states.add(state);
		}
		stateFocusRepository.save(states);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		log.info("Sync completed in {} ms", elapsedTime);
	}

}
