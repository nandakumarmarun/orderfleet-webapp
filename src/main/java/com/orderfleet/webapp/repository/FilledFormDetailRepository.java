package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.orderfleet.webapp.domain.FilledFormDetail;

public interface FilledFormDetailRepository extends JpaRepository<FilledFormDetail, Long> {

	@Modifying(clearAutomatically = true)
	void deleteByFilledFormId(Long filledFormId);
	
}
