package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupProduct;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SalesTargetGroupProductService;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;

/**
 * Service Implementation for managing SalesTargetGroupProduct.
 *
 * @author Sarath
 * @since Oct 14, 2016
 */

@Service
@Transactional
public class SalesTargetGroupProductServiceImpl implements SalesTargetGroupProductService {

	private final Logger log = LoggerFactory.getLogger(SalesTargetGroupProductServiceImpl.class);

	@Inject
	private SalesTargetGroupProductRepository salesTargetGroupProductRepository;

	@Inject
	private ProductProfileMapper productMapper;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductProfileRepository productRepository;

	@Override
	@Transactional(readOnly = true)
	public List<ProductProfileDTO> findSalesTargetGroupProductsBySalesTargetGroupPid(String salesTargetGroupPid) {
		log.debug("Request to get all Products");
		List<ProductProfile> productList = salesTargetGroupProductRepository
				.findProductsBySalesTargetGroupPid(salesTargetGroupPid);
		List<ProductProfileDTO> result = productMapper.productProfilesToProductProfileDTOs(productList);
		return result;
	}

	@Override
	public void save(String salesTargetGroupPid, String assignedProducts) {

		log.debug("Request to save User Sales Target Group Product");
		SalesTargetGroup salesTargetGroup = salesTargetGroupRepository.findOneByPid(salesTargetGroupPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] products = assignedProducts.split(",");
		List<SalesTargetGroupProduct> userProducts = new ArrayList<>();
		for (String activityPid : products) {
			ProductProfile product = productRepository.findOneByPid(activityPid).get();
			userProducts.add(new SalesTargetGroupProduct(product, salesTargetGroup, company));
		}
		salesTargetGroupProductRepository.deleteBySalesTargetGroupPid(salesTargetGroupPid);
		salesTargetGroupProductRepository.save(userProducts);
	}
}
