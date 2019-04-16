package com.orderfleet.webapp.web.rest.dto;

/**
 * used to calculate distance Api
 * 
 * @author Sarath
 * @since May 25, 2017
 *
 */
public class MapElementsDTO {

	private MapDistanceDTO distance;
	private MapDurationDTO duration;
	private String status;

	public MapElementsDTO() {
		super();
	}

	public MapDistanceDTO getDistance() {
		return distance;
	}

	public void setDistance(MapDistanceDTO distance) {
		this.distance = distance;
	}

	public MapDurationDTO getDuration() {
		return duration;
	}

	public void setDuration(MapDurationDTO duration) {
		this.duration = duration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Elements [distance=" + distance + ", duration=" + duration + ", status=" + status + "]";
	}

}
