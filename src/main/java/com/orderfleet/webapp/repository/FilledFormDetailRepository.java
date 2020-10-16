package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.FilledFormDetail;

public interface FilledFormDetailRepository extends JpaRepository<FilledFormDetail, Long> {

	@Modifying(clearAutomatically = true)
	void deleteByFilledFormId(Long filledFormId);

	@Query("SELECT ffd.id,ffd.filledForm.id,ffd.formElement.name,ffd.value FROM FilledFormDetail AS ffd WHERE ffd.filledForm.id IN ?1")
	List<Object[]> findAllByFormIdIn(Set<Long> ffIds);

	List<FilledFormDetail> findByFilledFormPidIn(Set<String> ffPids);
	
}
