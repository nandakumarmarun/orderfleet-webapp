package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Knowledgebase;

/**
 * Spring Data JPA repository for the Knowledgebase entity.
 * 
 * @author Muhammed Riyas T
 * @since August 08, 2016
 */
public interface KnowledgebaseRepository extends JpaRepository<Knowledgebase, Long> {

	Optional<Knowledgebase> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<Knowledgebase> findOneByPid(String pid);

	@Query("select knowledgebase from Knowledgebase knowledgebase where knowledgebase.company.id = ?#{principal.companyId}")
	List<Knowledgebase> findAllByCompanyId();

	@Query("select knowledgebase from Knowledgebase knowledgebase where knowledgebase.company.id = ?#{principal.companyId}")
	Page<Knowledgebase> findAllByCompanyId(Pageable pageable);

	@Query("select knowledgebase from Knowledgebase knowledgebase where knowledgebase.company.id = ?#{principal.companyId} and knowledgebase.activated = ?1")
	List<Knowledgebase> findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivated(boolean active);

	@Query("select knowledgebase from Knowledgebase knowledgebase where knowledgebase.company.id = ?#{principal.companyId} Order By knowledgebase.name asc")
	Page<Knowledgebase> findAllByCompanyIdOrderByKnowledgebaseName(Pageable pageable);

	@Query("select knowledgebase from Knowledgebase knowledgebase where knowledgebase.company.id = ?#{principal.companyId} and knowledgebase.activated = ?1 Order By knowledgebase.name asc")
	Page<Knowledgebase> findAllByCompanyIdAndActivatedKnowledgebaseOrderByName(Pageable pageable, boolean active);

	@Query("select knowledgebase from Knowledgebase knowledgebase where knowledgebase.company.id = ?#{principal.companyId} and knowledgebase.activated = ?1 and knowledgebase.lastModifiedDate > ?2")
	List<Knowledgebase> findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivatedAndLastModifiedDate(boolean active,
			LocalDateTime lastModifiedDate);

}
