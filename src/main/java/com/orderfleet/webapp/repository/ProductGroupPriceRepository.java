package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ProductGroupPrice;

/**
 * Spring Data JPA repository for the ProductGroupPrice entity.
 * 
 * @author Sarath
 * @since Aug 9, 2016
 */
public interface ProductGroupPriceRepository extends JpaRepository<ProductGroupPrice, Long> {

	@Query("select pgPrice from ProductGroupPrice pgPrice where pgPrice.company.id = ?#{principal.companyId}")
	List<ProductGroupPrice> findByCompanyIsCurrentCompany();
	
	@Query("select pgPrice from ProductGroupPrice pgPrice where pgPrice.productGroup.pid = ?1 and pgPrice.company.id = ?#{principal.companyId}")
	ProductGroupPrice findByProductGroupPidAndCurrentCompany(String productGroupPid);
	
}
