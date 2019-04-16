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

import com.orderfleet.webapp.domain.FormElementMasterType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FormElementMasterTypeRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FormElementMasterTypeService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.FormElementMasterTypeDTO;
import com.orderfleet.webapp.web.rest.mapper.FormElementMasterTypeMapper;

/**
 * Service Implementation for managing FormElementMasterType.
 * 
 * @author Sarath
 * @since Nov 2, 2016
 */

@Service
@Transactional
public class FormElementMasterTypeServiceImpl implements FormElementMasterTypeService {

	private final Logger log = LoggerFactory.getLogger(FormElementMasterTypeServiceImpl.class);

	@Inject
	private FormElementMasterTypeRepository formElementMasterRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private FormElementMasterTypeMapper formElementMasterMapper;

	/**
	 * Save a formElementMaster.
	 * 
	 * @param formElementMasterDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public FormElementMasterTypeDTO save(FormElementMasterTypeDTO formElementMasterDTO) {
		log.debug("Request to save FormElementMasterType : {}", formElementMasterDTO);
		formElementMasterDTO.setPid(FormElementMasterTypeService.PID_PREFIX + RandomUtil.generatePid()); // set
		// pid
		FormElementMasterType formElementMaster = formElementMasterMapper
				.formElementMasterDTOToFormElementMasterType(formElementMasterDTO);
		formElementMaster.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		formElementMaster = formElementMasterRepository.save(formElementMaster);
		FormElementMasterTypeDTO result = formElementMasterMapper
				.formElementMasterToFormElementMasterTypeDTO(formElementMaster);
		return result;
	}

	/**
	 * Update a formElementMaster.
	 * 
	 * @param formElementMasterDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public FormElementMasterTypeDTO update(FormElementMasterTypeDTO formElementMasterDTO) {
		log.debug("Request to Update FormElementMasterType : {}", formElementMasterDTO);
		return formElementMasterRepository.findOneByPid(formElementMasterDTO.getPid()).map(formElementMaster -> {
			formElementMaster.setName(formElementMasterDTO.getName());
			formElementMaster.setAlias(formElementMasterDTO.getAlias());
			formElementMaster.setDescription(formElementMasterDTO.getDescription());
			formElementMaster = formElementMasterRepository.save(formElementMaster);
			FormElementMasterTypeDTO result = formElementMasterMapper
					.formElementMasterToFormElementMasterTypeDTO(formElementMaster);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the formElementMasters.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<FormElementMasterTypeDTO> findAllByCompany() {
		log.debug("Request to get all FormElementMasterTypes");
		List<FormElementMasterType> formElementMasterList = formElementMasterRepository.findAllByCompanyId();
		List<FormElementMasterTypeDTO> result = formElementMasterMapper
				.formElementMastersToFormElementMasterTypeDTOs(formElementMasterList);
		return result;
	}

	/**
	 * Get all the formElementMasters.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<FormElementMasterTypeDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all FormElementMasterTypes");
		Page<FormElementMasterType> formElementMasters = formElementMasterRepository.findAllByCompanyId(pageable);
		Page<FormElementMasterTypeDTO> result = new PageImpl<FormElementMasterTypeDTO>(
				formElementMasterMapper.formElementMastersToFormElementMasterTypeDTOs(formElementMasters.getContent()),
				pageable, formElementMasters.getTotalElements());
		return result;
	}

	/**
	 * Get one formElementMaster by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public FormElementMasterTypeDTO findOne(Long id) {
		log.debug("Request to get FormElementMasterType : {}", id);
		FormElementMasterType formElementMaster = formElementMasterRepository.findOne(id);
		FormElementMasterTypeDTO formElementMasterDTO = formElementMasterMapper
				.formElementMasterToFormElementMasterTypeDTO(formElementMaster);
		return formElementMasterDTO;
	}

	/**
	 * Get one formElementMaster by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FormElementMasterTypeDTO> findOneByPid(String pid) {
		log.debug("Request to get FormElementMasterType by pid : {}", pid);
		return formElementMasterRepository.findOneByPid(pid).map(formElementMaster -> {
			FormElementMasterTypeDTO formElementMasterDTO = formElementMasterMapper
					.formElementMasterToFormElementMasterTypeDTO(formElementMaster);
			return formElementMasterDTO;
		});
	}

	/**
	 * Get one formElementMaster by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FormElementMasterTypeDTO> findByName(String name) {
		log.debug("Request to get FormElementMasterType by name : {}", name);
		return formElementMasterRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(formElementMaster -> {
					FormElementMasterTypeDTO formElementMasterDTO = formElementMasterMapper
							.formElementMasterToFormElementMasterTypeDTO(formElementMaster);
					return formElementMasterDTO;
				});
	}

	/**
	 * Delete the formElementMaster by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete FormElementMasterType : {}", pid);
		formElementMasterRepository.findOneByPid(pid).ifPresent(formElementMaster -> {
			formElementMasterRepository.delete(formElementMaster.getId());
		});
	}

	/**
	 * Update the FormElementMasterType status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public FormElementMasterTypeDTO updateFormElementMasterTypeStatus(String pid, boolean active) {
		log.debug("Request to update FormElementMasterType status");
		return formElementMasterRepository.findOneByPid(pid).map(formElementMasterType -> {
			formElementMasterType.setActivated(active);
			formElementMasterType = formElementMasterRepository.save(formElementMasterType);
			FormElementMasterTypeDTO result = formElementMasterMapper
					.formElementMasterToFormElementMasterTypeDTO(formElementMasterType);

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
	public Page<FormElementMasterTypeDTO> findAllByCompanyAndActivatedFormElementMasterTypeOrderByName(
			Pageable pageable, boolean active) {
		log.debug("Request to get all activated FormElementMasterTypes");
		Page<FormElementMasterType> formElementMasterTypepages = formElementMasterRepository
				.findAllByCompanyAndActivatedFormElementMasterTypeOrderByName(pageable, active);
		Page<FormElementMasterTypeDTO> formElementMasterTypeDTOpages = new PageImpl<FormElementMasterTypeDTO>(
				formElementMasterMapper
						.formElementMastersToFormElementMasterTypeDTOs(formElementMasterTypepages.getContent()),
				pageable, formElementMasterTypepages.getTotalElements());
		return formElementMasterTypeDTOpages;
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
	public List<FormElementMasterTypeDTO> findAllByCompanyAndDeactivatedFormElementMasterType(boolean deactive) {
		log.debug("Request to get all deactivated FormElementMasterTypes");
		List<FormElementMasterType> formElementMasterTypes = formElementMasterRepository
				.findAllByCompanyAndDeactivatedFormElementMasterType(deactive);
		List<FormElementMasterTypeDTO> formElementMasterTypeDTOs = formElementMasterMapper
				.formElementMastersToFormElementMasterTypeDTOs(formElementMasterTypes);
		return formElementMasterTypeDTOs;
	}
}
