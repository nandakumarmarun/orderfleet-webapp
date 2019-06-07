package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.EmployeeHierarchy;

/**
 * Spring Data JPA repository for the EmployeeHierarchy entity.
 * 
 * @author Shaheer
 * @since June 02, 2016
 */
public interface EmployeeHierarchyRepository extends JpaRepository<EmployeeHierarchy, Long> {

	@Query("select empHierarchy from EmployeeHierarchy empHierarchy where empHierarchy.company.id = ?#{principal.companyId} and empHierarchy.activated = 'TRUE'")
	List<EmployeeHierarchy> findByCompanyIdAndActivatedTrue();

	@Query("select empHierarchy from EmployeeHierarchy empHierarchy where empHierarchy.company.id = ?#{principal.companyId} and empHierarchy.activated = 'TRUE'")
	Page<EmployeeHierarchy> findByCompanyIdAndActivatedTrue(Pageable pageable);

	Optional<EmployeeHierarchy> findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(Long companyId);

	Long countByParentIdAndCompanyIdAndActivatedTrue(Long parentId, Long companyId);

	Optional<EmployeeHierarchy> findByEmployeeIdAndCompanyIdAndActivatedTrue(Long employeeId, Long companyId);

	EmployeeHierarchy findByEmployeePidAndActivatedTrue(String employeePid);
	
//	@Query(value = "WITH RECURSIVE children AS (SELECT activated,employee_id, parent_id FROM tbl_employee_hierarchy WHERE employee_id = ?1 UNION SELECT e.activated,e.employee_id, e.parent_id FROM tbl_employee_hierarchy e INNER JOIN children s ON s.employee_id = e.parent_id) SELECT COALESCE(employee_id, 0 ) FROM children WHERE activated = TRUE", nativeQuery = true)
	@Query(value = "WITH RECURSIVE children AS (SELECT activated,employee_id, parent_id FROM tbl_employee_hierarchy WHERE employee_id = ?1 UNION SELECT e.activated,e.employee_id, e.parent_id FROM tbl_employee_hierarchy e INNER JOIN children s ON s.employee_id = e.parent_id and e.activated = true) SELECT COALESCE(employee_id, 0 ) FROM children WHERE activated = TRUE", nativeQuery = true)
	List<Object> findChildrenByEmployeeIdAndActivatedTrue(Long employeeId);

	@Query("select empHierarchy from EmployeeHierarchy empHierarchy where empHierarchy.company.id = ?#{principal.companyId} and empHierarchy.activated = 'TRUE' and empHierarchy.parent.id = null")
	EmployeeHierarchy findEmployeeHierarchyRootByCompany();
	
	void deleteByEmployeePid(String employeePid);
}
