package com.orderfleet.webapp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.RegistrationData;

public interface RegistrationDataRepository extends JpaRepository<RegistrationData, Long> {

	@Query("select registrationData from RegistrationData registrationData where registrationData.company.id = ?1")
	RegistrationData findAllByCompanyId(Long companyId);
	
	@Query("select registrationData from RegistrationData registrationData where registrationData.company.id = ?#{principal.companyId}")
	RegistrationData findOneByCompanyId();
	
	@Query("select registrationData.userCount from RegistrationData registrationData where registrationData.company.pid = ?1")
	Integer findUserCountByCompany(String pid);
	
	@Query("select registrationData from RegistrationData registrationData where registrationData.company.pid = ?1")
	RegistrationData findAllByCompanyPid(String companyPid);
}
