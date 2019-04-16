package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DynamicDocumentSettingsRowColour;

/**
 * Spring Data JPA repository for the DynamicDocumentSettingsRowColour entity.
 *
 * @author Sarath
 * @since Aug 29, 2017
 *
 */
public interface DynamicDocumentSettingsRowColourRepository
		extends JpaRepository<DynamicDocumentSettingsRowColour, Long> {

	@Query("select dynamicDocumentSettingsRowColour from DynamicDocumentSettingsRowColour dynamicDocumentSettingsRowColour where dynamicDocumentSettingsRowColour.company.id = ?#{principal.companyId}")
	List<DynamicDocumentSettingsRowColour> findAllByCompanyId();
	
	@Transactional
	void deleteByDynamicDocumentSettingsHeaderPid(String dynamicDocumentSettingsHeaderPid);
}
