package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.SalesTargetBlock;
import com.orderfleet.webapp.domain.SalesTargetBlockSalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.SalesTargetBlockRepository;
import com.orderfleet.webapp.repository.SalesTargetBlockSalesTargetGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SalesTargetBlockSalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupMapper;

/**
 * Service Implementation for managing SalesTargetBlockSalesTargetGroup.
 *
 * @author Sarath
 * @since Feb 22, 2017
 */

@Transactional
@Service
public class SalesTargetBlockSalesTargetGroupServiceImpl implements SalesTargetBlockSalesTargetGroupService {

	private final Logger log = LoggerFactory.getLogger(SalesTargetBlockSalesTargetGroupServiceImpl.class);

	@Inject
	private SalesTargetBlockSalesTargetGroupRepository salesTargetBlockSalesTargetGroupRepository;

	@Inject
	private SalesTargetGroupMapper salesTargetGroupMapper;

	@Inject
	private SalesTargetBlockRepository salesTargetBlockRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	/**
	 * Save a salesTargetBlock.
	 * 
	 * @param salesTargetBlockDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public void save(String salesTargetBlockPid, String assignedSalesTargetgroups) {
		log.debug("Request to save SalesTargetBlock under SalesTargetGroup by pid : {}", salesTargetBlockPid);

		List<SalesTargetBlockSalesTargetGroup> stbstgs = new ArrayList<>();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<SalesTargetBlock> salesTargetBlock = salesTargetBlockRepository.findOneByPid(salesTargetBlockPid);

		SalesTargetBlock stBlock = new SalesTargetBlock();
		stBlock = salesTargetBlock.get();
		String[] salesTargetGroups = assignedSalesTargetgroups.split(",");
		for (String stg : salesTargetGroups) {
			SalesTargetGroup group = salesTargetGroupRepository.findOneByPid(stg).get();
			SalesTargetBlockSalesTargetGroup salesTargetBlockSalesTargetGroup = new SalesTargetBlockSalesTargetGroup(
					stBlock, group, company);
			stbstgs.add(salesTargetBlockSalesTargetGroup);
		}
		salesTargetBlockSalesTargetGroupRepository.deleteBySalesTargetBlockPid(salesTargetBlockPid);
		salesTargetBlockSalesTargetGroupRepository.save(stbstgs);
	}

	/**
	 * Get all the salesTargetGroups.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetGroupDTO> findSalesTargetGroupsBySalesTargetBlockPid(String salesTargetBlockPid) {
		List<SalesTargetGroup> salesTargetGroupList = salesTargetBlockSalesTargetGroupRepository
				.findSalesTargetGroupsBySalesTargetBlockPid(salesTargetBlockPid);
		List<SalesTargetGroupDTO> result = salesTargetGroupMapper
				.salesTargetGroupsToSalesTargetGroupDTOs(salesTargetGroupList);
		return result;
	}

	/**
	 * Get all the salesTargetGroups.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetBlockSalesTargetGroup> findAllByCompanyId() {
		List<SalesTargetBlockSalesTargetGroup> salesTargetBlockSalesTargetGroups = salesTargetBlockSalesTargetGroupRepository
				.findAllByCompanyId();
		return salesTargetBlockSalesTargetGroups;
	}

}
