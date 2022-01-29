package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;

/**
 * Mapper for the entity Location and its DTO LocationDTO.
 * 
 * @author Shaheer
 * @since May 26, 2016
 */
@Component
public abstract class LocationMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	public abstract	LocationDTO locationToLocationDTO(Location location);

	public abstract	List<LocationDTO> locationsToLocationDTOs(List<Location> locations);

//	@Mapping(target = "company", ignore = true)
	public abstract	Location locationDTOToLocation(LocationDTO locationDTO);

	public abstract	List<Location> locationDTOsToLocations(List<LocationDTO> locationDTOs);
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
