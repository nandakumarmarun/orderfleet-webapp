package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.orderfleet.webapp.domain.UserCustomerGroupTarget;

/**
 * A DTO for the ActivityUserTarget entity.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
public class UserCustomerGroupTargetDTO {

	private String pid;

	@NotNull
	private LocalDate startDate;

	@NotNull
	private LocalDate endDate;

	@NotNull
	private String stagePid;

	private String stageName;

	@NotNull
	private String userPid;

	private String userName;

	private Long targetNumber;

	private Long achivedNumber;

	public UserCustomerGroupTargetDTO() {
		super();
	}

	public UserCustomerGroupTargetDTO(UserCustomerGroupTarget userCustomerGroupTarget) {
		super();
		this.pid = userCustomerGroupTarget.getPid();
		this.startDate = userCustomerGroupTarget.getStartDate();
		this.endDate = userCustomerGroupTarget.getEndDate();
		this.stagePid = userCustomerGroupTarget.getStage().getPid();
		this.stageName = userCustomerGroupTarget.getStage().getName();
		this.userPid = userCustomerGroupTarget.getUser().getPid();
		this.userName = userCustomerGroupTarget.getUser().getFirstName();
		this.targetNumber = userCustomerGroupTarget.getTargetNumber();

	}

	public String getStagePid() {
		return stagePid;
	}

	public void setStagePid(String stagePid) {
		this.stagePid = stagePid;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

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
		return stagePid;
	}

	public void setActivityPid(String stagePid) {
		this.stagePid = stagePid;
	}

	public String getActivityName() {
		return stageName;
	}

	public void setActivityName(String stageName) {
		this.stageName = stageName;
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

		UserCustomerGroupTargetDTO userCustomerGroupTargetDTO = (UserCustomerGroupTargetDTO) o;

		if (!Objects.equals(pid, userCustomerGroupTargetDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "ActivityUserTarget{" + ", pid='" + pid + "'" + ", stageName='" + stageName + "'" + ", userName='"
				+ userName + "'" + '}';
	}
}
