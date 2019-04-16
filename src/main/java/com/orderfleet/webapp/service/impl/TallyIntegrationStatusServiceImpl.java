package com.orderfleet.webapp.service.impl;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.TallyIntegrationStatus;
import com.orderfleet.webapp.domain.enums.TallyIntegrationStatusType;
import com.orderfleet.webapp.repository.TallyIntegrationStatusRepository;
import com.orderfleet.webapp.service.TallyIntegrationStatusService;

/**
 * Service Implementation for managing TallyIntegrationStatus.
 *
 * @author Sarath
 * @since Mar 4, 2017
 */

@Service
@Transactional
public class TallyIntegrationStatusServiceImpl implements TallyIntegrationStatusService {

	private static final Logger log = LoggerFactory.getLogger(TallyIntegrationStatusServiceImpl.class);

	@Inject
	private TallyIntegrationStatusRepository integrationStatusRepository;

	@Override
	public void updateIntegratedStatus(Long companyId, TallyIntegrationStatusType tallyIntegrationStatusType,
			boolean status) {
		log.debug("update IntegratedStatus in TallyIntegrationStatus.......");
		integrationStatusRepository.updateIntegratedStatus(companyId, tallyIntegrationStatusType, status);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<TallyIntegrationStatus> findOneByCompanyIdTallyIntegrationStatusType(Long companyId,
			TallyIntegrationStatusType tallyIntegrationStatusType) {
		return integrationStatusRepository.findOneByCompanyIdTallyIntegrationStatusType(companyId,
				tallyIntegrationStatusType);
	}

	@Override
	public TallyIntegrationStatus save(TallyIntegrationStatus tallyIntegrationStatus) {
		return integrationStatusRepository.save(tallyIntegrationStatus);
	}

}
