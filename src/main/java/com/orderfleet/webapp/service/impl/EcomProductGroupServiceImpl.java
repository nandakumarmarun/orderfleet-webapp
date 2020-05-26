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

import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EcomProductGroupRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.TaxMasterRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EcomProductGroupService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.SetTaxRate;
import com.orderfleet.webapp.web.rest.mapper.EcomProductGroupMapper;

/**
 * Service Implementation for managing ProductGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Service
@Transactional
public class EcomProductGroupServiceImpl implements EcomProductGroupService {

	private final Logger log = LoggerFactory.getLogger(EcomProductGroupServiceImpl.class);

	@Inject
	private EcomProductGroupRepository productGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private EcomProductGroupMapper productGroupMapper;

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
	 * @param productGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public EcomProductGroupDTO save(EcomProductGroupDTO productGroupDTO) {
		log.debug("Request to save EcomProductGroup : {}", productGroupDTO);
		// set pid
		productGroupDTO.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
		EcomProductGroup productGroup = productGroupMapper.productGroupDTOToProductGroup(productGroupDTO);
		// set company
		productGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		productGroup = productGroupRepository.save(productGroup);
		EcomProductGroupDTO result = productGroupMapper.productGroupToProductGroupDTO(productGroup);
		return result;
	}

	/**
	 * Update a productGroup.
	 * 
	 * @param productGroup
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public EcomProductGroupDTO update(EcomProductGroupDTO productGroupDTO) {
		log.debug("Request to Update EcomProductGroup : {}", productGroupDTO);
		return productGroupRepository.findOneByPid(productGroupDTO.getPid()).map(productGroup -> {
			productGroup.setName(productGroupDTO.getName());
			productGroup.setAlias(productGroupDTO.getAlias());
			productGroup.setDescription(productGroupDTO.getDescription());
			productGroup.setImage(productGroupDTO.getImage());
			productGroup.setImageContentType(productGroupDTO.getImageContentType());
			productGroup = productGroupRepository.save(productGroup);
			EcomProductGroupDTO result = productGroupMapper.productGroupToProductGroupDTO(productGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the productGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<EcomProductGroup> findAll(Pageable pageable) {
		log.debug("Request to get all EcomProductGroups");
		Page<EcomProductGroup> result = productGroupRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the productGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<EcomProductGroupDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all EcomProductGroups");
		Page<EcomProductGroup> productGroups = productGroupRepository.findAllByCompanyId(pageable, true);
		Page<EcomProductGroupDTO> result = new PageImpl<EcomProductGroupDTO>(
				productGroupMapper.productGroupsToProductGroupDTOs(productGroups.getContent()), pageable,
				productGroups.getTotalElements());
		return result;
	}

	/**
	 * Get one productGroup by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public EcomProductGroupDTO findOne(Long id) {
		log.debug("Request to get EcomProductGroup : {}", id);
		EcomProductGroup productGroup = productGroupRepository.findOne(id);
		EcomProductGroupDTO productGroupDTO = productGroupMapper.productGroupToProductGroupDTO(productGroup);
		return productGroupDTO;
	}

	/**
	 * Get one productGroup by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<EcomProductGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get EcomProductGroup by pid : {}", pid);
		return productGroupRepository.findOneByPid(pid).map(productGroup -> {
			EcomProductGroupDTO productGroupDTO = productGroupMapper.productGroupToProductGroupDTO(productGroup);
			productGroupDTO.setImage(productGroup.getImage());
			return productGroupDTO;
		});
	}

	/**
	 * Get one productGroup by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<EcomProductGroupDTO> findByName(String name) {
		log.debug("Request to get EcomProductGroup by name : {}", name);
		return productGroupRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(productGroup -> {
					EcomProductGroupDTO productGroupDTO = productGroupMapper.productGroupToProductGroupDTO(productGroup);
					return productGroupDTO;
				});
	}

	/**
	 * Delete the productGroup by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete EcomProductGroup : {}", pid);
		productGroupRepository.findOneByPid(pid).ifPresent(productGroup -> {
			productGroupRepository.delete(productGroup.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductGroupDTO> findAllByCompany() {
		log.debug("Request to get all EcomProductGroup");
		List<EcomProductGroup> productGroups = productGroupRepository.findAllByCompanyId(true);
		List<EcomProductGroupDTO> result = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return result;
	}

	@Override
	public void saveTaxRate(SetTaxRate setTaxRate) {
		List<ProductProfile> productProfiles = productGroupProductRepository
				.findProductsByProductGroupPids(setTaxRate.getProductGroups());
		productProfileRepository.updateTaxRate(setTaxRate.getTaxRate(), productProfiles);
	}

	@Override
	public void saveUnitQuantity(SetTaxRate setUnitQty) {
		List<ProductProfile> productProfiles = productGroupProductRepository
				.findProductsByProductGroupPids(setUnitQty.getProductGroups());
		if(!productProfiles.isEmpty()) {
			productProfileRepository.updateUnitQuantity(setUnitQty.getTaxRate(), productProfiles);	
		}
	}
	
	/**
	 * Update the productGroup status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public EcomProductGroupDTO updateProductGroupStatus(String pid, boolean active) {
		log.debug("Request to Update EcomProductGroup activate or deactivate: {}", pid);
		return productGroupRepository.findOneByPid(pid).map(productGroup -> {
			if (!active) {
				productGroupProductRepository.deleteByProductGroupPid(pid);
				productGroupEcomProductsRepository.deleteByProductGroupPid(pid);
			}
			productGroup.setActivated(active);
			productGroup = productGroupRepository.save(productGroup);
			EcomProductGroupDTO res = productGroupMapper.productGroupToProductGroupDTO(productGroup);
			return res;
		}).orElse(null);

	}

	@Override
	public List<EcomProductGroupDTO> findAllByCompanyOrderByName() {
		log.debug("Request to get EcomProductGroup by company: {}");
		List<EcomProductGroup> productGroups = productGroupRepository.findAllByCompanyOrderByName();
		List<EcomProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return productGroupDTOs;
	}

	/**
	 * 
	 * 
	 *        find all active company
	 * 
	 * @param active
	 *            the active of the entity
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
	@Override
	public Page<EcomProductGroupDTO> findAllByCompanyAndActivatedProductGroupOrderByName(Pageable pageable,
			boolean active) {
		log.debug("Request to get activated EcomProductGroup ");
		Page<EcomProductGroup> pageProductGroups = productGroupRepository
				.findAllByCompanyIdAndActivatedProductGroupOrderByName(active, pageable);
		Page<EcomProductGroupDTO> pageProductGroupDTO = new PageImpl<EcomProductGroupDTO>(
				productGroupMapper.productGroupsToProductGroupDTOs(pageProductGroups.getContent()), pageable,
				pageProductGroups.getTotalElements());
		return pageProductGroupDTO;
	}

	/**
	 * 
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<EcomProductGroupDTO> findAllByCompanyAndDeactivatedProductGroup(boolean deactive) {
		log.debug("Request to get deactivated EcomProductGroup ");
		List<EcomProductGroup> productGroups = productGroupRepository
				.findAllByCompanyIdAndDeactivatedProductGroup(deactive);
		List<EcomProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return productGroupDTOs;
	}
	
	/**
	 * Get one productGroup by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<EcomProductGroupDTO> findByCompanyIdAndName(Long companyId,String name) {
		log.debug("Request to get EcomProductGroup by name : {}", name);
		return productGroupRepository.findByCompanyIdAndNameIgnoreCase(companyId, name)
				.map(productGroup -> {
					EcomProductGroupDTO productGroupDTO = productGroupMapper.productGroupToProductGroupDTO(productGroup);
					return productGroupDTO;
				});
	}

	/**
	 * Save a productGroup.
	 * 
	 * @param productGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public EcomProductGroupDTO saveProductGroup(Long companyId,EcomProductGroupDTO productGroupDTO) {
		log.debug("Request to save ProductGroup : {}", productGroupDTO);
		// set pid
		productGroupDTO.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
		EcomProductGroup productGroup = productGroupMapper.productGroupDTOToProductGroup(productGroupDTO);
		// set company
		productGroup.setCompany(companyRepository.findOne(companyId));
		productGroup = productGroupRepository.save(productGroup);
		EcomProductGroupDTO result = productGroupMapper.productGroupToProductGroupDTO(productGroup);
		return result;
	}

	@Override
	public EcomProductGroupDTO updateProductGroupThirdpartyUpdate(String pid, boolean Thirdparty) {
		
		return productGroupRepository.findOneByPid(pid).map(productGroup ->{
			System.out.println(Thirdparty);
			productGroup.setThirdpartyUpdate(Thirdparty);
			 productGroup=productGroupRepository.save(productGroup);
			 System.out.println(productGroup);
			EcomProductGroupDTO productGroupDTO=productGroupMapper.productGroupToProductGroupDTO(productGroup);
			return productGroupDTO;
		}).orElse(null);
	}

	@Override
	public void saveTaxMaster(List<String> taxmasterPids, List<String> productGroupPids) {
		log.debug("Request to save ProductGroup by taxmasterPids : {}", taxmasterPids);
		List<TaxMaster>taxMasters=new ArrayList<>();
		for(String taxPid:taxmasterPids){
			TaxMaster taxMaster=taxMasterRepository.findOneByPid(taxPid).get();
			taxMasters.add(taxMaster);
		}
		for(String pid:productGroupPids){
		EcomProductGroup productGroup=productGroupRepository.findOneByPid(pid).get();
		productGroup.setTaxMastersList(taxMasters);
		productGroupRepository.save(productGroup);
		List<ProductProfile> productProfiles=productGroupProductRepository.findProductByProductGroupPid(pid);
		for(ProductProfile productProfile:productProfiles){
			productProfile.setTaxMastersList(taxMasters);
			productProfileRepository.save(productProfile);
		}
	}
}

	@Override
	public List<EcomProductGroupDTO> findAllProductGroupByCompanyOrderByName() {
		log.debug("Request to get ProductGroup by company: {}");
		List<EcomProductGroup> productGroups = productGroupRepository.findAllByCompanyOrderByName();
		List<EcomProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return productGroupDTOs;
	}

	@Override
	public List<EcomProductGroupDTO> findAllProductGroupByCompanyPid(String companyPid) {
		List<EcomProductGroup> productGroups = productGroupRepository.findAllByCompanyPidOrderByProductId(companyPid);
		List<EcomProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return productGroupDTOs;
	}

	@Override
	public List<EcomProductGroupDTO> findAllProductGroupByCompanyPidAndGroupPid(String companyPid, List<String> groupPids) {
		List<EcomProductGroup> productGroups = productGroupRepository.findAllByCompanyPidAndGroupPidIn(companyPid, groupPids);
		List<EcomProductGroupDTO> productGroupDTOs = productGroupMapper.productGroupsToProductGroupDTOs(productGroups);
		return productGroupDTOs;
	}
	
	
}