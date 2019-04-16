package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.FeedbackGroup;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FeedbackGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FeedbackGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.FeedbackGroupDTO;

/**
 * Service Implementation for managing FeedbackGroup.
 * 
 * @author Muhammed Riyas T
 * @since Feb 11, 2017
 */
@Service
@Transactional
public class FeedbackGroupServiceImpl implements FeedbackGroupService {

	private final Logger log = LoggerFactory.getLogger(FeedbackGroupServiceImpl.class);

	@Inject
	private FeedbackGroupRepository feedbackGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a feedbackGroup.
	 * 
	 * @param feedbackGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public FeedbackGroupDTO save(FeedbackGroupDTO feedbackGroupDTO) {
		log.debug("Request to save FeedbackGroup : {}", feedbackGroupDTO);

		FeedbackGroup feedbackGroup = new FeedbackGroup();
		feedbackGroup.setName(feedbackGroupDTO.getName());
		feedbackGroup.setAlias(feedbackGroupDTO.getAlias());
		feedbackGroup.setDescription(feedbackGroupDTO.getDescription());
		// set pid
		feedbackGroup.setPid(FeedbackGroupService.PID_PREFIX + RandomUtil.generatePid());
		// set company
		feedbackGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		feedbackGroup = feedbackGroupRepository.save(feedbackGroup);
		FeedbackGroupDTO result = new FeedbackGroupDTO(feedbackGroup);
		return result;
	}

	/**
	 * Update a feedbackGroup.
	 * 
	 * @param feedbackGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public FeedbackGroupDTO update(FeedbackGroupDTO feedbackGroupDTO) {
		log.debug("Request to Update FeedbackGroup : {}", feedbackGroupDTO);

		return feedbackGroupRepository.findOneByPid(feedbackGroupDTO.getPid()).map(feedbackGroup -> {
			feedbackGroup.setName(feedbackGroupDTO.getName());
			feedbackGroup.setAlias(feedbackGroupDTO.getAlias());
			feedbackGroup.setDescription(feedbackGroupDTO.getDescription());
			feedbackGroup = feedbackGroupRepository.save(feedbackGroup);
			FeedbackGroupDTO result = new FeedbackGroupDTO(feedbackGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the feedbackGroups.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<FeedbackGroupDTO> findAllByCompany() {
		log.debug("Request to get all FeedbackGroups");
		List<FeedbackGroup> feedbackGroupList = feedbackGroupRepository.findAllByCompanyId();
		List<FeedbackGroupDTO> result = feedbackGroupList.stream().map(FeedbackGroupDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one feedbackGroup by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FeedbackGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get FeedbackGroup by pid : {}", pid);
		return feedbackGroupRepository.findOneByPid(pid).map(feedbackGroup -> {
			FeedbackGroupDTO feedbackGroupDTO = new FeedbackGroupDTO(feedbackGroup);
			return feedbackGroupDTO;
		});
	}

	/**
	 * Get one feedbackGroup by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FeedbackGroupDTO> findByName(String name) {
		log.debug("Request to get FeedbackGroup by name : {}", name);
		return feedbackGroupRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(feedbackGroup -> {
					FeedbackGroupDTO feedbackGroupDTO = new FeedbackGroupDTO(feedbackGroup);
					return feedbackGroupDTO;
				});
	}

	/**
	 * Delete the feedbackGroup by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete FeedbackGroup : {}", pid);
		feedbackGroupRepository.findOneByPid(pid).ifPresent(feedbackGroup -> {
			feedbackGroupRepository.delete(feedbackGroup.getId());
		});
	}

}
