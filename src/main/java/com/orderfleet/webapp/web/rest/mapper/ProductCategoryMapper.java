package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;

/**
 * Mapper for the entity ProductCategory and its DTO ProductCategoryDTO.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductCategoryMapper {

	ProductCategoryDTO productCategoryToProductCategoryDTO(ProductCategory productCategory);

	List<ProductCategoryDTO> productCategoriesToProductCategoryDTOs(List<ProductCategory> productCategories);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	ProductCategory productCategoryDTOToProductCategory(ProductCategoryDTO productCategoryDTO);

	List<ProductCategory> productCategoryDTOsToProductCategories(List<ProductCategoryDTO> productCategoryDTOs);

}
