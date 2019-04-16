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
import com.orderfleet.webapp.domain.DocumentProductCategory;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentProductCategoryRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.UserDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentProductCategoryService;
import com.orderfleet.webapp.web.rest.dto.DocumentProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductCategoryMapper;

/**
 * Service Implementation for managing DocumentProductCategory.
 * 
 * @author Sarath
 * @since July 8 2016
 */
@Service
@Transactional
public class DocumentProductCategoryServiceImpl implements DocumentProductCategoryService {

	private final Logger log = LoggerFactory.getLogger(DocumentProductCategoryServiceImpl.class);

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private DocumentProductCategoryRepository documentProductCategoryRepository;

	@Inject
	private ProductCategoryMapper productCategoryMapper;

	@Inject
	private UserDocumentRepository userDocumentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String documentPid, String assignedProductCategories) {

		log.debug("Request to save Document Product Categories");

		Document document = documentRepository.findOneByPid(documentPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] productCategories = assignedProductCategories.split(",");

		List<DocumentProductCategory> documentProductCategories = new ArrayList<>();

		for (String activityGroupPid : productCategories) {
			ProductCategory producttegory = productCategoryRepository.findOneByPid(activityGroupPid).get();
			documentProductCategories.add(new DocumentProductCategory(document, producttegory, company));
		}
		documentProductCategoryRepository.deleteByDocumentPid(documentPid);
		documentProductCategoryRepository.save(documentProductCategories);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductCategoryDTO> findProductCategoriesByDocumentPid(String documentPid) {
		log.debug("Request to get all ProductCategories under in a documents");
		List<ProductCategory> productCategoriesList = documentProductCategoryRepository
				.findProductCategoriesByDocumentPid(documentPid);
		List<ProductCategoryDTO> result = productCategoryMapper
				.productCategoriesToProductCategoryDTOs(productCategoriesList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentProductCategoryDTO> findByUserDocumentIsCurrentUser() {
		// get user documents
		List<Document> documents = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		// get Document Product Categories by user documents
		List<DocumentProductCategory> documentProductCategories = documentProductCategoryRepository
				.findByDocumentIn(documents);
		List<DocumentProductCategoryDTO> rsult = documentProductCategories.stream().map(DocumentProductCategoryDTO::new)
				.collect(Collectors.toList());
		return rsult;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentProductCategoryDTO> findByUserDocumentIsCurrentUserAndLastModifiedDate(
			LocalDateTime lastModifiedDate) {
		// get user documents
		List<Document> documents = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		// get Document Product Categories by user documents
		List<DocumentProductCategory> documentProductCategories = documentProductCategoryRepository
				.findByDocumentInAndLastModifiedandLastModifiedDate(documents, lastModifiedDate);
		List<DocumentProductCategoryDTO> rsult = documentProductCategories.stream().map(DocumentProductCategoryDTO::new)
				.collect(Collectors.toList());
		return rsult;
	}

}
