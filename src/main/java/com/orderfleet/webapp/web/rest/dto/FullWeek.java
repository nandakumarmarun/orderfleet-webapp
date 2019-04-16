package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class FullWeek {

	private String week;
	private List<PriceView> prices;

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public List<PriceView> getPrices() {
		return prices;
	}

	public void setPrices(List<PriceView> prices) {
		this.prices = prices;
	}

}
