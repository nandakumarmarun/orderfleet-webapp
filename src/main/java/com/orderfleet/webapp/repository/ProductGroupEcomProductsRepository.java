package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupEcomProduct;

/**
 * * Spring Data JPA repository for the ProductGroupEcomProduct entity.
 *
 * @author Sarath
 * @since Sep 24, 2016
 */
public interface ProductGroupEcomProductsRepository extends JpaRepository<ProductGroupEcomProduct, Long> {

	@Query("select productGroupEcomProduct.ecomProduct from ProductGroupEcomProduct productGroupEcomProduct where productGroupEcomProduct.productGroup.pid = ?1 ")
	List<EcomProductProfile> findEcomProductByProductGroupPid(String productGroupPid);

	@Query("select productGroupEcomProduct from ProductGroupEcomProduct productGroupEcomProduct where productGroupEcomProduct.company.id = ?#{principal.companyId}")
	List<ProductGroupEcomProduct> findAllByCompanyId();

	@Query("select pgEcomProduct from ProductGroupEcomProduct pgEcomProduct where pgEcomProduct.productGroup in ?1 and pgEcomProduct.company.id = ?#{principal.companyId}")
	List<ProductGroupEcomProduct> findByProductGroups(List<ProductGroup> productGroups);

	@Query("select pgEcomProduct.ecomProduct from ProductGroupEcomProduct pgEcomProduct where pgEcomProduct.productGroup in ?1 and pgEcomProduct.company.id = ?#{principal.companyId}")
	List<EcomProductProfile> findEcomProductProfileByProductGroups(List<ProductGroup> productGroups);

	@Transactional
	void deleteByProductGroupPid(String productGroupPid);

	@Query("select pgEcomProduct from ProductGroupEcomProduct pgEcomProduct where pgEcomProduct.company.id = ?#{principal.companyId} and pgEcomProduct.productGroup in ?1 and (pgEcomProduct.productGroup.lastModifiedDate > ?2) or (pgEcomProduct.ecomProduct.lastModifiedDate > ?2) or (pgEcomProduct.lastModifiedDate > ?2) ")
	List<ProductGroupEcomProduct> findByProductGroupsAndLastModifiedDate(List<ProductGroup> productGroups,
			LocalDateTime lastModifiedDate);

	@Query("select pgEcomProduct.productGroup from ProductGroupEcomProduct pgEcomProduct where pgEcomProduct.ecomProduct.pid in ?1 and pgEcomProduct.company.id = ?#{principal.companyId}")
	List<ProductGroup> findAllProductGroupByEcomProductPidIn(List<String> ecomProductPids);
	
	@Query("select pgEcomProduct.productGroup from ProductGroupEcomProduct pgEcomProduct where pgEcomProduct.ecomProduct.pid in ?1 ")
	List<ProductGroup> findProductGroupsByEcomProductPidIn(List<String> ecomProductPids);
	
	@Query("select pgEcomProduct from ProductGroupEcomProduct pgEcomProduct where pgEcomProduct.ecomProduct.pid in ?1 ")
	List<ProductGroupEcomProduct> findProductGroupEcomProductByEcomProductPidIn(List<String> ecomProductPids);
}
