package com.orderfleet.webapp.web.ecom.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;

/**
 * Mapper for the entity EcomProductProfile and its DTO EcomProductProfileDTO.
 * 
 * @author Sarath
 * @since Sep 23, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface EcomProductProfileMapper {

	@Mapping(target = "productGroupPid", ignore = true)
	@Mapping(target = "productGroupName", ignore = true)
	@Mapping(target = "productProfiles", ignore = true)
	EcomProductProfileDTO ecomProductProfileToEcomProductProfileDTO(EcomProductProfile ecomProductProfile);

	List<EcomProductProfileDTO> ecomProductProfilesToEcomProductProfileDTOs(List<EcomProductProfile> ecomProductProfiles);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	EcomProductProfile ecomProductProfileDTOToEcomProductProfile(EcomProductProfileDTO ecomProductProfileDTO);

	List<EcomProductProfile> ecomProductProfileDTOsToEcomProductProfiles(
			List<EcomProductProfileDTO> ecomProductProfileDTOs);
}
