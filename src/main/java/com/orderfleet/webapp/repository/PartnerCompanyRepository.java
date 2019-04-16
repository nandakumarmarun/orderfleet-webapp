package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PartnerCompany;

/**
 * Spring Data JPA repository for the PartnerCompany entity.
 *
 * @author Sarath
 * @since Feb 23, 2018
 *
 */
public interface PartnerCompanyRepository extends JpaRepository<PartnerCompany, Long> {

	Optional<PartnerCompany> findOneById(Long id);

	@Query("select partnerCompany from PartnerCompany partnerCompany where partnerCompany.partner.pid = ?1")
	List<PartnerCompany> findAllByPartnerPid(String partnerPid);
	
	@Query("select partnerCompany.company from PartnerCompany partnerCompany where partnerCompany.partner.pid = ?1")
	List<Company> findAllCompaniesByPartnerPid(String partnerPid);
}
