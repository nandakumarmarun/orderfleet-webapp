package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Mapper for the entity ProductProfile and its DTO ProductProfileDTO.
 * 
 * @author Shaheer
 * @since May 17, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class ProductProfileMapper {

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Mapping(target = "filesPid", ignore = true)
	@Mapping(source = "division.pid", target = "divisionPid")
	@Mapping(source = "division.name", target = "divisionName")
	@Mapping(source = "productCategory.pid", target = "productCategoryPid")
	@Mapping(source = "productCategory.name", target = "productCategoryName")
//	@Mapping(source = "stockAvailabilityStatus", target = "stockAvailabilityStatus")
	public abstract ProductProfileDTO productProfileToProductProfileDTO(ProductProfile productProfile);

	public abstract List<ProductProfileDTO> productProfilesToProductProfileDTOs(List<ProductProfile> productProfiles);

	@Mapping(target = "files", ignore = true)
	@Mapping(source = "divisionPid", target = "division")
	@Mapping(source = "productCategoryPid", target = "productCategory")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract ProductProfile productProfileDTOToProductProfile(ProductProfileDTO productProfileDTO);

	public abstract List<ProductProfile> productProfileDTOsToProductProfiles(
			List<ProductProfileDTO> productProfileDTOs);

	public Division divisionFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}

		return divisionRepository.findOneByPid(pid).map(division -> division).orElse(null);
	}

	public ProductCategory productCategoryFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return productCategoryRepository.findOneByPid(pid).map(category -> category).orElse(null);
	}
}
