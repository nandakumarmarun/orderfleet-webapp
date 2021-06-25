package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.User;

public interface CounrtyCRepository extends JpaRepository<CountryC,Long> {

	//CountryC findById(Long id);

	@Query("select countryc from CountryC countryc order by countryc.name")
	List<CountryC> findAllCountries();



	//Optional<User> findById(String counrtyId);

}
