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
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserProductGroup;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.UserProductGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserProductGroupService;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductGroupMapper;

/**
 * Service Implementation for managing UserProductGroup.
 * 
 * @author Sarath
 * @since July 9 2016
 */

@Service
@Transactional
public class UserProductGroupServiceImpl implements UserProductGroupService {

	private final Logger log = LoggerFactory.getLogger(UserProductGroupServiceImpl.class);

	@Inject
	private UserProductGroupRepository userProductGroupRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupMapper productGroupMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String userPid, String assignedProductGroup) {

		log.debug("Request to save User ProductGroup");

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		User user = userRepository.findOneByPid(userPid).get();
		String[] ProductGroups = assignedProductGroup.split(",");

		List<UserProductGroup> userProductGroups = new ArrayList<>();

		for (String ProductGroupPid : ProductGroups) {
			ProductGroup productGroup = productGroupRepository.findOneByPid(ProductGroupPid).get();
			userProductGroups.add(new UserProductGroup(user, productGroup, company));
		}
		userProductGroupRepository.deleteByUserPid(userPid);
		userProductGroupRepository.save(userProductGroups);

	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductGroupDTO> findProductGroupsByUserIsCurrentUser() {
		List<ProductGroup> productGroups = userProductGroupRepository.findProductGroupsByUserIsCurrentUser();
		return productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductGroupDTO> findProductGroupsByUserPid(String userPid) {
		log.debug("Request to get all ProductGroups");
		List<ProductGroup> activityList = userProductGroupRepository.findProductGroupsByUserPid(userPid);
		return productGroupMapper.productGroupsToProductGroupDTOs(activityList);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all productGroupDTO from ProductGroup by status and user.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public List<ProductGroupDTO> findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(boolean active) {
		log.debug("request to get all activated productGroups ");
		List<ProductGroup> productGroups = userProductGroupRepository
				.findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(active);
		return productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
	}

	/**
	 * get all product groups activated and lastModifiedDate
	 */
	@Override
	public List<ProductGroupDTO> findProductGroupsByUserIsCurrentUserAndProductGroupActivatedAndProductGroupLastModifiedDate(
			boolean active, LocalDateTime lastModifiedDate) {
		log.debug("request to get all activated productGroups ");
		List<ProductGroup> productGroups = userProductGroupRepository
				.findProductGroupsByUserIsCurrentUserAndProductGroupActivatedAndProductGroupLastModifiedDate(active,
						lastModifiedDate);
		return productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
	}

	@Override
	public void copyProductGroups(String fromUserPid, List<String> toUserPids) {
		// delete association first
		userProductGroupRepository.deleteByUserPidIn(toUserPids);
		List<UserProductGroup> userProductGroups = userProductGroupRepository.findByUserPid(fromUserPid);
		if (userProductGroups != null && !userProductGroups.isEmpty()) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for (User user : users) {
				List<UserProductGroup> newUserProductGroups = userProductGroups.stream()
						.map(upg -> new UserProductGroup(user,upg.getProductGroup(), upg.getCompany()))
						.collect(Collectors.toList());
				userProductGroupRepository.save(newUserProductGroups);
			}
		}
	}

}
