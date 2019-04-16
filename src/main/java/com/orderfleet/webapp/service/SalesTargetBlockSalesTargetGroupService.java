package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.SalesTargetBlockSalesTargetGroup;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupDTO;

/**
 * Service Interface for managing SalesTargetBlockSalesTargetGroup.
 *
 * @author Sarath
 * @since Feb 22, 2017
 */
public interface SalesTargetBlockSalesTargetGroupService {

	void save(String salesTargetBlockPid, String assignedSalesTargetgroups);

	List<SalesTargetGroupDTO> findSalesTargetGroupsBySalesTargetBlockPid(String salesTargetBlockPid);

	List<SalesTargetBlockSalesTargetGroup> findAllByCompanyId();
}
