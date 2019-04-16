package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.KnowledgebaseFiles;

/**
 * Spring Data JPA repository for the UserKnowledgebaseFiles entity.
 * 
 * @author Muhammed Riyas T
 * @since Aug 10, 2016
 */
public interface KnowledgebaseFilesRepository extends JpaRepository<KnowledgebaseFiles, Long> {

	Optional<KnowledgebaseFiles> findOneById(Long id);

	Optional<KnowledgebaseFiles> findOneByPid(String pid);

	@Query("select knowledgebaseFile from KnowledgebaseFiles knowledgebaseFile where knowledgebaseFile.company.id = ?#{principal.companyId}")
	Page<KnowledgebaseFiles> findAllByCompanyId(Pageable pageable);

	@Query("select knowledgebaseFile.searchTags from KnowledgebaseFiles knowledgebaseFile where knowledgebaseFile.company.id = ?#{principal.companyId}")
	Set<String> findSearchTagsByCompany();
	
	@Query("select knowledgebaseFile from KnowledgebaseFiles knowledgebaseFile where knowledgebaseFile.company.id = ?#{principal.companyId}")
	List<KnowledgebaseFiles> findAllByCompanyId();

}
