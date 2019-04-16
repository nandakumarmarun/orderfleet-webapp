package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.InventoryVoucherHeaderHistory;

/**
 * Spring Data JPA repository for the InventoryVoucherHeaderHistory entity.
 * 
 * @author Muhammed Riyas T
 * @since October 24, 2016
 */
public interface InventoryVoucherHeaderHistoryRepository extends JpaRepository<InventoryVoucherHeaderHistory, Long> {

	List<InventoryVoucherHeaderHistory> findByPidOrderByIdDesc(String pid);

}
