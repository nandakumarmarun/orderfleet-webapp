package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.TallyCompany;
import com.orderfleet.webapp.repository.TallyCompanyRepository;
import com.orderfleet.webapp.service.TallyCompanyService;

/**
 * Service Implementation for managing TallyCompany.
 *
 * @author Sarath
 * @since Feb 12, 2018
 *
 */
@Service
@Transactional
public class TallyCompanyServiceImpl implements TallyCompanyService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Inject
	private TallyCompanyRepository tallyCompanyRepository;

	@Override
	public TallyCompany save(TallyCompany tallyCompany) {
		log.debug("Request to save tallyCompany : {}", tallyCompany);
		return tallyCompanyRepository.save(tallyCompany);
	}

	@Override
	public TallyCompany update(TallyCompany tallyCompany) {
		log.debug("Request to Update tallyCompany : {}", tallyCompany);
		return tallyCompanyRepository.findByCompanyId(tallyCompany.getCompany().getId()).map(tc -> {
			return tallyCompanyRepository.save(tc);
		}).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<TallyCompany> findByCompanyIdAndTallyCompanyNameIgnoreCase(Long id, String name) {
		log.debug("Request to get tallyCompany by tallyCompany Name and company id: {}", name, " - ", id);
		return tallyCompanyRepository.findByCompanyIdAndTallyCompanyNameIgnoreCase(id, name);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<TallyCompany> findByCompanyId(Long id) {
		log.debug("Request to get tallyCompany by company Id: {}", id);
		return tallyCompanyRepository.findByCompanyId(id);
	}

	@Override
	public List<TallyCompany> findAll() {
		log.debug("Request to get all tallyCompany ");
		return tallyCompanyRepository.findAll();
	}

	@Override
	public Optional<TallyCompany> findByCompanyPid(String pid) {
		log.debug("Request to get tallyCompany by company pid: {}", pid);
		return tallyCompanyRepository.findByCompanyPid(pid);
	}

	@Override
	@Transactional
	public void deleteTallyCompany(TallyCompany tallyCompany) {
		log.debug("Request to delete tallyCompany ");
		tallyCompanyRepository.delete(tallyCompany);
	}

}
