package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Division;

/**
 * Spring Data JPA repository for the Division entity.
 * 
 * @author Shaheer
 * @since May 07, 2016
 */
public interface DivisionRepository extends JpaRepository<Division, Long> {

	Optional<Division> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<Division> findOneByPid(String pid);
	
	Division findFirstByCompanyId(Long id);

	@Query("select division from Division division where division.company.id = ?#{principal.companyId}")
	List<Division> findAllByCompanyId();

	@Query("select division from Division division where division.company.id = ?#{principal.companyId}")
	Page<Division> findAllByCompanyId(Pageable pageable);

	Optional<Division> findByCompanyIdAndAliasIgnoreCase(Long id, String alias);

	List<Division> findAllByCompanyPid(String companyPid);
	
	@Query("select division from Division division where division.company.id = ?#{principal.companyId} Order By division.name asc")
	Page<Division> findAllByCompanyIdOrderByDivisionName(Pageable pageable);
	
	@Query("select division from Division division where division.company.id = ?#{principal.companyId} and division.activated = ?1  Order By division.name asc")
	Page<Division> findAllByCompanyAndActivatedDivisionOrderByName(Pageable pageable,boolean active);
	
	@Query("select division from Division division where division.company.id = ?#{principal.companyId} and division.activated = ?1")
	List<Division> findAllByCompanyAndDeactivatedDivision(boolean deactive);
}
