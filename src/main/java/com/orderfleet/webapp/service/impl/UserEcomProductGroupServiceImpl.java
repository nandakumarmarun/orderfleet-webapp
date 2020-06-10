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
import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserEcomProductGroup;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EcomProductGroupRepository;
import com.orderfleet.webapp.repository.UserEcomProductGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserEcomProductGroupService;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.EcomProductGroupMapper;

/**
 * Service Implementation for managing UserProductGroup.
 * 
 * @author Anish
 * @since June 9 2020
 */

@Service
@Transactional
public class UserEcomProductGroupServiceImpl implements UserEcomProductGroupService {

	private final Logger log = LoggerFactory.getLogger(UserEcomProductGroupServiceImpl.class);

	@Inject
	private UserEcomProductGroupRepository userProductGroupRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private EcomProductGroupRepository productGroupRepository;

	@Inject
	private EcomProductGroupMapper productGroupMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String userPid, String assignedProductGroup) {

		log.debug("Request to save User ProductGroup");

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		User user = userRepository.findOneByPid(userPid).get();
		String[] ProductGroups = assignedProductGroup.split(",");

		List<UserEcomProductGroup> userProductGroups = new ArrayList<>();

		for (String ProductGroupPid : ProductGroups) {
			EcomProductGroup productGroup = productGroupRepository.findOneByPid(ProductGroupPid).get();
			userProductGroups.add(new UserEcomProductGroup(user, productGroup, company));
		}
		userProductGroupRepository.deleteByUserPid(userPid);
		userProductGroupRepository.save(userProductGroups);

	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductGroupDTO> findProductGroupsByUserIsCurrentUser() {
		List<EcomProductGroup> productGroups = userProductGroupRepository.findProductGroupsByUserIsCurrentUser();
		return productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductGroupDTO> findProductGroupsByUserPid(String userPid) {
		log.debug("Request to get all ProductGroups");
		List<EcomProductGroup> activityList = userProductGroupRepository.findProductGroupsByUserPid(userPid);
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
	public List<EcomProductGroupDTO> findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(boolean active) {
		log.debug("request to get all activated productGroups ");
		List<EcomProductGroup> productGroups = userProductGroupRepository
				.findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(active);
		return productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
	}

	/**
	 * get all product groups activated and lastModifiedDate
	 */
	@Override
	public List<EcomProductGroupDTO> findProductGroupsByUserIsCurrentUserAndProductGroupActivatedAndProductGroupLastModifiedDate(
			boolean active, LocalDateTime lastModifiedDate) {
		log.debug("request to get all activated productGroups ");
		List<EcomProductGroup> productGroups = userProductGroupRepository
				.findProductGroupsByUserIsCurrentUserAndProductGroupActivatedAndProductGroupLastModifiedDate(active,
						lastModifiedDate);
		return productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
	}

	@Override
	public void copyProductGroups(String fromUserPid, List<String> toUserPids) {
		// delete association first
		userProductGroupRepository.deleteByUserPidIn(toUserPids);
		List<UserEcomProductGroup> userProductGroups = userProductGroupRepository.findByUserPid(fromUserPid);
		if (userProductGroups != null && !userProductGroups.isEmpty()) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for (User user : users) {
				List<UserEcomProductGroup> newUserProductGroups = userProductGroups.stream()
						.map(upg -> new UserEcomProductGroup(user,upg.getProductGroup(), upg.getCompany()))
						.collect(Collectors.toList());
				userProductGroupRepository.save(newUserProductGroups);
			}
		}
	}

}
