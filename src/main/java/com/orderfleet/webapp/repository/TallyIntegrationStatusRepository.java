package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.TallyIntegrationStatus;
import com.orderfleet.webapp.domain.enums.TallyIntegrationStatusType;

/**
 * Spring Data JPA repository for the TallyIntegrationStatus entity.
 *
 * @author Sarath
 * @since Mar 4, 2017
 */
public interface TallyIntegrationStatusRepository extends JpaRepository<TallyIntegrationStatus, Long> {

	@Modifying(clearAutomatically = true)
	@Query("UPDATE TallyIntegrationStatus tis SET tis.integrated = ?3 WHERE tis.company.id = ?1 AND tis.tallyIntegrationStatusType = ?2")
	void updateIntegratedStatus(Long companyId, TallyIntegrationStatusType tallyIntegrationStatusType, boolean status);

	@Query("select tis from TallyIntegrationStatus tis  WHERE tis.company.id = ?1 AND tis.tallyIntegrationStatusType = ?2")
	Optional<TallyIntegrationStatus> findOneByCompanyIdTallyIntegrationStatusType(Long companyId,TallyIntegrationStatusType tallyIntegrationStatusType);
}
