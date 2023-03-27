package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.CompanyBilling;
import com.orderfleet.webapp.domain.enums.BillingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CompanyBillingRepository extends JpaRepository<CompanyBilling, Long> {

	Optional<CompanyBilling> findOneByPid(String companyPid);

	@Query("select companyBilling from CompanyBilling companyBilling where companyBilling.company.id =?1")
	Optional<CompanyBilling> findOneByCompanyId(Long id);

	@Query("select companyBilling from CompanyBilling companyBilling where companyBilling.company.pid =?1")
	Optional<CompanyBilling> findOneBycompanyPid(String companyPid);

	@Query("select companyBilling from CompanyBilling companyBilling where companyBilling.next_bill_date between ?1 and ?2")
	List<CompanyBilling> findCompanyBillingByDateBetween(LocalDate fromDate, LocalDate toDate);

	@Query("select companyBilling from CompanyBilling companyBilling where companyBilling.billingPeriod =?1 and companyBilling.next_bill_date between ?2 and ?3")
	List<CompanyBilling> findBybillingPeriodAndDateBetween(BillingPeriod billingPeriod, LocalDate fromDate, LocalDate toDate);

}
