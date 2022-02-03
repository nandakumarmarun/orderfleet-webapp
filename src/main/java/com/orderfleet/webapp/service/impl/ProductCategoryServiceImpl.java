package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
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

import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.SetDiscountPercentage;
import com.orderfleet.webapp.web.rest.mapper.ProductCategoryMapper;

/**
 * Service Implementation for managing ProductCategory.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

	private final Logger log = LoggerFactory.getLogger(ProductCategoryServiceImpl.class);

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductCategoryMapper productCategoryMapper;
	
	@Inject
	private ProductProfileRepository productProfileRepository;

	/**
	 * Save a productCategory.
	 * 
	 * @param productCategoryDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ProductCategoryDTO save(ProductCategoryDTO productCategoryDTO) {
		log.debug("Request to save ProductCategory : {}", productCategoryDTO);
		// set pid
		productCategoryDTO.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
		ProductCategory productCategory = productCategoryMapper.productCategoryDTOToProductCategory(productCategoryDTO);
		productCategory.setLastModifiedDate(LocalDateTime.now());
		// set company
		productCategory.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		productCategory = productCategoryRepository.save(productCategory);
		ProductCategoryDTO result = productCategoryMapper.productCategoryToProductCategoryDTO(productCategory);
		return result;
	}

	/**
	 * Update a productCategory.
	 * 
	 * @param productCategoryDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ProductCategoryDTO update(ProductCategoryDTO productCategoryDTO) {
		log.debug("Request to Update ProductCategory : {}", productCategoryDTO);
		return productCategoryRepository.findOneByPid(productCategoryDTO.getPid()).map(productCategory -> {
			productCategory.setName(productCategoryDTO.getName());
			productCategory.setAlias(productCategoryDTO.getAlias());
			productCategory.setDescription(productCategoryDTO.getDescription());
			productCategory = productCategoryRepository.save(productCategory);
			ProductCategoryDTO result = productCategoryMapper.productCategoryToProductCategoryDTO(productCategory);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the ProductCategories.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProductCategory> findAll(Pageable pageable) {
		log.debug("Request to get all ProductCategories");
		Page<ProductCategory> result = productCategoryRepository.findAll(pageable);
		return result;
	}

	@Override
	public List<ProductCategoryDTO> findAllByCompany() {
		List<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyIdAndActivatedTrue();
		List<ProductCategoryDTO> result = productCategoryMapper
				.productCategoriesToProductCategoryDTOs(productCategories);
		return result;
	}

	/**
	 * Get all the ProductCategories.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProductCategoryDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Product Categories");
		Page<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyId(pageable);
		Page<ProductCategoryDTO> result = new PageImpl<ProductCategoryDTO>(
				productCategoryMapper.productCategoriesToProductCategoryDTOs(productCategories.getContent()), pageable,
				productCategories.getTotalElements());
		return result;
	}

	/**
	 * Get one productCategory by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ProductCategoryDTO findOne(Long id) {
		log.debug("Request to get ProductCategory : {}", id);
		ProductCategory productCategory = productCategoryRepository.findOne(id);
		ProductCategoryDTO productCategoryDTO = productCategoryMapper
				.productCategoryToProductCategoryDTO(productCategory);
		return productCategoryDTO;
	}

	/**
	 * Get one productCategory by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductCategoryDTO> findOneByPid(String pid) {
		log.debug("Request to get ProductCategory by pid : {}", pid);
		return productCategoryRepository.findOneByPid(pid).map(productCategory -> {
			ProductCategoryDTO productCategoryDTO = productCategoryMapper
					.productCategoryToProductCategoryDTO(productCategory);
			return productCategoryDTO;
		});
	}

	/**
	 * Get one productCategory by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductCategoryDTO> findByName(String name) {
		log.debug("Request to get ProductCategory by name : {}", name);
		return productCategoryRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(productCategory -> {
					ProductCategoryDTO productCategoryDTO = productCategoryMapper
							.productCategoryToProductCategoryDTO(productCategory);
					return productCategoryDTO;
				});
	}

	/**
	 * Delete the productCategory by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ProductCategory : {}", pid);
		productCategoryRepository.findOneByPid(pid).ifPresent(productCategory -> {
			productCategoryRepository.delete(productCategory.getId());
		});
	}

	/**
	 * Update the productCategory status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activated
	 *            the activated of the entity
	 * @return the entity
	 */
	@Override
	public ProductCategoryDTO updateProductCategoryStatus(String pid, boolean activated) {
		log.debug("Request to update ProductCategory Status: {}", pid);
		return productCategoryRepository.findOneByPid(pid).map(productCategory -> {
			productCategory.setActivated(activated);
			productCategory = productCategoryRepository.save(productCategory);
			ProductCategoryDTO result = productCategoryMapper.productCategoryToProductCategoryDTO(productCategory);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 11, 2017
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
	public Page<ProductCategoryDTO> findAllByCompanyIdAndActivatedOrderByProductCategoryName(Pageable pageable,
			boolean active) {
		Page<ProductCategory> productCategories = productCategoryRepository
				.findAllByCompanyIdAndActivatedOrderByProductCategoryName(pageable, active);
		Page<ProductCategoryDTO> productCategoryDTOs = new PageImpl<ProductCategoryDTO>(
				productCategoryMapper.productCategoriesToProductCategoryDTOs(productCategories.getContent()), pageable,
				productCategories.getTotalElements());
		return productCategoryDTOs;
	}

	/**
	 * @author Fahad
	 * @since Feb 11, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<ProductCategoryDTO> findAllByCompanyAndDeactivated(boolean deactive) {
		List<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyIdAndDeactivated(deactive);
		List<ProductCategoryDTO> productCategoryDTOs = productCategoryMapper
				.productCategoriesToProductCategoryDTOs(productCategories);
		return productCategoryDTOs;
	}
	
	/**
	 * Get one productCategory by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductCategoryDTO> findByNameAndCompanyId(Long companyId,String name) {
		log.debug("Request to get ProductCategory by name : {}", name);
		return productCategoryRepository
				.findByCompanyIdAndNameIgnoreCase(companyId, name)
				.map(productCategory -> {
					ProductCategoryDTO productCategoryDTO = productCategoryMapper
							.productCategoryToProductCategoryDTO(productCategory);
					return productCategoryDTO;
				});
	}

	/**
	 * Save a productCategory.
	 * 
	 * @param productCategoryDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ProductCategoryDTO saveProductCategory(Long companyId,ProductCategoryDTO productCategoryDTO) {
		log.debug("Request to save ProductCategory : {}", productCategoryDTO);
		// set pid
		productCategoryDTO.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
		ProductCategory productCategory = productCategoryMapper.productCategoryDTOToProductCategory(productCategoryDTO);
		// set company
		productCategory.setCompany(companyRepository.findOne(companyId));
		productCategory = productCategoryRepository.save(productCategory);
		ProductCategoryDTO result = productCategoryMapper.productCategoryToProductCategoryDTO(productCategory);
		return result;
	}
	
	/**
	 * Update the productCategory status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activated
	 *            the activated of the entity
	 * @return the entity
	 */
	@Override
	public ProductCategoryDTO updateProductCategoryThirdpartyUpdateStatus(String pid, boolean thirdpartyUpdate) {
		log.debug("Request to update ProductCategory ThirdpartyUpdate Status: {}", pid);
		return productCategoryRepository.findOneByPid(pid).map(productCategory -> {
			productCategory.setThirdpartyUpdate(thirdpartyUpdate);
			productCategory = productCategoryRepository.save(productCategory);
			ProductCategoryDTO result = productCategoryMapper.productCategoryToProductCategoryDTO(productCategory);
			return result;
		}).orElse(null);
	}

	@Override
	public List<ProductCategoryDTO> findAllByCompanyPid(String companyPid) {
		List<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyPid(companyPid);
		List<ProductCategoryDTO> productCategoryDTOs = productCategoryMapper
				.productCategoriesToProductCategoryDTOs(productCategories);
		return productCategoryDTOs;
	}

	@Override
	public void saveDicountPercentage(SetDiscountPercentage setDiscountPercentage) {
		List<ProductProfile> productProfiles = productProfileRepository
				.findByProductCategoryPidIn(setDiscountPercentage.getProductGroups());
		if (!productProfiles.isEmpty()) {
			productProfileRepository.updateDiscoundPercentage(setDiscountPercentage.getDiscountPercentage(), productProfiles);
		}
		
	}

}
