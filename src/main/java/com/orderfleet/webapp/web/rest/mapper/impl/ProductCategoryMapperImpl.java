package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductCategoryMapper;

@Component
public class ProductCategoryMapperImpl extends ProductCategoryMapper {

	@Override
	public ProductCategoryDTO productCategoryToProductCategoryDTO(ProductCategory productCategory) {
		if (productCategory == null) {
			return null;
		}

		ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();

		productCategoryDTO.setPid(productCategory.getPid());
		productCategoryDTO.setName(productCategory.getName());
		productCategoryDTO.setAlias(productCategory.getAlias());
		productCategoryDTO.setDescription(productCategory.getDescription());
		productCategoryDTO.setActivated(productCategory.getActivated());
		productCategoryDTO.setThirdpartyUpdate(productCategory.getThirdpartyUpdate());
		productCategoryDTO.setProductCategoryId(productCategory.getProductCategoryId());
		productCategoryDTO.setLastModifiedDate(productCategory.getLastModifiedDate());

		return productCategoryDTO;
	}

	public ProductCategoryDTO productCategoryToProductCategoryDTODescription(ProductCategory productCategory) {
		if (productCategory == null) {
			return null;
		}

		ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();

		productCategoryDTO.setPid(productCategory.getPid());
		productCategoryDTO.setName(
				productCategory.getDescription() != null && !productCategory.getDescription().equalsIgnoreCase("common")
						? productCategory.getDescription()
						: productCategory.getName());
		productCategoryDTO.setAlias(productCategory.getAlias());
		productCategoryDTO.setDescription(productCategory.getDescription());
		productCategoryDTO.setActivated(productCategory.getActivated());
		productCategoryDTO.setThirdpartyUpdate(productCategory.getThirdpartyUpdate());
		productCategoryDTO.setProductCategoryId(productCategory.getProductCategoryId());
		productCategoryDTO.setLastModifiedDate(productCategory.getLastModifiedDate());

		return productCategoryDTO;
	}

	@Override
	public List<ProductCategoryDTO> productCategoriesToProductCategoryDTOs(List<ProductCategory> productCategories) {
		if (productCategories == null) {
			return null;
		}

		List<ProductCategoryDTO> list = new ArrayList<ProductCategoryDTO>();

		if (getCompanyCofig()) {
			for (ProductCategory productCategory : productCategories) {
				list.add(productCategoryToProductCategoryDTODescription(productCategory));
			}
		} else {
			for (ProductCategory productCategory : productCategories) {
				list.add(productCategoryToProductCategoryDTO(productCategory));
			}
		}

		return list;
	}

	@Override
	public ProductCategory productCategoryDTOToProductCategory(ProductCategoryDTO productCategoryDTO) {
		if (productCategoryDTO == null) {
			return null;
		}

		ProductCategory productCategory = new ProductCategory();

		productCategory.setActivated(productCategoryDTO.getActivated());
		productCategory.setPid(productCategoryDTO.getPid());
		productCategory.setName(productCategoryDTO.getName());
		productCategory.setAlias(productCategoryDTO.getAlias());
		productCategory.setDescription(productCategoryDTO.getDescription());
		productCategory.setThirdpartyUpdate(productCategoryDTO.getThirdpartyUpdate());
		productCategory.setLastModifiedDate(productCategoryDTO.getLastModifiedDate());
		productCategory.setProductCategoryId(productCategoryDTO.getProductCategoryId());

		return productCategory;
	}

	@Override
	public List<ProductCategory> productCategoryDTOsToProductCategories(List<ProductCategoryDTO> productCategoryDTOs) {
		if (productCategoryDTOs == null) {
			return null;
		}

		List<ProductCategory> list = new ArrayList<ProductCategory>();
		for (ProductCategoryDTO productCategoryDTO : productCategoryDTOs) {
			list.add(productCategoryDTOToProductCategory(productCategoryDTO));
		}

		return list;
	}

	private String productCategoryName(ProductCategory productCategory) {
		if (productCategory.getDescription() != null && getCompanyCofig()
				&& !productCategory.getDescription().equals("common")) {
			return productCategory.getDescription();
		}

		return productCategory.getName();
	}
}
