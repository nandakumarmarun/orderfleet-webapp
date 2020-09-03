package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.enums.PaymentMode;

/**
 * Spring Data JPA repository for the AccountingVoucherDetail entity.
 * 
 * @author Muhammed Riyas T
 * @since Mar 14, 2016
 */
public interface AccountingVoucherDetailRepository extends JpaRepository<AccountingVoucherDetail, Long> {

	@Query("select accVoucherDetail from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.company.id = ?#{principal.companyId} and accVoucherDetail.accountingVoucherHeader.createdDate between ?1 and ?2 Order By accVoucherDetail.accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherDetail> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select accVoucherDetail from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.company.id = ?#{principal.companyId} and accVoucherDetail.accountingVoucherHeader.createdBy.pid = ?1 and (accVoucherDetail.by.pid = ?2 or accVoucherDetail.to.pid = ?2) and accVoucherDetail.accountingVoucherHeader.document.pid = ?3 and accVoucherDetail.accountingVoucherHeader.createdDate between ?4 and ?5 Order By accVoucherDetail.accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherDetail> findAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(
			String userPid, String accountPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucherDetail from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.company.id = ?#{principal.companyId} and accVoucherDetail.accountingVoucherHeader.createdBy.pid = ?1 and (accVoucherDetail.by.pid = ?2 or accVoucherDetail.to.pid = ?2) and accVoucherDetail.accountingVoucherHeader.createdDate between ?3 and ?4 Order By accVoucherDetail.accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherDetail> findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByCreatedDateDesc(
			String userPid, String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucherDetail from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.company.id = ?#{principal.companyId} and accVoucherDetail.accountingVoucherHeader.createdBy.pid = ?1 and accVoucherDetail.accountingVoucherHeader.document.pid = ?2 and accVoucherDetail.accountingVoucherHeader.createdDate between ?3 and ?4 Order By accVoucherDetail.accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherDetail> findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(
			String userPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucherDetail from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.company.id = ?#{principal.companyId} and (accVoucherDetail.by.pid = ?1 or accVoucherDetail.to.pid = ?1) and accVoucherDetail.accountingVoucherHeader.document.pid = ?2 and accVoucherDetail.accountingVoucherHeader.createdDate between ?3 and ?4 Order By accVoucherDetail.accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherDetail> findAllByCompanyIdAccountPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(
			String accountPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucherDetail from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.company.id = ?#{principal.companyId} and accVoucherDetail.accountingVoucherHeader.createdBy.pid = ?1 and accVoucherDetail.accountingVoucherHeader.createdDate between ?2 and ?3 Order By accVoucherDetail.accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherDetail> findAllByCompanyIdUserPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucherDetail from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.company.id = ?#{principal.companyId} and (accVoucherDetail.by.pid = ?1 or accVoucherDetail.to.pid = ?1) and accVoucherDetail.accountingVoucherHeader.createdDate between ?2 and ?3 Order By accVoucherDetail.accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherDetail> findAllByCompanyIdAccountPidAndDateBetweenOrderByCreatedDateDesc(String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucherDetail from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.company.id = ?#{principal.companyId} and accVoucherDetail.accountingVoucherHeader.document.pid = ?1 and accVoucherDetail.accountingVoucherHeader.createdDate between ?2 and ?3 Order By accVoucherDetail.accountingVoucherHeader.createdDate desc")
	List<AccountingVoucherDetail> findAllByCompanyIdDocumentPidAndDateBetweenOrderByCreatedDateDesc(String documentPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select accVoucherDetail from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.company.id = ?#{principal.companyId} and accVoucherDetail.by.pid = ?1 and accVoucherDetail.to.pid = ?2 and accVoucherDetail.mode = ?3 and accVoucherDetail.accountingVoucherHeader.document.pid = ?4 and accVoucherDetail.accountingVoucherHeader.executiveTaskExecution.activity.pid = ?5 ")
	List<AccountingVoucherDetail> findAllByCompanyIdByAccountAndToAccountAndPaymentModeAndAccountingVoucherHeaderPid(
			String byaccount, String toaccount, PaymentMode paymentMode, String documentPid, String activityPid);

	@Query("select accVoucherDetail.accountingVoucherHeader.employee.pid, accVoucherDetail.accountingVoucherHeader.employee.name, accVoucherDetail.accountingVoucherHeader.createdDate, accVoucherDetail.amount from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.document.id in ?1 and accVoucherDetail.accountingVoucherHeader.createdDate between ?2 and ?3")
	List<Object[]> findByDocumentIdInAndDateBetweenOrderByCreatedDateDesc(Set<Long> documentId, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select accVoucherDetail from AccountingVoucherDetail accVoucherDetail where accVoucherDetail.accountingVoucherHeader.pid in ?1")
	List<AccountingVoucherDetail> findAllByAccountingVoucherHeaderPidIn(Set<String> accHeaderPids);

	@Query("select sum(voucherDetail.amount) from AccountingVoucherDetail voucherDetail where voucherDetail.accountingVoucherHeader.id in ?1")
	Double sumOfAmountByHeaderIds(Set<Long> avHeaderIds);

	@Query("select sum(voucherDetail.amount) from AccountingVoucherDetail voucherDetail where voucherDetail.accountingVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.accountingVoucherHeader.createdBy.id in ?1 and voucherDetail.accountingVoucherHeader.documentDate between ?2 and ?3 and voucherDetail.mode = ?4")
	Object[] findnetCollectionAmountByUserIdandDateBetweenAndPaymentMode(List<Long> userIds, LocalDateTime fromDate,
			LocalDateTime toDate, PaymentMode paymentMode);

}
