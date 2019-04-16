package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.InwardOutwardStockLocation;
import com.orderfleet.webapp.domain.StockLocation;

/**
 *Repository for InwardOutwardStockLocation
 *
 * @author fahad
 * @since Feb 22, 2017
 */
public interface InwardOutwardStockLocationRepository extends JpaRepository<InwardOutwardStockLocation, Long>{

	@Query("select inwardOutwardStockLocation.stockLocation from InwardOutwardStockLocation inwardOutwardStockLocation where inwardOutwardStockLocation.company.id = ?#{principal.companyId}")
	Page<StockLocation> findAllStockLocationByCompanyId(Pageable pageable);
	
	@Query("select inwardOutwardStockLocation.stockLocation from InwardOutwardStockLocation inwardOutwardStockLocation where inwardOutwardStockLocation.company.id = ?#{principal.companyId} ")
	List<StockLocation> findAllByCompanyId();
	
	@Transactional
	Long deleteByCompanyId(Long companyId);
}
