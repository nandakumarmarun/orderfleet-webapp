package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.orderfleet.webapp.domain.Country;

/**
 * Spring Data JPA repository for the Country entity.
 * 
 * @author Sarath
 * @since Aug 17, 2016
 */
public interface CountryRepository extends JpaRepository<Country, Long> {

	public Country findByCode(String code);

	public Country findByName(String name);
}
