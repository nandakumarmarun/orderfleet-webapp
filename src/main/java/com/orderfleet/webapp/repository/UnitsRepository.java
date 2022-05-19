package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.Units;
import com.orderfleet.webapp.web.rest.dto.BankDTO;


public interface UnitsRepository extends JpaRepository<Units, Long>{
	Optional<Units> findOneByPid(String pid);

	Optional<Units> findByName(String name);

	Optional<Units> findOneByName(String name);
	
	@Query("select units from Units units  order by units.name")
	List<Units> findAllUnits();
}
