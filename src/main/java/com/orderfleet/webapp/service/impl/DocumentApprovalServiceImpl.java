package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DocumentApproval;
import com.orderfleet.webapp.domain.DocumentApprovedUser;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.DocumentApprovalRepository;
import com.orderfleet.webapp.repository.DocumentApprovedUserRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentApprovalService;
import com.orderfleet.webapp.web.rest.dto.DocumentApprovalDTO;

/**
 * Service Implementation for managing DocumentApproval.
 * 
 * @author Muhammed Riyas T
 * @since November 23, 2016
 */
@Service
@Transactional
public class DocumentApprovalServiceImpl implements DocumentApprovalService {

	private final Logger log = LoggerFactory.getLogger(DocumentApprovalServiceImpl.class);

	@Inject
	private DocumentApprovalRepository documentApprovalRepository;

	@Inject
	private UserRepository userRepository;
	
	@Inject
	private DocumentApprovedUserRepository documentApprovedUserRepository;

	@Override
	public void updateDocumentApproval(DocumentApproval documentApproval) {
		documentApprovalRepository.save(documentApproval);
	}

	/**
	 * Get one documentApproval by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public DocumentApproval findOneByPid(String pid) {
		log.debug("Request to get DocumentApproval by pid : {}", pid);
		return documentApprovalRepository.findOneByPid(pid);
	}

	@Override
	public List<DocumentApprovalDTO> findDocumentApprovalsByCurrentUser() {
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		List<DocumentApproval> documentApprovals = documentApprovalRepository.findByCurrentCompany();
		List<DocumentApprovalDTO> documentApprovalDTOs = documentApprovals.stream()
				.filter(da -> da.getDocumentApprovalLevel().getUsers().contains(user)).map(docApproval -> {
					DocumentApprovalDTO documentApprovalDTO = new DocumentApprovalDTO(docApproval);
					//set current users approval status
					DocumentApprovedUser documentApprovedUser = documentApprovedUserRepository
							.findByUserIdAndDocumentApprovalId(user.getId(), docApproval.getId());
					if(documentApprovedUser != null){
						documentApprovalDTO.setApprovalStatus(documentApprovedUser.getApprovalStatus());
						documentApprovalDTO.setRemarks(documentApprovedUser.getRemarks());	
					}
					return documentApprovalDTO;
				}).collect(Collectors.toList());
		return documentApprovalDTOs;
	}

}
