package com.orderfleet.webapp.service.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormElementValue;
import com.orderfleet.webapp.domain.UserNotApplicableElement;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormElementTypeRepository;
import com.orderfleet.webapp.repository.FormElementValueRepository;
import com.orderfleet.webapp.repository.UserNotApplicableElementRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FormElementService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementValueDTO;
import com.orderfleet.webapp.web.rest.mapper.FormElementMapper;

/**
 * Service Implementation for managing FormElement.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class FormElementServiceImpl implements FormElementService {

	private final Logger log = LoggerFactory.getLogger(FormElementServiceImpl.class);

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private FormElementMapper formElementMapper;

	@Inject
	private FormElementTypeRepository formElementTypeRepository;

	@Inject
	private FormElementValueRepository formElementValueRepository;

	@Inject
	private UserNotApplicableElementRepository userNotApplicableElementRepository;

	/**
	 * Save a formElement.
	 * 
	 * @param formElementDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public FormElementDTO save(FormElementDTO formElementDTO) {
		log.debug("Request to save FormElement : {}", formElementDTO);

		// set pid
		formElementDTO.setPid(FormElementService.PID_PREFIX + RandomUtil.generatePid());
		FormElement formElement = formElementMapper.formElementDTOToFormElement(formElementDTO);
		// set company
		formElement.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		formElement = formElementRepository.save(formElement);
		FormElementDTO result = formElementMapper.formElementToFormElementDTO(formElement);
		return result;
	}

	@Override
	public void saveDefaultValue(String pid, String defaultValue) {
		FormElement formElement = formElementRepository.findOneByPid(pid).get();
		formElement.setDefaultValue(defaultValue);
		formElementRepository.save(formElement);
	}

	/**
	 * Update a formElement.
	 * 
	 * @param formElementDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public FormElementDTO update(FormElementDTO formElementDTO) {
		log.debug("Request to Update FormElement : {}", formElementDTO);
		Optional<FormElement> optionalFormElement = formElementRepository.findOneByPid(formElementDTO.getPid());
		if (optionalFormElement.isPresent()) {
			FormElement formElement = optionalFormElement.get();
			formElement.setName(formElementDTO.getName());
			formElement.setFormElementType(formElementTypeRepository.findOne(formElementDTO.getFormElementTypeId()));
			Set<FormElementValue> newFormElementValues = new LinkedHashSet<>();
			for (FormElementValueDTO formElementValueDto : formElementDTO.getFormElementValues()) {
				FormElementValue formElementValue = new FormElementValue();
				formElementValue.setName(formElementValueDto.getName());
				formElementValue.setSortOrder(0);
				formElementValue.setFormElement(formElement);
				newFormElementValues.add(formElementValue);
			}
			formElement.setFormElementValues(newFormElementValues);
			formElement = formElementRepository.save(formElement);
			formElementValueRepository.deleteByFormElementIdIsNull();
			return formElementMapper.formElementToFormElementDTO(formElement);
		}
		return null;
	}

	/**
	 * Get all the formElements.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<FormElement> findAll(Pageable pageable) {
		log.debug("Request to get all FormElements");
		Page<FormElement> result = formElementRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the formElements.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<FormElementDTO> findAllByCompany() {
		log.debug("Request to get all FormElements");
		List<FormElement> formElements = formElementRepository.findAllByCompanyId();
		List<FormElementDTO> result = formElementMapper.formElementsToFormElementDTOs(formElements);
		return result;
	}

	/**
	 * Get all the formElements.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<FormElementDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all FormElements");
		Page<FormElement> formElements = formElementRepository.findAllByCompanyIdOrderByFormElementName(pageable);
		Page<FormElementDTO> result = new PageImpl<FormElementDTO>(
				formElementMapper.formElementsToFormElementDTOs(formElements.getContent()), pageable,
				formElements.getTotalElements());
		return result;
	}

	/**
	 * Get one formElement by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public FormElementDTO findOne(Long id) {
		log.debug("Request to get FormElement : {}", id);
		FormElement formElement = formElementRepository.findOne(id);
		FormElementDTO formElementDTO = formElementMapper.formElementToFormElementDTO(formElement);
		return formElementDTO;
	}

	/**
	 * Get one formElement by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FormElementDTO> findOneByPid(String pid) {
		log.debug("Request to get FormElement by pid : {}", pid);
		return formElementRepository.findOneByPid(pid).map(formElement -> {
			FormElementDTO formElementDTO = formElementMapper.formElementToFormElementDTO(formElement);
			return formElementDTO;
		});
	}

	/**
	 * Get one formElement by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FormElementDTO> findByName(String name) {
		log.debug("Request to get FormElementGroup by name : {}", name);
		return formElementRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(formElement -> {
					FormElementDTO formElementDTO = formElementMapper.formElementToFormElementDTO(formElement);
					return formElementDTO;
				});
	}

	/**
	 * Delete the formElement by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete FormElement : {}", pid);
		formElementRepository.findOneByPid(pid).ifPresent(formElement -> {
			formElementRepository.delete(formElement.getId());
		});
	}

	@Override
	public List<FormElementDTO> findUsersFormElement() {
		List<UserNotApplicableElement> userNotApplicableElementList = userNotApplicableElementRepository
				.findByUserIsCurrentUser();
		List<FormElementDTO> result = null;
		if (userNotApplicableElementList != null && userNotApplicableElementList.size() > 0) {
			List<Long> formElementIds = userNotApplicableElementList.parallelStream()
					.map(unae -> unae.getFormElement().getId()).collect(Collectors.toList());
			List<FormElement> formElements = formElementRepository
					.findAllByCompanyIdAndNotFormElementIn(formElementIds);
			result = formElementMapper.formElementsToFormElementDTOs(formElements);
		} else {
			List<FormElement> formElements = formElementRepository.findAllByCompanyId();
			result = formElementMapper.formElementsToFormElementDTOs(formElements);
		}
		return result;
	}

	/**
	 * Update the FormElement status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public FormElementDTO updateFormElementStatus(String pid, boolean active) {
		log.debug("Request to update FormElement status: {}", pid);
		return formElementRepository.findOneByPid(pid).map(formElement -> {
			formElement.setActivated(active);
			formElement = formElementRepository.save(formElement);
			FormElementDTO result = formElementMapper.formElementToFormElementDTO(formElement);
			return result;
		}).orElse(null);
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
	public Page<FormElementDTO> findAllByCompanyAndActivatedFormElement(Pageable pageable, boolean active) {
		log.debug("Request to activate FormElement  ");
		Page<FormElement> pageFormElement = formElementRepository
				.findAllByCompanyIdAndActivatedFormElementOrderByName(pageable, active);
		Page<FormElementDTO> pageFormElementDTO = new PageImpl<FormElementDTO>(
				formElementMapper.formElementsToFormElementDTOs(pageFormElement.getContent()), pageable,
				pageFormElement.getTotalElements());
		return pageFormElementDTO;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<FormElementDTO> findAllByCompanyAndDeactivatedFormElement(boolean deactive) {
		log.debug("Request to activate FormElement  ");
		List<FormElement> formElements = formElementRepository.findAllByCompanyIdAndDeactivatedFormElement(deactive);
		List<FormElementDTO> formElementDTOs = formElementMapper.formElementsToFormElementDTOs(formElements);
		return formElementDTOs;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FormElementDTO> findAllByCompanyIdAndFormElementPidIn(List<String> formElementPids) {
		List<FormElement> formElements = formElementRepository.findAllByCompanyIdAndFormElementPidIn(formElementPids);
		List<FormElementDTO> formElementDTOs = formElementMapper.formElementsToFormElementDTOs(formElements);
		return formElementDTOs;
	}

}
