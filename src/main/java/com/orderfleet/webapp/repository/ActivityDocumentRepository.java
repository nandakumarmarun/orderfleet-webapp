package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityDocument;

/**
 * repository for ActivityDocument
 * 
 * @author Muhammed Riyas T
 * @since Feb 21, 2017
 */
public interface ActivityDocumentRepository extends JpaRepository<ActivityDocument, Long> {

	@Query("select activityDocument.document from ActivityDocument activityDocument where activityDocument.activity.pid = ?1 ")
	List<Document> findProductGroupsByActivityPid(String activityPid);

	@Modifying(clearAutomatically = true)
	void deleteByActivityPid(String activityPid);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "delete from tbl_activity_document where activity_id = ?1 ",nativeQuery = true)
	void deleteByActivityDocumentActivityId(Long id);

	List<ActivityDocument> findByActivityIn(List<Activity> activities);

	List<ActivityDocument> findAllByCompanyPid(String companyPid);

	List<ActivityDocument> findByActivityPid(String activityPid);
	
	List<ActivityDocument> findByDocumentDocumentType(DocumentType documentType);
	
	@Query("select activityDocument from ActivityDocument activityDocument where activityDocument.company.id = ?#{principal.companyId} and  activityDocument.document.documentType =?1")
	List<ActivityDocument> findByCompanyIdAndDocumentType(DocumentType documentType);

}
