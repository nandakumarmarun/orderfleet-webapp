package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;

/**
 * Mapper for the entity PriceTrendProduct and its DTO PriceTrendProductDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface PriceTrendProductMapper {

	PriceTrendProductDTO priceTrendProductToPriceTrendProductDTO(PriceTrendProduct priceTrendProduct);

	List<PriceTrendProductDTO> priceTrendProductsToPriceTrendProductDTOs(List<PriceTrendProduct> priceTrendProducts);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	PriceTrendProduct priceTrendProductDTOToPriceTrendProduct(PriceTrendProductDTO priceTrendProductDTO);

	List<PriceTrendProduct> priceTrendProductDTOsToPriceTrendProducts(List<PriceTrendProductDTO> priceTrendProductDTOs);

}
