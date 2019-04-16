package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.PriceTrendProduct;

/**
 * Spring Data JPA repository for the PriceTrendProduct entity.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public interface PriceTrendProductRepository extends JpaRepository<PriceTrendProduct, Long> {

	Optional<PriceTrendProduct> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<PriceTrendProduct> findOneByPid(String pid);

	@Query("select priceTrendProduct from PriceTrendProduct priceTrendProduct where priceTrendProduct.company.id = ?#{principal.companyId}")
	List<PriceTrendProduct> findAllByCompanyId();

	@Query("select priceTrendProduct from PriceTrendProduct priceTrendProduct where priceTrendProduct.company.id = ?#{principal.companyId}")
	Page<PriceTrendProduct> findAllByCompanyId(Pageable pageable);
	
	@Query("select priceTrendProduct from PriceTrendProduct priceTrendProduct where priceTrendProduct.company.id = ?#{principal.companyId} and priceTrendProduct.activated = ?1 Order By priceTrendProduct.name asc")
	List<PriceTrendProduct> findAllByCompanyIdAndPriceTrendProductActivatedOrDeactivated(boolean active);
	
	@Query("select priceTrendProduct from PriceTrendProduct priceTrendProduct where priceTrendProduct.company.id = ?#{principal.companyId} Order By priceTrendProduct.name asc")
	Page<PriceTrendProduct> findAllByCompanyIdOrderByPriceTrendProductName(Pageable pageable);
	
	@Query("select priceTrendProduct from PriceTrendProduct priceTrendProduct where priceTrendProduct.company.id = ?#{principal.companyId} and priceTrendProduct.activated = ?1 Order By priceTrendProduct.name asc")
	Page<PriceTrendProduct> findAllByCompanyIdAndActivatedOrderByPriceTrendProductName(Pageable pageable,boolean active);

	@Query("select priceTrendProduct from PriceTrendProduct priceTrendProduct where priceTrendProduct.company.id = ?#{principal.companyId} and priceTrendProduct.activated = ?1 and priceTrendProduct.lastModifiedDate >?2 Order By priceTrendProduct.name asc")
	List<PriceTrendProduct> findAllByCompanyIdAndPriceTrendProductActivatedOrDeactivatedAndLastModifiedDate(
			boolean active, LocalDateTime lastModifiedDate);
}
