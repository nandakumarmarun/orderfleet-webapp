package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ProductNameTextSettings;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ProductNameTextSettingsRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ProductNameTextSettingsService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ProductNameTextSettingsDTO;

/**
 * Service Implementation for managing ProductNameTextSettings.
 * 
 * @author Muhammed Riyas T
 * @since Dec 29, 2016
 */
@Service
@Transactional
public class ProductNameTextSettingsServiceImpl implements ProductNameTextSettingsService {

	private final Logger log = LoggerFactory.getLogger(ProductNameTextSettingsServiceImpl.class);

	@Inject
	private ProductNameTextSettingsRepository productNameTextSettingsRepository;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a productNameTextSettings.
	 * 
	 * @param productNameTextSettingsDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public void save(List<ProductNameTextSettingsDTO> productNameTextSettingsDTOs) {
		log.debug("Request to save ProductNameTextSettings : {}", productNameTextSettingsDTOs);
		for (ProductNameTextSettingsDTO productNameTextSettingsDTO : productNameTextSettingsDTOs) {
			ProductNameTextSettings productNameTextSettings = productNameTextSettingsRepository
					.findOneByPid(productNameTextSettingsDTO.getPid()).get();
			productNameTextSettings.setEnabled(productNameTextSettingsDTO.getEnabled());
			productNameTextSettingsRepository.save(productNameTextSettings);
		}
	}

	/**
	 * Get all the productNameTextSettingss.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProductNameTextSettingsDTO> findAllByCompany() {
		log.debug("Request to get all ProductNameTextSettings");
		List<ProductNameTextSettings> productNameTextSettingss = productNameTextSettingsRepository.findAllByCompanyId();
		List<ProductNameTextSettingsDTO> result = productNameTextSettingss.stream().map(ProductNameTextSettingsDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one productNameTextSettings by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductNameTextSettingsDTO> findOneByPid(String pid) {
		log.debug("Request to get ProductNameTextSettings by pid : {}", pid);
		return productNameTextSettingsRepository.findOneByPid(pid).map(productNameTextSettings -> {
			ProductNameTextSettingsDTO productNameTextSettingsDTO = new ProductNameTextSettingsDTO(
					productNameTextSettings);
			return productNameTextSettingsDTO;
		});
	}

	/**
	 * Get one productNameTextSettings by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ProductNameTextSettingsDTO> findByName(String name) {
		log.debug("Request to get ProductNameTextSettings by name : {}", name);
		return productNameTextSettingsRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(productNameTextSettings -> {
					ProductNameTextSettingsDTO productNameTextSettingsDTO = new ProductNameTextSettingsDTO(
							productNameTextSettings);
					return productNameTextSettingsDTO;
				});
	}

	@Override
	public void saveDefault(List<ProductNameTextSettingsDTO> productNameTextSettingsDTOs) {
		log.debug("Request to save ProductNameTextSettings : {}", productNameTextSettingsDTOs);
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		for (ProductNameTextSettingsDTO productNameTextSettingsDTO : productNameTextSettingsDTOs) {
			Optional<ProductNameTextSettings> opProductNameTextSettings = productNameTextSettingsRepository
					.findByCompanyIdAndNameIgnoreCase(companyId, productNameTextSettingsDTO.getName());
			if (!opProductNameTextSettings.isPresent()) {
				ProductNameTextSettings productNameTextSetting = new ProductNameTextSettings();
				productNameTextSetting.setName(productNameTextSettingsDTO.getName());
				productNameTextSetting.setPid(ProductNameTextSettingsService.PID_PREFIX + RandomUtil.generatePid());
				productNameTextSetting.setCompany(companyRepository.findOne(companyId));
				productNameTextSettingsRepository.save(productNameTextSetting);
			}
		}
	}

}
