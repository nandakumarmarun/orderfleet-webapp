package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.BankDetails;

/**
 * Spring Data JPA repository for the BankDetails entity.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {

	Optional<BankDetails> findByCompanyId(Long id);

	Optional<BankDetails> findOneByPid(String pid);

	@Query("select bankDetails from BankDetails bankDetails where bankDetails.company.id = ?#{principal.companyId}")
	List<BankDetails> findAllByCompanyId();

	@Query("select bankDetails from BankDetails bankDetails where bankDetails.company.id = ?#{principal.companyId}")
	Page<BankDetails> findAllByCompanyId(Pageable pageable);

	@Query("select bankDetails from BankDetails bankDetails where bankDetails.company.id = ?#{principal.companyId}")
	Page<BankDetails> findAllByCompanyIdOrderByBankDetailsName(Pageable pageable);

	@Query("select bankDetails from BankDetails bankDetails where bankDetails.company.id = ?#{principal.companyId}")
	Page<BankDetails> findAllByCompany(Pageable pageable);

	@Query("select bankDetails from BankDetails bankDetails where bankDetails.company.id = ?#{principal.companyId}")
	List<BankDetails> findByCompany();
}
