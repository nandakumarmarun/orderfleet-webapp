package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.SalesTargetGroupDocument;

/**
 * Spring Data JPA repository for the SalesTargetGroupDocument entity.
 *
 * @author Sarath
 * @since Oct 14, 2016
 */
public interface SalesTargetGroupDocumentRepository extends JpaRepository<SalesTargetGroupDocument, Long> {

	void deleteBySalesTargetGroupPid(String salesTargetGroupPid);

	@Query("select salesTargetGroupDocument.document from SalesTargetGroupDocument salesTargetGroupDocument where salesTargetGroupDocument.salesTargetGroup.pid = ?1")
	List<Document> findDocumentsBySalesTargetGroupPid(String salesTargetGroupPid);
	
	@Query("select salesTargetGroupDocument.document.id from SalesTargetGroupDocument salesTargetGroupDocument where salesTargetGroupDocument.salesTargetGroup.pid = ?1")
	Set<Long> findDocumentIdsBySalesTargetGroupPid(String salesTargetGroupPid);
		
	@Query("select salesTargetGroupDocument.document from SalesTargetGroupDocument salesTargetGroupDocument where salesTargetGroupDocument.salesTargetGroup.name = ?1 and salesTargetGroupDocument.company.id = ?#{principal.companyId}")
	List<Document> findDocumentsBySalesTargetGroupName(String salesTargetGroupName);
	
	@Query("select salesTargetGroupDocument.document.id from SalesTargetGroupDocument salesTargetGroupDocument where salesTargetGroupDocument.salesTargetGroup.name = ?1 and salesTargetGroupDocument.company.id = ?#{principal.companyId}")
	Set<Long> findDocumentIdsBySalesTargetGroupName(String salesTargetGroupName);
	
	@Query("select salesTargetGroupDocument.document from SalesTargetGroupDocument salesTargetGroupDocument where salesTargetGroupDocument.salesTargetGroup.pid in ?1")
	List<Document> findDocumentsBySalesTargetGroupPid(List<String> salesTargetGroupPids);
	
	@Query("select salesTargetGroupDocument.document.id, salesTargetGroupDocument.salesTargetGroup.name  from SalesTargetGroupDocument salesTargetGroupDocument where salesTargetGroupDocument.salesTargetGroup.pid in ?1 and salesTargetGroupDocument.company.id = ?#{principal.companyId}")
	Set<Object[]> findDocumentIdWithSalesTargetGroupNameBySalesTargetGroupPid(List<String> salesTargetGroupPids);
	

}
