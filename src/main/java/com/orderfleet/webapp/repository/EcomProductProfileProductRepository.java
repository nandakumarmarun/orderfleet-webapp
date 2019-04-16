package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.EcomProductProfileProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;

/**
 * Spring Data JPA repository for the EcomProductProfileProduct entity.
 * 
 * @author Sarath
 * @since Sep 23, 2016
 */
public interface EcomProductProfileProductRepository extends JpaRepository<EcomProductProfileProduct, Long> {

	@Query("select ecomProductProfileProduct.product from EcomProductProfileProduct ecomProductProfileProduct where ecomProductProfileProduct.ecomProductProfile.pid = ?1 ")
	List<ProductProfile> findProductByEcomProductProfilePid(String ecomProductProfilePid);

	@Query("select ecomProductProfileProduct.product from EcomProductProfileProduct ecomProductProfileProduct where ecomProductProfileProduct.ecomProductProfile.pid in ?1 ")
	List<ProductProfile> findProductByEcomProductProfilePidIn(Set<String> ecomProductProfilePids);

	void deleteByEcomProductProfilePid(String ecomProductProfilePid);

	@Query("select ecomProductProfileProduct from EcomProductProfileProduct ecomProductProfileProduct where ecomProductProfileProduct.company.id = ?#{principal.companyId}")
	List<EcomProductProfileProduct> findAllByCompanyId();

	List<EcomProductProfileProduct> findByEcomProductProfileIn(List<EcomProductProfile> ecomProductProfiles);

	@Query("select ecomProductProfileProduct.ecomProductProfile from EcomProductProfileProduct ecomProductProfileProduct where ecomProductProfileProduct.product.id = ?1 ")
	List<EcomProductProfile> findEcomProductProfilesByProductId(Long productId);

	@Query("select ecomProductProfileProduct.ecomProductProfile.pid from EcomProductProfileProduct ecomProductProfileProduct where ecomProductProfileProduct.product.pid in ?1 ")
	List<String> findEcomProductProfilePidByProductPidIn(List<String> ecomProductPids);

	@Query("select ecomProductProfileProduct.product from EcomProductProfileProduct ecomProductProfileProduct where ecomProductProfileProduct.ecomProductProfile.pid in ?1 and ecomProductProfileProduct.product.activated = ?2 and ecomProductProfileProduct.product.stockAvailabilityStatus =?3")
	List<ProductProfile> findProductByEcomProductProfilePidInAndActivatedAndStatus(Set<String> ecomProductProfilePids,
			boolean activated, StockAvailabilityStatus status);
	@Query("select ecomProductProfileProduct from EcomProductProfileProduct ecomProductProfileProduct where ecomProductProfileProduct.product.pid IN ?1")
	List<EcomProductProfileProduct> findByProductProfilePids(List<String> productPids); 
	
}