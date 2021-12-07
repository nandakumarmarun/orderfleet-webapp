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

import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.EcomProductGroupEcomProduct;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupEcomProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EcomProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.EcomProductProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.repository.UserEcomProductGroupRepository;
import com.orderfleet.webapp.repository.UserProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EcomProductProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.ecom.mapper.EcomProductProfileMapper;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Service Implementation for managing EcomProductProfile.
 *
 * @author sarath
 * @since Sep 23, 2016
 */
@Service
@Transactional
public class EcomProductProfileServiceImpl implements EcomProductProfileService {

	private final Logger log = LoggerFactory.getLogger(EcomProductProfileServiceImpl.class);

	@Inject
	private EcomProductProfileRepository ecomProductProfileRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private EcomProductProfileMapper ecomProductProfileMapper;

	// old code start
	@Inject
	private UserProductGroupRepository userProductGroupRepository;

	@Inject
	private ProductGroupEcomProductsRepository productGroupEcomProductsRepository;
	// old code ends
	@Inject
	private UserEcomProductGroupRepository userEcomProductGroupRepository;

	@Inject
	private EcomProductGroupEcomProductsRepository ecomProductGroupEcomProductsRepository;

	@Inject
	private EcomProductProfileProductRepository ecomProductProfileProductRepository;

