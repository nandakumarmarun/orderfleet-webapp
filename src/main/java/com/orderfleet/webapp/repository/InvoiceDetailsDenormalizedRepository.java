package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InvoiceDetailsDenormalized;
import com.orderfleet.webapp.domain.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface InvoiceDetailsDenormalizedRepository extends JpaRepository<InvoiceDetailsDenormalized,Long> {
@Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.executionPid in ?1")
    List<InvoiceDetailsDenormalized> findAllByExecutionPidIn(Set<String> exePids);
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId  = ?#{principal.companyId}")
    List<InvoiceDetailsDenormalized> findAllByCompanyId();
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId = ?#{principal.companyId} and invoiceDetails.createdDate between ?1 and ?2 and invoiceDetails.activityPid in ?3 and invoiceDetails.userId in ?4 and invoiceDetails.documentPid in ?5 Order By invoiceDetails.createdDate desc")
    List<InvoiceDetailsDenormalized> getByCreatedDateBetweenAndActivityPidInAndUserIdInAndDocumentPidIn(LocalDateTime fromDate, LocalDateTime toDate, List<String> activityPids, List<Long> userIds,List<String> docPids);
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId = ?#{principal.companyId} and invoiceDetails.createdDate between ?1 and ?2 and invoiceDetails.activityPid in ?3 and invoiceDetails.userId in ?4 and invoiceDetails.accountProfilePid in ?5 and invoiceDetails.documentPid in ?6 Order By invoiceDetails.createdDate desc")
    List<InvoiceDetailsDenormalized> getByCreatedDateBetweenAndActivityPidInAndUserIdInAndAccountPidInAndDocumentPidIn(LocalDateTime fromDate, LocalDateTime toDate, List<String> activityPids, List<Long> userIds, List<String> accountProfilePids,List<String> docpids);
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId  = ?#{principal.companyId} and invoiceDetails.executionPid = ?1")
    List<InvoiceDetailsDenormalized> findAllByExecutionPid(String pid);
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId = ?#{principal.companyId} and invoiceDetails.createdDate between ?1 and ?2 and invoiceDetails.documentPid = ?3 and invoiceDetails.userId in ?4  Order By invoiceDetails.createdDate desc")
    List<InvoiceDetailsDenormalized> getByCreatedDateDocumentPidAndUserIdsIn(LocalDateTime fromDate, LocalDateTime toDate,String documentPids, List<Long> userIds);
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId = ?#{principal.companyId} and invoiceDetails.createdDate between ?1 and ?2 and invoiceDetails.documentPid = ?3 and invoiceDetails.accountProfilePid = ?4 and invoiceDetails.userId in ?5  Order By invoiceDetails.createdDate desc")
    List<InvoiceDetailsDenormalized> getByCreatedDateDocumentPidAndAccountPidAndUserIdsIn(LocalDateTime fromDate, LocalDateTime toDate, String documentPids, String accountPid, List<Long> userIds);
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId = ?#{principal.companyId} and invoiceDetails.accountProfilePid = ?1 and invoiceDetails.executionPid =?2 and invoiceDetails.documentPid = ?3  Order By invoiceDetails.createdDate desc")
    List<InvoiceDetailsDenormalized> findAllByAccountProfilePidAndExecutionPidAndDocumentPid(String account,String exePid,String docPid);

    InvoiceDetailsDenormalized findTop1ByUserPidAndAccountProfilePidAndDocumentPidOrderByCreatedDateDesc(String userId, String accountPid,String documentPid);
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.createdDate between ?1 and ?2 Order By invoiceDetails.createdDate desc")
    List<InvoiceDetailsDenormalized> findAllByCreatedDateBetween(LocalDateTime fromDate,LocalDateTime toDate);

    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId  = ?#{principal.companyId} and invoiceDetails.executionPid = ?1 and invoiceDetails.documentType = ?2")
    List<InvoiceDetailsDenormalized> findAllByExecutionPidAndDocumentType(String pid, DocumentType docType);
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId = ?#{principal.companyId} and invoiceDetails.createdDate between ?1 and ?2 and invoiceDetails.activityPid in ?3 and invoiceDetails.userId in ?4 and invoiceDetails.accountProfilePid in ?5 Order By invoiceDetails.createdDate desc")

    List<InvoiceDetailsDenormalized> getByDateBetweenAndActivityPidInAndUserIdInAndAccountPidIn(LocalDateTime fromDate, LocalDateTime toDate, List<String> activityPids, List<Long> userIds, List<String> accountProfilePids);
}
