package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class FullMonth {

	private String name;
	private int noOfWeeks;
	private List<FullWeek> weeks;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FullWeek> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<FullWeek> weeks) {
		this.weeks = weeks;
	}

	public int getNoOfWeeks() {
		return noOfWeeks;
	}

	public void setNoOfWeeks(int noOfWeeks) {
		this.noOfWeeks = noOfWeeks;
	}

}
