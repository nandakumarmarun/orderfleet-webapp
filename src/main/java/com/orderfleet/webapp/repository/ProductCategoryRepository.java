package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductProfile;

/**
 * Spring Data JPA repository for the ProductCategory entity.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

	Optional<ProductCategory> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<ProductCategory> findOneByPid(String pid);
	
	Optional<ProductCategory> findTopByCompanyIdAndAlias(Long companyId, String alias);
	
	//to be delete
//	@Query("select productCategory from ProductCategory productCategory where productCategory.company.id = ?1 order by productCategory.id asc LIMIT 1")
//	ProductCategory findFirstByCompanyIdAsc(Long id);
	
	ProductCategory findFirstByCompanyIdOrderById(Long id);
	
	ProductCategory findFirstByCompanyId(Long id);

	@Query("select productCategory from ProductCategory productCategory where productCategory.company.id = ?#{principal.companyId} order by productCategory.name asc")
	List<ProductCategory> findAllByCompanyId();
	
	@Query("select productCategory from ProductCategory productCategory where productCategory.company.id = ?1 order by productCategory.name asc")
	List<ProductCategory> findAllByCompanyId(Long companyId);
	
	@Query("select productCategory from ProductCategory productCategory where productCategory.company.id = ?#{principal.companyId} and productCategory.activated=true order by productCategory.name asc")
	List<ProductCategory> findAllByCompanyIdAndActivatedTrue();

	@Query("select productCategory from ProductCategory productCategory where productCategory.company.id = ?#{principal.companyId}")
	Page<ProductCategory> findAllByCompanyId(Pageable pageable);

	@Query("select productCategory.name from ProductCategory productCategory where productCategory.company.id = ?#{principal.companyId}")
	List<String> findCategoryNamesByCompanyId();

	List<ProductCategory> findAllByCompanyPid(String companyPid);
	
	@Query("select productCategory from ProductCategory productCategory where productCategory.company.id = ?#{principal.companyId} and productCategory.activated = ?1 Order By productCategory.name asc ")
	Page<ProductCategory> findAllByCompanyIdAndActivatedOrderByProductCategoryName(Pageable pageable,boolean active);
	
	@Query("select productCategory from ProductCategory productCategory where productCategory.company.id = ?#{principal.companyId} and productCategory.activated = ?1")
	List<ProductCategory> findAllByCompanyIdAndDeactivated(boolean deactive);
	
	List<ProductCategory> findByCompanyId(Long id);
	
	@Query("select productCategory from ProductCategory productCategory where productCategory.company.pid = ?1 and productCategory.pid in ?2 order By productCategory.id asc")
	List<ProductCategory> findAllByCompanyPidOrderByProductId(String companyPid,List<String>categoryPids);
	
}
