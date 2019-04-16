package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.MobileMenuItemGroup;
import com.orderfleet.webapp.domain.MobileMenuItemGroupMenuItem;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserMobileMenuItemGroup;
import com.orderfleet.webapp.repository.MobileMenuItemGroupMenuItemRepository;
import com.orderfleet.webapp.repository.MobileMenuItemGroupRepository;
import com.orderfleet.webapp.repository.UserMobileMenuItemGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserMobileMenuItemGroupService;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemDTO;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemGroupDTO;

/**
 * Service Implementation for managing UserMobileMenuItemGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Service
@Transactional
public class UserMobileMenuItemGroupServiceImpl implements UserMobileMenuItemGroupService {

	private final Logger log = LoggerFactory.getLogger(UserMobileMenuItemGroupServiceImpl.class);

	@Inject
	private UserMobileMenuItemGroupRepository userMobileMenuItemGroupRepository;

	@Inject
	private MobileMenuItemGroupMenuItemRepository mobileMenuItemGroupMenuItemRepository;

	@Inject
	private MobileMenuItemGroupRepository mobileMenuItemGroupRepository;

	@Inject
	private UserRepository userRepository;

	@Override
	public void save(String userPid, String menuGroupPid) {
		UserMobileMenuItemGroup userMobileMenuItemGroup = userMobileMenuItemGroupRepository.findByUserPid(userPid);
		MobileMenuItemGroup mobileMenuItemGroup = mobileMenuItemGroupRepository.findOneByPid(menuGroupPid).get();
		if (userMobileMenuItemGroup != null) {
			userMobileMenuItemGroup.setMobileMenuItemGroup(mobileMenuItemGroup);
		} else {
			userMobileMenuItemGroup = new UserMobileMenuItemGroup();
			userMobileMenuItemGroup.setMobileMenuItemGroup(mobileMenuItemGroup);
			userMobileMenuItemGroup.setUser(userRepository.findOneByPid(userPid).get());
		}
		userMobileMenuItemGroupRepository.save(userMobileMenuItemGroup);
	}

	@Override
	public MobileMenuItemGroupDTO findUserMobileMenuItemGroupByUserPid(String userPid) {
		UserMobileMenuItemGroup userMobileMenuItemGroup = userMobileMenuItemGroupRepository.findByUserPid(userPid);
		if (userMobileMenuItemGroup != null) {
			return new MobileMenuItemGroupDTO(userMobileMenuItemGroup.getMobileMenuItemGroup());
		}
		return null;
	}

	@Override
	public List<MobileMenuItemDTO> findCurrentUserMenuItems() {
		log.info("find Current User Menu Items");
		List<MobileMenuItemDTO> mobileMenuItems = new ArrayList<>();
		UserMobileMenuItemGroup userMobileMenuItemGroup = userMobileMenuItemGroupRepository
				.findByUserLogin(SecurityUtils.getCurrentUserLogin());
		if (userMobileMenuItemGroup != null) {
			List<MobileMenuItemGroupMenuItem> mobileMenuItemGroupMenuItems = mobileMenuItemGroupMenuItemRepository
					.findAllByMobileMenuItemGroupPid(userMobileMenuItemGroup.getMobileMenuItemGroup().getPid());
			mobileMenuItems = mobileMenuItemGroupMenuItems.stream()
					.map(mmgmi -> new MobileMenuItemDTO(mmgmi.getMobileMenuItem().getName(), mmgmi.getLabel(),mmgmi.getLastModifiedDate()))
					.collect(Collectors.toList());
		}
		return mobileMenuItems;
	}

	@Override
	public void copyMobileMenuItemGroup(String fromUserPid, List<String> toUserPids) {
		//delete association first
		userMobileMenuItemGroupRepository.deleteByUserPidIn(toUserPids);
		UserMobileMenuItemGroup userMobileMenuItemGroup = userMobileMenuItemGroupRepository.findByUserPid(fromUserPid);
		if(userMobileMenuItemGroup != null) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for(User user : users){
				UserMobileMenuItemGroup newUserMobileMenuItemGroup = new UserMobileMenuItemGroup();
				newUserMobileMenuItemGroup.setMobileMenuItemGroup(userMobileMenuItemGroup.getMobileMenuItemGroup());
				newUserMobileMenuItemGroup.setUser(user);
				userMobileMenuItemGroupRepository.save(newUserMobileMenuItemGroup);
			}
		}
	}

}
