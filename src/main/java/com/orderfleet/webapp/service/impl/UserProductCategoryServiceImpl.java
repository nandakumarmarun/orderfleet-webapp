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
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserProductCategory;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.UserProductCategoryRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserProductCategoryService;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductCategoryMapper;

/**
 * Service Implementation for managing UserProductCategory.
 * 
 * @author Sarath
 * @since July 8 2016
 */
@Service
@Transactional
public class UserProductCategoryServiceImpl implements UserProductCategoryService {

	private final Logger log = LoggerFactory.getLogger(UserProductCategoryServiceImpl.class);

	@Inject
	private UserProductCategoryRepository userProductCategoryRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private ProductCategoryMapper productCategoryMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String userPid, String assignedProductCategory) {

		log.debug("Request to save User ProductCategory");

		User user = userRepository.findOneByPid(userPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] ProductCategorys = assignedProductCategory.split(",");

		List<UserProductCategory> userProductCategories = new ArrayList<>();

		for (String ProductCategoryPid : ProductCategorys) {
			ProductCategory productCategory = productCategoryRepository.findOneByPid(ProductCategoryPid).get();
			userProductCategories.add(new UserProductCategory(user, productCategory, company));
		}
		userProductCategoryRepository.deleteByUserPid(userPid);
		userProductCategoryRepository.save(userProductCategories);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductCategoryDTO> findProductCategoriesByUserIsCurrentUser() {
		List<ProductCategory> productCategories = userProductCategoryRepository
				.findProductCategorysByUserIsCurrentUser();
		return productCategoryMapper
				.productCategoriesToProductCategoryDTOs(productCategories);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductCategoryDTO> findProductCategoriesByUserPid(String userPid) {
		log.debug("Request to get all Activities");
		List<ProductCategory> activityList = userProductCategoryRepository.findProductCategorysByUserPid(userPid);
		return productCategoryMapper.productCategoriesToProductCategoryDTOs(activityList);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all productCategoryDTO from ProductCategory by status and
	 *        user.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public List<ProductCategoryDTO> findProductCategoriesByUserIsCurrentUserAndProductCategoryActivated(
			boolean active) {
		List<ProductCategory> productCategories = userProductCategoryRepository
				.findProductCategorysByUserIsCurrentUserAndProductCategoryActivated(active);
		return productCategoryMapper
				.productCategoriesToProductCategoryDTOs(productCategories);
	}

	@Override
	public List<ProductCategoryDTO> findByUserIsCurrentUserAndProductCategoryActivatedAndLastModifiedDate(
			boolean status, LocalDateTime lastModifiedDate) {
		List<ProductCategory> productCategories = userProductCategoryRepository
				.findByUserIsCurrentUserAndProductCategoryActivatedAndLastModifiedDate(status, lastModifiedDate);
		return productCategoryMapper
				.productCategoriesToProductCategoryDTOs(productCategories);
	}

	@Override
	public void copyProductCategories(String fromUserPid, List<String> toUserPids) {
		// delete association first
		userProductCategoryRepository.deleteByUserPidIn(toUserPids);
		List<UserProductCategory> userProductCategories = userProductCategoryRepository.findByUserPid(fromUserPid);
		if (userProductCategories != null && !userProductCategories.isEmpty()) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for (User user : users) {
				List<UserProductCategory> newUserProductCategories = userProductCategories.stream()
						.map(upg -> new UserProductCategory(user, upg.getProductCategory(), upg.getCompany()))
						.collect(Collectors.toList());
				userProductCategoryRepository.save(newUserProductCategories);
			}
		}
	}
}
