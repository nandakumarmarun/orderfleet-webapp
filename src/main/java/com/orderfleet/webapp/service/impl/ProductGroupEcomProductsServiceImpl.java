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
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupEcomProduct;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupEcomProductsService;
import com.orderfleet.webapp.web.ecom.mapper.EcomProductProfileMapper;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductGroupMapper;

/**
 * Service Implementation for managing ProductGroupEcomProduct.
 * 
 * @author Sarath
 * @since Sep 24, 2016
 */
@Service
@Transactional
public class ProductGroupEcomProductsServiceImpl implements ProductGroupEcomProductsService {

	private final Logger log = LoggerFactory.getLogger(ProductGroupEcomProductsServiceImpl.class);

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupEcomProductsRepository productGroupEcomProductRepository;

	@Inject
	private EcomProductProfileRepository ecomProductProfileRepository;

	@Inject
	private EcomProductProfileMapper ecomProductProfileMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductGroupMapper productGroupMapper;

	@Override
	public void save(String productGroupPid, String assignedProducts) {
		log.debug("Request to save ProductGroup Ecom Products");
		ProductGroup productGroup = productGroupRepository.findOneByPid(productGroupPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] products = assignedProducts.split(",");
		List<ProductGroupEcomProduct> productGroupEcomProducts = new ArrayList<>();
		for (String productPid : products) {
			EcomProductProfile product = ecomProductProfileRepository.findOneByPid(productPid).get();
			productGroupEcomProducts.add(new ProductGroupEcomProduct(product, productGroup, company));
		}
		productGroupEcomProductRepository.deleteByProductGroupPid(productGroupPid);
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
	public List<ProductGroupEcomProduct> findAllByCompany() {
		log.debug("Request to get all EcomProducts");
		List<ProductGroupEcomProduct> productGroupEcomProducts = productGroupEcomProductRepository.findAllByCompanyId();
		return productGroupEcomProducts;
	}

	@Override
	public List<ProductGroupDTO> findAllProductGroupByEcomProductPidIn(List<String> ecomProductPids) {
		
		List<ProductGroup> productList=new ArrayList<>();
		if(!ecomProductPids.isEmpty()) {
		 productList = productGroupEcomProductRepository
				.findAllProductGroupByEcomProductPidIn(ecomProductPids);
		}
		List<ProductGroup> newProductGroups = new ArrayList<>(new HashSet<>(productList));
		List<ProductGroupDTO> result = productGroupMapper.productGroupsToProductGroupDTOs(newProductGroups);
		return result;
	}

}
