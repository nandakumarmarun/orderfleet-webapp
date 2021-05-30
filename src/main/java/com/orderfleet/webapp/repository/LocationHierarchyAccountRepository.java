package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountingVoucherHeaderHistory;
import com.orderfleet.webapp.domain.LocationHierarchyAccount;

/**
 * Spring Data JPA repository for the AccountingVoucherHeaderHistory entity.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
public interface LocationHierarchyAccountRepository extends JpaRepository<LocationHierarchyAccount, Long> {

	@Transactional
	@Modifying
	@Query("delete from LocationHierarchyAccount locationAccountProfile where locationAccountProfile.company.id = ?#{principal.companyId}")
	void deleteByCompany();

	@Query("select lap.accountProfile.name,lap.locations,lap.accountProfile.pid from LocationHierarchyAccount lap where lap.company.id = ?#{principal.companyId} order by lap.accountProfile.name asc")
	List<Object[]> findAllByCompany();

	@Query("select lap.accountProfile.pid,lap.locations from LocationHierarchyAccount lap where lap.company.id = ?#{principal.companyId} and lap.accountProfile.pid in ?1 order by lap.accountProfile.name asc")
	List<Object[]> findAllByCompanyAndAccountProfilePids(List<String> accountProfilePids);

}
