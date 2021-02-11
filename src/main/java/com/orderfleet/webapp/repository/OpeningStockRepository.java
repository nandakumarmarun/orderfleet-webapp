package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;

/**
 * Spring Data JPA repository for the OpeningStock entity.
 * 
 * @author Muhammed Riyas T
 * @since July 16, 2016
 */
public interface OpeningStockRepository extends JpaRepository<OpeningStock, Long> {

	Optional<OpeningStock> findByCompanyIdAndProductProfilePidAndBatchNumberIgnoreCase(Long id, String pid,
			String batchNumber);

	Optional<OpeningStock> findOneByPid(String pid);

	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?#{principal.companyId}")
	List<OpeningStock> findAllByCompanyId();

	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?1")
	List<OpeningStock> findAllByCompanyId(Long companyId);

	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?#{principal.companyId}")
	Page<OpeningStock> findAllByCompanyId(Pageable pageable);

	Optional<OpeningStock> findByProductProfileIdOrderByCreatedDateDesc(Long productProfileId);

	OpeningStock findTop1ByProductProfilePidOrderByCreatedDateDesc(String productProfilePid);

	List<OpeningStock> findByProductProfilePidOrderByCreatedDateDesc(String productProfilePid);

	Optional<OpeningStock> findTopByStockLocation(StockLocation stockLocation);

	Optional<OpeningStock> findTopByStockLocationPid(String stockLocationPid);

	List<OpeningStock> findByStockLocationInOrderByCreatedDateAsc(List<StockLocation> stockLocations);

	List<OpeningStock> findByStockLocation(StockLocation stockLocation);

	@Query("select coalesce(sum(openingStock.quantity),0) from OpeningStock openingStock where openingStock.productProfile.pid = ?1 and openingStock.stockLocation in  ?2")
	Double findOpeningStockByProductAndStockLocations(String productPid, List<StockLocation> stockLocations);

	@Query("select MAX(openingStock.createdDate) from OpeningStock openingStock where openingStock.productProfile.pid = ?1 and openingStock.stockLocation in  ?2")
	LocalDateTime findMaxDateByProductAndStockLocations(String productPid, List<StockLocation> stockLocations);

	List<OpeningStock> findByProductProfileIn(List<ProductProfile> productProfiles);

	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?#{principal.companyId} and openingStock.productProfile.pid=?1")
	List<OpeningStock> findByCompanyIdAndProductProfilePid(String productPid);

	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?#{principal.companyId} and openingStock.productProfile.pid=?1 and openingStock.batchNumber = ?2")
	List<OpeningStock> findByCompanyIdAndProductProfilePidAndBatchNumber(String pid, String batchNumber);

	@Transactional
	Long deleteByCompanyId(Long companyId);

	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?#{principal.companyId} and openingStock.productProfile.pid= ?1 and openingStock.activated = ?2")
	List<OpeningStock> findByCompanyIdAndProductProfilePidAndOpeningStockActivated(String productPid, boolean active);

	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?#{principal.companyId} and openingStock.activated = ?1 Order By openingStock.productProfile.name asc")
	Page<OpeningStock> findAllByCompanyIdAndActivatedOpeningStockOrderByName(Pageable pageable, boolean active);

	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?#{principal.companyId} and openingStock.activated = ?1")
	List<OpeningStock> findAllByCompanyIdAndDeactivatedOpeningStock(boolean deactive);

	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?#{principal.companyId} and openingStock.productProfile.pid= ?1 and openingStock.activated = ?2 and openingStock.lastModifiedDate > ?3")
	List<OpeningStock> findByCompanyIdAndProductProfilePidAndOpeningStockActivatedAndLastModifiedDate(String productPid,
			boolean active, LocalDateTime lastModifiedDate);

	Optional<OpeningStock> findTop1ByProductProfilePidAndStockLocationPidOrderByCreatedDateDesc(String productPid,
			String stockLocationPid);

	OpeningStock findTop1ByProductProfilePidOrderByLastModifiedDateDesc(String productPid);

	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?#{principal.companyId} and openingStock.productProfile.pid = ?1 and openingStock.stockLocation in  ?2")
	List<OpeningStock> findAllOpeningStockByProductPidAndStockLocations(String productPid,
			List<StockLocation> stockLocations);

	@Query("select coalesce(sum(openingStock.quantity),0) from OpeningStock openingStock where openingStock.productProfile.pid = ?1 and openingStock.stockLocation.id in ?2 ")
	Double findSumOpeningStockByProductPidAndStockLocationIdIn(String productPid, Set<Long> stockLocationIds);

	@Transactional
	@Query(name = "delete from OpeningStock os where  os.stockLocation.id = ?1 and os.company.id = ?2")
	void deleteByStockLocationIdAndCompanyId(long stockLocationId, long companyId);

	@Query("select openingStock from OpeningStock openingStock where  openingStock.stockLocation.id in ?1")
	List<OpeningStock> findOpeningStocksAndStockLocationIdIn(Set<Long> sLocationIds);

	List<OpeningStock> findByStockLocationIn(List<StockLocation> stockLocations);

	@Query("select openingStock from OpeningStock openingStock where  openingStock.stockLocation.id = ?1 Order By openingStock.productProfile.name asc")
	List<OpeningStock> findOpeningStocksAndStockLocationId(Long id);

	@Transactional
	@Query(name = "delete from OpeningStock os where  os.company.id in ?1 and os.stockLocation.pid in ?2")
	void deleteByCompanyIdAndStockLocationPidIn(Long id, List<String> stockLocationPids);
	
	@Query("select openingStock from OpeningStock openingStock where openingStock.company.id = ?#{principal.companyId} and openingStock.stockLocation.pid in ?1")
	List<OpeningStock> findAllExistingStocks(List<String> stockLocationPids);

}
