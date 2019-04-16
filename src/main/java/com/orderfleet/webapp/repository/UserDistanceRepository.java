package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.UserDistance;

/**
 * Spring Data JPA repository for the UserDistance entity.
 *
 * @author Sarath
 * @since May 25, 2017
 *
 */
public interface UserDistanceRepository extends JpaRepository<UserDistance, Long> {

	Optional<UserDistance> findByCompanyIdAndUserPidAndDate(Long companyId, String userPid, LocalDate date);

	@Query("select userDistance from UserDistance userDistance where userDistance.company.id = ?#{principal.companyId}")
	List<UserDistance> findAllByCompany();

	@Query("select userDistance from UserDistance userDistance where userDistance.company.id = ?#{principal.companyId} Order By userDistance.createdDate desc")
	Page<UserDistance> findAllByCompanyIdOrderByDateDesc(Pageable pageable);

	@Query("select userDistance from UserDistance userDistance where userDistance.company.id = ?#{principal.companyId} and userDistance.user.pid = ?1")
	List<UserDistance> findAllByCompanyIdAndUserPid(String userPid);

	@Query("select userDistance from UserDistance userDistance where userDistance.company.id = ?#{principal.companyId} and userDistance.user.pid = ?1 and userDistance.date between ?2 and ?3 Order By userDistance.createdDate desc")
	List<UserDistance> findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			LocalDate fromDate, LocalDate toDate);

	@Query("select userDistance from UserDistance userDistance where userDistance.company.id = ?#{principal.companyId} and userDistance.user.pid = ?1 and userDistance.date =?2 Order By userDistance.createdDate desc")
	List<UserDistance> findAllByCompanyIdAndUserPidAndDateOrderByCreatedDateDesc(String userPid, LocalDate fromDate);

	@Query("select userDistance from UserDistance userDistance where userDistance.company.id = ?#{principal.companyId} and userDistance.date between ?1 and ?2 Order By userDistance.createdDate desc")
	List<UserDistance> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDate fromDate, LocalDate toDate);

	@Query("select userDistance from UserDistance userDistance where userDistance.company.id = ?#{principal.companyId} and userDistance.user.pid in ?1 and userDistance.date between ?2 and ?3 Order By userDistance.createdDate desc")
	List<UserDistance> findAllByCompanyIdAndUserPidInAndDateBetweenOrderByCreatedDateDesc(List<String> userPid,
			LocalDate fromDate, LocalDate toDate);

	@Query("select userDistance from UserDistance userDistance where userDistance.company.id = ?#{principal.companyId} and userDistance.user.pid in ?1 and userDistance.date =?2 Order By userDistance.createdDate desc")
	List<UserDistance> findAllByCompanyIdAndUserPidInAndDateOrderByCreatedDateDesc(List<String> userPid,
			LocalDate fromDate);
}
