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
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.mapper.DocumentMapper;

/**
 * Service Implementation for managing PrimarySalesDocument.
 * 
 * @author Sarath
 * @since Sep 6, 2016
 */
@Service
@Transactional
public class PrimarySecondaryDocumentServiceImpl implements PrimarySecondaryDocumentService {

	private final Logger log = LoggerFactory.getLogger(PrimarySecondaryDocumentServiceImpl.class);

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private DocumentMapper documentMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Override
	public void save(String assignedDocuments, VoucherType voucherType) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] documents = assignedDocuments.split(",");
		List<PrimarySecondaryDocument> userDocuments = new ArrayList<>();
		for (String activityPid : documents) {
			Document document = documentRepository.findOneByPid(activityPid).get();
			userDocuments.add(new PrimarySecondaryDocument(document, company,voucherType,true));

		}
		primarySecondaryDocumentRepository.deleteByVoucherTypeAndCompanyPid(voucherType, company.getPid());
		primarySecondaryDocumentRepository.save(userDocuments);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PrimarySecondaryDocument> findAllByCompany(VoucherType voucherType, Pageable pageable) {
		log.debug("Request to get all PrimarySalesDocuments");
		Page<PrimarySecondaryDocument> primarySalesDocuments = primarySecondaryDocumentRepository.findByVoucherTypeAndCompanyId(voucherType, pageable);
		return primarySalesDocuments;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findAllDocumentsByCompanyId() {
		log.debug("Request to get all PrimarySalesDocuments");
		List<Document> documents = primarySecondaryDocumentRepository.findAllDocumentsByCompanyId();
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documents);
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findAllDocumentsByCompanyIdAndVoucherType(VoucherType voucherType) {
		log.debug("Request to get all PrimarySalesDocuments");
		List<Document> documents = primarySecondaryDocumentRepository.findDocumentsByVoucherTypeAndCompanyId(voucherType);
		List<DocumentDTO> result = documentMapper.documentsToDocumentDTOs(documents);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PrimarySecondaryDocument> findAllByCompanyIdAndActivedTrue(Pageable pageable) {
		log.debug("Request to get all PrimarySalesDocuments");
		Page<PrimarySecondaryDocument> primarySalesDocuments = primarySecondaryDocumentRepository.findByCompanyIdAndActivedTrue(pageable);
		return primarySalesDocuments;
	}

	@Override
	@Transactional(readOnly = true)
	public List<VoucherType> findAllVoucherTypesByCompanyId() {
		log.debug("Request to get all vouchertypes");
		List<VoucherType> result = primarySecondaryDocumentRepository.findAllVoucherTypesByCompanyId();
		return result;
	}

	@Override
	public List<PrimarySecondaryDocument> findAllByCompanyIdAndActivedTrue() {
		List<PrimarySecondaryDocument> primarySalesDocuments = primarySecondaryDocumentRepository.findByCompanyIdAndActivedTrue();
		return primarySalesDocuments;
	}

}
