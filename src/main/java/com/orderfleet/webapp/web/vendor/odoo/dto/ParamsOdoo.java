package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ParamsOdoo {

	private List<Object> args;

	private String method;

	private String service;

	public List<Object> getArgs() {
		return args;
	}

	public void setArgs(List<Object> args) {
		this.args = args;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	@Override
	public String toString() {
		return "Params [args=" + args + ", method=" + method + ", service=" + service + "]";
	}

}
