package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SnrichProduct;
import com.orderfleet.webapp.domain.SnrichProductCompany;
import com.orderfleet.webapp.domain.enums.PartnerIntegrationSystem;

public interface SnrichProductCompanyRepository extends JpaRepository<SnrichProductCompany, Long>{

	@Query("select spc.snrichProduct from SnrichProductCompany spc where spc.company.id = ?1")
	SnrichProduct findSnrichProductByCompanyId(Long companyId);
	
	@Query("select spc from SnrichProductCompany spc where spc.tallyActivationKey = ?1")
	Optional<SnrichProductCompany> findByTallyActivationKey(String activationKey);
	
	@Query("select spc.partnerIntegrationSystem from SnrichProductCompany spc where spc.company.id = ?1")
	PartnerIntegrationSystem findPartnerIntegrationSystemByCompanyId(Long id);
}
