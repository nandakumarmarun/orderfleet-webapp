package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityUserTarget;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.dto.ActivityUserTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityUserTargetMapper;

@Component
public class ActivityUserTargetMapperImpl extends ActivityUserTargetMapper {

	@Override
	public ActivityUserTargetDTO activityUserTargetToActivityUserTargetDTO(ActivityUserTarget activityUserTarget) {
		if (activityUserTarget == null) {
			return null;
		}

		ActivityUserTargetDTO activityUserTargetDTO = new ActivityUserTargetDTO();

		activityUserTargetDTO.setActivityName(activityUserTargetActivityName(activityUserTarget));
		activityUserTargetDTO.setUserPid(activityUserTargetUserPid(activityUserTarget));
		activityUserTargetDTO.setUserName(activityUserTargetUserFirstName(activityUserTarget));
		activityUserTargetDTO.setActivityPid(activityUserTargetActivityPid(activityUserTarget));
		activityUserTargetDTO.setPid(activityUserTarget.getPid());
		activityUserTargetDTO.setStartDate(activityUserTarget.getStartDate());
		activityUserTargetDTO.setEndDate(activityUserTarget.getEndDate());
		activityUserTargetDTO.setTargetNumber(activityUserTarget.getTargetNumber());

		return activityUserTargetDTO;
	}

	@Override
	public List<ActivityUserTargetDTO> activityUserTargetsToActivityUserTargetDTOs(
			List<ActivityUserTarget> activityUserTargets) {
		if (activityUserTargets == null) {
			return null;
		}

		List<ActivityUserTargetDTO> list = new ArrayList<ActivityUserTargetDTO>();
		for (ActivityUserTarget activityUserTarget : activityUserTargets) {
			list.add(activityUserTargetToActivityUserTargetDTO(activityUserTarget));
		}

		return list;
	}

	@Override
	public ActivityUserTarget activityUserTargetDTOToActivityUserTarget(ActivityUserTargetDTO activityUserTargetDTO) {
		if (activityUserTargetDTO == null) {
			return null;
		}

		ActivityUserTarget activityUserTarget = new ActivityUserTarget();

		activityUserTarget.setActivity(activityFromPid(activityUserTargetDTO.getActivityPid()));
		activityUserTarget.setUser(userFromPid(activityUserTargetDTO.getUserPid()));
		activityUserTarget.setPid(activityUserTargetDTO.getPid());
		activityUserTarget.setStartDate(activityUserTargetDTO.getStartDate());
		activityUserTarget.setEndDate(activityUserTargetDTO.getEndDate());
		activityUserTarget.setTargetNumber(activityUserTargetDTO.getTargetNumber());

		return activityUserTarget;
	}

	@Override
	public List<ActivityUserTarget> activityUserTargetDTOsToActivityUserTargets(
			List<ActivityUserTargetDTO> activityUserTargetDTOs) {
		if (activityUserTargetDTOs == null) {
			return null;
		}

		List<ActivityUserTarget> list = new ArrayList<ActivityUserTarget>();
		for (ActivityUserTargetDTO activityUserTargetDTO : activityUserTargetDTOs) {
			list.add(activityUserTargetDTOToActivityUserTarget(activityUserTargetDTO));
		}

		return list;
	}

	private String activityUserTargetActivityName(ActivityUserTarget activityUserTarget) {

		if (activityUserTarget == null) {
			return null;
		}
		Activity activity = activityUserTarget.getActivity();
		if (activity == null) {
			return null;
		}
		String name = activity.getName();
		if (name == null) {
			return null;
		}

		if (activity.getDescription() != null && getCompanyCofig() && !activity.getDescription().equals("common")) {
			return activity.getDescription();

		}
		return name;
	}

	private String activityUserTargetUserPid(ActivityUserTarget activityUserTarget) {

		if (activityUserTarget == null) {
			return null;
		}
		User user = activityUserTarget.getUser();
		if (user == null) {
			return null;
		}
		String pid = user.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String activityUserTargetUserFirstName(ActivityUserTarget activityUserTarget) {

		if (activityUserTarget == null) {
			return null;
		}
		User user = activityUserTarget.getUser();
		if (user == null) {
			return null;
		}
		String firstName = user.getFirstName();
		if (firstName == null) {
			return null;
		}
		return firstName;
	}

	private String activityUserTargetActivityPid(ActivityUserTarget activityUserTarget) {

		if (activityUserTarget == null) {
			return null;
		}
		Activity activity = activityUserTarget.getActivity();
		if (activity == null) {
			return null;
		}
		String pid = activity.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

}
