package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.EmployeeProfileHistory;

/**
 * Spring Data JPA repository for the EmployeeProfileHistory entity.
 * 
 * @author Muhammed Riyas T
 * @since June 07, 2016
 */
public interface EmployeeProfileHistoryRepository extends JpaRepository<EmployeeProfileHistory, Long> {

}
