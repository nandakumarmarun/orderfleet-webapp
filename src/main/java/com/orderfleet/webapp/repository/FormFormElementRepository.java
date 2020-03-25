package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormFormElement;

/**
 * Spring Data JPA repository for the FormFormElement.
 * 
 * @author Muhammed Riyas T
 * @since July 22 2016
 */

public interface FormFormElementRepository extends JpaRepository<FormFormElement, Long> {

	List<FormFormElement> findByFormPid(String pid);

	@Query("SELECT f FROM FormFormElement AS f WHERE f.form.pid = ?1 AND f.reportOrder > 0 ORDER BY f.reportOrder ASC")
	List<FormFormElement> findByFormPidAndReportOrderGreaterThanZero(String pid);

	@Query("select formFormElement from FormFormElement formFormElement where formFormElement.company.id = ?#{principal.companyId}")
	List<FormFormElement> findAllByCompanyId();

	void deleteByFormPid(String formPid);

	@Query("select ffElement from FormFormElement ffElement where ffElement.company.id = ?#{principal.companyId} and ffElement.form in ?1 and ffElement.formElement.formElementType.name = 'datePicker'")
	List<FormFormElement> findByCompanyIdAndFormsAndFormElementTypeIsDatePicker(List<Form> forms);

	@Query("select ffElement from FormFormElement ffElement where ffElement.company.id = ?#{principal.companyId} and ffElement.form in ?1 order by ffElement.sortOrder asc")
	List<FormFormElement> findByCompanyIdAndFormsIn(List<Form> forms);

	List<FormFormElement> findAllByCompanyPid(String companyPid);

	@Query("select ffElement.formElement from FormFormElement ffElement where ffElement.company.id = ?#{principal.companyId} and ffElement.form in ?1 order by ffElement.sortOrder asc")
	List<FormElement> findFormElementsByCompanyIdAndFormsIn(List<Form> forms);

	@Query("SELECT f.formElement FROM FormFormElement AS f WHERE f.form.pid = ?1 ORDER BY f.reportOrder ASC")
	List<FormElement> findFormElementsByFormPid(String pid);

	@Query("select formFormElement from FormFormElement formFormElement where formFormElement.company.id = ?#{principal.companyId} and formFormElement.lastModifiedDate > ?1")
	List<FormFormElement> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate);

	@Query("SELECT f FROM FormFormElement AS f WHERE f.form.pid in ?1 ORDER BY f.reportOrder ASC")
	List<FormFormElement> findByFormPidIn(List<String> formPids);
	
	@Query("SELECT f FROM FormFormElement AS f WHERE f.formElement.pid = ?1 ORDER BY f.reportOrder ASC")
	List<FormFormElement>findByFormElementPid(String formElementPid);

	List<FormFormElement> findByFormPidAndDashboardVisibility(String formPid, boolean dashboardVisibility);
}
