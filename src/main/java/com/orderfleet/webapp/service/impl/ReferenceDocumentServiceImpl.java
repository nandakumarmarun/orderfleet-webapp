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
import com.orderfleet.webapp.domain.ReferenceDocument;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.ReferenceDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ReferenceDocumentService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.mapper.DocumentMapper;

@Service
@Transactional
public class ReferenceDocumentServiceImpl implements ReferenceDocumentService {

	private final Logger log = LoggerFactory.getLogger(ReferenceDocumentServiceImpl.class);

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ReferenceDocumentRepository referenceDocumentRepository;

	@Inject
	private DocumentMapper documentMapper;

	@Override
	public void saveReferenceDocument(String documentPid, String assignedReferenceDocuments) {
		log.debug("Request to save Reference Document");
		Document document = documentRepository.findOneByPid(documentPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] referenceDocumentPids = assignedReferenceDocuments.split(",");
		List<ReferenceDocument> referenceDocuments = new ArrayList<>();
		for (String referenceDocumentPid : referenceDocumentPids) {
			Document referenceDocument = documentRepository.findOneByPid(referenceDocumentPid).get();
			referenceDocuments.add(new ReferenceDocument(document, referenceDocument, company));
		}
		referenceDocumentRepository.deleteByDocumentPid(documentPid);
		referenceDocumentRepository.save(referenceDocuments);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findReferenceDocumentsByDocumentPid(String documentPid) {
		log.debug("Request to get all Reference Documents under in a documents");
		List<Document> documents = referenceDocumentRepository.findReferenceDocumentsByDocumentPid(documentPid);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documents);
		return result;
	}
}
