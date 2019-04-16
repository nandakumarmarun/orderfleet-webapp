package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.DocumentStockCalculation;

/**
 * Spring Data JPA repository for the DocumentStockCalculation entity.
 * 
 * @author Muhammed Riyas T
 * @since Sep 30, 2016
 */
public interface DocumentStockCalculationRepository extends JpaRepository<DocumentStockCalculation, Long> {

	Optional<DocumentStockCalculation> findOneByDocumentPid(String pid);

}
