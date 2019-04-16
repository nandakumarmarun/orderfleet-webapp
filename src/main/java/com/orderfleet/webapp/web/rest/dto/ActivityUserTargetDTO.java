package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotNull;

/**
 * A DTO for the ActivityUserTarget entity.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
public class ActivityUserTargetDTO {

	private String pid;

	@NotNull
	private LocalDate startDate;

	@NotNull
	private LocalDate endDate;

	@NotNull
	private String activityPid;

	private String activityName;

	@NotNull
	private String userPid;

	private String userName;

	private Long targetNumber;

	private Long achivedNumber;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
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

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getTargetNumber() {
		return targetNumber;
	}

	public void setTargetNumber(Long targetNumber) {
		this.targetNumber = targetNumber;
	}

	public Long getAchivedNumber() {
		return achivedNumber;
	}

	public void setAchivedNumber(Long achivedNumber) {
		this.achivedNumber = achivedNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ActivityUserTargetDTO activityUserTargetDTO = (ActivityUserTargetDTO) o;

		if (!Objects.equals(pid, activityUserTargetDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "ActivityUserTarget{" + ", pid='" + pid + "'" + ", activityName='" + activityName + "'" + ", userName='"
				+ userName + "'" + '}';
	}
}
