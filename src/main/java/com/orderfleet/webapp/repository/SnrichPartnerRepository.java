package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SnrichPartner;

public interface SnrichPartnerRepository extends JpaRepository<SnrichPartner, Long> {

	Optional<SnrichPartner> findOneByPid(String pid);
	
	Optional<SnrichPartner> findOneByName(String name);
	
	Optional<SnrichPartner> findOneByEmail(String email);
	
	@Query("select partner.pid, partner.name from SnrichPartner partner")
	List<Object[]> findAllByCompany();
	
}
