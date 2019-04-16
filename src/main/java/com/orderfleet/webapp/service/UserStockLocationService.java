package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;



import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Service Interface for managing UserStockLocation.
 * 
 * @author Muhammed Riyas T
 * @since July 19, 2016
 */
public interface UserStockLocationService {

	/**
	 * Save a UserStockLocation.
	 * 
	 * @param userPid
	 * @param assignedStockLocations
	 */
	void save(String userPid, String assignedStockLocations);

	List<StockLocationDTO> findStockLocationsByUserIsCurrentUser();

	List<StockLocationDTO> findStockLocationsByUserPid(String userPid);
	
	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all stockLocationDTOs from StockLocation by status and company user.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	List<StockLocationDTO> findStockLocationsByUserIsCurrentUserAndStockLocationActivated(boolean active);
	
	List<StockLocationDTO> findStockLocationsByUserIsCurrentUserAndStockLocationActivatedAndLastModifiedDate(boolean active,LocalDateTime lastModifiedDate);

	void copyStockLocations(String fromUserPid, List<String> toUserPids);

}
