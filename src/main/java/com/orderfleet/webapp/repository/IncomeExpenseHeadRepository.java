package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.orderfleet.webapp.domain.IncomeExpenseHead;

/**
 *Spring Data JPA repository for the IncomeExpenseHead entity
 *
 * @author fahad
 * @since Feb 15, 2017
 */
public interface IncomeExpenseHeadRepository extends JpaRepository<IncomeExpenseHead, Long>{

	Optional<IncomeExpenseHead> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<IncomeExpenseHead> findOneByPid(String pid);

	@Query("select incomeExpenseHead from IncomeExpenseHead incomeExpenseHead where incomeExpenseHead.company.id = ?#{principal.companyId}")
	List<IncomeExpenseHead> findAllByCompanyId();

	@Query("select incomeExpenseHead from IncomeExpenseHead incomeExpenseHead where incomeExpenseHead.company.id = ?#{principal.companyId}")
	Page<IncomeExpenseHead> findAllByCompanyId(Pageable pageable);

	@Query("select incomeExpenseHead from IncomeExpenseHead incomeExpenseHead where incomeExpenseHead.company.id = ?#{principal.companyId} Order By incomeExpenseHead.name asc")
	Page<IncomeExpenseHead> findAllByCompanyIdOrderByIncomeExpenseHeadName(Pageable pageable);
	
	@Query("select incomeExpenseHead from IncomeExpenseHead incomeExpenseHead where incomeExpenseHead.company.id = ?#{principal.companyId} and incomeExpenseHead.activated = ?1 Order By incomeExpenseHead.name asc")
	Page<IncomeExpenseHead> findAllByCompanyAndActivatedIncomeExpenseHeadOrderByName(Pageable pageable,boolean active);
	
	@Query("select incomeExpenseHead from IncomeExpenseHead incomeExpenseHead where incomeExpenseHead.company.id = ?#{principal.companyId} and incomeExpenseHead.activated = ?1")
	List<IncomeExpenseHead> findAllByCompanyAndDeactivatedIncomeExpenseHead(boolean deactive);
}
