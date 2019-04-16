package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class PriceTrendProductView {

	private String pid;
	private String name;
	private List<CompitatorView> compitators;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CompitatorView> getCompitators() {
		return compitators;
	}

	public void setCompitators(List<CompitatorView> compitators) {
		this.compitators = compitators;
	}

}
