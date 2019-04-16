package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.DynamicDocumentHeaderHistory;

/**
 * Spring Data JPA repository for the DynamicDocumentHeaderHistory entity.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
public interface DynamicDocumentHeaderHistoryRepository extends JpaRepository<DynamicDocumentHeaderHistory, Long> {

	List<DynamicDocumentHeaderHistory> findDynamicDocumentHistoriesByPidOrderByIdDesc(String pid);

}
