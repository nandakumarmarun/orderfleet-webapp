package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Designation;

/**
 * Spring Data JPA repository for the Designation entity.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
public interface DesignationRepository extends JpaRepository<Designation, Long> {

	Optional<Designation> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<Designation> findOneByPid(String pid);
	
	Designation findFirstByCompanyId(Long id);

	@Query("select designation from Designation designation where designation.company.id = ?#{principal.companyId}")
	List<Designation> findAllByCompanyId();

	@Query("select designation from Designation designation where designation.company.id = ?#{principal.companyId}")
	Page<Designation> findAllByCompanyId(Pageable pageable);

	List<Designation> findAllByCompanyPid(String companyPid);

	@Query("select designation from Designation designation where designation.company.id = ?#{principal.companyId} Order By designation.name asc")
	Page<Designation> findAllByCompanyIdOrderByDesignationName(Pageable pageable);

	@Query("select designation from Designation designation where designation.company.id = ?#{principal.companyId} and designation.activated = ?1 Order By designation.name asc")
	Page<Designation> findAllByCompanyAndActivatedDesignationOrderByName(Pageable pageable, boolean active);

	@Query("select designation from Designation designation where designation.company.id = ?#{principal.companyId} and designation.activated = ?1")
	List<Designation> findAllByCompanyAndDeactivatedDesignation(boolean deactive);
}
