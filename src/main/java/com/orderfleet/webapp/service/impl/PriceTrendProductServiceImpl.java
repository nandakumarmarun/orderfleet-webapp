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

import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PriceTrendProductCompetitorRepository;
import com.orderfleet.webapp.repository.PriceTrendProductRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PriceTrendProductService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;
import com.orderfleet.webapp.web.rest.mapper.PriceTrendProductMapper;

/**
 * Service Implementation for managing PriceTrendProduct.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Service
@Transactional
public class PriceTrendProductServiceImpl implements PriceTrendProductService {

	private final Logger log = LoggerFactory.getLogger(PriceTrendProductServiceImpl.class);

	@Inject
	private PriceTrendProductRepository priceTrendProductRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private PriceTrendProductMapper priceTrendProductMapper;
	
	@Inject
	private PriceTrendProductCompetitorRepository priceTrendProductCompetitorRepository;

	/**
	 * Save a priceTrendProduct.
	 * 
	 * @param priceTrendProductDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public PriceTrendProductDTO save(PriceTrendProductDTO priceTrendProductDTO) {
		log.debug("Request to save PriceTrendProduct : {}", priceTrendProductDTO);
		// set pid
		priceTrendProductDTO.setPid(PriceTrendProductService.PID_PREFIX + RandomUtil.generatePid());
		PriceTrendProduct priceTrendProduct = priceTrendProductMapper
				.priceTrendProductDTOToPriceTrendProduct(priceTrendProductDTO);
		// set company
		priceTrendProduct.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		priceTrendProduct = priceTrendProductRepository.save(priceTrendProduct);
		PriceTrendProductDTO result = priceTrendProductMapper
				.priceTrendProductToPriceTrendProductDTO(priceTrendProduct);
		return result;
	}

	/**
	 * Update a priceTrendProduct.
	 * 
	 * @param priceTrendProductDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public PriceTrendProductDTO update(PriceTrendProductDTO priceTrendProductDTO) {
		log.debug("Request to Update PriceTrendProduct : {}", priceTrendProductDTO);
		return priceTrendProductRepository.findOneByPid(priceTrendProductDTO.getPid()).map(priceTrendProduct -> {
			priceTrendProduct.setName(priceTrendProductDTO.getName());
			priceTrendProduct.setAlias(priceTrendProductDTO.getAlias());
			priceTrendProduct.setDescription(priceTrendProductDTO.getDescription());
			priceTrendProduct = priceTrendProductRepository.save(priceTrendProduct);
			PriceTrendProductDTO result = priceTrendProductMapper
					.priceTrendProductToPriceTrendProductDTO(priceTrendProduct);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the priceTrendProducts.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<PriceTrendProduct> findAll(Pageable pageable) {
		log.debug("Request to get all PriceTrendProducts");
		Page<PriceTrendProduct> result = priceTrendProductRepository.findAll(pageable);
		return result;
	}

	@Override
	public List<PriceTrendProductDTO> findAllByCompany() {
		log.debug("Request to get all PriceTrendProducts");
		List<PriceTrendProduct> priceTrendProductList = priceTrendProductRepository.findAllByCompanyId();
		List<PriceTrendProductDTO> result = priceTrendProductMapper
				.priceTrendProductsToPriceTrendProductDTOs(priceTrendProductList);
		return result;
	}

	/**
	 * Get all the priceTrendProducts.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<PriceTrendProductDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all PriceTrendProducts");
		Page<PriceTrendProduct> priceTrendProducts = priceTrendProductRepository
				.findAllByCompanyIdOrderByPriceTrendProductName(pageable);
		Page<PriceTrendProductDTO> result = new PageImpl<PriceTrendProductDTO>(
				priceTrendProductMapper.priceTrendProductsToPriceTrendProductDTOs(priceTrendProducts.getContent()),
				pageable, priceTrendProducts.getTotalElements());
		return result;
	}

	/**
	 * Get one priceTrendProduct by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public PriceTrendProductDTO findOne(Long id) {
		log.debug("Request to get PriceTrendProduct : {}", id);
		PriceTrendProduct priceTrendProduct = priceTrendProductRepository.findOne(id);
		PriceTrendProductDTO priceTrendProductDTO = priceTrendProductMapper
				.priceTrendProductToPriceTrendProductDTO(priceTrendProduct);
		return priceTrendProductDTO;
	}

	/**
	 * Get one priceTrendProduct by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PriceTrendProductDTO> findOneByPid(String pid) {
		log.debug("Request to get PriceTrendProduct by pid : {}", pid);
		return priceTrendProductRepository.findOneByPid(pid).map(priceTrendProduct -> {
			PriceTrendProductDTO priceTrendProductDTO = priceTrendProductMapper
					.priceTrendProductToPriceTrendProductDTO(priceTrendProduct);
			return priceTrendProductDTO;
		});
	}

	/**
	 * Get one priceTrendProduct by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PriceTrendProductDTO> findByName(String name) {
		log.debug("Request to get PriceTrendProduct by name : {}", name);
		return priceTrendProductRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(priceTrendProduct -> {
					PriceTrendProductDTO priceTrendProductDTO = priceTrendProductMapper
							.priceTrendProductToPriceTrendProductDTO(priceTrendProduct);
					return priceTrendProductDTO;
				});
	}

	/**
	 * Delete the priceTrendProduct by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete PriceTrendProduct : {}", pid);
		priceTrendProductRepository.findOneByPid(pid).ifPresent(priceTrendProduct -> {
			priceTrendProductRepository.delete(priceTrendProduct.getId());
		});
	}

	/**
	 * Update the priceTrendProduct by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public PriceTrendProductDTO updatePriceTrendProductStatus(String pid, boolean active) {
		log.debug("Request to update PriceTrendProduct status");
		return priceTrendProductRepository.findOneByPid(pid).map(priceTrendProduct -> {
			if(!active){
				priceTrendProductCompetitorRepository.deleteByPriceTrendProductPid(pid);
			}
			priceTrendProduct.setActivated(active);
			priceTrendProduct = priceTrendProductRepository.save(priceTrendProduct);
			PriceTrendProductDTO result = priceTrendProductMapper
					.priceTrendProductToPriceTrendProductDTO(priceTrendProduct);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all priceTrendProductDTO from PriceTrendProduct by status and
	 *        company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	@Override
	public List<PriceTrendProductDTO> findAllByCompanyIdAndPriceTrendProductActivated(boolean active) {
		List<PriceTrendProduct> priceTrendProducts = priceTrendProductRepository
				.findAllByCompanyIdAndPriceTrendProductActivatedOrDeactivated(active);
		List<PriceTrendProductDTO> priceTrendProductDTOs = priceTrendProductMapper
				.priceTrendProductsToPriceTrendProductDTOs(priceTrendProducts);
		return priceTrendProductDTOs;
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
	public Page<PriceTrendProductDTO> findAllByCompanyAndPriceTrendProductActivatedOrderByName(Pageable pageable,
			boolean active) {
		Page<PriceTrendProduct> pagePriceTrendProduct = priceTrendProductRepository
				.findAllByCompanyIdAndActivatedOrderByPriceTrendProductName(pageable, active);
		Page<PriceTrendProductDTO> pagePriceTrendProductDTOPage = new PageImpl<PriceTrendProductDTO>(
				priceTrendProductMapper.priceTrendProductsToPriceTrendProductDTOs(pagePriceTrendProduct.getContent()),
				pageable, pagePriceTrendProduct.getTotalElements());
		return pagePriceTrendProductDTOPage;
	}

	@Override
	public List<PriceTrendProductDTO> findAllByCompanyIdAndPriceTrendProductActivatedAndLastModifiedDate(boolean active,LocalDateTime lastModifiedDate) {
		List<PriceTrendProduct> priceTrendProducts = priceTrendProductRepository
				.findAllByCompanyIdAndPriceTrendProductActivatedOrDeactivatedAndLastModifiedDate(active,lastModifiedDate);
		List<PriceTrendProductDTO> priceTrendProductDTOs = priceTrendProductMapper
				.priceTrendProductsToPriceTrendProductDTOs(priceTrendProducts);
		return priceTrendProductDTOs;
	}
}
