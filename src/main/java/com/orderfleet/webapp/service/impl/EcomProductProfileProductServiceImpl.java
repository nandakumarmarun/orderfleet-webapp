package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.EcomProductProfileProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EcomProductProfileProductService;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;

/**
 * Service Implementation for managing EcomProductProfileProduct.
 * 
 * @author Sarath
 * @since Sep 23, 2016
 */
@Service
@Transactional
public class EcomProductProfileProductServiceImpl implements EcomProductProfileProductService {
	private final Logger log = LoggerFactory.getLogger(EcomProductProfileProductServiceImpl.class);

	@Inject
	private EcomProductProfileRepository ecomProductProfileRepository;

	@Inject
	private EcomProductProfileProductRepository ecomProductProfileProductRepository;

	@Inject
	private ProductProfileRepository productRepository;

	@Inject
	private ProductProfileMapper productMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String productGroupPid, String assignedProducts) {
		log.debug("Request to save ProductGroup Products");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		EcomProductProfile productGroup = ecomProductProfileRepository.findOneByPid(productGroupPid).get();
		String[] products = assignedProducts.split(",");
		List<EcomProductProfileProduct> ecomProductProfileProducts = new ArrayList<>();
		for (String productPid : products) {
			ProductProfile product = productRepository.findOneByPid(productPid).get();
			ecomProductProfileProducts.add(new EcomProductProfileProduct(product, productGroup, company));
		}
		// saveing EcomProductProfile for update the last updatedDate
		productGroup.setLastModifiedDate(LocalDateTime.now());
		ecomProductProfileRepository.save(productGroup);

		ecomProductProfileProductRepository.deleteByEcomProductProfilePid(productGroupPid);
		ecomProductProfileProductRepository.save(ecomProductProfileProducts);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findProductByEcomProductProfilePid(String productGroupPid) {
		log.debug("Request to get all Products under in a productGroups");
		List<ProductProfile> productList = ecomProductProfileProductRepository
				.findProductByEcomProductProfilePid(productGroupPid);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	/**
	 * Get all the products.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<EcomProductProfileProduct> findAllByCompany() {
		log.debug("Request to get all Products");
		List<EcomProductProfileProduct> ecomProductProfileProducts = ecomProductProfileProductRepository
				.findAllByCompanyId();
		return ecomProductProfileProducts;
	}

	@Override
	public List<String> findEcomProductProfilePidsByProductPorfileIn(List<ProductProfileDTO> productProfiles) {
		List<String> productProfilePids = new ArrayList<>();
		for (ProductProfileDTO productProfileDTO : productProfiles) {
			productProfilePids.add(productProfileDTO.getPid());
		}
		List<String> ecomProductProfilePids =new ArrayList<>();
		if(!productProfilePids.isEmpty()) {
		 ecomProductProfilePids = ecomProductProfileProductRepository
				.findEcomProductProfilePidByProductPidIn(productProfilePids);
		}
		return ecomProductProfilePids;
	}

}
