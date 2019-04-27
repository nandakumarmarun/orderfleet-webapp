package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountNameTextSettings;
import com.orderfleet.webapp.domain.ProductNameTextSettings;

/**
 * Spring Data JPA repository for the ProductNameTextSettings entity.
 * 
 * @author Muhammed Riyas T
 * @since Dec 29, 2016
 */
public interface AccountNameTextSettingsRepository extends JpaRepository<AccountNameTextSettings, Long> {

	Optional<AccountNameTextSettings> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<AccountNameTextSettings> findOneByPid(String pid);

	@Query("select accountNameTextSettings from AccountNameTextSettings accountNameTextSettings where accountNameTextSettings.company.id = ?#{principal.companyId}")
	List<AccountNameTextSettings> findAllByCompanyId();

	List<AccountNameTextSettings> findAllByCompanyIdAndEnabledTrue(Long companyId);
	
	@Query("select accountNameTextSettings.name from AccountNameTextSettings accountNameTextSettings where accountNameTextSettings.company.id = ?1 and accountNameTextSettings.enabled = TRUE")
	List<String> findNameByCompanyIdAndEnabledTrue(Long companyId);

}
