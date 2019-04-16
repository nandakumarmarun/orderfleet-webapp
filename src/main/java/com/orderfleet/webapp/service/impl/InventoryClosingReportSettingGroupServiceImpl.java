package com.orderfleet.webapp.service.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.InventoryClosingReportSettingGroup;
import com.orderfleet.webapp.repository.InventoryClosingReportSettingGroupRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.InventoryClosingReportSettingGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingReportSettingGroupDTO;


@Service
@Transactional
public class InventoryClosingReportSettingGroupServiceImpl implements InventoryClosingReportSettingGroupService{
	private final Logger log = LoggerFactory.getLogger(InventoryClosingReportSettingGroupServiceImpl.class);

	@Inject
	private InventoryClosingReportSettingGroupRepository inventoryClosingReportSettingGroupRepository;

	@Inject
	private CompanyRepository companyRepository;
	
	@Override
	public InventoryClosingReportSettingGroupDTO save(
			InventoryClosingReportSettingGroupDTO inventoryClosingReportSettingGroupDTO) {
		log.debug("Request to save InventoryClosingReportSettingGroup : {}", inventoryClosingReportSettingGroupDTO);
		InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup=new InventoryClosingReportSettingGroup();
		inventoryClosingReportSettingGroup.setPid(InventoryClosingReportSettingGroupService.PID_PREFIX + RandomUtil.generatePid()); // set
		inventoryClosingReportSettingGroup.setName(inventoryClosingReportSettingGroupDTO.getName());
		inventoryClosingReportSettingGroup.setAlias(inventoryClosingReportSettingGroupDTO.getAlias());
		inventoryClosingReportSettingGroup.setFlow(inventoryClosingReportSettingGroupDTO.getFlow());
		inventoryClosingReportSettingGroup.setSortOrder(inventoryClosingReportSettingGroupDTO.getSortOrder());
		inventoryClosingReportSettingGroup.setActivated(true);
		inventoryClosingReportSettingGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		inventoryClosingReportSettingGroup = inventoryClosingReportSettingGroupRepository.save(inventoryClosingReportSettingGroup);
		InventoryClosingReportSettingGroupDTO result=new InventoryClosingReportSettingGroupDTO(inventoryClosingReportSettingGroup);
		return result;
	}

	@Override
	public InventoryClosingReportSettingGroupDTO update(
			InventoryClosingReportSettingGroupDTO inventoryClosingReportSettingGroupDTO) {
		return inventoryClosingReportSettingGroupRepository.findOneByPid(inventoryClosingReportSettingGroupDTO.getPid()).map(inventoryClosingReportSettingGroup -> {
			inventoryClosingReportSettingGroup.setName(inventoryClosingReportSettingGroupDTO.getName());
			inventoryClosingReportSettingGroup.setAlias(inventoryClosingReportSettingGroupDTO.getAlias());
			inventoryClosingReportSettingGroup.setFlow(inventoryClosingReportSettingGroupDTO.getFlow());
			inventoryClosingReportSettingGroup.setSortOrder(inventoryClosingReportSettingGroupDTO.getSortOrder());
			inventoryClosingReportSettingGroup = inventoryClosingReportSettingGroupRepository.save(inventoryClosingReportSettingGroup);
			InventoryClosingReportSettingGroupDTO result=new InventoryClosingReportSettingGroupDTO(inventoryClosingReportSettingGroup);
			return result;
		}).orElse(null);
	}

	@Override
	public List<InventoryClosingReportSettingGroupDTO> findAllByCompany() {
		List<InventoryClosingReportSettingGroup> inventoryClosingReportSettingGroupList = inventoryClosingReportSettingGroupRepository.findAllByCompanyId(true);
		List<InventoryClosingReportSettingGroupDTO>inventoryClosingReportSettingGroupDTOs=new ArrayList<>();
		for (InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup : inventoryClosingReportSettingGroupList) {
			InventoryClosingReportSettingGroupDTO result = new InventoryClosingReportSettingGroupDTO(inventoryClosingReportSettingGroup);
			inventoryClosingReportSettingGroupDTOs.add(result);
		}
		return inventoryClosingReportSettingGroupDTOs;
	}

	@Override
	public InventoryClosingReportSettingGroupDTO findOne(Long id) {
		log.debug("Request to get InventoryClosingReportSettingGroup : {}", id);
		InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup = inventoryClosingReportSettingGroupRepository.findOne(id);
		InventoryClosingReportSettingGroupDTO result=new InventoryClosingReportSettingGroupDTO(inventoryClosingReportSettingGroup);
		return result;
	}

	@Override
	public Optional<InventoryClosingReportSettingGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get InventoryClosingReportSettingGroup by pid : {}", pid);
		return inventoryClosingReportSettingGroupRepository.findOneByPid(pid).map(inventoryClosingReportSettingGroup -> {
			InventoryClosingReportSettingGroupDTO result=new InventoryClosingReportSettingGroupDTO(inventoryClosingReportSettingGroup);
			return result;
		});
	}

	@Override
	public Optional<InventoryClosingReportSettingGroupDTO> findByName(String name) {
		log.debug("Request to get InventoryClosingReportSettingGroup by name : {}", name);
		return inventoryClosingReportSettingGroupRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(inventoryClosingReportSettingGroup -> {
					InventoryClosingReportSettingGroupDTO result=new InventoryClosingReportSettingGroupDTO(inventoryClosingReportSettingGroup);
					return result;
				});
	}

	@Override
	public void delete(String pid) {
		log.debug("Request to delete InventoryClosingReportSettingGroup : {}", pid);
		inventoryClosingReportSettingGroupRepository.findOneByPid(pid).ifPresent(inventoryClosingReportSettingGroup -> {
			inventoryClosingReportSettingGroupRepository.delete(inventoryClosingReportSettingGroup.getId());
		});
	}

	@Override
	public InventoryClosingReportSettingGroupDTO updateInventoryClosingReportSettingGroupStatus(String pid,
			boolean activate) {
		log.debug("Request to update inventoryClosingReportSettingGroup status: {}");
		return inventoryClosingReportSettingGroupRepository.findOneByPid(pid).map(inventoryClosingReportSettingGroup -> {
			inventoryClosingReportSettingGroup.setActivated(activate);
			inventoryClosingReportSettingGroup = inventoryClosingReportSettingGroupRepository.save(inventoryClosingReportSettingGroup);
			InventoryClosingReportSettingGroupDTO result=new InventoryClosingReportSettingGroupDTO(inventoryClosingReportSettingGroup);
			return result;
		}).orElse(null);
	}

	@Override
	public List<InventoryClosingReportSettingGroupDTO> findAllByCompanyAndDeactivatedInventoryClosingReportSettingGroup(
			boolean deactive) {
		log.debug("Request to get Deactivated InventoryClosingReportSettingGroup ");
		List<InventoryClosingReportSettingGroup> inventoryClosingReportSettingGroups = inventoryClosingReportSettingGroupRepository.findAllByCompanyAndDeactivatedInventoryClosingReportSettingGroup(deactive);
		List<InventoryClosingReportSettingGroupDTO>inventoryClosingReportSettingGroupDTOs=new ArrayList<>();
		for (InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup : inventoryClosingReportSettingGroups) {
			InventoryClosingReportSettingGroupDTO result=new InventoryClosingReportSettingGroupDTO(inventoryClosingReportSettingGroup);
			inventoryClosingReportSettingGroupDTOs.add(result);
		}
		return inventoryClosingReportSettingGroupDTOs;
	}


}
