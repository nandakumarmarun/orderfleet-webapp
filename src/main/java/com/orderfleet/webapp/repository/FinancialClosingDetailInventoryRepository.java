package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.FinancialClosingDetailInventory;

public interface FinancialClosingDetailInventoryRepository extends JpaRepository<FinancialClosingDetailInventory, Long>{
	
}
