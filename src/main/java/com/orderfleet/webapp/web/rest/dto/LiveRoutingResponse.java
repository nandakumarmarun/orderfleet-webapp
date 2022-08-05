package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class LiveRoutingResponse
{
	
	private List<LocationData>locations;

	public LiveRoutingResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<LocationData> getLocations() {
		return locations;
	}

	public void setLocations(List<LocationData> locations) {
		this.locations = locations;
	}

	


}