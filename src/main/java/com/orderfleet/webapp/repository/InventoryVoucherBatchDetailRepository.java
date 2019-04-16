package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.InventoryVoucherBatchDetail;

/**
 * Spring Data JPA repository for the InventoryVoucherBatchDetail entity.
 * 
 * @author Sarath
 * @since Dec 7, 2016
 */
public interface InventoryVoucherBatchDetailRepository extends JpaRepository<InventoryVoucherBatchDetail, Long> {

}
