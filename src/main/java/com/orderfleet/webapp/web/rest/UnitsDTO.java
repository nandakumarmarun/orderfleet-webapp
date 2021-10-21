package com.orderfleet.webapp.web.rest;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UnitsDTO {
	
    
	private String pid;

	@Size(min = 1, max = 255)
	private String name;
	
	private String shortName;
	
	private String alias;
	
	private String unitId;
	
	private String unitCode;

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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	@Override
	public String toString() {
		return "UnitsDTO [pid=" + pid + ", name=" + name + ", shortName=" + shortName + ", alias=" + alias + ", unitId="
				+ unitId + ", unitCode=" + unitCode + "]";
	}

	
}
