package com.orderfleet.webapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.CompanyEmail;
import com.orderfleet.webapp.domain.CompanyEmail.ModuleName;

public interface CompanyEmailRepository extends JpaRepository<CompanyEmail, Long> {
	
	Optional<CompanyEmail> findFirstByModuleNameAndCompanyId(ModuleName moduleName, Long companyId);

}
