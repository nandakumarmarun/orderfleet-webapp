package com.orderfleet.webapp.service;


import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.DistanceFareDTO;

public interface DistanceFareService {
	
	String PID_PREFIX = "DFARE-";

	DistanceFareDTO save(DistanceFareDTO distanceFareDTO);
	
	DistanceFareDTO update(DistanceFareDTO distanceFareDTO);
	
	void delete(String pid);
	
	Optional<DistanceFareDTO> findOneByPid(String pid);
	
	List<DistanceFareDTO> findAllByCompany();

}
