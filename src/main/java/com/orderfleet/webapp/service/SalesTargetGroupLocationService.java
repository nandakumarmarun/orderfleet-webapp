package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.LocationDTO;

/**
 * Service Interface for managing SalesTargetGroupLocation.
 * 
 * @author Sarath
 * @since Oct 14, 2016
 */
public interface SalesTargetGroupLocationService {

	List<LocationDTO> findSalesTargetGroupLocationsBySalesTargetGroupPid(String salesTargetGroupPid);

	void save(String salesTargetGroupPid, String assignedLocations);
}
