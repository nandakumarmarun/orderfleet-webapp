package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentApproval;

/**
 * Spring Data JPA repository for the DocumentApproval entity.
 * 
 * @author Muhammed Riyas T
 * @since November 21, 2016
 */
public interface DocumentApprovalRepository extends JpaRepository<DocumentApproval, Long> {

	DocumentApproval findOneByPid(String pid);

	@Query("select documentApproval from DocumentApproval documentApproval where documentApproval.company.id = ?#{principal.companyId}")
	List<DocumentApproval> findByCurrentCompany();

	@Query("select documentApproval from DocumentApproval documentApproval where documentApproval.company.id = ?#{principal.companyId} and  documentApproval.document in ?1 and documentApproval.completed = 'FALSE'")
	List<DocumentApproval> findByCompanyIdAndDocumentsAndCompletdIsFalse(Set<Document> documents);

}
