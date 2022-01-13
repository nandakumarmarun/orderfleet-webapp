package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.StateC;

public class StateCDTO {
	
	private Long id;
	
	 private String name;
	 
	 private Long country_id;
	 

	public StateCDTO() {
		super();
	}

	public StateCDTO(StateC state) {
		this.id=state.getId();
		this.name=state.getName();
		this.setCountry_id(state.getCountry().getId());
	}

	public StateCDTO(Long id, String name,Long country_id) {
		super();
		this.id = id;
		this.name = name;
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
	public Long getCountry_id() {
		return country_id;
	}

	public void setCountry_id(Long country_id) {
		this.country_id = country_id;
	}
	 
	 
	@Override
	public String toString() {
		return "StateCDTO [id=" + id + ", name=" + name + "]";
	}

	public void add(Long id2) {
		// TODO Auto-generated method stub
		
	}

	

}
