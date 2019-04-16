package com.orderfleet.webapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.UserForm;
import com.orderfleet.webapp.web.rest.dto.UserFormDTO;

/**
 * Service Interface for managing UserForm.
 *
 * @author Sarath
 * @since Apr 19, 2017
 *
 */
public interface UserFormService {

	UserFormDTO save(UserFormDTO userForm);

	Page<UserForm> findAll(Pageable pageable);

	Page<UserFormDTO> findAllByCompany(Pageable pageable);

	List<UserFormDTO> findAllByCompany();

	List<UserFormDTO> findFormByUserPid(String userPid);

	UserFormDTO findOne(Long id);

	List<UserFormDTO> findAllByFormPid(String formPid);

	void saveUserForm(String userPid, List<UserFormDTO> assignedUserForms);

}
