package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupSalesTargetGroupDTO;

public interface ProductGroupSalesTargetGroupService {

	void saveProductGroupSalesTargetGroup(String productGroupPid, String SalesTargetGroupPids);

	List<ProductGroupSalesTargetGroupDTO> findAllByCompanyId();

	ProductGroupSalesTargetGroupDTO findAllByProductGroupPid(String productGroupPid);

	List<ProductGroupDTO> findAllProductGroupByCompany();
}
