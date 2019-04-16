package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;

/**
 * Mapper for the entity ActivityGroup and its DTO ActivityGroupDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 09, 2016
 */
@Mapper(componentModel = "spring", uses = { ActivityMapper.class })
public interface ActivityGroupMapper {

	public abstract ActivityGroupDTO activityGroupToActivityGroupDTO(ActivityGroup activityGroup);

	public abstract List<ActivityGroupDTO> activityGroupsToActivityGroupDTOs(List<ActivityGroup> activityGroups);

	@Mapping(target = "activities", ignore = true)
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract ActivityGroup activityGroupDTOToActivityGroup(ActivityGroupDTO activityGroupDTO);

	public abstract List<ActivityGroup> activityGroupDTOsToActivityGroups(List<ActivityGroupDTO> activityGroupDTOs);

}
