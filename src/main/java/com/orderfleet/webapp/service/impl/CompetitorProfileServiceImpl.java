package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.CompetitorProfile;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CompetitorProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompetitorProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.CompetitorProfileMapper;

/**
 * Service Implementation for managing CompetitorProfile.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Service
@Transactional
public class CompetitorProfileServiceImpl implements CompetitorProfileService {

	private final Logger log = LoggerFactory.getLogger(CompetitorProfileServiceImpl.class);

	@Inject
	private CompetitorProfileRepository competitorProfileRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private CompetitorProfileMapper competitorProfileMapper;

	/**
	 * Save a competitorProfile.
	 * 
	 * @param competitorProfileDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public CompetitorProfileDTO save(CompetitorProfileDTO competitorProfileDTO) {
		log.debug("Request to save CompetitorProfile : {}", competitorProfileDTO);

		// set pid
		competitorProfileDTO.setPid(CompetitorProfileService.PID_PREFIX + RandomUtil.generatePid());
		CompetitorProfile competitorProfile = competitorProfileMapper
				.competitorProfileDTOToCompetitorProfile(competitorProfileDTO);
		// set company
		competitorProfile.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		competitorProfile = competitorProfileRepository.save(competitorProfile);
		CompetitorProfileDTO result = competitorProfileMapper
				.competitorProfileToCompetitorProfileDTO(competitorProfile);
		return result;
	}

	/**
	 * Update a competitorProfile.
	 * 
	 * @param competitorProfileDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public CompetitorProfileDTO update(CompetitorProfileDTO competitorProfileDTO) {
		log.debug("Request to Update CompetitorProfile : {}", competitorProfileDTO);
		return competitorProfileRepository.findOneByPid(competitorProfileDTO.getPid()).map(competitorProfile -> {
			competitorProfile.setName(competitorProfileDTO.getName());
			competitorProfile.setAlias(competitorProfileDTO.getAlias());
			competitorProfile.setDescription(competitorProfileDTO.getDescription());
			competitorProfile = competitorProfileRepository.save(competitorProfile);
			CompetitorProfileDTO result = competitorProfileMapper
					.competitorProfileToCompetitorProfileDTO(competitorProfile);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the competitorProfiles.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<CompetitorProfile> findAll(Pageable pageable) {
		log.debug("Request to get all CompetitorProfiles");
		Page<CompetitorProfile> result = competitorProfileRepository.findAll(pageable);
		return result;
	}

	@Override
	public List<CompetitorProfileDTO> findAllByCompany() {
		log.debug("Request to get all CompetitorProfiles");
		List<CompetitorProfile> competitorProfileList = competitorProfileRepository.findAllByCompanyId();
		List<CompetitorProfileDTO> result = competitorProfileMapper
				.competitorProfilesToCompetitorProfileDTOs(competitorProfileList);
		return result;
	}

	/**
	 * Get all the competitorProfiles.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<CompetitorProfileDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all CompetitorProfiles");
		Page<CompetitorProfile> competitorProfiles = competitorProfileRepository
				.findAllByCompanyIdOrderByNameDesc(pageable);
		Page<CompetitorProfileDTO> result = new PageImpl<CompetitorProfileDTO>(
				competitorProfileMapper.competitorProfilesToCompetitorProfileDTOs(competitorProfiles.getContent()),
				pageable, competitorProfiles.getTotalElements());
		return result;
	}

	/**
	 * Get one competitorProfile by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public CompetitorProfileDTO findOne(Long id) {
		log.debug("Request to get CompetitorProfile : {}", id);
		CompetitorProfile competitorProfile = competitorProfileRepository.findOne(id);
		CompetitorProfileDTO competitorProfileDTO = competitorProfileMapper
				.competitorProfileToCompetitorProfileDTO(competitorProfile);
		return competitorProfileDTO;
	}

	/**
	 * Get one competitorProfile by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<CompetitorProfileDTO> findOneByPid(String pid) {
		log.debug("Request to get CompetitorProfile by pid : {}", pid);
		return competitorProfileRepository.findOneByPid(pid).map(competitorProfile -> {
			CompetitorProfileDTO competitorProfileDTO = competitorProfileMapper
					.competitorProfileToCompetitorProfileDTO(competitorProfile);
			return competitorProfileDTO;
		});
	}

	/**
	 * Get one competitorProfile by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<CompetitorProfileDTO> findByName(String name) {
		log.debug("Request to get CompetitorProfile by name : {}", name);
		return competitorProfileRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(competitorProfile -> {
					CompetitorProfileDTO competitorProfileDTO = competitorProfileMapper
							.competitorProfileToCompetitorProfileDTO(competitorProfile);
					return competitorProfileDTO;
				});
	}

	/**
	 * Delete the competitorProfile by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete CompetitorProfile : {}", pid);
		competitorProfileRepository.findOneByPid(pid).ifPresent(competitorProfile -> {
			competitorProfileRepository.delete(competitorProfile.getId());
		});
	}

	/**
	 * update the competitorProfileDTO by "pid".
	 * 
	 * @param pid
	 *            the id of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public CompetitorProfileDTO updateCompetitorProfileStatus(String pid, boolean active) {
		log.debug("Request to update CompetitorProfile status");
		return competitorProfileRepository.findOneByPid(pid).map(competitorProfile -> {
			competitorProfile.setActivated(active);
			competitorProfile = competitorProfileRepository.save(competitorProfile);
			CompetitorProfileDTO result = competitorProfileMapper
					.competitorProfileToCompetitorProfileDTO(competitorProfile);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all competitorProfileDTO from CompetitorProfile by status and
	 *        company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CompetitorProfileDTO> findAllByCompanyIdAndCompetitorProfileActivatedOrDeactivated(boolean active) {
		log.debug("request to get activated CompetitorProfile ");
		List<CompetitorProfile> competitorProfiles = competitorProfileRepository
				.findAllByCompanyIdAndCompetitorProfileActivatedOrDeactivated(active);
		List<CompetitorProfileDTO> competitorProfileDTOs = competitorProfileMapper
				.competitorProfilesToCompetitorProfileDTOs(competitorProfiles);
		return competitorProfileDTOs;
	}

	/**
	 * @author Fahad
	 * @since Feb 11, 2017
	 * 
	 *        find all active company
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<CompetitorProfileDTO> findAllByCompanyAndCompetitorProfileActivatedOrderByName(Pageable pageable,
			boolean active) {
		Page<CompetitorProfile> pageCompetitorProfile = competitorProfileRepository
				.findAllByCompanyIdAndActivatedOrderByNameDesc(pageable, active);
		Page<CompetitorProfileDTO> pageCompetitorProfileDTO = new PageImpl<CompetitorProfileDTO>(
				competitorProfileMapper.competitorProfilesToCompetitorProfileDTOs(pageCompetitorProfile.getContent()),
				pageable, pageCompetitorProfile.getTotalElements());
		return pageCompetitorProfileDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CompetitorProfileDTO> findAllByCompanyIdAndCompetitorProfileActivatedAndLastModifiedDate(boolean active,
			LocalDateTime lastModifiedDate) {
		log.debug("request to get activated CompetitorProfile ");
		List<CompetitorProfile> competitorProfiles = competitorProfileRepository
				.findAllByCompanyIdAndCompetitorProfileActivatedAndLastModifiedDate(active, lastModifiedDate);
		List<CompetitorProfileDTO> competitorProfileDTOs = competitorProfileMapper
				.competitorProfilesToCompetitorProfileDTOs(competitorProfiles);
		return competitorProfileDTOs;
	}
}
