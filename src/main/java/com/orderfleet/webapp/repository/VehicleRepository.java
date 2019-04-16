package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	Optional<Vehicle> findOneByPid(String pid);
	
	Optional<Vehicle> findOneByNameAndCompanyId(String name,Long company);
	
	@Query("select vehicle from Vehicle vehicle where vehicle.company.id = ?#{principal.companyId}")
	List<Vehicle> findAllByCompany();
}
