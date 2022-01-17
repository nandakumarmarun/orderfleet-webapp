package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;

/**
 * Spring Data JPA repository for the ProductGroup entity.
 *
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {

	Optional<ProductGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<ProductGroup> findOneByPid(String pid);

	@Query("select productGroup from ProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} and productGroup.activated=?1 ORDER BY productGroup.name ASC")
	List<ProductGroup> findAllByCompanyId(boolean status);

	@Query("select productGroup from ProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} and productGroup.activated=?1")
	Page<ProductGroup> findAllByCompanyId(Pageable pageable, boolean status);

	@Query("select productGroup.name from ProductGroup productGroup where productGroup.company.id = ?#{principal.companyId}")
	List<String> findGroupNamesByCompanyId();

	List<ProductGroup> findAllByCompanyPid(String companyPid);
	
	@Query("select productGroup from ProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} and productGroup.activated=true order by productGroup.name asc")
	List<ProductGroup> findAllByCompanyIdAndActivatedTrue();
	
	@Query("select productGroup from ProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} ORDER BY productGroup.name ASC")
	List<ProductGroup> findAllByCompanyId();

	@Query("select productGroup from ProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} Order By productGroup.name asc")
	List<ProductGroup> findAllByCompanyOrderByName();

	@Query("select productGroup from ProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} and productGroup.activated = ?1 order By productGroup.name asc")
	Page<ProductGroup> findAllByCompanyIdAndActivatedProductGroupOrderByName(boolean active, Pageable pageable);

	@Query("select productGroup from ProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} and productGroup.activated = ?1 order By productGroup.name asc")
	List<ProductGroup> findAllByCompanyIdAndDeactivatedProductGroup(boolean deactive);

	List<ProductGroup> findByCompanyId(Long companyId);

	@Query("select productGroup from ProductGroup productGroup where productGroup.company.pid = ?1 order By productGroup.id asc")
	List<ProductGroup> findAllByCompanyPidOrderByProductId(String companyPid);

	@Query("select productGroup from ProductGroup productGroup where productGroup.company.pid = ?1 and productGroup.pid in ?2 order By productGroup.name asc")
	List<ProductGroup> findAllByCompanyPidAndGroupPidIn(String companyPid, List<String> groupPids);

	@Query("select productgroup from ProductGroup productgroup where productgroup.company.id = ?1 and UPPER(productgroup.name) in ?2")
	List<ProductGroup> findByCompanyIdAndNameIgnoreCaseIn(Long id, List<String> names);

	ProductGroup findFirstByCompanyId(Long id);
	ProductGroup findFirstByCompanyIdOrderByIdAsc(Long companyId);
	
	
//	@Query("select productgroup from ProductGroup productgroup where productgroup.company.id = ?1 order by productgroup.id asc LIMIT 1")
//	ProductGroup findFirstByCompanyIdAsc(Long id);
//	
	ProductGroup findFirstByCompanyIdOrderById(Long id);
	
}
