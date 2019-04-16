package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.FeedbackGroup;
import com.orderfleet.webapp.domain.FeedbackGroupFormElement;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.UserFeedbackGroup;
import com.orderfleet.webapp.domain.enums.FeedbackElementType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FeedbackGroupFormElementRepository;
import com.orderfleet.webapp.repository.FeedbackGroupRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.UserFeedbackGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FeedbackGroupFormElementService;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;

/**
 * Service Implementation for managing FeedbackGroup.
 * 
 * @author Muhammed Riyas T
 * @since Feb 11, 2017
 */
@Service
@Transactional
public class FeedbackGroupFormElementServiceImpl implements FeedbackGroupFormElementService {

	private final Logger log = LoggerFactory.getLogger(FeedbackGroupFormElementServiceImpl.class);

	@Inject
	private FeedbackGroupFormElementRepository feedbackGroupFormElementRepository;

	@Inject
	private FeedbackGroupRepository feedbackGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private UserFeedbackGroupRepository userFeedbackGroupRepository;

	@Override
	public void save(String feedbackGroupPid, String formElements, FeedbackElementType feedbackElementType) {
		log.info("save feedback Group formElements");
		FeedbackGroup feedbackGroup = feedbackGroupRepository.findOneByPid(feedbackGroupPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String formElementPids[] = formElements.split(",");
		List<FeedbackGroupFormElement> feedbackGroupFormElements = new ArrayList<>();
		for (String formElementPid : formElementPids) {
			FeedbackGroupFormElement feedbackGroupFormElement = new FeedbackGroupFormElement();
			feedbackGroupFormElement.setFeedbackGroup(feedbackGroup);
			feedbackGroupFormElement.setFormElement(formElementRepository.findOneByPid(formElementPid).get());
			feedbackGroupFormElement.setFeedbackElementType(feedbackElementType);
			feedbackGroupFormElement.setCompany(company);
			feedbackGroupFormElements.add(feedbackGroupFormElement);
		}
		feedbackGroupFormElementRepository.deleteByFeedbackGroupPidAndFeedbackElementType(feedbackGroupPid,
				feedbackElementType);
		feedbackGroupFormElementRepository.save(feedbackGroupFormElements);
	}

	@Override
	public void save(String feedbackGroupPid, String users) {
		FeedbackGroup feedbackGroup = feedbackGroupRepository.findOneByPid(feedbackGroupPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String userPids[] = users.split(",");
		List<UserFeedbackGroup> userFeedbackGroups = new ArrayList<>();
		for (String userPid : userPids) {
			UserFeedbackGroup userFeedbackGroup = new UserFeedbackGroup();
			userFeedbackGroup.setFeedbackGroup(feedbackGroup);
			userFeedbackGroup.setUser(userRepository.findOneByPid(userPid).get());
			userFeedbackGroup.setCompany(company);
			userFeedbackGroups.add(userFeedbackGroup);
		}
		userFeedbackGroupRepository.deleteByFeedbackGroupPid(feedbackGroupPid);
		userFeedbackGroupRepository.save(userFeedbackGroups);
	}

	@Override
	public void saveStatusField(String feedbackGroupPid, String statusFieldPid) {
		FeedbackGroup feedbackGroup = feedbackGroupRepository.findOneByPid(feedbackGroupPid).get();
		feedbackGroup.setStatusField(formElementRepository.findOneByPid(statusFieldPid).get());
		feedbackGroupRepository.save(feedbackGroup);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FormElementDTO> findFormElementsByGroupPidAnd(String feedbackGroupPid,
			FeedbackElementType feedbackElementType) {
		List<FormElement> formElements = feedbackGroupFormElementRepository
				.findFormElementsByGroupPidAnd(feedbackGroupPid, feedbackElementType);
		List<FormElementDTO> formElementDTOs = formElements.stream().map(FormElementDTO::new)
				.collect(Collectors.toList());
		return formElementDTOs;
	}

	@Override
	@Transactional(readOnly = true)
	public FeedbackGroupFormElement findByFormElementPid(String formElementPid) {
		return feedbackGroupFormElementRepository.findByFormElementPid(formElementPid);
	}

}
