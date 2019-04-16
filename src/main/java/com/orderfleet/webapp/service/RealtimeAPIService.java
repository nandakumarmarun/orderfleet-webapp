package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.RealtimeAPIDTO;

/**
 * Service Interface for managing RealtimeAPI.
 *
 * @author Sarath
 * @since Apr 10, 2018
 *
 */
public interface RealtimeAPIService {

	RealtimeAPIDTO save(RealtimeAPIDTO realtimeAPIDTO);

	RealtimeAPIDTO update(RealtimeAPIDTO realtimeAPIDTO);

	List<RealtimeAPIDTO> findAll();

	RealtimeAPIDTO findOne(Long id);

	Optional<RealtimeAPIDTO> findOneById(Long pid);

	Optional<RealtimeAPIDTO> findByName(String name);

	void delete(Long id);

	RealtimeAPIDTO updateRealtimeAPIStatus(Long id, boolean activate);

	List<RealtimeAPIDTO> findAllByActivatedRealtimeAPI(boolean activate);
	
	List<RealtimeAPIDTO> findAllByCompanyPid(String companyPid);
}
