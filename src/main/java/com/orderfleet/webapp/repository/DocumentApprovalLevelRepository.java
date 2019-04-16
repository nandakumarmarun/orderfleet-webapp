package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DocumentApprovalLevel;

/**
 * Spring Data JPA repository for the DocumentApprovalLevel entity.
 * 
 * @author Muhammed Riyas T
 * @since November 19, 2016
 */
public interface DocumentApprovalLevelRepository extends JpaRepository<DocumentApprovalLevel, Long> {

	Optional<DocumentApprovalLevel> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<DocumentApprovalLevel> findOneByPid(String pid);

	@Query("select documentApprovalLevel from DocumentApprovalLevel documentApprovalLevel where documentApprovalLevel.company.id = ?#{principal.companyId}")
	List<DocumentApprovalLevel> findAllByCompanyId();

	@Query("select documentApprovalLevel from DocumentApprovalLevel documentApprovalLevel where documentApprovalLevel.company.id = ?#{principal.companyId}")
	Page<DocumentApprovalLevel> findAllByCompanyId(Pageable pageable);

	List<DocumentApprovalLevel> findAllByCompanyPid(String companyPid);

	List<DocumentApprovalLevel> findAllByDocumentPid(String documentPid);
}
