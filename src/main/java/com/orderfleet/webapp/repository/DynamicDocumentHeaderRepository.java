package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * Spring Data JPA repository for the DynamicDocumentHeader entity.
 * 
 * @author Muhammed Riyas T
 * @since August 09, 2016
 */
public interface DynamicDocumentHeaderRepository extends JpaRepository<DynamicDocumentHeader, Long> {

	Optional<DynamicDocumentHeader> findOneByPid(String pid);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} Order By dDocument.createdDate desc")
	List<DynamicDocumentHeader> findAllByCompanyIdOrderByCreatedDateDesc();

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId}")
	Page<DynamicDocumentHeader> findAllByCompanyIdOrderByCreatedDateDesc(Pageable pageable);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} and dDocument.createdDate between ?1 and ?2 Order By dDocument.createdDate desc")
	List<DynamicDocumentHeader> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} and dDocument.createdBy.pid = ?1 and dDocument.document.pid = ?2 and dDocument.createdDate between ?3 and ?4 Order By dDocument.createdDate desc")
	List<DynamicDocumentHeader> findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} and dDocument.createdBy.pid = ?1 and dDocument.createdDate between ?2 and ?3 Order By dDocument.createdDate desc")
	List<DynamicDocumentHeader> findAllByCompanyIdUserPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} and dDocument.document.pid = ?1 and dDocument.createdDate between ?2 and ?3 Order By dDocument.createdDate desc")
	List<DynamicDocumentHeader> findAllByCompanyIdDocumentPidAndDateBetweenOrderByCreatedDateDesc(String documentPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} and dDocument.createdBy.pid = ?1 and dDocument.document.pid = ?2 and dDocument.tallyDownloadStatus in ?3 and dDocument.createdDate between ?4 and ?5 Order By dDocument.createdDate desc")
	List<DynamicDocumentHeader> findAllByCompanyIdUserPidDocumentPidAndTallyDownloadStatusAndDateBetweenOrderByCreatedDateDesc(
			String userPid, String documentPid, List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} and dDocument.createdBy.pid = ?1 and dDocument.tallyDownloadStatus in ?2 and dDocument.createdDate between ?3 and ?4 Order By dDocument.createdDate desc")
	List<DynamicDocumentHeader> findAllByCompanyIdUserPidAndTallyDownloadStatusAndDateBetweenOrderByCreatedDateDesc(
			String userPid, List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} and dDocument.document.pid = ?1 and dDocument.tallyDownloadStatus in ?2 and dDocument.createdDate between ?3 and ?4 Order By dDocument.createdDate desc")
	List<DynamicDocumentHeader> findAllByCompanyIdDocumentPidAndTallyDownloadStatusAndDateBetweenOrderByCreatedDateDesc(
			String documentPid, List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select dDocument.pid,dDocument.document.name,dDocument.document.documentType from DynamicDocumentHeader dDocument where dDocument.executiveTaskExecution.id = ?1")
	List<Object[]> findByExecutiveTaskExecutionId(Long executiveTaskExecutionId);

	List<DynamicDocumentHeader> findByExecutiveTaskExecutionPidAndDocumentPid(String executiveTaskExecutionPid,
			String documentPid);

	List<DynamicDocumentHeader> findByExecutiveTaskExecutionPidAndDocumentEditableTrue(
			String executiveTaskExecutionPid);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} and dDocument.document.name = ?1 and status=false Order By dDocument.createdDate desc")
	List<DynamicDocumentHeader> findAllByCompanyIdAndDocumentNameAndStatusFalseOrderByCreatedDateDesc(
			String documentName);

	List<DynamicDocumentHeader> findByExecutiveTaskExecutionAccountProfilePidAndDocumentPid(String accountProfilePid,
			String documentPid);

	List<DynamicDocumentHeader> findByExecutiveTaskExecutionAccountProfilePidAndDocumentIn(String accountProfilePid,
			List<Document> documents);

	List<DynamicDocumentHeader> findByExecutiveTaskExecutionActivityPidAndExecutiveTaskExecutionAccountProfilePidAndExecutiveTaskExecutionUserLoginAndDocumentPid(
			String activityPid, String accountProfilePid, String userLogin, String documentPid);

	DynamicDocumentHeader findTop1ByCreatedByLogin(String login);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} and dDocument.executiveTaskExecution.pid = ?1 and dDocument.document.name = ?2 and status=false")
	DynamicDocumentHeader findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(String executiveTaskExecutionPid,
			String documentName);

	DynamicDocumentHeader findTop1ByExecutiveTaskExecutionAccountProfilePidAndDocumentPidOrderByCreatedDateDesc(
			String accountProfilePid, String documentPid);

	Long countByCreatedDateBetweenAndDocumentIn(LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	Long countByCreatedDateBetweenAndDocumentInAndCreatedByPid(LocalDateTime fromDate, LocalDateTime toDate,
			List<Document> documents, String userPid);

	Long countByCreatedDateBetweenAndDocumentInAndExecutiveTaskExecutionExecutiveTaskPlanIsNotNull(
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	Long countByCreatedDateBetweenAndDocumentInAndCreatedByPidAndExecutiveTaskExecutionExecutiveTaskPlanIsNotNull(
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents, String userPid);

	Long countByCreatedDateBetweenAndDocumentInAndExecutiveTaskExecutionExecutiveTaskPlanIsNull(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	Long countByCreatedDateBetweenAndDocumentInAndCreatedByPidAndExecutiveTaskExecutionExecutiveTaskPlanIsNull(
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents, String userPid);

	DynamicDocumentHeader findTop1ByDocumentPidAndCreatedByLogin(String documentPid, String login);

	@Query("select dDocument.document from DynamicDocumentHeader dDocument where dDocument.createdBy.id in ?1")
	Set<Document> findDocumentsByUserIdIn(List<Long> userIds);

	// User wise - start
	Long countByCreatedDateBetweenAndDocumentInAndCreatedByIdIn(LocalDateTime fromDate, LocalDateTime toDate,
			List<Document> documents, List<Long> userIds);

	Long countByCreatedDateBetweenAndDocumentInAndCreatedByIdInAndExecutiveTaskExecutionExecutiveTaskPlanIsNotNull(
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents, List<Long> userIds);

	Long countByCreatedDateBetweenAndDocumentInAndCreatedByIdInAndExecutiveTaskExecutionExecutiveTaskPlanIsNull(
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents, List<Long> userIds);

	// Account wise
	Long countByCreatedDateBetweenAndDocumentInAndExecutiveTaskExecutionAccountProfileIn(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents, List<AccountProfile> accountProfiles);

	Long countByCreatedDateBetweenAndDocumentInAndExecutiveTaskExecutionAccountProfileInAndExecutiveTaskExecutionExecutiveTaskPlanIsNotNull(
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents,
			List<AccountProfile> accountProfiles);

	Long countByCreatedDateBetweenAndDocumentInAndExecutiveTaskExecutionAccountProfileInAndExecutiveTaskExecutionExecutiveTaskPlanIsNull(
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents,
			List<AccountProfile> accountProfiles);

	// dynamicDocumentReport
	List<DynamicDocumentHeader> findByFilledFormsIn(List<FilledForm> filledForms);

	@Query("select dDocument from DynamicDocumentHeader dDocument where dDocument.company.id = ?#{principal.companyId} and dDocument.executiveTaskExecution.pid = ?1")
	List<DynamicDocumentHeader> findAllByExecutiveTaskExecutionPid(String executiveTaskExecutionPid);

	// Activities and Transaction filter by document
	@Query("select dDocument.pid,dDocument.document.name,dDocument.document.documentType from DynamicDocumentHeader dDocument where dDocument.executiveTaskExecution.id = ?1 and dDocument.document.pid = ?2")
	List<Object[]> findByExecutiveTaskExecutionIdAndDocumentPid(Long executiveTaskExecutionId, String documentPid);

	@Query("select dynamicDocumentHeader from DynamicDocumentHeader dynamicDocumentHeader where dynamicDocumentHeader.company.id = ?#{principal.companyId}  and tally_download_status = 'PENDING' Order By dynamicDocumentHeader.createdDate desc")
	List<DynamicDocumentHeader> findByCompanyAndStatusOrderByCreatedDateDesc();

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE DynamicDocumentHeader dynamicDocumentHeader SET dynamicDocumentHeader.tallyDownloadStatus = ?1 WHERE dynamicDocumentHeader.company.id = ?#{principal.companyId} AND dynamicDocumentHeader.pid in ?2")
	int updateDynamicDocumentHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus tallyDownloadStatus,
			List<String> dynamicDocumentPids);

	@Query("select dd.pid,dd.document.name,dd.document.pid from DynamicDocumentHeader dd where dd.company.id = ?#{principal.companyId} and dd.executiveTaskExecution.pid = ?1")
	List<Object[]> findDynamicDocumentsHeaderByExecutiveTaskExecutionPid(String exeTasKPid);

	@Query("select dd from DynamicDocumentHeader dd where dd.company.id = ?#{principal.companyId} and dd.pid = ?1 Order By dd.createdDate desc")
	List<DynamicDocumentHeader> findDynamicDocumentHeaderByPid(String dynamicDocumentHeaderPid);

	@Query("select dd.pid,dd.document.name,dd.document.pid from DynamicDocumentHeader dd where dd.company.id = ?#{principal.companyId} and dd.executiveTaskExecution.id in ?1")
	List<Object[]> findDynamicDocumentsHeaderByExecutiveTaskExecutionIdin(Set<Long> exeIds);

	@Query(value = "select count(*),doc.pid from tbl_dynamic_document_header ddh " + 
			"inner join tbl_document doc on ddh.document_id = doc.id " + 
			"where ddh.company_id = ?#{principal.companyId} and ddh.created_by_id = ?1 " + 
			"and ddh.created_date between ?2 and ?3  group by doc.pid",nativeQuery = true)
	List<Object[]> findCountOfEachDynamicTypeDocuments(long userId,LocalDateTime fDate,LocalDateTime tDate);
	
	// @Query("select
	// dDocument.pid,dDocument.documentNumberLocal,dDocument.documentNumberServer,dDocument.document.pid,"
	// +
	// "dDocument.document.name,dDocument.createdDate,dDocument.documentDate,dDocument.employee.pid,dDocument.employee.name,"
	// +
	// "dDocument.createdBy.firstName,dDocument.createdBy.pid,dDocument.executiveTaskExecution.activity.name,"
	// +
	// "dDocument.executiveTaskExecution.accountProfile.name,dDocument.employee.phone,dDocument.executiveTaskExecution.accountProfile.address,"
	// +
	// "dDocument.executiveTaskExecution.accountProfile.phone,dDocument.executiveTaskExecution.accountProfile.email,"
	// +
	// "dDocument.status from DynamicDocumentHeader dDocument where
	// dDocument.company.id = ?#{principal.companyId} and "+
	// "dDocument.document.id in ?1 and dDocument.accountProfile.pid = ?2")
	// List<Object[]> findAllByAccountProfilePidAndDocumentIn(Set<Long> documentPids
	// ,String accountProfilePid);
}
