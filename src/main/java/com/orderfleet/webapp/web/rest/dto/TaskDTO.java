package com.orderfleet.webapp.web.rest.dto;

import java.util.List;
import java.util.Objects;

import javax.validation.constraints.Size;

/**
 * A DTO for the Task entity.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
public class TaskDTO {

	private String pid;

	private String activityPid;

	private String activityName;

	private String accountTypePid;

	private String accountTypeName;

	private String accountProfilePid;

	private String accountProfileName;

	private List<String> accountPids;
	
	private boolean activated;

	public TaskDTO() {
		super();
	}

	public TaskDTO(com.orderfleet.webapp.domain.Task task) {
		this(task.getPid(), task.getActivity().getPid(), task.getActivity().getName(), task.getAccountType().getPid(),
				task.getAccountType().getName(), task.getAccountProfile().getPid(), task.getAccountProfile().getName(),
				task.getRemarks());
	}

	public TaskDTO(String pid, String activityPid, String activityName, String accountTypePid, String accountTypeName,
			String accountProfilePid, String accountProfileName, String remarks) {
		super();
		this.pid = pid;
		this.activityPid = activityPid;
		this.activityName = activityName;
		this.accountTypePid = accountTypePid;
		this.accountTypeName = accountTypeName;
		this.accountProfilePid = accountProfilePid;
		this.accountProfileName = accountProfileName;
		this.remarks = remarks;
	}

	@Size(max = 255)
	private String remarks;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getAccountTypePid() {
		return accountTypePid;
	}

	public void setAccountTypePid(String accountTypePid) {
		this.accountTypePid = accountTypePid;
	}

	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<String> getAccountPids() {
		return accountPids;
	}

	public void setAccountPids(List<String> accountPids) {
		this.accountPids = accountPids;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TaskDTO taskDTO = (TaskDTO) o;

		if (!Objects.equals(pid, taskDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "TaskDTO{" + ", pid='" + pid + "'" + ", remarks='" + remarks + "'" + '}';
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
}