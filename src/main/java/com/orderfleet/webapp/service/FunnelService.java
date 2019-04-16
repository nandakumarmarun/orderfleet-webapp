package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.Funnel;

public interface FunnelService {

	Funnel save(String name, int sortOrder);

	Funnel update(Long id, String name, int sortOrder);
	
	Funnel findOne(Long id);

	Optional<Funnel> findByName(String name);

	void delete(Long id);
	
	List<Funnel> findAllByCompany();
	
}
