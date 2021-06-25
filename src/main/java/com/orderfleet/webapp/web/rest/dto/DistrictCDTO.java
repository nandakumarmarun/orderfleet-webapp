package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.DistrictC;

public class DistrictCDTO {

	 private Long id;
	 private String name;
	
	 public DistrictCDTO() {
		super();
	}

	public DistrictCDTO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public DistrictCDTO(DistrictC districtc) 
	 {
		 this.id=districtc.getId();
		 this.name=districtc.getName();
	 }


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	 
	 
	 
	 
}
