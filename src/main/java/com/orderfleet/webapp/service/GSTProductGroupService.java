package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.integration.dto.GSTProductGroupDTO;

/**
 * Service Interface for managing GSTProductGroup.
 *
 * @author Sarath
 * @since Jul 11, 2017
 *
 */
public interface GSTProductGroupService {

	GSTProductGroupDTO save(GSTProductGroupDTO gstProductGroupDTO);

	List<GSTProductGroupDTO> findAllByCompany();
}
