package com.orderfleet.webapp.web.rest.mapper;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;

import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

/**
 * Mapper for the entity LocationHierarchy and its DTO LocationHierarchyDTO.
 * 
 * @author Shaheer
 * @since May 26, 2016
 */
@Component
public abstract class LocationHierarchyMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
//    @Mapping(source = "location.id", target = "locationId")
//    @Mapping(source = "location.name", target = "locationName")
//    @Mapping(source = "parent.id", target = "parentId")
//    @Mapping(source = "parent.name", target = "parentName")
	public abstract  LocationHierarchyDTO locationHierarchyToLocationHierarchyDTO(LocationHierarchy locationHierarchy);

	public abstract  List<LocationHierarchyDTO> locationHierarchiesToLocationHierarchyDTOs(List<LocationHierarchy> locationHierarchies);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "company", ignore = true)
//    @Mapping(target = "activated", ignore = true)
//    @Mapping(target = "activatedDate", ignore = true)
//    @Mapping(target = "inactivatedDate", ignore = true)
//    @Mapping(target = "version", ignore = true)
//    @Mapping(source = "locationId", target = "location")
//    @Mapping(source = "parentId", target = "parent")
	public abstract LocationHierarchy locationHierarchyDTOToLocationHierarchy(LocationHierarchyDTO locationHierarchyDTO);

	public abstract List<LocationHierarchy> locationHierarchyDTOsToLocationHierarchies(List<LocationHierarchyDTO> locationHierarchyDTOs);

	public Location locationFromId(Long id) {
        if (id == null) {
            return null;
        }
        Location location = new Location();
        location.setId(id);
        return location;
    }
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
