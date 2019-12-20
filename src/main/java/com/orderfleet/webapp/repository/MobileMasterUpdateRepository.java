package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.MobileMasterUpdate;

/**
 * Spring Data JPA repository for the Department entity.
 * 
 * @author Prashob
 * @since Dec 18, 2019
 */
public interface MobileMasterUpdateRepository extends JpaRepository<MobileMasterUpdate, Long> {
	
	Optional<MobileMasterUpdate> findByUserPid(String userPid);
}
