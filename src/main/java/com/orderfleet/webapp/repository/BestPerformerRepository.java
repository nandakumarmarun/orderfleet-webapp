package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.BestPerformer;

public interface BestPerformerRepository  extends JpaRepository<BestPerformer, Long> {
	@Query("SELECT bp FROM BestPerformer bp WHERE bp.company.id = ?#{principal.companyId}")
	BestPerformer findOneByCompanyId();
	

	Optional<BestPerformer> findOneByPid(String pid);
}
