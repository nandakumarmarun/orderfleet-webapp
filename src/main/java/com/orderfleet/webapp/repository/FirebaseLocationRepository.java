package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.FirebaseLocation;

public interface FirebaseLocationRepository extends JpaRepository<FirebaseLocation, Long> {

	void deleteByCompanyId(Long companyId);

}
