package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;

/**
 * Mapper for the entity Activity and its DTO ActivityDTO.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
@Mapper(componentModel = "spring", uses = { AccountTypeMapper.class, DocumentMapper.class })
public interface ActivityMapper {

	ActivityDTO activityToActivityDTO(Activity activity);

	List<ActivityDTO> activitiesToActivityDTOs(List<Activity> activities);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	Activity activityDTOToActivity(ActivityDTO activityDTO);

	List<Activity> activityDTOsToActivities(List<ActivityDTO> activityDTOs);

}
