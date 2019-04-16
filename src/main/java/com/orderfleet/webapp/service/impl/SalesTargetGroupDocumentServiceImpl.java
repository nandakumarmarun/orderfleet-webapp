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
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupDocument;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SalesTargetGroupDocumentService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.mapper.DocumentMapper;

/**
 * Service Implementation for managing SalesTargetGroupDocument.
 *
 * @author Sarath
 * @since Oct 14, 2016
 */

@Service
@Transactional
public class SalesTargetGroupDocumentServiceImpl implements SalesTargetGroupDocumentService {

	private final Logger log = LoggerFactory.getLogger(SalesTargetGroupDocumentServiceImpl.class);

	@Inject
	private SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;

	@Inject
	private DocumentMapper documentMapper;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findSalesTargetGroupDocumentsBySalesTargetGroupPid(String salesTargetGroupPid) {
		log.debug("Request to get all Documents");
		List<Document> documentList = salesTargetGroupDocumentRepository
				.findDocumentsBySalesTargetGroupPid(salesTargetGroupPid);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documentList);
		return result;
	}

	@Override
	public void save(String salesTargetGroupPid, String assignedDocuments) {

		log.debug("Request to save User Sales Target Group Document");
		SalesTargetGroup salesTargetGroup = salesTargetGroupRepository.findOneByPid(salesTargetGroupPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] documents = assignedDocuments.split(",");
		List<SalesTargetGroupDocument> userDocuments = new ArrayList<>();
		for (String activityPid : documents) {
			Document document = documentRepository.findOneByPid(activityPid).get();
			userDocuments.add(new SalesTargetGroupDocument(document, salesTargetGroup, company));
		}
		salesTargetGroupDocumentRepository.deleteBySalesTargetGroupPid(salesTargetGroupPid);
		salesTargetGroupDocumentRepository.save(userDocuments);
	}
}
