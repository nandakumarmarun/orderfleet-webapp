package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.StateC;

public class StateCDTO {
	
	private Long id;
	
	 private String name;
	 
	 
	 

	public StateCDTO() {
		super();
	}

	public StateCDTO(StateC state) {
		this.id=state.getId();
		this.name=state.getName();
	}

	public StateCDTO(Long id, String name) {
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

	@Override
	public String toString() {
		return "StateCDTO [id=" + id + ", name=" + name + "]";
	}
	 
	 

}
