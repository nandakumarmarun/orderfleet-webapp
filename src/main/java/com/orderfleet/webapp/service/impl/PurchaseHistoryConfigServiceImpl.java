package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.PurchaseHistoryConfig;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PurchaseHistoryConfigRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PurchaseHistoryConfigService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.PurchaseHistoryConfigDTO;

/**
 * Service Implementation for managing PurchaseHistoryConfig.
 * 
 * @author Muhammed Riyas T
 * @since Feb 06, 2017
 */
@Service
@Transactional
public class PurchaseHistoryConfigServiceImpl implements PurchaseHistoryConfigService {

	private final Logger log = LoggerFactory.getLogger(PurchaseHistoryConfigServiceImpl.class);

	@Inject
	private PurchaseHistoryConfigRepository purchaseHistoryConfigRepository;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a purchaseHistoryConfig.
	 * 
	 * @param purchaseHistoryConfigDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public PurchaseHistoryConfigDTO save(PurchaseHistoryConfigDTO purchaseHistoryConfigDTO) {
		log.debug("Request to save PurchaseHistoryConfig : {}", purchaseHistoryConfigDTO);
		PurchaseHistoryConfig purchaseHistoryConfig = new PurchaseHistoryConfig();
		purchaseHistoryConfig.setCreateDynamicLabel(purchaseHistoryConfigDTO.getCreateDynamicLabel());
		purchaseHistoryConfig.setDescription(purchaseHistoryConfigDTO.getDescription());
		purchaseHistoryConfig.setEndMonth(purchaseHistoryConfigDTO.getEndMonth());
		purchaseHistoryConfig.setEndMonthName(purchaseHistoryConfigDTO.getEndMonthName());
		purchaseHistoryConfig.setEndMonthMinus(purchaseHistoryConfigDTO.getEndMonthMinus());
		purchaseHistoryConfig.setEndMonthYearMinus(purchaseHistoryConfigDTO.getEndMonthYearMinus());
		purchaseHistoryConfig.setName(purchaseHistoryConfigDTO.getName());
		purchaseHistoryConfig.setStartMonth(purchaseHistoryConfigDTO.getStartMonth());
		purchaseHistoryConfig.setStartMonthName(purchaseHistoryConfigDTO.getStartMonthName());
		purchaseHistoryConfig.setStartMonthMinus(purchaseHistoryConfigDTO.getStartMonthMinus());
		purchaseHistoryConfig.setStartMonthYearMinus(purchaseHistoryConfigDTO.getStartMonthYearMinus());
		purchaseHistoryConfig.setSortOrder(purchaseHistoryConfigDTO.getSortOrder());
		// set pid
		purchaseHistoryConfig.setPid(PurchaseHistoryConfigService.PID_PREFIX + RandomUtil.generatePid());
		// set company
		purchaseHistoryConfig.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		purchaseHistoryConfig = purchaseHistoryConfigRepository.save(purchaseHistoryConfig);
		PurchaseHistoryConfigDTO result = new PurchaseHistoryConfigDTO(purchaseHistoryConfig);
		return result;
	}

	/**
	 * Update a purchaseHistoryConfig.
	 * 
	 * @param purchaseHistoryConfigDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public PurchaseHistoryConfigDTO update(PurchaseHistoryConfigDTO purchaseHistoryConfigDTO) {
		log.debug("Request to Update PurchaseHistoryConfig : {}", purchaseHistoryConfigDTO);

		return purchaseHistoryConfigRepository.findOneByPid(purchaseHistoryConfigDTO.getPid())
				.map(purchaseHistoryConfig -> {
					purchaseHistoryConfig.setCreateDynamicLabel(purchaseHistoryConfigDTO.getCreateDynamicLabel());
					purchaseHistoryConfig.setDescription(purchaseHistoryConfigDTO.getDescription());
					purchaseHistoryConfig.setEndMonth(purchaseHistoryConfigDTO.getEndMonth());
					purchaseHistoryConfig.setEndMonthName(purchaseHistoryConfigDTO.getEndMonthName());
					purchaseHistoryConfig.setEndMonthMinus(purchaseHistoryConfigDTO.getEndMonthMinus());
					purchaseHistoryConfig.setEndMonthYearMinus(purchaseHistoryConfigDTO.getEndMonthYearMinus());
					purchaseHistoryConfig.setName(purchaseHistoryConfigDTO.getName());
					purchaseHistoryConfig.setStartMonth(purchaseHistoryConfigDTO.getStartMonth());
					purchaseHistoryConfig.setStartMonthName(purchaseHistoryConfigDTO.getStartMonthName());
					purchaseHistoryConfig.setStartMonthMinus(purchaseHistoryConfigDTO.getStartMonthMinus());
					purchaseHistoryConfig.setStartMonthYearMinus(purchaseHistoryConfigDTO.getStartMonthYearMinus());
					purchaseHistoryConfig.setSortOrder(purchaseHistoryConfigDTO.getSortOrder());
					purchaseHistoryConfig = purchaseHistoryConfigRepository.save(purchaseHistoryConfig);
					PurchaseHistoryConfigDTO result = new PurchaseHistoryConfigDTO(purchaseHistoryConfig);
					return result;
				}).orElse(null);
	}

	/**
	 * Get all the purchaseHistoryConfigs.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PurchaseHistoryConfigDTO> findAllByCompany() {
		log.debug("Request to get all PurchaseHistoryConfigs");
		List<PurchaseHistoryConfig> purchaseHistoryConfigList = purchaseHistoryConfigRepository.findAllByCompanyId();
		List<PurchaseHistoryConfigDTO> result = purchaseHistoryConfigList.stream().map(PurchaseHistoryConfigDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one purchaseHistoryConfig by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PurchaseHistoryConfigDTO> findOneByPid(String pid) {
		log.debug("Request to get PurchaseHistoryConfig by pid : {}", pid);
		return purchaseHistoryConfigRepository.findOneByPid(pid).map(purchaseHistoryConfig -> {
			PurchaseHistoryConfigDTO purchaseHistoryConfigDTO = new PurchaseHistoryConfigDTO(purchaseHistoryConfig);
			return purchaseHistoryConfigDTO;
		});
	}

	/**
	 * Get one purchaseHistoryConfig by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PurchaseHistoryConfigDTO> findByName(String name) {
		log.debug("Request to get PurchaseHistoryConfig by name : {}", name);
		return purchaseHistoryConfigRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(purchaseHistoryConfig -> {
					PurchaseHistoryConfigDTO purchaseHistoryConfigDTO = new PurchaseHistoryConfigDTO(
							purchaseHistoryConfig);
					return purchaseHistoryConfigDTO;
				});
	}

	/**
	 * Delete the purchaseHistoryConfig by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete PurchaseHistoryConfig : {}", pid);
		purchaseHistoryConfigRepository.findOneByPid(pid).ifPresent(purchaseHistoryConfig -> {
			purchaseHistoryConfigRepository.delete(purchaseHistoryConfig.getId());
		});
	}


	/**
	 * Get all the purchaseHistoryConfigs.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PurchaseHistoryConfigDTO> findAllByCompanyIdOrderBySortOrderDesc() {
		log.debug("Request to get all PurchaseHistoryConfigs");
		List<PurchaseHistoryConfig> purchaseHistoryConfigList = purchaseHistoryConfigRepository.findAllByCompanyIdOrderBySortOrderAsc();
		List<PurchaseHistoryConfigDTO> result = purchaseHistoryConfigList.stream().map(PurchaseHistoryConfigDTO::new)
				.collect(Collectors.toList());
		return result;
	}
}
