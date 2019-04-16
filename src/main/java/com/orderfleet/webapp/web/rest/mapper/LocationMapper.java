package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;

/**
 * Mapper for the entity Location and its DTO LocationDTO.
 * 
 * @author Shaheer
 * @since May 26, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface LocationMapper {

	LocationDTO locationToLocationDTO(Location location);

	List<LocationDTO> locationsToLocationDTOs(List<Location> locations);

	@Mapping(target = "company", ignore = true)
	Location locationDTOToLocation(LocationDTO locationDTO);

	List<Location> locationDTOsToLocations(List<LocationDTO> locationDTOs);

}
