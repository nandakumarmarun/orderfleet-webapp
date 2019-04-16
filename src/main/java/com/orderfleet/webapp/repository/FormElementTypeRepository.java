package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.FormElementType;

/**
 * Spring Data JPA repository for the FormElementType entity.
 * 
 * @author Muhammed Riyas T
 * @since June 23, 2016
 */
public interface FormElementTypeRepository extends JpaRepository<FormElementType, Long> {
	
	
	
}
