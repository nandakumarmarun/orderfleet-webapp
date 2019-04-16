package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.UserDistance;

/**
 *  A DTO For Generate UserDistance.
 *
 * @author Sarath
 * @since May 25, 2017
 *
 */
public class UserDistanceDTO {

	private double kilometre;
	private LocalDate date;
	private String startLocation;
	private String endLocation;
	private String userName;
	private String UserPid;
	private LocalDateTime createdDate;
	private LocalDateTime lastModifiedDate;

	
	public UserDistanceDTO() {
		super();
	}
	public UserDistanceDTO(UserDistance userDistance) {
		super();
		this.kilometre = userDistance.getKilometre();
		this.date = userDistance.getDate();
		this.startLocation = userDistance.getStartLocation();
		this.endLocation = userDistance.getEndLocation();
		this.userName = userDistance.getUser().getFirstName()+" "+userDistance.getUser().getLastName();
		UserPid = userDistance.getUser().getPid();
		this.createdDate = userDistance.getCreatedDate();
		this.lastModifiedDate = userDistance.getLastModifiedDate();

	}
	public double getKilometre() {
		return kilometre;
	}
	public void setKilometre(double kilometre) {
		this.kilometre = kilometre;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getStartLocation() {
		return startLocation;
	}
	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}
	public String getEndLocation() {
		return endLocation;
	}
	public void setEndLocation(String endLocation) {
		this.endLocation = endLocation;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPid() {
		return UserPid;
	}
	public void setUserPid(String userPid) {
		UserPid = userPid;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	@Override
	public String toString() {
		return "UserDistanceDTO [kilometre=" + kilometre + ", date=" + date + ", endLocation=" + endLocation
				+ ", userName=" + userName + ", createdDate=" + createdDate + "]";
	}

	
	
	
	
}
