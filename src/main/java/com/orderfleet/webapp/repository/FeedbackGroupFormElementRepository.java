package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FeedbackGroupFormElement;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.enums.FeedbackElementType;

/**
 * Spring Data JPA repository for the FeedbackGroupFormElement entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 11, 2017
 */
public interface FeedbackGroupFormElementRepository extends JpaRepository<FeedbackGroupFormElement, Long> {

	@Query("select fbfe.formElement from FeedbackGroupFormElement fbfe where fbfe.feedbackGroup.pid = ?1 and fbfe.feedbackElementType = ?2 ")
	List<FormElement> findFormElementsByGroupPidAnd(String feedbackGroupPid, FeedbackElementType feedbackElementType);

	void deleteByFeedbackGroupPidAndFeedbackElementType(String feedbackGroupPid,
			FeedbackElementType feedbackElementType);

	FeedbackGroupFormElement findByFormElementPid(String formElementPid);
}