	/**
	 * Save a ecomProductProfile.
	 *
	 * @param ecomProductProfileDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public EcomProductProfileDTO save(EcomProductProfileDTO ecomProductProfileDTO) {
		log.debug("Request to save EcomProductProfile : {}", ecomProductProfileDTO);
		ecomProductProfileDTO.setPid(EcomProductProfileService.PID_PREFIX + RandomUtil.generatePid()); // set
		if (!ecomProductProfileDTO.getEcomDisplayAttributes().equals("")
				&& ecomProductProfileDTO.getEcomDisplayAttributes() != null) {
			String ecomDisplayAttributes = ecomProductProfileDTO.getEcomDisplayAttributes().substring(0,
					ecomProductProfileDTO.getEcomDisplayAttributes().length() - 1);
			ecomProductProfileDTO.setEcomDisplayAttributes(ecomDisplayAttributes);
		}
		// pid
		EcomProductProfile ecomProductProfile = ecomProductProfileMapper
				.ecomProductProfileDTOToEcomProductProfile(ecomProductProfileDTO);
		ecomProductProfile.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		ecomProductProfile = ecomProductProfileRepository.save(ecomProductProfile);
		EcomProductProfileDTO result = ecomProductProfileMapper
				.ecomProductProfileToEcomProductProfileDTO(ecomProductProfile);
		return result;
	}

	/**
	 * Update a ecomProductProfile.
	 *
	 * @param ecomProductProfileDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public EcomProductProfileDTO update(EcomProductProfileDTO ecomProductProfileDTO) {
		log.debug("Request to Update EcomProductProfile : {}", ecomProductProfileDTO);

		return ecomProductProfileRepository.findOneByPid(ecomProductProfileDTO.getPid()).map(ecomProductProfile -> {
			ecomProductProfile.setName(ecomProductProfileDTO.getName());
			ecomProductProfile.setAlias(ecomProductProfileDTO.getAlias());
			ecomProductProfile.setDescription(ecomProductProfileDTO.getDescription());
			if (!ecomProductProfileDTO.getEcomDisplayAttributes().equals("")
					&& ecomProductProfileDTO.getEcomDisplayAttributes() != null) {
				String ecomDisplayAttributes = ecomProductProfileDTO.getEcomDisplayAttributes().substring(0,
						ecomProductProfileDTO.getEcomDisplayAttributes().length() - 1);
				ecomProductProfileDTO.setEcomDisplayAttributes(ecomDisplayAttributes);
			}
			ecomProductProfile.setImage(ecomProductProfileDTO.getImage());
			ecomProductProfile.setImageContentType(ecomProductProfileDTO.getImageContentType());
			ecomProductProfile.setOfferString(ecomProductProfileDTO.getOfferString());
			ecomProductProfile.setEcomDisplayAttributes(ecomProductProfileDTO.getEcomDisplayAttributes());
			ecomProductProfile = ecomProductProfileRepository.save(ecomProductProfile);
			EcomProductProfileDTO result = ecomProductProfileMapper
					.ecomProductProfileToEcomProductProfileDTO(ecomProductProfile);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the ecomProductProfiles.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<EcomProductProfileDTO> findAllByCompany() {
		log.debug("Request to get all EcomProductProfiles");
		List<EcomProductProfile> ecomProductProfileList = ecomProductProfileRepository.findAllByCompanyId();
		List<EcomProductProfileDTO> result = ecomProductProfileMapper
				.ecomProductProfilesToEcomProductProfileDTOs(ecomProductProfileList);
		return result;
	}

	/**
	 * Get all the ecomProductProfiles.
	 *
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<EcomProductProfileDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all EcomProductProfiles");
		Page<EcomProductProfile> ecomProductProfiles = ecomProductProfileRepository.findAllByCompanyId(pageable);
		Page<EcomProductProfileDTO> result = new PageImpl<EcomProductProfileDTO>(
				ecomProductProfileMapper.ecomProductProfilesToEcomProductProfileDTOs(ecomProductProfiles.getContent()),
				pageable, ecomProductProfiles.getTotalElements());
		return result;
	}

	/**
	 * Get one ecomProductProfile by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public EcomProductProfileDTO findOne(Long id) {
		log.debug("Request to get EcomProductProfile : {}", id);
		EcomProductProfile ecomProductProfile = ecomProductProfileRepository.findOne(id);
		EcomProductProfileDTO ecomProductProfileDTO = ecomProductProfileMapper
				.ecomProductProfileToEcomProductProfileDTO(ecomProductProfile);
		return ecomProductProfileDTO;
	}

	/**
	 * Get one ecomProductProfile by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<EcomProductProfileDTO> findOneByPid(String pid) {
		log.debug("Request to get EcomProductProfile by pid : {}", pid);
		return ecomProductProfileRepository.findOneByPid(pid).map(ecomProductProfile -> {
			EcomProductProfileDTO ecomProductProfileDTO = ecomProductProfileMapper
					.ecomProductProfileToEcomProductProfileDTO(ecomProductProfile);
			return ecomProductProfileDTO;
		});
	}

	/**
	 * Get one ecomProductProfile by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<EcomProductProfileDTO> findByName(String name) {
		log.debug("Request to get EcomProductProfile by name : {}", name);
		return ecomProductProfileRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(ecomProductProfile -> {
					EcomProductProfileDTO ecomProductProfileDTO = ecomProductProfileMapper
							.ecomProductProfileToEcomProductProfileDTO(ecomProductProfile);
					return ecomProductProfileDTO;
				});
	}

	/**
	 * Delete the ecomProductProfile by id.
	 *
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete EcomProductProfile : {}", pid);
		ecomProductProfileRepository.findOneByPid(pid).ifPresent(ecomProductProfile -> {
			ecomProductProfileRepository.delete(ecomProductProfile.getId());
		});
	}

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        Update the EcomProductProfile status by pid.
	 *
	 * @param pid      the pid of the entity
	 * @param activate the activate of the entity
	 * @return the entity
	 */
	@Override
	public EcomProductProfileDTO updateEcomProductProfileStatus(String pid, boolean activate) {
		log.debug("Request to update ecomProductProfile status: {}");
		return ecomProductProfileRepository.findOneByPid(pid).map(ecomProductProfile -> {
			if (!activate) {
				ecomProductProfileProductRepository.deleteByEcomProductProfilePid(pid);
			}
			ecomProductProfile.setActivated(activate);
			ecomProductProfile = ecomProductProfileRepository.save(ecomProductProfile);
			EcomProductProfileDTO result = ecomProductProfileMapper
					.ecomProductProfileToEcomProductProfileDTO(ecomProductProfile);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 18, 2017
	 *
	 *        Get all the Activated ecomProductGroups of a company.
	 *
	 * @param pageable the pagination information
	 *
	 * @param active   the active =true
	 *
	 * @return the entities
	 */
	@Override
	public Page<EcomProductProfileDTO> findAllByCompanyAndActivatedEcomProductProfileOrderByName(Pageable pageable,
			boolean active) {
		log.debug("Request to get all activated EcomProductProfiles");
		Page<EcomProductProfile> ecomProductProfiles = ecomProductProfileRepository
				.findAllByCompanyIdAndActivatedEcomProductProfileOrderByName(pageable, active);
		Page<EcomProductProfileDTO> result = new PageImpl<EcomProductProfileDTO>(
				ecomProductProfileMapper.ecomProductProfilesToEcomProductProfileDTOs(ecomProductProfiles.getContent()),
				pageable, ecomProductProfiles.getTotalElements());
		return result;
	}

	/**
	 * @author Fahad
	 * @since Feb 18, 2017
	 *
	 *        find all deactive company
	 *
	 * @param deactive the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<EcomProductProfileDTO> findAllByCompanyAndActivatedOrDeactivatedEcomProductProfile(boolean deactive) {
		log.debug("Request to get all deactivated EcomProductProfiles");
		List<EcomProductProfile> ecomProductProfiles = ecomProductProfileRepository
				.findAllByCompanyIdAndActivatedOrDeactivatedEcomProductProfile(deactive);
		List<EcomProductProfileDTO> ecomProductProfileDTOs = ecomProductProfileMapper
				.ecomProductProfilesToEcomProductProfileDTOs(ecomProductProfiles);
		return ecomProductProfileDTOs;
	}

// old code start
	@Override
	@Transactional(readOnly = true)
	public List<EcomProductProfileDTO> findByCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		List<ProductGroup> productGroups = userProductGroupRepository.findProductGroupsByUserIsCurrentUser();
		List<EcomProductProfileDTO> ecomProductProfileDTOs = new ArrayList<>();
		if (productGroups.size() > 0) {
			List<ProductGroupEcomProduct> productGroupEcomProducts = productGroupEcomProductsRepository
					.findByProductGroupsAndLastModifiedDate(productGroups, lastModifiedDate);
			productGroupEcomProducts.forEach(p -> {
				EcomProductProfileDTO ecomProductProfileDTO = new EcomProductProfileDTO(p.getEcomProduct());
				ecomProductProfileDTO.setProductGroupPid(p.getProductGroup().getPid());
				ecomProductProfileDTO.setProductGroupName(p.getProductGroup().getName());
				List<ProductProfile> productProfiles = ecomProductProfileProductRepository
						.findProductByEcomProductProfilePid(p.getEcomProduct().getPid());
				for (ProductProfile productProfile : productProfiles) {
					productProfile.getFiles().size();
					ProductProfileDTO productProfileDTO = new ProductProfileDTO(productProfile);
					productProfileDTO.setFilesPid("");
					for (File file : productProfile.getFiles()) {
						productProfileDTO.setFilesPid(productProfileDTO.getFilesPid() + file.getPid() + ",");
					}
					ecomProductProfileDTO.getProductProfiles().add(productProfileDTO);
				}
				ecomProductProfileDTOs.add(ecomProductProfileDTO);
			});
		}
		return ecomProductProfileDTOs;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductProfileDTO> findByCurrentUser() {
		List<ProductGroup> productGroups = userProductGroupRepository.findProductGroupsByUserIsCurrentUser();
		List<EcomProductProfileDTO> ecomProductProfileDTOs = new ArrayList<>();
		if (productGroups.size() > 0) {
			List<ProductGroupEcomProduct> productGroupEcomProducts = productGroupEcomProductsRepository
					.findByProductGroups(productGroups);
			productGroupEcomProducts.forEach(p -> {
				EcomProductProfileDTO ecomProductProfileDTO = new EcomProductProfileDTO(p.getEcomProduct());
				ecomProductProfileDTO.setProductGroupPid(p.getProductGroup().getPid());
				ecomProductProfileDTO.setProductGroupName(p.getProductGroup().getName());
				List<ProductProfile> productProfiles = ecomProductProfileProductRepository
						.findProductByEcomProductProfilePid(p.getEcomProduct().getPid());
				for (ProductProfile productProfile : productProfiles) {
					productProfile.getFiles().size();
					ProductProfileDTO productProfileDTO = new ProductProfileDTO(productProfile);
					productProfileDTO.setFilesPid("");
					for (File file : productProfile.getFiles()) {
						productProfileDTO.setFilesPid(productProfileDTO.getFilesPid() + file.getPid() + ",");
					}
					ecomProductProfileDTO.getProductProfiles().add(productProfileDTO);
				}
				ecomProductProfileDTOs.add(ecomProductProfileDTO);
			});
		}
		return ecomProductProfileDTOs;
	}
//old code ends

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductProfileDTO> findByCurrentUserAndLastModifiedDateForModern(LocalDateTime lastModifiedDate) {
		List<EcomProductGroup> productGroups = userEcomProductGroupRepository.findProductGroupsByUserIsCurrentUser();
		List<EcomProductProfileDTO> ecomProductProfileDTOs = new ArrayList<>();
		if (productGroups.size() > 0) {
			List<EcomProductGroupEcomProduct> productGroupEcomProducts = ecomProductGroupEcomProductsRepository
					.findByProductGroupsAndLastModifiedDate(productGroups, lastModifiedDate);
			productGroupEcomProducts.forEach(p -> {
				EcomProductProfileDTO ecomProductProfileDTO = new EcomProductProfileDTO(p.getEcomProduct());
				ecomProductProfileDTO.setProductGroupPid(p.getEcomProductGroup().getPid());
				ecomProductProfileDTO.setProductGroupName(p.getEcomProductGroup().getName());
				List<ProductProfile> productProfiles = ecomProductProfileProductRepository
						.findProductByEcomProductProfilePid(p.getEcomProduct().getPid());
				for (ProductProfile productProfile : productProfiles) {
					productProfile.getFiles().size();
					ProductProfileDTO productProfileDTO = new ProductProfileDTO(productProfile);
					productProfileDTO.setFilesPid("");
					for (File file : productProfile.getFiles()) {
						productProfileDTO.setFilesPid(productProfileDTO.getFilesPid() + file.getPid() + ",");
					}
					ecomProductProfileDTO.getProductProfiles().add(productProfileDTO);
				}
				ecomProductProfileDTOs.add(ecomProductProfileDTO);
			});
		}
		return ecomProductProfileDTOs;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EcomProductProfileDTO> findByCurrentUserForModern() {
		List<EcomProductGroup> productGroups = userEcomProductGroupRepository.findProductGroupsByUserIsCurrentUser();
		List<EcomProductProfileDTO> ecomProductProfileDTOs = new ArrayList<>();
		if (productGroups.size() > 0) {
			List<EcomProductGroupEcomProduct> productGroupEcomProducts = ecomProductGroupEcomProductsRepository
					.findByProductGroups(productGroups);
			productGroupEcomProducts.forEach(p -> {
				EcomProductProfileDTO ecomProductProfileDTO = new EcomProductProfileDTO(p.getEcomProduct());
				ecomProductProfileDTO.setProductGroupPid(p.getEcomProductGroup().getPid());
				ecomProductProfileDTO.setProductGroupName(p.getEcomProductGroup().getName());
				List<ProductProfile> productProfiles = ecomProductProfileProductRepository
						.findProductByEcomProductProfilePid(p.getEcomProduct().getPid());
				for (ProductProfile productProfile : productProfiles) {
					productProfile.getFiles().size();
					ProductProfileDTO productProfileDTO = new ProductProfileDTO(productProfile);
					productProfileDTO.setFilesPid("");
					for (File file : productProfile.getFiles()) {
						productProfileDTO.setFilesPid(productProfileDTO.getFilesPid() + file.getPid() + ",");
					}
					ecomProductProfileDTO.getProductProfiles().add(productProfileDTO);
				}
				ecomProductProfileDTOs.add(ecomProductProfileDTO);
			});
		}
		return ecomProductProfileDTOs;
	}

	@Override
	public List<EcomProductProfileDTO> findByproductgrupPId(String productgroupPid) {
		List<EcomProductProfileDTO> ecomProductProfileDTOs = new ArrayList<>();

		List<ProductGroupEcomProduct> productGroupEcomProducts = productGroupEcomProductsRepository
				.getEcomProductByProductGroupPid(productgroupPid);
		productGroupEcomProducts.forEach(p -> {
			EcomProductProfileDTO ecomProductProfileDTO = new EcomProductProfileDTO(p.getEcomProduct());
			ecomProductProfileDTO.setProductGroupPid(p.getProductGroup().getPid());
			ecomProductProfileDTO.setProductGroupName(p.getProductGroup().getName());
			List<ProductProfile> productProfiles = ecomProductProfileProductRepository
					.findProductByEcomProductProfilePid(p.getEcomProduct().getPid());
			for (ProductProfile productProfile : productProfiles) {
				productProfile.getFiles().size();
				ProductProfileDTO productProfileDTO = new ProductProfileDTO(productProfile);
				productProfileDTO.setFilesPid("");
				for (File file : productProfile.getFiles()) {
					productProfileDTO.setFilesPid(productProfileDTO.getFilesPid() + file.getPid() + ",");
				}
				ecomProductProfileDTO.getProductProfiles().add(productProfileDTO);
			}
			ecomProductProfileDTOs.add(ecomProductProfileDTO);
		});
		return ecomProductProfileDTOs;
	}

	@Override
	public EcomProductProfileDTO findOneByProductPid(String productPid) {
		// TODO Auto-generated method stub

		EcomProductProfileDTO ecomProductProfileDTO = new EcomProductProfileDTO();
		ProductProfile productProfile = ecomProductProfileProductRepository
				.findProductByProductProfilePid(productPid);

		productProfile.getFiles().size();
		ProductProfileDTO productProfileDTO = new ProductProfileDTO(productProfile);
		productProfileDTO.setFilesPid("");
		for (File file : productProfile.getFiles()) {
			productProfileDTO.setFilesPid(productProfileDTO.getFilesPid() + file.getPid() + ",");
		}
		ecomProductProfileDTO.getProductProfiles().add(productProfileDTO);

		return ecomProductProfileDTO;
	}

}
