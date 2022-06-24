package com.orderfleet.webapp.web.rest.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.LengthType;

public class LengthTypeDTO {

	private String pid;

	private String masterCode;

	private String masterName;
	
	private double meterConversion;

	public LengthTypeDTO() {
		super();
	}

	public String getPid() {
		return pid;
	}

	public LengthTypeDTO(LengthType lengthType) {
		super();
		this.pid = lengthType.getPid();
		this.masterCode = lengthType.getMasterCode();
		this.masterName = lengthType.getMasterName();
		this.meterConversion = lengthType.getMeterConversion();
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getMasterCode() {
		return masterCode;
	}

	public void setMasterCode(String masterCode) {
		this.masterCode = masterCode;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public double getMeterConversion() {
		return meterConversion;
	}

	public void setMeterConversion(double meterConversion) {
		this.meterConversion = meterConversion;
	}
}
