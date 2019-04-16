package com.orderfleet.webapp.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Knowledgebase;
import com.orderfleet.webapp.domain.KnowledgebaseFiles;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.UserKnowledgebaseFileRepository;
import com.orderfleet.webapp.service.UserKnowledgebaseFileService;
import com.orderfleet.webapp.web.rest.api.dto.KnowledgeBaseFilesDTO;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseFileDTO;

/**
 * Service Implementation for managing UserKnowledgebaseFiles.
 * 
 * @author Muhammed Riyas T
 * @since October 05, 2016
 */
@Service
@Transactional
public class UserKnowledgebaseFileServiceImpl implements UserKnowledgebaseFileService {

	private final Logger log = LoggerFactory.getLogger(UserKnowledgebaseFileServiceImpl.class);

	@Inject
	private UserKnowledgebaseFileRepository userKnowledgebaseFileRepository;

	@Override
	@Transactional(readOnly = true)
	public List<KnowledgebaseFileDTO> findKnowledgebaseFilesByUserIsCurrentUser() {
		log.debug("Request to get all KnowledgebaseFiles by current user ");
		List<KnowledgebaseFiles> knowledgebaseFiles = userKnowledgebaseFileRepository
				.findKnowledgebaseFilesByUserIsCurrentUser();
		List<KnowledgebaseFileDTO> result = knowledgebaseFiles.stream().map(KnowledgebaseFileDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<KnowledgebaseFileDTO> findKnowledgebaseFilesByUserPid(String userPid) {
		log.debug("Request to get all KnowledgebaseFiles by user pid");
		List<KnowledgebaseFiles> knowledgebaseFiles = userKnowledgebaseFileRepository
				.findKnowledgebaseFilesByUserIsCurrentUser();
		List<KnowledgebaseFileDTO> result = knowledgebaseFiles.stream().map(KnowledgebaseFileDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDTO> findUsersByKnowledgebaseFilePid(String knowledgeBaseFilePid) {
		log.debug("Request to get all user KnowledgebaseFile pid");
		List<User> users = userKnowledgebaseFileRepository.findUsersByKnowledgebaseFilesPid(knowledgeBaseFilePid);
		List<UserDTO> result = users.stream().map(UserDTO::new).collect(Collectors.toList());
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<KnowledgeBaseFilesDTO> findAllByUserIsCurrentUser() {
		log.debug("Request to get all KnowledgeBaseFiless");
		List<KnowledgebaseFiles> knowledgebaseFiles = userKnowledgebaseFileRepository
				.findKnowledgebaseFilesByUserIsCurrentUser();
		if(!knowledgebaseFiles.isEmpty()){
			Map<Knowledgebase, List<KnowledgebaseFiles>> knowledgeBaseFilesByKnowledgeBase = knowledgebaseFiles.stream()
					.collect(Collectors.groupingBy(KnowledgebaseFiles::getKnowledgebase));
			List<KnowledgeBaseFilesDTO> result = knowledgeBaseFilesByKnowledgeBase.entrySet().stream()
					.map(k -> new KnowledgeBaseFilesDTO(k.getKey(), k.getValue())).collect(Collectors.toList());
			return result;
		}
		return Collections.emptyList();
	}
	
	

}
