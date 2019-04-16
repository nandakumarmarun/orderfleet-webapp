package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DistanceFare;

public interface DistanceFareRepository extends JpaRepository<DistanceFare, Long> {

	Optional<DistanceFare> findOneByPid(String pid);
	
	@Query("select distanceFare from DistanceFare distanceFare where distanceFare.company.id = ?#{principal.companyId}")
	List<DistanceFare> findAllByCompanyId();
	
}
