package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.UserReceiptTarget;
import com.orderfleet.webapp.domain.UserReceiptTargetDocument;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.UserReceiptTargetDocumentRepository;
import com.orderfleet.webapp.repository.UserReceiptTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserReceiptTargetDocumentService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.mapper.DocumentMapper;

/**
 * Service Implementation for managing UserReceiptTargetDocument.
 * 
 * @author Sarath
 * @since Sep 3, 2016
 */

@Service
@Transactional
public class UserReceiptTargetDocumentServiceImpl implements UserReceiptTargetDocumentService {

	private final Logger log = LoggerFactory.getLogger(UserReceiptTargetDocumentServiceImpl.class);


	@Inject
	private DocumentMapper documentMapper;

	@Inject
	private UserReceiptTargetRepository userReceiptTargetRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private UserReceiptTargetDocumentRepository userReceiptTargetDocumentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findReceiptTargetDocumentsByReceiptTargetPid(String receiptTargetPid) {
		log.debug("Request to get all Documents");
		List<Document> documentList = userReceiptTargetDocumentRepository.findDocumentsByUserReceiptTargetPid(receiptTargetPid);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documentList);
		return result;
	}

	@Override
	public void save(String userReceiptTargetId, String assignedDocuments) {
		log.debug("Request to save User Documents");

		UserReceiptTarget userReceiptTarget = userReceiptTargetRepository.findOneByPid(userReceiptTargetId).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		String[] documents = assignedDocuments.split(",");
		List<UserReceiptTargetDocument> userDocuments = new ArrayList<>();
		for (String activityPid : documents) {
			String pid = UserReceiptTargetDocumentService.PID_PREFIX + RandomUtil.generatePid();
			Document document = documentRepository.findOneByPid(activityPid).get();
			userDocuments.add(new UserReceiptTargetDocument(pid, userReceiptTarget, document, company));

		}
		userReceiptTargetDocumentRepository.deleteByUserReceiptTargetPid(userReceiptTargetId);
		userReceiptTargetDocumentRepository.save(userDocuments);
	}
}
