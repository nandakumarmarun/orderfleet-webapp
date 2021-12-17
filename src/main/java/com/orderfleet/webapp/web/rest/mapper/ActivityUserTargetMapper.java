package com.orderfleet.webapp.web.rest.mapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityUserTarget;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.ActivityUserTargetDTO;

/**
 * Mapper for the entity ActivityUserTarget and its DTO ActivityUserTargetDTO.
 * 
 * @author Shaheer
 * @since May 17, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class ActivityUserTargetMapper {
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Activity act= activityRepository.findOneByPid(pid).map(activity -> activity).orElse(null);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
				return act;
	}

	public User userFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return userRepository.findOneByPid(pid).map(user -> user).orElse(null);
	}
}
