package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FormService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementOrderDTO;
import com.orderfleet.webapp.web.rest.dto.FormFormElementOrderDTO;
import com.orderfleet.webapp.web.rest.mapper.FormMapper;

/**
 * Service Implementation for managing Form.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class FormServiceImpl implements FormService {

	private final Logger log = LoggerFactory.getLogger(FormServiceImpl.class);

	@Inject
	private FormRepository formRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private FormMapper formMapper;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private FormFormElementRepository formFormElementRepository;

	/**
	 * Save a form.
	 * 
	 * @param formDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public FormDTO save(FormDTO formDTO) {
		log.debug("Request to save Form : {}", formDTO);

		// set pid
		formDTO.setPid(FormService.PID_PREFIX + RandomUtil.generatePid());
		Form form = formMapper.formDTOToForm(formDTO);
		// set company
		form.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		form = formRepository.save(form);
		FormDTO result = formMapper.formToFormDTO(form);
		return result;
	}

	@Override
	public void saveAssignedQuestions(FormFormElementOrderDTO formFormElementDTO) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Form form = formRepository.findOneByPid(formFormElementDTO.getFormPid()).get();
		List<FormElementOrderDTO> formElementOrderDTOs = formFormElementDTO.getFormElementOrderDTOs();
		Set<FormFormElement> formFormElements = new HashSet<FormFormElement>();
		for (FormElementOrderDTO formElementOrderDTO : formElementOrderDTOs) {
			FormElement formElement = formElementRepository.findOneByPid(formElementOrderDTO.getFormElementPid()).get();
			FormFormElement formFormElement = new FormFormElement();
			formFormElement.setForm(form);
			formFormElement.setFormElement(formElement);
			formFormElement.setSortOrder(formElementOrderDTO.getSortOrder());
			formFormElement.setReportOrder(formElementOrderDTO.getReportOrder());
			formFormElement.setCompany(company);
			formFormElement.setEditable(formElementOrderDTO.getEditable());
			formFormElement.setMandatory(formElementOrderDTO.getMandatory());
			formFormElement.setValidationEnabled(formElementOrderDTO.getValidationEnabled());
			formFormElement.setVisibility(formElementOrderDTO.getVisibility());
			formFormElement.setDashboardVisibility(formElementOrderDTO.getDashboardVisibility());
			formFormElements.add(formFormElement);
		}
		formFormElementRepository.deleteByFormPid(form.getPid());
		formFormElementRepository.save(formFormElements);
	}

	/**
	 * Update a form.
	 * 
	 * @param formDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public FormDTO update(FormDTO formDTO) {
		log.debug("Request to Update Form : {}", formDTO);
		return formRepository.findOneByPid(formDTO.getPid()).map(form -> {
			form.setName(formDTO.getName());
			form.setDescription(formDTO.getDescription());
			form.setMultipleRecord(formDTO.getMultipleRecord());
			form.setJsCode(formDTO.getJsCode());
			form = formRepository.save(form);
			FormDTO result = formMapper.formToFormDTO(form);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the forms.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Form> findAll(Pageable pageable) {
		log.debug("Request to get all Forms");
		Page<Form> result = formRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the forms.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<FormDTO> findAllByCompany() {
		log.debug("Request to get all Forms");
		List<Form> forms = formRepository.findAllByCompanyId();
		List<FormDTO> result = formMapper.formsToFormDTOs(forms);
		return result;
	}

	/**
	 * Get all the forms.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<FormDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Forms");
		Page<Form> forms = formRepository.findAllByCompanyId(pageable);
		Page<FormDTO> result = new PageImpl<FormDTO>(formMapper.formsToFormDTOs(forms.getContent()), pageable,
				forms.getTotalElements());
		return result;
	}

	/**
	 * Get one form by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public FormDTO findOne(Long id) {
		log.debug("Request to get Form : {}", id);
		Form form = formRepository.findOne(id);
		FormDTO formDTO = formMapper.formToFormDTO(form);
		return formDTO;
	}

	/**
	 * Get one form by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FormDTO> findOneByPid(String pid) {
		log.debug("Request to get Form by pid : {}", pid);
		return formRepository.findOneByPid(pid).map(form -> {
			FormDTO formDTO = formMapper.formToFormDTO(form);
			return formDTO;
		});
	}

	/**
	 * Get one form by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FormDTO> findByName(String name) {
		log.debug("Request to get FormGroup by name : {}", name);
		return formRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(form -> {
					FormDTO formDTO = formMapper.formToFormDTO(form);
					return formDTO;
				});
	}

	/**
	 * Delete the form by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Form : {}", pid);
		formRepository.findOneByPid(pid).ifPresent(form -> {
			formRepository.delete(form.getId());
		});
	}

	/**
	 * Update the Form status by pid.
	 * 
	 * @param pid    the pid of the entity
	 * @param active the active of the entity
	 * @return the entity
	 */
	@Override
	public FormDTO updateFormStatus(String pid, boolean active) {
		log.debug("Request to update Form Status");
		return formRepository.findOneByPid(pid).map(form -> {
			form.setActivated(active);
			form = formRepository.save(form);
			FormDTO result = formMapper.formToFormDTO(form);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active   the active of the entity
	 * 
	 * @param pageable the pageable of the entity
	 * @return the entity
	 */
	@Override
	public Page<FormDTO> findAllByCompanyAndActivatedFormOrderByName(Pageable pageable, boolean active) {
		log.debug("Request to get activated Form ");
		Page<Form> pageForm = formRepository.findAllByCompanyIdAndActivatedFormOrderByName(pageable, active);
		Page<FormDTO> pageFormDTO = new PageImpl<FormDTO>(formMapper.formsToFormDTOs(pageForm.getContent()), pageable,
				pageForm.getTotalElements());
		return pageFormDTO;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<FormDTO> findAllByCompanyAndDeactivatedForm(boolean deactive) {
		log.debug("Request to get deactivated Form ");
		List<Form> forms = formRepository.findAllByCompanyIdAndDeactivatedForm(deactive);
		List<FormDTO> formDTOs = formMapper.formsToFormDTOs(forms);
		return formDTOs;
	}

	/**
	 * Get all the forms.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<FormDTO> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		log.debug("Request to get all Forms");
		List<Form> forms = formRepository.findAllByCompanyIdAndLastModifiedDate(lastModifiedDate);
		List<FormDTO> result = formMapper.formsToFormDTOs(forms);
		return result;
	}
}
