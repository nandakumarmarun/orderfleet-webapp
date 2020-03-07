package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.FormElementValue;

/**
 * Spring Data JPA repository for the FormElementValue entity.
 * 
 * @author Muhammed Riyas T
 * @since June 23, 2016
 */
public interface FormElementValueRepository extends JpaRepository<FormElementValue, Long> {

	void deleteByFormElementIdIsNull();

	List<FormElementValue> findAllByFormElementPid(String formElementPid);

}
