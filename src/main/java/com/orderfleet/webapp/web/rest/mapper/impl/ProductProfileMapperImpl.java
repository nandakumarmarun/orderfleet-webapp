package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.Units;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;

@Component
public class ProductProfileMapperImpl extends ProductProfileMapper {

	@Override
	public ProductProfileDTO productProfileToProductProfileDTO(ProductProfile productProfile) {
		if (productProfile == null) {
			return null;
		}

		ProductProfileDTO productProfileDTO = new ProductProfileDTO();

		productProfileDTO.setProductCategoryName(productProfileProductCategoryName(productProfile));
		productProfileDTO.setUnitsPid(productProfileUnitsPid(productProfile));
		productProfileDTO.setDivisionName(productProfileDivisionName(productProfile));
		productProfileDTO.setProductCategoryPid(productProfileProductCategoryPid(productProfile));
		productProfileDTO.setDivisionPid(productProfileDivisionPid(productProfile));
		productProfileDTO.setUnitsName(productProfileUnitsName(productProfile));
		productProfileDTO.setActivated(productProfile.getActivated());
		productProfileDTO.setAlias(productProfile.getAlias());
		productProfileDTO.setBarcode(productProfile.getBarcode());
		if (productProfile.getColorImage() != null) {
			byte[] colorImage = productProfile.getColorImage();
			productProfileDTO.setColorImage(Arrays.copyOf(colorImage, colorImage.length));
		}
		productProfileDTO.setColorImageContentType(productProfile.getColorImageContentType());
		productProfileDTO.setCompoundUnitQty(productProfile.getCompoundUnitQty());
		productProfileDTO.setCreatedDate(productProfile.getCreatedDate());
		productProfileDTO.setDefaultLedger(productProfile.getDefaultLedger());
		productProfileDTO.setDescription(productProfile.getDescription());
		productProfileDTO.setHsnCode(productProfile.getHsnCode());
		productProfileDTO.setLastModifiedDate(productProfile.getLastModifiedDate());
		productProfileDTO.setMrp(productProfile.getMrp());
		productProfileDTO.setName(productProfile.getName());
		productProfileDTO.setPid(productProfile.getPid());
		productProfileDTO.setPrice(productProfile.getPrice());
		productProfileDTO.setProductCode(productProfile.getProductCode());
		productProfileDTO.setProductDescription(productProfile.getProductDescription());
		productProfileDTO.setProductGroup(productProfile.getProductGroup());
		productProfileDTO.setProductId(productProfile.getProductId());
		productProfileDTO.setPurchaseCost(productProfile.getPurchaseCost());
		productProfileDTO.setRemarks(productProfile.getRemarks());
		productProfileDTO.setSize(productProfile.getSize());
		productProfileDTO.setSku(productProfile.getSku());
		productProfileDTO.setStockAvailabilityStatus(productProfile.getStockAvailabilityStatus());
		productProfileDTO.setTaxRate(productProfile.getTaxRate());
		productProfileDTO.setCessTaxRate(productProfile.getCessTaxRate());
		productProfileDTO.setDiscountPercentage(productProfile.getDiscountPercentage());
		productProfileDTO.setTrimChar(productProfile.getTrimChar());
		productProfileDTO.setUnitQty(productProfile.getUnitQty());
		if (productProfile.getFiles() != null || !productProfile.getFiles().isEmpty()) {
			String filePids = productProfile.getFiles().stream().map(fileDTO -> fileDTO.getPid())
					.collect(Collectors.joining(","));
			productProfileDTO.setFilesPid(filePids);
		}
		productProfileDTO.setItemWidth(productProfile.getWidth());
		productProfileDTO.setBaseUnits(productProfile.getBaseUnits());
		return productProfileDTO;
	}

