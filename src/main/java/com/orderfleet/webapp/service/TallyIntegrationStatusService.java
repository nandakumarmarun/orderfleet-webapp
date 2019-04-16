package com.orderfleet.webapp.service;

import java.util.Optional;

import com.orderfleet.webapp.domain.TallyIntegrationStatus;
import com.orderfleet.webapp.domain.enums.TallyIntegrationStatusType;

/**
 * Service Interface for managing TallyIntegrationStatus.
 *
 * @author Sarath
 * @since Mar 4, 2017
 */
public interface TallyIntegrationStatusService {

	void updateIntegratedStatus(Long companyId, TallyIntegrationStatusType tallyIntegrationStatusType, boolean status);

	Optional<TallyIntegrationStatus> findOneByCompanyIdTallyIntegrationStatusType(Long companyId,
			TallyIntegrationStatusType ledger);

	TallyIntegrationStatus save(TallyIntegrationStatus tallyIntegrationStatus);
}
