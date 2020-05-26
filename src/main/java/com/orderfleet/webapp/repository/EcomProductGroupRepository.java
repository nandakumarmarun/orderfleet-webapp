package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.ProductCategory;

/**
 * Spring Data JPA repository for the ProductGroup entity.
 *
 * @author Anish
 * @since May 17, 2020
 */
public interface EcomProductGroupRepository extends JpaRepository<EcomProductGroup, Long> {

	Optional<EcomProductGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<EcomProductGroup> findOneByPid(String pid);

	@Query("select productGroup from EcomProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} and productGroup.activated=?1 ORDER BY productGroup.name ASC")
	List<EcomProductGroup> findAllByCompanyId(boolean status);

	@Query("select productGroup from EcomProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} and productGroup.activated=?1")
	Page<EcomProductGroup> findAllByCompanyId(Pageable pageable, boolean status);

	@Query("select productGroup.name from EcomProductGroup productGroup where productGroup.company.id = ?#{principal.companyId}")
	List<String> findGroupNamesByCompanyId();

	List<EcomProductGroup> findAllByCompanyPid(String companyPid);

	@Query("select productGroup from EcomProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} Order By productGroup.name asc")
	List<EcomProductGroup> findAllByCompanyOrderByName();

	@Query("select productGroup from EcomProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} and productGroup.activated = ?1 order By productGroup.name asc")
	Page<EcomProductGroup> findAllByCompanyIdAndActivatedProductGroupOrderByName(boolean active, Pageable pageable);

	@Query("select productGroup from EcomProductGroup productGroup where productGroup.company.id = ?#{principal.companyId} and productGroup.activated = ?1 order By productGroup.name asc")
	List<EcomProductGroup> findAllByCompanyIdAndDeactivatedProductGroup(boolean deactive);

	List<EcomProductGroup> findByCompanyId(Long companyId);

	@Query("select productGroup from EcomProductGroup productGroup where productGroup.company.pid = ?1 order By productGroup.id asc")
	List<EcomProductGroup> findAllByCompanyPidOrderByProductId(String companyPid);

	@Query("select productGroup from EcomProductGroup productGroup where productGroup.company.pid = ?1 and productGroup.pid in ?2 order By productGroup.id asc")
	List<EcomProductGroup> findAllByCompanyPidAndGroupPidIn(String companyPid, List<String> groupPids);

	@Query("select productgroup from EcomProductGroup productgroup where productgroup.company.id = ?1 and UPPER(productgroup.name) in ?2")
	List<EcomProductGroup> findByCompanyIdAndNameIgnoreCaseIn(Long id, List<String> names);

	EcomProductGroup findFirstByCompanyId(Long id);
	
//	@Query("select productgroup from ProductGroup productgroup where productgroup.company.id = ?1 order by productgroup.id asc LIMIT 1")
//	ProductGroup findFirstByCompanyIdAsc(Long id);
	
	EcomProductGroup findFirstByCompanyIdOrderById(Long id);
	
}
