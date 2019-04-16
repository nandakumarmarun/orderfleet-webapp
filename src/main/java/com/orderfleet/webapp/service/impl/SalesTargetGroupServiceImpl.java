package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SalesTargetGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.SalesTargetGroupMapper;

/**
 * Service Implementation for managing SalesTargetGroup.
 * 
 * @author sarath
 * @since July 27, 2016
 */
@Service
@Transactional
public class SalesTargetGroupServiceImpl implements SalesTargetGroupService {

	private final Logger log = LoggerFactory.getLogger(SalesTargetGroupServiceImpl.class);

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private SalesTargetGroupMapper salesTargetGroupMapper;

	/**
	 * Save a salesTargetGroup.
	 * 
	 * @param salesTargetGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public SalesTargetGroupDTO save(SalesTargetGroupDTO salesTargetGroupDTO) {
		log.debug("Request to save SalesTargetGroup : {}", salesTargetGroupDTO);
		salesTargetGroupDTO.setPid(SalesTargetGroupService.PID_PREFIX + RandomUtil.generatePid()); // set
		// pid
		SalesTargetGroup salesTargetGroup = salesTargetGroupMapper
				.salesTargetGroupDTOToSalesTargetGroup(salesTargetGroupDTO);
		salesTargetGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		salesTargetGroup = salesTargetGroupRepository.save(salesTargetGroup);
		SalesTargetGroupDTO result = salesTargetGroupMapper.salesTargetGroupToSalesTargetGroupDTO(salesTargetGroup);
		return result;
	}

	/**
	 * Update a salesTargetGroup.
	 * 
	 * @param salesTargetGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public SalesTargetGroupDTO update(SalesTargetGroupDTO salesTargetGroupDTO) {
		log.debug("Request to Update SalesTargetGroup : {}", salesTargetGroupDTO);

		return salesTargetGroupRepository.findOneByPid(salesTargetGroupDTO.getPid()).map(salesTargetGroup -> {
			salesTargetGroup.setName(salesTargetGroupDTO.getName());
			salesTargetGroup.setAlias(salesTargetGroupDTO.getAlias());
			salesTargetGroup.setDescription(salesTargetGroupDTO.getDescription());
			salesTargetGroup.setTargetUnit(salesTargetGroupDTO.getTargetUnit());
			salesTargetGroup.setTargetSettingType(salesTargetGroupDTO.getTargetSettingType());

			salesTargetGroup = salesTargetGroupRepository.save(salesTargetGroup);
			SalesTargetGroupDTO result = salesTargetGroupMapper.salesTargetGroupToSalesTargetGroupDTO(salesTargetGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the salesTargetGroups.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetGroupDTO> findAllByCompany() {
		log.debug("Request to get all SalesTargetGroups");
		List<SalesTargetGroup> salesTargetGroupList = salesTargetGroupRepository.findAllByCompanyId();
		List<SalesTargetGroupDTO> result = salesTargetGroupMapper
				.salesTargetGroupsToSalesTargetGroupDTOs(salesTargetGroupList);
		return result;
	}

	/**
	 * Get all the salesTargetGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<SalesTargetGroupDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all SalesTargetGroups");
		Page<SalesTargetGroup> salesTargetGroups = salesTargetGroupRepository.findAllByCompanyId(pageable);
		Page<SalesTargetGroupDTO> result = new PageImpl<SalesTargetGroupDTO>(
				salesTargetGroupMapper.salesTargetGroupsToSalesTargetGroupDTOs(salesTargetGroups.getContent()),
				pageable, salesTargetGroups.getTotalElements());
		return result;
	}

	/**
	 * Get one salesTargetGroup by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public SalesTargetGroupDTO findOne(Long id) {
		log.debug("Request to get SalesTargetGroup : {}", id);
		SalesTargetGroup salesTargetGroup = salesTargetGroupRepository.findOne(id);
		SalesTargetGroupDTO salesTargetGroupDTO = salesTargetGroupMapper
				.salesTargetGroupToSalesTargetGroupDTO(salesTargetGroup);
		return salesTargetGroupDTO;
	}

	/**
	 * Get one salesTargetGroup by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<SalesTargetGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get SalesTargetGroup by pid : {}", pid);
		return salesTargetGroupRepository.findOneByPid(pid).map(salesTargetGroup -> {
			SalesTargetGroupDTO salesTargetGroupDTO = salesTargetGroupMapper
					.salesTargetGroupToSalesTargetGroupDTO(salesTargetGroup);
			return salesTargetGroupDTO;
		});
	}

	/**
	 * Get one salesTargetGroup by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<SalesTargetGroupDTO> findByName(String name) {
		log.debug("Request to get SalesTargetGroup by name : {}", name);
		return salesTargetGroupRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(salesTargetGroup -> {
					SalesTargetGroupDTO salesTargetGroupDTO = salesTargetGroupMapper
							.salesTargetGroupToSalesTargetGroupDTO(salesTargetGroup);
					return salesTargetGroupDTO;
				});
	}

	/**
	 * Delete the salesTargetGroup by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete SalesTargetGroup : {}", pid);
		salesTargetGroupRepository.findOneByPid(pid).ifPresent(salesTargetGroup -> {
			salesTargetGroupRepository.delete(salesTargetGroup.getId());
		});
	}
	
	/**
	 * Get all the salesTargetGroups.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<SalesTargetGroupDTO> findAllByCompanyAndTargetSettingType(BestPerformanceType targetSettingType) {
		log.debug("Request to get all SalesTargetGroups");
		List<SalesTargetGroup> salesTargetGroupList = salesTargetGroupRepository.findAllByCompanyAndTargetSettingType(targetSettingType);
		List<SalesTargetGroupDTO> result = salesTargetGroupMapper
				.salesTargetGroupsToSalesTargetGroupDTOs(salesTargetGroupList);
		return result;
	}
}