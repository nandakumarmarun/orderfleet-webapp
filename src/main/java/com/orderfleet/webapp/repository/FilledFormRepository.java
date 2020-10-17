package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.FilledFormDetail;

/**
 * Spring Data JPA repository for the FilledForm entity.
 * 
 * @author Muhammed Riyas T
 * @since August 01, 2016
 */
public interface FilledFormRepository extends JpaRepository<FilledForm, Long> {

	Optional<FilledForm> findOneByPid(String pid);

	Optional<FilledForm> findOneByDynamicDocumentHeaderExecutiveTaskExecutionPidAndImageRefNo(
			String executiveTaskExecutionPid, String imageRefNo);

	@Query("SELECT f FROM FilledForm AS f WHERE f.dynamicDocumentHeader.document.pid = ?1 and f.form.pid = ?2")
	List<FilledForm> findFilledFormsByDocumentAndFormPid(String documentPid, String formPid);

	@Query("select filledForm.filledFormDetails from FilledForm filledForm where filledForm.dynamicDocumentHeader.document.pid = ?1 and filledForm.form.pid = ?2")
	List<FilledFormDetail> findFilledFormDetailsByDocumentAndFormPid(String documentPid, String formPid);

	List<FilledForm> findByDynamicDocumentHeaderPid(String dynamicDocumentPid);

	List<FilledForm> findByDynamicDocumentHeaderPidIn(Set<String> dynamicDocumentPids);

	List<FilledForm> findByDynamicDocumentHeaderDocumentPid(String documentPid);

	Optional<FilledForm> findOneByDynamicDocumentHeaderExecutiveTaskExecutionPidAndFormPid(
			String executiveTaskExecutionPid, String formPid);

