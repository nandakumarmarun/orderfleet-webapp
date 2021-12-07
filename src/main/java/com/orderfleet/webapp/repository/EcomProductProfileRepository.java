package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;

/**
 * Spring Data JPA repository for the EcomProductProfile entity.
 * 
 * @author Sarath
 * @since Sep 23, 2016
 */
public interface EcomProductProfileRepository extends JpaRepository<EcomProductProfile, Long> {

	Optional<EcomProductProfile> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<EcomProductProfile> findOneByPid(String pid);

	@Query("select ecomProductProfile from EcomProductProfile ecomProductProfile where ecomProductProfile.company.id = ?#{principal.companyId} order by ecomProductProfile.name asc")
	List<EcomProductProfile> findAllByCompanyId();

	@Query("select ecomProductProfile from EcomProductProfile ecomProductProfile where ecomProductProfile.company.id = ?#{principal.companyId}")
	Page<EcomProductProfile> findAllByCompanyId(Pageable pageable);

	@Query("select ecomProductProfile from EcomProductProfile ecomProductProfile where ecomProductProfile.company.id = ?#{principal.companyId} and ecomProductProfile.activated = ?1 Order By ecomProductProfile.name asc")
	Page<EcomProductProfile> findAllByCompanyIdAndActivatedEcomProductProfileOrderByName(Pageable pageable,
			boolean active);

	@Query("select ecomProductProfile from EcomProductProfile ecomProductProfile where ecomProductProfile.company.id = ?#{principal.companyId} and ecomProductProfile.activated = ?1 Order By ecomProductProfile.name asc")
	List<EcomProductProfile> findAllByCompanyIdAndActivatedOrDeactivatedEcomProductProfile(boolean deactive);

	List<EcomProductProfile> findByCompanyIdAndNameIgnoreCaseIn(Long id, Set<String> ppNames);
	
	@Query("select ecomProductProfile from EcomProductProfile ecomProductProfile where ecomProductProfile.pid IN ?1")
	List<EcomProductProfile> findAllByProductPid(List<String> pids);
	
   
	

}
