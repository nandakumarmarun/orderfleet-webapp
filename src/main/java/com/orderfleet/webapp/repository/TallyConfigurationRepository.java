package com.orderfleet.webapp.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.TallyConfiguration;

public interface TallyConfigurationRepository extends JpaRepository<TallyConfiguration, Long> {

	@Query("select tConfig from TallyConfiguration tConfig where tConfig.company.id = ?1")
	Optional<TallyConfiguration> findByCompanyId(Long companyId);
	
	@Query("select tConfig from TallyConfiguration tConfig where tConfig.tallyProductKey = ?1 AND tConfig.tallyCompanyName = ?2")
	Optional<TallyConfiguration> findByProductKeyAndCompanyName(String productKey,String companyName);
	
	@Query("select tConfig from TallyConfiguration tConfig where tConfig.pid = ?1")
	Optional<TallyConfiguration> findByPid(String pid);
	
	Optional<TallyConfiguration> findOneByPid(String pid);

	@Query("select tConfig from TallyConfiguration tConfig where tConfig.company.id = ?#{principal.companyId}")
	Optional<TallyConfiguration> findOneByCompanyId();
	
	@Query("select tConfig from TallyConfiguration tConfig where tConfig.company.id = ?1")
	Optional<TallyConfiguration> findOneByCompanyId(Long companyId);

	@Query("select tConfig from TallyConfiguration tConfig where tConfig.company.id = ?1")
	TallyConfiguration findTallyByCompanyId(Long companyId);
	
	Optional<TallyConfiguration> findByTallyProductKeyAndTallyCompanyName(String productKey, String companyName);

	@Transactional
	void deleteByPid(String pid);
}
