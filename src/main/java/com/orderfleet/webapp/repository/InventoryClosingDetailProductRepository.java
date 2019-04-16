package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.InventoryClosingDetailProduct;

public interface InventoryClosingDetailProductRepository extends JpaRepository<InventoryClosingDetailProduct, Long>{

}
