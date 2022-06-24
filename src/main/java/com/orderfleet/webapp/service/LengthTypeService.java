package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.LengthTypeDTO;

public interface LengthTypeService {

	String PID_PREFIX = "BRD-";

	void save(List<LengthTypeDTO> LengthTypeDTO);

	List<LengthTypeDTO> findAllLengthTypes();
}
