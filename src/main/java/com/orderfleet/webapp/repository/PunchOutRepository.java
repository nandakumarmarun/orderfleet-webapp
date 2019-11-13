package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.PunchOut;
import com.orderfleet.webapp.domain.User;

/**
 * Repository for managing PunchOut.
 * 
 * @author Athul
 * @since March 27,2018
 */

public interface PunchOutRepository extends JpaRepository<PunchOut, Long> {

	@Query("select pncout from PunchOut pncout where pncout.company.id = ?#{principal.companyId} and pncout.attendance.pid = ?1")
	Optional<PunchOut> findIsAttendancePresent(String attendancepid);

	@Query("select pncout from PunchOut pncout where pncout.company.id = ?#{principal.companyId} and pncout.punchOutDate between ?1 and ?2 Order By pncout.punchOutDate asc")
	List<PunchOut> findAllByCompanyIdAndPunchDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select pncout from PunchOut pncout where pncout.company.id = ?#{principal.companyId} and pncout.user in ?1 and pncout.punchOutDate between ?2 and ?3 Order By pncout.punchOutDate asc")
	List<PunchOut> findAllByCompanyIdUserPidInAndPunchDateBetween(List<User> user, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select pncout from PunchOut pncout where pncout.company.id = ?#{principal.companyId} and pncout.user.pid = ?1 and pncout.punchOutDate between ?2 and ?3 Order By pncout.punchOutDate asc")
	List<PunchOut> findAllByCompanyIdUserPidAndPunchDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select pncout from PunchOut pncout where pncout.company.id = ?#{principal.companyId} and pncout.createdDate between ?1 and ?2 Order By pncout.createdDate asc")
	List<PunchOut> findAllByCompanyIdAndCreatedDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select pncout from PunchOut pncout where pncout.company.id = ?#{principal.companyId} and pncout.user in ?1 and pncout.createdDate between ?2 and ?3 Order By pncout.createdDate asc")
	List<PunchOut> findAllByCompanyIdUserPidInAndCreatedDateBetween(List<User> user, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select pncout from PunchOut pncout where pncout.company.id = ?#{principal.companyId} and pncout.user.pid = ?1 and pncout.createdDate between ?2 and ?3 Order By pncout.createdDate asc")
	List<PunchOut> findAllByCompanyIdUserPidAndCreatedDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

}
