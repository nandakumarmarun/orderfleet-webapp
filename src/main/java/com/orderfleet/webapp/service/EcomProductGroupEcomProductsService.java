package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.EcomProductGroupEcomProduct;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;

/**
 * Spring Data JPA repository for the ProductGroupEcomProduct entity.
 * 
 * @author Sarath
 * @since Sep 24, 2016
 */
public interface EcomProductGroupEcomProductsService {

	void save(String productGroupPid, String assignedProducts);

	List<EcomProductProfileDTO> findEcomProductByProductGroupPid(String productGroupPid);

	List<EcomProductGroupEcomProduct> findAllByCompany();

	List<EcomProductGroupDTO> findAllProductGroupByEcomProductPidIn(List<String> ecomProductPids);
}
