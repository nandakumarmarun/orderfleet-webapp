package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.ReceiptDocument;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.ReceiptDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ReceiptDocumentService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.mapper.DocumentMapper;

/**
 * Service Implementation for managing ReceiptDocument.
 * 
 * @author Sarath
 * @since Sep 6, 2016
 */
@Service
@Transactional
public class ReceiptDocumentServiceImpl implements ReceiptDocumentService {

	private final Logger log = LoggerFactory.getLogger(ReceiptDocumentServiceImpl.class);

	@Inject
	private ReceiptDocumentRepository receiptDocumentRepository;

	@Inject
	private DocumentMapper documentMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Override
	public void save(String assignedDocuments) {

		log.debug("Request to save User Documents");

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		String[] documents = assignedDocuments.split(",");
		List<ReceiptDocument> userDocuments = new ArrayList<>();
		for (String activityPid : documents) {
			Document document = documentRepository.findOneByPid(activityPid).get();
			userDocuments.add(new ReceiptDocument(document, company, true));

		}
		receiptDocumentRepository.deleteByCompanyPid(company.getPid());
		receiptDocumentRepository.save(userDocuments);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ReceiptDocument> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all ReceiptDocuments");
		Page<ReceiptDocument> receiptDocuments = receiptDocumentRepository.findAllByCompanyId(pageable);
		return receiptDocuments;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findAllDocumentsByCompanyId() {
		log.debug("Request to get all ReceiptDocuments");
		List<Document> documents = receiptDocumentRepository.findAllDocumentsByCompanyId();
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documents);
		return result;
	}

	@Override
	public Page<ReceiptDocument> findAllByCompanyIdAndActivedTrue(Pageable pageable) {
		log.debug("Request to get all ReceiptDocuments");
		Page<ReceiptDocument> receiptDocuments = receiptDocumentRepository.findAllByCompanyIdAndActivedTrue(pageable);
		return receiptDocuments;
	}

	@Override
	public List<ReceiptDocument> findAllByCompanyIdAndActivedTrue() {
		List<ReceiptDocument> receiptDocuments = receiptDocumentRepository.findAllByCompanyIdAndActivedTrue();
		return receiptDocuments;
	}

}
