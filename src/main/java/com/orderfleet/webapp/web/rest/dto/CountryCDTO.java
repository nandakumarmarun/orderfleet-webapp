package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.repository.CounrtyCRepository;

public class CountryCDTO {
	
	
	private Long id;
	
	 private String name;

	public CountryCDTO() {
		super();
	}

	public CountryCDTO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	 
	 
	 public CountryCDTO(CountryC countryc) 
	 {
		 this.id=countryc.getId();
		 this.name=countryc.getName();
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
