package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.CompanySetting;

/**
 * Spring Data JPA repository for the CompanySetting entity.
 * 
 * @author Shaheer
 * @since October 18, 2016
 */
public interface CompanySettingRepository extends JpaRepository<CompanySetting, Long> {

	@Query("SELECT cs FROM CompanySetting cs WHERE cs.company.id = ?#{principal.companyId}")
	CompanySetting findOneByCompanyId();

	@Query("select companySetting from CompanySetting companySetting where companySetting.company.id = ?#{principal.companyId}")
	List<CompanySetting> findByCompanyIsCurrentCompany();
	
}
