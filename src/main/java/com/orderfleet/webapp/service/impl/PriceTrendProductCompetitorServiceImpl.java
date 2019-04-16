package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.CompetitorProfile;
import com.orderfleet.webapp.domain.PriceTrendProductCompetitor;
import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.repository.PriceTrendProductCompetitorRepository;
import com.orderfleet.webapp.repository.PriceTrendProductRepository;
import com.orderfleet.webapp.repository.CompetitorProfileRepository;
import com.orderfleet.webapp.service.PriceTrendProductCompetitorService;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductCompetitorDTO;
import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.CompetitorProfileMapper;

/**
 * Service Implementation for managing PriceTrendProductCompetitor.
 * 
 * @author Muhammed Riyas T
 * @since August 30, 2016
 */
@Service
@Transactional
public class PriceTrendProductCompetitorServiceImpl implements PriceTrendProductCompetitorService {
	private final Logger log = LoggerFactory.getLogger(PriceTrendProductCompetitorServiceImpl.class);

	@Inject
	private PriceTrendProductRepository priceTrendProductRepository;

	@Inject
	private PriceTrendProductCompetitorRepository priceTrendProductCompetitorRepository;

	@Inject
	private CompetitorProfileRepository competitorProfileRepository;

	@Inject
	private CompetitorProfileMapper competitorProfileMapper;

	@Override
	public void save(String priceTrendProductPid, String assignedCompetitors) {
		log.debug("Request to save PriceTrendProduct Competitors");

		PriceTrendProduct priceTrendProduct = priceTrendProductRepository.findOneByPid(priceTrendProductPid).get();
		String[] competitors = assignedCompetitors.split(",");
		List<PriceTrendProductCompetitor> priceTrendProductCompetitor = new ArrayList<>();
		for (String competitorPid : competitors) {
			CompetitorProfile competitorProfile = competitorProfileRepository.findOneByPid(competitorPid).get();
			priceTrendProductCompetitor.add(new PriceTrendProductCompetitor(priceTrendProduct, competitorProfile));
		}
		priceTrendProductCompetitorRepository.deleteByPriceTrendProductPid(priceTrendProductPid);
		priceTrendProductCompetitorRepository.save(priceTrendProductCompetitor);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CompetitorProfileDTO> findCompetitorsByPriceTrendProductPid(String priceTrendProductPid) {
		log.debug("Request to get all Competitors under in a PriceTrendProduct");
		List<CompetitorProfile> competitorProfiles = priceTrendProductCompetitorRepository
				.findCompetitorsByPriceTrendProductPid(priceTrendProductPid);
		List<CompetitorProfileDTO> result = competitorProfileMapper
				.competitorProfilesToCompetitorProfileDTOs(competitorProfiles);
		return result;
	}

	/**
	 * Get all the PriceTrendProductCompetitorDTO.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PriceTrendProductCompetitorDTO> findAllByCompany() {
		log.debug("Request to get all PriceLevel");
		List<PriceTrendProductCompetitor> priceTrendProductCompetitor = priceTrendProductCompetitorRepository
				.findAllByCompanyId();
		List<PriceTrendProductCompetitorDTO> result = priceTrendProductCompetitor.stream()
				.map(PriceTrendProductCompetitorDTO::new).collect(Collectors.toList());
		return result;
	}
	
	/**
	 * Get all the PriceTrendProductCompetitorDTO.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PriceTrendProductCompetitorDTO> findAllByCompanyAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		log.debug("Request to get all PriceLevel");
		List<PriceTrendProductCompetitor> priceTrendProductCompetitor = priceTrendProductCompetitorRepository
				.findAllByCompanyAndLastModifiedDate(lastModifiedDate);
		List<PriceTrendProductCompetitorDTO> result = priceTrendProductCompetitor.stream()
				.map(PriceTrendProductCompetitorDTO::new).collect(Collectors.toList());
		return result;
	}

}
