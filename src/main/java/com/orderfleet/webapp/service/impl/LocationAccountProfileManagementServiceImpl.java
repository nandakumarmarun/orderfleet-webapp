package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileManagmentDTO;

@Service
@Transactional
public class LocationAccountProfileManagementServiceImpl {

	private final Logger log = LoggerFactory.getLogger(LocationAccountProfileManagementServiceImpl.class);

	@Inject
	LocationAccountProfileRepository locationAccountProfileRepository;

	@Transactional(readOnly = true)
	public List<LocationAccountProfileManagmentDTO> findAllByCompanyAndActivated(boolean active) {

		List<Object[]> locationaccpro = locationAccountProfileRepository.findAllByAccountProfileOptimised();;
	
		List<LocationAccountProfileManagmentDTO> list = new ArrayList<LocationAccountProfileManagmentDTO>();

		for ( Object[] obj : locationaccpro) {
			LocationAccountProfileManagmentDTO accDTO = new LocationAccountProfileManagmentDTO();
			accDTO.setLocationPid(obj[1].toString());
			accDTO.setLocationName(obj[3].toString());
			accDTO.setPid(obj[4].toString());
			accDTO.setName(obj[5].toString());
			
			
			list.add(accDTO);
		}
		list.sort((LocationAccountProfileManagmentDTO s1, LocationAccountProfileManagmentDTO s2) -> s1.getName().compareTo(s2.getName()));

		return list;

	}
}
