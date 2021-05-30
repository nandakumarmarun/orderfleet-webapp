package com.orderfleet.webapp.service.impl;

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

import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.TaxMasterRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.SetTaxRate;
import com.orderfleet.webapp.web.rest.mapper.ProductGroupMapper;

/**
 * Service Implementation for managing ProductGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Service
@Transactional
public class ProductGroupServiceImpl implements ProductGroupService {

	private final Logger log = LoggerFactory.getLogger(ProductGroupServiceImpl.class);

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductGroupMapper productGroupMapper;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ProductGroupEcomProductsRepository productGroupEcomProductsRepository;

	@Inject
	private TaxMasterRepository taxMasterRepository;

	/**
	 * Save a productGroup.
	 * 
	 * @param productGroupDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ProductGroupDTO save(ProductGroupDTO productGroupDTO) {
		log.debug("Request to save ProductGroup : {}", productGroupDTO);
		// set pid
		productGroupDTO.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
		ProductGroup productGroup = productGroupMapper.productGroupDTOToProductGroup(productGroupDTO);
		// set company
		productGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		productGroup = productGroupRepository.save(productGroup);
		ProductGroupDTO result = productGroupMapper.productGroupToProductGroupDTO(productGroup);
		return result;
	}

	/**
	 * Update a productGroup.
	 * 
	 * @param productGroup the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ProductGroupDTO update(ProductGroupDTO productGroupDTO) {
		log.debug("Request to Update ProductGroup : {}", productGroupDTO);
		return productGroupRepository.findOneByPid(productGroupDTO.getPid()).map(productGroup -> {
			productGroup.setName(productGroupDTO.getName());
			productGroup.setAlias(productGroupDTO.getAlias());
			productGroup.setDescription(productGroupDTO.getDescription());
			productGroup.setImage(productGroupDTO.getImage());
			productGroup.setImageContentType(productGroupDTO.getImageContentType());
			productGroup = productGroupRepository.save(productGroup);
			ProductGroupDTO result = productGroupMapper.productGroupToProductGroupDTO(productGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the productGroups.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProductGroup> findAll(Pageable pageable) {
		log.debug("Request to get all ProductGroups");
		Page<ProductGroup> result = productGroupRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the productGroups.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProductGroupDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all ProductGroups");
		Page<ProductGroup> productGroups = productGroupRepository.findAllByCompanyId(pageable, true);
		Page<ProductGroupDTO> result = new PageImpl<ProductGroupDTO>(
				productGroupMapper.productGroupsToProductGroupDTOs(productGroups.getContent()), pageable,
				productGroups.getTotalElements());
		return result;
	}

	/**
	 * Get one productGroup by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ProductGroupDTO findOne(Long id) {
		log.debug("Request to get ProductGroup : {}", id);
		ProductGroup productGroup = productGroupRepository.findOne(id);
		ProductGroupDTO productGroupDTO = productGroupMapper.productGroupToProductGroupDTO(productGroup);
		return productGroupDTO;
	}

	/**
	 * Get one productGroup by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get ProductGroup by pid : {}", pid);
		return productGroupRepository.findOneByPid(pid).map(productGroup -> {
			ProductGroupDTO productGroupDTO = productGroupMapper.productGroupToProductGroupDTO(productGroup);
			productGroupDTO.setImage(productGroup.getImage());
			return productGroupDTO;
		});
	}

	/**
	 * Get one productGroup by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductGroupDTO> findByName(String name) {
		log.debug("Request to get ProductGroup by name : {}", name);
		return productGroupRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(productGroup -> {
					ProductGroupDTO productGroupDTO = productGroupMapper.productGroupToProductGroupDTO(productGroup);
					return productGroupDTO;
				});
	}

	/**
	 * Delete the productGroup by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ProductGroup : {}", pid);
		productGroupRepository.findOneByPid(pid).ifPresent(productGroup -> {
			productGroupRepository.delete(productGroup.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductGroupDTO> findAllByCompany() {
		log.debug("Request to get all ProductGroup");
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyId(true);
		List<ProductGroupDTO> result = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return result;
	}

	@Override
	public void saveTaxRate(SetTaxRate setTaxRate) {
		List<ProductProfile> productProfiles = productGroupProductRepository
				.findProductsByProductGroupPids(setTaxRate.getProductGroups());

		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyPidAndGroupPidIn(
				productProfiles.get(0).getCompany().getPid(), setTaxRate.getProductGroups());
		for (ProductGroup productGroup : productGroups) {
			productGroup.setTaxRate(setTaxRate.getTaxRate());
		}
		productGroupRepository.save(productGroups);
		productProfileRepository.updateTaxRate(setTaxRate.getTaxRate(), productProfiles);
	}

	@Override
	public void saveUnitQuantity(SetTaxRate setUnitQty) {
		List<ProductProfile> productProfiles = productGroupProductRepository
				.findProductsByProductGroupPids(setUnitQty.getProductGroups());
		if (!productProfiles.isEmpty()) {
			productProfileRepository.updateUnitQuantity(setUnitQty.getTaxRate(), productProfiles);
		}
	}

	/**
	 * Update the productGroup status by pid.
	 * 
	 * @param pid    the pid of the entity
	 * @param active the active of the entity
	 * @return the entity
	 */
	@Override
	public ProductGroupDTO updateProductGroupStatus(String pid, boolean active) {
		log.debug("Request to Update ProductGroup activate or deactivate: {}", pid);
		return productGroupRepository.findOneByPid(pid).map(productGroup -> {
			if (!active) {
				productGroupProductRepository.deleteByProductGroupPid(pid);
				productGroupEcomProductsRepository.deleteByProductGroupPid(pid);
			}
			productGroup.setActivated(active);
			productGroup = productGroupRepository.save(productGroup);
			ProductGroupDTO res = productGroupMapper.productGroupToProductGroupDTO(productGroup);
			return res;
		}).orElse(null);

	}

	@Override
	public List<ProductGroupDTO> findAllByCompanyOrderByName() {
		log.debug("Request to get ProductGroup by company: {}");
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyOrderByName();
		List<ProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return productGroupDTOs;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active   the active of the entity
	 * 
	 * @param pageable the pageable of the entity
	 * @return the entity
	 */
	@Override
	public Page<ProductGroupDTO> findAllByCompanyAndActivatedProductGroupOrderByName(Pageable pageable,
			boolean active) {
		log.debug("Request to get activated ProductGroup ");
		Page<ProductGroup> pageProductGroups = productGroupRepository
				.findAllByCompanyIdAndActivatedProductGroupOrderByName(active, pageable);
		Page<ProductGroupDTO> pageProductGroupDTO = new PageImpl<ProductGroupDTO>(
				productGroupMapper.productGroupsToProductGroupDTOs(pageProductGroups.getContent()), pageable,
				pageProductGroups.getTotalElements());
		return pageProductGroupDTO;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<ProductGroupDTO> findAllByCompanyAndDeactivatedProductGroup(boolean deactive) {
		log.debug("Request to get deactivated ProductGroup ");
		List<ProductGroup> productGroups = productGroupRepository
				.findAllByCompanyIdAndDeactivatedProductGroup(deactive);
		List<ProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return productGroupDTOs;
	}

	/**
	 * Get one productGroup by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductGroupDTO> findByCompanyIdAndName(Long companyId, String name) {
		log.debug("Request to get ProductGroup by name : {}", name);
		return productGroupRepository.findByCompanyIdAndNameIgnoreCase(companyId, name).map(productGroup -> {
			ProductGroupDTO productGroupDTO = productGroupMapper.productGroupToProductGroupDTO(productGroup);
			return productGroupDTO;
		});
	}

	/**
	 * Save a productGroup.
	 * 
	 * @param productGroupDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ProductGroupDTO saveProductGroup(Long companyId, ProductGroupDTO productGroupDTO) {
		log.debug("Request to save ProductGroup : {}", productGroupDTO);
		// set pid
		productGroupDTO.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
		ProductGroup productGroup = productGroupMapper.productGroupDTOToProductGroup(productGroupDTO);
		// set company
		productGroup.setCompany(companyRepository.findOne(companyId));
		productGroup = productGroupRepository.save(productGroup);
		ProductGroupDTO result = productGroupMapper.productGroupToProductGroupDTO(productGroup);
		return result;
	}

	@Override
	public ProductGroupDTO updateProductGroupThirdpartyUpdate(String pid, boolean Thirdparty) {

		return productGroupRepository.findOneByPid(pid).map(productGroup -> {
			System.out.println(Thirdparty);
			productGroup.setThirdpartyUpdate(Thirdparty);
			productGroup = productGroupRepository.save(productGroup);
			System.out.println(productGroup);
			ProductGroupDTO productGroupDTO = productGroupMapper.productGroupToProductGroupDTO(productGroup);
			return productGroupDTO;
		}).orElse(null);
	}

	@Override
	public void saveTaxMaster(List<String> taxmasterPids, List<String> productGroupPids) {
		log.debug("Request to save ProductGroup by taxmasterPids : {}", taxmasterPids);
		List<TaxMaster> taxMasters = new ArrayList<>();
		for (String taxPid : taxmasterPids) {
			TaxMaster taxMaster = taxMasterRepository.findOneByPid(taxPid).get();
			taxMasters.add(taxMaster);
		}
		for (String pid : productGroupPids) {
			ProductGroup productGroup = productGroupRepository.findOneByPid(pid).get();
			productGroup.setTaxMastersList(taxMasters);
			productGroupRepository.save(productGroup);
			List<ProductProfile> productProfiles = productGroupProductRepository.findProductByProductGroupPid(pid);
			for (ProductProfile productProfile : productProfiles) {
				productProfile.setTaxMastersList(taxMasters);
				productProfileRepository.save(productProfile);
			}
		}
	}

	@Override
	public List<ProductGroupDTO> findAllProductGroupByCompanyOrderByName() {
		log.debug("Request to get ProductGroup by company: {}");
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyOrderByName();
		List<ProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return productGroupDTOs;
	}

	@Override
	public List<ProductGroupDTO> findAllProductGroupByCompanyPid(String companyPid) {
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyPidOrderByProductId(companyPid);
		List<ProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return productGroupDTOs;
	}

	@Override
	public List<ProductGroupDTO> findAllProductGroupByCompanyPidAndGroupPid(String companyPid, List<String> groupPids) {
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyPidAndGroupPidIn(companyPid,
				groupPids);
		List<ProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return productGroupDTOs;
	}

}