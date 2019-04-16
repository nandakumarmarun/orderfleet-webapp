package com.orderfleet.webapp.service.impl;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.GuidedSellingConfig;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.GuidedSellingConfigRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.GuidedSellingConfigService;
import com.orderfleet.webapp.web.rest.dto.GuidedSellingConfigDTO;

/**
 * Service Implementation for managing GuidedSellingConfig.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
@Service
@Transactional
public class GuidedSellingConfigServiceImpl implements GuidedSellingConfigService {

	private final Logger log = LoggerFactory.getLogger(GuidedSellingConfigServiceImpl.class);

	@Inject
	private GuidedSellingConfigRepository guidedSellingConfigRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private DocumentRepository documentRepository;

	/**
	 * Save a guidedSellingConfigs.
	 * 
	 * @param guidedSellingConfigDTOs
	 *            the entity to save
	 */
	@Override
	public void save(GuidedSellingConfigDTO guidedSellingConfigDTO) {
		log.debug("Request to save GuidedSellingConfigs");
		GuidedSellingConfig guidedSellingConfig = guidedSellingConfigRepository.findByCompanyId();
		if (guidedSellingConfig == null) {
			guidedSellingConfig = new GuidedSellingConfig();
			guidedSellingConfig.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		}
		guidedSellingConfig.setGuidedSellingFilterItems(guidedSellingConfigDTO.getGuidedSellingFilterItems());
		if (!guidedSellingConfigDTO.getFavouriteProductGroupPid().equals("no")) {
			guidedSellingConfig.setFavouriteProductGroup(
					productGroupRepository.findOneByPid(guidedSellingConfigDTO.getFavouriteProductGroupPid()).get());
			guidedSellingConfig.setFavouriteItemCompulsory(guidedSellingConfigDTO.getFavouriteItemCompulsory());
		} else {
			guidedSellingConfig.setFavouriteProductGroup(null);
			guidedSellingConfig.setFavouriteItemCompulsory(false);
		}

		if (!guidedSellingConfigDTO.getPromotionProductGroupPid().equals("no")) {
			guidedSellingConfig.setPromotionProductGroup(
					productGroupRepository.findOneByPid(guidedSellingConfigDTO.getPromotionProductGroupPid()).get());
			guidedSellingConfig.setPromotionItemCompulsory(guidedSellingConfigDTO.getPromotionItemCompulsory());
		} else {
			guidedSellingConfig.setPromotionProductGroup(null);
			guidedSellingConfig.setPromotionItemCompulsory(false);
		}
		if (!guidedSellingConfigDTO.getGuidedSellingInfoDocumentPid().equals("no")) {
			guidedSellingConfig.setGuidedSellingInfoDocument(
					documentRepository.findOneByPid(guidedSellingConfigDTO.getGuidedSellingInfoDocumentPid()).get());
		} else {
			guidedSellingConfig.setGuidedSellingInfoDocument(null);
		}
		guidedSellingConfigRepository.save(guidedSellingConfig);
	}

	/**
	 * Get guidedSellingConfig.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public GuidedSellingConfigDTO findByCompany() {
		log.debug("Request to get  GuidedSellingConfigs");
		GuidedSellingConfig guidedSellingConfig = guidedSellingConfigRepository.findByCompanyId();
		GuidedSellingConfigDTO result = null;
		if (guidedSellingConfig != null)
			result = new GuidedSellingConfigDTO(guidedSellingConfig);
		return result;
	}

}
