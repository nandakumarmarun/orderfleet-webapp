package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.UserReceiptTargetDocument;

/**
 * Spring Data JPA repository for the UserReceiptTargetDocument entity.
 * 
 * @author Sarath
 * @since Sep 3, 2016
 */
public interface UserReceiptTargetDocumentRepository extends JpaRepository<UserReceiptTargetDocument, Long> {

	void deleteByUserReceiptTargetPid(String userReceiptTargetPid);

	@Query("select userReceiptTargetDocument.document from UserReceiptTargetDocument userReceiptTargetDocument where userReceiptTargetDocument.userReceiptTarget.pid = ?1")
	List<Document> findDocumentsByUserReceiptTargetPid(String receiptTargetPid);

}
