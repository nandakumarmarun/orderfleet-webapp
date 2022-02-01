package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;
import com.orderfleet.webapp.web.rest.mapper.TaskMapper;

@Component
public class TaskMapperImpl extends TaskMapper {

	@Override
	public TaskDTO taskToTaskDTO(Task task) {
		if (task == null) {
			return null;
		}

		TaskDTO taskDTO = new TaskDTO();

		taskDTO.setAccountProfilePid(taskAccountProfilePid(task));
		taskDTO.setAccountTypePid(taskAccountTypePid(task));
		taskDTO.setActivityName(taskActivityName(task));
		taskDTO.setAccountTypeName(taskAccountTypeName(task));
		taskDTO.setAccountProfileName(taskAccountProfileName(task));
		taskDTO.setActivityPid(taskActivityPid(task));
		taskDTO.setPid(task.getPid());
		taskDTO.setRemarks(task.getRemarks());
		taskDTO.setActivated(task.getActivated());

		return taskDTO;
	}
	public TaskDTO taskToTaskDTODescription(Task task) {
		if (task == null) {
			return null;
		}

		TaskDTO taskDTO = new TaskDTO();

		taskDTO.setAccountProfilePid(taskAccountProfilePid(task));
		taskDTO.setAccountTypePid(taskAccountTypePid(task));
		taskDTO.setActivityName(taskActivityDescription(task));
		taskDTO.setAccountTypeName(taskAccountTypeDescription(task));
		taskDTO.setAccountProfileName(taskAccountProfileDescription(task));
		taskDTO.setActivityPid(taskActivityPid(task));
		taskDTO.setPid(task.getPid());
		taskDTO.setRemarks(task.getRemarks());
		taskDTO.setActivated(task.getActivated());

		return taskDTO;
	}
	@Override
	public List<TaskDTO> tasksToTaskDTOs(List<Task> tasks) {
		if (tasks == null) {
			return null;
		}

		List<TaskDTO> list = new ArrayList<TaskDTO>();
		if(getCompanyCofig()) {
		for (Task task : tasks) {
			list.add(taskToTaskDTODescription(task));
		}}
		else
		{
			for (Task task : tasks) {
				list.add(taskToTaskDTO(task));
			}
		}

		return list;
	}

	@Override
	public Task taskDTOToTask(TaskDTO taskDTO) {
		if (taskDTO == null) {
			return null;
		}

		Task task = new Task();

		task.setActivity(activityFromPid(taskDTO.getActivityPid()));
		task.setAccountProfile(accountProfileFromPid(taskDTO.getAccountProfilePid()));
		task.setAccountType(accountTypeFromPid(taskDTO.getAccountTypePid()));
		task.setPid(taskDTO.getPid());
		task.setRemarks(taskDTO.getRemarks());
		task.setActivated(taskDTO.getActivated());

		return task;
	}

	@Override
	public List<Task> taskDTOsToTasks(List<TaskDTO> taskDTOs) {
		if (taskDTOs == null) {
			return null;
		}

		List<Task> list = new ArrayList<Task>();
		for (TaskDTO taskDTO : taskDTOs) {
			list.add(taskDTOToTask(taskDTO));
		}

		return list;
	}

	private String taskAccountProfilePid(Task task) {

		if (task == null) {
			return null;
		}
		AccountProfile accountProfile = task.getAccountProfile();
		if (accountProfile == null) {
			return null;
		}
		String pid = accountProfile.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String taskAccountTypePid(Task task) {

		if (task == null) {
			return null;
		}
		AccountType accountType = task.getAccountType();
		if (accountType == null) {
			return null;
		}
		String pid = accountType.getPid();
		if (pid == null) {
			return null;
		}
		return pid;
	}

	private String taskActivityName(Task task) {

		if (task == null) {
			return null;
		}
		Activity activity = task.getActivity();
		if (activity == null) {
			return null;
		}
		String name = activity.getName();
		if (name == null) {
			return null;
		}
//		if (activity.getDescription() != null && getCompanyCofig()
//				&& !activity.getDescription().equals("common")) {
//			return activity.getDescription();
//		}
		return name;
	}
	private String taskActivityDescription(Task task) {

		if (task == null) {
			return null;
		}
		Activity activity = task.getActivity();
		if (activity == null) {
			return null;
		}
		String name = activity.getName();
		if (name == null) {
			return null;
		}
		if (activity.getDescription() != null 
				&& !activity.getDescription().equals("common")) {
			return activity.getDescription();
		}
		return name;
	}
	private String taskAccountTypeName(Task task) {

		if (task == null) {
			return null;
		}
		AccountType accountType = task.getAccountType();
		if (accountType == null) {
			return null;
		}
		String name = accountType.getName();
		if (name == null) {
			return null;
		}
//		if (accountType.getDescription() != null && getCompanyCofig()
//				&& !accountType.getDescription().equals("common")) {
//			return accountType.getDescription();
//		}
		return name;
	}
	private String taskAccountTypeDescription(Task task) {

		if (task == null) {
			return null;
		}
		AccountType accountType = task.getAccountType();
		if (accountType == null) {
			return null;
		}
		String name = accountType.getName();
		if (name == null) {
			return null;
		}
		if (accountType.getDescription() != null
				&& !accountType.getDescription().equals("common")) {
			return accountType.getDescription();
		}
		return name;
	}
	private String taskAccountProfileName(Task task) {

		if (task == null) {
			return null;
		}
		AccountProfile accountProfile = task.getAccountProfile();
		if (accountProfile == null) {
			return null;
		}
		String name = accountProfile.getName();
		if (name == null) {
			return null;
		}

//		if (accountProfile.getDescription() != null && getCompanyCofig()
//				&& !accountProfile.getDescription().equals("common")) {
//			return accountProfile.getDescription();
//
//		}
		return name;
	}

	private String taskAccountProfileDescription(Task task) {

		if (task == null) {
			return null;
		}
		AccountProfile accountProfile = task.getAccountProfile();
		if (accountProfile == null) {
			return null;
		}
		String name = accountProfile.getName();
		if (name == null) {
			return null;
		}

		if (accountProfile.getDescription() != null 
				&& !accountProfile.getDescription().equals("common")) {
			return accountProfile.getDescription();

		}
		return name;
	}
	private String taskActivityPid(Task task) {

		if (task == null) {
			return null;
		}
		Activity activity = task.getActivity();
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
