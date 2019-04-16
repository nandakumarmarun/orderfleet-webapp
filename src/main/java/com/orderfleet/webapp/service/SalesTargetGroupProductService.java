package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Service Interface for managing SalesTargetGroupProduct.
 * 
 * @author Sarath
 * @since Oct 14, 2016
 */
public interface SalesTargetGroupProductService {

	List<ProductProfileDTO> findSalesTargetGroupProductsBySalesTargetGroupPid(String salesTargetGroupPid);

	void save(String salesTargetGroupPid, String assignedProducts);
}