	public ProductProfileDTO productProfileToProductProfileDTODescription(ProductProfile productProfile) {
		if (productProfile == null) {
			return null;
		}

		ProductProfileDTO productProfileDTO = new ProductProfileDTO();

		productProfileDTO.setProductCategoryName(productProfileProductCategoryName(productProfile));
		productProfileDTO.setUnitsPid(productProfileUnitsPid(productProfile));
		productProfileDTO.setDivisionName(productProfileDivisionName(productProfile));
		productProfileDTO.setProductCategoryPid(productProfileProductCategoryPid(productProfile));
		productProfileDTO.setDivisionPid(productProfileDivisionPid(productProfile));
		productProfileDTO.setUnitsName(productProfileUnitsName(productProfile));
		productProfileDTO.setActivated(productProfile.getActivated());
		productProfileDTO.setAlias(productProfile.getAlias());
		productProfileDTO.setBarcode(productProfile.getBarcode());
		if (productProfile.getColorImage() != null) {
			byte[] colorImage = productProfile.getColorImage();
			productProfileDTO.setColorImage(Arrays.copyOf(colorImage, colorImage.length));
		}
		productProfileDTO.setColorImageContentType(productProfile.getColorImageContentType());
		productProfileDTO.setCompoundUnitQty(productProfile.getCompoundUnitQty());
		productProfileDTO.setCreatedDate(productProfile.getCreatedDate());
		productProfileDTO.setDefaultLedger(productProfile.getDefaultLedger());
		productProfileDTO.setDescription(productProfile.getDescription());
		productProfileDTO.setHsnCode(productProfile.getHsnCode());
		productProfileDTO.setLastModifiedDate(productProfile.getLastModifiedDate());
		productProfileDTO.setMrp(productProfile.getMrp());
		productProfileDTO.setName(
				productProfile.getProductDescription() != null && !productProfile.getProductDescription().equalsIgnoreCase("common")
						? productProfile.getProductDescription()
						: productProfile.getName());
		productProfileDTO.setPid(productProfile.getPid());
		productProfileDTO.setPrice(productProfile.getPrice());
		productProfileDTO.setProductCode(productProfile.getProductCode());
		productProfileDTO.setProductDescription(productProfile.getProductDescription());
		productProfileDTO.setProductGroup(productProfile.getProductGroup());
		productProfileDTO.setProductId(productProfile.getProductId());
		productProfileDTO.setPurchaseCost(productProfile.getPurchaseCost());
		productProfileDTO.setRemarks(productProfile.getRemarks());
		productProfileDTO.setSize(productProfile.getSize());
		productProfileDTO.setSku(productProfile.getSku());
		productProfileDTO.setStockAvailabilityStatus(productProfile.getStockAvailabilityStatus());
		productProfileDTO.setTaxRate(productProfile.getTaxRate());
		productProfileDTO.setCessTaxRate(productProfile.getCessTaxRate());
		productProfileDTO.setTrimChar(productProfile.getTrimChar());
		productProfileDTO.setUnitQty(productProfile.getUnitQty());
		productProfileDTO.setDiscountPercentage(productProfile.getDiscountPercentage());
		productProfileDTO.setItemWidth(productProfile.getWidth());
		productProfileDTO.setBaseUnits(productProfile.getBaseUnits());

		return productProfileDTO;
	}

	@Override
	public List<ProductProfileDTO> productProfilesToProductProfileDTOs(List<ProductProfile> productProfiles) {
		if (productProfiles == null) {
			return null;
		}

		List<ProductProfileDTO> list = new ArrayList<ProductProfileDTO>();

		if (getCompanyCofig()) {
			for (ProductProfile productProfile : productProfiles) {
				list.add(productProfileToProductProfileDTODescription(productProfile));
			}
		} else {
			for (ProductProfile productProfile : productProfiles) {
				list.add(productProfileToProductProfileDTO(productProfile));
			}
		}

		return list;
	}

