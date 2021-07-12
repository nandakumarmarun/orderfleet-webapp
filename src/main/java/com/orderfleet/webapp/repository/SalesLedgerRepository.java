package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Bank;
import com.orderfleet.webapp.domain.SalesLedger;

/**
 * Spring Data JPA repository for the Bank entity.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
public interface SalesLedgerRepository extends JpaRepository<SalesLedger, Long> {

	@Query("select salesLedger from SalesLedger salesLedger where salesLedger.company.id = ?#{principal.companyId}")
	List<SalesLedger> findAllByCompanyId();

	Optional<SalesLedger> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<SalesLedger> findOneByPid(String pid);

	@Query("select salesLedger from SalesLedger salesLedger where salesLedger.company.id = ?#{principal.companyId} and salesLedger.activated = ?1")
	List<SalesLedger> findAllCompanyAndSalesLedgerActivated(boolean activated);

	
	@Transactional
	@Modifying
	@Query("update SalesLedger set activated = false where pid = ?1")
	void deleteOneByPid(String pid);

}
