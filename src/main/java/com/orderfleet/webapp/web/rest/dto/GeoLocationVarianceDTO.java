package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;

import com.orderfleet.webapp.domain.ExecutiveTaskExecution;

/**
 * A DTO for the geolocation variance.
 *
 * @author Sarath
 * @since Jul 27, 2017
 *
 */
public class GeoLocationVarianceDTO {

	private String executionPid;
	private String accountProfileName;
	private String accountProfilePid;
	private String activityName;
	private String activityPid;
	private LocalDate date;
	private String actualLocation;
	private double actualLocationLat;
	private double actualLocationLng;
	private String reportedLocation;
	private double reportedLocationLat;
	private double reportedLocationLng;
	private String variation;

	public GeoLocationVarianceDTO() {
		super();
	}

	public GeoLocationVarianceDTO(ExecutiveTaskExecution execution) {
		super();
		this.executionPid = execution.getPid();
		this.accountProfileName = execution.getAccountProfile().getName();
		this.accountProfilePid = execution.getAccountProfile().getPid();
		this.activityName = execution.getActivity().getName();
		this.activityPid = execution.getActivity().getPid();
		this.date = execution.getDate().toLocalDate();
		this.actualLocation = execution.getAccountProfile().getLocation() == null ? ""
				: execution.getAccountProfile().getLocation();
		this.actualLocationLat = execution.getAccountProfile().getLatitude() != null
				? execution.getAccountProfile().getLatitude().doubleValue()
				: 0;
		this.actualLocationLng = execution.getAccountProfile().getLongitude() != null
				? execution.getAccountProfile().getLongitude().doubleValue()
				: 0;
		this.reportedLocation = execution.getLocation() == null ? "" : execution.getLocation();
		this.reportedLocationLat = execution.getLatitude() != null ? execution.getLatitude().doubleValue() : 0;
		this.reportedLocationLng = execution.getLongitude() != null ? execution.getLongitude().doubleValue() : 0;

		if (execution.getLatitude() != null && execution.getAccountProfile().getLatitude() != null
				&& execution.getLatitude().doubleValue() != 0
				&& execution.getAccountProfile().getLatitude().doubleValue() != 0) {
			this.variation = execution.getLocationVariance() != null ? execution.getLocationVariance() : "Button";
		} else if (execution.getAccountProfile().getLatitude() == null
				|| execution.getAccountProfile().getLatitude().doubleValue() == 0) {
			if (execution.getLocation() == null || execution.getLocation().equalsIgnoreCase("No Location")
					|| execution.getLocation().isEmpty()) {
				this.variation = "-";
			} else {
				this.variation = "Customer Not Geo Tagged";
			}
		} else {
			this.variation = "-";

		}

	}

	public String getExecutionPid() {
		return executionPid;
	}

	public void setExecutionPid(String executionPid) {
		this.executionPid = executionPid;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accoountProfileName) {
		this.accountProfileName = accoountProfileName;
	}

	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accoountProfilePid) {
		this.accountProfilePid = accoountProfilePid;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getActualLocation() {
		return actualLocation;
	}

	public void setActualLocation(String actualLocation) {
		this.actualLocation = actualLocation;
	}

	public double getActualLocationLat() {
		return actualLocationLat;
	}

	public void setActualLocationLat(double actualLocationLat) {
		this.actualLocationLat = actualLocationLat;
	}

	public double getActualLocationLng() {
		return actualLocationLng;
	}

	public void setActualLocationLng(double actualLocationLng) {
		this.actualLocationLng = actualLocationLng;
	}

	public String getReportedLocation() {
		return reportedLocation;
	}

	public void setReportedLocation(String reportedLocation) {
		this.reportedLocation = reportedLocation;
	}

	public double getReportedLocationLat() {
		return reportedLocationLat;
	}

	public void setReportedLocationLat(double reportedLocationLat) {
		this.reportedLocationLat = reportedLocationLat;
	}

	public double getReportedLocationLng() {
		return reportedLocationLng;
	}

	public void setReportedLocationLng(double reportedLocationLng) {
		this.reportedLocationLng = reportedLocationLng;
	}

	public String getVariation() {
		return variation;
	}

	public void setVariation(String variation) {
		this.variation = variation;
	}

	@Override
	public String toString() {
		return "GeoLocationVarianceDTO [accountProfileName=" + accountProfileName + ", accountProfilePid="
				+ accountProfilePid + ", activityName=" + activityName + ", activityPid=" + activityPid + ", date="
				+ date + ", actualLocation=" + actualLocation + ", actualLocationLat=" + actualLocationLat
				+ ", actualLocationLng=" + actualLocationLng + ", reportedLocation=" + reportedLocation
				+ ", reportedLocationLat=" + reportedLocationLat + ", reportedLocationLng=" + reportedLocationLng + "]";
	}

}
