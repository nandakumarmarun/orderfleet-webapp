package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.BrandDeva;
import com.orderfleet.webapp.domain.City;
import com.orderfleet.webapp.domain.DistrictFocus;
import com.orderfleet.webapp.repository.CityFocusRepository;
import com.orderfleet.webapp.service.CityService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.CityFocusDTO;
import com.orderfleet.webapp.web.rest.dto.DistrictFocusDTO;

@Service
public class CityFocusServiceImpl implements CityService {

	private final  CityFocusRepository  cityFocusRepositoy;
	private final Logger log = LoggerFactory.getLogger(CityFocusServiceImpl.class);

	public CityFocusServiceImpl(CityFocusRepository cityFocusRepositoy) {
		super();
		this.cityFocusRepositoy = cityFocusRepositoy;
	}


	@Override
	public void save(List<CityFocusDTO> cityFocusDTOs) {
		log.info("Saving city...");
		long start = System.nanoTime();
		Optional<City> cityop = null;
		List<City> cityList = cityFocusRepositoy.findAll();
		List<City> cities =  new ArrayList();
		for(CityFocusDTO cityFocusDTO : cityFocusDTOs) {
			cityop = cityList.stream().filter(pc -> pc.getMasterCode().equals(cityFocusDTO.getMasterCode())).findAny();
			City city = new City();
			if(cityop.isPresent()){
				
				City cityoptional =cityop .get();
				city.setId(cityoptional.getId());
				city.setPid(cityoptional.getPid());
				city.setMasterCode( cityFocusDTO.getMasterCode());
				
				city.setMasterName( cityFocusDTO.getMasterName());
			}else {
	
				city.setPid(PID_PREFIX + RandomUtil.generatePid() );
				city.setMasterCode( cityFocusDTO.getMasterCode());
		
				city.setMasterName( cityFocusDTO.getMasterName());
			}
			 cities.add(city);
		}
		cityFocusRepositoy.save(cities);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		log.info("Sync completed in {} ms", elapsedTime);
	}

}
