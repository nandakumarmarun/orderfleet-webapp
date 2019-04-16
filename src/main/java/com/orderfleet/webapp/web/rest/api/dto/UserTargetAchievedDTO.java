package com.orderfleet.webapp.web.rest.api.dto;

/**
 * A UserTargetAchievedDTO used to set total activity count to the user.
 * dashboard of mobile.
 *
 * @author Sarath
 * @since Jul 24, 2017
 *
 */
public class UserTargetAchievedDTO {

	private long dayTarget;
	private long dayAchieved;
	private long monthTarget;
	private long monthAchieved;

	public UserTargetAchievedDTO() {
		super();
	}

	public UserTargetAchievedDTO(long dayTarget, long dayAchieved, long monthTarget, long monthAchieved) {
		super();
		this.dayTarget = dayTarget;
		this.dayAchieved = dayAchieved;
		this.monthTarget = monthTarget;
		this.monthAchieved = monthAchieved;
	}

	public long getDayTarget() {
		return dayTarget;
	}

	public void setDayTarget(long dayTarget) {
		this.dayTarget = dayTarget;
	}

	public long getDayAchieved() {
		return dayAchieved;
	}

	public void setDayAchieved(long dayAchieved) {
		this.dayAchieved = dayAchieved;
	}

	public long getMonthTarget() {
		return monthTarget;
	}

	public void setMonthTarget(long monthTarget) {
		this.monthTarget = monthTarget;
	}

	public long getMonthAchieved() {
		return monthAchieved;
	}

	public void setMonthAchieved(long monthAchieved) {
		this.monthAchieved = monthAchieved;
	}

	@Override
	public String toString() {
		return "UserTargetAchievedDTO [dayTarget=" + dayTarget + ", dayAchieved=" + dayAchieved + ", monthTarget="
				+ monthTarget + ", monthAchieved=" + monthAchieved + "]";
	}

}
