package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.InventoryVoucherColumn;

/**
 * Spring Data JPA repository for the InventoryVoucherColumn entity.
 * 
 * @author Muhammed Riyas T
 * @since July 25, 2016
 */
public interface InventoryVoucherColumnRepository extends JpaRepository<InventoryVoucherColumn, Long> {

	InventoryVoucherColumn findOneByName(String name);
	
	List<InventoryVoucherColumn> findByNameIn(List<String> names);

}
