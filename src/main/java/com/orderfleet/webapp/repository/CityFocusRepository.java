package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.City;

public interface CityFocusRepository extends JpaRepository<City, Long>{

}
