package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ProductNameTextSettings;

/**
 * Spring Data JPA repository for the ProductNameTextSettings entity.
 * 
 * @author Muhammed Riyas T
 * @since Dec 29, 2016
 */
public interface ProductNameTextSettingsRepository extends JpaRepository<ProductNameTextSettings, Long> {

	Optional<ProductNameTextSettings> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<ProductNameTextSettings> findOneByPid(String pid);

	@Query("select productNameTextSettings from ProductNameTextSettings productNameTextSettings where productNameTextSettings.company.id = ?#{principal.companyId}")
	List<ProductNameTextSettings> findAllByCompanyId();

	List<ProductNameTextSettings> findAllByCompanyIdAndEnabledTrue(Long companyId);
	
	@Query("select productNameTextSettings.name from ProductNameTextSettings productNameTextSettings where productNameTextSettings.company.id = ?1 and productNameTextSettings.enabled = TRUE")
	List<String> findNameByCompanyIdAndEnabledTrue(Long companyId);

}
