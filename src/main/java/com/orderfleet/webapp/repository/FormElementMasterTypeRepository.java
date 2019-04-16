package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FormElementMasterType;

/**
 * Spring Data JPA repository for the FormElementMasterType entity.
 *
 * @author Sarath
 * @since Nov 2, 2016
 */
public interface FormElementMasterTypeRepository extends JpaRepository<FormElementMasterType, Long> {

	Optional<FormElementMasterType> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<FormElementMasterType> findOneByPid(String pid);

	@Query("select formElementMasterType from FormElementMasterType formElementMasterType where formElementMasterType.company.id = ?#{principal.companyId}")
	List<FormElementMasterType> findAllByCompanyId();

	@Query("select formElementMasterType from FormElementMasterType formElementMasterType where formElementMasterType.company.id = ?#{principal.companyId}")
	Page<FormElementMasterType> findAllByCompanyId(Pageable pageable);

	@Query("select formElementMasterType from FormElementMasterType formElementMasterType where formElementMasterType.company.id = ?#{principal.companyId} and formElementMasterType.activated = ?1 Order By formElementMasterType.name asc")
	Page<FormElementMasterType> findAllByCompanyAndActivatedFormElementMasterTypeOrderByName(Pageable pageable,boolean active);
	
	@Query("select formElementMasterType from FormElementMasterType formElementMasterType where formElementMasterType.company.id = ?#{principal.companyId} and formElementMasterType.activated = ?1")
	List<FormElementMasterType> findAllByCompanyAndDeactivatedFormElementMasterType(boolean deactive);
}
