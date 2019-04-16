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

import com.orderfleet.webapp.domain.Knowledgebase;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.KnowledgebaseRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.KnowledgebaseService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseDTO;
import com.orderfleet.webapp.web.rest.mapper.KnowledgebaseMapper;

/**
 * Service Implementation for managing Knowledgebase.
 * 
 * @author Muhammed Riyas T
 * @since August 08, 2016
 */
@Service
@Transactional
public class KnowledgebaseServiceImpl implements KnowledgebaseService {

	private final Logger log = LoggerFactory.getLogger(KnowledgebaseServiceImpl.class);

	@Inject
	private KnowledgebaseRepository knowledgebaseRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private KnowledgebaseMapper knowledgebaseMapper;

	@Inject
	private ProductGroupRepository productGroupRepository;

	/**
	 * Save a knowledgebase.
	 * 
	 * @param knowledgebaseDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public KnowledgebaseDTO save(KnowledgebaseDTO knowledgebaseDTO) {
		log.debug("Request to save Knowledgebase : {}", knowledgebaseDTO);
		knowledgebaseDTO.setPid(KnowledgebaseService.PID_PREFIX + RandomUtil.generatePid()); // set
		// pid
		Knowledgebase knowledgebase = knowledgebaseMapper.knowledgebaseDTOToKnowledgebase(knowledgebaseDTO);
		knowledgebase.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		knowledgebase = knowledgebaseRepository.save(knowledgebase);
		KnowledgebaseDTO result = knowledgebaseMapper.knowledgebaseToKnowledgebaseDTO(knowledgebase);
		return result;
	}

	/**
	 * Update a knowledgebase.
	 * 
	 * @param knowledgebaseDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public KnowledgebaseDTO update(KnowledgebaseDTO knowledgebaseDTO) {
		log.debug("Request to Update Knowledgebase : {}", knowledgebaseDTO);
		return knowledgebaseRepository.findOneByPid(knowledgebaseDTO.getPid()).map(knowledgebase -> {
			knowledgebase.setName(knowledgebaseDTO.getName());
			knowledgebase.setAlias(knowledgebaseDTO.getAlias());
			knowledgebase.setDescription(knowledgebaseDTO.getDescription());
			knowledgebase
					.setProductGroup(productGroupRepository.findOneByPid(knowledgebaseDTO.getProductGroupPid()).get());
			knowledgebase = knowledgebaseRepository.save(knowledgebase);
			KnowledgebaseDTO result = knowledgebaseMapper.knowledgebaseToKnowledgebaseDTO(knowledgebase);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the knowledgebases.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<KnowledgebaseDTO> findAllByCompany() {
		log.debug("Request to get all Knowledgebases");
		List<Knowledgebase> knowledgebaseList = knowledgebaseRepository.findAllByCompanyId();
		List<KnowledgebaseDTO> result = knowledgebaseMapper.knowledgebasesToKnowledgebaseDTOs(knowledgebaseList);
		return result;
	}

	/**
	 * Get all the knowledgebases.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<KnowledgebaseDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Knowledgebases");
		Page<Knowledgebase> knowledgebases = knowledgebaseRepository
				.findAllByCompanyIdOrderByKnowledgebaseName(pageable);
		Page<KnowledgebaseDTO> result = new PageImpl<KnowledgebaseDTO>(
				knowledgebaseMapper.knowledgebasesToKnowledgebaseDTOs(knowledgebases.getContent()), pageable,
				knowledgebases.getTotalElements());
		return result;
	}

	/**
	 * Get one knowledgebase by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public KnowledgebaseDTO findOne(Long id) {
		log.debug("Request to get Knowledgebase : {}", id);
		Knowledgebase knowledgebase = knowledgebaseRepository.findOne(id);
		KnowledgebaseDTO knowledgebaseDTO = knowledgebaseMapper.knowledgebaseToKnowledgebaseDTO(knowledgebase);
		return knowledgebaseDTO;
	}

	/**
	 * Get one knowledgebase by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<KnowledgebaseDTO> findOneByPid(String pid) {
		log.debug("Request to get Knowledgebase by pid : {}", pid);
		return knowledgebaseRepository.findOneByPid(pid).map(knowledgebase -> {
			KnowledgebaseDTO knowledgebaseDTO = knowledgebaseMapper.knowledgebaseToKnowledgebaseDTO(knowledgebase);
			return knowledgebaseDTO;
		});
	}

	/**
	 * Get one knowledgebase by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<KnowledgebaseDTO> findByName(String name) {
		log.debug("Request to get Knowledgebase by name : {}", name);
		return knowledgebaseRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(knowledgebase -> {
					KnowledgebaseDTO knowledgebaseDTO = knowledgebaseMapper
							.knowledgebaseToKnowledgebaseDTO(knowledgebase);
					return knowledgebaseDTO;
				});
	}

	/**
	 * Delete the knowledgebase by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Knowledgebase : {}", pid);
		knowledgebaseRepository.findOneByPid(pid).ifPresent(knowledgebase -> {
			knowledgebaseRepository.delete(knowledgebase.getId());
		});
	}

	/**
	 * Update the Knowledgebase status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public KnowledgebaseDTO updateKnowledgebaseStatus(String pid, boolean active) {
		log.debug("Request to update Knowledgebase status");
		return knowledgebaseRepository.findOneByPid(pid).map(knowledgebase -> {
			knowledgebase.setActivated(active);
			knowledgebase = knowledgebaseRepository.save(knowledgebase);
			KnowledgebaseDTO result = knowledgebaseMapper.knowledgebaseToKnowledgebaseDTO(knowledgebase);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all knowledgebaseDTO from Knowledgebase by status and
	 *        company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	@Override
	public List<KnowledgebaseDTO> findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivated(boolean active) {
		log.debug("request to get Activated Knowledgebase");
		List<Knowledgebase> knowledgebases = knowledgebaseRepository
				.findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivated(active);
		List<KnowledgebaseDTO> knowledgebaseDTOs = knowledgebaseMapper
				.knowledgebasesToKnowledgebaseDTOs(knowledgebases);
		return knowledgebaseDTOs;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active
	 *            the active of the entity
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
	@Override
	public Page<KnowledgebaseDTO> findAllByCompanyIdAndActivatedKnowledgebaseOrderByName(Pageable pageable,
			boolean active) {
		log.debug("request to get Activated Knowledgebase Order by Name");
		Page<Knowledgebase> pageKnowledgebase = knowledgebaseRepository
				.findAllByCompanyIdAndActivatedKnowledgebaseOrderByName(pageable, active);
		Page<KnowledgebaseDTO> pageKnowledgebaseDTO = new PageImpl<KnowledgebaseDTO>(
				knowledgebaseMapper.knowledgebasesToKnowledgebaseDTOs(pageKnowledgebase.getContent()), pageable,
				pageKnowledgebase.getTotalElements());
		return pageKnowledgebaseDTO;
	}


	@Override
	public List<KnowledgebaseDTO> findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivatedAndLastModifiedDate(boolean active,LocalDateTime lastModifiedDate) {
		log.debug("request to get Activated Knowledgebase");
		List<Knowledgebase> knowledgebases = knowledgebaseRepository
				.findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivatedAndLastModifiedDate(active,lastModifiedDate);
		List<KnowledgebaseDTO> knowledgebaseDTOs = knowledgebaseMapper
				.knowledgebasesToKnowledgebaseDTOs(knowledgebases);
		return knowledgebaseDTOs;
	}
	
}
