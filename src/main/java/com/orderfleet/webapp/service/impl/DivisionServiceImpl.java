package com.orderfleet.webapp.service.impl;

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

import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DivisionService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.DivisionDTO;
import com.orderfleet.webapp.web.rest.mapper.DivisionMapper;

/**
 * Service Implementation for managing Division.
 * 
 * @author Shaheer
 * @since May 07, 2016
 */
@Service
@Transactional
public class DivisionServiceImpl implements DivisionService {

	private final Logger log = LoggerFactory.getLogger(DivisionServiceImpl.class);

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DivisionMapper divisionMapper;

	/**
	 * Save a division.
	 * 
	 * @param divisionDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public DivisionDTO save(DivisionDTO divisionDTO) {
		log.debug("Request to save Division : {}", divisionDTO);
		divisionDTO.setPid(DivisionService.PID_PREFIX + RandomUtil.generatePid()); // set
		Division division = divisionMapper.divisionDTOToDivision(divisionDTO);
		division.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		division = divisionRepository.save(division);
		DivisionDTO result = divisionMapper.divisionToDivisionDTO(division);
		return result;
	}

	/**
	 * Update a division.
	 * 
	 * @param divisionDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public DivisionDTO update(DivisionDTO divisionDTO) {
		log.debug("Request to Update Division : {}", divisionDTO);
		return divisionRepository.findOneByPid(divisionDTO.getPid()).map(division -> {
			division.setName(divisionDTO.getName());
			division.setAlias(divisionDTO.getAlias());
			division.setDescription(divisionDTO.getDescription());
			division = divisionRepository.save(division);
			DivisionDTO result = divisionMapper.divisionToDivisionDTO(division);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the divisions.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DivisionDTO> findAllByCompany() {
		log.debug("Request to get all Divisions");
		List<Division> divisionList = divisionRepository.findAllByCompanyId();
		List<DivisionDTO> result = divisionMapper.divisionsToDivisionDTOs(divisionList);
		return result;
	}

	/**
	 * Get all the divisions.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DivisionDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Divisions");
		Page<Division> divisions = divisionRepository.findAllByCompanyIdOrderByDivisionName(pageable);
		Page<DivisionDTO> result = new PageImpl<DivisionDTO>(
				divisionMapper.divisionsToDivisionDTOs(divisions.getContent()), pageable, divisions.getTotalElements());
		return result;
	}

	/**
	 * Get one division by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public DivisionDTO findOne(Long id) {
		log.debug("Request to get Division : {}", id);
		Division division = divisionRepository.findOne(id);
		DivisionDTO divisionDTO = divisionMapper.divisionToDivisionDTO(division);
		return divisionDTO;
	}

	/**
	 * Get one division by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DivisionDTO> findOneByPid(String pid) {
		log.debug("Request to get Division by pid : {}", pid);
		return divisionRepository.findOneByPid(pid).map(division -> {
			DivisionDTO divisionDTO = divisionMapper.divisionToDivisionDTO(division);
			return divisionDTO;
		});
	}

	/**
	 * Get one division by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DivisionDTO> findByName(String name) {
		log.debug("Request to get Division by name : {}", name);
		return divisionRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(division -> {
					DivisionDTO divisionDTO = divisionMapper.divisionToDivisionDTO(division);
					return divisionDTO;
				});
	}

	/**
	 * Delete the division by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Division : {}", pid);
		divisionRepository.findOneByPid(pid).ifPresent(division -> {
			divisionRepository.delete(division.getId());
		});
	}

	/**
	 * Get one division by alias.
	 *
	 * @param alias
	 *            the alias of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DivisionDTO> findByAlias(String alias) {
		log.debug("Request to get Division by alias : {}", alias);
		return divisionRepository.findByCompanyIdAndAliasIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), alias)
				.map(division -> {
					DivisionDTO divisionDTO = divisionMapper.divisionToDivisionDTO(division);
					return divisionDTO;
				});
	}

	/**
	 * Update the Division status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	@Override
	public DivisionDTO updateDivisionStatus(String pid, boolean activate) {
		log.debug("Request to update Division status: {}");
		return divisionRepository.findOneByPid(pid).map(division -> {
			division.setActivated(activate);
			division = divisionRepository.save(division);
			DivisionDTO result = divisionMapper.divisionToDivisionDTO(division);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
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
	public Page<DivisionDTO> findAllByCompanyAndActivatedDivisionOrderByName(Pageable pageable, boolean active) {
		log.debug("Request to get Activated Division : {}");
		Page<Division> pageDivision = divisionRepository.findAllByCompanyAndActivatedDivisionOrderByName(pageable,
				active);
		Page<DivisionDTO> pageDivisionDTO = new PageImpl<DivisionDTO>(
				divisionMapper.divisionsToDivisionDTOs(pageDivision.getContent()),pageable,pageDivision.getTotalElements());
		return pageDivisionDTO;
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<DivisionDTO> findAllByCompanyAndDeactivatedDivision(boolean deactive) {
		log.debug("Request to get Deactivates Division : {}");
		List<Division> divisions = divisionRepository.findAllByCompanyAndDeactivatedDivision(deactive);
		List<DivisionDTO> divisionDTOs = divisionMapper.divisionsToDivisionDTOs(divisions);
		return divisionDTOs;
	}

}
