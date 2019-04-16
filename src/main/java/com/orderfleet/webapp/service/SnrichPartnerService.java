package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.SnrichPartnerDTO;

public interface SnrichPartnerService {

	String PID_PREFIX = "PTNR-";
	
	SnrichPartnerDTO save(SnrichPartnerDTO snrichPartnerDTO);

	SnrichPartnerDTO update(SnrichPartnerDTO snrichPartnerDTO);

	List<SnrichPartnerDTO> GetAllActivatedPartners(boolean activated);

	List<SnrichPartnerDTO> GetAllPartners();

	Optional<SnrichPartnerDTO> findOneByPid(String pid);

	SnrichPartnerDTO updatePartnerStatus(String pid, Boolean activated);
}
