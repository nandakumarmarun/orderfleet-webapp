package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;

/**
 * Spring Data JPA repository for the AccountType entity.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
public interface CompanyConfigurationRepository extends JpaRepository<CompanyConfiguration, Long> {

	Optional<CompanyConfiguration> findByCompanyIdAndName(Long id, CompanyConfig name);

	Optional<CompanyConfiguration> findByCompanyPidAndName(String pid, CompanyConfig name);

	List<CompanyConfiguration> findAllCompanyByName(CompanyConfig name);

	List<CompanyConfiguration> findAllByCompanyPid(String companyPid);
	
	@Transactional
	void deleteByCompanyIdAndName(Long companyId, CompanyConfig name);

}
