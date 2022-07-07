package com.orderfleet.webapp.service;

import com.orderfleet.webapp.domain.AlterIdMaster;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AlterIdMasterDTO;

public interface AlterIdMasterService {

	String PID_PREFIX = "AIM-";

	/**
	 * Save a alteridMasterDto.
	 *
	 * @param alterIdmaster the entity to save
	 * @return the persisted entity
	 */

	AlterIdMasterDTO save(AlterIdMasterDTO alteridMasterDTO);

	/**
	 * Get id alteridmaster
	 * 
	 * @param id the id of the entity
	 * @return the entity
	 */

	AlterIdMasterDTO findByMasterName(String masterName ,Long companyId);

	AlterIdMaster alterIdMasterDTOToAlterIdMaster(AlterIdMasterDTO alterIdMasterDTO);

	AlterIdMasterDTO alterIdMasterToAlterIdMasterDTO(AlterIdMaster alterIdMaster);
}
