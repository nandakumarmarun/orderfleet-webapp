package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ProductGroupInfoSection;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ProductGroupInfoSectionRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupInfoSectionService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.ecom.dto.ProductGroupInfoSectionDTO;

/**
 * Service Implementation for managing ProductGroupInfoSection.
 * 
 * @author Muhammed Riyas T
 * @since Sep 21, 2016
 */
@Service
@Transactional
public class ProductGroupInfoSectionServiceImpl implements ProductGroupInfoSectionService {

	private final Logger log = LoggerFactory.getLogger(ProductGroupInfoSectionServiceImpl.class);

	@Inject
	private ProductGroupInfoSectionRepository productGroupInfoSectionRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	/**
	 * Save a productGroupInfoSection.
	 * 
	 * @param productGroupInfoSectionDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ProductGroupInfoSectionDTO save(ProductGroupInfoSectionDTO productGroupInfoSectionDTO) {
		log.debug("Request to save ProductGroupInfoSection : {}", productGroupInfoSectionDTO);

		ProductGroupInfoSection productGroupInfoSection = new ProductGroupInfoSection();
		productGroupInfoSection.setName(productGroupInfoSectionDTO.getName());
		productGroupInfoSection.setRichText(productGroupInfoSectionDTO.getRichText());
		productGroupInfoSection.setProductGroup(
				productGroupRepository.findOneByPid(productGroupInfoSectionDTO.getProductGroupPid()).get());
		// set pid
		productGroupInfoSection.setPid(ProductGroupInfoSectionService.PID_PREFIX + RandomUtil.generatePid());
		// set company
		productGroupInfoSection.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		productGroupInfoSection = productGroupInfoSectionRepository.save(productGroupInfoSection);
		ProductGroupInfoSectionDTO result = new ProductGroupInfoSectionDTO(productGroupInfoSection);
		return result;
	}

	/**
	 * Update a productGroupInfoSection.
	 * 
	 * @param productGroupInfoSectionDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ProductGroupInfoSectionDTO update(ProductGroupInfoSectionDTO productGroupInfoSectionDTO) {
		log.debug("Request to Update ProductGroupInfoSection : {}", productGroupInfoSectionDTO);

		return productGroupInfoSectionRepository.findOneByPid(productGroupInfoSectionDTO.getPid())
				.map(productGroupInfoSection -> {
					productGroupInfoSection.setName(productGroupInfoSectionDTO.getName());
					productGroupInfoSection.setRichText(productGroupInfoSectionDTO.getRichText());
					productGroupInfoSection.setProductGroup(
							productGroupRepository.findOneByPid(productGroupInfoSectionDTO.getProductGroupPid()).get());
					productGroupInfoSection = productGroupInfoSectionRepository.save(productGroupInfoSection);
					ProductGroupInfoSectionDTO result = new ProductGroupInfoSectionDTO(productGroupInfoSection);
					return result;
				}).orElse(null);
	}

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProductGroupInfoSection> findAll(Pageable pageable) {
		log.debug("Request to get all ProductGroupInfoSections");
		Page<ProductGroupInfoSection> result = productGroupInfoSectionRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProductGroupInfoSectionDTO> findAllByCompany() {
		log.debug("Request to get all ProductGroupInfoSections");
		List<ProductGroupInfoSection> productGroupInfoSectionList = productGroupInfoSectionRepository
				.findAllByCompanyId();
		List<ProductGroupInfoSectionDTO> result = productGroupInfoSectionList.stream()
				.map(ProductGroupInfoSectionDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<ProductGroupInfoSectionDTO> findAllByCompanyWithRichText() {
		log.debug("Request to get all ProductGroupInfoSections");
		List<ProductGroupInfoSection> productGroupInfoSectionList = productGroupInfoSectionRepository
				.findAllByCompanyId();
		List<ProductGroupInfoSectionDTO> result = productGroupInfoSectionList.stream()
				.map(p -> new ProductGroupInfoSectionDTO(p.getPid(), p.getName(), p.getRichText(), p.getProductGroup().getPid(), p.getProductGroup().getName())).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProductGroupInfoSectionDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all ProductGroupInfoSections");
		Page<ProductGroupInfoSection> activities = productGroupInfoSectionRepository.findAllByCompanyId(pageable);
		Page<ProductGroupInfoSectionDTO> result = new PageImpl<ProductGroupInfoSectionDTO>(
				activities.getContent().stream().map(ProductGroupInfoSectionDTO::new).collect(Collectors.toList()),
				pageable, activities.getTotalElements());
		return result;
	}

	/**
	 * Get one productGroupInfoSection by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ProductGroupInfoSectionDTO findOne(Long id) {
		log.debug("Request to get ProductGroupInfoSection : {}", id);
		ProductGroupInfoSection productGroupInfoSection = productGroupInfoSectionRepository.findOne(id);
		ProductGroupInfoSectionDTO productGroupInfoSectionDTO = new ProductGroupInfoSectionDTO(productGroupInfoSection);
		return productGroupInfoSectionDTO;
	}

	/**
	 * Get one productGroupInfoSection by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductGroupInfoSectionDTO> findOneByPid(String pid) {
		log.debug("Request to get ProductGroupInfoSection by pid : {}", pid);
		return productGroupInfoSectionRepository.findOneByPid(pid).map(productGroupInfoSection -> {
			ProductGroupInfoSectionDTO productGroupInfoSectionDTO = new ProductGroupInfoSectionDTO(
					productGroupInfoSection);
			// set rich text separte
			productGroupInfoSectionDTO.setRichText(productGroupInfoSection.getRichText());
			return productGroupInfoSectionDTO;
		});
	}

	/**
	 * Get one productGroupInfoSection by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductGroupInfoSectionDTO> findByName(String name) {
		log.debug("Request to get ProductGroupInfoSection by name : {}", name);
		return productGroupInfoSectionRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(productGroupInfoSection -> {
					ProductGroupInfoSectionDTO productGroupInfoSectionDTO = new ProductGroupInfoSectionDTO(
							productGroupInfoSection);
					return productGroupInfoSectionDTO;
				});
	}

	/**
	 * Delete the productGroupInfoSection by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ProductGroupInfoSection : {}", pid);
		productGroupInfoSectionRepository.findOneByPid(pid).ifPresent(productGroupInfoSection -> {
			productGroupInfoSectionRepository.delete(productGroupInfoSection.getId());
		});
	}

}
