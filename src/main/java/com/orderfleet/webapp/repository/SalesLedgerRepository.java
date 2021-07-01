package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

}
