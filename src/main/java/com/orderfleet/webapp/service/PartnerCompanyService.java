package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.PartnerCompany;

/**
 * a service for PartnerCompany.
 *
 * @author Sarath
 * @since Feb 23, 2018
 *
 */
public interface PartnerCompanyService {

	Optional<PartnerCompany> findOneById(Long id);

	List<PartnerCompany> findAllByCompanyId(String partnerPid);

	PartnerCompany save(String PartnerPid, String CompanyPid);
}
