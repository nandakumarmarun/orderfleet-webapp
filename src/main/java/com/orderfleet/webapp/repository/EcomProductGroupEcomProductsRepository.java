package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.EcomProductGroupEcomProduct;
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupEcomProduct;

/**
 * * Spring Data JPA repository for the EcomProductGroupEcomProduct entity.
 *
 * @author Anish
 * @since June 1, 2020
 */
public interface EcomProductGroupEcomProductsRepository extends JpaRepository<EcomProductGroupEcomProduct, Long> {

	@Query("select productGroupEcomProduct.ecomProduct from EcomProductGroupEcomProduct productGroupEcomProduct where productGroupEcomProduct.ecomProductGroup.pid = ?1 ")
	List<EcomProductProfile> findEcomProductByProductGroupPid(String productGroupPid);

	@Query("select productGroupEcomProduct from EcomProductGroupEcomProduct productGroupEcomProduct where productGroupEcomProduct.company.id = ?#{principal.companyId}")
	List<EcomProductGroupEcomProduct> findAllByCompanyId();

	@Query("select pgEcomProduct from EcomProductGroupEcomProduct pgEcomProduct where pgEcomProduct.ecomProductGroup in ?1 and pgEcomProduct.company.id = ?#{principal.companyId}")
	List<EcomProductGroupEcomProduct> findByProductGroups(List<EcomProductGroup> productGroups);

	@Query("select pgEcomProduct.ecomProduct from EcomProductGroupEcomProduct pgEcomProduct where pgEcomProduct.ecomProductGroup in ?1 and pgEcomProduct.company.id = ?#{principal.companyId}")
	List<EcomProductProfile> findEcomProductProfileByProductGroups(List<EcomProductGroup> productGroups);

	@Transactional
	void deleteByEcomProductGroupPid(String productGroupPid);

	@Query("select pgEcomProduct from EcomProductGroupEcomProduct pgEcomProduct where pgEcomProduct.company.id = ?#{principal.companyId} and pgEcomProduct.ecomProductGroup in ?1 and (pgEcomProduct.ecomProductGroup.lastModifiedDate > ?2) or (pgEcomProduct.ecomProduct.lastModifiedDate > ?2) or (pgEcomProduct.lastModifiedDate > ?2) ")
	List<EcomProductGroupEcomProduct> findByProductGroupsAndLastModifiedDate(List<EcomProductGroup> productGroups,
			LocalDateTime lastModifiedDate);

	@Query("select pgEcomProduct.ecomProductGroup from EcomProductGroupEcomProduct pgEcomProduct where pgEcomProduct.ecomProduct.pid in ?1 and pgEcomProduct.company.id = ?#{principal.companyId}")
	List<EcomProductGroup> findAllProductGroupByEcomProductPidIn(List<String> ecomProductPids);
	
	@Query("select pgEcomProduct.ecomProductGroup from EcomProductGroupEcomProduct pgEcomProduct where pgEcomProduct.ecomProduct.pid in ?1 ")
	List<EcomProductGroup> findProductGroupsByEcomProductPidIn(List<String> ecomProductPids);
	
	@Query("select pgEcomProduct from EcomProductGroupEcomProduct pgEcomProduct where pgEcomProduct.ecomProduct.pid in ?1 ")
	List<EcomProductGroupEcomProduct> findProductGroupEcomProductByEcomProductPidIn(List<String> ecomProductPids);
}
