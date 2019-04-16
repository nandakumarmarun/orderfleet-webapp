package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Funnel;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FunnelRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FunnelService;

@Service
@Transactional
public class FunnelServiceImpl implements FunnelService {

	@Inject
	private FunnelRepository funnelRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public Funnel save(String name, int sortOrder) {
		Funnel funnel = new Funnel();
		funnel.setName(name);
		funnel.setSortOrder(sortOrder);
		// set company
		funnel.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		return funnelRepository.save(funnel);
	}

	@Override
	public Funnel update(Long id, String name, int sortOrder) {
		Funnel funnel = funnelRepository.findOne(id);
		if(funnel != null) {
			funnel.setName(name);
			funnel.setSortOrder(sortOrder);
			return funnelRepository.save(funnel);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Funnel findOne(Long id) {
		return funnelRepository.findOne(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Funnel> findByName(String name) {
		return funnelRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(funnel -> funnel);
	}

	@Override
	public void delete(Long id) {
		funnelRepository.delete(id);
	}
	
	@Override
	public List<Funnel> findAllByCompany() {
		return funnelRepository.findAllByCompanyId();
	}
}
