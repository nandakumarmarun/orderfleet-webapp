package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.domain.ActivityGroupUserTarget;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityGroupUserTargetMapper;

@Component
public class ActivityGroupUserTargetMapperImpl extends ActivityGroupUserTargetMapper {

	@Override
	public ActivityGroupUserTargetDTO activityGroupUserTargetToActivityGroupUserTargetDTO(
			ActivityGroupUserTarget activityGroupUserTarget) {
		if (activityGroupUserTarget == null) {
			return null;
		}

		ActivityGroupUserTargetDTO activityGroupUserTargetDTO = new ActivityGroupUserTargetDTO();

		activityGroupUserTargetDTO.setUserPid(activityGroupUserTargetUserPid(activityGroupUserTarget));
		activityGroupUserTargetDTO.setUserName(activityGroupUserTargetUserFirstName(activityGroupUserTarget));
		activityGroupUserTargetDTO
				.setActivityGroupName(activityGroupUserTargetActivityGroupName(activityGroupUserTarget));
		activityGroupUserTargetDTO
				.setActivityGroupPid(activityGroupUserTargetActivityGroupPid(activityGroupUserTarget));
		activityGroupUserTargetDTO.setPid(activityGroupUserTarget.getPid());
		activityGroupUserTargetDTO.setStartDate(activityGroupUserTarget.getStartDate());
		activityGroupUserTargetDTO.setEndDate(activityGroupUserTarget.getEndDate());
		activityGroupUserTargetDTO.setTargetNumber(activityGroupUserTarget.getTargetNumber());

		return activityGroupUserTargetDTO;
	}

	@Override
	public List<ActivityGroupUserTargetDTO> activityGroupUserTargetsToActivityGroupUserTargetDTOs(
			List<ActivityGroupUserTarget> activityGroupUserTargets) {
		if (activityGroupUserTargets == null) {
			return null;
		}

		List<ActivityGroupUserTargetDTO> list = new ArrayList<ActivityGroupUserTargetDTO>();
		for (ActivityGroupUserTarget activityGroupUserTarget : activityGroupUserTargets) {
			list.add(activityGroupUserTargetToActivityGroupUserTargetDTO(activityGroupUserTarget));
		}

		return list;
	}

	@Override
	public ActivityGroupUserTarget activityGroupUserTargetDTOToActivityGroupUserTarget(
			ActivityGroupUserTargetDTO activityGroupUserTargetDTO) {
		if (activityGroupUserTargetDTO == null) {
			return null;
		}

		ActivityGroupUserTarget activityGroupUserTarget = new ActivityGroupUserTarget();

		activityGroupUserTarget
				.setActivityGroup(activityGroupFromPid(activityGroupUserTargetDTO.getActivityGroupPid()));
		activityGroupUserTarget.setUser(userFromPid(activityGroupUserTargetDTO.getUserPid()));
		activityGroupUserTarget.setPid(activityGroupUserTargetDTO.getPid());
		activityGroupUserTarget.setStartDate(activityGroupUserTargetDTO.getStartDate());
		activityGroupUserTarget.setEndDate(activityGroupUserTargetDTO.getEndDate());
		activityGroupUserTarget.setTargetNumber(activityGroupUserTargetDTO.getTargetNumber());

		return activityGroupUserTarget;
	}

	@Override
	public List<ActivityGroupUserTarget> activityGroupUserTargetDTOsToActivityGroupUserTargets(
			List<ActivityGroupUserTargetDTO> activityGroupUserTargetDTOs) {
		if (activityGroupUserTargetDTOs == null) {
			return null;
		}

		List<ActivityGroupUserTarget> list = new ArrayList<ActivityGroupUserTarget>();
		for (ActivityGroupUserTargetDTO activityGroupUserTargetDTO : activityGroupUserTargetDTOs) {
			list.add(activityGroupUserTargetDTOToActivityGroupUserTarget(activityGroupUserTargetDTO));
		}

		return list;
	}

	private String activityGroupUserTargetUserPid(ActivityGroupUserTarget activityGroupUserTarget) {

		if (activityGroupUserTarget == null) {
			return null;
		}
		User user = activityGroupUserTarget.getUser();
		if (user == null) {
			return null;
		}
		String pid = user.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String activityGroupUserTargetUserFirstName(ActivityGroupUserTarget activityGroupUserTarget) {

		if (activityGroupUserTarget == null) {
			return null;
		}
		User user = activityGroupUserTarget.getUser();
		if (user == null) {
			return null;
		}
		String firstName = user.getFirstName();
		if (firstName == null) {
			return null;
		}
		return firstName;
	}

	private String activityGroupUserTargetActivityGroupName(ActivityGroupUserTarget activityGroupUserTarget) {

		if (activityGroupUserTarget == null) {
			return null;
		}
		ActivityGroup activityGroup = activityGroupUserTarget.getActivityGroup();
		if (activityGroup == null) {
			return null;
		}
		String name = activityGroup.getName();
		if (name == null) {
			return null;
		}

		if (activityGroup.getDescription() != null && getCompanyCofig()
				&& !activityGroup.getDescription().equals("common")) {
			return activityGroup.getDescription();
		}
		return name;
	}

	private String activityGroupUserTargetActivityGroupPid(ActivityGroupUserTarget activityGroupUserTarget) {

		if (activityGroupUserTarget == null) {
			return null;
		}
		ActivityGroup activityGroup = activityGroupUserTarget.getActivityGroup();
		if (activityGroup == null) {
			return null;
		}
		String pid = activityGroup.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}
}
