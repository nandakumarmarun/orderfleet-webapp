package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.FormElementMaster;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FormElementMasterRepository;
import com.orderfleet.webapp.repository.FormElementMasterTypeRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FormElementMasterService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.FormElementMasterDTO;

/**
 * Service Implementation for managing FormElementMaster.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Service
@Transactional
public class FormElementMasterServiceImpl implements FormElementMasterService {

	private final Logger log = LoggerFactory.getLogger(FormElementMasterServiceImpl.class);

	@Inject
	private FormElementMasterRepository formElementMasterRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private FormElementMasterTypeRepository elementMasterTypeRepository;

	/**
	 * Get all the formElementMasters.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<FormElementMasterDTO> findAllByCompany() {
		log.debug("Request to get all formElementMasters");
		List<FormElementMaster> formElementMasters = formElementMasterRepository.findAllByCompanyId();
		List<FormElementMasterDTO> result = formElementMasters.stream().map(FormElementMasterDTO::new)
				.collect(Collectors.toList());
		return result;
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
	public Optional<FormElementMasterDTO> findOneByPid(String pid) {
		log.debug("Request to get FormElementMaster by pid : {}", pid);
		return formElementMasterRepository.findOneByPid(pid).map(formElementMaster -> {
			FormElementMasterDTO categoryDTO = new FormElementMasterDTO(formElementMaster);
			return categoryDTO;
		});
	}

	/**
	 * Delete the formElementMaster by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete FormElementMaster : {}", pid);
		formElementMasterRepository.findOneByPid(pid).ifPresent(formElementMaster -> {
			formElementMasterRepository.delete(formElementMaster.getId());
		});
	}

	@Override
	public Optional<FormElementMasterDTO> findOneByName(String name) {
		log.debug("Request to get FormElementMaster by name : {}", name);
		return formElementMasterRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(),
				name).map(formElementMaster -> {
			FormElementMasterDTO categoryDTO = new FormElementMasterDTO(formElementMaster);
			return categoryDTO;
		});
	}

	@Override
	public FormElementMasterDTO save(FormElementMasterDTO formElementMasterDTO) {
		log.debug("Request to save FormElementMaster by name : {}", formElementMasterDTO.getName());
		FormElementMaster formElementMaster = new FormElementMaster();
		formElementMaster.setPid(FormElementMasterService.PID_PREFIX + RandomUtil.generatePid());
		formElementMaster.setName(formElementMasterDTO.getName());
		// set company
		formElementMaster.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		formElementMaster.setFormElementMasterType(elementMasterTypeRepository.findByCompanyIdAndNameIgnoreCase(
				SecurityUtils.getCurrentUsersCompanyId(), formElementMasterDTO.getFormElementMasterTypeName()).get());
		formElementMaster = formElementMasterRepository.save(formElementMaster);
		FormElementMasterDTO result = new FormElementMasterDTO(formElementMaster);
		return result;
	}

}
