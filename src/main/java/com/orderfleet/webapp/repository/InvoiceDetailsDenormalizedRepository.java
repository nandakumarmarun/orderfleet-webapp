package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.InvoiceDetailsDenormalized;
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
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId = ?#{principal.companyId} and invoiceDetails.createdDate between ?1 and ?2 and invoiceDetails.activityPid in ?3 and invoiceDetails.userId in ?4  Order By invoiceDetails.createdDate desc")
    List<InvoiceDetailsDenormalized> getByCreatedDateBetweenAndActivityPidInAndUserIdIn(LocalDateTime fromDate, LocalDateTime toDate, List<String> activityPids, List<Long> userIds);
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId = ?#{principal.companyId} and invoiceDetails.createdDate between ?1 and ?2 and invoiceDetails.activityPid in ?3 and invoiceDetails.userId in ?4 and invoiceDetails.accountProfilePid in ?5 Order By invoiceDetails.createdDate desc")
    List<InvoiceDetailsDenormalized> getByCreatedDateBetweenAndActivityPidInAndUserIdInAndAccountPidIn(LocalDateTime fromDate, LocalDateTime toDate, List<String> activityPids, List<Long> userIds, List<String> accountProfilePids);
    @Query("select invoiceDetails from InvoiceDetailsDenormalized invoiceDetails where invoiceDetails.companyId  = ?#{principal.companyId} and invoiceDetails.executionPid = ?1")
    List<InvoiceDetailsDenormalized> findAllByExecutionPid(String pid);
}
