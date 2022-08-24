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
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * Spring Data JPA repository for the AccountingVoucherHeader entity.
 * 
 * @author Muhammed Riyas T
 * @since July 28, 2016
 */
public interface AccountingVoucherHeaderRepository extends JpaRepository<AccountingVoucherHeader, Long> {

	Optional<AccountingVoucherHeader> findOneByPid(String pid);

	@Query("select accountingVoucherHeader from AccountingVoucherHeader accountingVoucherHeader where accountingVoucherHeader.company.id = ?#{principal.companyId} Order By accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdOrderByCreatedDateDesc();

	@Query("select accountingVoucherHeader from AccountingVoucherHeader accountingVoucherHeader where accountingVoucherHeader.company.id = ?#{principal.companyId} Order By accountingVoucherHeader.createdDate desc")
	Page<AccountingVoucherHeader> findAllByCompanyIdOrderByCreatedDateDesc(Pageable pageable);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.createdDate between ?1 and ?2 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.createdBy.pid = ?1 and accVoucher.accountProfile.pid = ?2 and accVoucher.createdDate between ?3 and ?4 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByCreatedDateDesc(
			String userPid, String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.createdBy.pid = ?1 and accVoucher.createdDate between ?2 and ?3 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdUserPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate);

  @Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.accountProfile.pid = ?1 and accVoucher.createdDate between ?2 and ?3 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdAccountPidAndDateBetweenOrderByCreatedDateDesc(String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.createdBy.login = ?#{principal.username} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3")
	Double getCurrentUserAchievedAmount(List<Document> documents, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3")
	Object getCountAndAmountByDocumentsAndDateBetween(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.createdBy.pid = ?4 ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndUser(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate, String userPid);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndTaskPlanIsNull(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.createdBy.pid = ?4 and accVoucher.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndUserAndTaskPlanIsNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, String userPid);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndTaskPlanIsNotNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.createdBy.pid = ?4 and accVoucher.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndUserAndTaskPlanIsNotNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, String userPid);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.createdBy.pid = ?4 and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3")
	Object getCountAndAmountByDocumentsAndDateBetweenAndUserId(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate, String userPid);

	@Query("select accVoucher.pid,accVoucher.document.name,accVoucher.totalAmount,accVoucher.document.documentType from AccountingVoucherHeader accVoucher where accVoucher.executiveTaskExecution.id = ?1")
	List<Object[]> findByExecutiveTaskExecutionId(Long executiveTaskExecutionId);

  	 List<AccountingVoucherHeader> findByExecutiveTaskExecutionPidAndDocumentPid(String executiveTaskExecutionPid,
			String documentPid);

	List<AccountingVoucherHeader> findByExecutiveTaskExecutionPidAndDocumentEditableTrue(
			String executiveTaskExecutionPid);

	@Query("select accountingVoucherHeader from AccountingVoucherHeader accountingVoucherHeader where accountingVoucherHeader.company.id = ?#{principal.companyId} and status=false Order By accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdAndStatusOrderByCreatedDateDesc();

	@Query("select accountingVoucher from AccountingVoucherHeader accountingVoucher where accountingVoucher.company.id = ?#{principal.companyId} and accountingVoucher.executiveTaskExecution.pid=?1 Order By accountingVoucher.createdDate desc")
	List<AccountingVoucherHeader> findAllByExecutiveTaskExecutionPid(String executiveTaskExecutionPid);

	List<AccountingVoucherHeader> findByExecutiveTaskExecutionAccountProfilePidAndDocumentIn(String accountProfilePid,
			List<Document> documents);

	AccountingVoucherHeader findTop1ByCreatedByLoginOrderByCreatedDateDesc(String userLogin);

	List<AccountingVoucherHeader> findByExecutiveTaskExecutionAccountProfilePidAndDocumentPid(String accountProfilePid,
			String documentPid);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document.pid = ?1 and accVoucher.createdDate between ?2 and ?3 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdDocumentPidAndDateBetweenOrderByCreatedDateDesc(String documentPid,
			LocalDateTime fromDate, LocalDateTime toDate);

@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.createdBy.pid = ?1 and accVoucher.accountProfile.pid = ?2 and accVoucher.document.pid = ?3 and accVoucher.createdDate between ?4 and ?5 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(
			String userPid, String accountPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.createdBy.pid = ?1 and accVoucher.document.pid = ?2 and accVoucher.createdDate between ?3 and ?4 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(
			String userPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.accountProfile.pid = ?1 and accVoucher.document.pid = ?2 and accVoucher.createdDate between ?3 and ?4 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(
			String accountPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucher.document from AccountingVoucherHeader accVoucher where accVoucher.createdBy.id in ?1")
	Set<Document> findDocumentsByUserIdIn(List<Long> userIds);

	// User wise - start
	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.createdBy.id in ?4")
	Object getCountAndAmountByDocumentsAndDateBetweenAndUserIdIn(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate, List<Long> userIds);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.createdBy.id in ?4 and accVoucher.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndUserIdInAndTaskPlanIsNotNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, List<Long> userIds);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.createdBy.id in ?4 and accVoucher.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndUserIdInAndTaskPlanIsNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, List<Long> userIds);
	// User wise - end

	// #### Account wise start ######
	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.accountProfile in ?4 ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndAccountProfileIn(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, List<AccountProfile> accountProfiles);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.accountProfile in ?4 and accVoucher.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndAccountProfileInAndTaskPlanIsNotNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, List<AccountProfile> accountProfiles);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.accountProfile in ?4 and accVoucher.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndAccountProfileInAndTaskPlanIsNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, List<AccountProfile> accountProfiles);

	// #### Account wise end ######

	// for financial closing
	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document.pid = ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.accountProfile.pid = ?4 ")
	List<AccountingVoucherHeader> getByDocumentPidAndDateBetweenAndAccountProfilePid(String documentPid,
			LocalDateTime fromDate, LocalDateTime toDate, String accountProfilePid);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document.pid = ?1 and accVoucher.createdDate between ?2 and ?3 and accVoucher.createdBy.pid = ?4 ")
	List<AccountingVoucherHeader> getByDocumentPidAndDateBetweenAndUserPid(String documentPid, LocalDateTime fromDate,
			LocalDateTime toDate, String userPid);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document.pid = ?1 and accVoucher.accountProfile.pid = ?2 and  accVoucher.executiveTaskExecution.pid in ?3")
	AccountingVoucherHeader getByDocumentPidAndAccountProfilePidAndExecutiveTaskExecutionIn(String documentPid,
			String accountProfilePid, List<String> executiveTaskExecutionPids);

	// Activities and Transaction filter by document
	@Query("select accVoucher.pid,accVoucher.document.name,accVoucher.totalAmount,accVoucher.document.documentType from AccountingVoucherHeader accVoucher where accVoucher.executiveTaskExecution.id = ?1 and accVoucher.document.pid = ?2")
	List<Object[]> findByExecutiveTaskExecutionIdAndDocumentPid(Long executiveTaskExecutionId, String documentPid);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.documentDate=?1 and accVoucher.executiveTaskExecution.activity.pid=?2 and accVoucher.document.pid=?3 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> findAllByCompanyIdAndDocumentDateAndActivityAndDocumentOrderByCreatedDateDesc(
			LocalDateTime documentDate, String pid, String pid2);

	@Query("select count(accVoucher),sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.document in ?1 and accVoucher.documentDate between ?2 and ?3 and accVoucher.accountProfile.pid = ?4 ")
	Object getCountAndAmountByDocumentsAndDateBetweenAndAccountProfile(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate, String accountProfiles);

	@Query("select accVoucher.pid ,accVoucher.document.pid,accVoucher.document.name,accVoucher.accountProfile.pid,accVoucher.accountProfile.name,"
			+ "accVoucher.createdDate,accVoucher.documentDate,accVoucher.accountProfile.phone1,accVoucher.employee.pid,accVoucher.employee.name,"
			+ "accVoucher.createdBy.firstName,accVoucher.totalAmount,accVoucher.outstandingAmount,accVoucher.remarks,accVoucher.documentNumberLocal,"
			+ "accVoucher.documentNumberServer from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and "
			+ "accVoucher.document.id in ?1 and accVoucher.accountProfile.pid = ?2")
	List<Object[]> getAllByAccountAndRefDocuments(Set<Long> refDocumentIds, String accountPid);

	@Query("select accountingVoucherHeader from AccountingVoucherHeader accountingVoucherHeader where accountingVoucherHeader.company.id = ?1 and tally_download_status = 'PENDING' Order By accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherHeader> findByCompanyIdAndStatusOrderByCreatedDateDesc(Long companyId);

	@Query("select accountingVoucherHeader from AccountingVoucherHeader accountingVoucherHeader where accountingVoucherHeader.company.id = ?#{principal.companyId}  and tally_download_status = 'PENDING' Order By accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherHeader> findByCompanyAndStatusOrderByCreatedDateDesc();

	@Transactional
	@Modifying(clearAutomatically = true)
	  @Query("UPDATE AccountingVoucherHeader accVoucher SET accVoucher.status = TRUE WHERE accVoucher.company.id = ?1 AND accVoucher.pid in ?2")
	int updateAccountingVoucherHeaderStatusUsingPid(long companyId, List<String> inventoryPids);

	// For tally 15-02-2019 (may modify in future for fast performance)
	@Query("select avh from AccountingVoucherHeader avh where avh.company.id = ?1 and avh.tallyDownloadStatus = ?2 Order By avh.createdDate desc")
	List<AccountingVoucherHeader> getAllReceiptsForTally(Long companyId, TallyDownloadStatus tallyDownloadStatus);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.createdBy.pid in ?1 and accVoucher.accountProfile.pid = ?2 and accVoucher.document.pid in ?3 and accVoucher.tallyDownloadStatus in ?4 and accVoucher.createdDate between ?5 and ?6 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> getAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(
			List<String> userPids, String accountPid, List<String> documentPids,
			List<TallyDownloadStatus> tallyDownloadStatus, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher where accVoucher.company.id = ?#{principal.companyId} and accVoucher.createdBy.pid in ?1 and accVoucher.document.pid in ?2 and accVoucher.tallyDownloadStatus in ?3 and accVoucher.createdDate between ?4 and ?5 Order By accVoucher.createdDate desc")
	List<AccountingVoucherHeader> getAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(
			List<String> userPids, List<String> documentPids, List<TallyDownloadStatus> tallyDownloadStatus,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE AccountingVoucherHeader accVoucher SET accVoucher.tallyDownloadStatus = ?1 WHERE accVoucher.company.id = ?2 AND accVoucher.pid in ?3")
	int updateAccountingVoucherHeaderTallyDownloadStatusUsingPid(TallyDownloadStatus tallyDownloadStatus,
			long companyId, List<String> accountingPids);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE AccountingVoucherHeader accVoucher SET accVoucher.tallyDownloadStatus = ?1 WHERE accVoucher.company.id = ?2 AND accVoucher.documentNumberServer in ?3")
	int updateAccountingVoucherHeaderDownloadStatusUsingDocumentNumberServer(TallyDownloadStatus tallyDownloadStatus,
			long companyId, List<String> docNumberServer);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE AccountingVoucherHeader accVoucher SET accVoucher.tallyDownloadStatus = ?1 WHERE accVoucher.company.id = ?#{principal.companyId} AND accVoucher.pid in ?2")
	int updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus tallyDownloadStatus,
			List<String> accountingPids);

	@Query("select av.id from AccountingVoucherHeader av where av.accountProfile.id in ?1 and av.document.pid = ?2 and av.executiveTaskExecution.activity.pid=?3 and av.documentDate between ?4 and ?5")
	Set<Long> findIdByAccountProfileAndDocumentAndActivityDateBetween(Set<Long> accountProfileIds, String documentPid,
			String activityPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(av.totalAmount) from AccountingVoucherHeader av where av.accountProfile.id in ?1 and av.document.pid = ?2 and av.executiveTaskExecution.activity.pid=?3 and av.documentDate between ?4 and ?5")
	Double totalAmountByAccountProfileAndDocumentAndActivityDateBetween(Set<Long> accountProfileIds, String documentPid,
			String activityPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select av.pid,av.document.name,av.document.pid from AccountingVoucherHeader av where av.company.id = ?#{principal.companyId} and av.executiveTaskExecution.pid = ?1")
	List<Object[]> findAccountingVoucherHeaderByExecutiveTaskExecutionPid(String exeTasKPid);

	@Query("select avh from AccountingVoucherHeader avh LEFT JOIN FETCH avh.accountingVoucherDetails where avh.company.id = ?#{principal.companyId} and avh.pid = ?1 Order By avh.createdDate desc")
	List<AccountingVoucherHeader> findAccountingVoucherHeaderByPid(String accountingVoucherHeaderPid);

	@Query("select av.pid,av.document.name,av.document.pid,av.totalAmount from AccountingVoucherHeader av where av.company.id = ?#{principal.companyId} and av.executiveTaskExecution.id in ?1")
	List<Object[]> findAccountingVoucherHeaderByExecutiveTaskExecutionIdin(Set<Long> exeIds);

	Optional<AccountingVoucherHeader> findOneByExecutiveTaskExecutionPidAndImageRefNo(String executiveTaskExecutionPid,
			String imageRefNo);

	@Query("select avh from AccountingVoucherHeader avh LEFT JOIN FETCH avh.accountingVoucherDetails where avh.company.id = ?#{principal.companyId} and avh.documentNumberServer = ?1 Order By avh.createdDate desc")
	AccountingVoucherHeader findAccountingVoucherHeaderByDocumentNumber(String documentNumberServer);

	@Query(value = "select count(*),doc.pid from tbl_accounting_voucher_header avh "
			+ "inner join tbl_document doc on avh.document_id = doc.id "
			+ "where avh.company_id = ?#{principal.companyId} and avh.created_by_id = ?1 "
			+ "and avh.created_date between ?2 and ?3  group by doc.pid", nativeQuery = true)
	List<Object[]> findCountOfEachAccountingTypeDocuments(long userId, LocalDateTime fDate, LocalDateTime tDate);

	@Query("select sum(av.totalAmount) from AccountingVoucherHeader av where av.company.id = ?#{principal.companyId} and av.createdBy.id in ?1 and av.documentDate between ?2 and ?3")
	Object[] findnetCollectionAmountByUserIdandDateBetween(List<Long> userIds, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query(value = "SELECT id,pid,executive_task_execution_id,document_id,account_profile_id,created_date,document_date,total_amount,"
			+ "outstanding_amount,remarks,created_by_id,employee_id,document_number_local,document_number_server,status,updated_date,"
			+ "tally_download_status FROM tbl_accounting_voucher_header where tally_download_status ='PENDING' and company_id = ?#{principal.companyId} "
			+ "order by created_date desc", nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusByCreatedDateDesc();
	
	@Query(value = "SELECT id,pid,executive_task_execution_id,document_id,account_profile_id,created_date,document_date,total_amount,"
			+ "outstanding_amount,remarks,created_by_id,employee_id,document_number_local,document_number_server,status,updated_date,"
			+ "tally_download_status FROM tbl_accounting_voucher_header where tally_download_status ='PENDING' and company_id = ?#{principal.companyId} and employee_id = ?1 "
			+ "order by created_date desc", nativeQuery = true)
	List<Object[]> findByCompanyIdAndEmployeeIdAndTallyStatusByCreatedDateDesc(Long empId);

	@Query(value = "SELECT id,pid,executive_task_execution_id,document_id,account_profile_id,created_date,document_date,total_amount,"
			+ "outstanding_amount,remarks,created_by_id,employee_id,document_number_local,document_number_server,status,updated_date,"
			+ "tally_download_status FROM tbl_accounting_voucher_header where tally_download_status ='PENDING' and sales_management_status = 'APPROVE' and company_id = ?#{principal.companyId} "
			+ "order by created_date desc", nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusAndSalesManagementStatusByCreatedDateDesc();

	@Query("select accVoucher.pid,accVoucher.document.name,accVoucher.totalAmount,accVoucher.document.documentType,accVoucher.executiveTaskExecution.pid from AccountingVoucherHeader accVoucher where accVoucher.executiveTaskExecution.id IN ?1")
	List<Object[]> findByExecutiveTaskExecutionIdIn(Set<Long> exeIds);

	@Query("select accVoucher.pid,accVoucher.document.name,accVoucher.totalAmount,accVoucher.document.documentType,accVoucher.executiveTaskExecution.pid from AccountingVoucherHeader accVoucher where accVoucher.executiveTaskExecution.id IN ?1 and accVoucher.document.pid = ?2")
	List<Object[]> findByExecutiveTaskExecutionIdInAndDocumentPid(Set<Long> exeIds, String documentPid);

	@Query("select accVoucher from AccountingVoucherHeader accVoucher LEFT JOIN FETCH accVoucher.accountingVoucherDetails where accVoucher.company.id = ?#{principal.companyId} and accVoucher.documentNumberServer in ?1")
	List<AccountingVoucherHeader> findAllHeaderdByDocumentNumberServer(List<String> accountingHeaderPids);

	@Query("select accountingVoucherHeader.id,accountingVoucherHeader.accountProfile.id,accountingVoucherHeader.document.id,accountingVoucherHeader.documentDate,accountingVoucherHeader.totalAmount from AccountingVoucherHeader accountingVoucherHeader where accountingVoucherHeader.company.id = ?#{principal.companyId} Order By accountingVoucherHeader.createdDate desc")
	List<Object[]> findAllByCompanyIdAndOrderByCreatedDateDesc();

	@Query("select accVoucher from AccountingVoucherHeader accVoucher LEFT JOIN FETCH accVoucher.accountingVoucherDetails where accVoucher.company.pid = ?2 and accVoucher.documentNumberServer = ?1")
	AccountingVoucherHeader findOneHeaderByDocumentNumberServerAndCompanyPid(String reference, String companyPid);

	@Query("select avh.id,avh.pid,avh.createdDate,avh.documentDate,avh.accountProfile.pid,avh.accountProfile.name,avh.document.pid,avh.document.name,avh.totalAmount,avh.documentNumberServer from AccountingVoucherHeader avh where avh.createdBy.login = ?1 and avh.accountProfile.pid in ?2 and avh.createdDate between ?3 and ?4 order by avh.createdDate desc")
	List<Object[]> getCustomerWiseAccountingVoucherHeader(String userLogin, List<String> accountPids,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select avd.mode,avd.amount,avd.by.pid,avd.by.name,avd.to.pid,avd.to.name,avd.voucherNumber,avd.voucherDate,avd.referenceNumber from AccountingVoucherDetail avd where avd.accountingVoucherHeader.pid = ?1")
	List<Object[]> getCustomerWiseAccountingDetail(String accountingVoucherHeaderPid);

	@Query("SELECT av.documentNumberServer,av.document.pid,av.createdDate from AccountingVoucherHeader av where av.company.pid = ?1 and av.createdBy.pid = ?2 and av.document.pid IN(?3)")
	List<Object[]> getAllDocumentNumberForEachDocument(String companyPid, String userPid, List<String> documentPids);
	public static final String LAST_DOCUMENT_PID_DATE = "select max(cast(coalesce(nullif(SPLIT_PART(avh.document_number_local, ?4 , 2),''),'0') as bigint)) from tbl_accounting_voucher_header avh "
			+ "INNER JOIN tbl_company cmp on avh.company_id = cmp.id   "
			+ "INNER JOIN tbl_document doc on avh.document_id = doc.id  "
			+ "INNER JOIN tbl_user u on avh.created_by_id = u.id where  "
			+ "cmp.pid = ?1 and u.pid = ?2  and doc.pid = ?3 and "
			+ "avh.company_id = cmp.id and avh.created_by_id = u.id and avh.document_id = doc.id";

	@Query(value = LAST_DOCUMENT_PID_DATE, nativeQuery = true)
	Long getHigestDocumentNumberwithoutPrefix(String companyPid, String userPid, String pid, String prefix);

	// @Query("select
	// avh.id,avh.pid,avh.createdDate,avh.documnetDate,avh.accountProfile.pid,avh.accountProfile.name,avh.document.pid,avh.document.name,avh.totalAmount,avh.documentNumberServer
	// from AccountingVoucherHeader avh where avh.createdBy.login = ?1 and
	// avh.accountProfile.pid = ?2 and and avh.createdDate between ?3 and ?4 Order
	// By avh.createdDate desc")

//	List<Object[]> getCustomerWiseAccountingVoucherHeader(String userLogin, String accountPid, LocalDateTime fromDate,
//			LocalDateTime toDate);

	@Query("select sum(accVoucher.totalAmount) from AccountingVoucherHeader accVoucher where accVoucher.executiveTaskExecution.id IN ?1")
	List<Object[]> findByExecutiveTaskIdIn(List<Long> eteIds);

	@Query("select accVoucher.totalAmount,accVoucher.employee.name from AccountingVoucherHeader accVoucher where accVoucher.executiveTaskExecution.id IN ?1 ")
	List<Object[]> findByExecutiveTaskExecutionsIdIn(List<Long> eteIds);
	
	@Query("select accVoucher from AccountingVoucherHeader accVoucher  where accVoucher.executiveTaskExecution.id IN ?1 ")
    List<AccountingVoucherHeader> findByExecutiveTaskExecutionIdIn(List<Long> eteIds);
}
