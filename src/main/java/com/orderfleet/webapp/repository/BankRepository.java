package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Bank;

/**
 * Spring Data JPA repository for the Bank entity.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
public interface BankRepository extends JpaRepository<Bank, Long> {

	Optional<Bank> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<Bank> findOneByPid(String pid);

	@Query("select bank from Bank bank where bank.company.id = ?#{principal.companyId}")
	List<Bank> findAllByCompanyId();

	@Query("select bank from Bank bank where bank.company.id = ?#{principal.companyId}")
	Page<Bank> findAllByCompanyId(Pageable pageable);

	@Query("select bank from Bank bank where bank.company.id = ?#{principal.companyId} Order By bank.name asc")
	Page<Bank> findAllByCompanyIdOrderByBankName(Pageable pageable);
	
	@Query("select bank from Bank bank where bank.company.id = ?#{principal.companyId} and bank.activated = ?1 Order By bank.name asc")
	Page<Bank> findAllByCompanyAndActivatedBankOrderByName(Pageable pageable,boolean active);
	
	@Query("select bank from Bank bank where bank.company.id = ?#{principal.companyId} and bank.activated = ?1")
	List<Bank> findAllByCompanyAndDeactivatedBank(boolean deactive);
}
