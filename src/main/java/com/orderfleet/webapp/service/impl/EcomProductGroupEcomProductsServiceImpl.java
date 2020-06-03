package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.EcomProductGroupEcomProduct;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EcomProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.EcomProductGroupRepository;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EcomProductGroupEcomProductsService;
import com.orderfleet.webapp.service.ProductGroupEcomProductsService;
import com.orderfleet.webapp.web.ecom.mapper.EcomProductProfileMapper;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.EcomProductGroupMapper;

/**
 * Service Implementation for managing EcomProductGroupEcomProduct.
 * 
 * @author Anish
 * @since June 1, 2020
 */
@Service
@Transactional
public class EcomProductGroupEcomProductsServiceImpl implements EcomProductGroupEcomProductsService {

	private final Logger log = LoggerFactory.getLogger(EcomProductGroupEcomProductsServiceImpl.class);

	@Inject
	private EcomProductGroupRepository productGroupRepository;

	@Inject
	private EcomProductGroupEcomProductsRepository productGroupEcomProductRepository;

	@Inject
	private EcomProductProfileRepository ecomProductProfileRepository;

	@Inject
	private EcomProductProfileMapper ecomProductProfileMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private EcomProductGroupMapper productGroupMapper;

	@Override
	public void save(String productGroupPid, String assignedProducts) {
		log.debug("Request to save ProductGroup Ecom Products");
		EcomProductGroup productGroup = productGroupRepository.findOneByPid(productGroupPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] products = assignedProducts.split(",");
		List<EcomProductGroupEcomProduct> productGroupEcomProducts = new ArrayList<>();
		for (String productPid : products) {
			EcomProductProfile product = ecomProductProfileRepository.findOneByPid(productPid).get();
			productGroupEcomProducts.add(new EcomProductGroupEcomProduct(product, productGroup, company));
		}
		productGroupEcomProductRepository.deleteByEcomProductGroupPid(productGroupPid);
		productGroupEcomProductRepository.save(productGroupEcomProducts);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductProfileDTO> findEcomProductByProductGroupPid(String productGroupPid) {
		log.debug("Request to get all Products under in a productGroups");
		List<EcomProductProfile> productList = productGroupEcomProductRepository
				.findEcomProductByProductGroupPid(productGroupPid);
		List<EcomProductProfileDTO> result = ecomProductProfileMapper
				.ecomProductProfilesToEcomProductProfileDTOs(productList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductGroupEcomProduct> findAllByCompany() {
		log.debug("Request to get all EcomProducts");
		List<EcomProductGroupEcomProduct> productGroupEcomProducts = productGroupEcomProductRepository.findAllByCompanyId();
		return productGroupEcomProducts;
	}

	@Override
	public List<EcomProductGroupDTO> findAllProductGroupByEcomProductPidIn(List<String> ecomProductPids) {
		
		List<EcomProductGroup> productList=new ArrayList<>();
		if(!ecomProductPids.isEmpty()) {
		 productList = productGroupEcomProductRepository
				.findAllProductGroupByEcomProductPidIn(ecomProductPids);
		}
		List<EcomProductGroup> newProductGroups = new ArrayList<>(new HashSet<>(productList));
		List<EcomProductGroupDTO> result = productGroupMapper.productGroupsToProductGroupDTOs(newProductGroups);
		return result;
	}

}
