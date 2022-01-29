package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;

/**
 * Mapper for the entity ProductCategory and its DTO ProductCategoryDTO.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Component
public abstract class ProductCategoryMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract ProductCategoryDTO productCategoryToProductCategoryDTO(ProductCategory productCategory);

public abstract List<ProductCategoryDTO> productCategoriesToProductCategoryDTOs(List<ProductCategory> productCategories);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract ProductCategory productCategoryDTOToProductCategory(ProductCategoryDTO productCategoryDTO);

	public abstract List<ProductCategory> productCategoryDTOsToProductCategories(List<ProductCategoryDTO> productCategoryDTOs);
	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}
	
}
