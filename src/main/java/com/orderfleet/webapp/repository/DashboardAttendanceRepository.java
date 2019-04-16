package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DashboardAttendance;

public interface DashboardAttendanceRepository extends JpaRepository<DashboardAttendance, Long>{

	Optional<DashboardAttendance> findByCompanyIdAndNameIgnoreCase(Long id, String name);
	
	@Query("select dashboardAttendance from DashboardAttendance dashboardAttendance where dashboardAttendance.company.id = ?#{principal.companyId}")
	List<DashboardAttendance> findAllByCompanyId();
}
