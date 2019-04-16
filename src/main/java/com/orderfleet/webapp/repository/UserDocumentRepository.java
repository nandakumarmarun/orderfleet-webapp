package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.UserDocument;

public interface UserDocumentRepository extends JpaRepository<UserDocument, Long> {

	@Query("select userDocument.document from UserDocument userDocument where userDocument.user.login = ?#{principal.username}")
	List<Document> findDocumentsByUserIsCurrentUser();

	@Query("select userDocument.document from UserDocument userDocument where userDocument.user.pid = ?1 ")
	List<Document> findDocumentsByUserPid(String userPid);
	
	@Query("select userDocument.document from UserDocument userDocument where userDocument.user.id IN ?1 ")
	List<Document> findDocumentsByUserIds(List<Long> userIds);
	
	List<UserDocument> findByUserPid(String userPid);

	void deleteByUserPid(String userPid);
	
	@Query("select userDocument.document from UserDocument userDocument where userDocument.user.login = ?#{principal.username} and userDocument.document.lastModifiedDate > ?1")
	List<Document> findDocumentsByUserIsCurrentUserAndLlastModifiedDate(LocalDateTime lastModifiedDate);

	void deleteByUserPidIn(List<String> userPids);
	
	@Query("select userDocument from UserDocument userDocument where userDocument.user.login = ?#{principal.username} and userDocument.document.pid=?1")
	List<UserDocument> findByUserIsCurrentUserAndDocumentPid(String documentPid);
	
	List<String> findUserPidByCompanyPid(String pid);
}
