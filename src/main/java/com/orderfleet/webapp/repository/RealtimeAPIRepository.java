package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.RealtimeAPI;

/**
 * Repository for managing RealtimeAPI.
 *
 * @author Sarath
 * @since Apr 10, 2018
 *
 */
public interface RealtimeAPIRepository extends JpaRepository<RealtimeAPI, Long> {

	Optional<RealtimeAPI> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<RealtimeAPI> findOneById(Long id);

	@Query("select realtimeAPI from RealtimeAPI realtimeAPI where realtimeAPI.activated = ?1")
	List<RealtimeAPI> findAllByActivatedRealtimeAPI(boolean active);

	@Query("select realtimeAPI from RealtimeAPI realtimeAPI where realtimeAPI.company.pid = ?1")
	List<RealtimeAPI> findAllByCompanyPid(String companyPid);

}
