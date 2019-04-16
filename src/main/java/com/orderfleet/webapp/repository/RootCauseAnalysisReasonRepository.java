package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.RootCauseAnalysisReason;

public interface RootCauseAnalysisReasonRepository extends JpaRepository<RootCauseAnalysisReason, Long> {

	Optional<RootCauseAnalysisReason> findOneByPid(String pid);
	
	@Query("select rootCauseAnalysisReason from RootCauseAnalysisReason rootCauseAnalysisReason where rootCauseAnalysisReason.company.id = ?#{principal.companyId}")
	List<RootCauseAnalysisReason> findAllByCompanyId();
	
	@Query("select rca from RootCauseAnalysisReason rca where rca.company.id = ?#{principal.companyId} and pid in ?1")
	List<RootCauseAnalysisReason> findAllByPidsIn(List<String> rcaPids);
}
