package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;

/**
 * Mapper for the entity PriceTrendProduct and its DTO PriceTrendProductDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Component
public abstract class PriceTrendProductMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract PriceTrendProductDTO priceTrendProductToPriceTrendProductDTO(PriceTrendProduct priceTrendProduct);

	public abstract	List<PriceTrendProductDTO> priceTrendProductsToPriceTrendProductDTOs(List<PriceTrendProduct> priceTrendProducts);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract PriceTrendProduct priceTrendProductDTOToPriceTrendProduct(PriceTrendProductDTO priceTrendProductDTO);

	public abstract List<PriceTrendProduct> priceTrendProductDTOsToPriceTrendProducts(List<PriceTrendProductDTO> priceTrendProductDTOs);

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
