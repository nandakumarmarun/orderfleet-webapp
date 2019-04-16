package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.DocumentApprovedUser;

/**
 * Spring Data JPA repository for the DocumentApprovedUser entity.
 * 
 * @author Muhammed Riyas T
 * @since November 21, 2016
 */
public interface DocumentApprovedUserRepository extends JpaRepository<DocumentApprovedUser, Long> {

	DocumentApprovedUser findByUserIdAndDocumentApprovalId(Long userId, Long documentApprovalId);

	Long countByDocumentApprovalPid(String documentApprovalPid);

}
