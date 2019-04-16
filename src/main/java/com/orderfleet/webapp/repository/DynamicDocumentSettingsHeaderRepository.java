package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DynamicDocumentSettingsHeader;

/**
 * Spring Data JPA repository for the DynamicDocumentSettingsHeader entity.
 *
 * @author Sarath
 * @since Aug 29, 2017
 *
 */
public interface DynamicDocumentSettingsHeaderRepository extends JpaRepository<DynamicDocumentSettingsHeader, Long> {

	Optional<DynamicDocumentSettingsHeader> findOneByPid(String pid);
	
	Optional<DynamicDocumentSettingsHeader> findOneByName(String name);

	@Query("select dynamicDocumentSettingsHeader from DynamicDocumentSettingsHeader dynamicDocumentSettingsHeader where dynamicDocumentSettingsHeader.company.id = ?#{principal.companyId}")
	List<DynamicDocumentSettingsHeader> findAllByCompanyId();

	@Transactional
	void deleteByPid(String pid);

	
}
