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

import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.repository.ActivityGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.TaskGroupDTO;

/**
 * Mapper for the entity TaskGroup and its DTO TaskGroupDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Mapper(componentModel = "spring", uses = { TaskMapper.class, })
public abstract class TaskGroupMapper {
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ActivityGroupRepository activityGroupRepository;

	@Mapping(source = "activityGroup.pid", target = "activityGroupPid")
	@Mapping(source = "activityGroup.name", target = "activityGroupName")
	public abstract TaskGroupDTO taskGroupToTaskGroupDTO(TaskGroup taskGroup);

	public abstract List<TaskGroupDTO> taskGroupsToTaskGroupDTOs(List<TaskGroup> taskGroups);

	@Mapping(source = "activityGroupPid", target = "activityGroup")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract TaskGroup taskGroupDTOToTaskGroup(TaskGroupDTO taskGroupDTO);

	public abstract List<TaskGroup> taskGroupDTOsToTaskGroups(List<TaskGroupDTO> taskGroupDTOs);

	public ActivityGroup activityGroupFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AG_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			ActivityGroup aGroup= activityGroupRepository.findOneByPid(pid).map(activityGroup -> activityGroup).orElse(null);
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
					return aGroup;

	}

}
