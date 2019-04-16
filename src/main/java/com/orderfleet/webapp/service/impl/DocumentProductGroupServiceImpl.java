package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentProductGroup;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentProductGroupRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.UserDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentProductGroupService;
import com.orderfleet.webapp.web.rest.dto.DocumentProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductGroupMapper;

/**
 * Service Implementation for managing DocumentProductGroup.
 * 
 * @author Sarath
 * @since July 8, 2016
 */
@Service
@Transactional
public class DocumentProductGroupServiceImpl implements DocumentProductGroupService {

	private final Logger log = LoggerFactory.getLogger(DocumentProductGroupServiceImpl.class);

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentProductGroupRepository documentProductGroupRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupMapper productGroupMapper;

	@Inject
	private UserDocumentRepository userDocumentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(List<DocumentProductGroupDTO> documentProductGroupDTOs) {
		log.debug("Request to save Document Product Groups");

		Document document = documentRepository.findOneByPid(documentProductGroupDTOs.get(0).getDocumentPid()).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<DocumentProductGroup> documentProductGroups = new ArrayList<>();
		for (DocumentProductGroupDTO documentProductGroupDTO : documentProductGroupDTOs) {
			ProductGroup productGroup = productGroupRepository
					.findOneByPid(documentProductGroupDTO.getProductGroupPid()).get();
			documentProductGroups.add(
					new DocumentProductGroup(document, productGroup, company, documentProductGroupDTO.getSortOrder()));
		}
		documentProductGroupRepository.deleteByDocumentPid(document.getPid());
		documentProductGroupRepository.save(documentProductGroups);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductGroupDTO> findProductGroupsByDocumentPid(String documentPid) {
		log.debug("Request to get all ProductGroups under in a documents");
		List<ProductGroup> productGroupList = documentProductGroupRepository
				.findProductGroupsByDocumentPid(documentPid);
		List<ProductGroupDTO> result = productGroupMapper.productGroupsToProductGroupDTOs(productGroupList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentProductGroupDTO> findByUserDocumentIsCurrentUser() {
		// get user documents
		List<Document> documents = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		// get Document Product Groups by user documents
		List<DocumentProductGroup> documentProductGroups = documentProductGroupRepository.findByDocumentIn(documents);
		List<DocumentProductGroupDTO> rsult = documentProductGroups.stream().map(DocumentProductGroupDTO::new)
				.collect(Collectors.toList());
		return rsult;
	}

	@Override
	public List<DocumentProductGroupDTO> findByDocumentPid(String documentPid) {
		// get Document Product Groups by user documents
		List<DocumentProductGroup> documentProductGroups = documentProductGroupRepository
				.findByDocumentPid(documentPid);
		List<DocumentProductGroupDTO> rsult = documentProductGroups.stream().map(DocumentProductGroupDTO::new)
				.collect(Collectors.toList());
		return rsult;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentProductGroupDTO> findByUserDocumentIsCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		// get user documents
		List<Document> documents = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		// get Document Product Groups by user documents
		List<DocumentProductGroup> documentProductGroups = documentProductGroupRepository.findByDocumentInAndlastModifiedDate(documents,lastModifiedDate);
		List<DocumentProductGroupDTO> rsult = documentProductGroups.stream().map(DocumentProductGroupDTO::new)
				.collect(Collectors.toList());
		return rsult;
	}

}
