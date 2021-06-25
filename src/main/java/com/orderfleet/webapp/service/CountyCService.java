package com.orderfleet.webapp.service;

import com.orderfleet.webapp.domain.CountryC;

public interface CountyCService {
	

	public Iterable<CountryC> findAll();

	public CountryC find(Long id);


}
