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
import com.orderfleet.webapp.domain.DocumentEcomProductGroup;
import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentEcomProductGroupRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.EcomProductGroupRepository;
import com.orderfleet.webapp.repository.UserDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentEcomProductGroupService;
import com.orderfleet.webapp.web.rest.dto.DocumentEcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.EcomProductGroupMapper;


/**
 * Service Implementation for managing DocumentProductGroup.
 * 
 * @author Anish
 * @since June 8, 2020
 */
@Service
@Transactional
public class DocumentEcomProductGroupServiceImpl implements DocumentEcomProductGroupService {

	private final Logger log = LoggerFactory.getLogger(DocumentEcomProductGroupServiceImpl.class);

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentEcomProductGroupRepository documentProductGroupRepository;

	@Inject
	private EcomProductGroupRepository productGroupRepository;

	@Inject
	private EcomProductGroupMapper productGroupMapper;

	@Inject
	private UserDocumentRepository userDocumentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(List<DocumentEcomProductGroupDTO> documentProductGroupDTOs) {
		log.debug("Request to save Document Product Groups");

		Document document = documentRepository.findOneByPid(documentProductGroupDTOs.get(0).getDocumentPid()).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<DocumentEcomProductGroup> documentProductGroups = new ArrayList<>();
		for (DocumentEcomProductGroupDTO documentProductGroupDTO : documentProductGroupDTOs) {
			EcomProductGroup productGroup = productGroupRepository
					.findOneByPid(documentProductGroupDTO.getProductGroupPid()).get();
			documentProductGroups.add(
					new DocumentEcomProductGroup(document, productGroup, company, documentProductGroupDTO.getSortOrder()));
		}
		documentProductGroupRepository.deleteByDocumentPid(document.getPid());
		documentProductGroupRepository.save(documentProductGroups);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductGroupDTO> findProductGroupsByDocumentPid(String documentPid) {
		log.debug("Request to get all ProductGroups under in a documents");
		List<EcomProductGroup> productGroupList = documentProductGroupRepository
				.findProductGroupsByDocumentPid(documentPid);
		List<EcomProductGroupDTO> result = productGroupMapper.productGroupsToProductGroupDTOs(productGroupList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentEcomProductGroupDTO> findByUserDocumentIsCurrentUser() {
		// get user documents
		List<Document> documents = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		// get Document Product Groups by user documents
		List<DocumentEcomProductGroup> documentProductGroups = documentProductGroupRepository.findByDocumentIn(documents);
		List<DocumentEcomProductGroupDTO> rsult = documentProductGroups.stream().map(DocumentEcomProductGroupDTO::new)
				.collect(Collectors.toList());
		return rsult;
	}

	@Override
	public List<DocumentEcomProductGroupDTO> findByDocumentPid(String documentPid) {
		// get Document Product Groups by user documents
		List<DocumentEcomProductGroup> documentProductGroups = documentProductGroupRepository
				.findByDocumentPid(documentPid);
		List<DocumentEcomProductGroupDTO> rsult = documentProductGroups.stream().map(DocumentEcomProductGroupDTO::new)
				.collect(Collectors.toList());
		return rsult;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentEcomProductGroupDTO> findByUserDocumentIsCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		// get user documents
		List<Document> documents = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		// get Document Product Groups by user documents
		List<DocumentEcomProductGroup> documentProductGroups = documentProductGroupRepository.findByDocumentInAndlastModifiedDate(documents,lastModifiedDate);
		List<DocumentEcomProductGroupDTO> rsult = documentProductGroups.stream().map(DocumentEcomProductGroupDTO::new)
				.collect(Collectors.toList());
		return rsult;
	}

}
