package com.orderfleet.webapp.web.rest.mapper;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity LocationHierarchy and its DTO LocationHierarchyDTO.
 * 
 * @author Shaheer
 * @since May 26, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface LocationHierarchyMapper {

    @Mapping(source = "location.id", target = "locationId")
    @Mapping(source = "location.name", target = "locationName")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.name", target = "parentName")
    LocationHierarchyDTO locationHierarchyToLocationHierarchyDTO(LocationHierarchy locationHierarchy);

    List<LocationHierarchyDTO> locationHierarchiesToLocationHierarchyDTOs(List<LocationHierarchy> locationHierarchies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "activated", ignore = true)
    @Mapping(target = "activatedDate", ignore = true)
    @Mapping(target = "inactivatedDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(source = "locationId", target = "location")
    @Mapping(source = "parentId", target = "parent")
    LocationHierarchy locationHierarchyDTOToLocationHierarchy(LocationHierarchyDTO locationHierarchyDTO);

    List<LocationHierarchy> locationHierarchyDTOsToLocationHierarchies(List<LocationHierarchyDTO> locationHierarchyDTOs);

    default Location locationFromId(Long id) {
        if (id == null) {
            return null;
        }
        Location location = new Location();
        location.setId(id);
        return location;
    }
}
