package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Department;

/**
 * Spring Data JPA repository for the Department entity.
 * 
 * @author Muhammed Riyas T
 * @since May 30, 2016
 */
public interface DepartmentRepository extends JpaRepository<Department, Long> {

	Optional<Department> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<Department> findOneByPid(String pid);
	
	Department findFirstByCompanyId(Long id);

	@Query("select department from Department department where department.company.id = ?#{principal.companyId}")
	List<Department> findAllByCompanyId();

	@Query("select department from Department department where department.company.id = ?#{principal.companyId}")
	Page<Department> findAllByCompanyId(Pageable pageable);

	List<Department> findAllByCompanyPid(String companyPid);
	
	@Query("select department from Department department where department.company.id = ?#{principal.companyId} Order By department.name asc")
	Page<Department> findAllByCompanyIdOrderByDepartmentName(Pageable pageable);
	
	@Query("select department from Department department where department.company.id = ?#{principal.companyId} and department.activated = ?1 Order By department.name asc")
	Page<Department> findAllByCompanyAndActivatedDepartmentOrderByName(Pageable pageable,boolean active);
	
	@Query("select department from Department department where department.company.id = ?#{principal.companyId} and department.activated = ?1")
	List<Department> findAllByCompanyAndDeactivatedDepartment(boolean deactive);
}
