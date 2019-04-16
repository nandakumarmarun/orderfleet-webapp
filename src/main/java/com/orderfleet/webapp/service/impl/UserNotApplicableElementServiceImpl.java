package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserNotApplicableElement;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.repository.UserNotApplicableElementRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserNotApplicableElementService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.UserNotApplicableElementDTO;

/**
 * Service Implementation for managing UserNotApplicableElement.
 * 
 * @author Muhammed Riyas T
 * @since Jan 18, 2017
 */
@Service
@Transactional
public class UserNotApplicableElementServiceImpl implements UserNotApplicableElementService {

	private final Logger log = LoggerFactory.getLogger(UserNotApplicableElementServiceImpl.class);

	@Inject
	private UserNotApplicableElementRepository userNotApplicableElementRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private FormRepository formRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a userNotApplicableElement.
	 * 
	 * @param userNotApplicableElementDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public void save(String documentPid, List<UserNotApplicableElementDTO> userNotApplicableElementDTOs) {
		log.debug("Request to save UserNotApplicableElement ");
		Document document = documentRepository.findOneByPid(documentPid).get();
		List<UserNotApplicableElement> userNotApplicableElements = new ArrayList<>();
		for (UserNotApplicableElementDTO userNotApplicableElementDTO : userNotApplicableElementDTOs) {
			UserNotApplicableElement userNotApplicableElement = new UserNotApplicableElement();
			userNotApplicableElement.setDocument(document);
			userNotApplicableElement
					.setForm(formRepository.findOneByPid(userNotApplicableElementDTO.getFormPid()).get());
			userNotApplicableElement.setFormElement(
					formElementRepository.findOneByPid(userNotApplicableElementDTO.getFormElementPid()).get());
			userNotApplicableElement.setCompany(companyRepository.getOne(SecurityUtils.getCurrentUsersCompanyId()));
			userNotApplicableElement
					.setUser(userRepository.findOneByPid(userNotApplicableElementDTO.getUserPid()).get());
			userNotApplicableElements.add(userNotApplicableElement);
		}
		userNotApplicableElementRepository.save(userNotApplicableElements);
	}

	/**
	 * Get all the userNotApplicableElements.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserNotApplicableElementDTO> findAllByCompany() {
		log.debug("Request to get all UserNotApplicableElements");
		List<UserNotApplicableElement> userNotApplicableElementList = userNotApplicableElementRepository
				.findAllByCompanyId();
		List<UserNotApplicableElementDTO> result = userNotApplicableElementList.stream()
				.map(UserNotApplicableElementDTO::new).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get current user NotApplicableElements.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserNotApplicableElementDTO> findByUserIsCurrentUser() {
		log.debug("Request to current UserNotApplicableElements");
		List<UserNotApplicableElement> userNotApplicableElementList = userNotApplicableElementRepository
				.findByUserIsCurrentUser();
		List<UserNotApplicableElementDTO> result = userNotApplicableElementList.stream()
				.map(UserNotApplicableElementDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDTO> findUsersByDocumentAndFormAndFormElement(String documentPid, String formPid,
			String formElementPid) {
		List<User> users = userNotApplicableElementRepository.findUsersByDocumentAndFormAndFormElement(documentPid,
				formPid, formElementPid);
		List<UserDTO> result = users.stream()
				.map((User u) -> new UserDTO(u.getPid(), u.getFirstName(), u.getLastName()))
				.collect(Collectors.toList());
		return result;
	}

}
