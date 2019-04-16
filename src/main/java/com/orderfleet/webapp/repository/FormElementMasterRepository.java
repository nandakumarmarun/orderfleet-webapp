package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FormElementMaster;

/**
 * Spring Data JPA repository for the FormElementMaster entity.
 * 
 * @author Muhammed Riyas T
 * @since November 02, 2016
 */
public interface FormElementMasterRepository extends JpaRepository<FormElementMaster, Long> {

	Optional<FormElementMaster> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<FormElementMaster> findOneByPid(String pid);

	@Query("select formElementMaster from FormElementMaster formElementMaster where formElementMaster.company.id = ?#{principal.companyId}")
	List<FormElementMaster> findAllByCompanyId();

	@Query("select formElementMaster from FormElementMaster formElementMaster where formElementMaster.company.id = ?#{principal.companyId}")
	Page<FormElementMaster> findAllByCompanyId(Pageable pageable);

	@Query("select formElementMaster.name from FormElementMaster formElementMaster where formElementMaster.company.id = ?#{principal.companyId} and formElementMaster.formElementMasterType.pid = ?1")
	List<String> findAllNamesByCompanyIdAndTypePid(String masterTypePid);
}
