package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.MobileMenuItemGroup;
import com.orderfleet.webapp.domain.MobileMenuItemGroupMenuItem;
import com.orderfleet.webapp.repository.MobileMenuItemGroupMenuItemRepository;
import com.orderfleet.webapp.repository.MobileMenuItemGroupRepository;
import com.orderfleet.webapp.repository.MobileMenuItemRepository;
import com.orderfleet.webapp.service.MobileMenuItemGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemDTO;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemGroupDTO;

/**
 * Service Implementation for managing MobileMenuItemGroup.
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 */
@Service
@Transactional
public class MobileMenuItemGroupServiceImpl implements MobileMenuItemGroupService {

	private final Logger log = LoggerFactory.getLogger(MobileMenuItemGroupServiceImpl.class);

	@Inject
	private MobileMenuItemGroupRepository mobileMenuItemGroupRepository;

	@Inject
	private MobileMenuItemRepository mobileMenuItemRepository;

	@Inject
	private MobileMenuItemGroupMenuItemRepository mobileMenuItemGroupMenuItemRepository;

	/**
	 * Save a mobileMenuItemGroup.
	 * 
	 * @param mobileMenuItemGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public MobileMenuItemGroupDTO save(MobileMenuItemGroupDTO mobileMenuItemGroupDTO) {
		log.debug("Request to save MobileMenuItemGroup : {}", mobileMenuItemGroupDTO);

		MobileMenuItemGroup mobileMenuItemGroup = new MobileMenuItemGroup();
		// set pid
		mobileMenuItemGroup.setPid(MobileMenuItemGroupService.PID_PREFIX + RandomUtil.generatePid());
		mobileMenuItemGroup.setName(mobileMenuItemGroupDTO.getName());
		mobileMenuItemGroup.setAlias(mobileMenuItemGroupDTO.getAlias());
		mobileMenuItemGroup.setDescription(mobileMenuItemGroupDTO.getDescription());
		mobileMenuItemGroup = mobileMenuItemGroupRepository.save(mobileMenuItemGroup);
		MobileMenuItemGroupDTO result = new MobileMenuItemGroupDTO(mobileMenuItemGroup);
		return result;
	}

	@Override
	public void save(String groupPid, List<MobileMenuItemDTO> menuItems) {
		MobileMenuItemGroup mobileMenuItemGroup = mobileMenuItemGroupRepository.findOneByPid(groupPid).get();
		List<MobileMenuItemGroupMenuItem> groupMenuItems = new ArrayList<>();
		for (MobileMenuItemDTO mobileMenuItem : menuItems) {
			MobileMenuItemGroupMenuItem mobileMenuItemGroupMenuItem = new MobileMenuItemGroupMenuItem();
			mobileMenuItemGroupMenuItem.setMobileMenuItemGroup(mobileMenuItemGroup);
			mobileMenuItemGroupMenuItem.setMobileMenuItem(mobileMenuItemRepository.findOne(mobileMenuItem.getId()));
			mobileMenuItemGroupMenuItem.setLabel(mobileMenuItem.getLabel());
			groupMenuItems.add(mobileMenuItemGroupMenuItem);
		}
		mobileMenuItemGroupMenuItemRepository.save(groupMenuItems);
	}

	/**
	 * Update a mobileMenuItemGroup.
	 * 
	 * @param mobileMenuItemGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public MobileMenuItemGroupDTO update(MobileMenuItemGroupDTO mobileMenuItemGroupDTO) {
		log.debug("Request to Update MobileMenuItemGroup : {}", mobileMenuItemGroupDTO);

		return mobileMenuItemGroupRepository.findOneByPid(mobileMenuItemGroupDTO.getPid()).map(mobileMenuItemGroup -> {
			mobileMenuItemGroup.setName(mobileMenuItemGroupDTO.getName());
			mobileMenuItemGroup.setAlias(mobileMenuItemGroupDTO.getAlias());
			mobileMenuItemGroup.setDescription(mobileMenuItemGroupDTO.getDescription());
			mobileMenuItemGroup = mobileMenuItemGroupRepository.save(mobileMenuItemGroup);
			MobileMenuItemGroupDTO result = new MobileMenuItemGroupDTO(mobileMenuItemGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the mobileMenuItemGroups.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<MobileMenuItemGroupDTO> findAll() {
		log.debug("Request to get all MobileMenuItemGroups");
		List<MobileMenuItemGroup> mobileMenuItemGroupList = mobileMenuItemGroupRepository.findAll();
		List<MobileMenuItemGroupDTO> result = mobileMenuItemGroupList.stream().map(MobileMenuItemGroupDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one mobileMenuItemGroup by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<MobileMenuItemGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get MobileMenuItemGroup by pid : {}", pid);
		return mobileMenuItemGroupRepository.findOneByPid(pid).map(mobileMenuItemGroup -> {
			MobileMenuItemGroupDTO mobileMenuItemGroupDTO = new MobileMenuItemGroupDTO(mobileMenuItemGroup);
			return mobileMenuItemGroupDTO;
		});
	}

	/**
	 * Get one mobileMenuItemGroup by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<MobileMenuItemGroupDTO> findByName(String name) {
		log.debug("Request to get MobileMenuItemGroup by name : {}", name);
		return mobileMenuItemGroupRepository.findByNameIgnoreCase(name).map(mobileMenuItemGroup -> {
			MobileMenuItemGroupDTO mobileMenuItemGroupDTO = new MobileMenuItemGroupDTO(mobileMenuItemGroup);
			return mobileMenuItemGroupDTO;
		});
	}

	/**
	 * Delete the mobileMenuItemGroup by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete MobileMenuItemGroup : {}", pid);
		mobileMenuItemGroupRepository.findOneByPid(pid).ifPresent(mobileMenuItemGroup -> {
			mobileMenuItemGroupRepository.delete(mobileMenuItemGroup.getId());
		});
	}

}
