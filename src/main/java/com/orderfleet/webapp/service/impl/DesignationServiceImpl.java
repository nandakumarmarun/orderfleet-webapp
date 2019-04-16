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

import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DesignationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DesignationService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.DesignationDTO;
import com.orderfleet.webapp.web.rest.mapper.DesignationMapper;

/**
 * Service Implementation for managing Designation.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
@Service
@Transactional
public class DesignationServiceImpl implements DesignationService {

	private final Logger log = LoggerFactory.getLogger(DesignationServiceImpl.class);

	@Inject
	private DesignationRepository designationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DesignationMapper designationMapper;

	/**
	 * Save a designation.
	 * 
	 * @param designationDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public DesignationDTO save(DesignationDTO designationDTO) {
		log.debug("Request to save Designation : {}", designationDTO);

		// set pid
		designationDTO.setPid(DesignationService.PID_PREFIX + RandomUtil.generatePid());
		Designation designation = designationMapper.designationDTOToDesignation(designationDTO);
		// set company
		designation.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		designation = designationRepository.save(designation);
		DesignationDTO result = designationMapper.designationToDesignationDTO(designation);
		return result;
	}

	/**
	 * Update a designation.
	 * 
	 * @param designationDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public DesignationDTO update(DesignationDTO designationDTO) {
		log.debug("Request to Update Designation : {}", designationDTO);
		return designationRepository.findOneByPid(designationDTO.getPid()).map(designation -> {
			designation.setName(designationDTO.getName());
			designation.setAlias(designationDTO.getAlias());
			designation.setDescription(designationDTO.getDescription());
			designation = designationRepository.save(designation);
			DesignationDTO result = designationMapper.designationToDesignationDTO(designation);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the designations.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Designation> findAll(Pageable pageable) {
		log.debug("Request to get all Designations");
		Page<Designation> result = designationRepository.findAll(pageable);
		return result;
	}

	@Override
	public List<DesignationDTO> findAllByCompany() {
		log.debug("Request to get all Designations");
		List<Designation> designationList = designationRepository.findAllByCompanyId();
		List<DesignationDTO> result = designationMapper.designationsToDesignationDTOs(designationList);
		return result;
	}

	/**
	 * Get all the designations.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DesignationDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Designations");
		Page<Designation> designations = designationRepository.findAllByCompanyIdOrderByDesignationName(pageable);
		Page<DesignationDTO> result = new PageImpl<DesignationDTO>(
				designationMapper.designationsToDesignationDTOs(designations.getContent()), pageable,
				designations.getTotalElements());
		return result;
	}

	/**
	 * Get one designation by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public DesignationDTO findOne(Long id) {
		log.debug("Request to get Designation : {}", id);
		Designation designation = designationRepository.findOne(id);
		DesignationDTO designationDTO = designationMapper.designationToDesignationDTO(designation);
		return designationDTO;
	}

	/**
	 * Get one designation by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DesignationDTO> findOneByPid(String pid) {
		log.debug("Request to get Designation by pid : {}", pid);
		return designationRepository.findOneByPid(pid).map(designation -> {
			DesignationDTO designationDTO = designationMapper.designationToDesignationDTO(designation);
			return designationDTO;
		});
	}

	/**
	 * Get one designation by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DesignationDTO> findByName(String name) {
		log.debug("Request to get Designation by name : {}", name);
		return designationRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(designation -> {
					DesignationDTO designationDTO = designationMapper.designationToDesignationDTO(designation);
					return designationDTO;
				});
	}

	/**
	 * Delete the designation by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Designation : {}", pid);
		designationRepository.findOneByPid(pid).ifPresent(designation -> {
			designationRepository.delete(designation.getId());
		});
	}

	/**
	 * Update the Designation status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	@Override
	public DesignationDTO updateDesignationStatus(String pid, boolean activate) {
		log.debug("Request to update Designation status");
		return designationRepository.findOneByPid(pid).map(designation -> {
			designation.setActivated(activate);
			designation = designationRepository.save(designation);
			DesignationDTO result = designationMapper.designationToDesignationDTO(designation);
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
	 * @return the entity
	 */
	@Override
	public Page<DesignationDTO> findAllCompanyAndActivatedDesignation(Pageable pageable, boolean active) {
		log.debug("Request to get all Activated Designations");
		Page<Designation> pageDesignation = designationRepository
				.findAllByCompanyAndActivatedDesignationOrderByName(pageable, active);
		Page<DesignationDTO> pageDesignationDTO = new PageImpl<DesignationDTO>(
				designationMapper.designationsToDesignationDTOs(pageDesignation.getContent()), pageable,
				pageDesignation.getTotalElements());
		return pageDesignationDTO;
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
	public List<DesignationDTO> findAllCompanyAndDeactivatedDesignation(boolean deactive) {
		log.debug("Request to get all Deactivated Designations");
		List<Designation> designations = designationRepository.findAllByCompanyAndDeactivatedDesignation(deactive);
		List<DesignationDTO> designationDTOs = designationMapper.designationsToDesignationDTOs(designations);
		return designationDTOs;
	}

}
