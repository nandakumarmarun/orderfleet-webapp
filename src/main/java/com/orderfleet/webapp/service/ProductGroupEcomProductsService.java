package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.ProductGroupEcomProduct;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;

/**
 * Spring Data JPA repository for the ProductGroupEcomProduct entity.
 * 
 * @author Sarath
 * @since Sep 24, 2016
 */
public interface ProductGroupEcomProductsService {

	void save(String productGroupPid, String assignedProducts);

	List<EcomProductProfileDTO> findEcomProductByProductGroupPid(String productGroupPid);

	List<ProductGroupEcomProduct> findAllByCompany();

	List<ProductGroupDTO> findAllProductGroupByEcomProductPidIn(List<String> ecomProductPids);
}
