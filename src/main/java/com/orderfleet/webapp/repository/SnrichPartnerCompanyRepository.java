package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.SnrichPartnerCompany;

public interface SnrichPartnerCompanyRepository extends JpaRepository<SnrichPartnerCompany, Long>{

	List<SnrichPartnerCompany> findByUsersCountGreaterThan(int usersCount);
	
	SnrichPartnerCompany findByDbCompany(String dbCompanyId);
	
}
