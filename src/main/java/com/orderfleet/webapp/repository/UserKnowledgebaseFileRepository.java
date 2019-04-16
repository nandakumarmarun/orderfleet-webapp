package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.KnowledgebaseFiles;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserKnowledgebaseFile;

/**
 * Spring Data JPA repository for the UserKnowledgebaseFiles entity.
 * 
 * @author Muhammed Riyas T
 * @since October 05, 2016
 */
public interface UserKnowledgebaseFileRepository extends JpaRepository<UserKnowledgebaseFile, Long> {

	@Query("select userKnowledgebaseFile.knowledgebaseFiles from UserKnowledgebaseFile userKnowledgebaseFile where userKnowledgebaseFile.user.login = ?#{principal.username} and userKnowledgebaseFile.company.id = ?#{principal.companyId}")
	List<KnowledgebaseFiles> findKnowledgebaseFilesByUserIsCurrentUser();

	@Query("select userKnowledgebaseFile.knowledgebaseFiles from UserKnowledgebaseFile userKnowledgebaseFile where userKnowledgebaseFile.user.pid = ?1 and userKnowledgebaseFile.company.id = ?#{principal.companyId}")
	List<KnowledgebaseFiles> findKnowledgebaseFilesByUserPid(String userPid);

	@Query("select userKnowledgebaseFile.user from UserKnowledgebaseFile userKnowledgebaseFile where userKnowledgebaseFile.knowledgebaseFiles.pid = ?1 and userKnowledgebaseFile.company.id = ?#{principal.companyId}")
	List<User> findUsersByKnowledgebaseFilesPid(String knowledgebaseFilePid);

	void deleteByKnowledgebaseFilesPid(String knowledgebaseFilePid);

}
