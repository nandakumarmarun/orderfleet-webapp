package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.domain.PriceTrendProductGroup;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PriceTrendProductGroupRepository;
import com.orderfleet.webapp.repository.PriceTrendProductRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PriceTrendProductGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.PriceTrendProductGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.PriceTrendProductGroupMapper;

/**
 * Service Implementation for managing PriceTrendProductGroup.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Service
@Transactional
public class PriceTrendProductGroupServiceImpl implements PriceTrendProductGroupService {

	private final Logger log = LoggerFactory.getLogger(PriceTrendProductGroupServiceImpl.class);

	@Inject
	private PriceTrendProductGroupRepository priceTrendProductGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private PriceTrendProductGroupMapper priceTrendProductGroupMapper;

	@Inject
	private PriceTrendProductRepository priceTrendProductRepository;

	/**
	 * Save a priceTrendProductGroup.
	 * 
	 * @param priceTrendProductGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public PriceTrendProductGroupDTO save(PriceTrendProductGroupDTO priceTrendProductGroupDTO) {
		log.debug("Request to save PriceTrendProductGroup : {}", priceTrendProductGroupDTO);
		// set pid
		priceTrendProductGroupDTO.setPid(PriceTrendProductGroupService.PID_PREFIX + RandomUtil.generatePid());
		PriceTrendProductGroup priceTrendProductGroup = priceTrendProductGroupMapper
				.priceTrendProductGroupDTOToPriceTrendProductGroup(priceTrendProductGroupDTO);
		// set company
		priceTrendProductGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		priceTrendProductGroup = priceTrendProductGroupRepository.save(priceTrendProductGroup);
		PriceTrendProductGroupDTO result = priceTrendProductGroupMapper
				.priceTrendProductGroupToPriceTrendProductGroupDTO(priceTrendProductGroup);
		return result;
	}

	@Override
	public void saveAssignedProducts(String pid, String assignedProducts) {
		PriceTrendProductGroup priceTrendProductGroup = priceTrendProductGroupRepository.findOneByPid(pid).get();
		String[] products = assignedProducts.split(",");
		List<PriceTrendProduct> listProducts = new ArrayList<PriceTrendProduct>();
		for (String productPid : products) {
			PriceTrendProduct priceTrendProduct = priceTrendProductRepository.findOneByPid(productPid).get();
			listProducts.add(priceTrendProduct);
		}
		priceTrendProductGroup.setPriceTrendProducts(listProducts);
		priceTrendProductGroupRepository.save(priceTrendProductGroup);
	}

	/**
	 * Update a priceTrendProductGroup.
	 * 
	 * @param priceTrendProductGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public PriceTrendProductGroupDTO update(PriceTrendProductGroupDTO priceTrendProductGroupDTO) {
		log.debug("Request to Update PriceTrendProductGroup : {}", priceTrendProductGroupDTO);
		return priceTrendProductGroupRepository.findOneByPid(priceTrendProductGroupDTO.getPid())
				.map(priceTrendProductGroup -> {
					priceTrendProductGroup.setName(priceTrendProductGroupDTO.getName());
					priceTrendProductGroup.setAlias(priceTrendProductGroupDTO.getAlias());
					priceTrendProductGroup.setDescription(priceTrendProductGroupDTO.getDescription());
					priceTrendProductGroup = priceTrendProductGroupRepository.save(priceTrendProductGroup);
					PriceTrendProductGroupDTO result = priceTrendProductGroupMapper
							.priceTrendProductGroupToPriceTrendProductGroupDTO(priceTrendProductGroup);
					return result;
				}).orElse(null);
	}

	/**
	 * Get all the priceTrendProductGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<PriceTrendProductGroup> findAll(Pageable pageable) {
		log.debug("Request to get all PriceTrendProductGroups");
		Page<PriceTrendProductGroup> result = priceTrendProductGroupRepository.findAll(pageable);
		return result;
	}

	@Override
	public List<PriceTrendProductGroupDTO> findAllByCompany() {
		log.debug("Request to get all PriceTrendProductGroups");
		List<PriceTrendProductGroup> priceTrendProductGroupList = priceTrendProductGroupRepository.findAllByCompanyId();
		List<PriceTrendProductGroupDTO> result = priceTrendProductGroupMapper
				.priceTrendProductGroupsToPriceTrendProductGroupDTOs(priceTrendProductGroupList);
		return result;
	}

	/**
	 * Get all the priceTrendProductGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<PriceTrendProductGroupDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all PriceTrendProductGroups");
		Page<PriceTrendProductGroup> priceTrendProductGroups = priceTrendProductGroupRepository
				.findAllByCompanyIdOrderByPriceTrendProductGroupName(pageable);
		Page<PriceTrendProductGroupDTO> result = new PageImpl<PriceTrendProductGroupDTO>(priceTrendProductGroupMapper
				.priceTrendProductGroupsToPriceTrendProductGroupDTOs(priceTrendProductGroups.getContent()), pageable,
				priceTrendProductGroups.getTotalElements());
		return result;
	}

	/**
	 * Get one priceTrendProductGroup by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public PriceTrendProductGroupDTO findOne(Long id) {
		log.debug("Request to get PriceTrendProductGroup : {}", id);
		PriceTrendProductGroup priceTrendProductGroup = priceTrendProductGroupRepository.findOne(id);
		PriceTrendProductGroupDTO priceTrendProductGroupDTO = priceTrendProductGroupMapper
				.priceTrendProductGroupToPriceTrendProductGroupDTO(priceTrendProductGroup);
		return priceTrendProductGroupDTO;
	}

	/**
	 * Get one priceTrendProductGroup by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PriceTrendProductGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get PriceTrendProductGroup by pid : {}", pid);
		return priceTrendProductGroupRepository.findOneByPid(pid).map(priceTrendProductGroup -> {
			PriceTrendProductGroupDTO priceTrendProductGroupDTO = priceTrendProductGroupMapper
					.priceTrendProductGroupToPriceTrendProductGroupDTO(priceTrendProductGroup);
			return priceTrendProductGroupDTO;
		});
	}

	/**
	 * Get one priceTrendProductGroup by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PriceTrendProductGroupDTO> findByName(String name) {
		log.debug("Request to get PriceTrendProductGroup by name : {}", name);
		return priceTrendProductGroupRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(priceTrendProductGroup -> {
					PriceTrendProductGroupDTO priceTrendProductGroupDTO = priceTrendProductGroupMapper
							.priceTrendProductGroupToPriceTrendProductGroupDTO(priceTrendProductGroup);
					return priceTrendProductGroupDTO;
				});
	}

	/**
	 * Delete the priceTrendProductGroup by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete PriceTrendProductGroup : {}", pid);
		priceTrendProductGroupRepository.findOneByPid(pid).ifPresent(priceTrendProductGroup -> {
			priceTrendProductGroupRepository.delete(priceTrendProductGroup.getId());
		});
	}

	/**
	 * Update the priceTrendProductGroup by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */

	@Override
	public PriceTrendProductGroupDTO updatePriceTrendProductGroupStatus(String pid, boolean active) {
		log.debug("Request to update PriceTrendProductGroup status");
		return priceTrendProductGroupRepository.findOneByPid(pid).map(priceTrendProductGroup -> {
			if(!active){
				List<PriceTrendProduct> listProducts = null;
				priceTrendProductGroup.setPriceTrendProducts(listProducts);
			}
			priceTrendProductGroup.setActivated(active);
			priceTrendProductGroup = priceTrendProductGroupRepository.save(priceTrendProductGroup);
			PriceTrendProductGroupDTO result = priceTrendProductGroupMapper
					.priceTrendProductGroupToPriceTrendProductGroupDTO(priceTrendProductGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all priceTrendProductGroupDTO from PriceTrendProductGroup by
	 *        status and company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	@Override
	public List<PriceTrendProductGroupDTO> findAllByCompanyIdAndPriceTrendProductGroupActivated(boolean active) {
		log.debug("Request to get Activated PriceTrendProductGroup ");
		List<PriceTrendProductGroup> priceTrendProductGroups = priceTrendProductGroupRepository
				.findAllByCompanyIdAndPriceTrendProductGroupActivatedOrDeactivated(active);
		List<PriceTrendProductGroupDTO> priceTrendProductGroupDTOs = priceTrendProductGroupMapper
				.priceTrendProductGroupsToPriceTrendProductGroupDTOs(priceTrendProductGroups);
		return priceTrendProductGroupDTOs;
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all active company
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public Page<PriceTrendProductGroupDTO> findAllByCompanyAndPriceTrendProductGroupActivatedOrderByName(
			Pageable pageable, boolean active) {
		Page<PriceTrendProductGroup> pagePriceTrendProductGroup = priceTrendProductGroupRepository
				.findAllByCompanyIdAndActivatedOrderByPriceTrendProductGroupName(pageable, active);
		Page<PriceTrendProductGroupDTO> pagePriceTrendProductGroupDTO = new PageImpl<PriceTrendProductGroupDTO>(
				priceTrendProductGroupMapper
						.priceTrendProductGroupsToPriceTrendProductGroupDTOs(pagePriceTrendProductGroup.getContent()),
				pageable, pagePriceTrendProductGroup.getTotalElements());
		return pagePriceTrendProductGroupDTO;
	}

	@Override
	public List<PriceTrendProductGroupDTO> findAllByCompanyIdAndPriceTrendProductGroupActivatedAndlastModifiedDate(boolean active,LocalDateTime lastModifiedDate) {
		log.debug("Request to get Activated PriceTrendProductGroup ");
		List<PriceTrendProductGroup> priceTrendProductGroups = priceTrendProductGroupRepository
				.findAllByCompanyIdAndPriceTrendProductGroupActivatedAndlastModifiedDate(active,lastModifiedDate);
		List<PriceTrendProductGroupDTO> priceTrendProductGroupDTOs = priceTrendProductGroupMapper
				.priceTrendProductGroupsToPriceTrendProductGroupDTOs(priceTrendProductGroups);
		return priceTrendProductGroupDTOs;
	}
}
