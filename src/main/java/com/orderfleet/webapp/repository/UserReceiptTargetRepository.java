package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.UserReceiptTarget;

public interface UserReceiptTargetRepository extends JpaRepository<UserReceiptTarget, Long> {

	Optional<UserReceiptTarget> findOneByPid(String pid);

	@Query("select userReceiptTarget from UserReceiptTarget userReceiptTarget where userReceiptTarget.company.id = ?#{principal.companyId}")
	List<UserReceiptTarget> findAllByCompanyId();

	@Query("select userReceiptTarget from UserReceiptTarget userReceiptTarget where userReceiptTarget.company.id = ?#{principal.companyId}")
	Page<UserReceiptTarget> findAllByCompanyId(Pageable pageable);

	@Query("select userReceiptTarget from UserReceiptTarget userReceiptTarget where ?1 between userReceiptTarget.startDate and userReceiptTarget.endDate and userReceiptTarget.user.login = ?#{principal.username}")
	UserReceiptTarget findOneByCurrentUserAndDate(LocalDate date);

	@Query("select urTarget from UserReceiptTarget urTarget where urTarget.user.pid = ?1 and urTarget.startDate <= ?3 AND urTarget.endDate >= ?2")
	List<UserReceiptTarget> findUserAndDateWiseDuplicate(String userPid, LocalDate startDate, LocalDate endDate);

}
