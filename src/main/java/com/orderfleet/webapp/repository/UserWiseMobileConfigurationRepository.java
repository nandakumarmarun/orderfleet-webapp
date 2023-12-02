package com.orderfleet.webapp.repository;

import com.orderfleet.webapp.domain.UserWiseMobileConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Spring Data JPA repository for the mobileconfiguration entity.
 *
 * @author Resmi T R
 * @since Nov 27, 2023
 *
 */
public interface UserWiseMobileConfigurationRepository extends JpaRepository<UserWiseMobileConfiguration, Long> {

	 Optional<UserWiseMobileConfiguration> findOneByPid(String pid);

	@Query("select mConfig from UserWiseMobileConfiguration mConfig where mConfig.company.id = ?#{principal.companyId}")
	Optional<UserWiseMobileConfiguration> findOneByCompanyId();
	
	@Query("select mConfig from UserWiseMobileConfiguration mConfig where mConfig.company.id = ?1")
	Optional<UserWiseMobileConfiguration> findOneByCompanyId(Long companyId);

	@Query("select mConfig from UserWiseMobileConfiguration mConfig where mConfig.company.id = ?1")
	UserWiseMobileConfiguration findByCompanyId(Long companyId);

	@Transactional
	void deleteByPid(String pid);

	@Query("select mConfig from UserWiseMobileConfiguration mConfig where mConfig.user.id = ?1")
	Optional<UserWiseMobileConfiguration> findOneByUserId(Long id);
}
