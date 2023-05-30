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

	@Query("SELECT ffd.id,ffd.filledForm.id,ffd.filledForm.pid,ffd.formElement.name,ffd.value FROM FilledFormDetail AS ffd WHERE ffd.filledForm.pid IN ?1")
	List<Object[]> findAllByFormPidIn(Set<String> ffPids);

	List<FilledFormDetail> findByFilledFormPidIn(Set<String> ffPids);

	@Query(value="select id,value from tbl_filled_form_detail where filled_form_id in ?1 and form_element_id = ?2",nativeQuery = true)
	List<Object[]> findAllByFilledFormIdInAndFormElementId(Set<Long> filledFormIds, Long kid);
	
	
	
}
