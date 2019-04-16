package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.StaticFormJSCode;

/**
 * repository for StaticFormJSCode
 * 
 * @author Sarath
 * @since Aug 3, 2016
 */
public interface StaticFormJSCodeRepository extends JpaRepository<StaticFormJSCode, Long> {

	Optional<StaticFormJSCode> findOneById(Long id);

	@Query("select staticFormJSCode from StaticFormJSCode staticFormJSCode where staticFormJSCode.company.id = ?#{principal.companyId}")
	List<StaticFormJSCode> findAllByCompanyId();

	@Query("select staticFormJSCode from StaticFormJSCode staticFormJSCode where staticFormJSCode.company.id = ?#{principal.companyId}")
	Page<StaticFormJSCode> findAllByCompanyId(Pageable pageable);

	StaticFormJSCode findByJsCodeName(String jsCodeName);

	@Query("select staticFormJSCode from StaticFormJSCode staticFormJSCode where staticFormJSCode.document.pid = ?1 and staticFormJSCode.company.id = ?#{principal.companyId}  ")
	StaticFormJSCode findByDocumentPidAndCompany(String documentPid);

	StaticFormJSCode findByCompanyPidAndDocumentPid(String companyPid, String documentPid);

}
