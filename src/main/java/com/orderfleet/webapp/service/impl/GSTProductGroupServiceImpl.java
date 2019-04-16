package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.GSTProductGroup;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.GSTProductGroupRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.GSTProductGroupService;
import com.orderfleet.webapp.web.rest.integration.dto.GSTProductGroupDTO;

/**
 * Service Implementation for managing GSTProductGroup.
 *
 * @author Sarath
 * @since Jul 11, 2017
 *
 */
@Service
@Transactional
public class GSTProductGroupServiceImpl implements GSTProductGroupService {

	private final Logger log = LoggerFactory.getLogger(GSTProductGroupServiceImpl.class);

	@Inject
	private GSTProductGroupRepository gstProductGroupRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public GSTProductGroupDTO save(GSTProductGroupDTO gstProductGroupDTO) {
		log.debug("Request to save GSTproductgroups");
		GSTProductGroup gstProductGroup = new GSTProductGroup();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		ProductGroup productGroup = productGroupRepository
				.findByCompanyIdAndNameIgnoreCase(company.getId(), gstProductGroupDTO.getProductGroupName()).get();
		gstProductGroup.setProductGroup(productGroup);
		gstProductGroup.setHsnsacCode(gstProductGroupDTO.getHsnsacCode());
		gstProductGroup.setApplyDate(gstProductGroupDTO.getApplyDate());
		gstProductGroup.setCentralTax(gstProductGroupDTO.getCentralTax());
		gstProductGroup.setStateTax(gstProductGroupDTO.getStateTax());
		gstProductGroup.setIntegratedTax(gstProductGroupDTO.getIntegratedTax());
		gstProductGroup.setAditionalCess(gstProductGroupDTO.getAditionalCess());
		gstProductGroup.setTaxType(gstProductGroupDTO.getTaxType());

		gstProductGroup = gstProductGroupRepository.save(gstProductGroup);
		GSTProductGroupDTO result = new GSTProductGroupDTO(gstProductGroup);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<GSTProductGroupDTO> findAllByCompany() {
		log.debug("Request to get all GSTproductgroups");
		List<GSTProductGroup> gstProductGroups = gstProductGroupRepository.findAllByCompanyId();
		List<GSTProductGroupDTO> result = gstProductGroups.stream().map(GSTProductGroupDTO::new)
				.collect(Collectors.toList());
		return result;
	}

}
