package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
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

import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PriceLevelListRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;

/**
 * Service Implementation for managing PriceLevelList.
 * 
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
@Service
@Transactional
public class PriceLevelListServiceImpl implements PriceLevelListService {

	private final Logger log = LoggerFactory.getLogger(PriceLevelListServiceImpl.class);

	@Inject
	private PriceLevelListRepository priceLevelListRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductProfileMapper productProfileMapper;

	/**
	 * Save a priceLevelList.
	 * 
	 * @param priceLevelListDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public PriceLevelListDTO save(PriceLevelListDTO priceLevelListDTO) {
		log.debug("Request to save PriceLevelList : {}", priceLevelListDTO);

		PriceLevelList priceLevelList = new PriceLevelList();
		// set pid
		priceLevelList.setPid(PriceLevelListService.PID_PREFIX + RandomUtil.generatePid());
		priceLevelList.setPrice(priceLevelListDTO.getPrice());
		priceLevelList.setPriceLevel(priceLevelRepository.findOneByPid(priceLevelListDTO.getPriceLevelPid()).get());
		priceLevelList.setProductProfile(
				productProfileRepository.findOneByPid(priceLevelListDTO.getProductProfilePid()).get());
		priceLevelList.setRangeFrom(priceLevelListDTO.getRangeFrom());
		priceLevelList.setRangeTo(priceLevelListDTO.getRangeTo());
		// set company
		priceLevelList.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		priceLevelList = priceLevelListRepository.save(priceLevelList);
		PriceLevelListDTO result = new PriceLevelListDTO(priceLevelList);
		return result;
	}

	/**
	 * Update a priceLevelList.
	 * 
	 * @param priceLevelListDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public PriceLevelListDTO update(PriceLevelListDTO priceLevelListDTO) {
		log.debug("Request to Update PriceLevelList : {}", priceLevelListDTO);

		return priceLevelListRepository.findOneByPid(priceLevelListDTO.getPid()).map(priceLevelList -> {
			priceLevelList.setPrice(priceLevelListDTO.getPrice());
			priceLevelList.setPriceLevel(priceLevelRepository.findOneByPid(priceLevelListDTO.getPriceLevelPid()).get());
			priceLevelList.setRangeFrom(priceLevelListDTO.getRangeFrom());
			priceLevelList.setRangeTo(priceLevelListDTO.getRangeTo());
			priceLevelList = priceLevelListRepository.save(priceLevelList);
			PriceLevelListDTO result = new PriceLevelListDTO(priceLevelList);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the priceLevelLists.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<PriceLevelList> findAll(Pageable pageable) {
		log.debug("Request to get all PriceLevelLists");
		Page<PriceLevelList> result = priceLevelListRepository.findAll(pageable);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceLevelListDTO> findAllByCompany() {
		log.debug("Request to get all PriceLevelLists");
		List<PriceLevelList> priceLevelListList = priceLevelListRepository.findAllByCompanyId();
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the priceLevelLists.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<PriceLevelListDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all PriceLevelLists");
		Page<PriceLevelList> priceLevelLists = priceLevelListRepository.findAllByCompanyId(pageable);
		List<PriceLevelListDTO> priceLevelListDTOs = priceLevelLists.getContent().stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		Page<PriceLevelListDTO> result = new PageImpl<PriceLevelListDTO>(priceLevelListDTOs, pageable,
				priceLevelLists.getTotalElements());
		return result;
	}

	/**
	 * Get one priceLevelList by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public PriceLevelListDTO findOne(Long id) {
		log.debug("Request to get PriceLevelList : {}", id);
		PriceLevelList priceLevelList = priceLevelListRepository.findOne(id);
		PriceLevelListDTO priceLevelListDTO = new PriceLevelListDTO(priceLevelList);
		return priceLevelListDTO;
	}

	/**
	 * Get one priceLevelList by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PriceLevelListDTO> findOneByPid(String pid) {
		log.debug("Request to get PriceLevelList by pid : {}", pid);
		return priceLevelListRepository.findOneByPid(pid).map(priceLevelList -> {
			PriceLevelListDTO priceLevelListDTO = new PriceLevelListDTO(priceLevelList);
			return priceLevelListDTO;
		});
	}

	/**
	 * Delete the priceLevelList by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete PriceLevelList : {}", pid);
		priceLevelListRepository.findOneByPid(pid).ifPresent(priceLevelList -> {
			priceLevelListRepository.delete(priceLevelList.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceLevelListDTO> findAllByCompanyIdPriceLevelPidAndProductProfilePid(String priceLevelPid,
			String productProfilePid) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdPriceLevelPidAndProductProfilePid(priceLevelPid, productProfilePid);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPid(String priceLevelPid) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdAndPriceLevelPid(priceLevelPid);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceLevelListDTO> findAllByCompanyIdAndProductProfilePid(String productProfilePid) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdAndProductProfilePid(productProfilePid);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PriceLevelListDTO> findAllByCompanyIdPriceLevelNameAndProductProfileName(Long companyId,
			String priceLevelName, String productProfileName) {
		return priceLevelListRepository.findOneByCompanyIdAndPriceLevelNameIgnoreCaseAndProductProfileNameIgnoreCase(
				companyId, priceLevelName, productProfileName).map(priceLevelList -> {
					PriceLevelListDTO priceLevelListDTO = new PriceLevelListDTO(priceLevelList);
					return priceLevelListDTO;
				});
	}

	/**
	 * Save a priceLevelList.
	 * 
	 * @param priceLevelListDTO
	 *            the entity to save
	 * @return the persisted entity
	 * 
	 *         sending parameter companyId
	 * 
	 * @author Sarath
	 * @since march 3, 2017
	 */
	@Override
	public PriceLevelListDTO savePriceLevelList(PriceLevelListDTO priceLevelListDTO, Long companyId) {
		log.debug("Request to save PriceLevelList : {}", priceLevelListDTO);

		PriceLevelList priceLevelList = new PriceLevelList();
		// set pid
		priceLevelList.setPid(PriceLevelListService.PID_PREFIX + RandomUtil.generatePid());
		priceLevelList.setPrice(priceLevelListDTO.getPrice());
		priceLevelList.setPriceLevel(priceLevelRepository.findOneByPid(priceLevelListDTO.getPriceLevelPid()).get());
		priceLevelList.setProductProfile(
				productProfileRepository.findOneByPid(priceLevelListDTO.getProductProfilePid()).get());
		priceLevelList.setRangeFrom(priceLevelListDTO.getRangeFrom());
		priceLevelList.setRangeTo(priceLevelListDTO.getRangeTo());
		// set company
		priceLevelList.setCompany(companyRepository.findOne(companyId));
		priceLevelList = priceLevelListRepository.save(priceLevelList);
		PriceLevelListDTO result = new PriceLevelListDTO(priceLevelList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPidAndpriceLevelLastModifiedDate(String priceLevelPid,
			LocalDateTime lastModifiedDate) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdAndPriceLevelPidAndpriceLevelLastModifiedDate(priceLevelPid, lastModifiedDate);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPidAndLastModifiedDate(String priceLevelPid,
			LocalDateTime lastModifiedDate) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdAndPriceLevelPidAndLastModifiedDate(priceLevelPid, lastModifiedDate);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<ProductProfileDTO> findProductProfileByPriceLevel(String priceLevel) {
		List<ProductProfile> productProfiles = priceLevelListRepository
				.findAllProductProfileByCompanyIdAndPriceLevelPid(priceLevel);
		List<ProductProfileDTO> productProfileDTOs = productProfileMapper
				.productProfilesToProductProfileDTOs(productProfiles);
		return productProfileDTOs;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PriceLevelListDTO> findByCompanyIdAndPriceLevelPidAndProductProfilePidOrderByProductProfileNameDesc(
			Long companyId, String priceLevelPid, String productPid) {
		return priceLevelListRepository
				.findByCompanyIdAndPriceLevelPidAndProductProfilePidOrderByProductProfileNameDesc(companyId,
						priceLevelPid, productPid)
				.map(priceLevelList -> {
					PriceLevelListDTO priceLevelListDTO = new PriceLevelListDTO(priceLevelList);
					return priceLevelListDTO;
				});
	}

	@Override
	public List<PriceLevelListDTO> findAllByCompanyIdAndProductCategoryPid(String productCategoryPid) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdAndProductCategoryPid(productCategoryPid);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<PriceLevelListDTO> findAllByCompanyIdAndProductProfilePidAndProductCategoryPid(String productProfilePid,
			String productCategoryPid) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdAndProductProfilePidAndProductCategoryPid(productProfilePid, productCategoryPid);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPidAndProductCategoryPid(String priceLevelPid,
			String productCategoryPid) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdAndPriceLevelPidAndProductCategoryPid(priceLevelPid, productCategoryPid);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPidAndProductProfilePidAndProductCategoryPid(
			String priceLevelPid, String productProfilePid, String productCategoryPid) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdAndPriceLevelPidAndProductProfilePidAndProductCategoryPid(priceLevelPid,
						productProfilePid, productCategoryPid);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<PriceLevelListDTO> findAllByCompanyIdAndProductProfilePidIn(List<String> productPids) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdAndProductProfilePidIn(productPids);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPidAndProductProfilePidIn(String priceLevelPid,
			List<String> productPids) {
		List<PriceLevelList> priceLevelListList = priceLevelListRepository
				.findAllByCompanyIdAndPriceLevelPidAndProductProfilePidIn(priceLevelPid, productPids);
		List<PriceLevelListDTO> result = priceLevelListList.stream().map(PriceLevelListDTO::new)
				.collect(Collectors.toList());
		return result;
	}

}
