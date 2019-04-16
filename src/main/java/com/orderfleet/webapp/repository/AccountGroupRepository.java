package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.AccountGroup;

/**
 * Spring Data JPA repository for the AccountGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
public interface AccountGroupRepository extends JpaRepository<AccountGroup, Long> {

	Optional<AccountGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<AccountGroup> findOneByPid(String pid);

	@Query("select accountGroup from AccountGroup accountGroup where accountGroup.company.id = ?#{principal.companyId}")
	List<AccountGroup> findAllByCompanyId();

	@Query("select accountGroup from AccountGroup accountGroup where accountGroup.company.id = ?#{principal.companyId}")
	Page<AccountGroup> findAllByCompanyId(Pageable pageable);

	List<AccountGroup> findAllByCompanyPid(String companyPid);
	
	@Query("select accountGroup from AccountGroup accountGroup where accountGroup.company.id = ?#{principal.companyId} Order By accountGroup.name asc")
	Page<AccountGroup> findAllByCompanyIdOrderByAccountGroupName(Pageable pageable);
	
	@Query("select accountGroup from AccountGroup accountGroup where accountGroup.company.id = ?#{principal.companyId} and accountGroup.activated = ?1 Order By accountGroup.name asc")
	Page<AccountGroup> findAllByCompanyAndActive(Pageable pageable, boolean active);
	
	@Query("select accountGroup from AccountGroup accountGroup where accountGroup.company.id = ?#{principal.companyId} and accountGroup.activated = ?1")
	List<AccountGroup> findAllByCompanyAndDeactive(boolean deactive);
}
