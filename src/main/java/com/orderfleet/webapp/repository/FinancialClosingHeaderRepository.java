package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.FinancialClosingHeader;

public interface FinancialClosingHeaderRepository extends JpaRepository<FinancialClosingHeader, Long>{
	
	Optional<FinancialClosingHeader> findTop1ByUserPidOrderByIdDesc(String userPid);
	
}
