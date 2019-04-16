package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.RootPlanHeader;

public interface RootPlanHeaderRepository extends JpaRepository<RootPlanHeader, Long> {

	@Query("select rootPlanHeader from RootPlanHeader rootPlanHeader where rootPlanHeader.company.id =?#{principal.companyId}")
	List<RootPlanHeader> findAllByCompanyId();

	Optional<RootPlanHeader> findOneByPid(String pid);

	Optional<RootPlanHeader> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	@Query("select rootPlanHeader from RootPlanHeader rootPlanHeader where rootPlanHeader.company.id =?#{principal.companyId} and rootPlanHeader.user.login=?1")
	List<RootPlanHeader> findAllByUser(String userLogin);

	@Query("select rootPlanHeader from RootPlanHeader rootPlanHeader where rootPlanHeader.company.id =?#{principal.companyId} and rootPlanHeader.user.login =?1 and rootPlanHeader.activated =?2")
	RootPlanHeader findRootPlanHeaderByUserLoginAndActivated(String login, boolean activated);

	@Query("select rootPlanHeader from RootPlanHeader rootPlanHeader where rootPlanHeader.company.id =?#{principal.companyId} and rootPlanHeader.user.pid=?1")
	List<RootPlanHeader> findAllByUserPid(String userPid);

	@Query("select rootPlanHeader from RootPlanHeader rootPlanHeader where rootPlanHeader.company.id =?#{principal.companyId} and rootPlanHeader.fromDate>=?1 and rootPlanHeader.toDate<=?2")
	List<RootPlanHeader> findAllByDateGreaterFromDateAndLessThanToDate(LocalDate fromDate, LocalDate toDate);

	@Query("select rootPlanHeader from RootPlanHeader rootPlanHeader where rootPlanHeader.company.id =?#{principal.companyId} and rootPlanHeader.user.pid=?1 and rootPlanHeader.fromDate>=?2 and rootPlanHeader.toDate<=?3")
	List<RootPlanHeader> findAllByUserPidAndDateGreaterFromDateAndLessThanToDate(String userPid, LocalDate fromDate,
			LocalDate toDate);

	@Query("select rootPlanHeader from RootPlanHeader rootPlanHeader where rootPlanHeader.company.id =?#{principal.companyId} and rootPlanHeader.activated =?1")
	List<RootPlanHeader> findAllByActivated(boolean activated);

	@Query("select rootPlanHeader from RootPlanHeader rootPlanHeader where rootPlanHeader.company.id =?#{principal.companyId} and rootPlanHeader.user.pid=?1 and rootPlanHeader.activated =?2")
	List<RootPlanHeader> findAllByUserPidAndActivated(String userPid, boolean activated);
}
