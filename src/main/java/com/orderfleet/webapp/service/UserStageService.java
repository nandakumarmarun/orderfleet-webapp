package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.StageDTO;
import com.orderfleet.webapp.web.rest.dto.UserStageDTO;

/**
 * Service Interface for managing UserStage.
 * 
 * @author Muhammed Riyas T
 * @since August 29, 2016
 */

public interface UserStageService {

	/**
	 * Save a UserStage.
	 * 
	 * @param userPid
	 * @param assignedStages
	 */
	void save(String userPid, String assignedStages);

	List<UserStageDTO> findUserStagesByUserPid(String userPid);

}
