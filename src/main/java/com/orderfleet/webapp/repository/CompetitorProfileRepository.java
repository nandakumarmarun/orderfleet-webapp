package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.CompetitorProfile;

/**
 * Spring Data JPA repository for the CompetitorProfile entity.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public interface CompetitorProfileRepository extends JpaRepository<CompetitorProfile, Long> {

	Optional<CompetitorProfile> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<CompetitorProfile> findOneByPid(String pid);

	@Query("select competitorProfile from CompetitorProfile competitorProfile where competitorProfile.company.id = ?#{principal.companyId}")
	List<CompetitorProfile> findAllByCompanyId();

	@Query("select competitorProfile from CompetitorProfile competitorProfile where competitorProfile.company.id = ?#{principal.companyId}")
	Page<CompetitorProfile> findAllByCompanyId(Pageable pageable);
	
	@Query("select competitorProfile from CompetitorProfile competitorProfile where competitorProfile.company.id = ?#{principal.companyId} and competitorProfile.activated = ?1 Order By competitorProfile.name asc")
	List<CompetitorProfile> findAllByCompanyIdAndCompetitorProfileActivatedOrDeactivated(boolean active);
	
	@Query("select competitorProfile from CompetitorProfile competitorProfile where competitorProfile.company.id = ?#{principal.companyId}  Order By competitorProfile.name asc")
	Page<CompetitorProfile> findAllByCompanyIdOrderByNameDesc(Pageable pageable);
	
	@Query("select competitorProfile from CompetitorProfile competitorProfile where competitorProfile.company.id = ?#{principal.companyId} and competitorProfile.activated =?1  Order By competitorProfile.name asc")
	Page<CompetitorProfile> findAllByCompanyIdAndActivatedOrderByNameDesc(Pageable pageable,boolean active);
	
	@Query("select competitorProfile from CompetitorProfile competitorProfile where competitorProfile.company.id = ?#{principal.companyId} and competitorProfile.activated = ?1 and competitorProfile.lastModifiedDate > ?2 Order By competitorProfile.name asc")
	List<CompetitorProfile> findAllByCompanyIdAndCompetitorProfileActivatedAndLastModifiedDate(boolean active ,LocalDateTime lastModifiedDate);
}
