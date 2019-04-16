package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.SnrichProductRateDTO;

public interface SnrichProductRateService {

	String PID_PREFIX = "SNRPR-";
	
	SnrichProductRateDTO save(SnrichProductRateDTO snrichProductRateDTO);

	SnrichProductRateDTO update(SnrichProductRateDTO snrichProductRateDTO);
	
	List<SnrichProductRateDTO> GetAllSnrichProductRate();

	Optional<SnrichProductRateDTO> findOneByPid(String pid);
	
	void delete(String pid);
}