	@Override
	public ProductProfile productProfileDTOToProductProfile(ProductProfileDTO productProfileDTO) {
		if (productProfileDTO == null) {
			return null;
		}

		ProductProfile productProfile = new ProductProfile();

		productProfile.setDivision(divisionFromPid(productProfileDTO.getDivisionPid()));
		productProfile.setProductCategory(productCategoryFromPid(productProfileDTO.getProductCategoryPid()));
		productProfile.setActivated(productProfileDTO.getActivated());
		productProfile.setAlias(productProfileDTO.getAlias());
		productProfile.setBarcode(productProfileDTO.getBarcode());
		if (productProfileDTO.getColorImage() != null) {
			byte[] colorImage = productProfileDTO.getColorImage();
			productProfile.setColorImage(Arrays.copyOf(colorImage, colorImage.length));
		}
		productProfile.setColorImageContentType(productProfileDTO.getColorImageContentType());
		productProfile.setCompoundUnitQty(productProfileDTO.getCompoundUnitQty());
		productProfile.setDefaultLedger(productProfileDTO.getDefaultLedger());
		productProfile.setDescription(productProfileDTO.getDescription());
		productProfile.setHsnCode(productProfileDTO.getHsnCode());
		productProfile.setLastModifiedDate(productProfileDTO.getLastModifiedDate());
		productProfile.setMrp(productProfileDTO.getMrp());
		productProfile.setName(productProfileDTO.getName());
		productProfile.setPid(productProfileDTO.getPid());
		productProfile.setPrice(productProfileDTO.getPrice());
		productProfile.setProductCode(productProfileDTO.getProductCode());
		productProfile.setProductDescription(productProfileDTO.getProductDescription());
		productProfile.setProductGroup(productProfileDTO.getProductGroup());
		productProfile.setProductId(productProfileDTO.getProductId());
		productProfile.setPurchaseCost(productProfileDTO.getPurchaseCost());
		productProfile.setRemarks(productProfileDTO.getRemarks());
		productProfile.setSize(productProfileDTO.getSize());
		productProfile.setSku(productProfileDTO.getSku());
		productProfile.setStockAvailabilityStatus(productProfileDTO.getStockAvailabilityStatus());
		productProfile.setTaxRate(productProfileDTO.getTaxRate());
		productProfile.setCessTaxRate(productProfileDTO.getCessTaxRate());
		productProfile.setTrimChar(productProfileDTO.getTrimChar());
		productProfile.setUnitQty(productProfileDTO.getUnitQty());
		productProfile.setDiscountPercentage(productProfileDTO.getDiscountPercentage());

		return productProfile;
	}

	@Override
	public List<ProductProfile> productProfileDTOsToProductProfiles(List<ProductProfileDTO> productProfileDTOs) {
		if (productProfileDTOs == null) {
			return null;
		}

		List<ProductProfile> list = new ArrayList<ProductProfile>();
		for (ProductProfileDTO productProfileDTO : productProfileDTOs) {
			list.add(productProfileDTOToProductProfile(productProfileDTO));
		}

		return list;
	}

	private String productProfileProductCategoryName(ProductProfile productProfile) {

		if (productProfile == null) {
			return null;
		}
		ProductCategory productCategory = productProfile.getProductCategory();
		if (productCategory == null) {
			return null;
		}
		String name = productCategory.getName();
		if (name == null) {
			return null;
		}
		if (productCategory.getDescription() != null && getCompanyCofig()
				&& !productCategory.getDescription().equals("common")) {

			return productCategory.getDescription();
		}
		return name;
	}

	private String productProfileUnitsPid(ProductProfile productProfile) {

		if (productProfile == null) {
			return null;
		}
		Units units = productProfile.getUnits();
		if (units == null) {
			return null;
		}
		String pid = units.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String productProfileDivisionName(ProductProfile productProfile) {

		if (productProfile == null) {
			return null;
		}
		Division division = productProfile.getDivision();
		if (division == null) {
			return null;
		}
		String name = division.getName();
		if (name == null) {
			return null;
		}
		return name;
	}

	private String productProfileProductCategoryPid(ProductProfile productProfile) {

		if (productProfile == null) {
			return null;
		}
		ProductCategory productCategory = productProfile.getProductCategory();
		if (productCategory == null) {
			return null;
		}
		String pid = productCategory.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String productProfileDivisionPid(ProductProfile productProfile) {

		if (productProfile == null) {
			return null;
		}
		Division division = productProfile.getDivision();
		if (division == null) {
			return null;
		}
		String pid = division.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String productProfileUnitsName(ProductProfile productProfile) {

		if (productProfile == null) {
			return null;
		}
		Units units = productProfile.getUnits();
		if (units == null) {
			return null;
		}
		String name = units.getName();
		if (name == null) {
			return null;
		}
		return name;
	}

	private String productProfileName(ProductProfile productProfile) {
		if (productProfile.getProductDescription() != null && getCompanyCofig()
				&& !productProfile.getProductDescription().equals("common")) {
			return productProfile.getProductDescription();
		}

		return productProfile.getName();
	}

}
