package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.Units;
import com.orderfleet.webapp.web.rest.dto.BankDTO;


public interface UnitsRepository extends JpaRepository<Units, Long>{
	Optional<Units> findOneByPid(String pid);

	Optional<Units> findByName(String name);

	Optional<Units> findOneByName(String name);
}
