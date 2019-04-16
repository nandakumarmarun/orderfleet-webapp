package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DynamicDocumentSettingsColumns;

/**
 * Spring Data JPA repository for the DynamicDocumentSettingsColumns entity.
 *
 * @author Sarath
 * @since Aug 29, 2017
 *
 */
public interface DynamicDocumentSettingsColumnsRepository extends JpaRepository<DynamicDocumentSettingsColumns, Long> {

	@Query("select dynamicDocumentSettingsColumns from DynamicDocumentSettingsColumns dynamicDocumentSettingsColumns where dynamicDocumentSettingsColumns.company.id = ?#{principal.companyId}")
	List<DynamicDocumentSettingsColumns> findAllByCompanyId();
	
	@Transactional
	void deleteByDynamicDocumentSettingsHeaderPid(String dynamicDocumentSettingsHeaderPid);
}
