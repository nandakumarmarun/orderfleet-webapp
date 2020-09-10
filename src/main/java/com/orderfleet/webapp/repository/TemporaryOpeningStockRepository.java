package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.TemporaryOpeningStock;

/**
 * Spring Data JPA repository for the TemporaryOpeningStock entity.
 * 
 * @author Muhammed Riyas T
 * @since July 16, 2016
 */
public interface TemporaryOpeningStockRepository extends JpaRepository<TemporaryOpeningStock, Long> {

	Optional<TemporaryOpeningStock> findByCompanyIdAndProductProfilePidAndBatchNumberIgnoreCase(Long id, String pid,
			String batchNumber);

	Optional<TemporaryOpeningStock> findOneByPid(String pid);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.company.id = ?#{principal.companyId}")
	List<TemporaryOpeningStock> findAllByCompanyId();

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.company.id = ?1")
	List<TemporaryOpeningStock> findAllByCompanyId(Long companyId);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.company.id = ?#{principal.companyId}")
	Page<TemporaryOpeningStock> findAllByCompanyId(Pageable pageable);

	Optional<TemporaryOpeningStock> findByProductProfileIdOrderByCreatedDateDesc(Long productProfileId);

	TemporaryOpeningStock findTop1ByProductProfilePidOrderByCreatedDateDesc(String productProfilePid);

	List<TemporaryOpeningStock> findByProductProfilePidOrderByCreatedDateDesc(String productProfilePid);

	Optional<TemporaryOpeningStock> findTopByStockLocation(StockLocation stockLocation);

	Optional<TemporaryOpeningStock> findTopByStockLocationPid(String stockLocationPid);

	List<TemporaryOpeningStock> findByStockLocationInOrderByCreatedDateAsc(List<StockLocation> stockLocations);

	List<TemporaryOpeningStock> findByStockLocation(StockLocation stockLocation);

	@Query("select coalesce(sum(temporaryOpeningStock.quantity),0) from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.productProfile.pid = ?1 and temporaryOpeningStock.stockLocation in  ?2")
	Double findTemporaryOpeningStockByProductAndStockLocations(String productPid, List<StockLocation> stockLocations);

	@Query("select MAX(temporaryOpeningStock.createdDate) from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.productProfile.pid = ?1 and temporaryOpeningStock.stockLocation in  ?2")
	LocalDateTime findMaxDateByProductAndStockLocations(String productPid, List<StockLocation> stockLocations);

	List<TemporaryOpeningStock> findByProductProfileIn(List<ProductProfile> productProfiles);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.company.id = ?#{principal.companyId} and temporaryOpeningStock.productProfile.pid=?1")
	List<TemporaryOpeningStock> findByCompanyIdAndProductProfilePid(String productPid);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.company.id = ?#{principal.companyId} and temporaryOpeningStock.productProfile.pid=?1 and temporaryOpeningStock.batchNumber = ?2")
	List<TemporaryOpeningStock> findByCompanyIdAndProductProfilePidAndBatchNumber(String pid, String batchNumber);

	@Transactional
	Long deleteByCompanyId(Long companyId);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.company.id = ?#{principal.companyId} and temporaryOpeningStock.productProfile.pid= ?1 and temporaryOpeningStock.activated = ?2")
	List<TemporaryOpeningStock> findByCompanyIdAndProductProfilePidAndTemporaryOpeningStockActivated(String productPid,
			boolean active);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.company.id = ?#{principal.companyId} and temporaryOpeningStock.activated = ?1 Order By temporaryOpeningStock.productProfile.name asc")
	Page<TemporaryOpeningStock> findAllByCompanyIdAndActivatedTemporaryOpeningStockOrderByName(Pageable pageable,
			boolean active);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.company.id = ?#{principal.companyId} and temporaryOpeningStock.activated = ?1")
	List<TemporaryOpeningStock> findAllByCompanyIdAndDeactivatedTemporaryOpeningStock(boolean deactive);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.company.id = ?#{principal.companyId} and temporaryOpeningStock.productProfile.pid= ?1 and temporaryOpeningStock.activated = ?2 and temporaryOpeningStock.lastModifiedDate > ?3")
	List<TemporaryOpeningStock> findByCompanyIdAndProductProfilePidAndTemporaryOpeningStockActivatedAndLastModifiedDate(
			String productPid, boolean active, LocalDateTime lastModifiedDate);

	Optional<TemporaryOpeningStock> findTop1ByProductProfilePidAndStockLocationPidOrderByCreatedDateDesc(
			String productPid, String stockLocationPid);

	TemporaryOpeningStock findTop1ByProductProfilePidOrderByLastModifiedDateDesc(String productPid);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.company.id = ?#{principal.companyId} and temporaryOpeningStock.productProfile.pid = ?1 and temporaryOpeningStock.stockLocation in  ?2")
	List<TemporaryOpeningStock> findAllTemporaryOpeningStockByProductPidAndStockLocations(String productPid,
			List<StockLocation> stockLocations);

	@Query("select coalesce(sum(temporaryOpeningStock.quantity),0) from TemporaryOpeningStock temporaryOpeningStock where temporaryOpeningStock.productProfile.pid = ?1 and temporaryOpeningStock.stockLocation.id in ?2 ")
	Double findSumTemporaryOpeningStockByProductPidAndStockLocationIdIn(String productPid, Set<Long> stockLocationIds);

	@Transactional
	@Query(name = "delete from TemporaryOpeningStock os where  os.stockLocation.id = ?1 and os.company.id = ?2")
	void deleteByStockLocationIdAndCompanyId(long stockLocationId, long companyId);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where  temporaryOpeningStock.stockLocation.id in ?1")
	List<TemporaryOpeningStock> findTemporaryOpeningStocksAndStockLocationIdIn(Set<Long> sLocationIds);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where  temporaryOpeningStock.stockLocation.pid in ?1")
	List<TemporaryOpeningStock> findTemporaryOpeningStocksAndStockLocationPIdIn(List<String> sLocationPIds);

	@Query("select temporaryOpeningStock from TemporaryOpeningStock temporaryOpeningStock where  temporaryOpeningStock.stockLocation.id = ?1 Order By temporaryOpeningStock.productProfile.name asc")
	List<TemporaryOpeningStock> findTemporaryOpeningStocksAndStockLocationId(Long sLocationIds);

	List<TemporaryOpeningStock> findByStockLocationIn(List<StockLocation> stockLocations);

}
