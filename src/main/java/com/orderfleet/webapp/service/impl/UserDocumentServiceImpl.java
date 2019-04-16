package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserDocument;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.UserDocumentRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.UserDocumentDTO;
import com.orderfleet.webapp.web.rest.mapper.DocumentMapper;

/**
 * Service Implementation for managing UserDocument.
 * 
 * @author Sarath
 * @since July 5, 2016
 */
@Service
@Transactional
public class UserDocumentServiceImpl implements UserDocumentService {

	private final Logger log = LoggerFactory.getLogger(UserActivityServiceImpl.class);

	@Inject
	private UserDocumentRepository userDocumentRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentMapper documentMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String pid, List<UserDocumentDTO> assignedDocuments) {
		log.debug("Request to save User Documents");

		Optional<User> user = userRepository.findOneByPid(pid);
		if (user.isPresent()) {
			Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
			List<UserDocument> userDocuments = new ArrayList<>();
			for (UserDocumentDTO userDocumentDTO : assignedDocuments) {
				UserDocument userDocument = new UserDocument();
				String documentPid = userDocumentDTO.getDocumentPid();
				Optional<Document> document = documentRepository.findOneByPid(documentPid);
				if (document.isPresent()) {
					userDocument.setDocument(document.get());
					userDocument.setCompany(company);
					userDocument.setUser(user.get());
					userDocument.setImageOption(userDocumentDTO.getImageOption());
					userDocuments.add(userDocument);
				}
			}
			userDocumentRepository.deleteByUserPid(pid);
			userDocumentRepository.save(userDocuments);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findDocumentsByUserIsCurrentUser() {
		List<Document> documentList = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documentList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findDocumentsByUserPid(String userPid) {
		log.debug("Request to get all Documents");
		List<Document> documentList = userDocumentRepository.findDocumentsByUserPid(userPid);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documentList);
		return result;
	}

	@Override
	public void copyDocuments(String fromUserPid, List<String> toUserPids) {
		// delete association first
		userDocumentRepository.deleteByUserPidIn(toUserPids);
		List<UserDocument> userDocuments = userDocumentRepository.findByUserPid(fromUserPid);
		if (userDocuments != null && !userDocuments.isEmpty()) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for (User user : users) {
				List<UserDocument> newUserDocuments = userDocuments.stream()
						.map(ud -> new UserDocument(user, ud.getDocument(), ud.getCompany(), ud.getImageOption()))
						.collect(Collectors.toList());
				userDocumentRepository.save(newUserDocuments);
			}
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findDocumentsByUserAndDocumentType(String userPid, DocumentType documentType) {
		List<Document> documentList = userDocumentRepository.findDocumentsByUserPid(userPid);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documentList);
		List<DocumentDTO> dynamicDocuments = new ArrayList<>();
		for (DocumentDTO documentDTO : result) {
			if (documentDTO.getDocumentType().equals(documentType)) {
				dynamicDocuments.add(documentDTO);
			}
		}
		return dynamicDocuments;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findDocumentsByUserIdsAndDocumentType(List<Long> userIds, DocumentType documentType) {
		List<Document> documentList = userDocumentRepository.findDocumentsByUserIds(userIds);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documentList);
		List<DocumentDTO> dynamicDocuments = new ArrayList<>();
		for (DocumentDTO documentDTO : result) {
			if (documentDTO.getDocumentType().equals(documentType)) {
				dynamicDocuments.add(documentDTO);
			}
		}
		Set<String> deptSet = new HashSet<>();
        // directly removing the elements from list if already existed in set
		dynamicDocuments.removeIf(p -> !deptSet.add(p.getName()));
		return dynamicDocuments;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDocumentDTO> findByUserPid(String userPid) {
		log.debug("To fing user document dto by userPid", userPid);
		List<UserDocument> userDocuments = userDocumentRepository.findByUserPid(userPid);
		List<UserDocumentDTO> result = userDocuments.stream().map(UserDocumentDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDocumentDTO> findByUserIsCurrentUserAndDocumentPid(String documentPid) {
		log.debug("To fing user document dto by documentPid", documentPid);
		List<UserDocument> userDocuments = userDocumentRepository.findByUserIsCurrentUserAndDocumentPid(documentPid);
		List<UserDocumentDTO> result = userDocuments.stream().map(UserDocumentDTO::new).collect(Collectors.toList());
		return result;
	}

}
