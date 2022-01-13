package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.DistrictC;

public class DistrictCDTO {

	 private Long id;
	 private String name;
	private Long state_id;
	 public DistrictCDTO() {
		super();
	}

	public DistrictCDTO(Long id, String name,Long state_id) {
		super();
		this.id = id;
		this.name = name;
		this.state_id=state_id;
	}
	
	public DistrictCDTO(DistrictC districtc) 
	 {
		 this.id=districtc.getId();
		 this.name=districtc.getName();
		 this.state_id=districtc.getState().getId();
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

	public Long getState_id() {
		return state_id;
	}

	public void setState_id(Long state_id) {
		this.state_id = state_id;
	}
	
	
	 
	 
	 
	 
}
