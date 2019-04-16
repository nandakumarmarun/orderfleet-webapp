package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;

/**
 *Repository for AttendanceStatusSubgroup
 *
 * @author fahad
 * @since Jul 25, 2017
 */
public interface AttendanceStatusSubgroupRepository extends JpaRepository<AttendanceStatusSubgroup, Long>{


	@Query("select attendanceStatusSubgroup from AttendanceStatusSubgroup attendanceStatusSubgroup where attendanceStatusSubgroup.company.id = ?#{principal.companyId} order by sortOrder asc")
	List<AttendanceStatusSubgroup> findAllByCompanyId();
	
	Optional<AttendanceStatusSubgroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);
}
