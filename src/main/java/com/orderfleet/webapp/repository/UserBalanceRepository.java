package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.UserBalance;

public interface UserBalanceRepository extends JpaRepository<UserBalance, Long>{

	@Query("select userBalance from UserBalance userBalance where userBalance.company.id = ?#{principal.companyId}")
	List<UserBalance> findAllByCompanyId();
	
	Optional<UserBalance> findTop1ByUserPidOrderByIdDesc(String userPid);
}
