package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.EcomProductProfileProduct;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Spring Data JPA repository for the EcomProductProfileProduct entity.
 * 
 * @author Sarath
 * @since Sep 23, 2016
 */
public interface EcomProductProfileProductService {

	void save(String ecomProductProfilePid, String assignedProducts);

	List<ProductProfileDTO> findProductByEcomProductProfilePid(String ecomProductProfilePid);

	List<EcomProductProfileProduct> findAllByCompany();

	List<String>findEcomProductProfilePidsByProductPorfileIn(List<ProductProfileDTO>productProfiles);
}
