package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ProductGroupInfoSection;

/**
 * Spring Data JPA repository for the ProductGroupInfoSection entity.
 * 
 * @author Muhammed Riyas T
 * @since Sep 21, 2016
 */
public interface ProductGroupInfoSectionRepository extends JpaRepository<ProductGroupInfoSection, Long> {

	Optional<ProductGroupInfoSection> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<ProductGroupInfoSection> findOneByPid(String pid);

	@Query("select productGroupInfoSection from ProductGroupInfoSection productGroupInfoSection where productGroupInfoSection.company.id = ?#{principal.companyId}")
	List<ProductGroupInfoSection> findAllByCompanyId();

	@Query("select productGroupInfoSection from ProductGroupInfoSection productGroupInfoSection where productGroupInfoSection.company.id = ?#{principal.companyId}")
	Page<ProductGroupInfoSection> findAllByCompanyId(Pageable pageable);

	List<ProductGroupInfoSection> findAllByCompanyPid(String companyPid);

}
