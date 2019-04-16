package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.ActivityType;
import com.orderfleet.webapp.web.rest.dto.ActivityTypeDTO;

@Mapper(componentModel = "spring", uses = {})
public interface ActivityTypeMapper {
	
	ActivityTypeDTO activityTypeToActivityTypeDTO(ActivityType activityType);

	List<ActivityTypeDTO> activityTypesToActivityTypeDTOs(List<ActivityType> activityTypes);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	ActivityType activityTypeDTOToActivityType(ActivityTypeDTO activityTypeDTO);

	List<ActivityType> activityTypeDTOsToActivityTypes(List<ActivityTypeDTO> activityTypeDTOs);
}
