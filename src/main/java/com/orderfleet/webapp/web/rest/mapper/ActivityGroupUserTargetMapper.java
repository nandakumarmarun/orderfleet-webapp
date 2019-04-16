package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.domain.ActivityGroupUserTarget;
import com.orderfleet.webapp.repository.ActivityGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupUserTargetDTO;

/**
 * Mapper for the entity ActivityGroupUserTarget and its DTO
 * ActivityGroupUserTargetDTO.
 * 
 * @author Shaheer
 * @since May 17, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class ActivityGroupUserTargetMapper {

	@Inject
	private ActivityGroupRepository activityGroupRepository;

	@Inject
	private UserRepository userRepository;

	@Mapping(source = "activityGroup.pid", target = "activityGroupPid")
	@Mapping(source = "activityGroup.name", target = "activityGroupName")
	@Mapping(source = "user.pid", target = "userPid")
	@Mapping(source = "user.firstName", target = "userName")
	public abstract ActivityGroupUserTargetDTO activityGroupUserTargetToActivityGroupUserTargetDTO(
			ActivityGroupUserTarget activityGroupUserTarget);

	public abstract List<ActivityGroupUserTargetDTO> activityGroupUserTargetsToActivityGroupUserTargetDTOs(
			List<ActivityGroupUserTarget> activityGroupUserTargets);

	@Mapping(source = "activityGroupPid", target = "activityGroup")
	@Mapping(source = "userPid", target = "user")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract ActivityGroupUserTarget activityGroupUserTargetDTOToActivityGroupUserTarget(
			ActivityGroupUserTargetDTO activityGroupUserTargetDTO);

	public abstract List<ActivityGroupUserTarget> activityGroupUserTargetDTOsToActivityGroupUserTargets(
			List<ActivityGroupUserTargetDTO> activityGroupUserTargetDTOs);

	public ActivityGroup activityGroupFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return activityGroupRepository.findOneByPid(pid).map(activityGroup -> activityGroup).orElse(null);
	}

	public User userFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return userRepository.findOneByPid(pid).map(user -> user).orElse(null);
	}
}
