package com.orderfleet.webapp.repository;

import java.util.List;

import org.jboss.logging.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.StateC;
import com.orderfleet.webapp.web.rest.dto.StateCDTO;

public interface StateCRepository extends JpaRepository<StateC, Long>
{
	

	@Query("select statec from StateC statec where statec.country.id =?1")
	List<StateC> findAllByCountryId(Long id);
	
	@Query("select statec from StateC statec order by statec.name")
	List<StateC> findAllStates();


}