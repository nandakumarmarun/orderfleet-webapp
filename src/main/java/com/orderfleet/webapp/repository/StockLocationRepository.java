package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.StockLocation;

/**
 * Spring Data JPA repository for the StockLocation entity.
 * 
 * @author Muhammed Riyas T
 * @since July 15, 2016
 */
public interface StockLocationRepository extends JpaRepository<StockLocation, Long> {

	Optional<StockLocation> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<StockLocation> findOneByPid(String pid);

	@Query("select stockLocation from StockLocation stockLocation where stockLocation.company.id = ?#{principal.companyId}")
	List<StockLocation> findAllByCompanyId();

	@Query("select stockLocation from StockLocation stockLocation where stockLocation.company.id = ?#{principal.companyId}")
	Page<StockLocation> findAllByCompanyId(Pageable pageable);

	@Query("select stockLocation from StockLocation stockLocation where stockLocation.company.id = ?#{principal.companyId} and stockLocation.stockLocationType = 'ACTUAL' ")
	List<StockLocation> findAllActualByCompanyId();

	List<StockLocation> findAllByCompanyPid(String companyPid);

	@Query("select stockLocation from StockLocation stockLocation where stockLocation.company.id = ?#{principal.companyId} Order By stockLocation.name asc")
	Page<StockLocation> findAllByCompanyIdOrderByStockLocationName(Pageable pageable);

	@Query("select stockLocation from StockLocation stockLocation where stockLocation.company.id = ?#{principal.companyId} and stockLocation.activated = ?1 Order By stockLocation.name asc")
	Page<StockLocation> findAllByCompanyIdAndActivatedStockLocationOrderByName(Pageable pageable, boolean active);

	@Query("select stockLocation from StockLocation stockLocation where stockLocation.company.id = ?#{principal.companyId} and stockLocation.activated = ?1 Order By stockLocation.name asc")
	List<StockLocation> findAllByCompanyIdAndDeactivatedStockLocation(boolean deactive);

	List<StockLocation> findAllByCompanyId(Long companyId);

	StockLocation findFirstByCompanyId(Long companyId);
	
	@Query("select stockLocation from StockLocation stockLocation where stockLocation.company.id = ?#{principal.companyId} and stockLocation.pid in ?1")
	List<StockLocation> findAllByStockLocationPidIn(List<String>stockLocationPids);
}
