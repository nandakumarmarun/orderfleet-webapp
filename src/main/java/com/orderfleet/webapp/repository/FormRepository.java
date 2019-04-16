package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Form;

/**
 * Spring Data JPA repository for the Form entity.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
public interface FormRepository extends JpaRepository<Form, Long> {

	Optional<Form> findOneByPid(String pid);

	Optional<Form> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	@Query("select form from Form form where form.company.id = ?#{principal.companyId}")
	List<Form> findAllByCompanyId();

	@Query("select form from Form form where form.company.id = ?#{principal.companyId}")
	Page<Form> findAllByCompanyId(Pageable pageable);

	List<Form> findAllByCompanyPid(String companyPid);

	@Query("select form from Form form where form.company.id = ?#{principal.companyId} and form.activated = ?1 Order By form.name asc")
	Page<Form> findAllByCompanyIdAndActivatedFormOrderByName(Pageable pageable, boolean active);

	@Query("select form from Form form where form.company.id = ?#{principal.companyId} and form.activated = ?1")
	List<Form> findAllByCompanyIdAndDeactivatedForm(boolean deactive);

	@Query("select form from Form form where form.company.id = ?#{principal.companyId} and form.lastModifiedDate > ?1")
	List<Form> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate);
}
