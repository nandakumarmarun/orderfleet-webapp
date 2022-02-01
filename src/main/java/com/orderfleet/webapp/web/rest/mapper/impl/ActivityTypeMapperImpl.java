package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.ActivityType;
import com.orderfleet.webapp.web.rest.dto.ActivityTypeDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityTypeMapper;

@Component
public class ActivityTypeMapperImpl extends ActivityTypeMapper {

	@Override
	public ActivityTypeDTO activityTypeToActivityTypeDTO(ActivityType activityType) {
		if (activityType == null) {
			return null;
		}

		ActivityTypeDTO activityTypeDTO = new ActivityTypeDTO();

		activityTypeDTO.setPid(activityType.getPid());
		activityTypeDTO.setName(activityType.getName());
		activityTypeDTO.setAlias(activityType.getAlias());
		activityTypeDTO.setDescription(activityType.getDescription());
		activityTypeDTO.setActivated(activityType.isActivated());

		return activityTypeDTO;
	}

	public ActivityTypeDTO activityTypeToActivityTypeDTODescription(ActivityType activityType) {
		if (activityType == null) {
			return null;
		}

		ActivityTypeDTO activityTypeDTO = new ActivityTypeDTO();

		activityTypeDTO.setPid(activityType.getPid());
		activityTypeDTO.setName(
				activityType.getDescription() != null && !activityType.getDescription().equalsIgnoreCase("common")
						? activityType.getDescription()
						: activityType.getName());
		activityTypeDTO.setAlias(activityType.getAlias());
		activityTypeDTO.setDescription(activityType.getDescription());
		activityTypeDTO.setActivated(activityType.isActivated());

		return activityTypeDTO;
	}

	@Override
	public List<ActivityTypeDTO> activityTypesToActivityTypeDTOs(List<ActivityType> activityTypes) {
		if (activityTypes == null) {
			return null;
		}

		List<ActivityTypeDTO> list = new ArrayList<ActivityTypeDTO>();
		if (getCompanyCofig()) {
			for (ActivityType activityType : activityTypes) {
				list.add(activityTypeToActivityTypeDTODescription(activityType));
			}
		} else {
			for (ActivityType activityType : activityTypes) {
				list.add(activityTypeToActivityTypeDTO(activityType));
			}
		}
		return list;
	}

	@Override
	public ActivityType activityTypeDTOToActivityType(ActivityTypeDTO activityTypeDTO) {
		if (activityTypeDTO == null) {
			return null;
		}

		ActivityType activityType = new ActivityType();

		activityType.setPid(activityTypeDTO.getPid());
		activityType.setName(activityTypeDTO.getName());
		activityType.setAlias(activityTypeDTO.getAlias());
		activityType.setDescription(activityTypeDTO.getDescription());
		activityType.setActivated(activityTypeDTO.getActivated());

		return activityType;
	}

	@Override
	public List<ActivityType> activityTypeDTOsToActivityTypes(List<ActivityTypeDTO> activityTypeDTOs) {
		if (activityTypeDTOs == null) {
			return null;
		}

		List<ActivityType> list = new ArrayList<ActivityType>();
		for (ActivityTypeDTO activityTypeDTO : activityTypeDTOs) {
			list.add(activityTypeDTOToActivityType(activityTypeDTO));
		}

		return list;
	}

	private String activityTypeName(ActivityType activityType) {
	        if(activityType.getDescription()!=null && getCompanyCofig() && !activityType.getDescription().equals("common")) {
	        return activityType.getDescription();
	        }

		return activityType.getName();
	}
}
