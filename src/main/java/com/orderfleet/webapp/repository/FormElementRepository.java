package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FormElement;

/**
 * Spring Data JPA repository for the FormElement entity.
 * 
 * @author Muhammed Riyas T
 * @since June 23, 2016
 */
public interface FormElementRepository extends JpaRepository<FormElement, Long> {

	Optional<FormElement> findOneByPid(String pid);

	Optional<FormElement> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	@Query("select formElement from FormElement formElement where formElement.company.id = ?#{principal.companyId}")
	List<FormElement> findAllByCompanyId();

	@Query("select formElement from FormElement formElement where formElement.company.id = ?#{principal.companyId}")
	Page<FormElement> findAllByCompanyId(Pageable pageable);

	List<FormElement> findAllByCompanyPid(String companyPid);
	
	@Query("select formElement from FormElement formElement where formElement.company.id = ?#{principal.companyId} and formElement.id NOT IN ?1")
	List<FormElement> findAllByCompanyIdAndNotFormElementIn(List<Long> formElementIds);
	
	@Query("select formElement from FormElement formElement where formElement.company.id = ?#{principal.companyId} Order By formElement.name asc")
	Page<FormElement> findAllByCompanyIdOrderByFormElementName(Pageable pageable);
	
	@Query("select formElement from FormElement formElement where formElement.company.id = ?#{principal.companyId} and formElement.activated = ?1 Order By formElement.name asc")
	Page<FormElement> findAllByCompanyIdAndActivatedFormElementOrderByName(Pageable pageable,boolean active);
	
	@Query("select formElement from FormElement formElement where formElement.company.id = ?#{principal.companyId} and formElement.activated = ?1 Order By formElement.id asc")
	List<FormElement> findAllByCompanyIdAndDeactivatedFormElement(boolean deactive);
	
	@Query("select formElement from FormElement formElement where formElement.company.id = ?#{principal.companyId} and formElement.pid IN ?1")
	List<FormElement> findAllByCompanyIdAndFormElementPidIn(List<String> formElementPids);
	
	@Query("select formElement from FormElement formElement where formElement.company.id = ?1 and formElement.formLoadFromMobile = TRUE")
	List<FormElement> findAllByCompanyIdAndLoadFromMobile(long companyId);

	List<FormElement> findAllByCompanyIdAndFormElementTypeId(Long companyId,Long formElementTypeId);
}
