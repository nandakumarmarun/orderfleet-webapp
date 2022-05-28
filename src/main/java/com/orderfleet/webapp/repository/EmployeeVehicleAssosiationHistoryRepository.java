package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeVehicleAssosiationHistory;


public interface EmployeeVehicleAssosiationHistoryRepository extends JpaRepository<EmployeeVehicleAssosiationHistory, Long>{

}
