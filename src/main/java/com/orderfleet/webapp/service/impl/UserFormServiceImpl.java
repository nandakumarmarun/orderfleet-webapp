package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

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
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserForm;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.repository.UserFormRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserFormService;
import com.orderfleet.webapp.web.rest.dto.UserFormDTO;
import com.orderfleet.webapp.web.rest.mapper.UserFormMapper;

/**
 * Service Implementation for managing UserForm.
 *
 * @author Sarath
 * @since Apr 19, 2017
 *
 */
@Service
@Transactional
public class UserFormServiceImpl implements UserFormService {

	private final Logger log = LoggerFactory.getLogger(UserFormServiceImpl.class);

	@Inject
	private UserFormRepository userFormRepository;

	@Inject
	private UserFormMapper userFormMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private FormRepository formRepository;

	@Override
	public UserFormDTO save(UserFormDTO userFormDTO) {
		log.debug("Request to save UserForm : {}", userFormDTO);
		UserForm userForm = userFormMapper.userFormDTOToUserForm(userFormDTO);
		// set company
		userForm.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		userForm = userFormRepository.save(userForm);
		UserFormDTO result = userFormMapper.userFormToUserFormDTO(userForm);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserForm> findAll(Pageable pageable) {
		log.debug("Request to get all UserForm");
		Page<UserForm> result = userFormRepository.findAll(pageable);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserFormDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all UserForms");
		Page<UserForm> userForms = userFormRepository.findAllByCompanyId(pageable);
		Page<UserFormDTO> result = new PageImpl<UserFormDTO>(
				userFormMapper.userFormsToUserFormDTOs(userForms.getContent()), pageable, userForms.getTotalElements());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserFormDTO> findAllByCompany() {
		log.debug("Request to get all UserForm");
		List<UserForm> userForms = userFormRepository.findAllByCompanyId();
		List<UserFormDTO> result = userFormMapper.userFormsToUserFormDTOs(userForms);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public UserFormDTO findOne(Long id) {
		log.debug("Request to get UserForm : {}", id);
		UserForm userForm = userFormRepository.findOne(id);
		UserFormDTO userFormDTO = userFormMapper.userFormToUserFormDTO(userForm);
		return userFormDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserFormDTO> findAllByFormPid(String formPid) {
		log.debug("request to get all by formPid");
		List<UserForm> userForms = userFormRepository.findAllByFormPid(formPid);
		List<UserFormDTO> result = userFormMapper.userFormsToUserFormDTOs(userForms);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserFormDTO> findFormByUserPid(String userPid) {
		log.debug("Request to get all by userPid");
		List<UserForm> Forms = userFormRepository.findFormByUserPid(userPid);
		List<UserFormDTO> result = userFormMapper.userFormsToUserFormDTOs(Forms);
		return result;
	}

	@Override
	public void saveUserForm(String userPid, List<UserFormDTO> assignedUserForms) {
		log.debug("Request to save User Forms");

		User user = userRepository.findOneByPid(userPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		List<UserForm> userForms = new ArrayList<>();
		for (UserFormDTO userFormDTO : assignedUserForms) {
			Form form = formRepository.findOneByPid(userFormDTO.getFormPid()).get();
			userForms.add(new UserForm(company, user, form, userFormDTO.getSortOrder()));
		}
		userFormRepository.deleteByUserPid(userPid);
		userFormRepository.save(userForms);

	}
}
