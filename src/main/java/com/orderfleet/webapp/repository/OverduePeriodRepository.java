package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.OverduePeriod;

/**
 * Spring Data JPA repository for the OverduePeriod entity.
 * 
 * @author Muhammed Riyas T
 * @since Mar 07, 2017
 */
public interface OverduePeriodRepository extends JpaRepository<OverduePeriod, Long> {

	@Query("select overduePeriod from OverduePeriod overduePeriod where overduePeriod.company.id = ?#{principal.companyId}")
	List<OverduePeriod> findAllByCompanyId();

}
