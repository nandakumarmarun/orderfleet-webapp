package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.MobileConfiguration;

/**
 * Spring Data JPA repository for the mobileconfiguration entity.
 *
 * @author Sarath
 * @since Jul 17, 2017
 *
 */
public interface MobileConfigurationRepository extends JpaRepository<MobileConfiguration, Long> {

	Optional<MobileConfiguration> findOneByPid(String pid);

	@Query("select mConfig from MobileConfiguration mConfig where mConfig.company.id = ?#{principal.companyId}")
	Optional<MobileConfiguration> findOneByCompanyId();
	
	@Query("select mConfig from MobileConfiguration mConfig where mConfig.company.id = ?1")
	Optional<MobileConfiguration> findOneByCompanyId(Long companyId);

	@Query("select mConfig from MobileConfiguration mConfig where mConfig.company.id = ?1")
	MobileConfiguration findByCompanyId(Long companyId);

	@Transactional
	void deleteByPid(String pid);
}
