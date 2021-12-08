package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.orderfleet.webapp.repository.AccountTypeRepository;
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

	@Inject
	private AccountTypeRepository accountTypeRepository;

	/**
	 * Save a formElement.
	 * 
	 * @param formElementDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public FormElementDTO save(FormElementDTO formElementDTO) {
		log.debug("Request to save FormElement : {}", formElementDTO);

		// set pid
		formElementDTO.setPid(FormElementService.PID_PREFIX + RandomUtil.generatePid());
		FormElement formElement = formElementMapper.formElementDTOToFormElement(formElementDTO);

		// set company
		System.out.println(formElement.getFormLoadFromMobile() + "+++++++++++++" + formElement.getFormLoadMobileData());
		if (!formElementDTO.getFormAccountTypePid().equals("all")) {
			System.out.println("--------------------------------------------------------------------------");
			formElement
					.setAccountType(accountTypeRepository.findOneByPid(formElementDTO.getFormAccountTypePid()).get());
		}
		formElement.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		formElement = formElementRepository.save(formElement);
		FormElementDTO result = formElementMapper.formElementToFormElementDTO(formElement);
		result.setFormAccountTypePid(
				formElement.getAccountType() != null ? formElement.getAccountType().getPid() : "all");
		System.out.println("--------------------------------------------------------------------------"
				+ result.getFormAccountTypePid());
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
	 * @param formElementDTO the entity to update
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

				Optional<FormElementValue> opFormElementValue = formElement.getFormElementValues().stream()
						.filter(fev -> fev.getName().equalsIgnoreCase(formElementValueDto.getName())).findAny();
				if (!opFormElementValue.isPresent()) {
					FormElementValue formElementValue = new FormElementValue();
					formElementValue.setName(formElementValueDto.getName());
					formElementValue.setSortOrder(0);
					formElementValue.setFormElement(formElement);
					newFormElementValues.add(formElementValue);
				} else {
					newFormElementValues.add(opFormElementValue.get());
				}
			}
			formElement.setFormLoadFromMobile(formElementDTO.getFormLoadFromMobile());
			formElement.setFormLoadMobileData(formElementDTO.getFormLoadMobileData());
			formElement.setFormElementValues(newFormElementValues);
			if (!formElementDTO.getFormAccountTypePid().equals("all")) {
				System.out.println("-------------------------------------------------------");
				formElement.setAccountType(
						accountTypeRepository.findOneByPid(formElementDTO.getFormAccountTypePid()).get());
			}
			formElement = formElementRepository.save(formElement);
			try {
				formElementValueRepository.deleteByFormElementIdIsNull();
			} catch (Exception e) {
				throw new IllegalArgumentException("Form Element Value is reffered to another table");
			}
			FormElementDTO result = formElementMapper.formElementToFormElementDTO(formElement);
			result.setFormAccountTypePid(
					formElement.getAccountType() != null ? formElement.getAccountType().getPid() : "all");
			System.out.println(result + "---------------------------------------------");
			return result;
			// return formElementMapper.formElementToFormElementDTO(formElement);
		}
		return null;
	}

	/**
	 * Get all the formElements.
	 * 
	 * @param pageable the pagination information
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
	 * @param pageable the pagination information
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
	 * @param id the id of the entity
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
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FormElementDTO> findOneByPid(String pid) {
		log.debug("Request to get FormElement by pid : {}", pid);
		return formElementRepository.findOneByPid(pid).map(formElement -> {
			FormElementDTO formElementDTO = formElementMapper.formElementToFormElementDTO(formElement);
			formElementDTO.setFormAccountTypePid(
					formElement.getAccountType() != null ? formElement.getAccountType().getPid() : "all");
			return formElementDTO;
		});
	}

	/**
	 * Get one formElement by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FormElementDTO> findByName(String name) {
		log.debug("Request to get FormElementGroup by name : {}", name);
		return formElementRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(formElement -> {
					FormElementDTO formElementDTO = formElementMapper.formElementToFormElementDTO(formElement);
					formElementDTO.setFormAccountTypePid(
							formElement.getAccountType() != null ? formElement.getAccountType().getPid() : "all");
					return formElementDTO;
				});
	}

	/**
	 * Delete the formElement by id.
	 * 
	 * @param id the id of the entity
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

			// result = formElementMapper.formElementsToFormElementDTOs(formElements);

			result = convertFoemElementToFormElementDtos(formElements);

		} else {
			List<FormElement> formElements = formElementRepository.findAllByCompanyId();
//			System.out.println("---*****************************");
//			for(FormElement fe : formElements) {
//				System.out.println("----"+fe.getName());
//				for(FormElementValue fv : fe.getFormElementValues()) {
//					System.out.println("---- *"+fv.getName());
//					for(FormElement fee : fv.getFormElements()) {
//						System.out.println("        "+fee.toString());
//					}
//				}
//			}
//	
//			List<FormElementDTO> formElementList = new ArrayList<>();
//			for(FormElement formElement : formElements) {
//				
//				List<FormElementValueDTO> formElementValueList = new ArrayList<>();
//				for(FormElementValue formValue : formElement.getFormElementValues()) {
//					
//					List<FormElementDTO> subElementList = new ArrayList<>();
//					for(FormElement subElement : formValue.getFormElements()) {
//						
//						 Optional<FormElement> sub = formElements.stream()
//						.filter(formElem -> formElem.getPid().equals(subElement.getPid())).findAny();
//						
//						if(sub.isPresent()) {
//							FormElementDTO feDTO = new FormElementDTO(sub.get());
//							
//							subElementList.add(feDTO);
//						}
//					}
//					FormElementValueDTO formValueDto = new FormElementValueDTO(formValue);
//					formElementValueList.add(formValueDto);
//				}
//				FormElementDTO formElementDto = new FormElementDTO(formElement);
//				formElementDto.setFormElementValues(formElementValueList);
//				formElementList.add(formElementDto);
//			}
//			result = formElementList;
			// result = formElementMapper.formElementsToFormElementDTOs(formElements);

			result = convertFoemElementToFormElementDtos(formElements);
		}
		return result;
	}

	private List<FormElementDTO> convertFoemElementToFormElementDtos(List<FormElement> formElements) {
		
		SecurityUtils.getCurrentUserLogin();

		List<FormElementDTO> formElementDtos = new ArrayList<>();

		for (FormElement formElement : formElements) {
			if (formElement == null) {
				return null;
			}

			FormElementDTO formElementDTO = new FormElementDTO();

			formElementDTO.setFormElementTypeName(formElement.getFormElementType().getName());
			formElementDTO.setFormElementTypeId(formElement.getFormElementType().getId());
			formElementDTO.setPid(formElement.getPid());
			formElementDTO.setName(formElement.getName());
			List<FormElementValueDTO> list = formElementValueSetToFormElementValueDTOList(
					formElement.getFormElementValues());
			if (list != null) {
				formElementDTO.setFormElementValues(list);
			}
			formElementDTO.setActivated(formElement.getActivated());
			formElementDTO.setDefaultValue(formElement.getDefaultValue());
			formElementDTO.setLastModifiedDate(formElement.getLastModifiedDate());
			formElementDTO.setFormLoadFromMobile(formElement.getFormLoadFromMobile());
			formElementDTO.setFormLoadMobileData(formElement.getFormLoadMobileData());

			formElementDTO.setFormAccountTypePid(
					formElement.getAccountType() != null ? formElement.getAccountType().getPid() : null);

			formElementDtos.add(formElementDTO);
		}

		return formElementDtos;
	}

	private List<FormElementValueDTO> formElementValueSetToFormElementValueDTOList(Set<FormElementValue> set) {
		if (set == null) {
			return null;
		}

		List<FormElementValueDTO> list = new ArrayList<FormElementValueDTO>();
		for (FormElementValue formElementValue : set) {
			list.add(formElementValueToFormElementValueDTO(formElementValue));
		}

		return list;
	}

	private FormElementValueDTO formElementValueToFormElementValueDTO(FormElementValue formElementValue) {
		if (formElementValue == null) {
			return null;
		}

		FormElementValueDTO formElementValueDTO = new FormElementValueDTO();

		if (formElementValue.getId() != null) {
			formElementValueDTO.setId(String.valueOf(formElementValue.getId()));
		}
		formElementValueDTO.setName(formElementValue.getName());

		return formElementValueDTO;
	}

	/**
	 * Update the FormElement status by pid.
	 * 
	 * @param pid    the pid of the entity
	 * @param active the active of the entity
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
	 * @param active   the active of the entity
	 * 
	 * @param pageable the pageable of the entity
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
	 * @param deactive the deactive of the entity
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
