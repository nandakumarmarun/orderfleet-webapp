package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.PurchaseHistoryConfig;

/**
 * Spring Data JPA repository for the PurchaseHistoryConfig entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 06, 2017
 */
public interface PurchaseHistoryConfigRepository extends JpaRepository<PurchaseHistoryConfig, Long> {

	Optional<PurchaseHistoryConfig> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<PurchaseHistoryConfig> findOneByPid(String pid);

	@Query("select purchaseHistoryConfig from PurchaseHistoryConfig purchaseHistoryConfig where purchaseHistoryConfig.company.id = ?#{principal.companyId}")
	List<PurchaseHistoryConfig> findAllByCompanyId();
	
	@Query("select purchaseHistoryConfig from PurchaseHistoryConfig purchaseHistoryConfig where purchaseHistoryConfig.company.id = ?#{principal.companyId} Order By sortOrder asc")
	List<PurchaseHistoryConfig> findAllByCompanyIdOrderBySortOrderAsc();

}
