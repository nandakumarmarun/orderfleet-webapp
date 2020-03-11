package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SubFormElement;

public interface SubFormElementRepository extends JpaRepository<SubFormElement, Long>{

	public static final String ASSIGNED_SUB_FORMS = "select sub_frm_elmt.pid  from tbl_sub_form_element sub " + 
			"INNER JOIN tbl_document doc on doc.id = sub.document_id " + 
			"INNER JOIN tbl_form form on form.id = sub.form_id " + 
			"INNER JOIN tbl_form_element frm_elmt on frm_elmt.id = sub.form_element_id " + 
			"INNER JOIN tbl_form_element_values fev on fev.id = sub.form_element_value_id " + 
			"INNER JOIN tbl_form_element sub_frm_elmt on sub_frm_elmt.id = sub.sub_form_element_id " + 
			"where sub.company_id = ?1 and doc.pid = ?2 and form.pid = ?3 and " +
			"frm_elmt.pid = ?4 and fev.id = ?5";
	@Query(value = ASSIGNED_SUB_FORMS , nativeQuery = true)
	List<String> findAllAssignedAccounts(Long companyId ,String documentPid,String formPid,String formElement,Long formElementValueId );
	
	@Query("select subFormElement from SubFormElement subFormElement where subFormElement.company.id = ?#{principal.companyId}")
	List<SubFormElement> findAllByCompanyId();
	
	@Query("delete from SubFormElement subFormElement where "
			+ "subFormElement.document.id = ?1 and subFormElement.form.id = ?2 and subFormElement.formElement.id = ?3"
			+ " and subFormElement.formElementValue.id = ?4 and subFormElement.company.id = ?#{principal.companyId}")
	
	@Modifying
	void deleteAllAssociatedFormElements(long docId,long formId,long formElementId,long formElementValueId);
}
