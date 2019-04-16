package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.TallyCompany;

/**
 * Service Interface for managing ProductCategory.
 *
 * @author Sarath
 * @since Feb 12, 2018
 *
 */
public interface TallyCompanyService {

	TallyCompany save(TallyCompany tallyCompany);

	TallyCompany update(TallyCompany tallyCompany);

	Optional<TallyCompany> findByCompanyIdAndTallyCompanyNameIgnoreCase(Long id, String name);

	Optional<TallyCompany> findByCompanyId(Long id);

	List<TallyCompany> findAll();

	Optional<TallyCompany> findByCompanyPid(String pid);

	void deleteTallyCompany(TallyCompany tallyCompany);
}
