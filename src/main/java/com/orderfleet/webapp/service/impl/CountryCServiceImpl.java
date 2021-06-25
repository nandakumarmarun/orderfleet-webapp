package com.orderfleet.webapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.repository.CounrtyCRepository;
import com.orderfleet.webapp.service.CountyCService;

public class CountryCServiceImpl implements CountyCService{
	
	@Autowired
	CounrtyCRepository countrycrepository;

	@Override
	public Iterable<CountryC> findAll() {
		
		return countrycrepository.findAll();
	}

	@Override
	public CountryC find(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @Override public CountryC find(Long id) {
	 * 
	 * return countrycrepository.findById(id); }
	 */

}
