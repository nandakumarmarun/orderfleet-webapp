package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AttendanceStatus;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;

/**
 * Spring Data JPA repository for the Attendance entity.
 * 
 * @author Sarath
 * @since Aug 17, 2016
 */
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

	Optional<Attendance> findOneByPid(String pid);

	@Query("select attendance from Attendance attendance where attendance.company.id = ?#{principal.companyId}")
	List<Attendance> findAllByCompanyId();

	@Query("select attendance from Attendance attendance where attendance.company.id = ?#{principal.companyId}")
	Page<Attendance> findAllByCompanyId(Pageable pageable);

	@Query("select attendance from Attendance attendance where attendance.user.login = ?#{principal.username}")
	List<Attendance> findByUserIsCurrentUser();

	@Query("select attendance from Attendance attendance where attendance.company.id = ?#{principal.companyId} and attendance.plannedDate between ?1 and ?2 Order By attendance.plannedDate asc")
	List<Attendance> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select attendance from Attendance attendance where attendance.company.id = ?#{principal.companyId} and attendance.user.pid = ?1 and attendance.plannedDate between ?2 and ?3 Order By attendance.plannedDate asc")
	List<Attendance> findAllByCompanyIdUserPidAndDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select attendance from Attendance attendance where attendance.company.id = ?#{principal.companyId} and attendance.user.login = ?1 and attendanceStatus = ?2 and attendance.createdDate between ?3 and ?4")
	List<Attendance> findByCompanyIdUserLoginAndDateBetweenAndAttendanceStatus(String login,
			AttendanceStatus attendanceStatus, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select count(attendance) from Attendance attendance where attendance.company.id = ?#{principal.companyId} and attendance.plannedDate between ?1 and ?2")
	long countByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	@Query(nativeQuery = true, value = "select count(*) from tbl_attendance where user_id in ?1 and planned_date between ?2 and ?3")
	long countByUserIdInAndDateBetween(List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query(nativeQuery = true, value = "select count(DISTINCT user_id) as count from tbl_attendance where user_id in ?1 and planned_date between ?2 and ?3")
	long countByUserIdInAndDateBetween(Set<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select count(attendance) from Attendance attendance where attendance.company.id = ?#{principal.companyId} and attendance.plannedDate between ?1 and ?2 and attendanceStatus = ?3")
	long countByCompanyIdAndDateBetweenAndAttendanceStatus(LocalDateTime fromDate, LocalDateTime toDate,
			AttendanceStatus attendanceStatus);

	@Query("select count(attendance) from Attendance attendance where attendance.company.id = ?#{principal.companyId} and attendance.plannedDate between ?1 and ?2 and attendanceStatusSubgroup = ?3 ")
	long countByCompanyIdAndDateBetweenAndAttendanceSubGroup(LocalDateTime fromDate, LocalDateTime toDate,
			AttendanceStatusSubgroup attendanceStatusSubgroup);

	@Query("select attendance from Attendance attendance where attendance.company.id = ?#{principal.companyId} and attendance.user in ?1 and attendance.plannedDate between ?2 and ?3 Order By attendance.plannedDate asc")
	List<Attendance> findAllByCompanyIdUserPidInAndDateBetween(List<User> user, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select attendance.user.login from Attendance attendance where attendance.user.id in ?1 and attendance.plannedDate between ?2 and ?3")
	List<String> findByCompanyPidAndUserPidInAndDateBetween(List<Long> userIds, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select attendance from Attendance attendance where attendance.company.id = ?#{principal.companyId} and attendance.user.login = ?1 and attendance.plannedDate between ?2 and ?3 Order By attendance.plannedDate asc")
	List<Attendance> findByCompanyIdAndUserLoginAndDateBetweenOrderByDate(String login, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select distinct attendance.user from Attendance attendance where attendance.company.pid = ?1")
	List<User> findAllUniqueUsersFromAttendance(String companypid);

	@Query("select distinct attendance.user from Attendance attendance where attendance.company.pid = ?1 and attendance.createdDate between ?2 and ?3")
	List<User> getCountUniqueUsersFromAttendanceAndCreateDateBetween(String companypid, LocalDateTime startDate,
			LocalDateTime endDate);

	@Query("select count(attendance) from Attendance attendance where attendance.company.pid = ?1 and attendance.user.pid = ?2 and attendance.createdDate between ?3 and ?4")
	Long getCountOfAttendanceFromAttendanceByCompanyPidAndUserPidAndCreateDateBetween(String companypid, String userPid,
			LocalDateTime startDate, LocalDateTime endDate);

	Optional<Attendance> findTop1ByCompanyPidAndUserPidOrderByCreatedDateDesc(String companyPid, String userPid);

	Optional<Attendance> findTop1ByCompanyIdAndUserPidOrderByCreatedDateDesc(Long companyPid, String userPid);

	List<Attendance> findAllByCompanyIdAndUserPidOrderByCreatedDateDesc(Long companyPid, String userPid);

	@Query("select attendance from Attendance attendance where attendance.company.id = ?#{principal.companyId} and attendance.plannedDate between ?1 and ?2 and attendance.user.id = ?3 Order By attendance.plannedDate desc")
	List<Attendance> getByDateBetweenAndUser(LocalDateTime fromDate, LocalDateTime toDate, Long userId);

	List<Attendance> findTop61ByUserIdOrderByPlannedDateDesc(long userId);

	Optional<Attendance> findOneByPidAndImageRefNo(String attendancePid, String imageRefNo);

	Optional<Attendance> findOneByImageRefNo(String imageRefNo);

	Optional<Attendance> findTop1ByCompanyPidAndUserPidAndCreatedDateBetweenOrderByCreatedDateDesc(String companyPid,
			String userPid, LocalDateTime clientFromDate, LocalDateTime clientToDate);

	Optional<Attendance> findTop1ByCompanyPidAndUserPidAndPlannedDateBetweenOrderByCreatedDateDesc(String companyPid,
			String userPid, LocalDateTime clientFromDate, LocalDateTime clientToDate);

	/*
	 * @Query("select planned_date,attendance_status,remarks from tbl_attendance WHERE company_id = ?#{principal.companyId} and  user_id = ?1 ORDER BY  DESC top 61"
	 * ) List<Object[]> getAttendanceByUserandUptoLimitDesc(Long userId);
	 */

}
