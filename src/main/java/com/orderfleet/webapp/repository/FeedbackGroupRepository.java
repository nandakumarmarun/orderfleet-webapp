package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FeedbackGroup;

/**
 * Spring Data JPA repository for the FeedbackGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 11, 2017
 */
public interface FeedbackGroupRepository extends JpaRepository<FeedbackGroup, Long> {

	Optional<FeedbackGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<FeedbackGroup> findOneByPid(String pid);

	@Query("select feedbackGroup from FeedbackGroup feedbackGroup where feedbackGroup.company.id = ?#{principal.companyId}")
	List<FeedbackGroup> findAllByCompanyId();

	List<FeedbackGroup> findAllByCompanyPid(String companyPid);

}
