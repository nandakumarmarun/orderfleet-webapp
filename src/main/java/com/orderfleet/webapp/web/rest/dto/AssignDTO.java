package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class AssignDTO<T> {

	public List<T> allData;
	public List<T> assignedData;

	public List<T> getAllData() {
		return allData;
	}

	public void setAllData(List<T> allData) {
		this.allData = allData;
	}

	public List<T> getAssignedData() {
		return assignedData;
	}

	public void setAssignedData(List<T> assignedData) {
		this.assignedData = assignedData;
	}

}
