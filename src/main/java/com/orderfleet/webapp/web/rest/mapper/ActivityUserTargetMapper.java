package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityUserTarget;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.web.rest.dto.ActivityUserTargetDTO;

/**
 * Mapper for the entity ActivityUserTarget and its DTO ActivityUserTargetDTO.
 * 
 * @author Shaheer
 * @since May 17, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class ActivityUserTargetMapper {

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private UserRepository userRepository;

	@Mapping(source = "activity.pid", target = "activityPid")
	@Mapping(source = "activity.name", target = "activityName")
	@Mapping(source = "user.pid", target = "userPid")
	@Mapping(source = "user.firstName", target = "userName")
	@Mapping(target = "achivedNumber", ignore = true)
	public abstract ActivityUserTargetDTO activityUserTargetToActivityUserTargetDTO(
			ActivityUserTarget activityUserTarget);

	public abstract List<ActivityUserTargetDTO> activityUserTargetsToActivityUserTargetDTOs(
			List<ActivityUserTarget> activityUserTargets);

	@Mapping(source = "activityPid", target = "activity")
	@Mapping(source = "userPid", target = "user")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract ActivityUserTarget activityUserTargetDTOToActivityUserTarget(
			ActivityUserTargetDTO activityUserTargetDTO);

	public abstract List<ActivityUserTarget> activityUserTargetDTOsToActivityUserTargets(
			List<ActivityUserTargetDTO> activityUserTargetDTOs);

	public Activity activityFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return activityRepository.findOneByPid(pid).map(activity -> activity).orElse(null);
	}

	public User userFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return userRepository.findOneByPid(pid).map(user -> user).orElse(null);
	}
}
