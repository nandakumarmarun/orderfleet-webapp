package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.StateFocus;
import com.orderfleet.webapp.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


public interface StockCalculationRepository  extends JpaRepository<Stock, Long> {

    @Query("select stock from Stock stock where stock.company = ?1")
    List<Stock> findAllByCompanyId(long companyId);

    @Query("SELECT stock FROM Stock stock WHERE stock.company = ?1 AND stock.productPid IN ?2 AND stock.StockLocationPid IN ?3")
    List<Stock> findByCompanyProductAndOrderDateBetween(long companyId, List<String> productPids,List<String> stockLocations);

    @Query("SELECT stock FROM Stock stock WHERE stock.company = ?1 AND stock.StockLocationPid IN ?2")
    List<Stock> findByCompanyProductAndOrderDateBetween(long companyId,List<String> stockLocations);

    @Query("SELECT stock.avilableQuantity FROM Stock stock WHERE stock.company = ?1 AND stock.productPid IN ?2 AND stock.StockLocationPid IN ?3")
    List<Double> findAvilableQuantityByCompanyProductAndOrderDateBetween(long companyId, String productPids, List<String> stockLocations);

    @Query("SELECT stock FROM Stock stock WHERE stock.company = ?1 AND stock.productPid IN ?2 AND stock.StockLocationPid IN ?3")
    Stock findByCompanyProductAndOrderDateBetween(long companyId, String productPids,String stockLocations);

    @Query("SELECT stock FROM Stock stock WHERE stock.company = ?1")
    List<Stock> findAllStockByCompanyid(long companyId);

    @Query("SELECT stock FROM Stock stock WHERE stock.company = ?1 AND stock.StockLocationPid IN ?2")
    List<Stock> findAllLiveStockByCompanyid(long companyId,List<String> stockLocations);
}
