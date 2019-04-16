package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.InventoryOrderStatusHistory;

public interface InventoryOrderStatusHistoryRepository extends JpaRepository<InventoryOrderStatusHistory, Long>{

}
