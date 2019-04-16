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

import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.mapper.PriceLevelMapper;

/**
 * Service Implementation for managing PriceLevel.
 *
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
@Service
@Transactional
public class PriceLevelServiceImpl implements PriceLevelService {

	private final Logger log = LoggerFactory.getLogger(PriceLevelServiceImpl.class);

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private PriceLevelMapper priceLevelMapper;

	/**
	 * Save a priceLevel.
	 *
	 * @param priceLevelDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public PriceLevelDTO save(PriceLevelDTO priceLevelDTO) {
		log.debug("Request to save PriceLevel : {}", priceLevelDTO);
		// set pid
		priceLevelDTO.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
		PriceLevel priceLevel = priceLevelMapper.priceLevelDTOToPriceLevel(priceLevelDTO);
		// set company
		priceLevel.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		priceLevel = priceLevelRepository.save(priceLevel);
		PriceLevelDTO result = priceLevelMapper.priceLevelToPriceLevelDTO(priceLevel);
		return result;
	}

	/**
	 * Update a priceLevel.
	 *
	 * @param priceLevelDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public PriceLevelDTO update(PriceLevelDTO priceLevelDTO) {
		log.debug("Request to Update PriceLevel : {}", priceLevelDTO);
		return priceLevelRepository.findOneByPid(priceLevelDTO.getPid()).map(priceLevel -> {
			priceLevel.setName(priceLevelDTO.getName());
			priceLevel.setAlias(priceLevelDTO.getAlias());
			priceLevel.setDescription(priceLevelDTO.getDescription());
			priceLevel.setSortOrder(priceLevelDTO.getSortOrder());
			priceLevel = priceLevelRepository.save(priceLevel);
			PriceLevelDTO result = priceLevelMapper.priceLevelToPriceLevelDTO(priceLevel);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the priceLevels.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<PriceLevel> findAll(Pageable pageable) {
		log.debug("Request to get all PriceLevels");
		Page<PriceLevel> result = priceLevelRepository.findAll(pageable);
		return result;
	}

	@Override
	public List<PriceLevelDTO> findAllByCompany() {
		log.debug("Request to get all PriceLevels");
		List<PriceLevel> priceLevelList = priceLevelRepository.findAllByCompanyId();
		List<PriceLevelDTO> result = priceLevelMapper.priceLevelsToPriceLevelDTOs(priceLevelList);
		return result;
	}

	/**
	 * Get all the priceLevels.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<PriceLevelDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all PriceLevels");
		Page<PriceLevel> priceLevels = priceLevelRepository.findAllByCompanyIdOrderByPriceLevelName(pageable);
		Page<PriceLevelDTO> result = new PageImpl<PriceLevelDTO>(
				priceLevelMapper.priceLevelsToPriceLevelDTOs(priceLevels.getContent()), pageable,
				priceLevels.getTotalElements());
		return result;
	}

	/**
	 * Get one priceLevel by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public PriceLevelDTO findOne(Long id) {
		log.debug("Request to get PriceLevel : {}", id);
		PriceLevel priceLevel = priceLevelRepository.findOne(id);
		PriceLevelDTO priceLevelDTO = priceLevelMapper.priceLevelToPriceLevelDTO(priceLevel);
		return priceLevelDTO;
	}

	/**
	 * Get one priceLevel by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PriceLevelDTO> findOneByPid(String pid) {
		log.debug("Request to get PriceLevel by pid : {}", pid);
		return priceLevelRepository.findOneByPid(pid).map(priceLevel -> {
			PriceLevelDTO priceLevelDTO = priceLevelMapper.priceLevelToPriceLevelDTO(priceLevel);
			return priceLevelDTO;
		});
	}

	/**
	 * Get one priceLevel by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PriceLevelDTO> findByName(String name) {
		log.debug("Request to get PriceLevel by name : {}", name);
		return priceLevelRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(priceLevel -> {
					PriceLevelDTO priceLevelDTO = priceLevelMapper.priceLevelToPriceLevelDTO(priceLevel);
					return priceLevelDTO;
				});
	}

	/**
	 * Delete the priceLevel by id.
	 *
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete PriceLevel : {}", pid);
		priceLevelRepository.findOneByPid(pid).ifPresent(priceLevel -> {
			priceLevelRepository.delete(priceLevel.getId());
		});
	}

	/**
	 * Update the PriceLevel status by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public PriceLevelDTO updatePriceLevelStatus(String pid, boolean active) {
		log.debug("Request to update PriceLevel status");
		return priceLevelRepository.findOneByPid(pid).map(priceLevel -> {
			priceLevel.setActivated(active);
			priceLevel = priceLevelRepository.save(priceLevel);
			PriceLevelDTO result = priceLevelMapper.priceLevelToPriceLevelDTO(priceLevel);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
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
	public Page<PriceLevelDTO> findAllByCompanyIdAndActivatedPriceLevelOrderByName(Pageable pageable, boolean active) {
		log.debug("Request to get Activated PriceLevel ");
		Page<PriceLevel> pagePriceLevel = priceLevelRepository
				.findAllByCompanyIdAndActivatedPriceLevelOrderByName(pageable, active);
		Page<PriceLevelDTO> pagePriceLevelDTO = new PageImpl<PriceLevelDTO>(
				priceLevelMapper.priceLevelsToPriceLevelDTOs(pagePriceLevel.getContent()), pageable,
				pagePriceLevel.getTotalElements());
		return pagePriceLevelDTO;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 *
	 *        find all deactive company
	 *
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<PriceLevelDTO> findAllByCompanyIdAndDeactivatedPriceLevel(boolean deactive) {
		log.debug("Request to get Deactivated PriceLevel ");
		List<PriceLevel> priceLevels = priceLevelRepository.findAllByCompanyIdAndDeactivatedPriceLevel(deactive);
		List<PriceLevelDTO> priceLevelDTOs = priceLevelMapper.priceLevelsToPriceLevelDTOs(priceLevels);
		return priceLevelDTOs;
	}

	/**
	 * Get one priceLevel by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PriceLevelDTO> findByCompanyIdAndName(Long companyId, String name) {
		log.debug("Request to get PriceLevel by name : {}", name);
		return priceLevelRepository.findByCompanyIdAndNameIgnoreCase(companyId, name).map(priceLevel -> {
			PriceLevelDTO priceLevelDTO = priceLevelMapper.priceLevelToPriceLevelDTO(priceLevel);
			return priceLevelDTO;
		});
	}

	/**
	 * Get one priceLevel by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PriceLevelDTO> findByCompanyIdName(Long companyId, String name) {
		log.debug("Request to get PriceLevel by name : {}", name);
		return priceLevelRepository.findByCompanyIdAndNameIgnoreCase(companyId, name).map(priceLevel -> {
			PriceLevelDTO priceLevelDTO = priceLevelMapper.priceLevelToPriceLevelDTO(priceLevel);
			return priceLevelDTO;
		});
	}

	/**
	 * Save a priceLevel.
	 *
	 * @param priceLevelDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public PriceLevelDTO savePriceLevel(Long companyId, PriceLevelDTO priceLevelDTO) {
		log.debug("Request to save PriceLevel : {}", priceLevelDTO);
		// set pid
		priceLevelDTO.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
		PriceLevel priceLevel = priceLevelMapper.priceLevelDTOToPriceLevel(priceLevelDTO);
		// set company
		priceLevel.setCompany(companyRepository.findOne(companyId));
		priceLevel = priceLevelRepository.save(priceLevel);
		PriceLevelDTO result = priceLevelMapper.priceLevelToPriceLevelDTO(priceLevel);
		return result;
	}


	@Override
	@Transactional(readOnly = true)
	public Optional<PriceLevel> findPriceLevelByCompanyIdName(Long companyId, String name) {
		log.debug("Request to get PriceLevel by name : {}", name);
		return priceLevelRepository.findByCompanyIdAndNameIgnoreCase(companyId, name);
	}
}
