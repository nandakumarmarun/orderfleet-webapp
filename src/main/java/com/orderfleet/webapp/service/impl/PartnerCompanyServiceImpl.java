package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PartnerCompany;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PartnerCompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.PartnerCompanyService;

/**
 * Service Implementation for managing PartnerCompany.
 *
 * @author Sarath
 * @since Feb 23, 2018
 *
 */

@Service
@Transactional
public class PartnerCompanyServiceImpl implements PartnerCompanyService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private PartnerCompanyRepository partnerCompanyRepository;

	@Override
	public Optional<PartnerCompany> findOneById(Long id) {
		log.debug("request to find partnercompany by id : {}", id);
		return partnerCompanyRepository.findOneById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PartnerCompany> findAllByCompanyId(String partnerPid) {
		log.debug("request to find partnercompany by partnerPid : {}", partnerPid);
		return partnerCompanyRepository.findAllByPartnerPid(partnerPid);
	}

	@Override
	public PartnerCompany save(String PartnerPid, String CompanyPid) {
		log.debug("request to find save by partnercompany  {}");
		Optional<User> opUser = userRepository.findOneByPid(PartnerPid);
		Optional<Company> opCompany = companyRepository.findOneByPid(CompanyPid);
		if (opUser.isPresent() && opCompany.isPresent()) {
			PartnerCompany partnerCompany = new PartnerCompany(opCompany.get(), opUser.get());
			partnerCompany = partnerCompanyRepository.save(partnerCompany);
			return partnerCompany;
		}
		return null;
	}
}