	@Query("SELECT f FROM FilledForm AS f WHERE f.dynamicDocumentHeader.document.pid = ?1 and f.form.pid = ?2 and f.dynamicDocumentHeader.createdDate between ?3 and ?4")
	List<FilledForm> findFilledFormsByDocumentAndFormPidAndCreatedDateBetween(String documentPid, String formPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("SELECT f FROM FilledForm AS f WHERE f.dynamicDocumentHeader.document.pid = ?1 and f.form.pid = ?2 and f.dynamicDocumentHeader.createdBy.pid = ?3 and f.dynamicDocumentHeader.createdDate between ?4 and ?5")
	List<FilledForm> findFilledFormsByDocumentAndFormPidAndCreatedByAndCreatedDateBetween(String documentPid,
			String formPid, String userPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "SELECT count(f.id) > 0 FROM tbl_filled_form f cross join tbl_dynamic_document_header dh WHERE f.dynamic_document_header_id = dh.id and dh.pid = ?1 and (f.id IN (select files.filled_form_id from tbl_filled_form_file files where f.id=files.filled_form_id))", nativeQuery = true)
	boolean existsByHeaderPidIfFiles(String headerPid);

	// New AccountProfile Creation
	@Query("select filledForm.filledFormDetails from FilledForm filledForm where filledForm.dynamicDocumentHeader.document.pid = ?1 and filledForm.form.pid = ?2 and filledForm.dynamicDocumentHeader.executiveTaskExecution.pid = ?3")
	List<FilledFormDetail> findFilledFormDetailsByDocumentAndFormPidAndExecutiveTaskExecution(String documentPid,
			String formPid, String exePid);

	@Query("SELECT f.files FROM FilledForm AS f WHERE f.dynamicDocumentHeader.document.pid = ?1")
	Set<File> findFilesByDynamicDocumentHeaderPid(String dynamicDocumentPid);

	@Query("SELECT f FROM FilledForm AS f WHERE f.dynamicDocumentHeader.document.pid = ?1 and f.form.pid = ?2 and f.dynamicDocumentHeader.createdBy.pid IN ?3 and f.dynamicDocumentHeader.createdDate between ?4 and ?5")
	List<FilledForm> findFilledFormsByDocumentAndFormPidAndAndCreatedDateBetween(String documentPid, String formPid,
			List<String> userPids, LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select ff.pid as formFieldPid,ddh.pid from tbl_filled_form ff,tbl_dynamic_document_header ddh where "
			+ "ff.dynamic_document_header_id = ddh.id and ddh.pid IN ?1 ", nativeQuery = true)
	List<Object[]> findFilleFormPidAndDynamicDocumentHeaderPidByCompany(Set<String> dynamicDocHeaderPid);

	@Query(value = "select ff.id as filledFormIid,ff.dynamic_document_header_id as dynamicDocumentHeaderId from tbl_filled_form ff where "
			+ "ff.dynamic_document_header_id = ddh.id and ddh.pid IN ?1 ", nativeQuery = true)
	List<Object[]> findFilledFormsByDocumentIdAndFormIddAndCreatedDateBetween(long documentId, long formId,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("SELECT f.id,f.dynamicDocumentHeader.id FROM FilledForm AS f WHERE f.dynamicDocumentHeader.document.pid = ?1 and f.form.pid = ?2 and f.dynamicDocumentHeader.createdDate between ?3 and ?4")
	List<Object[]> findFilledFormsIdsByDocumentAndFormPidAndCreatedDateBetween(String documentPid, String formPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("SELECT f.id,f.dynamicDocumentHeader.id FROM FilledForm AS f WHERE f.dynamicDocumentHeader.document.pid = ?1 and f.form.pid = ?2 and f.dynamicDocumentHeader.createdBy.pid IN ?3 and f.dynamicDocumentHeader.createdDate between ?4 and ?5")
	List<Object[]> findFilledFormsIdsByDocumentAndFormPidAndUserPidCreatedByAndCreatedDateBetween(String documentPid,
			String formPid, List<String> userPids, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("SELECT f.dynamicDocumentHeader.id FROM FilledForm AS f WHERE id = ?1")
	Long findDynamicDocumentHeaderById(Long ffId);

	@Query("select filledForm.filledFormDetails from FilledForm filledForm where filledForm.id IN ?1")
	List<FilledFormDetail> findFilledFormDetailsByFormIdsIn(Set<Long> filledFormIds);
	
	@Query(value ="select ffid  AS FilledFormId, ddhid AS DynamicDocumentHeaderId from ("
			+ "select distinct on (temp.accountprofileid) temp.accountprofileid, temp.accountprofilename,temp.DDHCreatedDate, temp.ffid,temp.ddhid  from (select exe.account_profile_id as AccountProfileId, acc.name as AccountProfileName, ddh.created_date as DDHCreatedDate, ff.id as ffid, ddh.id as ddhid from tbl_filled_form ff "
			+ "inner join tbl_dynamic_document_header ddh on ff.dynamic_document_header_id=ddh.id "
			+ "inner join tbl_document doc on ddh.document_id = doc.id "
			+ "inner join tbl_form form on ff.form_id = form.id "
			+ "inner join tbl_user usr on ddh.created_by_id = usr.id "
			+ "inner join tbl_executive_task_execution exe on ddh.executive_task_execution_id = exe.id "
			+ "inner join tbl_account_profile acc on exe.account_profile_id = acc.id "
			+ "where doc.pid = ?1 and form.pid = ?2 "
			+ "and ddh.created_date between ?3 and ?4) as temp order by temp.accountprofileid, temp.ddhcreateddate desc "
			+ ") as temp1 order by accountprofilename", nativeQuery = true)
	List<Object[]> findFilledFormsIdsByDocumentAndFormPidAndCreatedDateBetweenFilterAndOrderByAccount(String documentPid, String formPid,
			LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query(value ="select ffid  AS FilledFormId, ddhid AS DynamicDocumentHeaderId from ("
			+ "select distinct on (temp.accountprofileid) temp.accountprofileid, temp.accountprofilename,temp.DDHCreatedDate, temp.ffid,temp.ddhid  from (select exe.account_profile_id as AccountProfileId, acc.name as AccountProfileName, ddh.created_date as DDHCreatedDate, ff.id as ffid, ddh.id as ddhid from tbl_filled_form ff "
			+ "inner join tbl_dynamic_document_header ddh on ff.dynamic_document_header_id=ddh.id "
			+ "inner join tbl_document doc on ddh.document_id = doc.id "
			+ "inner join tbl_form form on ff.form_id = form.id "
			+ "inner join tbl_user usr on ddh.created_by_id = usr.id "
			+ "inner join tbl_executive_task_execution exe on ddh.executive_task_execution_id = exe.id "
			+ "inner join tbl_account_profile acc on exe.account_profile_id = acc.id "
			+ "where doc.pid = ?1 and form.pid = ?2 "
			+ "and usr.pid in ?3 and ddh.created_date between ?4 and ?5) as temp order by temp.accountprofileid, temp.ddhcreateddate desc "
			+ ") as temp1 order by accountprofilename", nativeQuery = true)
	List<Object[]> findFilledFormsIdsByDocumentAndFormPidAndUserPidCreatedByAndCreatedDateBetweenFilterAndOrderByAccount(String documentPid,
			String formPid, List<String> userPids, LocalDateTime fromDate, LocalDateTime toDate);
	
	@Query(value = "select ff.id AS FilledFormId, ddh.id AS DynamicDocumentHeaderId from tbl_filled_form ff "
			+ "inner join tbl_dynamic_document_header ddh on ff.dynamic_document_header_id=ddh.id "
			+ "inner join tbl_document doc on ddh.document_id = doc.id "
			+ "inner join tbl_form form on ff.form_id = form.id "
			+ "inner join tbl_executive_task_execution exe on ddh.executive_task_execution_id = exe.id "
			+ "inner join tbl_account_profile acc on exe.account_profile_id = acc.id "
			+ "where doc.pid = ?1 and form.pid = ?2 "
			+ "and acc.pid = ?5 "
			+ "and ddh.created_date between ?3 and ?4 order by ddh.created_date desc limit 1",  nativeQuery = true)
	List<Object[]> findFilledFormsIdsByDocumentAndFormPidAndAccountPidInAndCreatedDateBetween(String documentPid, String formPid, 
			LocalDateTime fromDate, LocalDateTime toDate, String accountPid);
	
	@Query(value = "select ff.id AS FilledFormId, ddh.id AS DynamicDocumentHeaderId from tbl_filled_form ff "
			+ "inner join tbl_dynamic_document_header ddh on ff.dynamic_document_header_id=ddh.id "
			+ "inner join tbl_document doc on ddh.document_id = doc.id "
			+ "inner join tbl_form form on ff.form_id = form.id "
			+ "inner join tbl_user usr on ddh.created_by_id = usr.id "
			+ "inner join tbl_executive_task_execution exe on ddh.executive_task_execution_id = exe.id "
			+ "inner join tbl_account_profile acc on exe.account_profile_id = acc.id "
			+ "where doc.pid = ?1 and form.pid = ?2 "
			+ "and usr.pid in ?3 and acc.pid = ?6 "
			+ "and ddh.created_date between ?4 and ?5 order by ddh.created_date desc limit 1",  nativeQuery = true)
	List<Object[]> findFilledFormsIdsByDocumentAndFormPidAndUserPidCreatedByAndAccountPidInAndCreatedDateBetween(String documentPid,
			String formPid, List<String> userPids, LocalDateTime fromDate, LocalDateTime toDate, String accountPid);
}
