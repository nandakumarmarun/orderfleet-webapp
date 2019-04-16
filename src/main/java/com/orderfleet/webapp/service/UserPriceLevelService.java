package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;

/**
 * Service Interface for managing UserPriceLevel.
 * 
 * @author Muhammed Riyas T
 * @since August 29, 2016
 */
public interface UserPriceLevelService {

	/**
	 * Save a UserPriceLevel.
	 * 
	 * @param userPid
	 * @param assignedPriceLevels
	 */
	void save(String userPid, String assignedPriceLevels);

	List<PriceLevelDTO> findPriceLevelsByUserIsCurrentUser();

	List<PriceLevelDTO> findPriceLevelsByUserPid(String userPid);
	
	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all priceLevelDTO from PriceLevel by status and user.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	List<PriceLevelDTO> findPriceLevelsByUserIsCurrentUserAndPriceLevelActivated(boolean active);

	List<PriceLevelDTO> findPriceLevelsByUserIsCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate);

	void copyPriceLevels(String fromUserPid, List<String> toUserPids);

}
