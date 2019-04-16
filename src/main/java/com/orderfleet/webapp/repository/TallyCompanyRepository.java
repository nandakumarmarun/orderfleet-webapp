package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.TallyCompany;

/**
 * Spring Data JPA repository for the TallyCompany entity.
 *
 * @author Sarath
 * @since Feb 12, 2018
 *
 */
public interface TallyCompanyRepository extends JpaRepository<TallyCompany, Long> {
	Optional<TallyCompany> findByCompanyIdAndTallyCompanyNameIgnoreCase(Long id, String name);

	Optional<TallyCompany> findByCompanyId(Long id);

	Optional<TallyCompany> findByCompanyPid(String pid);
}
